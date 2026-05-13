package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/**
 * US-01: Filtrar conciertos por género musical.
 * Prueba funcional para verificar que un usuario pueda filtrae un concierto por tipo de genero.
 */

@RunWith(AndroidJUnit4::class)
class FilterConcertTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun filterConcertByRockFlow() {
        val homeText = "Descubre nuevos conciertos"
        val loginButtonText = "Iniciar Sesión"

        composeTestRule.waitUntil(20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()
        }

        if (composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText(loginButtonText).performClick()

            composeTestRule.waitUntil(10000) {
                composeTestRule.onAllNodesWithText("Ingresa tu correo electrónico").fetchSemanticsNodes().isNotEmpty()
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
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Ver más conciertos")
            .performClick()

        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Buscar conciertos...").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Abrir filtro")
            .performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("FILTRAR POR GÉNERO").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("ROCK")
            .performClick()

        composeTestRule.onNodeWithText("APLICAR FILTROS")
            .performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("concierto rock", ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("concierto rock", ignoreCase = true)
            .assertExists()
    }
}