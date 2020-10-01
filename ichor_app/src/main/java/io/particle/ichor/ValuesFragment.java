package io.particle.hydroalert;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.Date;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.hydroalert.util.CommonUtil;
import io.particle.hydroalert.util.DataHolder;

/**
 * Created by qz2zvk on 4/10/17.
 */

public class ValuesFragment extends Fragment {
    private final String LOG_TAG = ValuesFragment.class.getName();
    private final int ERROR_NUMBER = 1000;


    private Handler mHandler;
    ParticleDevice mDevice;
    ListView mListView;
    EventItemAdapter mAdapter;
    View rootView;
    public ValuesFragment(){

    }

   // @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        this.mHandler = new Handler();
//        mDevice = DataHolder.getInstance().getSelectedDevice();  //Reading the device selected by the user
//        getActivity().setTitle(mDevice.getName());
//        mAdapter = new EventItemAdapter(getActivity().getApplicationContext(), CommonUtil.convertQueueToList());
//        mListView.setAdapter(mAdapter);
//        //mListView.setStackFromBottom(t);
//        //((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
//        mAdapter.notifyDataSetChanged();
//        mRunnable.run();
//    }


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_values, container, false);
        mListView = (ListView)rootView.findViewById(R.id.list);
        this.mHandler = new Handler();
        mDevice = DataHolder.getSelectedDevice();  //Reading the device selected by the user
        getActivity().setTitle(mDevice.getName());

        mAdapter = new EventItemAdapter(getActivity().getApplicationContext(), CommonUtil.convertQueueToList());
        mListView.setAdapter(mAdapter);
        //mListView.setStackFromBottom(t);
        //((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
       mRunnable.run();
      // mAdapter.notifyDataSetChanged();
        return rootView;
    }
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            executeAsyncTaskToReadInVariable();
            ValuesFragment.this.mHandler.postDelayed(mRunnable,30000);  //Refreshing screen every 15 seconds
        }
    };
    @Override
    public void onResume() {    //called when user returns to the app screen
        super.onResume();
        mRunnable.run();
    }

    @Override
    public void onStop() {   //Called when user leaves the app
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
        DataHolder.getInstance().getEventItems().clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        DataHolder.getInstance().getEventItems().clear();

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    private void executeAsyncTaskToReadInVariable(){      // Task to read glucose level from cloud
        Async.executeAsync(ParticleCloud.get(getContext()), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                //ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));

                int variable=ERROR_NUMBER;     // Error condition, device not reachable
                try {
                    variable = mDevice.getIntVariable("glucose");      // Reading the glucose level from cloud        //srd
                    //if (variable < 100) {
                    // SmsManager smsManager = SmsManager.getDefault();
                    // smsManager.sendTextMessage("5123836659", null, "Glucose Alert", null, null);
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
                int iGlucoseLevel = ((Integer) i).intValue();
              DataHolder.getEventItems().add(new EventItem(iGlucoseLevel, new Date()));    // On success updating the message based new glucose level
               // mAdapter = new EventItemAdapter(getActivity().getApplicationContext(), CommonUtil.convertQueueToList());
               mAdapter.setItems(CommonUtil.convertQueueToList());
                //mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(ParticleCloudException e) {
                e.printStackTrace();
            }
        });


    }




}
