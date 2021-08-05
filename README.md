# iCard Mobile Checkout Android SDK
Accepting mobile payments for merchants

### Table of Contents

* [Security and availability](#security-and-availability)

* [Introduction](#introduction)

* [Integration](#integration)
  
  * [Requirements](#requirements)
  
  * [Setup](#setup)
  
  * [Initialization](#initialization)
  
  * [Make a payment with a new or already stored card](#make-a-payment-with-a-new-or-already-stored-card)
  
  * [Make a payment and store card](#make-a-payment-and-store-card)
  
  * [Add a Card](#add-a-card)
   
  * [Perform a Refund](#perform-a-refund)
  
  * [Check transaction status](#check-transaction-status)
  
* [UI customization](#ui-customization)

* [Enumerators](#enumerators)
  
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
  
  * Android Studio 4.0 or higher
  * Android SDK Build Tools 30.0.2
  * Kotlin version 1.3 or higher
  * The minSdkVersion set to 19 (KitKat) or higher
  * Latest Android Support Repository
  * Latest Android Support Library

  ## Setup
  
  Start using iCard Mobile Checkout Android SDK by initializing the setup. Simply fill Originator, MID and secret key provided upon integration and add them to your app's main Activity class. Test settings are ready to use in the test app. Live settings will be kindly provided to you upon integration process.
  
  1. Add following code into the Mobile App project configuration in Build.Gradle:
      ```Kotlin 
      maven { url 'https://icarddirect.android.sdk.icard.com/artifactory/iCardDirectRepository'}  
      ```
  2. Add following code into the Mobile App project configuration in App Build.Gradle
      ```Kotlin
      implementation 'com.icard.icarddirect:mobilepaymentssdk:1.4.0'  
      ```

  ## Initialization

```Kotlin
ICardDirectSDK.initialize( 
                            context           = this,
                            mid               = “112”,
                            currency          = “EUR”,
                            clientPrivateKey  = “MIICXAIBAAKBg ...”,
                            icardPublicKey    = ”MIIBkDCB+q ...”,
                            originator        = 33,
                            backendUrl        = "https://callback.url/",
                            taxUrl            = "",
                            keyIndex          = 1,
                            isSandbox         = true 
                            language          = “en”  // Available languages en, bg, de, es, it, nl, ro. Translation is managed by SDK.
)
```

Additional information:
  * At backendUrl you will be notified about payment status after completion. Below are the returned parameters. For more information about signature verification please visit our documentation [here](https://icard.direct/documents/IPG_API_v3.4_22.pdf).
  
```Kotlin
    IPGmethod       => IPGPurchaseOK 
    MID             => '000000000000123'
    OrderID         => '1854'
    Amount          => '23.45'
    Currency        => '978'
    CustomerIP      => '82.119.81.30'
    CardType        => 'MasterCard'
    Pan             => '4567'
    ExpdtYYMM       => '2112'
    Approval        => 'MSQI258'
    IPG_Trnref      => '20210716141055303234'
    RequestSTAN     => '349875'
    RequestDateTime => '2021-07-16 14:10:55'
    Signature       => 'kcBs8LoJkXZlclhpykaWIx............'
```

## Make a payment with a new or already stored card
    
```Kotlin
  ICardDirectSDK.purchase(
        context           = requireContext(),
        orderId           = orderId,
        ipgCartItems      = ipgCartItems,
        cardToken         = storedCard?.cardToken,  // optional if you pay with stored card
        purchaseListener  = object : ICardDirectSDK.PurchaseListener {
            override fun purchaseSuccess( transactionReference: String, amount: Double, currency: String ) {
                /*your code here*/
            }
            override fun errorWithPurchase(status: Int) {
                /*your code here*/
            }
        }
    )
```
Note: Please make sure that you are using a unique Order ID.

## Make a payment and store card
 ```Kotlin
         ICardDirectSDK.storeCardAndPurchase(
                context                       = this,
                orderId                       = orderId,
                ipgCartItems                  = ipgCartItems,
                storeCardAndPurchaseListener  = object : StoreCardAndPurchaseListener {
                    override fun storeCardAndPurchaseSuccess(storedCard: StoredCard, transactionReference: String, amount: Double, currency: String) {
                        /*your code here*/
                    }

                    override fun errorWithStoreCardAndPurchase(status: Int) {
                        /*your code here*/
                    }
                }
        )
 ```
 Note: Please make sure that you are using a unique Order ID.
 
## Add a Card

 ```Kotlin
ICardDirectSDK.storeCard(
                context             = this,
                orderId             = orderId,
                storeCardListener   = object : ICardDirectSDK.StoreCardListener {
                    override fun storeCardSuccess(storedCard: StoredCard) {
                        /*your code here*/
                    }
    
                    override fun errorWithStoreCard(status: Int) {
                        /*your code here*/
                    }
                }
            )
```
Note: Please make sure that you are using a unique Order ID.
 

 ## Perform a Refund

Refunding a payment requires that you have the transactionRef of the payment transaction.

```Kotlin
ICardDirectSDK.refundTransaction(transactionRef, amount, orderId, object :
            ICardDirectSDK.RefundListener {
            override fun refundSuccess(transactionReference: String, amount: Double, currency: String) {
              /*your code here*/
            }

            override fun errorWithRefund(status: Int) {
              /*your code here*/
            }
        })
```
Note: Please make sure that you are using the correct Transaction Reference ID for the transaction that you want to be refunded.

 ## Check transaction status
 
 The method will retrieve the transaction status and the transaction reference:
 
```Kotlin
ICardDirectSDK.getTransactionStatus(orderId, object :
            ICardDirectSDK.GetTransactionStatusListener {
              override fun transactionStatusSuccess( transactionStatus: Int, transactionReference: String ) {
                  /*your code here*/
              }

              override fun errorWithTransactionStatus(status: Int) {
                /*your code here*/
              }
            })
```
Note: Please make sure that you are using a correct Order ID.

# UI customization

Use iCard Mobile Checkout Android SDK UI components for a frictionless checkout in your app. Minimize your PCI scope with a UI that can be themed to match your brand colors.

```Kotlin
ICardDirectSDK.setupUISettings(
          isDarkMode      = true/false,
          font            = ICardDirectSDK.FONT_CAROSSOFT_LIGHT,
          buttonColor     = R.color.YOUR_COLOUR,
          buttonTextColor = R.color.YOUR_COLOUR,
          merchantLogo    = null
          toolbarColor    = R.color.YOUR_COLOUR,
          toolbarTextColor= R.color.YOUR_COLOUR
)
```

 # Enumerators
 
 Operation statuses :
 
 ```Kotlin
ICardDirectSDK.STATUS_INTERNAL_API_ERROR                      
ICardDirectSDK.STATUS_COMPLETED_SUCCESSFUL                    
ICardDirectSDK.STATUS_TECHNICAL_ISSUE_REJECTED_BY_ICARD       
ICardDirectSDK.STATUS_INVALID_REQUEST_REJECTED_BY_ICARD       
ICardDirectSDK.STATUS_RISK_ASSESSMENT_REJECTED_BY_ICARD       
ICardDirectSDK.STATUS_REJECTED_BY_ISSUER                      
ICardDirectSDK.STATUS_INSUFFICIENT_FUNDS_REJECTED_BY_ISSUER   
ICardDirectSDK.STATUS_RISK_ASSESSMENT_REJECTED_BY_ISSUER      
ICardDirectSDK.STATUS_INVALID_CARD_REJECTED_BY_ISSUER         
ICardDirectSDK.STATUS_INVALID_AMOUNT_REJECTED_BY_ISSUER       
ICardDirectSDK.STATUS_FAILED_3DS                              
ICardDirectSDK.STATUS_3DS_USER_INPUT_TIME_OUT                 
ICardDirectSDK.STATUS_NO_CUSTOMER_INPUT_OR_3DS_RESPONSE       
ICardDirectSDK.STATUS_CANCELED_BY_THE_CUSTOMER_NO_3DS_RESPONSE
ICardDirectSDK.STATUS_REVERSED                                
ICardDirectSDK.STATUS_INTERNAL_ERROR                          
ICardDirectSDK.STATUS_NOT_FOUND                                  
```

Card types :

 ```Kotlin
ICardDirectSDK.CARD_TYPE_MASTERCARD      
ICardDirectSDK.CARD_TYPE_MAESTRO         
ICardDirectSDK.CARD_TYPE_VISA            
ICardDirectSDK.CARD_TYPE_ELECTRON        
ICardDirectSDK.CARD_TYPE_VPAY            
ICardDirectSDK.CARD_TYPE_JCB             
ICardDirectSDK.CARD_TYPE_BAN_CONTACT     
ICardDirectSDK.CARD_TYPE_AMERICAN_EXPRESS
ICardDirectSDK.CARD_TYPE_UNION_PAY                                
```

Fonts :

```Kotlin
ICardDirectSDK.FONT_CAROSSOFT       
ICardDirectSDK.FONT_CAROSSOFT_LIGHT
ICardDirectSDK.FONT_LATO           
ICardDirectSDK.FONT_MONTSERRAT     
ICardDirectSDK.FONT_OPEN_SANS      
ICardDirectSDK.FONT_RALEWAY        
ICardDirectSDK.FONT_ROBOTO_SLAB    
ICardDirectSDK.FONT_SF_PRO    
```
