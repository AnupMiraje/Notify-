package in.orange.noticeboard;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static android.os.Environment.getExternalStoragePublicDirectory;

public class CreateNotice extends AppCompatActivity {

    private LinearLayout bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private EditText subject,summary;
    private Button date,attachment,capturephoto,choosephoto,choosepdf;
    private Spinner category;
    private ImageView photo;

    private DatePickerDialog datePickerDialog;
    private Calendar cal1,cal2;
    private int year,month,day;
    private Date d;

    private static final int PICK_IMAGE_REQUEST=234;
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private Uri filepath,image;
    private StorageReference filename,Folder;
    private File fileuri;
    private String pathtofile;

    private String sub,sum,cat,fileurl;
    private Date rel_date,end_date;
    private int id,temp;
    private boolean file;

    private FirebaseDatabase database;
    private DatabaseReference myref;

    private Notice t;

    //private APIService apiService;
    boolean notify=false;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_notice);

        FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
        uid="";
        try {
            uid = u.getUid();
        }catch(Exception e){}


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT>=23)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
        bottomSheetLayout = CreateNotice.this.findViewById(R.id.create_notice_bottomsheet);
        bottomSheetBehavior= BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        d=NoticeBoard.getDate();
        cat=new String();

        assert getSupportActionBar()!= null;
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.create_notice_discard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subject=(EditText)findViewById(R.id.create_notice_subject);
        summary=(EditText)findViewById(R.id.create_notice_summary);
        date=(Button)findViewById(R.id.datepick);
        attachment=(Button)findViewById(R.id.create_notice_attachment);
        category=(Spinner)findViewById(R.id.create_notice_choose_category);

        photo=(ImageView)findViewById(R.id.create_notice_uploadimage);

        //apiService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        setDateTimeField();
        setSpinner();
        getAttachment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_notice_menu, menu);
        return true;
    }

    private void setDateTimeField() {
        cal1= Calendar.getInstance();
        d=NoticeBoard.getDate();
        cal1.setTime(d);
        end_date=cal1.getTime();
        year=cal1.get(Calendar.YEAR);
        month=cal1.get(Calendar.MONTH);
        day=cal1.get(Calendar.DAY_OF_MONTH);
        date.setTextColor(Color.RED);
        date.setText(day+"-"+(month+1)+"-"+year);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(CreateNotice.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
                        cal2=Calendar.getInstance();
                        cal2.set(year,monthOfYear,dayOfMonth);
                        end_date=cal2.getTime();
                    }
                }, year, month,day);
                datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.show();
            }
        });
    }

    private void setSpinner()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat=category.getSelectedItem().toString();
                getChildCount();
            }
            @Override public void onNothingSelected(AdapterView<?> parent){}
        });
    }

    private void getAttachment()
    {
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                photo.setVisibility(View.INVISIBLE);
                getbutton();
            }
        });
    }

    private void getbutton()
    {
        capturephoto=(Button)findViewById(R.id.takephoto);
        choosephoto=(Button)findViewById(R.id.attachphoto);
        choosepdf=(Button)findViewById(R.id.attachpdf);

        capturephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showCameraPermission();
            }
        });

        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooserImage();
            }
        });
        choosepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePDF();
            }
        });
    }

    private void choosePDF()
    {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    private void showCameraPermission()
    {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            takepicture();
        }
    }

    public void takepicture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            fileuri=null;
            fileuri=createPhotoFile();
            if(fileuri!=null)
            {
                pathtofile=fileuri.getAbsolutePath();
                image= FileProvider.getUriForFile(CreateNotice.this,"in.orange.noticeboard.fileprovider",fileuri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,image);
                startActivityForResult(intent,1);
            }
        }
    }

    public File createPhotoFile()
    {
        File storageDir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File jpg=null;
        try{
            jpg=File.createTempFile("123456789",".jpg",storageDir);
        }catch(IOException e){}
        return jpg;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showFileChooserImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filepath=data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                photo.setImageBitmap(bitmap);
                file=true;
                photo.setVisibility(View.VISIBLE);
                image=data.getData();
            }catch(IOException e){Log.d("notice",e+"");}
        }else if( requestCode==1 && resultCode==RESULT_OK)
        {
            Bitmap bitmap= BitmapFactory.decodeFile(pathtofile);
            photo.setImageBitmap(bitmap);
            file=true;
            photo.setVisibility(View.VISIBLE);
        }else if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            image=data.getData();
            photo.setImageDrawable(getResources().getDrawable(R.drawable.pdfviewer));
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent=new Intent(CreateNotice.this,PdfView.class);
                    intent.setData(image);
                    intent.putExtra("flag","create");
                    startActivity(intent);
                }
            });
            file=false;
            photo.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    public void getChildCount() {
        try{
            temp=Integer.parseInt(NoticeDatabase.getNoticeDatabaseInstance(this).getCreateNotice(cat).get(0).id);
        }catch(Exception e){}
    }

    class Publishing extends AsyncTask
    {
        private ProgressDialog progress;

        private void publish()
        {
            id=temp;
            cat=category.getSelectedItem().toString();
            rel_date=d;
            if(!subject.getText().toString().matches("") && !summary.getText().toString().matches("") &&
                    !cat.matches("Choose Category") && rel_date!=null && end_date!=null && image!=null) {
                putFile();
            }
            else{
                Toast.makeText(CreateNotice.this, "Fields are Empty", Toast.LENGTH_LONG).show();
            }
        }

        private void putFile()
        {
            Folder= FirebaseStorage.getInstance().getReference().child("ImageFolder").child(cat);
            filename = Folder.child("image" + image.getLastPathSegment());
            filename.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fileurl=String.valueOf(uri);
                            publishNotice();
                        }
                    });
                }
            });
        }

        private void publishNotice()
        {
            sub=subject.getText().toString();
            sum=summary.getText().toString();
            cat=category.getSelectedItem().toString();
            rel_date=d;
            database=FirebaseDatabase.getInstance();
            t=new Notice(sub,sum,rel_date.getTime()+"",end_date.getTime()+"",file+"",fileurl);
            database=FirebaseDatabase.getInstance();
            id++;
            myref=database.getReference("Notice/"+cat+"/"+id);
            myref.setValue(t);
            notify=true;
            if(notify){
                //senNotification(sub+":"+sum);
            }
            notify=false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(CreateNotice.this);
            progress.setMessage("Publishing...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
            CreateNotice.this.onBackPressed();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            publish();
            return null;
        }
    }

    /*private void senNotification(final String s) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo("UQGS0KwqO5cKYsgosZmyjWyzw2Y2");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data("UQGS0KwqO5cKYsgosZmyjWyzw2Y2",s,"New Notice",uid,R.drawable.pdfviewer);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            Toast.makeText(CreateNotice.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.publish:
                Publishing p=new Publishing();
                p.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
