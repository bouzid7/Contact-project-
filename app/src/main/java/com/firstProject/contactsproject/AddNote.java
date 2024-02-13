package com.firstProject.contactsproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddNote extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText objectNote;
    TextInputEditText noteText;
    Button add_note;
    Button cancel_note;
    private FirebaseUser dbu;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        objectNote= (TextInputEditText) findViewById(R.id.objectNote);
        objectNote.setOnClickListener(this);
        noteText= (TextInputEditText) findViewById(R.id.noteText);
        noteText.setOnClickListener(this);
        add_note=(Button) findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        cancel_note=(Button) findViewById(R.id.cancel_note);
        cancel_note.setOnClickListener(this);
        auth= FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    private void AddNewNote(){

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
             String date = df.format(Calendar.getInstance().getTime());
            String object=objectNote.getText().toString();
            String content = noteText.getText().toString();
            Note note=new Note(object,content,date,"no");
            DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
            docRef.collection("notebooks")
                    .add(note)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddNote.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                            Toast.makeText(AddNote.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        Intent intent1= new Intent(this, NoteBooks.class);
        startActivity(intent1);
        finish();

    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.add_note) {
            AddNewNote();
        }

        if(v.getId()==R.id.cancel_note){
            Intent intent= new Intent(this, NoteBooks.class);
            startActivity(intent);
            finish();
        }

    }


}
