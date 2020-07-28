package in.orange.noticeboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import in.orange.noticeboard.faculty_main_ui.noticeboard.NoticeFragment;

public class FacultyMain extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser u;
    private Firebase root;
    private TextView email,name;
    private String uid;
    private ImageView imageview;
    private FacultyUser facultyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_main_activity);
        Toolbar toolbar = findViewById(R.id.faculty_main_toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.faculty_main_drawer_layout);
        NavigationView navigationView = findViewById(R.id.faculty_main_nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.faculty_main_noticeboard, R.id.faculty_main_pinned, R.id.faculty_main_reminders,
                    R.id.faculty_main_archives, R.id.faculty_main_myprofile, R.id.faculty_main_logout)
                    .setDrawerLayout(drawer)
                    .build();
        NavController navController = Navigation.findNavController(this, R.id.faculty_main_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView = findViewById(R.id.faculty_main_nav_view);
        View headerView = navigationView.getHeaderView(0);

        name=headerView.findViewById(R.id.faculty_main_nav_header_name);
        email=(TextView)headerView.findViewById(R.id.faculty_main_nav_header_email);
        imageview=(ImageView)headerView.findViewById(R.id.faculty_main_nav_header_imageView);

        LoadData loadData=new LoadData();
        loadData.execute();

    }

    class LoadData extends AsyncTask{
        private ProgressBar progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressBar(FacultyMain.this);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            u= FirebaseAuth.getInstance().getCurrentUser();
            try{
                uid = u.getUid();
            }catch(Exception e){}

            root=new Firebase("https://apponfire-4b488.firebaseio.com/user");

            root.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.getKey().equals(uid)){
                        facultyUser=dataSnapshot.getValue(FacultyUser.class);
                        name.setText(facultyUser.name);
                        email.setText(facultyUser.email);
                        Picasso.get().load(facultyUser.image).into(imageview);
                    }
                }
                @Override public void onChildChanged(DataSnapshot dataSnapshot,String s){}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot){}
                @Override public void onChildMoved(DataSnapshot dataSnapshot,String s){}
                @Override public void onCancelled(FirebaseError firebaseError){}
            });

            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.faculty_main_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.faculty_main_drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        }else {
        }
    }
}
