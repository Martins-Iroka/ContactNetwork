package com.martdev.android.contactnetwork;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CONTACTS = 1;
    private Cursor mCursor;
    private List<String> mContacts;
    private ListView mListView;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);

        mSearchView = findViewById(R.id.search_view);
        mContacts = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mContacts
        );
        mListView.setAdapter(adapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                adapter.getFilter().filter(text);
                return false;
            }
        });

        setPermissionRequestContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContact();
            } else {
                Toast.makeText(this, "Please grant request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPermissionRequestContacts() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showContact();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
        }
    }

    private void showContact() {
        mCursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

        try {
            if (mCursor.getCount() == 0) {
                return;
            }

            mCursor.moveToFirst();
            while (mCursor.moveToNext()) {
                String contactName = mCursor.getString(mCursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = mCursor.getString(mCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                showContactNetwork(contactName, contactNumber);
            }
        } finally {
            mCursor.close();
        }
    }

    private void showContactNetwork(String contactName ,String contactNumber) {
        String mtn[] = { "0703", "0706", "0803", "0806", "0810", "0813", "0814", "0903", "0906" };
        String globacom[] = { "0705", "0805", "0807", "0811", "0815", "0905" };
        String airtel[] = { "0701", "0708", "0802", "0808", "0812", "0902", "0907" };
        String _9mobile[] = { "0809", "0817", "0818", "0909", "0908" };

        for (String mTn : mtn) {
            if (contactNumber.startsWith(mTn)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " MTN");
                break;
            }
        }

        for (String glo : globacom) {
            if (contactNumber.startsWith(glo)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber +  "\t" + " GLO");
                break;
            }
        }

        for (String airTel : airtel) {
            if (contactNumber.startsWith(airTel)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " AIRTEL");
                break;
            }
        }

        for (String mobile9 : _9mobile) {
            if (contactNumber.startsWith(mobile9)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " 9Mobile");
                break;
            }
        }
    }
}
