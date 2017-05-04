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
        ${setRequestAttribute("OUTPUT_FILENAME", "ChequeBouncingDetails.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if printChequeBouncingDetailList?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "${roId}_Header"}, true)>
                    <#assign reportSubHeader1 = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "${roId}_Header01"}, true)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"><#if reportSubHeader?has_content >${reportSubHeader.description?if_exists} </#if></fo:block>	
        			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"><#if reportSubHeader1?has_content >${reportSubHeader1.description?if_exists}</#if></fo:block>
        			 <fo:block >&#160;</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">CHEQUE BOUNCING DETAILS</fo:block>
          		<fo:block text-align="center" keep-together="always"  white-space-collapse="false" font-family="Arial" font-size = "10pt"> From ${fromDate?if_exists} - To ${thruDate?if_exists} </fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
	        	<fo:table font-family="Arial" border-style="solid">
		                    <fo:table-column column-width="35pt"/>
		                    <fo:table-column column-width="140pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="55pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="75pt"/>
		                    <fo:table-header>
								<fo:table-row>
					                    <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Sl.No</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Cheque Issued By</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell border-style="solid">
							            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Cheque No.</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Cheque Date</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Cheque Amount(in Rs)</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Reason</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Whether Cheque Bouncing Charges of Rs 250/-are recovered or not</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Whether Dues are cleared by Party?YES/NO</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell border-style="solid">
							            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false" font-weight="bold">Remarks</fo:block>  
							            </fo:table-cell>
							            
							     </fo:table-row>
							</fo:table-header>
		        <fo:table-body>
		        <#assign slNo = 1>         
		        <#list printChequeBouncingDetailList as printChequeBouncingDetailEntry>
				 	<fo:table-row>
	                    <fo:table-cell border-style="solid">
			            	<fo:block  text-align="center" font-size="8pt" white-space-collapse="false">${slNo}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.issuingAuthority?if_exists}</fo:block>  
			            </fo:table-cell>
			             <fo:table-cell border-style="solid">
			            	<fo:block  text-align="left" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.chequeNo?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.chequeDate?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.chequeAmount?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.cancelComments?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="right" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.chrges?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="center" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.duescleared?if_exists}</fo:block>  
			            </fo:table-cell>
			            <fo:table-cell border-style="solid">
			            	<fo:block text-align="left" font-size="8pt" white-space-collapse="false">${printChequeBouncingDetailEntry.remarks?if_exists}</fo:block>  
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
			        	No Cheques found
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>