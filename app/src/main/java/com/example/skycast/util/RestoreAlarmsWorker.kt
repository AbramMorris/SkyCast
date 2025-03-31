package com.example.skycast.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.repo.AlarmRepoImp
import com.example.skycast.util.AlarmScheduler.scheduleAlarm

class RestoreAlarmsWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val alarmLocal = AlarmLocalDataSource(AppDatabase.getDatabase(applicationContext).alarmDao())
        val alarmRepo = AlarmRepoImp(alarmLocal)

        return try {
            alarmRepo.getAllAlarms().collect { alarms ->
                alarms.forEach { alarm ->
                    scheduleAlarm(applicationContext, alarm)
                }
                Log.d("RestoreAlarmsWorker", "Restored ${alarms.size} alarms after reboot.")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("RestoreAlarmsWorker", "Failed to restore alarms", e)
            Result.failure()
        }
    }
}
