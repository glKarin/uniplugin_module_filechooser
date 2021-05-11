package com.pengniaoyun.uniplugin_module_filechooser.network;
import java.io.*;
import java.util.Map;

public abstract class NetworkReply
{
	protected static final String ID_TAG = "NetworkReply";
	private NetworkRequest m_request = null;
	private byte m_data[] = null;
	private boolean m_result = false;
	private int m_code = 0;
	private Map<String, String> m_responseHeaders = null;
	
	protected NetworkReply()
	{
		
	}
	
	public void SetNetworkRequest(NetworkRequest req)
	{
		m_request = req;
	}
	
	public void SetReplyResult(int code)
	{
		m_code = code;
		m_result = (code == 200);
	}

	public NetworkRequest GetNetworkRequest()
	{
		return m_request;
	}

	public byte[] GetReplyData()
	{
		return m_data;	
	}

	public int GetReplyLength()
	{
		return m_data != null ? m_data.length : 0;
	}

	public boolean GetResponseResult()
	{
		return m_result;
	}
	
	public int GetResponseCode()
	{
		return m_code;
	}
	
	protected void SetData(byte data[])
	{
		m_data = data;
	}
	
	public abstract int Read(InputStream input);
}
