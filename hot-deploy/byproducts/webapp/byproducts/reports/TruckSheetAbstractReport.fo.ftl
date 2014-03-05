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
            margin-top="0.5in" margin-bottom=".5in" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trabs.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#if truckSheetReportList?has_content>	
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
				<#if shipmentTypeId=="AM_SHIPMENT_SUPPL" || shipmentTypeId=="AM_SHIPMENT">          		
              		<fo:block text-align="left" white-space-collapse="false">&#160;                           SACHET SCHEDULE FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd-MMMM-yyyy")}- MORNING SHIFT </fo:block>  
              	<#elseif shipmentTypeId=="PM_SHIPMENT_SUPPL" || shipmentTypeId=="PM_SHIPMENT">
              		<fo:block text-align="left" white-space-collapse="false">&#160;                           SACHET SCHEDULE FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd-MMMM-yyyy")}- EVENING SHIFT </fo:block>
              	</#if> 
              	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">ROUTE  PRODUCT  PRODUCT                            SUBS.MILK  QTY/    TOTAL   TOTAL </fo:block>
                <fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">CODE   CODE     NAME                                          CRATES  CRATES  CANS  </fo:block>
            	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">	

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
            		
            				 <fo:table  table-layout="fixed">                
				                <fo:table-column column-width="40pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="51pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-body>
                				    <#if productEntries?has_content>                     		                      	                     	
                      					<#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)>
                       			        <fo:table-row>                            
                            		      <fo:table-cell>
                                		    <fo:block >
	                                		<#assign totalcrates = 0>
	                                			<#list productEntries as productEntry>
	                      							<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType" && productEntry.getKey() != "PREV_DUE" && productEntry.getKey() != "paidAmount">
	                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
			                              		           <fo:table >
					             						 		<fo:table-column column-width="33pt"/>
					             						  		<fo:table-column column-width="55pt"/>
					             						  		<fo:table-column column-width="72pt"/>
					             						   	    <fo:table-column column-width="195pt"/>
					             						   	    <fo:table-column column-width="65pt"/>
					             						    	
					             						        <fo:table-body> 
				              							          <fo:table-row >  
				              							             <fo:table-cell>
	                            			                            <fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityId"))),5)}</fo:block>
	                            		                             </fo:table-cell>                  
							                            		     <fo:table-cell>
							                                			<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
							                            			 </fo:table-cell>
							                            	         <fo:table-cell>
							                                	        <fo:block  text-align="left" keep-together="always">${product.get("productName")}</fo:block>
							                            	          </fo:table-cell>	
							                            	          
							                            	           
										                            	<#assign typeEntries = (productEntry.getValue()).entrySet()>
										                      			<#assign crates = 0>
										                      			<#assign qty = 0>
										                      			<#list typeEntries as typeEntry> 
										                      				<#if typeEntry.getKey() == "TOTAL">
										                      				<#assign qty=typeEntry.getValue()>
										                      				</#if> 
										                      				<#if typeEntry.getKey() == "NOCRATES">
										                      				<#assign crates=typeEntry.getValue()>
										                      				<#assign totalcrates = totalcrates+crates>
										                      				</#if> 
												                        </#list>
	 								                                  <fo:table-cell>
							                                	        <fo:block  text-align="right">${qty}/${crates}</fo:block>
							                            	         </fo:table-cell>
							                         	         </fo:table-row>
							                                   </fo:table-body>
					                         	           </fo:table> 	                          
	                       				 			</#if>
	                      				 		</#list>
	                      				 		<fo:table >
	                                              <fo:table-column column-width="380pt"/>
	                                                <fo:table-body> 
												       <fo:table-row >  
												            <fo:table-cell>
												                <fo:block  text-align="right" keep-together="always">${totalcrates}</fo:block>
														    </fo:table-cell>
													   </fo:table-row>
												    </fo:table-body>
										        </fo:table>	              				 		                    
                                       </fo:block>
                                        </fo:table-cell>	                           
   			                   </fo:table-row>
 				            </#if>
	                     </fo:table-body>
                      </fo:table>
                 <fo:block font-family="Courier,monospace" font-size="9pt">-------------------------------------------------------------------------------------------------</fo:block> 
          </#if>	
       </fo:block>
       
   </#list> 
  </fo:flow>						        	
</fo:page-sequence>
 <#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 <fo:block font-size="9pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>						
</fo:root>
</#escape>