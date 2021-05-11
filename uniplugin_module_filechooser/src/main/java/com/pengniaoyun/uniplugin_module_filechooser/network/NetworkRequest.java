package com.pengniaoyun.uniplugin_module_filechooser.network;

import java.util.*;
import java.net.*;

public class NetworkRequest
{
	private static final String ID_TAG = "NetworkRequest";
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String REQUEST_METHOD_PUT = "PUT";
	public static final String REQUEST_METHOD_DELETE = "DELETE";
	
	private String m_url;
	private String m_method = NetworkRequest.REQUEST_METHOD_GET;
	private Map<String, Object> m_params = null;
	private Map<String, Object> m_headers = null;
	private byte m_data[];
	private URL m_uri = null;
	
	public NetworkRequest()
	{
		super();
	}
	
	public NetworkRequest(String url)
	{
		SetUrl(url);
	}

	public NetworkRequest(String url, String method)
	{
		SetUrl(url);
		SetMethod(method);
	}
	
	public NetworkRequest SetUrl(String url)
	{
		this.m_url = url;
		return this;
	}
	
	public NetworkRequest SetMethod(String method)
	{
		this.m_method = method;
		return this;
	}
	
	public String Method()
	{
		return m_method;
	}

	public URL Uri()
	{
		return m_uri;
	}
	
	public NetworkRequest AddParam(String name, Object value)
	{
		if(m_params == null)
			m_params = new HashMap<String, Object>();
		m_params.put(name, value);
		return this;
	}
	
	public NetworkRequest RemoveParam(String name)
	{
		if(m_params != null)
			m_params.remove(name);
		return this;
	}

	public NetworkRequest SetParams(Map<String, ? extends Object> params)
	{
		if(m_params == null)
			m_params = new HashMap<String, Object>();
		else
			m_params.clear();
		if(params != null && !params.isEmpty())
		{
			for(Iterator<? extends Map.Entry<String, ?>> itor = params.entrySet().iterator();
				itor.hasNext();
			)
			{
				Map.Entry<String, ? extends Object> entry = itor.next();
				m_params.put(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public NetworkRequest AddHeader(String name, Object value)
	{
		if(m_headers == null)
			m_headers = new HashMap<String, Object>();
		m_headers.put(name, value);
		return this;
	}

	public NetworkRequest RemoveHeader(String name)
	{
		if(m_headers != null)
			m_headers.remove(name);
		return this;
	}

	public NetworkRequest SetHeaders(Map<String, ? extends Object> headers)
	{
		if(m_headers == null)
			m_headers = new HashMap<String, Object>();
		else
			m_headers.clear();
		if(headers != null && !headers.isEmpty())
		{
			for(Iterator<? extends Map.Entry<String, ?>> itor = headers.entrySet().iterator();
				itor.hasNext();
			)
			{
				Map.Entry<String, ? extends Object> entry = itor.next();
				m_headers.put(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}
	
	public Iterator<String> HeaderIterator()
	{
		if(m_headers != null)
			return m_headers.keySet().iterator();
		return null;
	}
	
	public String HeaderValue(String name)
	{
		if(m_headers == null || m_headers.isEmpty())
			return null;
		return m_headers.get(name).toString();
	}
	
	public NetworkRequest SetData(byte data[])
	{
		m_data = data;
		return this;
	}

	public NetworkRequest SetData(Map<String, Object> map)
	{
		m_data = null;
		if(map != null && !map.isEmpty())
		{
			Set<String> keys = map.keySet();
			Iterator<String> itor = keys.iterator();
			String data = "";
			while(itor.hasNext())
			{
				String name = itor.next();
				String value = map.get(name).toString();
				data += name + "=" + URLEncoder.encode(value);
				if(itor.hasNext())
					data += '&';
			}
			SetData(data);
		}
		return this;
	}
	
	public byte[] Data()
	{
		return m_data;
	}

	public NetworkRequest SetData(String data)
	{
		m_data = data == null || data.isEmpty() ? null : data.getBytes(); // TODO: normal UTF-8
		//m_data = Common.String8BitsByteArray(data); // TODO: 8bits
		return this;
	}
	
	public URL Ready()
	{
		m_uri = null;
		if(m_url == null || m_url.isEmpty())
			return m_uri;

		String url = m_url;
		boolean addToUrl = m_method == REQUEST_METHOD_GET || m_method == REQUEST_METHOD_DELETE;
		if(m_params != null && !m_params.isEmpty())
		{
			Set<String> keys = m_params.keySet();
			Iterator<String> itor = keys.iterator();
			String data = "";
			while(itor.hasNext())
			{
				String name = itor.next();
				String value = m_params.get(name).toString();
				data += name + "=" + URLEncoder.encode(value);
				if(itor.hasNext())
					data += '&';
			}
			
			if(addToUrl)
			{
				boolean hasQuery = m_url.contains("?");
				url += hasQuery ? '&' : '?';
				url += data;
			}
			else
			{
				SetData(data);
			}
		}
		
		try
		{
			m_uri = new URL(url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return m_uri;
	}
}
