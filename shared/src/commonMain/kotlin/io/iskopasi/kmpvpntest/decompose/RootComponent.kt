package io.iskopasi.kmpvpntest.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext

class RootComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {
    val main = MainComponentImpl(
        componentContext = childContext("main"),
    )
}