package com.example.medhomeapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medhomeapp.view.DashboardActivity
import com.example.medhomeapp.view.QrScannerActivity
import com.example.medhomeapp.view.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardInstrumentedTest {

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
    fun testScanTab_navigatesToQrScanner() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("scanTab")
            .performClick()
        Intents.intended(hasComponent(QrScannerActivity::class.java.name))
    }

    @Test
    fun testSettingsTab_navigatesToSettings() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("settingsTab")
            .performClick()
        Intents.intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testHomeTab_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("homeTab")
            .performClick()
    }

    @Test
    fun testNotificationsTab_switchesToNotifications() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("notificationsTab")
            .performClick()
    }

    @Test
    fun testOrdersTab_switchesToOrders() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ordersTab")
            .performClick()
    }
}