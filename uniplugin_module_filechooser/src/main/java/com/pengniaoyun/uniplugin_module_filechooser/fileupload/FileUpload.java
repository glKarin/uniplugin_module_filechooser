package com.pengniaoyun.uniplugin_module_filechooser.fileupload;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileUploadParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.FileChooserInterface;
import com.pengniaoyun.uniplugin_module_filechooser.network.HttpUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

import java.io.File;
import java.util.Map;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.utils.UniLogUtils;

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
        UniLogUtils.e(str);
        Logf.w("FileUpload", str);
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
            return false;
        }

        if(!params.IsValid())
        {
            data.Ref("参数不合法");
            return false;
        }

        params.ResetMethod();
        boolean isFileUpload = params.IsFileUpload();
        byte[] res = null;
        if(isFileUpload)
        {
            Logf.e("文件上传");
            Map<String, Object> param = params.CombineParam();
            res = HttpUtility.UploadFiles(params.url, params.method.toUpperCase(), param, params.header, params.timeout);
        }
        else
        {
            Logf.e("一般请求");
            res = HttpUtility.Request(params.url, params.method.toUpperCase(), params.data, params.header, params.timeout);
        }

        if(res == null)
        {
            data.Ref("网络错误");
            return false;
        }

        data.Ref(res);

        return true;
    }
}
