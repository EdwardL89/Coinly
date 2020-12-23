package com.eightnineapps.coinly.views.activities.startup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.startup.LoginViewModel
import com.eightnineapps.coinly.views.activities.profiles.CreateProfileActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_splash_screen)
        checkForReturningUser()
    }

    /**
     * Query the firestore to check if the current user is an existing one to determine
     * which activity to launch
     */
    private fun checkForReturningUser() {
        loginViewModel.attemptToGetCurrentUSer().addOnCompleteListener {
            if (it.isSuccessful) {
                handleUserQuery(it)
            } else {
                startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            }
        }
    }

    /**
     * Redirects the user to appropriate activity depending on whether they've created a profile
     */
    private fun handleUserQuery(task: Task<DocumentSnapshot>) {
        if (!task.result?.exists()!!) {
            startActivity(Intent(this, CreateProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        } else {
            startActivity(
                Intent(this, HomeActivity::class.java)
                .putExtra("current_user", task.result?.toObject(User::class.java)!!)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
}