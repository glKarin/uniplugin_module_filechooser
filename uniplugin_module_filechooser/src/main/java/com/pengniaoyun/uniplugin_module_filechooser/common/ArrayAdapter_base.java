package com.pengniaoyun.uniplugin_module_filechooser.common;
import android.widget.*;
import android.content.*;
import java.util.*;
import android.view.*;

public abstract class ArrayAdapter_base<T> extends ArrayAdapter<T>
{
    private int m_resource = 0;

    public ArrayAdapter_base(Context context, int resource)
    {
        super(context, resource);
        m_resource = resource;
    }

    public ArrayAdapter_base(Context context, int resource, T array[])
    {
        super(context, resource, array);
        m_resource = resource;
    }

    public ArrayAdapter_base(Context context, int resource, List<T> list)
    {
        super(context, resource, list);
        m_resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        T obj = getItem(position);
        View view = null;

        if(convertView == null)
            view = LayoutInflater.from(parent.getContext()).inflate(m_resource, parent, false);
        else
            view = convertView;
        return obj != null ? GenerateView(position, view, parent, obj) : null;
    }

    public void Remove(int index)
    {
        int count = getCount();
        int i = index < 0 ? count + index : index;
        if(i < 0 || i >= count)
            return;
        remove(getItem(i));
    }

    public void SetData(T array[])
    {
        clear();
        addAll(array);
    }

    public void SetData(Collection<? extends T> list)
    {
        clear();
        addAll(list);
    }

    public abstract View GenerateView(int position, View view, ViewGroup parent, T data);
}

    