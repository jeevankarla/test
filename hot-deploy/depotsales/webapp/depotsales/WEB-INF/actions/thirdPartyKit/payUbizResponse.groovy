
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

//payment gateway response parameters showing here

Debug.log("===paymentIdd===>"+parameters.mihpayid);

Debug.log("===Payment==mode==="+parameters.mode);
Debug.log("===Payment==status==="+parameters.status);
Debug.log("===Payment==TransId==="+parameters.txnid);
Debug.log("===Payment==paid=Amount==="+parameters.amount);
