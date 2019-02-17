package com.romanport.arkwebmap.Parts.ArkMap;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.romanport.arkwebmap.FragmentServerMapView;

public class ArkMapJavascriptInterface {
    Context mContext;
    FragmentServerMapView mFrag;

    public ArkMapJavascriptInterface(Context c, FragmentServerMapView f) {
        mContext = c;
        mFrag = f;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void onMapReady() {
        //Tell the fragment we are ready.
        mFrag.OnMapReady();
    }

    @JavascriptInterface
    public void onDinoClicked(String url) {
        //Dino clicked. Pass it onto the native area.
        mFrag.OnDinoClicked(url);
    }
}
