package eu.icard.androidmobilepaymentsipgsdk.orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.*
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.model.OrderModel
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.android.synthetic.main.activity_orders.toolbar


class OrdersActivity : AppCompatActivity() {

    private val orders      = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        init()
        setData()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {  onBackPressed() }
    }

    private fun setData() {
        val sharedPreferences   = PreferenceManager.getDefaultSharedPreferences(this)
        val ordersSet           = sharedPreferences.getStringSet("orders", null)
        val orderList           = ordersSet?.toList()
        val gson                = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val ordersModelList = orderList?.map { gson.fromJson(it, OrderModel::class.java) } ?: listOf()
        orders.addAll(ordersModelList)
        orders.sortByDescending { it.date }

        if(orders.isEmpty())
            emptyTxt.visibility = View.VISIBLE
        else
            emptyTxt.visibility = View.GONE

        val adapter                 = OrdersAdapter(orders)
        ordersRecyclerView.adapter  = adapter
    }
}