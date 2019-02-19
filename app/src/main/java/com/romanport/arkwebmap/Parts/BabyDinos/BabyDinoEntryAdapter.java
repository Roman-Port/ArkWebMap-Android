package com.romanport.arkwebmap.Parts.BabyDinos;

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
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.romanport.arkwebmap.Parts.MainViewFragmentInterface;
import com.romanport.arkwebmap.R;
import com.romanport.arkwebmap.TimeTool;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class BabyDinoEntryAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArkDinosReply[] dinos;
    private final MainViewFragmentInterface mInterface;

    public BabyDinoEntryAdapter(Activity context, ArkDinosReply[] dinos, MainViewFragmentInterface mInterface) {
        super(context, R.layout.ark_server_list_entry, new String[dinos.length]);

        this.context=context;
        this.dinos=dinos;
        this.mInterface = mInterface;
    }

    public View getView(int position, View view, ViewGroup parent) {
        //Continue as usual and create
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.baby_dino_entry, null,true);

        UpdateUI(position, rowView);

        return rowView;

    }

    void UpdateUI(int position, View rowView) {
        //Calculate stats
        ArkDinosReply data = dinos[position];
        BabyDinoInfoPackage stats = BabyDinoStatsCalculator.GetFullDinoInfo(data, mInterface.GetServerTimeSinceUpdate());
        int maturingAmount = Math.round(data.dino.babyAge*100);
        TimeTool timeUntilFoodEmpty = new TimeTool(stats.timeToDepletionMs);

        //Set options
        setDinoImage(rowView, data.dino_entry.icon_url); //Image
        setTextViewById(rowView, R.id.dinoBottomSheetName, data.dino.tamedName + " (Lvl "+data.dino.level+")"); //Name
        setTextViewById(rowView, R.id.dinoBottomSheetClassname, data.dino_entry.screen_name); //Classname

        View textStats = rowView.findViewById(R.id.textStats);
        setFormItem(textStats, R.id.timeUntilDepleted, getContext().getString(R.string.baby_dino_m_timetilfoodgone), timeUntilFoodEmpty.ToTimeString());
        setFormItem(textStats, R.id.foodRemaining, getContext().getString(R.string.baby_dino_m_foodremaining), RoundToString(stats.totalCurrentFood, 2));

        View barStats = rowView.findViewById(R.id.barStats);
        setBarItem(barStats, R.id.imprintingBar, getContext().getString(R.string.baby_dino_m_imprinting), "Imprint ready!", 100, 75);
        setBarItem(barStats, R.id.maturationBar, getContext().getString(R.string.baby_dino_m_maturing), maturingAmount+"%", 100, maturingAmount);
    }

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

    public static String RoundToString(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return Double.toString(bd.doubleValue());
    }

}
