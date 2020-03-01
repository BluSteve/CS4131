package com.stevecao.assignment2.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.stevecao.assignment2.AddTempActivity;
import com.stevecao.assignment2.R;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notifIntent = new Intent(context, AddTempActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, notifIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "temperature")
                .setSmallIcon(R.drawable.ic_ac_unit_black_24dp)
                .setContentTitle(context.getString(R.string.notif_temperatureTitle))
                .setContentText(context.getString(R.string.notif_temperatureContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat.from(context).notify(0, builder.build());
    }
}
