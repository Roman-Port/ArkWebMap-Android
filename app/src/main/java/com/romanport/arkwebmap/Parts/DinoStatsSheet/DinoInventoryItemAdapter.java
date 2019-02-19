package com.romanport.arkwebmap.Parts.DinoStatsSheet;

import android.app.Activity;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romanport.arkwebmap.BabyDinoStatsCalculator;
import com.romanport.arkwebmap.MainActivity;
import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.NetEntities.Dinos.BabyDinoInfoPackage;
import com.romanport.arkwebmap.NetEntities.Items.ArkItem;
import com.romanport.arkwebmap.NetEntities.Items.ArkItemEntry;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.romanport.arkwebmap.Parts.MainViewFragmentInterface;
import com.romanport.arkwebmap.R;
import com.romanport.arkwebmap.TimeTool;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class DinoInventoryItemAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArkDinosReply dino;

    public DinoInventoryItemAdapter(Activity context, ArkDinosReply dino) {
        super(context, R.layout.ark_server_list_entry, new String[dino.inventory_items.length]);

        this.context=context;
        this.dino=dino;
    }

    public View getView(int position, View view, ViewGroup parent) {
        //Continue as usual and create
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.dino_stats_inventory_item, null,true);

        //Grab data
        ArkItem item = dino.inventory_items[position];
        ArkItemEntry itemEntry = dino.item_class_data.get(item.classnameString);

        //Set info

        setImage(rowView, itemEntry.icon.icon_url); //Image
        setTextViewById(rowView, R.id.itemAmount, item.stackSize+"x");
        setTextViewById(rowView, R.id.itemWeight, new DecimalFormat("#.0").format(item.stackSize * itemEntry.baseItemWeight));

        return rowView;

    }

    void setImage(View view, String url) {
        //Request dino image
        RequestCreator p = Picasso.get().load(url);
        ImageView v = (ImageView) view.findViewById(R.id.itemIcon);
        p.noFade();
        p.error(R.drawable.failed_to_load_image);
        p.into(v);
    }

    void setTextViewById(View view, int id, String text) {
        TextView object = view.findViewById(id);
        object.setText(text);
    }

}
