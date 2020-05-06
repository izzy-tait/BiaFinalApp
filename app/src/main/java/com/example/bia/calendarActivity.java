package com.example.bia;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.timessquare.CalendarPickerView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.time.*;

public class calendarActivity extends AppCompatActivity {

    ImageView rightHeart, leftHeart, notifyVoiceRecord;
    private int i=0;
    private int j=0;
    long startTime;

    private MediaRecorder recorder = null;
    private static String fileName = null;
    private static final String LOG_TAG = "Record_log";
    private Date periodStart;
    private CalendarView calendarView;
    private Button logPeriodBtn;

  /*  private mySQLiteDBHandler dbHandler;
    private String selectedDate;
    private SQLiteDatabase sqLiteDatabase;  //creates database*/


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

/*    View.OnClickListener logPeriodOnClickListener= new View.OnClickListener(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v){
                Intent updatePeriodArrival = new Intent(calendarActivity.this, MainActivity.class);

                Long estimatedDaysLeftLong = logPeriod();
                estimatedDaysLeft= Long.toString(estimatedDaysLeftLong);

                updatePeriodArrival.putExtra("DAYS_LEFT", estimatedDaysLeft);

                startActivity(updatePeriodArrival);

        }

    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getSupportActionBar().hide();
        rightHeart=findViewById(R.id.imageViewRight);
        leftHeart=findViewById(R.id.imageViewLeft);
        calendarView=findViewById(R.id.calendarView);
        notifyVoiceRecord=findViewById(R.id.notifyVoiceRecord);

        logPeriodBtn=findViewById(R.id.logPeriodBtn);



        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);

        fileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/temporaryFile.3gp";

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
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
        notifyVoiceRecord.setVisibility(View.VISIBLE);   //little black circle will appear next to top right heart when recording
    }

    private void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();
        notifyVoiceRecord.setVisibility(View.GONE); //black dot next to top right heart will disappear when recording has stopped
    }

    private void uploadAudio() {
        Toast.makeText(calendarActivity.this, "Uploading started", Toast.LENGTH_SHORT).show();

        StorageReference filepath= mStorageRef.child("Audio").child(getCurrentTime());
        Uri uri = Uri.fromFile(new File(fileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(calendarActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                deleteLocalFile(fileName);
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

    private String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        //System.out.println(formatter.format(date));

        return formatter.format(date);
    }

    private void deleteLocalFile(String file){
        File deleteFile = new File(file);

        if(deleteFile.delete())
        {
            Toast.makeText(calendarActivity.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //System.out.println("Failed to delete the file");
            Toast.makeText(calendarActivity.this, "File deletion failure", Toast.LENGTH_SHORT).show();

        }
    }



}
