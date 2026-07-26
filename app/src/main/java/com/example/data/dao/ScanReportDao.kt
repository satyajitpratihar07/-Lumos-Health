package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ScanReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanReportDao {
    @Query("SELECT * FROM scan_reports WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getReportsForUser(email: String): Flow<List<ScanReportEntity>>

    @Query("SELECT * FROM scan_reports WHERE id = :id LIMIT 1")
    fun getReportById(id: Long): Flow<ScanReportEntity?>

    @Query("SELECT * FROM scan_reports WHERE id = :id LIMIT 1")
    suspend fun getReportByIdSync(id: Long): ScanReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ScanReportEntity): Long

    @Query("DELETE FROM scan_reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)
}
