package eu.icard.androidmobilepaymentsipgsdk.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.Window
import androidx.core.widget.addTextChangedListener
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.utils.formatAmount
import kotlinx.android.synthetic.main.dialog_refund.*
import kotlinx.android.synthetic.main.item_order_layout.*

class RefundDialog(
    context: Context,
    private val onRefundClicked: (tranRef: String, amount: Double, orderId: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_refund)

        setListeners()

        window?.let {
            val params      = it.attributes
            params.width    = ViewGroup.LayoutParams.MATCH_PARENT
            params.height   = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes   = params
        }

        cancelBtn.setOnClickListener { dismiss() }
        refundBtn.setOnClickListener {
            if(validate()) {
                dismiss()
                onRefundClicked(
                    tranReferenceEdt.text.toString(),
                    amountEdt.text.toString().toDouble().formatAmount().toDouble(),
                    orderIdEdt.text.toString()
                )
            }
        }
    }

    private fun setListeners() {
        tranReferenceEdt.addTextChangedListener {
            tranRefLayout.error             = null
            tranRefLayout.isErrorEnabled    = false
        }
        amountEdt.addTextChangedListener {
            amountLayout.error             = null
            amountLayout.isErrorEnabled    = false
        }
        orderIdEdt.addTextChangedListener {
            orderIdLayout.error             = null
            orderIdLayout.isErrorEnabled    = false
        }
    }

    private fun validate() : Boolean {
        var validationPassed = true
        if(tranReferenceEdt.text.isNullOrEmpty()) {
            tranRefLayout.error             = context.getString(R.string.please_enter_trannsaction_reference)
            tranRefLayout.isErrorEnabled    = true
            validationPassed = false
        }

        if(amountEdt.text.isNullOrEmpty()) {
            amountLayout.error             = context.getString(R.string.please_enter_trannsaction_reference)
            amountLayout.isErrorEnabled    = true
            validationPassed = false
        }

        if(orderIdEdt.text.isNullOrEmpty()) {
            orderIdLayout.error             = context.getString(R.string.please_enter_trannsaction_reference)
            orderIdLayout.isErrorEnabled    = true
            validationPassed = false
        }

        return validationPassed
    }
}