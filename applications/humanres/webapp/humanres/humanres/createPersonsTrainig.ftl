<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />


<style>
	.selector .customSelect {
    width: 300px;
    height: 17.2em;
    text-align: center;
}
	.selector-available, .selector-chosen {
    float: left;
    width: 500px;
    text-align: center;
    margin-bottom: 5px;
}
.selector-chosen select {
    border-top: none;
}
selector-available h2, .selector-chosen h2 {
    border: 1px solid #ccc;
    border-radius: 4px 4px 0 0;
}
selector-chosen h2 {
    background: #79aec8;
    color: #fff;
}
.selector .selector-available h2 {
    background: #f8f8f8;
    color: #666;
}
.selector .selector-filter {
    background: white;
    border: 1px solid #ccc;
    padding: 8px;
    color: #999;
    font-size: 10px;
    margin: 0;
    text-align: center;
}
.selector .selector-filter label,
.inline-group .aligned .selector .selector-filter label {
    float: left;
    margin: 7px 0 0;
    width: 18px;
    height: 18px;
    padding: 0;
    overflow: hidden;
    line-height: 1;
}

.selector .selector-available input{
    width: 440px;
    margin-left: 8px;
}
.selector ul.selector-chooser {
    float: left;
    width: 22px;
    background-color: #eee;
    border-radius: 10px;
    margin: 10em 5px 0 5px;
    padding: 0;
}
.selector-chooser li {
    margin: 0;
    padding: 3px;
    list-style-type: none;
}

.selector select {
    padding: 0 10px;
    margin: 0 0 10px;
    border-radius: 0 0 4px 4px;
}
.selector-add, .selector-remove {
    width: 16px;
    height: 16px;
    display: block;
    text-indent: -3000px;
    overflow: hidden;
    cursor: default;
    opacity: 0.3;
}

.active.selector-add, .active.selector-remove {
    opacity: 1;
}

.active.selector-add:hover, .active.selector-remove:hover {
    cursor: pointer;
}

.active.selector-add:focus, .active.selector-add:hover {
    background-position: 0 -112px;
}

.active.selector-remove:focus, .active.selector-remove:hover {
    background-position: 0 -80px;
}

a.selector-chooseall, a.selector-clearall {
    display: inline-block;
    height: 16px;
    text-align: left;
    margin: 1px auto 3px;
    overflow: hidden;
    font-weight: bold;
    line-height: 16px;
    color: #FF7922;
    text-decoration: none;
}
.submit-row {
    padding: 12px 14px;
    margin: 0 0 20px;
    background: #f8f8f8;
    border: 1px solid #eee;
    border-radius: 4px;
    overflow: hidden;
}

body.popup .submit-row {
    overflow: auto;
}

.submit-row input {
    height: 35px;
    line-height: 15px;
    margin: 0 0 0 5px;
}

.submit-row input.default {
    margin: 0 0 0 8px;
    text-transform: uppercase;
}

.submit-row p {
    margin: 0.3em;
}

.submit-row p.deletelink-box {
    float: left;
    margin: 0;
}

.submit-row a.deletelink {
    display: block;
    background: #ba2121;
    border-radius: 4px;
    padding: 10px 15px;
    height: 15px;
    line-height: 15px;
    color: #fff;
}

.submit-row a.deletelink:focus,
.submit-row a.deletelink:hover,
.submit-row a.deletelink:active {
    background: #a41515;
}
.selector ul li{
  display: inline;
  margin:15px;
}
select {
    vertical-align: top;
    height: 24px;
    background: none;
    color: #000;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    padding: 0 0 0 4px;
    margin: 0;
    margin-left: 10px;
}
.submit-row .createButton {
	margin:auto;
	margin-left:0px;
}

