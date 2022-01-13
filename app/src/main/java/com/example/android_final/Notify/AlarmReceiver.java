package com.example.android_final.Notify;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.android_final.MainActivity;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlarmReceiver extends BroadcastReceiver {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build notification based on Intent
        String document = intent.getStringExtra("document");
        String temp = intent.getStringExtra("signal");
        Intent intent1 = new Intent(context, Audio.class);
        intent1.putExtra("signal", temp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        if (title != null && text != null) {
            Intent tapnotifi = new Intent(context, MainActivity.class);
            tapnotifi.putExtra("des", "alarm");
            tapnotifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent resultPendingIntent = PendingIntent.getActivities(context, 1, new Intent[]{tapnotifi}, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "ec")
                    .setSmallIcon(R.drawable.ic_clock)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            // Show notification
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
                wl.acquire(3000); //set your time in milliseconds
            }
            manager.notify((int)Math.floor(Math.random()*(1000-10+1)+10), notification.build());
        }
    }
}