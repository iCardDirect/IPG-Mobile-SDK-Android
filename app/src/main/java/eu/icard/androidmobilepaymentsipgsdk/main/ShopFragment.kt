package eu.icard.androidmobilepaymentsipgsdk.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.icard.androidmobilepaymentsipgsdk.PaymentsOptionsDialog
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.model.CartItemModel
import eu.icard.androidmobilepaymentsipgsdk.model.OrderModel
import eu.icard.androidmobilepaymentsipgsdk.utils.Utils
import eu.icard.androidmobilepaymentsipgsdk.utils.showMessage
import eu.icard.androidmobilepaymentsipgsdk.utils.showStatusMessage
import eu.icard.mobilepaymentssdk.ICardDirectSDK
import eu.icard.mobilepaymentssdk.model.CartItem
import eu.icard.mobilepaymentssdk.model.ClientDetails
import eu.icard.mobilepaymentssdk.model.StoredCard
import kotlinx.android.synthetic.main.fragment_shop.*
import java.util.*

class ShopFragment : Fragment(), ItemsAdapter.ItemsListener, View.OnClickListener {

    companion object {
        private const val GRID_ITEM_SPACING = 10 //dp
    }

    private val cartItems   = mutableListOf<CartItemModel>()

    private lateinit var preferences            : SharedPreferences

    private lateinit var shopItems              : List<CartItemModel>

    private var clientDetails                   : ClientDetails?        = null

    private var orderId     = ""

    private var amount      = 0.0

    private lateinit var gson: Gson

    var shopListener: ShopFragmentListeners?   = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gson                = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        loadCartItems()

        checkoutBtn.setOnClickListener(this)
    }

    private fun loadCartItems() {
        shopItems = mutableListOf(
            CartItemModel(
                1,
                "Run Max Shoes",
                "Some basic description",
                20.0,
                "EUR",
                R.drawable.run_max_shoes
            ),
            CartItemModel(
                2,
                "Run Max 4TS",
                "Some basic description",
                145.0,
                "EUR",
                R.drawable.run_max_4ts
            ),
            CartItemModel(
                3,
                "Sport Shoes TTX",
                "Some basic description",
                510.0,
                "EUR",
                R.drawable.sport_shoes_ttx
            ),
            CartItemModel(
                4,
                "Sport Shoes XS2",
                "Some basic description",
                145.0,
                "EUR",
                R.drawable.sport_shoes_xs2
            )
        )
        cartItemsRecyclerView.addItemDecoration(SpacesItemDecoration((GRID_ITEM_SPACING * resources.displayMetrics.density).toInt()))
        cartItemsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        cartItemsRecyclerView.adapter       = ItemsAdapter(shopItems, this)
    }

    private fun onPay() {
        val storedCards = preferences.getStringSet(Utils.PREFERENCES_STORED_CARDS, null)
        showPaymentOptions(storedCards)
    }

    private fun showPaymentOptions(storedCardsSet: Set<String>?) {
        val storedCards = mutableListOf<StoredCard>()
        storedCardsSet?.forEach {
            val storedCardModel = gson.fromJson(it, StoredCard::class.java)
            storedCards.add(storedCardModel)
        }
        storedCards.sortBy { it.cardCustomName }

        val paymentOptionsDialog = PaymentsOptionsDialog(requireContext(), storedCards, object : PaymentsOptionsDialog.OnPaymentOptionSelected {
            override fun onNewCardSelected() {
                iCardPurchase(null)
            }

            override fun onStoreCardAndPurchaseSelected() {
                storeCardAndPurchase()
            }

            override fun onStoredCardSelected(storeCard: StoredCard) {
                iCardPurchase(storeCard)
            }
        })
        paymentOptionsDialog.show()
    }

    private fun iCardPurchase(storedCard: StoredCard?) {
        orderId = System.currentTimeMillis().toString()
        amount  = cartItems.sumByDouble { it.price ?: 0.0 }
        val ipgCartItems = cartItems.map { CartItem().apply {
            article     = it.title ?: ""
            quantity    = cartItems.count { item -> item.id == it.id}
            price       = it.price ?: 0.0
            currency    = it.currency ?: ""
        }
        }
        ICardDirectSDK.purchase(
            context          = requireContext(),
            orderId          = orderId,
            ipgCartItems     = ipgCartItems,
            cardToken        = storedCard?.cardToken,            // optional
            purchaseListener = object : ICardDirectSDK.PurchaseListener {
                override fun purchaseSuccess(
                    transactionReference: String,
                    amount: Double,
                    currency: String
                ) {
                    showMessage("Purchase completed Transaction reference - $transactionReference")
                    val orderModel          = OrderModel()
                        .apply {
                            this.amount              = amount
                            this.currency            = currency
                            transactionType          = "Purchase"
                            date                     = Calendar.getInstance().time
                            this.tranRef             = transactionReference
                            orderId                  = this@ShopFragment.orderId
                        }

                    shopListener?.onSaveOrderInPreferences(orderModel)
                }

                override fun errorWithPurchase(status: Int) {
                    requireContext().showStatusMessage(status)
                }
            }
        )
    }

    private fun storeCardAndPurchase() {
        orderId = System.currentTimeMillis().toString()
        amount  = cartItems.sumByDouble { it.price ?: 0.0 }
        val ipgCartItems = cartItems.map { CartItem().apply {
            article     = it.title ?: ""
            quantity    = cartItems.count { item -> item.id == it.id}
            price       = it.price ?: 0.0
            currency    = it.currency ?: ""
        }
        }
        ICardDirectSDK.storeCardAndPurchase(
            context                      = requireContext(),
            orderId                      = orderId,
            ipgCartItems                 = ipgCartItems,
            storeCardAndPurchaseListener = object : ICardDirectSDK.StoreCardAndPurchaseListener {
                override fun storeCardAndPurchaseSuccess(storedCard: StoredCard, transactionReference: String, amount: Double, currency: String) {
                    val orderModel          = OrderModel()
                        .apply {
                            this.amount              = amount
                            this.currency            = currency
                            transactionType          = "Purchase"
                            date                     = Calendar.getInstance().time
                            this.tranRef             = transactionReference
                            orderId                  = this@ShopFragment.orderId
                        }

                    shopListener?.onSaveOrderInPreferences(orderModel)
                    shopListener?.onSaveStoredCardDataInPreferences(storedCard)
                    showMessage("Purchase completed Transaction reference - $transactionReference")
                }

                override fun errorWithStoreCardAndPurchase(status: Int) {
                    requireContext().showStatusMessage(status)
                }
            }
        )
    }

    override fun onItemAdded(cartItemModel: CartItemModel) {
        cartItems.add(cartItemModel)
        checkoutBtn.visibility  = View.VISIBLE
        showMessage("Item added to cart")
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            checkoutBtn.id -> onPay()
        }
    }
}