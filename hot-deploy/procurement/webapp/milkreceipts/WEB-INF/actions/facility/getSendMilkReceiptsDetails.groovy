import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.Container;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;


dctx = dispatcher.getDispatchContext();

Map sendQtyDetailsMap = FastMap.newInstance();


sendQtyDetailsMap.put("fcQtyLtrs", BigDecimal.ZERO);
sendQtyDetailsMap.put("fcFat", BigDecimal.ZERO);
sendQtyDetailsMap.put("fcSnf", BigDecimal.ZERO);
sendQtyDetailsMap.put("fcTemp", BigDecimal.ZERO);
sendQtyDetailsMap.put("fcAcid", BigDecimal.ZERO);
sendQtyDetailsMap.put("fcLR", BigDecimal.ZERO);

sendQtyDetailsMap.put("mcQtyLtrs", BigDecimal.ZERO);
sendQtyDetailsMap.put("mcFat", BigDecimal.ZERO);
sendQtyDetailsMap.put("mcSnf", BigDecimal.ZERO);
sendQtyDetailsMap.put("mcTemp", BigDecimal.ZERO);
sendQtyDetailsMap.put("mcAcid", BigDecimal.ZERO);
sendQtyDetailsMap.put("mcLR", BigDecimal.ZERO);

sendQtyDetailsMap.put("bcQtyLtrs", BigDecimal.ZERO);
sendQtyDetailsMap.put("bcFat", BigDecimal.ZERO);
sendQtyDetailsMap.put("bcSnf", BigDecimal.ZERO);
sendQtyDetailsMap.put("bcTemp", BigDecimal.ZERO);
sendQtyDetailsMap.put("bcAcid", BigDecimal.ZERO);
sendQtyDetailsMap.put("bcLR", BigDecimal.ZERO);

String milkTransferId = parameters.milkTransferId;
List<GenericValue> milkTransferItemList = FastList.newInstance();
List conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

milkTransferItemList = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,null,null,false);
/*for(mlkTrnItem in milkTransferItemList){	
	String cellType = null;
	cellType = (String) mlkTrnItem.get("cellType");
	if(UtilValidate.isNotEmpty(cellType)){
	sendQtyDetailsMap.put("vehicleId",mlkTrnItem.get("vehicleId"));
	sendQtyDetailsMap.put("capacity",mlkTrnItem.get("capacity"));
	sendQtyDetailsMap.put("cob",mlkTrnItem.get("sendCob"));
	sendQtyDetailsMap.put("productId",mlkTrnItem.get("sendProductId"));
	sendQtyDetailsMap.put("facilityId",mlkTrnItem.get("facilityId"));
	sendQtyDetailsMap.put("facilityIdTo",mlkTrnItem.get("facilityIdTo"));
	sendQtyDetailsMap.put("sendDate",mlkTrnItem.get("sendDate"));
	 if(cellType.equalsIgnoreCase("FC")){
		 sendQtyDetailsMap.put("fcQtyLtrs", mlkTrnItem.get("quantityLtrs"));
		 sendQtyDetailsMap.put("fcFat",  mlkTrnItem.get("fat"));
		 sendQtyDetailsMap.put("fcSnf",  mlkTrnItem.get("snf"));
		 sendQtyDetailsMap.put("fcTemp",  mlkTrnItem.get("sendTemparature"));
		 sendQtyDetailsMap.put("fcAcid",  mlkTrnItem.get("sendAcidity"));
		 sendQtyDetailsMap.put("fcLR",  mlkTrnItem.get("sendLR"));
		 }else if(cellType.equalsIgnoreCase("MC")){
		 	sendQtyDetailsMap.put("mcQtyLtrs", mlkTrnItem.get("quantityLtrs"));
			sendQtyDetailsMap.put("mcFat",  mlkTrnItem.get("fat"));
			sendQtyDetailsMap.put("mcSnf",  mlkTrnItem.get("snf"));
			sendQtyDetailsMap.put("mcTemp",  mlkTrnItem.get("sendTemparature"));
			sendQtyDetailsMap.put("mcAcid",  mlkTrnItem.get("sendAcidity"));
			sendQtyDetailsMap.put("mcLR",  mlkTrnItem.get("sendLR"));
		 }else{
			 sendQtyDetailsMap.put("bcQtyLtrs", mlkTrnItem.get("quantityLtrs"));
			 sendQtyDetailsMap.put("bcFat",  mlkTrnItem.get("fat"));
			 sendQtyDetailsMap.put("bcSnf",  mlkTrnItem.get("snf"));
			 sendQtyDetailsMap.put("bcTemp",  mlkTrnItem.get("sendTemparature"));
			 sendQtyDetailsMap.put("bcAcid",  mlkTrnItem.get("sendAcidity"));
			 sendQtyDetailsMap.put("bcLR",  mlkTrnItem.get("sendLR"));
		 }
	 
	}
	}
context.putAt("sendQtyDetailsMap", sendQtyDetailsMap);*/

