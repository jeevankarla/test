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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trabs.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" white-space-collapse="false">.             SUPRAJA DAIRY PRIVATE LIMITED</fo:block>				
					<#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
				<#if shipment.shipmentTypeId="AM_SHIPMENT_SUPPL">          		
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>  MORNING GATE PASS ABSTRACT OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>  
              	<#elseif shipment.shipmentTypeId="PM_SHIPMENT_SUPPL">
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>  EVENING GATE PASS ABSTRACT OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
              	<#elseif shipment.shipmentTypeId="AM_SHIPMENT">
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>  MORNING TRUCK SHEET ABSTRACT OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
              	<#elseif shipment.shipmentTypeId="PM_SHIPMENT">
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>  EVENING TRUCK SHEET ABSTRACT OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
              	</#if> 
              	<fo:block>--------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">ROUTE    ${uiLabelMap.ProductTypeName}      ${uiLabelMap.TypeCredit}   ${uiLabelMap.TypeCard}   ${uiLabelMap.TypeSpecialOrder}  ${uiLabelMap.TypeCash}  LITRES  AMOUNT</fo:block>
            	<fo:block>--------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">	

<#if truckSheetReportList?has_content>	
				<#list truckSheetReportList as truckSheetReport>
           			<#assign facilityGrandTotal = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign totalLitres = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign facilityTypeId = truckSheetReport.get("facilityType")>
           			<#assign productEntries = (truckSheetReport).entrySet()>           			       		        			
           			<#if (lineNumber > numberOfLines)> 
	           			<#assign lineNumber = 5>
	           			<#assign facilityNumberInPage = 0>	           				          				
           				<fo:block font-size="8pt" break-before="page">
           				<#elseif (facilityNumberInPage == 4)>           					
           					<#assign lineNumber = 5>
           					<#assign facilityNumberInPage = 0>           					          
           			 		<fo:block  font-size="8pt" break-after="page"> 
           				<#else>           					         					
           					<fo:block  font-size="8pt">
           					<#assign lineNumber = 5>           					         					  
           			</#if>         			
           			 <#if (facilityTypeId == "ROUTE" )>
           			 	<#assign lineNumber = lineNumber + productEntries.size()+3>
           			 	<#assign facilityNumberInPage = (facilityNumberInPage+1)>               			 	      		
            			<fo:table width="100%" table-layout="fixed" space-after="0.0in">
            				<fo:table-column column-width="100%"/>
            				<fo:table-body>
			                <fo:table-row column-width="100%">
			                <fo:table-cell column-width="100%">
            				 <fo:table  table-layout="fixed" >                
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="40pt"/>
				                <fo:table-column column-width="45pt"/>
				                <fo:table-column column-width="51pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-body>
                				    <#if productEntries?has_content>                     		                      	                     	
                      					<#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)>
                       			<fo:table-row>                            
                            		<fo:table-cell>
                            			<fo:block text-align="left" keep-together="always">${facility.get("facilityName")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                                		<fo:block >
                                			<#list productEntries as productEntry>
                      							<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType" && productEntry.getKey() != "PREV_DUE" && productEntry.getKey() != "paidAmount"&& productEntry.getKey() != "CANS20"&& productEntry.getKey() != "CANS30"&& productEntry.getKey() != "CANS40">
                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
		                              		<fo:table >
	             						 		<fo:table-column column-width="50pt"/>
	             						  		<fo:table-column column-width="12pt"/>
	             						   	    <fo:table-column column-width="33pt"/>
	             						    	<fo:table-column column-width="25pt"/>
	             						   		<fo:table-column column-width="30pt"/>
	             						   		<fo:table-column column-width="40pt"/>
	             					      		<fo:table-column column-width="50pt"/>
	             				        		<fo:table-column column-width="50pt"/>
	             						   		<fo:table-column column-width="43pt"/>
	             						   		<fo:table-column column-width="50pt"/>	
	             				          		<fo:table-column column-width="53pt"/>	
	             						        <fo:table-body> 
			              							<fo:table-row >                    
						                            	<fo:table-cell>
						                                	<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
						                            	</fo:table-cell>								                            
						                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>
						                      			<#list typeEntries as typeEntry> 
						                      				<#if typeEntry.getKey() == "LITRES">
						                      				<#assign totalLitres=(totalLitres.add(typeEntry.getValue()))>
						                      				</#if> 
						                      				<#if typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES" && typeEntry.getKey() != "TOTAL" && typeEntry.getKey() != "AGNTCS" && typeEntry.getKey() != "PTCCS" && typeEntry.getKey() != "CRATES" && typeEntry.getKey() != "CARD_AMOUNT" && typeEntry.getKey() != "CASH_FS" && typeEntry.getKey() != "CANS20"&& typeEntry.getKey() != "CANS30"&& typeEntry.getKey() != "CANS40"> 
							                      			   <fo:table-cell >
							                      			   <#if typeEntry.getKey() == "TOTALAMOUNT">
									                            	<#assign facilityGrandTotal = (facilityGrandTotal.add(typeEntry.getValue()))>
									                            	<fo:block  text-align="right"  font-size="8pt">${typeEntry.getValue()?string("##0.00")}</fo:block>
									                            <#else>
									                            	<fo:block  text-align="right"  font-size="8pt">${typeEntry.getValue()}</fo:block>
									                            							                      			   		
									                           </#if>
									                            </fo:table-cell>
									                           </#if> 
								                        </#list>
						                         		</fo:table-row>
						                         	</fo:table-body>
				                         	</fo:table> 	                          
                       				 			</#if>
                      				 		</#list>             
                                		</fo:block>
                            		</fo:table-cell>	                           
                       			</fo:table-row>
                       			<fo:table-row >
                  					<fo:table-cell >
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell>
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>
	                 					
	                 				<fo:table-cell>
	                     				<fo:block text-align="left" text-indent="40pt">${totalLitres?string("##0.0")}*</fo:block>
	                 				</fo:table-cell>
	                  				<fo:table-cell>
	                    	 			<fo:block text-align="left" text-indent="30pt">${facilityGrandTotal?string("##0.00")}*</fo:block>
	                 				</fo:table-cell>	
	            				</fo:table-row>
	            				<fo:table-row>
	            					<fo:table-cell >
	                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                 				</fo:table-cell>
	            				</fo:table-row>
                 				</#if>
            				</fo:table-body>
       					 </fo:table>
        			</fo:table-cell>
        		</fo:table-row>
         	</fo:table-body>
         </fo:table>
         	</#if>	
        </fo:block>
       </#list>
       <#assign grandTotal=grandTotalMap.entrySet()>
       <#list grandTotal as grandTotalValue>
        <fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        	<fo:table-column column-width="100%"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="40pt"/>
				    		<fo:table-body>
                			<fo:table-row>                            
                        		<fo:table-cell>
                            		<fo:block text-align="left" font-size="8pt">G.TOT</fo:block>
                       			</fo:table-cell>
                            	<fo:table-cell>
            						 <#assign grandTotalEntry=grandTotalValue.getValue()>
        							 <#assign grandTot=grandTotalEntry.entrySet()>
        							 <#assign totCrates=0>
       								 <#assign gTotLitres= (Static["java.math.BigDecimal"].ZERO)>
        							 <#list grandTot as grandTotEntrie>        							
        							 <#if grandTotEntrie.getKey() != "facilityId" && grandTotEntrie.getKey() != "facilityType" && grandTotEntrie.getKey() != "PREV_DUE" && grandTotEntrie.getKey() != "paidAmount">
        							 <#assign product = delegator.findOne("Product", {"productId" : grandTotEntrie.getKey()}, true)>
                                 <fo:block>
        							<fo:table >
	           							<fo:table-column column-width="55pt"/>
	           							<fo:table-column column-width="24pt"/>
	           							<fo:table-column column-width="35pt"/>
	           							<fo:table-column column-width="30pt"/>
	           							<fo:table-column column-width="30pt"/>
	           							<fo:table-column column-width="40pt"/>
	           							<fo:table-column column-width="33pt"/>
	          							<fo:table-column column-width="40pt"/>
	           							<fo:table-column column-width="42pt"/>
	           							<fo:table-column column-width="46pt"/>	
	           							<fo:table-column column-width="45pt"/>	
	             						<fo:table-body> 
			      							<fo:table-row >                    
					 							<fo:table-cell>
					       							<fo:block  text-align="left" keep-together="always" font-size="8pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					   							</fo:table-cell>								                            
											<#assign typeEntries = (grandTotEntrie.getValue()).entrySet()>
											<#list typeEntries as typeEntry>
												<#if typeEntry.getKey() == "LITRES">
						                      		<#assign gTotLitres=(gTotLitres.add(typeEntry.getValue()))>
						                      	</#if> 
						                      	<#if typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES" && typeEntry.getKey() != "TOTAL" && typeEntry.getKey() != "AGNTCS" && typeEntry.getKey() != "PTCCS" && typeEntry.getKey() != "CRATES" && typeEntry.getKey() != "CARD_AMOUNT" && typeEntry.getKey() != "CASH_FS" && typeEntry.getKey() != "CANS20"&& typeEntry.getKey() != "CANS30"&& typeEntry.getKey() != "CANS40">
													<fo:table-cell >
													<#if typeEntry.getKey() != "TOTALAMOUNT" >
														<#if typeEntry.getKey() == "LITRES">
						                            		<fo:block  text-align="right"  font-size="8pt">${typeEntry.getValue()}</fo:block>
						                            		<#else>
						                            			<fo:block  text-align="right"  font-size="8pt">${typeEntry.getValue()}</fo:block>
									                     </#if>
													</#if>
													</fo:table-cell>
												</#if>	
											</#list>
											</fo:table-row>
										</fo:table-body>
				    				</fo:table>
					 			</fo:block>
					 			</#if>
					 			</#list>
					 			</fo:table-cell>
							</fo:table-row>
							<fo:table-row >
                  					<fo:table-cell >
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell>
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                  				<fo:table-cell>
	                     				<fo:block ></fo:block>
	                 				</fo:table-cell>	
	                 				<fo:table-cell >
	                     				<fo:block></fo:block>
	                 				</fo:table-cell>
	                 				<fo:table-cell>
	                    	 			<fo:block text-align="center">${gTotLitres}*</fo:block>
	                 				</fo:table-cell>	
	            				</fo:table-row>
	            				<fo:table-row>
	            					<fo:table-cell>
	            						<fo:block>----------------------------------------------------------------------------------------------------------</fo:block>
	                 				</fo:table-cell>
	            				</fo:table-row>					
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>	 		
        				
        </#list> 
    </fo:flow>						        	
   </fo:page-sequence>
    <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
</#if>						
</fo:root>
</#escape>