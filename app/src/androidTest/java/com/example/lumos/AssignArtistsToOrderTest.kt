package com.example.lumos

import android.widget.DatePicker
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lumos.presentation.views.activities.MainActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture.allOf

@RunWith(AndroidJUnit4::class)
class AssignArtistsToOrderTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun assign_artists_to_order_should_update_artist_schedule() {
        // 1. Мокируем начальные данные
        mockInitialData()

        // 2. Логинимся как менеджер
        loginAsManager()

        // 3. Создаем тестовый заказ и назначаем артистов на заказ
        createTestOrder()

        // 4. Проверяем отображение у артиста
        verifyArtistSchedule()
    }

    private fun mockInitialData() {
        // Мок списка номеров
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "id": 11,
                "title": "Индиго",
                "cost": 14000,
                "cntArtists": 2
            }]
        """.trimIndent()))

        // Мок списка артистов
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "id": 1,
                "artist": {"id": 1, "firstName": "Надежда", "lastName": "Копылова"},
                "rate": {"rate": 2000}
            }, {
                "id": 2,
                "artist": {"id": 2, "firstName": "Виктория", "lastName": "Радченко"},
                "rate": {"rate": 2000}
            }]
        """.trimIndent()))

        // Мок создания заказа
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "date": "2025-05-22",
                "location": "Место",
                "performance": 11,
                "amount": 14000,
                "comment": "Комментарий",
                "completed": false
            }
        """.trimIndent()))

        // Мок обновления заказа
        mockWebServer.enqueue(MockResponse().setBody("""
            {"success": true}
        """.trimIndent()))

        // Мок получения заказов после обновления
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "id": 100,
                "date": "2025-05-22",
                "location": "Место",
                "performance": {"id": 11, "title": "Индиго"},
                "amount": 14000,
                "comment": "Комментарий",
                "completed": false,
                "artists": [
                    {"id": 1, "firstName": "Надежда", "lastName": "Копылова"},
                    {"id": 2, "firstName": "Виктория", "lastName": "Радченко"}
                ]
            }]
        """.trimIndent()))
    }

    private fun loginAsManager() {
        onView(withId(R.id.usernameEditText)).perform(typeText("kopikou"))
        onView(withId(R.id.passwordEditText)).perform(typeText("123"))
        onView(withId(R.id.loginButton)).perform(click())
    }

    private fun createTestOrder() {
        onView(withId(R.id.nav_schedule)).perform(click())
        onView(withId(R.id.fabAddOrder)).perform(click())

        // Заполнение формы заказа
        onView(withId(R.id.etDate)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2025, 5, 22))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.spinnerPerformance)).perform(click())
        onView(withId(R.id.spinnerPerformance)).perform(click())
        onData(`is`("Индиго"))
            .inRoot(isPlatformPopup()) // Указываем, что ищем в выпадающем списке
            .perform(scrollTo(), click())

        onView(withId(R.id.etLocation)).perform(typeText("Место"), closeSoftKeyboard())
        onView(withId(R.id.etComment)).perform(typeText("Комментарий"), closeSoftKeyboard())
        assignArtistsToOrder()

        onView(withText("Создать")).perform(click())
    }

    private fun assignArtistsToOrder() {
        onView(withText("Индиго")).perform(click())
        onView(withText("Надежда Копылова")).perform(click())
        onView(withText("Виктория Радченко")).perform(click())
        onView(withText("Сохранить")).perform(click())
    }

    private fun verifyArtistSchedule() {
        // Выходим из аккаунта менеджера
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.btnLogout)).perform(click())

        // Логинимся как артист
        onView(withId(R.id.usernameEditText)).perform(typeText("artist1"))
        onView(withId(R.id.passwordEditText)).perform(typeText("kopylova"))
        onView(withId(R.id.loginButton)).perform(click())

        // Проверяем расписание
        onView(withId(R.id.nav_schedule)).perform(click())
        onView(withText("Индиго")).check(matches(isDisplayed()))
        onView(withText("22 мая")).check(matches(isDisplayed()))
    }
}