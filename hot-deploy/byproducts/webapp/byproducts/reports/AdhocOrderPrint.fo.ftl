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
            <fo:simple-page-master master-name="main" page-height="10in" page-width="10in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
       
        <#if boothOrderItemMap?has_content> 
        <#assign boothOrderItemList=boothOrderItemMap.entrySet()>
      
        <#assign facilityId = parameters.originFacilityId>
		        <fo:page-sequence master-reference="main">		        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		        	      <fo:block  border-style="solid" font-family="Courier,monospace">
		        	             <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
		        	            <fo:block text-align="center" border-style="solid">
		        	            <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-size="9pt" keep-together="always">&#160;                                                      &#160;                                           </fo:block>
							    <fo:block text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" keep-together="always">${reportHeader.description}</fo:block>
							    
							    <fo:block text-align="center"  white-space-collapse="false" font-family="Courier,monospace" font-size="10pt" keep-together="always"></fo:block>
							     <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"></fo:block>
							     </fo:block>	
							    <#-- 
							     <fo:block text-align="center" border-style="solid">
							     <fo:block text-align="left"  white-space-collapse="false"  font-family="Courier,monospace" font-size="10pt" keep-together="always"><fo:inline font-style="italic" >&#160;          Our range of Products:5 Varities Of Milk,Curd Cups(100 gm,200 gm,500 gm),</fo:inline></fo:block>
							     <fo:block text-align="left" white-space-collapse="false"  font-family="Courier,monospace" font-size="10pt" keep-together="always"> <fo:inline font-style="italic" >&#160;          Sachet Curd(200 Grams,500 Grams),Bulk Curd(10 Kgs,20 Kgs,30 Kgs,40 Kgs)</fo:inline></fo:block>
							     <fo:block text-align="left" white-space-collapse="false"  font-family="Courier,monospace"  font-size="10pt" keep-together="always"><fo:inline font-style="italic" >&#160;          Butter Milk,Panner(200 Grams,500 Grams,1 Kg),Ghee,Pure Cow Ghee,Flavoured Milk</fo:inline></fo:block>
							     </fo:block> -->
								<fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
							    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="14pt" keep-together="always">&#160;  BILL/INVOICE</fo:block>
							   
							    
        							<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    <fo:table-column column-width="50%"/>
        						    <fo:table-column column-width="50%"/>
        						    <fo:table-header>
		    							 <fo:table-row> 
		    								<fo:table-cell><fo:block text-align="left" white-space-collapse="false" keep-together="always"></fo:block></fo:table-cell>
		    							 </fo:table-row>
		    							 <#assign facility = delegator.findOne("Facility", {"facilityId" :facilityId}, true)>
		    							 <fo:table-row>
		    								<fo:table-cell border-style="solid">
		    									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">Party Code   : ${facility.facilityId?if_exists}</fo:block>
		    								    <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">Party Name   : ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facility.ownerPartyId, false)}</fo:block>
		    								    <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">Address      : </fo:block>
		    								    <fo:block text-align="right" font-family="Courier,monospace"  font-size="12pt">&#160;</fo:block>
				                                <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
				                               <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="12pt" keep-together="always">&#160;</fo:block>
		    								</fo:table-cell>
		    								<fo:table-cell border-style="solid">
				                               <fo:block text-align="center" font-family="Courier,monospace"  font-size="12pt">
		    									<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;   Date Of DeliveryGoods</fo:block>
		    								    <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;   Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd/MM/yyyy")}</fo:block>
		    								     </fo:block>
		    								</fo:table-cell>
		    							 </fo:table-row> 
	        							<fo:table-row> 
	        								<fo:table-cell><fo:block text-align="left" white-space-collapse="false" keep-together="always"> &#160;</fo:block></fo:table-cell>
	        							</fo:table-row>       							
        						   </fo:table-header>
        						  <fo:table-body>
        							<fo:table-row width="100%">
        								<fo:table-cell> 
        									<fo:block  font-size="12pt">   
        										<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="60pt"/>
					        						<fo:table-column column-width="280pt"/>
					        						<fo:table-column column-width="168pt"/>
					        						<fo:table-column column-width="120pt"/>
					        						<fo:table-header>
						        						<fo:table-row>
						        							<fo:table-cell border-style="solid">
						        								<fo:block font-size="12pt"  white-space-collapse="false" >&#160;  SNO</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">Description of Product</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        						         <fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">Qty.in Ltrs/Packets</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell border-style="solid">
						        								<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">&#160; Value in(RS)</fo:block>
						        							</fo:table-cell>
						        						</fo:table-row>	
					        						</fo:table-header>
					        						<fo:table-body>
					        					
										        		<#assign totalValue =0>
										        		<#assign totLtrs =0>
										        		<#assign sno =1>
										        		
										        		 <#list boothOrderItemList as productInvoice>
					        							<fo:table-row>
					        							<#assign product = delegator.findOne("Product", {"productId" : productInvoice.getKey()}, true)> 
					        							   <fo:table-cell border-style="solid">
					        									<fo:block text-align="center" font-size="12pt">${sno}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell border-style="solid">
					        									<fo:block text-align="center" font-size="12pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					        								</fo:table-cell>
					        								<#assign productItems = productInvoice.getValue().entrySet()>  
		        	                                          <#list productItems as eachItem>
		        	                                          <#if eachItem.getKey()=="QTY">
		        	                                          	<#assign totLtrs =totLtrs+eachItem.getValue()>
		        	                                          	 <fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="12pt">${eachItem.getValue()}</fo:block>
					        								   </fo:table-cell>
		        	                                          	</#if>
		        	                                          	<#if eachItem.getKey()=="TOTALAMOUNT">
		        	                                          	<#assign totalValue =totalValue+eachItem.getValue()>
		        	                                          	 <fo:table-cell border-style="solid">
					        									<fo:block text-align="right" font-size="12pt">${eachItem.getValue()?string("#0.00")}</fo:block>
					        								   </fo:table-cell>
		        	                                          	</#if>
					        								</#list>				        								
					        							</fo:table-row>
					        							<#assign sno =sno+1>
					        						   </#list>	
					        						<fo:table-row>
					        							<fo:table-cell>
					        								<fo:block keep-together="always" text-align="center"></fo:block>
					        							</fo:table-cell>					        							
					        							<fo:table-cell >
					        								<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always">Grand Total:</fo:block>
					        							</fo:table-cell>
					        						   <fo:table-cell >
					        								<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always"></fo:block>
					        							</fo:table-cell>
					        							<fo:table-cell >
					        								<fo:block keep-together="always"  font-size="12pt" text-align="right">${totalValue?if_exists?string("#0.00")}</fo:block>
					        							</fo:table-cell>
					        						</fo:table-row>
					        						</fo:table-body>
					        						</fo:table>	
					        						
					        						<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
					        						<fo:table-column column-width="200pt"/>
					        						<fo:table-column column-width="448pt"/>
					        						<fo:table-body>
					        						<fo:table-row>
					        							<fo:table-cell border-style="solid">		
					        								<fo:block keep-together="always" text-align="center" font-size="12pt" >&#160;      In Words:</fo:block>
					        							</fo:table-cell>					
					        							<fo:table-cell border-style="solid">					
					        								<fo:block keep-together="always" text-align="left" font-size="12pt">&#160;     ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>   
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
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
    </#if>  
     </fo:root>
</#escape>