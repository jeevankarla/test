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
			<fo:simple-page-master master-name="main" page-height="11.69in" page-width="8.27in"  margin-bottom=".1in" margin-left="0.5in" margin-right=".3in" margin-top="1in">
		        <fo:region-body margin-top="2in"/>
		        <fo:region-before extent="1in"/>
		        <fo:region-after extent="1in"/>        
		    </fo:simple-page-master>   
		</fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "NoteSheet.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if finalRegionPaySheetMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>	
        			  
                	
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">NOTE SHEET</fo:block>
          		<#-->	<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Arial" font-size = "10pt"> From ${fromDate} - ${thruDate} </fo:block> -->

            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
	        	<fo:table font-family="Arial">
		                    <fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="25%"/>
		                    <fo:table-column column-width="20%"/>
		                    <fo:table-column column-width="25%"/>
		                    <fo:table-column column-width="25%"/>
		                    
		                    <fo:table-header>
								<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Sl.No</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Name of Office</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Gross Salary</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Employer's Contribution to CPF</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Gross Total</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							</fo:table-header>
		                    <fo:table-body>
		        <#assign slNo = 1>         
		        <#assign regionPaySheetMapKeys = finalRegionPaySheetMap.keySet()>
		        <#assign benefitTotal = 0>
		        <#assign empCpfTotal = 0>
		        <#assign totalOfGrossTotal = 0>
		        
		        <#list regionPaySheetMapKeys as region>
		        <#assign regionPaySheetEntry = finalRegionPaySheetMap.get(region)>
				 	<fo:table-row>
	                    <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${slNo}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${region?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false">${regionPaySheetEntry.get("totalBenifit")}</fo:block>  
			            </fo:table-cell>
			            <#assign benefitTotal = benefitTotal + regionPaySheetEntry.get("totalBenifit")>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${regionPaySheetEntry.get("empCpf")}</fo:block>  
			            </fo:table-cell>
			            <#assign empCpfTotal = empCpfTotal + regionPaySheetEntry.get("empCpf")>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${regionPaySheetEntry.get("grossTotal")}</fo:block>  
			            </fo:table-cell>
			            <#assign totalOfGrossTotal = totalOfGrossTotal + regionPaySheetEntry.get("grossTotal")>
				     </fo:table-row>
				     <#assign slNo = slNo + 1>
		         </#list>            
		       		<fo:table-row>
		       			<fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false" font-weight="bold">TOTAL</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false"></fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${benefitTotal}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${empCpfTotal}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block  text-align="right" font-size="8pt" white-space-collapse="false" font-weight="bold">${totalOfGrossTotal}</fo:block>  
			            </fo:table-cell>
		       		</fo:table-row>
			</fo:table-body>
			</fo:table>
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