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
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateSerializer
import com.example.lumos.domain.entities.EarningCreateUpdateSerializer
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import com.example.lumos.domain.entities.PerformanceCreateUpdateSerializer
import com.example.lumos.domain.entities.ShowRateCreateUpdateSerializer
import com.example.lumos.retrofit.ApiClient
import com.example.lumos.retrofit.services.ArtistPerformanceServiceImpl
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.example.lumos.retrofit.services.EarningServiceImpl
import com.example.lumos.retrofit.services.OrderServiceImpl
import com.example.lumos.retrofit.services.PerformanceServiceImpl
import com.example.lumos.retrofit.services.ShowRateServiceImpl
import com.example.lumos.retrofit.services.TypeServiceImpl
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
            val artists = ArtistServiceImpl().getArtists()
            //val artist = ArtistServiceImpl().getArtistById(2)
            //val artist = ArtistServiceImpl().createArtist(Artist(0,"ВDиктория","Радченко","0000",0.00))
            //val artist = ArtistServiceImpl().createArtist(Artist(0,"Виктория","Радченко","0000",0.00))
            //val artist = ArtistServiceImpl().deleteArtist(9)

            val types = TypeServiceImpl().getTypes()
            //val type = TypeServiceImpl().getTypeById(1)
            //val type = TypeServiceImpl().createType(Type(0,"effd"))
            //val type = TypeServiceImpl().updateType(4,Type(0,"eff"))
            //val type = TypeServiceImpl().deleteType(4)

            val showRates = ShowRateServiceImpl().getShowRates()
            //val showRate = ShowRateServiceImpl().getShowRateById(5)
            //val showRate = ShowRateServiceImpl().createShowRate(ShowRateCreateUpdateSerializer(3,135.0))
            //val showRate = ShowRateServiceImpl().updateShowRate(5,ShowRateCreateUpdateSerializer(3,2000.0))
            //val showRate = ShowRateServiceImpl().deleteShowRate(9)

            val performances = PerformanceServiceImpl().getPerformances()
            //val performance = PerformanceServiceImpl().getPerformanceById(2)
//            val performance = PerformanceServiceImpl().createPerformance(
//                PerformanceCreateUpdateSerializer("fdfvdf",7,1200.0,3,3)
//            )
//            val performance = PerformanceServiceImpl().updatePerformance(3,
//                PerformanceCreateUpdateSerializer("fdfvdf",7,12000.0,3,3)
//            )
            //val performance = PerformanceServiceImpl().deletePerformance(3)

            val artistPerformances = ArtistPerformanceServiceImpl().getArtistPerformances()
            //val artistPerformance = ArtistPerformanceServiceImpl().getArtistPerformanceById(5)
//            val artistPerformance = ArtistPerformanceServiceImpl().createArtistPerformance(
//                ArtistPerformanceCreateUpdateSerializer(4,1,1)
//            )
//            val artistPerformance = ArtistPerformanceServiceImpl().updateArtistPerformance(7,
//                ArtistPerformanceCreateUpdateSerializer(4,2,1)
//            )
            //val artistPerformance = ArtistPerformanceServiceImpl().deleteArtistPerformance(7)

            val orders = OrderServiceImpl().getOrders()
            //val order = OrderServiceImpl().getOrderById(1)
            //val order = OrderServiceImpl().createOrder(OrderCreateUpdateSerializer("2025-04-14","fddff",1,330.0,"dffdfd",true))
            //val order = OrderServiceImpl().updateOrder(2,OrderCreateUpdateSerializer("2025-04-14","fddff",1,18000.0,"dffdfd",true))
            //val order = OrderServiceImpl().deleteOrder(2)

            val earnings = EarningServiceImpl().getEarnings()
            //val earning = EarningServiceImpl().getEarningById(1)
            //val earning = EarningServiceImpl().createEarning(EarningCreateUpdateSerializer(1,4,1000.00,false))
            //val earning = EarningServiceImpl().updateEarning(4,EarningCreateUpdateSerializer(1,4,1000.00,true))
            //val earning = EarningServiceImpl().deleteEarning(4)

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