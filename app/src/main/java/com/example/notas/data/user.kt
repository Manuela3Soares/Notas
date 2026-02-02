package com.example.notas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String, // In a real app, use BCrypt or similar
    val createdAt: Long = System.currentTimeMillis()
)
