package com.groupq.sth.vintellig.model.manageLock;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.KeyManagerConnection;

import java.util.ArrayList;

/**
 * Created by sth on 6/16/15.
 */

public class KeyManagerAdaptor extends RecyclerView.Adapter<KeyManagerAdaptor.ViewHolder> {
    private ArrayList<KeyData> itemsData;
    public ShareContext shareContext;


    public KeyManagerAdaptor(ArrayList<KeyData> itemsData, ShareContext shareContext){
        this.shareContext = shareContext;
        this.itemsData = itemsData;
    }

    @Override
    public KeyManagerAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_key, null);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(KeyManagerAdaptor.ViewHolder holder, int position) {
        holder.owenNameView.setText(itemsData.get(position).getOwnerName());
        holder.startTimeView.setText(itemsData.get(position).getStartTime());
        holder.endTimeView.setText(itemsData.get(position).getEndTime());
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView owenNameView, startTimeView, endTimeView;
        public ImageButton deleteButton;

        public ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            owenNameView = (TextView) itemLayoutView.findViewById(R.id.owner_name);
            startTimeView = (TextView) itemLayoutView.findViewById(R.id.key_start);
            endTimeView = (TextView) itemLayoutView.findViewById(R.id.key_end);
            deleteButton = (ImageButton) itemLayoutView.findViewById(R.id.key_delete);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemLayoutView.setVisibility(View.GONE);
                    KeyManagerConnection managerConnection =
                            new KeyManagerConnection(shareContext);
                    managerConnection.execute("delete", owenNameView.getText().toString());
                }
            });

        }
    }
}
