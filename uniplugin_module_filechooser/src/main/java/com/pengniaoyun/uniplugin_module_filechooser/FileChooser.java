package com.pengniaoyun.uniplugin_module_filechooser;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.call.CallbackPool;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileUploadParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileUploadResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.FileChooser_base;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.SystemFileChooser;
import com.pengniaoyun.uniplugin_module_filechooser.fileupload.FileUpload;
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
    public void helloworld_notui(JSONObject params, UniJSCallback callback)
    {
        Log("helloworld_notui");
        if(callback != null)
            callback.invoke(params);
    }

    @UniJSMethod(uiThread = true)
    public void Log_e(String str)
    {
        Logf.e(str);
    }

    @UniJSMethod(uiThread = true)
    public void Log_w(String str)
    {
        Logf.w(str);
    }

    @UniJSMethod(uiThread = true)
    public void Log_i(String str)
    {
        Logf.i(str);
    }

    @UniJSMethod(uiThread = true)
    public void Log_d(String str)
    {
        Logf.d(str);
    }

    @UniJSMethod(uiThread = true)
    public void Log_v(String str)
    {
        Logf.v(str);
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
            fileChooser = new SystemFileChooser(mUniSDKInstance.getContext());
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
        UniLogUtils.e(str);
        Log.w(ID_TAG, str);
    }

    @UniJSMethod(uiThread = false)
    public void FileUpload(JSONObject params, UniJSCallback callback, UniJSCallback failCallback, UniJSCallback finalCallback)
    {
        Log("FileUpload");
        CallRequestStruct req = new CallRequestStruct(Constants.ENUM_MODULE_ACTION_FILE_UPLOAD, new CallFileUploadParamStruct(params), callback, failCallback, finalCallback);

        FileUpload fileUpload = new FileUpload(mUniSDKInstance.getContext());

        VarRef ref = new VarRef(new Object());
        boolean res = fileUpload.UploadFile(req, ref);
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
        req.CallFinalCallback();
        Log("FileUpload -> " + (res ? "ok" : "err"));
    }
}
