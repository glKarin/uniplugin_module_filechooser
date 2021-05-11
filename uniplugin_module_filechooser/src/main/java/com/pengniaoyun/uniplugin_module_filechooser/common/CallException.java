package com.pengniaoyun.uniplugin_module_filechooser.common;

public class CallException extends Exception
{
    public CallException(String message)
    {
        super(message);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
