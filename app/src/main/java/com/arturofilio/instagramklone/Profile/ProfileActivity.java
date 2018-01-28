package com.arturofilio.instagramklone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.arturofilio.instagramklone.R;
import com.arturofilio.instagramklone.Utils.BottomNavigationViewHelper;
import com.arturofilio.instagramklone.Utils.GridImageAdapter;
import com.arturofilio.instagramklone.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by arturofiliovilla on 1/17/18.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
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
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);

        setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();
        setProfileImage();
        tempGridSetup();
    }

    private void tempGridSetup() {
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("http://getwallpapers.com/wallpaper/full/f/5/0/679699.jpg");
        imgURLs.add("https://s5.favim.com/orig/51/amazing-beauty-cool-images-landscapes-Favim.com-549267.jpg");
        imgURLs.add("https://wallup.net/wp-content/uploads/2017/03/29/478082-winter-nature-water-snow-748x421.jpg");
        imgURLs.add("http://www.chinastarholiday.com/wp-content/uploads/2017/01/Cambodia-Extension.jpg");
        imgURLs.add("http://miriadna.com/desctopwalls/images/max/Piazza-di-San-Marco-(Venezia).jpg");
        imgURLs.add("https://media.cntraveler.com/photos/54f0b943560f0bf2218ea181/master/pass/paris-eiffel-tower-carin-olsson-2048.jpg");
        imgURLs.add("https://www.barcelona-tourist-guide.com/images/int/attractions/casa-batllo/L/CasaBatllo-4.jpg");
        imgURLs.add("https://imagesvc.timeincapp.com/v3/mm/image?url=http%3A%2F%2Fcdn-image.travelandleisure.com%2Fsites%2Fdefault%2Ffiles%2Fstyles%2F1600x1000%2Fpublic%2Fistanbul-turkey0616.jpg%3Fitok%3DJCyAzlRN&w=700&q=85");
        imgURLs.add("https://img.purch.com/w/640/aHR0cDovL3d3dy5saXZlc2NpZW5jZS5jb20vaW1hZ2VzL2kvMDAwLzA1My83MTgvaTAyL2FidS1zaW1iZWwuanBnPzEzNzA5OTE2Mjk=");
        imgURLs.add("https://img.purch.com/h/1000/aHR0cDovL3d3dy5saXZlc2NpZW5jZS5jb20vaW1hZ2VzL2kvMDAwLzAzMy85OTkvb3JpZ2luYWwvc2h1dHRlcnN0b2NrXzU4MDQxNy5qcGc=");
        imgURLs.add("https://images.iacpublishinglabs.com/reference-production-images/question/aq/1400px-788px/buddhist-temple-called_5eb8a7fb24c15531.jpg");
        imgURLs.add("https://img-aws.ehowcdn.com/750x428p/photos.demandstudios.com/getty/article/83/64/55845161.jpg");

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgUrls){
        GridView gridView = (GridView) findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgUrls);
        gridView.setAdapter(adapter);
    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: setting profile photo.");
        String imgUrl = "https://vignette.wikia.nocookie.net/megajump/images/2/27/Android_Robot.png/revision/latest?cb=20110822221600";
        UniversalImageLoader.setImage(imgUrl, profilePhoto, mProgressBar, "");
    }

    private  void setupActivityWidgets() {
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext, AccountsSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
    BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavigationViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx );
        BottomNavigationViewHelper.enableNavigation( mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