milkTankerCellsList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"MILK_CELL_TYPE"), null, ['sequenceId'], null, true);
milkCellsList=EntityUtil.getFieldListFromEntityList(milkTankerCellsList, "enumId", false);
milkCellsList.add("FC_ACK");
milkCellsList.add("MC_ACK");
milkCellsList.add("BC_ACK");
JSONArray dataJSONList= new JSONArray();
cellWiseDetailsMap=[:];
for(mlkTrnItem in milkTransferItemList){
	String cellType = null;
	cellType = (String) mlkTrnItem.get("cellType");
	if(UtilValidate.isNotEmpty(cellType)){	
		cellWiseDetailsMap.put("milkTransferId",mlkTrnItem.get("milkTransferId"));
		cellWiseDetailsMap.put("vehicleId",mlkTrnItem.get("vehicleId"));
		cellWiseDetailsMap.put("capacity",mlkTrnItem.get("capacity"));
		cellWiseDetailsMap.put("cob",mlkTrnItem.get("sendCob"));
		cellWiseDetailsMap.put("productId",mlkTrnItem.get("sendProductId"));
		cellWiseDetailsMap.put("facilityId",mlkTrnItem.get("facilityId"));
		cellWiseDetailsMap.put("facilityIdTo",mlkTrnItem.get("facilityIdTo"));
		cellWiseDetailsMap.put("sendDate",mlkTrnItem.get("sendDate"));
		cellWiseDetailsMap.put("milkCondition",mlkTrnItem.get("milkCondition"));	
		cellWiseDetailsMap.put("sendTime",mlkTrnItem.get("sendTime"));
		cellWiseDetailsMap.put("soda",mlkTrnItem.get("soda"));
		milkCellsList.each{ cellDetails->
			if(cellType.equals(cellDetails)){
				JSONObject newObj = new JSONObject();
				newObj.put("id",cellDetails);
				newObj.put("quantity",mlkTrnItem.get("quantity"));
				newObj.put("quantityLtrs",mlkTrnItem.get("quantityLtrs"));
				newObj.put("fat",mlkTrnItem.get("fat"));
				newObj.put("snf",mlkTrnItem.get("snf"));
				newObj.put("acid",mlkTrnItem.get("sendAcidity"));
				newObj.put("temp",mlkTrnItem.get("sendTemparature"));
				newObj.put("clr",mlkTrnItem.get("sendLR"));	
				newObj.put("sequenceNum", mlkTrnItem.get("sequenceNum"));
				dataJSONList.add(newObj);
				if(cellType+"_"+"ACK".equals(cellDetails)){
					newObj.put("id",cellType+"_"+"ACK");
					newObj.put("quantity","");
					newObj.put("quantityLtrs","");
					newObj.put("fat","");
					newObj.put("snf","");
					newObj.put("acid","");
					newObj.put("temp","");
					newObj.put("clr","");
					dataJSONList.add(newObj);
				}
			}			
		}
	}	
}
context.cellWiseDetailsMap=cellWiseDetailsMap;
context.dataJSON = dataJSONList.toString();


