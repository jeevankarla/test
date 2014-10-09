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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left="0.09in">
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "fncollection.txt")}
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
        	<fo:static-content flow-name="xsl-region-before">
                <fo:block font-size="10pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>        	
        		<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
				<#assign centerFacility = delegator.findOne("Facility", {"facilityId" : centerFacilityId}, true)>
				<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block font-size="7.5pt" text-align="center" white-space-collapse="false" keep-together="always">YEARLY CONSOLIDATED REPORT OF MILK PAYMENT,DEDUCTIONS,NET AMOUNT PAYABLE AND T.I.P DEDUCTIONS,SHARE-CAPITAL DEDUCTIONS PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
				<fo:block font-size="7.5pt" text-align="center" white-space-collapse="false" keep-together="always">MILK SHED NAME:  ${parameters.shedId}  			  				          &#160; UNIT NAME:${facility.getString("facilityName")?if_exists}                          MCC NAME:  ${centerFacility.getString("facilityName")?if_exists}              </fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                 <fo:block font-size="7pt">
                 <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <fo:table >
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="0.1pt"/> 
               	    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="47pt"/>
                    </#list>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="5pt"/>
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="50pt"/>       
                    <fo:table-column column-width="40pt"/>             
		          	<fo:table-header border-width="1pt" >
		          	    
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left" >MONTH</fo:block><fo:block font-size="7pt" text-align="left">YEAR</fo:block></fo:table-cell>	
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block></fo:block></fo:table-cell>	                    	                  
		            	<fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">.            DETAILS OF GROSS AMOUNT</fo:block></fo:table-cell>
		            	<#list procurementProductList as procProducts>
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------</fo:block><fo:block font-size="7pt" text-align="right">${procProducts.brandName}</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>		                    	                  		            
	                    </#list>
		           		<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">COMSN</fo:block><fo:block font-size="7pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">OP</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center" >CART</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="center">ADDN</fo:block><fo:block font-size="7pt" text-align="left">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">------</fo:block><fo:block font-size="7pt" text-align="left" >&#160;GROSS</fo:block><fo:block font-size="7pt" text-align="left">&#160;AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                           DETAILS OF RECOVERIES</fo:block><fo:block font-size="7pt" text-align="left"></fo:block>----</fo:table-cell>
		               	<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",parameters.shedId)).get("shedAdjustmentDescriptionMap")>
		               	<#list orderAdjItemsList as orderAdjItems>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">----------</fo:block><fo:block font-size="7pt" text-align="left" >${orderAdjItems.description?if_exists}</fo:block><fo:block font-size="7pt" text-align="left">AMT</fo:block></fo:table-cell>
		                </#list>
		                <fo:table-cell><fo:block font-size="2pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block text-align="left" keep-together="always" white-space-collapse="false">.   OTHER DEDUCTIONS</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">---------------------------</fo:block><fo:block font-size="7pt" text-align="right" >TOTAL</fo:block><fo:block font-size="7pt" text-align="right">Dedn's Amount</fo:block></fo:table-cell>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="right" >NET</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="center" >T.I.P</fo:block><fo:block font-size="7pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
				    </fo:table-header>	
				  	<fo:table-body>
				  	 	<fo:table-row> <fo:table-cell><fo:block></fo:block></fo:table-cell></fo:table-row>
                    </fo:table-body>
				  </fo:table>
				   <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				 </fo:block>   
				 <fo:block font-size="7pt">
                 	<fo:table>
                    	<fo:table-column column-width="60pt"/>
                    	<fo:table-column column-width="50pt"/>  
               	    	<fo:table-column column-width="45pt"/>
               	    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="40pt"/>  
                    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="50pt"/>  
                    	<fo:table-column column-width="55pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<#list orderAdjItemsList as orderAdjItems>
                    		<fo:table-column column-width="47pt"/>
                    	</#list>
                    	<fo:table-column column-width="43pt"/>
                    	<fo:table-column column-width="36pt"/>
                    	<fo:table-body>
		                    <#assign unitDetail = finalMap.entrySet()>
			                <#list unitDetail as unitData>
			                <#assign unitAnnualAbstractData = unitData.getValue()>
	                        <fo:table-row>
	                        		<#assign periodBilling = delegator.findOne("PeriodBilling", {"periodBillingId" : unitData.getKey()}, true)>
           							<#assign customTimePeriodId = periodBilling.get("customTimePeriodId")>
           							<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, true)>
           							<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd/MM/yyyy")/>
                			 		<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "MMMdd yyyy")/>	
	                            <fo:table-cell >
	                        		<fo:block font-size="6pt" text-align="left" font-weight="bold">${fromDate}</fo:block>
	                        	</fo:table-cell>
	                       <#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get(procProducts.productId).get("price")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                         </#list>	
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                             <fo:table-cell >
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="6.9pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>	
						     </#list>                  
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
               <fo:block font-size="7pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block font-size="7pt">
                 	<fo:table>
                    	<fo:table-column column-width="30pt"/>
                    	<fo:table-column column-width="80pt"/>
                    	<fo:table-column column-width="45pt"/> 
               	    	<fo:table-column column-width="35pt"/>
                    	<fo:table-column column-width="40pt"/> 
                    	<fo:table-column column-width="35pt"/> 
                    	<fo:table-column column-width="45pt"/>
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="55pt"/> 
                    	 <fo:table-column column-width="49pt"/> 
                    	<fo:table-column column-width="50pt"/>
                    	<fo:table-column column-width="45pt"/>
                    	<#list orderAdjItemsList as orderAdjItems>
                    		<fo:table-column column-width="47pt"/>
                    	</#list>
                    	<fo:table-column column-width="40pt"/>
                    	<fo:table-column column-width="40pt"/>
                    	<fo:table-column column-width="40pt"/>
                    	<fo:table-body>
		                    <#assign totalValue = totalValuesMap.entrySet()>
	                         <fo:table-row>
	                            <fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="left" font-weight="bold">TOTAL:</fo:block>
	                        	</fo:table-cell>
	                      <#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get(procProducts.productId).get("price")?if_exists}</fo:block>
	                        	</fo:table-cell>
	                         </#list>	
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                             <fo:table-cell >
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="6.5pt" text-align="right" font-weight="bold">${totalValuesMap.get("TOT").get("tipAmt")?if_exists?string("##0.00")}&#160;&#160;</fo:block>
	                        	</fo:table-cell> 
	                        </fo:table-row>	
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
               <fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
           </fo:flow>
        </fo:page-sequence>
        <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt">
	            			No order Found.......!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
</#if>
     </fo:root>
</#escape>