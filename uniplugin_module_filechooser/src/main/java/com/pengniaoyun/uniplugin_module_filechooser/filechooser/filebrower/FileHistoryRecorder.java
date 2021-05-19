package com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower;

import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class FileHistoryRecorder
{
    protected List<FileHistoryItem> m_fileHistory = new ArrayList<FileHistoryItem>();
    protected int m_maxCount = 20;

    protected FileHistoryRecorder()
    {
    }

    public List<FileHistoryItem> FileHistory()
    {
        return m_fileHistory;
    }

    public boolean Add(String path)
    {
        return Add(path, System.currentTimeMillis());
    }

    public boolean Add(String path, long time)
    {
        if(TextUtils.isEmpty(path))
            return false;
        FileHistoryItem item = Get(path);
        if(item != null)
        {
            if(item.IsValid())
                return UpdateTime(item, time);
            else
            {
                m_fileHistory.remove(item);
                return true;
            }
        }
        item = FileHistoryItem.Make(path, time);
        if(item == null)
            return false;
        int i = 0;
        for(; i < m_fileHistory.size(); i++)
        {
            if(m_fileHistory.get(i).time <= time)
                break;
        }
        m_fileHistory.add(i, item);
        if(IsFull())
            Pop(0);
        return true;
    }

    public boolean Remove(String path)
    {
        if(TextUtils.isEmpty(path))
            return false;
        FileHistoryItem item = Get(path);
        if(item == null)
            return false;
        m_fileHistory.remove(item);
        return true;
    }

    public int Pop(int num)
    {
        if(m_fileHistory.isEmpty())
            return 0;

        int i = 0;
        int n = num == 0 ? m_fileHistory.size() - m_maxCount : num;
        while(i < n)
        {
            m_fileHistory.remove(m_fileHistory.size() - 1);
            i++;
            if(m_fileHistory.isEmpty())
                break;
        }
        return i;
    }

    public boolean Exists(String path)
    {
        if(TextUtils.isEmpty(path))
            return false;
        for (Iterator<FileHistoryItem> itor = m_fileHistory.iterator(); itor.hasNext(); )
        {
            if(path.equals(itor.next().path))
                return true;
        }
        return false;
    }

    public FileHistoryItem Get(String path)
    {
        if(TextUtils.isEmpty(path))
            return null;
        for (Iterator<FileHistoryItem> itor = m_fileHistory.iterator(); itor.hasNext(); )
        {
            FileHistoryItem item = itor.next();
            if(path.equals(item.path))
                return item;
        }
        return null;
    }

    public long GetTime(String path)
    {
        FileHistoryItem item = Get(path);
        if(item == null)
            return -1;
        return item.time;
    }

    public int Count()
    {
        return m_fileHistory.size();
    }

    public void Clear()
    {
        m_fileHistory.clear();
    }

    public abstract boolean Dump();
    public abstract boolean Restore();

    public boolean IsEmpty()
    {
        return m_fileHistory.isEmpty();
    }

    public boolean IsFull()
    {
        return m_fileHistory.size() >= m_maxCount;
    }

    public FileHistoryRecorder SetMaxCount(int maxCount)
    {
        if(m_maxCount != maxCount)
        {
            m_maxCount = maxCount;
            Pop(0);
        }
        return this;
    }

    protected boolean UpdateTime(FileHistoryItem item, long l)
    {
        boolean b = item.UpdateTime(l);
        if(b)
            SortHistory();
        return b;
    }

    public static class FileHistoryItem
    {
        public String path;
        public long time;

        public FileHistoryItem()
        {
        }

        public FileHistoryItem(String path)
        {
            this(path, System.currentTimeMillis());
        }

        public FileHistoryItem(String path, long time)
        {
            this.path = path;
            this.time = time;
        }

        public static FileHistoryItem Make(String path)
        {
            return Make(path, System.currentTimeMillis());
        }

        public static FileHistoryItem Make(String path, long time)
        {
            File file = new File(path);
            if(!file.exists())
                return null;
            return new FileHistoryItem(path, time);
        }

        public boolean IsValid()
        {
            return !TextUtils.isEmpty(path) && (new File(path)).exists();
        }

        public boolean UpdateTime(long l)
        {
            if(this.time == l)
                return false;
            this.time = l;
            return true;
        }

        public boolean UpdateTime()
        {
            return UpdateTime(System.currentTimeMillis());
        }
    }

    protected void SortHistory()
    {
        Collections.sort(m_fileHistory, m_fileComparator);
        //m_fileHistory.sort(m_fileComparator);
    }

    private Comparator<FileHistoryItem> m_fileComparator = new Comparator<FileHistoryItem>(){
        @Override
        public int compare(FileHistoryItem a, FileHistoryItem b) {
            VarRef<Integer> res = new VarRef<Integer>();
            boolean unused = CompareFileTime(a, b, res) || CompareFilePath(a, b, res);

            return res.ref;
        }

        private boolean CompareFileTime(FileHistoryItem a, FileHistoryItem b, VarRef<Integer> ref)
        {
            int res = Long.signum(a.time - b.time);
            ref.ref = -res;
            return res != 0;
        }

        private boolean CompareFilePath(FileHistoryItem a, FileHistoryItem b, VarRef<Integer> ref)
        {
            int res = a.path.compareToIgnoreCase(b.path);
            ref.ref = res;
            return res != 0;
        }
    };
}
