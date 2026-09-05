package com.example.ch03

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch03.ui.theme.Ch03Theme

data class Mahasiswa(val nama: String, val nim: String, val ipk: Double, val prodi: String = "TI")

val dummyMahasiswa = listOf(
    Mahasiswa("Ali Rahman", "22001", 3.85, "TI"),
    Mahasiswa("Budi Santoso", "22002", 3.40, "SI"),
    Mahasiswa("Cici Wulandari", "22003", 3.92, "TI"),
    Mahasiswa("Dian Pratama", "22004", 2.95, "SI"),
    Mahasiswa("Eka Fitriani", "22005", 3.75, "TI"),
    Mahasiswa("Fandi Ahmad", "22006", 3.50, "TI"),
    Mahasiswa("Gita Permata", "22007", 3.88, "SI"),
    Mahasiswa("Hendra Kusuma", "22008", 2.80, "TI"),
    Mahasiswa("Indah Lestari", "22009", 3.65, "SI"),
    Mahasiswa("Joko Pratama", "22010", 3.20, "TI"),
    // Tambahan 10 mahasiswa
    Mahasiswa("Michael", "22011", 3.90, "TI"),
    Mahasiswa("Benaya", "22012", 3.15, "SI"),
    Mahasiswa("Yoel", "22013", 3.45, "TI"),
    Mahasiswa("Evan", "22014", 3.80, "SI"),
    Mahasiswa("Jayden Wijaya", "22015", 2.75, "TI"),
    Mahasiswa("Evan", "22016", 3.60, "SI"),
    Mahasiswa("Jesty", "22017", 3.95, "TI"),
    Mahasiswa("Felix", "22018", 3.30, "SI"),
    Mahasiswa("Darren", "22019", 3.70, "TI"),
    Mahasiswa("Tian", "22020", 3.55, "SI")
)

@Composable
fun StudentListScreen() {
    var selectedProdi by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "TI", "SI")

    val filteredList = if (selectedProdi == "Semua") {
        dummyMahasiswa
    } else {
        dummyMahasiswa.filter { it.prodi == selectedProdi }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Task 5: LazyRow berisi kategori
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                AssistChip(
                    onClick = { selectedProdi = category },
                    label = { Text(category) },
                    enabled = true
                )
            }
        }
        
        DaftarMahasiswa(mahasiswaList = filteredList)
    }
}

@Composable
fun DaftarMahasiswa(
    mahasiswaList: List<Mahasiswa>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Task 3: Header
        item {
            Text(
                text = "Daftar Mahasiswa Aktif",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = mahasiswaList,
            key = { it.nim }
        ) { mahasiswa ->
            MahasiswaCard(mahasiswa)
        }

        // Task 3: Footer
        item {
            Text(
                text = "Total Mahasiswa: ${mahasiswaList.size}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MahasiswaCard(mahasiswa: Mahasiswa) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${mahasiswa.nim} - ${mahasiswa.prodi}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "IPK ${mahasiswa.ipk}",
                style = MaterialTheme.typography.labelLarge,
                color = if (mahasiswa.ipk >= 3.5) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentListScreenPreview() {
    Ch03Theme {
        StudentListScreen()
    }
}
