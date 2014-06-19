<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<script>
jQuery(document).ready(function() {
		paymentMethodType();
		getBankNames();
	});

    function getBankNames(){
 		var str=jQuery("#paymentMethodTypeId").val();
		if(str == 'CHALLAN_PAYIN'){
		    jQuery('#finAccountId').parent().parent().parent().show();
			htmlMsg = "";
			htmlMsg += "<#if finAccount?has_content><#list finAccount as eachFinAccount><option value='${eachFinAccount.finAccountId?if_exists}'  <#if finAccountName?if_exists ==eachFinAccount.finAccountName> selected='selected'</#if>>${eachFinAccount.finAccountName?if_exists}</option></#list></#if>";        
			//alert("####"+htmlMsg);
			jQuery("#finAccountId").html('');
			jQuery("#finAccountId").html(htmlMsg);
		}
		else{
		    jQuery('#finAccountId').parent().parent().parent().hide();
		}
			
	}
	 function paymentMethodType(){
	 var str=jQuery("#paymentMethodTypeId").val();
			msg = "";
			msg += "<option value='CASH_PAYIN' <#if paymentMethodType == 'CASH_PAYIN'> selected='selected'</#if>>CSH</option>"+ 
					"<option value='CHALLAN_PAYIN' <#if paymentMethodType == 'CHALLAN_PAYIN'> selected='selected'</#if>>CHLN</option>"+ 
					"<option value='CHEQUE_PAYIN' <#if paymentMethodType == 'CHEQUE_PAYIN'> selected='selected'</#if>>CHQ</option>"+            
					       
			//alert("####"+msg);
			jQuery("#paymentMethodTypeId").html('');
			jQuery("#paymentMethodTypeId").html(msg);
	 
	 }
</script>

	