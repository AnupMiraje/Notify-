package in.orange.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class FacultyLogin extends Activity {

    private EditText etu,etp;
    private Button b;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.faculty_login);

        etu=(EditText)findViewById(R.id.faculty_login_user);
        etp=(EditText)findViewById(R.id.faculty_login_password);
        b=(Button)findViewById(R.id.faculty_login_button);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser u=mAuth.getCurrentUser();
                uid="";
                try{
                    uid=u.getUid();
                    if(firebaseAuth.getCurrentUser()!=null){
                        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("Current_USERID",uid);
                        editor.apply();
                        //updateToken(FirebaseInstanceId.getInstance().getToken());
                        Intent intent=new Intent(FacultyLogin.this,FacultyMain.class);
                        startActivity(intent);
                    }
                }
                catch(Exception e){}

            }
        };

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setBackgroundColor(Color.GRAY);
                startSignIn();
            }
        });

    }

    /*public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");

        Token mToken=new Token(token);
        ref.child(uid).setValue(mToken);
    }*/

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(FacultyLogin.this,ChooseActivity.class));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void startSignIn()
    {
        String user=etu.getText().toString();
        String pass=etp.getText().toString();
        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
            b.setBackgroundColor(Color.BLUE);
            Toast.makeText(FacultyLogin.this,"Fields are Empty",Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        b.setBackgroundColor(Color.BLUE);
                        Toast.makeText(FacultyLogin.this, "Inavlid email or password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}