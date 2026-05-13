package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewConcertDetailsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun viewCompleteConcertInformationFlow() {
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

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithContentDescription("Ver más conciertos").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithContentDescription("Ver más conciertos")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Buscar conciertos...").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
            .onFirst()
            .performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Tickets").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Tickets").assertExists().assertHasClickAction()
        
        composeTestRule.onAllNodes(hasText("Confirmar asistencia") or hasText("Cancelar confirmación"))
            .onFirst()
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("Eventos relacionados", ignoreCase = true).assertExists()
        composeTestRule.onNodeWithText("Usuarios que confirmaron su asistencia", ignoreCase = true).assertExists()

        composeTestRule.onNode(hasScrollAction()).assertExists()
        
        composeTestRule.onAllNodesWithText(" de ", substring = true).onFirst().assertExists()
    }
}
