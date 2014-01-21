
<script type="text/javascript">

function inputDateConversion() {	
	var test = document.getElementById("sdate").value;	
	var current_date = new Date(test);	
	var res = current_date.getTime();;
	document.selectdate.salesDate.value=res;		
}
</script>
<#if requestParameters.salesDate?exists>
<#assign value = requestParameters.salesDate>
<#else>
<#assign value = nowTimestamp>
</#if>


<form name="selectdate" onSubmit="inputDateConversion()"><b>Select Date</b>

	<input type="hidden" name="salesDate" id="sdate"/>

	<@htmlTemplate.renderDateTimeField name="x" event="" action="" value="" className="" alert="" title="Format: dd/MM/yyyy" size="25" maxlength="30" id="temp" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
	<div id="response" class="error"></div>
	<input type="submit" value="Submit" id="button1" class="smallSubmit" onClick="document.getElementById('sdate').value=document.getElementById('temp').value;return dateValidation()"/>
</form>
<br>