package com.example.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class HealthConnectAvailability {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}

data class WeightDataPoint(
    val weightKg: Double,
    val time: Instant
)

data class SleepDataPoint(
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Long
)

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class),
    )

    fun checkAvailability(): HealthConnectAvailability {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            when (status) {
                HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.INSTALLED
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
                else -> HealthConnectAvailability.NOT_SUPPORTED
            }
        } catch (_: Exception) {
            HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    suspend fun hasAllPermissions(): Boolean = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext false
        try {
            val granted = client.permissionController.getGrantedPermissions()
            permissions.all { it in granted }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun writeWeight(weightKg: Double, time: Instant = Instant.now()): Result<Unit> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(IllegalStateException("Health Connect is unavailable"))
        try {
            val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(time)
            val record = WeightRecord(
                weight = Mass.kilograms(weightKg),
                time = time,
                zoneOffset = zoneOffset,
                metadata = Metadata()
            )
            client.insertRecords(listOf(record))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readRecentWeights(days: Long = 30): Result<List<WeightDataPoint>> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(IllegalStateException("Health Connect is unavailable"))
        try {
            val startTime = Instant.now().minus(days, ChronoUnit.DAYS)
            val endTime = Instant.now()
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            val points = response.records.map { record ->
                WeightDataPoint(
                    weightKg = record.weight.inKilograms,
                    time = record.time
                )
            }.sortedByDescending { it.time }
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun writeSleepSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "Sleep Session"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(IllegalStateException("Health Connect is unavailable"))
        try {
            val startOffset = ZoneOffset.systemDefault().rules.getOffset(startTime)
            val endOffset = ZoneOffset.systemDefault().rules.getOffset(endTime)
            val record = SleepSessionRecord(
                startTime = startTime,
                startZoneOffset = startOffset,
                endTime = endTime,
                endZoneOffset = endOffset,
                title = title,
                metadata = Metadata()
            )
            client.insertRecords(listOf(record))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readRecentSleepSessions(days: Long = 7): Result<List<SleepDataPoint>> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(IllegalStateException("Health Connect is unavailable"))
        try {
            val startTime = Instant.now().minus(days, ChronoUnit.DAYS)
            val endTime = Instant.now()
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            val points = response.records.map { record ->
                val duration = ChronoUnit.MINUTES.between(record.startTime, record.endTime)
                SleepDataPoint(
                    title = record.title ?: "Sleep Session",
                    startTime = record.startTime,
                    endTime = record.endTime,
                    durationMinutes = duration
                )
            }.sortedByDescending { it.startTime }
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun writeHydration(volumeLiters: Double, time: Instant = Instant.now()): Result<Unit> = withContext(Dispatchers.IO) {
        val client = healthConnectClient ?: return@withContext Result.failure(IllegalStateException("Health Connect is unavailable"))
        try {
            val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(time)
            val record = HydrationRecord(
                startTime = time.minusSeconds(60),
                startZoneOffset = zoneOffset,
                endTime = time,
                endZoneOffset = zoneOffset,
                volume = Volume.liters(volumeLiters),
                metadata = Metadata()
            )
            client.insertRecords(listOf(record))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
