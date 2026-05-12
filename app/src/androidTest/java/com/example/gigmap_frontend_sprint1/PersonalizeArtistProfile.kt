package com.example.gigmap_frontend_sprint1

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.gigmap_frontend_sprint1.view.EditProfile
import com.example.gigmap_frontend_sprint1.viewmodel.UserViewModel
import org.junit.Rule
import org.junit.Test

class PersonalizeArtistProfile {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPersonalizeArtistProfile() {

        composeTestRule.setContent {
            val nav = rememberNavController()
            val userVM = UserViewModel()

            userVM.currentUserId = 1 
            EditProfile(nav = nav, userVM = userVM)
        }

        val textFields = composeTestRule.onAllNodes(hasSetTextAction())

        assert(textFields.fetchSemanticsNodes().size >= 4)

        textFields[0].performTextInput("Artista Test")
        textFields[1].performTextInput("artistatest")
        textFields[2].performTextInput("test@artist.com")
        textFields[3].performTextInput("Esta es una descripción de prueba para un artista.")


        composeTestRule.onNodeWithText("Artista").performClick()


        composeTestRule.onNodeWithText("Guardar cambios").assertExists().performClick()
    }
}
