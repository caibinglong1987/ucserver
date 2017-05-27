package com.roamtech.uc.handler.om;


import com.roamtech.uc.handler.AbstractServiceHandler;

//OM Operation & Maintenance
public class OMServiceHandler extends AbstractServiceHandler {

	@Override
	public String getServicePrefix() {
		return "/om/services/";
	}

}