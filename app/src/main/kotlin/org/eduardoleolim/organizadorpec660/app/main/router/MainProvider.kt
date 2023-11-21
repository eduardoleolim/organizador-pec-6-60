package org.eduardoleolim.organizadorpec660.app.main.router

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class MainProvider : ScreenProvider {
    data object AuthScreen : MainProvider()
    data object HomeScreen : MainProvider()
}
