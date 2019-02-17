package com.romanport.arkwebmap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentServerSearchInventoriesView.FragmentServerSearchInventoriesViewInterface} interface
 * to handle interaction events.
 * Use the {@link FragmentServerSearchInventoriesView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServerSearchInventoriesView extends Fragment {

    private FragmentServerSearchInventoriesViewInterface mListener;

    private EditText inputBox;

    public FragmentServerSearchInventoriesView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentServerSearchInventoriesView.
     */
    public static FragmentServerSearchInventoriesView newInstance() {
        FragmentServerSearchInventoriesView fragment = new FragmentServerSearchInventoriesView();
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
        View v = inflater.inflate(R.layout.fragment_server_search_inventories_view, container, false);

        //Open keyboard on text input
        inputBox = v.findViewById(R.id.itemSearchInput);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputBox, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentServerSearchInventoriesViewInterface) {
            mListener = (FragmentServerSearchInventoriesViewInterface) context;
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

    public interface FragmentServerSearchInventoriesViewInterface {
        void onDinoClick(String dinoUrl);
    }
}
