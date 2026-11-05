package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PregnancyWeeksData
import com.example.health.HealthConnectManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import org.junit.Before

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Maman & bébé Pregnancy Tracker", appName)
    }

    @Test
    fun `verify pregnancy weeks data coverage`() {
        val week24 = PregnancyWeeksData.getWeek(24)
        assertNotNull(week24)
        assertEquals(24, week24.weekNumber)
        assertEquals(2, week24.trimester)
        assertTrue(week24.fruitAnalogy.isNotBlank())
    }

    @Test
    fun `verify health connect permissions declared`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = HealthConnectManager(context)
        assertTrue(manager.permissions.isNotEmpty())
        assertTrue(manager.permissions.any { it.contains("weight", ignoreCase = true) })
        assertTrue(manager.permissions.any { it.contains("sleep", ignoreCase = true) })
    }

    @Test
    fun `verify database and pregnancy viewmodel initialize without crash`() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val db = com.example.data.local.AppDatabase.getInstance(context)
        assertNotNull(db)
        val dao = db.pregnancyDao()
        assertNotNull(dao)

        val vm = com.example.ui.viewmodel.PregnancyViewModel(context)
        assertNotNull(vm)
        assertNotNull(vm.currentTab.value)
        assertNotNull(vm.currentWeekNumber.value)
    }

    @Test
    fun `verify MainActivity launches cleanly`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun `verify default parents Fatou and Bala in viewmodel`() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.PregnancyViewModel(context)
        assertEquals("Fatou", vm.profileName.value)
        assertEquals("Bala", vm.fatherName.value)
    }

    @Test
    fun `verify maternity bag and purchases checklist data contains required categories`() {
        val items = com.example.data.ChecklistData.defaultItems
        assertTrue(items.any { it.category == "Valise Maternité (Maman)" })
        assertTrue(items.any { it.category == "Valise Maternité (Bébé - Jour J)" })
        assertTrue(items.any { it.category == "Valise Accompagnant (Papa)" })
        assertTrue(items.any { it.category == "À Acheter Avant la Naissance" })
        assertTrue(items.any { it.category == "À Acheter Après la Naissance" })
    }

    @Test
    fun `verify public shared app url is valid and secure`() {
        val url = com.example.ui.components.PUBLIC_SHARED_APP_URL
        assertTrue(url.startsWith("https://"))
        assertTrue(url.contains("run.app"))
    }
}
