package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.dao.ScanReportDao
import com.example.data.model.UserEntity
import com.example.data.model.ScanReportEntity
import kotlinx.coroutines.flow.Flow

class HealthRepository(
    private val userDao: UserDao,
    private val scanReportDao: ScanReportDao
) {
    val activeUser: Flow<UserEntity?> = userDao.getActiveUser()

    suspend fun getActiveUserSync(): UserEntity? = userDao.getActiveUserSync()

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun logoutActiveUser() {
        userDao.logoutAllUsers()
    }

    fun getReportsForUser(email: String): Flow<List<ScanReportEntity>> {
        return scanReportDao.getReportsForUser(email)
    }

    fun getReportById(id: Long): Flow<ScanReportEntity?> {
        return scanReportDao.getReportById(id)
    }

    suspend fun getReportByIdSync(id: Long): ScanReportEntity? {
        return scanReportDao.getReportByIdSync(id)
    }

    suspend fun insertReport(report: ScanReportEntity): Long {
        val localId = scanReportDao.insertReport(report)
        try {
            com.example.data.database.MongoDBManager.saveReport(report.copy(id = localId))
        } catch (t: Throwable) {
            // Silently fallback to Room offline storage
        }
        return localId
    }

    suspend fun deleteReport(id: Long) {
        scanReportDao.deleteReportById(id)
    }
}
