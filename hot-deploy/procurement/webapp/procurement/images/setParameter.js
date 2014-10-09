jQuery(document).ready(function() {
	setFormTarget();
});
function setFormTarget(){
	var shedCode = ""; 
		shedCode =	$('[name=shedCode]').val();
		var actionVal = (document.dbfToVbizProc.action);
		var index = actionVal.indexOf("?");
		if(index>0){
			actionVal = actionVal.substring(0,index);
		}
		document.dbfToVbizProc.action = actionVal+"?shedCode="+shedCode;
}
