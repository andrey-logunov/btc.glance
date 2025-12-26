package com.logunov.btcglance

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdatePriceAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val priceValue = CryptoApiClient.fetchBitcoinPrice()
                ?: throw Exception("No price returned")

            val price = String.format("$%,.2f", priceValue)

            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val time = sdf.format(Date())

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[BitcoinWidget.KEY_PRICE] = price
                prefs[BitcoinWidget.KEY_LAST_UPDATED] = time
            }

            BitcoinWidget().update(context, glanceId)
        } catch (e: Exception) {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[BitcoinWidget.KEY_PRICE] = "Error: ${e.message}"
            }
            BitcoinWidget().update(context, glanceId)
        }
    }
}