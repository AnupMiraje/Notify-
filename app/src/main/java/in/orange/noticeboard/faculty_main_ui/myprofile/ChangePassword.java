package in.orange.noticeboard.faculty_main_ui.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.orange.noticeboard.FacultyMain;
import in.orange.noticeboard.R;

public class ChangePassword extends Activity{

    private Button save;
    private EditText old,newpassword,repeat;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private AuthCredential credential;
    private Task task,newtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_change_password);

        user= FirebaseAuth.getInstance().getCurrentUser();


        save=(Button)findViewById(R.id.save_password);
        old=(EditText)findViewById(R.id.password_old);
        newpassword=(EditText)findViewById(R.id.password_new);
        repeat=(EditText)findViewById(R.id.password_repeat);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newpassword.getText().toString().equals(repeat.getText().toString())) {
                    credential = EmailAuthProvider.getCredential(user.getEmail(), old.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task tasks) {
                            if (tasks.isSuccessful()) {
                                user.updatePassword(repeat.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePassword.this, "Password change Successful", Toast.LENGTH_SHORT).show();
                                            ChangePassword.super.onBackPressed();
                                        } else {
                                            Toast.makeText(ChangePassword.this, "Password change Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ChangePassword.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ChangePassword.this, "New and Repeat password do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}