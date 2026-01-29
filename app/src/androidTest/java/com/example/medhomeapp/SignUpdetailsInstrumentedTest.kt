package com.example.medhomeapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medhomeapp.view.SignupDetailsActivity
import com.example.medhomeapp.view.DashboardActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupDetailsInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignupDetailsActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testBackButton_finishesActivity() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("backButton")
            .performClick()
    }

    @Test
    fun testNameField_acceptsInput() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nameField")
            .performTextInput("John Doe")
    }

    @Test
    fun testContactField_acceptsInput() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("contactField")
            .performTextInput("9876543210")
    }

    @Test
    fun testDateOfBirthField_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("dateOfBirthField")
            .performClick()
    }

    @Test
    fun testGenderSelection_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("genderMale")
            .performClick()
    }

    @Test
    fun testAddressField_acceptsInput() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("addressField")
            .performTextInput("123 Main St")
    }

    @Test
    fun testCompleteProfileButton_navigatesToDashboard() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("completeProfileButton")
            .performClick()
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }
}