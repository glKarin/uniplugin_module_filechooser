package com.pengniaoyun.uniplugin_module_filechooser.call.request;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CallFileChooserParamStruct extends CallParamStruct
{
    public String type = Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM;
    public String mime = "*/*";
    public Set<String> extra_mime = new HashSet<String>();
    public boolean multiple = false;

    public CallFileChooserParamStruct()
    {
        super();
    }

    public CallFileChooserParamStruct(JSONObject json)
    {
        super(json);
        Set(json);
    }

    public void Set(JSONObject params)
    {
        this.type = this.<String>Get_T("type", Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM);
        this.mime = this.<String>Get_T("mime", "*/*");
        this.multiple = this.<Boolean>Get_T("multiple", false);

        Object mimeObj = this.Get("extra_mime", null);
        if(mimeObj != null)
        {
            if(mimeObj instanceof List)
            {
                List<String> mimes = (List<String>)mimeObj;
                mimes.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String item) {
                        CallFileChooserParamStruct.this.extra_mime.add(item);
                    }
                });
            }
            else
            {
                String str = mimeObj.toString();
                String mimes[] = str.split("\\|");
                for (String item : mimes)
                {
                    this.extra_mime.add(item);
                }
            }
        }
    }

    public String[] ExtraMime()
    {
        if(extra_mime.isEmpty())
            return null;
        String mimes[] = new String[extra_mime.size()];
        Object objs[] = extra_mime.toArray();
        for(int i = 0; i < extra_mime.size(); i++)
            mimes[i] = objs[i].toString();
        return mimes;
    }
}
