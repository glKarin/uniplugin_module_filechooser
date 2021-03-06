package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallFileChooserParamStruct;
import com.pengniaoyun.uniplugin_module_filechooser.common.ArrayAdapter_base;
import com.pengniaoyun.uniplugin_module_filechooser.common.ViewHolder;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower.FileEngine;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower.FileEngine_base;
import com.pengniaoyun.uniplugin_module_filechooser.filechooser.filebrower.FileHistoryEngine;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ActivityUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Common;
import com.pengniaoyun.uniplugin_module_filechooser.utility.FS;
import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;
import com.pengniaoyun.uniplugin_module_filechooser.utility.ModuleUtility;
import com.pengniaoyun.uniplugin_module_filechooser.utility.STL;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileChooserActivity extends Activity {
    private static final String ID_TAG = "FileChooserActivity";
    public static final String ID_CURRENT_PAGE_PATH = "CURRENT_PAGE_PATH";
    private static final int ID_REQUEST_PERMISSION_RESULT = 1;

    private ListView m_menuView;
    private ViewPager m_viewPager;
    private DrawerLayout m_drawerLayout;
    private boolean m_inited = false;
    private Set<FileModel> m_chooseFiles = new HashSet<FileModel>();
    private int m_currentPage = 0;
    private FileMenuListAdapter m_pageAdapter;

    private Map<String, Integer> m_iconMap = null;
    private Map<String, Integer> m_idMap = null;
    private Set<String> m_mimes = new HashSet<String>();
    private boolean m_multiple = false;
    private String m_mime = "*/*";
    private Menu m_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_mimes.clear();
        m_multiple = false;
        m_mime = "*/*";
        Serializable extra = getIntent().getSerializableExtra("request");
        if(extra != null && extra instanceof CallFileChooserParamStruct)
        {
            CallFileChooserParamStruct param = (CallFileChooserParamStruct)extra;
            if(!STL.CollectionIsEmpty(param.extra_mime))
            {
                for (Iterator<String> itor = param.extra_mime.iterator();
                     itor.hasNext();
                )
                {
                    m_mimes.add(itor.next());
                }
            }
            m_multiple = param.multiple;
            m_mime = param.mime;
        }

        int id = GetResId("layout.file_chooser_page");
        setContentView(id);
        id = GetResId("id.file_browser_drawer");
        m_drawerLayout = (DrawerLayout)findViewById(id);
        id = GetResId("id.file_browser_menu");
        m_menuView = (ListView)findViewById(id);
        id = GetResId("id.file_browser_content");
        m_viewPager = (ViewPager)findViewById(id);
        m_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                FileChooserActivity.this.SetCurrentPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        int color = getResources().getColor(GetResId("color.file_chooser_action_bar_color"));
        //m_drawerLayout.setScrimColor(Color.TRANSPARENT);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, m_drawerLayout, 0, 0);
        mToggle.syncState();
        m_drawerLayout.addDrawerListener(mToggle);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        SetupUI();
        ModuleUtility.Log(ID_TAG, "?????????????????????Activity");
    }

    private void SetupUI()
    {
        m_pageAdapter = new FileMenuListAdapter(this);
        m_menuView.setAdapter(m_pageAdapter);
        m_menuView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_viewPager.setCurrentItem(position);
                m_drawerLayout.closeDrawer(Gravity.LEFT, true);
            }
        });
    }

    private boolean CompareFileMIME(String mime)
    {
        if(STL.CollectionIsEmpty(m_mimes))
            return true; // not check
        return FS.CompareMIME(mime, m_mimes);
    }

    private int GetResId(String name)
    {
        if(m_idMap != null && m_idMap.containsKey(name))
            return m_idMap.get(name);
        int id = ActivityUtility.GetId(this, name, null);
        if(id > 0)
        {
            if(m_idMap == null)
                m_idMap = new HashMap<String, Integer>();
            m_idMap.put(name, id);
        }
        return id;
    }

    private Map<String, Integer> InitIconMap()
    {
        if(m_iconMap != null)
            return m_iconMap;
        m_iconMap = new HashMap<String, Integer>();
        // word
        int id = GetResId("drawable.icon_m_content_word");
        m_iconMap.put("application/msword", id);
        m_iconMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", id);
        // pdf
        id = GetResId("drawable.icon_m_content_pdf");
        m_iconMap.put("application/pdf", id);
        // xls
        id = GetResId("drawable.icon_m_content_excel");
        m_iconMap.put("application/vnd.ms-excel", id);
        m_iconMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", id);
        m_iconMap.put("text/csv", id); // csv
        // image
        id = GetResId("drawable.icon_m_content_image");
        m_iconMap.put("image/*", id);
        // audio
        id = GetResId("drawable.icon_m_content_audio");
        m_iconMap.put("audio/*", id);
        m_iconMap.put("application/x-shockwave-flash", id); // wav
        // video
        id = GetResId("drawable.icon_m_content_video");
        m_iconMap.put("video/*", id);
        m_iconMap.put("application/x-shockwave-flash", id);
        // m_iconMap.put("application/octet-stream", id); // flv??? bin
        // ppt
        id = GetResId("drawable.icon_m_content_powerpoint");
        m_iconMap.put("application/vnd.ms-powerpoint", id);
        m_iconMap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", id);
        // text
        id = GetResId("drawable.icon_m_content_text");
        m_iconMap.put("text/plain", id);
        // archive
        id = GetResId("drawable.icon_m_content_archive");
        m_iconMap.put("application/zip", id);
        m_iconMap.put("application/x-rar-compressed", id);
        m_iconMap.put("application/x-7z-compressed", id);
        m_iconMap.put("application/x-tar", id);
        m_iconMap.put("application/x-bzip", id);
        m_iconMap.put("application/x-bzip2", id);
        // application
        id = GetResId("drawable.icon_m_content_application");
        m_iconMap.put("application/java-archive", id); // jar
        m_iconMap.put("application/x-sh", id); // sh
        m_iconMap.put("application/vnd.android.package-archive", id); // apk
        // code
        id = GetResId("drawable.icon_m_content_document");
        m_iconMap.put("text/javascript", id);
        m_iconMap.put("application/json", id);
        m_iconMap.put("text/css", id);
        m_iconMap.put("text/html", id);
        // bin
        id = GetResId("drawable.icon_m_content_file");
        m_iconMap.put("{file}", id);
        // folder
        id = GetResId("drawable.icon_m_folder");
        m_iconMap.put("{folder}", id);
        id = GetResId("drawable.icon_m_parent_folder");
        m_iconMap.put("{parent_folder}", id);
        return m_iconMap;
    }

    private ViewHolder MakeViewHolder(View view, String names[])
    {
        ViewHolder viewHolder = new ViewHolder();
        for (String name : names)
        {
            viewHolder.Add(name, view.findViewById(GetResId(name)));
        }
        return viewHolder;
    }

    private FileEngine_base CreateFileEngine(String path)
    {
        FileEngine_base engine = null;
        if(path != null && path.startsWith(":"))
        {
            // if(":history".equals(path))
            {
                engine = new FileHistoryEngine(this);
            }
        }
        else
        {
            engine = new FileEngine(path);
        }
        if(TextUtils.isEmpty(m_mime))
            engine.SetMIMEs(m_mimes);
        else
        {
            if(!"*/*".equals(m_mime))
            {
                Set<String> m = new HashSet<String>();
                m.add(m_mime);
                engine.SetMIMEs(m);
            }
        }
        engine.SetShowHidden(false);
        return engine;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        int id = GetResId("menu.file_chooser_page_menu");
        getMenuInflater().inflate(id, menu);
        m_menu = menu;
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!ActivityUtility.IsGrantPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            if(!ActivityUtility.RequestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, ID_REQUEST_PERMISSION_RESULT))
                OpenPermissionGrantFailDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityUtility.IsGrantPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) && !m_inited)
        {
            FileMenuModel item;
            final List<FileMenuModel> menuList;

            menuList = new ArrayList<FileMenuModel>();
            m_inited = true;

            item = new FileMenuModel("??????", FS.ExternalStorageFilePath("/Download"), 0,false);
            menuList.add(item);

            item = new FileMenuModel("??????", FS.ExternalStorageFilePath("/Documents"), 0,false);
            menuList.add(item);

            item = new FileMenuModel("??????", FS.ExternalStorageFilePath("/Android/data/com.tencent.mm/MicroMsg/Download"), 0, false);
            menuList.add(item);

            item = new FileMenuModel("QQ", FS.ExternalStorageFilePath("/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv"), 0,false);
            menuList.add(item);

            item = new FileMenuModel("????????????", FS.ExternalStorageFilePath(null), 0, false);
            menuList.add(item);

            item = new FileMenuModel("????????????", ":history", 0,false);
            menuList.add(item);

            m_pageAdapter.SetData(menuList);
            m_viewPager.setAdapter(new FileViewPagerAdapter(menuList));

            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                int current = bundle.getInt(ID_CURRENT_PAGE_PATH);
                ModuleUtility.Log(ID_TAG, "????????????????????????: " + current);
                m_viewPager.setCurrentItem(current);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int openId = GetResId("id.file_chooser_menu_choose");
        int showHiddenId = GetResId("id.file_chooser_menu_hidden_file");
        int id = item.getItemId();
        if(id == openId)
        {
            Intent intent = new Intent();
            String files[] = null;
            if(!m_chooseFiles.isEmpty())
            {
                files = new String[m_chooseFiles.size()];
                int i = 0;
                for (Iterator<FileModel> itor = m_chooseFiles.iterator(); itor.hasNext(); i++)
                {
                    files[i] = itor.next().data.path;
                }
            }
            intent.putExtra("data", files);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        else if(id == showHiddenId)
        {
            item.setChecked(!item.isChecked());
            for (int i = 0; i < m_pageAdapter.getCount(); i++)
            {
                FileMenuModel menuItem = m_pageAdapter.getItem(i);
                if(menuItem.fileView == null || menuItem.fileView.m_adapter == null)
                    continue;
                menuItem.fileView.m_adapter.SetShowHidden(item.isChecked());
            }
            return true;
        }
        else if(id == android.R.id.home)
        {
            if(m_drawerLayout.isDrawerOpen(Gravity.LEFT))
                m_drawerLayout.closeDrawer(Gravity.LEFT, true);
            else
                m_drawerLayout.openDrawer(Gravity.LEFT, true);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModuleUtility.Log(ID_TAG, "?????????????????????Activity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ID_REQUEST_PERMISSION_RESULT)
        {
            int index = 0; // only storage
            if(grantResults[index] != PackageManager.PERMISSION_GRANTED)
            {
                OpenPermissionGrantFailDialog();
            }
        }
    }

    private void OpenPermissionGrantFailDialog()
    {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ActivityUtility.OpenAppSetting(FileChooserActivity.this);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                    default:
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????????????????????????????!");
        builder.setMessage("?????????????????????????????????????????????????????????");
        //builder.setIcon(R.drawable.icon_warning);
        builder.setCancelable(false);
        builder.setPositiveButton("??????", listener);
        builder.setNegativeButton("??????", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Choose(int index, boolean check, FileModel file)
    {
        FileViewPagerAdapter adapter = (FileViewPagerAdapter)m_viewPager.getAdapter();
        file.choose = check;
        if(m_multiple)
        {
            adapter.MultiChoose(m_currentPage, index, check);
        }
        else
        {
            m_chooseFiles.clear();
            adapter.Choose(m_currentPage, index, check);
        }
        if(check)
            m_chooseFiles.add(file);
        else
            m_chooseFiles.remove(file);
        UpdateChooseButton();
    }

    private void ClearChooseFile()
    {
        m_chooseFiles.clear();
        ((FileViewPagerAdapter)m_viewPager.getAdapter()).ClearChoose(m_currentPage);
        UpdateChooseButton();
    }

    private void UpdateChooseButton()
    {
        if(m_menu == null)
            return;
        MenuItem menuItem = m_menu.findItem(GetResId("id.file_chooser_menu_choose"));
        if(m_chooseFiles.isEmpty())
            menuItem.setVisible(false);
        else
        {
            menuItem.setTitle(getResources().getString(GetResId("string.file_chooser_menu_open")) + " (" + m_chooseFiles.size() + "???)");
            menuItem.setVisible(true);
        }
    }

    private void SetCurrentPage(int page)
    {
        //if(m_currentPage != page)
        {
            ClearChooseFile();
            m_currentPage = page;
        }
    }

    private static class FileModel
    {
        FileEngine.FileModel data;
        boolean choose = false;
        public FileModel(FileEngine.FileModel data)
        {
            this.data = data;
        }
        public static FileModel Make(FileEngine.FileModel data)
        {
            return new FileModel(data);
        }
    }

    private void OpenFileDetailDialog(int index, final FileModel model)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");

        int id = GetResId("layout.file_chooser_detail");

        View view = getLayoutInflater().inflate(id, null);
        final FileEngine.FileModel data = model.data;

        id = GetResId("id.file_chooser_detail_name");
        ((TextView)view.findViewById(id)).setText(data.name);
        id = GetResId("id.file_chooser_detail_path");
        ((TextView)view.findViewById(id)).setText(data.path);
        id = GetResId("id.file_chooser_detail_size");
        ((TextView)view.findViewById(id)).setText(FS.FormatSize(data.size) + " (" + data.size + " ??????)");
        id = GetResId("id.file_chooser_detail_mime");
        ((TextView)view.findViewById(id)).setText(data.mime);

        File file = new File(data.path);
        id = GetResId("id.file_chooser_detail_modify_time");
        ((TextView)view.findViewById(id)).setText(Common.TimestampToStr(data.time));

        id = GetResId("id.file_chooser_detail_read");
        ((TextView)view.findViewById(id)).setText(file.canRead() ? "???" : "???");
        id = GetResId("id.file_chooser_detail_write");
        ((TextView)view.findViewById(id)).setText(file.canWrite() ? "???" : "???");
        id = GetResId("id.file_chooser_detail_exec");
        ((TextView)view.findViewById(id)).setText(file.canExecute() ? "???" : "???");
        id = GetResId("id.file_chooser_detail_hidden");
        ((TextView)view.findViewById(id)).setText(file.isHidden() ? "???" : "???");

        builder.setView(view);

        builder.setIcon(GetFileIcon(data.type, data.mime, data.name));
        builder.setCancelable(true);
        builder.setNegativeButton("??????", null);
        if(FileChooserActivity.this.CompareFileMIME(data.mime))
        {
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FileChooserActivity.this.Choose(index, true, model);
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int GetFileIcon(int type, String mime, String name)
    {
        Map<String, Integer> map = FileChooserActivity.this.InitIconMap();
        if(type == FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY)
        {
            if("../".equals(name))
                return map.get("{parent_folder}"); // 0
            else
                return map.get("{folder}");
        }
        else
        {
            for (Iterator<Map.Entry<String, Integer>> itor = map.entrySet().iterator(); itor.hasNext(); )
            {
                Map.Entry<String, Integer> entry = itor.next();
                if(!entry.getKey().startsWith("{")) // special
                {
                    if(FS.CompareMIME(mime, entry.getKey()))
                        return entry.getValue();
                }
            }
            return map.get("{file}");
        }
    }

    // internal
    private class FileViewAdapter extends ArrayAdapter_base<FileModel>
    {
        private FileEngine_base m_fileEngine;
        private TextView m_partner;
        private ListView m_listView;

        public FileViewAdapter(Context context, String path, TextView partner, ListView listView)
        {
            super(context, FileChooserActivity.this.GetResId("layout.file_chooser_delegate"));
            m_partner = partner;
            m_listView = listView;
            m_fileEngine = FileChooserActivity.this.CreateFileEngine(path);
            ResetData(m_fileEngine.FileList());
            if(m_partner != null && m_fileEngine instanceof FileHistoryEngine)
                m_partner.setText(":?????????????????? " + getCount() + "???");
            m_fileEngine.SetOnCurrentChangedListener(new FileEngine.FileBrowserCurrentChangedListener() {
                @Override
                public void OnCurrentChanged(FileEngine_base browser, int mask) {
                    FileChooserActivity.this.ClearChooseFile();
                    if((mask & ID_FILE_BROWSER_CURRENT_CHANGE_LIST) != 0)
                    {
                        ResetData(browser.FileList());
                        if(m_partner != null && m_fileEngine instanceof FileHistoryEngine)
                            m_partner.setText(":?????????????????? " + getCount() + "???");
                    }
                    if((mask & ID_FILE_BROWSER_CURRENT_CHANGE_PATH) != 0)
                    {
                        if(m_partner != null && m_fileEngine instanceof FileEngine)
                            m_partner.setText(browser.CurrentPath());
                        // m_listView.smoothScrollToPosition(0);
                        m_listView.setSelection(0);
                    }
                }
            });
        }

        private void ResetData(List<FileEngine.FileModel> data)
        {
            List<FileModel> list = new ArrayList<FileModel>();
            for(Iterator<FileEngine.FileModel> itor = data.iterator(); itor.hasNext(); )
                list.add(new FileModel(itor.next()));
            SetData(list);
        }

        public View GenerateView(int position, View view, ViewGroup parent, final FileModel model)
        {
            TextView textView;
            ViewHolder viewHolder = (ViewHolder)view.getTag();
            if(viewHolder == null)
            {
                String names[] = {
                    "id.file_browser_delegate_name",
                    "id.file_browser_delegate_size",
                    "id.file_browser_delegate_time",
                    "id.file_browser_delegate_icon",
                    "id.file_browser_delegate_checkbox",
                };
                viewHolder = MakeViewHolder(view, names);
                view.setTag(viewHolder);
            }

            FileEngine.FileModel data = model.data;
            boolean ok = data.type != FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY && FileChooserActivity.this.CompareFileMIME(data.mime);
            textView = viewHolder.<TextView>Get_T("id.file_browser_delegate_name");
            textView.setText(data.name);
            int color;
            if(data.type == FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY)
                color = getResources().getColor(GetResId("color.file_list_delegate_name_folder_color"));
            else
                color = getResources().getColor(GetResId(ok ? "color.file_list_delegate_name_color" : "color.file_list_delegate_name_disable_color"));
            textView.setTextColor(color);
            textView = viewHolder.<TextView>Get_T("id.file_browser_delegate_size");
            textView.setText(FS.FormatSize(data.size));
            textView = viewHolder.<TextView>Get_T("id.file_browser_delegate_time");
            textView.setText(data.type == FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY ? "" : Common.TimestampToStr(data.time));
            ImageView iconView = viewHolder.<ImageView>Get_T("id.file_browser_delegate_icon");
            int icon = FileChooserActivity.this.GetFileIcon(data.type, data.mime, data.name);
            if(icon == 0)
            {
                iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                iconView.setImageBitmap(null);
            }
            else
            {
                if("../".equals(data.name))
                    iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                else
                    iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iconView.setImageResource(icon);
            }

            final CheckBox checkbox = viewHolder.<CheckBox>Get_T("id.file_browser_delegate_checkbox");
            checkbox.setChecked(model.choose);
            if(data.type == FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY)
            {
                checkbox.setOnClickListener(null);
                checkbox.setVisibility(View.INVISIBLE);
                checkbox.setEnabled(false);
            }
            else
            {
                checkbox.setEnabled(ok);
                checkbox.setVisibility(View.VISIBLE);
                checkbox.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(checkbox.isEnabled())
                            FileChooserActivity.this.Choose(position, checkbox.isChecked(), model);
                    }
                });
            }
            return view;
        }

        public void Open(int index)
        {
            FileModel model;

            model = getItem(index);
            if(model == null)
                return;
            FileEngine.FileModel item = model.data;
            if(item.type != FileEngine.FileModel.ID_FILE_TYPE_DIRECTORY) // TODO: ????????????????????????
            {
                ModuleUtility.Log(ID_TAG, "????????????(" + item.path + ")");
                //ActivityUtility.OpenExternally(FileChooserActivity.this, item.path);
                FileChooserActivity.this.OpenFileDetailDialog(index, model);
                return;
            }

            ModuleUtility.Log(ID_TAG, "???????????????(" + item.path + ")");
            SetPath(item.path);
        }

        public void SetPath(String path)
        {
            m_fileEngine.SetCurrentPath(path);
        }

        public void SetRoot(String root)
        {
            m_fileEngine.SetRoot(root);
        }

        public void SetShowHidden(boolean b)
        {
            m_fileEngine.SetShowHidden(b);
        }
    }

    private class FileView
    {
        public View view;
        private FileViewAdapter m_adapter;

        public FileView(View view, String path)
        {
            this.view = view;

            SetupUI(path);
        }

        private void SetupUI(String path)
        {
            ListView listView = GetListView();
            int id = FileChooserActivity.this.GetResId("id.file_browser_title");
            m_adapter = new FileViewAdapter(view.getContext(), path != null && path.startsWith(":") ? path : null, (TextView)view.findViewById(id), listView);
            listView.setAdapter(m_adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FileViewAdapter adapter;

                    adapter = (FileViewAdapter)parent.getAdapter();
                    adapter.Open(position);
                }
            });
        }

        public ListView GetListView()
        {
            ListView listView;
            int id = FileChooserActivity.this.GetResId("id.file_browser_list_view");

            listView = (ListView)view.findViewById(id);
            return listView;
        }
    }

    private class FileMenuModel
    {
        public String label;
        public String name;
        public int icon;
        public boolean can_go_up = true;
        public FileView fileView = null;

        public FileMenuModel(String label, String name, int icon, boolean can_go_up) {
            this.label = label;
            this.name = name;
            this.icon = icon;
            this.can_go_up = can_go_up;
        }
    }

    private class FileMenuListAdapter extends ArrayAdapter_base<FileMenuModel>
    {
        public FileMenuListAdapter(Context context)
        {
            super(context, FileChooserActivity.this.GetResId("layout.file_chooser_menu_delegate"));
        }

        public View GenerateView(int position, View view, ViewGroup parent, FileMenuModel data)
        {
            TextView textView;
            ViewHolder viewHolder = (ViewHolder)view.getTag();
            if(viewHolder == null)
            {
                String names[] = {
                    "id.file_browser_menu_delegate_name",
                    "id.file_browser_menu_delegate_path",
                };
                viewHolder = MakeViewHolder(view, names);
                view.setTag(viewHolder);
            }

            textView = viewHolder.<TextView>Get_T("id.file_browser_menu_delegate_name");
            textView.setText(data.label);
            textView = viewHolder.<TextView>Get_T("id.file_browser_menu_delegate_path");
            textView.setText(data.name);
            return view;
        }
    }

    private class FileViewPagerAdapter extends PagerAdapter
    {
        private List<FileMenuModel> m_menuList;

        public FileViewPagerAdapter(List<FileMenuModel> menuList)
        {
            m_menuList = menuList;
        }

        @Override
        public int getCount() {
            return m_menuList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater li;
            FileMenuModel item;

            li = LayoutInflater.from(container.getContext()); //getLayoutInflater();
            item = m_menuList.get(position);
            if(item.fileView == null)
            {
                int id = FileChooserActivity.this.GetResId("layout.file_chooser");
                View view = li.inflate(id, null, false);
                item.fileView = new FileView(view, item.name);
            }
            if(!item.can_go_up)
                item.fileView.m_adapter.SetRoot(item.name);
            item.fileView.m_adapter.SetPath(item.name);
            container.addView(item.fileView.view);
            return item.fileView.view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(m_menuList.get(position).fileView.view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return m_menuList.get(position).label;
        }

        public void ClearChoose(int index)
        {
            if(index >= m_menuList.size())
                return;
            FileMenuModel model = m_menuList.get(index);
            int count = model.fileView.m_adapter.getCount();
            for(int i = 0; i < count; i++)
            {
                model.fileView.m_adapter.getItem(i).choose = false;
            }
            model.fileView.m_adapter.notifyDataSetChanged();
        }

        public void Choose(int index, int itemIndex, boolean choose)
        {
            if(index >= m_menuList.size())
                return;
            FileMenuModel model = m_menuList.get(index);
            int count = model.fileView.m_adapter.getCount();
            if(itemIndex >= count)
                return;
            for(int i = 0; i < count; i++)
            {
                if(itemIndex == i)
                    model.fileView.m_adapter.getItem(i).choose = choose;
                else
                    model.fileView.m_adapter.getItem(i).choose = false;
            }
            model.fileView.m_adapter.notifyDataSetChanged();
        }

        public void MultiChoose(int index, int itemIndex, boolean choose)
        {
            if(index >= m_menuList.size())
                return;
            FileMenuModel model = m_menuList.get(index);
            int count = model.fileView.m_adapter.getCount();
            if(itemIndex >= count)
                return;
            model.fileView.m_adapter.getItem(itemIndex).choose = choose;
            model.fileView.m_adapter.notifyDataSetChanged();
        }
    }
}
