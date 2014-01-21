jQuery(document).ready(function() {
	
	setFormTarget();
	
});
function setFormTarget(){
	var shedCode = ""; 
		shedCode =	$('[name=shedCode]').val();
		var actionVal = (document.dbfToVbiz.action);
		var index = actionVal.indexOf("?");
		if(index>0){
			actionVal = actionVal.substring(0,index);
		}
		document.dbfToVbiz.action = actionVal+"?shedCode="+shedCode;
}
