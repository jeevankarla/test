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
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         <#if finalMap?has_content> 
           ${setRequestAttribute("OUTPUT_FILENAME", "shedAnnualAbst.txt")}       
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block font-size="5pt" >VST_ASCII-015 VST_ASCII-027VST_ASCII-103</fo:block>
        		<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
        		<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">MILK SHED NAME :${facility.facilityName}                             FORTINIGHT CONSOLIDATED OF MILK PAYMENT, DEDUCTION, NET AMOUNT PAYABLE AND T.I.P DEDUCTION, SHARE-CAPITAL DEDUCTIONS PERIOD   FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block> 	 
        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block font-size="10pt" white-space-collapse="false" text-align="left">&#160;                                         DETAILS OF GROSS AMOUNT                                                                                     DETAILS OF RECOVERIES                                                                                                OTHER DEDUCTIONS</fo:block>
        		<fo:block font-size="10pt" white-space-collapse="false" text-align="left">------------------------------------------------------------------------------------------------------------  ------------------------------------------------------------------------------------------------------------------------------------------------------  ------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">   		           
                 <fo:block font-size="5pt">
                 	<fo:table>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="20pt"/>  
               	    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="50pt"/>  
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="33pt"/>  
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="42pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="37.2pt"/>
                    </#list>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="49pt"/>
                    <fo:table-column column-width="50pt"/>           
		          	<fo:table-header>
		            	<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="left" >&#160;NAME OF THE</fo:block><fo:block font-size="5pt" text-align="left">&#160;MCC/DAIRY</fo:block></fo:table-cell>		                    	                  
		           		<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
		            	<#list procurementProductList as procProducts>
		            	 		<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center">${procProducts.brandName}</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>		                    	                  		                        
	                    </#list>
		           		<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center">COMSN</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		           		<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center">OP-COST</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center">CART</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center" >ADDN</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center" >GROSS</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		               	<#list orderAdjItemsList as orderAdjItems>
		               			<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="right" >${orderAdjItems.description?if_exists}</fo:block><fo:block font-size="5pt" text-align="right">AMT</fo:block></fo:table-cell>
		                </#list>
		                <fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center" >TOTAL</fo:block><fo:block font-size="5pt" text-align="center">Dedn's</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		               	<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center" >NET</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="center" >T.I.P</fo:block><fo:block font-size="5pt" text-align="center">AMOUNT</fo:block></fo:table-cell>
				    </fo:table-header>	          	
				  <fo:table-body>
				  <fo:table-row><fo:table-cell></fo:table-cell></fo:table-row>
				  	<fo:table-row>	
    					<fo:table-cell >	
                    		<fo:block font-size="5pt" text-align="left">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    	</fo:table-cell>
                    </fo:table-row>
				  </fo:table-body>
		          </fo:table>
		      </fo:block>   
				   <fo:block>
                 	<fo:table>
               	    <fo:table-column column-width="42pt"/>
                    <fo:table-column column-width="5pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="55pt"/>  
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="32pt"/>
                    <fo:table-column column-width="44pt"/>
                    <fo:table-column column-width="8pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="36.5pt"/>
                    </#list>
                    <fo:table-column column-width="46pt"/>
                    <fo:table-column column-width="54pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="38pt"/>
                    <fo:table-column column-width="32pt"/>
                    <fo:table-body>
	                    <#assign unitDetail = finalMap.entrySet()>
			                <#list unitDetail as unitData>
			                <#assign unitAnnualAbstractData = unitData.getValue()>
	                     <#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : unitData.getKey()}, true)>
            				<fo:table-row>
	                    		<fo:table-cell><fo:block  font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                    	</fo:table-row>		
            				<fo:table-row>
            					<fo:table-cell >
            			   			<fo:block font-size="5pt" text-align="left" keep-together="always" font-weight="bold">${Static["org.ofbiz.order.order.OrderServices"].nameTrim(facilityDetails.get("facilityName"),15)}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt"></fo:block>
	                        	</fo:table-cell>
	                        	<#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="5pt" text-align="right">${unitAnnualAbstractData.get(procProducts.productId).get("price")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
								</#list>	                        	
	                            <fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
	                            </fo:table-cell> 
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">&#xA;</fo:block>
	                        	</fo:table-cell>
	                            <#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" font-weight="bold">${unitAnnualAbstractData.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${unitAnnualAbstractData.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell> 
				            </fo:table-row>	
				            </#list> 
                    </fo:table-body>
                </fo:table>
               </fo:block> 
               <fo:block font-size="5pt">
                <fo:table>
                 	 <fo:table-column column-width="42pt"/>
                    <fo:table-column column-width="5pt"/>  
               	    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="53pt"/>  
                    <fo:table-column column-width="18pt"/>
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="38pt"/>  
                    <fo:table-column column-width="32pt"/>
                    <fo:table-column column-width="46pt"/>
                    <fo:table-column column-width="8pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="36.4pt"/>
                    </#list>
                    <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="53pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="38pt"/>
                    <fo:table-column column-width="32pt"/>          
                    <fo:table-body>
	                    	<fo:table-row>	
            					<fo:table-cell >	
		                    		<fo:block font-size="5.9pt" text-align="left">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                    	</fo:table-cell>
	                        </fo:table-row>	
            				<fo:table-row>	
            					<fo:table-cell >	
	                        		<fo:block font-size="5pt"   font-weight="bold">TOTAL:</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("BMprice")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >
	                        		<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("CMprice")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("opCost")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("cartage")?if_exists?string("##0.00")}</fo:block>
	                            </fo:table-cell> 
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("grsAddn")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("grossAmt")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell> 
	                            <fo:table-cell >	
	                        		<fo:block font-size="5pt" text-align="right" font-weight="bold">&#xA;</fo:block>
	                        	</fo:table-cell>
	                            <#list orderAdjItemsList as orderAdjItems>
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get(orderAdjItems.orderAdjustmentTypeId)?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                             </#list>
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("grsDed")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                        	<fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("netAmt")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="5pt" text-align="right" keep-together="always" font-weight="bold">${shedWiseTotalsMap.get("tipAmt")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell> 
				            </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>  
               <fo:block font-size="5pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
           </fo:flow>
        </fo:page-sequence>
        <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt">
	            			No Shed Has Been Selected.......!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
     </fo:root>
</#escape>