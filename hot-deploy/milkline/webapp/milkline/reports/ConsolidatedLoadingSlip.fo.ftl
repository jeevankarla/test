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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                   margin-top="0.8in" margin-bottom="0.8in"    margin-left="1in" margin-right="1in">
                <fo:region-body margin-top="1.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent=".5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "Consolidated_Loading_Slip.txt")}
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace" font-size="12pt" keep-together="always">Page:<fo:page-number/>       SUPRAJA DAIRY PRIVATE LIMITED : VISAKHAPATNAM</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always">
        		    <#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, true)>
							<#if shipment.shipmentTypeId.startsWith("AM")>         		
              	                <fo:block white-space-collapse="false" font-family="Courier,monospace" keep-together="always">   MORNING CONSOLIDATED LOADING SLIP OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}   Time :</fo:block>		               
            			    <#else>
            				<fo:block white-space-collapse="false" font-family="Courier,monospace" keep-together="always">    EVENING CONSOLIDATED LOADING SLIP OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}   Time :</fo:block>
            			</#if>
        		</fo:block>
        		
        		<fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">GROUP     ROUTE          ${uiLabelMap.ProductTypeName}              LITRES    CRATES        CANS </fo:block>
        		<fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">&#160;                                                              20L     30L     40L</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
        	  <#assign grandTotalCrates=0><#--For caliculationg all groups crates sum and used in grand total   	-->
            
        	<#assign groupWiseDetais = groupDetailsMap.entrySet()>
       	    <#list groupWiseDetais as groupDetails>
       	    <fo:block  white-space-collapse="false"  font-family="Courier,monospace"  text-align="left" >${groupDetails.getKey()}</fo:block>	 
            	<#assign cans20=0><#--For caliculationg group wise cans and crates sum -->
                 <#assign cans30=0>
			     <#assign cans40=0>
			     <#assign totalCrates=0>
			    <#assign totalLitres = (Static["java.math.BigDecimal"].ZERO)>
            	<#assign groupWiseDetails = groupDetails.getValue().entrySet()>     
            	<#list groupWiseDetails as routeWiseValues> 
			      <#assign routeCans20=0><#--For caliculationg each route wise cans and crates sum -->
                  <#assign  routeCans30=0>
			      <#assign  routeCans40=0>
			      <#assign  routeTotalCrates=1><#--As LMS Custmer are asking extra Crate irresepective of loose packets we are adding One extra crate i.e intilize with 1 insted of 0 -->
			     
			        <#assign routeTotalLitres = (Static["java.math.BigDecimal"].ZERO)>
            	<fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
            		<#assign productEntries = routeWiseValues.getValue().entrySet()> 
            		
              		<fo:block>
              			<fo:table width="100%" table-layout="fixed">
            				<fo:table-column column-width="100%"/>
            				<fo:table-body>
			                <fo:table-row column-width="100%">
			                <fo:table-cell column-width="100%">
            					<fo:table  table-layout="fixed" >                
					                <fo:table-column column-width="40pt"/>
					                <fo:table-column column-width="60pt"/>
					                <fo:table-column column-width="60pt"/>
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
		                       			<fo:table-row> 
		                       				<fo:table-cell> 
		                       					<fo:block>&#160;  </fo:block> 
		                       				</fo:table-cell>	                 
		                            		<fo:table-cell>
		                            			<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routeWiseValues.getKey()}, true)>
		                            			<fo:block white-space-collapse="false" font-size="8pt"  font-family="Courier,monospace"  text-align="left" keep-together="always">${routeDetails.get("facilityName")} </fo:block>
		                            		</fo:table-cell>
		                            		<fo:table-cell>
		                                		<fo:block >
		                                			<#list productEntries as productEntry>
		                      							<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType" && productEntry.getKey() != "PREV_DUE" && productEntry.getKey() != "paidAmount">
		                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
				                              		<fo:table >
			             						 		<fo:table-column column-width="100pt"/>
			             						  		<fo:table-column column-width="12pt"/>
			             						   	    <fo:table-column column-width="40pt"/>
			             						    	<fo:table-column column-width="25pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             					      		<fo:table-column column-width="40pt"/>
			             				        		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>	
			             				          		<fo:table-column column-width="40pt"/>	
			             						        <fo:table-body> 
					              							<fo:table-row >                    
								                            	<fo:table-cell>
								                                	<fo:block  text-align="left" font-family="Courier,monospace" font-size="8pt" text-indent="30pt"  keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
								                            	</fo:table-cell>
								                            	<fo:table-cell>
								                            	</fo:table-cell>								                            
								                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>
								                      			<#list typeEntries as typeEntry> 
								                      			 <#if typeEntry.getKey() == "LITRES">	
								                      			 <#assign routeTotalLitres=(routeTotalLitres.add(typeEntry.getValue()))>
								                      			     
								                      			 <fo:table-cell >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt">${typeEntry.getValue()?string("##0.00")}</fo:block>
												                 </fo:table-cell>
												                 <fo:table-cell>								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                 </fo:table-cell>
												                 </#if>
												                 <#if ((!butterProductList.contains(productEntry.getKey()))&&(!flavredProductList.contains(productEntry.getKey())))><#-- crates should  be applicable for other than Butter Milk,Flavred Milk and LMS-BULK type of products -->
												                  <#if !bulkMilkProducts.contains(productEntry.getKey())>
												                   <#if typeEntry.getKey() == "CRATES">
												                   <#assign crates1=typeEntry.getValue()>
						                      						<#assign cratesValue1=StringUtil.split(crates1,".||-")>
						                      						<#assign routeTotalCrates=(routeTotalCrates+Static["java.lang.Integer"].valueOf(cratesValue1[0]))>
								                      		      <fo:table-cell>								                            	
												                  <fo:block  text-align="right" font-family="Courier,monospace"   font-size="8pt">${typeEntry.getValue()}</fo:block>
												                  </fo:table-cell>
												                 </#if> 
												                 </#if>
												                  </#if>
												                <#if bulkMilkProducts.contains(productEntry.getKey())>
												                 <#if typeEntry.getKey() == "CANS20">
												                   <#assign routeCans20=routeCans20+typeEntry.getValue()>
												                 <fo:table-cell>								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                 </fo:table-cell>
												                  <fo:table-cell >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"><#if typeEntry.getValue()!=0>${typeEntry.getValue()}<#else>-</#if></fo:block>
												                 </fo:table-cell>
												                 </#if>	
												                 <#if typeEntry.getKey() == "CANS30">
												                   <#assign routeCans30=routeCans30+typeEntry.getValue()>
												                  <fo:table-cell >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"><#if typeEntry.getValue()!=0>${typeEntry.getValue()}<#else>-</#if></fo:block>
												                 </fo:table-cell>
												                 </#if>
												                  <#if typeEntry.getKey() == "CANS40">
												                  <fo:table-cell>
												                    <#assign routeCans40=routeCans40+typeEntry.getValue()>								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"><#if typeEntry.getValue()!=0>${typeEntry.getValue()}<#else>-</#if></fo:block>
												                 </fo:table-cell>
												                 </#if>
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
		            				</fo:table-body>
		       					 </fo:table>
		        			</fo:table-cell>
		        		</fo:table-row>
         			</fo:table-body>
         		</fo:table>
              </fo:block>
              <fo:block>&#160;</fo:block>
               <fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">&#160;       Route Total Crates and Cans:      ${routeTotalLitres?string("##0.00")}          ${routeTotalCrates}        ${routeCans20}      ${routeCans30}       ${routeCans40}</fo:block>
              <fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
           
           <#assign cans20=cans20+routeCans20>
           <#assign cans30=cans30+routeCans30>
           <#assign cans40=cans40+routeCans40>
           <#assign totalCrates=totalCrates+routeTotalCrates>
               <#assign totalLitres=(totalLitres.add(routeTotalLitres))>
           </#list> 
           <#assign grandTotalCrates=grandTotalCrates+totalCrates>
           
            <fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace" font-weight="bold" text-align="left">&#160;     Group Total Crates and Cans:     ${totalLitres?string("##0.00")}            ${totalCrates}       ${cans20}      ${cans30}      ${cans40}</fo:block>
          <fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
           </#list>
          
           <#assign grandTotal=groupsGrandTotal.entrySet()>
       <#list grandTotal as grandTotalValue>
        <fo:block  white-space-collapse="false"  font-family="Courier,monospace"  text-align="left" >GRAND TOTAL</fo:block>	
        <fo:block font-family="Courier,monospace" font-weight="8pt">
        	<fo:table width="100%" table-layout="fixed" space-after="0.0in">
          		<fo:table-column column-width="100%"/>
            				<fo:table-body>
			                <fo:table-row column-width="100%">
			                <fo:table-cell column-width="100%">
            					<fo:table  table-layout="fixed" >                
					                <fo:table-column column-width="40pt"/>
					                <fo:table-column column-width="60pt"/>
					                <fo:table-column column-width="60pt"/>
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
              								<fo:table-row>   
              									<fo:table-cell> 
                     								<fo:block>&#160; </fo:block> 
                     							</fo:table-cell>                         
                      							<fo:table-cell>
                          							<fo:block white-space-collapse="false"   font-family="Courier,monospace"  text-align="left" keep-together="always"></fo:block>
                     							</fo:table-cell>
                          						<fo:table-cell>
				          						 <#assign grandTotalEntry=grandTotalValue.getValue()>
				      							 <#assign grandTot=grandTotalEntry.entrySet()>
				      						
				     								 <#assign gTotLitres= (Static["java.math.BigDecimal"].ZERO)>
				     								 <#assign gTcans20=0>
                                                     <#assign gTcans30=0>
			                                         <#assign gTcans40=0>
			                                         <#assign gTotalCrates=0>
				      							 <#list grandTot as grandTotEntrie>        							
				      							 <#if grandTotEntrie.getKey() != "facilityId" && grandTotEntrie.getKey() != "facilityType" && grandTotEntrie.getKey() != "PREV_DUE" && grandTotEntrie.getKey() != "paidAmount">
				      							 <#assign product = delegator.findOne("Product", {"productId" : grandTotEntrie.getKey()}, true)>
                               						<fo:block>
                               							<fo:table>
			             				          		<fo:table-column column-width="100pt"/>
			             						  		<fo:table-column column-width="12pt"/>
			             						   	    <fo:table-column column-width="40pt"/>
			             						    	<fo:table-column column-width="25pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             					      		<fo:table-column column-width="40pt"/>
			             				        		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>
			             						   		<fo:table-column column-width="40pt"/>	
			             				          		<fo:table-column column-width="40pt"/>	
					            						        <fo:table-body> 
						              							<fo:table-row >                    
									                            	<fo:table-cell>
									                                	<fo:block  white-space-collapse="false" font-size="8pt" text-indent="30pt"  font-family="Courier,monospace"  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
									                            	</fo:table-cell>
									                                 <fo:table-cell>
								                            	       </fo:table-cell>						                            
																	<#assign typeEntries = (grandTotEntrie.getValue()).entrySet()>
																	<#list typeEntries as typeEntry>
																		<#if typeEntry.getKey() == "LITRES">
												                      		<#assign gTotLitres=(gTotLitres.add(typeEntry.getValue()))>
												                      	</#if> 
												                      	<#if typeEntry.getKey() == "LITRES">	
								                      		                   <fo:table-cell >								                            	
												                            	<fo:block  text-align="right" font-family="Courier,monospace"   font-size="8pt">${typeEntry.getValue()?string("##0.00")}</fo:block>
												                            	</fo:table-cell>
												                            	 <fo:table-cell>								                            	
												                 	                <fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                                 </fo:table-cell>
												                            </#if>
												                   <#if !bulkMilkProducts.contains(grandTotEntrie.getKey())>
												                     <#if typeEntry.getKey() == "CRATES">
												                      <#assign gCrates=typeEntry.getValue()>
						                      					      <#assign gCratesValue=StringUtil.split(gCrates,".||-")>
						                      					      <#assign gTotalCrates=(gTotalCrates+Static["java.lang.Integer"].valueOf(gCratesValue[0]))>
								                      		           <fo:table-cell>								                            	
												                        <fo:block  text-align="right" font-family="Courier,monospace"   font-size="8pt"></fo:block>
												                       </fo:table-cell>
												                  </#if> 
												                  </#if>
												                  <#if bulkMilkProducts.contains(grandTotEntrie.getKey())>
												                 <#if typeEntry.getKey() == "CANS20">
												                 <#assign gTcans20=gTcans20+typeEntry.getValue()>
												                 <fo:table-cell>								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                 </fo:table-cell>
												                  <fo:table-cell >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                 </fo:table-cell>
												                 </#if>	
												                 <#if typeEntry.getKey() == "CANS30">
										                           <#assign gTcans30=gTcans30+typeEntry.getValue()>
												                  <fo:table-cell >								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
												                 </fo:table-cell>
												                 </#if>
												                  <#if typeEntry.getKey() == "CANS40">
												                   <#assign gTcans40=gTcans40+typeEntry.getValue()>
												                  <fo:table-cell>								                            	
												                 	<fo:block  text-align="right" font-family="Courier,monospace"  font-size="8pt"></fo:block>
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
								</fo:table-body>
							</fo:table>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
		  <fo:block white-space-collapse="false" font-size="9pt" font-weight="bold"  font-family="Courier,monospace"  text-align="left">&#160;        Grand Total Crates and Cans:   ${gTotLitres?string("##.00")}          ${grandTotalCrates}      ${gTcans20}     ${gTcans30}      ${gTcans40}</fo:block>
        
          <fo:block>------------------------------------------------------------------------------------------------------------------</fo:block>
        </#list>
	</fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>