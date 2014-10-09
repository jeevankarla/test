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
<#assign numberOfLines = 62>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".5in" >
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "BillsAbst.txt")}
        <#assign pageStart= parameters.pageStart>
        <#assign pageEnd= parameters.pageEnd>       
        ${setRequestAttribute("VST_PAGE_START", "${pageStart}")}
        ${setRequestAttribute("VST_PAGE_END", "${pageEnd}")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>
		<#else> 
		<#if finalMap?has_content>
  <fo:page-sequence master-reference="main">
  			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always" >&#160;                			    			     ${reportHeader.description?if_exists}.  </fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always">&#160;                     			    	    B I L L S    A B S T R A C T    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160; ANAND PATTERN :            			    			UNIT CODE AND NAME : ${unitCode?if_exists}   ${unitName?if_exists}                                    PAGE NO:<fo:page-number/></fo:block>	 	 	  
        	</fo:static-content>
       	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 	
       	<#assign lineNumber = 11>   
       	<fo:block  text-align="left" font-size="8pt"> 	
			<fo:table>
				<fo:table-column column-width="15pt"/>
	    		<fo:table-column column-width="90pt"/>
	    		<fo:table-column column-width="42pt"/>
	    		<fo:table-column column-width="42pt"/>
	    		<fo:table-column column-width="42pt"/>
	    		<fo:table-column column-width="42pt"/>
	    		<fo:table-column column-width="52pt"/>
	    		<fo:table-column column-width="52pt"/>
	    		<fo:table-column column-width="140pt"/>
	    		<fo:table-column column-width="50pt"/>
	    		<fo:table-column column-width="40pt"/>
	    		<fo:table-column column-width="45pt"/>
	    		<fo:table-column column-width="30pt"/>
	    		<fo:table-column column-width="30pt"/>
	    		<fo:table-column column-width="30pt"/>
	    		<fo:table-header>
	            	<fo:table-cell>
	            		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                       TOTAL GROSS DETAILS    </fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                      =====================   </fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;    NAME OF THE CENTER                                                            CARTAGE        </fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">CODE -----------------------                                                        OTHERS          </fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;    NAME OF THE PRESIDENT    TOTAL     TOTAL     TOTAL      TOTAL   COMISSN   GROSS-AMT         </fo:block>
		        		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        	</fo:table-cell>
			     	<fo:table-cell>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                              </fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                         QTY-KG    QTY-LTS    KG-FAT    KG-SNF</fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                         =======   =======   =======    ======</fo:block>
			    			<fo:block>
								<fo:table>
									<fo:table-column column-width="50pt"/>
									<fo:table-column column-width="50pt"/>
									<fo:table-column column-width="50pt"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
											<#list procurementProductList as procProduct>
				        						<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                           ${procProduct.brandName}       ${procProduct.brandName}        ${procProduct.brandName}         ${procProduct.brandName}     ${procProduct.brandName} AMT</fo:block>
											</#list>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>                   													
							</fo:block>
			        	</fo:table-cell>
		         		<fo:table-cell>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                                                               </fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                        TOTAL DEDUCTIONS DETAILS                           T.I.P  SHR-CAP</fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                     ============================    TOT DED     NET AMT   ======  ======</fo:block>
			    			<fo:block>
								<fo:table>
									<fo:table-column column-width="55pt"/>
									<fo:table-column column-width="45pt"/>
									<fo:table-column column-width="45pt"/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
											<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",shedId)).get("shedAdjustmentDescriptionMap")>
												<#assign dedTypes = adjustmentDedTypes.entrySet()>
				        						<#list dedTypes as adjType>
				        							<#if adjType.getKey() <= 3 >
				        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                     ${orderAdjustmentDesc[(adjType.getValue()).orderAdjustmentTypeId]?if_exists}   </fo:block>
				        							</#if>
				        						</#list>	          
											</fo:table-cell>
											<fo:table-cell>
												<#assign dedTypes = adjustmentDedTypes.entrySet()>
				        						<#list dedTypes as adjType>
				        							<#if (adjType.getKey() > 3) && (adjType.getKey() < 8)>
				        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                   ${orderAdjustmentDesc[(adjType.getValue()).orderAdjustmentTypeId]?if_exists}   </fo:block>
				        							</#if>
				        						</#list>
											</fo:table-cell>
											<fo:table-cell>
												<#assign dedTypes = adjustmentDedTypes.entrySet()>
				        						<#list dedTypes as adjType>
				        							<#if (adjType.getKey() > 7) && (adjType.getKey() < 12)>
				        								<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                  ${orderAdjustmentDesc[(adjType.getValue()).orderAdjustmentTypeId]?if_exists}   </fo:block>
				        							</#if>
				        						</#list>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>                   													
							</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;</fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;</fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;</fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                                           ********   ********    B.M     B.M          </fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                                         	 ********   RND NET     C.M     C.M          </fo:block>
							<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                                           ********   ********    TOT     TOT        </fo:block>
			        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                                           ********   ********</fo:block>
			        	</fo:table-cell>	                  	                  
			   	</fo:table-header>  
	    			<fo:table-body>
	    				<#assign centerData = finalMap.entrySet()>
	    				<#list centerData as eachData>
	    		        <#assign centerDetailsMap = eachData.getValue()>
	    			<fo:table-row>
	    			<#assign facility = delegator.findOne("Facility", {"facilityId" :eachData.getKey()}, true)>
	    			<#assign facilityParty = facility.ownerPartyId?if_exists>
	    			<#assign ownerNameDetails = delegator.findOne("PartyNameView", {"partyId" :facilityParty}, true)>
	    				<fo:table-cell >	
	            			<fo:block text-align="left" font-size="7pt">${facility.get("facilityCode")}</fo:block>
	            		</fo:table-cell>
	    				<fo:table-cell >
	            			<fo:block text-align="left" font-size="7pt">${facility.get("facilityName")}</fo:block>
	            			<fo:block text-align="left" font-size="7pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(ownerNameDetails.get("groupName"))),20)}</fo:block>
	            		</fo:table-cell>                                
	    				<fo:table-cell >	
	    				<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("qtyKgs")?if_exists?string("#0.0")}</fo:block>	                        			
	            		</#list>
	            		<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("qtyKgs")?if_exists?string("#0.0")}</fo:block>
	            		</fo:table-cell>
	            		<fo:table-cell>
	            		<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("qtyLtrs")?if_exists?string("#0.0")}</fo:block>	                        			
	            		</#list>
	            		<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("qtyLtrs")?if_exists?string("#0.0")}</fo:block>
	            		</fo:table-cell>
	            		<fo:table-cell>
	            		<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("kgFat")?if_exists?string("#0.00")}</fo:block>	                        			
	            		</#list>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("kgFat")?if_exists?string("#0.00")}</fo:block>
	            		</fo:table-cell>
	            		
	    				<fo:table-cell>
	            		<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("kgSnf")?if_exists?string("#0.00")}</fo:block>	                        			
	            		</#list>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("kgSnf")?if_exists?string("#0.00")}</fo:block>
	            		</fo:table-cell>
	    				<fo:table-cell>
	            		<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("price")?if_exists?string("#0.00")}</fo:block>	                        			
	            		</#list>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("commissionAmount")?if_exists?string("#0.00")}</fo:block>
	            		</fo:table-cell>
	            		<fo:table-cell >	
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("cartage")?if_exists?string("#0.00")}</fo:block>
	            			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("grossAmt")?if_exists?string("#0.00")}</fo:block>
	            		</fo:table-cell>
	            		<fo:table-cell >
	            		<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>
									<fo:table-row>
									<#assign procProd = procurementProductList.get(0)>
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 0>
										<fo:table-cell>
										<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",shedId)).get("shedAdjustmentDescriptionMap")>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if adjType.getKey() <= 3 >
			        							<#assign dedValue = centerDetailsMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}   </fo:block>
			        							</#if>
			        						</#list>	          
										</fo:table-cell>
										<fo:table-cell>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if (adjType.getKey() > 3) && (adjType.getKey() < 8)>
			        							<#assign dedValue = centerDetailsMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}    </fo:block>
			        							</#if>
			        						</#list>
										</fo:table-cell>
										<fo:table-cell>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if (adjType.getKey() > 7) && (adjType.getKey() < 12)>
			        							<#assign dedValue = centerDetailsMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}   </fo:block>
			        							</#if>
			        						</#list>
										</fo:table-cell>
										</#if>
			        				</#list>
									</fo:table-row>
									<fo:table-row>
			                    		<fo:table-cell><fo:block  font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			                    	</fo:table-row>	
								</fo:table-body>
							</fo:table>                   													
						</fo:block>
			        	</fo:table-cell >	
	            		<fo:table-cell >
	            			<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
	            							<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("grsDed")?if_exists?string("#0.00")}</fo:block>
	            						</fo:table-cell>
	            					</fo:table-row>	
	            					<fo:table-row>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>	
		                        		<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
	            					</fo:table-row>
	            					<fo:table-row>	
		                        		<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
	            					</fo:table-row>		
	            				</fo:table-body>		
	            			</fo:table>			
	            			</fo:block>
	            		</fo:table-cell >
	            		<fo:table-cell >
	            			<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>	
									<fo:table-row>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("netAmt")?if_exists?string("#0.00")}</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
									<fo:table-row>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
		                        	<#assign netAmount =  centerDetailsMap.get("TOT").get("netAmt")?if_exists>
		                        	<#assign rndNetAmount=Static["java.lang.Math"].round(netAmount)>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">${rndNetAmount?string("#0.00")}</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
								</fo:table-body>		
	            			</fo:table>			
	            			</fo:block>
	            		</fo:table-cell>
	            		<fo:table-cell >	
	            		<#list procurementProductList as procProducts>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get(procProducts.productId).get("tipAmt")?if_exists?string("#0.00")}</fo:block>	                        			
	            		</#list>
	            			<fo:block text-align="right" font-size="7pt">${centerDetailsMap.get("TOT").get("tipAmt")?if_exists?string("#0.00")}</fo:block>
	            		</fo:table-cell>
	            		
	            		<fo:table-cell >	
	            			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	            			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	            			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	            		</fo:table-cell>
	            	</fo:table-row>
	            	<fo:table-row>
        				<fo:table-cell >
        					<#assign lineNumber = lineNumber + 5>
        					<#if (lineNumber >= numberOfLines)>
        							<#assign lineNumber = 11 >
        							<fo:block font-size="7pt" page-break-after="always"></fo:block>
        						<#else>
        					</#if>	
                		</fo:table-cell>
        			</fo:table-row>
	        	</#list>
				</fo:table-body>
        </fo:table> 
     </fo:block>
     	<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		<fo:block  text-align="left" font-size="8pt"> 	
			<fo:table>
				<fo:table-column column-width="15pt"/>
        		<fo:table-column column-width="90pt"/>
        		<fo:table-column column-width="42pt"/>
        		<fo:table-column column-width="42pt"/>
        		<fo:table-column column-width="42pt"/>
        		<fo:table-column column-width="42pt"/>
        		<fo:table-column column-width="52pt"/>
        		<fo:table-column column-width="52pt"/>
        		<fo:table-column column-width="140pt"/>
        		<fo:table-column column-width="50pt"/>
        		<fo:table-column column-width="40pt"/>
        		<fo:table-column column-width="45pt"/>
        		<fo:table-column column-width="30pt"/>
        		<fo:table-column column-width="30pt"/>
        		<fo:table-column column-width="30pt"/>
        			<fo:table-body>
        			<fo:table-row>
        				<fo:table-cell >	
                			<fo:block text-align="left" font-size="7pt"></fo:block>
                		</fo:table-cell>
        				<fo:table-cell >
                			<fo:block text-align="right" font-size="7pt">TOTALS:</fo:block>
                		</fo:table-cell>
        				<fo:table-cell >	
        				<#list procurementProductList as procProducts>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("qtyKgs")?if_exists?string("#0.0")}</fo:block>	                        			
                		</#list> 
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("qtyKgs")?if_exists?string("#0.0")}</fo:block>                                      
                		</fo:table-cell>
                  		<fo:table-cell>
                    		<#list procurementProductList as procProducts>
                    			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("qtyLtrs")?if_exists?string("#0.0")}</fo:block>	                        			
                    		</#list>
                    		<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("qtyLtrs")?if_exists?string("#0.0")}</fo:block>
                    	</fo:table-cell>
                  		<fo:table-cell>
                		<#list procurementProductList as procProducts>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("kgFat")?if_exists?string("#0.00")}</fo:block>	                        			
                		</#list>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("kgFat")?if_exists?string("#0.00")}</fo:block>
                		</fo:table-cell>
        				<fo:table-cell>
                		<#list procurementProductList as procProducts>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("kgSnf")?if_exists?string("#0.00")}</fo:block>	                        			
                		</#list>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("kgSnf")?if_exists?string("#0.00")}</fo:block>
                		</fo:table-cell>
        				<fo:table-cell>
                		<#list procurementProductList as procProducts>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("price")?if_exists?string("#0.00")}</fo:block>	                        			
                		</#list>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("commissionAmount")?if_exists?string("#0.00")}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell >	
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("cartage")?if_exists?string("#0.00")}</fo:block>
                			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
                			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("grossAmt")?if_exists?string("#0.00")}</fo:block>
                		</fo:table-cell>            
                		<fo:table-cell >
                			<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>
									<fo:table-row>
									<#assign procProd = procurementProductList.get(0)>
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 0>
										<fo:table-cell>
										<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",shedId)).get("shedAdjustmentDescriptionMap")>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if adjType.getKey() <= 3 >
			        							<#assign dedValue = totalMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}   </fo:block>
			        							</#if>
			        						</#list>	          
										</fo:table-cell>
										<fo:table-cell>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if (adjType.getKey() > 3) && (adjType.getKey() < 8)>
			        							<#assign dedValue = totalMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}    </fo:block>
			        							</#if>
			        						</#list>
										</fo:table-cell>
										<fo:table-cell>
											<#assign dedTypes = adjustmentDedTypes.entrySet()>
			        						<#list dedTypes as adjType>
			        							<#if (adjType.getKey() > 7) && (adjType.getKey() < 12)>
			        							<#assign dedValue = totalMap.get("TOT").get((adjType.getValue()).orderAdjustmentTypeId)>
			                        			<#if !(dedValue?has_content)>
			                        				<#assign dedValue = 0>
			                        			</#if>
			        								<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-size="7pt">${dedValue?if_exists?string("##0.00")}   </fo:block>
			        							</#if>
			        						</#list>
										</fo:table-cell>
										</#if>
			        				</#list>
									</fo:table-row>
								</fo:table-body>
							</fo:table>                   													
						</fo:block>
			        	</fo:table-cell >	
     					<fo:table-cell >
                			<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
                							<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("grsDed")?if_exists?string("#0.00")}</fo:block>
                						</fo:table-cell>
                					</fo:table-row>	
                					<fo:table-row>
                						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>	
		                        		<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
                					</fo:table-row>
                					<fo:table-row>	
		                        		<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
                					</fo:table-row>		
                				</fo:table-body>		
                			</fo:table>			
                			</fo:block>
                		</fo:table-cell >
                		<fo:table-cell >
                			<fo:block>
							<fo:table>
								<fo:table-column column-width="55pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-column column-width="45pt"/>
								<fo:table-body>	
									<fo:table-row>
                						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("netAmt")?if_exists?string("#0.00")}</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
									<fo:table-row>
                						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
			                        	<#assign totNetAmount =  totalMap.get("TOT").get("netAmt")?if_exists>
			                        	<#assign rndTotNetAmount=Static["java.lang.Math"].round(totNetAmount)>
	            						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">${rndTotNetAmount?string("#0.00")}</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row>
                						<fo:table-cell >	
		                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
		                        		</fo:table-cell>
		                        	</fo:table-row>
								</fo:table-body>		
                			</fo:table>			
                			</fo:block>
                		</fo:table-cell>
			        	<fo:table-cell >	
			        		<#list procurementProductList as procProducts>
                    			<fo:block text-align="right" font-size="7pt">${totalMap.get(procProducts.productId).get("tipAmt")?if_exists?string("#0.0")}</fo:block>	                        			
                    		</#list>
                				<fo:block text-align="right" font-size="7pt">${totalMap.get("TOT").get("tipAmt")?if_exists?string("#0.00")}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell >	
                			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
                			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
                			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
                		</fo:table-cell>
                	</fo:table-row>
				</fo:table-body>
			</fo:table> 
		 </fo:block>
		 <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
    </fo:flow>
 </fo:page-sequence>
 <#else>
	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	 		<fo:block font-size="14pt">
    			${uiLabelMap.OrderNoOrderFound}.
	 		</fo:block>
		</fo:flow>
	</fo:page-sequence>
  </#if>
 </#if>
</fo:root>
</#escape>
