
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-effort-driven {
		text-align: center;
	}
	
	.tooltip { /* tooltip style */
    background-color: #ffffbb;
    border: 0.1em solid #999999;
    color: #000000;
    font-style: arial;
    font-size: 80%;
    font-weight: normal;
    margin: 0.4em;
    padding: 0.1em;
}
.tooltipWarning { /* tooltipWarning style */
    background-color: #ffffff;
    border: 0.1em solid #FF0000;
    color: #FF0000;
    font-style: arial;
    font-size: 80%;
    font-weight: bold;
    margin: 0.4em;
    padding: 0.1em;
}	

.messageStr {
    background:#e5f7e3;
    background-position:7px 7px;
    border:4px solid #c5e1c8;
    font-weight:700;
    color:#005e20;    
    text-transform:uppercase;
}

</style>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}

	$(document).ready(function() {	
		makeDatePicker("fromDate","thruDate");
		$('#MR_VIn').css("background-color", "#A9A9A9");
		$('#MR_VGw').css("background-color", "#F0E68C");
		$('#MR_VQc').css("background-color", "#FF7F50");
		$('#MR_Vunload').css("background-color", "#6495ED");
		$('#MR_VCip').css("background-color","#88FAFA");
		$('#MR_VTw').css("background-color", "#FFEBCD");
		$('#MR_Vout').css("background-color", "#90EE90");
		
	});
	

</script>
<div class="screenlet">
	 <div class="screenlet-title-bar">
      <h3>Find Tanker Status List </h3>
    </div>
    <div class="screenlet-body">
        <form method="post" name="findMilkReceipts" id="findMilkReceipts" action="<@ofbizUrl>MilkReceiptDashboard</@ofbizUrl>">
      		<table width="60%" border="0" cellspacing="0" cellpadding="0">     
        	<tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          	<table>
	          		<tr>
	          			<table>
	        				<tr>
	        					<td><span class='h3'> Date: </span></td><td><input  size="12" type="text" id="fromDate" name="shiftDate" value="${parameters.shiftDate?if_exists}"/>
	        						<input  size="12" type="hidden" id="hideSearch" name="hideSearch" value="N"/>
	        					</td>
	        				</tr>
						    <tr><td align='right'><span class='h2'><input type="submit"  size="10" value="Find" class="buttontext h1"/></span> </td></tr>      					 
                      	</table>
	          		</tr>
	          	</table>
	         </td>  
	       </tr>
      	</table>
	</form>
   </div>
</div>  

<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Status Indications</h3>
    </div>
    <div class="screenlet-body">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
    	
    	<tr>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_VIn"/>VEHICLE IN</td>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_VGw"/>VEHICLE AT GROSS WEIGHT</td>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_VQc"/>VEHICLE AT QC </td>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_Vunload"/>VEHICLE AT UN-LOAD </td>
    	</tr>
    	<tr>	
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_VCip"/>VEHICLE AT CIP </td>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_VTw"/>VEHICLE AT TARE WEIGHT</td>
    		<td ><input type="text" size="1" readOnly="readOnly" id="MR_Vout"/>VEHICLE OUT</td>
    	</tr>
    </table>
    </div>
</div>