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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
      	margin-left="0.3in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in">
        	<fo:region-body margin-top=".9in"/>
        	<fo:region-before extent="1in"/>
        	<fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence> 
	<#else>
 		<#if finalMap?has_content>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
 			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
 			<#assign SNo=1>
     		<fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     			<fo:static-content flow-name="xsl-region-before">
	     			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                	<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>      
        			<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Courier,monospace">&#160;${reportHeader.description?if_exists}</fo:block>
                	<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Courier,monospace">&#160;${reportSubHeader.description?if_exists}</fo:block>
	     			<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;                                                                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     			<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;              INCREMENT DUE REPORT FOR  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMMMM,yyyy"))?upper_case}                  PAGE: <fo:page-number/></fo:block>
	     			</fo:static-content>       	 	 	  	 	
	     		 <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	     			<fo:block font-family="Courier,monospace">
	     				<fo:table>
	     					<fo:table-column column-width="50pt"/>
	     					<fo:table-column column-width="45pt"/>
	     					<fo:table-column column-width="165pt"/>
	     					<fo:table-column column-width="180pt"/>
	       					<fo:table-column column-width="150pt"/>
	       					<fo:table-body>
	       						<fo:table-cell><fo:block text-align="center" font-size="13pt" font-weight="bold" border-style="solid">SL No</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block text-align="center" font-size="13pt" font-weight="bold" border-style="solid">Empl</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block text-align="center" font-size="13pt" font-weight="bold" border-style="solid">Employee Name</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block text-align="center" font-size="13pt" font-weight="bold" border-style="solid">Designation</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block text-align="center" font-size="13pt" font-weight="bold" border-style="solid">Increment Due Date</fo:block></fo:table-cell>
	       					</fo:table-body>
	     				</fo:table>
	     			</fo:block>
           			<fo:block text-align="center">
           				<fo:table text-align="center">
           					<fo:table-column column-width="50pt"/>
	     					<fo:table-column column-width="45pt"/>
	     					<fo:table-column column-width="165pt"/>
	     					<fo:table-column column-width="180pt"/>
	       					<fo:table-column column-width="150pt"/>
	       					<fo:table-body>
	       					<#assign sNo = 0>
	       					<#assign employeeDetails=finalMap.entrySet()>
	       					<#list employeeDetails as employeeDetail>
	       					<#assign sNo = sNo+1>
   								<fo:table-row>
		       						<fo:table-cell><fo:block keep-together="always" font-size="11pt" text-align="left" border-style="solid">${sNo}</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block keep-together="always" font-size="11pt" text-align="left" border-style="solid">${employeeDetail.getKey()}</fo:block></fo:table-cell>
	       							<fo:table-cell><fo:block keep-together="always" font-size="11pt" text-align="left" border-style="solid">${employeeDetail.getValue().get("partyName")}</fo:block></fo:table-cell>
	       							<fo:table-cell><fo:block keep-together="always" font-size="11pt" text-align="left" border-style="solid">${employeeDetail.getValue().get("designation")}</fo:block></fo:table-cell>
	       							<fo:table-cell><fo:block keep-together="always" font-size="11pt" text-align="left" border-style="solid">${employeeDetail.getValue().get("incrementDate")}</fo:block></fo:table-cell>
	       						</fo:table-row>
			       			</#list>
	       					</fo:table-body>
	           			</fo:table>           		
	           		</fo:block>
	           		<fo:block font-family="Courier,monospace">
	                	<fo:table>
		                	<fo:table-column column-width="1030pt"/>
	                		<fo:table-body> 
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
                 				<fo:table-row>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" font-size="11pt">Signature of Manager (Admin)&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Signature of Director</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                      	</fo:table-body>
                     	</fo:table>
                 	</fo:block>
	          </fo:flow>          
	        </fo:page-sequence> 
	      <#else>    	
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	   		 		<fo:block font-size="14pt">
	        			No Records found.......!
	   		 		</fo:block>
				</fo:flow>
			</fo:page-sequence>		
    	</#if> 
    </#if>
  </fo:root>
</#escape>