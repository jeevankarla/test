import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.common.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import java.util.Calendar;
import java.util.List;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
EntityFindOptions findOpts = new EntityFindOptions();
findOpts.setMaxRows(1);
findOpts.setFetchSize(1);

orderBy = UtilMisc.toList("salesDate");
periodSalesDateIterator = delegator.find("LMSPeriodSalesSummary", EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_DAY"), null, null, orderBy, findOpts);

saleDateList=EntityUtil.getFieldListFromEntityListIterator(periodSalesDateIterator, "salesDate", true);
fromSaleDate=saleDateList.getFirst();
 orderBydate = UtilMisc.toList("-salesDate");
periodSalesIterator = delegator.find("LMSPeriodSalesSummary", EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_DAY"), null, null, orderBydate, findOpts);

thrusaleDateList=EntityUtil.getFieldListFromEntityListIterator(periodSalesIterator, "salesDate", true);
thruSaleDate=thrusaleDateList.getFirst();
context.fromSaleDate=fromSaleDate;
context.thruSaleDate=thruSaleDate;