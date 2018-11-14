package com.timdebooij.hueapplicatie.services;

import com.timdebooij.hueapplicatie.models.Bridge;

public interface ApiListener {
    void onResponse(String response);
    void usernameReceived(Bridge bridgeWithToken);
}