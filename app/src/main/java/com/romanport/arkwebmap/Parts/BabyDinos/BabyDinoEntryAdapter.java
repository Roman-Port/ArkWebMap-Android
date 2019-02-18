package com.romanport.arkwebmap.Parts.BabyDinos;

import android.app.Activity;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.romanport.arkwebmap.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import de.hdodenhof.circleimageview.CircleImageView;

public class BabyDinoEntryAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArkDinosReply[] dinos;

    public BabyDinoEntryAdapter(Activity context, ArkDinosReply[] dinos) {
        super(context, R.layout.ark_server_list_entry, new String[dinos.length]);

        this.context=context;
        this.dinos=dinos;
    }

    public View getView(int position, View view, ViewGroup parent) {
        //Get data
        ArkDinosReply data = dinos[position];

        //Continue as usual and create
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.baby_dino_entry, null,true);

        //Set options
        setDinoImage(rowView, data.dino_entry.icon_url); //Image
        setTextViewById(rowView, R.id.dinoBottomSheetName, data.dino.tamedName + " (Lvl "+data.dino.level+")"); //Name
        setTextViewById(rowView, R.id.dinoBottomSheetClassname, data.dino_entry.screen_name); //Classname

        View textStats = rowView.findViewById(R.id.textStats);
        setFormItem(textStats, R.id.timeUntilDepleted, getContext().getString(R.string.baby_dino_m_timetilfoodgone), "00:00:00");
        setFormItem(textStats, R.id.foodRemaining, getContext().getString(R.string.baby_dino_m_foodremaining), "00:00:00");

        View barStats = rowView.findViewById(R.id.barStats);
        setBarItem(barStats, R.id.imprintingBar, getContext().getString(R.string.baby_dino_m_imprinting), "Imprint ready!", 100, 75);
        setBarItem(barStats, R.id.maturationBar, getContext().getString(R.string.baby_dino_m_maturing), "94%", 100, 94);

        return rowView;

    };

    private static final float[] NEGATIVE = {
            -1.0f,     0,     0,    0, 255, // red
            0, -1.0f,     0,    0, 255, // green
            0,     0, -1.0f,    0, 255, // blue
            0,     0,     0, 1.0f,   0  // alpha
    }; //Inverts colors. Thanks to https://stackoverflow.com/questions/17841787/invert-colors-of-drawable-android

    void setDinoImage(View view, String url) {
        //Request dino image
        RequestCreator p = Picasso.get().load(url);
        ImageView v = (ImageView) view.findViewById(R.id.dinoBottomSheetImage);
        p.noFade();
        p.error(R.drawable.failed_to_load_image);
        p.into(v);
        v.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));
    }

    void setTextViewById(View view, int id, String text) {
        TextView object = view.findViewById(id);
        object.setText(text);
    }

    void setFormItem(View v, int id, String name, String data) {
        //Get the enclosing view
        View ev = v.findViewById(id);

        //Get and set
        ((TextView)ev.findViewById(R.id.itemTitle)).setText(name);
        ((TextView)ev.findViewById(R.id.itemSub)).setText(data);
    }

    void setBarItem(View v, int id, String name, String sub, int max, int progress) {
        //Get enclosing view
        View ev = v.findViewById(id);

        //Get and set text
        ((TextView)ev.findViewById(R.id.itemTitle)).setText(name);
        ((TextView)ev.findViewById(R.id.itemSub)).setText(sub);

        //Grab status bar and set values
        ProgressBar bar = ev.findViewById(R.id.itemProgressbar);
        bar.setMax(max);
        bar.setProgress(progress);

    }
}
