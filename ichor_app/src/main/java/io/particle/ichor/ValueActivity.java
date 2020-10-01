package io.particle.hydroalert;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.hydroalert.util.DataHolder;

public class ValueActivity extends AppCompatActivity {


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

    private final String LOG_TAG = ValueActivity.class.getName();
    private final int ERROR_NUMBER = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Method to set up the screen layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);  //Displaying the screen
        this.mHandler = new Handler();   // Handler can be used to repeat an action
       // mDevice = getIntent().getParcelableExtra("device");   //Reading the device selected by the user
        mDevice = DataHolder.getInstance().getSelectedDevice();  //Reading the device selected by the user
        setTitle(mDevice.getName()); // Showing device name in the title bar
       // int value = getIntent().getIntExtra(ARG_VALUE, 0);
        int value = DataHolder.getInstance().getDistance();
        //devices = getIntent().getParcelableArrayListExtra(DEVICE_LIST); //Reading the device List
        devices = DataHolder.getInstance().getDeviceList(); //Reading the device List
        processValues(value);   // Method to decide the message on the alert screen
        mRunnable.run();
        refreshButton = (Button) findViewById(R.id.refresh_button);    //Handling refresh button click
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
                // Do network work on background thread
                executeAsyncTaskToReadInVariable();
            }
        });

        //refreshButton.performClick();   // Auto refresh
    }

//    public static Intent buildIntent(Context ctx, int value, String deviceid) {
//        Intent intent = new Intent(ctx, ValueActivity.class);
//        intent.putExtra(ARG_VALUE, value);
//        intent.putExtra(ARG_DEVICEID, deviceid);
//
//        return intent;
//    }

    //Method to create the alert message

    public void processValues(int value) {
        DateFormat df = new SimpleDateFormat("h:mm:ss a");  // Creatinga  timestamp
        //String timeMsg =  df.format(Calendar.getInstance().getTime());
        tv = (TextView) findViewById(R.id.value);
        alertTitle = (TextView)findViewById(R.id.alertTitle);
        //warningImage = (ImageView)findViewById(R.id.warningImage);

        mRelativeLayout = (RelativeLayout)findViewById(R.id.valuelayout);

        String msgToDisplay ="";
      //  int difference = bridgeLevel - value ;
        int difference = value;
        if(difference == 1000){    //Error condition
            msgToDisplay = mDevice.getName() + " is currently unavailable  ";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D")); //Setting background color
            alertTitle.setVisibility(View.GONE);  //hide title
            //warningImage.setVisibility(View.GONE); //hide warning image

        }
        else if(difference < -5) {   //Safe level
            msgToDisplay = "Water Level Is " + Math.abs(difference) + "  Inches Below Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#4CAF50"));  //Green background
            alertTitle.setVisibility(View.GONE);
            //warningImage.setVisibility(View.GONE);
        }
        else if(difference>=-5 && difference  <0){   //Watch level
            msgToDisplay = "Water Level Is " + Math.abs(difference) + " Inches Below Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D")); //Yellow background
            alertTitle.setText(R.string.flood_watch_title);
            //warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);   //Showing title
            //warningImage.setVisibility(View.VISIBLE); //SHowing warning/watch image
        }
        else if(difference  >= 0) {   // Warning level
            if(difference == 0){
                msgToDisplay = "Water Level Is At Road Surface";
            }else {
                msgToDisplay = "Water Level Is " + Math.abs(difference) + " Inches Above Road Surface  ";
            }
            mRelativeLayout.setBackgroundColor(Color.parseColor("#000000")); //Red Background
            //alertTitle.setText("Flood Warning");
            alertTitle.setText(R.string.flood_warning_title);
            //warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);
            //warningImage.setVisibility(View.VISIBLE);
        }
        String timeMsg =  df.format(Calendar.getInstance().getTime());
        msgToDisplay = msgToDisplay + "\n" +timeMsg;
        tv.setText(msgToDisplay);
       // Message message = new Message(CommonUtil.generateStatusMessageWithOutTimeStamp(value), CommonUtil.getDateTimeStampInString());
       EventItem eventItem = new EventItem(value, new Date());
        DataHolder.getInstance().getEventItems().add(eventItem);
        //tv.setTextColor(Color.parseColor("#F44336"));
        tv.setTextColor(Color.WHITE);


    }



    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            executeAsyncTaskToReadInVariable();

            ValueActivity.this.mHandler.postDelayed(mRunnable,15000);  //Refreshing screen every 15 seconds
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:       // Handling back arrow
                Intent upInent = NavUtils.getParentActivityIntent(this);
              //  if(NavUtils.shouldUpRecreateTask(this, upInent)){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upInent).startActivities();
                finish();
                }
                return true;
    }

    @Override
    protected void onResume() {    //called when user returns to the app screen
        super.onResume();
        mRunnable.run();
    }

    @Override
    protected void onStop() {   //Called when user leaves the app
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }

    private void executeAsyncTaskToReadInVariable(){      // Task to read water level from cloud
        Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                //ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));

                int variable=ERROR_NUMBER;     // Error condition, device not reachable
                try {
                    //srd variable = mDevice.getIntVariable("in");      // Reading the water level from cloud
                    variable = mDevice.getIntVariable("glucose");      // Reading the water level from cloud        //srd
                    //if (variable < 100) {
                    // SmsManager smsManager = SmsManager.getDefault();
                    // smsManager.sendTextMessage("5123836659", null, "Flood Alert", null, null);
                    //}
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    //Toaster.l(ValueActivity.this, e.getMessage());
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
                catch(ParticleCloudException e){
                    Log.e(LOG_TAG, e.getBestMessage());
                    e.printStackTrace();
                }
                return variable;
            }

            @Override
            public void onSuccess(Object i) { // this goes on the main thread
                int iWaterLevel = ((Integer) i).intValue();
                processValues(iWaterLevel);    // On success updating the message based new water leavel
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });
    }

public void onBackPressed(){   // When Android back button is pressed,  taking user back to devices screen
    super.onBackPressed();
    Intent upInent = NavUtils.getParentActivityIntent(this);
    TaskStackBuilder.create(this).addNextIntentWithParentStack(upInent).startActivities();
    finish();
}
}