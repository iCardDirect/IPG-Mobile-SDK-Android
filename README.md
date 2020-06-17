# iCard Mobile Checkout Android SDK
Accepting mobile payments for merchants

### Table of Contents

* [Security and availability](#security-and-availability)

* [Introduction](#introduction)

* [Integration](#integration)
  
  * [Requirements](#requirements)
  
  * [Setup](#setup)
  
  * [Perform a Payment](#perform-a-payment)
  
  * [Add a Card](#add-a-card)
  
  * [Perform a Payment with stored card](#perform-a-payment-with-stored-card)
  
  * [Perform a Refund](#perform-a-refund)
  
  * [Check transaction status](#check-transaction-status)
  
* [UI customization](#ui-customization)

  * [Hide custom name field](#hide-custom-name-field)
  
  * [Set custom banner](#set-custom-banner)

  * [Configuring displayed colors](#configuring-displayed-colors)
  
  * [Configuring displayed text](#configuring-displayed-text)
  
  
# Security and availability
  
  Connection between Merchant and iCARD is handled through internet using HTTPS protocol (SSL over HTTP). Requests and responses are digitally signed both. iCARD host is located at tier IV datacenter in Luxembourg. The system is designed specifically for the unique challenges of mobile fraud and comes as standard in our SDK. It is powered by the latest machine learning algorithms, as well as trusted methodologies. The SDK comes with built-in checks and security features, including real-time error detection, 3D Secure, data and address validation for frictionless card data capture.
  
  Exchange folder for partners (if needed) is located at a SFTP server which enables encrypted file sharing between parties. The partner receives the account and password for the SFTP directory via fax, email or SMS.
  
  # Introduction
  
  This document describes the methods and interface for mobile checkout. The Merchants should integrate the iCard Mobile Checkout Android SDK to their mobile apps to accept card payments. The iCard Mobile Checkout Android SDK will gain access to the entry point of the payment gateway managed by Intercard Finance AD (iCARD). The cardholder will be guided during the payment process and iCARD will check the card sensitive data and will process a payment transaction through card schemes (VISA and MasterCard).
  
  The iCard Mobile Checkout Android SDK will provide:
  * Secured communication channel with the Merchant
  * Storing of merchant private data (shopping cart, amount, payment methods, transaction details etc.)
  * Financial transactions to VISA, MasterCard – transparent for the Merchant
  * Operations for the front-end: Purchase transaction
  * Operations for the back-end: Refund, Void, Payment
  * 3D processing
  * Card Storage

  Out of scope for this document: 
  * Merchant statements and payouts
  * Merchant back-end
  
  The purpose of this document is to specify the iCard Mobile Checkout Android SDK Interface and demonstrate how it is used in the most common way.
  All techniques used within the interface are standard throughout the industry and should be very easy to implement on any platform.
  
  
  # Integration
  
  A “by appointment” test service is available which allows the validation of the iCard Mobile Checkout Android SDK calls. Testers   should negotiate an exclusive access to the testing service and ensure monitoring by iCARD engineer.
  
  ## Requirements
  
  * Android Studio 2.0 or higher
  * Android SDK Build Tools 25.0.0
  * The minSdkVersion set to 16 (Jelly Bean) or higher
  * Latest Android Support Repository
  * Latest Android Support Library

  ## Setup
  
  Start using iCard Mobile Checkout Android SDK by initializing the setup. Simply fill Originator, MID and secret key provided upon integration and add them to your app's main Activity class. Test settings are ready to use in the test app. Live settings will be kindly provided to you upon integration process.
  
  #### Steps to Import Module in Android Studio:
  
  1. Go to File -> New -> Import Module
  2. Select the mobilepaymentsdk.aar from IPG-Mobile-SDK-Android/mobilepaymentssdk folder and click Finish.
  3. Go to File -> Project Structure -> Dependencies
  4. Click the (+) icon and select Module Dependency. Select the module and click Ok.
  5. Open your build.gradle file and check that the module is now listed under dependencies.
  
```Java
protected void onCreate(Bundle savedInstanceState) {
...
 ICard iCard = ICard.getInstance();
 iCard.init(
            “112”,                /*MID*/
            33,               	  /*Originator ID*/
            “EUR”,                /*currency ISO code*/
            “MIICXAIBAAKBg ...”,  /*client private key*/
            ”MIIBkDCB+q ...”,     /*server public key*/
            true,                 /*is Sandbox*/
 );
 iCard.setKeyIndex(1); 
 iCard.setLanguage(“EN”);

...
}
```

The SDK allows further configuration by using the existing settings. These are the options:
  * Supported card networks – Allows you to determine the accepted card networks when using your app. The default value includes Visa, Visa Electron, MasterCard, Maestro and VPay.
  * Address Verification Service (AVS) – You will be able to capture the consumer’s country and postcode as an additional security layer.
  
  ## Perform a Payment
    
    Create an intent for the Purchase Activity with the required:
 
```Java
ArrayList<CartItem> mIPGCartItems;
public void onPayBtnClick(View view) {
...
  Intent intent = new Intent(this, PurchaseActivity.class);
  intent.putExtra(ICard.INTENT_EXTRA_CART_ITEMS , mIPGCartItems);
  intent.putExtra(ICard.INTENT_EXTRA_ORDER_ID   , “12345678”);
  startActivityForResult(intent, ICard.REQUEST_CODE_PURCHASE);
...
}
```
Note: Please make sure that you are using a unique Order ID.

  In your calling Activity, override the onActivityResult method to receive a reference of the payment card, customer ID and transaction reference from Performing a Payment:
  
```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if( resultCode == RESULT_OK && requestCode == ICard.REQUEST_CODE_PURCHASE ) {
      int status = data.getIntExtra(ICard.INTENT_EXTRA_STATUS, ICard.STATUS_INTERNAL_API_ERROR);
        
      if( status == ICard.STATUS_SUCCESS)
        String tranRef = data.getStringExtra(ICard.INTENT_EXTRA_TRANSACTION_REFERENCE);
    }
}
```
  
## Add a Card

 Create an Intent for the StoreCardActivity with the required Intent extras:

```Java
public void onAddCardBtnClick(View view) {
...
  Intent intent = new Intent(this, StoreCardActivity.class);
  intent.putExtra(ICard.INTENT_EXTRA_VERIFICATION_AMOUNT, 0.00 /*verification amount*/);
  startActivityForResult(intent, ICard.REQUEST_CODE_STORE_CARD);
...
}
```
  
 Note: Please make sure that you are using a unique Reference ID for each different consumer.
 
 In your calling Activity, override the onActivityResult method to receive a card reference for the linked card:
 
 ```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if( resultCode == RESULT_OK  && requestCode == ICard.REQUEST_CODE_STORE_CARD){
    int status = data.getIntExtra(ICard.INTENT_EXTRA_STATUS,  ICard.STATUS_INTERNAL_API_ERROR);
   
    if( status == ICard.STATUS_SUCCESS)
      StoredCardModel storedCard = data.getParcelableExtra(ICard.INTENT_EXTRA_STORED_CARD);
  }
}
 ```
 
 ## Perform a Payment with stored card
 
 Create an Intent for the PurchaseActivity with the required Intent extras:
 
```Java
public void onPayWithCardBtnClick(View view) {
...
  Intent intent = new Intent(this, PurchaseActivity.class);
  intent.putExtra(ICard.INTENT_EXTRA_CART_ITEMS , mIPGCartItems);
  intent.putExtra(ICard.INTENT_EXTRA_ORDER_ID   , “12345678”);
  intent.putExtra(ICard.INTENT_EXTRA_CARD_TOKEN , “card token”);
  startActivityForResult(intent, ICard.REQUEST_CODE_PURCHASE);
...
}
```
Note: Please make sure that you are using a unique Order ID.

In your calling Activity, override the onActivityResult method to receive a reference of the payment card, customer ID and transaction reference from Performing a Payment:

```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
...
  if( resultCode == RESULT_OK  && requestCode == ICard.REQUEST_CODE_PURCHASE) {
    int status = data.getIntExtra(ICard.INTENT_EXTRA_STATUS,  ICard.STATUS_INTERNAL_API_ERROR);
        
      if( status == ICard.STATUS_SUCCESS)
        String tranRef = data.getStringExtra(ICard.INTENT_EXTRA_TRANSACTION_REFERENCE);
    }
...
}
```

 ## Perform a Refund

Refunding a payment requires that you have the transactionRef of the payment transaction. Check that you have initialized the SDK before attempting to perform a refund.

Create an Intent for the RefundActivity with the required Intent extras:

```Java
public void onRefundBtnClick(View view) {
...
  Intent intent = new Intent(this, RefundActivity.class);
  intent.putExtra(ICard.INTENT_EXTRA_TRANSACTION_REFERENCE  , "transactionRef");
  intent.putExtra(ICard.INTENT_EXTRA_AMOUNT                 , 10.00);

  if( !orderId.equalsIgnoreCase(""))
    intent.putExtra(ICard.INTENT_EXTRA_ORDER_ID, orderId);

  startActivityForResult(intent, ICard.REQUEST_CODE_REFUND);
...
}
```
Note: Please make sure that you are using the correct Transaction Reference ID for the transaction that you want to be refunded.

In your calling Activity, override the onActivityResult method to receive a transaction reference of the operation and the reference of the original transaction:

```Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
...
  if( resultCode == RESULT_OK  && requestCode == ICard.REQUEST_CODE_REFUND){
    int status = data.getIntExtra(ICard.INTENT_EXTRA_STATUS,  ICard.STATUS_INTERNAL_API_ERROR);
    
    if( status == ICard.STATUS_SUCCESS) {
      float amount                = data.getFloatExtra(ICard.INTENT_EXTRA_AMOUNT, 0.00f);
      String currency             = data.getStringExtra(ICard.INTENT_EXTRA_CURRENCY);
      String transactionReference = data.getStringExtra(ICard.INTENT_EXTRA_TRANSACTION_REFERENCE);
    }
  }
...
}
```

 ## Check transaction status
 
 You can choose between the transaction types of Purchase or Refund and to send the order ID of which transaction status needed to be checked. The method will retrieve the transaction type, order ID, transaction status and the transaction reference:
 
```Java
IPGGetTransactionStatus ipgGetTransactionStatus = new IPGGetTransactionStatus();
ipgGetTransactionStatus.setOrderId(orderId);
ipgGetTransactionStatus.setTxtType(txnType);
ipgGetTransactionStatus.setOnCommandCompleteListener(new IPGGetTransactionStatus.OnCommandCompleteListener() {
    @Override
    public void onCommandComplete(final int transactionStatus, final String transactionReference) { }

    @Override
    public void onError(final int status) { }
});

ipgGetTransactionStatus.sendRequest();
```

# UI customization

Use iCard Mobile Checkout Android SDK UI components for a frictionless checkout in your app. Minimize your PCI scope with a UI that can be themed to match your brand colors.

Built-in features include quick data entry, optional security checks and fraud prevention that let you focus on developing other areas of your app.

The iCard Mobile Checkout Android SDK supports a range of UI customization options to allow you to match payment screen appearance to your app's branding.

## Hide custom name field

Pass true in the intent opening StoreCardActivity or PurchaseActivity if you don't want to show 'Custom name' field like this:
```Java
intent.putExtra(ICard.INTENT_EXTRA_WITHOUT_CUSTOM_NAME, true);
```

## Set custom banner

There is a possibility to set your merchant brand logo or banner in the action bar on StoreCardActivity, UpdateCardActivity and PurchaseActivty. The image should be added in your project as a resource and the resource ID should be passed like this:
```Java
intent.putExtra(ICard.INTENT_EXTRA_CUSTOM_LOGO_RESOURCE, R.drawable.merchang_banner);
```

## Configuring displayed colors

Create a new theme that has a theme as a parent, in this example we'll customize the button color:

```Java
<style name="AppTheme" parent="ICard">
    <item name="colorButtonNormal">#F68121</item>
    <item name="colorAccent">@android:color/white</item>
</style>
```
Depending on the styles used in your app, you could customize colors of the following elements:
  * Field on focus
  * Hint text in the fields
  * Entered text colors
  * Field under line color
  * Buttons colors
  
```Java
<color name="iCardFocusedColor"       tools:override="true">#ffa500</color>
<color name="iCardEditTextHintColor"  tools:override="true">#9c9c9c</color>
<color name="iCardEditTextColor"      tools:override="true">#444444</color>
<color name="iCardEdtTitleTextColor"  tools:override="true">#bbbbbb</color>
<color name="iCardUnderlineColor"     tools:override="true">#bbbbbb</color>
<color name="iCardsButtonColor"       tools:override="true">#008000</color>
<color name="iCardButtonTextColor"    tools:override="true">#ffffff</color>
<color name="iCardTitleTextColor"     tools:override="true">#ffffff</color>
<color name="iCardBackgroundColor"    tools:override="true">#ffffff</color>

```

## Configuring displayed text
  
  To provide the best user experience, you could customize strings for all languages you want. When changing text labels, consider using text that gives a clear intention to user actions.
  
```Java
<resources>
    <string name="app_name"                     tools:override="true">MobilePaymentsSdk</string>
    <string name="verifying_dots"               tools:override="true">Verifying...</string>
    <string name="payment"                      tools:override="true">Payment</string>
    <string name="card_number"                  tools:override="true">Card number</string>
    <string name="enter_card_number"            tools:override="true">Enter card number</string>
    <string name="expiry_date"                  tools:override="true">Expiry date</string>
    <string name="mm_yy"                        tools:override="true">MM/YY</string>
    <string name="cvc"                          tools:override="true">CVC</string>
    <string name="enter_cvc_code"               tools:override="true">Enter CVC code</string>
    <string name="emboss_name"                  tools:override="true">Cardholder name</string>
    <string name="enter_emboss_name"            tools:override="true">Enter cardholder name</string>
    <string name="custom_name"                  tools:override="true">Custom name</string>
    <string name="enter_custom_name"            tools:override="true">Enter custom name</string>
    <string name="pay"                          tools:override="true">Pay</string>
    <string name="operation_failed"             tools:override="true">Operation failed</string>
    <string name="refund"                       tools:override="true">Refund</string>
    <string name="invalid_card_details"         tools:override="true">Invalid card details</string>
    <string name="store_new_card"               tools:override="true">Store new card</string>
    <string name="store_card"                   tools:override="true">Store card</string>
    <string name="update_stored_card"           tools:override="true">Update stored card</string>
    <string name="update_card"                  tools:override="true">Update card</string>
    <string name="invalid_card_pan"             tools:override="true">Invalid PAN</string>
    <string name="invalid_card_exp_date"        tools:override="true">Invalid expiry date</string>
    <string name="invalid_card_cvc"             tools:override="true">Invalid CVC</string>
    <string name="invalid_card_emboss_name"     tools:override="true">Invalid cardholder name</string>
    <string name="invalid_card_custom_name"     tools:override="true">Invalid custom name</string>
</resources>
```


