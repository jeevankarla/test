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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15.5in" margin-left="0.09in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         <#if shedWiseAmountAbstractMap?has_content>        
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">MILK SHED NAME :${parameters.shedId}                             FORTINIGHT CONSOLIDATED OF MILK PAYMENT, DEDUCTION, NET AMOUNT PAYABLE AND T.I.P DEDUCTION, SHARE-CAPITAL DEDUCTIONS PERIOD   FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMM d, yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMM d, yyyy")}</fo:block> 	 	  
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">   		           
                 <fo:block font-size="7pt">
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="5pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="55pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="40pt"/>
                    </#list>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>              
		          	<fo:table-header border-width="1pt" border-style="dotted">
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left" >NAME OF THE</fo:block><fo:block font-size="7pt" text-align="left">MCC/DAIRY</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">.                                                              DETAILS OF GROSS AMOUNT</fo:block><fo:block font-size="7pt" text-align="left">-----------------------------</fo:block></fo:table-cell>
		            	<#list procurementProductList as procProducts>
		            	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------------------</fo:block><fo:block font-size="7pt" text-align="right">${procProducts.brandName}</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>		                    	                  		            
	                    </#list>
		           		<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------------------</fo:block><fo:block font-size="7pt" text-align="right">COMSN</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		           		<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------------------</fo:block><fo:block font-size="7pt" text-align="right">OP-COST</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">--------------------</fo:block><fo:block font-size="7pt" text-align="right">CART</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">---------------------------------------</fo:block><fo:block font-size="7pt" text-align="right" >ADDN</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="right" >GROSS</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false">.                                                    DETAILS OF RECOVERIES</fo:block><fo:block font-size="7pt" text-align="left">--------------------</fo:block></fo:table-cell>
		               	<#list orderAdjItemsList as orderAdjItems>
		               		<#assign orderAdjustmentDesc=Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getShedOrderAdjustmentDescription( dctx,Static["org.ofbiz.base.util.UtilMisc"].toMap("shedId",parameters.shedId)).get("shedAdjustmentDescriptionMap")>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="center">--------------------</fo:block><fo:block font-size="7pt" text-align="right" >${orderAdjustmentDesc.get(orderAdjItems.orderAdjustmentTypeId)?if_exists}</fo:block><fo:block font-size="7pt" text-align="right">AMT</fo:block></fo:table-cell>
		                </#list>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="left">-------------------</fo:block><fo:block font-size="7pt" text-align="right" >TOTAL</fo:block><fo:block font-size="7pt" text-align="right">Dedn's Amount</fo:block></fo:table-cell>
		               	<fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="right" >NET</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
		                <fo:table-cell><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="7pt" text-align="right" >T.I.P</fo:block><fo:block font-size="7pt" text-align="right">AMOUNT</fo:block></fo:table-cell>
				    </fo:table-header>	
				  <fo:table-body>
				  	<fo:table-row><fo:table-cell></fo:table-cell></fo:table-row>
				  </fo:table-body>
				  </fo:table>
				 </fo:block>   
				   <fo:block font-size="7pt">
                 	<fo:table>
                    <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="5pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="55pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="40pt"/>
                    </#list>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="45pt"/> 	           
                    <fo:table-body>
	                    <#assign unitDetail = shedWiseAmountAbstractMap.entrySet()>
	                    <#list unitDetail as unitData>
	                    <#assign unitAmountAbstractData = unitData.getValue()>
	                    <#if unitData.getKey() == "TOTAL">
	                    	<fo:table-row>	
            					<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="left" >------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        	</fo:table-cell>
	                        </fo:table-row>	
	                    </#if>
	                    	<#if unitAmountAbstractData.get("netAmount")!=0>
	                    	<fo:table-row>
	                    		<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                    	</fo:table-row>
            				<fo:table-row>	
            					<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="left" keep-together="always" font-weight="bold">${Static["org.ofbiz.order.order.OrderServices"].nameTrim(unitAmountAbstractData.get("unitName"),18)}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>
	                        	</fo:table-cell>
	                        	<#list procurementProductList as procProducts>
	                        	<fo:table-cell >
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAmountAbstractData.get(procProducts.brandName+"Amount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
								</#list>	                        	
	                            <fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAmountAbstractData.get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAmountAbstractData.get("opCost")?if_exists?string("##0.00")}</fo:block>
	                        	</fo:table-cell>
	                        	<fo:table-cell >	
	                        		<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAmountAbstractData.get("cartage")?if_exists?string("##0.00")}</fo:block>
	                            </fo:table-cell> 
	                        	<fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" keep-together="always" font-weight="bold">${unitAmountAbstractData.get("AddnTot")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${unitAmountAbstractData.get("grossAmount")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell> 
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"></fo:block>                               
	                            </fo:table-cell>
	                            <#list orderAdjItemsList as orderAdjItems>
	                            <#assign deductionType = unitAmountAbstractData.get(orderAdjItems.orderAdjustmentTypeId)>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" font-weight="bold">${deductionType?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <#assign deductionType = 0>
	                             </#list>
	                        	<fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" keep-together="always" font-weight="bold">${unitAmountAbstractData.get("DednsTot")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                        	<fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" keep-together="always" font-weight="bold">${unitAmountAbstractData.get("netAmount")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell>     
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right" keep-together="always" font-weight="bold">${unitAmountAbstractData.get("tipAmount")?if_exists?string("##0.00")}</fo:block> 
	                            </fo:table-cell> 
				            </fo:table-row>
				            </#if>	    
				            </#list> 
                    </fo:table-body>
                </fo:table>
               </fo:block>      
           </fo:flow>
        </fo:page-sequence>
        <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 		<fo:block font-size="14pt">
	            			No Shed Has Been Selected.......!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
     </fo:root>
</#escape>