package jdc.trabajos.smarthelper.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import jdc.trabajos.smarthelper.R;
import jdc.trabajos.smarthelper.activities.MainActivity;

public class AlertaReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "canal_recordatorios";

    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = intent.getStringExtra("titulo");
        String descripcion = intent.getStringExtra("descripcion");

        crearCanalNotificacion(context);

        int notificationId = (int) System.currentTimeMillis();

        // Intent para abrir la app
        Intent intentAbrir = new Intent(context, MainActivity.class);
        PendingIntent piAbrir = PendingIntent.getActivity(
                context, notificationId,
                intentAbrir,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent para posponer (reprograma en 10 min)
        Intent intentPosponer = new Intent(context, ReprogramarReceiver.class);
        intentPosponer.putExtra("titulo", titulo);
        intentPosponer.putExtra("descripcion", descripcion);

        PendingIntent piPosponer = PendingIntent.getBroadcast(
                context, notificationId + 1,
                intentPosponer,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo != null ? titulo : "Recordatorio")
                .setContentText(descripcion != null ? descripcion : "¡Es hora de tu recordatorio!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(piAbrir)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_add, "Posponer 10 min", piPosponer);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(notificationId, builder.build());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para recordatorios importantes");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
