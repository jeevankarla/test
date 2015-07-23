<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.3in" margin-bottom="0.3in" margin-left="0.7in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    
 	<#assign totalAmount=0>
 	<#if bankWiseEmplDetailsMap?has_content>   
 		<#assign bankDetailsList=bankWiseEmplDetailsMap.entrySet()>
 		<#if bankDetailsList?has_content>  
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
 			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
 			<#list bankDetailsList as companyBankDetails>
 				<#assign temp=0>
  				<fo:page-sequence master-reference="main">
					<fo:static-content flow-name="xsl-region-before">
						<#assign finAccDetails = delegator.findOne("FinAccount", {"finAccountId" : companyBankDetails.getKey()}, true)>
						<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
						<fo:block white-space-collapse="false" font-weight="bold" text-align="left" text-indent="60pt" keep-together="always">${partyGroup.groupName?if_exists}, <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if> </fo:block>
					    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt"> &#160;                                                                                                                                                 ${uiLabelMap.CommonPage}No: <fo:page-number/></fo:block>
					    <#if parameters.billingTypeId=="SP_LEAVE_ENCASH">   
					    	<#assign timePeriodEnd=basicSalDate?if_exists>
					    </#if>                                                  																					
					    <fo:block text-align="right" keep-together="always" white-space-collapse="false" font-weight="bold">LOANS DISBURSED : BANK ADVISE REPORT FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd-MMM-yyyy")}</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">&#160; </fo:block>
						<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">&#160; ${finAccDetails.finAccountName?if_exists} &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					</fo:static-content>  	
    				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
      					<#if CanaraBankMap.get(companyBankDetails.getKey())?has_content>
      
      					</#if>  	
      					<fo:block>
							<fo:table width="100%" table-layout="fixed">
					    		<fo:table-header height="13px">
							       	<fo:table-row height="14px" space-start=".15in" text-align="center">
					                	<fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
					                    	<fo:block text-align="center" font-weight="bold">Sl.No</fo:block>
					                    </fo:table-cell>
					                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
					                    	<fo:block text-align="center" font-weight="bold">Emp No</fo:block>
					                    </fo:table-cell>
					                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="170px">
					                        <fo:block text-align="left" font-weight="bold" >&#160;&#160;Employee Name</fo:block>
					                     </fo:table-cell> 
					                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="130px">
					                        <fo:block text-align="left" font-weight="bold" >&#160;&#160;${uiLabelMap.Designation}</fo:block>
					                     </fo:table-cell> 
					                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
					                        <fo:block font-weight="bold" text-align="left" >&#160;&#160;Account Number</fo:block>
					                    </fo:table-cell>
					                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="80px">
					                        <fo:block font-weight="bold" text-align="left" >&#160;&#160;${uiLabelMap.Amount}</fo:block>
					                    </fo:table-cell>
					                </fo:table-row>
					            </fo:table-header>
         						<fo:table-body font-size="10pt">
						         	<#assign totalNetAmt=0>
						         	<#assign recordCnt=0>
						         	<#assign emplPosition=0 />
						         	<#assign bankAdviceDetailsList=BankAdvicePayRollMap.entrySet()>
         	  						<#assign partyIdList=companyBankDetails.getValue()>
               						<#list partyIdList as partyId>               	
           	   							<#assign netAmt = 0>
					                    <#if employeeLoanDisbursedMap?has_content>
					                    	<#assign employeeLoanDisbursedList=employeeLoanDisbursedMap.entrySet()>
					                    	<#list employeeLoanDisbursedList as employee>  
					                    		<#if employee.getKey() == partyId>
					                    			<#assign netAmt = employee.getValue().get("totAmount")>
			                    					<#if netAmt != 0>
								                   		<fo:table-row height="14px" space-start=".15in">
									                   		<fo:table-cell  border="solid">
									                   			<#assign temp=(temp+1)>
									                        	<fo:block text-align="center">${temp?if_exists}</fo:block>
										                   	</fo:table-cell >
										                   	<fo:table-cell border="solid">
										                    	<fo:block text-align="center">${employee.getKey()?if_exists}</fo:block>
										                   	</fo:table-cell>
										                   	<#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : employee.getKey()})/>  	
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  ${employee.getValue().get("partyName")?if_exists}</fo:block>
										                   	</fo:table-cell>
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; ${employee.getValue().get("employeePosition")?if_exists} </fo:block>
										                   	</fo:table-cell>
										                    <fo:table-cell  border="solid">
										                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  ${employee.getValue().get("accNo")?if_exists}</fo:block>
										                    </fo:table-cell>
										                    <#assign totalNetAmt=totalNetAmt+netAmt?if_exists>
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="center">${netAmt?if_exists?string("#0.00")}</fo:block>
										                   	</fo:table-cell>
										                   	<#assign recordCnt=recordCnt+1>
									               		</fo:table-row>
									               	</#if>
									          	</#if>
					                    	</#list>
					                    </#if>
	               						<#if recordCnt==40>
				               			 	<#assign recordCnt=0>
				               			 	<fo:table-row>
				               			 		<fo:table-cell>
				               			 			<fo:block page-break-after="always"></fo:block>        
				               			 		</fo:table-cell>
				               			 	</fo:table-row>
	               						</#if> 
                  				</#list>
              					<fo:table-row border="solid">
              						<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              	<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              	<fo:table-cell>
					              		<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
					              	</fo:table-cell>
					              	<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              	<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              	<fo:table-cell border="solid"><fo:block text-align="center" font-weight="bold">${totalNetAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
              					</fo:table-row>
				              	<#if CanaraBankMap.get(companyBankDetails.getKey())?has_content>
				              		<fo:table-row>
				       			 		<fo:table-cell>
				       			 			<fo:block page-break-before="always"></fo:block>        
				       			 		</fo:table-cell>
				       			 	</fo:table-row>
				              		<#assign totAmt=0>
				              		<#assign canaraBankIds=CanaraBankMap.get(companyBankDetails.getKey())>  
				          			<fo:table-row>
				       			 		<fo:table-cell>
				       			 			<fo:block font-weight="bold" keep-together="always">CANARA BANK</fo:block>        
				       			 		</fo:table-cell>
				   			 		</fo:table-row>
				       			 	<#if canaraBankIds?has_content>
				       			 		<#assign sno=0>	
						              	<#list canaraBankIds as partyId>            
				                    		<#assign employeeLoanDisbursedList=employeeLoanDisbursedMap.entrySet()>
					                    	<#list employeeLoanDisbursedList as employee>  
					                    		<#if employee.getKey() == partyId>
					                    			<#assign netAmt = employee.getValue()>
						                    		<#if netAmt != 0>
								                   		<fo:table-row height="14px" space-start=".15in">
										                   	<fo:table-cell  border="solid">
										                   		<#assign sno=(sno+1)>
										                        <fo:block text-align="center">${sno?if_exists}</fo:block>
										                   	</fo:table-cell >
										                   	<fo:table-cell border="solid">
										                    	<fo:block text-align="center">${employee.getKey()?if_exists}</fo:block>
										                   	</fo:table-cell>
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  ${employee.getValue().get("partyName")?if_exists}</fo:block>
										                   	</fo:table-cell>
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; ${employee.getValue().get("employeePosition")?if_exists} </fo:block>
										                   	</fo:table-cell>
										                    <fo:table-cell  border="solid">
										                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  ${employee.getValue().get("accNo")?if_exists}</fo:block>
										                    </fo:table-cell>
										                    <#assign totAmt=totAmt+netAmt?if_exists>
										                   	<fo:table-cell  border="solid">
										                        <fo:block text-align="center">${netAmt?if_exists?string("#0.00")}</fo:block>
										                   	</fo:table-cell>
								               			</fo:table-row>
								               		</#if>
								                </#if>
								         	</#list>
					                    </#list>
					           		</#if>
					              	<fo:table-row border="solid">
					              		<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              		<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              		<fo:table-cell>
					              			<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
					              		</fo:table-cell>
					              		<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              		<fo:table-cell><fo:block text-align="center" font-weight="bold">&#160;</fo:block></fo:table-cell>
					              		<fo:table-cell border="solid"><fo:block text-align="center" font-weight="bold">${totAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
					              	</fo:table-row>
		            			</#if>          
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
