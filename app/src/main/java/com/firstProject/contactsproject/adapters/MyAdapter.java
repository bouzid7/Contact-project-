package com.firstProject.contactsproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firstProject.contactsproject.Detail_contact;
import com.firstProject.contactsproject.Model.Contact;
import com.firstProject.contactsproject.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LinkedList<Contact> contacts;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<Contact> contacts, Context context) {
        this.contacts = new LinkedList<Contact>() ;
        this.contacts.addAll( contacts );
        this.context=context;
    }

      //   for search in list contact
    public  void setFilteredContacts(LinkedList<Contact> filteredContacts){
          this.contacts=filteredContacts;
          notifyDataSetChanged();
    }

     // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         // create a new view
        View itemLayoutView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout,
                        parent, false);
        MyViewHolder vh = new MyViewHolder(itemLayoutView );
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contact=contacts.get(position);

// - get element from your dataset at this position
// - replace the contents of the view with that element
        holder.identification.setText(contacts.get(position).getNomContact()+" "+contacts.get(position).getPrenomContact());
        holder.identification_phone.setText(contacts.get(position).getTel());
// Reference to an image file in Cloud Storage
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(contacts.get(position).getImg_url());
// Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
        Glide.with(context /* context */)
                .load(storageReference)
                .into(holder.photo);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return contacts.size();
    }
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public  class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        Contact contact;
        public TextView identification_phone;
        public TextView identification;
        public ImageView photo;
        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            identification =itemLayoutView.findViewById(R.id.identification);
            photo= itemLayoutView.findViewById(R.id.contact_photo);
            identification_phone=itemLayoutView.findViewById(R.id.identification_phone);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Intent myintent= new Intent(context, Detail_contact.class);
            myintent.putExtra("contact", contact);
            context.startActivity(myintent);

        }

    }

}