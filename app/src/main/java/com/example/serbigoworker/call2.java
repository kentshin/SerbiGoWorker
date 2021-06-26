package com.example.serbigoworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class call2 extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private static final String APP_KEY = "3798d145-41a1-40ad-bcc1-2be8c7bae3e0";
    private static final String APP_SECRET = "IiOdpcJ5/0W72T6M0sSyvA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private SinchClient sinchClient;
    //private TextView callState;
    private Button button;
    private String callerId;
    private String recipientId;

    ImageButton end_call2;
    ImageButton start_call2;
    TextView text_label;
    ImageView worker_image;
    Chronometer call_timer;

    MediaPlayer call_sound;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);

        callerId = fAuth.getUid().toString();


        call_sound = MediaPlayer.create(call2.this, R.raw.making_call_alarm);

        download_photo();



        //permission asking
        if (ContextCompat.checkSelfPermission(call2.this,
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (call2.this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(call2.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }



        //implementation of sinch
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


        start_call2 = (ImageButton) findViewById(R.id.start_call);
        end_call2 = (ImageButton) findViewById(R.id.end_call);
        text_label = (TextView) findViewById(R.id.text_lbl);
        worker_image = (ImageView)findViewById(R.id.client_photo_call);
        call_timer = (Chronometer)findViewById(R.id.timer) ;

        start_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recipientId = global_variables.client_id;

                if (call == null) {

                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    text_label.setText("Calling please wait...");

                    start_call2.setEnabled(false);

                    call_timer.setBase(SystemClock.elapsedRealtime());
                    call_timer.start();

                } else {

                    call.hangup();
                    call_timer.setBase(SystemClock.elapsedRealtime());
                    call_timer.stop();


                }
            }
        });


        end_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (call == null) {

                }else {

                    call.hangup();
                    call_timer.setBase(SystemClock.elapsedRealtime());
                    call_timer.stop();

                }

            }
        });


    }



    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            text_label.setText(" ");
            Toast.makeText(call2.this, "Call Ended!", Toast.LENGTH_LONG).show();
            call_timer.setBase(SystemClock.elapsedRealtime());
            call_timer.stop();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            start_call2.setEnabled(true);

            call_sound.stop();

        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            text_label.setText("Call Connected!");
            call_sound.stop();

            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            text_label.setText("Ringing...");
            call_sound = MediaPlayer.create(call2.this, R.raw.making_call_alarm);
            call_sound.start();
            // Toast.makeText(call2.this, "Ringing...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            call.addCallListener(new SinchCallListener());



        }


    }






    void download_photo() {

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String uid = global_variables.client_id;
        //Toast.makeText(getContext(), uid, Toast.LENGTH_LONG).show();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {

                    Glide.with(worker_image.getContext()).load(uri).into(worker_image);
                }

            }
        });


    }










}
