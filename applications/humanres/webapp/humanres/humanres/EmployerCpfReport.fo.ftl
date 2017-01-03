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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
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
       <#if EmployerCpfReportList?has_content>
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
					</#if>                                                  																					Page No: <fo:page-number/></fo:block>	
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">Employer CPF Report for the month : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()} </fo:block>
            	</fo:static-content>	        	
	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
		<fo:block>
		<fo:table>
            <fo:table-column column-width="5%"/>
            <fo:table-column column-width="6%"/>
            <fo:table-column column-width="10%"/>
            <fo:table-column column-width="10%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="8%"/>
            <fo:table-column column-width="10%"/>
            <fo:table-header>
				<fo:table-row>
	                    <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Sl.No</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Id</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Name</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Dept</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Basic</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Dearness Allowance</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Total Benefits</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Employee C.P Fund</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">PF.VC</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Employer C.P Fund</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">F.P.F</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Total</fo:block>  
			            </fo:table-cell>
			     </fo:table-row>
			</fo:table-header>
			
			<fo:table-body>
				<#assign slNo = 1>
				<#list EmployerCpfReportList as EmployerCpfReportEntry>
				<#if EmployerCpfReportEntry.partyId == "">
					<#assign slNo = 1>
				<fo:table-row>
	                <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.partyId}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.partyName}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.region}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.basic}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.dearnessAlw}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.totalBenefit}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.empCpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.pfVc}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.emplyrCpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.empFpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${EmployerCpfReportEntry.total}</fo:block>  
		            </fo:table-cell>
				</fo:table-row>
				
				<#else>
				<fo:table-row>
	                <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${slNo}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.partyId}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.partyName}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.region}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.basic}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.dearnessAlw}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.totalBenefit}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.empCpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.pfVc}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.emplyrCpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.empFpf}</fo:block>  
		            </fo:table-cell>
		            <fo:table-cell border-style="solid">
		            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${EmployerCpfReportEntry.total}</fo:block>  
		            </fo:table-cell>
				</fo:table-row>
				<#assign slNo = slNo + 1>	
				</#if>
				
				</#list>
			</fo:table-body>
			</fo:table>
		</fo:block>
    	
	</fo:flow>
		</fo:page-sequence>
			<#else>
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	No Records Found
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>