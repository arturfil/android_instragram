package com.arturofilio.instagramklone.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arturofilio.instagramklone.R;
import com.arturofilio.instagramklone.Utils.UniversalImageLoader;

/**
 * Created by arturofiliovilla on 1/23/18.
 */

public class EditProfileFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "EditProfileFragment";

    private ImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);

        setProfileImage();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        return view;
    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: setting profile Image.");
        String imgUrl = "https://vignette.wikia.nocookie.net/megajump/images/2/27/Android_Robot.png/revision/latest?cb=20110822221600";
        UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null, "");
    }
}
