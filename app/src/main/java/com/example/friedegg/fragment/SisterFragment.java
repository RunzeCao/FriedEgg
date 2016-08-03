package com.example.friedegg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.friedegg.modul.Picture;

public class SisterFragment extends PictureFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mType = Picture.PictureType.Sister;
    }
}
