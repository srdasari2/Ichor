package io.particle.hydroalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.cimosys.basic.encryption.util.CipherHelper;

import java.io.IOException;
import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.hydroalert.util.DataHolder;
import io.particle.hydroalert.util.EncryptionSetupReources;


public class SplashActivity extends AppCompatActivity {

    private String email;
    private String password;
    private ProgressBar progressBar;
    SharedPreferences SP;  //Sharing values across different screens
    EncryptionSetupReources encryptionSetupReources;
    CipherHelper cipherHelper;
    private ArrayList<ParticleDevice> deviceList;

private final String LOG_TAG = SplashActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {  //Entry point for Android screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);   // Loading the view xml file
        SP = getApplication().getSharedPreferences("encryption", Context.MODE_PRIVATE);
        SP.edit().putString("CipherPwd", getString(R.string.cipher_password)).commit();  // Encryption
        cipherHelper = new CipherHelper(SP.getString("CipherPwd", null));

        SP.edit().putString("username",  "j4tKVc6tfPL6xbfbRVC7Jq+XfUpkMy74").commit();  //Putting Username in shared preferences
        SP.edit().putString("password", "kV5cnTROajGXNXQKv1a/Qg==").commit();  // Putting Password in shared preferences

        encryptionSetupReources = EncryptionSetupReources.getInstance(getApplicationContext());



        progressBar = (ProgressBar) findViewById(R.id.progressbar);   //Showing progress Circle
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY); //Setting while color to Progress Circle

        login(); //This method is to perform login activity and to acquire device information form cloud

    }


    private void login() {
        //srd email = cipherHelper.decrypt(SP.getString("username", null));   //Reading encrypted username
        //srd password = cipherHelper.decrypt(SP.getString("password", null));  //Reading encrypted password
        email = "sdprojectsf@gmail.com";        //srd
        password = "Sfproject1!";               //srd


        Async.executeAsync(ParticleCloud.get(SplashActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

            private ParticleDevice mDevice;
            int distance;

            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                sparkCloud.logIn(email, password);                 //Login to the IoT cloud
                Log.d(LOG_TAG, "Login Successful ");
               deviceList =  (ArrayList)sparkCloud.getDevices();  //Get all Particle devices for this account
                DataHolder.getInstance().setDeviceList(deviceList);
                Log.d(LOG_TAG, "Number of devices retrieved from Particle Cloud :" + deviceList.size() );
                return -1;

            }

            @Override
            public void onSuccess(Object value) {   // executes when login is successful
                Log.d("Login Successful", "Logged in");
                Intent intent = new Intent(SplashActivity.this, ListActivity.class);  //
                startActivity(intent);                        //If successful, moving to screen that has  list of devices
                finish();
            }

            @Override
            public void onFailure(ParticleCloudException e) {  //executes if login fails
                Log.e(LOG_TAG, "Login failed ");
                Log.e(LOG_TAG,  e.getBestMessage());
                e.printStackTrace();
                Log.e(LOG_TAG, e.getBestMessage());
            }
        });
    }


}
