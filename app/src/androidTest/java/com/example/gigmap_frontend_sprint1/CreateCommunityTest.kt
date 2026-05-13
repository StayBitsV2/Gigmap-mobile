    package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateCommunityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun createCommunityFlow() {
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

        composeTestRule.onNodeWithTag("tab_Comunidades")
            .performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Buscar comunidad")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("btnCrearComunidad")
        .performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Ingrese el nombre de la comunidad")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Ingrese el nombre de la comunidad")
            .performTextInput("Comunidad Test Automatizada")

        composeTestRule.onNodeWithText("Ingrese una descripción")
            .performTextInput("Comunidad creada desde prueba funcional automatizada")

        composeTestRule.onNodeWithText("Crear comunidad")
            .performClick()

        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithText("✅ Comunidad creada")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("✅ Comunidad creada")
            .assertExists()
    }
}