package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.koin

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

abstract class KtormAppKoinComponent(private val context: KtormAppKoinContext) : KoinComponent {
    override fun getKoin(): Koin = context.koin
}
