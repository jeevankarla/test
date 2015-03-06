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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "EmployeeAdvancesAndSubScheduleReport.pdf")}
 <#if finAccountTypeIdsMap?has_content>
	<#assign finAccountTypeId=finAccountTypeIdsMap.entrySet()> 
	<#list finAccountTypeId as finAccntId>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            		<#assign finAccountType = delegator.findOne("FinAccountType", {"finAccountTypeId" :finAccntId.getKey()}, true)>
                    <#assign finAccountTypeglAccnt = delegator.findOne("FinAccountTypeGlAccount", {"finAccountTypeId" :finAccntId.getKey(),"organizationPartyId":"Company"}, true)>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                                                                                                               Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                   MOTHER DAIRY, YALAHANKA KMF UNIT : GKVK POST.BANGALORE-560 065                              Page No:<fo:page-number/></fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;STATEMENT FOR ${finAccountTypeglAccnt.glAccountId?if_exists} - ${finAccountType.description?if_exists?upper_case}</fo:block>
              		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="30%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">DATE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">SL CODE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">SL DESCRIPTION</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">DEBIT</fo:block>  
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
				<#list finAccntValues as finAccntValue>
				<#assign grandOpenBalDebit=grandOpenBalDebit+finAccntValue.get("currentDebit")>
				<#assign grandOpenBalCredit=grandOpenBalCredit+finAccntValue.get("currentCredit")>
               <fo:block>
                    <fo:table>
				    <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="30%"/>
			        <fo:table-column column-width="4%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("transactionDate")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("partyId")?if_exists}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("Name")?if_exists?upper_case}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("currentDebit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${finAccntValue.get("currentCredit")?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 	
				</#list>	
			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
                    <fo:table>
				    <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="30%"/>
			        <fo:table-column column-width="4%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
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
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
			<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>

			 </fo:flow>
			 </fo:page-sequence>
			 </#list>	
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