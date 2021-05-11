package com.pengniaoyun.uniplugin_module_filechooser.fileupload;

import com.pengniaoyun.uniplugin_module_filechooser.call.request.CallRequestStruct;
import com.pengniaoyun.uniplugin_module_filechooser.utility.VarRef;

public interface FileUploadInterface
{
    public boolean UploadFile(CallRequestStruct req, VarRef data);
}
