package com.example.ahmednazeer.notes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ahmednazeer.notes.data.NoteContract.NoteEntry;

/**
 * Created by Ahmed Nazeer on 02/12/2017.
 */

public class NoteDBHelper extends SQLiteOpenHelper {
    public static final String dbName="Note.db";
    public static final int dbVersion=1;

    public NoteDBHelper(Context context){
        super(context,dbName,null,dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ NoteContract.NoteEntry.notesTable+" ( "
                + NoteEntry.noteIdColumn  +" INTEGER PRIMARY KEY AUTOINCREMENT , "//INTEGER AUTO INCREMENT PRIMARY KEY
                +NoteEntry.noteTextColumn +" TEXT , "
                +NoteEntry.noteImageColumn+" TEXT , "
                +NoteEntry.noteCategoryColumn+" TEXT default 'Un Categorized' , "
                +NoteEntry.noteDateColumn+" TEXT NOT NULL );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
