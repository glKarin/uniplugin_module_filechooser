package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CallFileChooserResultStruct extends CallResultStruct<List<CallFileChooserResultItemStruct>>
{
    public CallFileChooserResultStruct(JSONObject param)
    {
        super(param, new ArrayList<CallFileChooserResultItemStruct>());
    }

    public CallFileChooserResultStruct AddFile(String path)
    {
        CallFileChooserResultItemStruct item = CallFileChooserResultItemStruct.Make(path);
        this.data.add(item);
        return this;
    }
}
