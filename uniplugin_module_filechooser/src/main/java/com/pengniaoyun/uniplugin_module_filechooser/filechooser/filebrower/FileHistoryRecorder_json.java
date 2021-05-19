package com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ActivityUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

public class FileHistoryRecorder_json extends FileHistoryRecorder
{
    private static final String ID_TAG = "FileHistoryRecorder_json";
    private static final String FILE_NAME = "uniplugin_module_filechooser_history.json";
    private String m_filePath;
    private long m_time = 0L;
    private static FileHistoryRecorder_json _instance = null;
    protected boolean m_dumpWhenModify = true;

    private FileHistoryRecorder_json(String path)
    {
        super();
        m_filePath = path;
        Restore();
    }

    private boolean SaveModifyIfNeed()
    {
        if(m_dumpWhenModify)
            return Dump();
        return true;
    }

    public void Clear()
    {
        super.Clear();
        SaveModifyIfNeed();
    }

    @Override
    public boolean Add(String path) {
        return this.Add(path, System.currentTimeMillis());
    }

    @Override
    public boolean Remove(String path) {
        boolean b = super.Remove(path);
        if(b)
            b = SaveModifyIfNeed();
        return b;
    }

    @Override
    public int Pop(int num) {
        int b = super.Pop(num);
        if(b > 0)
            SaveModifyIfNeed();
        return b;
    }
/*
    @Override
    protected boolean UpdateTime(FileHistoryItem item, long l)
    {
        boolean b = super.UpdateTime(item, l);
        if(b)
            SaveModifyIfNeed();
        return b;
    }*/

    @Override
    public boolean Add(String path, long time) {
        boolean b = super.Add(path, time);
        if(b)
            b = SaveModifyIfNeed();
        return b;
    }

    public static FileHistoryRecorder_json Instance(Context context)
    {
        if(_instance == null)
        {
            if(context == null)
                return null;
            if(!ActivityUtility.IsGrantPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || !ActivityUtility.IsGrantPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                return null;
            final String filePath = context.getExternalCacheDir()
                    + File.separator + FILE_NAME;

            ModuleUtility.Log(ID_TAG, "File history json file -> " + filePath);
            _instance = new FileHistoryRecorder_json(filePath);
        }
        return _instance;
    }

    private int Load(String text)
    {
        if(TextUtils.isEmpty(text))
            return 0;
        try
        {
            JSONObject json = JSONObject.parseObject(text);
            m_time = json.getLong("time");
            JSONArray list = json.getJSONArray("file");
            int res = 0;
            for (Iterator itor = list.iterator(); itor.hasNext(); )
            {
                JSONObject item = (JSONObject)itor.next();
                if(super.Add(item.getString("path"), item.getLong("time")))
                    res++;
            }
            return res;
        }
        catch (Exception e)
        {
            ModuleUtility.DumpException(e);
            return -1;
        }
    }

    private String MakeText()
    {
        JSONObject json = new JSONObject();
        json.put("time", System.currentTimeMillis());
        JSONArray arr = new JSONArray();
        for (Iterator<FileHistoryItem> itor = FileHistory().iterator(); itor.hasNext(); )
        {
            FileHistoryItem item = (FileHistoryItem)itor.next();
            JSONObject j = new JSONObject();
            j.put("path", item.path);
            j.put("time", item.time);
            arr.add(j);
        }
        json.put("file", arr);
        return json.toJSONString();
    }

    @Override
    public boolean Restore()
    {
        super.Clear();
        File file = new File(m_filePath);
        if(!file.isFile())
        {
            ModuleUtility.Log(ID_TAG, "读取本地文件不是文件");
            return true;
        }
        FileReader reader = null;
        boolean res = false;
        try
        {
            reader = new FileReader(file);
            int BUF_SIZE = 1024;
            char buf[] = new char[BUF_SIZE];
            int len = 0;
            StringBuffer sb = new StringBuffer();
            while((len = reader.read(buf)) > 0)
            {
                sb.append(buf, 0, len);
            }
            int count = Load(sb.toString());
            ModuleUtility.Log(ID_TAG, "加载本地文件数量 -> " + count);
            ModuleUtility.Log(ID_TAG, "加载本地文件历史成功");
            res = true;
        }
        catch (Exception e)
        {
            res = false;
            ModuleUtility.Log(ID_TAG, "加载本地文件历史失败");
            ModuleUtility.DumpException(e);
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (Exception e)
                {
                    ModuleUtility.DumpException(e);
                }
            }
        }
        return res;
    }

    @Override
    public boolean Dump()
    {
        File file = new File(m_filePath);
        if(file.exists() && !file.isFile())
        {
            ModuleUtility.Log(ID_TAG, "写入本地文件不是文件");
            return false;
        }
        File dir = file.getParentFile();
        if(!dir.exists())
        {
            ModuleUtility.Log(ID_TAG, "创建本地文件夹");
            if(!dir.mkdirs())
            {
                ModuleUtility.Log(ID_TAG, "创建本地文件夹失败");
                return false;
            }
        }
        FileWriter writer = null;
        boolean res = false;
        try
        {
            writer = new FileWriter(file);
            writer.append(MakeText());
            writer.flush();
            ModuleUtility.Log(ID_TAG, "写入本地文件历史成功");
            res = true;
        }
        catch (Exception e)
        {
            res = false;
            ModuleUtility.Log(ID_TAG, "写入本地文件历史失败");
            ModuleUtility.DumpException(e);
        }
        finally
        {
            if(writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (Exception e)
                {
                    ModuleUtility.DumpException(e);
                }
            }
        }
        return res;
    }
}
