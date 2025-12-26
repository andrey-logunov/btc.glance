package com.logunov.btcglance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.*
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.*

class BitcoinWidget : GlanceAppWidget() {

    companion object {
        val KEY_PRICE = stringPreferencesKey("btc_price")
        val KEY_LAST_UPDATED = stringPreferencesKey("last_updated")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    override val stateDefinition = PreferencesGlanceStateDefinition

    @Composable
    private fun Content() {
        val price = currentState<String>(key = KEY_PRICE) ?: "..."
        val lastUpdated = currentState<String>(key = KEY_LAST_UPDATED) ?: ""

        GlanceTheme {

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .background(GlanceTheme.colors.background),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BTC/USD",
                    style = TextStyle(color = ColorProvider(day = Color.White, night = Color.White))
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = price,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(
                            day = Color(0xFF00C853),
                            night = Color(0xFF00C853)
                        ) // green
                    )
                )

                if (lastUpdated.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Updated: $lastUpdated",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(day = Color.LightGray, night = Color.LightGray)
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                Button(
                    text = "Update",
                    onClick = actionRunCallback<UpdatePriceAction>(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ColorProvider(day = Color.White, night = Color.White)
                    )
                )
            }

        }
    }
}