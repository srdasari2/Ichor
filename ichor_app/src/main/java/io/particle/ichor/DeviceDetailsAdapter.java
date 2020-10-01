package io.particle.hydroalert;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;




public class DeviceDetailsAdapter extends ArrayAdapter<DeviceDetails> {

    ArrayList<DeviceDetails> mDeviceDetails ;
    public  DeviceDetailsAdapter(Context context, ArrayList<DeviceDetails> resource){
        super(context, 0, resource);
        mDeviceDetails = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;
        if(currentView == null){
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        DeviceDetails details = mDeviceDetails.get(position);
        TextView deviceName = (TextView)currentView.findViewById(R.id.devicename);
        TextView status = (TextView)currentView.findViewById(R.id.status);
        GradientDrawable mGradientDrawable = (GradientDrawable)status.getBackground();
        deviceName.setText(details.getDeviceName());
        Date lastHeard = details.getLastHeard();
        long differenceInMinutes = (Math.abs(lastHeard.getTime() - new Date().getTime())/60000);
        if(details.isConnected() ){ //&& differenceInMinutes < 10)
            mGradientDrawable.setColor(ContextCompat.getColor(getContext(), R.color.device_online));
            status.setText("On");
        }
        else {
            mGradientDrawable.setColor(ContextCompat.getColor(getContext(), R.color.device_offline));
            status.setText("Off");
        }
        return currentView;
    }
    public void setmDeviceDetails(ArrayList<DeviceDetails> deviceDetails){
        this.mDeviceDetails = deviceDetails;
    }
}
