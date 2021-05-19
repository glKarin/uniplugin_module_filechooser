package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;

public class CallFileChooserSupportResultItemStruct
{
    public String type;
    public String name;

    public static CallFileChooserSupportResultItemStruct Make(String type)
    {
        if(TextUtils.isEmpty(type))
            return null;

        String name = null;
        if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM.equals(type))
            name = "系统文件选择器";
        else if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT.equals(type))
            name = "系统文档选择器";
        else if(Constants.ENUM_FILE_CHOOSER_TYPE_INTERNAL.equals(type))
            name = "内置文件选择器";
        else
            return null;

        CallFileChooserSupportResultItemStruct item = new CallFileChooserSupportResultItemStruct();
        item.type = type;
        item.name = name;

        return item;
    }

}
