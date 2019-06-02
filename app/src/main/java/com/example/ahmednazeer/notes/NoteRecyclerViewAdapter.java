package com.example.ahmednazeer.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ahmednazeer.notes.data.Note;

import java.util.ArrayList;

/**
 * Created by Ahmed Nazeer on 05/12/2017.
 */

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.NoteViewHolder> {
    private ArrayList<Note>noteArrayList;
    private Context context;
    private NoteClickListener listener;
    private boolean isLong;//=false;
    public static int position;
    private ArrayList<String>clickedItems;


    public interface NoteClickListener{
        public void onNoteClicked(int noteIndex,boolean changeCount,int changeCase);
        public void onLongClick(boolean isChecked,int clickedPosition);
    }

    public NoteRecyclerViewAdapter(Context context,ArrayList<Note>noteArrayList,NoteClickListener listener
            ,boolean isLong/*,int position*/,ArrayList<String>clickedItems){
        this.context=context;
        this.noteArrayList=noteArrayList;
        this.listener=listener;
        this.isLong=isLong;
        //NoteRecyclerViewAdapter.position=position;
        this.clickedItems=clickedItems;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note=noteArrayList.get(position);
        String content=note.getNoteContent();
        String[] result = content.split(System.lineSeparator(), 2);
        String txt=result[0];
        if (txt.length()>50){
            txt=txt.substring(0,27)+"...";
        }
        String date=note.getNoteDate();
        holder.noteContentTV.setText(txt);
        holder.noteDateTV.setText(note.getNoteDate().toString());
        holder.noteCategoryTV.setText(note.getCategory().toString());
        if(isLong==true){
            holder.checkIB.setVisibility(View.VISIBLE);
            if(clickedItems.contains(""+position)){//position==this.position
                holder.checkIB.setImageResource(R.mipmap.ic_checked);
                //listener.onNoteClicked(position,true,1);
                holder.checkIB.setTag(R.string.checked_tag);
            }
        }


        //holder.noteNumberTV.setText(""+(position+1));
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView noteContentTV,noteDateTV,noteCategoryTV;
        ImageButton checkIB;
        int position;
        boolean isFirstClick=true;
        public NoteViewHolder(View itemView) {
            super(itemView);
            noteContentTV=(TextView)itemView.findViewById(R.id.noteContentTV);
            noteDateTV=(TextView)itemView.findViewById(R.id.noteDateTV);
            noteCategoryTV=(TextView)itemView.findViewById(R.id.noteCategoryTV);
            checkIB=(ImageButton) itemView.findViewById(R.id.checkIB);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            position=getAdapterPosition();
            isFirstClick=!clickedItems.contains(""+position);
            if(isLong&&isFirstClick==true){
                isFirstClick=true;
                checkIB.setImageResource(R.mipmap.ic_checked);
                checkIB.setTag(R.string.checked_tag);
                listener.onNoteClicked(position,true,1);
                //position=getAdapterPosition();
                /*
                if (position==NoteRecyclerViewAdapter.position){
                    if(checkIB.getTag().equals(R.string.checked_tag)){
                        isFirstClick=true;
                        checkIB.setImageResource(R.mipmap.ic_un_checked);
                        checkIB.setTag(R.string.un_checked_tag);
                        listener.onNoteClicked(position,true,-1);
                    }else {
                        isFirstClick=true;
                        checkIB.setImageResource(R.mipmap.ic_checked);
                        checkIB.setTag(R.string.checked_tag);
                        listener.onNoteClicked(position,true,1);
                    }
                }else {
                    isFirstClick=false;
                    checkIB.setImageResource(R.mipmap.ic_checked);
                    //checkIB.setVisibility(View.VISIBLE);
                    listener.onNoteClicked(position,true,1);
                }*/
            }
            //***************************************************************************//
            else if(isLong&&isFirstClick==false) {
                checkIB.setImageResource(R.mipmap.ic_un_checked);
                isFirstClick=true;
                position=getAdapterPosition();
                listener.onNoteClicked(position,true,-1);
            } else {
            position=getAdapterPosition();
            listener.onNoteClicked(position,false,2);
            }

        }

        @Override
        public boolean onLongClick(View v) {
            isLong=true;
            position=getAdapterPosition();
            listener.onLongClick(isLong,position);
            return true;
        }
    }


}
