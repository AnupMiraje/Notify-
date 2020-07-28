package in.orange.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfView extends AppCompatActivity {

    private Uri pdffile;
    private PDFView pdfview;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar()!= null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfview=findViewById(R.id.viewpdf);
        String flag=getIntent().getStringExtra("flag");
        if(flag.equals("create")){

            pdfview.fromUri(pdffile).password(null).defaultPage(0).enableSwipe(true).swipeHorizontal(false)
                    .enableDoubletap(true).onDraw(new OnDrawListener() {
                @Override
                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                }
            }).onDrawAll(new OnDrawListener() {
                @Override
                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                }
            }).onPageError(new OnPageErrorListener() {
                @Override
                public void onPageError(int page, Throwable t) {

                }
            }).onPageChange(new OnPageChangeListener() {
                @Override
                public void onPageChanged(int page, int pageCount) {

                }
            }).onTap(new OnTapListener() {
                @Override
                public boolean onTap(MotionEvent e) {
                    return true;
                }
            }).onRender(new OnRenderListener() {
                @Override
                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                    pdfview.fitToWidth();
                }
            }).enableAnnotationRendering(true).invalidPageColor(Color.WHITE).load();
        }
        else if(flag.equals("view")){
            String data=getIntent().getStringExtra("data");
            new RetrievePDF().execute(data);

        }
    }

    class RetrievePDF extends AsyncTask<String,Void,InputStream> {

        private ProgressDialog progress;

        @Override
        protected InputStream doInBackground(String... strings) {
            inputStream=null;
            try{
                URL uRL=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)uRL.openConnection();
                if(urlConnection.getResponseCode()==200){
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());
                    pdfview.fromStream(inputStream).defaultPage(0).enableSwipe(true).swipeHorizontal(false)
                            .enableDoubletap(true).onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        }
                    }).onDrawAll(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        }
                    }).onPageError(new OnPageErrorListener() {
                        @Override
                        public void onPageError(int page, Throwable t) {

                        }
                    }).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                    }).onTap(new OnTapListener() {
                        @Override
                        public boolean onTap(MotionEvent e) {
                            return true;
                        }
                    }).onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                            pdfview.fitToWidth();
                        }
                    }).enableAnnotationRendering(true).invalidPageColor(Color.WHITE).load();
                    try{
                        Thread.sleep(3000);
                    }catch(InterruptedException e){}
                }
            }catch(IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(PdfView.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            progress.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
