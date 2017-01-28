<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<style>
.selector {
    width: 100%;
    float: left;
}

.selector .customSelect {
    width: 500px;
    height: 17.2em;
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

.selector-available h2, .selector-chosen h2 {
    border: 1px solid #ccc;
    border-radius: 4px 4px 0 0;
}

.selector-chosen h2 {
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

.selector .selector-available input#branchId {
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
<script>
$(document).ready(function(){
	var SecurityGroupJSON = ${StringUtil.wrapString(SecurityGroupJSON)!'[]'};
	
	$("#partyId").autocomplete({
		autoFocus: true, 
  		source: function( request, response ) {
			$.ajax({
  					url: "LookupBranchCustomers",
  					dataType: "html",
  					data: {
    					ajaxLookup: "Y",
    					term : request.term,
    					partyIdFrom :  $('#branchName').val(),
  					},
  					success: function( data ) {
  						var dom = $(data);
						dom.filter('script').each(function(){
    						$.globalEval(this.text || this.textContent || this.innerHTML || '');
						});
    					response($.map(autocomp, function(v,i){
							return {
        						label: v.label,
        						value: v.value
       						};
						}));
  					}
			});
		},
		select: function(e, ui) {
        	if ( $("#selectedCustList option[value="+ui.item.value+"]").length == 0 ){
  				$('#selectedCustList').append('<option value='+ui.item.value+'>'+ui.item.label+'</option>');
			}
			else{
	    		alert("Already added!");
	    	}
        	$(this).val("");
    		return false;
        }
	  });
	 
	 $("#groupId").autocomplete({ source: SecurityGroupJSON , autoFocus: true,select: function( event, ui ) {
			if ( $("#selectedPermissionList option[value="+ui.item.value+"]").length == 0 ){
  				$('#selectedPermissionList').append('<option value='+ui.item.value+'>'+ui.item.label+'</option>');
			}
			else{
	    		alert("Already added!");
	    	}
        	$(this).val("");
    		return false;
	 } });
	 
	 $('#selectedCustList').find("option:selected").remove();
	 
	 $("#remove-permis-all").click(function() {
	 	$('#selectedPermissionList').find("option").remove();
	 });
	 $("#remove-permis").click(function() {
	 	$('#selectedPermissionList').find("option:selected").remove();
	 });
	 
	 $("#remove-cust-all").click(function() {
	 	$('#selectedCustList').find("option").remove();
	 });
	 $("#remove-cust").click(function() {
	 	$('#selectedCustList').find("option:selected").remove();
	 });
	 
	$('#createUserLogin').click(function(e)
	{
		e.preventDefault();
		var partyIds = $('#selectedCustList').children('option').map(function(i,e){
       		return e.value;
    	}).get();
    	
    	var groupIds = $('#selectedPermissionList').children('option').map(function(i,e){
       		return e.value;
    	}).get();
    	
        if(partyIds == ""){
            alert("Select Customers");
            return false;
        }
        if(groupIds == ""){
            alert("Select Permissions");
            return false;
        }

    	var password = $('#password').val();
    	var form = document.createElement("form");
		$(form).attr("action", "CreateUserLogins").attr("method", "post");
		$(form).attr("enctype", "multipart/form-data");
		
		var input = $("<input>").attr({"type":"hidden","name":"partyIds"}).val(partyIds);
        $(form).append(input);  
		input = $("<input>").attr({"type":"hidden","name":"groupIds"}).val(groupIds);
		$(form).append(input);
		input = $("<input>").attr({"type":"hidden","name":"password"}).val(password);
		$(form).append(input);
		document.body.appendChild(form);
		$(form).submit();
		document.body.removeChild(form);
	});
});
</script>

<div class="selector">

<form action="CreateUserLogin" name="CreateUserLogin" enctype="multipart/form-data" method="post">
<div class="selector-available"><h2 style="padding: 8px;font-weight: 400;font-size: 13px;margin-bottom:0px;">Select Customers</h2>
<p id="id_permissions_filter" class="selector-filter">
	<select name="partyIdFrom" id="branchName" size="1"><option value="">select branch</option>
 <option value="INT23">AGARTALA</option> <option value="INT40">AHMEDABAD</option> <option value="INT53">AIZWL</option> <option value="INT49">ALLEPPY</option> <option value="INT41">ANANTHPUR</option> <option value="INT55">BALARAMPURAM</option> <option value="INT42">BANGALORE</option> <option value="INT24">BARGARH</option> <option value="INT25">BERHAMPORE</option> <option value="INT13">BHAGALPUR</option> <option value="INT50">BHUBANESHWAR</option> <option value="INT27">BURDWAN</option> <option value="INT34">CHENNAI</option> <option value="INT10">COIMBATORE</option> <option value="INT56">Delhi</option> <option value="INT35">ERODE</option> <option value="INT51">GUWAHATI</option> <option value="INT11">HYDERABAD</option> <option value="INT29">IMPHAL</option> <option value="INT14">INDORE</option> <option value="INT20">JAIPUR</option> <option value="INT21">JAMMU</option> <option value="INT36">KANCHEEPURAM</option> <option value="INT12">KANNUR</option> <option value="INT39">WARANGAL</option> <option value="INT37">KARUR</option> <option value="INT9">KOLKATA</option> <option value="INT16">KULLU</option> <option value="INT15">LUCKNOW</option> <option value="INT22">LUDHIANA</option> <option value="INT38">MADURAI</option> <option value="INT17">MORADABAD</option> <option value="INT44">MUMBAI</option> <option value="INT19">MURSHIDABAD</option> <option value="INT18">MUZAFFAR NAGAR</option> <option value="INT45">NAGPUR</option> <option value="INT33">ODISHA</option> <option value="INT8">PANIPAT</option> <option value="INT46">RAIPUR</option> <option value="INT30">RANCHI</option> <option value="INT32">SHANTIPUR</option> <option value="INT31">SIVASAGAR</option> <option value="INT54">TIRUPUR</option> <option value="INT7">VARANASI</option> <option value="INT52">VIJAYAWADA</option></select>
	<input name="partyIds" type="text" placeholder="Type Customer Name" id="partyId" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" style="border: 1px solid #ccc;border-radius: 4px;padding: 5px 6px;margin-top: 0;line-height: 2em;">
</p>
<select multiple class="customSelect" id="selectedCustList" name="selectedCustList"></select><ul><li><a title="Click to remove all customers at once." href="#" id="remove-cust-all" class="selector-chooseall active">Remove all</a></li><li><a title="Click to remove selected customers." href="#" id="remove-cust" class="selector-chooseall active">Remove</a></li></ul></div>

<ul class="selector-chooser"></ul>

<div class="selector-available"><h2 style="padding: 8px;font-weight: 400;font-size: 13px;margin-bottom:0px;">Select Permissions</h2>
<p id="id_permissions_filter" class="selector-filter"> 
	<input name="groupIds" type="text" placeholder="Type Permission Name" id="groupId" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" style="border: 1px solid #ccc;border-radius: 4px;padding: 5px 6px;margin-top: 0;line-height: 2em;">
</p>
<select multiple class="customSelect" id="selectedPermissionList" name="selectedPermissionList"></select><ul><li><a title="Click to remove all permissions at once." href="#" id="remove-permis-all" class="selector-chooseall active">Remove all</a></li><li><a title="Click to remove selected permissions." href="#" id="remove-permis" class="selector-chooseall active">Remove</a></li></ul></div>

<ul class="selector-chooser"></ul>

<div class="selector-available" style="width: 200px;"><h2 style="padding: 8px;font-weight: 400;font-size: 13px;margin-bottom:0px;">Default Password</h2>
<p id="id_permissions_filter" class="selector-filter"> 
	<input name="password" type="password" id="password" placeholder="nhdc123" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" style="border: 1px solid #ccc;border-radius: 4px;padding: 5px 6px;margin-top: 0;line-height: 2em;width:150px;">
</p>


<div class="submit-row">
	<input type="submit" value="Create" id="createUserLogin" name="_save" style="width:150px">
</div>
</form>
</div>