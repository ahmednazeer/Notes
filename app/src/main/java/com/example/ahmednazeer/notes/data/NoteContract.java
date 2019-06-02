package com.example.ahmednazeer.notes.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahmed Nazeer on 02/12/2017.
 */

public class NoteContract {
    public static final String content_authority ="com.example.ahmednazeer.notes";
    public static final String schema="content://";
    public static final Uri baseUri=Uri.parse(schema+ content_authority);

    public static class NoteEntry implements BaseColumns{

        public static final String path="notes";
        public static final Uri contentUri=baseUri.buildUpon().appendPath(path).build();

        public static final String notesTable="notes";
        public static final String noteTextColumn="text";
        public static final String noteDateColumn="date";
        public static final String noteImageColumn="image";
        public static final String noteCategoryColumn="category";
        public static final String noteIdColumn=BaseColumns._ID;
    }
}
