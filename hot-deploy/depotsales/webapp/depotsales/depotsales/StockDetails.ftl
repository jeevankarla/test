
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<style type="text/css">
	
	
	.ui-tooltip-titlebar {
	  position: relative;
	  min-height: 30px;
	  padding: 5px 35px 5px 10px;
	  overflow: hidden;
	  border-radius: 20px;
	  border-width: 0 0 5px;
	  font-weight: bold;
	}
</style>

<script type="application/javascript">
	$(document).ready(function() {
	  $("select[name='partyIdFrom']").change(function() {
	     populateBranchDepots()
	  });
	});
	function populateBranchDepots() {
		var ownerPartyId = $("#FindStockDetails_partyIdFrom").val();
		$.ajax({
			 type: "POST",
             url: 'populateDepot',
             data: {ownerPartyId : ownerPartyId},
             dataType: 'JSON',
	            
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Fetching available Depots');
				}else{
					var optionList = [];
					var depotJSON = result["depotJSON"];
					$.each(depotJSON, function(key, item){
					  optionList.push('<option value="'+item.value+'">'+item.label+'</option>');
					});
					$("select[name='depot']").html(optionList.join(''));
				}								 
			},
			error: function(){
				alert("record not found");
			}							
		});
		
	}	
</script>
