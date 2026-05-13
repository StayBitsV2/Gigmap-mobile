package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US-02: Crear Concierto por Artista.
 * Prueba funcional para verificar que un usuario puede crear un concierto.
 */

@RunWith(AndroidJUnit4::class)
class CreateConcertTest {
     @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun createConcertFlow() {

    val homeText = "Descubre nuevos conciertos"
    val loginButtonText = "Iniciar Sesión"
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()
        }

        if (composeTestRule.onAllNodesWithText(loginButtonText).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText(loginButtonText).performClick()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText("Ingresa tu correo electrónico").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("Ingresa tu correo electrónico").performTextInput("usuarioArtista@gmail.com")
            composeTestRule.onNodeWithText("Ingresa tu contraseña").performTextInput("Artista12345")
            
            composeTestRule.onAllNodesWithText("Iniciar sesión")
                .filter(hasClickAction())
                .onFirst()
                .performClick()
        }
        
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithContentDescription("Ver más conciertos").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithContentDescription("Ver más conciertos").onFirst().performClick()

        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Buscar conciertos...").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 20000){
            composeTestRule.onAllNodesWithContentDescription("Crear concierto").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithContentDescription("Crear concierto").onFirst().performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 20000){
            composeTestRule.onAllNodesWithText("Crear").fetchSemanticsNodes().isNotEmpty()
        }
        if(composeTestRule.onAllNodesWithText("Crear").fetchSemanticsNodes().isNotEmpty()){
            
            composeTestRule.onNodeWithText("Ingresa el nombre del concierto")
             .performTextInput("Concierto Test")

            composeTestRule.onNodeWithText("DD")
            .performTextInput("25")

            composeTestRule.onNodeWithText("MM")
            .performTextInput("12")

            composeTestRule.onNodeWithText("YYYY")
            .performTextInput("2026")

            composeTestRule.onNodeWithText("Selecciona el género musical").performClick()
            composeTestRule.waitUntil(timeoutMillis=2000){
                 composeTestRule.onAllNodesWithText("ROCK")
                .fetchSemanticsNodes()
                .isNotEmpty()
            }
            composeTestRule.onNodeWithText("ROCK").performClick()


            composeTestRule.onNodeWithText("Ingresa una descripción del evento")
            .performTextInput("Evento de prueba automatizada")
            
            composeTestRule.onNodeWithText("Selecciona la plataforma de venta").performClick()
            composeTestRule.waitUntil(timeoutMillis=2000){
                 composeTestRule.onAllNodesWithText("Joinnus")
                .fetchSemanticsNodes()
                .isNotEmpty()
            }
            composeTestRule.onNodeWithText("Joinnus").performClick()

            composeTestRule.onNodeWithText("Ingresa el nombre del recinto")
            .performTextInput("Estadio Nacional")

            composeTestRule.onNodeWithText("Ingresa la dirección completa")
            .performTextInput("Ciudad Universitaria - UNMSM, Francisco Moreyra y Riglos 637, Lima 15081")

            composeTestRule.onNodeWithText("Capacidad del recinto")
            .performTextInput("5000")

            composeTestRule.onNodeWithText("Crear")
            .performScrollTo()
            .performClick()
        }
        composeTestRule.waitUntil(timeoutMillis = 15000) {
        composeTestRule.onAllNodesWithText("Concierto creado correctamente 🎉")
        .fetchSemanticsNodes()
        .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Concierto creado correctamente 🎉")
        .assertExists()




       

    }
}