package com.samuelvialle.recyclerviewimagesandvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class AddMediaActivity extends AppCompatActivity {

    private static final String TAG = "AddMediaActivity";

    /**
     * Variables Globales
     **/
    ImageView iv1, mediaContainer;
    ArrayList<Uri> listLocalMediaFile = new ArrayList<>();
    ArrayList<String> savedImageUrl = new ArrayList<>();
    LinearLayout llMediaContainer;
    int counter; //
    int i = 0;

    /**
     * Variable pour les URI des medias
     **/
    Uri localMediaUri, serverMediaUri;

    /**
     * Variables Firebase
     **/
    private FirebaseFirestore db;
    private CollectionReference mediaList;
    private FirebaseStorage storage;
    StorageReference dossierMedia;

    /**
     * Méthode initUI pour lier les widgets au code
     **/
    private void initUI() {
        iv1 = findViewById(R.id.iv1);
        llMediaContainer = findViewById(R.id.llMediaContainer);
    }

    /**
     * 3 Méthode pour l'initialisation des composants Firebase
     **/
    public void initFirebase() {
        // Instance pour le storage des images
        storage = FirebaseStorage.getInstance();
        dossierMedia = storage.getReference().child("Medias");
        // Instance pour Firestore
        db = FirebaseFirestore.getInstance();
        // Assignation de la collection
        mediaList = db.collection("mediaList");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        /** Appel des méthodes **/
        initUI();
        initFirebase();
    }

    /**
     * Méthode pour récupérer l'extension des medias
     **/
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Méthodep pour l'ajout des médias
     **/
//    public void uploadMedias() {
//        fileStorage = fileStorage.child("medias/" + System.currentTimeMillis()
//                + "." + getFileExtension(localMediaUri));
//        // Upload vers le storage
//        fileStorage.putFile(localMediaUri)
//                .addOnCompleteListener(AddMediaActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // On récupére l'url de l'image uploadée
//                            fileStorage.getDownloadUrl()
//                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                        @Override
//                                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
//                                            ArrayList<Uri> urlMedia = task.getResult();
//                                            MediaModel newMedia = new MediaModel(urlMedia,"","","","");
//
//                                            mediaList.add(newMedia)
//                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                        @Override
//                                                        public void onSuccess(DocumentReference documentReference) {
//                                                            Toast.makeText(AddMediaActivity.this, "Server upload OK", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull @NotNull Exception e) {
//                                                            Toast.makeText(AddMediaActivity.this, "Error adding medias " + e, Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        }
//                                    });
//                        }
//                    }
//                });
//    }
    private void addMediaToLinear() {
        listLocalMediaFile.add(localMediaUri);
        if ((listLocalMediaFile.size() != 0)) {
            mediaContainer = new ImageView(getApplicationContext());
            // Params de la taille
            mediaContainer.setLayoutParams(new LinearLayout.LayoutParams(100,100));
            mediaContainer.setPadding(4, 4, 4, 4);
            // TODO Ajouter le clic sur l'image pour la modifier ou la supprimer en s'appuyant sur l'id ;)
            //mediaContainer.setId();
            llMediaContainer.addView(mediaContainer);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_add_a_photo_24)
                    .placeholder(R.drawable.ic_add_a_photo_24);

            Glide.with(getApplicationContext())
                    .load(listLocalMediaFile.get(i))
                    .apply(options)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mediaContainer);

            i++;
        }
    }

    public void uploadMedias(View view) {
        if ((listLocalMediaFile.size() != 0)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("uplaoded 0/" + listLocalMediaFile.size());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < listLocalMediaFile.size(); i++) {
                final int index = i;
                StorageReference fileReference = dossierMedia.child(System.currentTimeMillis()
                        + "." + getFileExtension(listLocalMediaFile.get(i)));

                fileReference.putFile(listLocalMediaFile.get(i))
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    fileReference.getDownloadUrl()
                                            .addOnCompleteListener(new OnCompleteListener<Uri>() {

                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    counter++;
                                                    progressDialog.setMessage("Uploaded " + counter + "/" + listLocalMediaFile.size());
                                                    if (task.isSuccessful()) {
                                                        savedImageUrl.add(task.getResult().toString());
                                                    } else {
                                                        //this is to delete the image if the download url is not complete
                                                        storageReference.child("UserImages/").child(listLocalMediaFile.get(index).toString()).delete();
                                                        Toast.makeText(AddMediaActivity.this, "Could'nt save ", Toast.LENGTH_LONG).show();
                                                    }
                                                    if (counter == listLocalMediaFile.size()) {
                                                        saveImageDataToFirestore(progressDialog);
                                                    }
                                                }
                                            });
                                } else {
                                    progressDialog.setMessage("Uploaded" + counter + "/" + listLocalMediaFile.size());
                                    counter++;
                                    Toast.makeText(AddMediaActivity.this, "could'nt upload" + listLocalMediaFile.get(index).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Please add some images first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageDataToFirestore(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Saving uploaded images...");
        Map<String, Object> dataMap = new HashMap<>();
        //Below line of code will put your images list as an array in fireStore
        dataMap.put("images", savedImageUrl);

        mediaList.add(dataMap)
                .addOnSuccessListener(new OnSuccessListener<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        progressDialog.dismiss();
                        Toast.makeText(AddMediaActivity.this, "images uploaded and saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddMediaActivity.this, "Images uploaded but we couldn't save them to database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage() {
        /**
         *  9 Ajout de la vérification de la permission de parcourir les dossiers du terminal
         * Avant toute chose il faut ajouter la permission dans le manifest
         **/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                localMediaUri = data.getData();
                // Ajout dans le Array
                addMediaToLinear();

            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(this, R.string.access_permission_is_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Méthode de gestion du clic sur boutons d'ajout des images
     **/
    public void btnAddMedia(View v) {
        pickImage();
    }

    /** Intent vers la seconde activité **/
    public void  goToSecondActivity(View view){
        Intent intent = new Intent(AddMediaActivity.this, ShowMediaActivity.class);
        intent.putExtra("mediaId", "toto");//TODO
        startActivity(intent);
    }

}