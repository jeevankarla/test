<#include "PrePackInc.ftl"/>

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<script type="application/javascript">

		
		
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
	function makeDayDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}
    $(document).ready(function(){
		makeDayDatePicker("effectiveDate","fromDateId");
		$('#ui-datepicker-div').css('clip', 'auto');
 		var hideSubmit = document.getElementById('submitDiv');
        hideSubmit.setAttribute('class', 'hidden');
	});
	function cancellPrePackEntry(){
     location.reload();
    }
   

</script>

<style type="text/css">
.styled-button {
	-webkit-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	color:#333;
	background-color:#FA2;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	border:none;
	font-family:'Helvetica Neue',Arial,sans-serif;
	font-size:25px;
	font-weight:700;
	height:32px;
	padding:4px 16px;
	text-shadow:#FE6 0 1px 0
}
   .hidden {
        display: none;
   {
   .visible {
        display: block;
   }

</style>	
<form id="AddPrePackProducts"  action="<@ofbizUrl>cancelPrePackItems</@ofbizUrl>" name="AddPrePackProducts" method="post">
  <div class="screenlet-title-bar">
	<div class="grid-header" style="width:100%">
	    <label >Pre Pack Section</label>
	</div>
	</div>
    <table class='h2' width="100%">
    <tr>
    <td width="5%"></td>
   <#--<td width="20%"> Date  : <input type="text" value="" name="effectiveDate" id="effectiveDate" size="18" maxlength="60" readOnly autocomplete="off" required/> <font color="red">*</font></td> -->
    <td width="20%"> Shift : <select name="shiftId"  id="shiftId" required >
    				<option value=''></option>
			 <#list  shiftsList as shift>
		          <option value='${shift.enumId?if_exists}'>${shift.description?if_exists}</option>
		        </#list> 
      </select> <font color="red">*</font></td>
    </tr>
    </table>
   </form>
    <div class="screenlet-title-bar">
	 	<div class="grid-header" style="width:100%">
			<label>Product Details</label>
		</div>
		<div id="myGrid" style="width:100%;height:300px;">
		</div>
		<#assign formAction ='processPrePackItems'>		
    	<div id="submitDiv" class"hidden" align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processPrePackEntry('AddPrePackProducts','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:cancellPrePackEntry();"/>   	
    	</div>
 	</div>
 	</div>


