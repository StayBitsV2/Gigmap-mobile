package com.example.gigmap_frontend_sprint1

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationPermissionTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun requestLocationPermissionWhenOpeningMap() {
        val homeText = "Descubre nuevos conciertos"
        val loginButtonText = "Iniciar Sesión"

        composeTestRule.waitUntil(20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()
        }

        if (composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText(loginButtonText)
                .filter(hasClickAction())
                .onFirst()
                .performClick()

            composeTestRule.waitUntil(15000) {
                composeTestRule.onAllNodesWithText("Ingresa tu correo electrónico")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeTestRule.onNodeWithText("Ingresa tu correo electrónico")
                .performTextInput("usuariofan")

            composeTestRule.onNodeWithText("Ingresa tu contraseña")
                .performTextInput("12345678")

            composeTestRule.onAllNodesWithText("Iniciar sesión")
                .filter(hasClickAction())
                .onFirst()
                .performClick()
        }

        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText(homeText)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("tab_Mapa")
            .performClick()

        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Descubre conciertos cerca de ti")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Descubre conciertos cerca de ti")
            .assertExists()
    }
}