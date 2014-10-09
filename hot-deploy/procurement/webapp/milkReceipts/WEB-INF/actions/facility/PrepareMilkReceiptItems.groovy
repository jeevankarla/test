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

Map initMap = FastMap.newInstance();

initMap.put("sendQtyLtrs", BigDecimal.ZERO);
initMap.put("sendFat", BigDecimal.ZERO);
initMap.put("sendSnf", BigDecimal.ZERO);
initMap.put("sendClr", BigDecimal.ZERO);
initMap.put("sendAcid", BigDecimal.ZERO);
initMap.put("sendTemp", BigDecimal.ZERO);
initMap.put("recdQtyLtrs", BigDecimal.ZERO);
initMap.put("recdFat", BigDecimal.ZERO);
initMap.put("recdSnf", BigDecimal.ZERO);
initMap.put("recdClr", BigDecimal.ZERO);
initMap.put("recdAcid", BigDecimal.ZERO);
initMap.put("recdTemp", BigDecimal.ZERO);

Map fcMap = FastMap.newInstance();
Map mcMap = FastMap.newInstance();
Map bcMap = FastMap.newInstance();
fcMap.putAll(initMap);
mcMap.putAll(initMap);
bcMap.putAll(initMap);

context.put("fcMap",fcMap);
context.put("mcMap",mcMap);
context.put("bcMap",bcMap);

