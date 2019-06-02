package com.example.ahmednazeer.notes.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Ahmed Nazeer on 02/12/2017.
 */

public class NoteProvider extends ContentProvider {
    private NoteDBHelper mNoteDBHelper;
    private UriMatcher mUriMatcher=getMatcher();
    private static final int NOTES=100;
    private static final int NOTES_ID=101;

    public UriMatcher getMatcher (){
        UriMatcher myMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        myMatcher.addURI(NoteContract.content_authority,NoteContract.NoteEntry.path,NOTES);
        myMatcher.addURI(NoteContract.content_authority,NoteContract.NoteEntry.path+"/#",NOTES_ID);
        return myMatcher;
    }

    @Override
    public boolean onCreate() {
        mNoteDBHelper =new NoteDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match=mUriMatcher.match(uri);
        Cursor cursor=null;
        switch (match){
            case NOTES:
                cursor=getAllNotes(uri,sortOrder);
                break;
            case NOTES_ID:
                cursor=getNoteDetails(uri,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Error While getting AllNotes");
        }
        return cursor;
    }
    public Cursor getNoteDetails(Uri uri,String sort_order){
        SQLiteDatabase database= mNoteDBHelper.getReadableDatabase();
        String selection="_id=?";
        String selec_args[]=new String[]{uri.getLastPathSegment()};
        Cursor cursor=database.query(NoteContract.NoteEntry.notesTable,null,selection,selec_args,null,null,null);
        return cursor;
    }
    public Cursor getAllNotes(Uri uri,String order_by){
        SQLiteDatabase database= mNoteDBHelper.getReadableDatabase();
        Cursor cursor=database.query(NoteContract.NoteEntry.notesTable,null,null,null,null,null,order_by);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database= mNoteDBHelper.getWritableDatabase();
        String noteText=values.getAsString(NoteContract.NoteEntry.noteTextColumn);
        String noteImage=values.getAsString(NoteContract.NoteEntry.noteImageColumn);
        long newItemID=-1;
        if((noteText.isEmpty()&&!noteImage.isEmpty())       ||
                (!noteText.isEmpty()&&noteImage.isEmpty())  ||
                (!noteText.isEmpty()&&!noteImage.isEmpty())
                ){
            newItemID=database.insert(NoteContract.NoteEntry.notesTable,null,values);
            if(newItemID>0){
                uri=Uri.withAppendedPath(uri,""+newItemID);
            }else{
                Toast.makeText(getContext(),"Error With Saving Note",Toast.LENGTH_SHORT).show();
                newItemID=0;
                uri=Uri.withAppendedPath(uri,""+newItemID);
            }
        }else {
            uri=Uri.withAppendedPath(uri,""+newItemID);
        }
        Uri uri1=uri;

        return uri;
    }





    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match=mUriMatcher.match(uri);
        int numOfDeletedRows=0;
        switch (match){
            case NOTES:
                SQLiteDatabase deleteAllNotesDB=mNoteDBHelper.getWritableDatabase();
                if(selection.isEmpty()){
                    numOfDeletedRows =ProviderUtils.deleteAllNotes(deleteAllNotesDB,uri);
                }
                else {
                    numOfDeletedRows =ProviderUtils.deleteSingleNote(deleteAllNotesDB,selection,selectionArgs);
                }
                //getContext().getContentResolver().notifyChange(uri,null);
                break;

            default:
                throw  new IllegalArgumentException("Invalid Uri");
        }

        return numOfDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database=mNoteDBHelper.getWritableDatabase();
        int n=database.update(NoteContract.NoteEntry.notesTable,values,selection,selectionArgs);
        return n;
    }
}
