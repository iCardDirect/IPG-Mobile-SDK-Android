package eu.icard.androidmobilepaymentsipgsdk.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.core.widget.addTextChangedListener
import eu.icard.androidmobilepaymentsipgsdk.R
import kotlinx.android.synthetic.main.dialog_transaction_status.*

class TransactionStatusDialog(
    context: Context,
    private val onSendClicked: (orderId: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_transaction_status)

        setListeners()

        window?.let {
            val params      = it.attributes
            params.width    = ViewGroup.LayoutParams.MATCH_PARENT
            params.height   = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes   = params
        }
    }

    private fun setListeners() {
        cancelBtn.setOnClickListener { dismiss() }
        sendBtn.setOnClickListener {
            if(validate()) {
                dismiss()
                onSendClicked(orderIdEdt.text.toString())
            }
            else {
                orderIdLayout.isErrorEnabled  = true
                orderIdLayout.error           = context.getString(R.string.please_enter_order_id)
            }
        }

        orderIdEdt.addTextChangedListener {
            orderIdLayout.isErrorEnabled        = false
            orderIdLayout.error                 = null
        }
    }

    private fun validate() : Boolean {
        return orderIdEdt.text.toString().isNotEmpty()
    }
}