package com.firstProject.contactsproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Contact;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

public class ajouter_contact extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layout_newContact;
    TextInputEditText firstname_newContact;
    TextInputEditText lastname_newContact;
    TextInputEditText email_newContact;
    TextInputEditText service_newContact;
    TextInputEditText phoneNumber_newContact;
    Button add_newContact;
    Button cancel_newContact;
    private FirebaseAuth uAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_contact);
        //initialisation des views
        layout_newContact=(LinearLayout)findViewById(R.id.layout_newContact);
        firstname_newContact= (TextInputEditText) findViewById(R.id.firstname_newContact);
        lastname_newContact= (TextInputEditText) findViewById(R.id.lastname_newContact);
        email_newContact= (TextInputEditText) findViewById(R.id.email_newContact);
        service_newContact= (TextInputEditText) findViewById(R.id.service_newContact);
        phoneNumber_newContact= (TextInputEditText) findViewById(R.id.phoneNumber_newContact);
        //-----------------------------------------------------------
        add_newContact=(Button) findViewById(R.id.add_newContact);
        cancel_newContact=(Button) findViewById(R.id. cancel_newContact);
        //mettre les boutons en ecoute
        add_newContact.setOnClickListener(this);
        cancel_newContact.setOnClickListener(this);
        //initialisation de Firebase Authentication
         uAuth = FirebaseAuth.getInstance();
         db=FirebaseFirestore.getInstance();

    }


    private  void AddNewContact(){

        FirebaseUser currentUser = uAuth.getCurrentUser();
        if (currentUser != null) {

            String Firstname = firstname_newContact.getText().toString();
            String Lastname = lastname_newContact.getText().toString();
            String Email = email_newContact.getText().toString();
            String Service = service_newContact.getText().toString();
            String PhoneNumber = phoneNumber_newContact.getText().toString();
            Contact contact=new Contact(Firstname,Lastname, Service, Email,"gs://contacts-898f1.appspot.com/user.png",PhoneNumber);
            DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
            docRef.collection("contact")
                    .add(contact)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ajouter_contact.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                            Toast.makeText(ajouter_contact.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        Intent myintent1= new Intent(this, Liste_contacts.class);
        startActivity(myintent1);
        finish();
    }


    private void clearEditText() {
        firstname_newContact.setText("");
        lastname_newContact.setText("");
        email_newContact.setText("");
        service_newContact.setText("");
        phoneNumber_newContact.setText("");

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.add_newContact) {
            AddNewContact();
        }
        if(v.getId()==R.id.cancel_newContact){
            clearEditText();
            Intent myintent1= new Intent(this, Liste_contacts.class);
            startActivity(myintent1);
            finish();
        }
    }


}