package com.pengniaoyun.uniplugin_module_filechooser.common;

import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.utility.Pair;

import java.util.Objects;

public final class MIME
{
    public String primary_type = "*";
    public String secondary_type = "*";

    public MIME(String primary_type, String secondary_type)
    {
        super();
        this.primary_type = primary_type;
        this.secondary_type = secondary_type;
    }

    public MIME(String mime)
    {
        super();
        String inList[] = mime.split("/", 2);
        if(inList == null || inList.length < 2)
            return;
        if(TextUtils.isEmpty(inList[0]) || TextUtils.isEmpty(inList[1]))
            return;
        primary_type = inList[0];
        secondary_type = inList[1];
    }

    public static MIME Make(String mime)
    {
        String inList[] = mime.split("/", 2);
        if(inList == null || inList.length < 2)
            return null;
        if(TextUtils.isEmpty(inList[0]) || TextUtils.isEmpty(inList[1]))
            return null;
        return new MIME(inList[0], inList[1]);
    }

    public String PrimaryType()
    {
        return primary_type;
    }

    public String SecondaryType()
    {
        return secondary_type;
    }

    public MIME SetPrimaryType(String type)
    {
        primary_type = type;
        return this;
    }

    public MIME SetSecondaryType(String type)
    {
        secondary_type = type;
        return this;
    }

    @Override
    public String toString()
    {
        return primary_type + '/' + secondary_type;
    }

    public boolean Compare(MIME o)
    {
        return TextUtils.equals(this.primary_type, o.primary_type)
                && TextUtils.equals(this.secondary_type, o.secondary_type);
    }

    public boolean IsSame(MIME targetMime)
    {
        if("*/*".equals(targetMime.toString()))
            return true;

        // image/*
        if(!"*".equals(targetMime.secondary_type))
        {
            if(this.Compare(targetMime))
                return true;
        }

        // image/png
        if(TextUtils.equals(this.primary_type, targetMime.primary_type))
            return true;
        return false;
    }

}
