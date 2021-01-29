package eu.icard.androidmobilepaymentsipgsdk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaymentOptionsAdapter(
    private val items: List<PaymentOptionModel>,
    private val onItemCLicked: (position: Int) -> Unit
) : RecyclerView.Adapter<PaymentOptionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_option, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTo(option: PaymentOptionModel) {
            itemView.findViewById<TextView>(R.id.paymentOptionTxt).text = option.name
            itemView.findViewById<TextView>(R.id.paymentOptionTxt).setCompoundDrawablesRelativeWithIntrinsicBounds(option.drawableIcon, null, null, null)

            itemView.setOnClickListener {
                onItemCLicked(adapterPosition)
            }
        }
    }
}