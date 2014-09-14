package com.t3coode.togg.activities.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.adapters.MenuAdapter.MenuItem;

public class MenuAdapter extends ArrayAdapter<MenuItem> {

    private static final int mLayout = R.layout.layout_menu_simple_item;

    private List<MenuItem> mList;
    private Context mContext;

    public MenuAdapter(Context context, List<MenuItem> objects) {
        super(context, mLayout, objects);

        this.mList = objects;
        this.mContext = context;
    }

    private static class Holder {
        public TextView textTV;
        public ImageView logoIV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayout,
                    parent, false);

            holder = new Holder();

            holder.textTV = (TextView) convertView.findViewById(R.id.text);
            holder.logoIV = (ImageView) convertView.findViewById(R.id.logo);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        MenuItem menuItem = mList.get(position);

        holder.textTV.setText(menuItem.getTextId());
        holder.logoIV.setImageResource(menuItem.logoId);
        convertView.setId(menuItem.getId());

        return convertView;
    }

    public static class MenuItem {
        private int logoId;
        private int textId;
        private int id;
        private boolean selected;

        public MenuItem(int logoId, int textId, int id) {
            super();
            this.logoId = logoId;
            this.textId = textId;
            this.id = id;
        }

        public int getLogoId() {
            return logoId;
        }

        public void setLogoId(int logoId) {
            this.logoId = logoId;
        }

        public int getTextId() {
            return textId;
        }

        public void setTextId(int textId) {
            this.textId = textId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }
}
