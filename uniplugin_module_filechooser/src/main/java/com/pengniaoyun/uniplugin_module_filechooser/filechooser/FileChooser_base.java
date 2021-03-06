package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower.FileHistoryRecorder;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower.FileHistoryRecorder_json;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

public abstract class FileChooser_base implements FileChooserInterface
{
    private static final String ID_TAG = "FileChooser_base";

    protected Context m_context;

    protected FileChooser_base(Context context)
    {
        m_context = context;
    }

    protected void Log(String str)
    {
        ModuleUtility.Log(ID_TAG, str);
        //FileChooser.LogF(m_context, str);
    }

    protected void Callback(CallRequestStruct req, CallResultStruct res)
    {
        if(req != null)
        {
            UniJSCallback callback = req.GetUniJSCallback();
            if(callback != null)
            {
                Object map = res.MakeResult();
                callback.invoke(map);
            }
        }
    }

    protected void AddHistory(String path)
    {
        FileHistoryRecorder recorder = FileHistoryRecorder_json.Instance(m_context);
        if(recorder != null)
            recorder.Add(path);
    }
}
