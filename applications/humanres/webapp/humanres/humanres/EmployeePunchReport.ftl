
<script type="application/javascript">
     
function selDate() {
    $("#selectMonth").hide();
    $("#selectDate").show();
}
function selMonth() {
    $("#selectDate").hide();
    $("#selectMonth").show();
}

function dateValidation(){
    flag=true;  
    var test = document.getElementById("sdate").value;
    var current_date = new Date(test);
    var res = current_date.getTime();   
    if(isNaN(res)){ 
     document.getElementById('response').innerHTML = 'incorrect Date format Please verify(Format: MM/dd/yyyy)'; 
     flag=false;
    }    
    return flag;
    
}
</script>

<div>
  <a href="#" class="dailyReport" onclick="selDate()">Day Report</a>
  <div id="selectDate" style="display:none">
    <form name="selectDateForm" action="<@ofbizUrl>EmplDailyPunchReport.csv</@ofbizUrl>" method="post">
        <input type="hidden" name="seleDate" id="sdate"/>
        <input type="hidden" name="partyId" value="${parameters.partyId}"/>
        <input type="hidden" name="reportType" value="DAILY"/>
        <@htmlTemplate.renderDateTimeField name="x" event="" action="" value="" className="" alert="" title="Format: MM/dd/yyyy" size="25" maxlength="30" id="temp" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
        <input type="submit" value="Get Report" class="smallSubmit" onClick="document.getElementById('sdate').value=document.getElementById('temp').value;return dateValidation()"/>
    </form>
  </div>
</div>

<div>
  <a href="#" class="monthlyReport" onclick="selMonth()">Monthly Report</a> 
  <div id="selectMonth" style="display:none">
    <form name="selectMonthForm" action="<@ofbizUrl>EmplMonthlyPunchReport.csv</@ofbizUrl>" method="post">
        <input type="hidden" name="partyId" value="${parameters.partyId}"/>
        <input type="hidden" name="reportType" value="MONTHLY"/>
        <label>Select Date</label>
        <select name="selectedMonth">
          <#-- option value="JANUARY">01</option>
          <option value="FEBRUARY">02</option>
          <option value="MARCH">03</option>
          <option value="APRIL">04</option>
          <option value="MAY">05</option>
          <option value="JUNE">06</option>
          <option value="JULY">07</option>
          <option value="AUGUST">08</option>
          <option value="SEPTEMBER">09</option>
          <option value="OCTOBER">10</option>
          <option value="NOVEMBER">11</option>
          <option value="DECEMBER">12</option -->
<option value="01">01</option>
<option value="02">02</option>
<option value="03">03</option>
<option value="04">04</option>
<option value="05">05</option>
<option value="06">06</option>
<option value="07">07</option>
<option value="08">08</option>
<option value="09">09</option>
<option value="10">10</option>
<option value="11">11</option>
<option value="12">12</option>
        </select>
        <input type="submit" value="Get Report" class="smallSubmit"/>
    </form>
  </div>
</div>
