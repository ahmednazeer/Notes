package com.example.ahmednazeer.notes.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmed Nazeer on 02/12/2017.
 */

public class Note implements Parcelable {
    private String noteContent,noteDate;
    private String noteImage;
    private int id;
    private String category;

    protected Note(Parcel in) {
        noteContent = in.readString();
        noteDate = in.readString();
        noteImage = in.readString();
        id = in.readInt();
        category = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Note(){}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteImage() {
        return noteImage;
    }

    public void setNoteImage(String noteImage) {
        this.noteImage = noteImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noteContent);
        dest.writeString(noteDate);
        dest.writeString(noteImage);
        dest.writeInt(id);
        dest.writeString(category);
    }
}
