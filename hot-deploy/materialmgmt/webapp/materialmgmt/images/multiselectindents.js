
jQuery(document).ready(function() {
	//alert("welcome");
});



function checkAllIndentApprovalStatus(master){
	 var shipIds = $('input[name="IndentHeadApproval"]');
    jQuery.each(shipIds, function() {
         this.checked = master.checked;
     });
 }

function getAllIndentApprovals(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..from Approval!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
	 var makeMassApproval = 'makeMassApproval';	
	    var action;
     jQuery.each(custRqstList, function() {
   	  var curreElem = $(this);
 		var varform = curreElem.parent().parent();
 	    var form = curreElem.parent().parent().find("form");
 	    var formId = form.attr('id');
 	    var str = "#"+formId;
 	    var tempQty=$(varform).find("[name='"+"tempQty"+"']");
 	    var tempQty=$(tempQty).val();
   	 var custRqst=$(this).val();
        var inputElementIdSplit = custRqst.split('_');
   	 var statusId="CRQ_SUBMITTED";   
   	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
        appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
        appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
        appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
        appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
 	$("#ListIndentSubmit").append(appendStr);
 	index = index+1;
    });
     action= makeMassApproval;
     jQuery('#ListIndentSubmit').attr("action", action);
     jQuery('#ListIndentSubmit').submit();
}

function getAllIndentRejects(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
	 var makeMassReject = 'makeMassReject';	
	    var action;
     jQuery.each(custRqstList, function() {
   	  var curreElem = $(this);
 		var varform = curreElem.parent().parent();
 	    var form = curreElem.parent().parent().find("form");
 	    var formId = form.attr('id');
 	    var str = "#"+formId;
 	    var tempQty=$(varform).find("[name='"+"tempQty"+"']");
 	    var tempQty=$(tempQty).val();
   	 var custRqst=$(this).val();
        var inputElementIdSplit = custRqst.split('_');
   	 var statusId="CRQ_REJECTED";
   	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
        appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
        appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
        appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
        appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
 	$("#ListIndentSubmit").append(appendStr);
 	index = index+1;
    });
     action= makeMassReject;
     jQuery('#ListIndentSubmit').attr("action", action);
     jQuery('#ListIndentSubmit').submit();
}

