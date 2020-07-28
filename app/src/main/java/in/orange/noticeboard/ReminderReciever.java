package in.orange.noticeboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReciever extends BroadcastReceiver{
    private String name,subject;
    @Override
    public void onReceive(Context context, Intent intent){
        subject=intent.getStringExtra("subject");
        name=intent.getStringExtra("department");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"channel1")
                .setSmallIcon(R.drawable.pdfviewer)
                .setContentTitle(name)
                .setContentText(subject)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(context);
        notificationManager.notify(1,builder.build());

        Vibrator vibrator=(Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        Uri notification =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r= RingtoneManager.getRingtone(context,notification);
        r.play();
    }
}