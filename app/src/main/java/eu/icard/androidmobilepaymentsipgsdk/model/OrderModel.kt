package eu.icard.androidmobilepaymentsipgsdk.model

import java.util.*

data class OrderModel(

    var orderId: String         = "",

    var tranRef: String         = "",

    var date: Date?             = null,

    var amount: Double          = 0.0,

    var currency: String        = "",

    var transactionType: String = ""
)