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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
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
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture.allOf
@RunWith(AndroidJUnit4::class)
class PaySalaryTest {

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
    fun pay_salary_should_update_artist_balance_and_mark_earnings_as_paid() {
        // 1. Мокируем начальные данные
        mockInitialData()

        // 2. Логинимся как руководитель
        loginAsManager()

        // 3. Выполняем выплату зарплаты и проверяем статус выплаты
        paySalaryToArtist()

        // 4. Проверяем обновление баланса артиста
        verifyArtistBalanceUpdated()
    }

    private fun mockInitialData() {
        // Мок данных руководителя
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "id": 1,
                "firstName": "-",
                "lastName": "-",
                "isAdmin": true
            }
        """.trimIndent()))

        // Мок списка артистов с невыплаченной зарплатой
        mockWebServer.enqueue(MockResponse().setBody("""
            [{
                "artist": {
                    "id": 2,
                    "firstName": "Виктория",
                    "lastName": "Радченко",
                    "balance": 2000.0
                },
                "unpaidAmount": 2000.0,
                "unpaidEarnings": [{
                    "order": {"id": 100, "performance": {"title": "Индиго"}},
                    "amount": 2000.0,
                    "paid": false
                }]
            }]
        """.trimIndent()))

        // Мок успешного обновления выплаты
        mockWebServer.enqueue(MockResponse().setBody("""
            {"success": true}
        """.trimIndent()))

        // Мок обновленных данных артиста
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "id": 1,
                "firstName": "Виктория",
                "lastName": "Радченко",
                "balance": 0.0
            }
        """.trimIndent()))

        // Мок обновленного списка артистов (после выплаты)
        mockWebServer.enqueue(MockResponse().setBody("""
            []
        """.trimIndent()))
    }

    private fun loginAsManager() {
        onView(withId(R.id.usernameEditText)).perform(typeText("kopikou"))
        onView(withId(R.id.passwordEditText)).perform(typeText("123"))
        onView(withId(R.id.loginButton)).perform(click())
    }

    private fun paySalaryToArtist() {
        // Переходим в раздел управления
        onView(withId(R.id.nav_management)).perform(click())

        // Нажимаем на карточку невыплаченных зарплат
        onView(withId(R.id.unpaid_earnings_card)).perform(click())

        // Нажимаем кнопку выплаты для артиста
        onView(withText("Выплатить")).perform(click())

        // Ждем завершения операции
        onView(isRoot()).perform(waitFor(2000))

        verifyEarningsMarkedAsPaid()
    }

    private fun verifyArtistBalanceUpdated() {
        // Выходим из аккаунта менеджера
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.btnLogout)).perform(click())

        // Логинимся как артист
        onView(withId(R.id.usernameEditText)).perform(typeText("artist2"))
        onView(withId(R.id.passwordEditText)).perform(typeText("radchenko"))
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.nav_profile)).perform(click())

        // Проверяем что баланс обнулился
        onView(withText("0,00 ₽")).check(matches(isDisplayed()))
    }

    private fun verifyEarningsMarkedAsPaid() {
        // Возвращаемся в раздел управления
        onView(withId(R.id.nav_management)).perform(click())

        // Проверяем что карточка невыплаченных зарплат скрыта
        onView(withId(R.id.unpaid_earnings_card)).check(matches(not(isDisplayed())))
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