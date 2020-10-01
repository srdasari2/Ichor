package io.particle.hydroalert.util;

import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.hydroalert.EventItem;

/**
 * Created by qz2zvk on 4/10/17.
 */

public class DataHolder {

    private static ArrayList<ParticleDevice> deviceList = new ArrayList<>();
    private static ParticleDevice selectedDevice;

    public static String getThingspeakUrlString() {
        return thingspeakUrlString;
    }

    public static void setThingspeakUrlString(String thingspeakUrlString) {
        DataHolder.thingspeakUrlString = thingspeakUrlString;
    }

    private static String thingspeakUrlString;

    public static void setEventItems(LimitedQueue<EventItem> eventItems) {
        DataHolder.eventItems = eventItems;
    }

    private static  LimitedQueue<EventItem> eventItems = new LimitedQueue<>(10);
    private static int distance;


    public static LimitedQueue<EventItem> getEventItems() {
        return eventItems;
    }




    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int distance) {
        DataHolder.distance = distance;
    }



    public static ArrayList<ParticleDevice> getDeviceList() {
        return deviceList;
    }

    public static void setDeviceList(ArrayList<ParticleDevice> deviceList) {
        DataHolder.deviceList = deviceList;
    }

    public static ParticleDevice getSelectedDevice() {
        return selectedDevice;
    }

    public static void setSelectedDevice(ParticleDevice selectedDevice) {
        DataHolder.selectedDevice = selectedDevice;
    }

private static final DataHolder holder = new DataHolder();
    public static   DataHolder  getInstance(){
        return holder;
    }

}
