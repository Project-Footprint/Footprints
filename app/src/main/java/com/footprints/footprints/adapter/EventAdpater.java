package com.footprints.footprints.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.models.Event;

import java.util.ArrayList;

public class EventAdpater extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventAdpater(@NonNull Context context, int resource) {
        super(context, resource);

    }

    public EventAdpater(ArrayList<Event> eventsLists, Context context, int item_custom_event) {
        super(context,item_custom_event,eventsLists);
        this.events= eventsLists;
        this.context = context;
    }
    // View lookup cache
    private static class ViewHolder {
        TextView eventName,eventPlace,eventStartDate,eventEndDate;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Event event = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_custom_event, parent, false);

            viewHolder.eventName = (TextView) convertView.findViewById(R.id.eventName);
            viewHolder.eventPlace = (TextView) convertView.findViewById(R.id.eventPlace);
            viewHolder.eventStartDate = (TextView) convertView.findViewById(R.id.eventStartDate);
            viewHolder.eventEndDate = (TextView) convertView.findViewById(R.id.eventEndDate);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        String eventName = event.getEname().replaceAll("\n","");
        String eventPlace = event.getEplace().replaceAll("\n","");
        viewHolder.eventName.setText(eventName);
        viewHolder.eventPlace.setText(eventPlace);
        viewHolder.eventStartDate.setText(event.getStartdate());
        viewHolder.eventEndDate.setText(event.getEnddate());

        return  result;
    }
}
