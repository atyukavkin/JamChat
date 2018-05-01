package com.smileberry.jamchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smileberry.jamchat.R;

import java.util.List;

public class DrawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private List<String> items;
    private Context context;
    private int icons[] = {R.drawable.ic_settings, R.drawable.ic_info};

    public DrawerAdapter(Context context, List<String> items) {
        super(context, R.layout.drawer_list_item, items);
        inflater = (LayoutInflater) (getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.item);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(items.get(position));
        viewHolder.icon.setImageDrawable(context.getResources().getDrawable(icons[position]));

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
