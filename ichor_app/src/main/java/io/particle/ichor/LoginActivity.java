package io.particle.hydroalert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cimosys.basic.encryption.util.CipherHelper;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class LoginActivity extends AppCompatActivity {
 Button loginButton;
 private String email;
 private String password;
 protected AlertDialog dialog = null;
 protected static final int PROGRESS_BAR = 5;
 protected boolean dialogShowing = false;
    SharedPreferences SP;
    CipherHelper cipherHelper;
 AsyncTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SP = getApplication().getSharedPreferences("encryption", Context.MODE_PRIVATE);
        SP.edit().putString("CipherPwd", getString(R.string.cipher_password)).commit();
        cipherHelper = new CipherHelper(SP.getString("CipherPwd", null));
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        showDialog(PROGRESS_BAR);
        runLoginAsyncTask();
        loginButton.performClick();
    }


    private void runLoginAsyncTask(){

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                            email = cipherHelper.decrypt(SP.getString("username", null));
                            password = cipherHelper.decrypt(SP.getString("password", null));

                        // Don't:
                        task = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] params) {
                                try {

                                    ParticleCloud.get(LoginActivity.this).logIn(email, password);
                                } catch (final ParticleCloudException e) {
                                    Runnable mainThread = new Runnable() {
                                        @Override
                                        public void run() {
                                            //Toaster.l(LoginActivity.this, e.getBestMessage());
                                            Log.d("Login Info", e.getBestMessage());
                                            e.printStackTrace();
                                            Log.d("info", e.getBestMessage());
//                                            Log.d("info", e.getCause().toString());
                                        }
                                    };
                                    runOnUiThread(mainThread);

                                }

                                return null;
                            }

                        };
                        Async.executeAsync(ParticleCloud.get(v.getContext()), new Async.ApiWork<ParticleCloud, Object>() {

                            private ParticleDevice mDevice;
                            int distance;
                            @Override
                            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                                sparkCloud.logIn(email, password);
                                sparkCloud.getDevices();
                                mDevice = sparkCloud.getDevice(getString(R.string.deviceid));

                                try {
                                    //srd distance = mDevice.getIntVariable("in");
                                    distance = mDevice.getIntVariable("glucose");           //srd
                                    Log.d("Distance", "IN: " + distance);
                                } catch (ParticleDevice.VariableDoesNotExistException e) {
                                    Log.e("Error", "Variable does not exist");
                                }

                                return -1;

                            }

                            @Override
                            public void onSuccess(Object value) {
                                 Log.d("Login Successful", "Logged in");
                                dismissDialog(PROGRESS_BAR);
                               // Intent intent = ValueActivity.buildIntent(LoginActivity.this, distance,  DataHolder.getInstance().getSelectedDevice().getID());
                                //startActivity(intent);
                            }

                            @Override
                            public void onFailure(ParticleCloudException e) {
                                Log.d("Login failed" , e.getBestMessage());
                                e.printStackTrace();
                                Log.d("info", e.getBestMessage());
                            }
                        });


                    }
                }

        );

    }

    @Override
    protected void onResume() {
        runLoginAsyncTask();
        loginButton.performClick();
        super.onResume();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_BAR:
                // dialog = ProgressDialog.show(this, "", "");
                dialog = new ProgressDialog(this, AlertDialog.THEME_TRADITIONAL);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (null != task) {
                            task.cancel(true);
                            dialogShowing = false;
                        }

                    }
                });

                break;
            default:
                break;
        }
        return dialog;
    }

}
