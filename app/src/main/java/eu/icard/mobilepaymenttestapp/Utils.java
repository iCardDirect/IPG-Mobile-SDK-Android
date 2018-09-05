package eu.icard.mobilepaymenttestapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.icard.mobilepaymentssdk.Currency;

/**
 * Created by kamen.troshev on 19.12.2016 г..
 */

class Utils {

    public static final String ICARD_MID                    = "112";
    public static final String CURRENCY_CODE                = Currency.EUR;
    public static final String CURRENCY_NAME                = "EUR";
    public static final String LANGUAGE                     = "EN";
    public static final int KEY_INDEX                       = 1;
    public static final float VERIFICATION_AMOUNT           = 1.00f;

    /* public static final String CLIENT_PRIVATE_KEY =
             "MIICXAIBAAKBgQDDqLMaPIpg45YjCMfYsPHpWeQPvCagDCHYgiHr7mGlufA/yngs" +
                     "zz01KpbcCAB6Qr2hxMIdzV3qHAjfiQOF+9oxm9c7ol2MK8f8nVQ03rGUfs9fEYBR" +
                     "PjM84j23eaYjbxpc3MKjGBxSJ50gFbHjrRhLG5USnGzMN+4xp24V63x2TQIDAQAB" +
                     "AoGActnmOHJtMC6oBOy0kuhbOIVBsFLbwXsdqv/Idbu6qhOZiXiKTpNf9IOJhqNT" +
                     "1HC06+6Zm/DfPfsy4jSFUvLhwtJAvovOOp30iRkWfg3VzrSv0Sujnb2/UQd7+aFb" +
                     "oMkquUU4RtrdD6rRP3xhJgKaPmnaGV05ZQVnM98IeRTIpIECQQDmgOiJ8tr1lkAy" +
                     "2klaZeVogpy47H9az6EYRpPHNwWBF+EZyCFrKkBu0S6KRpOdTZhJg35pw49zV//7" +
                     "iZ0GKHC9AkEA2U0cAWZ1Bt9zynNgrKLocifT0w46P7hsDqj+lRsVHSPwhcKDyLxI" +
                     "osppNX3ZhPn6mIRO7b3dY8nPunoXkk7c0QJALyeJ6saa0ojVQ1HylaKoxrOJmi8P" +
                     "cqVtIEk37BYucxVOgHa3l3PCUKlMaw87HYAFOmzDDKHsE72Z6XxieiMFxQJAKQ/J" +
                     "GvqhYosW9kqXGggupGOiQ1+M4j2XLa4BbWuQsdD4wk3fWS87Cof1GYaOc/JIyEk4" +
                     "IPSfwRuBhVtM2PjOAQJBAMz+chO2F5NThwte/gOFVM+ZfkM/qTVJJHsnk/Bb+gUm" +
                     "RAymNw5EKoUFi0yyRR6vA57eaWi3oUlZqcHuXGkItjM=";
     */
    public static final String CLIENT_PRIVATE_KEY =
            "MIICXgIBAAKBgQC+NIHevraPmAvx5//z38qjcqlCeyiLwXI5CRNZoL+Ms+/itElM" +
                    "ITVpaILCBF5+Uwp+A0pPYy/Gn9S+1gz/LL/mBDbWpTuMhHvEgJilX6CsVIah9/c/" +
                    "Bn8U3gT724aBhyIJeKVLO54pILKlkrKId4w76KDaouaFxyCECBMLaXQZoQIDAQAB" +
                    "AoGBAI0zVaYSVlzLNzLiU/Srkjc8i8K6wyLc/Pqybhb/arP9cHwP8sn9bTVPTKLT" +
                    "s4J8CzH5J1VAANunE7yIEyXsBphnr4lfC0ZPVHavPPBfFR/v9QVI1HByhnjihmG9" +
                    "uPZBuUAm/+s20rPOERepEMBmjpHnA7vTefMbtBXhRKbwszYxAkEA3Nl6ZmAIe50y" +
                    "yyK3IyCDYitqqQIpMDDTBs8Pn3L+Cen7+a5UXt2+mP87uJSid7m6qK6tQrdKBXgI" +
                    "TCMf9DZmBwJBANx6a9liZtQBM+GD0vAMZ3kTcBBKQe/c63pPpDBRSbiIgdhKJzcD" +
                    "lfJoGL6wl2QI2NHhXc9eaH6gVGOsBQYD2RcCQQCVYp4Cpa7XPqve7+qE3jdArjGF" +
                    "hKqrqDr1/hWJO1VPC3CfoSX8zW1hPDP/VLrY1U7HTvBvkl+Fd33VUmUI4cr9AkAR" +
                    "PBSgKpwFKI7oqwhbMW0JPua8r0FWQbu6lO0txbzwiuMziCBmoYYgK9j7VwyOik6A" +
                    "oZBWvHeIpnnSTMkbvkNDAkEAvYoCwTJWAGYUDSSLSN+nP1nmrbyJVSSJMNNQ5974" +
                    "bBzRvEz9OIgvFL2LslY3kBdwE5JIFacyvDXBVUVqv7MdlQ==";

