package eu.icard.androidmobilepaymentsipgsdk.utils

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.widget.Toast
import androidx.fragment.app.Fragment
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.mobilepaymentssdk.ICardDirectSDK
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_3DS_USER_INPUT_TIME_OUT
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_CANCELED_BY_THE_CUSTOMER_NO_3DS_RESPONSE
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_COMPLETED_SUCCESSFUL
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_FAILED_3DS
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_INSUFFICIENT_FUNDS_REJECTED_BY_ISSUER
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_INTERNAL_ERROR
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_INVALID_AMOUNT_REJECTED_BY_ISSUER
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_INVALID_CARD_REJECTED_BY_ISSUER
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_INVALID_REQUEST_REJECTED_BY_ICARD
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_NOT_FOUND
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_NO_CUSTOMER_INPUT_OR_3DS_RESPONSE
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_REJECTED_BY_ISSUER
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_REVERSED
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_RISK_ASSESSMENT_REJECTED_BY_ICARD
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_RISK_ASSESSMENT_REJECTED_BY_ISSUER
import eu.icard.mobilepaymentssdk.ICardDirectSDK.STATUS_TECHNICAL_ISSUE_REJECTED_BY_ICARD
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.formatAmount() : String {
    try {
        val pattern = "0.00"
        val numberFormat: NumberFormat = DecimalFormat(pattern)
        return numberFormat.format(this).replace(',', '.')
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun Activity.showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showMessage(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun String.getSpannableCurrency(currencySymbol: String) : SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        RelativeSizeSpan(1.3f),
        if(indexOf(currencySymbol) == 0) currencySymbol.length else 0,
        if(indexOf(currencySymbol) == 0) length else indexOf(currencySymbol),
        0
    )
    return spannableString
}

fun String.getCurrencySymbol() : String {
    return when(this) {
        "EUR" -> "€"

        else -> ""
    }
}

fun Date.formatDate(format: String) : String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun Context.showStatusMessage(status: Int) {
    val message = when(status) {
        STATUS_COMPLETED_SUCCESSFUL -> R.string.operation_completed_successful

        STATUS_TECHNICAL_ISSUE_REJECTED_BY_ICARD -> R.string.operation_failed_please_contact_us_for_more_details

        STATUS_INVALID_REQUEST_REJECTED_BY_ICARD -> R.string.operation_failed_please_contact_us_for_more_details

        STATUS_RISK_ASSESSMENT_REJECTED_BY_ICARD -> R.string.operation_failed_please_contact_us_for_more_details

        STATUS_REJECTED_BY_ISSUER -> R.string.operation_failed_please_contact_your_card_issuer_for_more_details

        STATUS_INSUFFICIENT_FUNDS_REJECTED_BY_ISSUER -> R.string.insufficient_funds_please_contact_your_card_issuer

        STATUS_RISK_ASSESSMENT_REJECTED_BY_ISSUER -> R.string.operation_failed_please_contact_your_card_issuer_for_more_details

        STATUS_INVALID_CARD_REJECTED_BY_ISSUER -> R.string.operation_failed_please_check_your_card_details

        STATUS_INVALID_AMOUNT_REJECTED_BY_ISSUER -> R.string.invalid_amount_please_try_again_or_contact_your_card_issuer

        STATUS_FAILED_3DS -> R.string.secure_3d_verification_failed

        STATUS_3DS_USER_INPUT_TIME_OUT -> R.string.operation_failed_please_try_again

        STATUS_NO_CUSTOMER_INPUT_OR_3DS_RESPONSE -> R.string.operation_failed_please_try_again

        STATUS_CANCELED_BY_THE_CUSTOMER_NO_3DS_RESPONSE -> R.string.operation_cancelled

        STATUS_REVERSED -> R.string.operation_failed_please_contact_us_for_more_details

        STATUS_INTERNAL_ERROR -> R.string.operation_failed_please_contact_us_for_more_details

        STATUS_NOT_FOUND -> R.string.operation_failed_please_contact_us_for_more_details

        else -> R.string.operation_failed_please_try_again
    }

    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Int.getCardTypeImageResource() : Int {
    return when(this) {
        ICardDirectSDK.CARD_TYPE_MASTERCARD -> eu.icard.mobilepaymentssdk.R.drawable.mastercard_logo

        ICardDirectSDK.CARD_TYPE_MAESTRO -> eu.icard.mobilepaymentssdk.R.drawable.maestro_logo_icon

        ICardDirectSDK.CARD_TYPE_VISA -> eu.icard.mobilepaymentssdk.R.drawable.visa_logo_icon

        else -> 0
    }
}