package com.romanport.arkwebmap.Parts.BabyDinos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanport.arkwebmap.Parts.MainViewFragmentInterface;
import com.romanport.arkwebmap.R;

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
        return inflater.inflate(R.layout.fragment_server_baby_dinos, container, false);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
