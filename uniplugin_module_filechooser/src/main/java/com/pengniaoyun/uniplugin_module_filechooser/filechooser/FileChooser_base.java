package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.FileChooser;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

import java.util.List;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.utils.UniLogUtils;

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
        UniLogUtils.w(str);
        Logf.e(ID_TAG, str);
        //FileChooser.LogF(m_context, str);
    }

    protected JSONObject MakeResultJson(CallRequestStruct req, CallResultStruct res)
    {
        JSONObject map = new JSONObject();
        CallFileChooserResultStruct result = (CallFileChooserResultStruct)res;
        map.put("count", result.data.size());
        map.put("data", result.data);
        map.put("select_count", result.select_count);
        return map;
    }

    protected void Callback(CallRequestStruct req, CallResultStruct res)
    {
        if(req != null)
        {
            UniJSCallback callback = req.GetUniJSCallback();
            if(callback != null)
            {
                JSONObject map = MakeResultJson(req, res);
                callback.invoke(map);
            }
        }
    }
}
