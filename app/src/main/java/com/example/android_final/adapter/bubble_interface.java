package com.example.android_final.adapter;


import androidx.recyclerview.widget.RecyclerView;

interface ItemClickListener {

    void onItemClicked(RecyclerView.ViewHolder vh, Object item, int pos);
}

interface GenericItemClickListener<T, VH extends RecyclerView.ViewHolder> {

    void onItemClicked(VH vh, T item, int pos);
}