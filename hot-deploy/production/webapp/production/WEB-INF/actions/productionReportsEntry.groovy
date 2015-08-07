import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


conditionList =[];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "PLANT"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkVehicleTransferList = delegator.findList("Facility", condition, null, null, null, false);
List floorList = EntityUtil.getFieldListFromEntityList(milkVehicleTransferList, "facilityId", false);
context.floorList=floorList;
