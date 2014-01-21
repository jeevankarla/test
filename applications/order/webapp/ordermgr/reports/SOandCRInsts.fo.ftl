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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".5in"/>
        <fo:region-before extent=".5in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if masterList?has_content>
<#list masterList as SOandCRReportEntry>
<#assign SOandCRReportEntries = (SOandCRReportEntry).entrySet()>
<#if SOandCRReportEntries?has_content>
<#list SOandCRReportEntries as tempSOandCRReportEntrie>
<#assign SOandCRReportList=tempSOandCRReportEntrie.getValue() >
<#if SOandCRReportList?has_content>
	<#list SOandCRReportList as SOandCRReport>
		<#assign facilityId = SOandCRReport.get("facilityId")>			                      	                     	
        <#assign facility = delegator.findOne("Facility", {"facilityId" : facilityId}, true)>      
		<fo:page-sequence master-reference="main" >
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                        ${reportHeader.description?if_exists}</fo:block>
				<#if categoryTypeEnum != "CR_INST">
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">ZNRT:${facility.parentFacilityId?if_exists}                                         Spl.Order supply to institution for the month of  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMMM-yyyy")}      Bill No:<fo:page-number/></fo:block>				    		
              	 <#else>
              	 <fo:block text-align="left" keep-together="always" white-space-collapse="false">ZNRT:${facility.parentFacilityId?if_exists}                                         Milk Credit Sales Bill for the month of  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMMM-yyyy")}          Bill No:<fo:page-number/></fo:block>
              	 </#if>
              	 <fo:block  white-space-collapse="false"  keep-together="always">Booth No. :${facilityId?if_exists}                               Booth Name:${facility.facilityName?if_exists}                  Agent Name :${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,facility.ownerPartyId,true)}</fo:block>				 		
            </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				<fo:block keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block  font-size="10pt">
            			<fo:table width="100%" table-layout="fixed">
            				 		<fo:table-column column-width="100%"/>
            				 		<fo:table-body>
			                		<fo:table-row column-width="100%">
			                		<fo:table-cell column-width="100%">
            				 		<fo:table width="100%" table-layout="fixed">                
				                		<fo:table-column column-width="75pt"/>
				                		<#assign ListSize=(productList.size())>
				                		<#list 0 .. ListSize-1 as product>
				                		<fo:table-column column-width="45pt"/>
				                		</#list>
				                		<fo:table-column column-width="40pt"/>
				                		<fo:table-header>
			                    		<fo:table-row>
			                    			<fo:table-cell><fo:block >${uiLabelMap.Day}</fo:block></fo:table-cell>
			                    			<#list productList as product>
			                    			<fo:table-cell><fo:block keep-together="always" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block></fo:table-cell>			                    	
			                    			</#list>
			                    			<fo:table-cell keep-together="always"><fo:block>Total Amount</fo:block></fo:table-cell>
			                    		</fo:table-row>
			                    		<fo:table-row>                        
			                        		<fo:table-cell column-width="100%"><fo:block>------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			                    		</fo:table-row>				                     		                     
			                		</fo:table-header>	                   
                			<fo:table-body>
                					<#assign productEntries = (SOandCRReport).entrySet()>						                      	
                      				<#if productEntries?has_content>                      				
                      			<fo:table-row width="100%">                            
                            		<fo:table-cell>
                              			<fo:block>
                                			<#list productEntries as productEntry>
                      							<#if productEntry.getKey()?has_content>
                      							<#if productEntry.getKey() != "facilityId">
		                                			<fo:table width="100%" space-after="0.0in">
		                                				<fo:table-column column-width="40pt"/>
		                                				<fo:table-column column-width="60pt"/>
			             						  		<#list 0 .. ListSize-1 as product>
			             						 		<fo:table-column column-width="45pt"/>
			             						 		</#list>
			             						 		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-body>
			              								<#if productEntry.getKey()=="Tot">
				              								<fo:table-row >
			            										<fo:table-cell column-width="100%">
			            											<fo:block keep-together="always">------------------------------------------------------------------------------------------------------------------------------</fo:block>
			            										</fo:table-cell>
			            									</fo:table-row>	
			            								</#if> 
			            									<#assign typeEntrieValues = (productEntry.getValue())>
			            									<#assign amount = typeEntrieValues.get("TOTALAMOUNT")>
						                      			 	<#if amount != 0>                   						 
				              								<fo:table-row width="100%">				              									
				              									<fo:table-cell >
				              							 			<#if productEntry.getKey() !="Tot">				              							 			 
				              							 				<#assign date = typeEntrieValues.get("supplyDate")>
				              									        <fo:block keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(date, "dd/MM/yyyy")}</fo:block>
								                         			<#else>
								                         				<fo:block keep-together="always">Total</fo:block>
								                         			</#if>
								                         		</fo:table-cell>                          
						                      			 		 <#assign typeEntries = typeEntrieValues.entrySet()>						                      			 		                   	
							                      			 		<#list typeEntries as typeEntry>
							                      			 			<#if typeEntry.getKey() !="supplyDate">                      				
							                      						<fo:table-cell >
									                                		<fo:block text-align="right">${typeEntry.getValue()}</fo:block>
									                            		</fo:table-cell>
									                            		</#if>
									                        		</#list>								                        		
						                         			</fo:table-row>
						                         			</#if>								                        
					                         			</fo:table-body>
				                         			</fo:table>
				                         			</#if> 	                          
                       				 				</#if>
                      						 	</#list>             
                                		</fo:block>
                            		</fo:table-cell>	                           
                       			</fo:table-row>
                      			</#if>                  	  		            			
            				</fo:table-body>
        				</fo:table>
      	  		</fo:table-cell>
     	 		</fo:table-row>	
					</fo:table-body>
	 			</fo:table>	
			</fo:block>
			<fo:block keep-together="always"  line-height="15pt"  white-space-collapse="false" font-size="10pt">------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:flow>	
 		</fo:page-sequence>
 	    </#list>
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>
   </#list>
  </#if>
 </#list>
</#if>						
</fo:root>
</#escape>