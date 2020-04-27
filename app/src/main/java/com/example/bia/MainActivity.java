package com.example.bia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.io.File.createTempFile;


public class MainActivity extends AppCompatActivity {

        ImageView rightHeart;
        private int i=0;
        long startTime;

        private MediaRecorder recorder = null;
        private static String fileName = null;
        private static final String LOG_TAG = "Record_log";
        //Context context= getApplicationContext();
        //File cacheDirectory = this.getCacheDir();
        //File tempFile= File.createTempFile("test1", "3gp", cacheDirectory);
        //File tempFile=Context.getCacheDir().getAbsolutePath();
        String cacheDirectory=null;

    //String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-kkmmss"));
    //File outputFile = new File(context.getCacheDir(), "output-${timestamp}.txt")


        private StorageReference mStorageRef;      //to be used with Firebase storage


        View.OnTouchListener recordVoiceListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                countUserClicks(v, motionEvent);

                return false;
            }
        };

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            rightHeart=findViewById(R.id.imageViewRight);
            Context context= getApplicationContext();
            cacheDirectory= context.getCacheDir().toString();
            //File cacheDirectory = this.getCacheDir();
            //tempFile = File.createTempFile("first", "3gp");

        //tempFile=new File(cacheDirectory, "testTempfile" + "3gp");


        mStorageRef = FirebaseStorage.getInstance().getReference();

            fileName= Environment.getExternalStorageDirectory().getAbsolutePath();
            //fileName = cacheDirectory;
            fileName += "/audiorecordtestTempFileTest.3gp";

            rightHeart.setOnTouchListener(recordVoiceListener);
        }



       void countUserClicks(View v, MotionEvent motionEvent) {

           if (i == 0) { //first time button is clicked
               startTime = System.nanoTime();
               ++i;

           }

           else if(i == 1) {
               if (System.nanoTime() - startTime < 1000000000) {//if less than one second occurs between the two clicks
                   startTime = System.nanoTime();
                   //Toast.makeText(MainActivity.this, "Second click", Toast.LENGTH_SHORT).show();
                   ++i;
               }
               else{
                   i=0;
               }
           }
           else if (i ==2) {  //third time button is clicked
               if (System.nanoTime() - startTime < 1000000000) {
                   //do voice recording and show that voice is recording
                   Toast.makeText(MainActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();
                   startRecording();
                   ++i;
               } //end of if (System.nanoTime() - startTime < 1000000000)
               else{
                   i=0;
               }

           }
           else if(i>2 && i<12){ //keep incrementing until reaches 12, once i=12 stop recording
               ++i;
           }
           else if(i>=12){
               stopRecording();
               Toast.makeText(MainActivity.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
               i=0;
           }


        }





       private void startRecording(){
           recorder = new MediaRecorder();
           recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
           //recorder.setOutputFile(fileName);
           //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           recorder.setOutputFile(cacheDirectory);
           //}
           recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

           try {
               recorder.prepare();
           } catch (IOException e) {
               Log.e(LOG_TAG, "prepare() failed");
           }

           recorder.start();
       }

        private void stopRecording(){
            recorder.stop();
            recorder.release();
            recorder = null;

            uploadAudio();
        }

        private void uploadAudio() {
            Toast.makeText(MainActivity.this, "Uploading started", Toast.LENGTH_SHORT).show();

            StorageReference filepath= mStorageRef.child("Audio").child("new_audio123.3gp");
            Uri uri = Uri.fromFile(new File(fileName));

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                }
            });
        }


}

