package com.pengniaoyun.uniplugin_module_filechooser.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.webkit.MimeTypeMap;

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

    // 请求权限
    public static boolean RequestPermission(Activity activity, String permission, int reqCode)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) // 棉花糖以上
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            {
                return false; // 用户拒绝授权, 并且希望不要再提示?
            }
            ActivityCompat.requestPermissions(activity, new String[] { permission }, reqCode);
            return true;
        }
        return false;
    }

    public static void OpenAppSetting(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static int GetId(Context context, String name, String defType, String defPackage)
    {
        return context.getResources().getIdentifier(name, defType, defPackage != null ? defPackage : context.getPackageName());
    }

    public static int GetId(Context context, String str, String defPackage)
    {
        String arr[] = str.split("\\.");
        String type = arr.length > 1 ? arr[0] : "id";
        String name = arr.length > 1 ? arr[1] : arr[0];
        return GetId(context, name, type, defPackage != null ? defPackage : context.getPackageName());
    }
}
