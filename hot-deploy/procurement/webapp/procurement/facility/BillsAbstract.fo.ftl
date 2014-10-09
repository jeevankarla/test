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
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence> 
		<#else>
        <fo:page-sequence master-reference="main">
        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always" >&#160;                			    			${reportHeader.description?if_exists}.  </fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always">&#160;                     			    	       B I L L S    A B S T R A C T   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                			    			UNIT CODE AND NAME : ${unitCode?if_exists}   ${unitName?if_exists}                                     PAGE NO:<fo:page-number/></fo:block>	 	 	  
        	</fo:static-content>
        <#assign opcost = 0>
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
                    		<fo:table-column column-width="54pt"/>
                    		<fo:table-column column-width="42pt"/>
                    		<fo:table-column column-width="42pt"/>
                    		<fo:table-column column-width="41pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="55pt"/>
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="30pt"/>
                    		<fo:table-header>
				            	<fo:table-cell>
				            		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                       TOTAL GROSS DETAILS    </fo:block>
					        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;                                                                      =====================   </fo:block>
					        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;    NAME OF THE CENTER                                                            CARTAGE        </fo:block>
					        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">CODE -----------------------                                                        OTHERS          </fo:block>
					        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">&#160;    NAME OF THE PRESIDENT    TOTAL     TOTAL     TOTAL      TOTAL   OPC/COMN   GROSS-AMT         </fo:block>
					        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
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
							
                    		<#assign centerData = unitMilkBillAbstMap.entrySet()>
                    		<#list centerData as centerDetail>
                    		
                    			<#if centerDetail.getValue().get("centerName") == "TOTAL">
                    			
                    				<fo:table-row>
	                    				<fo:table-cell >	
	                    					<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                    				</fo:table-cell>
	                    			</fo:table-row>
                   				</#if>
                    			
                    			<#assign procProd = procurementProductList.get(0)>
                    			<fo:table-row>
                    				<fo:table-cell >	
	                        			<fo:block text-align="left" font-size="7pt">${centerDetail.getValue().get("centerCode")?if_exists}</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >
	                        			<fo:block text-align="left" font-size="7pt">${centerDetail.getValue().get("centerName")?if_exists}</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"QtyKgs")?has_content>${centerDetail.getValue().get(procProd.brandName+"QtyKgs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"QtyLtrs")?has_content>${centerDetail.getValue().get(procProd.brandName+"QtyLtrs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"KgFat")?has_content>${centerDetail.getValue().get(procProd.brandName+"KgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"KgSnf")?has_content>${centerDetail.getValue().get(procProd.brandName+"KgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"Price")?has_content>${centerDetail.getValue().get(procProd.brandName+"Price")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("cartage")?has_content>${centerDetail.getValue().get("cartage")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 0>
	                        		<fo:table-cell >
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        				<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 4>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 8>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
						        	</#list>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("DednsTot")?has_content>${centerDetail.getValue().get("DednsTot")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("netAmount")?has_content>${centerDetail.getValue().get("netAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"tipAmount")?has_content>${centerDetail.getValue().get(procProd.brandName+"tipAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	                        		</fo:table-cell>
                    			</fo:table-row>
                    			<#assign procProd = procurementProductList.get(1)>
                    			<fo:table-row>
                    				<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
                    					
	                        			<fo:block text-align="left" font-size="7pt"><#if centerDetail.getValue().get("centerOwnerName")?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerDetail.getValue().get("centerOwnerName"))),20)}<#else></#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"QtyKgs")?has_content>${centerDetail.getValue().get(procProd.brandName+"QtyKgs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"QtyLtrs")?has_content>${centerDetail.getValue().get(procProd.brandName+"QtyLtrs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"KgFat")?has_content>${centerDetail.getValue().get(procProd.brandName+"KgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"KgSnf")?has_content>${centerDetail.getValue().get(procProd.brandName+"KgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"Price")?has_content>${centerDetail.getValue().get(procProd.brandName+"Price")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	                        		</fo:table-cell>
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 1>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 5>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 9>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
						        	</#list>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get(procProd.brandName+"tipAmount")?has_content>${centerDetail.getValue().get(procProd.brandName+"tipAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	                        		</fo:table-cell>
                    			</fo:table-row>

                    			<fo:table-row>
                    				<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("totQtyKgs")?has_content>${centerDetail.getValue().get("totQtyKgs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("totQtyLtrs")?has_content>${centerDetail.getValue().get("totQtyLtrs")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("totKgFat")?has_content>${centerDetail.getValue().get("totKgFat")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("totKgSnf")?has_content>${centerDetail.getValue().get("totKgSnf")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("commAmt")?has_content>${((centerDetail.getValue().get("commAmt"))+opcost)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("grossAmount")?has_content>${centerDetail.getValue().get("grossAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
                    			
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 2>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 6>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 10>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
						        	</#list>
                    			<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("TOTtipAmount")?has_content>${centerDetail.getValue().get("TOTtipAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">0.00</fo:block>
	                        		</fo:table-cell>
                    			</fo:table-row>
                    			
                    			<#assign procNewProd = procurementProductList.get(2)>
                    			<fo:table-row>
                    				<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if procNewProd?has_content>${centerDetail.getValue().get(procNewProd.brandName+"QtyKgs")?if_exists?string("##0.0")}<#else></#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if procNewProd?has_content>${centerDetail.getValue().get(procNewProd.brandName+"QtyLtrs")?if_exists?string("##0.0")}<#else></#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if procNewProd?has_content>${centerDetail.getValue().get(procNewProd.brandName+"KgFat")?if_exists?string("##0.00")}<#else></#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if procNewProd?has_content>${centerDetail.getValue().get(procNewProd.brandName+"KgSnf")?if_exists?string("##0.00")}<#else></#if></fo:block>
	                        		</fo:table-cell>
                    				<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if procNewProd?has_content>${centerDetail.getValue().get(procNewProd.brandName+"Price")?if_exists?string("##0.00")}<#else></#if></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        		</fo:table-cell>
	                        		<#assign dedTypes = testAdjMap.entrySet()>
						        	<#list dedTypes as adjType>
						        	<#if adjType.getKey() = 3>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 7>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<#if adjType.getKey() = 11>
	                        		<fo:table-cell >	
	                        			<#assign dedValue = centerDetail.getValue().get(adjType.getValue())>
	                        			<#if !(dedValue?has_content)>
	                        				<#assign dedValue = 0>
	                        			</#if>
	                        			<fo:block text-align="right" font-size="7pt">${dedValue?if_exists?string("##0.00")}</fo:block>
	                        			<#assign dedValue = 0>
	                        		</fo:table-cell>
	                        		</#if>
						        	</#list>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt">********</fo:block>
	                        		</fo:table-cell>
	                        		<#if centerDetail.getValue().get("centerName") == "TOTAL">   
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if unitTotals.get("netRndAmountWithOp")?has_content>${unitTotals.get("netRndAmountWithOp")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		<#else>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"><#if centerDetail.getValue().get("netRndAmount")?has_content>${centerDetail.getValue().get("netRndAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
	                        		</fo:table-cell>
	                        		</#if>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"></fo:block>
	                        		</fo:table-cell>
	                        		<fo:table-cell >	
	                        			<fo:block text-align="right" font-size="7pt"></fo:block>
	                        		</fo:table-cell>
                    			</fo:table-row>


                    			<fo:table-row>
                    				<fo:table-cell >
                    					<#assign lineNumber = lineNumber + 5>
                    					<#if (lineNumber >= numberOfLines)>
                    							<#assign lineNumber = 11 >
                    							<fo:block font-size="7pt" page-break-after="always"></fo:block>
                    						<#else>
                    							<#if centerDetail.getValue().get("centerName") == "TOTAL">
				                    			<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				                   				<#else>
                    							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    							</#if>
                    					</#if>	
	                        		</fo:table-cell>
                    			</fo:table-row>
                    			</#list>
                    		</fo:table-body>					
						</fo:table>
					</fo:block>
            	</fo:flow>		
			</fo:page-sequence>
			</#if>
</fo:root>
</#escape>