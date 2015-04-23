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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.party.party.PartyHelper;

if(UtilValidate.isNotEmpty(parameters.incidentDate)){
	Timestamp fromDateTime=null;
	def sdf = new SimpleDateFormat("dd:MM:yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.incidentDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+parameters.incidentDate, "");
	}
	fromDateTime = UtilDateTime.getDayStart(fromDateTime);
	context.fromDateTime=fromDateTime;	
}