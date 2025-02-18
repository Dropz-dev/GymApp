package com.example.gymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NumberPadInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLength: Int = 5,
    maxValue: Float? = null,
    showDecimal: Boolean = true,
    onDone: () -> Unit = {}
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    val filtered = if (showDecimal) {
                        newValue.filter { it.isDigit() || it == '.' }
                    } else {
                        newValue.filter { it.isDigit() }
                    }
                    
                    val isValid = filtered.toFloatOrNull()?.let { num ->
                        maxValue?.let { max -> num <= max } ?: true
                    } ?: true

                    if (isValid) {
                        onValueChange(filtered)
                        hasError = false
                        errorMessage = ""
                    } else {
                        hasError = true
                        errorMessage = "Value too high"
                    }
                }
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = hasError,
            supportingText = if (hasError) { { Text(errorMessage) } } else null,
            modifier = Modifier.fillMaxWidth()
        )

        // Number pad grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..3).forEach { col ->
                        val number = row * 3 + col
                        NumberPadButton(
                            text = number.toString(),
                            onClick = { onValueChange(value + number.toString()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Last row with 0, decimal, and done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberPadButton(
                    text = "0",
                    onClick = { onValueChange(value + "0") },
                    modifier = Modifier.weight(1f)
                )
                if (showDecimal) {
                    NumberPadButton(
                        text = ".",
                        onClick = { 
                            if (!value.contains(".")) {
                                onValueChange(value + ".")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                NumberPadButton(
                    text = "Done",
                    onClick = onDone,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NumberPadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
} 