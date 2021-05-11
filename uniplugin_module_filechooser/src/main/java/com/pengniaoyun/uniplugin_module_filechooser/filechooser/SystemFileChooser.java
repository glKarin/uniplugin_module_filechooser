package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.CallException;
import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

public class SystemFileChooser extends FileChooser_base {

    public SystemFileChooser(Context context)
    {
        super(context);
    }

    public boolean OpenFileChooser(CallRequestStruct req, Object obj)
    {
        Log("OpenSystemFileChooser");
        if (m_context instanceof Activity)
        {
            CallFileChooserParamStruct params = req.<CallFileChooserParamStruct>GetParamStruct_T();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(params.mime);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.multiple);//多选参数
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            Activity activity = (Activity)m_context;
            activity.startActivityForResult(intent, ((Integer)obj).intValue());

            Log("Activity");
            return true;
        }
        else
        {
            req.CallFailCallback("缺失上下文");
            req.CallFinalCallback(); // finish
            Log("Not Activity");
            return false;
        }
    }

    public boolean FileChooserResult(CallRequestStruct req, Object obj)
    {
        CallFileChooserResultStruct result = new CallFileChooserResultStruct(req.GetParamStruct().json);
        try
        {
            if(obj != null)
            {
                Intent data = (Intent) obj;
                if(data.getData() != null)
                {
                    result.AddFile(FS.UriPath(m_context, data.getData()));
                }
                else
                {
                    ClipData clipData = data.getClipData();
                    if (clipData != null)
                    {
                        for (int i = 0; i < clipData.getItemCount(); i++)
                        {
                            ClipData.Item item = clipData.getItemAt(i);
                            Log(item.toString());
                            Uri uri = item.getUri();
                            result.AddFile(FS.UriPath(m_context, uri));
                        }
                    }
                }
            }
            this.Callback(req, result);
        }
        catch (Exception e)
        {
            Logf.DumpException(e);
            req.CallFailCallback(e.getMessage());
        }
        req.CallFinalCallback();

        return true;
    }

}
