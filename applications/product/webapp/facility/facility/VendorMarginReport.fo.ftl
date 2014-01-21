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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent=".05in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if errorMessage?exists>
	<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${errorMessage}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
	<#else>
		<#if masterList?has_content>
		<#list masterList as vendorMarginReportEntry>
		<#assign vendorMarginReportEntries = (vendorMarginReportEntry).entrySet()>
		<#if vendorMarginReportEntries?has_content>
		<#list vendorMarginReportEntries as tempVendorMarginReportEntrie>
		<#assign vendorMarginReportList=tempVendorMarginReportEntrie.getValue() >
		<#if vendorMarginReportList?has_content>
			<#list vendorMarginReportList as vendorMarginReport>
				<#assign facilityId = vendorMarginReport.get("facilityId")>			                      	                     	
		        <#assign facility = delegator.findOne("Facility", {"facilityId" : facilityId}, true)>
		        <#assign totalQty=Static["java.lang.Math"].round(vendorMarginReport.Tot.get("TOTAL"))>
		        <#assign cashDue =(Static["java.math.BigDecimal"].ZERO)>
		        <#if  (totalQty != (Static["java.math.BigDecimal"].ZERO))>    
				<fo:page-sequence master-reference="main" >
					<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
						<fo:block text-align="center" keep-together="always"> ${uiLabelMap.ApDairyMsg}</fo:block>
						<fo:block text-align="center" keep-together="always"> ${uiLabelMap.VendorWiseMarginMsg} ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMMM-yyyy")}</fo:block>				    		
		              	 <fo:block line-height="15pt" white-space-collapse="false"  keep-together="always">
		                         ZONE&amp;RT: ${facility.parentFacilityId?if_exists}                                 LOCATION: ${facility.description?if_exists}
		              	  </fo:block>		
		              	 <fo:block line-height="10pt" white-space-collapse="false"  keep-together="always">
		              	 		VENDORID: ${vendorMarginReport.facilityId?if_exists}                        						NAME :      ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facility.ownerPartyId, false)}		
		              	  </fo:block>           	      	 	  
		        	</fo:static-content>
					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
						<fo:block keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>
						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt"> 
		            	 		&lt;-------Total Quantity Dispatched------------&gt;     &lt;-----------Vendor Margin on--------&gt; 
		            	 </fo:block>									
									<fo:block  font-size="10pt">
		            					<fo:table width="100%" table-layout="fixed">
		            				 		<fo:table-column column-width="100%"/>
		            				 		<fo:table-body>
					                		<fo:table-row column-width="100%">
					                		<fo:table-cell column-width="100%">
		            				 		<fo:table width="100%" table-layout="fixed">                
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>	
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>	
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>
						                		<fo:table-column column-width="50pt"/>        
					                	 		<fo:table-header>
					                    		<fo:table-row>			                    	
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell>                       
					                        		<fo:table-cell><fo:block >${uiLabelMap.Day}</fo:block></fo:table-cell>              
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeCard}</fo:block></fo:table-cell>			                                          
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeCash}</fo:block></fo:table-cell>			                                           
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeSpecialOrder}</fo:block></fo:table-cell>			                                            
					                        		<fo:table-cell><fo:block >${uiLabelMap.CommonTotal}</fo:block></fo:table-cell>			                                            
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeCard}</fo:block></fo:table-cell>                                          
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeCash}</fo:block></fo:table-cell>                                          
					                        		<fo:table-cell><fo:block >${uiLabelMap.TypeSpecialOrder}</fo:block></fo:table-cell>
					                        		 <fo:table-cell keep-together="always"><fo:block>Total Margin</fo:block></fo:table-cell>
					                    		</fo:table-row>
					                    		<fo:table-row>                        
					                        		<fo:table-cell column-width="100%"><fo:block>------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell> 
					                    			<fo:table-cell><fo:block ></fo:block></fo:table-cell>			                    	
					                    		</fo:table-row>				                     		                     
					                		</fo:table-header>	                   
		                					<fo:table-body>
		                					<#assign productEntries = (vendorMarginReport).entrySet()>						                      	
		                      				<#if productEntries?has_content>                      				
		                      			<fo:table-row width="100%">                            
		                            		<fo:table-cell>
		                                		<fo:block></fo:block>
		                            		</fo:table-cell>
		                            		<fo:table-cell>
		                              		<fo:block>
		                                			<#list productEntries as productEntry>
		                      				<#if productEntry.getKey()?has_content>
		                      				<#if productEntry.getKey() != "facilityId">
				                                			<fo:table width="100%" space-after="0.0in">
					             						 		<fo:table-column column-width="50pt"/>
					             						  		<fo:table-column column-width="25pt"/>
					             						   		<fo:table-column column-width="45pt"/>
				             						    		<fo:table-column column-width="45pt"/>
				             						     		<fo:table-column column-width="60pt"/>
				             						      		<fo:table-column column-width="60pt"/>
				             						       		<fo:table-column column-width="55pt"/>
				             						        	<fo:table-column column-width="30pt"/>
				             						        	<fo:table-column column-width="65pt"/>					                 						                        						         
					              								<fo:table-body>
					              								<#if productEntry.getKey()=="Tot">
						              								<fo:table-row >
					            										<fo:table-cell column-width="100%">
					            											<fo:block keep-together="always">-------------------------------------------------------------------------------------------------------------------</fo:block>
					            										</fo:table-cell>
					            									</fo:table-row>	
					            									</#if>                  						 
						              								<fo:table-row width="100%">
						              							 		<fo:table-cell >
										                                		<fo:block >								                                			
										                                				${productEntry.getKey()}								                                			
										                                		</fo:block>
										                         		</fo:table-cell>                          
								                      			 		<#assign typeEntries = (productEntry.getValue()).entrySet()>                      	
								                      			 		<#list typeEntries as typeEntry>
								                      			 			<#if (typeEntry.getKey() != "CASH_DUE")>                      				
									                      						<fo:table-cell >
											                                		<fo:block text-align="right">
											                                		<#if (typeEntry.getValue() != (Static["java.math.BigDecimal"].ZERO))>
											                                			${typeEntry.getValue().toEngineeringString()}
											                                			<#else>
											                                				${typeEntry.getValue()}
											                                		</#if>
											                                		</fo:block>
											                            		</fo:table-cell>
										                            		</#if>
										                        		</#list>
								                         		</fo:table-row>								                        
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
						<#assign total=Static["java.lang.Math"].round(vendorMarginReport.Tot.get("TOTAL_MR"))>
						<#assign cashDue=(vendorMarginReport.Tot.get("CASH_DUE")) />
						<#assign NetMargin = total - cashDue>
						<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">
						.       Rounded Rs.                                                       ${total?if_exists}.00
						</fo:block>				
						<#if (cashDue != (Static["java.math.BigDecimal"].ZERO))>
							<#assign NetMargin = total - cashDue>
							<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">
						.       CASH DUES:                                                        ${cashDue?if_exists}.00
							</fo:block>
							<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">
						.       NET PAYABLE:                                                      ${NetMargin?if_exists}.00
							</fo:block>
						</#if>	
						<fo:block keep-together="always"  line-height="15pt"  white-space-collapse="false" font-size="10pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
						<fo:block  padding="2pt"  line-height="10pt"  white-space-collapse="false"  keep-together="always">
							Bill passed for Rs. ${NetMargin?if_exists}/-  (Rupees
						</fo:block>
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
						<fo:block   line-height="10pt"  white-space-collapse="false"  keep-together="always" font-size="10pt">
						Through Chq No:----------------Dt:----------------Bank&amp;Branch----------------------
						</fo:block>
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						<fo:block line-height="40pt" white-space-collapse="false"  keep-together="always" font-size="10pt">
								Verified by             A.S                         A.A.O                 Pre-Audit
						</fo:block>
		 			</fo:flow>	
		 		</fo:page-sequence>
		 		</#if>
		 	    </#list> <#-- vendorMarginReportList -->
			<#else>
				<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${uiLabelMap.OrderNoOrderFound}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
			</#if>
		   </#list> <#--vendorMarginReportEntries -->
		  </#if>
		 </#list> <#-- end of master -->
		</#if>
	</#if>							
</fo:root>
</#escape>