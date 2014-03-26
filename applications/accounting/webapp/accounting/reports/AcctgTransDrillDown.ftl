

     <#assign alt_row = false>
      <#list acctgTransEntryList as acctgTransEntry>
	      <tr id="${acctgTransEntry.acctgTransId}-${acctgTransEntry.acctgTransId.acctgTransEntrySeqId}" class="child-of-${acctgTransEntry.accountCode} child-type-txns">
	        <td/>
	        <td/>
	        <td>${acctgTransEntry.transactionDate}</td>
	        <td/>
	        <td/>
	        <td/>
	        <td/>
	        <td><#if acctgTransEntry.debitCreditFlag?if_exists == 'D'><@ofbizCurrency amount=acctgTransEntry.amount isoCode=defaultOrganizationPartyCurrencyUomId/></#if></td>
	        <td><#if acctgTransEntry.debitCreditFlag?if_exists == 'C'><@ofbizCurrency amount=acctgTransEntry.amount isoCode=defaultOrganizationPartyCurrencyUomId/></#if></td>
	        <td/>                
	      </tr>	      
          <#-- toggle the row color -->
          <#assign alt_row = !alt_row>	      
	  </#list> 