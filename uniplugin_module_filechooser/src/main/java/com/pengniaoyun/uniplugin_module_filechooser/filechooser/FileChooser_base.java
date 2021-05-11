package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
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
        UniLogUtils.e(str);
        Logf.w(ID_TAG, str);
    }

    protected JSONObject MakeResultJson(CallRequestStruct req, CallResultStruct res)
    {
        JSONObject map = new JSONObject();
        List list = (List)res.data;
        map.put("count", list.size());
        map.put("data", list);
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
