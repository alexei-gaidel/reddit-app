package ru.gas.humblr.presentation.main

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.gas.humblr.R
import ru.gas.humblr.data.remote.models.TokenStorage
import ru.gas.humblr.databinding.ActivityMainBinding
import ru.gas.humblr.domain.model.NavArgs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)


        val sharedPrefs = getPreferences(MODE_PRIVATE) ?: return
        TokenStorage.accessToken = sharedPrefs.getString(ACCESS_TOKEN, null)
        TokenStorage.refreshToken = sharedPrefs.getString(REFRESH_TOKEN, null)

        authViewModel.onBoardingState.value =
            getPreferences(MODE_PRIVATE).getBoolean(NavArgs.ONBOARDED.key, false)



        val getAuthResponse =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val dataIntent = it.data ?: return@registerForActivityResult
                if (Uri.parse(dataIntent.toString()).getQueryParameter("error") != null) {

                    if (it.resultCode == RESULT_OK) {
                        Toast.makeText(applicationContext, getString(R.string.success), Toast.LENGTH_SHORT).show()
                    }
                    if (it.resultCode == RESULT_CANCELED) {
                        Toast.makeText(applicationContext, getString(R.string.failure), Toast.LENGTH_SHORT).show()
                    }
                }
            }


        lifecycleScope.launchWhenCreated {
            authViewModel.onBoardingState.collect { onBoardingState ->
                if (onBoardingState) {
                    if (TokenStorage.accessToken == null) {
                        if (intent.data == null) {
                            val authIntent = authViewModel.startAuthIntent()
                            getAuthResponse.launch(authIntent)


                        } else {
                            val code = Uri.parse(intent.data.toString()).getQueryParameter("code")
                            code?.let { authViewModel.requestToken(it) }
                            lifecycleScope.launchWhenStarted {
                                authViewModel.isAuthSuccess.collect { success ->
                                    if (success) {

                                        navController.navigate(R.id.navigation_main)
                                        navView.isVisible = true


                                    }


                                }
                            }
                        }

                    }

                }

            }
        }
    }


    override fun onStop() {
        super.onStop()
        val sharedPrefs = getPreferences(MODE_PRIVATE) ?: return
        with(sharedPrefs.edit()) {
            putString(ACCESS_TOKEN, TokenStorage.accessToken)
            putString(REFRESH_TOKEN, TokenStorage.refreshToken)
            apply()
        }
    }
    companion object{
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
//        const val ONBOARDED = "onboarded"
    }
}