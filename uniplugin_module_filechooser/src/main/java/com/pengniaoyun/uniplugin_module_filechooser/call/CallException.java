package com.pengniaoyun.uniplugin_module_filechooser.call;

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
