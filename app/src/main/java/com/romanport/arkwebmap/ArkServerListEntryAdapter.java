package com.romanport.arkwebmap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArkServerListEntryAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final UsersMeServer[] servers;

    public ArkServerListEntryAdapter(Activity context, UsersMeServer[] servers) {
        super(context, R.layout.ark_server_list_entry, new String[servers.length]);

        this.context=context;
        this.servers=servers;
    }

    public View getView(int position,View view,ViewGroup parent) {
        //Get data
        UsersMeServer data = servers[position];

        //Continue as usual and create
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.ark_server_list_entry, null,true);

        //Add image
        RequestCreator p = Picasso.get().load(data.image_url);
        p.noFade();
        p.error(R.drawable.failed_to_load_image);
        CircleImageView t = (CircleImageView)rowView.findViewById(R.id.server_entry_icon);
        p.into(t);

        return rowView;

    };

}
