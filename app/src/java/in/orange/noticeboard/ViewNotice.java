package in.orange.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewNotice extends AppCompatActivity {

    private TextView subject,summary,release_date,time,end_date;
    private ImageView iv;

    private Button pin,share,remind;

    DisplayNotice displayNotice;
    String id,category;

    private static InputStream inputStream;

    DatabaseReference mRef;

    class LoadActivity extends AsyncTask{

        private ProgressDialog progress;
        private RequestCreator picasso;

        public String cal()
        {
            long l1=Long.parseLong(displayNotice.release_date);
            Date d2=NoticeBoard.getDate();
            long l2=d2.getTime();
            Date d3=new Date(l2-l1-19800000);
            Calendar c=Calendar.getInstance();
            c.setTime(d3);
            int year=c.get(Calendar.YEAR)-1970;
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH)-1;
            int hrs=c.get(Calendar.HOUR_OF_DAY);
            int min=c.get(Calendar.MINUTE);
            if(year>1){return year+" years ago";}
            else if(year==1) {return year+" year ago";}
            else if(month>1) {return month+" months ago"; }
            else if(month==1) {return year+" month ago"; }
            else if(day>1) { return day+" days ago"; }
            else if(day==1) { return day+" day ago"; }
            else if(hrs>1) { return hrs+" hrs ago"; }
            else if(hrs==1) { return hrs+" hr ago"; }
            else if(min>1) { return min+" mins ago"; }
            else if(min==1) { return min+" min ago"; }
            return "0 mins ago";
        }

        public void setAttributes()
        {
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

            subject=(TextView)findViewById(R.id.view_notice_subject);
            subject.setText(displayNotice.subject);

            summary=(TextView)findViewById(R.id.view_notice_summary);
            summary.setText(displayNotice.summary);

            release_date=(TextView)findViewById(R.id.view_notice_rel_date);
            release_date.setText(sdf.format(new Date(Long.parseLong(displayNotice.release_date))));

            end_date=(TextView)findViewById(R.id.view_notice_end_date);
            end_date.setText(sdf.format(new Date(Long.parseLong(displayNotice.end_date))));

            time=(TextView)findViewById(R.id.time);
            time.setText(cal());

            iv=(ImageView)findViewById(R.id.view_notice_data);

            if(displayNotice.file.equals("true")){
                picasso=Picasso.get().load(displayNotice.data);
            }
            else{
                iv.setImageResource(R.drawable.pdfviewer);
            }

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayNotice.file.equals("true")){
                        Intent intent=new Intent(ViewNotice.this,ImageViewer.class);
                        intent.putExtra("image",displayNotice.data);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(ViewNotice.this,PdfView.class);
                        intent.putExtra("flag","view");
                        intent.putExtra("data",displayNotice.data);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayNotice=NoticeDatabase.getNoticeDatabaseInstance(ViewNotice.this).getNotice(id,category);
            Log.d("sahil",displayNotice.id+" "+displayNotice.name+" "+displayNotice.data);
            progress=new ProgressDialog(ViewNotice.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            if(displayNotice.file.equals("true")){
                iv=(ImageView)findViewById(R.id.view_notice_data);
                picasso.into(iv);
            }
            super.onPostExecute(o);
            progress.dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            setAttributes();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        category=intent.getStringExtra("category");

        LoadActivity loadActivity=new LoadActivity();
        loadActivity.execute();


        if(displayNotice.file.equals("true")){
            try{
                //Picasso.get().load(displayNotice.data).into(iv);
            }catch(IllegalStateException e){}
        }

        pin=(Button)findViewById(R.id.view_notice_pin);
        if(displayNotice.pin.equals("0")){
            pin.setBackgroundColor(Color.RED);
        }
        else{
            pin.setBackgroundColor(Color.GREEN);
        }
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayNotice.pin.equals("0")){
                    displayNotice.pin="1";
                    pin.setBackgroundColor(Color.GREEN);
                    NoticeDatabase.getNoticeDatabaseInstance(ViewNotice.this).updatepin(displayNotice.name,displayNotice.id,"1");
                    Toast.makeText(ViewNotice.this, "Notice pinned", Toast.LENGTH_LONG).show();
                }
                else{
                    displayNotice.pin="0";
                    pin.setBackgroundColor(Color.RED);
                    NoticeDatabase.getNoticeDatabaseInstance(ViewNotice.this).updatepin(displayNotice.name,displayNotice.id,"0");
                    Toast.makeText(ViewNotice.this, "Notice unpinned", Toast.LENGTH_LONG).show();
                }
            }
        });

        share=(Button)findViewById(R.id.view_notice_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent=new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,displayNotice.subject);
                sendIntent.setType("text/plain");
                Intent.createChooser(sendIntent,"Share via");
                v.getContext().startActivity(sendIntent);
            }
        });

        remind=(Button)findViewById(R.id.view_notice_remind);
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(ViewNotice.this,CreateReminders.class);
                //startActivity(intent);
            }
        });
    }
}
