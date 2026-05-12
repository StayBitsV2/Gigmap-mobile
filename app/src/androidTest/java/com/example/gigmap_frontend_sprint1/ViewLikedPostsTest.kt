package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US: Ver publicaciones con like.
 * Prueba funcional para verificar que un usuario puede acceder a la sección de "Likes"
 * desde su perfil.
 */
@RunWith(AndroidJUnit4::class)
class ViewLikedPostsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun viewLikedPostsFlow() {
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

        composeTestRule.onNodeWithTag("tab_Perfil").performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Editar perfil").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Likes", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("Likes", ignoreCase = true).performClick()

        composeTestRule.onNodeWithText("Likes", ignoreCase = true).assertExists()
        
        composeTestRule.onNodeWithText("Likes", ignoreCase = true).assertIsSelected()
    }
}
