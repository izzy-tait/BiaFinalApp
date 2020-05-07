package com.example.bia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

        ImageView rightHeart;
        ImageButton calendarIcon;
        ImageView notifyVoiceRecord;
        Button sendMood;
        Button sendSymptom;
        private int i=0;
        long startTime;

        private MediaRecorder recorder = null;
        private static String fileName = null;
        private static final String LOG_TAG = "Record_log";

    Spinner symptoms_spinner;
    Spinner mood_spinner;



    private StorageReference mStorageRef;      //to be used with Firebase storage


        View.OnTouchListener recordVoiceListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                countUserClicks();


                return false;
            }
        };

    View.OnClickListener calendarPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayCalendarPage();
        }
    };


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getSupportActionBar().hide();
            rightHeart=findViewById(R.id.imageViewRight);
            calendarIcon=findViewById(R.id.calendarIcon);
            notifyVoiceRecord=findViewById(R.id.notifyVoiceRecord);
            sendMood=findViewById(R.id.logMood);
            sendSymptom=findViewById(R.id.logSymptoms);


        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);

            fileName= Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/temporaryFile.3gp";

            rightHeart.setOnTouchListener(recordVoiceListener);
            calendarIcon.setOnClickListener(calendarPage);



        Spinner symptoms_spinner = (Spinner) findViewById(R.id.symptoms_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> symptom_adapter = ArrayAdapter.createFromResource(this,
                R.array.symptoms_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        symptom_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        symptoms_spinner.setAdapter(symptom_adapter);


        Spinner mood_spinner = (Spinner) findViewById(R.id.mood_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> mood_adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mood_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mood_spinner.setAdapter(mood_adapter);



        }



       void countUserClicks() {

           if (i == 0) { //first time button is clicked
               startTime = System.nanoTime();
               ++i;

           }

           else if(i == 1) {
               if (System.nanoTime() - startTime < 1000000000) {//if less than one second occurs between the two clicks
                   startTime = System.nanoTime();
                   ++i;
               }
               else{
                   i=0;
               }
           }
           else if (i ==2) {  //third time button is clicked
               if (System.nanoTime() - startTime < 1000000000) {
                   //do voice recording and show that voice is recording
                   //Toast.makeText(MainActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();
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
            notifyVoiceRecord.setVisibility(View.GONE);  //black dot next to top right heart will disappear when recording has stopped

        }

        private void uploadAudio() {
            Toast.makeText(MainActivity.this, "Uploading started", Toast.LENGTH_SHORT).show();

            StorageReference filepath= mStorageRef.child("Audio").child(getCurrentTime()); //File named with current date and time
            Uri uri = Uri.fromFile(new File(fileName));

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                    deleteLocalFile(fileName);
                }

            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        private void displayCalendarPage(){
            Intent calendarUI = new Intent(this, calendarActivity.class);
            startActivity(calendarUI);
        }

        private String getCurrentTime(){
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date date = new Date();


            return formatter.format(date);
        }

        private void deleteLocalFile(String file){
            File deleteFile = new File(file);

            if(deleteFile.delete())
            {
                Toast.makeText(MainActivity.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "File deletion failure", Toast.LENGTH_SHORT).show();
            }
        }


}

