package com.roamtech.uc.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;


public class UCRequestLog extends AbstractLifeCycle implements RequestLog {
    private Logger LOG =LoggerFactory.getLogger(UCRequestLog.class);
    private static ThreadLocal<StringBuilder> _buffers = new ThreadLocal<StringBuilder>()
            {
                @Override
                protected StringBuilder initialValue()
                {
                    return new StringBuilder(256);
                }
            };

    private String _filename;
    private boolean _extended;
    private boolean _append;
    private int _retainDays;
    private boolean _closeOut;
    private boolean _preferProxiedForAddress;
    private String _logDateFormat = "yyyy-MMM-dd HH:mm:ss.sss";
    private String _filenameDateFormat = null;
    private Locale _logLocale = Locale.getDefault();
    private String _logTimeZone = "GMT";
    private String[] _ignorePaths;
    private boolean _logLatency = false;
    private boolean _logCookies = false;
    private boolean _logServer = false;
    private boolean _logDispatch = false;
    private boolean _logResponse = false;

    private transient OutputStream _out;
    private transient OutputStream _fileOut;
    private transient DateCache _logDateCache;
    private transient PathMap _ignorePathMap;
    private transient Writer _writer;

    /* ------------------------------------------------------------ */
    /**
     * Create request log object with default settings.
     */
    public UCRequestLog()
    {
        _extended = true;
        _append = true;
        _retainDays = 31;
    }

