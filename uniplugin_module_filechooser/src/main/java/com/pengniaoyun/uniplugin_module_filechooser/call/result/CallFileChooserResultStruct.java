package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CallFileChooserResultStruct extends CallResultStruct<List<CallFileChooserResultItemStruct>>
{
    public int select_count = 0;

    public CallFileChooserResultStruct(JSONObject param)
    {
        super(param, new ArrayList<CallFileChooserResultItemStruct>());
    }

    public boolean AddFile(String path)
    {
        CallFileChooserResultItemStruct item = CallFileChooserResultItemStruct.Make(path);
        if(item != null)
        {
            this.data.add(item);
            return true;
        }
        return false;
    }

    public boolean AddFile(String path, String mime[])
    {
        return AddFile(path, null);
    }

    public Object MakeResult()
    {
        JSONObject map = new JSONObject();
        map.put("count", data.size());
        map.put("data", data);
        map.put("select_count", select_count);
        return map;
    }
}
