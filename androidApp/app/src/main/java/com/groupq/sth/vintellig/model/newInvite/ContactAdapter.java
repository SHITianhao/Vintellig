package com.groupq.sth.vintellig.model.newInvite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import android.content.Context;

import com.groupq.sth.vintellig.R;

/**
 * Created by Tianhao on 6/15/2015.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<ContactInfo> contactList;
    private Context mcontext;

    public ContactAdapter(ArrayList<ContactInfo> contactList,Context context){
        super();
        this.contactList = contactList;
        mcontext = context;
    }

    @Override
    public int getItemCount(){
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder,int i){
        contactViewHolder.vNewInviteText.setText(contactList.get(i).newInviteInfo);
        contactViewHolder.vImageView.setImageResource(contactList.get(i).photoId);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup,int i){
        View itemView;
        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card,viewGroup,false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView vNewInviteText;
        protected ImageView vImageView;

        public ContactViewHolder(View v){
            super(v);
            vNewInviteText = (TextView)v.findViewById(R.id.cardtxt);
            vImageView = (ImageView)v.findViewById(R.id.cardimg);
        }
    }
}
