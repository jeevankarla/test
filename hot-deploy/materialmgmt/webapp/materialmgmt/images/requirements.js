jQuery(document).ready(function() {
	//alert("welcome");
});

function createRequirementForCustRequestItem(element){
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
    var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var str = "#"+formId;
    //alert("fid"+str);
    var custmQuantity = $(varform).find( "[name='"+"custmQuantity"+"']");
    var custmQuantity = $(custmQuantity).val();
    //alert("qty"+custmQuantity);
    var createRequirementForCustRequestItem = $(str).attr("action", "createRequirementForCustRequestItem");
    createRequirementForCustRequestItem.append("<input type='hidden' name='custmQuantity' value='"+custmQuantity+"'/>");
    createRequirementForCustRequestItem.submit();
}
function approveRequestByHOD(element){
	//alert("In Approve");
	var statusId="CRQ_SUBMITTED";
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
    var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var str = "#"+formId;
    var tempQty=$(varform).find("[name='"+"tempQty"+"']");
    var tempQty=$(tempQty).val();
    var approveRequestByHOD = $(str).attr("action", "approveRequestByHOD");
    approveRequestByHOD.append("<input type='hidden' name='quantity' value='"+tempQty+"'/>");
    approveRequestByHOD.append("<input type='hidden' name='statusId' value='"+statusId+"'/>");
    approveRequestByHOD.submit();
    
}
function rejectMaterialRequest(element){
	var statusId ="CRQ_REJECTED";
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
	var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var str = "#"+formId;
    var description = $(varform).find("[name='"+"description"+"']");
    var description = $(description).val();
    if(description.length==0){
    	alert("Please Write The Reason For Rejection");
    }else{
	    var rejectMaterialRequest = $(str).attr("action", "rejectMaterialRequest");
	    rejectMaterialRequest.append("<input type='hidden' name='statusId' value='"+statusId+"'/>");
	    rejectMaterialRequest.submit();
    }
}
function issueProductForRequest(element){
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
	var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var str = "#"+formId;
    var issueProductForRequest = $(str).attr("action", "issueProductForRequest");
    issueProductForRequest.submit();
}