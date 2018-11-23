package com.timdebooij.hueapplicatie.services;

import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.ColorScheme;

public interface ApiListener {
    void onResponse(String response);
    void usernameReceived(Bridge bridgeWithToken);
    void onError(String error);
    void onLightBulbs(Bridge bridgeWithLightbulbs);
    void onNewScheme(ColorScheme scheme);
}
