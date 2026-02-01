package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.AUTHENTICATED
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.UNAUTHENTICATED
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.databinding.ActivityRemindersBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * //TODO
 * This class should be the starting point of the app,
 * It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val loginLauncher = registerForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { }

    private val viewModel: AuthenticationViewModel by viewModels()

    private fun navigateToRemindersActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        setupObservables()
        setupBackNavigation()
        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun setContentView() {
        val binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback {
            finishAffinity()
        }
    }

    private fun setupObservables() {
        observeAuthenticationState()
        observeLoginClicked()
    }

    private fun observeLoginClicked() {
        viewModel.onLoginClicked.observe(this) { clicked ->
            if (clicked) {
                viewModel.launchSignInFlow(loginLauncher)
            }
        }
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                AUTHENTICATED -> {
                    navigateToRemindersActivity()
                }

                UNAUTHENTICATED -> {
                    Log.d(localClassName, "unauthenticated")
                }

                INVALID_AUTHENTICATION -> {
                    Log.d(localClassName, "invalid authentication")
                }

                else -> {}
            }
        }
    }
}