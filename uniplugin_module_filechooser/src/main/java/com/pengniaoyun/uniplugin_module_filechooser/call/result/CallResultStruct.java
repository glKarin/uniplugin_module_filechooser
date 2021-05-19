package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.alibaba.fastjson.JSONObject;

public class CallResultStruct<T>
{
    public JSONObject param;
    public T data;
    //public boolean success = false;

    public CallResultStruct()
    {
        super();
    }

    public CallResultStruct(JSONObject param, T data)
    {
        super();
        this.param = param;
        this.data = data;
        //this.success = data != null;
    }

    public Object MakeResult()
    {
        return null;
    }
}
