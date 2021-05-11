package com.pengniaoyun.uniplugin_module_filechooser.filechooser;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;

public interface FileChooserInterface
{
    public boolean OpenFileChooser(CallRequestStruct req, Object data);
    public boolean FileChooserResult(CallRequestStruct req, Object data);
}
