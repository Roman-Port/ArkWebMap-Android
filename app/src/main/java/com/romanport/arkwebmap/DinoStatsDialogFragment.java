package com.romanport.arkwebmap;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.w3c.dom.Text;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     DinoStatsDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link DinoStatsDialogFragment.Listener}.</p>
 */
public class DinoStatsDialogFragment extends BottomSheetDialogFragment {

    private Listener mListener;

    public ArkDinosReply dinoData;

    public static DinoStatsDialogFragment newInstance(ArkDinosReply dinoData) {
        final DinoStatsDialogFragment fragment = new DinoStatsDialogFragment();
        fragment.dinoData = dinoData;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dino_list_dialog, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onDinoModalButtonClicked(int position);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        //Show
        onGotDinoData(view, dinoData);

    }

    public void onGotDinoData(final View view, final ArkDinosReply data) {
        //Set data
        setDinoImage(view, data.dino_entry.icon_url); //Image
        setTextViewById(view, R.id.dinoBottomSheetName, data.dino.tamedName); //Name
        setTextViewById(view, R.id.dinoBottomSheetClassname, data.dino_entry.screen_name); //Classname
        setProgressBarById(view, R.id.dinoBottomSheetStatHealth, data.max_stats.health, data.dino.currentStats.health, "Health", getActivity().getDrawable(R.drawable.arkstatusicon_health));
        setProgressBarById(view, R.id.dinoBottomSheetStatStamina, data.max_stats.stamina, data.dino.currentStats.stamina, "Stamina", getActivity().getDrawable(R.drawable.arkstatusicon_stamina));
        setProgressBarById(view, R.id.dinoBottomSheetStatWeight, data.max_stats.inventoryWeight, data.dino.currentStats.inventoryWeight, "Weight", getActivity().getDrawable(R.drawable.arkstatusicon_inventoryweight));
        setProgressBarById(view, R.id.dinoBottomSheetStatFood, data.max_stats.food, data.dino.currentStats.food, "Food", getActivity().getDrawable(R.drawable.arkstatusicon_food));
    }

    private static final float[] NEGATIVE = {
            -1.0f,     0,     0,    0, 255, // red
            0, -1.0f,     0,    0, 255, // green
            0,     0, -1.0f,    0, 255, // blue
            0,     0,     0, 1.0f,   0  // alpha
    }; //Inverts colors. Thanks to https://stackoverflow.com/questions/17841787/invert-colors-of-drawable-android

    public void setDinoImage(View view, String url) {
        //Request dino image
        RequestCreator p = Picasso.get().load(url);
        ImageView v = (ImageView) view.findViewById(R.id.dinoBottomSheetImage);
        p.noFade();
        p.error(R.drawable.failed_to_load_image);
        p.into(v);
        v.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));
    }

    public void setTextViewById(View view, int id, String text) {
        TextView object = view.findViewById(id);
        object.setText(text);
    }

    public void setProgressBarById(View view, int id, float max, float current, String name, Drawable icon) {
        //Grab container
        View container = view.findViewById(id);

        //Convert max and current to integers
        int maxInt = Math.round(max);
        int currentInt = Math.round(current);

        //Inside of the container, grab the elements we need
        ProgressBar barElement = container.findViewById(R.id.dino_stat_progressbar);
        TextView nameElement = container.findViewById(R.id.dino_stat_progressbar_name);
        TextView amountElemnt = container.findViewById(R.id.dino_stat_progressbar_amount);
        ImageView iconElement = container.findViewById(R.id.dino_stat_progressbar_image);

        //Set data
        barElement.setMax(maxInt);
        barElement.setProgress(currentInt);
        nameElement.setText(name);
        iconElement.setImageDrawable(icon);
        amountElemnt.setText(currentInt+" / "+maxInt);
    }
}
