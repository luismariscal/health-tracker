package com.transformationprotocol.shell

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = (20 * resources.displayMetrics.density).toInt()

        val root = ScrollView(this).apply {
            setBackgroundColor(0xFF0B1220.toInt())
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        val title = TextView(this).apply {
            text = "Health Connect permissions"
            textSize = 22f
            setTextColor(0xFFFFFFFF.toInt())
        }

        val body = TextView(this).apply {
            text =
                "Transformation Protocol uses Health Connect to import weight, steps, sleep, and resting heart rate into the tracker. We only request read access for those records so the app can update your daily workflow and history."
            textSize = 16f
            setTextColor(0xFFD8E1EE.toInt())
            setLineSpacing(0f, 1.3f)
            setPadding(0, padding / 2, 0, padding)
        }

        val continueButton = Button(this).apply {
            text = "Open tracker"
            setOnClickListener {
                startActivity(Intent(this@PermissionsRationaleActivity, MainActivity::class.java))
                finish()
            }
        }

        val closeButton = Button(this).apply {
            text = "Close"
            setOnClickListener { finish() }
        }

        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START
            setPadding(0, padding / 2, 0, 0)
            addView(continueButton)
            addView(closeButton)
        }

        content.addView(title)
        content.addView(body)
        content.addView(buttonRow)
        root.addView(content)
        setContentView(root)
    }
}
