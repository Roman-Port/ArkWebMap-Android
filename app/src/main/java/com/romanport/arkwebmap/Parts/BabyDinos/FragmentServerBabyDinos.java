package com.romanport.arkwebmap.Parts.BabyDinos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.Parts.Hub.ArkHubServerListAdapter;
import com.romanport.arkwebmap.Parts.MainViewFragmentInterface;
import com.romanport.arkwebmap.R;
import com.romanport.arkwebmap.WebUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainViewFragmentInterface} interface
 * to handle interaction events.
 * Use the {@link FragmentServerBabyDinos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServerBabyDinos extends Fragment {

    private MainViewFragmentInterface mListener;
    public ArkDinosReply[] dinos;

    public FragmentServerBabyDinos() {
        // Required empty public constructor
    }

    public static FragmentServerBabyDinos newInstance() {
        FragmentServerBabyDinos fragment = new FragmentServerBabyDinos();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_server_baby_dinos, container, false);

        //Add list of dinos
        ListView list = v.findViewById(R.id.baby_dino_list);
        BabyDinoEntryAdapter adapter = new BabyDinoEntryAdapter(this.getActivity(), dinos, mListener);
        list.setAdapter(adapter);
        //TODO: Update this on a timer

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainViewFragmentInterface) {
            mListener = (MainViewFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        //Grab dinos using the interface
        dinos = mListener.GetTribe().baby_dinos;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
