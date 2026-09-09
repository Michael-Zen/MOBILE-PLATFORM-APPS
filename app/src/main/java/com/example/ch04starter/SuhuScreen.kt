package com.example.ch04starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// ============================================================================
// Pertemuan 4 — SuhuScreen (soal #2 di Tugas Pertemuan 4)
//
// Kalkulator konversi suhu Celsius <-> Fahrenheit <-> Kelvin, memakai
// `derivedStateOf` — sama seperti pola `bmi` di BmiScreen, tapi di sini
// hasil derivasinya dipakai sebagai TAMPILAN saja (read-only), bukan input.
//
// Rumus yang perlu diimplementasikan:
//   F = C * 9/5 + 32
//   K = C + 273.15
//
// [x] 2a. Field "Celsius" di bawah masih kosong logikanya — parse teks
//         input jadi Float (hati-hati input tidak valid / kosong!)
// [x] 2b. Hitung `fahrenheit` dan `kelvin` dari `celsius` memakai
//         `derivedStateOf`, lalu tampilkan di kedua Text di bawah field
// ============================================================================

@Composable
fun SuhuScreen() {
    var celsiusText by rememberSaveable { mutableStateOf("0") }

    val celsius = celsiusText.toFloatOrNull() ?: 0f

    val fahrenheit by remember(celsius) {
        derivedStateOf { celsius * 9 / 5 + 32 }
    }
    val kelvin by remember(celsius) {
        derivedStateOf { celsius + 273.15f }
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text  = "Konversi Suhu",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value         = celsiusText,
            onValueChange = { celsiusText = it },
            label         = { Text("Celsius (°C)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.fillMaxWidth()
        )

        Text(
            text  = "Fahrenheit: ${"%.1f".format(fahrenheit)} °F",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text  = "Kelvin: ${"%.1f".format(kelvin)} K",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SuhuScreenPreview() {
    Ch04StarterTheme { SuhuScreen() }
}
