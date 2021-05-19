package com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower;

import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.STL;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FileEngine_base {
    public static final int ID_ORDER_BY_NAME = 1;
    public static final int ID_ORDER_BY_TIME = 2;
    public static final int ID_ORDER_BY_SIZE = 3;
    public static final int ID_ORDER_BY_TYPE = 4;

    public static final int ID_SEQUENCE_ASC = 1;
    public static final int ID_SEQUENCE_DESC = 2;

    public static final int ID_FILTER_FILE = 1;
    public static final int ID_FILTER_DIRECTORY = 1 << 1;
    public static final int ID_FILTER_ALL = ID_FILTER_FILE | ID_FILTER_DIRECTORY;

    protected String m_currentPath;
    protected Set<String> m_history;
    protected List<FileModel> m_fileList = null;
    protected int m_sequence = ID_SEQUENCE_ASC;
    protected int m_filter = 0;
    protected int m_order = ID_ORDER_BY_NAME;
    protected Set<String> m_mimes;
    protected boolean m_showHidden = true;
    protected boolean m_ignoreDotDot = false;
    protected FileBrowserCurrentChangedListener m_onCurrentChangedListener;
    protected String m_root = "/";

    public FileEngine_base()
    {
        m_history = new HashSet<String>();
        m_fileList = new ArrayList<FileModel>();
        m_mimes = new HashSet<String>();
    }

    protected abstract boolean ListFiles(String path);

    public FileEngine_base SetCurrentPath(String path)
    {
        if(path != null && !path.equals(m_currentPath) && CanOpen(path))
        {
            if(ListFiles(path))
            {
                //m_currentPath = path;
                m_history.add(m_currentPath);
            }
        }
        return this;
    }

    public void Rescan()
    {
        m_fileList.clear();
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_LIST);
        ListFiles(m_currentPath);
    }

    public String CurrentPath() {
        return m_currentPath;
    }

    public List<FileModel> FileList() {
        return m_fileList;
    }

    public FileModel GetFileModel(int index)
    {
        if(index >= m_fileList.size())
            return null;
        return m_fileList.get(index);
    }

    public FileEngine_base SetIgnoreDotDot(boolean b) {
        if(m_ignoreDotDot != b)
        {
            this.m_ignoreDotDot = b;
            ListFiles(m_currentPath);
        }
        return this;
    }

    public boolean ShowHidden() {
        return m_showHidden;
    }

    public FileEngine_base SetShowHidden(boolean showHidden) {
        if(m_showHidden != showHidden)
        {
            this.m_showHidden = showHidden;
            ListFiles(m_currentPath);
        }
        return this;
    }

    public FileEngine_base SetRoot(String root) {
        if(m_root != root)
        {
            this.m_root = root;
            ListFiles(m_currentPath);
        }
        return this;
    }

    protected boolean CanOpen(String path)
    {
        if(TextUtils.isEmpty(path))
            return false;
        if(TextUtils.isEmpty(m_root))
            return true;
        if(IsInRoot(path))
            return true;
        return(path.startsWith(m_root));
    }

    protected boolean IsInRoot(String path)
    {
        String root = TextUtils.isEmpty(m_root) ? "/" : m_root;
        return(root.equals(path));
    }

    public FileEngine_base SetOrder(int i) {
        if(m_order != i)
        {
            this.m_order = i;
            ListFiles(m_currentPath);
        }
        return this;
    }

    public FileEngine_base SetSequence(int i) {
        if(m_sequence != i)
        {
            this.m_sequence = i;
            ListFiles(m_currentPath);
        }
        return this;
    }

    public FileEngine_base SetMIMEs(Set<String> mimes) {
        this.m_mimes = mimes;
        ListFiles(m_currentPath);
        return this;
    }

    public FileEngine_base SetFilters(int filter) {
        int f = filter < 0 ? 0 : filter;
        if(m_filter != f)
        {
            this.m_filter = f;
            ListFiles(m_currentPath);
        }
        return this;
    }

    public FileEngine_base SetOnCurrentChangedListener(FileBrowserCurrentChangedListener listener) {
        if(m_onCurrentChangedListener != listener)
            m_onCurrentChangedListener = listener;
        return this;
    }

    public boolean CompareFileMIME(String mime)
    {
        if(STL.CollectionIsEmpty(m_mimes))
            return true; // not check
        return FS.CompareMIME(mime, m_mimes);
    }

    public boolean FilterFile(File f)
    {
        if(m_filter <= 0)
            return true; // not check
        if((m_filter & ID_FILTER_DIRECTORY) != 0 && !f.isDirectory())
            return false;
        if((m_filter & ID_FILTER_FILE) != 0 && !f.isFile())
            return false;
        return true;
    }

    public FileModel MakeFileModel(File f)
    {
        return MakeFileModel(f, true);
    }

    public FileModel MakeFileModel(File f, boolean check)
    {
        if(check && !FilterFile(f))
            return null;

        if(!m_showHidden && f.isHidden())
            return null;

        String name = f.getName();
        if(".".equals(name))
            return null;

        if(f.isDirectory())
            name += File.separator;

        String mime = f.isDirectory() ? null : FS.FileMIME(f.getAbsolutePath());
        if(check && f.isFile() && !CompareFileMIME(mime))
            return null;

        FileModel item = new FileModel();
        item.name = name;
        item.path = f.getAbsolutePath();
        item.size = f.length();
        item.time = f.lastModified();
        item.type = f.isDirectory() ? FileModel.ID_FILE_TYPE_DIRECTORY : FileModel.ID_FILE_TYPE_FILE;
        item.mime = mime;
        item.suffix = f.isDirectory() ? "/" : FS.GetFileCompleteSuffix(f.getAbsolutePath());
        return item;
    }

    public FileModel MakeFileModelAsParent(File f)
    {
        File dir = f.getParentFile();
        FileModel item = MakeFileModel(dir, false);
        item.name = "../";
        return item;
    }

    public static class FileModel
    {
        public static final int ID_FILE_TYPE_FILE = 0;
        public static final int ID_FILE_TYPE_DIRECTORY = 1;
        public static final int ID_FILE_TYPE_SYMBOL = 2;

        public String path;
        public String name;
        public long size;
        public int type;
        public String permission;
        public long time;
        public String mime;
        public String suffix;
    }

    public interface FileBrowserCurrentChangedListener
    {
        public static final int ID_FILE_BROWSER_CURRENT_CHANGE_PATH = 1;
        public static final int ID_FILE_BROWSER_CURRENT_CHANGE_LIST = 1 << 1;
        public static final int ID_FILE_BROWSER_CURRENT_CHANGE_ALL = 0xff;
        public void OnCurrentChanged(FileEngine_base browser, int mask);
    }

    protected void SortFileList()
    {
        Collections.sort(m_fileList, m_fileComparator);
        //m_fileList.sort(m_fileComparator);
    }

    private Comparator<FileModel> m_fileComparator = new Comparator<FileModel>(){
        @Override
        public int compare(FileModel a, FileModel b) {
            if("./".equals(a.name))
                return -1;
            if("../".equals(a.name))
                return -1;
            if(a.type != b.type)
            {
                if(a.type == FileModel.ID_FILE_TYPE_DIRECTORY)
                    return -1;
                if(b.type == FileModel.ID_FILE_TYPE_DIRECTORY)
                    return 1;
            }

            VarRef<Integer> res = new VarRef<Integer>();
            boolean unused = false;
            if(m_order == ID_ORDER_BY_TIME)
                unused = CompareFileTime(a, b, res) || CompareFileName(a, b, res);
            else if(m_order == ID_ORDER_BY_NAME)
                unused = CompareFileName(a, b, res);
            else if(m_order == ID_ORDER_BY_SIZE)
                unused = CompareFileSize(a, b, res) || CompareFileName(a, b, res);
            else if(m_order == ID_ORDER_BY_TYPE)
                unused = CompareFileType(a, b, res) || CompareFileName(a, b, res);

            if(m_sequence == ID_SEQUENCE_DESC)
                res.ref = -res.ref;

            return res.ref;
        }

        private boolean CompareFileTime(FileModel a, FileModel b, VarRef<Integer> ref)
        {
            int res = Long.signum(a.time - b.time);
            ref.ref = res;
            return res != 0;
        }

        private boolean CompareFileName(FileModel a, FileModel b, VarRef<Integer> ref)
        {
            int res = a.name.compareToIgnoreCase(b.name);
            ref.ref = res;
            return res != 0;
        }

        private boolean CompareFileSize(FileModel a, FileModel b, VarRef<Integer> ref)
        {
            int res = Long.signum(a.size - b.size);
            ref.ref = res;
            return res != 0;
        }

        private boolean CompareFileType(FileModel a, FileModel b, VarRef<Integer> ref)
        {
            int res = 0;
            if(a.mime != null)
                res = a.mime.compareToIgnoreCase(b.mime);
            else if(b.mime != null)
                res = -b.mime.compareToIgnoreCase(a.mime);
            ref.ref = res;
            return res != 0;
        }
    };
}
