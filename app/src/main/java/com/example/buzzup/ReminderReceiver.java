package com.example.buzzup;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Receiver called");
        String event_name = intent.getStringExtra("event_name");
        String evnt_id = intent.getStringExtra("eventID");
        Intent notificaionIntent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, notificaionIntent, 0);
        String ch_id = "buzzup_1";
        NotificationManager notifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notCh = new NotificationChannel(ch_id, "BuzzUp notifications", NotificationManager.IMPORTANCE_HIGH);
            notCh.setDescription("BuzzUp");
            notCh.enableVibration(true);
            notifM.createNotificationChannel(notCh);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ch_id)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(event_name)
                .setContentText("An event you showed interest in is happening right now.")
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notifM.notify(1, builder.build());
    }
}
