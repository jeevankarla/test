
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="text/javascript">

	$(document).ready(function(){
		$("#productId").trigger("onchange");
		
		jQuery("input[name='snf']").parent().parent().hide();
		jQuery("input[name='fat']").parent().parent().hide();
		
		
	}); 
	
	function refreshFields(){
		
		var productId = $("#productId").find(":selected").val();
		if(productId == "RAW_MILK"){
			jQuery("input[name='snf']").parent().parent().show();
			jQuery("input[name='fat']").parent().parent().show();
		}else{
			jQuery("input[name='snf']").parent().parent().hide();
			jQuery("input[name='fat']").parent().parent().hide();
		}
	
	}
	
	
	
</script>

               