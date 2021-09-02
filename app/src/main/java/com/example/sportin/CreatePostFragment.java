package com.example.sportin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kotlinx.coroutines.GlobalScope;

import static android.app.Activity.RESULT_OK;


@SuppressWarnings("deprecation")
public class CreatePostFragment extends Fragment {
    private Context context;
    private static final int STORAGE_PERMISSION_CODE = 211;
    private String TAG = "CreatePostFragment";
    private static final int PICK_IMAGE_REQUEST = 21;
    private static final int CLICK_FROM_CAMERA = 32;
    private ImageView openImageFileChooser, postImage, openCameraView, profileImage;
    private EditText postText;
    private Button post;
    private String Document_img1 = null;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openImageFileChooser = view.findViewById(R.id.attachImage);
        openCameraView = view.findViewById(R.id.openCamera);
        profileImage = view.findViewById(R.id.profileImage);
        postText = view.findViewById(R.id.postText);
        post = view.findViewById(R.id.post);
        postImage = view.findViewById(R.id.postImage);

        context = getContext();

        openImageFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                showFileChooser();
            }
        });

        openCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraFun();
            }
        });
    }


    public void openCameraFun() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CLICK_FROM_CAMERA);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();

//            Thread th= new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "uri path" + selectedImage);
//                    String[] filePath = {MediaStore.Images.Media.DATA};
//                    Cursor c = getContext().getContentResolver().query(selectedImage, filePath, null, null, null);
//                    c.moveToFirst();
//                    int columnIndex = c.getColumnIndex(filePath[0]);
//                    String picturePath = c.getString(columnIndex);
//                    c.close();
//                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                    thumbnail = getResizedBitmap(thumbnail, 400);
//                    Log.w(TAG, picturePath + "");
//                    postImage.setImageBitmap(thumbnail);
//                    BitMapToString(thumbnail);
//                }
//            });
//
//            th.start();

//            final Bitmap[] bt = new Bitmap[1];
//            Thread th = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
//                        bt[0] = bitmap;
//                        Log.d(TAG, bitmap.toString());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            th.start();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap redBitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
            postImage.setImageBitmap(redBitmap);
//            postImage.setImageURI(selectedImage);
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);

        }


        if (requestCode == CLICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            postImage.setImageBitmap(thumbnail);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            FileOutputStream fo;
            try {
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 211) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public String BitMapToString(Bitmap userImage1) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
//        byte[] b = baos.toByteArray();
//        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
//        return Document_img1;
//    }
//
//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = 0, height = 0;
//        width = image.getWidth();
//        height = image.getHeight();
//
//        float bitmapRatio = (float) width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }
}