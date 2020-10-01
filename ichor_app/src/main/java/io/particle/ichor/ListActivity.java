package io.particle.hydroalert;
//Importing all the files needed for this class

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.hydroalert.util.DataHolder;

public class ListActivity extends AppCompatActivity {
    public static final String DEVICE_LIST = "DEVICE_LIST";
    private ArrayList<DeviceDetails> mDeviceDetails;
    private ArrayList<ParticleDevice> deviceList= null;
    private final String LOG_TAG = ListActivity.class.getName();
    private ProgressBar progressBar;
    private DeviceDetailsAdapter adapter;
    private Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Required Android super class method called onCreate
        setContentView(R.layout.activity_list); // Setting View
        final ListView listView = (ListView)findViewById(R.id.devicelist);
       // deviceList = getIntent().getParcelableArrayListExtra(DEVICE_LIST); //Getting the device list to show
        deviceList = DataHolder.getInstance().getDeviceList();
        extractDeviceDetails(deviceList);        //Getting device details form the cloud
        adapter = new DeviceDetailsAdapter(ListActivity.this, mDeviceDetails); //Assigns the values to different UI elements on the screen
       // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, deviceNames);
        listView.setAdapter(adapter);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);   //Showing progress Circle
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY); //Setting while color to Progress Circle
        progressBar.setVisibility(View.GONE);
        refresh = (Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCloudDevices();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {  //Handling touch event for the device selected from the list
                Log.d(LOG_TAG, "Device Clicked at position " + position);
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                refresh.setVisibility(View.GONE);

                ParticleDevice device = deviceList.get(position);   // Get the position of the device selected by the user
                if(device.isConnected()) {  //disabling the click action for offline devices
                    Log.d(LOG_TAG, device.getName() + "Is Online");
                    DataHolder.getInstance().setSelectedDevice(device);
                    Async.executeAsync(ParticleCloud.get(ListActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

                        private ParticleDevice mDevice;
                        int distance = 1000; // Initialing the distance value

                        @Override
                        public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {

                            mDevice = deviceList.get(position); //Get the device handle using deviceId

                            try {
                                distance = mDevice.getIntVariable("glucose");  //Read the glucose from Cloud      //srd
                                String url = mDevice.getStringVariable("url");
                                DataHolder.setThingspeakUrlString(url);
                                        Log.d("Distance", "IN: " + distance);
                                        Log.d("url ", url);
                            } catch (ParticleDevice.VariableDoesNotExistException e) {
                                Log.e(LOG_TAG, "Variable does not exist");
                                DataHolder.setThingspeakUrlString("https://thingspeak.com/channels/262254");
                                e.printStackTrace();
                            }
                            catch(ParticleCloudException e){
                                Log.e(LOG_TAG, "Particle Cloud Exception " + e.getBestMessage());
                                e.printStackTrace();
                            }

                            return String.valueOf(distance);

                        }

                        @Override

                        public void onSuccess(Object value) { //Successfully read the glucose from the device
                            Log.d(LOG_TAG, "In variable read successfully " + value.toString());
                            DataHolder.setDistance(distance);
                            //DataHolder.getEventItems().add(new EventItem(distance, new Date()));
                           // Intent intent = new Intent(ListActivity.this, ValueActivity.class);
                            Intent intent = new Intent(ListActivity.this, SummaryActivity.class);
                            startActivity(intent);  //Transferring to Alert screen
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }

                        @Override
                        public void onFailure(ParticleCloudException e) {
                            Log.e(LOG_TAG, "Particle Cloud Exception " + e.getBestMessage());
                            e.printStackTrace();
                            Log.e("info", e.getMessage());  //
                        }
                    });
                }

         }
        });

    }

    private String[] extractDeviceNames(ArrayList<ParticleDevice> cloudDevices){
       String[] devices = new String[cloudDevices.size()-1];
        for(int i=0;i<cloudDevices.size()-1;i++){
            devices[i] = cloudDevices.get(i).getName();
        }
        return devices;
    }
    private void extractDeviceDetails(ArrayList<ParticleDevice> cloudDevices){
        mDeviceDetails  = new ArrayList<>();
        for(int i=0;i<cloudDevices.size(); i++){  //Looping through all particle devices
            DeviceDetails details = new DeviceDetails();
            ParticleDevice device = cloudDevices.get(i);
            if(device !=null) {
                details.setDeviceName(device.getName());    //getting device name
                details.setConnected(device.isConnected()); // Getting device status
                details.setLastHeard(device.getLastHeard());    // Getting the date and time of the last heard
                mDeviceDetails.add(details);
            }
        }

    }

    private void updateCloudDevices() {
        Async.executeAsync(ParticleCloud.get(ListActivity.this), new Async.ApiWork<ParticleCloud, Object>() {


            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                deviceList =  (ArrayList)sparkCloud.getDevices();  //Get all Particle devices for this account
                DataHolder.getInstance().setDeviceList(deviceList);
                Log.d(LOG_TAG, "Number of devices retrived from Particle Cloud :" + deviceList.size() );
                return -1;

            }

            @Override
            public void onSuccess(Object value) {   // executes when login is successful
                extractDeviceDetails(deviceList);
                adapter.setmDeviceDetails(mDeviceDetails);
                adapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Device List updated ");

            }

            @Override
            public void onFailure(ParticleCloudException e) {  //executes if login fails
                Log.e(LOG_TAG, "Retrieving the device list failed ");
                Log.e(LOG_TAG,  e.getBestMessage());
                e.printStackTrace();
                Log.e(LOG_TAG, e.getBestMessage());
            }
        });
    }
    }

