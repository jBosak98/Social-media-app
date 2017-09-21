package com.jakubbosak.udemycourse.yora.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.services.Contacts;
import com.jakubbosak.udemycourse.yora.services.Messages;
import com.jakubbosak.udemycourse.yora.services.entities.Message;
import com.jakubbosak.udemycourse.yora.services.entities.UserDetails;
import com.jakubbosak.udemycourse.yora.views.MainNavDrawer;
import com.jakubbosak.udemycourse.yora.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class ContactActivity extends BaseAunthenticatedActivity implements MessagesAdapter.OnMessageClickedListener {
    public static final String EXTRA_USER_DETAILS = "EXTRA_USER_DETAILS";
    public static final int RESULT_USER_REMOVED = 101;

    private static final int REQUEST_SEND_MESSAGE = 1;

    private UserDetails userDetails;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;

    @Override
    protected void onYoraCreate(Bundle savedState) {
        setContentView(R.layout.activity_contact);

        progressFrame = findViewById(R.id.activity_contact_progressFrame);
        userDetails = getIntent().getParcelableExtra(EXTRA_USER_DETAILS);
        if(userDetails == null){
            userDetails = new UserDetails(1, true, "A Contact", "a_contact", "http://gravatar.com/avatar/1.jpg");
        }

        getSupportActionBar().setTitle(userDetails.getDisplayName());
        toolbar.setNavigationIcon(R.drawable.ic_ab_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new MessagesAdapter(this, this);
        messages = adapter.getMessages();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_contact_messages);
        recyclerView.setAdapter(adapter);


        if(isTablet){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }



        scheduler.postEveryMilliseconds(
                new Messages.SearchMessagesRequest(userDetails.getId(), true, true), 3 * 60 * 1000);

    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Subscribe
    public void onMessagesRecevied(final Messages.SearchMessagesResponse response){
        scheduler.invokeOnResume(Messages.SearchMessagesResponse.class, new Runnable() {
            @Override
            public void run() {
                progressFrame.setVisibility(View.GONE);

                if(!response.didSucceed()){
                    response.showErrorToast(ContactActivity.this);
                    return;
                }

                int oldSize = messages.size();
                messages.clear();
                adapter.notifyItemRangeRemoved(0, oldSize);

                messages.addAll(response.Messages);
                adapter.notifyItemRangeInserted(0, messages.size());

            }
        });
    }

    private void doRemoveContact(){
        progressFrame.setVisibility(View.VISIBLE);
        bus.post(new Contacts.RemoveContactRequest(userDetails.getId()));
    }

    @Subscribe
    public void onRemoveContact(final Contacts.RemoveContactResponse response){
        scheduler.invokeOnResume(Contacts.RemoveContactResponse.class, new Runnable() {
            @Override
            public void run() {
                if(!response.didSucceed()){
                    response.showErrorToast(ContactActivity.this);
                    progressFrame.setVisibility(View.GONE);
                    return;
                }
                setResult(RESULT_USER_REMOVED);
                finish();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contact, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.activity_contact_menuNewMessage){
            Intent intent = new Intent(this, NewMessageActivity.class);
            intent.putExtra(NewMessageActivity.EXTRA_CONTACT, userDetails);
            startActivityForResult(intent, REQUEST_SEND_MESSAGE);
            return true;
        }
        if (id == R.id.activity_contact_menuRemoveFriend){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("RemoveFriend?")
                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doRemoveContact();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK){
            progressFrame.setVisibility(View.VISIBLE);
            bus.post(new Messages.SearchMessagesRequest(userDetails.getId(), true, true));
        }
    }
}
