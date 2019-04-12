package com.martdev.android.contactnetwork;

public class PhoneNumbers {
    private String mName;
    private String mNumber;

    public PhoneNumbers(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }
}
