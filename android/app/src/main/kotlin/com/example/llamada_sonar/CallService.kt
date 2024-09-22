package com.example.llamada_sonar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat

class CallService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private var previousRingerMode: Int = AudioManager.RINGER_MODE_NORMAL
    private lateinit var audioManager: AudioManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación si es necesario (Android O+)
        createNotificationChannel()

        // Verificar acceso al modo "No molestar"
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        // Iniciar el listener para llamadas
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        // Iniciar un servicio en primer plano para mantenerlo activo
        val notification = createNotification()
        startForeground(1, notification)
    }

    // Listener para detectar cambios en el estado de las llamadas
  private val callStateListener = object : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                // Guardar el estado anterior del modo de sonido
                previousRingerMode = audioManager.ringerMode

                // Solo cambiar al modo normal si está en silencio o vibración
                if (previousRingerMode == AudioManager.RINGER_MODE_SILENT || 
                    previousRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                    
                    // Verificar que tenemos acceso a la política de notificaciones
                    if (notificationManager.isNotificationPolicyAccessGranted) {
                        // Desactivar "No molestar" si está activo
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    }

                    // Cambiar al modo normal y subir el volumen del tono de llamada
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_RING,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                        0
                    )
                }
            }
            TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_IDLE -> {
                // Restaurar el modo de sonido anterior cuando la llamada termine o sea contestada
                audioManager.ringerMode = previousRingerMode
            }
        }
    }
}


    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "llamada_sonar_channel")
            .setContentTitle("Servicio de llamadas")
            .setContentText("Escuchando llamadas entrantes")
            .setSmallIcon(R.drawable.ic_launcher)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "llamada_sonar_channel",
                "Llamada Sonar Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }
}
