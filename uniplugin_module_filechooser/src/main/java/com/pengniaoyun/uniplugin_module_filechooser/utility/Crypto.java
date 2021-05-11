package com.pengniaoyun.uniplugin_module_filechooser.utility;
import java.security.*;
import java.io.*;

public final class Crypto
{
	private Crypto(){}
	
	public static String MD5(String str)
	{
		if(str == null || str.isEmpty())
			return null;
		return MD5(str.getBytes());
	}
		
	public static String MD5(byte data[])
	{
		if(data == null || data.length == 0)
			return null;
		MessageDigest md = null;
		
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		byte d[] = md.digest(data);
		StringBuffer buf = new StringBuffer();
		for(byte b : d)
		{
			String s = Integer.toHexString(b & 0xff);
			if(s.length() == 1)
				s = "0" + s;
			buf.append(s);
		}
		return buf.toString();
	}
	
	public static String MD5(File file)
	{
		if(file == null || !file.isFile())
			return null;
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return MD5(is);
	}
	
	public static String MD5(InputStream is)
	{
		if(is == null)
			return null;
		MessageDigest md = null;

		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		final int Buf_Len = 4096;
		int len = 0;
		byte vbuf[] = new byte[Buf_Len];
		try
		{
			while((len = is.read(vbuf, 0, Buf_Len)) > 0)
			{
				md.update(vbuf, 0, len);
			}
			is.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		byte d[] = md.digest();
		StringBuffer buf = new StringBuffer();
		for(byte b : d)
		{
			String s = Integer.toHexString(b & 0xff);
			if(s.length() == 1)
				s = "0" + s;
			buf.append(s);
		}
		return buf.toString();
	}
}
