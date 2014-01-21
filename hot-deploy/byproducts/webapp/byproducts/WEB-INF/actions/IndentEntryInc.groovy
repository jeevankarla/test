
import org.ofbiz.base.util.*;



indentEntryInc = [:];
indentEntryIncList = [];
for (int i=0;i< context.indentEntryIncListSize ;i++){
	indentEntryInc["sno"] = i+1;
	indentEntryInc["productId"] = "";
	indentEntryInc["quantity"]= "";
	temp = [:];
	temp.putAll(indentEntryInc);
	indentEntryIncList.add(temp);
}
context.indentEntryIncList = indentEntryIncList;



