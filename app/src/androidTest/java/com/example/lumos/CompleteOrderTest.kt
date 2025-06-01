package com.example.lumos
import android.view.View
import android.widget.DatePicker
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lumos.data.remote.ApiClient
import com.example.lumos.presentation.views.activities.MainActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture.allOf
@RunWith(AndroidJUnit4::class)
class CompleteOrderTest {

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
    fun complete_order_should_move_to_history_and_update_artist_balances() {
        // 1. Мокируем начальные данные
        mockInitialData()

        // 2. Логинимся как артист
        loginAsArtist()

        // 3. Находим и выполняем заказ
        completeOrder()

        // 4. Проверяем что заказ исчез из текущих
        verifyOrderRemovedFromSchedule()

        // 5. Проверяем что заказ появился в истории
        verifyOrderInHistory()

        // 6. Проверяем обновление баланса
        verifyBalanceUpdated()
    }

    private fun mockInitialData() {
        // Мок данных артиста
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "id": 1,
                "firstName": "Виктория",
                "lastName": "Радченко",
                "balance": 0.0
            }
        """.trimIndent()))

        // Мок текущих заказов
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "date": "2025-05-22",
                "location": "Место",
                "performance": {"id": 11, "title": "Индиго"},
                "amount": 14000,
                "comment": "Комментарий",
                "completed": false,
                "artists": [
                    {"id": 1, "firstName": "Виктория", "lastName": "Радченко"}
                ]
            }]
        """.trimIndent()))

        // Мок обновления статуса заказа
        mockWebServer.enqueue(MockResponse().setBody("""
            {"success": true}
        """.trimIndent()))

        // Мок обновленных данных артиста (после начисления)
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "id": 1,
                "firstName": "Виктория",
                "lastName": "Радченко",
                "balance": 2000.0
            }
        """.trimIndent()))

        // Мок истории заказов
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "date": "2025-05-22",
                "location": "Место",
                "performance": {"id": 11, "title": "Индиго"},
                "amount": 14000,
                "comment": "Комментарий",
                "completed": true
            }]
        """.trimIndent()))
    }

    private fun loginAsArtist() {
        onView(withId(R.id.usernameEditText)).perform(typeText("artist2"))
        onView(withId(R.id.passwordEditText)).perform(typeText("radchenko"))
        onView(withId(R.id.loginButton)).perform(click())
    }

    private fun completeOrder() {
        // Переходим в расписание
        onView(withId(R.id.nav_schedule)).perform(click())

        // Находим и открываем заказ
        onView(withText("Индиго")).perform(click())

        // Отмечаем как выполненный
        onView(withId(R.id.switchCompleted)).perform(click())

        // Подтверждаем
        onView(withText("Сохранить")).perform(click())

        // Ждем обновления
        onView(isRoot()).perform(waitFor(2000))
    }

    private fun verifyOrderRemovedFromSchedule() {
        // Проверяем что заказ исчез из списка
        onView(withText("Индиго"))
            .check(doesNotExist())
    }

    private fun verifyOrderInHistory() {
        // Переходим в историю заказов
        onView(withId(R.id.nav_history)).perform(click())

        // Проверяем наличие выполненного заказа
        onView(withText("Индиго"))
            .check(matches(isDisplayed()))
        onView(withText("22 мая"))
            .check(matches(isDisplayed()))
    }

    private fun verifyBalanceUpdated() {
        // Переходим в профиль
        onView(withId(R.id.nav_profile)).perform(click())

        // Проверяем обновленный баланс
        onView(withText("2 000 ₽"))
            .check(matches(isDisplayed()))
    }

    private fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}