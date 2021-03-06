var prncplAmt;
var prinplInst;
var intrstInst;
var intrstAmt;
var rateOfInt;

$(document).ready(function(){
	
	$("#principalAmount").blur(function(){
		var prinAmount = $("#principalAmount").val();
		if(prinAmount && !isNaN(prinAmount)){
			if(prinAmount>prncplAmt){
				if(loanType != "external"){
					$("#principalAmount").val(prncplAmt);
					alert("You cannot give more than the predefined amount");
				}
			}
		}
	});
	$("#numPrincipalInst").blur(function(){
		var numPrinInst = $("#numPrincipalInst").val();
		if(numPrinInst && !isNaN(numPrinInst)){
			if(numPrinInst>prinplInst){
				if(loanType != "external"){
					$("#numPrincipalInst").val(prinplInst);
					alert("You cannot give more than the predefined amount");
				}
			}
		}
	});
	$("#numInterestInst").blur(function(){
		var numIntrstInst = $("#numInterestInst").val();
		if(numIntrstInst && !isNaN(numIntrstInst)){
			if(numIntrstInst>intrstInst){
				if(loanType != "external"){
					$("#numInterestInst").val(intrstInst);
					alert("You cannot give more than the predefined amount");
				}
			}
		}
	});
	$("#interestAmount").blur(function(){
		
		var intrestAmount = $("#interestAmount").val();
		if(intrestAmount && !isNaN(intrestAmount)){
			if(intrestAmount>intrstAmt){
				if(loanType != "external"){
					$("#interestAmount").val(intrstAmt);
					alert("You cannot give more than the predefined amount");
				}
			}
		}
	});
	$("#rateOfInterest").blur(function(){
		var rateOfIntrst = $("#rateOfInterest").val();
		if(rateOfIntrst && !isNaN(rateOfIntrst)){
			if((rateOfIntrst>rateOfInt) || (rateOfIntrst<rateOfInt)){
				if(loanType != "external"){
					$("#rateOfInterest").val(rateOfInt);
					alert("You cannot modify rate of interest");
				}
			}
		}
	});
});

var loanType = '';
function loanTypesAmountChange(){
	var loanTypeId = $("#loanTypeId").val();
	var partyId = $("[name='partyId']").val();
	
	var selected = $("input[type='radio'][name='loanType']:checked");
	if (selected.length > 0) {
		loanType = selected.val();
	}
	
	$.ajax({
         type: "POST",
         url: 'getLoanAmountsByLoanType',
         data: {loanTypeId : loanTypeId, partyId: partyId},
         dataType: 'json',
         success: function(result) {
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
        	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
           }else{               			
        	   	principalAmount = result["principalAmount"];
        	   	numPrincipalInst = result["numPrincipalInst"];
        	   	numInterestInst = result["numInterestInst"];
        	   	interestAmount = result["interestAmount"];
        	   	rateOfInterest = result["rateOfInterest"];
        	   	retirementDate = result["retirementDate"];
        	   	noOfMonthsToRetire = result["noOfMonthsToRetire"];
        	   	
        	   	prncplAmt = principalAmount;
        	   	prinplInst = numPrincipalInst;
        	   	intrstInst = numInterestInst;
        	   	intrstAmt = interestAmount;
        	   	rateOfInt = rateOfInterest;
        	   	
        	   	$("#principalAmount").val(principalAmount);
        	   	$("#numPrincipalInst").val(numPrincipalInst);
        	   	$("#numInterestInst").val(numInterestInst);
        	   	$("#interestAmount").val(interestAmount);
        	   	$("#rateOfInterest").val(rateOfInterest);
        	   	$("#retirementDate").val(retirementDate);
        	   	$("#noOfMonthsToRetire").val(noOfMonthsToRetire);
           }
         } 
    });
}


		


