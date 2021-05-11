package com.pengniaoyun.uniplugin_module_filechooser.network;
import java.io.*;
import java.net.*;
import android.util.*;
import java.util.*;
import javax.net.ssl.*;
import javax.security.cert.*;
import java.security.cert.X509Certificate;
import java.security.cert.*;
import java.security.cert.CertificateException;
import android.os.*;

import com.pengniaoyun.uniplugin_module_filechooser.utility.Logf;

public class NetworkAccessManager
{
	private static final String ID_TAG = "NetworkAccessManager";
	private int m_timeout = 0;
	private boolean m_sslVerify = false;
	private boolean m_followRedirection = false;
	private List<Thread> m_threadPool = null;
	private boolean m_debugMode = true;
	
	public NetworkAccessManager()
	{
		super();
	}

	private synchronized void AddThread(Thread thread)
	{
		if(m_threadPool == null)
			m_threadPool = new ArrayList<Thread>();
		m_threadPool.add(thread);
		LogThreadPool();
	}

	private synchronized void RemoveThread(Thread thread)
	{
		if(m_threadPool == null)
			return;
		m_threadPool.remove(thread);
		LogThreadPool();
	}

	private void LogThreadPool()
	{
		if(!m_debugMode)
			return;
		if(m_threadPool == null)
		{
			Logf.e(ID_TAG, "线程池为空");
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (Thread t : m_threadPool)
			sb.append(t.toString()).append("\n");
		Logf.e(ID_TAG, "当前线程池：\n" + sb.toString());
	}
	
	public NetworkAccessManager SetTimeout(int t)
	{
		m_timeout = t;
		return this;
	}

	public NetworkAccessManager SetSslVerfify(boolean v)
	{
		m_sslVerify = v;
		return this;
	}

	public NetworkAccessManager SetFollowRediection(boolean r)
	{
		m_followRedirection = r;
		return this;
	}

	public NetworkReply Send(final NetworkRequest req)
	{
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		NetworkReply reply = null;
		URL url = req.Uri();

		Logf.d(ID_TAG, "[Harmattan]: request -> " + url.toString());
		try
		{
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod(req.Method());
			if(m_timeout > 0)
				conn.setConnectTimeout(m_timeout);
			if(!m_followRedirection)
				conn.setFollowRedirects(true);
			String scheme = url.getProtocol();
			if(!m_sslVerify)
			{
				if("https".equals(scheme) && conn instanceof HttpsURLConnection)
				{
					HttpsURLConnection https = (HttpsURLConnection)conn;
					TrustAllHosts(https);
					https.setHostnameVerifier(DO_NOT_VERIFY);
				}
			}
			conn.setDoInput(true); // 总是读取结果
			conn.setUseCaches(false);

			Iterator<String> itor = req.HeaderIterator();
			if(itor != null)
			{
				while(itor.hasNext())
				{
					String name = itor.next();
					conn.setRequestProperty(name, req.HeaderValue(name));
				}
			}

			byte data[] = req.Data();
			if(data != null)
			{
				conn.setDoOutput(true);
				outputStream = conn.getOutputStream();
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();
			}
			conn.connect();

			int respCode = conn.getResponseCode();
			Logf.d(ID_TAG, "[Harmattan]: response code -> " + respCode);
			reply = new NetworkReply_local();
			reply.SetReplyResult(respCode);
			reply.SetNetworkRequest(req);
			if(respCode == HttpURLConnection.HTTP_OK)
			{
				inputStream = conn.getInputStream();
				reply.Read(inputStream);
				if(reply.GetReplyLength() < 256 && false)
					Logf.d(ID_TAG, "[Harmattan]: response -> " + new String(reply.GetReplyData()));
				inputStream.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try
			{
				if(inputStream != null)
					inputStream.close();
				if(outputStream != null)
					outputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return reply;
	}

	// 异步
	public boolean Request(final NetworkRequest req, final NetworkReplyHandler handler)
	{
		final URL url = req.Ready();
		if(url == null)
			return false;

		Runnable runnable = new Runnable(){
			public void run()
			{
				NetworkReply reply = Send(req);
					
				if(handler != null)
					handler.Handle(reply);
				RemoveThread(Thread.currentThread());
			}
		};
		Thread thread = new Thread(runnable);
		AddThread(thread);
		thread.start();
		return true;
	}

	public boolean Get(final NetworkRequest req, final NetworkReplyHandler handler)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_GET);
		return Request(req, handler);
	}

	public boolean Post(final NetworkRequest req, final NetworkReplyHandler handler, byte data[])
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return Request(req, handler);
	}

	public boolean Delete(final NetworkRequest req, final NetworkReplyHandler handler)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_DELETE);
		return Request(req, handler);
	}

	public boolean Put(final NetworkRequest req, final NetworkReplyHandler handler, byte data[])
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return Request(req, handler);
	}

	public boolean Request(final NetworkRequest req, final Handler handler, final int sucCode, final int errCode)
	{
		return Request(req, handler != null ? new NetworkReplyHandler(){
			public void Handle(NetworkReply reply)
			{
				Message msg = new Message();
				msg.what = reply.GetResponseResult() ? sucCode : errCode;
				msg.obj = reply.GetReplyData();
				handler.sendMessage(msg);
			}
		} : null);
	}

	public boolean Get(final NetworkRequest req, final Handler handler, int sucCode, int errCode)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_GET);
		return Request(req, handler, sucCode, errCode);
	}

	public boolean Post(final NetworkRequest req, final Handler handler, byte data[], int sucCode, int errCode)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return Request(req, handler, sucCode, errCode);
	}

	public boolean Delete(final NetworkRequest req, final Handler handler, int sucCode, int errCode)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_DELETE);
		return Request(req, handler, sucCode, errCode);
	}

	public boolean Put(final NetworkRequest req, final Handler handler, byte data[], int sucCode, int errCode)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return Request(req, handler, sucCode, errCode);
	}

	// 同步, UI程序需要自己创建线程
	public NetworkReply SyncRequest(final NetworkRequest req)
	{
		final URL url = req.Ready();
		if(url == null)
			return null;

		NetworkReply reply = Send(req);
		return reply;
	}

	public NetworkReply SyncGet(final NetworkRequest req)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_GET);
		return SyncRequest(req);
	}

	public NetworkReply SyncPost(final NetworkRequest req, byte data[])
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return SyncRequest(req);
	}

	public NetworkReply SyncDelete(final NetworkRequest req)
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_DELETE);
		return SyncRequest(req);
	}

	public NetworkReply SyncPut(final NetworkRequest req, byte data[])
	{
		req.SetMethod(NetworkRequest.REQUEST_METHOD_POST);
		req.SetData(data);
		return SyncRequest(req);
	}
	
	private static class NetworkReply_local extends NetworkReply
	{
		public int Read(InputStream inputStream)
		{
			final int BUFFER_SIZE = 1024;
			ByteArrayOutputStream bytesStream = null;
			int size = 0;
			byte buffer[] = new byte[BUFFER_SIZE];
			int len = 0;
			
			try
			{
				bytesStream = new ByteArrayOutputStream();
				while((len = inputStream.read(buffer, 0, BUFFER_SIZE)) > 0)
				{
					bytesStream.write(buffer, 0, len);
					size += len;
				}
				bytesStream.flush();
				
				SetData(bytesStream.toByteArray());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally {
				try
				{
					if(bytesStream != null)
						bytesStream.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			return size;
		}
	}
	
	/**
     * 覆盖java默认的证书验证
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
		new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
			}
		}};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     * @param connection
     * @return
     */
    private static SSLSocketFactory TrustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

	public interface NetworkReplyHandler
	{
		public void Handle(final NetworkReply reply);
	}

}
