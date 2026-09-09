package com.example.ch04starter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// ============================================================================
// Pertemuan 4 — BmiScreen (soal #1 & #4 di Tugas Pertemuan 4)
//
// Base kalkulator BMI di bawah ini SUDAH BERFUNGSI (baseline dari praktikum:
// rememberSaveable untuk berat/tinggi/status hitung, derivedStateOf untuk
// bmi & kategori). Tugas kalian menambahkan tiga hal ke atasnya:
//
// [x] 1a. Tabel interpretasi BMI di bawah kartu hasil — 4 baris:
//         Kurus (<18.5) / Normal (18.5–24.9) / Gemuk (25–29.9) / Obesitas (≥30)
// [x] 1b. Tombol "Reset" yang mengembalikan berat & tinggi ke nilai default
//         (60 kg / 165 cm) dan menyembunyikan kartu hasil lagi
// [x] 4.  (Tantangan) Tampilkan Snackbar berisi kategori BMI selama 3 detik
//         setiap kali tombol "Hitung BMI" ditekan, pakai LaunchedEffect
// ============================================================================

@Composable
fun BmiScreen() {
    // rememberSaveable: bertahan saat rotasi layar
    var beratKg    by rememberSaveable { mutableFloatStateOf(60f) }
    var tinggiCm   by rememberSaveable { mutableFloatStateOf(165f) }
    var isDihitung by rememberSaveable { mutableStateOf(false) }

    // derivedStateOf: hanya recompose saat nilai BMI benar-benar berubah
    val bmi by remember {
        derivedStateOf {
            val tinggiM = tinggiCm / 100f
            beratKg / (tinggiM * tinggiM)
        }
    }

    val (kategori, warna) = remember(bmi) {
        when {
            bmi < 18.5f -> "Berat Badan Kurang" to Color(0xFF1565C0)
            bmi < 25.0f -> "Berat Badan Normal" to Color(0xFF2E7D32)
            bmi < 30.0f -> "Kelebihan Berat"    to Color(0xFFE65100)
            else        -> "Obesitas"            to Color(0xFFC62828)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(snackbarTrigger) {
        if (snackbarTrigger > 0) {
            snackbarHostState.showSnackbar(
                message = kategori,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text  = "Kalkulator BMI",
                style = MaterialTheme.typography.headlineMedium
            )

            InputSlider(
                label         = "Berat Badan",
                value         = beratKg,
                unit          = "kg",
                range         = 30f..150f,
                onValueChange = { beratKg = it; isDihitung = false }
            )

            InputSlider(
                label         = "Tinggi Badan",
                value         = tinggiCm,
                unit          = "cm",
                range         = 100f..220f,
                onValueChange = { tinggiCm = it; isDihitung = false }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick  = {
                        isDihitung = true
                        snackbarTrigger++
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hitung BMI")
                }

                // Tombol Reset
                Button(
                    onClick  = {
                        beratKg = 60f
                        tinggiCm = 165f
                        isDihitung = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            // AnimatedVisibility: fade-in saat isDihitung = true
            AnimatedVisibility(visible = isDihitung) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = warna.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier            = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text  = "%.1f".format(bmi),
                                style = MaterialTheme.typography.displayMedium,
                                color = warna
                            )
                            Text(
                                text  = kategori,
                                style = MaterialTheme.typography.titleMedium,
                                color = warna
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text  = "Berat: ${"%.0f".format(beratKg)} kg  " +
                                        "Tinggi: ${"%.0f".format(tinggiCm)} cm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    InterpretasiBmiTable()
                }
            }
        }
    }
}

@Composable
fun InputSlider(
    label:         String,
    value:         Float,
    unit:          String,
    range:         ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text       = "${"%.0f".format(value)} $unit",
                style      = MaterialTheme.typography.bodyLarge,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value         = value,
            onValueChange = onValueChange,
            valueRange    = range,
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InterpretasiBmiTable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Interpretasi BMI",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BmiRow("Kurus", "< 18.5")
                BmiRow("Normal", "18.5 – 24.9")
                BmiRow("Gemuk", "25 – 29.9")
                BmiRow("Obesitas", "≥ 30")
            }
        }
    }
}

@Composable
fun BmiRow(kategori: String, rentang: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = kategori, style = MaterialTheme.typography.bodyMedium)
        Text(text = rentang, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BmiScreenPreview() {
    Ch04StarterTheme { BmiScreen() }
}
