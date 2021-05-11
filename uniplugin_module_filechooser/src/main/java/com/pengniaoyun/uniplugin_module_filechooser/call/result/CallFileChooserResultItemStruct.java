package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;

import java.io.File;

public class CallFileChooserResultItemStruct
{
    public String path;
    public String name;
    public long size;
    public String mime;

    public boolean IsValid()
    {
        return path != null;
    }

    public static CallFileChooserResultItemStruct Make(String path)
    {
        if(path == null || path.isEmpty())
            return null;

        File file = new File(path);
        if(!file.exists())
            return null;

        CallFileChooserResultItemStruct item = new CallFileChooserResultItemStruct();
        item.path = file.getAbsolutePath();
        item.size = file.length();
        item.name = file.getName();
        item.mime = FS.FileMIME(item.path);

        return item;
    }
}
