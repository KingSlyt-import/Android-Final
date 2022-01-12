package com.example.android_final.Notify;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.example.android_final.MainActivity;
import com.example.android_final.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationReceiver extends BroadcastReceiver {
    FirebaseFirestore db;
    String title = "";
    String text = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build notification based on Intent'
        title = intent.getStringExtra("title");
        text = intent.getStringExtra("text");
        db = FirebaseFirestore.getInstance();
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Intent tapnotifi = new Intent(context, MainActivity.class);
        tapnotifi.putExtra("des", "schedule");
        tapnotifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivities(context, 1, new Intent[]{tapnotifi}, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "ec")
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
//                .setSound(uri)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                //.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ayaya))
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
//        DocumentReference docRef = db.collection("notifyID").document("jZMYg6szkhWGPR1651Rd");
//        db.runTransaction(new Transaction.Function<Void>() {
//            @Override
//            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
//                DocumentSnapshot snapshot = transaction.get(docRef);
//
//                // Note: this could be done without a transaction
//                //       by updating the population using FieldValue.increment()
//                double temp = snapshot.getDouble("id");
//                manager.notify((int) temp, notification.build());
//                double newPopulation = temp + 1;
//                transaction.update(docRef, "id", newPopulation);
//                // Success
//                return null;
//            }
//        }).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Transaction success!");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Transaction failure.", e);
//            }
//        });
    }
}