    public static final String SERVER_PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4ur+fZBqNjnm1XJSJrzf8vyIv" +
                    "xfXew44RKJv9kpPiSEtGaRiAmqZhMWsW/fD2Drnh1A6gCgfWIv/3Zgr18GZ/Heqm" +
                    "h5n9HmQndHAB2nZnFLOioL9v6awAbqVeqYBMzp97UkruxXDtqejL7w8WkxearqpU" +
                    "BBbcPHA2gMp0hRN/MwIDAQAB";

    /*public static final String SERVER_PUBLIC_KEY =
            "MIIBkDCB+qADAgECAgAwDQYJKoZIhvcNAQEFBQAwDzENMAsGA1UEChMEaVBheTAe" +
                    "Fw0xNjA5MjgxMTI2MjRaFw0yNjA5MjYxMTI2MjRaMA8xDTALBgNVBAoTBGlQYXkw" +
                    "gZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAMGKgmD9E2EO4O6H8vyCuS/O9qxs" +
                    "YRy4QBbmK4z1HJ2pLr/d217Ifxrs5NlhHY8OpEKllXbFfRNjDTygrDwPRZ9HrweX" +
                    "5NbLOIYdRIPPr2kjbCxxPBJX+Fx7gE+o6e3WqegIHNTMOchZt+RghCFKV26mdnSf" +
                    "htXTChDKmgrPadGZAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEAqsGqlGMb3fHi6Crm" +
                    "O2ufLlAoqAgOvimj+EzdrDbVt6yg4OZGjhu2AZL7K5w37oxrflGEefEELp1dli4E" +
                    "sZa0C8FEEZp8Fg4GnF5uizNEnf30i4jMnyRdWcu7M5xReLAl3JDLQ4XrW7wPMyEZ" +
                    "3JV3s+S37mucl07z+7FAU3sQDbg=";*/

    public static final boolean isSandbox = true;

    public static final String PREFERENCES_STORED_CARDS = "stored_cards";

    public static String formatAmount(float amount){
        try {
            String pattern = "0.00";
            NumberFormat numberFormat = new DecimalFormat(pattern);
            return numberFormat.format(amount).replace(',', '.');
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String formatAmount(String amount){
        try {
            String pattern = "0.00";
            NumberFormat numberFormat = new DecimalFormat(pattern);

            if (!amount.equalsIgnoreCase("")) {
                double dAmntFrom = Double.parseDouble(amount);
                return numberFormat.format(dAmntFrom).replace(',', '.');
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static void showToastMessage(final Context context, final String message){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if (textView != null) textView.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
    }

    public static void addFocusChangeListener(final Context context, final TextView title, EditText editText, final View underline){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if( focused){
                    title.setTextColor(ContextCompat.getColor(context, eu.icard.mobilepaymentssdk.R.color.iCardFocusedColor));
                    underline.setBackgroundColor(ContextCompat.getColor(context, eu.icard.mobilepaymentssdk.R.color.iCardFocusedColor));
                }
                else{
                    title.setTextColor(ContextCompat.getColor(context, eu.icard.mobilepaymentssdk.R.color.iCardEdtTitleTextColor));
                    underline.setBackgroundColor(ContextCompat.getColor(context, eu.icard.mobilepaymentssdk.R.color.iCardUnderlineColor));
                }
            }
        });
    }

    public static void addOrderToPreferences(Context context, OrderModel order){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> orders = sharedPreferences.getStringSet("orders", null);

        if (orders == null)
            orders = new LinkedHashSet<>();

        Set<String> newOrders = new LinkedHashSet<>();
        newOrders.addAll(orders);
        newOrders.add(order.toJson());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("orders", newOrders);
        editor.apply();
    }
}
