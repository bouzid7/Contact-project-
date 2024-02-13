package com.firstProject.contactsproject;


import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import com.firstProject.contactsproject.Model.Contact;
import com.firstProject.contactsproject.Model.User;
import com.firstProject.contactsproject.adapters.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;



public class Liste_contacts extends AppCompatActivity implements View.OnClickListener {

    RecyclerView contactsRecycler;
    LinkedList<Contact> contacts;
    SearchView searchView;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FloatingActionButton fab_add;
    ImageView button_favoris;
    ImageView button_contacts;
    User data_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_contacts);
        contactsRecycler = (RecyclerView) findViewById(R.id.list_contacts);
        button_favoris = (ImageView) findViewById(R.id.button_favoris);
        button_contacts = (ImageView) findViewById(R.id.button_contacts);
        button_favoris.setOnClickListener(this);
        button_contacts.setOnClickListener(this);
        /* to search in the list contact */
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    private void filterContacts(String Text) {
        LinkedList<Contact> filteredContacts = new LinkedList<Contact>();

        for (Contact contact : contacts) {
            if (
                    contact.getNomContact().toLowerCase().contains(Text.toLowerCase())||
                    contact.getPrenomContact().toLowerCase().contains(Text.toLowerCase())||
                    contact.getTel().toLowerCase().contains(Text.toLowerCase())
            )

            {
                filteredContacts.add(contact);
            }
        }

        if (filteredContacts.isEmpty()) {
            Toast.makeText(this, "NO DATA FOUND", Toast.LENGTH_SHORT).show();
        }
        else {
            MyAdapter myAdapter = new MyAdapter(contacts, Liste_contacts.this);
            myAdapter.setFilteredContacts(filteredContacts);
            contactsRecycler.setAdapter(myAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent myintent = new Intent(this, ajouter_contact.class);
            startActivity(myintent);
        } else if (view.getId() == R.id.button_favoris) {
            getFavoris();
        } else if (view.getId() == R.id.button_contacts) {
            getContacts();
        }

    }

    void getContacts() {
        contacts = new LinkedList<Contact>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("contact").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contact c = new Contact(document.get("nomContact").toString(), document.get("prenomContact").toString(), document.get("serviceContact").toString(), document.get("emailContact").toString(), document.get("img_url").toString(), document.get("tel").toString());
                                contacts.add(c);

                            }

                            // use this setting to improve performance if you know that changes
                            // in content do not change the layout size of the RecyclerView
                            contactsRecycler.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(Liste_contacts.this);
                            contactsRecycler.setLayoutManager(layoutManager);
                            MyAdapter myAdapter = new MyAdapter(contacts, Liste_contacts.this);
                            contactsRecycler.setAdapter(myAdapter);
                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Drawable drawable1 = getResources().getDrawable(R.drawable.ic_contact_true);
        button_contacts.setImageDrawable(drawable1);
        Drawable drawable2 = getResources().getDrawable(R.drawable.ic_favoris);
        button_favoris.setImageDrawable(drawable2);
        fab_add.setVisibility(View.VISIBLE);
    }

    void getFavoris() {

        contacts = new LinkedList<Contact>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("favoris").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contact c = new Contact(document.get("nomContact").toString(), document.get("prenomContact").toString(), document.get("serviceContact").toString(), document.get("emailContact").toString(), document.get("img_url").toString(), document.get("tel").toString());
                               // data_user= new User(document.get("nomContact").toString(), document.get("prenomContact").toString(), document.get("serviceContact").toString(), document.get("prenomContact").toString(), document.get("serviceContact").toString());
                                contacts.add(c);
                            }

                            // use this setting to improve performance if you know that changes
                            // in content do not change the layout size of the RecyclerView
                            contactsRecycler.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(Liste_contacts.this);
                            contactsRecycler.setLayoutManager(layoutManager);
                            MyAdapter myAdapter = new MyAdapter(contacts, Liste_contacts.this);
                            contactsRecycler.setAdapter(myAdapter);
                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Drawable drawable1 = getResources().getDrawable(R.drawable.ic_contact);
        button_contacts.setImageDrawable(drawable1);
        Drawable drawable2 = getResources().getDrawable(R.drawable.ic_action_favori);
        button_favoris.setImageDrawable(drawable2);
        fab_add.setVisibility(View.GONE);
    }

    public void Show_profile(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.profile_menu);
        popup.show();
    }



    void getProfileUser() {

        /* FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
         String currentuid=user.getUid();
         DocumentReference reference;
         FirebaseFirestore firestore=FirebaseFirestore.getInstance();
         reference=firestore.collection("user").document();
         reference.get().
       addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if (task.getResult().exists())
                         {

                            data_user=new User(
                                     task.getResult().getString("First Name"),
                                     task.getResult().getString("Last Name"),
                                     task.getResult().getString("Birth Date"),
                                     task.getResult().getString("User Name"),
                                     task.getResult().getString("Password")
                             );
                             Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                             intent.putExtra("User Name" ,data_user.getUserName());
                             intent.putExtra("First Name",data_user.getFirstName());
                             intent.putExtra("Last Name",data_user.getLastName());
                             intent.putExtra("Birth Date",data_user.getBirthDate());
                             intent.putExtra("Password",data_user.getPassword());
                             startActivity(intent);

                         }
                         else
                         {

                             data_user=new User("bouzid false",
                                     "Abdelfattah",
                                     "2000/01/21",
                                     "bouzid7@gmail.com",
                                     "ZT277359"
                             );
                             Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                             intent.putExtra("User Name" ,user.getEmail());
                             intent.putExtra("First Name",data_user.getFirstName());
                             intent.putExtra("Last Name",data_user.getLastName());
                             intent.putExtra("Birth Date",data_user.getBirthDate());
                             intent.putExtra("Password",data_user.getPassword());
                             startActivity(intent);
                         }

                     }
                 });*/
        db.collection("user")
                .whereEqualTo("User Name", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data_user=new User(
                                        document.getString("First Name"),
                                        document.getString("Last Name"),
                                        document.getString("Birth Date"),
                                        document.getString("User Name"),
                                        document.getString("Password")
                                );
                                Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                                intent.putExtra("User Name" ,data_user.getUserName());
                                intent.putExtra("First Name",data_user.getFirstName());
                                intent.putExtra("Last Name",data_user.getLastName());
                                intent.putExtra("Birth Date",data_user.getBirthDate());
                                intent.putExtra("Password",data_user.getPassword());
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    void signOut(){
        auth.signOut();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    void getNotebook(){
        Intent intent=new Intent(this,NoteBooks.class);
        startActivity(intent);
        finish();
    }
    
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.option_deconexion:
                  signOut();
                  return true;
            case R.id.option_Myprofile:
                 getProfileUser();
                return true;
            case R.id.option_Notebook:
                getNotebook();
                return true;
            default:
                return false;
        }
    }


/*
    void Synchronous(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("contact")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Erreur", "Listen failed.", e);
                            return;
                        }

                        contacts = new LinkedList<Contact>();
                        for (QueryDocumentSnapshot doc : value) {
                            Contact c= new Contact(doc.get("nom").toString(),doc.get("prenom").toString(),doc.get("service").toString(),doc.get("email").toString(),doc.get("url").toString());
                            contacts.add(c);
                        }
                        MyAdapter myAdapter = new MyAdapter(contacts, Liste_contacts.this);

                        contactsRecycler.setAdapter(myAdapter);
                        Log.d("Success", "ok " );
                    }
                });
    }

 */
}