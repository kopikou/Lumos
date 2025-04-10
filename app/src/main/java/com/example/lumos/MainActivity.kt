package com.example.lumos

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.retrofit.ApiClient
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.example.lumos.ui.theme.LumosTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        lifecycleScope.launch {
            //val art = ArtistServiceImpl().createArtist(Artist(0,"Виктория","Радченко","0000",0.00))

            try {
                val types = ApiClient.getTypeService().getTypes()
                Log.d(TAG, "Received types: $types")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching types", e)
            }

            try {
                val showRates = ApiClient.getShowRateService().getShowRates()
                Log.d(TAG, "Received showRates: $showRates")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching showRates", e)
            }

            try {
                val performances = ApiClient.getPerformanceService().getPerformances()
                Log.d(TAG, "Received performances: $performances")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching performances", e)
            }

            try {
                val artistPerformances = ApiClient.getArtistPerformanceService().getArtistPerformances()
                Log.d(TAG, "Received artistPerformances: $artistPerformances")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching artistPerformances", e)
            }

            try {
                val orders = ApiClient.getOrderService().getOrders()
                Log.d(TAG, "Received orders: $orders")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching orders", e)
            }

            try {
                val earnings = ApiClient.getEarningService().getEarnings()
                Log.d(TAG, "Received earnings: $earnings")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching earnings", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LumosTheme {
        Greeting("Android")
    }
}