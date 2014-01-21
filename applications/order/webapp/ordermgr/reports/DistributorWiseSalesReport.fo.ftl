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
<#macro header distributorDetails>   
	${setRequestAttribute("OUTPUT_FILENAME", "distsale.txt")}
    <fo:page-sequence master-reference="main" force-page-count="no-force">
    				
		<fo:static-content flow-name="xsl-region-before">		
				<fo:block text-align="left" white-space-collapse="false">.          ${uiLabelMap.ApDairyMsg}</fo:block>
			    <fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>      EX-FACTORY SALES: ${distributorDetails.description?if_exists}   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dateValue, "dd/MM/yyyy")} E &amp; ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDate, "dd/MM/yyyy")} M</fo:block>  
              	              	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left" keep-together="always">ROUTE        ${uiLabelMap.ProductTypeName}       ${uiLabelMap.TypeCredit}    ${uiLabelMap.TypeCard}   ${uiLabelMap.TypeSpecialOrder}    AGNTCS   PTCCS  LITRES  CASHVALUE  TRNSPTCOMM  NET</fo:block>
            	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
		</fo:static-content>			   
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">   
</#macro>


<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign tempDis = 0>
<#assign GRTotComm=0>
<#assign distributorDetails = 0>
<#assign isPreviousDistributor = false >
<#if resultDistList?has_content>
<#assign distributorDetails = delegator.findOne("Facility", {"facilityId" :firstDistributorId}, false)>
		 <@header distributorDetails=distributorDetails />
	     <#list resultDistList as distributorEntry>	
		     <#assign gTotLitres=0> 
		     <#assign trnsptComm =0>             	
		     <#assign facilityGrandTotal =0>
		     <#assign tempDis=StringUtil.split(distributorEntry.get("facilityId"),"-")>
		      <#assign distributorList=distributorEntry.entrySet()>  
		      <#assign lineNumber = lineNumber + distributorList.size()+4> 
		     <#if (distributorEntry.get("facilityType") !="DISTRIBUTOR")>
		     	<#assign zoneDetails = delegator.findOne("Facility", {"facilityId" : tempDis[0]}, false)>
		     	<#assign distributorDetails = delegator.findOne("Facility", {"facilityId" : zoneDetails.parentFacilityId}, false)>
		     </#if>		    			      
		   	 <#if (lineNumber > numberOfLines)>	       			
	       			   </fo:flow>						        	
	   				  </fo:page-sequence> 
	   				  <@header distributorDetails=distributorDetails/>	   				    						    					        				          				
	   				<fo:block font-size="9pt" break-before="page">
	   				 <#if (distributorEntry.get("facilityType") == "DISTRIBUTOR")>
        				<#assign isPreviousDistributor = true>
        			</#if>
	   				<#assign lineNumber = 5>
	   			<#elseif isPreviousDistributor == true>
	       			   </fo:flow>						        	
	   				  </fo:page-sequence> 
	   				  <@header distributorDetails=distributorDetails/>  					      					         					          
	   			 		<fo:block  font-size="9pt"> 
	   			 		<#assign GRTotComm=0>
	   			 		<#assign isPreviousDistributor = false>
	   			 		<#assign lineNumber = 5>     				
	   			<#elseif (distributorEntry.get("facilityType")=="DISTRIBUTOR")>     					
	   					<#assign isPreviousDistributor = true>
						<#assign lineNumber = 5>					      					         					          
	   			 		<fo:block  font-size="9pt" break-after="page">           			 	         				
	   			<#else>           					         					
	   					<fo:block  font-size="9pt">	   									         					  
	   		</#if>	 
	   		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        	<fo:table-column column-width="100%"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
				    		<fo:table-column column-width="6%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>
				    		<fo:table-column column-width="7.5%"/>				    		
				    		<fo:table-body>
                			<fo:table-row>                            
                        		<fo:table-cell>
                        		    <#if distributorEntry.get("facilityType") == "DISTRIBUTOR">
                        		    	<fo:block text-align="left">G.TOT</fo:block>
                        		    <#else>
                        				<fo:block text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(distributorEntry.get("facilityId"))),4)} </fo:block>
                       					
                       				</#if>
                       			</fo:table-cell>
                            	<fo:table-cell>                            		                          		 
	     							<#list distributorList as distributorValues>
	     							 	<#if distributorValues.getKey() != "facilityId" && distributorValues.getKey() != "facilityType">
        							 	<#assign product = delegator.findOne("Product", {"productId" : distributorValues.getKey()}, true)>
                                 	<fo:block>
        								<fo:table>
	           								<fo:table-column column-width="45pt"/>
	           								<fo:table-column column-width="30pt"/>
	           								<fo:table-column column-width="35pt"/>
	           								<fo:table-column column-width="30pt"/>
	           								<fo:table-column column-width="45pt"/>
	           								<fo:table-column column-width="35pt"/>
	           								<fo:table-column column-width="55pt"/>
	           								<fo:table-column column-width="65pt"/>
	           								<fo:table-body> 
			      								<fo:table-row >                    
					 								<fo:table-cell>
					       								<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					   								</fo:table-cell>								                            
												<#assign typeEntries = (distributorValues.getValue()).entrySet()>
												<#list typeEntries as typeEntry>
													<#if typeEntry.getKey() == "LITRES">
														<#assign netLitres=(typeEntry.getValue())>
						                      			<#assign gTotLitres=(gTotLitres+netLitres)>
						                      		</#if>
						                      		<#if typeEntry.getKey() == "TOTALAMOUNT">
									                	<#assign facilityGrandTotal = (facilityGrandTotal+typeEntry.getValue())>
									            	</#if>  
									            	<#if distributorEntry.get("facilityId")=="APDDCF" || tempDis[0]=="HO">              	 
														<#if typeEntry.getKey() != "CRATES" && typeEntry.getKey() != "TOTALAMOUNT" && typeEntry.getKey() != "CASH"  && typeEntry.getKey() != "TOTAL" && typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES">
							                      			<fo:table-cell >
																<fo:block  text-align="right">${typeEntry.getValue()}</fo:block>
															</fo:table-cell>
														</#if>
													<#else>	
														<#if typeEntry.getKey() != "CRATES" && typeEntry.getKey() != "CASH"  && typeEntry.getKey() != "TOTAL" && typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES">
							                      			<fo:table-cell >
																<fo:block  text-align="right">${typeEntry.getValue()}</fo:block>
															</fo:table-cell>
														</#if>
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
                       				<fo:table-cell/>
                       				<fo:table-cell/>
                       				<fo:table-cell/>
                       				<#if zoneComissionRates.get(tempDis[0])?has_content>
                       					<#if tempDis[0] =="TN" || tempDis[0] =="NL">
                       						<#assign trnsptComm=Static["java.lang.Math"].round(zoneComissionRates.get(tempDis[0]))>                       						
                       					<#elseif  tempDis[1] !="E">	
                       						<#assign trnsptComm=Static["java.lang.Math"].round((gTotLitres*zoneComissionRates.get(tempDis[0])))>
                       					</#if>
                       					<#assign GRTotComm=(GRTotComm+trnsptComm)>                       						
                       				</#if>
                       				<#if distributorEntry.get("facilityId")=="APDDCF" || tempDis[0]=="HO">
                  						<fo:table-cell>
                  					    	<#if distributorEntry.get("facilityType")== "DISTRIBUTOR">
                  								<fo:block white-space-collapse="false"  keep-together="always">Milk:                              ${gTotLitres}*        0.00*</fo:block>
	                     					<#else>	
	                     						<fo:block white-space-collapse="false" keep-together="always">Milk:                               ${gTotLitres}*        0.00*</fo:block>
	                     					</#if>	
	                     				</fo:table-cell>
	                     			<#else>
	                     				<fo:table-cell>
                  					    <#if distributorEntry.get("facilityType")== "DISTRIBUTOR">
                  							<fo:block white-space-collapse="false"  keep-together="always">Milk:                              ${gTotLitres}*        ${facilityGrandTotal}*         ${Static["java.lang.Math"].round(GRTotComm)}.00*     ${facilityGrandTotal-(Static["java.lang.Math"].round(GRTotComm))}</fo:block>
	                     				<#else>	
	                     					<fo:block white-space-collapse="false" keep-together="always">Milk:                           ${gTotLitres}*        ${facilityGrandTotal}*               ${trnsptComm}.00*          ${(facilityGrandTotal-(trnsptComm))}</fo:block>
	                     				</#if>	
	                     				</fo:table-cell>
	                     			</#if>	
	                     		</fo:table-row>
	            			<#if distributorEntry.get("facilityType") !="DISTRIBUTOR">
	            				<fo:table-row>
	            				    <fo:table-cell>
	            						    <fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                                                       (Com/Ltr SBS=1.16;TFM=1.22;VEF=1.32;CRD=2.00;BML=2.50 &amp; oth.<#if zoneComissionRates.get(tempDis[0])?has_content>${zoneComissionRates.get(tempDis[0])}</#if>)</fo:block>
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
		</fo:block>
		</#list>
  </fo:flow>						        	
   </fo:page-sequence>
   </#if>
   </fo:root>
</#escape>