package com.example.ahmednazeer.notes;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.ahmednazeer.notes.data.Note;
import com.example.ahmednazeer.notes.data.NoteContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView noteDateTV,noteContentTV,noteCategoryTV;
    private ImageView noteIV;
    private EditText detailContentET;
    private ViewSwitcher viewSwitcher;
    private Spinner spinner;
    private String noteContent="";
    private String category="";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=125;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri actualUri=null;
    private boolean isEdit=false;
    private ArrayList<String>categories;
    private Note note;
    //==========================================================
    public static final int REQUEST_CODE = 1;
    private FloatingActionButton removeImageFAB;
    private boolean fabIsClicked=false;
    //==========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        noteDateTV=(TextView)findViewById(R.id.detailDateTV);
        noteContentTV=(TextView)findViewById(R.id.detailContentTV);
        noteCategoryTV=(TextView)findViewById(R.id.detailCategoryTV);
        noteIV=(ImageView)findViewById(R.id.detailIV);
        detailContentET=(EditText)findViewById(R.id.detailContentET);
        viewSwitcher=(ViewSwitcher)findViewById(R.id.noteContentVS);

        removeImageFAB=(FloatingActionButton)findViewById(R.id.deleteImageFAB);

        spinner = (Spinner) findViewById(R.id.catsSpinner);


        note=getIntent().getParcelableExtra("note");
        String s=note.getNoteContent();
        String cat=note.getCategory();
        noteIV.setImageURI(Uri.parse(note.getNoteImage()));
        noteDateTV.setText(note.getNoteDate());
        noteContentTV.setText(""+note.getNoteContent());
        noteCategoryTV.setText(note.getCategory());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void prepareSpinner(){
        spinner.setOnItemSelectedListener(this);
        categories=new ArrayList<>();
        categories.add("Automobile");
        categories.add("Business Services");
        categories.add("Computers");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }



    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_details,menu);
        if(isEdit){

            menu.findItem(R.id.action_edit).setIcon(R.mipmap.ic_done);
            if (note.getNoteImage().isEmpty()||note.getNoteImage().equals(null)){
                menu.findItem(R.id.action_edit_image).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
            }else {
            removeImageFAB.setVisibility(View.VISIBLE);
            removeImageFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabIsClicked=true;
                    noteIV.setVisibility(View.GONE);
                    removeImageFAB.setVisibility(View.GONE);
                    menu.findItem(R.id.action_edit_image).setVisible(true);
                }
            });
                menu.findItem(R.id.action_delete).setVisible(false);
            }

        }else {
            menu.findItem(R.id.action_edit_image).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_edit).setIcon(R.mipmap.ic_edit);
            removeImageFAB.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_edit:
                if (viewSwitcher.getCurrentView() != detailContentET){
                    noteContent=noteContentTV.getText().toString();
                    viewSwitcher.showNext();
                    detailContentET.setText(noteContent);
                    //item.setIcon(R.mipmap.ic_done);
                    isEdit=true;
                    spinner.setVisibility(View.VISIBLE);
                    prepareSpinner();
                } else if (viewSwitcher.getCurrentView() == detailContentET){
                    noteContent=detailContentET.getText().toString();
                    viewSwitcher.showPrevious();
                    noteContentTV.setText(noteContent);
                    //item.setIcon(R.mipmap.ic_edit);
                    isEdit=false;
                    spinner.setVisibility(View.GONE);
                    getNoteData();

                }
                invalidateOptionsMenu();
                break;
            case R.id.action_edit_image:
                tryToOpenImageSelector();
                break;
            case R.id.action_delete:
                String selection="_id=?";
                String selection_args[]=new String[]{""+note.getId()};
                getBaseContext().getContentResolver().delete(NoteContract.NoteEntry.contentUri,selection,selection_args);
                onBackPressed();
            //invalidateOptionsMenu();
        }
            /*
        if (id==R.id.home){
            Intent i = new Intent(DetailsActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else if(id==R.id.action_edit){
            if (viewSwitcher.getCurrentView() != detailContentET){
                noteContent=noteContentTV.getText().toString();
                viewSwitcher.showNext();
                detailContentET.setText(noteContent);
                //item.setIcon(R.mipmap.ic_done);
                isEdit=true;
                spinner.setVisibility(View.VISIBLE);
                prepareSpinner();

                //invalidateOptionsMenu();
            } else if (viewSwitcher.getCurrentView() == detailContentET){
                noteContent=detailContentET.getText().toString();
                viewSwitcher.showPrevious();
                noteContentTV.setText(noteContent);
                //item.setIcon(R.mipmap.ic_edit);
                isEdit=false;
                spinner.setVisibility(View.GONE);
                getNoteData();

            }
            invalidateOptionsMenu();
        }else if (id==R.id.action_edit_image){
            tryToOpenImageSelector();

        }*/
        return true;
    }


    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            if (resultData != null) {
                actualUri = resultData.getData();
                noteIV.setVisibility(View.VISIBLE);
                noteIV.setImageURI(actualUri);
                noteIV.invalidate();
            }
        }
    }

    public void   getNoteData(){
        String noteText="";
        String noteImage="";
        if(actualUri!=null){
            noteImage=actualUri.toString();
        }else if(fabIsClicked) {
            noteImage="";
        }else {
            noteImage=note.getNoteImage();
        }
        if (!detailContentET.getText().toString().equals(null)){
            noteText=detailContentET.getText().toString();
        }
        /*
        int hours=Integer.parseInt(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
        int minutes=Integer.parseInt(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy ").format(Calendar.getInstance().getTime());
        String date="";
        if(hours>=12){
            hours-=12;
            date=timeStamp+" "+hours+":"+minutes+" PM";
        }else{
            date=timeStamp+" "+hours+":"+minutes+" AM";
        }
        */


        ContentValues values=new ContentValues();
        values.put(NoteContract.NoteEntry.noteTextColumn,noteText);
        values.put(NoteContract.NoteEntry.noteImageColumn,noteImage);
        if (!category.equals("none")){
            values.put(NoteContract.NoteEntry.noteCategoryColumn,category);
        }

        Uri uri=NoteContract.NoteEntry.contentUri;
        String sele="_id=?";
        String select_args[]=new String []{""+note.getId()};
        int n=getBaseContext().getContentResolver().update(NoteContract.NoteEntry.contentUri,values,sele,select_args);
        if (n>0){
            Toast.makeText(getBaseContext(),"Note Updated Successfully",Toast.LENGTH_SHORT).show();
            getContentResolver().notifyChange(NoteContract.NoteEntry.contentUri,null);
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category=categories.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailsActivity.this,
                MainActivity.class);
        startActivityForResult(intent , REQUEST_CODE);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEdit",isEdit);
    }
}
