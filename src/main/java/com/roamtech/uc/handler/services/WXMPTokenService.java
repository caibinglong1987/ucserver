package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.client.RoamtechApis;
import com.roamtech.uc.client.wx.WXToken;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.User;
import com.roamtech.uc.model.UserAssociate;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import rx.Observer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class WXMPTokenService extends AbstractService {
	private static final Logger LOG = LoggerFactory
			.getLogger(WXMPTokenService.class);
	@Autowired
	@Qualifier("roamtechApis")
	RoamtechApis roamtechApis;
	private class WXMPTokenRequest extends AbstractRequest {
		private String code;
		private String state;
		public WXMPTokenRequest(HttpServletRequest request) {
			super(request);
			code = getParameter("code");
			state = getParameter("state");
		}
		public boolean validate() {
			return (code != null && !code.isEmpty() && state != null && !state.isEmpty());
		}
	}
	
	@Override
	public void doHandle(final String target, final Request baseRequest,
			HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {
		WXMPTokenRequest tokenRequest = new WXMPTokenRequest(request);


		if(tokenRequest.validate()){
			final Continuation continuation = ContinuationSupport.getContinuation(request);

			if (!continuation.isInitial())
				response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT); // Need better test that isInitial
			else {
				continuation.suspend(response);
			}
			roamtechApis.getWXMPTokenAsync(tokenRequest.code,tokenRequest.state).subscribe(new Observer<WXToken>() {
				@Override
				public void onCompleted() {
					continuation.complete();
				}

				@Override
				public void onError(Throwable throwable) {
					int status = ErrorCode.ERR_SERVER_ERROR;
					LOG.warn(target,throwable);
					UCResponse ucResp = new UCResponse(status, ErrorCode.getErrorInfo(status));
					try {
						postProcess(baseRequest, response, ucResp);
					} catch (IOException |ServletException e) {
						LOG.warn(target,e);
					}
					continuation.complete();
				}

				@Override
				public void onNext(WXToken token) {
					User user = null;
					Session session = null;
					String associateUsername = null;
					int status = ErrorCode.SUCCESS;
					if(StringUtils.isBlank(token.getOpenid())) {
						status = ErrorCode.ERR_ACCOUNT_NOEXIST;
					} else {
						Phone phone = ucService.findPhone(token.getOpenid(), User.ROAM_TENANT_ID, Phone.TYPE_WX);
						if (phone != null) {
							user = ucService.findOne(phone.getUserId());
						}
						if (user == null) {
							user = new User();
							user.setUserName(token.getOpenid());
							user.setPhone(token.getOpenid());
							user.setType(User.NORMAL_USER);
							user.setStatus(User.STATUS_ACTIVE);
							user.setTenantId(User.ROAM_TENANT_ID);
							user.setPhoneType(Phone.TYPE_WX);
							user = ucService.save(user);
							ucService.bindPhone(user.getUserId(), token.getOpenid(), Phone.TYPE_WX, User.ROAM_TENANT_ID, true);
						} else {
							UserAssociate userAssociate = ucService.getParentAccount(user.getUserId());
							if (userAssociate != null) {
								User parentUser = ucService.findOne(userAssociate.getParentUserId());
								if (parentUser != null) {
									associateUsername = parentUser.getUserName();
								}
							}
						}
						session = ucService.login(user);
					}
					UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), user);
					ucResp.setSessionId(session!=null?session.getSessionId():null);
					ucResp.addAttribute("token", token);
					ucResp.addAttribute("associated_username", associateUsername);
					try {
						postProcess(baseRequest, response, ucResp);
					} catch (IOException |ServletException e) {
						LOG.warn(target,e);
					}
				}
			} );
		} else {
			int status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
			UCResponse ucResp = new UCResponse(status, ErrorCode.getErrorInfo(status));
			postProcess(baseRequest, response, ucResp);
		}		

	}

}
