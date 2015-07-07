
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
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
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	var contracterNameObj = ${StringUtil.wrapString(contracterNameObj)}
   var contractorJSON = ${StringUtil.wrapString(contractorJSON)}
$(document).ready(function() {	
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson)}
$("#partyId").autocomplete({ source: contractorJSON }).keydown(function(e){});
	$("input").keyup(function(e){
	  		if(e.target.name == "tankerName"){
	  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
	  			populateVehicleName();
				populateVehicleSpan();
	  		}
	}); 
	$( "#fromDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
		});
	$( "#thruDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
		});	
		$('#ui-datepicker-div').css('clip', 'auto');
});


function populateVehicleSpan(){
	var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson?if_exists)}
	var tempVehJson = vehicleCodeJson[$('[name=tankerName]').val()];
	if(tempVehJson){
	$('span#tankerToolTip').addClass("tooltip");
	$('span#tankerToolTip').removeClass("tooltipWarning");
	var vehicleName = tempVehJson["vehicleName"];
	var vehicleId = tempVehJson["vehicleId"];
	if(!vehicleName){
		vehicleName = vehicleId;
	}
	$('span#tankerToolTip').html(vehicleName);
	$('[name=tankerNo]').val(vehicleId);
	}else{
		$('[name=tankerNo]').val('');
		$('span#tankerToolTip').removeClass("tooltip");
		$('span#tankerToolTip').addClass("tooltipWarning");
		$('span#tankerToolTip').html('Code not found');
		}
	}	
	function populateVehicleName(){
			var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerNo").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=tankerName]').val(selectedValue);
					        populateVehicleSpan();
					    }
				});
	}
	
    function displayName(selection){
	  var value = $("#partyId").val();
	   var name = contracterNameObj[value];
	   $("#contractorName").html("<h6>"+name+"</h6>");
	}
</script>
<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Search Options</h3>
    </div>
    <div class="screenlet-body">
    	<form name="findVehicleRate" id="findVehicleRate" action="findVehicleRate" method="post">
    	<table style="border-spacing: 50px 5px;" border="1">
    	<tr>
    	<td align='left'><span class="h3">Vehicle No</span> </td><td>
    		<input  name="tankerName" size="12pt" type="text" id="tankerNo"  autocomplete="off"  /><span class="tooltip h2" id ="tankerToolTip">none</span></td>
    		<input  name="tankerNo" size="12pt" type="hidden"   autocomplete="off" required/></td>
    	</td>
       </tr>
       <tr>
			<td><span class="h3">Contractor </span></td>
			<td><input type="text" name="partyId" id="partyId" size="12" onblur="javascript:displayName(this);"/><span class="tooltip h2" id="contractorName"/> </td>
		</tr>
        <tr>
    	<td align='left'><span class="h3">Rate </span> </td><td>
    		<input  name="rateAmount" size="12pt" type="text" id="rateAmount"   /></td>
    	</td>
       </tr>
       <tr>
    	<td align='left'><span class="h3">From Date</span> </td><td>
    		<input  name="fromDate" size="12pt" type="text" id="fromDate"   /></td>
    	</td>
       </tr>
        <tr>
    	<td align='left'><span class="h3">Thru Date</span> </td><td>
    		<input  name="thruDate" size="12pt" type="text" id="thruDate"   /></td>
    	</td>
       </tr>
       <tr>
		<td></td>
		<td><input type="submit" style="padding:.3em" id="changeSave" value="Find" /></td>
		</tr>
    	</table>
        </form>
    </div>
</div>        	
