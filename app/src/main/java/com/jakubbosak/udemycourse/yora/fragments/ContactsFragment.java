package com.jakubbosak.udemycourse.yora.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jakubbosak.udemycourse.yora.R;
import com.jakubbosak.udemycourse.yora.activities.AddContactActivity;
import com.jakubbosak.udemycourse.yora.activities.BaseActivity;
import com.jakubbosak.udemycourse.yora.activities.ContactActivity;
import com.jakubbosak.udemycourse.yora.activities.ContactsActivity;
import com.jakubbosak.udemycourse.yora.services.Contacts;
import com.jakubbosak.udemycourse.yora.services.entities.UserDetails;
import com.jakubbosak.udemycourse.yora.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private UserDetailsAdapter adapter;
    private View progressFrame;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        adapter = new UserDetailsAdapter((BaseActivity) getActivity());
        progressFrame = view.findViewById(R.id.fragment_contacts_progressFrame);

        ListView listView = (ListView) view.findViewById(R.id.fragment_contacts_list);
        listView.setEmptyView(view.findViewById(R.id.fragment_contacts_emptyList));
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);


        bus.post(new Contacts.GetContactsRequest(false));
       // progressFrame.setVisibility(View.GONE);


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserDetails details = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtra(ContactActivity.EXTRA_USER_DETAILS, details);
        startActivity(intent);
    }

    @Subscribe
    public void onContactsResponse(final Contacts.GetContactsResponse response){
        scheduler.invokeOnResume(Contacts.GetContactsResponse.class, new Runnable() {
            @Override
            public void run() {
                progressFrame.animate()
                        .alpha(0)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                progressFrame.setVisibility(View.GONE);
                            }
                        }).start();
                if(!response.didSucceed()){
                    response.showErrorToast(getActivity());
                    return;
                }

                adapter.clear();
                adapter.addAll(response.Contacts);
            }
        });
        response.showErrorToast(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_contacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.fragment_contacts_menu_addContact){
            startActivity(new Intent(getActivity(), AddContactActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
