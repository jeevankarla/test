import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

finalList =[];
List fieldsKeyList = FastList.newInstance();

fieldsKeyList.add("qtyLtrs");
fieldsKeyList.add("qtyKgs");
fieldsKeyList.add("kgFat");
fieldsKeyList.add("kgSnf");
fieldsKeyList.add("fat");
fieldsKeyList.add("snf");
fieldsKeyList.add("price");
fieldsKeyList.add("sQtyKgs");
fieldsKeyList.add("cQtyLtrs");
fieldsKeyList.add("ptcCurd");
fieldsKeyList.add("amQtyLtrs");
fieldsKeyList.add("pmQtyLtrs");
fieldsKeyList.add("opCost");
fieldsKeyList.add("cartage");
fieldsKeyList.add("grossAmt");
fieldsKeyList.add("commissionAmount");
fieldsKeyList.add("grsDed");
fieldsKeyList.add("grsAddn");
fieldsKeyList.add("MILKPROC_MTESTER");
fieldsKeyList.add("MILKPROC_OTHERDED");
fieldsKeyList.add("MILKPROC_OTHER_ADDNs");
fieldsKeyList.add("MILKPROC_SEEDDED");
fieldsKeyList.add("MILKPROC_FEEDDED");
fieldsKeyList.add("MILKPROC_STATONRY");
fieldsKeyList.add("MILKPROC_STOREA");
fieldsKeyList.add("MILKPROC_STORET");
fieldsKeyList.add("MILKPROC_VACCINE");
fieldsKeyList.add("MILKPROC_VIJAYALN");
fieldsKeyList.add("MILKPROC_VIJAYARD");

finalList.addAll(fieldsKeyList);
context.put("finalList",finalList);








