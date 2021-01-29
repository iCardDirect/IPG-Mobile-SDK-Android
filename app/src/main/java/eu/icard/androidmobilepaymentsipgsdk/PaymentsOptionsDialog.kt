package eu.icard.androidmobilepaymentsipgsdk

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import eu.icard.androidmobilepaymentsipgsdk.utils.getCardTypeImageResource
import eu.icard.mobilepaymentssdk.model.StoredCard
import kotlinx.android.synthetic.main.dialog_payments_options.*

class PaymentsOptionsDialog(
    context: Context,
    private val storedCards: List<StoredCard>,
    private val listener: OnPaymentOptionSelected
) : Dialog(context) {

    interface OnPaymentOptionSelected {
        fun onNewCardSelected()

        fun onStoreCardAndPurchaseSelected()

        fun onStoredCardSelected(storeCard: StoredCard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_payments_options)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val paymentOptions  = mutableListOf<PaymentOptionModel>()
        paymentOptions.add(PaymentOptionModel(name = "New card", drawableIcon = ContextCompat.getDrawable(context, R.drawable.ic_plus)))
        paymentOptions.add(PaymentOptionModel(name = "Store card and purchase", drawableIcon = ContextCompat.getDrawable(context, R.drawable.ic_plus)))

        val cardsOptions = storedCards.map { PaymentOptionModel().apply {
                name            = "${it.cardCustomName} *${it.maskedPan?.substring(it.maskedPan?.length?.minus(4) ?: 0) ?: ""}"
                drawableIcon    = ContextCompat.getDrawable(context, it.cardType?.getCardTypeImageResource() ?: 0)
            }
        }
        paymentOptions.addAll(cardsOptions)
        paymentOptionsRecyclerView.adapter = PaymentOptionsAdapter(paymentOptions) { position ->
            dismiss()
            when(position) {

                0 -> listener.onNewCardSelected()

                1 -> listener.onStoreCardAndPurchaseSelected()

                else -> listener.onStoredCardSelected(storedCards[position - 2])
            }
        }
    }
}