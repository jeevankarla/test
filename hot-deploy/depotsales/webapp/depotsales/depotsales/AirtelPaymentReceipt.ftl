<div style="text-align:center;vertical-align:middle;">
	<#if parameters.STATUS == "SUC">
		<img src="<@ofbizContentUrl>/vasista/complogos/success.png</@ofbizContentUrl>" style="border-width:0px;" />
		<p><h2>Payment Successful!</h2></p>
		<p><h3>Transaction Id: ${parameters.TRAN_ID}</h3></p>
		<p><h3>Transaction Amount: ${parameters.TRAN_AMT}</h3></p>
		<p><h3>Please Wait(Redirecting to dashboard)...</h3></p>
		
		
	</#if>
	<#if parameters.STATUS == "FAL">
		<img src="<@ofbizContentUrl>/vasista/complogos/fail.png</@ofbizContentUrl>" style="border-width:0px;" />
		<p><h2>Payment Failed!</h2></p>
		<p><h2>Transaction Amount: ${parameters.TRAN_AMOUNT}</h2></p>
	</#if>
	
</div>
