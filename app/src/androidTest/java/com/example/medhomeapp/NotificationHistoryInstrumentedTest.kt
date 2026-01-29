package com.example.medhomeapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medhomeapp.view.NotificationHistoryActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationHistoryInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<NotificationHistoryActivity>()

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
    fun testClearAllButton_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("clearAllButton")
            .performClick()
    }
}