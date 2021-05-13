package com.pengniaoyun.uniplugin_module_filechooser.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

	public static void f(String filePath, String tag, String format, Object ...args)
	{
		FileWriter writer = null;
		try
		{
			File file = new File(filePath);
			File dir = file.getParentFile();
			if(!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
				return;

			if(!file.exists())
			{
				if(!file.createNewFile())
					return;
			}
			writer = new FileWriter(file, true);
			String text = String.format("[%s@%s]", Common.Now(), tag) + String.format(format + System.getProperty("line.separator"), args);
			writer.write(text);
			writer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try
			{
				if(writer != null)
					writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void DumpException(Throwable e)
	{
		try
		{
			String tag = e instanceof Exception ? DEFAULT_RUNTIME_EXCEPTION_TAG : DEFAULT_RUNTIME_ERROR_TAG;
			String text = Common.ThrowableToString(e);
			e(tag, text);

			/*final String filePath = Environment.getExternalStorageDirectory()
					+ File.separator + "uniplugin_module_filechooser"
					+ File.separator + "filechooser_"
					+ Common.TimestampToDateStr(System.currentTimeMillis()) + ".log";
			f(filePath, tag, text);*/
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
