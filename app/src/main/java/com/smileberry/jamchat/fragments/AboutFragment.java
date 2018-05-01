package com.smileberry.jamchat.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smileberry.jamchat.R;

public class AboutFragment extends Fragment {

    private static final String EMAIL_ADDRESS = "smileberry.games@gmail.com";
    private static final String MAIL_TO_ACTION = "mailto";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView about = (TextView) rootView.findViewById(R.id.version);
        about.setText(buildSoftwareName(getActivity()));

        TextView contactView = (TextView) rootView.findViewById(R.id.contacts);
        contactView.setText(Html.fromHtml(getString(R.string.contactText)));
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        MAIL_TO_ACTION, EMAIL_ADDRESS, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        return rootView;
    }

    public static String buildSoftwareName(Context context) {
        return context.getString(R.string.app_name) + " " + getSoftwareVersion(context) + ", Smileberry";
    }


    /**
     * Gets the software version retrieved from the Manifest.
     */
    private static String getSoftwareVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //
        }
        return "";
    }

}
