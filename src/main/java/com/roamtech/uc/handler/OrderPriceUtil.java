package com.roamtech.uc.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.roamtech.uc.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roamtech.uc.cache.model.ODAttr;
import com.roamtech.uc.cache.model.ODPrdAttr;
import com.roamtech.uc.repository.PrdDiscountRepository;
import com.roamtech.uc.repository.PrdUnitPriceRepository;
import com.roamtech.uc.repository.ProductRepository;
import com.roamtech.uc.util.JSONUtils;

public class OrderPriceUtil {
	private static final Logger LOG = LoggerFactory.getLogger(OrderPriceUtil.class);
	private static PrdUnitPriceRepository prdUnitPriceRepo;
	private static ProductRepository productRepo;
	private static PrdDiscountRepository prdDiscountRepo;
	public static void setRepos(PrdUnitPriceRepository prdUnitPriceRepo,ProductRepository productRepo, PrdDiscountRepository prdDiscountRepo) {
		OrderPriceUtil.prdUnitPriceRepo = prdUnitPriceRepo;
		OrderPriceUtil.productRepo = productRepo;
		OrderPriceUtil.prdDiscountRepo = prdDiscountRepo;
	}
	public static Double calcTotalFee(Order order) {
		Double fee = order.getPayableAmount();
		if(order.getShippingFee()!=null)
			fee+=order.getShippingFee();
		if(order.getTaxFee()!=null)
			fee+=order.getTaxFee();
		if(order.getVoucherAmount()!=null)
			fee-=order.getVoucherAmount();
		return fee;
	}
	public static ODPrdAttr findODPrdAttrByVarname(List<ODPrdAttr> cartPrdAttrs, String varname) {
		if(cartPrdAttrs == null) {
			return null;
		}
		for(ODPrdAttr attr:cartPrdAttrs) {
			if(attr.getVarname().equalsIgnoreCase(varname)) {
				return attr;
			}
		}
		return null;
	}
	
	public static PrdUnitPrice getPriceByTime(Long productId, Date startTime) {
		String currDatetime = DateFormatUtils.format(startTime,
				"yyyy-MM-dd HH:mm:ss");
		System.out.println("product:"+productId+","+currDatetime);
		List<PrdUnitPrice> prices = prdUnitPriceRepo.findByfailureDatetime(productId, currDatetime);
		if(prices == null || prices.isEmpty()) {
			return null;
		}
		PrdUnitPrice currPrice = prices.get(0);
		for(PrdUnitPrice price:prices) {
			if (currPrice.getEffectDatetime().before(
					price.getEffectDatetime())) {
				if(startTime.after(price.getEffectDatetime())) {
					currPrice = price;
				}
			} else {
				currPrice = price;
			}
		}
		return currPrice;
	}
	public static List<PrdUnitPrice> getPricesByTime(Product prd, Date startTime) {
		List<PrdUnitPrice> prices = new ArrayList<PrdUnitPrice> ();
		PrdUnitPrice price = getPriceByTime(prd.getId(), startTime);
		if(price != null) {
			prices.add(price);
		} else {
			if(prd.getBpackage()) {
				List<PrdPackage> pkgs = prd.getPackages();
				for(PrdPackage pkg:pkgs) {
					price = getPriceByTime(pkg.getProductid(), startTime);
					if(price != null) {
						prices.add(price);
					}
				}
			}
		}
		return prices;
	}
	public static Double calcPrdAttrPrice(Product prd, List<ODPrdAttr> cartPrdAttrs) {
		Double price = 0.0;
		if(cartPrdAttrs == null || cartPrdAttrs.isEmpty()) {
			return price;
		}
		List<PrdAttr> attrs = prd.getPrdAttrs();
		if(attrs != null) {
			for(PrdAttr attr:attrs) {
				for(ODPrdAttr odAtrr:cartPrdAttrs) {
					if(StringUtils.isNotBlank(attr.getPrice())) {
						if(attr.getAttr().getVarname().equals(odAtrr.getVarname())&& (StringUtils.isBlank(attr.getValue())||attr.getValue().equals(odAtrr.getValue()))) {
							price+=Double.valueOf(attr.getPrice());
						}
					}
				}
			}
		}
		return price;
	}

