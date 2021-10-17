package com.example.sportin;

import android.Manifest;
import android.content.ContentValues;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportin.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import kotlinx.coroutines.GlobalScope;

import static android.app.Activity.RESULT_OK;


@SuppressWarnings("deprecation")
public class CreatePostFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;
    private static final int STORAGE_PERMISSION_CODE = 211;
    private String TAG = "CreatePostFragment";
    private static final int PICK_IMAGE_REQUEST = 21;
    private static final int CLICK_FROM_CAMERA = 32;
    private ImageView openImageFileChooser, postImage, openCameraView, profileImage;
    private EditText postText;
    private Button post;
    private TextView uName;
    private ProgressBar progressBar;
    private Uri postImageUri=null;
    private String caption, userName, userId;
    private Bitmap redBitmap;
    private String Document_img1 = null;

    //test
    private Bitmap bitmap;
    //test

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
        uName = view.findViewById(R.id.user_name);
        postImage = view.findViewById(R.id.postImage);
        progressBar = view.findViewById(R.id.uploadPostProgress);
        context = getContext();

        progressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        db.collection("Users")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                        if (!(value == null)) {
                            userName = value.getString("userName");
                            uName.setText(userName);
                            Log.d(TAG + "userName", userName);
                        }
                    }
                });



//        if (caption.isEmpty()) {
//            post.setEnabled(false);
//        }
//        else post.setEnabled(true);


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

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = postText.getText().toString();
                if (postImageUri != null && caption!=null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG,"Entering !(postImageUri == null) && !caption.isEmpty()");
                    uploadPost(caption, redBitmap, view);
                }
                else Toast.makeText(context,"Write some text and add some images you want to share",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void openCameraFun() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        postImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, postImageUri);
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
            postImageUri = data.getData();
            postImage.setImageURI(postImageUri);
            imageToBitmap(postImageUri);
            ////
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), postImageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            redBitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);

        }


        if (requestCode == CLICK_FROM_CAMERA && resultCode == RESULT_OK) {
            postImage.setImageURI(postImageUri);
            imageToBitmap(postImageUri);
//
//            //
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            postImage.setImageBitmap(thumbnail);
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//            File destination = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//            FileOutputStream fo;
//            try {
//                fo = new FileOutputStream(destination);
//                fo.write(bytes.toByteArray());
//                fo.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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

    private void imageToBitmap(Uri imageUri) {
        //Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        redBitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
    }

    private void uploadPost(String postCaption, Bitmap bitmap, View view) {
        Log.d(TAG,"Entering uploadpost method");
        String filePathName = "Users/" + "UserId/" + userId + "Post";
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 240000, byteArrayOutputStream);
//        byte[] data = byteArrayOutputStream.toByteArray();

        final int lnth=bitmap.getByteCount();
        ByteBuffer dst= ByteBuffer.allocate(lnth);
        bitmap.copyPixelsToBuffer( dst);
        byte[] data=dst.array();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathName);
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"Entering onSuccessLinstner of data putting bytes method");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("UserId", userId);
                            hashMap.put("userName", userName);
                            hashMap.put("downloadUri", downloadUri);
                            hashMap.put("caption", postCaption);
                            hashMap.put("postLike", "0");
                            hashMap.put("postComments", "0");

                            db.collection("Users").document(userId)
                                    .collection("userPost")
                                    .document("post")
                                    .set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(context, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                            ;
                                            postImageUri = null;
                                            postImage.setImageURI(null);

                                            Navigation.findNavController(view).navigate(R.id.action_createPostFragment_to_homeFragment);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed firestore", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed upload", Toast.LENGTH_LONG).show();
                    }
                });
    }
}