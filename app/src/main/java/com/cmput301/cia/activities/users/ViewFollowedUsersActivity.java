/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.activities.users;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cmput301.cia.R;
import com.cmput301.cia.activities.events.CreateHabitEventActivity;
import com.cmput301.cia.models.AddHabitEvent;
import com.cmput301.cia.models.ElasticSearchable;
import com.cmput301.cia.models.Follow;
import com.cmput301.cia.models.HabitEvent;
import com.cmput301.cia.models.OfflineEvent;
import com.cmput301.cia.models.Profile;
import com.cmput301.cia.utilities.ElasticSearchUtilities;

import java.io.Serializable;
import java.util.List;

/**
 * @author Adil Malik
 * @version 1
 * Date: Nov 13 2017
 *
 * This activity displays all of the people a user is following
 */

public class ViewFollowedUsersActivity extends AppCompatActivity {

    // Intent identifiers for passed in profiles
    public static final String ID_VIEWED = "Profile", ID_USER = "User";

    // Return identifiers for the activity result
    public static final String RETURNED_FOLLOWED = "Followed", RETURNED_ISUSER = "User";

    // Activity result codes
    private static final int VIEW_PROFILE = 0;

    // the profile being displayed
    private Profile profile;
    // the currently signed in user
    private Profile user;

    // list of followed users
    private ListView followedList;
    private ArrayAdapter<Profile> listAdapter;
    private List<Profile> followed;
    private List<String> followingIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followed_users);

        Intent intent = getIntent();
        profile = (Profile) intent.getSerializableExtra(ID_VIEWED);
        user = (Profile) intent.getSerializableExtra(ID_USER);

        followedList = (ListView)findViewById(R.id.vfuList);

        followingIds = Follow.getFollowing(profile.getId());
        followed = ElasticSearchUtilities.getListOf(Profile.TYPE_ID, Profile.class, followingIds);
        listAdapter = new ArrayAdapter<>(this, R.layout.list_item, followed);
        followedList.setAdapter(listAdapter);

        // view a profile when it is clicked
        followedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent profileIntent = new Intent(ViewFollowedUsersActivity.this, UserProfileActivity.class);
                profileIntent.putExtra(UserProfileActivity.PROFILE_ID, followed.get(position));
                profileIntent.putExtra(UserProfileActivity.USER_ID, user);
                startActivityForResult(profileIntent, VIEW_PROFILE);
            }
        });
    }

    /**
     * Handle the back button being pressed
     */
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        // TODO: test if profile.getFollowing() works as serializable. if not then just return profile
        returnIntent.putExtra(RETURNED_FOLLOWED, (Serializable)followingIds);
        returnIntent.putExtra(RETURNED_ISUSER, profile.equals(user));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        followingIds = Follow.getFollowing(profile.getId());
        followed = ElasticSearchUtilities.getListOf(Profile.TYPE_ID, Profile.class, followingIds);
        listAdapter.clear();
        listAdapter.addAll(followed);
        listAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        followed = ElasticSearchUtilities.getListOf(Profile.TYPE_ID, Profile.class, followingIds);
//        listAdapter = new ArrayAdapter<>(this, R.layout.list_item, followed);
//        followedList.setAdapter(listAdapter);
//    }

    /**
     * Handle the results of an activity that has finished
     * @param requestCode the activity's identifying code
     * @param resultCode the result status of the finished activity
     * @param data the activity's returned intent information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // When a new habit event is created
        if (requestCode == VIEW_PROFILE) {
            if (resultCode == RESULT_OK) {
                Profile newProfile = (Profile) data.getSerializableExtra(UserProfileActivity.RESULT_PROFILE_ID);
                profile.copyFrom(newProfile);
            }
        }
    }
}
