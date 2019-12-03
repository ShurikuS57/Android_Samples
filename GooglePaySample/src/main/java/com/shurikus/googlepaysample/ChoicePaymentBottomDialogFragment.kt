package com.shurikus.googlepaysample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.shurikus.googlepaysample.creditcard.EnterCreditCardActivity
import com.shurikus.googlepaysample.extentions.fromJsonAuto
import com.shurikus.googlepaysample.extentions.toJsonAuto
import com.shurikus.googlepaysample.models.ProductEntity
import com.shurikus.googlepaysample.pay.PaymentsUtil
import com.shurikus.googlepaysample.pay.microsToString
import com.shurikus.googlepaysample.utils.CalculateUtils
import kotlinx.android.synthetic.main.bs_choise_payment_method.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToLong

class ChoicePaymentBottomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var paymentsClient: PaymentsClient

    private val selectProductList: List<ProductEntity> by lazy {
        val arg = arguments?.getString(ARG_SELECT_PRODUCT_LIST) ?: ""
        return@lazy Gson().fromJsonAuto<List<ProductEntity>>(arg)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_choise_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentsClient = PaymentsUtil.createPaymentsClient(requireActivity())
        possiblyShowGooglePayButton()
        button_google_pay.setOnClickListener {
            requestPayment()
            dismiss()
        }

        button_buy_card.setOnClickListener {
            startActivity(EnterCreditCardActivity.buildIntent(requireContext(), selectProductList))
            dismiss()
        }
    }

    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            button_google_pay.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                requireContext(),
                "Unfortunately, Google Pay is not available on this device",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPayment() {
        button_google_pay.isClickable = false
        val calcPrice = CalculateUtils.calculatePrice(selectProductList)
        val price = (calcPrice * 1000000).roundToLong().microsToString()
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }
                    Activity.RESULT_CANCELED -> {
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
                button_google_pay.isClickable = true
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type") == "PAYMENT_GATEWAY" && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token") == "examplePaymentMethodToken") {

                AlertDialog.Builder(requireContext())
                    .setTitle("Warning")
                    .setMessage("Gateway name set to \"example\" - please modify " +
                            "Constants.java and replace it with your own gateway.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }

            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            Toast.makeText(requireContext(), getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }

    }

    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    companion object {
        const val TAG = "ChoicePaymentBottomDialogFragment"
        private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
        private const val ARG_SELECT_PRODUCT_LIST = "arg_select_product_list"

        fun newInstance(productList: List<ProductEntity>): ChoicePaymentBottomDialogFragment {
            val fragment = ChoicePaymentBottomDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_SELECT_PRODUCT_LIST, Gson().toJsonAuto<List<ProductEntity>>(productList))
            fragment.arguments = bundle
            return fragment
        }
    }
}