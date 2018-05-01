package com.smileberry.jamchat.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.smileberry.jamchat.R;
import com.smileberry.jamchat.utils.Preferences;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        CheckBox showTrafficCheckBox = (CheckBox) rootView.findViewById(R.id.showTraffic);
        showTrafficCheckBox.setChecked(Preferences.isShowTraffic(getActivity()));

        initHideMeRangeRadioGroup(rootView);

        return rootView;
    }

    private void initHideMeRangeRadioGroup(View rootView) {
        RadioGroup hideMeRangeRadioGroup = (RadioGroup) rootView.findViewById(R.id.hideMeRangeRadioGroup);
        String selectedHideMeRangeRadio = Preferences.getHideMeRange(getActivity());
        if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.DONT_HIDE))) {
            hideMeRangeRadioGroup.check(R.id.radioDontHide);
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_50))) {
            hideMeRangeRadioGroup.check(R.id.radioHide50);
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_100))) {
            hideMeRangeRadioGroup.check(R.id.radioHide100);
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_500))) {
            hideMeRangeRadioGroup.check(R.id.radioHide500);
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_1000))) {
            hideMeRangeRadioGroup.check(R.id.radioHide1000);
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_TRAVEL_WORLD))) {
            hideMeRangeRadioGroup.check(R.id.radioHideTravelTheWorld);
        }
    }
}
