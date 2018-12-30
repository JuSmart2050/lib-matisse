package com.flower.android.matisse.sample;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.flower.android.matisse.Matisse;
import com.flower.android.matisse.MimeType;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class SampleActivity extends AppCompatActivity {


    private final int REQUEST_CODE_PHOTO = 1001;
    private final int REQUEST_CODE_ALBUM = 1002;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);


        findViewById(R.id.photoBtn).setOnClickListener(view -> {

            Disposable disposable = rxPermissions.request(Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        Toast.makeText(this, granted + "", Toast.LENGTH_SHORT).show();
                        if (granted) {
                            Matisse.from(this)
                                    .choose(MimeType.ofImage())
                                    .theme(R.style.Matisse_Dracula)
                                    .countable(false)
                                    .maxSelectable(1)
                                    .originalEnable(true)
                                    .maxOriginalSize(10)
                                    .fromCamera(REQUEST_CODE_PHOTO);
                        } else {
                            // At least one permission is denied
                        }
                    });
        });

        findViewById(R.id.albumBtn).setOnClickListener(view -> {
            Disposable disposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        Toast.makeText(this, granted + "", Toast.LENGTH_SHORT).show();
                        if (granted) {
                            Matisse.from(this)
                                    .choose(MimeType.ofImage())
                                    .theme(R.style.Matisse_Dracula)
                                    .countable(false)
                                    .maxSelectable(1)
                                    .originalEnable(true)
                                    .maxOriginalSize(10)
                                    .fromAlbum(REQUEST_CODE_ALBUM);
                        } else {

                        }
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ALBUM) {
                List<String> pathList = Matisse.obtainPathResult(data);
                List<Uri> uriList = Matisse.obtainResult(data);


            } else if (requestCode == REQUEST_CODE_PHOTO) {
                List<String> pathList = Matisse.obtainPathResult(data);
                List<Uri> uriList = Matisse.obtainResult(data);
            }
        }
    }
}