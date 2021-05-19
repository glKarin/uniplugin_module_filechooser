package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;

import java.util.ArrayList;
import java.util.List;

public class CallFileChooserSupportResultStruct extends CallResultStruct<List<CallFileChooserSupportResultItemStruct>>
{
    public CallFileChooserSupportResultStruct()
    {
        super(null, new ArrayList<CallFileChooserSupportResultItemStruct>());
    }

    public boolean Add(String type)
    {
        if(Exists(type))
            return false;
        CallFileChooserSupportResultItemStruct item = CallFileChooserSupportResultItemStruct.Make(type);
        if(item != null)
        {
            this.data.add(item);
            return true;
        }
        return false;
    }

    public void Fill(String ...types)
    {
        data.clear();
        for (String type : types)
            Add(type);
    }

    public void FillAll()
    {
        Fill(
                Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT,
                Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM,
                Constants.ENUM_FILE_CHOOSER_TYPE_INTERNAL
                );
    }

    public boolean Exists(String type)
    {
        for (CallFileChooserSupportResultItemStruct item : data)
        {
            if(item.name.equals(type))
                return true;
        }
        return false;
    }

    public Object MakeResult()
    {
        JSONObject map = new JSONObject();
        map.put("count", data.size());
        map.put("data", data);
        return map;
    }
}
