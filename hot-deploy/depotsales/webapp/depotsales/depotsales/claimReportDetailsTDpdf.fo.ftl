
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
        <#if silkNonDepotList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOEmail?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
				<fo:block text-align="center" font-size="12pt" font-weight="bold" >AGENCY WISE INVOICE OUTSTANDING REPORT</fo:block>
				
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="3%"/>
			            <fo:table-column column-width="18.8%"/>
			            <fo:table-column column-width="12.8%"/>
			            <fo:table-column column-width="12.8%"/>
	                    <fo:table-column column-width="12.8%"/>
	                    <fo:table-column column-width="12.8%"/>
	                    <fo:table-column column-width="12.8%"/>
			            <fo:table-body>
		            		 <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">SILK DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list silkDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>



							<fo:table-row>
								 <fo:table-cell  number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">SILK NON-DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list silkNonDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("##0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("##0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("##0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("##0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>

  

                                
                              <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">COTTON DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list cottonDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>




                                <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">COTTON NON-DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list cottonNonDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>







									<fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">JUTE DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list juteDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>



                               <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">JUTE NON-DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list juteNonDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>
							     
							     
							     
							     <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">OTHER DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list otherDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
							     </#list>









                              <fo:table-row>
								 <fo:table-cell number-columns-spanned="7">
										<fo:block  text-align="left" font-size="12pt"  font-weight="bold" white-space-collapse="false">OTHER NON DEPOT</fo:block>
					            </fo:table-cell >
							</fo:table-row>  
			                 <fo:table-row>
								 <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">SNO</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
										<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">NAME OF USER AGENCIES</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">QUANTITY SUPPLEID IN Kg</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">VALUE OF YARN SUPPLIED Rs</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL  COST OF TRANSPORTATION</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ACTUAL TRANSPORTATION ELIGIBLE FOR REIMBURSEMENT</fo:block>
					            </fo:table-cell>
					           <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">DEPOT  Charges</fo:block>
					            </fo:table-cell>
							</fo:table-row>  
							
								<#list otherNonDepotList as invoice>
										<fo:table-row>
										    <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("sNo")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell border-style="solid" >
								            	<fo:block  text-align="left" font-size="9pt" <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("partyName")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if>  white-space-collapse="false">${invoice.get("totInvQty")?if_exists}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("totInvValue")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"   <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false"> ${invoice.get("actualFrightCharges")?if_exists?string("0.00")} </fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("frightCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
								            <fo:table-cell  border-style="solid">
								            	<fo:block  text-align="right" font-size="9pt"  <#if invoice.get("partyName")=="TOTAL"> font-weight="bold"  </#if> white-space-collapse="false">${invoice.get("depotCharges")?if_exists?string("0.00")}</fo:block>
								            </fo:table-cell >
										</fo:table-row> 
								
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

