package com.example.splitit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class NewSettlement extends AppCompatActivity {
    private static Bitmap Image = null;
    private static Bitmap rotateImage = null;
    private ImageView imageView;
    private static final int GALLERY = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_settlement);

    }

}
