<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.3in" margin-bottom="0.3in" margin-left="1in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    
    <#assign totalAmount=0>
 	<#if bankWiseEmplDetailsMap?has_content>   
 		<#assign bankDetailsList=bankWiseEmplDetailsMap.entrySet()>
 		<#if bankDetailsList?has_content>  
			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "company"}, true)>
			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "company", "userLogin", userLogin))/>
			<#list bankDetailsList as companyBankDetails>
				<#assign temp=0>
				<#assign sno=0>
				<#assign totAmt=0>
				<#assign recordCnt=0>
  				<fo:page-sequence master-reference="main">
				  	<fo:static-content flow-name="xsl-region-before">
				  		<#assign finAccDetails = delegator.findOne("FinAccount", {"finAccountId" : companyBankDetails.getKey()}, true)>
				  		<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
				  		<fo:block white-space-collapse="false" font-weight="bold" text-align="left" text-indent="60pt" keep-together="always">${partyGroup.groupName?if_exists}, <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if> </fo:block>
				        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    ${finAccDetails.finAccountName?if_exists}                                                     																					${uiLabelMap.CommonPage}No: <fo:page-number/></fo:block>
				        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                   Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd-MMM-yyyy")}</fo:block>
				  	</fo:static-content> 
				  	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
					  	<fo:block>
							<fo:table width="100%" table-layout="fixed">
							   <fo:table-header height="14px">
							       	<fo:table-row height="14px" space-start=".15in" text-align="center">
					                	<fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
					                    	<fo:block text-align="center" font-weight="bold">Sl.No</fo:block>
					                    </fo:table-cell>
					                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
					                    	<fo:block text-align="center" font-weight="bold">EMP No</fo:block>
					                    </fo:table-cell>
					                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="170px">
					                        <fo:block text-align="center" font-weight="bold" >${uiLabelMap.EmployeeName}</fo:block>
					                     </fo:table-cell> 
					                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
					                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.AccountNumber}</fo:block>
					                    </fo:table-cell>
					                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="80px">
					                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.Amount}</fo:block>
					                    </fo:table-cell>
					                </fo:table-row>
					            </fo:table-header>
					        	<fo:table-body font-size="10pt">
						         	<#assign bankAdviceDetailsList=companyBankDetails.getValue().entrySet()>
						         	<#list bankAdviceDetailsList as employeeDetails>
						         		<fo:table-row height="14px" space-start=".15in">
						         			<#assign sno=(sno+1)>
						                   	<fo:table-cell border-style="solid">
						                   		<fo:block text-align="center" >${sno?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell border-style="solid">
						                   		<fo:block text-align="center" >${employeeDetails.getValue().get("emplNo")?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell border-style="solid">
						                   		<fo:block text-align="left" >&#160;${employeeDetails.getValue().get("empName")?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell border-style="solid">
						                   		<fo:block text-align="left" >&#160; ${employeeDetails.getValue().get("acNo")?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell border-style="solid">
						                   		<#assign totAmt=totAmt+employeeDetails.getValue().get("netAmt")?if_exists>
						                   		<fo:block text-align="right" >&#160; ${employeeDetails.getValue().get("netAmt")?if_exists} &#160;</fo:block>
						                   	</fo:table-cell>
						              	</fo:table-row>
						              	<#assign recordCnt=recordCnt+1>
						              	<#if recordCnt==45>
					               			 <#assign recordCnt=0>
					               			 <fo:table-row>
					               			 	<fo:table-cell>
					               			 		<fo:block page-break-after="always"></fo:block>        
					               			 	</fo:table-cell>
					               			 </fo:table-row>
					               		</#if> 
						         	</#list>
						         	<fo:table-row border="solid">
					          			<fo:table-cell/>
					          			<fo:table-cell>              		
					          			</fo:table-cell>
					          			<fo:table-cell>
					          				<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
					          			</fo:table-cell>
					          			<fo:table-cell />
					          			<fo:table-cell border="solid"><fo:block text-align="right" font-weight="bold">${totAmt?if_exists?string("#0.00")} &#160;</fo:block></fo:table-cell>
					          		</fo:table-row>
							     </fo:table-body>
							</fo:table> 
						</fo:block>
					    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>    
					    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					    <fo:block>Authorized Signatory</fo:block>
					</fo:flow>
				</fo:page-sequence>
			</#list>
	 	<#else>
		 	<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			 		<fo:block font-size="14pt">
		    			No Employee Found.......!
			 		</fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>
	 <#else>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		 		<fo:block font-size="14pt">
	    			No Employee Found.......!
		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	 </#if>
</fo:root>
</#escape>
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	
						         	