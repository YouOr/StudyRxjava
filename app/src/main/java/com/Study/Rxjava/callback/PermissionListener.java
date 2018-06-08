package com.Study.Rxjava.callback;

import java.util.List;


public interface PermissionListener {

    void onGranted();
    void onDenied(List<String> deniedPermission);
}
