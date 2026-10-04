package com.example.ch07.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Satu objek = satu baris tabel "notes".
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val updatedAt: Long = createdAt
)
