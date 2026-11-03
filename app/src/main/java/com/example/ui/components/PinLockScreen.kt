package com.example.ui.components

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun PinLockScreen(
    viewModel: PregnancyViewModel
) {
    val userEmail by viewModel.userEmail.collectAsState()
    val pinResetStatus by viewModel.pinResetStatus.collectAsState()
    val context = LocalContext.current

    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }
    var resetCodeInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }

    val activity = context as? FragmentActivity

    fun launchBiometricPrompt() {
        if (activity == null) return
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Unlock app via ViewModel
                    viewModel.unlockWithBiometric()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Fallback to PIN
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Maman & Bébé Biometric Security")
            .setSubtitle("Confirm fingerprint or face recognition to unlock")
            .setNegativeButtonText("Use PIN")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (_: Exception) {}
    }

    // Automatically prompt biometric on launch if available
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            launchBiometricPrompt()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Lock Emblem
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PeachHeaderBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "App Locked",
                    tint = DeepEspressoPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Maman & Bébé Protected",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDarkEspresso
                )
            )

            Text(
                text = "Enter your 4-digit PIN or use Biometric ID",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextWarmTaupe
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Biometric Unlock Button
            OutlinedButton(
                onClick = { launchBiometricPrompt() },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepEspressoPrimary),
                modifier = Modifier.testTag("btn_biometric_unlock")
            ) {
                Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp), tint = DeepEspressoPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unlock with Fingerprint / Face ID", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4 Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isError) Color.Red
                                else if (isFilled) DeepEspressoPrimary
                                else SquircleItemBg
                            )
                            .border(
                                width = 1.dp,
                                color = if (isFilled) DeepEspressoPrimary else TextWarmTaupe.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }

            if (isError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Incorrect PIN. Try again.",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (pinResetStatus != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = pinResetStatus ?: "",
                    color = DeepEspressoPrimary,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Numeric Keypad (1 to 9, 0, Backspace)
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("RESET", "0", "DEL")
            )

            keypad.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        when (key) {
                            "DEL" -> {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isError = false
                                            }
                                        }
                                        .testTag("keypad_del"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = TextDarkEspresso,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            "RESET" -> {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            viewModel.requestPinResetCode()
                                            showResetDialog = true
                                        }
                                        .testTag("keypad_reset"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Reset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepEspressoPrimary
                                        )
                                    )
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(CrispWhiteSurface)
                                        .clickable {
                                            if (enteredPin.length < 4) {
                                                val next = enteredPin + key
                                                enteredPin = next
                                                isError = false
                                                if (next.length == 4) {
                                                    val success = viewModel.unlockAppWithPin(next)
                                                    if (!success) {
                                                        isError = true
                                                        enteredPin = ""
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("keypad_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 22.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    if (showResetDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset PIN via Email", fontWeight = FontWeight.Bold, color = TextDarkEspresso) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "A 6-digit verification code was generated for:\n$userEmail",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe)
                    )
                    OutlinedTextField(
                        value = resetCodeInput,
                        onValueChange = { resetCodeInput = it },
                        label = { Text("6-Digit Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_lock_reset_code")
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinInput = it },
                        label = { Text("New 4-Digit PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_lock_new_pin")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = viewModel.verifyResetCodeAndResetPin(resetCodeInput, newPinInput)
                        if (ok) {
                            showResetDialog = false
                            enteredPin = ""
                            isError = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Unlock & Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
