package com.arturofilio.instagramklone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import com.arturofilio.instagramklone.R;
import com.arturofilio.instagramklone.Share.ShareActivity;
import com.arturofilio.instagramklone.Utils.FirebaseMethods;
import com.arturofilio.instagramklone.Utils.UniversalImageLoader;
import com.arturofilio.instagramklone.dialogs.ConfirmPasswordDialog;
import com.arturofilio.instagramklone.models.User;
import com.arturofilio.instagramklone.models.UserAccountSettings;
import com.arturofilio.instagramklone.models.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by arturofiliovilla on 1/23/18.
 */

public class EditProfileFragment extends android.support.v4.app.Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener {

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials form the user for re-authentication . The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as Google Auth Provider or facebookAuthProvider.

        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password );

        //Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated");

                            //check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(
                                    new OnCompleteListener<ProviderQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                            if (task.isSuccessful()) {
                                                try {
                                                    if (task.getResult().getProviders().size() == 1) {
                                                        Log.d(TAG, "onComplete: that email is allready in user");
                                                        Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.d(TAG, "onComplete: That email is available ");

                                                        // the email is available so update it
                                                        mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "user email adrdress was updated");
                                                                            Toast.makeText(getActivity(), "Email was updated succesfully   ", Toast.LENGTH_SHORT).show();
                                                                            mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } catch (NullPointerException e) {
                                                    Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                                }
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }
                    }
                });
    }

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //EditProfile fragment widgets
    private EditText mDispalyName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    //variables
    private UserSettings mUserSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mDispalyName = (EditText) view.findViewById(R.id.display_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mWebsite = (EditText) view.findViewById(R.id.website);
        mDescription = (EditText) view.findViewById(R.id.description);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumner);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());

//       setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes");
                saveProfileSettings();
            }
        });
        return view;
    }

    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSettings() {
        final String displayName = mDispalyName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        //case1: if the user made a change to their username
        if(!mUserSettings.getUser().getUsername().equals(username)) {

            checkIfUsernameExists(username);

        }

        //case2; if the user made a change to their email
        if (!mUserSettings.getUser().getUsername().equals(email)) {
            //step 1 - Reauthenticate
            //          - Confirm the password and email

            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);

            //step 2 - Check if the email is already authenticated
            //          - Fetch providers for Email (stringEmail)
            //step 3 - chenge the email
            //          - submit the new email to the database and authenctication
        }

        /**
         *  change the rest of the settings that do not require uniqueness
         */
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }

        if(!mUserSettings.getSettings().getWebsite().equals(website)) {
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);

        }
        if(!mUserSettings.getSettings().getDescription().equals(description)) {
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);

        }
        if(!mUserSettings.getSettings().getProfile_photo().equals(phoneNumber)) {
            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);

        }

    }

    /**
     * Check if @param username already exists in the database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                
                if(!dataSnapshot.exists()) {
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings) {
//      Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving from firebase database: " + userSettings.toString());
//      Log.d(TAG, "setProfileWidgets: settings widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        mUserSettings = userSettings;
//      User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDispalyName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //26843565
                getActivity().startActivity(intent);
            }
        });
    }

    //-----------------------------------Firebase------------------------------

    // Set up the Firebase auth object

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: starting");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
