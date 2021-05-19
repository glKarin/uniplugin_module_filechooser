package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.call.result.CallFileChooserResultStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.Constants;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.STL;

public class SystemFileChooser extends FileChooser_base
{
    public enum ContentType_e {
        CONTENT_TYPE_FILE,
        CONTENT_TYPE_DOCUMENT,
    };
    private ContentType_e m_contentType = ContentType_e.CONTENT_TYPE_FILE;

    public SystemFileChooser(Context context)
    {
        this(context, ContentType_e.CONTENT_TYPE_FILE);
    }

    public SystemFileChooser(Context context, ContentType_e type)
    {
        super(context);
        this.m_contentType = type;
    }

    public boolean OpenFileChooser(CallRequestStruct req, Object obj)
    {
        Log("OpenSystemFileChooser");
        if (m_context instanceof Activity)
        {
            CallFileChooserParamStruct params = req.<CallFileChooserParamStruct>GetParamStruct_T();
            Intent intent = null;

            if(m_contentType == ContentType_e.CONTENT_TYPE_DOCUMENT)
                intent = CreateDocumentIntent(params);
            else
                intent = CreateFileIntent(params);

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

    private Intent CreateFileIntent(CallFileChooserParamStruct params)
    {
        Log("OpenSystemFileChooser -> file");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(params.mime);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.multiple); //多选参数
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); // 仅本地
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        return intent;
    }

    private Intent CreateDocumentIntent(CallFileChooserParamStruct params)
    {
        Log("OpenSystemFileChooser -> document");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // 4.4
        {
            if (!params.extra_mime.isEmpty())
            {
                String mimes[] = params.ExtraMime();
                //Logf.e(params.extra_mime);
                //intent.setType(mimes[0]);
                intent.setType(params.mime);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
            }
            else
                intent.setType(params.mime);
        }
        else
        {
            if (!params.extra_mime.isEmpty())
                intent.setType(STL.CollectionJoin(params.extra_mime, "|"));
            else
                intent.setType(params.mime);
        }

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.multiple);//多选参数
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        return intent;
    }

    public boolean FileChooserResult(CallRequestStruct req, Object obj)
    {
        CallFileChooserResultStruct result = new CallFileChooserResultStruct(req.GetParamStruct().json);
        try
        {
            if(obj != null)
            {
                CallFileChooserParamStruct params = (CallFileChooserParamStruct)req.GetParamStruct();
                String mimes[] = params.ExtraMime();
                Intent data = (Intent) obj;
                if(data.getData() != null) // 单选
                {
                    result.select_count = 1;
                    Uri uri = data.getData();
                    Log("Single selection: " + uri);
                    String path = FS.UriPath(m_context, uri);
                    Log("path -> " + path);
                    boolean res = false;
                    if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT.equals(params.type))
                        res = result.AddFile(path);
                    else
                        res = result.AddFile(path, mimes);
                    if(res)
                        AddHistory(path);
                }
                else // 多选
                {
                    ClipData clipData = data.getClipData();
                    if (clipData != null)
                    {
                        int count = clipData.getItemCount();
                        Log("Multi selection: " + count);
                        result.select_count = count;
                        for (int i = 0; i < count; i++)
                        {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            Log("URI-" + i + " -> " + uri);
                            String path = FS.UriPath(m_context, uri);
                            Log("path-" + i + " -> " + path);
                            boolean res = false;
                            if(Constants.ENUM_FILE_CHOOSER_TYPE_SYSTEM_DOCUMENT.equals(params.type))
                                res = result.AddFile(path);
                            else
                                res = result.AddFile(path, mimes);
                            if(res)
                                AddHistory(path);
                        }
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
