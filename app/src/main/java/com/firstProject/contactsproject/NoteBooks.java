package com.firstProject.contactsproject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Note;
import com.firstProject.contactsproject.adapters.NotesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class NoteBooks extends AppCompatActivity implements View.OnClickListener  {

    // creating a variable for our list view,
    // arraylist and firebase Firestore.
    ListView listNotes;
    ArrayList<Note> noteModalArrayList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    ImageView add_note;
    ImageView back ;
    ImageView favorites,favorites_false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebooks);

        favorites=(ImageView)findViewById(R.id.favorites);
        favorites_false=(ImageView)findViewById(R.id.favorites_false);
        favorites_false=(ImageView)findViewById(R.id.favorites_false);

        favorites_false.setOnClickListener(this);
        favorites.setOnClickListener(this);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        add_note=(ImageView) findViewById(R.id.add_notebook);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteBooks.this, AddNote.class);
                startActivity(intent);
            }
        });
        // below line is use to initialize our variables
        listNotes = findViewById(R.id.idNotes);
        noteModalArrayList = new ArrayList<>();
        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        // here we are calling a method
        // to load data in our list view.

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataNotesInListview();
    }

   private void loadDataNotesInListview() {
        noteModalArrayList.clear();
       FirebaseUser currentUser = auth.getCurrentUser();
       DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
       docRef.collection("notebooks").get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {

                               Note allNotes = new Note(
                                       document.get("objectNote").toString(),
                                       document.get("contentNote").toString(),
                                       document.get("dateNote").toString(),
                                       document.get("isFavorite").toString()
                               );

                               noteModalArrayList.add(allNotes);
                           }

                           NotesAdapter adapter = new NotesAdapter(NoteBooks.this, noteModalArrayList);
                           // after passing this array list to our adapter
                           // class we are setting our adapter to our list view.
                             listNotes.setAdapter(adapter);
                       }
                       else
                       {
                           Log.d("not ok", "Error getting documents: ", task.getException());
                       }
                   }
               });

   }

         private void getFavorites(){

             Drawable drawable1 = getResources().getDrawable(R.drawable.favorite);
             favorites.setImageDrawable(drawable1);
             favorites_false.setVisibility(View.GONE);
             favorites.setVisibility(View.VISIBLE);

             noteModalArrayList.clear();
             FirebaseUser currentUser = auth.getCurrentUser();
             DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
             docRef.collection("favorites").get()
                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<QuerySnapshot> task) {

                             if (task.isSuccessful()) {
                                 for (QueryDocumentSnapshot document : task.getResult()) {

                                     Note favoritesNotes = new Note(
                                             document.get("objectNote").toString(),
                                             document.get("contentNote").toString(),
                                             document.get("dateNote").toString(),
                                             document.get("isFavorite").toString()
                                     );

                                     noteModalArrayList.add(favoritesNotes);
                                 }
                                 NotesAdapter adapter = new NotesAdapter(NoteBooks.this, noteModalArrayList);

                                 // after passing this array list to our adapter
                                 // class we are setting our adapter to our list view.
                                 listNotes.setAdapter(adapter);
                             }
                             else
                             {
                                 Log.d("not ok", "Error getting documents: ", task.getException());
                             }
                         }
                     });
         }

    @Override
    public void onClick(View v) {

       if(v.getId()==R.id.back){
          Intent intent=new Intent(this,Liste_contacts.class);
          startActivity(intent);
        }
       else if(v.getId()==R.id.favorites_false){
           getFavorites();
       }

    }

}
