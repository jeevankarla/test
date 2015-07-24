<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<script type="text/javascript">
   var listSize=${listSize};
	function datetimepick(){
		 var currentTime = new Date();
		 // First Date Of the month 
		 var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
		 // Last Date Of the Month 
		 var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
		  
		 $("#recordDateTime").datetimepicker({
			dateFormat:'dd:mm:yy',
			changeMonth: true,
		    minDate: startDateFrom,
		   // maxDate: startDateTo
		 });	
			
	$('#ui-datepicker-div').css('clip', 'auto');	
	  }
	function numbersOnly(e){
      	var event = e || window.event;
         //alert(event.keyCode);
         if( !(event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 110 || event.keyCode == 190 || event.keyCode == 189 || event.keyCode == 109                            // backspace/Tab/"."
          || event.keyCode == 46                              // delete
          || (event.keyCode >= 35 && event.keyCode <= 40)     // arrow keys/home/end
          || (event.keyCode >= 48 && event.keyCode <= 57)     // numbers on keyboard
          || (event.keyCode >= 96 && event.keyCode <= 105))   // number on keypad
         ) {
           event.preventDefault();     // Prevent character input
    		}
      }
      $(document).ready(function() {	
        datetimepick();
        numbersOnly();
	
       });  
       function processFacilityTemp(){
       	var countVal=0;
       	var recordDateTime = $("#recordDateTime").val();
       	var recordDateTimeStr = jQuery("<input>").attr("type", "hidden").attr("name", "recordDateTime").val(recordDateTime);
		jQuery("#AddFacilityTemperature").append(jQuery(recordDateTimeStr));
       	for(var i=0;i<listSize;i++){
	       	var facilityId = "facilityId_o_"+[i];
	       	var temperature = "temperature_o_"+[i];
	       	var comments = "comments_o_"+[i];
	       	var facilityIdVal = $("[name="+facilityId+"]").val();
	       	var temperatureVal = $("[name="+temperature+"]").val();
	       	var commentsVal = $("[name="+comments+"]").val();
	       	if(temperatureVal != undefined && temperatureVal!="" && temperatureVal!=null){
	       		var facilityIdStr = jQuery("<input>").attr("type", "hidden").attr("name", "facilityId_o_"+countVal).val(facilityIdVal);
				var temperatureStr = jQuery("<input>").attr("type", "hidden").attr("name", "temperature_o_"+countVal).val(temperatureVal);
				var commentsStr = jQuery("<input>").attr("type", "hidden").attr("name", "comments_o_"+countVal).val(commentsVal);
				jQuery("#AddFacilityTemperature").append(jQuery(facilityIdStr));
				jQuery("#AddFacilityTemperature").append(jQuery(temperatureStr));
		        jQuery("#AddFacilityTemperature").append(jQuery(commentsStr));
	       	
	       		countVal=countVal+1;
	       	}
       	}
       	if(countVal == 0){
	       	 alert("Please Enter At Least One Temperature To Process!.");
	       	 return false;
	       	 
	     }
		jQuery("#AddFacilityTemperature").submit();
       }
</script>
    <div>
        <form id="AddFacilityTemperature" name="AddFacilityTemperature" method="post" action="processFacilityTemperature"></form>     
       	<form id="AddTempRecord" name="AddTempRecord"  method="post" >
       	<table class="basic-table hover-bar h3" widht='80%' style="border-spacing: 50px 5px;" border="1"> 
       	  <tr>
       	  	<td align='left'><h3>Date Time</h3></td>
       	  	<td >:
       	  	 <input type="text" id="recordDateTime" name="recordDateTime" onmouseover="datetimepick()"  required/></td>
       	  	</td>
       	  	<td></td>
		    <td></td>
       	  </tr>
       	  <tr>
       	  <td ><span class="label"><u>SILO / PLANT</u></span></td>
       	  <td ><span class="label"><u>TEMPERATURE<u></span></td>
       	  <td ><span class="label"><u>COMMENTS</u></span></td>
       	  </tr>
       	  <#assign index =0>
       	  <#list facilityGroupAndMemberAndFacility as facility>	
           <tr>
           	<td>
				<span class="label">${facility.description?if_exists} </span><input name="facilityId_o_${index}" id="facilityId_o_${index}" value="${facility.facilityId}" type="hidden" size="10"/></input>
			</td>
			<td>:
				<input type="text" size="10" maxlength="15" name="temperature_o_${index}" id="temperature_o_${index}" onkeydown="numbersOnly()"/></input>
			</td>
            <td><textarea name="comments_o_${index}" id="comments_o_${index}" ></textarea></td>
           </tr> 
          <#assign index=index+1>
       	  </#list>
			 <tr><td></td><td align='left'><span class='h2'><input type="button"  size="15" value="Add" class="buttontext h1" onclick="javascript:processFacilityTemp();"/></span> </td></tr>
        </table>
       	</form>
    </div>

