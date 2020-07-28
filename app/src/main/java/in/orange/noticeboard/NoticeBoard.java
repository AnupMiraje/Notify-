package in.orange.noticeboard;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

public class NoticeBoard extends Application {

    private static Firebase firebase;
    private static String timestamp="42";
    private static Date d;
    private DataSnapshot ds;

    public static Date gettime()
    {
        return d;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
        getDate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        createNotificationChannels();

        FirebaseMessaging.getInstance().subscribeToTopic("sahil");


        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("General");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Central");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Staff");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Computer Science and Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Mechanical Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Electronics Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Biotechnology Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Electronics and Telecommunication Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Environmental Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Electrical Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Civil Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Production Engineering");
        NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).putNotices("Information Technology");
    }


    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1=new NotificationChannel("channel1","channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("this is channel 1");
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
    }



    public static Date getDate()
    {
        firebase=new Firebase("https://apponfire-4b488.firebaseio.com/Date");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timestamp=dataSnapshot.getValue().toString();
                Long t=Long.parseLong(timestamp);
                d=new Date(t);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        firebase.setValue(ServerValue.TIMESTAMP);
        if(d!=null)
            Log.d("date",d.getTime()+" "+d);
        return d;
    }
}