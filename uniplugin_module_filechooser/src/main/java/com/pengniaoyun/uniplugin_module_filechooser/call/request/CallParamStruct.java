package com.pengniaoyun.uniplugin_module_filechooser.call.request;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.ModuleUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

public class CallParamStruct
{
    public JSONObject json;

    public CallParamStruct()
    {
        super();
    }

    public CallParamStruct(JSONObject json)
    {
        super();
        //this.Set(json);
        this.json = json;
    }

    public boolean Has(String name)
    {
        boolean res = json.containsKey(name);
        return res;
    }

    public Object Get(String name, Object def)
    {
        if(this.json == null)
            return def;
        //Object obj = json.containsKey(name) ? json.get(name) : def;
        Object obj = json.getOrDefault(name, def);
        return obj;
    }

    public<T> T Get_T(String name, T def)
    {
        Object obj = Get(name, def);
        try
        {
            T t = (T)obj;
            return t;
        }
        catch (Exception e)
        {
            ModuleUtility.DumpException(e);
            return def;
        }
    }

    public void Set(JSONObject params)
    {
        this.json = params;
    }
}
