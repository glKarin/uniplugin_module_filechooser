package com.pengniaoyun.uniplugin_module_filechooser.fileupload;

import android.content.Context;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileUploadParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;
import com.pengniaoyun.uniplugin_module_filechooser.network.HttpUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

import java.util.Map;

public class FileUpload implements FileUploadInterface
{
    private static final String ID_TAG = "FileUpload";

    protected Context m_context;

    public FileUpload(Context context)
    {
        m_context = context;
    }

    protected void Log(String str)
    {
        ModuleUtility.Log(ID_TAG, str);
    }

    public Object MakeResultJson(CallRequestStruct req, CallResultStruct res)
    {
        return res.data;
    }

    public boolean UploadFile(CallRequestStruct req, VarRef data)
    {
        CallFileUploadParamStruct params = req.<CallFileUploadParamStruct>GetParamStruct_T();
        if(params == null)
        {
            data.Ref("无效参数");
            Log("Parameter is null");
            return false;
        }

        if(!params.IsValid())
        {
            data.Ref("参数不合法");
            Log("Parameter is invalid");
            return false;
        }

        params.ResetMethod();
        boolean isFileUpload = params.IsFileUpload();
        byte[] res = null;
        if(isFileUpload)
        {
            Log("Start -> file upload");
            Map<String, Object> param = params.CombineParam();
            res = HttpUtility.UploadFiles(params.url, params.method.toUpperCase(), param, params.header, params.timeout);
        }
        else
        {
            Log("Start -> request");
            res = HttpUtility.Request(params.url, params.method.toUpperCase(), params.data, params.header, params.timeout);
        }

        if(res == null)
        {
            data.Ref("网络错误");
            Log("Network error");
            return false;
        }

        data.Ref(res);
        Log("Network request success");

        return true;
    }
}
