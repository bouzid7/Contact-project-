package com.firstProject.contactsproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firstProject.contactsproject.Model.Contact;
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
import java.util.HashMap;
import java.util.Map;


public class ReceiveDataToUpdate extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText firstname_up,lastname_up,email_up,tel_up,service_up;
    Button update_contact;
    Button cancel;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);
        //Hooks
        firstname_up =findViewById(R.id.firstname_up);
        lastname_up = findViewById(R.id.lastname_up);
        email_up= findViewById(R.id.email_up);
        tel_up= findViewById(R.id.tel_up);
        service_up= findViewById(R.id.service_up);

        update_contact=(Button)findViewById(R.id.update_contact);
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpdate();
            }
        });
        //Get text from Intent
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        String firstname = intent.getStringExtra("firstname");
        String lastname = intent.getStringExtra("lastname");
        String email = intent.getStringExtra("email");
        String tel = intent.getStringExtra("tel");
        String service = intent.getStringExtra("service");

        //Set Text
        firstname_up.setHint(firstname);
        lastname_up.setHint(lastname);
        email_up.setHint(email);
        tel_up.setHint(tel);
        service_up.setHint(service);

        //intanciation of db
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        //update  contactd
        update_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname1 = firstname_up.getText().toString();
                String lastname1 =  lastname_up.getText().toString();
                String service1 =  service_up.getText().toString();
                String email1 =  email_up.getText().toString();
                String tel1 =  tel_up.getText().toString();
                Contact contact=new Contact(firstname,lastname, service, email,image,tel);
                // validating the text fields if empty or not.
                if (TextUtils.isEmpty( firstname1))
                {
                    firstname_up.setError("Please enter firstname");
                }
                else if (TextUtils.isEmpty(lastname1))
                {
                    lastname_up.setError("Please enter latsname");
                }
                else if (TextUtils.isEmpty(service1))
                {
                     service_up.setError("Please enter sevice");
                }
                else if (TextUtils.isEmpty(email1))
                {
                    email_up.setError("Please enter email");
                }
                else if (TextUtils.isEmpty(tel1))
                {
                    tel_up.setError("Please enter Course Duration");
                }

                else
                {
                    // calling a method to update our course.
                    // we are passing our object class, course name,
                    // course description and course duration from our edittext field.
                    updateContact(contact, firstname1, lastname1, service1,email1,tel1,image);
                }
            }
        });

    }


    private void updateContact(Contact contact, String firstname, String lastname, String service,String email,String tel,String image) {

        Map<String,Object> newContact=new HashMap<>();
        newContact.put("nomContact",firstname);
        newContact.put("prenomContact",lastname);
        newContact.put("serviceContact",service);
        newContact.put("emailContact",email);
        newContact.put("tel",tel);


        FirebaseUser currentUser = auth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("contact").whereEqualTo("nomContact",contact.getNomContact()).whereEqualTo("prenomContact",contact.getPrenomContact())
                        .whereEqualTo("emailContact",contact.getEmailContact()).whereEqualTo("serviceContact",contact.getServiceContact())
                        .whereEqualTo("tel",contact.getTel()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            String documentID=documentSnapshot.getId();
                            db.collection("user").document(currentUser.getEmail()).collection("contact").document(documentID)
                                    .update(newContact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                                 Toast.makeText(ReceiveDataToUpdate.this,"contact has been updated",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ReceiveDataToUpdate.this,"contact has been failed to update",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    }
                });

    }

    private void cancelUpdate(){
        firstname_up.setHint("");
        lastname_up.setHint("");
        service_up.setHint("");
        email_up.setHint("");
        tel_up.setHint("");
        Intent myintent1= new Intent(this, Detail_contact.class);
        startActivity(myintent1);
        finish();
    }

    @Override
    public void onClick(View v) {}


}

