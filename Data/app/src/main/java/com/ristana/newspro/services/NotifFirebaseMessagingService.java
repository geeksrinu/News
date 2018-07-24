package com.ristana.newspro.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ristana.newspro.R;
import com.ristana.newspro.manager.PrefManager;
import com.ristana.newspro.ui.CategoryActivity;
import com.ristana.newspro.ui.LoadActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hsn on 23/02/2018.
 */


public class NotifFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {




        String type = remoteMessage.getData().get("type");
        String id = remoteMessage.getData().get("id");
        String title = remoteMessage.getData().get("title");
        String image = remoteMessage.getData().get("image");
        String icon = remoteMessage.getData().get("icon");
        String message = remoteMessage.getData().get("message");
        String languages = remoteMessage.getData().get("languages");

        PrefManager prf = new PrefManager(getApplicationContext());

        List<String> list = new ArrayList<String>(Arrays.asList(languages.split("_")));
        Boolean exit = false;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(prf.getString("LANGUAGE_DEFAULT"))){
                exit=true;
            }
        }
        if (!prf.getString("notifications").equals("false") && exit) {

            if (type.equals("article")){



                sendNotification(
                        id,
                        title,
                        image,
                        icon,
                        message

                );
            }else if(type.equals("category")){
                String category_title = remoteMessage.getData().get("title_category");
                String category_image = remoteMessage.getData().get("image_category");
                String color_image = remoteMessage.getData().get("color_category");

                sendNotificationCategory(
                        id,
                        title,
                        image,
                        icon,
                        message,
                        category_title,
                        category_image,
                        color_image);
            } else if (type.equals("link")){
                String link = remoteMessage.getData().get("link");

                sendNotificationUrl(
                        id,
                        title,
                        image,
                        icon,
                        message,
                        link
                );
            }
        }


    }
    private void sendNotificationCategory(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message,
            String category_title,
            String category_image,
            String category_color
    ) {


        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));


        intent.putExtra("id", Integer.parseInt(id));
        intent.putExtra("title",category_title);
        intent.putExtra("image",category_image);
        intent.putExtra("color",category_color);
        intent.putExtra("from", "notification");




        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(icon)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))

                ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // The id of the channel.
            String channelid = "my_channel_01";

            // The user-visible name of the channel.
            CharSequence name = title;

            // The user-visible description of the channel.
            String description = title;

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = null;

            mChannel = new NotificationChannel(channelid, name,importance);


            // Configure the notification channel.
            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(mChannel);

            notificationBuilder.setChannelId(channelid);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(id) /* ID of notification */, notificationBuilder.build());


    }
    private void sendNotificationUrl(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message,
            String url

    ) {




        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);



        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);





        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(icon)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))

                ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // The id of the channel.
            String channelid = "my_channel_01";

            // The user-visible name of the channel.
            CharSequence name = title;

            // The user-visible description of the channel.
            String description = title;

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = null;

            mChannel = new NotificationChannel(channelid, name,importance);


            // Configure the notification channel.
            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(mChannel);

            notificationBuilder.setChannelId(channelid);
        }


        NotificationManager notificationManager2 =  (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager2.notify(0, notificationBuilder.build());




    }
    private void sendNotification(
            String id,
            String title,
            String imageUri,
            String iconUrl,
            String message
    ) {

        Bitmap image = getBitmapfromUrl(imageUri);
        Bitmap icon = getBitmapfromUrl(iconUrl);
        Intent intent = new Intent(this, LoadActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));


        intent.putExtra("id", Integer.parseInt(id));


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))

                ;



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // The id of the channel.
            String channelid = "my_channel_01";

            // The user-visible name of the channel.
            CharSequence name = title;

            // The user-visible description of the channel.
            String description = title;

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = null;

            mChannel = new NotificationChannel(channelid, name,importance);


            // Configure the notification channel.
            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(mChannel);

            notificationBuilder.setChannelId(channelid);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(id) /* ID of notification */, notificationBuilder.build());


    }
    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}