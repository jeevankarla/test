<#escape x as x?xml>
  <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
  <#if payRollSummaryMap?has_content>
     <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     		<fo:static-content flow-name="xsl-region-before">
	     		<#if deptId?exists>
	     			<#assign department=deptId/>
	     		<#else>
	     			<#assign department= parameters.partyId/>
	     		</#if> 
     			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" :department}, true)>
     			<#assign oragnizationId = partyGroup.get("comments")>
     			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", department, "userLogin", userLogin))/>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "10pt">ANDHRA PRADESH DAIRY DEVELOPMENT CO-OP. FEDERATION LIMITED</fo:block>
     			<fo:block text-align="left" white-space-collapse="false" font-weight="bold" font-size = "10pt">UNIT : ${oragnizationId?if_exists}           ${partyGroup.groupName?if_exists?upper_case}  <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}  </#if>           JOURNAL VOUCHER NO :             </fo:block>
        	 	<fo:block text-align="left" white-space-collapse="false" font-weight="bold" font-size = "10pt">MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MM/yyyy")).toUpperCase()}                                                                               BILL NO :</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>	 	 	  
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">SL.     UAS         NOMENCLATURE                                                                            DEBIT                           CREDIT</fo:block>
        		<#elseif (parameters.netPayglCode?exists && parameters.netPayglCode == "No")>
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">SL.                      NOMENCLATURE                                                                            DEBIT                           CREDIT</fo:block>
        		<#else>
        			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">SL.                 NOMENCLATURE                                                            DEBIT                                       CREDIT</fo:block>	
        		</#if>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>       
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
           		<fo:block>
           			<#assign summaryReportList =payRollSummaryMap.entrySet()>
           			<#assign totalEarnings =0 />
           			<#assign sNo =1>
        			<#assign totalDeductions =0 />
           			<fo:table>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="60pt"/>
       					 <fo:table-column column-width="250pt"/>
       					 <fo:table-column column-width="150pt"/>
       					 <fo:table-column column-width="130pt"/>
       					 <fo:table-body>
       					 	<#list benefitTypeIds as benefitType>
	                    		<#assign value=0>
	                    		<#if payRollSummaryMap.get(benefitType)?has_content>
	                    			<#assign value=payRollSummaryMap.get(benefitType)>	
	                    		</#if>
	                    		<#if value !=0>
		                    		<fo:table-row>   
		                    			<fo:table-cell > 
			                    			<fo:block linefeed-treatment="preserve">${sNo}</fo:block>
			                    			<#assign sNo = sNo+1>
			                    		</fo:table-cell >                   
			                    		<fo:table-cell >  
				                    		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>                  		
				                      			<#assign extrnalGlCodeGroup = delegator.findOne("BenefitType", {"benefitTypeId" :benefitType}, true)> 
				                      			<#if extrnalGlCodeGroup.externalGlCode?exists >               			
				                    				<fo:block text-align="left" keep-together="always">${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>
				                    			<#else>
				                    				<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :benefitType}, true)>
				                    				<fo:block text-align="left" keep-together="always">${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
				                    			</#if> 
				                    		<#else>	 
				                    			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				                    		</#if>	  
			                    		</fo:table-cell>
			                    		<fo:table-cell>
				                    		 <#assign totalEarnings=(totalEarnings+(value))> 
				                    		 <fo:block keep-together="always" text-align="left">${benefitDescMap[benefitType]?if_exists}</fo:block>
				                    	</fo:table-cell>               	              		
			                    		<fo:table-cell>
			                    			<fo:block text-align="right">${value?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
		                    	</#if>
	                    	</#list>
	                    	<#list dedTypeIds as deductionType>
	                    		<#assign dedValue=0>
	                    		<#if payRollSummaryMap.get(deductionType)?has_content>
	                    			<#assign dedValue=payRollSummaryMap.get(deductionType)>	
	                    		</#if>
	                    		<#if dedValue !=0>
		                    		<fo:table-row>   
		                    			<fo:table-cell > 
			                    			<fo:block linefeed-treatment="preserve">${sNo}</fo:block>
			                    			<#assign sNo = sNo+1>
			                    		</fo:table-cell >                    
			                    		<fo:table-cell> 
				                    		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")>  
				                    			<#assign extrnalGlCodeGroup = delegator.findOne("DeductionType", {"deductionTypeId" :deductionType}, true)>                 		
				                      			<#if extrnalGlCodeGroup.externalGlCode?exists >  							                      			               			
				                    				<fo:block keep-together="always">${extrnalGlCodeGroup.externalGlCode?if_exists}</fo:block>  
				                    			<#else>        
					                    			<#assign extrnalGlCodeGroup = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" :deductionType}, true)>
					                    			<fo:block keep-together="always">${extrnalGlCodeGroup.defaultGlAccountId?if_exists}</fo:block>
				                    			</#if> 
				                    		<#else>
				                    			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				                    		</#if>	     			
			                    		</fo:table-cell>  
			                    		<fo:table-cell>
			                    		 	<#assign totalDeductions=(totalDeductions+(dedValue))>
			                    			<fo:block keep-together="always">${dedDescMap[deductionType]?if_exists}</fo:block>
			                    		</fo:table-cell>       
			                    		<fo:table-cell > 
			                    			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                    		</fo:table-cell >         	              		
			                    		<fo:table-cell>
			                    			<fo:block text-align="right">${((-1)*(dedValue))?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
		                    	</#if>
	                    	</#list>
	                    	<fo:table-row>   
                    			<fo:table-cell > 
	                    			<fo:block>${sNo}</fo:block>
	                    		</fo:table-cell > 
	                    		<#if (parameters.netPayglCode?exists && parameters.netPayglCode == "Yes")> 
		                    		<fo:table-cell > 
		                    			<fo:block>09101</fo:block>
		                    		</fo:table-cell > 
		                    	<#else>
		                    		<fo:table-cell > 
		                    			<fo:block></fo:block>
		                    		</fo:table-cell > 
		                    	</#if>
	                    		<fo:table-cell > 
	                    			<fo:block>SALARY PAYABLE(NET)</fo:block>
	                    		</fo:table-cell >
	                    		<#assign netSalary = (totalEarnings-(totalDeductions*(-1)))> 
	                    		<fo:table-cell > 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right">${netSalary?if_exists?string("##0.00")}</fo:block>
	                    		</fo:table-cell >   
	                    	</fo:table-row>
	                    	<fo:table-row>   
                    			<fo:table-cell > 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell > 
	                    		<fo:table-cell > 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell > 
	                    		<fo:table-cell > 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell > 
	                    		<fo:table-cell > 
	                    			<fo:block> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;---------------------------</fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block> &#160;&#160;&#160;&#160;&#160;&#160;---------------------------</fo:block>
	                    		</fo:table-cell >   
	                    	</fo:table-row> 
	                    	<fo:table-row>   
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right"></fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right"></fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right">TOTAL</fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right">${totalEarnings?if_exists?string("##0.00")}</fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block text-align="right">${totalEarnings?if_exists?string("##0.00")}</fo:block>
	                    		</fo:table-cell >
	                    	</fo:table-row>               
	                    	<fo:table-row>   
                    			<fo:table-cell> 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell> 
	                    		<fo:table-cell> 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell > 
	                    		<fo:table-cell > 
	                    			<fo:block></fo:block>
	                    		</fo:table-cell > 
	                    		<fo:table-cell > 
	                    			<fo:block> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;---------------------------</fo:block>
	                    		</fo:table-cell >
	                    		<fo:table-cell > 
	                    			<fo:block> &#160;&#160;&#160;&#160;&#160;&#160;---------------------------</fo:block>
	                    		</fo:table-cell >   
	                    	</fo:table-row>
       					 </fo:table-body>
           			</fo:table>
           		</fo:block>
           	</fo:flow>          
        </fo:page-sequence>
        <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Earnigs and Deductions Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>		
    </#if>  
  </fo:root>
</#escape>
        	