package com.pengniaoyun.uniplugin_module_filechooser.utility;

import android.util.Log;

public final class Logf
{
	private static final String ID_TAG = "Logf";

	private static final String DEFAULT_INFO_TAG = "_Info";
	private static final String DEFAULT_ERROR_TAG = "_Error";
	private static final String DEFAULT_WARNING_TAG = "_Warning";
	private static final String DEFAULT_DEBUG_TAG = "_Debug";
	private static final String DEFAULT_VERBOSE_TAG = "_Verbose";
	private static final String DEFAULT_RUNTIME_EXCEPTION_TAG = "_Runtime_exception";
	private static final String DEFAULT_RUNTIME_ERROR_TAG = "_Runtime_error";
	
	private Logf(){}

	public static void DumpException(Throwable e)
	{
		try
		{
			e(e instanceof Exception ? DEFAULT_RUNTIME_EXCEPTION_TAG : DEFAULT_RUNTIME_ERROR_TAG, Common.ThrowableToString(e));
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	public static void e(String tag, String format, Object ...args)
	{
		Log.e(tag == null ? DEFAULT_ERROR_TAG : tag, format == null ? "null" : String.format(format, args));
	}
	
	public static void d(String tag, String format, Object ...args)
	{
		Log.d(tag == null ? DEFAULT_DEBUG_TAG : tag, format == null ? "null" : String.format(format, args));
	}
	
	public static void i(String tag, String format, Object ...args)
	{
		Log.i(tag == null ? DEFAULT_INFO_TAG : tag, format == null ? "null" : String.format(format, args));
	}
	
	public static void v(String tag, String format, Object ...args)
	{
		Log.v(tag == null ? DEFAULT_VERBOSE_TAG : tag, format == null ? "null" : String.format(format, args));
	}
	
	public static void w(String tag, String format, Object ...args)
	{
		Log.w(tag == null ? DEFAULT_WARNING_TAG : tag, format == null ? "null" : String.format(format, args));
	}

	public static void i(String format, Object ...args)
	{
		i(null, format, args);
	}
	
	public static void d(String format, Object ...args)
	{
		d(null, format, args);
	}
	
	public static void w(String format, Object ...args)
	{
		w(null, format, args);
	}
	
	public static void e(String format, Object ...args)
	{
		e(null, format, args);
	}
	
	public static void v(String format, Object ...args)
	{
		v(null, format, args);
	}
	
	public static void e(String tag, Object obj)
	{
		Log.e(tag == null ? DEFAULT_ERROR_TAG : tag, obj == null ? "null" : obj.toString());
	}

	public static void i(String tag, Object obj)
	{
		Log.i(tag == null ? DEFAULT_INFO_TAG : tag, obj == null ? "null" : obj.toString());
	}

	public static void d(String tag, Object obj)
	{
		Log.d(tag == null ? DEFAULT_DEBUG_TAG : tag, obj == null ? "null" : obj.toString());
	}

	public static void w(String tag, Object obj)
	{
		Log.w(tag == null ? DEFAULT_WARNING_TAG : tag, obj == null ? "null" : obj.toString());
	}

	public static void v(String tag, Object obj)
	{
		Log.v(tag == null ? DEFAULT_VERBOSE_TAG : tag, obj == null ? "null" : obj.toString());
	}

	public static void i(Object obj)
	{
		i(null, obj);
	}

	public static void d(Object obj)
	{
		d(null, obj);
	}

	public static void e(Object obj)
	{
		e(null, obj);
	}

	public static void v(Object obj)
	{
		v(null, obj);
	}

	public static void w(Object obj)
	{
		w(null, obj);
	}
}
