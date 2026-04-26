package hu.kocsisgeri.betterneptun.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hu.kocsisgeri.betterneptun.R

// Defining the Armata font family. 
// Since armata.xml is a downloadable font, 
// for Compose it's often better to have the .ttf in res/font or use GoogleFonts API.
// Assuming for now it's available or will be added.
val Armata = FontFamily(
    Font(R.font.armata, FontWeight.Normal),
    Font(R.font.armata, FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Mapping from XML styles
    titleLarge = TextStyle(
        fontFamily = Armata,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Armata,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Armata,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MainTextColor
    )
)
