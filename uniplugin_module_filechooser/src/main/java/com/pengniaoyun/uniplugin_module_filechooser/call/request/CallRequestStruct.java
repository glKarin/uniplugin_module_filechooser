package com.pengniaoyun.uniplugin_module_filechooser.call.request;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

public class CallRequestStruct
{
    public String action;
    public CallParamStruct param;
    public UniJSCallback callback;
    public UniJSCallback fail_callback;
    public UniJSCallback final_callback;

    public CallRequestStruct()
    {
        super();
    }

    public CallRequestStruct(String action, CallParamStruct param, UniJSCallback callback, UniJSCallback fail_callback, UniJSCallback final_callback)
    {
        this();
        this.action = action;
        this.param = param;
        this.callback = callback;
        this.fail_callback = fail_callback;
        this.final_callback = final_callback;
    }

    public CallRequestStruct(String action, CallParamStruct param, UniJSCallback callback)
    {
        this(action, param, callback, null, null);
    }

    public CallRequestStruct(String action, CallParamStruct param, UniJSCallback callback, UniJSCallback fail_callback)
    {
        this(action, param, callback, fail_callback, null);
    }

    public CallParamStruct GetParamStruct()
    {
        return param;
    }

    public <T> T GetParamStruct_T()
    {
        try
        {
            T t = (T)param;
            return t;
        }
        catch (Exception e)
        {
            Logf.DumpException(e);
            return null;
        }
    }

    public UniJSCallback GetUniJSCallback()
    {
        return callback;
    }

    public UniJSCallback GetFailUniJSCallback()
    {
        return fail_callback;
    }

    public UniJSCallback GetFinalUniJSCallback()
    {
        return final_callback;
    }

    public boolean CallCallback(@NonNull Object json)
    {
        if(callback != null)
        {
            callback.invoke(json);
            return true;
        }
        return false;
    }

    public boolean CallFailCallback(String msg)
    {
        if(fail_callback != null)
        {
            fail_callback.invoke(msg);
            return true;
        }
        return false;
    }

    public boolean CallFinalCallback()
    {
        if(final_callback != null)
        {
            final_callback.invoke(null);
            return true;
        }
        return false;
    }
}
