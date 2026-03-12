package com.example.watchdog

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun WatchdogScreen(context: Context) {

    var packageName by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text("App Watchdog", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = packageName,
            onValueChange = {
                packageName = it
            },
            label = { Text("Enter package name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {

            Text("Watchdog")

            Spacer(modifier = Modifier.width(20.dp))

            Switch(
                checked = enabled,
                onCheckedChange = {

                    enabled = it

                    if (enabled) {
                        val cleanPackage = packageName.trim().lowercase()
                        Log.d("WATCHDOG","Toggle ON")

                        val intent = Intent(context, WatchdogService::class.java)

                        intent.putExtra("package", packageName)
                        intent.putExtra("package", cleanPackage)
                        ContextCompat.startForegroundService(context, intent)

                    } else {

                        Log.d("WATCHDOG","Toggle OFF")

                        context.stopService(Intent(context, WatchdogService::class.java))

                    }

                }
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)

        }) {

            Text("Grant Usage Access")

        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )

            context.startActivity(intent)

        }) {

            Text("Grant Overlay Permission")

        }

    }

}