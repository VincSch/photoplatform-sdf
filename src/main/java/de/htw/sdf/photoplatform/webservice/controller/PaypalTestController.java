package de.htw.sdf.photoplatform.webservice.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.core.rest.PayPalRESTException;

import de.htw.sdf.photoplatform.exception.BadRequestException;
import de.htw.sdf.photoplatform.exception.common.AbstractBaseException;
import de.htw.sdf.photoplatform.manager.common.PaypalService;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.webservice.BaseAPIController;
import de.htw.sdf.photoplatform.webservice.Endpoints;

@RestController
public class PaypalTestController extends BaseAPIController {

    @Resource
	private PaypalService paypalService;
	
    @RequestMapping(value = Endpoints.PAYPALTEST_OAUTHTOKEN, method = RequestMethod.GET)
    @ResponseBody
    public String GetPaypalToken() throws AbstractBaseException {
    	try {
        	return paypalService.getOAuthToken();
    	} catch (PayPalRESTException ex)
    	{
    		return ex.getMessage();
    	}
    }

    @RequestMapping(value = Endpoints.PAYPALTEST_PURCHASE, method = RequestMethod.GET)
    @ResponseBody
    public String StartPaypalPurchase(HttpServletRequest request) throws AbstractBaseException {
    	
    	String BaseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    	
    	try{
    		return paypalService.CreatePayment((double)15.75 , BaseURL);
    	} catch (AbstractBaseException ex)
    	{
    		return ex.getMessage();
    	}
    }
    
    @RequestMapping(value = Endpoints.PAYPALTEST_PURCHASE_APPROVED, method = RequestMethod.GET)
    @ResponseBody
    public String PaypalPurchaseApproved(
            @RequestParam(required = true, value  = "paymentId") String paymentId,
            @RequestParam(required = true, value = "PayerID") String payerId,
            HttpServletRequest request) throws AbstractBaseException {
    	
    	try{
    		return paypalService.ExecutePayment(paymentId, payerId);
    	} catch (AbstractBaseException ex)
    	{
    		return ex.getMessage();
    	}
    } 
    
}
