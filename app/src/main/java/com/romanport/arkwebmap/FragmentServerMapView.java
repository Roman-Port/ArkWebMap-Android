package com.romanport.arkwebmap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.romanport.arkwebmap.Parts.ArkMap.ArkMapJavascriptInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentServerMapView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentServerMapView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServerMapView extends Fragment {

    public String mWebViewUrl;

    private OnFragmentInteractionListener mListener;

    public FragmentServerMapView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 URL for the map to load, including hash data.
     * @return A new instance of fragment FragmentServerMapView.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentServerMapView newInstance(String param1) {
        FragmentServerMapView fragment = new FragmentServerMapView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onDinoClick(String apiUrl);
    }

    //Specific functions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_server_map_view, container, false);

        //Configure WebView
        WebView mapWebview = v.findViewById(R.id.map_webview);
        mapWebview.setBackground(getActivity().getDrawable(R.color.colorPrimary));
        mapWebview.getSettings().setJavaScriptEnabled(true);
        mapWebview.getSettings().setLoadWithOverviewMode(true);
        mapWebview.getSettings().setUseWideViewPort(true);
        mapWebview.addJavascriptInterface(new ArkMapJavascriptInterface(getContext(), this), "app");
        mapWebview.getSettings();

        //Load WebView
        mapWebview.loadUrl(mWebViewUrl);

        return v;
    }

    public void OnMapReady() {
        //Called by the JS client. Tells us to show the WebView.
        //We'll need to run this on the UI thread.
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                WebView wv = GetMapWebview();
                wv.setVisibility(View.VISIBLE);
            }
        });

    }

    public void OnDinoClicked(String url) {
        mListener.onDinoClick(url);
    }

    public WebView GetMapWebview() {
        return (WebView)getView().findViewById(R.id.map_webview);
    }
}
