package com.example.serbigoworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.*;

public class register extends FragmentActivity implements  AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage;
    private GoogleMap mMap;
    private Marker newlocationmarker;
    Spinner service_lists;
    EditText fname, lname, sp_address, credentials;
    Button save_details;
    ImageView worker_image;

    String service = "";
    double latitude;
    double longitude;
    int Take_Image_Code = 10001;

    private StorageReference mStorageRef;
    String uid = fAuth.getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //for dropdown box
        service_lists = findViewById(R.id.service_list);
        service_lists.setOnItemSelectedListener(this);

        fstorage = FirebaseFirestore.getInstance();
        //fAuth = FirebaseAuth.getInstance();

        sp_address = findViewById(R.id.address);
        fname = findViewById(R.id.first_name);
        lname = findViewById(R.id.last_name);
        save_details = findViewById(R.id.save);
        worker_image = findViewById(R.id.imageView);
        credentials = findViewById(R.id.credentials);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



            save();
            upload();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       //Toast.makeText(this, parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
        if(parent.getSelectedItemPosition() == 1) {
            service = "1";
        }else if(parent.getSelectedItemPosition() == 2) {
            service = "2";
        }else if(parent.getSelectedItemPosition() == 3) {
            service = "3";
        }else if(parent.getSelectedItemPosition() == 4) {
            service = "4";
        }else if(parent.getSelectedItemPosition() == 5) {
            service = "5";
        }else if(parent.getSelectedItemPosition() == 6) {
            service = "6";
        }else if(parent.getSelectedItemPosition() == 7) {
            service = "7";
        }else if(parent.getSelectedItemPosition() == 8) {
            service = "8";
        }else if(parent.getSelectedItemPosition() == 9) {
            service = "9";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng location = new LatLng (14.2786, 121.4156);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        //MarkerOptions markerOptions = new MarkerOptions();
       // markerOptions.position(location);
       // markerOptions.title("Laguna, Philippines");
       // newlocationmarker = mMap.addMarker(markerOptions);





      googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
          @Override
          public void onMapClick(LatLng latLng) {

              {


                  if (newlocationmarker != null) {
                      newlocationmarker.remove();

                  }

                 Geocoder geocoder;
                  List<Address> addresses;
                  geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                  latitude = latLng.latitude;
                 longitude = latLng.longitude;


                  try {

                      addresses = geocoder.getFromLocation(latitude, longitude, 1);
                      if (addresses != null && addresses.size() > 0) {
                          String address = addresses.get(0).getAddressLine(0);
                          String city = addresses.get(0).getLocality();
                          String state = addresses.get(0).getAdminArea();
                          String country = addresses.get(0).getCountryName();
                          String postalCode = addresses.get(0).getPostalCode();
                          String knownName = addresses.get(0).getFeatureName();
                          String province = addresses.get(0).getSubAdminArea();

                          sp_address.setText(knownName + ", " + city + ", " + province + ", " + country);


                          MarkerOptions markerOptions = new MarkerOptions();
                          markerOptions.position(latLng);
                          markerOptions.title(knownName + ", " + city + ", " + province + ", " + country);
                          newlocationmarker = mMap.addMarker(markerOptions);
                          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));


                      }


                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }

          }
      });



    }





    public void dialog_box () {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialogbox);
            dialog.show();

            Button ok = (Button)dialog.findViewById(R.id.ok);



            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


    }


    @Override
    protected void onStart() {
        super.onStart();

        dialog_box ();
    }




    public void save(){


        //saving provider details

        final DocumentReference docRef = fstorage.collection( "provider"). document(fAuth.getCurrentUser().getUid());
        final DocumentReference docRef2 = fstorage.collection( "geo_location"). document(fAuth.getCurrentUser().getUid());

        save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fname.getText().toString().isEmpty() || lname.getText().toString().isEmpty( ) || sp_address.getText().toString().isEmpty( )||service.isEmpty() ) {

                    Toast.makeText(getApplicationContext(), "All fields are Required.", Toast.LENGTH_SHORT).show();


                }else {

                    String credits;
                    if (credentials.getText().toString().isEmpty()) {
                        credits = "No credentials to be shown.";
                    }
                    else {
                        credits = credentials.getText().toString();
                    }

                    String firstname = fname.getText().toString();
                    String lastname = lname.getText().toString();
                    String address = sp_address.getText().toString();
                    Long rate = Long.valueOf(5);
                    Long status = Long.valueOf(0);
                    String contact = fAuth.getCurrentUser().getPhoneNumber().toString();



                    Map<String,Object> provider = new HashMap<>();
                    Map<String,Object> geo_location = new HashMap<>();

                    provider.put("first_name", firstname );
                    provider.put("last_name", lastname );
                    provider.put("address", address );
                    provider.put("rate", rate);
                    provider.put("status", status);
                    provider.put("service", service);
                    provider.put("contact", contact);
                    provider.put("credentials", credits);

                    GeoPoint geo = new GeoPoint(latitude,longitude);
                    geo_location.put("location", geo);


                    //for provider details
                    docRef.set(provider).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"Service Provider Account Succesfully Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), main_container.class));
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(),"Provider details not Inserted. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                    //for location latitude longitude
                    docRef2.set(geo_location).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                            }else {

                            }


                        }
                    });

                }


            }
        });
        //end of saving details


    }



    //for photo

    public void upload() {

        worker_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCamera();

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Take_Image_Code) {

            if(resultCode == RESULT_OK) {

                Bitmap bitmap  = (Bitmap) data.getExtras().get("data");
               worker_image.setImageBitmap(bitmap);
                handleUpload(bitmap);



            }
        }

    }


    //handles the photo to the uid of the user
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_image);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");

        mStorageRef.putBytes(output_image.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                getImageUrl(mStorageRef);
            }
        });



    }

    //getting the storage reference for the firebase storage
    private void getImageUrl(StorageReference mStorageRef) {
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                setUserProfile(uri);


            }
        });

    }

    //for updating and fetching profile image fetching
    private void setUserProfile(Uri uri) {
        FirebaseUser user = fAuth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(register.this,"Profile Photo Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }


    //permission for camera asking
    private void askCamera(){

        if(ContextCompat.checkSelfPermission(register.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(register.this, new String[]{Manifest.permission.CAMERA}, Take_Image_Code);
        }else {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, Take_Image_Code);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Take_Image_Code) {
            if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, Take_Image_Code);

            }else {

                Toast.makeText(register.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }

    }








}
