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

int nextSeqId;
payGradeId = "";
PayGradeList = delegator.findList("PayGrade", null , null, ["-lastUpdatedStamp"], null, false );

if(UtilValidate.isNotEmpty(PayGradeList)){
	PayGradeList = EntityUtil.getFirst(PayGradeList);
	payGradeId = PayGradeList.get("payGradeId");
	seqId = PayGradeList.get("seqId");
	int sequenceId = Integer.parseInt(seqId);
	nextSeqId = sequenceId + 1;
	
}

context.put("payGradeId", payGradeId);
context.put("seqId", nextSeqId);