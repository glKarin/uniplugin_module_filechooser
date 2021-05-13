package com.pengniaoyun.uniplugin_module_filechooser.utility;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Common
{
	private Common(){}

	public static String ThrowableToString(Throwable e)
	{
		if(e == null)
			return ""; // 不返回NULL

		StringBuffer sb = new StringBuffer();
		StackTraceElement arr[] = e.getStackTrace();

		String tag = e instanceof Exception ? "异常" : "运行错误";
		sb.append("[" + tag + "]").append('\n');
		sb.append("\t" + e.toString() + ": " + e.getMessage()).append('\n');
		for(StackTraceElement ste : arr)
		{
			sb.append("\t\tat " + ste.toString()).append('\n');
		}
		return sb.toString();
	}

	public static byte[] String8BitsByteArray(String str)
	{
		try
		{
			return str != null ? str.getBytes("ISO8859-1") : null;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			//return null;
			return str.getBytes();
		}
	}

	public static String TrimString(String str)
	{
		return str != null ? str.trim() : "";
	}

	public static <T> T dynamic_cast(Object obj)
	{
		try
		{
			return (T)obj;
		}
		catch (Exception e)
		{
			Logf.DumpException(e);
			return null;
		}
	}
	public static String TimestampToStr(long ts)
	{
		SimpleDateFormat format;

		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(ts));
	}

	public static String TimestampToDateStr(long ts)
	{
		SimpleDateFormat format;

		format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(ts));
	}

	public static String Now()
	{
		return TimestampToStr(System.currentTimeMillis());
	}

	public static boolean ArrayIsEmpty(Object arr[])
	{
		return arr == null || arr.length == 0;
	}
}
