package io.iskopasi.kmpvpntest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import io.iskopasi.kmpvpntest.decompose.MainComponentImpl
import io.iskopasi.kmpvpntest.decompose.RootComponent
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val model = RootComponent(
            componentContext = defaultComponentContext(),
            koin = GlobalContext.get()
        )

        setContent {
            App(model= model.main)
        }
    }
}