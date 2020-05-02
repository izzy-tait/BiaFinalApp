package com.example.bia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class calendarActivity extends AppCompatActivity {

    ImageView rightHeart, leftHeart;
    private int i=0;
    private int j=0;
    long startTime;

    private MediaRecorder recorder = null;
    private static String fileName = null;
    private static final String LOG_TAG = "Record_log";

    private StorageReference mStorageRef;      //to be used with Firebase storage


    View.OnTouchListener recordVoiceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            countUserClicks();


            return false;
        }
    };


    View.OnTouchListener displayAudioFiles = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            //displayAudioUI();
            UserClicksLeftHeart();


            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getSupportActionBar().hide();
        rightHeart=findViewById(R.id.imageViewRight);
        leftHeart=findViewById(R.id.imageViewLeft);


        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);

        fileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/audiorecordtestTempFileTest.3gp";

        rightHeart.setOnTouchListener(recordVoiceListener);
        leftHeart.setOnTouchListener(displayAudioFiles);
    }

    void countUserClicks() {

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
                Toast.makeText(calendarActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(calendarActivity.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
            i=0;
        }


    }





    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //recorder.setOutputFile(cacheDirectory);
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
        Toast.makeText(calendarActivity.this, "Uploading started", Toast.LENGTH_SHORT).show();

        StorageReference filepath= mStorageRef.child("Audio").child("new_audio123.3gp");
        Uri uri = Uri.fromFile(new File(fileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(calendarActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UserClicksLeftHeart(){
        if (i == 0) { //first time button is clicked
            startTime = System.nanoTime();
            ++i;

        }

        else if(i < 10) {
            if (System.nanoTime() - startTime < 1000000000) {//if less than one second occurs between the two clicks
                startTime = System.nanoTime();
                //Toast.makeText(MainActivity.this, "Second click", Toast.LENGTH_SHORT).show();
                ++i;
            }
            else{
                i=0;
            }
        }

        else if(i>=10){
            //stopRecording();
            //Toast.makeText(calendarActivity.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
            displayAudioUI();
            i=0;
        }

    }

    private void displayAudioUI(){
        Intent audioFiles= new Intent(this, retrieveVoiceRecordingsActivity.class);
        startActivity(audioFiles);
    }


}
