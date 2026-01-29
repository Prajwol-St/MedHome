package com.example.medhomeapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medhomeapp.view.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DoctorHomeScreenInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<DashboardActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSetAvailabilityCard_navigatesToDoctorAvailability() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("setAvailabilityCard")
            .performClick()
        Intents.intended(hasComponent(DoctorAvailabilityActivity::class.java.name))
    }

    @Test
    fun testMyAppointmentsCard_navigatesToDoctorAppointments() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("myAppointmentsCard")
            .performClick()
        Intents.intended(hasComponent(DoctorAppointmentsActivity::class.java.name))
    }

    @Test
    fun testManageLeavesCard_navigatesToManageLeaves() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("manageLeavesCard")
            .performClick()
        Intents.intended(hasComponent(ManageLeavesActivity::class.java.name))
    }

    @Test
    fun testHealthPackagesCard_navigatesToHealthPackagesManagement() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("healthPackagesCard")
            .performClick()
        Intents.intended(hasComponent(HealthPackagesManagementActivity::class.java.name))
    }
}