package com.example.lumos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lumos.presentation.ManagementFragment
import com.example.lumos.presentation.ProfileFragment
import com.example.lumos.presentation.ScheduleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationBarView

//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNav.setOnNavigationItemSelectedListener(navListener)
//
//        // Установка начального фрагмента
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(
//                R.id.fragment_container,
//                ScheduleFragment()
//            ).commit()
//        }
//    }
//
//    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        val selectedFragment: Fragment = when (item.itemId) {
//            R.id.nav_schedule -> ScheduleFragment()
//            R.id.nav_control -> ManagementFragment()
//            R.id.nav_profile -> ProfileFragment()
//            else -> return@OnNavigationItemSelectedListener false
//        }
//
//        supportFragmentManager.beginTransaction().replace(
//            R.id.fragment_container,
//            selectedFragment
//        ).commit()
//
//        true
//    }
//}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.isItemHorizontalTranslationEnabled = false//itemHorizontalTranslationEnabled = false
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_schedule -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ScheduleFragment())
                        .commit()
                    true
                }
                R.id.nav_control -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ManagementFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Установка фрагмента по умолчанию
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_schedule
        }
    }
}






//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            LumosTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//        lifecycleScope.launch {
//            val artists = ArtistServiceImpl().getArtists()
//            //val artist = ArtistServiceImpl().getArtistById(2)
//            //val artist = ArtistServiceImpl().createArtist(Artist(0,"ВDиктория","Радченко","0000",0.00))
//            //val artist = ArtistServiceImpl().createArtist(Artist(0,"Виктория","Радченко","0000",0.00))
//            //val artist = ArtistServiceImpl().deleteArtist(9)
//
//            val types = TypeServiceImpl().getTypes()
//            //val type = TypeServiceImpl().getTypeById(1)
//            //val type = TypeServiceImpl().createType(Type(0,"effd"))
//            //val type = TypeServiceImpl().updateType(4,Type(0,"eff"))
//            //val type = TypeServiceImpl().deleteType(4)
//
//            val showRates = ShowRateServiceImpl().getShowRates()
//            //val showRate = ShowRateServiceImpl().getShowRateById(5)
//            //val showRate = ShowRateServiceImpl().createShowRate(ShowRateCreateUpdateSerializer(3,135.0))
//            //val showRate = ShowRateServiceImpl().updateShowRate(5,ShowRateCreateUpdateSerializer(3,2000.0))
//            //val showRate = ShowRateServiceImpl().deleteShowRate(9)
//
//            val performances = PerformanceServiceImpl().getPerformances()
//            //val performance = PerformanceServiceImpl().getPerformanceById(2)
////            val performance = PerformanceServiceImpl().createPerformance(
////                PerformanceCreateUpdateSerializer("fdfvdf",7,1200.0,3,3)
////            )
////            val performance = PerformanceServiceImpl().updatePerformance(3,
////                PerformanceCreateUpdateSerializer("fdfvdf",7,12000.0,3,3)
////            )
//            //val performance = PerformanceServiceImpl().deletePerformance(3)
//
//            val artistPerformances = ArtistPerformanceServiceImpl().getArtistPerformances()
//            //val artistPerformance = ArtistPerformanceServiceImpl().getArtistPerformanceById(5)
////            val artistPerformance = ArtistPerformanceServiceImpl().createArtistPerformance(
////                ArtistPerformanceCreateUpdateSerializer(4,1,1)
////            )
////            val artistPerformance = ArtistPerformanceServiceImpl().updateArtistPerformance(7,
////                ArtistPerformanceCreateUpdateSerializer(4,2,1)
////            )
//            //val artistPerformance = ArtistPerformanceServiceImpl().deleteArtistPerformance(7)
//
//            val orders = OrderServiceImpl().getOrders()
//            //val order = OrderServiceImpl().getOrderById(1)
//            //val order = OrderServiceImpl().createOrder(OrderCreateUpdateSerializer("2025-04-14","fddff",1,330.0,"dffdfd",true))
//            //val order = OrderServiceImpl().updateOrder(2,OrderCreateUpdateSerializer("2025-04-14","fddff",1,18000.0,"dffdfd",true))
//            //val order = OrderServiceImpl().deleteOrder(2)
//
//            val earnings = EarningServiceImpl().getEarnings()
//            //val earning = EarningServiceImpl().getEarningById(1)
//            //val earning = EarningServiceImpl().createEarning(EarningCreateUpdateSerializer(1,4,1000.00,false))
//            //val earning = EarningServiceImpl().updateEarning(4,EarningCreateUpdateSerializer(1,4,1000.00,true))
//            //val earning = EarningServiceImpl().deleteEarning(4)
//
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LumosTheme {
//        Greeting("Android")
//    }
//}