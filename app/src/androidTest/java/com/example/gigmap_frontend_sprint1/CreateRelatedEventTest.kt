package com.example.gigmap_frontend_sprint1

import android.os.Build
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.SdkSuppress
import com.example.gigmap_frontend_sprint1.view.CreateRelatedEventDialog
import com.example.gigmap_frontend_sprint1.viewmodel.RelatedEventViewModel
import org.junit.Rule
import org.junit.Test

class CreateRelatedEventTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun testCreateRelatedEvent() {
        // Arrange: cargamos el diálogo de crear evento relacionado
        composeTestRule.setContent {
            val relatedEventVM = RelatedEventViewModel()

            CreateRelatedEventDialog(
                concertId = 1,
                currentUserId = 1,
                relatedEventVM = relatedEventVM,
                onDismiss = {}
            )
        }

        val textFields = composeTestRule.onAllNodes(hasSetTextAction())

        assert(textFields.fetchSemanticsNodes().size >= 8)

        textFields[0].performTextInput("Reunión antes del concierto")
        textFields[1].performTextInput("15")
        textFields[2].performTextInput("11")
        textFields[3].performTextInput("2026")

        composeTestRule.onNodeWithText("Reunion").performClick()

        textFields[4].performTextInput("Evento para conocer personas con gustos musicales similares antes del concierto.")
        composeTestRule.onNodeWithText("Publicado").performClick()
        textFields[5].performTextInput("Centro de Convenciones")
        textFields[6].performTextInput("Av. Javier Prado 123")
        textFields[7].performTextInput("100")

        composeTestRule.onNodeWithText("Crear").assertExists().performClick()
    }
}