package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;

public class PageFileChooser extends FileChooser_base
{
    public PageFileChooser(Context context)
    {
        super(context);
    }

    public boolean OpenFileChooser(CallRequestStruct req, Object obj)
    {
        Log("OpenPageFileChooser");
        if (m_context instanceof Activity)
        {
            CallFileChooserParamStruct params = req.<CallFileChooserParamStruct>GetParamStruct_T();
            Intent intent = new Intent(m_context, FileChooserActivity.class);
            intent.putExtra("request", params);

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
                Intent data = (Intent)obj;
                String files[] = data.getStringArrayExtra("data");
                if (files != null)
                {
                    int count = files.length;
                    Log("Selection(s): " + count);
                    result.select_count = count;
                    for (int i = 0; i < count; i++)
                    {
                        String path = files[i];
                        Log("file-" + i + " -> " + path);
                        result.AddFile(path);
                    }
                }
            }
            else
                Log("No selection");
            this.Callback(req, result);
        }
        catch (Exception e)
        {
            ModuleUtility.DumpException(e);
            req.CallFailCallback(Common.ThrowableToString(e));
        }
        req.CallFinalCallback();

        return true;
    }

}
