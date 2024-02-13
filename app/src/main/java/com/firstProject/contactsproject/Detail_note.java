package com.firstProject.contactsproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;


public class Detail_note extends AppCompatActivity implements View.OnClickListener {

       Note note;
       TextView object_note;
       TextView content_note;
       TextView date_note;
       ImageView edit_note;
       ImageView delete_note;
       ImageView back;
       ImageView favori_true;
       ImageView favori_false;
       private FirebaseAuth auth;
       private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_note);

        object_note=(TextView)findViewById(R.id.object_note);
        content_note=(TextView)findViewById(R.id.content_note);
        date_note=(TextView)findViewById(R.id.date_note);

        edit_note=(ImageView) findViewById(R.id.edit_note);
        delete_note=(ImageView) findViewById(R.id.delete_note);
        favori_false=(ImageView) findViewById(R.id.favori_false);
        favori_true=(ImageView) findViewById(R.id.favori_true);
        back=(ImageView) findViewById(R.id.back);

        delete_note.setOnClickListener(this);
        favori_false.setOnClickListener(this);
        favori_true.setOnClickListener(this);
        back.setOnClickListener(this);

        object_note.setOnClickListener(this);
        content_note.setOnClickListener(this);
        date_note.setOnClickListener(this);

        note= (Note) getIntent().getSerializableExtra("Note");
        object_note.setText(note.getObjectNote());
        content_note.setText(note.getContentNote());
        date_note.setText(note.getDateNote());

        if (note.getIsFavorite().equals("no")) {
            favori_false.setVisibility(View.VISIBLE);
            favori_true.setVisibility(View.GONE);
        }
        else if (note.getIsFavorite().equals("yes")) {
            favori_false.setVisibility(View.GONE);
            favori_true.setVisibility(View.VISIBLE);
        }

        auth=FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get data from input field
                //Pass data to 2nd activity
                Intent intent = new Intent(Detail_note.this, UpdateNote.class);
                intent.putExtra("objectNote", note.getObjectNote());
                intent.putExtra("contentNote", note.getContentNote());
                intent.putExtra("dateNote", note.getDateNote());
                intent.putExtra("isFavorite", note.getIsFavorite());
                startActivity(intent);

            }
        });
    }

    private void deleteNote() {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Modify the query to retrieve the document reference for the contact that needs to be deleted.
            Task<QuerySnapshot> docRef = db.collection("user").document(currentUser.getEmail())
                    .collection("notebooks")
                    .whereEqualTo("objectNote",note.getObjectNote())
                    .whereEqualTo("contentNote", note.getContentNote())
                    .whereEqualTo("dateNote", note.getDateNote())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Get the reference of the document to be deleted.
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            DocumentReference documentReference = documentSnapshot.getReference();

                            // Delete the document from the favorites collection.
                            documentReference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Detail_note.this, "note removed", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(Detail_note.this, "Failed to remove note", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }

        Intent intent2= new Intent(getApplicationContext(), NoteBooks.class);
        startActivity(intent2);
        finish();
    }





    private void FavoriteFalse(){

        favori_true.setVisibility(View.GONE);
        favori_false.setVisibility(View.VISIBLE);
        // delete note from favorites in cloud
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Modify the query to retrieve the document reference for the contact that needs to be deleted.
            Task<QuerySnapshot> docRef = db.collection("user").document(currentUser.getEmail())
                    .collection("favorites")
                    .whereEqualTo("objectNote", note.getObjectNote())
                    .whereEqualTo("contentNote",note.getContentNote())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Get the reference of the document to be deleted.
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            DocumentReference documentReference = documentSnapshot.getReference();

                            // Delete the document from the favorites collection.
                            documentReference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Detail_note.this, "Contact removed from favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(Detail_note.this, "Failed to remove contact from favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }

        Map<String,Object> NoteFavoritesChange=new HashMap<>();
        NoteFavoritesChange.put("objectNote",note.getObjectNote());
        NoteFavoritesChange.put("contentNote",note.getContentNote());
        NoteFavoritesChange.put("dateNote",note.getDateNote());
        NoteFavoritesChange.put("isFavorite","no");
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("notebooks").whereEqualTo("objectNote",note.getObjectNote()).whereEqualTo("contentNote",note.getContentNote())
                .whereEqualTo("dateNote",note.getDateNote()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            String documentID=documentSnapshot.getId();
                            db.collection("user").document(currentUser.getEmail()).collection("notebooks").document(documentID)
                                    .update(NoteFavoritesChange).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Detail_note.this,"note has been deleted from favorites",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Detail_note.this,"note has been failed to delete it from favorites",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                });

    }


    private  void FavoriteTrue(){

        favori_false.setVisibility(View.GONE);
        favori_true.setVisibility(View.VISIBLE);
        // add note to favorites in cloud

        Note  noteF=new Note (note.getObjectNote(),note.getContentNote(),note.getDateNote(),note.getIsFavorite());
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
            docRef.collection("favorites")
                    .add(noteF)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Detail_note.this, "Successful,note become favorite", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                            Toast.makeText(Detail_note.this, "Failed to be favorite", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



        Map<String,Object> NoteFavoritesChange=new HashMap<>();
        NoteFavoritesChange.put("objectNote",note.getObjectNote());
        NoteFavoritesChange.put("contentNote",note.getContentNote());
        NoteFavoritesChange.put("dateNote",note.getDateNote());
        NoteFavoritesChange.put("isFavorite","yes");
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("notebooks").whereEqualTo("objectNote",note.getObjectNote()).whereEqualTo("contentNote",note.getContentNote())
                .whereEqualTo("dateNote",note.getDateNote()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            String documentID=documentSnapshot.getId();
                            db.collection("user").document(currentUser.getEmail()).collection("notebooks").document(documentID)
                                    .update(NoteFavoritesChange).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Detail_note.this,"note has been added to favorites",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Detail_note.this,"note has been failed to added to favorites",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                });
    }


    @Override
    public void onClick(View v) {

        if  (v.getId()==R.id.back){
            Intent intent1= new Intent(this, NoteBooks.class);
            startActivity(intent1);
            finish();
        }

        else if (v.getId()==R.id.delete_note){
             deleteNote();
        }

        else if (v.getId()==R.id.favori_false){
            FavoriteTrue();
        }

        else if (v.getId()==R.id.favori_true){
            FavoriteFalse();
        }

    }

}
