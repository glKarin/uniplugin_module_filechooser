package com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower;

import android.content.Context;
import android.text.TextUtils;

import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

import java.io.File;
import java.util.List;

public class FileHistoryEngine extends FileEngine_base
{
    private FileHistoryRecorder m_recorder = null;

    public FileHistoryEngine(Context context)
    {
        super();
        m_recorder = FileHistoryRecorder_json.Instance(context);
        m_ignoreDotDot = true;
        m_sequence = ID_SEQUENCE_DESC;
        m_order = ID_ORDER_BY_TIME;
    }

    public FileEngine_base SetCurrentPath(String path)
    {
        m_currentPath = path;
        ListFiles(path);
        return this;
    }

    @Override
    protected boolean CanOpen(String path) {
        return false;
    }

    protected boolean ListFiles(String path)
    {
        List<FileHistoryRecorder.FileHistoryItem> files;

        if(m_recorder == null)
            return false;

        files = m_recorder.FileHistory();

        m_fileList.clear();
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_LIST);

        for(FileHistoryRecorder.FileHistoryItem f : files)
        {
            FileModel item = MakeFileModel(new File(f.path));
            if(item == null)
                continue;
            item.time = f.time;
            m_fileList.add(item);
        }

        SortFileList();

        int mask = FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_LIST;
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, mask);

        return true;
    }

}
