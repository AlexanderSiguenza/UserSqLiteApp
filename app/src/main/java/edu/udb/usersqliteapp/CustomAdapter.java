package edu.udb.usersqliteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UserModel> userModelArrayList;

    public CustomAdapter(Context context, ArrayList<UserModel> userModelArrayList) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
    }

    @Override
    public int getCount() {
        return userModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvname = (TextView) convertView.findViewById(R.id.name);
            holder.tvhobby = (TextView) convertView.findViewById(R.id.hobby);
            holder.tvcity = (TextView) convertView.findViewById(R.id.city);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvname.setText("Name: "+userModelArrayList.get(position).getName());
        holder.tvhobby.setText("Hobby: "+userModelArrayList.get(position).getHobby());
        holder.tvcity.setText("City: "+userModelArrayList.get(position).getCity());
        return convertView;
    }

    private class ViewHolder {
        protected TextView tvname, tvhobby, tvcity;
    }

}