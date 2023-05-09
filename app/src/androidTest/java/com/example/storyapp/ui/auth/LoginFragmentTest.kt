package com.example.storyapp.ui.auth

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.storyapp.R
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.onboarding.OnBoardingActivity
import com.example.storyapp.ui.splash.SplashActivity
import com.example.storyapp.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val email = "rippedjeans@gmail.com"
    private val password = "rippedjeans"

    private val wrongEmail = "wrongemail@gmail.com"
    private val wrongPassword = "wrongpassword"

    private val invalidEmail = "a"
    private val invalidPassword = "a"

    @Before
    fun setUp() = runTest {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        ActivityScenario.launch(SplashActivity::class.java)
        ActivityScenario.launch(OnBoardingActivity::class.java)
        ActivityScenario.launch(AuthActivity::class.java)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_Component_Show_Correctly() {
        onView(withId(R.id.tvLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_move_register)).check(matches(isDisplayed()))
    }

    @Test
    fun move_To_Regsiter_Success() {
        onView(withId(R.id.btn_move_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_move_register)).perform(click())
        onView(withId(R.id.tvRegister)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_register_name)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_register_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_register_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_move_login)).check(matches(isDisplayed()))
    }

    @Test
    fun login_Success() {
        Intents.init()
        onView(withId(R.id.ed_login_email)).perform(typeText(email))
        onView(withId(R.id.ed_login_password)).perform(typeText(password))
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.rv_list_user)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_list_user)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_list_user)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )
        Intents.intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun login_Wrong() {
        onView(withId(R.id.ed_login_email)).perform(typeText(wrongEmail))
        onView(withId(R.id.ed_login_password)).perform(typeText(wrongPassword))
        onView(withId(R.id.btn_login)).perform(click())
        login_Component_Show_Correctly()
    }

    @Test
    fun login_Invalid() {
        onView(withId(R.id.ed_login_email)).perform(typeText(invalidEmail))
        onView(withId(R.id.ed_login_password)).perform(typeText(invalidPassword))
        onView(withId(R.id.btn_login)).perform(click())
        login_Component_Show_Correctly()
    }

    @Test
    fun logout_Success() = runTest {
        Intents.init()
        onView(withId(R.id.rv_list_user)).check(matches(isDisplayed()))
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Logout")).check(matches(isDisplayed()))
        onView(withText("Logout")).perform(click())
        Intents.intended(hasComponent(AuthActivity::class.java.name))
    }
}