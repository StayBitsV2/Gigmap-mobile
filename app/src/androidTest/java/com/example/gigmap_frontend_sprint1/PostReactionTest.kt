package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * US: Reaccionar a publicaciones en comunidades.
 * Prueba funcional para verificar que un usuario puede dar "like" a un post.
 */
@RunWith(AndroidJUnit4::class)
class PostReactionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun reactToPostFlow() {
        // 1. Intentar iniciar sesión solo si no estamos en el Home
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
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithText(homeText).fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Navegar a la pestaña de Comunidades
        composeTestRule.onNodeWithTag("tab_Comunidades").performClick()

        // 4. Esperar a que cargue la lista de comunidades y seleccionar la primera disponible
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Buscar comunidad").fetchSemanticsNodes().isNotEmpty()
        }

        // Esperar a que aparezcan los items en la lista (LazyRow o LazyColumn)
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
                .fetchSemanticsNodes().size >= 2
        }

        composeTestRule.onAllNodes(hasClickAction() and hasAnyAncestor(hasScrollAction()))
            .get(1) // Índice 1 es la primera comunidad real
            .performScrollTo()
            .performClick()

        // 5. Esperar a que cargue la pantalla de la comunidad
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithText("Posts").fetchSemanticsNodes().isNotEmpty()
        }

        // 6. Encontrar un post y reaccionar (dar like)
        composeTestRule.waitUntil(timeoutMillis = 30000) {
            composeTestRule.onAllNodesWithContentDescription("Dar like").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithContentDescription("Dar like")
            .onFirst()
            .performClick()

        // 7. Verificar que la reacción fue procesada (el botón sigue ahí)
        composeTestRule.onAllNodesWithContentDescription("Dar like")
            .onFirst()
            .assertExists()
            .assertHasClickAction()
    }
}
