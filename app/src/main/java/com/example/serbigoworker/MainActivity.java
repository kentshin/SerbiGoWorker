package com.example.serbigoworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private boolean ispermission;
    FirebaseAuth fAuth;
    FirebaseFirestore fstorage;
    EditText phone,otp;
    Button verify;
    ProgressBar progressBar;

    String verification;
    CountryCodePicker codePicker;

    PhoneAuthProvider.ForceResendingToken Token;
    Boolean verificationProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fstorage = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        phone = findViewById(R.id.log_in);
        otp = findViewById(R.id.passcode);
        verify = findViewById(R.id.verify);
        codePicker = findViewById(R.id.ccp);




        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!verificationProgress) {

                    if (!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {
                        String phonenumber = "+" + codePicker.getSelectedCountryCode() + phone.getText().toString();
                        requestOTP(phonenumber);
                        verify.setText("Sending...");

                    } else {
                        phone.setError("Phone Number is Not Valid");
                    }

                } else {

                    String userOTP = otp.getText().toString();
                    PhoneAuthCredential credential;

                    if (!userOTP.isEmpty() && userOTP.length() == 6) {

                        credential = PhoneAuthProvider.getCredential(verification, userOTP);
                        verifyAuth(credential);
                        verify.setEnabled(false);
                        verify.setText("Signing in...");

                    } else {
                        otp.setError("Invalid Account Details");
                    }

                }

            }

        });

    }



    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    checkUser();
                    verify.setEnabled(false);


                }else {

                    otp.setError("Invalid Verification Code.");

                }
            }
        });
    }





    private void checkUser() {

        final DocumentReference docRef = fstorage.collection("client").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {

                    //Toast.makeText(getApplicationContext(),"Please input a valid phone number", Toast.LENGTH_SHORT).show();
                    verify.setEnabled(true);
                    verify.setText("Sign In");
                    phone.setError("Phone Number Already Registered.");

                }else {

                    dialogbox();
                    //
                    final DocumentReference provider_ref = fstorage.collection("provider").document(fAuth.getCurrentUser().getUid());
                    provider_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            if(snapshot.exists()) {
                                startActivity(new Intent(getApplicationContext(),main_container.class));
                                finish();
                            }else {

                                startActivity(new Intent(getApplicationContext(),register.class));
                                finish();
                            }
                        }
                    });

                    //
                }
            }
        });

    }



    @Override
    protected void onStart () {
        super.onStart();
        if(fAuth.getCurrentUser() != null) {
            progressBar.setVisibility(View.VISIBLE);
            checkUser();
            verify.setEnabled(false);
            verify.setText("Signing in...");

        }


    }


    public void dialogbox () {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.verifying_dialog);
        dialog.show();

    }






    private void requestOTP(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification = s;
                Token = forceResendingToken;
                otp.setVisibility(View.VISIBLE);
                verify.setText("Verify");
                verificationProgress = true;
                Toast.makeText(MainActivity.this,"Verification Sent!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(MainActivity.this,"Cannot Create a Service Provider Account" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }













    }
