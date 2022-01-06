package com.example.android_final.Notify;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.android_final.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "ayeee";
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
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "ec")
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle(title)
                .setContentText(text)
//                .setSound(uri)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                //.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ayaya))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        // Show notification
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
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