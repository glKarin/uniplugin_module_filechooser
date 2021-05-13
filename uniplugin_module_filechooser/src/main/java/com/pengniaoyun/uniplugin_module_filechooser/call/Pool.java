package com.pengniaoyun.uniplugin_module_filechooser.call;

import com.pengniaoyun.uniplugin_module_filechooser.utility.STL;

import java.util.HashMap;

public class Pool<T>
{
    private static final int CONST_QUEUE_NO_BEGIN = 1;

    private HashMap<String, T> m_callbackQueue = null;
    private int m_queueCount = 0;

    public Pool()
    {

    }

    private int GenNo()
    {
        m_queueCount++;
        int no;
        do
        {
            m_queueCount++;
            if(m_queueCount <= 0)
                m_queueCount = 1;
            no = CONST_QUEUE_NO_BEGIN + m_queueCount;
        }
        while(m_callbackQueue != null && m_callbackQueue.containsKey(no));
        return no;
    }

    public int PushCallback(final T callback)
    {
        int no = GenNo();
        if(callback == null)
            return no;

        if(m_callbackQueue == null)
            m_callbackQueue = new HashMap<String, T>();

        m_callbackQueue.put("" + no, callback);

        return no;
    }

    public T PopCallback(int no)
    {
        if(m_callbackQueue == null)
            return null;

        String key = "" + no;
        if(!m_callbackQueue.containsKey(key))
            return null;

        T cb = (T)m_callbackQueue.get(key);
        m_callbackQueue.remove(key);
        return cb;
    }

    public T GetCallback(int no)
    {
        if(m_callbackQueue == null)
            return null;

        String key = "" + no;
        if(!m_callbackQueue.containsKey(key))
            return null;

        T cb = (T)m_callbackQueue.get(key);
        return cb;
    }

    public T RemoveCallback(T t)
    {
        if(m_callbackQueue == null)
            return null;

        if(!m_callbackQueue.containsValue(t))
            return null;

        STL.<String, T>MapRemoveValue(m_callbackQueue, t);
        return t;
    }

    public void Destroy()
    {
        if(m_callbackQueue == null)
            return;
        m_callbackQueue.clear();
        m_callbackQueue = null;
    }

    public void Reset()
    {
        Destroy();
        m_queueCount = 0;
    }
}
