var prncplAmt;
var prinplInst;
var intrstInst;
var intrstAmt;

$(document).ready(function(){
		
	$("#principalAmount").blur(function(){
			var prinAmount = $("#principalAmount").val();
			if(prinAmount && !isNaN(prinAmount)){
				if(prinAmount>prncplAmt){
					$("#principalAmount").val(prncplAmt);
					alert("You cannot give more than the predefined amount");
				}
				
			}
	
	});
	$("#numPrincipalInst").blur(function(){
		var numPrinInst = $("#numPrincipalInst").val();
		if(numPrinInst && !isNaN(numPrinInst)){
			if(numPrinInst>prinplInst){
				$("#numPrincipalInst").val(prinplInst);
				alert("You cannot give more than the predefined amount");
			}
			
		}

	});
	$("#numInterestInst").blur(function(){
		var numIntrstInst = $("#numInterestInst").val();
		if(numIntrstInst && !isNaN(numIntrstInst)){
			if(numIntrstInst>intrstInst){
				$("#numInterestInst").val(intrstInst);
				alert("You cannot give more than the predefined amount");
			}
			
		}

	});
	$("#interestAmount").blur(function(){
		var intrestAmount = $("#interestAmount").val();
		if(intrestAmount && !isNaN(intrestAmount)){
			if(intrestAmount>intrstAmt){
				$("#interestAmount").val(intrstAmt);
				alert("You cannot give more than the predefined amount");
			}
			
		}

	});
		
});

function loanTypesAmountChange(){
	var loanTypeId = $("#loanTypeId").val();
	$.ajax({
         type: "POST",
         url: 'getLoanAmountsByLoanType',
         data: {loanTypeId : loanTypeId},
         dataType: 'json',
         success: function(result) {
           if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
        	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
           }else{               			
        	   	principalAmount = result["principalAmount"];
        	   	numPrincipalInst = result["numPrincipalInst"];
        	   	numInterestInst = result["numInterestInst"];
        	   	interestAmount = result["interestAmount"];
        	   	prncplAmt = principalAmount;
        	   	prinplInst = numPrincipalInst;
        	   	intrstInst = numInterestInst;
        	   	intrstAmt = interestAmount;
        	   	$("#principalAmount").val(principalAmount);
        	   	$("#numPrincipalInst").val(numPrincipalInst);
        	   	$("#numInterestInst").val(numInterestInst);
        	   	$("#interestAmount").val(interestAmount);
           }
         } 
    });
}


		


