package com.logunov.btcglance

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.logunov.btcglance.BitcoinWidget.Companion.KEY_LAST_UPDATED
import com.logunov.btcglance.BitcoinWidget.Companion.KEY_PRICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdatePriceAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = CryptoApiClient.api.getBitcoinPrice()
                val price = response.bitcoin["usd"]?.let { String.format("%,.2f", it) } ?: "N/A"

                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val time = sdf.format(Date())

                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs.toMutablePreferences()
                        .apply {
                            this[KEY_PRICE] = "$$price"
                            this[KEY_LAST_UPDATED] = time
                        }
                }

                BitcoinWidget().update(context, glanceId)
            } catch (e: Exception) {
                // Handle error (e.g., show "Error")
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs.toMutablePreferences()
                        .apply {
                            this[KEY_PRICE] = "Error"
                        }
                }
                BitcoinWidget().update(context, glanceId)
            }
        }
    }
}