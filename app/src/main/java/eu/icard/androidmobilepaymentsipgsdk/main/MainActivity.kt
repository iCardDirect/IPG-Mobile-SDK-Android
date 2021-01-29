package eu.icard.androidmobilepaymentsipgsdk.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.model.OrderModel
import eu.icard.androidmobilepaymentsipgsdk.orders.OrdersActivity
import eu.icard.androidmobilepaymentsipgsdk.utils.Utils
import eu.icard.androidmobilepaymentsipgsdk.utils.showMessage
import eu.icard.androidmobilepaymentsipgsdk.utils.showStatusMessage
import eu.icard.mobilepaymentssdk.*
import eu.icard.mobilepaymentssdk.model.ClientDetails
import eu.icard.mobilepaymentssdk.model.StoredCard
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

interface ShopFragmentListeners {
    fun onSaveOrderInPreferences(orderModel: OrderModel)

    fun onSaveStoredCardDataInPreferences(storedCard: StoredCard)
}

class MainActivity : AppCompatActivity(), ShopFragmentListeners  {

    private lateinit var preferences            : SharedPreferences

    private lateinit var editor                 : SharedPreferences.Editor

    private var clientDetails                   : ClientDetails?        = null

    private var orderId     = ""

    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToShopFragment()

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor      = preferences.edit()

        setUpActionBar()

        gson                = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        clientDetails = ClientDetails().apply {
            name                    = "Name"
            billingAddressCity      = "Billing Address City"
            billingAddressCountry   = "Billing Address Country"
            billingAddress          = "Billing Address 1"
            billingAddressPostCode  = "Billing Address Post Code"
            emailAddress            = "email@test.com"
        }

        ICardDirectSDK.initialize(
            context             = this,
            mid                 = Utils.ICARD_MID,
            currency            = Utils.CURRENCY_CODE,
            clientPrivateKey    = Utils.CLIENT_PRIVATE_KEY,
            icardPublicKey      = Utils.SERVER_PUBLIC_KEY,
            originator          = Utils.ORIGINATOR,
            backendUrl          = Utils.SERVER_URL,
            taxUrl              = Utils.TAX_URL,
            keyIndex            = Utils.KEY_INDEX,
            isSandbox           = Utils.isSandbox
        )
        ICardDirectSDK.setupUISettings(
            isDarkMode                  = preferences.getBoolean(Utils.PREFERENCES_IS_DARK_MODE , false),
            font                        = preferences.getString(Utils.PREFERENCES_FONT          , ""),
            toolbarColor                = preferences.getInt(Utils.PREFERENCES_COLOR_ACCENT     , 0),
            toolbarTextColor            = preferences.getInt(Utils.PREFERENCES_TEXT_COLOR       , 0),
            buttonColor                 = preferences.getInt(Utils.PREFERENCES_COLOR_ACCENT     , 0),
            buttonTextColor             = preferences.getInt(Utils.PREFERENCES_TEXT_COLOR       , 0),
            merchantLogo                = ContextCompat.getDrawable(this, R.mipmap.mobile_payment_icon)
        )