	public static List<PrdUnitPrice> getPrdPrices(List<ODPrdAttr> cartPrdAttrs, Product prd, ODAttr odAttr) {
		List<PrdUnitPrice> prices = getPricesByTime(prd, odAttr.getStartTime());
		if(prices.size() == 1) {
			PrdUnitPrice price = prices.get(0);
			Integer amount = getPrdAmount(prd,1,odAttr);
			price.setPrice((price.getPrice()+calcPrdAttrPrice(prd,cartPrdAttrs))*amount);
			return prices;
		}
		for(PrdUnitPrice price:prices) {
			/*if(odAttr.getQuantityPerUnit() == 1) {
				vprice +=(price.getPrice()+calcPrdAttrPrice(cartPrdAttrs));
			} else*/ {
				Product subprd = productRepo.findOne(price.getProductid());
				Integer amount = getPrdAmount(subprd,1,odAttr);
				price.setPrice((price.getPrice()+calcPrdAttrPrice(subprd,cartPrdAttrs))*amount);
			}
		}
		return prices;
	}
	public static Double getPrdPrice(List<PrdUnitPrice> prices) {
		Double vprice = 0.0;
		for(PrdUnitPrice price:prices) {
			vprice+=price.getPrice();
		}
		return vprice;
	}
/*	public static Double getPrdPrice(List<ODPrdAttr> cartPrdAttrs, Product prd, ODAttr odAttr) {
		List<PrdUnitPrice> prices = getPricesByTime(prd, odAttr.getStartTime());
		if(prices.size() == 1) {
			if(PrdType.isVoiceType(prd.getTypeid())) {
				return (prices.get(0).getPrice()+calcPrdAttrPrice(cartPrdAttrs))*odAttr.getCallDuration();
			} else {
				return (prices.get(0).getPrice()+calcPrdAttrPrice(cartPrdAttrs))*(odAttr.getQuantityPerUnit()<prd.getQuantityPerUnit()?1:odAttr.getQuantityPerUnit()/prd.getQuantityPerUnit());
			}
		}
		Double vprice = 0.0;
		for(PrdUnitPrice price:prices) {
			if(odAttr.getQuantityPerUnit() == 1) {
				vprice +=(price.getPrice()+calcPrdAttrPrice(cartPrdAttrs));
			} else {
				Product subprd = productRepo.findOne(price.getProductid());
				if(PrdType.isVoiceType(subprd.getTypeid())) {
					vprice += (price.getPrice()+calcPrdAttrPrice(subprd,cartPrdAttrs))*odAttr.getCallDuration();
				} else if(PrdType.isDataOrVoiceNumber(subprd.getTypeid())) {					
					vprice += (price.getPrice()+calcPrdAttrPrice(subprd,cartPrdAttrs))*(odAttr.getQuantityPerUnit()<subprd.getQuantityPerUnit()?1:odAttr.getQuantityPerUnit()/subprd.getQuantityPerUnit());
				} else {
					vprice +=(price.getPrice()+calcPrdAttrPrice(subprd,cartPrdAttrs));
				}
			}
		}
		return vprice;
	}*/
	public static PrdDiscount getDiscountByTimeAndQuantity(Long productId, Date startTime, Integer quantity) {
		String currDatetime = DateFormatUtils.format(startTime,
				"yyyy-MM-dd HH:mm:ss");
		List<PrdDiscount> discounts = prdDiscountRepo.findByfailureDatetime(productId, currDatetime);
		PrdDiscount currDiscount = null;
		if((discounts == null)||discounts.isEmpty()) {
			LOG.warn("productId:"+productId+",startTime:"+currDatetime+",quantity:"+quantity);
			return null;
		}
		for(PrdDiscount discount:discounts) {
			if (currDiscount != null && currDiscount.getEffectDatetime().before(
					discount.getEffectDatetime())) {
				if(startTime.after(discount.getEffectDatetime())) {
					if(quantity >= discount.getQuantity() && currDiscount.getQuantity() < discount.getQuantity()) {
						currDiscount = discount;
					}
				}
			} else {
				if(quantity >= discount.getQuantity() && (currDiscount == null || currDiscount.getQuantity() < discount.getQuantity())) {
					currDiscount = discount;
				}
			}
		}
		return currDiscount;
	}
	private static Integer getPrdAmount(Product prd, Integer quantity, ODAttr odAttr) {
		Integer amount;
		if(PrdType.isVoiceType(prd.getTypeid())) {
			amount = quantity*odAttr.getCallDuration().intValue();
		} else if(PrdType.isDataOrVoiceNumber(prd.getTypeid())) {
			amount = quantity*(odAttr.getQuantityPerUnit()<prd.getQuantityPerUnit()?1:odAttr.getQuantityPerUnit()/prd.getQuantityPerUnit());
		} else {
			amount = quantity;
		}
		return amount;
	}
	public static List<PrdDiscount> getDiscountsByTimeAndQuantity(Product prd, Date startTime, Integer quantity,ODAttr odAttr) {
		List<PrdDiscount> discounts = new ArrayList<PrdDiscount>();

		PrdDiscount discount = getDiscountByTimeAndQuantity(prd.getId(),startTime, getPrdAmount(prd,quantity,odAttr));//quantity);
		if(discount != null) {
			discounts.add(discount);
		} else {
			if(prd.getBpackage()) {
				List<PrdPackage> pkgs = prd.getPackages();
				for(PrdPackage pkg:pkgs) {
					Product subprd = productRepo.findOne(pkg.getProductid());
					discount = getDiscountByTimeAndQuantity(pkg.getProductid(), startTime, getPrdAmount(subprd,quantity,odAttr));
					if(discount != null) {
						discounts.add(discount);
					}
				}
			}
		}
		return discounts;
	}
	public static Double getPrdDiscount(List<PrdUnitPrice> prices,Product prd, Integer quantity ,ODAttr odAttr) {
		List<PrdDiscount> discounts = getDiscountsByTimeAndQuantity(prd, odAttr.getStartTime(), quantity, odAttr);//PrdType.isVoiceType(prd.getTypeid())?quantity*odAttr.getCallDuration().intValue():quantity*(odAttr.getQuantityPerUnit()<prd.getQuantityPerUnit()?1:odAttr.getQuantityPerUnit()/prd.getQuantityPerUnit()));
		if(discounts.isEmpty()) {
			LOG.info("prices:"+JSONUtils.serialize(prices)+quantity+JSONUtils.serialize(odAttr));
			return 0.0;
		}
		Double vdiscount = 0.0;
		for(PrdUnitPrice price:prices) {
			for(PrdDiscount discount:discounts) {
				if(price.getProductid() == discount.getProductid()) {
					vdiscount+=price.getPrice()*quantity*(100-discount.getDiscount())/100;
				}
			}
		}
		return vdiscount;
	}
/*	public static PrdDiscount getMaxDiscount(Product prd, Integer quantity , ODAttr odAttr) {		
		List<PrdDiscount> discounts = getDiscountsByTimeAndQuantity(prd, odAttr.getStartTime(), PrdType.isVoiceType(prd.getTypeid())?quantity*odAttr.getCallDuration().intValue():quantity*(odAttr.getQuantityPerUnit()<prd.getQuantityPerUnit()?1:odAttr.getQuantityPerUnit()/prd.getQuantityPerUnit()));
		if(discounts.isEmpty()) {
			return null;
		}
		if(discounts.size() == 1) {
			return discounts.get(0);
		}
		PrdDiscount currDiscount = discounts.get(0);
		for(PrdDiscount discount:discounts) {
			if(odAttr.getQuantityPerUnit() == 1) {
				if(discount.getDiscount() < currDiscount.getDiscount()) {
					currDiscount = discount;
				}
			} else {
				Product subprd = productRepo.findOne(discount.getProductid());
				if(PrdType.isDataOrVoiceNumber(subprd.getTypeid())) {
					currDiscount = discount;
					break;
				}
			}
		}
		return currDiscount;
	}*/
}
