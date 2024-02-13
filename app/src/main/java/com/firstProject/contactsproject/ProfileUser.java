package com.firstProject.contactsproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import com.firstProject.contactsproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;


public class ProfileUser extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText email;
    TextInputEditText password1;
    TextInputEditText birthDate;
    FirebaseAuth auth;
    FirebaseFirestore db;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);
        firstName=(TextInputEditText)findViewById(R.id.firstName);
        lastName=(TextInputEditText)findViewById(R.id.lastName);
        email=(TextInputEditText)findViewById(R.id.email);
        password1=(TextInputEditText)findViewById(R.id.password);
        birthDate=(TextInputEditText)findViewById(R.id.birthDate);
        //Get data  from Intent
        Intent intent = getIntent();
        user= new User(
                intent.getStringExtra("First Name"),
                intent.getStringExtra("Last Name"),
                intent.getStringExtra("Birth Date"),
                intent.getStringExtra("User Name"),
                intent.getStringExtra("Password")
        );
        //set  data in layout
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getUserName());
        password1.setText(user.getPassword());
        birthDate.setText(user.getBirthDate());
        //intialisation of firebase
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    public void logOut(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.logout);
        popup.show();
    }
    void signOut(){
        auth.signOut();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    void back(){
        Intent intent=new Intent(this,Liste_contacts.class);
        startActivity(intent);
        finish();
    }
    void updateDataUser(){

        String NewFirstname = firstName.getText().toString();
        String NewLastname = lastName.getText().toString();
        String NewUsername = email.getText().toString();
        String Newpassword= password1.getText().toString();
        String NewBirthdate =  birthDate.getText().toString();
       // User  updateUser=new User(Firstname,Lastname,birthdate,Username,password0);
        Map<String,Object> updateUser=new HashMap<>();
        updateUser.put("First Name",NewFirstname);
        updateUser.put("Last Name",NewLastname);
        updateUser.put("User Name",NewUsername);
        updateUser.put("Password",Newpassword);
        updateUser.put("Birth Date",NewBirthdate);
        FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        currentUser.updateEmail(NewUsername).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "email updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "email failed to  update it", Toast.LENGTH_SHORT).show();
                }
            });

            currentUser.updatePassword(Newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "password updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "password failed to  update it", Toast.LENGTH_SHORT).show();
                }
            });


            db.collection("user")
                    .whereEqualTo("User Name", user.getUserName()).whereEqualTo("First Name", user.getFirstName())
                    .whereEqualTo("Last Name", user.getLastName())
                    .whereEqualTo("Password", user.getPassword())
                    .whereEqualTo("Birth Date", user.getBirthDate())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                db.collection("user").document(documentID)
                                        .update(updateUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "user has been updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "user has been failed to update", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });

        signOut();
    }
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.option_deconexion:
                signOut();
                return true;
            case  R.id.option_save:
                   updateDataUser();
                return true;
            case  R.id.option_back:
                    back();
                return true;

            default:
                return false;
        }
    }


    @Override
    public void onClick(View v) {

    }


}
