package org.eduardoleolim.organizadorpec660.app.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
val RobotoFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Roboto_Thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.Roboto_ThinItalic, FontWeight.Thin, FontStyle.Italic),
        Font(Res.font.Roboto_Light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.Roboto_LightItalic, FontWeight.Light, FontStyle.Italic),
        Font(Res.font.Roboto_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Roboto_Italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.Roboto_Medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.Roboto_MediumItalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.Roboto_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Roboto_BoldItalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.Roboto_Black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.Roboto_BlackItalic, FontWeight.Black, FontStyle.Italic)
    )

val RobotoTypography: Typography
    @Composable get() = Typography(
        displayLarge = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.25.sp
        ),
        displayMedium = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )

@OptIn(ExperimentalResourceApi::class)
val montserratFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Montserrat_Thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.Montserrat_ThinItalic, FontWeight.Thin, FontStyle.Italic),
        Font(Res.font.Montserrat_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
        Font(Res.font.Montserrat_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.Montserrat_Light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.Montserrat_LightItalic, FontWeight.Light, FontStyle.Italic),
        Font(Res.font.Montserrat_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Montserrat_Italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.Montserrat_Medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.Montserrat_MediumItalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.Montserrat_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.Montserrat_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Montserrat_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Montserrat_BoldItalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.Montserrat_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
        Font(Res.font.Montserrat_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.Montserrat_Black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.Montserrat_BlackItalic, FontWeight.Black, FontStyle.Italic),
    )

val MontserratTypography: Typography
    @Composable get() = Typography(
        displayLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.25.sp
        ),
        displayMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )

