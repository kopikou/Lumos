package com.example.lumos.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.example.lumos.presentation.fragments.artists.ManagementFragmentArtist
import com.example.lumos.presentation.fragments.artists.ProfileFragmentArtist
import com.example.lumos.presentation.fragments.artists.ScheduleFragmentArtist
import com.example.lumos.presentation.fragments.managers.ManagementFragmentManager
import com.example.lumos.presentation.fragments.managers.ProfileFragmentManager
import com.example.lumos.presentation.fragments.managers.ScheduleFragmentManager
import com.example.lumos.presentation.viewModels.MainViewModel
import com.example.lumos.retrofit.authentification.TokenManager
import com.example.lumos.retrofit.services.ArtistServiceImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch

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
    private lateinit var tokenManager: TokenManager
    private val viewModel: MainViewModel by viewModels() // Add ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        // Проверка авторизации
        if (tokenManager.getAccessToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.isItemHorizontalTranslationEnabled = false

        // Observe the artist data
        viewModel.artist.observe(this) { artist ->
            bottomNav.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_schedule -> {
                        replaceFragmentForCurrentUser(ScheduleFragmentManager(), ScheduleFragmentArtist())
                        true
                    }
                    R.id.nav_control -> {
                        replaceFragmentForCurrentUser(ManagementFragmentManager(), ManagementFragmentArtist())
                        true
                    }
                    R.id.nav_profile -> {
                        // Use the artist from ViewModel
                        replaceFragmentForCurrentUser(ProfileFragmentManager(), ProfileFragmentArtist(artist))
                        true
                    }
                    else -> false
                }
            }

            // Установка начального фрагмента
            if (savedInstanceState == null) {
                bottomNav.selectedItemId = R.id.nav_schedule
            }
        }

        // Load artist data
        lifecycleScope.launch {
            viewModel.loadArtist(tokenManager.getFirstName(), tokenManager.getLastName())
        }
    }

    private fun replaceFragmentForCurrentUser(
        adminFragment: Fragment,
        userFragment: Fragment
    ) {
        val fragment = if (tokenManager.isAdmin()) {
            adminFragment
        } else {
            userFragment
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Remove the userInfo() function as it's now handled by ViewModel
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