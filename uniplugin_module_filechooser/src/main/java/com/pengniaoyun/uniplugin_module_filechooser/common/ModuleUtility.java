package com.pengniaoyun.uniplugin_module_filechooser.common;

import com.pengniaoyun.uniplugin_module_filechooser.call.EventListener;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.utils.UniLogUtils;

public final class ModuleUtility
{
    private ModuleUtility() {}

    public static void CallUniJSCallback(UniJSCallback callback, Object params)
    {
        if(callback != null)
            callback.invoke(params);
    }

    public static void ExecUniJSCallback(UniJSCallback callback, Object params)
    {
        if(callback != null)
            callback.invokeAndKeepAlive(params);
    }

    public static void DumpException(Throwable e)
    {
        Logf.DumpException(e);
        TriggerModuleEvent(EventListener.EVENT_EXCEPTION, Common.ThrowableToString(e));
    }

    public static void Log(String tag, String str)
    {
        UniLogUtils.w(str);
        Logf.e(tag, str);
        TriggerModuleEvent(EventListener.EVENT_LOG, str);
    }

    public static void TriggerModuleEvent(String type, Object arg)
    {
        // return;
        EventListener.Instance().TriggerEvent(type, arg);
    }
}
