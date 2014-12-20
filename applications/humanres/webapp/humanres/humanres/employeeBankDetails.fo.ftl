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
        margin-top="0.3in" margin-bottom="0.3in" margin-left="1in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    
    <#assign totalAmount=0>
 <#if BankAdvicePayRollMap?has_content>   
 <#assign bankDetailsList=bankWiseEmplDetailsMap.entrySet()>
 <#if bankDetailsList?has_content>  
 <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
 <#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", parameters.partyId, "userLogin", userLogin))/>
 <#list bankDetailsList as companyBankDetails>
 <#assign temp=0>
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before">
  		<#assign finAccDetails = delegator.findOne("FinAccount", {"finAccountId" : companyBankDetails.getKey()}, true)>
  		<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
  		<fo:block white-space-collapse="false" font-weight="bold" text-align="left" text-indent="60pt" keep-together="always">${partyGroup.groupName?if_exists}, <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if> </fo:block>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">${finAccDetails.finAccountName?if_exists}                                                     																					${uiLabelMap.CommonPage}No: <fo:page-number/></fo:block>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                                             Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd-MMM-yyyy")}</fo:block>
  	</fo:static-content>  	
    <fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
      <#if CanaraBankMap.get(companyBankDetails.getKey())?has_content>
      
      </#if>  	
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
                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="130px">
                        <fo:block text-align="center" font-weight="bold" >${uiLabelMap.Designation}</fo:block>
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
         	<#assign totalNetAmt=0>
         	<#assign recordCnt=0>
         	<#assign emplPosition=0 />
         	<#assign bankAdviceDetailsList=BankAdvicePayRollMap.entrySet()>
         	  <#assign partyIdList=companyBankDetails.getValue()>
               <#list partyIdList as partyId>               	
               	   <#assign recordCnt=recordCnt+1>
               	   <#if BankAdvicePayRollMap.get(partyId)?has_content>
	                   <fo:table-row height="14px" space-start=".15in">
		                   <fo:table-cell  border="solid">
		                   		<#assign temp=(temp+1)>
		                        <fo:block text-align="center">${temp?if_exists}</fo:block>
		                   </fo:table-cell >
		                   <fo:table-cell border="solid">
		                    	<fo:block text-align="center">${BankAdvicePayRollMap.get(partyId).get("emplNo")?if_exists}</fo:block>
		                   </fo:table-cell>
		                   <#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : BankAdvicePayRollMap.get(partyId).get("emplNo")})/>  	
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  ${BankAdvicePayRollMap.get(partyId).get("empName")?if_exists}</fo:block>
		                   </fo:table-cell>
		                   <#assign designationName="">
		                   <#if emplPosition[0].name?has_content>
		                   		<#assign designationName=emplPosition[0].name>
		                   <#else>
		                   		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPosition[0].emplPositionId}, true)>
                     			<#assign designationName=designation.description?if_exists>
                     		</#if>	
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; ${designationName?if_exists} </fo:block>
		                   </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  ${BankAdvicePayRollMap.get(partyId).get("acNo")?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <#assign totalNetAmt=totalNetAmt+BankAdvicePayRollMap.get(partyId).get("netAmt")?if_exists>
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="center">${BankAdvicePayRollMap.get(partyId).get("netAmt")?if_exists?string("#0.00")}</fo:block>
		                   </fo:table-cell>
	               		</fo:table-row>
	               		<#if recordCnt==40>
	               			 <#assign recordCnt=0>
	               			 <fo:table-row>
	               			 	<fo:table-cell>
	               			 		<fo:block page-break-after="always"></fo:block>        
	               			 	</fo:table-cell>
	               			 </fo:table-row>
	               		</#if> 
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
	              	 <#if BankAdvicePayRollMap.get(partyId)?has_content> 
	                   <fo:table-row height="14px" space-start=".15in">
		                   <fo:table-cell  border="solid">
		                   		<#assign sno=(sno+1)>
		                        <fo:block text-align="center">${sno?if_exists}</fo:block>
		                   </fo:table-cell >
		                   <fo:table-cell border="solid">
		                    	<fo:block text-align="center">${BankAdvicePayRollMap.get(partyId).get("emplNo")?if_exists}</fo:block>
		                   </fo:table-cell>
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;  ${BankAdvicePayRollMap.get(partyId).get("empName")?if_exists}</fo:block>
		                   </fo:table-cell>
		                   <#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : BankAdvicePayRollMap.get(partyId).get("emplNo")})/>  
		                   <#assign designationName="">
		                   <#if emplPosition[0].name?has_content>
		                   		<#assign designationName=emplPosition[0].name>
		                   <#else>
		                   		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPosition[0].emplPositionId}, true)>
                     			<#assign designationName=designation.description?if_exists>
                     		</#if>	
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160; ${designationName?if_exists} </fo:block>
		                   </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  ${BankAdvicePayRollMap.get(partyId).get("acNo")?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <#assign totAmt=totAmt+BankAdvicePayRollMap.get(partyId).get("netAmt")?if_exists>
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="center">${BankAdvicePayRollMap.get(partyId).get("netAmt")?if_exists?string("#0.00")}</fo:block>
		                   </fo:table-cell>
	               		</fo:table-row>
	               		 </#if>       
	                  </#list>
	                  </#if>
		              <fo:table-row border="solid">
		              	<fo:table-cell/>
		              	<fo:table-cell>              		
		              	</fo:table-cell>
		              	<fo:table-cell>
		              		<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
		              	</fo:table-cell>
		              	<fo:table-cell />
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
