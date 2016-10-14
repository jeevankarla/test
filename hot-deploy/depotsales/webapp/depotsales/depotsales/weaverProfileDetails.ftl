<script type="application/javascript">
function datepick()	{
		$('#daoDate').datetimepicker({
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
 				 <#if facilityList?has_content>
	 				  $('#daoDatelabel').show();
				      $('#daoDate').show();
 				 <#else>
	  				  $('#daoDatelabel').hide();
				      $('#daoDate').hide();
			     </#if>

		$('#daoDate').datetimepicker({
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
			
	});
function setDAO(){
	var  Depot =$( "#Depot   option:selected" ).val();
		  if(Depot=='Y'){
		  		 $('#daoDatelabel').show();
				 $("#daoDate").show();
		  }else{
		  		 $('#daoDatelabel').hide();
			      $('#daoDate').hide();
		  }
	}

</script>
<form name="weaverDetails" id="weaverDetails"   method="post"  action="updateWeaverDetails">
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
       <tr><td width="15%">WeaverId: </td><td>${partyDetailsMapsList[0].partyId} <input type="hidden" name="weaverId" value="${partyDetailsMapsList[0].partyId}"/></td> </tr>
       
       <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyDetailsMapsList[0].partyId, false)>
       <tr><td width="15%">Weaver Name: </td><td>${partyName} </td></tr>
         <tr>
			<td class="label" id="Depotlabel"><b>Depot :</b></td>					    
				 <td>
					 <select name="Depot" id="Depot" onchange="setDAO()">
						<#if facilityList?has_content>
		                     <option value="Y" selected="selected" >Yes</option>
		                     <option value="N">No</option>
		  			  	<#else>
			   				 <option value="Y" >Yes</option>
			   				 <option value="N" selected="selected">No</option>
		                </#if>
				    </select>
				  </td>
				<td class="label" id="daoDatelabel"><b>DAO Date :</b></td>
				<td>    
				<#if facilityList?has_content>
                          <#assign facilityDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(facilityList) />
				    <input class='h3' type="text" name="daoDate" id="daoDate" value="${facilityDetail.openedDate?if_exists}" />           		
				<#else>
					<input class='h3' type="text" name="daoDate" id="daoDate" />           		
				
				</#if>
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