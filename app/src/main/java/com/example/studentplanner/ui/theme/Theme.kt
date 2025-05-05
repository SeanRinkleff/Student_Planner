package com.example.studentplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    background = backgroundColor,
    surface = Color.White,
    onPrimary = onPrimaryColor,
    onSecondary = onSecondaryColor
)

@Composable
fun StudentPlannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorPalette,
        typography = typography,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StudentPlannerTheme {
        // Your UI Preview goes here
    }
}
