package com.example.google_lens_application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchRVModal> searchRVModals;


    public SearchRVAdapter(Context context, ArrayList<SearchRVModal> searchRVModals) {
        this.context = context;
        this.searchRVModals = searchRVModals;
    }

    @NonNull
    @Override
    public SearchRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRVAdapter.ViewHolder holder, int position) {
    SearchRVModal rvModal=searchRVModals.get(position);

    holder.title.setText(rvModal.getTitle());
    holder.description.setText(rvModal.getSnippet());
    holder.snippet.setText(rvModal.getLink());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(rvModal.getLink()));
            context.startActivity(i);
        }
    });

    }

    @Override
    public int getItemCount() {
        return searchRVModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,snippet,description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.titleTv);
            snippet=itemView.findViewById(R.id.snippetTv);
            description=itemView.findViewById(R.id.descriptionTv);
        }
    }
}
