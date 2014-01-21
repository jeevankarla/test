window.onload = changeColor;
function changeColor() {
document.getElementById('percentageUsed').style.background="lightgray";
}//end of changeColor function

function enableRate() {
	if((document.AddPartyRate.rateAmount.disabled)==true) {
	    document.AddPartyRate.rateAmount.disabled=false;
	    document.AddPartyRate.percentageUsed.disabled=true;
	    document.getElementById('percentageUsed').style.background="lightgray"; 
	    document.getElementById('rateAmount').style.background="white";

	} else {
	  document.AddPartyRate.rateAmount.disabled=true;
	  document.AddPartyRate.percentageUsed.disabled=false;
	  document.getElementById('rateAmount').style.background="lightgray";
	  document.getElementById('percentageUsed').style.background="white";
	
   }//end of else

}//end of enableRate function

