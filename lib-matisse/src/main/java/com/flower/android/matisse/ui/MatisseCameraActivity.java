package com.flower.android.matisse.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static com.flower.android.matisse.ui.MatisseActivity.EXTRA_RESULT_SELECTION;
import static com.flower.android.matisse.ui.MatisseActivity.EXTRA_RESULT_SELECTION_PATH;

/**
 * 相机获取图片
 */
public class MatisseCameraActivity extends AppCompatActivity {

    private static final String TAG = "MatisseCameraActivity";

    private File imageFile;
    private boolean crop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crop = getIntent().getBooleanExtra("crop", false);
        toCamera();
    }

    private void toCamera() {
        Activity activity = this;
        if (activity == null) {
            return;
        }
        imageFile = new File(activity.getCacheDir(), "migu_"+System.currentTimeMillis()+".jpg");
        Uri outputUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outputUri = FileProvider.getUriForFile(activity,
                    "com.flower.android.matisse.fileprovider", imageFile);
        } else {
            outputUri = Uri.fromFile(imageFile);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(takePictureIntent, 10000);
        }
    }

    private void toCameraAndCrop() {
        Activity activity = this;
        if (activity == null) {
            return;
        }
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(activity,
                    activity.getApplicationInfo().packageName + ".fileprovider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        if (getPackageManager().queryIntentActivities(intent, 0).size() <= 0) {
            return ;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", false);
        // no face detection
        intent.putExtra("noFaceDetection", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && 10000 == requestCode) {
            if (crop) {
                crop = false;
                toCameraAndCrop();
            }else {
                Log.i(TAG, "choose camera: " + imageFile.getAbsolutePath());
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                selectedUris.add(Uri.fromFile(imageFile));
                selectedPaths.add(imageFile.getAbsolutePath());
                if (data == null) {
                    data = new Intent();
                }
                data.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
                data.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                setResult(RESULT_OK, data);
                finish();
            }
        }else {
            finish();
        }
    }
}
