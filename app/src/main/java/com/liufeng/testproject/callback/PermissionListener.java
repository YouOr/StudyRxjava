package com.liufeng.testproject.callback;

import java.util.List;


public interface PermissionListener {

    void onGranted();
    void onDenied(List<String> deniedPermission);
}
