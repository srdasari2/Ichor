package io.particle.hydroalert;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.hydroalert.util.DataHolder;



public class StatusFragment extends Fragment {

    private Handler mHandler;
    private String email;
    private String password;
    private Button refreshButton;
    private TextView tv;
    private TextView alertTitle;
    //private ImageView warningImage;
    RelativeLayout mRelativeLayout;
    ParticleDevice mDevice;
    private ArrayList<ParticleDevice> devices;
    View rootView;

    private final String LOG_TAG = ValueActivity.class.getName();
    private final int ERROR_NUMBER = 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_value, container, false);
        this.mHandler = new Handler();   // Handler can be used to repeat an action
        // mDevice = getIntent().getParcelableExtra("device");   //Reading the device selected by the user
        mDevice = DataHolder.getInstance().getSelectedDevice();  //Reading the device selected by the user
        getActivity().setTitle(mDevice.getName()); // Showing device name in the title bar
        // int value = getIntent().getIntExtra(ARG_VALUE, 0);
        int value = DataHolder.getInstance().getDistance();
        //devices = getIntent().getParcelableArrayListExtra(DEVICE_LIST); //Reading the device List
        devices = DataHolder.getInstance().getDeviceList(); //Reading the device List
        processValues(value);   // Method to decide the message on the alert screen
        mRunnable.run();
        refreshButton = (Button) rootView.findViewById(R.id.refresh_button);    //Handling refresh button click
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
                // Do network work on background thread
                executeAsyncTaskToReadInVariable();
            }
        });

        return rootView;
    }

    public void processValues(int value) {
        DateFormat df = new SimpleDateFormat("h:mm:ss a");  // Creating a timestamp
        tv = (TextView) rootView.findViewById(R.id.value);
        alertTitle = (TextView) rootView.findViewById(R.id.alertTitle);

        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.valuelayout);

        String msgToDisplay = "";
        int difference = value;
        if (difference == 1000) {    //Error condition
            msgToDisplay = mDevice.getName() + " is currently unavailable  ";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D")); //Setting background color
            alertTitle.setVisibility(View.GONE);  //hide title

        } else if (difference >= 80 && difference <= 130) {   //Safe level
            msgToDisplay = "Blood Glucose Level: " + difference + " mg/dL";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#000000"));  //Setting background color
            alertTitle.setText("SAFE");
            alertTitle.setTextColor(Color.GREEN);
        } else if (difference > 130 && difference <= 180) {   //Unsafe if before meal
            msgToDisplay = "Blood Glucose Level: " + difference + " mg/dL";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#000000")); //Setting background color
            alertTitle.setText("UNSAFE \n (if before meal)");
            alertTitle.setTextColor(Color.YELLOW);
        } else if (difference < 80 || difference > 180){   //Unsafe at any time
            msgToDisplay = "Blood Glucose Level: " + difference + " mg/dL";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#000000")); //Setting background color
            alertTitle.setText("UNSAFE");
            alertTitle.setTextColor(Color.RED);
        }
        String timeMsg = df.format(Calendar.getInstance().getTime());
        //DataHolder.getInstance().getEventItems().add(new EventItem(value, new Date()));
        msgToDisplay = msgToDisplay + "\n\n\n\n" + timeMsg;
        tv.setText(msgToDisplay);
        //tv.setTextColor(Color.parseColor("#F44336"));
        tv.setTextColor(Color.WHITE);


    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            executeAsyncTaskToReadInVariable();

            StatusFragment.this.mHandler.postDelayed(mRunnable, 15000);  //Refreshing screen every 15 seconds
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:       // Handling back arrow
                Intent upInent = NavUtils.getParentActivityIntent(getActivity());
                //  if(NavUtils.shouldUpRecreateTask(this, upInent)){
                TaskStackBuilder.create(getContext()).addNextIntentWithParentStack(upInent).startActivities();
                getActivity().finish();
        }
        return true;
    }

    @Override
    public void onResume() {    //called when user returns to the app screen
        super.onResume();
        mRunnable.run();
    }

    @Override
    public void onStop() {   //Called when user leaves the app
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }

    private void executeAsyncTaskToReadInVariable() {      // Task to read glucose from cloud
        Async.executeAsync(ParticleCloud.get(getContext()), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                //ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));

                int variable = ERROR_NUMBER;     // Error condition, device not reachable
                try {
                    variable = mDevice.getIntVariable("glucose");      // Reading the glucose from cloud        //srd
                    //if (variable < 100) {
                    // SmsManager smsManager = SmsManager.getDefault();
                    // smsManager.sendTextMessage("5123836659", null, "Glucose Alert", null, null);
                    //}
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    //Toaster.l(ValueActivity.this, e.getMessage());
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                } catch (ParticleCloudException e) {
                    Log.e(LOG_TAG, e.getBestMessage());
                    e.printStackTrace();
                }
                return variable;
            }

            @Override
            public void onSuccess(Object i) { // this goes on the main thread
                int iGlucoseLevel = ((Integer) i).intValue();
                processValues(iGlucoseLevel);    // On success updating the message based new glucose
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });
    }

    public void onBackPressed() {   // When Android back button is pressed,  taking user back to devices screen
        //getActivity().super.onBackPressed();
        Intent upInent = NavUtils.getParentActivityIntent(getActivity());
        TaskStackBuilder.create(getContext()).addNextIntentWithParentStack(upInent).startActivities();
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
       // for(Fragment fragment: fragmentPages){
           // getChildFragmentManager().beginTransaction().remove(fragment).commit();
       // }
    }
}