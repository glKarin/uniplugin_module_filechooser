package com.pengniaoyun.uniplugin_module_filechooser.network;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Crypto;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;

public final class HttpUtility
{
    private HttpUtility(){}

    public static byte[] UploadFiles(String url, String method, Map<String, Object> params, Map<String, String> headers, int timeout)
    {
        ByteArrayOutputStream os = null;
        FileInputStream fis = null;
        String randStr = android.util.Base64.encodeToString(("" + System.currentTimeMillis()).getBytes(), android.util.Base64.NO_WRAP);
        String boundaryValue = Crypto.MD5(randStr).substring(8, 8 + 16);
        String boundary = "----WebKitFormBoundary" + boundaryValue;
        byte ret[] = null;
        final int BUF_LEN = 1024;

        os = new ByteArrayOutputStream();

        try
        {
            for (Iterator<Map.Entry<String, Object> > itor = params.entrySet().iterator();
                 itor.hasNext(); )
            {
                Map.Entry<String, Object> entry = itor.next();
                String nameParam = entry.getKey();
                Object obj = entry.getValue();

                if(obj != null && obj instanceof File) // 文件上传
                {
                    File file = (File)obj;
                    String filename = file.getName();
                    String filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl("file://" + file.getAbsolutePath()));

                    fis = new FileInputStream(file);
                    byte filedata[] = new byte[BUF_LEN];
                    int len = 0;

                    os.write(Common.String8BitsByteArray("--"));
                    os.write(Common.String8BitsByteArray(boundary));
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(("Content-Disposition: form-data; name=\"" + nameParam + "\"; filename=\"" + filename + "\"").getBytes());
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(("Content-Type: " + filetype).getBytes());
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(Common.String8BitsByteArray("\r\n"));
                    while((len = fis.read(filedata)) > 0)
                    {
                        os.write(filedata, 0, len);
                    }
                    fis.close();
                    fis = null;
                    os.write(Common.String8BitsByteArray("\r\n"));
                }
                else // 普通字段
                {
                    String str = obj != null ? obj.toString() : "";

                    os.write(Common.String8BitsByteArray("--"));
                    os.write(Common.String8BitsByteArray(boundary));
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(Common.String8BitsByteArray("Content-Disposition: form-data; name=\"" + nameParam + "\""));
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(Common.String8BitsByteArray("\r\n"));
                    os.write(str.getBytes());
                    os.write(Common.String8BitsByteArray("\r\n"));
                }

                os.flush();
            }

            os.write(Common.String8BitsByteArray("--"));
            os.write(Common.String8BitsByteArray(boundary));
            os.write(Common.String8BitsByteArray("--"));

            os.flush();

            NetworkAccessManager manager = new NetworkAccessManager();
            NetworkRequest req = new NetworkRequest(url);

            req.SetHeaders(headers);
            req.AddHeader("Content-Type", "multipart/form-data;boundary=" + boundary);
            req.SetData(os.toByteArray());
            req.SetMethod(method);

            manager.SetTimeout(timeout);

            NetworkReply reply = manager.SyncRequest(req);
            if(reply != null && reply.GetResponseResult())
            {
                ret = reply.GetReplyData();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try
            {
                if(fis != null)
                    fis.close();
                if(os != null)
                    os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return ret;
    }

    public static byte[] Request(String url, String method, Object data, Map<String, String> headers, int timeout)
    {
        byte[] ret = null;

        NetworkAccessManager manager = new NetworkAccessManager();
        NetworkRequest req = new NetworkRequest(url);

        req.SetHeaders(headers);
        if(data != null)
        {
            if(data instanceof Map)
            {
                if("application/json".equals(req.HeaderValue("Content-Type")))
                    req.SetData(JSONObject.toJSONBytes(data));
                else
                {
                    req.AddHeader("Content-Type", "application/x-www-form-urlencoded");
                    req.SetParams((Map<String, Object>)data);
                }
            }
            else
            {
                req.SetData(data.toString().getBytes());
            }
        }
        req.SetMethod(method);

        manager.SetTimeout(timeout);

        NetworkReply reply = manager.SyncRequest(req);
        if(reply != null && reply.GetResponseResult())
        {
            ret = reply.GetReplyData();
        }

        return ret;
    }
}

