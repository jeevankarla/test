
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".2in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentReport.pdf")}
        <#if finalList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
                <#-- <fo:block text-align="left"    font-size="10pt" >STATEMENT SHOWING THE AGENCYWISE DETAILS OF COTTON/SILK/JUTE YARN SUPPLIED BY NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED UNDER THE SCHEME FOR SUPPLY OF YARN AT :<fo:inline font-weight="bold" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:inline></fo:block> -->
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >STATEMENT FOR CLAIMING REIMBURSEMENT OF THE COST OF TRANSPORTATION AND OVERHEADS TOWARDS </fo:block>
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >THE QUANTUM OF YARN SUPPLIED UNDER MILL GATE PRICE SCHEME FOR THE PERIOD </fo:block>
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >TRANSPORTATION AND DEPOT EXPENSES FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			<#if reportType?has_content && reportType=="WITHOUT_DEPOT">
			            	<fo:table-column column-width="10%"/>
						</#if>
             		    <fo:table-column column-width="5%"/>
			            <fo:table-column column-width="21%"/>
			            <fo:table-column column-width="14%"/>
			            <fo:table-column column-width="14%"/>
	                    <fo:table-column column-width="14%"/>
			            <fo:table-column column-width="14%"/>
			            <#if reportType?has_content && reportType=="DEPOT">
			            <fo:table-column column-width="14%"/>
						</#if>
			            <fo:table-body>
			                <fo:table-row>
								<#if reportType?has_content && reportType=="WITHOUT_DEPOT">
									<fo:table-cell>

									</fo:table-cell>
								</#if>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">S No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">STATE</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLIED IN KG</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED RS</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
								<#if reportType?has_content && reportType=="DEPOT">
					             <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT CHARGES 2.50%</fo:block>
					            </fo:table-cell>
					            </#if>
							</fo:table-row>
			                  <#list finalList as eachList>
	                            <#assign eachStateDetails = eachList.entrySet()>
								 <#list eachStateDetails as eachState>
								<#assign state = eachState.getKey()>
								<#assign stateDetails = eachState.getValue()>
							  <fo:table-row>
							  	
	                             <#if reportType?has_content && reportType=="WITHOUT_DEPOT">
									<fo:table-cell>

									</fo:table-cell>
								</#if>
			                    <fo:table-cell border-style="solid" number-columns-spanned="6">
					            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold" >${state?if_exists}</fo:block>
					            </fo:table-cell>
							  </fo:table-row>
							 
							  <#list stateDetails as eachCustomer>
							  	 <fo:table-row>
								<#if reportType?has_content && reportType=="WITHOUT_DEPOT">
									<fo:table-cell>
									</fo:table-cell>
								</#if>
			                    <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="left" font-size="8pt" white-space-collapse="false">${eachCustomer.get("sr")?if_exists} </fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block text-align="left" font-size="8pt" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if>white-space-collapse="false">${eachCustomer.get("partyName")}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="right" font-size="8pt" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if> white-space-collapse="false">${eachCustomer.get("invoiceQTY")?if_exists}</fo:block>
					            </fo:table-cell>  
					             <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if> font-size="8pt" white-space-collapse="false">${eachCustomer.get("invoiceAMT")?if_exists}</fo:block>
						         </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  keep-together="always" text-align="right" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if>font-size="8pt" white-space-collapse="false">${eachCustomer.get("shippingCost")?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="8pt" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if> white-space-collapse="false">${eachCustomer.get("reimbursentAMT")?if_exists}</fo:block>
					            </fo:table-cell>
					            <#if reportType?has_content && reportType=="DEPOT">
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="8pt" <#if eachCustomer.get("partyName")=="TOTAL"> font-weight="bold" </#if> white-space-collapse="false">${eachCustomer.get("depotCharges")?if_exists}</fo:block>
					            </fo:table-cell>
					            </#if>
							</fo:table-row>
							
							</#list>
							</#list>
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
 	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if> 
 </fo:root>
</#escape>