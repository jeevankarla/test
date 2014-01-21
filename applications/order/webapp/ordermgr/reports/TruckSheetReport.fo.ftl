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
            margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trsheet.txt")}
<#assign lineNumber = 7>
<#assign numberOfLines = 66>
<#assign isPreviousRoute = false> 
<#assign truckSheetFirstBooth=truckSheetReportList.get(0)>
<#if truckSheetReportList?has_content>			
			<#import "trucksheetHeader.fo.ftl" as TrucksheetHeader/>
        	<@TrucksheetHeader.trucksheetHeader  estimatedDeliveryDate = estimatedDeliveryDate boothId =truckSheetFirstBooth.get("facilityId") dctx = dctx facilityTypeId ="BOOTH"/>							
           		<#list truckSheetReportList as truckSheetReport>
           			<#assign facilityGrandTotal = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign totalCrates=0>
           			<#assign totalLitres = 0>
           			<#assign facilityTypeId = truckSheetReport.get("facilityType")>
           			<#assign productEntries = (truckSheetReport).entrySet()>                   
                    <#if (facilityTypeId !="ZONE")>
                      <#assign lineNumber = lineNumber + productEntries.size()+5>                                      	                      		
           			<#if (lineNumber > numberOfLines)> 
           				 	 </fo:flow>						        	
   							</fo:page-sequence>   													
   							<#import "trucksheetHeader.fo.ftl" as TrucksheetHeader/>
        					<@TrucksheetHeader.trucksheetHeader  estimatedDeliveryDate = estimatedDeliveryDate boothId = truckSheetReport.get("facilityId") dctx = dctx facilityTypeId = facilityTypeId/>
        				<#assign lineNumber = 7>
        				<#if (facilityTypeId == "ROUTE")>
        					<#assign isPreviousRoute = true>
        				</#if>		 		          				
           				<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page">           				
           				<#elseif (isPreviousRoute == true)> 
           					     </fo:flow>						        	
	   							</fo:page-sequence>   													
	   							<#import "trucksheetHeader.fo.ftl" as TrucksheetHeader/>
	        					<@TrucksheetHeader.trucksheetHeader  estimatedDeliveryDate = estimatedDeliveryDate boothId = truckSheetReport.get("facilityId") dctx = dctx facilityTypeId = facilityTypeId/>
	        					<#assign lineNumber = 7>	 
	        					<#assign isPreviousRoute = false>		          				
	           					<fo:block font-family="Courier,monospace" font-size="10pt"  break-before="page">	           						    				
           				<#elseif (facilityTypeId == "ROUTE")>           					
        					<#assign lineNumber = 7>        					
        					<#assign isPreviousRoute = true>
        					<fo:block font-family="Courier,monospace" font-size="10pt" break-after="page"> 	           			 			
           			 	<#else>          			 		
           			 		<fo:block font-family="Courier,monospace" font-size="10pt"> 
           			 	</#if>   		
            			<fo:table width="100%" table-layout="fixed" space-after="0.0in">
            				 <fo:table-column column-width="100%"/>
            				 <fo:table-header>			                	
			                	<fo:table-row>                        
			                        <fo:table-cell column-width="100%"><fo:block>----------------------------------------------------------------------------------</fo:block></fo:table-cell>
			                    </fo:table-row>	
			                </fo:table-header>
			                <fo:table-body>
			                <fo:table-row column-width="100%">
			                <fo:table-cell column-width="100%">
            				 <fo:table  table-layout="fixed" >                
				                <fo:table-column column-width="117pt"/>
				                <fo:table-column column-width="170pt"/>
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
                            	<#if facilityTypeId == "ROUTE">
                            		<fo:block text-align="left">ROUTE TOTAL:</fo:block>
                            		<#else>
                            			<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("description").toUpperCase())),15)}</fo:block>
                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityId"))),4)}(${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")})</fo:block>
                            	</#if>                            	
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block >
                                	<#list productEntries as productEntry>
                      		<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType">
                      		<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
		                              <fo:table >
	             						 <fo:table-column column-width="40pt"/>
	             						  <fo:table-column column-width="45pt"/>
	             						   <fo:table-column column-width="43pt"/>
	             						    <fo:table-column column-width="40pt"/>
	             						     <fo:table-column column-width="40pt"/>
	             						      <fo:table-column column-width="40pt"/>
	             						       <fo:table-column column-width="52pt"/>
	             						        <fo:table-column column-width="65pt"/>
	             						        <fo:table-body>                  						 
				              						<fo:table-row >                    
						                            <fo:table-cell>
						                                <fo:block  text-align="left" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
						                            </fo:table-cell>								                            
						                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>
						                      			                      	
						                      			<#list typeEntries as typeEntry> 
						                      				<#if typeEntry.getKey() == "LITRES">
						                      				<#assign totalLitres=(totalLitres+typeEntry.getValue())>
						                      				</#if>
						                      				<#if typeEntry.getKey() == "CRATES">
						                      				<#assign crates=typeEntry.getValue()>
						                      				<#assign cratesValue=StringUtil.split(crates,".||-")>
						                      				<#assign totalCrates=(totalCrates+Static["java.lang.Integer"].valueOf(cratesValue[0]))>
						                      				</#if> 
						                      			 <#if (typeEntry.getKey() != "AGNTCS") && (typeEntry.getKey() != "PTCCS") && (typeEntry.getKey() != "LITRES") && typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES">
						                      			   <fo:table-cell >						                      			  
							                      			   <#if typeEntry.getKey() == "TOTALAMOUNT">
									                            	<#assign facilityGrandTotal = (facilityGrandTotal.add(typeEntry.getValue()))>
									                            	 <#if (typeEntry.getValue() != (Static["java.math.BigDecimal"].ZERO))>
									                            	 	<fo:block  text-align="right">${typeEntry.getValue().toEngineeringString()}</fo:block>
									                            	 </#if>
									                           	<#else>
							                      			   		<fo:block  text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(typeEntry.getValue().toString())),6)}</fo:block>
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
                       </#if>                
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
	                 <fo:table-cell >
	                     <fo:block text-align="left">${totalCrates}-0</fo:block>
	                 </fo:table-cell>
	                 <fo:table-cell >
	                 <#if (facilityTypeId != "ROUTE")>
	                	 <fo:block text-align="right">${facilityGrandTotal.toEngineeringString()}*</fo:block>
	                 </#if>
	                 </fo:table-cell>	
	                 <fo:table-cell >
	                     <fo:block></fo:block>
	                 </fo:table-cell>								                 	
	                 <fo:table-cell>
	                 	<fo:block></fo:block>
	                 </fo:table-cell>	
	            </fo:table-row>
            </fo:table-body>
        </fo:table>
        </fo:table-cell>
        </fo:table-row>
        	<fo:table-row>                        
			    <fo:table-cell >
			    <#if (facilityTypeId != "ROUTE")>
			    <fo:block text-align="center">VENDOR'S MARGIN INCREASED FROM RS. 1.10 TO RS.1.20  PER LT;</fo:block>
			    <fo:block  text-align="center"> CARD DISCOUNT FROM PS.30 TO PS. 50 W.E.F 01/07/2012.</fo:block>
			    </#if>
			    <#if (facilityTypeId == "ROUTE") >
			    <fo:block>---------------------------------------------------------------------------------</fo:block>
        		<fo:block white-space-collapse="false" white-space-treatment="preserve">TOT LTRS:      ${totalLitres}      TOT CRATES:${totalCrates}-0                 TOT CASH:${facilityGrandTotal.toEngineeringString()}*</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>        		        		
        		<fo:block white-space-collapse="false" white-space-treatment="preserve">Q.P.S        LOADED BY          SECURITY               RMRD</fo:block>
        		</#if>
			    </fo:table-cell>
			</fo:table-row>	
        </fo:table-body>
         </fo:table>       
        </fo:block>
          </#if>
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