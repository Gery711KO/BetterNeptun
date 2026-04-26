package hu.kocsisgeri.betterneptun.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import hu.kocsisgeri.betterneptun.ui.activity.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            viewModel.initialized.value != true
        }

        enableEdgeToEdge()

        viewModel.initialized.observe(this) { initialized ->
            if (initialized) {
                startActivity(
                    Intent(
                        this@SplashActivity,
                        MainActivity::class.java
                    )
                )
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }
}
