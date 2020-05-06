package com.example.bia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class retrieveVoiceRecordingsActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private ListView listView;
    ArrayList<String> arrayList;
    int dog;
    StorageReference item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_voice_recordings);
        getSupportActionBar().hide();

        listView=findViewById(R.id.listView);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference(userUid);

        arrayList= new ArrayList<>();

        //listFiles();


        StorageReference listRef= mStorageRef.child("Audio");


        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Toast.makeText(retrieveVoiceRecordingsActivity.this, "Get Prefixes", Toast.LENGTH_SHORT).show();

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Toast.makeText(retrieveVoiceRecordingsActivity.this, "Audio file retrieval successful", Toast.LENGTH_SHORT).show();

                            //Toast.makeText(retrieveVoiceRecordingsActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
                            //Line above displays file location
                            setArrayList(item);
                            createArrayAdapter();
                        }
                    }
                }) //end of OnSuccess method
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(retrieveVoiceRecordingsActivity.this, "Could not retrieve audio files", Toast.LENGTH_SHORT).show();
                    }
                });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(retrieveVoiceRecordingsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }   //end of OnCreate method

    public void setArrayList(StorageReference item){
        arrayList.add(item.toString());
        dog=23;
    }

    public void createArrayAdapter(){
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

   /* private void listFiles(){
        //StorageReference listRef = storage.getReference().child("files/uid");
        //StorageReference listRef= mStorageRef.child("Audio").child("new_audioTESTUSER.3gp");
        StorageReference listRef= mStorageRef.child("Audio");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Toast.makeText(retrieveVoiceRecordingsActivity.this, "Get Prefixes", Toast.LENGTH_SHORT).show();

                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Toast.makeText(retrieveVoiceRecordingsActivity.this, "Get Items", Toast.LENGTH_SHORT).show();

                            //Toast.makeText(retrieveVoiceRecordingsActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
                            //Line above displays file location
                            arrayList.add(item.toString());

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(retrieveVoiceRecordingsActivity.this, "Could not retrieve audio files", Toast.LENGTH_SHORT).show();
                    }
                });

    }*/
}
