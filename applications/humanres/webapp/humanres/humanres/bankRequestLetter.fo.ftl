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
    
 
 <#if BankAdvicePayRollMap?has_content>   
 	<#assign bankDetailsList=bankWiseEmplDetailsMap.entrySet()>
 	<#if bankDetailsList?has_content>
 		
 		<#list bankDetailsList as companyBankDetails>
 		<#assign netAmt = 0>
 		<fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before">
  		<#assign finAccDetails = delegator.findOne("FinAccount", {"finAccountId" : companyBankDetails.getKey()}, true)>
		<fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
        <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;(A GOVT. OF INDIA ENTERPRISE)</fo:block> 
        <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;BANK STATEMENT FOR OFFICERS AND STAFF</fo:block>
        <fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                             </fo:block>
  	</fo:static-content>  	
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">  
     				<#assign emplDetails = companyBankDetails.getValue()>
     				<#list emplDetails as empl>
     					<#assign emplId = empl>	
     					 <#assign netAmt=netAmt+BankAdvicePayRollMap.get(emplId).get("netAmt")?if_exists>
     					 <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(netAmt, "%indRupees-and-paiseRupees", locale)>
     				</#list>
			      	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      	<fo:block>
					    TO,
					    </fo:block>
					    <fo:block>
					    THE BRANCH MANAGER	
					    </fo:block>
					    <fo:block>
                        ${finAccDetails.finAccountName?if_exists}	
	                    </fo:block>
	                    <fo:block>
                        SUBJECT:- STATEMENT OF SALARY FOR THE MONTH OF ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()} 	
						</fo:block>
						<fo:block>
						Dear Sir,
						</fo:block>	
						<fo:block>
			        	Please Credit the Amounts written against individual names of employes N.H.D.C. Ltd. Which are given in the enclosed list.	
			        	THE TOTAL AMOUNT OF ${netAmt?if_exists?string("#0.00")} (${amountWords} Only) may be debited our Current Account with you.	
			     	   </fo:block>
			     	   
    			        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			      	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
    			        <fo:block>
    			        ASST. MANAGER&#160;&#160;&#160;&#160;DY MGR(COM)
    			        </fo:block>
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
 	
 </#if>
</fo:root>
</#escape>
