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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#assign size =0>
        <#assign centerDetails =0>
        <#if agentEntryDetails?has_content>
            	<#assign dayTotalsEntries = agentEntryDetails.entrySet()>  
            	<#assign agentDetails =0>
    	<#list dayTotalsEntries as dayTotalsEntry>  
    	  	<#if dayTotalsEntry.getKey() !="totalQtyLtrs" && dayTotalsEntry.getKey() !="totalQtyKgs" && dayTotalsEntry.getKey() !="totalKgFat" && dayTotalsEntry.getKey() !="totalKgSnf" && dayTotalsEntry.getKey() !="totalPrice">          
				<#assign agentDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", dayTotalsEntry.getKey())).get("unitFacility")>
				<#assign centerDetails = delegator.findOne("Facility", {"facilityId" : dayTotalsEntry.getKey()}, true)>
		  	</#if> 	
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : centerDetails.get("parentFacilityId")}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">&#160;                    ${centerDetails.get("facilityName")}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" keep-together="always">MP Payment Payable Details From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}  PAGE NO:<fo:page-number/></fo:block>
        		<fo:block font-size="8pt">------------------------------------------------------------------------------</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">ROUTE : ${routeDetails.get("facilityCode")} ${routeDetails.get("facilityName")}            Representative : ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, centerDetails.get("ownerPartyId"), false))),18)}</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">UNIT  : ${agentDetails.get("facilityCode")} ${agentDetails.get("facilityName")}           Village : ${centerDetails.get("facilityCode")} ${centerDetails.get("facilityName")}</fo:block>	 	 	  
        		<fo:block font-size="8pt">------------------------------------------------------------------------------</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="7pt">&#160;Dt Shf  QTY.Kgs QTY.Lts ST FAT%  SNF% Fat(Kg) Snf(Kg)  COMMN   Tot.Amt</fo:block>
        		<fo:block font-size="8pt">------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
         <#assign dayTotalValues= dayTotalsEntry.getValue().entrySet()> 
        <#list dayTotalValues as dayTotals>  
        	<#assign dayWiseAgentTotals= dayTotals.getValue().entrySet()>   
        	     	
       	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 	 
       		<fo:block>MILK TYPE : ${parameters.productName}</fo:block>  
           <#list dayWiseAgentTotals as dayWiseTotals>	   			
            <fo:block>
            	<fo:table>
                    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>
               	    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="30pt"/>                             	          			           
                    <fo:table-body>  
                    	<fo:table-row>                    		
	                    	<fo:table-cell>	
	                        <#assign dayWiseTotalsEntries = (dayWiseTotals.getValue()).entrySet()>                            	
	                        	<#list dayWiseTotalsEntries  as dayWiseTotalsEntry>
	                       			<#if dayWiseTotalsEntry.getKey() !="TOT">                      
	                           			<fo:block font-size="8pt">
	                           				<fo:table>
                    							<fo:table-column column-width="5pt"/>
                    							<fo:table-column column-width="15pt"/>
                    							<fo:table-column column-width="15pt"/>
                    							<fo:table-column column-width="30pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="18pt"/>
                    							<fo:table-column column-width="30pt"/>
                    							<fo:table-column column-width="33pt"/>
                    							<fo:table-column column-width="37pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="47pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="37pt"/>
                    							<fo:table-column column-width="35pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="42pt"/>
                    							<fo:table-column column-width="50pt"/>
                    							 <fo:table-body>                
                    						<#if dayWiseTotals.getKey() !="TOT">     							
                    							 <#list procurementProductList as procProducts>                    							 	
                    								<fo:table-row>
	                    								<fo:table-cell></fo:table-cell>                    								            									
	                        						<#if  parameters.productName == procProducts.productName>
	                        						<#assign size = dayWiseTotalsEntries.size()>
	                        							<#assign billReportEntries = (dayWiseTotalsEntry.getValue()).get(parameters.productName)>
	                        							<#if billReportEntries.get("qtyKgs") !=0>
	                        							<fo:table-cell >	
	                        								<fo:block text-align="left" >${dayWiseTotals.getKey().substring(8)}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell >	
	                        								<fo:block >${dayWiseTotalsEntry.getKey()}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyKgs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>	                        							               	  
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("qtyLtrs"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">G</fo:block>
	                        							</fo:table-cell>	                        							
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(billReportEntries.get("fat"))?string("##0.0")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(billReportEntries.get("snf"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${((billReportEntries.get("qtyKgs")*billReportEntries.get("fat"))/100)?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${((billReportEntries.get("qtyKgs")*billReportEntries.get("snf"))/100)?string("##0.00")}</fo:block>
	                        							</fo:table-cell>
	                        							<fo:table-cell>
	                        									<fo:block text-align="right">${(agentWiseCommnMap[centerDetails.get("facilityId")].get(procProducts.productName)*billReportEntries.get("qtyLtrs"))?string("##0.00")}</fo:block>	 
	                        							</fo:table-cell>	                        							
	                        							<fo:table-cell>
	                        								<fo:block text-align="right">${(billReportEntries.get("price"))?string("##0.00")}</fo:block>
	                        							</fo:table-cell>	                        								                        						
	                        						</#if>	
	                        						</#if>
	                        						</fo:table-row>
	                        					</#list>
	                        				</#if>		
	                        					</fo:table-body>
	                        				</fo:table>			
	                           			</fo:block>
	                           		</#if> 	                           		              		
	                        	 </#list> 
	                        </fo:table-cell>	                        	
	                        </fo:table-row>	                          
                       		</fo:table-body>
                		</fo:table>
					</fo:block> 
					<#assign totalEntries=(dayWiseTotals.getValue()).get("TOT")>
					<#list procurementProductList as procProducts>
						<#if  parameters.productName == procProducts.productName>
							<#assign GrTotEntries= totalEntries.get(procProducts.productName)>				
						</#if>
					</#list>											
				</#list>														
					<fo:block font-size="8pt">------------------------------------------------------------------------------</fo:block>
				<#if GrTotEntries.get("qtyKgs") !=0>	
					<fo:block font-size="8pt">
						<fo:table>
							<fo:table-column column-width="15pt"/>
                    		<fo:table-column column-width="20pt"/>
                    		<fo:table-column column-width="30pt"/>
                    		<fo:table-column column-width="37pt"/>
                    		<fo:table-column column-width="20pt"/>
                    		<fo:table-column column-width="30pt"/>
                    		<fo:table-column column-width="31pt"/>
                    		<fo:table-column column-width="37pt"/>
                    		<fo:table-column column-width="37pt"/>
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="55pt"/>
                    		<fo:table-column column-width="35pt"/>
                    		<fo:table-column column-width="40pt"/>
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="52pt"/>
                    		<fo:table-column column-width="51pt"/>  
                    		<fo:table-column column-width="45pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="50pt"/>
                    		<fo:table-column column-width="50pt"/>                 		
                    		<fo:table-body>                    			
                    			<fo:table-row font-size="8pt">
                    				<fo:table-cell><fo:block >TOTAL</fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("qtyKgs"))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("qtyLtrs"))?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    				<fo:table-cell></fo:table-cell>                    				
                    				<fo:table-cell>
                    					<fo:block text-align="right"><#if GrTotEntries.get("qtyKgs") !=0>${((GrTotEntries.get("kgFat")*100)/GrTotEntries.get("qtyKgs"))?string("##0.00")}</#if></fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block text-align="right"><#if GrTotEntries.get("qtyKgs") !=0>${((GrTotEntries.get("kgSnf")*100)/GrTotEntries.get("qtyKgs"))?string("##0.00")}</#if></fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block text-align="right">${(GrTotEntries.get("kgFat").setScale(2,2))?string("##0.00")}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block text-align="right">${GrTotEntries.get("kgSnf")?string("##0.00")}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell>
                    					<fo:block text-align="right">${(agentWiseCommnMap[centerDetails.get("facilityId")].get(parameters.productName)*GrTotEntries.get("qtyLtrs"))?string("##0.00")}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell><fo:block text-align="right">${(GrTotEntries.get("price"))?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    				<fo:table-cell><fo:block font-size="8pt">------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    			</fo:table-row>                    		
                    			<#assign ltrCost =(Static["java.math.BigDecimal"].ZERO)>	
                       				<#if GrTotEntries.get("qtyKgs") !=0>
                       					<#assign ltrCost = (((GrTotEntries.get("price")+GrTotEntries.get("sPrice")))/(GrTotEntries.get("qtyKgs")+(GrTotEntries.get("sQtyLtrs")*1.03)))>                       					
                       				</#if>
                    			<fo:table-row>
                    				<fo:table-cell>
                    					<fo:block keep-together="always" white-space-collapse="false">Rate/Ltr  : ${ltrCost?string("##0.00")}</fo:block>
                    				</fo:table-cell>
                    				<fo:table-cell/> 
                    				<fo:table-cell/> 
                    				<fo:table-cell/>                    				
                    				<fo:table-cell>
                    					<fo:block keep-together="always" white-space-collapse="false">Net Amount Payable : ${Static["java.lang.Math"].round((GrTotEntries.get("price")-(GrTotEntries.get("totPrem"))))?if_exists?string("##0.00")}</fo:block>
                    				</fo:table-cell>
                    			</fo:table-row>
                    		</fo:table-body>					
						</fo:table>
					</fo:block>
					</#if>
            	</fo:flow>	
			</#list>	
			</fo:page-sequence>
		</#list> 
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