<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-top="0.1in" >
                <fo:region-body margin-top="0.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
            ${setRequestAttribute("OUTPUT_FILENAME", "MonthlyBankStatement.txt")}
        </fo:layout-master-set>
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      <fo:block font-size="4pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>         
     <#if bankWiseEmplDetailsMap?has_content> 
     	<#assign bankDetailsList=bankWiseEmplDetailsMap.entrySet()>
 		<#if bankDetailsList?has_content>  
			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "company"}, true)>
			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "company", "userLogin", userLogin))/>
			<#list bankDetailsList as companyBankDetails> 
				<#assign temp=0>
				<#assign sno=0>
				<#assign count=0>
				<#assign totAmt=0>
				<#assign recordCnt=1>
				<#assign organizationDetails = delegator.findOne("PartyGroup", {"partyId" : parameters.bankAdvise_deptId}, true)>
	           	<#assign oragnizationId = organizationDetails.get("comments")>
				<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
				  		<#assign finAccDetails = delegator.findOne("FinAccount", {"finAccountId" : companyBankDetails.getKey()}, true)>
				  		<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
				  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${partyGroup.groupName?if_exists}, <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if> </fo:block>
				        <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">UNIT CODE :${oragnizationId?if_exists}&#160;&#160;&#160;${finAccDetails.finAccountName?if_exists}    BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MM/yyyy")).toUpperCase()}</fo:block>        
				  		<fo:block font-family="Courier,monospace" font-size="9pt">
							<fo:table>
				  				<fo:table-column column-width="12pt"/>
		                       	<fo:table-column column-width="20pt"/>
		                       	<fo:table-column column-width="70pt"/>
		                       	<fo:table-column column-width="65pt"/>
		                       	<fo:table-column column-width="40pt"/>
		                       	<fo:table-column column-width="40pt"/>
		                       	<fo:table-body>
		                       		<fo:table-row>
							   			<fo:table-cell>
							   				<fo:block font-size="8pt">---------------------------------------------------------------------------------------------</fo:block>
							   			</fo:table-cell>
							   		</fo:table-row>
							       	<fo:table-row>
					                	<fo:table-cell>
					                    	<fo:block font-size="5pt" text-align="left">SL</fo:block>
					                    </fo:table-cell>
					                    <fo:table-cell>
					                    	<fo:block font-size="5pt" text-align="left">EMP No</fo:block>
					                    </fo:table-cell>
					                     <fo:table-cell>
					                        <fo:block font-size="5pt" text-align="left">${(uiLabelMap.EmployeeName).toUpperCase()}</fo:block>
					                     </fo:table-cell> 
					                     <fo:table-cell>
					                        <fo:block font-size="5pt" text-align="left">DESIGNATION</fo:block>
					                     </fo:table-cell>
					                    <fo:table-cell>
					                        <fo:block font-size="5pt" text-align="center">A/C.NO</fo:block>
					                    </fo:table-cell>
					                    <fo:table-cell>
					                        <fo:block font-size="5pt" text-align="right">${(uiLabelMap.Amount).toUpperCase()}</fo:block>
					                    </fo:table-cell>
					                </fo:table-row>
					                <fo:table-row>
							   			<fo:table-cell>
							   				<fo:block font-size="8pt">----------------------------------------------------------------------------------------------</fo:block>
							   			</fo:table-cell>
							   		</fo:table-row>
							   	</fo:table-body>
							</fo:table> 
						</fo:block>
				  	</fo:static-content> 
				  	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
						<fo:block font-family="Courier,monospace" font-size="9pt">
							<fo:table>
								<fo:table-column column-width="12pt"/>
		                       	<fo:table-column column-width="20pt"/>
		                       	<fo:table-column column-width="70pt"/>
		                       	<fo:table-column column-width="65pt"/>
		                       	<fo:table-column column-width="40pt"/>
		                       	<fo:table-column column-width="40pt"/>
		                       	<fo:table-body>
							   		<#assign bankAdviceDetailsList=companyBankDetails.getValue().entrySet()>
						         	<#list bankAdviceDetailsList as employeeDetails>
						         		<#assign partyId = employeeDetails.getValue().get("emplNo")>
						         		<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyId})/>
					            		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
				                     	<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
				                     	<fo:table-row>
						         			<#assign sno=(sno+1)>
						         			<#assign count=(count+1)>
						                   	<fo:table-cell>
						                   		<fo:block font-size="5pt" text-align="left">${sno?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell>
						                   		<fo:block font-size="5pt" text-align="left">${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, employeeDetails.getValue().get("emplNo"))}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell>
						                   		<fo:block font-size="5pt" text-align="left">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(employeeDetails.getValue().get("empName"))),20)).toUpperCase()}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell>
						                   		<fo:block font-size="5pt" text-align="left"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell>
						                   		<fo:block font-size="5pt" text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(employeeDetails.getValue().get("acNo"))),17)?if_exists}</fo:block>
						                   	</fo:table-cell>
						                   	<fo:table-cell>
						                   		<#assign totAmt=totAmt+employeeDetails.getValue().get("netAmt")?if_exists>
						                   		<fo:block font-size="5pt" text-align="right">${employeeDetails.getValue().get("netAmt")?if_exists?string("##0.00")}</fo:block>
						                   	</fo:table-cell>
						              	</fo:table-row>
						              	<fo:table-row>
						                   	<fo:table-cell>
						                   		<fo:block font-size="4pt">&#160;</fo:block>
						                   	</fo:table-cell>
						              	</fo:table-row>
						              	<#assign listSize = (bankAdviceDetailsList.size())>
						              	<#assign remainingList = listSize - count>
						              	<#if remainingList != 1>
						              		<#if recordCnt==29>
						               			 <#assign recordCnt=0>
						               			 <fo:table-row>
						               			 	<fo:table-cell>
						               			 		<fo:block page-break-after="always"></fo:block>        
						               			 	</fo:table-cell>
						               			 </fo:table-row>
						               		</#if>
						               	</#if>
						              	<#assign recordCnt=recordCnt+1>
						         	</#list>
						         	<fo:table-row border="solid">
					          			<fo:table-cell/>
					          			<fo:table-cell/>
					          			<fo:table-cell>              		
					          			</fo:table-cell>
					          			<fo:table-cell>
					          				<fo:block text-align="left" font-size="5pt">TOTAL</fo:block>
					          			</fo:table-cell>
					          			<fo:table-cell />
					          			<fo:table-cell><fo:block  font-size="5pt" text-align="right">${totAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
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
			 		<fo:block font-size="4pt">
		    			No Data Found.......!
			 		</fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>	
	<#else>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="4pt">
        			${uiLabelMap.NoOrdersFound}.
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</#if>
</#if> 
</fo:root>
</#escape>
					
					
					
					
					
					
					
					
					
					
