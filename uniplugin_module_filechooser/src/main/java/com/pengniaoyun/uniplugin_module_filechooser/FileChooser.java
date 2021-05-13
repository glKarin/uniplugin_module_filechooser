package com.pengniaoyun.uniplugin_module_filechooser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.call.CallbackPool;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileUploadParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileUploadResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.FileChooser_base;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.SystemFileChooser;
import com.pengniaoyun.uniplugin_module_filechooser.fileupload.FileUpload;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ActivityUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

import java.io.File;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.feature.uniapp.utils.UniLogUtils;

public class FileChooser extends UniModule
{
    private static final String ID_TAG = "FileChooserModule";

    protected CallbackPool m_callbackPool;

    public FileChooser()
    {
        super();
        m_callbackPool = CallbackPool.Instance();
        Log("construction");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log("destruction");
    }

    @UniJSMethod(uiThread = true)
    public void helloworld(JSONObject params, UniJSCallback callback)
    {
        Log("helloworld");
        if(callback != null)
            callback.invoke(params);
    }

    @UniJSMethod(uiThread = false)
    public void helloworld_nonui(JSONObject params, UniJSCallback callback)
    {
        Log("helloworld_nonui");
        if(callback != null)
            callback.invoke(params);
    }

    @UniJSMethod(uiThread = true)
    public void Test(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("Test");
    }

    @UniJSMethod(uiThread = false)
    public void Test_nonui(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("Test_nonui");
    }

    @UniJSMethod(uiThread = true)
    public void OpenSystemDocumentFileChooser(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("OpenSystemFileChooser");
        JSONObject newParams = (JSONObject)params.clone();
        newParams.put("type", Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT);
        OpenFileChooser(newParams, callback, failCallback, finalCallback);
    }

    @UniJSMethod(uiThread = true)
    public void OpenSystemFileChooser(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("OpenSystemFileChooser");
        JSONObject newParams = (JSONObject)params.clone();
        newParams.put("type", Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM);
        OpenFileChooser(newParams, callback, failCallback, finalCallback);
    }

    @UniJSMethod(uiThread = true)
    public void OpenFileChooser(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        CallRequestStruct req = new CallRequestStruct(Constants.ENUM_MODULE_ACTION_OPEN_FILE_CHOOSER, new CallFileChooserParamStruct(params), callback, failCallback, finalCallback);
        FileChooser_base fileChooser = CreateFileChooser(req);
        int no = m_callbackPool.PushCallback(req);
        boolean res = fileChooser.OpenFileChooser(req, no);
        if(!res)
            m_callbackPool.PopCallback(no);
        Log("OpenFileChooser -> " + (res ? "ok" : "err"));
    }

    private FileChooser_base CreateFileChooser(CallRequestStruct req)
    {
        String type = req.<CallFileChooserParamStruct>GetParamStruct_T().type;
        FileChooser_base fileChooser = null;
        if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM.equalsIgnoreCase(type))
        {
            fileChooser = new SystemFileChooser(mUniSDKInstance.getContext(), SystemFileChooser.ContentType_e.CONTENT_TYPE_FILE);
        }
        else if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT.equalsIgnoreCase(type))
        {
            fileChooser = new SystemFileChooser(mUniSDKInstance.getContext(), SystemFileChooser.ContentType_e.CONTENT_TYPE_DOCUMENT);
        }
        else
        {
            fileChooser = new SystemFileChooser(mUniSDKInstance.getContext());
        }
        return fileChooser;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log("onActivityResult");
        CallRequestStruct req = m_callbackPool.GetCallback(requestCode);

        if(Constants.ENUM_MODULE_ACTION_OPEN_FILE_CHOOSER.equals(req.action))
        {
            m_callbackPool.RemoveCallback(req);
            if(!HandleSystemFileChooser(req, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data);
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean HandleSystemFileChooser(CallRequestStruct req, int resultCode, Intent data)
    {
        Log("HandleSystemFileChooser");

        FileChooser_base fileChooser = CreateFileChooser(req);
        boolean res = fileChooser.FileChooserResult(req, resultCode == Activity.RESULT_OK ? data : null);
        //if(!res)
        return true;
    }

    private void Log(String str)
    {
        UniLogUtils.w(str);
        Logf.e(ID_TAG, str);
        //LogF(mUniSDKInstance.getContext(), str);
    }

    @UniJSMethod(uiThread = false)
    public void FileUpload(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("FileUpload");
        boolean res = false;
        CallRequestStruct req = new CallRequestStruct(Constants.ENUM_MODULE_ACTION_FILE_UPLOAD, new CallFileUploadParamStruct(params), callback, failCallback, finalCallback);

        if(ActivityUtility.IsGrantPermission(mUniSDKInstance.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            FileUpload fileUpload = new FileUpload(mUniSDKInstance.getContext());

            VarRef ref = new VarRef(new Object());
            res = fileUpload.UploadFile(req, ref);
            if(res)
            {
                CallFileUploadResultStruct result = new CallFileUploadResultStruct(req.GetParamStruct().json, req.<CallFileUploadParamStruct>GetParamStruct_T().responseType);
                result.Set(new String(Common.<byte []>dynamic_cast(ref.Ref())));
                req.CallCallback(result.data);
            }
            else
            {
                req.CallFailCallback(ref.ref.toString());
            }
        }
        else
        {
            Log("READ_EXTERNAL_STORAGE permission is not granted!");
            req.CallFailCallback("没有授予文件存储读取权限");
        }
        req.CallFinalCallback();
        Log("FileUpload -> " + (res ? "ok" : "err"));
    }

    @UniJSMethod(uiThread = true)
    public void flogf(String params)
    {
        //Log("flogf");
        if(!ActivityUtility.IsGrantPermission(mUniSDKInstance.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Log("WRITE_EXTERNAL_STORAGE permission is not granted!");
            return;
        }

        LogF(mUniSDKInstance.getContext(), params);
    }

    public static void LogF(Context context, String params, Object...args)
    {
        final String filePath = context.getExternalCacheDir().getParent()
                + File.separator + "uniplugin_module_filechooser"
                + File.separator + "filechooser_"
                + Common.TimestampToDateStr(System.currentTimeMillis()) + ".log";

        Logf.e(filePath);
        Logf.f(filePath, ID_TAG, params, args);
        Logf.e(ID_TAG, params, args);
        UniLogUtils.e(String.format(params, args));
    }
}