        preferences.getString(Utils.PREFERENCES_LANGUAGE, "en")?.let {
            ICardDirectSDK.setLanguage(it)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar)
    }

    private fun iCardRefund(
        transactionRef: String,
        amount: Double,
        orderId: String
    ) {
        progressLayout.visibility = View.VISIBLE
        ICardDirectSDK.refundTransaction(transactionRef, amount, orderId, object :
            ICardDirectSDK.RefundListener {
            override fun refundSuccess(transactionReference: String, amount: Double, currency: String) {
                progressLayout.visibility = View.GONE

                val orderModel =
                    OrderModel(
                        orderId,
                        transactionRef,
                        Calendar.getInstance().time,
                        amount,
                        currency,
                        "Refund"
                    )
                saveOrderInPreferences(orderModel)
                showMessage(getString(R.string.refund_successful) + ": " + transactionReference)
            }

            override fun errorWithRefund(status: Int) {
                progressLayout.visibility = View.GONE
                showStatusMessage(status)
            }
        })
    }

    private fun iCardGetTransactionStatus(orderId: String) {
        progressLayout.visibility = View.VISIBLE
        ICardDirectSDK.getTransactionStatus(orderId, object :
            ICardDirectSDK.GetTransactionStatusListener {
            override fun transactionStatusSuccess(
                transactionStatus: Int,
                transactionReference: String
            ) {
                progressLayout.visibility = View.GONE

                val statusMsg = when(transactionStatus) {
                    ICardDirectSDK.TRANSACTION_SUCCESSFUL -> "Successful"

                    ICardDirectSDK.TRANSACTION_DECLINED -> "Declined"

                    else -> transactionStatus.toString()
                }

                val alertBuilder =
                    AlertDialog.Builder(this@MainActivity)
                alertBuilder.setTitle("Transaction status")
                alertBuilder.setMessage(
                    "Transaction type:\nPurchase\n\n" +
                            "OrderId:\n${orderId}\n\n"  +
                            "Transaction status:\n${statusMsg}\n\n" +
                            "Transaction Reference:\n${transactionReference}"
                )
                alertBuilder.setCancelable(true)
                alertBuilder.setPositiveButton("Ok", null)
                val alertDialog = alertBuilder.create()
                alertDialog.show()
            }

            override fun errorWithTransactionStatus(status: Int) {
                progressLayout.visibility = View.GONE
                showStatusMessage(status)
            }
        })
    }

    private fun iCardStoreNewCard() {
        orderId = System.currentTimeMillis().toString()

        ICardDirectSDK.storeCard(
            context             = this,
            orderId             = orderId,
            storeCardListener   = object : ICardDirectSDK.StoreCardListener {
                override fun storeCardSuccess(storedCard: StoredCard) {
                    saveStoredCardDataInPreferences(storedCard)
                }

                override fun errorWithStoreCard(status: Int) {
                    showStatusMessage(status)
                }
            }
        )
    }

    private fun iCardUpdateStoredCard(cardToken: String) {
        ICardDirectSDK.updateCard(
            context             = this,
            cardToken           = cardToken,
            updateCardListener = object : ICardDirectSDK.UpdateCardListener {
                override fun updateCardSuccess(storedCard: StoredCard) {
                    updateStoredCardListInPreferences(storedCard, cardToken)
                }

                override fun errorWithUpdateCard(status: Int) {
                    showStatusMessage(status)
                }
            }
        )
    }

    private fun saveStoredCardDataInPreferences(storedCard: StoredCard) {
        var storedCards         = preferences.getStringSet(Utils.PREFERENCES_STORED_CARDS, null)

        if (storedCards == null)
            storedCards         = LinkedHashSet()

        val newStoredCards: MutableSet<String> = LinkedHashSet()
        newStoredCards.addAll(storedCards)
        newStoredCards.add(gson.toJson(storedCard))
        editor.putStringSet(Utils.PREFERENCES_STORED_CARDS, newStoredCards)
        editor.apply()
        showMessage( getString(R.string.card_stored_successfully_card_token))
    }

    private fun saveOrderInPreferences(orderModel: OrderModel) {
        val orders             = preferences.getStringSet("orders", null)

        val newOrders          = mutableListOf<String>()
        newOrders.addAll(orders?.toMutableList() ?: mutableListOf())
        newOrders.add(gson.toJson(orderModel))

        editor.putStringSet("orders", newOrders.toSet())
        editor.apply()
    }

    private fun goToOrdersActivity() {
        val intent = Intent(this, OrdersActivity::class.java)
        startActivity(intent)
    }

    private fun showDialogAboutRefund() {
        val dialog  = RefundDialog(this) { tranRef: String, amount: Double, orderId: String ->
            iCardRefund(tranRef, amount, orderId)
        }
        dialog.show()
    }

    private fun showDialogForTransactionStatus() {
        val dialog = TransactionStatusDialog(this) {
            iCardGetTransactionStatus(it)
        }
        dialog.show()
    }

    private fun selectCardToUpdate() {

        val storedCards = preferences.getStringSet(Utils.PREFERENCES_STORED_CARDS, null)

        val storedCardsList = storedCards?.toMutableList()?.map { gson.fromJson(it, StoredCard::class.java) }

        storedCardsList?.sortedBy { it.cardCustomName }

        val paymentOptions = arrayOfNulls<CharSequence>(storedCardsList?.size ?: 0)
        storedCardsList?.forEachIndexed { index, storedCard ->
            paymentOptions[index] = storedCard.cardCustomName +
                    "(*" + storedCard.maskedPan?.substring(storedCard.maskedPan?.length?.minus(4) ?: 0) +
                    ", " + storedCard.cardExpDate?.substring(0, 2) +
                    "/" + storedCard.cardExpDate?.substring(2, 4) +
                    ", " + storedCard.cardType + ")"
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select card to update")
        builder.setItems(paymentOptions) { dialog, position -> storedCardsList?.get(position)?.cardToken?.let { iCardUpdateStoredCard(it) } }
        builder.show()
    }

    private fun updateStoredCardListInPreferences(newStoredCard: StoredCard, oldStoredCardToken: String) {
        showMessage(getString(R.string.card_updated_successfully_card_token))

        val storedCards         = preferences.getStringSet(Utils.PREFERENCES_STORED_CARDS, null)
        val storedCardsList     = storedCards?.toMutableList()?.map { gson.fromJson(it, StoredCard::class.java) }?.toMutableList()

        storedCardsList?.removeAll { it.cardToken == oldStoredCardToken }
        storedCardsList?.add(newStoredCard)
        val storedCardsJson = storedCardsList?.map { gson.toJson(it) }
        editor.putStringSet(Utils.PREFERENCES_STORED_CARDS, storedCardsJson?.toSet())
        editor.apply()
    }

    private fun showSettingsFragment() {
        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .add(R.id.rootView, fragment)
            .addToBackStack(SettingsFragment::class.java.name)
            .commit()
    }

    private fun goToShopFragment() {
        val shopFragment            = ShopFragment()
        shopFragment.shopListener   = this
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .add(R.id.rootView, shopFragment)
            .commit()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale             = Locale(language)
        val res     = context.resources
        val config             = Configuration(res.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.store_card -> {
                iCardStoreNewCard()
                true
            }
//            R.id.update_card -> {
//                selectCardToUpdate()
//                true
//            }
            R.id.refund -> {
                showDialogAboutRefund()
                true
            }
            R.id.get_transaction_status -> {
                showDialogForTransactionStatus()
                true
            }
            R.id.orders -> {
                goToOrdersActivity()
                true
            }
            R.id.settings -> {
                showSettingsFragment()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveOrderInPreferences(orderModel: OrderModel) {
        saveOrderInPreferences(orderModel)
    }

    override fun onSaveStoredCardDataInPreferences(storedCard: StoredCard) {
        saveStoredCardDataInPreferences(storedCard)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            updateResources(
                base,
                PreferenceManager.getDefaultSharedPreferences(base).getString(Utils.PREFERENCES_LANGUAGE, "en") ?: "en"
            )
        )
    }
}