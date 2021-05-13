package com.pengniaoyun.uniplugin_module_filechooser.call.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.common.CallException;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.common.ModuleUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.STL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CallFileUploadParamStruct extends CallParamStruct
{
    public String url;
    public String method = Constants.ENUM_FILE_UPLOAD_METHOD_POST;
    public Map<String, File> files = new HashMap<String, File>();
    public Object data = null;
    public Map<String, String> header = new HashMap<String, String>();
    public int timeout = 0;
    public String dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_JSON;
    public String responseType = Constants.ENUM_FILE_UPLOAD_RESPONSE_TYPE_TEXT;
    public boolean sslVerify = false;

    public CallFileUploadParamStruct()
    {
        super();
    }

    public CallFileUploadParamStruct(JSONObject json)
    {
        super(json);
        Set(json);
    }

    public void Set(JSONObject json)
    {
        String dataType = this.<String>Get_T("dataType", null);
        this.url = this.<String>Get_T("url", "");
        this.method = this.<String>Get_T("method", Constants.ENUM_FILE_UPLOAD_METHOD_POST);
        this.responseType = this.<String>Get_T("responseType", Constants.ENUM_FILE_UPLOAD_RESPONSE_TYPE_TEXT);
        this.sslVerify = this.<Boolean>Get_T("sslVerify", false);
        this.timeout = this.<Integer>Get_T("timeout", 0);
        this.files.clear();
        this.header.clear();

        Map<String, String> header = this.<Map<String, String>>Get_T("header", null);
        if(header != null)
        {
            header.forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String key, String value) {
                    CallFileUploadParamStruct.this.header.put(Common.TrimString(key), Common.TrimString(value));
                }
            });
        }

        Object dataObj = this.Get("data", null);
        if(dataObj != null)
        {
            if(Constants.ENUM_FILE_UPLOAD_DATA_TYPE_JSON.equalsIgnoreCase(dataType))
            {
                if(dataObj instanceof Map)
                {
                    Map<String, Object> data = (Map<String, Object>)dataObj;
                    CallFileUploadParamStruct.this.data = new HashMap<String, String>();
                    data.forEach(new BiConsumer<String, Object>() {
                        @Override
                        public void accept(String key, Object value) {
                            ((HashMap<String, String>)CallFileUploadParamStruct.this.data).put(Common.TrimString(key), Common.TrimString(value != null ? value.toString() : ""));
                        }
                    });
                    dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_JSON;
                }
                else
                {
                    try
                    {
                        Object data = JSONObject.parse(dataObj.toString());
                        if(data instanceof Map)
                        {
                            Map<String, String> map = new HashMap<String, String>();
                            for(Iterator itor = ((Map<String, Object>) data).entrySet().iterator();
                                itor.hasNext();
                            )
                            {
                                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itor.next();
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                map.put(Common.TrimString(key), Common.TrimString(value != null ? value.toString() : ""));
                            }
                            this.data = map;
                            dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_JSON;
                        }
                        else
                        {
                            throw new CallException("data不是有效的json字符串");
                        }
                    }
                    catch (Exception e)
                    {
                        ModuleUtility.DumpException(e);
                        dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT;
                    }
                }
            }
            else if(Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT.equalsIgnoreCase(dataType))
            {
                if(dataObj instanceof Map)
                {
                    try
                    {
                        String text = JSONObject.toJSONString(dataObj);
                        this.data = text;
                    }
                    catch (Exception e)
                    {
                        ModuleUtility.DumpException(e);
                    }
                    dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT;
                }
                else
                {
                    this.data = dataObj.toString();
                    dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT;
                }
            }
            else
            {
                if(dataObj instanceof Map)
                {
                    Map<String, Object> data = (Map<String, Object>)dataObj;
                    CallFileUploadParamStruct.this.data = new HashMap<String, String>();
                    data.forEach(new BiConsumer<String, Object>() {
                        @Override
                        public void accept(String key, Object value) {
                            ((HashMap<String, String>)CallFileUploadParamStruct.this.data).put(Common.TrimString(key), Common.TrimString(value != null ? value.toString() : ""));
                        }
                    });
                    dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_JSON;
                }
                else
                {
                    this.data = dataObj.toString();
                    dataType = Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT;
                }
            }
        }
        this.dataType = TextUtils.isEmpty(dataType) ? Constants.ENUM_FILE_UPLOAD_DATA_TYPE_TEXT : dataType;

        Object fileObj = this.Get("files", null);
        if(fileObj != null)
        {
            if(fileObj instanceof Map)
            {
                Map<String, String> files = (Map<String, String>)fileObj;
                files.forEach(new BiConsumer<String, String>() {
                    @Override
                    public void accept(String key, String value) {
                        CallFileUploadParamStruct.this.files.put(Common.TrimString(key), new File(Common.TrimString(value)));
                    }
                });
            }
            else
            {
                this.files.put("file", new File(fileObj.toString()));
            }
        }
    }

    public boolean IsFileUpload()
    {
        return !files.isEmpty();
    }

    public boolean IsValid()
    {
        if(CheckInvalidFiles() > 0)
            return false;
        if(TextUtils.isEmpty(url))
            return false;
        return true;
    }

    public int CheckInvalidFiles()
    {
        if(files.isEmpty())
            return 0;

        int res = 0;
        for(Iterator<Map.Entry<String, File>> itor = files.entrySet().iterator();
            itor.hasNext();
        )
        {
            Map.Entry<String, File> entry = itor.next();
            File file = entry.getValue();
            if(file == null || !file.exists() || !file.isFile())
                res++;
        }
        return res;
    }

    public String ResetMethod()
    {
        if(TextUtils.isEmpty(method))
        {
            method = files.isEmpty() ? Constants.ENUM_FILE_UPLOAD_METHOD_GET : Constants.ENUM_FILE_UPLOAD_METHOD_POST;
        }
        else
        {
            if(Constants.ENUM_FILE_UPLOAD_METHOD_GET.equalsIgnoreCase(method))
                method = Constants.ENUM_FILE_UPLOAD_METHOD_POST;
        }
        method = method.toLowerCase();
        return method;
    }

    public Map<String, Object> CombineParam()
    {
        Map<String, Object> res = new HashMap<String, Object>();

        if(data != null && data instanceof Map)
        {
            for(Iterator<Map.Entry<String, String>> itor = ((Map<String, String>)data).entrySet().iterator();
                itor.hasNext();
            )
            {
                Map.Entry<String, String> entry = itor.next();
                res.put(entry.getKey(), entry.getValue());
            }
        }

        for(Iterator<Map.Entry<String, File>> itor = files.entrySet().iterator();
            itor.hasNext();
        )
        {
            Map.Entry<String, File> entry = itor.next();
            res.put(entry.getKey(), entry.getValue());
        }

        return res;
    }
}
