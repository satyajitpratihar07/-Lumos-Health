package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val name: String,
    val gender: String = "Not Specified",
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val avatarSeed: String = "default",
    val emailVerified: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isLoggedIn: Boolean = false
)
