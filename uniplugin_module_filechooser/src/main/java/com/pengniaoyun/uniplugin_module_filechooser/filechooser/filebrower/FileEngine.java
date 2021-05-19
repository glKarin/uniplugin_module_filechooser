package com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower;

import android.text.TextUtils;

import java.io.File;

public class FileEngine extends FileEngine_base
{
    public FileEngine()
    {
        this(System.getProperty("user.home"));
    }

    public FileEngine(String path)
    {
        super();
        if(!TextUtils.isEmpty(path))
            SetCurrentPath(path);
    }

    protected boolean ListFiles(String path)
    {
        File dir;
        File files[];

        if(TextUtils.isEmpty(path))
            return false;

        dir = new File(path);
        if(!dir.isDirectory())
            return false;

        // TODO: 触发两次监听, 需要添加返回动作
        /*m_fileList.clear();
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, false);*/

        files = dir.listFiles();
        if(files == null)
        {
            // 当目录无法访问时也更新当前路径
            if(!path.equals(m_currentPath))
            {
                m_currentPath = path;
                if(m_onCurrentChangedListener != null)
                    m_onCurrentChangedListener.OnCurrentChanged(this, FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_PATH);
            }
            return false;
        }

        // listFiles函数可能返回null返回, 而导致无法调用监听函数, 在这里清空文件列表
        m_fileList.clear();
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_LIST);

        for(File f : files)
        {
            FileModel item = MakeFileModel(f);
            if(item == null)
                continue;
            m_fileList.add(item);
        }

        SortFileList();

        // 添加上级目录
        if(!m_ignoreDotDot)
        {
            FileModel item = MakeFileModelAsParent(dir);
            if(item != null)
                m_fileList.add(0, item);
        }

        int mask = FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_LIST;
        if(!path.equals(m_currentPath))
        {
            m_currentPath = path;
            mask |= FileBrowserCurrentChangedListener.ID_FILE_BROWSER_CURRENT_CHANGE_PATH;
        }
        if(m_onCurrentChangedListener != null)
            m_onCurrentChangedListener.OnCurrentChanged(this, mask);

        return true;
    }

}
