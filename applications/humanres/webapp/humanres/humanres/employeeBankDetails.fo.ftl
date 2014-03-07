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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.2in" margin-bottom="0.3in" margin-left=".7in" margin-right="1in">
          <fo:region-body margin-top=".7in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign temp=0>
    <#assign totalAmount=0>
  <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before">
  		<fo:block text-align="center" font-weight="bold"><fo:inline text-decoration="underline">Payment Advice </fo:inline></fo:block>
  		<#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
  		<fo:block text-align="right">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowDate, "dd/MM/yyyy")}</fo:block>
  		<fo:block>The Manager</fo:block>
  		<fo:block>${(addresses[0].postalAddress).address1}</fo:block>
  		<fo:block>${(addresses[0].postalAddress).address2}</fo:block>
  	</fo:static-content>
  	 <fo:static-content flow-name="xsl-region-after">
        <fo:block font-size="8pt" text-align="right">             
             <#if footerImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${footerImageUrl}</@ofbizContentUrl>" overflow="hidden" height="20px" content-height="scale-to-fit"/></#if>             
         </fo:block>          
     </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    	<fo:block>Dear Sir,</fo:block>
    	<fo:block keep-together="always"><fo:inline text-decoration="underline">Payment Advice from ${groupName} A/c# for  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMMMM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMMMM-yyyy")}</fo:inline></fo:block>
    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    	<fo:block>Please make the payroll transfer from Above Account Number to the below mentioned account numbers towards Employee Salary.</fo:block>
      	
      <fo:block>
		<fo:table width="100%" table-layout="fixed">
		    <fo:table-header height="14px">
		       	<fo:table-row height="14px" space-start=".15in" text-align="center">
                	<fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
                    	<fo:block text-align="center" font-weight="bold">Sl.No</fo:block>
                    </fo:table-cell>
                     <fo:table-cell number-columns-spanned="1" border-style="solid" width="150px">
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
               <#list employeeBankList as employeeProfile>
                   <fo:table-row height="14px" space-start=".15in">
                   <fo:table-cell  border="solid">
                   		<#assign temp=(temp+1)>
                        <fo:block text-align="center">${temp}</fo:block>
                   </fo:table-cell >
                   <fo:table-cell border="solid">
                    	<fo:block ><#if employeeProfile.empName?has_content>${employeeProfile.empName}</#if></fo:block>
                   </fo:table-cell>
                   <fo:table-cell  border="solid">
                        <fo:block text-align="center"><#if employeeProfile.accountNumber?has_content>${employeeProfile.accountNumber}</#if></fo:block>
                   </fo:table-cell>
                   <fo:table-cell  border="solid">
                   		<#assign totalAmount=(totalAmount+(employeeProfile.amount))>
                        <fo:block text-align="right">${employeeProfile.amount}.00
                        </fo:block>
                   </fo:table-cell>
               </fo:table-row>
                  </#list>
              <fo:table-row border="solid">
              	<fo:table-cell/>
              	<fo:table-cell>
              		<fo:block text-align="center">Total</fo:block>
              	</fo:table-cell>
              	<fo:table-cell />
              	<fo:table-cell border="solid"><fo:block text-align="right" font-weight="bold">${totalAmount}.00</fo:block></fo:table-cell>
              </fo:table-row>    
          </fo:table-body>
        </fo:table> 
     </fo:block>
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     <fo:block>Yours Sincerely</fo:block>
     <fo:block>For ${groupName}</fo:block>
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     <fo:block>Authorized Signatory</fo:block>
    </fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>
