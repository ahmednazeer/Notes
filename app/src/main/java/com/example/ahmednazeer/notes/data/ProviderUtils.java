package com.example.ahmednazeer.notes.data;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.ahmednazeer.notes.data.NoteContract;

/**
 * Created by Ahmed Nazeer on 11/12/2017.
 */

public class ProviderUtils {

    public static int deleteSingleNote(SQLiteDatabase database, String selection,String selec_args[]){
        //String selection="_id=?";
        //String noteId=uri.getLastPathSegment();
        //String selec_args[]=new String []{noteId};
        int total_num=0;
        for (int i=0;i<selec_args.length;i++){
            String []args=new String[]{selec_args[i]};
            int x=database.delete(NoteContract.NoteEntry.notesTable,selection,args);
            total_num+=x;
        }
        return total_num;
    }
    public static int deleteAllNotes(SQLiteDatabase database, Uri uri){
        int numOfDeletedRows=database.delete(NoteContract.NoteEntry.notesTable,null,null);
        int z=0;
        return numOfDeletedRows;
    }

}
