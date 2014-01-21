import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;


netSalesMap=[:];

shipDate=UtilDateTime.toTimestamp(context.estimatedDeliveryDate);
context.put("shipDate",shipDate);
netSalesMap[shipDate]=context.grandTotalMap;
netSalesList.add(netSalesMap);
context.put("netSalesList",netSalesList);
listShipments.removeAll(shipmentIds);
context.put("shipmentIds",listShipments);
