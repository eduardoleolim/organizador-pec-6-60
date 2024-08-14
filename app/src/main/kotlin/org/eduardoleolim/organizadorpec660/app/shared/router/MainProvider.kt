package org.eduardoleolim.organizadorpec660.app.shared.router

import cafe.adriel.voyager.core.registry.ScreenProvider
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse

sealed class MainProvider : ScreenProvider {
    data object AuthScreen : MainProvider()
    data class HomeScreen(val user: AuthUserResponse) : MainProvider()
}
