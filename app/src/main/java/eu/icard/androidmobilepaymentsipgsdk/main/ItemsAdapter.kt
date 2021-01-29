package eu.icard.androidmobilepaymentsipgsdk.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.databinding.ItemNewLayoutBinding
import eu.icard.androidmobilepaymentsipgsdk.model.CartItemModel

class ItemsAdapter(
    cartItems: List<CartItemModel>,
    private val listener: ItemsListener
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    interface ItemsListener {
        fun onItemAdded(cartItemModel: CartItemModel)
    }

    private val items               = cartItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder
        = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_new_layout,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int)
        = holder.bindTo(items[position], listener)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemNewLayoutBinding.bind(itemView)

        fun bindTo(cartItemModel: CartItemModel, listener: ItemsListener) {
            binding.cartItemModel = cartItemModel
            binding.listener      = listener
        }
    }
}