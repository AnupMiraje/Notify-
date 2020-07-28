package in.orange.noticeboard.faculty_main_ui.logout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import in.orange.noticeboard.FacultyLogin;
import in.orange.noticeboard.R;

public class LogoutFragment extends Fragment {

    private SendViewModel sendViewModel;
    private AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.logout, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        builder=new AlertDialog.Builder(getContext());
        builder.setMessage("Are you Sure want to Signout?");
        builder.setCancelable(false);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), FacultyLogin.class));
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getContext(), FacultyLogin.class));
            }
        });
        AlertDialog alertDialog=builder.create();

        alertDialog.show();
        return root;
    }
}