
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.*;
import java.security.*;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

Debug.log("===I==am==in==PayUbiz-===");
def hashCal(type,str){
    /*
	<%!
	public String hashCal(String type,String str){ */
		
		byte[] hashseq=str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try{
		MessageDigest algorithm = MessageDigest.getInstance(type);
		algorithm.reset();
		algorithm.update(hashseq);
		
		byte[] messageDigest = algorithm.digest();
			
		
		for (int i=0;i<messageDigest.length;i++) {
			String hex=Integer.toHexString(0xFF & messageDigest[i]);
			if(hex.length()==1) hexString.append("0");
			hexString.append(hex);
		}
			
		}catch(NoSuchAlgorithmException nsae){ }
		
		return hexString.toString();
	}

	String merchant_key="gtKFFx";
	String salt="eCwWELxi";
	String action1 ="";
	String base_url="https://test.payu.in";
	int error=0;
	String hashString="";
	
	Enumeration paramNames = request.getParameterNames();
	Map<String,String> params= new HashMap<String,String>();
		while(paramNames.hasMoreElements()){
			  String paramName = (String)paramNames.nextElement();
	  
			  String paramValue = request.getParameter(paramName);
		params.put(paramName,paramValue);
	}
	String txnid ="";
	
	if(UtilValidate.isEmpty(params.get("txnid"))){
		Random rand = new Random();
		String rndm = Integer.toString(rand.nextInt())+(System.currentTimeMillis() / 1000L);
		txnid=hashCal("SHA-256",rndm).substring(0,20);
	}
	else
		txnid=params.get("txnid");
		
	
	params.put("surl","https://nhdc-test.vasista.in/depotsales/control/FindPaymentDepotSales?subTabItem=PayUBizPayment&defaultMethodTypeId=PAYUBIZ_PAYMENT");
	params.put("furl","https://nhdc-test.vasista.in/depotsales/control/PayUBizInit");
	
    String txn="abcd";
	String hash="";
	String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
	
	if(UtilValidate.isEmpty(params.get("hash")) && params.size()>0)
	{
		if( UtilValidate.isEmpty(params.get("key"))
			|| UtilValidate.isEmpty(params.get("txnid"))
			|| UtilValidate.isEmpty(params.get("amount"))
			|| UtilValidate.isEmpty(params.get("firstname"))
			|| UtilValidate.isEmpty(params.get("email"))
			|| UtilValidate.isEmpty(params.get("phone"))
			|| UtilValidate.isEmpty(params.get("productinfo"))
			|| UtilValidate.isEmpty(params.get("surl"))
			|| UtilValidate.isEmpty(params.get("furl"))	){
			
			error=1;
			Debug.log("=====key====="+params.get("key"));
		}
		else{
			String[] hashVarSeq=hashSequence.split("\\|");
			
			for(String part : hashVarSeq)
			{
				hashString= (UtilValidate.isEmpty(params.get(part)))?hashString.concat(""):hashString.concat(params.get(part));
				hashString=hashString.concat("|");
			}
			hashString=hashString.concat(salt);
			
			Debug.log("=====hashString====="+hashString);
			
			 hash=hashCal("SHA-512",hashString);
			action1=base_url.concat("/_payment");
		}
	}
	else if(!UtilValidate.isEmpty(params.get("hash")))
	{
		hash=params.get("hash");
		action1=base_url.concat("/_payment");
	}
		//Debug.log("===final===params=====>"+params+"==hash=="+hash);
	context.params=params;
	context.merchant_key=merchant_key;
	context.salt=salt;
	context.action1=action1;
	context.base_url=base_url;
	context.txnid=txnid;
	context.hash=hash;