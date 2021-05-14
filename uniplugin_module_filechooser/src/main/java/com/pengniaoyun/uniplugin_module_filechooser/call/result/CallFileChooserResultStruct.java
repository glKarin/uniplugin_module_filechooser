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

    public CallFileChooserResultStruct AddFile(String path)
    {
        CallFileChooserResultItemStruct item = CallFileChooserResultItemStruct.Make(path);
        if(item != null)
            this.data.add(item);
        return this;
    }

    public CallFileChooserResultStruct AddFile(String path, String mime[])
    {
        CallFileChooserResultItemStruct item = CallFileChooserResultItemStruct.MakeIfInMime(path, mime);
        if(item != null)
            this.data.add(item);
        return this;
    }
}
