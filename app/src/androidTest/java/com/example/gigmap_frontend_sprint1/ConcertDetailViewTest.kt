package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US: Ver información detallada del evento asociado.
 * Prueba funcional para verificar que un usuario puede ver el detalle de un concierto
 * y encontrar la sección de "Eventos relacionados".
 */
@RunWith(AndroidJUnit4::class)
class ConcertDetailViewTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun viewConcertDetailFlow() {
        // 1. Iniciar Sesión
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

        // 2. Esperar al Home
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Hacer clic en la flecha de "Ver más conciertos" en el Home
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithContentDescription("Ver más conciertos").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithContentDescription("Ver más conciertos")
            .performScrollTo()
            .performClick()

        // 4. Esperar a que cargue la lista de conciertos (ConcertsList.kt)
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Buscar conciertos...").fetchSemanticsNodes().isNotEmpty()
        }

        // 5. Esperar a que aparezcan los conciertos en la lista y hacer clic en el primero
        // Usamos hasAnyAncestor(hasScrollAction()) para asegurar que es un item de la LazyColumn
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
            .onFirst()
            .performClick()

        // 6. Verificar que se está viendo el detalle y buscar "Eventos relacionados"
        // En ConcertDetails.kt, el texto es "Eventos relacionados"
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Eventos relacionados", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Eventos relacionados", ignoreCase = true).assertExists()
    }
}
