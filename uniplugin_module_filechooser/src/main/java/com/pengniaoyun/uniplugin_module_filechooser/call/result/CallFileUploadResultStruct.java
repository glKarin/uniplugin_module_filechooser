package com.pengniaoyun.uniplugin_module_filechooser.call.result;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;

public class CallFileUploadResultStruct extends CallResultStruct
{
    private String m_responseType = Constants.ENUM_FILE_UPLOAD_RESPONSE_TYPE_TEXT;

    public CallFileUploadResultStruct(JSONObject param)
    {
        super(param, null);
    }

    public CallFileUploadResultStruct(JSONObject param, String responseType)
    {
        super(param, null);
        m_responseType = responseType;
    }

    public CallFileUploadResultStruct Set(String text)
    {
        if(Constants.ENUM_FILE_UPLOAD_RESPONSE_TYPE_JSON.equalsIgnoreCase(this.m_responseType))
        {
            try
            {
                this.data = JSONObject.parse(text);
            }
            catch (Exception e)
            {
                ModuleUtility.DumpException(e);
            }
        }
        else
            this.data = text;
        return this;
    }

    public Object MakeResult()
    {
        return data;
    }
}
