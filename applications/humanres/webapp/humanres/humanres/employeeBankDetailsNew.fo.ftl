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
      <fo:simple-page-master master-name="main" page-height="10in" page-width="12in"
        margin-top="0.3in" margin-bottom="0.3in" margin-left=".5in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    
        ${setRequestAttribute("OUTPUT_FILENAME", "BankAdviceStatement.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if BankAdvicePayRollMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>
				    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">
        			<#if parameters.billingTypeId=="SP_LEAVE_ENCASH">   
        			<#assign timePeriodEnd=basicSalDate?if_exists>
					</#if>                                                  																					${uiLabelMap.CommonPage}No: <fo:page-number/></fo:block>	
                	<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                  Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd-MMM-yyyy")}</fo:block>
          			<#--><fo:block text-align="left"  keep-together="always" font-family="Arial" white-space-collapse="false" font-size="8pt"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                 &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>-->
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
    	<fo:block>
		<fo:table width="100%" table-layout="fixed">
		    <fo:table-header height="14px">
		       	<fo:table-row height="14px" space-start=".15in" text-align="center">
                	<fo:table-cell number-columns-spanned="1" border-style="solid" width="40px">
                    	<fo:block text-align="center" font-weight="bold">Sl.No</fo:block>
                    </fo:table-cell>
                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
                        <fo:block text-align="center" font-weight="bold" >${uiLabelMap.EmployeeName}</fo:block>
                     </fo:table-cell> 
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.AccountNumber}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.AccountingBankName}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="80px">
                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.AccountingBranchName}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="80px">
                        <fo:block font-weight="bold" text-align="center" >${uiLabelMap.AccountingIfscCode}</fo:block>
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
         	<#assign slNo = 1>
               <#list AllPartyList as partyId>               	
               	   <#assign recordCnt=recordCnt+1>
               	   <#if BankAdvicePayRollMap.get(partyId)?has_content>
	                   <fo:table-row height="14px" space-start=".15in">
		                   <fo:table-cell  border="solid">
		                   		<#assign temp=(temp+1)>
		                        <fo:block text-align="center">${slNo?if_exists}</fo:block>
		                   </fo:table-cell >
		                   <#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : BankAdvicePayRollMap.get(partyId).get("emplNo")})/>  	
		                   <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false">${BankAdvicePayRollMap.get(partyId).get("empName")?if_exists}</fo:block>
		                   </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">${BankAdvicePayRollMap.get(partyId).get("acNo")?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false">${BankAdvicePayRollMap.get(partyId).get("finAccountName")?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false">${BankAdvicePayRollMap.get(partyId).get("finAccountBranch")?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell  border="solid">
		                        <fo:block text-align="left" white-space-collapse="false" keep-together="always">${BankAdvicePayRollMap.get(partyId).get("ifscCode")?if_exists}</fo:block>
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
	               	<#assign slNo = slNo + 1>             
                  </#list>
              <fo:table-row border="solid">
              	<fo:table-cell/>
              	<fo:table-cell>              		
              	</fo:table-cell>
              	<fo:table-cell>
              		<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
              	</fo:table-cell>
              	<fo:table-cell />
              	<fo:table-cell/>
              	<fo:table-cell/>
              	<fo:table-cell><fo:block text-align="center" font-weight="bold">${totalNetAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
              </fo:table-row>
          </fo:table-body>
        </fo:table> 
     </fo:block>
	        	
	</fo:flow>
		</fo:page-sequence>
			<#else>
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	${uiLabelMap.NoOrdersFound}.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>