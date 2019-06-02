package com.example.ahmednazeer.notes;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ahmednazeer.notes.data.NoteContract;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

public class AddNoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=125;
    private static final int PICK_IMAGE_REQUEST=1;

    private ImageView noteImageView;
    private EditText noteEditText;
    private Uri actualUri=null;
    private ArrayList<String>categories;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        noteImageView =(ImageView)findViewById(R.id.add_PicImageView);
        noteEditText=(EditText)findViewById(R.id.noteContent);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.addImageButton:
                tryToOpenImageSelector();
                break;
            case R.id.saveNoteButton:
                int newItemID=getNoteData();
                int x=0;
                if(newItemID>0){
                    Intent intent=new Intent(AddNoteActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }
    public int  getNoteData(){
        String noteText="";
        String noteImage="";
        if(actualUri!=null){
            noteImage=actualUri.toString();
        }
        if (!noteEditText.getText().toString().equals(null)){
            noteText=noteEditText.getText().toString();
        }
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
        int x=0;

        ContentValues values=new ContentValues();
        values.put(NoteContract.NoteEntry.noteTextColumn,noteText);
        values.put(NoteContract.NoteEntry.noteImageColumn,noteImage);
        values.put(NoteContract.NoteEntry.noteDateColumn,date);
        if (!category.equals("none")){
            values.put(NoteContract.NoteEntry.noteCategoryColumn,category);
        }

        Uri uri=NoteContract.NoteEntry.contentUri;
        Uri resultUri=getBaseContext().getContentResolver().insert(NoteContract.NoteEntry.contentUri,values);
        int id=Integer.parseInt(resultUri.getLastPathSegment());
        return id;
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
    public void tryToOpenCamera() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
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

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                actualUri = resultData.getData();
                noteImageView.setVisibility(View.VISIBLE);
                noteImageView.setImageURI(actualUri);
                noteImageView.invalidate();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category="none";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

    }
}
