<div style="text-align:center">
	<#if parameters.STATUS == "SUC">
		<img src="<@ofbizContentUrl>/vasista/complogos/success.png</@ofbizContentUrl>" style="border-width:0px;" />
		<p><h2>Payment Successful!</h2></p>
		<p><h2>Transaction Id: ${parameters.TRAN_ID}</h2></p>
		<p><h2>Transaction Amount: ${parameters.TRAN_AMT}</h2></p>
	</#if>
	<#if parameters.STATUS == "FAL">
		<img src="<@ofbizContentUrl>/vasista/complogos/fail.png</@ofbizContentUrl>" style="border-width:0px;" />
		<p><h2>Payment Failed!</h2></p>
		<p><h2>Transaction Amount: ${parameters.TRAN_AMOUNT}</h2></p>
	</#if>
	
</div>
