package com.martdev.android.contactnetwork;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CONTACTS = 1;
    private static final int PERMISSION_CALL_CONTACT = 2;
    private static final String PERMISSIONS[] = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
    };
    private Cursor mCursor;
    private List<String> mContacts;
    private List<PhoneNumbers> mPhoneNumbers;
    private ListView mListView;
    private SearchView mSearchView;
    private String contactNumber;
    private ArrayAdapter<String> adapter;
    private PhoneNumbers mNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);

        mSearchView = findViewById(R.id.search_view);
        mContacts = new ArrayList<>();
        mPhoneNumbers = new ArrayList<>();

         adapter = new ArrayAdapter<>(
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        setPermissionRequestContacts();
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter adapter = (ArrayAdapter) mListView.getAdapter();
        mListView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CONTACTS || requestCode == PERMISSION_CALL_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showContact();
            } else {
                Toast.makeText(this, "Please grant request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callNumber(int position, Intent callNumber) {
        PhoneNumbers phoneNumbers = mPhoneNumbers.get(position);
        Log.i("this", "test2");
        setNumber(callNumber, phoneNumbers.getNumber());
    }

    private void setPermissionRequestContacts() {
        final int permissionCheck = ContextCompat.checkSelfPermission(this, PERMISSIONS[0]);
        int callPermission = ContextCompat.checkSelfPermission(this, PERMISSIONS[1]);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED || callPermission == PackageManager.PERMISSION_GRANTED) {
            showContact();
            final Intent callNumber = new Intent(Intent.ACTION_CALL);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    callNumber(position, callNumber);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
        }
    }

    private void showContact() {
        String[] query = {
                ContactsContract.Contacts.DISPLAY_NAME,
                CommonDataKinds.Phone.NUMBER
        };

        mCursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                query, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

        try {
            if (mCursor.getCount() == 0) {
                return;
            }

            mCursor.moveToFirst();
            while (mCursor.moveToNext()) {
                String contactName = mCursor.getString(1);
                contactNumber = mCursor.getString(2);
                showContactNetwork(contactName, contactNumber);
            }
        } finally {
            mCursor.close();
        }
    }

    private void showContactNetwork(String contactName, String contactNumber) {
        String mtn[] = {"+234703", "+234706", "+234803", "+234806", "+234810", "+234813", "+234814",
                "+234903", "+234906", "0703", "0706", "0803", "0806", "0810", "0813", "0814", "0903", "0906"};
        String globacom[] = {"+234705", "+234805", "+234807", "+234811", "+234815", "+234905",
                "0705", "0805", "0807", "0811", "0815", "0905"};
        String airtel[] = {"+234701", "+234708", "+234802", "+234808", "+234812", "+234902", "+234907",
                "0701", "0708", "0802", "0808", "0812", "0902", "0907"};
        String _9mobile[] = {"+234809", "+234817", "+234818", "+234909", "+234908",
                "0809", "0817", "0818", "0909", "0908"};

        for (String mTn : mtn) {
            if (contactNumber.startsWith(mTn)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " MTN");
                mNumbers = new PhoneNumbers(contactName, contactNumber);
                mPhoneNumbers.add(mNumbers);
                break;
            }
        }

        for (String glo : globacom) {
            if (contactNumber.startsWith(glo)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber +  "\t" + " GLO");
                mNumbers = new PhoneNumbers(contactName, contactNumber);
                mPhoneNumbers.add(mNumbers);
                break;
            }
        }

        for (String airTel : airtel) {
            if (contactNumber.startsWith(airTel)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " AIRTEL");
                mNumbers = new PhoneNumbers(contactName, contactNumber);
                mPhoneNumbers.add(mNumbers);
                break;
            }
        }

        for (String mobile9 : _9mobile) {
            if (contactNumber.startsWith(mobile9)) {
                mContacts.add("Name: " + contactName + "\n" + "PhoneNo: " + contactNumber + "\t" + " 9Mobile");
                mNumbers = new PhoneNumbers(contactName, contactNumber);
                mPhoneNumbers.add(mNumbers);
                break;
            }
        }
    }

    private void setNumber(@NonNull Intent callNumber, String contactNumber) {
        Uri number = Uri.parse("tel: " + contactNumber);
        callNumber.setData(number);
        startActivity(callNumber);
    }
}
