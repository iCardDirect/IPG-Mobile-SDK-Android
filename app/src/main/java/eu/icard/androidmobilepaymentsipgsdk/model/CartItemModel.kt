package eu.icard.androidmobilepaymentsipgsdk.model

data class CartItemModel(
    val id: Long?               = null,

    val title: String?          = null,

    val description: String?    = null,

    val price: Double?          = null,

    var currency: String?       = null,

    var imageResource: Int?     = null
)