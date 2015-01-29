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
function changeQuoteItemStatus(element){
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
	var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var name= form.attr('name');
    var str = "#"+formId;
    var description = $(varform).find("[name='"+"comments"+"']");
    var description = $(description).val();
    
    if(description.length==0){
    	alert("Please Write The Comments For Disqualify.");
    }else{
    	$('#comments').val(description);
    	var changeQuoteItemStatus = $(str).attr("action", "changeQuoteItemStatus");
        changeQuoteItemStatus.submit();
    }
}

function quoteNegotiateAndStatusChange(element){
	var curreElem = $(element);
	var varform = curreElem.parent().parent();
    var form = curreElem.parent().parent().find("form");
    var formId = form.attr('id');
    var str = "#"+formId;
    var quoteUnitPrice=$(varform).find("[name='"+"quoteUnitPrice"+"']");
    var quoteUnitPrice=$(quoteUnitPrice).val();
    $('#quoteUnitPrice').val(quoteUnitPrice);
    $('#statusId').val("QTITM_NEGOTIATION");
    var quoteNegotiateAndStatusChange = $(str).attr("action", "quoteNegotiateAndStatusChange");
    //quoteNegotiateAndStatusChange.append("<input type='hidden' name='quoteUnitPrice' value='"+quoteUnitPrice+"'/>");
    quoteNegotiateAndStatusChange.submit();
	
}