package com.jakubbosak.udemycourse.yora.activities;


import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.services.Contacts;
import com.jakubbosak.udemycourse.yora.services.entities.UserDetails;
import com.jakubbosak.udemycourse.yora.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class SelectContactActivity extends BaseAunthenticatedActivity implements AdapterView.OnItemClickListener {
    public static String RESULT_CONTACT = "RESULT_CONTACT";



    private static final int REQUEST_ADD_CONTACT = 1;
    private UserDetailsAdapter adapter;

    @Override
    protected void onYoraCreate(Bundle savedState) {
        setContentView(R.layout.activity_select_contact);
        getSupportActionBar().setTitle("Select Contact");

        adapter = new UserDetailsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.activity_select_contact_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactsRequest(true));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.activity_select_contact_menuAddContact){
            startActivityForResult(new Intent(this, AddContactActivity.class), REQUEST_ADD_CONTACT);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_contact, menu);
        return true;
    }

    @Subscribe
    public void onContactsReceived(final Contacts.GetContactsResponse response){
        scheduler.invokeOnResume(Contacts.GetContactsResponse.class, new Runnable() {
            @Override
            public void run() {
                response.showErrorToast(SelectContactActivity.this);
                adapter.clear();
                adapter.addAll(response.Contacts);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ADD_CONTACT && resultCode == RESULT_OK){
            UserDetails user = data.getParcelableExtra(AddContactActivity.RESULT_CONTACT);
            selectUser(user);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectUser(adapter.getItem(position));
    }

    private void selectUser(UserDetails user){
        Intent intent = new Intent();
        intent.putExtra(RESULT_CONTACT, user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
