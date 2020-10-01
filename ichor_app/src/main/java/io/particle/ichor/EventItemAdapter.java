package io.particle.hydroalert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.particle.hydroalert.util.CommonUtil;



public class EventItemAdapter extends ArrayAdapter<EventItem> {


    public void setItems(List<EventItem> items) {
        Collections.reverse(items);
        this.items = items;
        notifyDataSetChanged();
    }

    private List<EventItem> items;

//DataHolder.getInstance().getEventItems().addAll(eventItems);
 public EventItemAdapter(Context context, List<EventItem> eventItems){
        super(context, 0, eventItems);
     this.items = eventItems;




    }

    public void udpateList()
    {
        items.clear();
       items = CommonUtil.convertQueueToList();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;
        if(currentView == null){
           currentView =  LayoutInflater.from(getContext()).inflate(R.layout.list_event_item, parent, false);
        }
        LinearLayout mLayout = (LinearLayout)currentView.findViewById(R.id.event_list_item);

        EventItem mItem = (EventItem)items.get(position);
        TextView eventMessage = (TextView)currentView.findViewById(R.id.event_item_message);
        TextView eventTime = (TextView)currentView.findViewById(R.id.event_item_time);
        eventMessage.setText(CommonUtil.generateStatusMessageWithOutTimeStamp(mItem.getMessage()));
        eventTime.setText(CommonUtil.getDateTimeStampInStringForGivenDate(mItem.getMessageTime()));
        //mLayout.setBackgroundColor(CommonUtil.getStatusColor(mItem.getMessage()));
        return currentView;
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
