package com.firstProject.contactsproject;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firstProject.contactsproject.Model.Contact;
import com.firstProject.contactsproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;


public class Detail_contact extends AppCompatActivity implements View.OnClickListener  {
    Contact contact;
    TextView identification;
    TextView service;
    ImageView photo;
    ImageView favori_true;
    ImageView favori_false;
    ImageView edit_contact;
    ImageView WhatsApp;
    ImageView call;
    ImageView sms;
    ImageView gmail;
    ImageView share_contact_public;
    ImageView back;
    TextView email;
    TextView numero;
    User user;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contact);
        contact= (Contact) getIntent().getSerializableExtra("contact");
        this.context=context;
        identification=(TextView) findViewById(R.id.identification_contact);
        service=(TextView) findViewById(R.id.service_contact);
        email=(TextView) findViewById(R.id.email_contact);
        numero=(TextView) findViewById(R.id.numero_contact);

        photo=(ImageView) findViewById(R.id.photo_contact);

        edit_contact=(ImageView)findViewById(R.id.edit_contact);

        favori_true=(ImageView)findViewById(R.id.favori_true);
        favori_false=(ImageView)findViewById(R.id.favori_false);

        call=(ImageView) findViewById(R.id.call);
        sms=(ImageView) findViewById(R.id.sms);
        gmail=(ImageView) findViewById(R.id.gmail);
        WhatsApp=(ImageView)findViewById(R.id.WhatsApp);
        share_contact_public=(ImageView) findViewById(R.id.share_contact_public);
        back=(ImageView)findViewById(R.id.back);

        gmail.setOnClickListener(this);
        call.setOnClickListener(this);
        sms.setOnClickListener(this);
        WhatsApp.setOnClickListener(this);
        favori_false.setOnClickListener(this);
        favori_true.setOnClickListener(this);
        share_contact_public.setOnClickListener(this);
        back.setOnClickListener(this);


        identification.setText(contact.getNomContact()+" "+ contact.getPrenomContact());
        email.setText(contact.getEmailContact());
        numero.setText(contact.getTel());
        service.setText(contact.getServiceContact());

         db=FirebaseFirestore.getInstance();
         auth=FirebaseAuth.getInstance();

        // Reference to an image file in Cloud Storage
         StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(contact.getImg_url());
        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        Glide.with(this /* context */)
                .load(storageReference)
                .into(photo);



        edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get data from input field
                //Pass data to 2nd activity
                Intent intent = new Intent(Detail_contact.this, ReceiveDataToUpdate.class);
                intent.putExtra("firstname", contact.getNomContact());
                intent.putExtra("lastname", contact.getPrenomContact());
                intent.putExtra("email", contact.getEmailContact());
                intent.putExtra("tel", contact.getTel());
                intent.putExtra("service", contact.getServiceContact());
                intent.putExtra("image", contact.getImg_url());
                startActivity(intent);
            }
        });



    }



    private void shareContactToPublic(){

        Contact contact1=new Contact(contact.getNomContact(),contact.getPrenomContact(),contact.getServiceContact(), contact.getServiceContact(),contact.getImg_url(),contact.getTel());
        db.collection("contacts")
                .add(contact1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Detail_contact.this, "Successful,contact become public", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Toast.makeText(Detail_contact.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void manageFavori_false(){
        favori_true.setVisibility(View.GONE);
        favori_false.setVisibility(View.VISIBLE);

        //  delete contact from favoris in cloud

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Modify the query to retrieve the document reference for the contact that needs to be deleted.
            Task<QuerySnapshot> docRef = db.collection("user").document(currentUser.getEmail())
                    .collection("favoris")
                    .whereEqualTo("nomContact", contact.getNomContact())
                    .whereEqualTo("prenomContact", contact.getPrenomContact())
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
                                            Toast.makeText(Detail_contact.this, "Contact removed from favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(Detail_contact.this, "Failed to remove contact from favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }

    }

    private  void manageFavori_true(){
        favori_false.setVisibility(View.GONE);
        favori_true.setVisibility(View.VISIBLE);

        // add contact to favoris in cloud
        Contact contact1=new Contact(contact.getNomContact(),contact.getPrenomContact(),contact.getServiceContact(), contact.getServiceContact(),contact.getImg_url(),contact.getTel());
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
            docRef.collection("favoris")
                    .add(contact1)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Detail_contact.this, "Successful,contact become favorite", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                            Toast.makeText(Detail_contact.this, "Failed to be favorite", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    private void deleteContact(){

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Modify the query to retrieve the document reference for the contact that needs to be deleted.
            Task<QuerySnapshot> docRef = db.collection("user").document(currentUser.getEmail())
                    .collection("contact")
                    .whereEqualTo("nomContact", contact.getNomContact())
                    .whereEqualTo("prenomContact", contact.getPrenomContact())
                    .whereEqualTo("emailContact",contact.getEmailContact())
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
                                            Toast.makeText(Detail_contact.this, "Contact removed", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(Detail_contact.this, "Failed to remove contact", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }
        Intent myintent1= new Intent(getApplicationContext(), Liste_contacts.class);
        startActivity(myintent1);
        finish();
    }
   private void getProfileUser(){
        db.collection("user")
                .whereEqualTo("User Name", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user=new User(
                                        document.getString("First Name"),
                                        document.getString("Last Name"),
                                        document.getString("Birth Date"),
                                        document.getString("User Name"),
                                        document.getString("Password")
                                );
                                Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                                intent.putExtra("User Name" ,user.getUserName());
                                intent.putExtra("First Name",user.getFirstName());
                                intent.putExtra("Last Name",user.getLastName());
                                intent.putExtra("Birth Date",user.getBirthDate());
                                intent.putExtra("Password",user.getPassword());
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.overflow_menu);
        popup.show();
    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_profile:
                getProfileUser();
                return true;
            case R.id.option_delete:
                deleteContact();
                Toast.makeText(this, "contact deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }


  /*  private boolean isWhatappInstalled(){

        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;

        try {

            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;


        }catch (PackageManager.NameNotFoundException e){

            whatsappInstalled = false;

        }

        return whatsappInstalled;

    }*/


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.call)
        {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + contact.getTel()));//change the number
            startActivity(callIntent);
        }
        else if(view.getId()==R.id.sms){
             // Create the intent with the ACTION_SENDTO action and the "smsto:" URI scheme
             // Intent intent0 = new Intent(Intent.ACTION_SENDTO);
             // intent0.setData(Uri.parse("sms to:" + contact.getTel()));
              Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",  contact.getTel(), null));
              intent.putExtra("sms_body","hello"+" "+contact.getNomContact());
              startActivity(intent);
        }

        else if(view.getId()==R.id.gmail){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,contact.getEmailContact());
            intent.putExtra(Intent.EXTRA_TEXT,"hello "+contact.getNomContact());
            startActivity(intent);
        }

        else if(view.getId()==R.id.WhatsApp){
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+contact.getTel()+
                        "hello"+contact.getNomContact()));
                startActivity(i);
        }

        else if(view.getId()==R.id.share_contact_public){
            shareContactToPublic();
        }

        else if(view.getId()==R.id.favori_false){
            manageFavori_true();
        }
        else if(view.getId()==R.id.favori_true){
            manageFavori_false();
        }
        else if (view.getId()==R.id.back){
            Intent myintent1= new Intent(this, Liste_contacts.class);
            startActivity(myintent1);
            finish();
        }
    }
}