    /* ------------------------------------------------------------ */
    /**
     * Create request log object with specified output file name.
     * 
     * @param filename the file name for the request log.
     *                 This may be in the format expected
     *                 by {@link RolloverFileOutputStream}
     */
    public UCRequestLog(String filename)
    {
        _extended = true;
        _append = true;
        _retainDays = 31;
        setFilename(filename);
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the output file name of the request log.
     * The file name may be in the format expected by
     * {@link RolloverFileOutputStream}.
     * 
     * @param filename file name of the request log
     *                
     */
    public void setFilename(String filename)
    {
        if (filename != null)
        {
            filename = filename.trim();
            if (filename.length() == 0)
                filename = null;
        }
        _filename = filename;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the output file name of the request log.
     * 
     * @return file name of the request log
     */
    public String getFilename()
    {
        return _filename;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the file name of the request log with the expanded
     * date wildcard if the output is written to the disk using
     * {@link RolloverFileOutputStream}.
     * 
     * @return file name of the request log, or null if not applicable
     */
    public String getDatedFilename()
    {
        if (_fileOut instanceof RolloverFileOutputStream)
            return ((RolloverFileOutputStream)_fileOut).getDatedFilename();
        return null;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the timestamp format for request log entries in the file.
     * If this is not set, the pre-formated request timestamp is used.
     * 
     * @param format timestamp format string 
     */
    public void setLogDateFormat(String format)
    {
        _logDateFormat = format;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the timestamp format string for request log entries.
     * 
     * @return timestamp format string.
     */
    public String getLogDateFormat()
    {
        return _logDateFormat;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the locale of the request log.
     * 
     * @param logLocale locale object
     */
    public void setLogLocale(Locale logLocale)
    {
        _logLocale = logLocale;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the locale of the request log.
     * 
     * @return locale object
     */
    public Locale getLogLocale()
    {
        return _logLocale;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the timezone of the request log.
     * 
     * @param tz timezone string
     */
    public void setLogTimeZone(String tz)
    {
        _logTimeZone = tz;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the timezone of the request log.
     * 
     * @return timezone string
     */
    public String getLogTimeZone()
    {
        return _logTimeZone;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the number of days before rotated log files are deleted.
     * 
     * @param retainDays number of days to keep a log file
     */
    public void setRetainDays(int retainDays)
    {
        _retainDays = retainDays;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the number of days before rotated log files are deleted.
     * 
     * @return number of days to keep a log file
     */
    public int getRetainDays()
    {
        return _retainDays;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the extended request log format flag.
     * 
     * @param extended true - log the extended request information,
     *                 false - do not log the extended request information
     */
    public void setExtended(boolean extended)
    {
        _extended = extended;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the extended request log format flag.
     * 
     * @return value of the flag
     */
    public boolean isExtended()
    {
        return _extended;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set append to log flag.
     * 
     * @param append true - request log file will be appended after restart,
     *               false - request log file will be overwritten after restart
     */
    public void setAppend(boolean append)
    {
        _append = append;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve append to log flag.
     * 
     * @return value of the flag
     */
    public boolean isAppend()
    {
        return _append;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set request paths that will not be logged.
     * 
     * @param ignorePaths array of request paths
     */
    public void setIgnorePaths(String[] ignorePaths)
    {
        _ignorePaths = ignorePaths;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the request paths that will not be logged.
     * 
     * @return array of request paths
     */
    public String[] getIgnorePaths()
    {
        return _ignorePaths;
    }

    /* ------------------------------------------------------------ */
    /**
     * Controls logging of the request cookies.
     * 
     * @param logCookies true - values of request cookies will be logged,
     *                   false - values of request cookies will not be logged
     */
    public void setLogCookies(boolean logCookies)
    {
        _logCookies = logCookies;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve log cookies flag
     * 
     * @return value of the flag
     */
    public boolean getLogCookies()
    {
        return _logCookies;
    }

    /* ------------------------------------------------------------ */
    /**
     * Controls logging of the request hostname.
     * 
     * @param logServer true - request hostname will be logged,
     *                  false - request hostname will not be logged
     */
    public void setLogServer(boolean logServer)
    {
        _logServer = logServer;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve log hostname flag.
     * 
     * @return value of the flag
     */
    public boolean getLogServer()
    {
        return _logServer;
    }

    /* ------------------------------------------------------------ */
    /**
     * Controls logging of request processing time.
     * 
     * @param logLatency true - request processing time will be logged
     *                   false - request processing time will not be logged
     */
    public void setLogLatency(boolean logLatency)
    {
        _logLatency = logLatency;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve log request processing time flag.
     * 
     * @return value of the flag
     */
    public boolean getLogLatency()
    {
        return _logLatency;
    }

    /* ------------------------------------------------------------ */
    /**
     * Controls whether the actual IP address of the connection or
     * the IP address from the X-Forwarded-For header will be logged.
     * 
     * @param preferProxiedForAddress true - IP address from header will be logged,
     *                                false - IP address from the connection will be logged
     */
    public void setPreferProxiedForAddress(boolean preferProxiedForAddress)
    {
        _preferProxiedForAddress = preferProxiedForAddress;
    }
    
    /* ------------------------------------------------------------ */
    /**
     * Retrieved log X-Forwarded-For IP address flag.
     * 
     * @return value of the flag
     */
    public boolean getPreferProxiedForAddress()
    {
        return _preferProxiedForAddress;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the log file name date format.
     * @see RolloverFileOutputStream#RolloverFileOutputStream(String, boolean, int, TimeZone, String, String)
     * 
     * @param logFileDateFormat format string that is passed to {@link RolloverFileOutputStream}
     */
    public void setFilenameDateFormat(String logFileDateFormat)
    {
        _filenameDateFormat = logFileDateFormat;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve the file name date format string.
     * 
     * @return the log File Date Format
     */
    public String getFilenameDateFormat()
    {
        return _filenameDateFormat;
    }

    /* ------------------------------------------------------------ */
    /** 
     * Controls logging of the request dispatch time
     * 
     * @param value true - request dispatch time will be logged
     *              false - request dispatch time will not be logged
     */
    public void setLogDispatch(boolean value)
    {
        _logDispatch = value;
    }

    /* ------------------------------------------------------------ */
    /**
     * Retrieve request dispatch time logging flag
     * 
     * @return value of the flag
     */
    public boolean isLogDispatch()
    {
        return _logDispatch;
    }

    /* ------------------------------------------------------------ */
    /**
     * Writes the request and response information to the output stream.
     * 
     * @see org.eclipse.jetty.server.RequestLog#log(org.eclipse.jetty.server.Request, org.eclipse.jetty.server.Response)
     */
    public void log(Request request, Response response)
    {
        try
        {
            if (_ignorePathMap != null && _ignorePathMap.getMatch(request.getRequestURI()) != null)
                return;

            if (_fileOut == null)
                return;

            StringBuilder buf= _buffers.get();
            buf.setLength(0);
            buf.append('{');
            if (_logServer)
            {
            	buf.append("\"server\":\"");
                buf.append(request.getServerName());
                buf.append("\",");
            }

            String addr = null;
            if (_preferProxiedForAddress)
            {
                addr = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
            }

            if (addr == null)
                addr = request.getRemoteAddr();
            buf.append("\"remote_addr\":\"");
            buf.append(addr);
            buf.append("\"");
            
            Authentication authentication=request.getAuthentication();
            if (authentication instanceof Authentication.User) {
            	buf.append(",\"remote_user\":\"");
                buf.append(((Authentication.User)authentication).getUserIdentity().getUserPrincipal().getName());
                buf.append("\"");
            }

            buf.append(",\"timestamp\":\"");
            if (_logDateCache != null)
                buf.append(_logDateCache.format(request.getTimeStamp()));
            else
                buf.append(request.getTimeStampBuffer().toString());

            buf.append("\"");
            buf.append(",\"method\":\"");
            buf.append(request.getMethod());
            buf.append("\"");
            buf.append(",\"request_uri\":\"");
            String request_uri = request.getRequestURI();
            buf.append(request_uri);
            buf.append("\"");
            String params = request.getQueryString();
            if(!StringUtils.isBlank(params)) {
	            int index = params.indexOf("password=");
	    		if(index>0) {
		    		int endIndex = params.indexOf("&",index);
		    		if(endIndex<0)
		    			endIndex=params.length();
		    		params = params.replace(params.subSequence(index+9, endIndex),"******");
	    		}
	    		buf.append(",\"params\":\"").append(params).append("\"");
            }
            buf.append(",\"protocol\":\"");
            buf.append(request.getProtocol());
            buf.append("\"");
            buf.append(",\"status\":\"");
            if (request.getAsyncContinuation().isInitial())
            {
                int status = response.getStatus();
                if (status <= 0)
                    status = 404;
                buf.append((char)('0' + ((status / 100) % 10)));
                buf.append((char)('0' + ((status / 10) % 10)));
                buf.append((char)('0' + (status % 10)));
            }
            else
                buf.append("Async");
            buf.append("\"");

            long responseLength = response.getContentCount();
            if (responseLength >= 0)
            {
                buf.append(",\"body_bytes_sent\":\"");
                if (responseLength > 99999)
                    buf.append(responseLength);
                else
                {
                    if (responseLength > 9999)
                        buf.append((char)('0' + ((responseLength / 10000) % 10)));
                    if (responseLength > 999)
                        buf.append((char)('0' + ((responseLength / 1000) % 10)));
                    if (responseLength > 99)
                        buf.append((char)('0' + ((responseLength / 100) % 10)));
                    if (responseLength > 9)
                        buf.append((char)('0' + ((responseLength / 10) % 10)));
                    buf.append((char)('0' + (responseLength) % 10));
                }
                buf.append("\"");
            }

            
            if (_extended)
                logExtended(request, response, buf);

            if (_logCookies)
            {
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length != 0)
                {
                    buf.append(",\"cookies\":[");
                    for (int i = 0; i < cookies.length; i++)
                    {
                    	if(i!= 0) {
                    		buf.append(",\"");
                    	} else {
                    		buf.append("\"");
                    	}
                        buf.append(cookies[i].getName());
                        buf.append("\":\"");
                        buf.append(cookies[i].getValue());
                        buf.append("\"");
                    }
                    buf.append("]");
                }
            }

            if (_logDispatch || _logLatency)
            {
                long now = System.currentTimeMillis();

                if (_logDispatch)
                {   
                    long d = request.getDispatchTime();
                    buf.append(",\"dispatch_time\":");
                    buf.append(now - (d==0 ? request.getTimeStamp():d));
                }

                if (_logLatency)
                {
                	buf.append(",\"latency\":");
                    buf.append(now - request.getTimeStamp());
                }
            }
            Attributes attrs = request.getAttributes();
            if(_logResponse){
            	buf.append(",\"response\":").append(request.getAttribute("response"));
            } else {
            	buf.append(",\"response\":{");
            	boolean first = true;
	            for(Enumeration<String> e = attrs.getAttributeNames();e.hasMoreElements();) {
	            	String attrName = e.nextElement();
	            	if(attrName.equals("response"))
	            		continue;
	            	if(attrName.contains("javax.servlet.async"))
	            		continue;
	            	Object value = attrs.getAttribute(attrName);
	            	if(first){
	            		buf.append("\"");
	            		first = false;
	            	}
	            	else {
	            		buf.append(",\"");
	            	}
	            	buf.append(attrName);
	            	buf.append("\":\"");
	            	buf.append(value);
	            	buf.append('"');
	            }
	            buf.append("}");
            }
            buf.append("}");
            buf.append(StringUtil.__LINE_SEPARATOR);
            
            String log = buf.toString();
            write(log);
        }
        catch (IOException e)
        {
            //LOG.warn(e);
        }
    }

    /* ------------------------------------------------------------ */
    protected void write(String log) throws IOException 
    {
		LOG.info(log);
        /*synchronized(this)
        {
            if (_writer==null)
                return;
            _writer.write(log);
            _writer.flush();
        }*/
    }

    
    /* ------------------------------------------------------------ */
    /**
     * Writes extended request and response information to the output stream.
     * 
     * @param request request object
     * @param response response object
     * @param b StringBuilder to write to
     * @throws IOException
     */
    protected void logExtended(Request request,
                               Response response,
                               StringBuilder b) throws IOException
    {
        String referer = request.getHeader(HttpHeaders.REFERER);
        if (referer != null)
        {
            b.append(",\"referer\":\"");
            b.append(referer);
            b.append('"');
        }

        String agent = request.getHeader(HttpHeaders.USER_AGENT);
        if (agent != null)
        {
            b.append(",\"user_agent\":\"");
            b.append(agent);
            b.append('"');
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * Set up request logging and open log file.
     * 
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#doStart()
     */
    @Override
    protected synchronized void doStart() throws Exception
    {
        if (_logDateFormat != null)
        {
            _logDateCache = new DateCache(_logDateFormat,_logLocale);
            _logDateCache.setTimeZoneID(_logTimeZone);
        }

        if (_filename != null)
        {
            _fileOut = new RolloverFileOutputStream(_filename,_append,_retainDays,TimeZone.getTimeZone(_logTimeZone),_filenameDateFormat,null);
            _closeOut = true;
            LOG.info("Opened " + getDatedFilename());
        }
        else
            _fileOut = System.err;

        _out = _fileOut;

        if (_ignorePaths != null && _ignorePaths.length > 0)
        {
            _ignorePathMap = new PathMap();
            for (int i = 0; i < _ignorePaths.length; i++)
                _ignorePathMap.put(_ignorePaths[i],_ignorePaths[i]);
        }
        else
            _ignorePathMap = null;

        synchronized(this)
        {
            _writer = new OutputStreamWriter(_out, "UTF-8");
        }
        super.doStart();
    }

    /* ------------------------------------------------------------ */
    /**
     * Close the log file and perform cleanup.
     * 
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#doStop()
     */
    @Override
    protected void doStop() throws Exception
    {
        synchronized (this)
        {
            super.doStop();
            try
            {
                if (_writer != null)
                    _writer.flush();
            }
            catch (IOException e)
            {
                ;//LOG.ignore(e);
            }
            if (_out != null && _closeOut)
                try
                {
                    _out.close();
                }
                catch (IOException e)
                {
                    ;//LOG.ignore(e);
                }

            _out = null;
            _fileOut = null;
            _closeOut = false;
            _logDateCache = null;
            _writer = null;
        }
    }
/*    public static void main(String[] args) {
    	String request_uri = "/sso/v1/hsid/register?username=zhengsj&tenantId=hs&password=123456";
        int index = request_uri.indexOf("password=");
		if(index>0) {
    		int endIndex = request_uri.indexOf("&",index);
    		if(endIndex < 0)
    			endIndex = request_uri.length();
    		request_uri = request_uri.replace(request_uri.subSequence(index+9, endIndex),"******");
		}
        System.out.println(request_uri);
        String resultStr = "{\"error_no\":0,\"error_info\":\"成功\",\"session\":{\"bizaccount\":{\"tenantId\":\"hs\",\"systemId\":\"security\",\"account\":\"10001234\",\"orgId\":\"zhongxin\",\"hsid\":1,\"version\":0},\"op_branch_no\":0,\"op_entrust_way\":null,\"op_station\":null,\"branch_no\":0,\"client_id\":null,\"fund_account\":null,\"password\":null,\"password_type\":null,\"user_token\":null}}";

    		int index = resultStr.indexOf("\"password\":");
    		if(index>0) {
	    		int endIndex = resultStr.indexOf(",",index);
	    		if(endIndex < 0)
	    			endIndex = resultStr.length();
	    		resultStr = resultStr.replace(resultStr.subSequence(index, endIndex+1),"");
    		}
    		System.out.println(resultStr);
    }*/

	public boolean isLogResponse() {
		return _logResponse;
	}

	public void setLogResponse(boolean logResponse) {
		this._logResponse = logResponse;
	}
}
