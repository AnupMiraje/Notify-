package in.orange.noticeboard.faculty_main_ui.myprofile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import in.orange.noticeboard.FacultyUser;
import in.orange.noticeboard.R;

public class EditProfile extends Activity {

    private FirebaseUser u;
    private Firebase root,rootUser;
    private String uid,fileurl;
    private FacultyUser facultyUser;

    private ImageView iv;
    private TextView name,email;
    private Spinner role,department;
    private Button change,save;

    private Uri filepath;
    private boolean flag=false;

    private StorageReference Folder,filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_edit);
        u= FirebaseAuth.getInstance().getCurrentUser();
        try{
            uid = u.getUid();
            Log.d("uid",uid);
        }catch(Exception e){}

        iv=findViewById(R.id.profile_pic);


        change=findViewById(R.id.change_profile);
        name=(TextView)findViewById(R.id.myprofile_name);
        email=(TextView)findViewById(R.id.myprofile_email);

        role=(Spinner)findViewById(R.id.myprofile_role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditProfile.this, R.array.role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);

        department=(Spinner)findViewById(R.id.myprofile_department);
        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(EditProfile.this, R.array.departments, android.R.layout.simple_spinner_item);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(adap);

        root=new Firebase("https://apponfire-4b488.firebaseio.com/user");

        root.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(uid)){
                    facultyUser=dataSnapshot.getValue(FacultyUser.class);
                    Picasso.get().load(facultyUser.image).into(iv);
                    name.setText(facultyUser.name);
                    email.setText(facultyUser.email);
                    for(int i=0;i<role.getCount();i++)
                    {
                        if(role.getItemAtPosition(i).toString().equals(facultyUser.role))
                        {
                            role.setSelection(i);
                            break;
                        }
                    }
                    for(int i=0;i<department.getCount();i++)
                    {
                        if(department.getItemAtPosition(i).toString().equals(facultyUser.department))
                        {
                            department.setSelection(i);
                            break;
                        }
                    }
                    fileurl=facultyUser.image;
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot,String s){
                if(dataSnapshot.getKey().equals(uid)){
                    facultyUser=dataSnapshot.getValue(FacultyUser.class);
                    Picasso.get().load(facultyUser.image).into(iv);
                    name.setText(facultyUser.name);
                    email.setText(facultyUser.email);
                    for(int i=0;i<role.getCount();i++)
                    {
                        if(role.getItemAtPosition(i).toString().equals(facultyUser.role))
                        {
                            role.setSelection(i);
                            break;
                        }
                    }
                    for(int i=0;i<department.getCount();i++)
                    {
                        if(department.getItemAtPosition(i).toString().equals(facultyUser.department))
                        {
                            department.setSelection(i);
                            break;
                        }
                    }
                }
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot){}
            @Override public void onChildMoved(DataSnapshot dataSnapshot,String s){}
            @Override public void onCancelled(FirebaseError firebaseError){}
        });


        save=(Button)findViewById(R.id.myprofile_edit);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveDetails().execute();
            }
        });

        change=(Button)findViewById(R.id.change_profile);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select an Image"),234);
            }
        });
    }

    class SaveDetails extends AsyncTask{
        private ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(EditProfile.this);
            progress.setMessage("Publishing...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
            EditProfile.super.onBackPressed();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(flag){
                putFile();
            }
            FacultyUser facultyusers=new FacultyUser(facultyUser.name,facultyUser.email,
                    department.getSelectedItem().toString(),fileurl,role.getSelectedItem().toString());
            rootUser=new Firebase("https://apponfire-4b488.firebaseio.com/user/"+uid);
            rootUser.setValue(facultyusers);
            return null;
        }
    }


    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 234 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                iv.setImageBitmap(bitmap);
                flag=true;
            } catch (IOException e) {
                Log.d("notice", e + "");
            }
        }
    }

    private void putFile()
    {
        Folder= FirebaseStorage.getInstance().getReference();
        filename = Folder.child("image" + filepath.getLastPathSegment());
        filename.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fileurl=String.valueOf(uri);
                    }
                });
            }
        });
        Folder.child(facultyUser.image).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}