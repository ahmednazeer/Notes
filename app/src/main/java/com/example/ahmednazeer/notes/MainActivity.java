package com.example.ahmednazeer.notes;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ahmednazeer.notes.data.Note;
import com.example.ahmednazeer.notes.data.NoteContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
        ,NoteRecyclerViewAdapter.NoteClickListener{
    private NoteRecyclerViewAdapter adapter;
    private RecyclerView notesRV;
    private ProgressBar loadingPB;
    private TextView noNotesTV,deleteTV;
    private ImageButton deleteIB;
    private static final int loaderID=1000;
    ArrayList<Note>myNotes;

    //===========================================================================//
    public static boolean isLongItemClicked=false;
    public static int numOfClickedItems=0;
    FloatingActionButton fab;
    private int clickedPosition=-1;

    private ArrayList<String>clickedItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            notesRV = (RecyclerView) findViewById(R.id.notesRV);
            loadingPB = (ProgressBar) findViewById(R.id.loadingIndicatorPB);
            noNotesTV = (TextView) findViewById(R.id.emptyNotesTV);
            deleteTV = (TextView) findViewById(R.id.deleteTV);
            deleteIB = (ImageButton) findViewById(R.id.deleteIB);
            myNotes = new ArrayList<>();
            //numOfClickedItems=0;

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                    startActivity(addNoteIntent);
                }
            });
        if (savedInstanceState==null) {
            getSupportLoaderManager().initLoader(loaderID, null, this).forceLoad();
        }else {
            //==================================================================================
            myNotes=savedInstanceState.getParcelableArrayList("notes");
            clickedItems=savedInstanceState.getStringArrayList("clickedItems");
            isLongItemClicked=savedInstanceState.getBoolean("isLong");
            numOfClickedItems=savedInstanceState.getInt("numOfClickedItems");
            clickedPosition=savedInstanceState.getInt("clickedPosition");

            if (!myNotes.isEmpty()){
                showRecyclerView();
                controlAdapter();
                controlDeleteOperation();
            }else {
                showNoNotes();
                }
            }
            /*
            controlAdapter();
            controlDeleteOperation();
            */


    }




    public ArrayList<Note>viewNotes(Cursor cursor){
        int count=cursor.getCount();

        if(count>0){
            for (int i=0;i<count;i++){
                Note note=new Note();
                cursor.moveToPosition(i);
                int noteContentIndex=cursor.getColumnIndex(NoteContract.NoteEntry.noteTextColumn);
                int noteDateIndex=cursor.getColumnIndex(NoteContract.NoteEntry.noteDateColumn);
                int noteIDIndex=cursor.getColumnIndex(NoteContract.NoteEntry.noteIdColumn);
                int noteImageIndex=cursor.getColumnIndex(NoteContract.NoteEntry.noteImageColumn);
                int noteCategoryIndex=cursor.getColumnIndex(NoteContract.NoteEntry.noteCategoryColumn);
                String noteContent=cursor.getString(noteContentIndex);
                String noteDate=cursor.getString(noteDateIndex);
                String noteImage=cursor.getString(noteImageIndex);
                String noteCategory=cursor.getString(noteCategoryIndex);
                int noteId=cursor.getInt(noteIDIndex);
                note.setNoteContent(noteContent);
                note.setNoteDate(noteDate);
                note.setId(noteId);
                note.setNoteImage(noteImage);
                note.setCategory(noteCategory);
                myNotes.add(note);
            }
        }
        return myNotes;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), NoteContract.NoteEntry.contentUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        loadingPB.setVisibility(View.GONE);

        if(viewNotes(cursor).isEmpty()){
            showNoNotes();
            //noNotesTV.setText(getString(R.string.no_notes));
        }else {
            showRecyclerView();
        }
        controlAdapter();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onNoteClicked(int noteIndex,boolean changeCount,int changeCase) {
        if(changeCount&&changeCase==1){
            ++numOfClickedItems;
            clickedItems.add(""+noteIndex);
            invalidateOptionsMenu();
            controlDeleteOperation();
        }else if(changeCount&&changeCase==-1){
            --numOfClickedItems;
            clickedItems.remove(""+noteIndex);
            invalidateOptionsMenu();
            controlDeleteOperation();
        }else {
        //numOfClickedItems=adapter.getNumOfClickedItems();
        Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            Note note=myNotes.get(noteIndex);
            String noteCat=note.getCategory();
            String x="";
        intent.putExtra("note",note);
        startActivity(intent);
        }

    }

    @Override
    public void onLongClick(boolean isChecked,int clickedPosition) {
        if(isChecked){
            this.clickedPosition=clickedPosition;
            ++numOfClickedItems;
            isLongItemClicked=true;
            clickedItems.add(""+clickedPosition);
            invalidateOptionsMenu();
            controlAdapter();
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item=menu.findItem(R.id.action_select_all);
        if(isLongItemClicked==false){
            item.setVisible(false);
            show_hideMenu(menu,true);
        }else {
            show_hideMenu(menu,false);
            if(numOfClickedItems<myNotes.size()&&numOfClickedItems>0){
                item.setVisible(true);
                item.setIcon(R.mipmap.ic_un_select_all);
                item.setTitle("No");
            }else if(numOfClickedItems==myNotes.size()) {
                item.setVisible(true);
                item.setIcon(R.mipmap.ic_select_all);
                item.setTitle("Yes");
            }else if(numOfClickedItems==0){
                isLongItemClicked=false;
                clickedPosition=-1;
                controlAdapter();
            }
            controlDeleteOperation();

        }
        return true;
    }

    public void show_hideMenu(Menu menu,Boolean isShow){
        for (int i=0;i<menu.size();i++){
            MenuItem item=menu.getItem(i);
            if (item.getItemId()!=R.id.action_select_all){
                if (isShow){
                    item.setVisible(true);
                }else {
                    item.setVisible(false);
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_delete_all) {
            int num_of_rows=getContentResolver().delete(NoteContract.NoteEntry.contentUri,"",null);
            if (num_of_rows>0){
                showNoNotes();
            }

        }else if (id==R.id.action_select_all){

            if (item.getTitle().equals("No")){
                item.setIcon(R.mipmap.ic_select_all);
                //item.setTitle("Yes");
                for (int i=0;i<myNotes.size();i++){
                    if (!clickedItems.contains(""+i)){
                        clickedItems.add(""+i);
                    }
                }
                numOfClickedItems=myNotes.size();
                controlAdapter();
                invalidateOptionsMenu();

            }
            else if(item.getTitle().equals("Yes")){
                item.setIcon(R.mipmap.ic_un_select_all);
                //item.setTitle("No");
                //=====================================================
                isLongItemClicked=false;
                clickedPosition=-1;
                numOfClickedItems=0;
                clickedItems.clear();
                invalidateOptionsMenu();
                controlAdapter();
            }
        }

        return true;
    }
    public void controlAdapter(){
        adapter=new NoteRecyclerViewAdapter(getBaseContext(),myNotes,this,isLongItemClicked /*clickedPosition*/,clickedItems);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getBaseContext());
        notesRV.setLayoutManager(layoutManager);
        notesRV.setHasFixedSize(true);
        notesRV.setAdapter(adapter);
        invalidateOptionsMenu();
    }
    public void controlDeleteOperation(){
        if(numOfClickedItems>=1){
            deleteIB.setVisibility(View.VISIBLE);
            deleteTV.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);



        }else {
            deleteIB.setVisibility(View.GONE);
            deleteTV.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }

    }
    public void confirmDelete(Uri uri, final String sele, final String []args){
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setMessage("The selected notes will be deleted.")
                .setTitle("Delete")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getBaseContext().getContentResolver().delete(NoteContract.NoteEntry.contentUri,sele,args);
                        myNotes=new ArrayList<>();
                        getSupportLoaderManager().restartLoader(loaderID, null, MainActivity.this);
                        isLongItemClicked=false;
                        clickedPosition=-1;
                        controlAdapter();
                        numOfClickedItems=0;
                        controlDeleteOperation();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setIcon(R.mipmap.ic_warn);
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public void removeNotes(View view) {
        Uri uri=null;
        String selection="_id=?";
        String selection_args[];
        if (numOfClickedItems==1&&myNotes.size()>1){
            selection_args=new String[]{""+(myNotes.get(clickedPosition).getId())};
            confirmDelete(NoteContract.NoteEntry.contentUri, "_id=?",selection_args);
        }else if(numOfClickedItems>1&&numOfClickedItems<myNotes.size()){
            selection_args= new String[clickedItems.size()];
            String selc="";
            for(int i=0;i<selection_args.length;i++){
                int noteIndex=Integer.parseInt(clickedItems.get(i));
                selection_args[i]=""+myNotes.get(noteIndex).getId();
                if(i<(selection_args.length-1)){
                selc+="_id="+myNotes.get(noteIndex).getId()+" AND ";
                }else {
                    selc+="_id="+myNotes.get(noteIndex).getId();
                }
            }
            //getBaseContext().getContentResolver().delete(NoteContract.NoteEntry.contentUri,selection,selection_args);
            confirmDelete(NoteContract.NoteEntry.contentUri, selection,selection_args);
            //getSupportLoaderManager().restartLoader(loaderID, null, this);
        }else if(numOfClickedItems==myNotes.size()){
            //int x=getBaseContext().getContentResolver().delete(NoteContract.NoteEntry.contentUri,"",null);
            confirmDelete(NoteContract.NoteEntry.contentUri, "",null);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("notes",myNotes);
        outState.putInt("numOfClickedItems",numOfClickedItems);
        //outState.putCharSequenceArrayList("clickedItems", (ArrayList<CharSequence>) clickedItems);
        outState.putStringArrayList("clickedItems",clickedItems);
        outState.putBoolean("isLong",isLongItemClicked);
        outState.putInt("clickedPosition",clickedPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    public void showProgress(){
        loadingPB.setVisibility(View.VISIBLE);
        notesRV.setVisibility(View.GONE);
        noNotesTV.setVisibility(View.GONE);
    }
    public void showNoNotes(){
        loadingPB.setVisibility(View.GONE);
        notesRV.setVisibility(View.GONE);
        noNotesTV.setVisibility(View.VISIBLE);
    }
    public void showRecyclerView(){
        loadingPB.setVisibility(View.GONE);
        notesRV.setVisibility(View.VISIBLE);
        noNotesTV.setVisibility(View.GONE);
    }
}
