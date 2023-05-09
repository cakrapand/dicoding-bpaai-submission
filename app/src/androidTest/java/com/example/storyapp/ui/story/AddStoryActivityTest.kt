package com.example.storyapp.ui.story

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.storyapp.R
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.onboarding.OnBoardingActivity
import com.example.storyapp.ui.splash.SplashActivity
import com.example.storyapp.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AddStoryActivityTest {

    private val descriptiion = "Dummy descriptiion"

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest{

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        ActivityScenario.launch(SplashActivity::class.java)
        ActivityScenario.launch(OnBoardingActivity::class.java)
        ActivityScenario.launch(AuthActivity::class.java)
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_add_story)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_add_story)).perform((click()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun component_Show_Correctly() {
        onView(withId(R.id.previewImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_camera)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_gallery)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_add)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_currentLocation)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_upload_location)).check(matches(isDisplayed()))
    }

    @Test
    fun upload_Success() {
        onView(withId(R.id.btn_camera)).perform((click()))
        onView(withId(R.id.captureImage)).check(matches(isDisplayed()))
        onView(withId(R.id.captureImage)).perform((click()))
        onView(withId(R.id.previewImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_upload_location)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).perform(typeText(descriptiion))
        onView(withId(R.id.cb_upload_location)).perform((click()))
        onView(withId(R.id.btn_add)).perform((click()))
        onView(withId(R.id.rv_list_user)).check(matches(isDisplayed()))
    }

    @Test
    fun upload_Error() {
        onView(withId(R.id.btn_camera)).perform((click()))
        onView(withId(R.id.captureImage)).check(matches(isDisplayed()))
        onView(withId(R.id.captureImage)).perform((click()))
        onView(withId(R.id.previewImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_upload_location)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_upload_location)).perform((click()))
        onView(withId(R.id.btn_add)).perform((click()))
        component_Show_Correctly()
    }


}