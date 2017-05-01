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
			<fo:simple-page-master master-name="main" page-height="11.69in" page-width="8.27in" margin-top=".6in"  margin-bottom=".1in" margin-left="0.2in" margin-right=".1in">
		        <fo:region-body margin-top="1.5in"/>
		        <fo:region-before extent="1in"/>
		        <fo:region-after extent="1in"/>        
		    </fo:simple-page-master>   
		</fo:layout-master-set>
      
	   <#if outstandingList?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
	        		<#if roId?has_content>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "${roId}_Header"}, true)>
				    <#assign reportSubHeader1 = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "${roId}_Header01"}, true)>
				    </#if>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"><#if reportSubHeader?has_content >${reportSubHeader.description?if_exists}</#if></fo:block>	
        			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"><#if reportSubHeader1?has_content >${reportSubHeader1.description?if_exists}</#if></fo:block>
        			 <fo:block >&#160;</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">DETAIL OF IMPREST/ STAFF ADVANCE OUTSTANDING FOR MORE THAN 45 DAYS</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
	        	<fo:table font-family="Arial" border-style="solid">
		                    <fo:table-column column-width="35pt"/>
		                    <fo:table-column column-width="140pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="75pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="70pt"/>
		                   
		                    <fo:table-header>
								<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Sl.No</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Name of Staff</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Date of Advance</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Amount of advance given </fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Amount of Advance outstanding as on ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(AsOnDate?if_exists, "dd/MM/yyyy ")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Purpose of Advance</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Whether Bills are submitted to competent authority? YES/ NO</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Remark</fo:block>  
							            </fo:table-cell>
							            
							            
							     </fo:table-row>
							</fo:table-header>
		                    <fo:table-body>	
		                    	<fo:table-row>
		                    		<fo:table-cell></fo:table-cell>
		                    	</fo:table-row>	                
		        				<#assign slNo = 1>         
		        				<#list outstandingList as outStanding> 
							 	<fo:table-row>
				                     <fo:table-cell border-style="solid">
						            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false">${slNo?if_exists}</fo:block>  
						            </fo:table-cell>
						              <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, outStanding.ownerPartyId, false)>
						             <fo:table-cell border-style="solid">
						            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${partyName?if_exists}</fo:block>  
						            </fo:table-cell>
						             <fo:table-cell border-style="solid">
						            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${outStanding.fromDate?if_exists}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell border-style="solid">
						            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false"><@ofbizCurrency amount=outStanding.depositAmt  isoCode=currencyUomId/></fo:block>  
						            </fo:table-cell>
						            <fo:table-cell border-style="solid">
						            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false"><@ofbizCurrency amount=outStanding.actualBalance isoCode=currencyUomId/> </fo:block>  
						            </fo:table-cell>
						            <fo:table-cell border-style="solid">
						            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">&#160;${outStanding.description?if_exists}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell border-style="solid">
						            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false"></fo:block>  
						            </fo:table-cell>
						            <fo:table-cell border-style="solid">
						            	<fo:block text-align="left" font-size="8pt"   white-space-collapse="false">${outStanding.remarks?if_exists}</fo:block>  
						            </fo:table-cell>
							    </fo:table-row>
				     <#assign slNo = slNo + 1>
		         </#list>    
		             
			</fo:table-body>
			</fo:table>
			</fo:flow>
		  </fo:page-sequence>
<#else> 
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt" text-align="center" >
			        	No Advance Outstandings Found
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
 </#if>
 </fo:root>
</#escape>