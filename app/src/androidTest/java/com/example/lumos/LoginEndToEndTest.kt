package com.example.lumos
import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lumos.presentation.views.activities.LoginActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.lumos.presentation.views.activities.MainActivity
import org.hamcrest.Matcher
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class LoginEndToEndTest {

    // Правило для автоматической инициализации/очистки Intents
    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

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
    fun successful_login_navigates_to_MainActivity() {
        // 1. Мокируем успешный ответ сервера
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {
                    "access": "fake_token",
                    "refresh": "fake_refresh",
                    "user": {
                        "id": 1,
                        "firstName": "-",
                        "lastName": "-",
                        "isAdmin": true
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // 3. Вводим данные и нажимаем кнопку
        onView(withId(R.id.usernameEditText)).perform(typeText("kopikou"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // 3. Проверяем что был запущен MainActivity
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun failed_login_shows_error() {
        // 1. Мокируем ошибку сервера
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
            .setBody("""
                {
                    "error": "Invalid credentials"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // 2. Вводим неверные данные
        onView(withId(R.id.usernameEditText)).perform(typeText("artist"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // 3. Проверяем что остались на LoginActivity
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }
}



class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun describeTo(description: org.hamcrest.Description) {
        description.appendText("is toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken
            if (windowToken === appToken) {
                // windowToken == appToken means this window isn't contained by any other windows
                return true
            }
        }
        return false
    }
}

fun isToast(): Matcher<Root> = ToastMatcher()