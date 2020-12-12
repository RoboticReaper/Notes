package com.hoversfw.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.RecyclerviewHolder> {
    private ArrayList<RecyclerviewItem> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new RecyclerviewHolder(v,mListener);
    }

    public ArrayList<RecyclerviewItem> getlist(){
        return list;
    }

    public void setList(ArrayList<RecyclerviewItem> mlist){
        list=mlist;
        notifyDataSetChanged();
    }

    public RecyclerviewAdapter(ArrayList<RecyclerviewItem> mlist){
        list=mlist;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        RecyclerviewItem item=list.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
    }

    public void removeByTitle(String title){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getTitle().equals(title)){
                list.remove(list.get(i));
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }
        else{
            return list.size();
        }
    }

    public void add(String title,String description){
        if(list==null){
            list=new ArrayList<>();
        }
        list.add(new RecyclerviewItem(title,description));
        notifyDataSetChanged();
    }

    public int size(){
        return list.size();
    }

    public void remove(){
        if(list.size()!=0) {
            list.remove(list.size() - 1);
            notifyDataSetChanged();
        }
    }

    public static class RecyclerviewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView description;
        public RecyclerviewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            description=itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }
}
