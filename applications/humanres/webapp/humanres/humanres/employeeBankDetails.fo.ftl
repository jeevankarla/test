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
        margin-top="0.2in" margin-bottom="0.3in" margin-left=".7in" margin-right="1in">
          <fo:region-body margin-top=".7in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign temp=0>
    <#assign totalAmount=0>
 <#if BankAdvicePayRollMap?has_content>   
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before">
  		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
  		<#assign postalAddress=delegator.findByAnd("PartyAndPostalAddress", {"partyId" : parameters.partyId})/>
  		<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always">${partyGroup.groupName?if_exists}  ${postalAddress[0].address1?if_exists}                                     ${uiLabelMap.CommonPage}No: <fo:page-number/></fo:block>
        	<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160; BANK STATEMENT(SALARY) FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}                                Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd-MMM-yyyy")}</fo:block>
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
         	<#assign totalNetAmt=0>
         	<#assign bankAdviceDetailsList=BankAdvicePayRollMap.entrySet()>
               <#list bankAdviceDetailsList as bankAdvice>
                   <fo:table-row height="14px" space-start=".15in">
                   <fo:table-cell  border="solid">
                   		<#assign temp=(temp+1)>
                        <fo:block text-align="center">${temp?if_exists}</fo:block>
                   </fo:table-cell >
                   <fo:table-cell border="solid">
                    	<fo:block text-align="center">${bankAdvice.getValue().get("emplNo")?if_exists}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell  border="solid">
                        <fo:block text-align="left" keep-together="always">${bankAdvice.getValue().get("empName")?if_exists}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell  border="solid">
                        <fo:block text-align="left">${bankAdvice.getValue().get("acNo")?if_exists}</fo:block>
                    </fo:table-cell>
                    <#assign totalNetAmt=totalNetAmt+bankAdvice.getValue().get("netAmt")>
                   <fo:table-cell  border="solid">
                        <fo:block text-align="right">${bankAdvice.getValue().get("netAmt")?if_exists?string("#0.00")}</fo:block>
                   </fo:table-cell>
               </fo:table-row>
                  </#list>
              <fo:table-row border="solid">
              	<fo:table-cell/>
              	<fo:table-cell>              		
              	</fo:table-cell>
              	<fo:table-cell>
              		<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
              	</fo:table-cell>
              	<fo:table-cell />
              	<fo:table-cell border="solid"><fo:block text-align="right" font-weight="bold">${totalNetAmt?if_exists?string("#0.00")}</fo:block></fo:table-cell>
              </fo:table-row>    
          </fo:table-body>
        </fo:table> 
     </fo:block>
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>    
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     <fo:block>Authorized Signatory</fo:block>
    </fo:flow>
 </fo:page-sequence>
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
