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
					 margin-left="0.5in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.8in" >
				<fo:region-body margin-top="1.7in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if assetsGrandTotal?has_content || liablitiesChildWiseMap?has_content>	
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<#assign reportHeader ={}>
				<#if parameters.organizationPartyId !="Company">
				<#assign reportHeader= delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "${parameters.organizationPartyId}_HEADER"}, true)>
				<#else>
				<#assign reportHeader= delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				</#if>
	      		<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<#assign reportSecSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable_01"}, true)>
				<fo:block font-family="Courier,monospace">
				   <fo:table>
					 <fo:table-column column-width="20%"/>
					 <fo:table-column column-width="20%"/>
		      		 <fo:table-column column-width="20%"/>
					  <fo:table-body>
					    <fo:table-row>
					      <fo:table-cell>
					        <fo:block-container >
	                           <fo:block text-align="left" font-size="13pt"><fo:external-graphic src="<@ofbizContentUrl>/vasista/complogos/logo_nhdc.png</@ofbizContentUrl>" content-height="scale-to-fit" scaling="uniform" height="50" width="45"/></fo:block>
	                        </fo:block-container>
					      </fo:table-cell>
					      <fo:table-cell>
					        <fo:block text-align="center" font-weight = "bold"  white-space-collapse="false">  </fo:block>
					      </fo:table-cell>
					    </fo:table-row>
					  </fo:table-body>
					</fo:table>
			    </fo:block>
				<fo:block text-align="center" keep-together="always"  >${reportHeader.description?if_exists} </fo:block>
				<#if parameters.glAccountCategoryTypeId?has_content>
					<#if parameters.glAccountCategoryTypeId == "BS">
					<fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
               		 <fo:block text-align="center" white-space-collapse="false">&#160;  BALANCE SHEET AS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                <#elseif parameters.glAccountCategoryTypeId == "MFA">
                	<fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
                     <fo:block text-align="center" white-space-collapse="false">&#160;   MANUFACTURING ACCOUNT FOR THE YEAR ENDED ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                 <#elseif parameters.glAccountCategoryTypeId == "PL">
                     <fo:block text-align="center" white-space-collapse="false">&#160; PROFIT AND LOSS ACCOUNT FOR THE YEAR ENDED 31.3.2016</fo:block>
                  <#elseif parameters.glAccountCategoryTypeId == "TRA">
                  <fo:block text-align="center" white-space-collapse="false">&#160;   FINAL AUDIT FOR THE YEAR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
                     <fo:block text-align="center" white-space-collapse="false">&#160;  TRADING ACCOUNT FOR THE YEAR ENDED ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd.MM.yyyy")}</fo:block>
                   <#elseif parameters.glAccountCategoryTypeId == "COST_CENTER">
                     <fo:block text-align="center" white-space-collapse="false">&#160;   COST CENTER FOR THE YEAR ENDED 31.3.2016</fo:block>
                   </#if>
                    <#if parameters.glAccountCategoryTypeId =="RC">
                    	<fo:block text-align="center" white-space-collapse="false" keep-together="always">&#160;   RECEIPTS AND PAYMENTS</fo:block>
                    </#if>
                 </#if>
                                 
                <fo:block text-align="left" keep-together="always"  >&#160;-------------------------------------------------------------------------------------------------------------------------------</fo:block>				   	
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
                <fo:block font-family="Courier,monospace" font-size="11pt">
                 <fo:table>
					  <fo:table-column column-width="505pt"/>
					  <fo:table-column column-width="505pt"/>
					      <fo:table-body>
						      <fo:table-row >
						      	  <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">PARTICULARS</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevFromDate, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevThruDate, "yy")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											 <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">PARTICULARS</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "yy")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevFromDate, "yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevThruDate, "yy")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				 <fo:table-row >
						      	  <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											  <#assign liablitiesCategoryList = liablitiesChildWiseMap.entrySet()>
											      <fo:table-body>
											      <#assign categoryId ="">
											      <#assign parentCategoryId ="">
												     <#list liablitiesCategoryList as category>
													 <#assign profitValues=category.getValue()>
													 <#assign presentYearVal=0>
													 <#if profitValues.get(presentYear)?has_content>
													 <#assign presentYearVal=profitValues.get(presentYear)?if_exists>
													 </#if>
													 <#assign prevsYearVal=0>
													 <#if profitValues.get(prevsYear)?has_content>
													 <#assign prevsYearVal=profitValues.get(prevsYear)?if_exists>
													 </#if>
													 <#assign glCategry ={} >
													 <#assign glCategry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",category.getKey(),"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
													 <#assign glCategry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",glCategry.parentCategoryId,"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
													 <#if categoryId=="" || categoryId!=glCategry.glAccountCategoryId>
													 <#assign categoryId=glCategry.glAccountCategoryId>
													 <#if glCategry.parentCategoryId?has_content && (parentCategoryId="" || parentCategoryId!=glCategry.parentCategoryId)>
													 	<#assign parentGlCatgry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",glCategry.parentCategoryId,"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
													 	<#assign parentCategoryId= parentGlCatgry.glAccountCategoryId>
													 	<fo:table-row >
											             <fo:table-cell border="solid" >
															 <fo:block text-align="left"  font-weight="bold">${parentGlCatgry.description?if_exists}</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" >
															 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
													 </#if>
													 <fo:table-row >
											             <fo:table-cell border="solid" >
															 <fo:block text-align="left"  font-weight="bold">${glCategry.description?if_exists}</fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid" >
															 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
													 </#if>
												 	<fo:table-row >
											             <fo:table-cell border="solid" >
															 <fo:block text-align="left" font-weight="bold">${profitValues.get("scheduleNo")} - ${profitValues.get("childCatName")}</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearVal?if_exists?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevsYearVal?if_exists?string("#0.00")}&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
												 	<#if GlAccountIdsWiseMap?has_content && GlAccountIdsWiseMap.get(category.getKey())?has_content>
												 	<#assign GlAccountIdsWise = {}>
												 	<#assign GlAccountIdsWise = GlAccountIdsWiseMap.get(category.getKey())>
												 	<#assign glAccountIdWisDetails =GlAccountIdsWise.entrySet() >
												 	<#list glAccountIdWisDetails as glAccountDetails>
												 	<#assign glAccountIdWise=glAccountDetails.getValue()>
												 	<#assign presentYrGlIdVal=0>
												 	<#assign prevYrGlIdVal=0>
												 	<#if glAccountIdWise.get(presentYear)?has_content >
												 		<#assign presentYrGlIdVal=glAccountIdWise.get(presentYear)>
												 	</#if>
												 	<#if glAccountIdWise.get(prevsYear)?has_content >
												 	<#assign prevYrGlIdVal=glAccountIdWise.get(prevsYear)>
												 	</#if>
												 	<fo:table-row >
											             <fo:table-cell border="solid" >
															 <fo:block text-align="left"  >${glAccountIdWise.get("glAccountId")} - ${glAccountIdWise.get("description")} </fo:block>
														 </fo:table-cell>
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" >${presentYrGlIdVal?if_exists?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" >${prevYrGlIdVal?if_exists?string("#0.00")}&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
												 	</#list>
												 	</#if>
												 	</#list>
												 	<#if GrandTotal?has_content>
												 	<#assign presentYearProfitVal=0>
													 <#assign presentYearProfitVal=GrandTotal.get(presentYear)?if_exists>
													 <#assign prevsYearProfitVal=0>
													 <#if GrandTotal.get(prevsYear)?has_content>
													 <#assign prevsYearProfitVal=GrandTotal.get(prevsYear)?if_exists></#if>
												 	<fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TO VALUE FROM MANUFATURING A/C.</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearProfitVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevsYearProfitVal?if_exists?string("#0.00")}&#160;</fo:block>
														 </fo:table-cell>
												 	</fo:table-row>
												 	</#if>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											 <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											      <fo:table-body>
											            <#assign assetsCategoryList= assetsChildWiseMap.entrySet()>
											            <#assign categoryId ="">
											            <#assign parentCategoryId ="">
											             <#list assetsCategoryList as category>
														 <#assign values=category.getValue()>
														 <#assign presentYearVal=0>
														 <#if values.get(presentYear)?has_content>
														 <#assign presentYearVal=values.get(presentYear)?if_exists>
														 </#if>
														 <#assign prevsYearVal=0>
														 <#if values.get(prevsYear)?has_content>
														 <#assign prevsYearVal=values.get(prevsYear)?if_exists>
														 </#if>
														 <#assign glCategry = {}>
														 <#assign glCategry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",category.getKey(),"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
														 <#assign glCategry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",glCategry.parentCategoryId,"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
														 <#if categoryId=="" || categoryId!=glCategry.glAccountCategoryId>
														 <#assign categoryId=glCategry.glAccountCategoryId>
														  <#if glCategry.parentCategoryId?has_content && (parentCategoryId="" || parentCategoryId!=glCategry.parentCategoryId)>
														 	<#assign parentGlCatgry = delegator.findOne("GlAccountCategory",{"glAccountCategoryId",glCategry.parentCategoryId,"glAccountCategoryTypeId",parameters.glAccountCategoryTypeId},false)>
														 	<#assign parentCategoryId= parentGlCatgry.glAccountCategoryId>
														 	<fo:table-row >
												             <fo:table-cell border="solid" >
																 <fo:block text-align="left"  font-weight="bold">${parentGlCatgry.description?if_exists}</fo:block>
															 </fo:table-cell>
															 <fo:table-cell border="solid" >
																 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
															 </fo:table-cell>
								                             <fo:table-cell border="solid">
								                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">&#160;</fo:block>
															 </fo:table-cell>
													 	</fo:table-row>
														 </#if>
														 <fo:table-row >
												             <fo:table-cell border="solid" >
																 <fo:block text-align="left"  font-weight="bold">${glCategry.description?if_exists}</fo:block>
															 </fo:table-cell>
															 <fo:table-cell border="solid" >
																 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
															 </fo:table-cell>
								                             <fo:table-cell border="solid">
								                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">&#160;</fo:block>
															 </fo:table-cell>
													 	</fo:table-row>
														 </#if>
													 	<fo:table-row>
												             <fo:table-cell border="solid" >
																 <fo:block text-align="left"  font-weight="bold">${values.get("scheduleNo")} - ${values.get("childCatName")}</fo:block>
															 </fo:table-cell>
															 
															 <fo:table-cell border="solid">
																 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearVal?if_exists?string("#0.00")}</fo:block>
															 </fo:table-cell>
								                             <fo:table-cell border="solid">
								                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevsYearVal?if_exists?string("#0.00")}</fo:block>
															 </fo:table-cell>
													 	</fo:table-row>
													 	<#if GlAccountIdsWiseMap?has_content && GlAccountIdsWiseMap.get(category.getKey())?has_content>
													 	<#assign GlAccountIdsWise = {}>
													 	<#assign GlAccountIdsWise = GlAccountIdsWiseMap.get(category.getKey())>
													 	<#assign glAccountIdWisDetails =GlAccountIdsWise.entrySet() >
													 	<#list glAccountIdWisDetails as glAccountDetails>
													 	<#assign glAccountIdWise=glAccountDetails.getValue()>
													 	<#assign presentYrGlIdVal=0>
													 	<#assign prevYrGlIdVal=0>
													 	<#if glAccountIdWise.get(presentYear)?has_content >
													 		<#assign presentYrGlIdVal=glAccountIdWise.get(presentYear)>
													 	</#if>
													 	<#if glAccountIdWise.get(prevsYear)?has_content >
													 	<#assign prevYrGlIdVal=glAccountIdWise.get(prevsYear)>
													 	</#if>
													 	<fo:table-row >
												             <fo:table-cell border="solid" >
																 <fo:block text-align="left"  >${glAccountIdWise.get("glAccountId")} - ${glAccountIdWise.get("description")} </fo:block>
															 </fo:table-cell>
															 <fo:table-cell border="solid">
																 <fo:block text-align="right" keep-together="always" >${presentYrGlIdVal?if_exists?string("#0.00")}</fo:block>
															 </fo:table-cell>
								                             <fo:table-cell border="solid">
								                             	 <fo:block text-align="right" keep-together="always" >${prevYrGlIdVal?if_exists?string("#0.00")}</fo:block>
															 </fo:table-cell>
													 	</fo:table-row>
													 	</#list>
													 	</#if>
													 	</#list>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
			    				</fo:table-row>
			    				<#assign presentYearLiablityTotVal=0>
			    				<#assign presentYearAssetsTotVal=0>
			    				<#assign prevYearLiablityTotVal=0>
			    				<#assign prevYearAssetsTotVal=0>
			    				 <#if GrandLiablitesTotal.get(presentYear)?has_content>
			    					<#assign presentYearLiablityTotVal=GrandLiablitesTotal.get(presentYear)?if_exists>
			    				</#if>
								 <#if GrandLiablitesTotal.get(prevsYear)?has_content>
								 	<#assign prevYearLiablityTotVal=GrandLiablitesTotal.get(prevsYear)?if_exists>
								 </#if>
								 <#if GrandassetsTotal.get(presentYear)?has_content>
			    					<#assign presentYearAssetsTotVal=GrandassetsTotal.get(presentYear)?if_exists>
			    				</#if>
								 <#if GrandassetsTotal.get(prevsYear)?has_content>
								 	<#assign prevYearAssetsTotVal=GrandassetsTotal.get(prevsYear)?if_exists>
								 </#if>
			    				<fo:table-row>
			    					<fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											  <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTALS</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearLiablityTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevYearLiablityTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
								     </fo:table-cell>
								      <fo:table-cell border="solid">
							      	  <fo:block>
										   <fo:table>
											 <fo:table-column column-width="249pt"/>
											  
											  <fo:table-column column-width="128pt"/>
											  <fo:table-column column-width="128pt"/>
											      <fo:table-body>
												      <fo:table-row >
											             <fo:table-cell border="solid" border-right-style="hidden">
															 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTALS</fo:block>
														 </fo:table-cell>
														 
														 <fo:table-cell border="solid">
															 <fo:block text-align="right" keep-together="always" font-weight="bold">${presentYearAssetsTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
							                             <fo:table-cell border="solid">
							                             	 <fo:block text-align="right" keep-together="always" font-weight="bold">${prevYearAssetsTotVal?string("#0.00")}</fo:block>
														 </fo:table-cell>
													 </fo:table-row>
											     </fo:table-body>
										     </fo:table>
									     </fo:block>
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
       		 		<fo:block font-size="14pt" text-align="center">
            			 No Records Found....!
       		 		</fo:block>
    			</fo:flow>
	  </fo:page-sequence>	
	</#if>	
</fo:root>
</#escape>	 	    