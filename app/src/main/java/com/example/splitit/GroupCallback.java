package com.example.splitit;

import java.util.ArrayList;

/*
Interface used for reading the groups from firebase
 */
public interface GroupCallback {
    void onCallback(String key);
}
