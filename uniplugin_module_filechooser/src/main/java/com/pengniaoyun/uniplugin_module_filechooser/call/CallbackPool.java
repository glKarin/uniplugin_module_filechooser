package com.pengniaoyun.uniplugin_module_filechooser.call;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;

public class CallbackPool extends Pool<CallRequestStruct>
{
    private static CallbackPool _callback_pool = null;

    private CallbackPool()
    {

    }

    public static CallbackPool Instance()
    {
        if(_callback_pool == null)
            _callback_pool = new CallbackPool();
        return _callback_pool;
    }
}
