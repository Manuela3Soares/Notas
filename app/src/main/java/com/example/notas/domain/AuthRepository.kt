package com.example.notas.domain

import com.example.notas.data.User
import com.example.notas.data.UserDao
import com.example.notas.data.SessionManager

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    val currentUserId = sessionManager.userId

    suspend fun login(email: String, passwordRaw: String): Result<User> {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.passwordHash == passwordRaw) { // In prod, check hash
            sessionManager.saveSession(user.id)
            Result.success(user)
        } else {
            Result.failure(Exception("Email ou senha inválidos"))
        }
    }

    suspend fun signup(username: String, email: String, passwordRaw: String): Result<User> {
        val existingUsername = userDao.getUserByUsername(username)
        if (existingUsername != null) return Result.failure(Exception("Nome de usuário já existe"))

        val existingEmail = userDao.getUserByEmail(email)
        if (existingEmail != null) return Result.failure(Exception("Email já está em uso"))
        
        val user = User(username = username, email = email, passwordHash = passwordRaw)
        val id = userDao.insert(user)
        val newUser = user.copy(id = id)
        sessionManager.saveSession(id)
        return Result.success(newUser)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
