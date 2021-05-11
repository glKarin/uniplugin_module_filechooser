package com.pengniaoyun.uniplugin_module_filechooser.call.request;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;

public class CallFileChooserParamStruct extends CallParamStruct
{
    public String type = Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM;
    public String mime = "*/*";
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
    }
}
