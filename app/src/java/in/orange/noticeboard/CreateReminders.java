package in.orange.noticeboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class CreateReminders extends AppCompatActivity{

    private ToggleButton t1,t2,t3,t4,t5,t6;
    private Button b;
    private String c1;
    private TextView textView;
    Date d;
    Date timerDate;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private String subject,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_reminders);
        Toolbar toolbar = findViewById(R.id.create_reminders_toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        subject=intent.getStringExtra("subject");
        name=intent.getStringExtra("department");

        assert getSupportActionBar()!= null;
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.create_notice_discard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        d=NoticeBoard.getDate();

        textView=findViewById(R.id.tv);


        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag("TAG_DATETIME_FRAGMENT");
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        dateTimeFragment.setTimeZone(TimeZone.getDefault());
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(d);
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2030, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("notice", e.getMessage());
        }
        textView.setText(myDateFormat.format(d));

        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date){
                timerDate=date;
                textView.setText(myDateFormat.format(date));
            }
            @Override public void onNegativeButtonClick(Date date){}
            @Override
            public void onNeutralButtonClick(Date date) {
                textView.setText("");
            }
        });
        d=NoticeBoard.gettime();

        b=(Button)findViewById(R.id.datetimepicker);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.setDefaultDateTime(d);
                dateTimeFragment.setMinimumDateTime(d);
                dateTimeFragment.show(getSupportFragmentManager(), "TAG_DATETIME_FRAGMENT");
            }
        });

        t1=(ToggleButton)findViewById(R.id.t1);
        t2=(ToggleButton)findViewById(R.id.t2);
        t3=(ToggleButton)findViewById(R.id.t3);
        t4=(ToggleButton)findViewById(R.id.t4);
        t5=(ToggleButton)findViewById(R.id.t5);
        t6=(ToggleButton)findViewById(R.id.t6);

        t1.setText("1 Hrs");
        t1.setTextOff("1 Hrs");
        t1.setTextOn("1 Hrs");

        t2.setText("3 Hrs");
        t2.setTextOff("3 Hrs");
        t2.setTextOn("3 Hrs");

        t3.setText("6 Hrs");
        t3.setTextOff("6 Hrs");
        t3.setTextOn("6 Hrs");

        t4.setText("8 Hrs");
        t4.setTextOff("8 Hrs");
        t4.setTextOn("8 Hrs");

        t5.setText("12 Hrs");
        t5.setTextOff("12 Hrs");
        t5.setTextOn("12 Hrs");

        t6.setText("1 day");
        t6.setTextOff("1 day");
        t6.setTextOn("1 day");

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.setChecked(true);
                t2.setChecked(false);
                t3.setChecked(false);
                t4.setChecked(false);
                t5.setChecked(false);
                t6.setChecked(false);
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t2.setChecked(true);
                t1.setChecked(false);
                t3.setChecked(false);
                t4.setChecked(false);
                t5.setChecked(false);
                t6.setChecked(false);
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t3.setChecked(true);
                t2.setChecked(false);
                t1.setChecked(false);
                t4.setChecked(false);
                t5.setChecked(false);
                t6.setChecked(false);
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t4.setChecked(true);
                t2.setChecked(false);
                t3.setChecked(false);
                t1.setChecked(false);
                t5.setChecked(false);
                t6.setChecked(false);
            }
        });
        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t5.setChecked(true);
                t2.setChecked(false);
                t3.setChecked(false);
                t4.setChecked(false);
                t1.setChecked(false);
                t6.setChecked(false);
            }
        });
        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t6.setChecked(true);
                t2.setChecked(false);
                t3.setChecked(false);
                t4.setChecked(false);
                t5.setChecked(false);
                t1.setChecked(false);
            }
        });




    }

    public void check()
    {
        if(t1.isChecked())
        {
            c1="1hr";
        }
        else if(t2.isChecked())
        {
            c1="3hr";
        }
        else if(t3.isChecked())
        {
            c1="6hr";
        }
        else if(t4.isChecked())
        {
            c1="8hr";
        }
        else if(t5.isChecked())
        {
            c1="12hr";
        }
        else if(t6.isChecked())
        {
            c1="1day";
        }
    }

    public void setReminder(){
        d=NoticeBoard.getDate();
        d=NoticeBoard.getDate();
        if(d.before(timerDate)){
            Calendar c=Calendar.getInstance();
            c.setTime(timerDate);
            c.set(Calendar.SECOND,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(this,ReminderReciever.class);
            intent.putExtra("subject",subject);
            intent.putExtra("department",name);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(this,
                    (int)c.getTimeInMillis(),intent,0);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
            super.onBackPressed();
        }
        else{
            Toast.makeText(this, "Reminder Date is passed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.publish:
                setReminder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
