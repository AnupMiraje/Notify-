package in.orange.noticeboard.faculty_main_ui.myprofile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import in.orange.noticeboard.CreateNotice;
import in.orange.noticeboard.FacultyUser;
import in.orange.noticeboard.R;

public class MyprofileFragment extends Fragment {

    private FirebaseUser u;
    private Firebase root;
    private String uid;
    private FacultyUser facultyUser;

    private ImageView iv;
    private TextView name,email,role,department,password;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.myprofile, container, false);
        setHasOptionsMenu(true);
        u= FirebaseAuth.getInstance().getCurrentUser();
        try{
            uid = u.getUid();
            Log.d("uid",uid);
        }catch(Exception e){}

        iv=view.findViewById(R.id.profile_pic);
        name=(TextView)view.findViewById(R.id.profile_name);
        email=(TextView)view.findViewById(R.id.profile_email);
        role=(TextView)view.findViewById(R.id.profile_role);
        department=(TextView)view.findViewById(R.id.profile_department);
        password=(TextView)view.findViewById(R.id.password);

        root=new Firebase("https://apponfire-4b488.firebaseio.com/user");

        root.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(uid)){
                    facultyUser=dataSnapshot.getValue(FacultyUser.class);
                    Picasso.get().load(facultyUser.image).into(iv);
                    name.setText(facultyUser.name);
                    email.setText(facultyUser.email);
                    role.setText(facultyUser.role);
                    department.setText(facultyUser.department);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot,String s){
                if(dataSnapshot.getKey().equals(uid)){
                    facultyUser=dataSnapshot.getValue(FacultyUser.class);
                    Picasso.get().load(facultyUser.image).into(iv);
                    name.setText(facultyUser.name);
                    email.setText(facultyUser.email);
                    role.setText(facultyUser.role);
                    department.setText(facultyUser.department);
                }
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot){}
            @Override public void onChildMoved(DataSnapshot dataSnapshot,String s){}
            @Override public void onCancelled(FirebaseError firebaseError){}
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyprofileFragment.this.getActivity(),ChangePassword.class));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.myprofile_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item= menu.findItem(R.id.menu_item_edit);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MyprofileFragment.this.getContext(),EditProfile.class));
                return false;
            }
        });
    }
}