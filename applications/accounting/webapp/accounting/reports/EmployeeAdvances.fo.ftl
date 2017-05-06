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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="2.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "EmployeeAdvancesAndSubScheduleReport.pdf")}
 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			 <#if finAccountTypeIdsMap?has_content>
			<#assign finAccountTypeId=finAccountTypeIdsMap.entrySet()> 
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                                                          Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
					<#assign roId = parameters.division>
              	 	<#assign roHeader = roId+"_HEADER">
              	 	<#assign roSubheader = roId+"_HEADER01">
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader}, true)>
					<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubheader}, true)>
					<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false"  font-weight="bold">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				  	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;               ${reportSubHeader.description?if_exists}                              Page No:<fo:page-number/></fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>
           
			<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            		<#list finAccountTypeId as finAccntId>
			   	<#assign finAccountType = delegator.findOne("FinAccountType", {"finAccountTypeId" :finAccntId.getKey()}, true)>
                    <#assign finAccountTypeglAccnt = delegator.findOne("FinAccountTypeGlAccount", {"finAccountTypeId" :finAccntId.getKey(),"organizationPartyId":"Company"}, true)>
            
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;STATEMENT FOR ${finAccountTypeglAccnt.glAccountId?if_exists} - ${finAccountType.description?if_exists?upper_case}</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
              		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;                              OPENING BALANCE     DURING THER PERIOD       CLOSING BALANCE </fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="13%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">SL CODE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">SL DESCRIPTION</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="12pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 	
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <#assign finAccntValues=finAccntId.getValue()>
				<#assign grandOpenBalDebit=0>
				<#assign grandOpenBalCredit=0>
                <#assign grandCurrentDebit=0>
	            <#assign grandCurrentCredit=0>
	            <#assign grandClosingDebit=0>
                <#assign grandClosingCredit=0>
                <#assign grandTotalBalance=0>
				<#list finAccntValues as finAccntValue>
                <#assign openBalanceDebit=0> 
                <#assign openBalanceDebit=finAccntValue.get("openBalanceDebit")> 
                <#assign openBalanceCreditt=0>  
                <#assign openBalanceCredit=finAccntValue.get("openBalanceCredit")> 
                <#assign currentDebit=0>  
                <#assign currentDebit=finAccntValue.get("currentDebit")>
                <#assign currentCredit=0>  
                <#assign currentCredit=finAccntValue.get("currentCredit")>
                <#assign balance=0>
    			<#if finAccntValue.get("balance")?has_content>
                  <#assign balance=finAccntValue.get("balance")>
                </#if>
				<#assign grandOpenBalDebit=grandOpenBalDebit+openBalanceDebit>
				<#assign grandOpenBalCredit=grandOpenBalCredit+openBalanceCredit>
				<#assign grandCurrentDebit=grandCurrentDebit+currentDebit>
				<#assign grandCurrentCredit=grandCurrentCredit+currentCredit>
                <#if openBalanceDebit !=0 || openBalanceCreditt !=0 || currentDebit !=0 || currentCredit!=0 || balance!=0>
               <fo:block>
                    <fo:table>
				    <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("partyId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("Name")?if_exists?upper_case}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${openBalanceDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${openBalanceCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${currentDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${currentCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		<#--	<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("closingDebit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("closingCredit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell> -->
                             <#if balance gt 0> 
                             <#assign grandClosingDebit=grandClosingDebit+balance> 
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${balance?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			 <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">0.00</fo:block>  
                			</fo:table-cell>
                			<#else>
                           <#assign grandClosingCredit=grandClosingCredit+balance>
                              <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">0.00</fo:block>  
                			</fo:table-cell>  
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${((-1)*balance)?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			</#if>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
                </#if>	
				</#list>	
			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
                    <fo:table>
				    <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>

                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">TOTAL :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandOpenBalDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandOpenBalCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandCurrentDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandCurrentCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		 <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grandClosingDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${((-1)*grandClosingCredit)?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell> 
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				 </#list>
				<fo:block>
                    <fo:table>
				    <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
						<fo:table-row>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
						<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
						<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="left" keep-together="always" >&#160;Prepared by </fo:block>
		                	</fo:table-cell>
		                	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
		                	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160; Pre Auditor</fo:block>
		                	</fo:table-cell>
		                	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
		                	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" >&#160;</fo:block>
		                	</fo:table-cell>
	                    	<fo:table-cell>
	                    	    <fo:block font-size="11pt" text-align="right" keep-together="always" > &#160;Manager (Finance)</fo:block>
		                	</fo:table-cell>
						</fo:table-row>
						</fo:table-body>
                </fo:table>
               </fo:block> 
			 </fo:flow>
			
			 <#else>	
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                                                          Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
					<#assign roId = parameters.division>
              	 	<#assign roHeader = roId+"_HEADER">
              	 	<#assign roSubheader = roId+"_HEADER01">
					<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader}, true)>
					<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubheader}, true)>
					<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-size="12pt" font-weight="bold">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
					<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				  	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
					<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block font-size="14pt">
			            	${uiLabelMap.NoOrdersFound}.
			       		 </fo:block>
				</fo:flow>
		    </#if>
			 </fo:page-sequence>
			 
    	  
</fo:root>
</#escape>