package br.com.fiap.aquaalert.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.fiap.aquaalert.data.model.FloodReport

@Dao
interface ReportDao {

    @Query("SELECT * FROM flood_reports ORDER BY timestamp DESC")
    fun getAllReports(): LiveData<List<FloodReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: FloodReport): Long

    @Delete
    suspend fun deleteReport(report: FloodReport)

    @Query("SELECT COUNT(*) FROM flood_reports")
    fun getReportCount(): LiveData<Int>
}
