package com.samuelvialle.recyclerviewimagesandvideo.adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samuelvialle.recyclerviewimagesandvideo.R;

import java.io.File;
import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> implements View.OnClickListener{

    Context context;
    ArrayList<String> mediaList;

    public MyRecyclerAdapter(Context context, ArrayList<String> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_media, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull MyRecyclerAdapter.MyViewHolder holder, int position) {

//        final String imageString = mediaList.get(position);
        Uri imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-example-c7dcb.appspot.com/o/Medias%2F1624743661357.jpg?alt=media&token=e0283dcd-8b18-4c59-8a70-64f0eccb50c7");
        holder.mediaContainer.setImageURI(imageUri);
//        holder.mediaContainer.setImageURI(Uri.parse(mediaList.get(position)));

    }

    @Override
    public int getItemCount() {
//        return mediaList.size();
        return 1;
    }

    /** Méthode pour la gestion du clic sur un des item **/
    @Override
    public void onClick(View v) {

    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaContainer;
        public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            mediaContainer = itemView.findViewById(R.id.mediaContainer);


        }
    }
}
