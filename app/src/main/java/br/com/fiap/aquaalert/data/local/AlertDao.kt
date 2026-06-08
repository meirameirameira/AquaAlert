package br.com.fiap.aquaalert.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.fiap.aquaalert.data.model.FloodAlert

@Dao
interface AlertDao {

    @Query("SELECT * FROM flood_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): LiveData<List<FloodAlert>>

    @Query("SELECT * FROM flood_alerts WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    fun getActiveAlerts(): LiveData<List<FloodAlert>>

    @Query("SELECT COUNT(*) FROM flood_alerts WHERE status = 'ACTIVE'")
    fun getActiveAlertCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: FloodAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<FloodAlert>)

    @Update
    suspend fun updateAlert(alert: FloodAlert)

    @Delete
    suspend fun deleteAlert(alert: FloodAlert)

    @Query("DELETE FROM flood_alerts")
    suspend fun deleteAll()
}
