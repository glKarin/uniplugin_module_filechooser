package com.pengniaoyun.uniplugin_module_filechooser.common;

import android.content.Context;
import android.view.View;

import com.pengniaoyun.uniplugin_module_filechooser.utility.ActivityUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;

import java.util.HashMap;
import java.util.Map;

public class ViewHolder
{
    protected Map<String, View> m_viewHolder = null;

    public ViewHolder Add(String name, View view)
    {
        if(view == null)
            return Remove(name);
        if(m_viewHolder == null)
            m_viewHolder = new HashMap<String, View>();
        m_viewHolder.put(name, view);
        return this;
    }

    public ViewHolder Remove(String name)
    {
        if(m_viewHolder == null)
            return this;
        if(!m_viewHolder.containsKey(name))
            return this;
        m_viewHolder.remove(name);
        return this;
    }

    public ViewHolder Clear()
    {
        if(m_viewHolder == null)
            return this;
        m_viewHolder.clear();
        return this;
    }

    public View Get(String name)
    {
        if(m_viewHolder == null)
            return null;
        if(!m_viewHolder.containsKey(name))
            return null;
        return m_viewHolder.get(name);
    }

    public <T> T Get_T(String name)
    {
        View view = Get(name);
        if(view == null)
            return null;
        return Common.dynamic_cast(view);
    }

}

    