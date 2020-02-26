package com.indra.indra.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.indra.indra.R;
import com.indra.indra.objects.RemoteConfig;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

public class RemoteConfigListAdapter extends ArrayAdapter<RemoteConfig> {

    private LayoutInflater mInflater;
    private List<RemoteConfig> mContacts = null;
    private ArrayList<RemoteConfig> arrayList; //used for the search bar
    private int layoutResource;
    private Context mContext;
    private String mAppend;

    public RemoteConfigListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<RemoteConfig> contacts, String append) {
        super(context, resource, contacts);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        mAppend = append;
        this.mContacts = contacts;
        arrayList = new ArrayList<>();
        this.arrayList.addAll(mContacts);
    }

    private static class ViewHolder {
        TextView name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
         ************ ViewHolder Build Pattern Start ************
         */
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.name =  convertView.findViewById(R.id.contactName);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }


        String name_ = getItem(position).getName();
        holder.name.setText(name_);

        return convertView;
    }

    // filter name in Search Bar
    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        mContacts.clear();
        if (characterText.length() == 0) {
            mContacts.addAll(arrayList);
        } else {
            mContacts.clear();
            for (RemoteConfig contact: arrayList) {
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    mContacts.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

}
