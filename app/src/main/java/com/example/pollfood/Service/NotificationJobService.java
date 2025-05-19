package com.example.pollfood.Service;
import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.pollfood.MainActivity;
import com.example.pollfood.R;

public class NotificationJobService extends JobService {

    private static final String CHANNEL_ID = "pollfood_delayed";

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.recipes)
                .setContentTitle("Itt az ideje szavazni")
                .setContentText("Időzített értesítés JobScedulerrel ez is plusz pont")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            jobFinished(params, false);
            return false;
        }
        notificationManager.notify(1001, builder.build());

        jobFinished(params, false); // job done, don't reschedule
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false; // no retry
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "pollfood_delayed";
            String description = "Channel for scheduled notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
