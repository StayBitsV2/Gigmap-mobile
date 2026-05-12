package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginSuccessFlow() {
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Iniciar Sesión")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule
            .onNodeWithText("Iniciar Sesión")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Ingresa tu correo electrónico")
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithText("Ingresa tu correo electrónico")
            .performTextInput("usuariofan")

        composeTestRule
            .onNodeWithText("Ingresa tu contraseña")
            .performTextInput("12345678")

        composeTestRule
            .onNodeWithText("Iniciar sesión")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("Descubre nuevos conciertos")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Descubre nuevos conciertos")
            .assertIsDisplayed()
    }
}