</style>
<script type="text/javascript">
	$(document).ready(function(){
	
	  var employeesJSON = ${StringUtil.wrapString(employeesJSON)!'[]'};
	  
	 $("#employeeId").autocomplete({ source: employeesJSON , autoFocus: true,select: function( event, ui ) {
			if ( $("#selectedCustList option[value="+ui.item.employeeId+"]").length == 0 ){
  				$('#selectedCustList').append('<option value='+ui.item.employeeId+'>'+ui.item.name+'</option>');
			}
			else{
	    		alert("Already added!");
	    	}
        	$(this).val("");
    		return false;
	 } });
	 
	  $('#selectedCustList').find("option:selected").remove();
	 
	 $("#remove-permis-all").click(function() {
	 	$('.selectedPermissionList').find("option").remove();
	 });
	 $("#remove-permis").click(function() {
	 	$('.selectedPermissionList').find("option:selected").remove();
	 });
	 
	 $("#remove-cust-all").click(function() {
	 	$('#selectedCustList').find("option").remove();
	 });
	 $("#remove-cust").click(function() {
	 	$('#selectedCustList').find("option:selected").remove();
	 });
	
	$('#createPersonTrainig').click(function(e)
	{
		e.preventDefault();
		var partyIds = $('#selectedCustList').children('option').map(function(i,e){
       		return e.value;
    	}).get();
    	
        if(partyIds == ""){
            alert("Select employees");
            return false;
        }
    	var topicsCoverd = $('#topicsCoverd').val();
    	var trainingLocation = $('#trainingLocation').val();
    	var fromDate = $('#fromDate').val();
    	var thruDate = $('#thruDate').val();
    	var duration = $('#duration').val();
    	var trgCategory = $('#trgCategory').val();
    	var facultyType = $('#facultyType').val();
    	var nameOfInstitute = $('#nameOfInstitute').val();
    	var traingCost = $('#traingCost').val();
    	
    	var form = document.createElement("form");
		$(form).attr("action", "createNewTraining").attr("method", "post");
		$(form).attr("enctype", "multipart/form-data");
		
		var input = $("<input>").attr({"type":"hidden","name":"partyIdList"}).val(partyIds);
        $(form).append(input);  
		input = $("<input>").attr({"type":"hidden","name":"topicsCoverd"}).val(topicsCoverd);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"trainingLocation"}).val(trainingLocation);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"fromDate"}).val(fromDate);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"thruDate"}).val(thruDate);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"duration"}).val(duration);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"trgCategory"}).val(trgCategory);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"facultyType"}).val(facultyType);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"nameOfInstitute"}).val(nameOfInstitute);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"traingCost"}).val(traingCost);
		$(form).append(input);
		document.body.appendChild(form);
		$(form).submit();
		document.body.removeChild(form);
	});
});
	
</script>




<div class="selector">
	<div class="screenlet-title-bar">
      	<h3>Create New Training</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="createTrainig">
		<table class="basic-table" cellspacing="0">
			<tr>
				<td align="right" width="10%"><span class='h2'>${uiLabelMap.Topic}  </td>
				<td><input type="text" name="topicsCoverd" id="topicsCoverd" class="commonPartyId"></td>
				<td><span class='h2'>Employee Id: <input name="employeeId" type="text" placeholder="Type Employee Name" id="employeeId" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" ></span><td>
			</tr>
        	<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.Venue}  </td>
				<td><input type="text" name="trainingLocation" id="trainingLocation" class="commonPartyId"></td>
				<td rowspan="7"><select multiple class="customSelect" id="selectedCustList" name="selectedCustList"></select><ul><li><a title="Click to remove all customers at once." href="#" id="remove-cust-all" class="selector-chooseall active">Remove all</a></li><li><a title="Click to remove selected customers." href="#" id="remove-cust" class="selector-chooseall active">Remove</a></li></ul></div>
				</td>
			</tr>
			<tr>
				<td align="right" width="10%"><span class='h2'>From Date </span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="fromDate" name="fromDate"/></td> 
			</tr>
			<tr>
				<td align="right" width="10%"><span class='h2'>${uiLabelMap.toDate}</span></td>
            	<td width="20%"><input class="mycalendar" type="text" id="thruDate" name="thruDate"/></td>
			</tr>
			<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.duration}  </td>
				<td><input type="text" name="duration" id="duration" class="commonPartyId"></td>
			</tr>
			<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.trgCategory}  </td>
				<td><input type="text" name="trgCategory" id="trgCategory" class="commonPartyId"></td>
			</tr>
			<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.facultyType}  </td>
				<td><input type="text" name="facultyType" id="facultyType" class="commonPartyId"></td>
			</tr>
			<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.nameOfInstitute}  </td>
				<td><input type="text" name="nameOfInstitute" id="nameOfInstitute" class="commonPartyId"></td>
			</tr>
			<tr>
        		<td align="right" width="10%"><span class='h2'>${uiLabelMap.TotalCost}  </td>
				<td><input type="text" name="traingCost" id="traingCost" class="commonPartyId"></td>
				<td align="center"><input type="submit" value="Create" id="createPersonTrainig" class="smallSubmit" /></td>
			</tr>
			
    	</table>    	
	</form>
	</div>
</div>