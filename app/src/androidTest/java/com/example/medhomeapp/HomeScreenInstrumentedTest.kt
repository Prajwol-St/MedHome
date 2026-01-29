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
class HomeScreenInstrumentedTest {

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
    fun testHealthRecordsCard_navigatesToHealthRecords() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("healthRecordsCard")
            .performClick()
        Intents.intended(hasComponent(HealthRecords::class.java.name))
    }

    @Test
    fun testBookConsultationCard_navigatesToBookConsultation() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("bookConsultationCard")
            .performClick()
        Intents.intended(hasComponent(BookConsultationActivity::class.java.name))
    }

    @Test
    fun testAIHealthAssistantCard_navigatesToChatbot() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("aiHealthAssistantCard")
            .performClick()
        Intents.intended(hasComponent(ChatbotActivity::class.java.name))
    }

    @Test
    fun testPastBookingsCard_navigatesToPastBookings() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pastBookingsCard")
            .performClick()
        Intents.intended(hasComponent(PastBookingsActivity::class.java.name))
    }

    @Test
    fun testAppointmentsCard_navigatesToMyAppointments() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("appointmentsCard")
            .performClick()
        Intents.intended(hasComponent(MyAppointmentsActivity::class.java.name))
    }

    @Test
    fun testCaloriesCalculatorCard_navigatesToCaloriesCalculator() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("caloriesCalculatorCard")
            .performClick()
        Intents.intended(hasComponent(CaloriesCalculatorActivity::class.java.name))
    }

    @Test
    fun testBloodDonationCard_navigatesToBloodDonation() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("bloodDonationCard")
            .performClick()
        Intents.intended(hasComponent(BloodDonationActivity::class.java.name))
    }

    @Test
    fun testHealthPackagesCard_navigatesToHealthPackages() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("healthPackagesCard")
            .performClick()
        Intents.intended(hasComponent(HealthPackagesActivity::class.java.name))
    }

    @Test
    fun testPharmacyCard_navigatesToPharmacy() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pharmacyCard")
            .performClick()
        Intents.intended(hasComponent(PharmacyActivity::class.java.name))
    }

    @Test
    fun testMedicineRemindersCard_navigatesToMedicineReminder() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("medicineRemindersCard")
            .performClick()
        Intents.intended(hasComponent(MedicineReminderActivity::class.java.name))
    }

    @Test
    fun testQRButton_navigatesToQrActivity() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("qrButton")
            .performClick()
        Intents.intended(hasComponent(QrActivity::class.java.name))
    }
}