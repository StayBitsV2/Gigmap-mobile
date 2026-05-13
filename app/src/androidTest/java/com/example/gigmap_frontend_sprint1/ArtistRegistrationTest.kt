package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ArtistRegistrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun artistRegistrationFlow() {
        val uniqueId = UUID.randomUUID().toString().substring(0, 8)
        val email = "artist_$uniqueId@test.com"
        val username = "artist_$uniqueId"
        val password = "Password123!"

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodes(hasText("Regístrate") and hasClickAction())
                .fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule
            .onNode(hasText("Regístrate") and hasClickAction())
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Ingresa tu correo electrónico")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Ingresa tu correo electrónico")
            .performTextInput(email)

        composeTestRule
            .onNodeWithText("Ingresa tu contraseña")
            .performTextInput(password)

        composeTestRule
            .onNodeWithText("Artista")
            .performClick()

        composeTestRule
            .onNodeWithText("Ingresa tu username")
            .performTextInput(username)

        composeTestRule
            .onNodeWithText("Siguiente")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("Recordar credenciales")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Recordar credenciales")
            .assertIsDisplayed()
        
        composeTestRule
            .onNode(hasText("Inicia sesión") and hasClickAction().not())
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ingresa tu correo electrónico")
            .assertIsDisplayed()
    }
}
