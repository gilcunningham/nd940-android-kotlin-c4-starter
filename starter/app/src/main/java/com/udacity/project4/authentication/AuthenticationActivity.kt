package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.AUTHENTICATED
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.udacity.project4.authentication.AuthenticationViewModel.AuthenticationState.UNAUTHENTICATED
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthenticationViewModel>()

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
            //
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
        viewModel.onSignInFlowLaunched()
    }

    private fun navigateToMainActivity() = startActivity(
        Intent(this, RemindersActivity::class.java)
    )

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    localClassName,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(localClassName, "Sign in unsuccessful ${response?.error?.errorCode}")

                if (response == null) {
                    finishAffinity()
                }
                // TODO: Otherwise check response.getError().getErrorCode() and handle the error.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        observeAuthenticationState()
        setupBackNavigation()

        // TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback {
            finishAffinity()
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
                AUTHENTICATED -> navigateToMainActivity()
                UNAUTHENTICATED -> launchSignInFlow()
                INVALID_AUTHENTICATION -> Log.i(localClassName, "handle back navigation")
                else -> {}
            }
        }
    }

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

}