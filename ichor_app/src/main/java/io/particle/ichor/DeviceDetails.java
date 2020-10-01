package io.particle.hydroalert;

import java.util.Date;



public class DeviceDetails {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    private String deviceName;
    private boolean isConnected;

    public DeviceDetails(String deviceName, boolean isConnected, Date lastHeard) {
        this.deviceName = deviceName;
        this.isConnected = isConnected;
        this.lastHeard = lastHeard;
    }
public DeviceDetails(){

}
    public Date getLastHeard() {

        return lastHeard;
    }

    public void setLastHeard(Date lastHeard) {
        this.lastHeard = lastHeard;
    }

    private Date lastHeard;

}



