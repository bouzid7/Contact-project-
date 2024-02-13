package com.firstProject.contactsproject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class UpdateNote extends AppCompatActivity implements View.OnClickListener {

    Note note ;
    TextInputEditText objectNote;
    TextInputEditText noteText;
    Button cancelNote;
    Button updateNote;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_note);

        note= (Note) getIntent().getSerializableExtra("Note");
        objectNote=(TextInputEditText) findViewById(R.id.objectNote);
        noteText=(TextInputEditText) findViewById(R.id.noteText);
        cancelNote=(Button) findViewById(R.id.cancelNote);
        updateNote=(Button) findViewById(R.id.updateNote);

        cancelNote.setOnClickListener(this);
        //Get text from Intent
        Intent intent = getIntent();
        String objectNoteO = intent.getStringExtra("objectNote");
        String contentNoteO = intent.getStringExtra("contentNote");
        String dateNoteO = intent.getStringExtra("dateNote");
        //Set Text
        objectNote.setText(objectNoteO);
        noteText.setText(contentNoteO);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        cancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpdate();
            }
        });

        updateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onN = objectNote.getText().toString();
                String ntN =  noteText.getText().toString();
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
                String dateN = df.format(Calendar.getInstance().getTime());

                Note n=new Note(objectNoteO,contentNoteO,dateNoteO,"no");
                // validating the text fields if empty or not.
                if (TextUtils.isEmpty(onN))
                {
                    objectNote.setError("Please enter object");
                }
                else if (TextUtils.isEmpty(ntN))
                {
                    noteText.setError("Please enter note");
                }

                else
                {
                    // calling a method to update our course.
                    // we are passing our object class, course name,
                    // course description and course duration from our edittext field.
                    updateNoteInDb(n, onN, ntN, dateN);
                }
            }
        });

    }

    private void updateNoteInDb(Note  note, String ob, String nt, String date) {

        Map<String,Object> newNote=new HashMap<>();
        newNote.put("objectNote",ob);
        newNote.put("contentNote",nt);
        newNote.put("dateNote",date);
        newNote.put("isFavorite","no");
        FirebaseUser currentUser = auth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("notebooks").whereEqualTo("objectNote",note.getObjectNote()).whereEqualTo("contentNote",note.getContentNote())
                .whereEqualTo("dateNote",note.getDateNote()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            String documentID=documentSnapshot.getId();
                            db.collection("user").document(currentUser.getEmail()).collection("notebooks").document(documentID)
                                    .update(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(UpdateNote.this,"note has been updated",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateNote.this,"note has been failed to update",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                });

    }

    private void cancelUpdate(){
        objectNote.setText("");
        noteText.setText("");
        Intent intent= new Intent(this, Detail_note.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

    }

}
