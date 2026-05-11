package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US18 – Ver comunidades accedidas
 * Prueba funcional
 */
@RunWith(AndroidJUnit4::class)
class SearchJoinedCommunities {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun viewJoinedCommunitiesFlow() {

        // 1. Desde Welcome, presionar "Iniciar Sesión"
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Iniciar Sesión")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule
            .onNodeWithText("Iniciar Sesión")
            .performClick()

        // 2. Ingresar credenciales
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

        // 3. Presionar botón "Iniciar sesión"
        composeTestRule
            .onNodeWithText("Iniciar sesión")
            .performClick()

        // 4. Esperar a que cargue el Home
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("Descubre nuevos conciertos")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 5. Presionar el tab perfil
        composeTestRule
            .onNodeWithTag("tab_Perfil")
            .performClick()

        // 6. Esperar a que cargue el perfil
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Editar perfil")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 7. Presionar el tab "Comunidades" en el perfil
        composeTestRule
            .onNodeWithText("Comunidades")
            .performClick()

        Thread.sleep(2000)

        // 8. Verificar que se muestran comunidades

        composeTestRule
            .onNodeWithText("COMUNIDAD DE POP", ignoreCase = true)
            .assertIsDisplayed()
    }
}