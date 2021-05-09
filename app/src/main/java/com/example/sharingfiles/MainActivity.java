package com.example.sharingfiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private Intent mRequestFileIntent;
    private ParcelFileDescriptor mInputPFD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void request(View view) {
        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("image/jpg");
        startActivityForResult(mRequestFileIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);

        Uri returnUri = returnIntent.getData();
        ImageView imageView = (ImageView) findViewById(R.id.requested_image_view);
        imageView.setImageURI(returnUri);

        /*
         * Try to open the file for "read" access using the
         * returned URI. If the file isn't found, write to the
         * erro log and return.
         */
        try {
            /*
             * Get the content resolver instance for this context, and use it
             * to get a ParcelFileDescriptor for the file.
             */
            mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("MainActivity", "File not found.");
            return;
        }

        // Get a regular file descriptor for the file
        FileDescriptor fd = mInputPFD.getFileDescriptor();

        String mimeType = getContentResolver().getType(returnUri);
        Log.d("MainActivity", "mineType: " + mimeType);

        Cursor returnCursor =
                getContentResolver().query(returnUri, null, null, null, null);
        returnCursor.moveToFirst();
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        Log.d("MainActivity", "name: " + returnCursor.getString(nameIndex));
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        Log.d("MainActivity", "size: " + Long.toString(returnCursor.getLong(sizeIndex)));
    }
}