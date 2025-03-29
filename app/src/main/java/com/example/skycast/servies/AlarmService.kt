package com.example.skycast.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.skycast.R
import java.util.*
import android.content.*

import androidx.work.*
import com.example.skycast.util.AlarmWorker
import java.util.concurrent.TimeUnit


class AlarmService : Service(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var ringtone: Ringtone? = null
    private var message: String = "Fetching weather data..." // Default message

    private val channelId = "weather_alarms_channel"
    private val notificationId = 1
    private val stopAction = "com.example.skycast.STOP_ALARM"

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.weathersync.ALARM_TRIGGER" -> {
                    val temperature = intent.getDoubleExtra("temperature", 0.0)
                    val description = intent.getStringExtra("description") ?: "Unknown"
                    val humidity = intent.getIntExtra("humidity", 0)
                    val tempUnit = intent.getStringExtra("currentTemperatureUnit") ?: "°C"

                    message = "Weather: $description,\nTemperature: ${temperature}°$tempUnit,\nHumidity: $humidity%."
                    Log.d("AlarmService", "Updated Message: $message")

                    updateNotification(message)
                    playNotificationSound()
                }
                stopAction -> {
                    stopSelf() // Stop the service when the button is clicked
                }
            }
        }
    }

    @SuppressLint("ForegroundServiceType", "UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction("com.example.weathersync.ALARM_TRIGGER")
            addAction(stopAction)
        }
        registerReceiver(broadcastReceiver, filter)
        createNotificationChannel()

        startForeground(notificationId, createNotification(message))

        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun createNotification(contentText: String): Notification {
        val stopIntent = Intent(stopAction).apply { setPackage(packageName) }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Weather Alert")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message ?: "Weather alert dismissed"))
            .addAction(R.drawable.reject, getString(R.string.cancel_alarm), stopPendingIntent) // Cancel button
            .build()
    }

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        val notification = createNotification(contentText)
        manager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alarms",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun playNotificationSound() {
        ringtone?.stop()
        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        ringtone?.play()
        Handler(Looper.getMainLooper()).postDelayed({
            ringtone?.stop()
            if (isTtsInitialized) speakAlarmMessage(message)
        }, 5000)
    }

    private fun speakAlarmMessage(message: String) {
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ALARM_MESSAGE")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isTtsInitialized = true
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        ringtone?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
