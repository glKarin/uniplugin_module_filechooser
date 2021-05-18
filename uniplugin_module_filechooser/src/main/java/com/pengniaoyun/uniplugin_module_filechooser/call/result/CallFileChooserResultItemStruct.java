package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

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

    public static CallFileChooserResultItemStruct MakeIfInMime(String path, String mimes[])
    {
        if(TextUtils.isEmpty(path))
            return null;

        File file = new File(path);
        if(!file.exists())
            return null;

        String p = file.getAbsolutePath();
        String mime = FS.FileMIME(p);
        if(/*!TextUtils.isEmpty(mime) && */!Common.ArrayIsEmpty(mimes))
        {
            if(!FS.CompareMIME(mime, mimes))
                return null;
        }

        CallFileChooserResultItemStruct item = new CallFileChooserResultItemStruct();
        item.path = p;
        item.size = file.length();
        item.name = file.getName();
        item.mime = mime;

        return item;
    }

    public static CallFileChooserResultItemStruct Make(String path)
    {
        return MakeIfInMime(path, null);
    }

}
