package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US06 – Buscar conciertos
 * Prueba funcional
 */
@RunWith(AndroidJUnit4::class)
class SearchConcertTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun searchConcertFlow() {

        // 1. Presionar Iniciar Sesión
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

        // 3. Presionar botón Iniciar sesión
        composeTestRule
            .onNodeWithText("Iniciar sesión")
            .performClick()

        // 4. Esperar a que cargue el Home
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("Descubre nuevos conciertos")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 5. Presionar el ícono para de lista de conciertos
        composeTestRule
            .onNodeWithContentDescription("Ver más conciertos")
            .performClick()

        // 6. Esperar a que cargue el buscador
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Buscar conciertos...")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 7. Escribir el nombre del concierto en el buscador
        composeTestRule
            .onNodeWithText("Buscar conciertos...")
            .performTextInput("rock")

        // 8. Verificar que se muestran conciertos coincidentes
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("rock", substring = true)
                .fetchSemanticsNodes().size > 1
        }
    }
}