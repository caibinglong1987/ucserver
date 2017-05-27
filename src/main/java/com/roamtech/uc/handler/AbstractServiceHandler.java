package com.roamtech.uc.handler;

import com.roamtech.uc.cache.handler.SessionHandler;
import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class AbstractServiceHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(UCServiceHandler.class);	
	protected Map<String,ServiceHandler> handlers;
	@Autowired
    SessionHandler sessionHandler;
	public void extractParameters(Request baseRequest) throws IOException
    {
            // handle any _content.
            String encoding = baseRequest.getCharacterEncoding();
            String content_type = baseRequest.getContentType();
            if (content_type != null && content_type.length() > 0)
            {
                content_type = HttpFields.valueParameters(content_type,null);

                if (MimeTypes.FORM_ENCODED.equalsIgnoreCase(content_type) && (HttpMethods.POST.equals(baseRequest.getMethod()) || HttpMethods.PUT.equals(baseRequest.getMethod())))
                {
                    int content_length = baseRequest.getContentLength();
                    if (content_length != 0)
                    {
                        try
                        {
                            int maxFormContentSize = -1;
                            int maxFormKeys = -1;
                            ContextHandler.Context _context = baseRequest.getContext();

                            if (_context != null)
                            {
                                maxFormContentSize = _context.getContextHandler().getMaxFormContentSize();
                                maxFormKeys = _context.getContextHandler().getMaxFormKeys();
                            }
                            AbstractHttpConnection _connection = baseRequest.getConnection();
                            if (maxFormContentSize < 0)
                            {
                                Object obj = _connection.getConnector().getServer().getAttribute("org.eclipse.jetty.server.Request.maxFormContentSize");
                                if (obj == null)
                                    maxFormContentSize = 200000;
                                else if (obj instanceof Number)
                                {                      
                                    Number size = (Number)obj;
                                    maxFormContentSize = size.intValue();
                                }
                                else if (obj instanceof String)
                                {
                                    maxFormContentSize = Integer.valueOf((String)obj);
                                }
                            }
                            
                            if (maxFormKeys < 0)
                            {
                                Object obj = _connection.getConnector().getServer().getAttribute("org.eclipse.jetty.server.Request.maxFormKeys");
                                if (obj == null)
                                    maxFormKeys = 1000;
                                else if (obj instanceof Number)
                                {
                                    Number keys = (Number)obj;
                                    maxFormKeys = keys.intValue();
                                }
                                else if (obj instanceof String)
                                {
                                    maxFormKeys = Integer.valueOf((String)obj);
                                }
                            }

                            if (content_length > maxFormContentSize && maxFormContentSize > 0)
                            {
                                throw new IllegalStateException("Form too large " + content_length + ">" + maxFormContentSize);
                            }
                            InputStream in = baseRequest.getInputStream();
                            ByteArrayBuffer buffer = new ByteArrayBuffer((int)content_length);
                            int length = 0;
                            do {
	                            length = in.read(buffer.array(), buffer.putIndex(), buffer.space());
	                            if(length < 0) {
	                            	break;
	                            }
	                            buffer.setPutIndex(buffer.putIndex()+length);
                            } while(buffer.space()>0);
                        	MultiMap _baseParameters = new MultiMap(16);

                            // Add form params to query params
                            UrlEncoded.decodeTo(new String(buffer.array(),encoding==null?UrlEncoded.ENCODING:encoding),_baseParameters,encoding,maxFormKeys);
                            baseRequest.setParameters(_baseParameters);
                            //baseRequest.setAttribute("post_form_params", buffer);
                        } catch (IllegalStateException e)
                        {
                        	LOG.warn(e.getMessage());
                        }
                    }
                } else if (HttpMethods.POST.equals(baseRequest.getMethod()) || HttpMethods.PUT.equals(baseRequest.getMethod())) {
                	ByteArrayBuffer buffer = null;

  				int content_length = baseRequest.getContentLength();
  				if (content_length != 0)
  	            {
  					InputStream in = baseRequest.getInputStream();
  	                buffer = new ByteArrayBuffer((int)content_length);
  	                int length = 0;
  	                do {
  	                    length = in.read(buffer.array(), buffer.putIndex(), buffer.space());
  	                    if(length < 0) {
  	                    	break;
  	                    }
  	                    buffer.setPutIndex(buffer.putIndex()+length);
  	                } while(buffer.space()>0);
  	            }
  				baseRequest.setAttribute("post_body", buffer);
        		}
              
            }
    }
	public Map<String,ServiceHandler> getHandlers() {
		return handlers;
	}
	public void setHandlers(Map<String,ServiceHandler> handlers) {
		this.handlers = handlers;
	}	
	public abstract String getServicePrefix();
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse resp) throws IOException, ServletException {
        try {
            if (!target.startsWith(getServicePrefix())) {
                return;
            }
            if (HttpMethods.OPTIONS.equals(baseRequest.getMethod())) {
                resp.setStatus(200);
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.setHeader("Access-Control-Allow-Credentials", "true");
                resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                resp.setHeader("Access-Control-Allow-Headers", "Authorization,DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type");
                if (baseRequest != null)
                    baseRequest.setHandled(true);
                return;
            }
            String service = target.substring(getServicePrefix().length());
            ServiceHandler handler = handlers.get(service);
            if (handler == null) {
                resp.setStatus(404);
                if (baseRequest != null)
                    baseRequest.setHandled(true);
                return;
            }
            resp.setHeader("Content-Type", "application/json; charset=utf-8");
            resp.setHeader("Pragma", "no-cache");
            resp.addHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", 0L);
            String auth = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(auth)) {
                String[] strArray = auth.split("\\s+");
                if (strArray.length == 2 && strArray[0].equals("Basic")) {
                    String[] strBasic = new String(new BASE64Decoder().decodeBuffer(strArray[1])).split(":");
                    if (strBasic.length == 2) {
                        if (service.equals("token_get")) {
                            request.setAttribute("appId", strBasic[0]);
                            request.setAttribute("appKey", strBasic[1]);
                        } else {
                            Session session = sessionHandler.findOne(strBasic[1]);
                            if (session != null) {
                                if (strBasic[0].equals(session.getPackageName()) || strBasic[0].equals(session.getBundleId())) {
                                    request.setAttribute("userid", session.getUserId());
                                    request.setAttribute("tenantid", session.getTenantId());
                                    request.setAttribute("clientid", strBasic[0]);
                                    request.setAttribute("sessionid", strBasic[1]);
                                } else {
                                    unauthorized(resp, ErrorCode.ERR_UNAUTHORIZED_CLIENT);
                                    if (baseRequest != null)
                                        baseRequest.setHandled(true);
                                    return;
                                }
                            } else {
                                unauthorized(resp, ErrorCode.ERR_SESSIONID_INVALID);
                                if (baseRequest != null)
                                    baseRequest.setHandled(true);
                                return;
                            }
                        }
                    } else {
                        unauthorized(resp, ErrorCode.ERR_INVALID_AUTHORIZATION_HEAD);
                        if (baseRequest != null)
                            baseRequest.setHandled(true);
                        return;
                    }
                } else {
                    unauthorized(resp, ErrorCode.ERR_INVALID_AUTHORIZATION_HEAD);
                    if (baseRequest != null)
                        baseRequest.setHandled(true);
                    return;
                }
            }
            extractParameters(baseRequest);
            handler.handle(target, baseRequest, request, resp);
            if (baseRequest != null)
                baseRequest.setHandled(true);
        } catch(Throwable e) {
            LOG.warn(target,e);
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setStatus(200);
            UCResponse ucResp = new UCResponse(ErrorCode.ERR_SERVER_ERROR,ErrorCode.ERR_SERVER_ERROR_INFO);
            resp.getWriter().write(ucResp.toString());
            if (baseRequest != null)
                baseRequest.setHandled(true);
        }
	}

	private void unauthorized(HttpServletResponse resp, int errorCode) throws IOException {
        resp.setStatus(401);
        UCResponse ucResp = UCResponse.buildResponse(errorCode, ErrorCode.getErrorInfo(errorCode));
        resp.getWriter().write(ucResp.toString());
    }

}
