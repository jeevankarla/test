import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilNumber;

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




dcNo = parameters.dcNo;

conditionList =[];
if(UtilValidate.isNotEmpty(dcNo)){

	conditionList.add(EntityCondition.makeCondition("dcNo", EntityOperator.EQUALS, dcNo));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);
	milkTransferTank1 = MilkTransferList[0];
	milkTransferTank2 = MilkTransferList[1];
	partyName = "";
	if(UtilValidate.isNotEmpty(milkTransferTank1.partyIdTo)){
		
		partyId = milkTransferTank1.partyIdTo;
		
	partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
	}
  context.milkTransferTank1 = milkTransferTank1;	
  context.milkTransferTank2 = milkTransferTank2;
  context.partyName = partyName;
}
	
	
	
	
	
	
	
	
	




