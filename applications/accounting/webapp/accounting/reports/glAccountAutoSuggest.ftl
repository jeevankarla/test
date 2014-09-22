<script type="text/javascript">
	$(document).ready(function(){
		var glAccountJSON = ${StringUtil.wrapString(glAccountJSON)!'[]'};
		var glAccountDescriptionJSON = ${StringUtil.wrapString(glAccountDescriptionJSON)!'{}'};
		$("#glAccountId").autocomplete({ source: glAccountJSON }).keydown(function(e){
		});
		
		$("#glAccountId").blur(function() {
			var glVal = $("#glAccountId"). val();
			var glDescription = "";
			if(glAccountDescriptionJSON){
				
				glDescription = glAccountDescriptionJSON[glVal];
				$(".tooltip").text(glDescription);
			}
			if(!glVal){
				$(".tooltip").text("");
			}
			
		});
		
	});
</script>