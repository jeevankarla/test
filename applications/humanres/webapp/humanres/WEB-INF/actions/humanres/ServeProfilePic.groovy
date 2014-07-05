	import javolution.util.FastList;
	
	import javax.servlet.http.HttpServletRequest;
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.GenericEntityException;
	import org.ofbiz.entity.GenericValue;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.EntityUtil;
	import net.sf.json.JSONObject;
	import net.sf.json.JSONArray;
	import org.ofbiz.entity.DelegatorFactory;
	import org.ofbiz.service.GenericDispatcher;
	import org.ofbiz.service.ServiceUtil;
	
	HttpServletRequest httpRequest = (HttpServletRequest) request;
	//HttpServletResponse httpResponse = (HttpServletResponse) response;
	
	dctx = dispatcher.getDispatchContext();
	delegator = request.getAttribute("delegator");
	imageUrl = "";
	
	String actualRequest = (String) request.getAttribute("thisRequestUri");
	String requestUrl = httpRequest.getRequestURL();
	String source = requestUrl.replace(actualRequest, "");
	
	partyContentDetails = delegator.findList("PartyContentDetail", EntityCondition.makeCondition([partyId : partyId, partyContentTypeId : "INTERNAL", contentTypeId : "IMAGE_FRAME", statusId : "CTNT_AVAILABLE", mimeTypeId : "image/jpeg"]), null, ["-fromDate"], null, false);
	
	if(UtilValidate.isNotEmpty(partyContentDetails)){
		dataResourceId = (EntityUtil.getFirst(partyContentDetails)).get("dataResourceId");
		context.dataResourceId = dataResourceId;
		
		if(UtilValidate.isNotEmpty(dataResourceId)){
			sessionId = (String) session.getId();
			imageUrl = source + "img;jsessionid="+sessionId+"?imgId="+dataResourceId; // "https://localhost:58443/humanres/control/img;jsessionid="+sessionId+"?imgId="+dataResourceId;
		}
	}
	
	context.imageUrl = imageUrl;
	
	
	