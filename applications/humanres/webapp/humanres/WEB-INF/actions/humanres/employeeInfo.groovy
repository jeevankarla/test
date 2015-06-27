import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;
import org.ofbiz.party.party.PartyHelper;

partyId = parameters.partyId;

passportExpireDate = "";
personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",partyId), false);
if(UtilValidate.isNotEmpty(personDetails)){
	passportExpireDate = personDetails.get("passportExpireDate");
	if(UtilValidate.isNotEmpty(passportExpireDate)){
		passportExpireDate = UtilDateTime.toDateString(passportExpireDate, "dd/MM/yyyy");
	}
}
context.put("passportExpiryDate", passportExpireDate);