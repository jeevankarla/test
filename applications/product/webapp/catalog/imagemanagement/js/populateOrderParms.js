function appendParamsToProcessChangeOrder() {
	
	formName=document.forms['ChangeSubscriptionProductForm'];	
	var hiddenBoothId = document.createElement("input");
    hiddenBoothId.setAttribute("type", "hidden");
    hiddenBoothId.setAttribute("name", "boothId");
    hiddenBoothId.setAttribute("value", jQuery('input[name=originFacilityId]').val());
    formName.appendChild(hiddenBoothId);
    var hiddenEffectiveDate = document.createElement("input");
    hiddenEffectiveDate.setAttribute("type", "hidden");
    hiddenEffectiveDate.setAttribute("name", "effectiveDate");
    hiddenEffectiveDate.setAttribute("value", jQuery('input[name=effectiveDate]').val());
    formName.appendChild(hiddenEffectiveDate);
    formName.submit();	
}

function appendParamsToProcessChangeIndentMIS() {
	
	formName=document.forms['ChangeSubscriptionIndentMIS'];	
	var hiddenBoothId = document.createElement("input");
    hiddenBoothId.setAttribute("type", "hidden");
    hiddenBoothId.setAttribute("name", "boothId");
    hiddenBoothId.setAttribute("value", jQuery('input[name=originFacilityId]').val());
    formName.appendChild(hiddenBoothId);
    var hiddenEffectiveDate = document.createElement("input");
    hiddenEffectiveDate.setAttribute("type", "hidden");
    hiddenEffectiveDate.setAttribute("name", "effectiveDate");
    hiddenEffectiveDate.setAttribute("value", jQuery('input[name=effectiveDate]').val());
    formName.appendChild(hiddenEffectiveDate);
    formName.submit();	
}

function appendParamsToCreateGatePassIndent() {
	
	formName=document.forms['GatePassForm'];	
	var hiddenBoothId = document.createElement("input");
    hiddenBoothId.setAttribute("type", "hidden");
    hiddenBoothId.setAttribute("name", "boothId");
    hiddenBoothId.setAttribute("value", jQuery('input[name=originFacilityId]').val());
    formName.appendChild(hiddenBoothId);
    var hiddenEffectiveDate = document.createElement("input");
    hiddenEffectiveDate.setAttribute("type", "hidden");
    hiddenEffectiveDate.setAttribute("name", "effectiveDate");
    hiddenEffectiveDate.setAttribute("value", jQuery('input[name=effectiveDate]').val());
    formName.appendChild(hiddenEffectiveDate);
    formName.submit();	
}

function appendParamsToCancelCardSale() {
	
	formName=document.forms['ViewMilkCardOrderItems'];	
	var hiddenOrderId = document.createElement("input");
	hiddenOrderId.setAttribute("type", "hidden");
	hiddenOrderId.setAttribute("name", "orderId");
	hiddenOrderId.setAttribute("value", jQuery('input[name=orderId]').val());
    formName.appendChild(hiddenOrderId);
    formName.submit();
}

function appendParamsToEditCardSale() {
	
	formName=document.forms['ViewMilkCardOrderItems'];	
	var hiddenOrderId = document.createElement("input");
	hiddenOrderId.setAttribute("type", "hidden");
	hiddenOrderId.setAttribute("name", "orderId");
	hiddenOrderId.setAttribute("value", jQuery('input[name=orderId]').val());
    formName.appendChild(hiddenOrderId);
    formName.submit();
}

