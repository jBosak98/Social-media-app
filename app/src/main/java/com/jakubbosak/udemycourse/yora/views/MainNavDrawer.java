package com.jakubbosak.udemycourse.yora.views;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.activities.BaseActivity;
import com.jakubbosak.udemycourse.yora.activities.ContactsActivity;
import com.jakubbosak.udemycourse.yora.activities.MainActivity;
import com.jakubbosak.udemycourse.yora.activities.ProfileActivity;
import com.jakubbosak.udemycourse.yora.activities.SentMessagesActivity;
import com.jakubbosak.udemycourse.yora.infrastructure.User;
import com.jakubbosak.udemycourse.yora.infrastructure.YoraApplication;
import com.jakubbosak.udemycourse.yora.services.Account;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainNavDrawer extends NavDrawer{
    private final TextView displayNameText;
    private final ImageView avatarImage;

    public MainNavDrawer(final BaseActivity activity){
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class,"Inbox", null, R.drawable.ic_action_unread,R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(SentMessagesActivity.class, "Sent Messages", null, R.drawable.ic_action_send_now,R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(ContactsActivity.class,"Contacts",null,R.drawable.ic_action_group,R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityNavDrawerItem(ProfileActivity.class,"Profile", null, R.drawable.ic_action_person,R.id.include_main_nav_drawer_topItems));

        addItem(new BasicNavDrawerItem("Logout",null,R.drawable.ic_action_backspace,R.id.include_main_nav_drawer_bottomItems){
            @Override
            public void onClick(View view){
                activity.getYoraApplication().getAuth().logout();

                Toast.makeText(activity,"You have logged out!", Toast.LENGTH_LONG).show();
            }
        });
        displayNameText = (TextView) navDraverView.findViewById(R.id.include_main_nav_drawer_displayName);
        avatarImage = (ImageView) navDraverView.findViewById(R.id.include_main_nav_drawer_avatar);

        User loggedInUser = activity.getYoraApplication().getAuth().getUser();
        displayNameText.setText(loggedInUser.getDisplayName());

        //todo: change avatarImage to avatarUrl from loggedInUser
    }

    @Subscribe
    public void onUserDetailsUpdated(Account.UserDetailsUpdatedEvent event){
        //todo: update avatar url
        displayNameText.setText(event.user.getDisplayName());
    }
}
