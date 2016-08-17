


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
		
   $(document).ready(function(){
 				
 		 var calssificationele = document.getElementById('calssification');
		     calssificationele.value = $("#partyClassificationGroupId").val();		
	});


</script>
<form name="PartyClassificationDetails" id="PartyClassificationDetails"   method="post"  action="updateWeaverPartyClassification">
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
       <tr><td width="15%">WeaverId: </td><td>${partyDetailsMapsList[0].partyId} <input type="hidden" name="weaverId" value="${partyDetailsMapsList[0].partyId}"/></td> </tr>
       
       <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyDetailsMapsList[0].partyId, false)>
       <tr><td width="15%">Weaver Name: </td><td>${partyName} </td></tr>
        <tr>
			<td class="label" id="Calssilabel"><b>Calssification :</b></td>					    
				 <td>
					 <select name="calssification" id="calssification" >
						<#list partyClassiTYpeList as partyClassiTYpeList>
		                     <option value=${partyClassiTYpeList.partyClassificationGroupId}  >${partyClassiTYpeList.description}</option>
		                </#list>
				    </select>
				  </td>
		</tr>
		<tr>
		<td></td>
		<td><input type="submit" name="update" value="update"/></td>
		</tr>
      </thead>
      <tbody>
      </tbody>
    </table>
  </form>