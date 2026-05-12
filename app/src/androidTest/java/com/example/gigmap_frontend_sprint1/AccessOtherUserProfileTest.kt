package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US: Acceder a perfil de otros usuarios.
 * Prueba funcional para verificar que al hacer clic en la foto de perfil de un usuario,
 * se navega a su vista de perfil.
 */
@RunWith(AndroidJUnit4::class)
class AccessOtherUserProfileTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun accessOtherUserProfileFlow() {
        val homeText = "Descubre nuevos conciertos"
        val loginButtonText = "Iniciar Sesión"

        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()
        }

        if (composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText(loginButtonText).performClick()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText("Ingresa tu correo electrónico").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("Ingresa tu correo electrónico").performTextInput("usuariofan")
            composeTestRule.onNodeWithText("Ingresa tu contraseña").performTextInput("12345678")
            
            composeTestRule.onAllNodesWithText("Iniciar sesión")
                .filter(hasClickAction())
                .onFirst()
                .performClick()
        }

        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Nuevos artistas en GigMap").fetchSemanticsNodes().isNotEmpty()
        }

        val harryMatcher = hasText("holi", substring = true, ignoreCase = true) or 
                           hasContentDescription("holi", substring = true, ignoreCase = true)

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodes(harryMatcher).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNode(harryMatcher).performScrollTo()

        composeTestRule.onAllNodes(
            hasClickAction() and 
            (hasContentDescription("holi", substring = true, ignoreCase = true) or 
             hasAnySibling(hasText("holi", substring = true, ignoreCase = true)))
        ).onFirst().performClick()

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("holi", substring = true, ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("holi", substring = true, ignoreCase = true)
            .onFirst()
            .assertExists()

        composeTestRule.onAllNodes(
            hasText("Seguir", ignoreCase = true) or hasText("Siguiendo", ignoreCase = true)
        ).onFirst().assertExists()
    }
}
