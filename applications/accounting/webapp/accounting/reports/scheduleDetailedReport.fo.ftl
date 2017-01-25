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
		<#if finalScheduleWiseMap?has_content>	
		 <#assign scheduleList = finalScheduleWiseMap.entrySet()> 	             		
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
	                           <fo:block text-align="left" font-size="13pt"><fo:external-graphic src="http://localhost:22080//vasista/complogos/aavin_logo.png" content-height="scale-to-fit" scaling="uniform" height="45" width="35"/></fo:block>
	                        </fo:block-container>
					      </fo:table-cell>
					      <fo:table-cell>
					        <fo:block text-align="center" font-weight = "bold"  white-space-collapse="false">  </fo:block>
					      </fo:table-cell>
					     <#--><fo:table-cell>
					        <fo:block text-align="center" font-weight = "bold"  white-space-collapse="false">  </fo:block>
					        <fo:block  keep-together="always" text-align="center" font-size="13pt" font-weight = "bold" white-space-collapse="false" font-family="Courier,monospace" >&#160;                      ${reportHeader.description?if_exists}                   Page-<fo:page-number/> </fo:block>
					        <fo:block  keep-together="always" text-align="center" font-size="12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
					        <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">${reportSecSubHeader.description?if_exists}</fo:block>
					      </fo:table-cell> -->
					    </fo:table-row>
					  </fo:table-body>
					</fo:table>
			    </fo:block>
				<fo:block text-align="center" keep-together="always"  >${reportHeader.description?if_exists} </fo:block>
				<#if parameters.glAccountCategoryTypeId?has_content>
					<#if parameters.glAccountCategoryTypeId == "BS">
               		 <fo:block text-align="center" white-space-collapse="false">&#160;  BALANCE SHEET AS ON 31.03.2016 </fo:block>
                <#elseif parameters.glAccountCategoryTypeId == "MFA">
                     <fo:block text-align="center" white-space-collapse="false">&#160;   MANUFACTURING ACCOUNT FOR THE YEAR ENDED 31.3.2016</fo:block>
                 <#elseif parameters.glAccountCategoryTypeId == "PL">
                     <fo:block text-align="center" white-space-collapse="false">&#160; PROFIT AND LOSS ACCOUNT FOR THE YEAR ENDED 31.3.2016</fo:block>
                  <#elseif parameters.glAccountCategoryTypeId == "TRA">
                     <fo:block text-align="center" white-space-collapse="false">&#160;  TRADING ACCOUNT FOR THE YEAR ENDED 31.3.2016</fo:block>
                   <#elseif parameters.glAccountCategoryTypeId == "COST_CENTER">
                     <fo:block text-align="center" white-space-collapse="false">&#160;   COST CENTER FOR THE YEAR ENDED 31.3.2016</fo:block>
                   </#if>
                    <#if parameters.glAccountCategoryTypeId =="RC">
                    	<fo:block text-align="center" white-space-collapse="false" keep-together="always">&#160;   RECEIPTS AND PAYMENTS</fo:block>
                    </#if>
                 </#if>
                                 
                <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------------------</fo:block>				   	
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
                <fo:block font-family="Courier,monospace">
				   <fo:table>
					  <fo:table-column column-width="250pt"/>
					  <fo:table-column column-width="100pt"/>
					  <fo:table-column column-width="270pt"/>
					  <fo:table-column column-width="270pt"/>
					      <fo:table-body>
						      <fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold">PARTICULARS</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold">SCHEDULE</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMM.yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMM.yyyy")}</fo:block>
									 <#if parameters.glAccountCategoryTypeId =="RC">
										 <fo:block >
										 	<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="center" font-weight="bold">Receipts</fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="right" font-weight="bold">Charges</fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 	</fo:table>
										 </fo:block>
									 </#if>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
	                             	 <fo:block text-align="center" keep-together="always" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevFromDate, "MMM.yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prevThruDate, "MMM.yyyy")}</fo:block>
									<#-- <fo:block text-align="center" keep-together="always" font-weight="bold">${prevsYear}</fo:block>-->
									 <#if parameters.glAccountCategoryTypeId =="RC">
										 <fo:block>
										 	<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="center" font-weight="bold">Receipts</fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="right" font-weight="bold">Charges</fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 	</fo:table>
										 </fo:block>
									 </#if>
								 </fo:table-cell>
							 </fo:table-row>
							  <#if finalScheduleWiseMap?has_content>
							  		<#assign previousYearData=finalScheduleWiseMap.get(prevsYear)?if_exists>
							  		<#assign presentYearData=finalScheduleWiseMap.get(presentYear)?if_exists>
							  		
							  		
							  		<#assign parentList=presentYearData.keySet()>
							  		<#if !( parentList?has_content)>
							  			<#assign parentList=previousYearData.keySet()>
							  		</#if>
							  		<#assign presentRcGrTotPstDebits=0>
							  		<#assign presentRcGrTotPstCredits=0>
							  		<#assign previousRcGrTotPstDebits=0>
							  		<#assign previousRcGrTotPstCredits=0>
							  	<#if parameters.glAccountCategoryTypeId?has_content>
								<#if parameters.glAccountCategoryTypeId != "BS">
								 <#list parentList as eachParent>
								 
								 	<#assign parentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign presentParentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign previousParentDetails=previousYearData.get(eachParent)?if_exists>
								 	
								 	<#if !( parentDetails?has_content)>
							  			<#assign parentDetails=previousYearData.get(eachParent)>
							  		</#if>
							  		<#if parameters.glAccountCategoryTypeId !="RC">
									 <fo:table-row >
								  		<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > <#if parentDetails?has_content>${parentDetails.parentCatName?if_exists}</#if>[${eachParent}]</fo:block>
								 				<#assign childDetails=parentDetails.get("childDetails") >
								 				<#assign presentchildDetails=presentParentDetails.get("childDetails")?if_exists >
								 				<#if previousParentDetails?has_content>
								 				<#assign previouschildDetails=previousParentDetails.get("childDetails")?if_exists >
								 				</#if>
								 				<#assign childList=childDetails.keySet() >
											 	<fo:block text-align="center" keep-together="always">
											 
											 <fo:table>
								  				 <#list childList as eachChild>
						 							 <fo:table-column column-width="250pt"/>
						 							 <fo:table-column column-width="100pt"/>
					  								 <fo:table-column column-width="120pt"/>
					  								 <fo:table-column column-width="120pt"/>
					 							 </#list>
											  <fo:table-body>
											  	<#list childList as eachChild>
						    					  <fo:table-row >
							    					   <fo:table-cell border="solid">
															 <fo:block text-align="left"><#if childDetails.get(eachChild)?has_content>${childDetails.get(eachChild).childCatName?if_exists}<#else></#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="center">${eachChild?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
															 
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
													   </fo:table-cell>
											      </fo:table-row >
											     </#list>
											   </fo:table-body>
											 </fo:table>
											 </fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > ${eachParent}</fo:block>
											 <fo:block text-align="center" keep-together="always"></fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <fo:block text-align="right" font-weight="bold" > <#if presentParentDetails?has_content>${presentParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											  	 <fo:block text-align="right"  font-weight="bold"  keep-together="always"><#if previousParentDetails?has_content>${previousParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
									 </fo:table-row>
									<#else>
										<fo:table-row >
								  		<fo:table-cell border="solid">
											 <#--<fo:block text-align="center" font-weight="bold" > <#if parentDetails?has_content>${parentDetails.parentCatName?if_exists}</#if></fo:block>-->
								 				<#assign childDetails=parentDetails.get("childDetails") >
								 				<#assign presentchildDetails=presentParentDetails.get("childDetails")?if_exists >
								 				<#if previousParentDetails?has_content>
								 				<#assign previouschildDetails=previousParentDetails.get("childDetails")?if_exists >
								 				</#if>
								 				<#assign childList=childDetails.keySet() >
											 	<fo:block text-align="center" keep-together="always">
											 
											 <fo:table>
								  				 <#list childList as eachChild>
						 							 <fo:table-column column-width="250pt"/>
						 							 <fo:table-column column-width="100pt"/>
					  								 <fo:table-column column-width="120pt"/>
					  								  <fo:table-column column-width="150pt"/>
					  								 <fo:table-column column-width="230pt"/>
					 							 </#list>
											  <fo:table-body>
											  	<#list childList as eachChild>
						    					  <fo:table-row >
							    					   <fo:table-cell border="solid">
															 <fo:block text-align="left"><#if childDetails.get(eachChild)?has_content>${childDetails.get(eachChild).childCatName?if_exists}<#else></#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="center">${eachChild?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell >
															 <#--<fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>-->
															 <fo:block>
															 	<fo:table>
															 		 <fo:table-column column-width="120pt"/>
															 		 <fo:table-column column-width="150pt"/>
															 		 <fo:table-body>
															 		 	<fo:table-row>
															 		 		<fo:table-cell border="solid">
															 		 			<fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentPostedCredit?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
															 		 		</fo:table-cell>
															 		 		<fo:table-cell>
															 		 			<fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentPostedDebit?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
															 		 		</fo:table-cell>
															 		 	</fo:table-row>
															 		 </fo:table-body>
															 	</fo:table>
															 </fo:block>
													   </fo:table-cell>
													   <fo:table-cell></fo:table-cell>
													   <fo:table-cell >
															 <#--<fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>-->
													   		 <fo:block>
															 	<fo:table>
															 		 <fo:table-column column-width="120pt"/>
															 		 <fo:table-column column-width="150pt"/>
															 		 <fo:table-body>
															 		 	<fo:table-row>
															 		 		<fo:table-cell border="solid">
															 		 			<fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentPostedCredit?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
															 		 		</fo:table-cell>
															 		 		<fo:table-cell>
															 		 			<fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentPostedDebit?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
															 		 		</fo:table-cell>
															 		 	</fo:table-row>
															 		 </fo:table-body>
															 	</fo:table>
															 </fo:block>
													   </fo:table-cell>
											      </fo:table-row >
											     </#list>
											   </fo:table-body>
											 </fo:table>
											 </fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" ></fo:block>
											 <fo:block text-align="center" keep-together="always"></fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <#--<fo:block text-align="right" font-weight="bold" > <#if presentParentDetails?has_content>${presentParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>-->
											 <fo:block>
										 		<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right"><#if presentParentDetails?has_content><#assign presentRcGrTotPstCredits=presentRcGrTotPstCredits+presentParentDetails.parentCurrentYearPostedCredits>${presentParentDetails.parentCurrentYearPostedCredits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right"><#if presentParentDetails?has_content><#assign presentRcGrTotPstDebits=presentRcGrTotPstDebits+presentParentDetails.parentCurrentYearPostedDebits>${presentParentDetails.parentCurrentYearPostedDebits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 		</fo:table>
										 	</fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											  	<#--<fo:block text-align="right"  font-weight="bold"  keep-together="always"><#if previousParentDetails?has_content>${previousParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>-->
											<fo:block>
										 		<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right"><#if previousParentDetails?has_content><#assign previousRcGrTotPstDebits=previousRcGrTotPstDebits+previousParentDetails.parentCurrentYearPostedCredits>${previousParentDetails.parentCurrentYearPostedCredits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right"><#if previousParentDetails?has_content><#assign previousRcGrTotPstCredits=previousRcGrTotPstCredits+previousParentDetails.parentCurrentYearPostedDebits>${previousParentDetails.parentCurrentYearPostedDebits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 		</fo:table>
										 	</fo:block>
										</fo:table-cell>
									 </fo:table-row> 
									</#if>	 
								</#list>
								<#else>
								<fo:table-row >
			                             <fo:table-cell border="solid">
											 <fo:block text-align="left" keep-together="always" font-weight="bold">****LIABLITIES****</fo:block>
										 </fo:table-cell>
									 </fo:table-row>
									 <#assign preliablitytotal=0>
									 <#assign prvliablitytotal=0>
									 
								 <#list liablityList as eachParent>
								 
								 	<#assign parentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign presentParentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign previousParentDetails=previousYearData.get(eachParent)?if_exists>
								 	
								 	<#if !( parentDetails?has_content)>
							  			<#assign parentDetails=previousYearData.get(eachParent)>
							  		</#if>
							  		  
									 <fo:table-row >
								  		<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > <#if parentDetails?has_content>${parentDetails.parentCatName?if_exists}</#if>[${eachParent}]</fo:block>
								 				<#assign childDetails=parentDetails.get("childDetails") >
								 				<#assign presentchildDetails=presentParentDetails.get("childDetails")?if_exists >
								 				<#if previousParentDetails?has_content>
								 				<#assign previouschildDetails=previousParentDetails.get("childDetails")?if_exists >
								 				</#if>
								 				<#assign childList=childDetails.keySet() >
											 	<fo:block text-align="center" keep-together="always">
											 
											 <fo:table>
								  				 <#list childList as eachChild>
						 							 <fo:table-column column-width="250pt"/>
						 							 <fo:table-column column-width="100pt"/>
					  								 <fo:table-column column-width="120pt"/>
					  								 <fo:table-column column-width="120pt"/>
					 							 </#list>
											  <fo:table-body>
											  	<#list childList as eachChild>
						    					  <fo:table-row >
							    					   <fo:table-cell border="solid">
															 <fo:block text-align="left"><#if childDetails.get(eachChild)?has_content>${childDetails.get(eachChild).childCatName?if_exists}<#else></#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="center">${eachChild?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
													   </fo:table-cell>
											      </fo:table-row >
											     </#list>
											   </fo:table-body>
											 </fo:table>
											 </fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > ${eachParent}</fo:block>
											 <fo:block text-align="center" keep-together="always"></fo:block>
										</fo:table-cell>
										<#assign preliablitytotal=preliablitytotal+presentParentDetails.parentCurrentYearBal?if_exists>
										<fo:table-cell border="solid">
											 <fo:block text-align="right" font-weight="bold" > <#if presentParentDetails?has_content>${presentParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
										<#assign prvliablitytotal=prvliablitytotal+previousParentDetails.parentCurrentYearBal?if_exists>
										
										<fo:table-cell border="solid">
											  	 <fo:block text-align="right"  font-weight="bold"  keep-together="always"><#if previousParentDetails?has_content>${previousParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
									 </fo:table-row>
									 
								</#list>
								<fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left"  font-weight="bold">LIABLITIES TOTAL</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${preliablitytotal?if_exists}</fo:block>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${prvliablitytotal?if_exists}</fo:block>
								 </fo:table-cell>
								 </fo:table-row>
								<fo:table-row >
			                             <fo:table-cell border="solid">
											 <fo:block text-align="left" font-weight="bold" keep-together="always">****ASSETS****</fo:block>
										 </fo:table-cell>
									 </fo:table-row>
									 <#assign preasserttotal=0>
									 <#assign prvasserttotal=0>
								 <#list AssertsList as eachParent>
								 
								 	<#assign parentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign presentParentDetails=presentYearData.get(eachParent)?if_exists>
								 	<#assign previousParentDetails=previousYearData.get(eachParent)?if_exists>
								 	
								 	<#if !( parentDetails?has_content)>
							  			<#assign parentDetails=previousYearData.get(eachParent)>
							  		</#if>
							  		
									 <fo:table-row >
								  		<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > <#if parentDetails?has_content>${parentDetails.parentCatName?if_exists}</#if>[${eachParent}]</fo:block>
								 				<#assign childDetails=parentDetails.get("childDetails") >
								 				<#assign presentchildDetails=presentParentDetails.get("childDetails")?if_exists >
								 				<#if previousParentDetails?has_content>
								 				<#assign previouschildDetails=previousParentDetails.get("childDetails")?if_exists >
								 				</#if>
								 				<#assign childList=childDetails.keySet() >
											 	<fo:block text-align="center" keep-together="always">
											 
											 <fo:table>
								  				 <#list childList as eachChild>
						 							 <fo:table-column column-width="250pt"/>
						 							 <fo:table-column column-width="100pt"/>
					  								 <fo:table-column column-width="120pt"/>
					  								 <fo:table-column column-width="120pt"/>
					 							 </#list>
											  <fo:table-body>
											  	<#list childList as eachChild>
						    					  <fo:table-row >
							    					   <fo:table-cell border="solid">
															 <fo:block text-align="left"><#if childDetails.get(eachChild)?has_content>${childDetails.get(eachChild).childCatName?if_exists}<#else></#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="center">${eachChild?if_exists}</fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if presentchildDetails?has_content><#if presentchildDetails.get(eachChild)?has_content>${presentchildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
													   </fo:table-cell>
													   <fo:table-cell border="solid">
															 <fo:block text-align="right"><#if previouschildDetails?has_content><#if previouschildDetails.get(eachChild)?has_content>${previouschildDetails.get(eachChild).childCurrentYearBal?if_exists?string("##0.00")}<#else>0.00</#if><#else>0.00</#if></fo:block>
													   </fo:table-cell>
											      </fo:table-row >
											     </#list>
											   </fo:table-body>
											 </fo:table>
											 </fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid">
											 <fo:block text-align="center" font-weight="bold" > ${eachParent}</fo:block>
											 <fo:block text-align="center" keep-together="always"></fo:block>
										</fo:table-cell>
											<#assign preasserttotal=preasserttotal+presentParentDetails.parentCurrentYearBal?if_exists>
										<fo:table-cell border="solid">
											 <fo:block text-align="right" font-weight="bold" > <#if presentParentDetails?has_content>${presentParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
											<#assign prvasserttotal=prvasserttotal+previousParentDetails.parentCurrentYearBal?if_exists>
										<fo:table-cell border="solid">
											  	 <fo:block text-align="right"  font-weight="bold"  keep-together="always"><#if previousParentDetails?has_content>${previousParentDetails.parentCurrentYearBal?string("##0.00")}<#else>0.00</#if></fo:block>
										</fo:table-cell>
									 </fo:table-row>
								</#list>
								<fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold">ASSETS TOTAL</fo:block>
								 </fo:table-cell>
								 
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${preasserttotal?if_exists}</fo:block>
								 </fo:table-cell>
								 
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${prvasserttotal?if_exists}</fo:block>
								 </fo:table-cell>
								 </fo:table-row>
								</#if>
								</#if>
								<#if grandTotal?has_content>
								
								<#assign presenttotal= grandTotal.get(presentYear)>
								<#assign prvtotal= grandTotal.get(prevsYear)>
								<#if parameters.glAccountCategoryTypeId?has_content>
								<#if parameters.glAccountCategoryTypeId == "MFA">
								<fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTAL</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${presenttotal?if_exists}</fo:block>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${prvtotal?if_exists}</fo:block>
								 </fo:table-cell>
								 </fo:table-row>
									
								<fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold">BY VALUE TRANSFERRED TO TRADING A/c</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${presenttotal?if_exists}</fo:block>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${prvtotal?if_exists}</fo:block>
								 </fo:table-cell>
							 </fo:table-row>
							 <#elseif parameters.glAccountCategoryTypeId == "TRA">
							 
							 
							 <fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold">TO VALUE FROM MANUFACTUING A/c</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">1095293404.49</fo:block>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">0.00</fo:block>
								 </fo:table-cell>
							 </fo:table-row>
							 <#assign loss=presenttotal-prvtotal>
								<fo:table-row>
									<fo:table-cell border="solid">
										 <fo:block text-align="left" keep-together="always" font-weight="bold">GROSS LOSS TO PL A/c</fo:block>
									 </fo:table-cell>
									 <fo:table-cell border="solid">
										 <fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
									 </fo:table-cell>
									 <fo:table-cell border="solid">
										 <fo:block text-align="right" keep-together="always" font-weight="bold">${loss?if_exists}</fo:block>
									 </fo:table-cell>
		                             <fo:table-cell border="solid">
										 <fo:block text-align="right" keep-together="always" font-weight="bold">0</fo:block>
									 </fo:table-cell>
								 </fo:table-row>
							 	 </#if>
								 
							 </#if>
							 <#if parameters.glAccountCategoryTypeId !="RC">
								<fo:table-row >
					             <fo:table-cell border="solid">
									 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTAL</fo:block>
								 </fo:table-cell>
								 <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${presenttotal?if_exists}</fo:block>
								 </fo:table-cell>
	                             <fo:table-cell border="solid">
									 <fo:block text-align="right" keep-together="always" font-weight="bold">${prvtotal?if_exists}</fo:block>
								 </fo:table-cell>
							 </fo:table-row>
							 <#else>
							 	<fo:table-row >
						             <fo:table-cell border="solid">
										 <fo:block text-align="center" keep-together="always" font-weight="bold"></fo:block>
									 </fo:table-cell>
									 <fo:table-cell border="solid">
										 <fo:block text-align="left" keep-together="always" font-weight="bold">TOTAL</fo:block>
									 </fo:table-cell>
									 <fo:table-cell border="solid">
										 <#--<fo:block text-align="right" keep-together="always" font-weight="bold">${presenttotal?if_exists}</fo:block>-->
										 <fo:block>
										 		<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="right" font-weight="bold"><#if presentRcGrTotPstCredits &gt; 0>${presentRcGrTotPstCredits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right" font-weight="bold"><#if presentRcGrTotPstDebits &gt; 0>${presentRcGrTotPstDebits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 		</fo:table>
										 	</fo:block>
										 
									 </fo:table-cell>
		                             <fo:table-cell border="solid">
										 <#--<fo:block text-align="right" keep-together="always" font-weight="bold">${prvtotal?if_exists}</fo:block>-->
										 <fo:block>
										 		<fo:table>
										 		 <fo:table-column column-width="120pt"/>
										 		 <fo:table-column column-width="150pt"/>
										 		 <fo:table-body>
										 		 	<fo:table-row>
										 		 		<fo:table-cell border="solid">
										 		 			<fo:block text-align="right" font-weight="bold"><#if previousRcGrTotPstDebits &gt; 0>${previousRcGrTotPstDebits}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 		<fo:table-cell>
										 		 			<fo:block text-align="right" font-weight="bold"><#if previousRcGrTotPstCredits &gt; 0>${previousRcGrTotPstCredits?string("##0.00")}<#else>0.00</#if></fo:block>
										 		 		</fo:table-cell>
										 		 	</fo:table-row>
										 		 </fo:table-body>
										 		</fo:table>
										 	</fo:block>
									 </fo:table-cell>
							 	</fo:table-row>
							  </#if>
							 </#if>
							</#if> 
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