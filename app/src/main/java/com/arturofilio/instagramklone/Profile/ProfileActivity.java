package com.arturofilio.instagramklone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arturofilio.instagramklone.R;
import com.arturofilio.instagramklone.Utils.ViewCommentsFragment;
import com.arturofilio.instagramklone.Utils.ViewPostFragment;
import com.arturofilio.instagramklone.Utils.ViewProfileFragment;
import com.arturofilio.instagramklone.models.Photo;

/**
 * Created by arturofiliovilla on 1/17/18.
 */

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener {

    private static final String TAG = "ProfileActivity";

    @Override
    public void onCommentThreadListener(Photo photo) {
        Log.d(TAG, "onCommentThreadListener: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private Context mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");

        init();
    }

    private void init() {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.calling_activity))) {

                Log.d(TAG, "init: inflating view profile");
                ViewProfileFragment fragment = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.intent_user),
                        intent.getParcelableExtra(getString(R.string.intent_user)));
                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();

            } else {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else {

            Log.d(TAG, "init: inflating Profile");

            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();

        }


    }

}
