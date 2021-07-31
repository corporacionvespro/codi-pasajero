package codi.drive.pasajero.chiclayo.notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import codi.drive.pasajero.chiclayo.MainActivity;
import codi.drive.pasajero.chiclayo.NotificadoActivity;
import codi.drive.pasajero.chiclayo.R;
import codi.drive.pasajero.chiclayo.SolicitudTaxiActivity;
import codi.drive.pasajero.chiclayo.compartido.PrefUtil;
import codi.drive.pasajero.chiclayo.compartido.Utils;

import static codi.drive.pasajero.chiclayo.compartido.Funciones.primero;

/**
 * By: El Bryant
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;
    private static final String TAG = "FMSConductor";
    CountDownTimer countDownTimer;
    MediaPlayer mPlayer;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("contador para notif", "" + millisUntilFinished);
            }
            @Override
            public void onFinish() {
                Log.i("contador para notif", "borrado");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(STOP_FOREGROUND_REMOVE);
                }
            }
        };
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage);
            if (remoteMessage.getData().containsKey("accion")) {
                procesarSolicitud(remoteMessage.getData());
            } else {
                if (remoteMessage.getNotification() != null) {
                    sendNotification(remoteMessage);
                }
            }
        } else {
            if (remoteMessage.getNotification() != null) {
                sendNotification(remoteMessage);
            }
        }
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    private void procesarSolicitud(Map<String, String> datos) {
        Intent intent = new Intent(PrefUtil.BROADCAST_SOLICITUD_TAXI);
        for (Map.Entry<String, String> entry : datos.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        broadcaster.sendBroadcast(intent);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, NotificadoActivity.class);
        intent.putExtra("idSolicitud", remoteMessage.getData().toString().substring(remoteMessage.getData().toString().indexOf("id_pedido=") + 10,
                remoteMessage.getData().toString().indexOf("}")));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1234, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel (Utils.CHANNEL_ID, "Location Service Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, Utils.CHANNEL_ID)
                .setContentTitle("Codi Drive")
                .setContentTitle("El conductor ha llegado a recogerte")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1234, notification);
        mPlayer = MediaPlayer.create(this, R.raw.alerta_codi_llegada);
        mPlayer.setLooping(false);
        mPlayer.start();
        countDownTimer.start();
        startActivity(new Intent(this, NotificadoActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("idSolicitud",
                remoteMessage.getData().toString().substring(remoteMessage.getData().toString().indexOf("id_pedido=") + 10, remoteMessage.getData().toString().indexOf("}"))));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN", s);
        insertarToken(s);
    }

    public void insertarToken(String idconductor) {
        Log.i("insertarToken", "AccesoActivity");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("WebService", "actualizarToken", task.getException());
                return;
            }
            String token = task.getResult().getToken();
            new Thread() {
                @Override
                public void run() {
                    final String result = primero("http://codidrive.com/vespro/logica/insertar_token.php?idconductor=" + idconductor + "&token=" + token);
                    Log.i("insertarToken", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });
    }
}
