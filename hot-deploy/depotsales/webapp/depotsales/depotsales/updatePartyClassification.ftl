


<input type="hidden" name="partyClassificationGroupId" id="partyClassificationGroupId" value="${partyClassificationGroupId}">

<script type="application/javascript">
function datepick()	{
		$( "#daoDate" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			//onSelect: function(onlyDate){ // Just a work around to append current time without time picker
	        //    var nowTime = new Date(); 
	        //    onlyDate=onlyDate+" "+nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();
	        //    $('#transactionDate').val(onlyDate);
	        //},
	        changeMonth: false,
			numberOfMonths: 1});
					
		$('#ui-datepicker-div').css('clip', 'auto');
	}		
	
	function makeDatePicker(fromDateId ,thruDateId){
	
	$(  "input[name='toDate1']" ).datepicker({
			dateFormat:'mm/dd/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	
	function makeDatePicker1(fromDateId ,thruDateId){
	$( "input[name='cfromDate']").datepicker({
			dateFormat:'mm/dd/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$(  "input[name='ctoDate']" ).datepicker({   
			dateFormat:'mm/dd/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}	
   $(document).ready(function(){
 		 makeDatePicker("fromDate1","toDate1");
 		 makeDatePicker1("cfromDate","ctoDate");		
 		 var calssificationele = document.getElementById('calssification');
		     calssificationele.value = $("#partyClassificationGroupId").val();		
	});

    function submitForm(action,index)
    { 
    	
    	if(action == "updateForm")
    	{
            var clasid="#calssification_"+index;   
            var classification= $(clasid).val();

			var frmDate="#fromDate_"+index;
            var fDate= $(frmDate).val();

			var toDate="#toDate_"+index;
            var tDate= $(toDate).val();

			var weverId='${partyDetailsMapsList[0].partyId}';
		    var appendStr = "<input type=hidden name=partyClassificationGroupId value="+classification+" />";
			appendStr += "<input type=hidden name=partyId value="+weverId+" />";
			appendStr += "<input type=hidden name=actionType value="+action+" />";
			appendStr += "<input type=hidden name=fromDate value="+fDate+" />";
			appendStr += "<input type=hidden name=thruDate value="+tDate+" />";
			
			$('#updateForm').append(appendStr);
    		$('#updateForm').submit();
    	}
    	
    	if(action == "createForm")
    	{
            var classification= $('#calssification').val();
            var fDate= $('#cfromDate').val();
            var tDate= $('#ctoDate').val();
			var weverId='${partyDetailsMapsList[0].partyId}';
		    var appendStr = "<input type=hidden name=partyClassificationGroupId value="+classification+" />";
			appendStr += "<input type=hidden name=partyId value="+weverId+" />";
			appendStr += "<input type=hidden name=actionType value="+action+" />";
			appendStr += "<input type=hidden name=fromDate value="+fDate+" />";
			appendStr += "<input type=hidden name=thruDate value="+tDate+" />";
			$('#createForm').append(appendStr);
    		$('#createForm').submit();
    	}
    	
    }
</script>
<form name="createForm" id="createForm"   method="post"  action="createOrUpdateWeaversPartyClassification"> </form>
<form name="updateForm" id="updateForm"   method="post"  action="createOrUpdateWeaversPartyClassification"> </form>

<form name="PartyClassification" id="PartyClassification"   method="post">
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
       <tr><td width="15%">WeaverId: </td><td>${partyDetailsMapsList[0].partyId} <input type="hidden" name="weaverId" value="${partyDetailsMapsList[0].partyId}"/></td> </tr>
       
       <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyDetailsMapsList[0].partyId, false)>
       <tr><td width="15%">Weaver Name: </td><td>${partyName} </td></tr>
       <#assign index=0>
		<#list partyClassificationDetails as eachRecord>
		<tr>
			<td id="Calssilabel"><b>Calssification :</b><br>
				<select name="calssification1" id="calssification_${index}" >
						<option value=${eachRecord.partyClassificationGroupId}  >${eachRecord.partyClassificationGroupId}</option>
						<#-- <#list partyClassiTYpeList as partyClassiTYpeList>
		                     <option value=${partyClassiTYpeList.partyClassificationGroupId}  >${partyClassiTYpeList.description}</option>
		                </#list>  -->
			    </select>
			</td>					    
		  	<td ><b>From Date :</b> <input type="text" name="fromDate1" id="fromDate_${index}" size="18" maxlength="40"   <#if eachRecord.fromDate?has_content> value="${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachRecord.fromDate, "MM/dd/yyyy")}"</#if> readonly /></td> 
		  	<td ><b>To Date :</b><input type="text" name="toDate1" id="toDate_${index}" size="18" maxlength="40"  <#if eachRecord.thruDate?has_content>value="${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachRecord.thruDate, "MM/dd/yyyy")}"</#if> /></td> 
		  	<td><input type="button" name="update" value="update" onClick="javascript:submitForm('updateForm','${index}');"/></td>
		</tr>
		<tr>
		<#assign index=index+1>
		</#list> 
        <tr>
			<td id="Calssilabel"><b>Calssification :</b>
				<select name="calssification2" id="calssification" >
						<#list partyClassiTYpeList as partyClassiTYpeList>
		                     <option value=${partyClassiTYpeList.partyClassificationGroupId}  >${partyClassiTYpeList.description}</option>
		                </#list>
			    </select>
			</td>					    
		  	<td ><b>From Date :</b> <input type="text" name="cfromDate" id="cfromDate" size="18" maxlength="40" </td> 
		  	<td ><b>To Date :</b><input type="text" name="ctoDate" id="ctoDate" size="18" maxlength="40"/></td>
		  	<td><input type="button" name="create" value="Create" onClick="javascript:submitForm('createForm');"/></td>  
		</tr>
	
      </thead>
      <tbody>
      </tbody>
    </table>
  </form>