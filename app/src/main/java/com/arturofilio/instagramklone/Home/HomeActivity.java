package com.arturofilio.instagramklone.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.arturofilio.instagramklone.Login.LoginActivity;
import com.arturofilio.instagramklone.R;
import com.arturofilio.instagramklone.Utils.BottomNavigationViewHelper;
import com.arturofilio.instagramklone.Utils.SectionsPagerAdapter;
import com.arturofilio.instagramklone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting");

        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Resonsible for adding the 3 tabs: Camera, Home, Messasges
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.d(TAG, "About to setup topbar");
        adapter.addFragment(new CameraFragment());   // index 0
        Log.d(TAG, "Check Camera");
        adapter.addFragment(new HomeFragment());     // index 1
        Log.d(TAG, "Check Home");
        adapter.addFragment(new MessagesFragment()); // index 2
        Log.d(TAG, "Check Messages");

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }

    /**
     * BottomNavigationView
     */

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavigationViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx );
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //-----------------------------------Firebase------------------------------

    //checks to see if the @param 'user is logged in

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: cheking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    // Set up the Firebase auth object

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: starting");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null ) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
