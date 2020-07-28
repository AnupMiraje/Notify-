package in.orange.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;


public class ChooseActivity extends Activity {

    RelativeLayout r;
    Button student,faculty;
    static int y=0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        r=findViewById(R.id.choose_activity_relative_layout);
        int y=r.getHeight();
        Log.d("height",y+"");
        student=findViewById(R.id.choose_activity_student);
        faculty=findViewById(R.id.choose_activity_faculty);
        r.removeView(student);
        r.removeView(faculty);
        student.setHeight(150);
        faculty.setHeight(150);
        RelativeLayout.LayoutParams p=
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams q=
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.topMargin=(y/2)-275;
        q.topMargin=(y/2)+125;
        p.leftMargin=80;
        p.rightMargin=80;
        q.leftMargin=80;
        q.rightMargin=80;
        int x=p.topMargin;
        int z=q.topMargin;
        Log.d("height",x+" "+y+" "+z);
        r.addView(student,p);
        r.addView(faculty,q);
    }

    @Override
    protected void onDestroy() {
        Log.d("noticedddd","done");
        //NoticeDatabase.getNoticeDatabaseInstance(getApplicationContext()).updatex();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);

        student=findViewById(R.id.choose_activity_student);
        faculty=findViewById(R.id.choose_activity_faculty);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,old_main.class));
            }
        });
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faculty.setBackgroundColor(Color.GRAY);
                startActivity(new Intent(ChooseActivity.this,FacultyLogin.class));
            }
        });

    }


}
