package com.shurikus.googlepaysample.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.shurikus.googlepaysample.models.ProductEntity
import kotlinx.android.synthetic.main.item_product.view.*
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import com.shurikus.googlepaysample.R
import com.shurikus.googlepaysample.utils.PriceFormatUtils

class ProductAdapter(private var data: List<ProductEntity>, private val callback: OnProductCallback) :
    RecyclerView.Adapter<ProductAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = data[position]
        with(holder.itemView) {
            text_product_name.text = item.title
            text_maker.text = item.companyName
            text_price.text = preparePriceFormat(context, item.price)
            if (item.priceWithoutDiscount > 0) {
                text_without_discount.text = preparePriceFormat(context, item.priceWithoutDiscount)
                text_without_discount.paintFlags =
                    text_without_discount.paintFlags or STRIKE_THRU_TEXT_FLAG
            } else {
                text_without_discount.text = ""
            }
            edit_count.setText(item.count.toString())
            Glide.with(imageView).load(item.image).into(imageView)
            button_minus.setOnClickListener {
                if (item.count > 1) {
                    item.count--
                    edit_count.setText(item.count.toString())
                    callback.onChangeCounts(data)
                }
            }
            button_plus.setOnClickListener {
                item.count++
                edit_count.setText(item.count.toString())
                callback.onChangeCounts(data)
            }
        }
    }

    private fun preparePriceFormat(context: Context, price: Double): String {
        return context.getString(R.string.format_dollar, PriceFormatUtils.priceFormat(price))
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

interface OnProductCallback {
    fun onChangeCounts(changedData: List<ProductEntity>)
}