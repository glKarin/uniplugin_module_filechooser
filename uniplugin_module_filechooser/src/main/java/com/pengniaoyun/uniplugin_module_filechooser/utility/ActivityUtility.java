package com.pengniaoyun.uniplugin_module_filechooser.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.File;

public final class ActivityUtility {
    private static final String ID_TAG = "ActivityUtility";
    private ActivityUtility(){}

    // 判断是否声明权限
    public static boolean IsGrantPermission(Context activity, String permission)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) // 棉花糖以上
        {
            return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        else
        {
            return true;
        }
    }

    public static void OpenAppSetting(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
