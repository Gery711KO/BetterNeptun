package hu.kocsisgeri.betterneptun.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import hu.kocsisgeri.betterneptun.databinding.ActivitySplashBinding
import hu.kocsisgeri.betterneptun.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.initialized.observe(this as LifecycleOwner) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}