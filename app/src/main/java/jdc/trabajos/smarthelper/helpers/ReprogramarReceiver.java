package jdc.trabajos.smarthelper.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.SystemClock;

import jdc.trabajos.smarthelper.R;

public class ReprogramarReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = intent.getStringExtra("titulo");
        String descripcion = intent.getStringExtra("descripcion");

        // Reprogramar en 10 minutos
        long tiempoPospuesto = SystemClock.elapsedRealtime() + 10 * 60 * 1000;

        Intent alertaIntent = new Intent(context, AlertaReceiver.class);
        alertaIntent.putExtra("titulo", titulo);
        alertaIntent.putExtra("descripcion", descripcion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),
                alertaIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, tiempoPospuesto, pendingIntent);
        }
    }
}
