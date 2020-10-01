package io.particle.hydroalert.util;

import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.particle.hydroalert.EventItem;
import io.particle.hydroalert.Message;



public class CommonUtil {
    public static String generateStatusMessageWithOutTimeStamp(int distance){

        String msgToDisplay ="";
        //  int difference = bridgeLevel - value ;
        int difference = distance;
        if(difference == 1000){    //Error condition
            msgToDisplay = DataHolder.getInstance().getSelectedDevice().getName() + " is currently unavailable  ";
        }
        else if(difference < -5) {   //Safe level
            msgToDisplay = "Water Level Is " + Math.abs(difference) + "  Inches Below Road Surface";

        }
        else if(difference>=-5 && difference  <0){   //Watch level
            msgToDisplay = "Water Level Is " + Math.abs(difference) + " Inches Below Road Surface";
        }
        else if(difference  >= 0) {   // Warning level
            if(difference == 0){
                msgToDisplay = "Water Level Is At Road Surface";
            }else {
                msgToDisplay = "Water Level Is " + Math.abs(difference) + " Inches Above Road Surface  ";
            }

        }
        return msgToDisplay;
    }

    public static int getStatusColor(int distance){
        if((distance == 1000 )| (distance >= -5 && distance < 0)){
         return Color.parseColor("#FBC02D");
        }
        if(distance < -5){
            return Color.parseColor("#4CAF50");
        }
        if(distance>= 0){
            return Color.parseColor("#B71C1C");
        }
        return Color.parseColor("#FBC02D");
    }
    public static Message generageObjectMessage(int distance){
        String statusMsg = generateStatusMessageWithOutTimeStamp(distance);
        Message message = new Message(statusMsg, getTimeStampInString());
        return null;
    }
    public static String getTimeStampInString(){
        DateFormat df = new SimpleDateFormat("h:mm:ss a");  // Creatinga  timestamp
        String timeMsg =  df.format(Calendar.getInstance().getTime());
        return timeMsg;
    }
    public static String getDateTimeStampInString(){
        DateFormat df = new SimpleDateFormat("dd-MM-yyy h:mm:ss a");  // Creatinga  timestamp
        String timeMsg =  df.format(Calendar.getInstance().getTime());
        return timeMsg;
    }
    public static String getDateTimeStampInStringForGivenDate(Date date){
        DateFormat df = new SimpleDateFormat("dd-MM-yyy h:mm:ss a");
        String timeMsg = df.format(date);
        return timeMsg;
    }

    public static String generateStatusMessageWithTimeStamp(String message){

     return( message + "\n" +getTimeStampInString());

    }
    public static List<EventItem> convertQueueToList(){
        List items = new ArrayList<>(DataHolder.getInstance().getEventItems());

        return items;
    }
}
