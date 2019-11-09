package com.shurikus.googlepaysample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shurikus.googlepaysample.adapters.OnProductCallback
import com.shurikus.googlepaysample.adapters.ProductAdapter
import com.shurikus.googlepaysample.extentions.round
import com.shurikus.googlepaysample.models.ProductEntity
import com.shurikus.googlepaysample.utils.PriceFormatUtils
import kotlinx.android.synthetic.main.activity_google_pay.*

class GooglePayActivity : AppCompatActivity() {

    private lateinit var paymentDialog: ChoicePaymentBottomDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_pay)

        calculateTotalAmount(productList)
        setupProductList()

        button_go_to_payment.setOnClickListener {
            paymentDialog = ChoicePaymentBottomDialogFragment.newInstance(productList)
            paymentDialog.show(supportFragmentManager, ChoicePaymentBottomDialogFragment.TAG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        paymentDialog.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupProductList() {
        val productAdapter = ProductAdapter(productList, object : OnProductCallback {
            override fun onChangeCounts(changedData: List<ProductEntity>) {
                calculateTotalAmount(changedData)
            }
        })
        recycler_view.adapter = productAdapter
    }

    private fun calculateTotalAmount(data: List<ProductEntity>) {
        var totalAmount = 0.0
        data.forEach {
            totalAmount += it.price * it.count
        }
        totalAmount = totalAmount.round(2)
        text_total_amount.text = PriceFormatUtils.priceFormat(totalAmount)
    }

    private var productList = listOf(
        ProductEntity("1", "Billy/Oxberg", "by IKEA", 79.0, 85.0, 1, R.raw.a1),
        ProductEntity("2","Brunsta", "by IKEA", 19.99, 0.0, 1, R.raw.a2),
        ProductEntity("3","Dinera", "by IKEA", 2.0, 0.0, 1, R.raw.a3)
    )
}
