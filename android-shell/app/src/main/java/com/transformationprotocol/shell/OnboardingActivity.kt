package com.transformationprotocol.shell

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Review note: if Health Connect launches this app from its own
        // onboarding flow, send the user straight into the shell so the
        // tracker can request permission and continue normally.
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
        )
        finish()
    }
}
