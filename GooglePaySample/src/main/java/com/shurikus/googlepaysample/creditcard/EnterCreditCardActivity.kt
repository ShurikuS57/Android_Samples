package com.shurikus.googlepaysample.creditcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.shurikus.googlepaysample.ChoicePaymentBottomDialogFragment
import com.shurikus.googlepaysample.R
import com.shurikus.googlepaysample.extentions.fromJsonAuto
import com.shurikus.googlepaysample.extentions.toJsonAuto
import com.shurikus.googlepaysample.models.ProductEntity
import com.shurikus.googlepaysample.utils.CalculateUtils
import com.tenbis.library.consts.CardType
import com.tenbis.library.listeners.OnCreditCardStateChanged
import com.tenbis.library.models.CreditCard
import kotlinx.android.synthetic.main.activity_enter_credit_card.*

class EnterCreditCardActivity: AppCompatActivity(), OnCreditCardStateChanged {

    private val selectProductList: List<ProductEntity> by lazy {
        val arg = intent?.getStringExtra(ARG_SELECT_PRODUCT_LIST) ?: ""
        return@lazy Gson().fromJsonAuto<List<ProductEntity>>(arg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_credit_card)
        compact_credit_card_input.attachLifecycle(lifecycle)
        compact_credit_card_input.addOnCreditCardStateChangedListener(this)

        val price = CalculateUtils.calculatePrice(selectProductList)
        text_total_amount.text = getString(R.string.format_dollar, price.toString())
        button_pay.setOnClickListener {
            showLoadingAnimation()
        }
    }

    override fun onCreditCardValid(creditCard: CreditCard) {
        button_pay.isEnabled = true
    }

    override fun onInvalidCardTyped() {
        button_pay.isEnabled = false
    }

    override fun onCreditCardCvvValid(cvv: String) { }

    override fun onCreditCardExpirationDateValid(month: Int, year: Int) { }

    override fun onCreditCardNumberValid(creditCardNumber: String) { }

    override fun onCreditCardTypeFound(cardType: CardType) { }

    private fun showLoadingAnimation() {
        frame_loading.visibility = View.VISIBLE
        animation_view.setMinAndMaxFrame(20, 58)
        val timer = object: CountDownTimer(5000, 5000) {
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                showSuccessAnimation()
            }
        }
        timer.start()
    }

    private fun showSuccessAnimation() {
        animation_view.setMinAndMaxFrame(60, 90)
        animation_view.repeatCount = 1

        val timer = object: CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                finish()
            }
        }
        timer.start()
    }

    companion object {
        private const val ARG_SELECT_PRODUCT_LIST = "arg_select_product_list"

        fun buildIntent(context: Context, productList: List<ProductEntity>): Intent {
            val intent = Intent(context, EnterCreditCardActivity::class.java)
            intent.putExtra(ARG_SELECT_PRODUCT_LIST, Gson().toJsonAuto<List<ProductEntity>>(productList))
            return intent
        }
    }
}