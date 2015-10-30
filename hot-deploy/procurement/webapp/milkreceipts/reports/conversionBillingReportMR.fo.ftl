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
specific language governing permissions and limitationsborder-style="solid"border-style="solid"
under the License.
-->
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0in" margin-bottom=".7in" margin-left=".1in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ConversionBillingReport.pdf")}
<#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			 		<fo:block font-size="14pt">
			 			${errorMessage}
			 		</fo:block>
				</fo:flow>
	</fo:page-sequence>
<#else>
<#if partyWiseProductWiseConversionMap?has_content>
<#assign pageNumber = 0>

			<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
					<fo:static-content flow-name="xsl-region-before">
		            </fo:static-content>	
		            	
		            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	<#assign partyIds = partyWiseProductWiseConversionMap.keySet()>
	<#list partyIds as partyIdstr>
		<#assign conversionProductMap = "">
		<#assign conversionProductMap = partyWiseProductWiseConversionMap.get(partyIdstr)>
		
		<#if conversionProductMap?has_content> 
			<#assign partyName = conversionProductMap.get("unionName")>    
		            	
				 <#assign productKeys ="">
				 <#assign productKeys = conversionProductMap.keySet()>
				 <#list productKeys as productKey>
				 	<#if productKey!="unionName">
				 	<#assign convProdMap ="">
					<#assign convProdMap = conversionProductMap.get(productKey)>	
					<#if convProdMap?has_content>
						<#assign conProductKeys="">	
					   <#assign conProductKeys = convProdMap.keySet()>	
					   <#list conProductKeys as conProductKey>
							<#if pageNumber != 0>	
			                  <fo:block page-break-before="always" text-align="center" keep-together="always" font-weight="bold">          </fo:block>				    	                
			                </#if>
			                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false"> KARNATAKA CO-OPERATIVE MILK PRODUCTS FEDERATION LTD.</fo:block>
			                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false"> UNIT :MOTHER DAIRY : G.K.V.K POST :YELHANKA : BANGLORE -560065 </fo:block>
			                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">----------------------------------------------------------------------------------------</fo:block>
					        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">   Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">DETAILS OF MILK/PRODUCTS RECEIVED FROM :${partyName} BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}   </fo:block>
					        <#if showReference?has_content && showReference == "Y">
					        	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">ONLY FOR REFERENCE NOT FOR ACCOUNTING</fo:block>
					        </#if>
					        <#assign conProdDetailsMap = "">
					        <#assign conProdDetailsMap = convProdMap.get(conProductKey)>
					        <#assign conProductBrandName = conProdDetailsMap.get("conProductBrandName")>
					        <#assign productBrandName = conProdDetailsMap.get("productBrandName")>
					        <fo:block>
					        	<fo:table>
					        		<fo:table-column column-width="20pt"/>
			                    	<fo:table-column column-width="30pt"/>
			            			<fo:table-column column-width="40pt"/>
			            			<fo:table-column column-width="245pt"/>
			                    	<fo:table-column column-width="585pt"/>
			            			<fo:table-column column-width="150pt"/>
			            			<fo:table-body>
			            				<fo:table-row >
			                                <fo:table-cell border-style="solid">
			                            		<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">SL NO </fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">Date </fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">Tanker No </fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Acknowledge Details</fo:block>
			                            		<fo:block>
			                            			<fo:table>
			                            				<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
			            										<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">DC No </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Qty in Kgs </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Fat %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Snf %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG FAT</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG SNF</fo:block>  
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${productBrandName} To ${conProductBrandName} Conversion Details </fo:block>
			                            		<fo:block>
			                            			<fo:table>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="45pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="55pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="60pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="60pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Qty in Kgs </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Fat %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Snf %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG FAT</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG SNF</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">TOTAL</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KGFAT +</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KGSNF </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<#assign sugarAddn =0>
								                       				<#assign sugarAddn = conProdDetailsMap.get("addSugar")>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Sugar Add</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${sugarAddn}%</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">TOT TS</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<#assign totSolidsLoss = 0>
								                            		<#assign totSolidsLoss = conProdDetailsMap.get("totSolidsLoss")>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">HL${totSolidsLoss?if_exists?string('##0')}%</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">NET TS</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<#assign prodYield = 0>
								                       				<#assign prodYield = conProdDetailsMap.get("productYield")>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${conProductBrandName} Yield</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${prodYield?if_exists?string('##0.000')} %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<#assign conCost = 0>
								                       				<#assign conCost = conProdDetailsMap.get("prodConversionPrice")>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${conProductBrandName} Conv.</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Cost Rs.</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">${conCost?if_exists?string('##0.00')}</fo:block>  
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Diff. in FAT</fo:block>
			                            		<fo:block>
			                            			<fo:table>
			                            				<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
			            										<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Net Fat </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">But </fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Yield </fo:block>
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<#assign butConCost = 0>
								                       				<#assign butConCost = conProdDetailsMap.get("butterConversionPrice")>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">But</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Conv</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Cost Rs ${butConCost?if_exists?string('##0.00')}</fo:block>  
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
		                       			</fo:table-row >
		                       			<fo:table-row >
		                                <fo:table-cell>
		                            		<fo:block >&#160; </fo:block>  
		                       			</fo:table-cell>
		                       			</fo:table-row >	
		                       			<#assign transfersList = conProdDetailsMap.get("tankerList")>
		                       			<#if transfersList?has_content>
			                       			<#list  transfersList as transfer>
			                       				<#assign slNo = transfer.get("slNo")>
			                       				<#assign date = transfer.get("date")>
			                       				<#assign tankerNo = transfer.get("tankerNo")>
			                       				<#assign dcNo = transfer.get("dcNo")>
			                       				<#assign recdQty = transfer.get("recdQty")>
			                       				<#assign recdFat = transfer.get("recdFat")>
			                       				<#assign recdSnf = transfer.get("recdSnf")>
			                       				<#assign recdKgFat = transfer.get("recdKgFat")>
			                       				<#assign recdKgSnf = transfer.get("recdKgSnf")>
			                       				<#assign prodQty = transfer.get("prodQty")>
			                       				<#assign prodFat = transfer.get("prodFat")>
			                       				<#assign prodSnf = transfer.get("prodSnf")>
			                       				<#assign prodKgFat = transfer.get("prodKgFat")>
			                       				<#assign prodKgSnf = transfer.get("prodKgSnf")>
			                       				<#assign prodTs = transfer.get("prodTs")>
			                       				<#assign conSugarAddn = transfer.get("conSugarAddn")>
			                       				<#assign prodTotTs = transfer.get("prodTotTs")>
			                       				<#assign conTsLoss = transfer.get("conTsLoss")>
			                       				<#assign prodNetTs = transfer.get("prodNetTs")>
			                       				<#assign prodYieldVal = transfer.get("prodYield")>
			                       				<#assign prodAmount = transfer.get("prodAmount")>
			                       				<#assign diffFat = transfer.get("diffFat")>
			                       				<#assign butterYield = transfer.get("butterYield")>
			                       				<#assign butterAmount = transfer.get("butterAmount")>
			                       				<#if tankerNo == "TOTAL" >
			                       					<fo:table-row >
					                                <fo:table-cell>
					                            		<fo:block >&#160; </fo:block>  
					                       			</fo:table-cell>
					                       			</fo:table-row >	
			                       				</#if>
			                       				<fo:table-row >
					                                <fo:table-cell>
					                            		<fo:block   text-align="left" font-size="9pt" white-space-collapse="false">${slNo} </fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell  >
					                            		<fo:block   text-align="left" font-size="8pt" white-space-collapse="false">${date} </fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
					                            		<fo:block   text-align="right" font-size="8pt" white-space-collapse="false">${tankerNo} </fo:block>  
					                       			</fo:table-cell >
					                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
					                            				<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
										            			<fo:table-body>
					            									<fo:table-row  >
					            										<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="center" font-size="9pt" white-space-collapse="false">${dcNo} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdQty?if_exists?string('##0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdFat?if_exists?string('##0.0')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdKgFat?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdKgSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
										                    	<fo:table-column column-width="55pt"/>
										            			<fo:table-column column-width="30pt"/>
										            			<fo:table-column column-width="30pt"/>
										                    	<fo:table-column column-width="45pt"/>
										            			<fo:table-column column-width="50pt"/>
										            			<fo:table-column column-width="55pt"/>
										                    	<fo:table-column column-width="50pt"/>
										            			<fo:table-column column-width="50pt"/>
										            			<fo:table-column column-width="50pt"/>
										                    	<fo:table-column column-width="60pt"/>
										            			<fo:table-column column-width="50pt"/>
										            			<fo:table-column column-width="60pt"/>
										            			<fo:table-body>
					            									<fo:table-row >
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodQty?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodFat?if_exists?string('##0.0')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodKgFat?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodKgSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                       				<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodTs?if_exists?string('##0.00')}</fo:block>
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${conSugarAddn?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodTotTs?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${conTsLoss?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodNetTs?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodYieldVal?if_exists?string('##0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodAmount?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
					                            				<fo:table-column column-width="50pt"/>
										                    	<fo:table-column column-width="50pt"/>
										            			<fo:table-column column-width="50pt"/>
										            			<fo:table-body>
					            									<fo:table-row >
					            										<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${diffFat?if_exists?string('#0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${butterYield?if_exists?string('#0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if tankerNo=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${butterAmount?if_exists?string('#0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
				                       			</fo:table-row >	
			                       			</#list>
		                       			</#if>
			            			</fo:table-body>
					        	</fo:table>
					        </fo:block>
					        <#assign pageNumber = pageNumber+1>
			   	        </#list>
			   	     </#if>
			   	     </#if>   
		          </#list>
		          </#if>
				 </#list>
				 <#if unionWiseProductAbstract?has_content>
		          		 <fo:block page-break-before="always" text-align="center" keep-together="always" font-weight="bold">          </fo:block>
		          		 
		          		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false"> KARNATAKA CO-OPERATIVE MILK PRODUCTS FEDERATION LTD.</fo:block>
			                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false"> UNIT :MOTHER DAIRY : G.K.V.K POST :YELHANKA : BANGLORE -560065 </fo:block>
			                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">----------------------------------------------------------------------------------------</fo:block>
					        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">   Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">DETAILS OF MILK/PRODUCTS RECEIVED FROM :${abstPartyName} BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}   </fo:block>
					        <#if showReference?has_content && showReference == "Y">
					        	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">ONLY FOR REFERENCE NOT FOR ACCOUNTING</fo:block>
					        </#if>
					        <fo:block>
					        	<fo:table>
			            			<fo:table-column column-width="60pt"/>
			            			<fo:table-column column-width="235pt"/>
			                    	<fo:table-column column-width="605pt"/>
			            			<fo:table-column column-width="170pt"/>
			            			<fo:table-body>
			            				<fo:table-row >
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="left" font-size="11pt" white-space-collapse="false">Product </fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Acknowledge Details</fo:block>
			                            		<fo:block>
			                            			<fo:table>
								                    	<fo:table-column column-width="65pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="55pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Qty in Kgs </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Fat %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Snf %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG FAT</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG SNF</fo:block>  
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">All Conversion Details </fo:block>
			                            		<fo:block>
			                            			<fo:table>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="60pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="60pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="60pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Qty in Kgs </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Fat %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Snf %</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG FAT</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KG SNF</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                       				<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">TOTAL</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KGFAT +</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">KGSNF </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Sugar Add</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false"></fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">TOT TS</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">HL</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">NET TS</fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">product Yield</fo:block>
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Conv.</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Cost </fo:block>
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
			                       			<fo:table-cell border-style="solid" >
			                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Butter FAT</fo:block>
			                            		<fo:block>
			                            			<fo:table>
			                            				<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="60pt"/>
								            			<fo:table-column column-width="60pt"/>
								            			<fo:table-body>
			            									<fo:table-row >
			            										<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Net Fat </fo:block>  
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">But </fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Yield </fo:block>
								                       			</fo:table-cell>
								                       			<fo:table-cell border-style="solid" >
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">But</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Conv</fo:block>
								                            		<fo:block   text-align="center" font-size="11pt" white-space-collapse="false">Cost Rs</fo:block>  
								                       			</fo:table-cell>
			            									</fo:table-row >
			            								</fo:table-body>	
			                            			</fo:table> 
			                            		</fo:block>  
			                       			</fo:table-cell>
		                       			</fo:table-row >
		                       			<fo:table-row >
		                                <fo:table-cell>
		                            		<fo:block >&#160; </fo:block>  
		                       			</fo:table-cell>
		                       			</fo:table-row >	
		                       			<#assign transfersList = unionWiseProductAbstract.get("tankerList")>
		                       			<#if transfersList?has_content>
			                       			<#list  transfersList as transfer>
			                       				<#assign prodConvKey = transfer.get("prodConvKey")>
			                       				<#assign recdQty = transfer.get("recdQty")>
			                       				<#assign recdFat = transfer.get("recdFat")>
			                       				<#assign recdSnf = transfer.get("recdSnf")>
			                       				<#assign recdKgFat = transfer.get("recdKgFat")>
			                       				<#assign recdKgSnf = transfer.get("recdKgSnf")>
			                       				<#assign prodQty = transfer.get("prodQty")>
			                       				<#assign prodFat = transfer.get("prodFat")>
			                       				<#assign prodSnf = transfer.get("prodSnf")>
			                       				<#assign prodKgFat = transfer.get("prodKgFat")>
			                       				<#assign prodKgSnf = transfer.get("prodKgSnf")>
			                       				<#assign prodTs = transfer.get("prodTs")>
			                       				<#assign conSugarAddn = transfer.get("conSugarAddn")>
			                       				<#assign prodTotTs = transfer.get("prodTotTs")>
			                       				<#assign conTsLoss = transfer.get("conTsLoss")>
			                       				<#assign prodNetTs = transfer.get("prodNetTs")>
			                       				<#assign prodYieldVal = transfer.get("prodYield")>
			                       				<#assign prodAmount = transfer.get("prodAmount")>
			                       				<#assign diffFat = transfer.get("diffFat")>
			                       				<#assign butterYield = transfer.get("butterYield")>
			                       				<#assign butterAmount = transfer.get("butterAmount")>
			                       				<#if tankerNo == "TOTAL" >
			                       					<fo:table-row >
					                                <fo:table-cell>
					                            		<fo:block >&#160; </fo:block>  
					                       			</fo:table-cell>
					                       			</fo:table-row >	
			                       				</#if>
			                       				<fo:table-row >
					                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                   <fo:block  text-align="center" font-size="9pt" white-space-collapse="false">${prodConvKey} </fo:block>  
										             </fo:table-cell>
					                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
								                    	<fo:table-column column-width="65pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="55pt"/>
										            			<fo:table-body>
					            									<fo:table-row >
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdQty?if_exists?string('##0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdFat?if_exists?string('##0.0')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdKgFat?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${recdKgSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
										                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="30pt"/>
								            			<fo:table-column column-width="30pt"/>
								                    	<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="55pt"/>
								            			<fo:table-column column-width="60pt"/>
								                    	<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="60pt"/>
								            			<fo:table-column column-width="50pt"/>
								            			<fo:table-column column-width="60pt"/>
										            			<fo:table-body>
					            									<fo:table-row >
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodQty?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodFat?if_exists?string('##0.0')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodKgFat?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodKgSnf?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                       				<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodTs?if_exists?string('##0.00')}</fo:block>
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${conSugarAddn?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodTotTs?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${conTsLoss?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodNetTs?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodYieldVal?if_exists?string('##0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if> >
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${prodAmount?if_exists?string('##0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
					                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
					                            		<fo:block>
					                            			<fo:table>
					                            				<fo:table-column column-width="50pt"/>
								                    	<fo:table-column column-width="60pt"/>
								            			<fo:table-column column-width="60pt"/>
										            			<fo:table-body>
					            									<fo:table-row >
					            										<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${diffFat?if_exists?string('#0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${butterYield?if_exists?string('#0.00')} </fo:block>  
										                       			</fo:table-cell>
										                       			<fo:table-cell <#if prodConvKey=="TOTAL"> border-style="solid" </#if>>
										                            		<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${butterAmount?if_exists?string('#0.00')}</fo:block>  
										                       			</fo:table-cell>
					            									</fo:table-row >
					            								</fo:table-body>	
					                            			</fo:table> 
					                            		</fo:block>  
					                       			</fo:table-cell>
				                       			</fo:table-row >	
			                       			</#list>
		                       			</#if>
			            			</fo:table-body>
					        	</fo:table>
					        </fo:block>
		          		
		          </#if>
				</fo:flow>
		 	</fo:page-sequence>
		 <#else>
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			 		<fo:block font-size="14pt">
						NO RECORDS FOUND
			 		</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</#if>  
	</#if>
</fo:root>
</#escape>

