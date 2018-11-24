package com.lkj.chinacitydata.runtimepermissions;

import android.annotation.TargetApi;
import android.app.Activity;

/**
 *@author lkj
 *@email lkjfyy@gmail.com
 *@date 2018/1/18 20:53
 *@description 动态请求权限
 */

public class PermissionsUtils {

    @TargetApi(23)
    public static void requestPermissions(Activity activity) {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(activity, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
