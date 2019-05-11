package com.brahamaputra.mahindra.brahamaputrajio.Services;


import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.brahamaputra.mahindra.brahamaputrajio.Activities.DashboardCircularActivity;
import com.brahamaputra.mahindra.brahamaputrajio.R;


import java.util.Random;

public class NotifyService extends IntentService {
    private NotificationManager alarmNotificationManager;
    //Notification ID for Alarm
    public static final int NOTIFICATION_ID = 1;

    public NotifyService() {
        super("NotifyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Send notification
        sendNotification("Complete the remaining reading for battery bank test report");
     }

    private void sendNotification(String msg) {

       /* alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //get pending intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DashboardCircularActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle(msg)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        //notiy notification manager about new notification

        //alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());

        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        //final int SIMPLE_NOTIFICATION_RANDOM_ID = new Random().nextInt(851) + 90;
        notificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());*/


        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.applogo);
        Intent resultIntent = new Intent(this, DashboardCircularActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = ""+(int )(Math. random() * 5000 + 1);//getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.drawable.applogo)//.setSmallIcon(R.drawable.ic_circle)
                        .setContentTitle("Battery Bank Test Report")//.setContentTitle(getString(R.string.fcm_message))
                        .setContentText(msg)
                        //.setGroup(getApplicationContext().getPackageName())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        } else {

        }

        final int SIMPLE_NOTIFICATION_RANDOM_ID = new Random().nextInt(851) + 90;
        notificationManager.notify(SIMPLE_NOTIFICATION_RANDOM_ID, notificationBuilder.build());

    }
}
