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
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.By
import androidx.test.platform.app.InstrumentationRegistry
import android.view.KeyEvent
import androidx.test.espresso.action.ViewActions.replaceText

@RunWith(AndroidJUnit4::class)
class CreateOrderEndToEndTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        // Инициализация MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun create_new_order_should_appear_in_manager_schedule() {
        // 1. Мокируем данные
        mockInitialData()

        // 2. Вводим данные и нажимаем кнопку
        onView(withId(R.id.usernameEditText)).perform(typeText("kopikou"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // 3. Запускаем экран расписания руководителя
        onView(withId(R.id.nav_schedule)).perform(click())

        // 4. Нажимаем кнопку создания заказа
        onView(withId(R.id.fabAddOrder)).perform(click())

        // 5. Заполняем форму заказа
        fillOrderForm()

        // 6. Нажимаем кнопку "Создать"
        onView(withText("Создать")).perform(click())

        // 7. Проверяем что заказ появился в интерфейсе руководителя
        onView(withText("Индиго")).check(matches(isDisplayed()))
        onView(withText("22 мая")).check(matches(isDisplayed()))
    }

    private fun mockInitialData() {
        // Мокируем список номеров
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "id": 11,
                "title": "Индиго",
                "cost": 14000,
                "cntArtists": 2
            }]
        """.trimIndent()))

        // Мокируем список артистов для номера "Индиго"
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "id": 1,
                "artist": {
                    "id": 1,
                    "firstName": "Надежда",
                    "lastName": "Копылова"
                },
                "rate": {
                    "rate": 2000
                }
            }, {
                "id": 2,
                "artist": {
                    "id": 2,
                    "firstName": "Виктория",
                    "lastName": "Радченко"
                },
                "rate": {
                    "rate": 2000
                }
            }]
        """.trimIndent()))

        // Мокируем успешное создание заказа
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

        // Мокируем получение созданного заказа
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "date": "2025-05-22",
                "location": "Место",
                "performance": {
                    "id": 11,
                    "title": "Индиго",
                    "cost": 14000,
                    "cntArtists": 2
                },
                "amount": 14000,
                "comment": "Комментарий",
                "completed": false
            }]
        """.trimIndent()))
    }

    private fun fillOrderForm() {
        // Выбираем дату
        onView(withId(R.id.etDate)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2025, 5, 22))
        onView(withId(android.R.id.button1)).perform(click())

        // Выбираем номер
        onView(withId(R.id.spinnerPerformance)).perform(click())
        onData(`is`("Индиго"))
            .inRoot(isPlatformPopup()) // Указываем, что ищем в выпадающем списке
            .perform(scrollTo(), click())

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Открываем меню переключения языка (например, долгим нажатием пробела)
        device.pressKeyCode(KeyEvent.KEYCODE_SPACE, KeyEvent.META_ALT_ON)

        // Ищем кнопку "Русский" и кликаем
        val russianLang = device.wait(
            Until.findObject(By.text("Русский")),
            2000
        )
        russianLang?.click()
        // Вводим место
        onView(withId(R.id.etLocation)).perform(replaceText("Место"), closeSoftKeyboard())

        // Вводим комментарий
        onView(withId(R.id.etComment)).perform(replaceText("Комментарий"), closeSoftKeyboard())

        // Выбираем артистов
        onView(withText("Надежда Копылова")).perform(click())
        onView(withText("Виктория Радченко")).perform(click())
    }
    @Test
    fun create_new_order_should_appear_in_artist_schedule() {

        // Выходим из аккаунта
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.btnLogout)).perform(click())

        // Логинимся как артист
        onView(withId(R.id.usernameEditText)).perform(typeText("artist1"))
        onView(withId(R.id.passwordEditText)).perform(typeText("kopylova"))
        onView(withId(R.id.loginButton)).perform(click())

        // Проверяем заказ в расписании артиста
        onView(withId(R.id.nav_schedule)).perform(click())
        onView(withText("Индиго")).check(matches(isDisplayed()))
    }
}