package com.technofreak.bdtextbooks.Classes;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

public class Class1 extends AppCompatActivity {

    private static String [] file_url = {
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEb1hRam9kd3JyWWs",
            "https://drive.google.com/uc?export=download&id=0B8L6VJQZe3EEVjFuT0xIRjQ1bnM" };
    private int fileNo;
    static final String appDir = Environment.getExternalStorageDirectory().toString() + "/Android/data/BDtextbook";
    private LinearLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class1);
        setTitle("Class 1 Books");
        progress = findViewById(R.id.pbar);

        if (!isNetworkAvailable(getApplicationContext())){
            Toast.makeText(this,"Please connect to internet for first time download",Toast.LENGTH_LONG).show();
        }

    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void ban(View view) {
        File f = new File(appDir + "/ban_1.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=0;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_ban(View view) {
        del("/ban_1.pdf");
    }

    public void eng(View view) {
        File f = new File(appDir + "/eng_1.pdf");
        if (f.exists()){
            open(f);
        }else {
            fileNo=1;
            new DownloadFileFromURL().execute(file_url[fileNo]);
        }
    }

    public void del_eng(View view) {
        del("/eng_1.pdf");
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
                    output = new FileOutputStream(appDir + "/ban_1.pdf");
                } else if (fileNo==1){
                    output = new FileOutputStream(appDir + "/eng_1.pdf");
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

}
