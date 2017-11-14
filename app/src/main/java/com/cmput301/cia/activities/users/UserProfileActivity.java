/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.activities.users;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmput301.cia.R;
import com.cmput301.cia.models.Profile;
import com.cmput301.cia.utilities.DateUtilities;
import com.cmput301.cia.utilities.ImageUtilities;

import java.io.IOException;
import java.io.InputStream;

import static com.cmput301.cia.activities.events.CreateHabitEventActivity.MAX_IMAGE_SIZE;

/**
 * Version 2
 * Authors: Adil Malik, Shipin Guan
 * Date: Nov 13 2017
 *
 * This activity displays the information about a user's profile
 */

// TODO: following/unfollowing, messages

public class UserProfileActivity extends AppCompatActivity {

    public static final String PROFILE_ID = "Profile", USER_ID = "User";
    public static final String RESULT_PROFILE_ID = "Profile";

    // Result code for selecting an image from gallery
    public static final int SELECT_IMAGE_CODE = 1;

    // the profile being displayed
    private Profile profile;
    // the currently signed in user
    private Profile user;

    private Button followButton;
    private Button unfollowButton;
    private Button saveButton;

    // the profile's name
    private TextView nameText;

    // the profile's comment
    private EditText commentText;

    // the profile's photo
    private ImageView imageView;

    // the image attached to the viewed profile
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();

        profile = (Profile) intent.getSerializableExtra(PROFILE_ID);
        user = (Profile) intent.getSerializableExtra(USER_ID);

        // initialize view member variables
        nameText = (TextView)findViewById(R.id.profileNameText);
        commentText = (EditText)findViewById(R.id.profileCommentDynamicText);
        followButton = (Button)findViewById(R.id.profileFollowButton);
        unfollowButton = (Button)findViewById(R.id.profileUnfollowButton);
        saveButton = (Button)findViewById(R.id.profileSaveButton);
        imageView = (ImageView)findViewById(R.id.profileImageView);

        // if user is viewing their own profile
        if (user.equals(profile)){
            followButton.setVisibility(View.INVISIBLE);
            unfollowButton.setVisibility(View.INVISIBLE);
        } else {
            saveButton.setText("Return");
            //saveButton.setVisibility(View.INVISIBLE);
            commentText.setEnabled(false);
            imageView.setClickable(false);

            if (user.isFollowing(profile))
                followButton.setVisibility(View.INVISIBLE);
            else
                unfollowButton.setVisibility(View.INVISIBLE);
        }

        commentText.setText(profile.getComment());
        nameText.setText(profile.getName());

        ((TextView)findViewById(R.id.profileDateDynamicText)).setText(DateUtilities.formatDate(profile.getCreationDate()));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                // return the viewer
                if (!user.equals(profile))
                    intent.putExtra(RESULT_PROFILE_ID, user);
                else {
                    // modify and return the user's profile
                    profile.setComment(commentText.getText().toString());
                    if (image != null)
                        profile.setImage(ImageUtilities.imageToBase64(image));

                    intent.putExtra(RESULT_PROFILE_ID, profile);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.addFollowRequest(user);
            }
        });

        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.unfollow(profile);
                unfollowButton.setVisibility(View.INVISIBLE);
                followButton.setVisibility(View.VISIBLE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // only the user can change their profile's image
                if (!profile.equals(user))
                    return;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE_CODE);
            }
        });

        if (profile.getImage() == null || profile.getImage().equals(""))
            image = null;
        else
            image = ImageUtilities.base64ToImage(profile.getImage());
        updateImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle the results of the image selection activity
     * @param requestCode id of the finished activity
     * @param resultCode code representing whether that activity was successful or not
     * @param data the data returned from that activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap chosenImage = BitmapFactory.decodeStream(inputStream);

                // attempt to resize the image if necessary
                chosenImage = ImageUtilities.compressImageToMax(chosenImage, MAX_IMAGE_SIZE);

                if (chosenImage == null) {
                    image = null;
                    updateImage();
                } else if (chosenImage.getByteCount() <= MAX_IMAGE_SIZE) {
                    image = chosenImage;
                    updateImage();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the image view after an image has been selected or removed
     */
    private void updateImage(){
        if (image != null) {
            imageView.clearColorFilter();
            imageView.setBackgroundColor(Color.rgb(255, 255, 255));
            imageView.setImageBitmap(image);
        }
        else {
            imageView.setColorFilter(Color.rgb(0, 0, 0));
            imageView.setBackgroundColor(Color.rgb(0, 0, 0));
        }
    }

}