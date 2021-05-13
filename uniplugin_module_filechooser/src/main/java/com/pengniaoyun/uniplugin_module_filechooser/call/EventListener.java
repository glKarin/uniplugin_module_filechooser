package com.pengniaoyun.uniplugin_module_filechooser.call;

import java.util.HashMap;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

final public class EventListener
{
    public final static String EVENT_EXCEPTION = "exception";
    public final static String EVENT_LOG = "log";

    private static EventListener _callback_table = null;

    private HashMap<String, UniJSCallback> m_eventListenerTable = null;

    private EventListener()
    {
    }

    public static EventListener Instance()
    {
        if(_callback_table == null)
            _callback_table = new EventListener();
        return _callback_table;
    }

    public UniJSCallback AddEventListener(String name, UniJSCallback callback)
    {
        if(m_eventListenerTable == null)
            m_eventListenerTable = new HashMap<String, UniJSCallback>();

        m_eventListenerTable.put(name, callback);

        return callback;
    }

    public UniJSCallback RemoveEventListener(String name)
    {
        if(m_eventListenerTable == null)
            return null;

        if(!m_eventListenerTable.containsKey(name))
            return null;

        UniJSCallback cb = m_eventListenerTable.get(name);
        m_eventListenerTable.remove(name);
        return cb;
    }

    public UniJSCallback GetEventListener(String name)
    {
        if(m_eventListenerTable == null)
            return null;

        if(!m_eventListenerTable.containsKey(name))
            return null;

        UniJSCallback cb = m_eventListenerTable.get(name);
        return cb;
    }

    public UniJSCallback TriggerEvent(String name, Object args)
    {
        UniJSCallback cb = GetEventListener(name);
        if(cb == null)
            return null;
        cb.invokeAndKeepAlive(args);
        return cb;
    }

    public void Destroy()
    {
        if(m_eventListenerTable == null)
            return;
        m_eventListenerTable.clear();
        m_eventListenerTable = null;
    }

    public void Reset()
    {
        Destroy();
    }
}
