package com.roamtech.uc.handler.services.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.roamtech.uc.cache.model.ODPrdAttr;
import com.roamtech.uc.handler.UCService;
import com.roamtech.uc.model.PrdAttr;
import com.roamtech.uc.model.Product;

public class CartSetRequest extends UCRequest {
	protected Long productId;
	protected Integer quantity;
	private Long cartId;
	private List<ODPrdAttr> cartPrdAttrs; 
	private Date startTime;
	private Date endTime;
	private Boolean startTimeInvalidFormat = false;
	private Boolean endTimeInvalidFormat = false;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public CartSetRequest(HttpServletRequest request, UCService ucService) {
		super(request);
		JSONObject cart = getParametersJSON("cart");
		Product product;
		String temp = getParameter(cart, "productid");
		productId = -1L;
		if(StringUtils.isNotBlank(temp)) {
			productId = Long.parseLong(temp);
		}
		product = ucService.getProduct(productId);
		temp = getParameter(cart, "cartid");
		cartId = -1L;
		if(StringUtils.isNotBlank(temp)) {
			cartId = Long.parseLong(temp);
		}
		temp = getParameter(cart, "quantity");
		quantity = 1;
		if(StringUtils.isNotBlank(temp)) {
			quantity = Integer.parseInt(temp);
		}
		if(product == null || product.getPrdAttrs()==null) {
			return;
		}
		List<PrdAttr> prdAttrs = product.getPrdAttrs();
		if(prdAttrs.isEmpty()) {
			return;
		}
		cartPrdAttrs = new ArrayList<ODPrdAttr>();
		for(PrdAttr attr:prdAttrs) {
			temp = getParameter(cart, attr.getAttr().getVarname());
			if(StringUtils.isNotBlank(temp)) {
				if(StringUtils.isBlank(attr.getValue())||attr.getValue().equals(temp)) {
					ODPrdAttr odattr = new ODPrdAttr();
					odattr.setName(attr.getAttr().getName());
					odattr.setVarname(attr.getAttr().getVarname());
					odattr.setPrice(attr.getPrice());
					odattr.setValue(temp);
					cartPrdAttrs.add(odattr);
					if(odattr.getVarname().equals(ODPrdAttr.STARTTIME_TAG)) {
						try {
							startTime = sdf.parse(odattr.getValue());
						} catch (ParseException e) {
							startTimeInvalidFormat = true;
						}
					} else if(odattr.getVarname().equals(ODPrdAttr.ENDTIME_TAG)) {
						try {
							endTime = sdf.parse(odattr.getValue());
						} catch (ParseException e) {
							endTimeInvalidFormat = true;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean validate() {
		if(startTime != null) {
			Date now = new Date();
			if(now.getTime() - startTime.getTime() > 86400000) {
				return false;
			}
			if(endTime.before(startTime)) {
				return false;
			}
		}
		return (super.validate() && !startTimeInvalidFormat && !endTimeInvalidFormat/*&& productId != -1L*/);
	}
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public List<ODPrdAttr> getCartPrdAttrs() {
		return cartPrdAttrs;
	}

	public void setCartPrdAttrs(List<ODPrdAttr> cartPrdAttrs) {
		this.cartPrdAttrs = cartPrdAttrs;
	}

}
