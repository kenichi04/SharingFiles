package com.example.sharingfiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSelectActivity extends AppCompatActivity {

    // ファイル選択の結果を返すためのIntent
    private Intent mResultIntent =
            new Intent("com.example.sharingfiles.ACTION_RETURN_FILE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        // Load file
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.image);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setImageURI(uri);

        // Create file
        // 共有するためのファイルを内部ストレージに作成
        File imagePath = new File(getFilesDir(), "images");
        if (!imagePath.isDirectory()) {
            imagePath.mkdir();
        }
        File newFile = new File(imagePath, "image.jpg");
        FileOutputStream outputStream;
        try {
            // いったん画像ファイルを読み込み、内部ストレージに書き出し
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            outputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sharing Setting
        // ファルを共有するためのURIを設定
        Uri fileUri = FileProvider.getUriForFile(
                FileSelectActivity.this,
                "com.example.sharingfiles.fileprovider",
                newFile
        );

        // Intentに一時的にファイルにアクセスするためのパーミッション付与
        mResultIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // コンテンツURI設定
        mResultIntent.setDataAndType(
                fileUri,
                getContentResolver().getType(fileUri));
        setResult(Activity.RESULT_OK, mResultIntent);
    }

    public void select(View view){
        finish();
    }
}