package eu.icard.androidmobilepaymentsipgsdk.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

object Binding {

    @BindingAdapter(value = ["amount", "currency"])
    @JvmStatic
    fun setAmountAndCurrency(textView: TextView, amount: String, currency: String) {
        textView.text = currency.getCurrencySymbol().plus(amount).getSpannableCurrency(currency.getCurrencySymbol())
    }
}