function appendParamsToNewMilkCardList() {

	formName=document.forms['NewMilkCardList'];
	var boothId = document.createElement("input");
	boothId.setAttribute("type", "hidden");
	boothId.setAttribute("name", "milkCardTypeId");
	boothId.setAttribute("value",jQuery('select[name=milkCardTypeId] option:selected').val());
    formName.appendChild(boothId);    
	var timePeriod = document.createElement("input");
	timePeriod.setAttribute("type", "hidden");
	timePeriod.setAttribute("name", "customTimePeriodId");
	timePeriod.setAttribute("value",jQuery('select[name=customTimePeriodId] option:selected').val());	
    formName.appendChild(timePeriod);
    var paymentTypeId = document.createElement("input");
    paymentTypeId.setAttribute("type", "hidden");
    paymentTypeId.setAttribute("name", "paymentTypeId");
    paymentTypeId.setAttribute("value",jQuery('select[name=paymentTypeId] option:selected').val());	
    formName.appendChild(paymentTypeId);  
    var bookNumber = document.createElement("input");
    bookNumber.setAttribute("type", "hidden");
    bookNumber.setAttribute("name", "bookNumber");
    bookNumber.setAttribute("value",jQuery('input[name=bookNumber]').val());
    formName.appendChild(bookNumber);
    var counterNumber = document.createElement("input");
    counterNumber.setAttribute("type", "hidden");
    counterNumber.setAttribute("name", "counterNumber");
    counterNumber.setAttribute("value",jQuery('input[name=counterNumber]').val());
    formName.appendChild(counterNumber);
    var orderDate = document.createElement("input");
    orderDate.setAttribute("type", "hidden");
    orderDate.setAttribute("name", "orderDate");
    orderDate.setAttribute("value",jQuery('input[name=orderDate]').val());
   	formName.appendChild(orderDate);
    formName.submit();
}

function appendParamsToCreateSpecialOrder() {
	
	formName=document.forms['SpecialOrderSubscriptionProductForm'];	
	var hiddenBoothId = document.createElement("input");
    hiddenBoothId.setAttribute("type", "hidden");
    hiddenBoothId.setAttribute("name", "boothId");
    hiddenBoothId.setAttribute("value", jQuery('input[name=originFacilityId]').val());
    formName.appendChild(hiddenBoothId);
    var hiddenEffectiveDate = document.createElement("input");
    hiddenEffectiveDate.setAttribute("type", "hidden");
    hiddenEffectiveDate.setAttribute("name", "fromDate");
    hiddenEffectiveDate.setAttribute("value", jQuery('input[name=fromDate]').val());
    formName.appendChild(hiddenEffectiveDate);
    var hiddenEffectiveDate = document.createElement("input");
    hiddenEffectiveDate.setAttribute("type", "hidden");
    hiddenEffectiveDate.setAttribute("name", "thruDate");
    hiddenEffectiveDate.setAttribute("value", jQuery('input[name=thruDate]').val());
    formName.appendChild(hiddenEffectiveDate);
    formName.submit();	
}
function appendParamsToProcessBulkCardSale(){
	
	formName=document.forms['CreateCardSaleForm'];
	var hiddenBoothId=document.createElement("input");
    hiddenBoothId.setAttribute("type","hidden");
	hiddenBoothId.setAttribute("name", "boothId");
	hiddenBoothId.setAttribute("value", jQuery('input[name=originFacilityId]').val());	
	formName.appendChild(hiddenBoothId);
	var hiddencustomTimePeriodId=document.createElement("input");
	hiddencustomTimePeriodId.setAttribute("type","hidden");
	hiddencustomTimePeriodId.setAttribute("name","customTimePeriodId");
	var e = document.getElementById("customTimePeriodId");
	var strUser = e.options[e.selectedIndex].value;
	hiddencustomTimePeriodId.setAttribute("value",strUser);
	formName.appendChild(hiddencustomTimePeriodId);	
	var hiddencustomerName=document.createElement("input");
	hiddencustomerName.setAttribute("type","hidden");
	hiddencustomerName.setAttribute("name","customerName");
	hiddencustomerName.setAttribute("value",jQuery('input[name=customerName]').val());
	formName.appendChild(hiddencustomerName);
	var hiddencustomerContactNumber=document.createElement("input");
	hiddencustomerContactNumber.setAttribute("type","hidden");
	hiddencustomerContactNumber.setAttribute("name","customerContactNumber");
	hiddencustomerContactNumber.setAttribute("value",jQuery('input[name=customerContactNumber]').val());
	formName.appendChild(hiddencustomerContactNumber);
	formName.submit();
	   
}
