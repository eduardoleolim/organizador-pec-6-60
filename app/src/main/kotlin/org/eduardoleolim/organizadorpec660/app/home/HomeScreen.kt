package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import java.awt.Dimension

enum class HomeScreenTab {
    ENTIDADES_FEDERATIVAS,
    MUNICIPIOS,
    TIPOS_DE_ESTADISTICA,
    TIPOS_DE_INSTRUMENTO
}

class HomeScreen(
    private val window: ComposeWindow,
    private val user: AuthUserResponse
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeScreenModel(navigator) }
        var selectedTab by remember { mutableStateOf(HomeScreenTab.ENTIDADES_FEDERATIVAS) }

        LaunchedEffect(Unit) {
            window.apply {
                isResizable = true
                size = Dimension(800, 600)
                minimumSize = Dimension(800, 600)
                setLocationRelativeTo(null)
            }
        }

        Row {
            NavigationRail(
                modifier = Modifier.padding(8.dp),
                header = {
                    IconButton(
                        onClick = { },
                        content = { Icon(Icons.Default.Menu, contentDescription = null) }
                    )
                }
            ) {

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.ENTIDADES_FEDERATIVAS,
                    onClick = { selectedTab = HomeScreenTab.ENTIDADES_FEDERATIVAS },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Entidades\nfederativas", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.MUNICIPIOS,
                    onClick = { selectedTab = HomeScreenTab.MUNICIPIOS },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == HomeScreenTab.MUNICIPIOS) Icons.Default.ListAlt else Icons.Outlined.ListAlt,
                            contentDescription = null
                        )
                    },
                    label = { Text(text = "Municípios", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.TIPOS_DE_ESTADISTICA,
                    onClick = { selectedTab = HomeScreenTab.TIPOS_DE_ESTADISTICA },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Tipos de\nestadísticas", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.TIPOS_DE_INSTRUMENTO,
                    onClick = {
                        selectedTab = HomeScreenTab.TIPOS_DE_INSTRUMENTO
                    },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Tipos de\ninstrumentos", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = false,
                    onClick = { screenModel.logout() },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    label = { Text("Cerrar sesión") }
                )
            }

            Divider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxHeight().width(1.dp)
            )

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                when (selectedTab) {
                    HomeScreenTab.ENTIDADES_FEDERATIVAS -> screenModel.navigateToFederalEntity()

                    HomeScreenTab.MUNICIPIOS -> screenModel.navigateToMunicipality()

                    HomeScreenTab.TIPOS_DE_ESTADISTICA -> screenModel.navigateToStatisticType()

                    HomeScreenTab.TIPOS_DE_INSTRUMENTO -> screenModel.navigateToInstrumentType()
                }
            }
        }
    }
}
