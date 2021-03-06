package com.technofreak.bdtextbooks.Classes;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.technofreak.bdtextbooks.ForumActivity;
import com.technofreak.bdtextbooks.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Class6 extends AppCompatActivity {

    private static String [] file_url = {
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEOUlUZnJScFgtdUU",
            "https://drive.google.com/uc?export=download&id=0B0EXISX1gyucWkdTSTQ5Q2lYM2c",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEZnkwbU16WWM4M00",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEcXZuYkJXVmpvZjA",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEcmF0Vm1YZjJFdXc",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EETXhYSHpBSVA0cmM",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EENFlFUlJidU1mQXc",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EELW9PUm1NN09IM2c" };

    private int fileNo;
    static final String appDir = Environment.getExternalStorageDirectory().toString() + "/Android/data/BDtextbook";
    private LinearLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class6);
        setTitle("Class 6 Books");
        progress = findViewById(R.id.pbar);

        if (!isNetworkAvailable(getApplicationContext())){
            Toast.makeText(this,"Please connect to internet for first time download",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    private class DownloadFileFromURL extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(progress_bar_type);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // getting file length
                int lenghtOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                //InputStream input = new BufferedInputStream(url.openStream(), 8192);
                InputStream input = connection.getInputStream();
                // Output stream to write file
                OutputStream output = null;
                if (fileNo==0) {
                    output = new FileOutputStream(appDir + "/ban1_6.pdf");
                } else if (fileNo==1){
                    output = new FileOutputStream(appDir + "/mat_6.pdf");
                }else if (fileNo==2){
                    output = new FileOutputStream(appDir + "/eng_6.pdf");
                }else if (fileNo==3){
                    output = new FileOutputStream(appDir + "/isl_6.pdf");
                }else if (fileNo==4){
                    output = new FileOutputStream(appDir + "/hin_6.pdf");
                }else if (fileNo==5){
                    output = new FileOutputStream(appDir + "/sci_6.pdf");
                }else if (fileNo==6){
                    output = new FileOutputStream(appDir + "/ban2_6.pdf");
                }else if (fileNo==7){
                    output = new FileOutputStream(appDir + "/info_6.pdf");
                }

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            //dismissDialog(progress_bar_type);
            progress.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Downloaded Successfully",Toast.LENGTH_LONG).show();
        }
    }

    private void open(File f){
        Uri uri = Uri.fromFile(f);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.setDataAndType(uri,"application/pdf");
        Intent intent = Intent.createChooser(target, "Open File");
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Please,install any pdf app",Toast.LENGTH_SHORT).show();
        }
    }

    private void del(String s){
        File file = new File(appDir + s);
        if (file.exists()){
            if(file.delete()){
                Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void forum(View view) {
        String tag=view.getTag().toString();
        Intent intent = new Intent(this, ForumActivity.class);
        intent.putExtra("BOOKNAME",tag);
        startActivity(intent);

    }


    public void ban1(View view) {
        File f = new File(appDir + "/ban1_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=0;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_ban1(View view) {
        del("/ban1_6.pdf");
    }

    public void mat(View view) {
        File f = new File(appDir + "/mat_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=1;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_mat(View view) {
        del("/mat_6.pdf");
    }

    public void eng(View view) {
        File f = new File(appDir + "/eng_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=2;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_eng(View view) {
        del("/eng_6.pdf");
    }

    public void islam(View view) {
        File f = new File(appDir + "/isl_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=3;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_isl(View view) {
        del("/isl_6.pdf");
    }

    public void hindu(View view) {
        File f = new File(appDir + "/hin_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=4;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_hin(View view) {
        del("/hin_6.pdf");
    }

    public void sci(View view) {
        File f = new File(appDir + "/sci_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=5;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_sci(View view) {
        del("/sci_6.pdf");
    }

    public void ban2(View view) {
        File f = new File(appDir + "/ban2_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=6;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_ban2(View view) {
        del("/ban2_6.pdf");
    }

    public void info(View view) {
        File f = new File(appDir + "/info_6.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=7;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_info(View view) {
        del("/info_6.pdf");
    }

}
