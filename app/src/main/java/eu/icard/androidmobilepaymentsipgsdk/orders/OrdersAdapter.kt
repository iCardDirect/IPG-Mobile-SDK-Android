package eu.icard.androidmobilepaymentsipgsdk.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.databinding.ItemOrderLayoutBinding
import eu.icard.androidmobilepaymentsipgsdk.model.OrderModel

class OrdersAdapter(
    private val orders: List<OrderModel>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder
        =
        OrderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_layout,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int)
        = holder.bindTo(orders[position])

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemOrderLayoutBinding.bind(itemView)

        fun bindTo(orderModel: OrderModel) {
            binding.orderModel    = orderModel
        }
    }
}