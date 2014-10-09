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
         ${setRequestAttribute("OUTPUT_FILENAME", "ShedAbstract.txt")}
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left="0.09in">
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         <#if regionDetailMap?has_content>        
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block font-size="6pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-103</fo:block>
        		<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">&#160;                                               YEARLY CONSOLIDATED REPORT OF MILK PAYMENT, DEDUCTIONS, NET AMOUNT PAYABLE AND T.I.P DEDUCTION, SHARE-CAPITAL DEDUCTIONS PERIOD   FROM: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}   TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block> 
        		<fo:block font-size="10pt" white-space-collapse="false" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 
        		<fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block font-size="10pt" white-space-collapse="false" text-align="left">&#160;                                         DETAILS OF GROSS AMOUNT                                                                                     DETAILS OF RECOVERIES                                                                                                OTHER DEDUCTIONS</fo:block>
        		<fo:block font-size="10pt" white-space-collapse="false" text-align="left">------------------------------------------------------------------------------------------------------------  ------------------------------------------------------------------------------------------------------------------------------------------------------  ------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                   <fo:block font-size="5pt">
                 	<fo:table>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="20pt"/>  
               	    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="50pt"/>  
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="42pt"/>
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="37pt"/>
                    </#list>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="49pt"/>
                    <fo:table-column column-width="50pt"/>           
		          	<fo:table-header>
		            	<fo:table-cell><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block><fo:block font-size="5pt" text-align="left" >&#160;SHED NAME</fo:block></fo:table-cell>		                    	                  
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
                    		<fo:block font-size="5pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    	</fo:table-cell>
                    </fo:table-row>
				  </fo:table-body>
		          </fo:table>
		      </fo:block>  
				 <fo:block>
                 	<fo:table>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="2pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="25pt"/>  
                    <fo:table-column column-width="45pt"/>                                    
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="37pt"/>
                    </#list>                     
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>   
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="38pt"/>
                    <fo:table-column column-width="32pt"/>
                    <#assign regionEntries = regionDetailMap.entrySet()>
                    	<fo:table-body>
                    	<#list regionEntries as RegionNames>
                    		<#list RegionNames.getValue() as shedNames>
				               <#assign shedDetail = shedMap.entrySet()>
			                 	<#list shedDetail as shedData>
			                 		<#if shedData.getKey()==shedNames>
					                	<#assign shedPaymentData = shedData.getValue()>
					                	<fo:table-row>
				                    		<fo:table-cell><fo:block  font-size="5pt" linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
				                    	</fo:table-row>	
				                        <fo:table-row>
				                            <fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="left">${shedData.getKey()?if_exists}</fo:block>
				                        	</fo:table-cell>
				                       		<#list procurementProductList as procProducts>
				                        	<fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get(procProducts.productId).get("price")?if_exists}</fo:block>
				                        	</fo:table-cell>
				                            </#list>	
				                        	<fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                            <fo:table-cell >	
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                            <fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                            <fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                            <#list orderAdjItemsList as orderAdjItems>
				                        	<#assign reqAdjustments = shedPaymentData.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)>
				                            <fo:table-cell >	
				                            	<fo:block font-size="5pt" text-align="right">${reqAdjustments?if_exists?string("##0.00")}</fo:block>                               
				                            </fo:table-cell>
					                        </#list>
				                             <fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell >
				                        		<fo:block font-size="5pt" text-align="right">${shedPaymentData.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
				                        	</fo:table-cell>
			                        	</fo:table-row>	
			                        </#if> 	
			                    </#list> 
				            </#list>        
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
              <fo:block font-size="5pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block>
                 	<fo:table>
                     <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="2pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="25pt"/>  
                    <fo:table-column column-width="45pt"/>                                    
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="37pt"/>
                    </#list>                     
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>   
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="38pt"/>
                    <fo:table-column column-width="32pt"/>
							<fo:table-body>
		                        <fo:table-row>
		                        <#assign regionDetail = regionWiseMap.entrySet()>
								<#list regionDetail as regionData>
									<#if regionData.getKey()==RegionNames.getKey()>
									<#assign regionPaymentData = regionData.getValue()>
			                            <fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="left">${regionData.getKey()?if_exists}</fo:block>
			                        	</fo:table-cell>
			                      		<#list procurementProductList as procProducts>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get(procProducts.productId).get("price")?if_exists}</fo:block>
			                        	</fo:table-cell>
			                       	    </#list>	
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<#list orderAdjItemsList as orderAdjItems>
			                        	<#assign totReqAdjustments = regionPaymentData.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)>
			                            <fo:table-cell >	
			                            	<fo:block font-size="5pt" text-align="right">${totReqAdjustments?if_exists?string("##0.00")}</fo:block>                               
			                            </fo:table-cell>
			                             </#list>
		                                <fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >
			                        		<fo:block font-size="5pt" text-align="right">${regionPaymentData.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
			                        	</fo:table-cell>
				                    </#if>         
	                			</#list>  	
	                             </fo:table-row>
				                <fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		           					</fo:table-cell>
		           				</fo:table-row>
				        </#list>        
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
               <fo:block>
                 	<fo:table>
                     <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="2pt"/>  
               	    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="25pt"/>  
                    <fo:table-column column-width="45pt"/>                                    
                    <#list orderAdjItemsList as orderAdjItems>
                    <fo:table-column column-width="37pt"/>
                    </#list>                     
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>   
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="38pt"/>
                    <fo:table-column column-width="32pt"/>
							<fo:table-body>
		                        <fo:table-row>
		                            <fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="left">TOTAL:</fo:block>
		                        	</fo:table-cell>
		                      		<#list procurementProductList as procProducts>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get(procProducts.productId).get("price")?if_exists}</fo:block>
		                        	</fo:table-cell>
		                       	    </#list>	
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("commissionAmount")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >	
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("opCost")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("cartage")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("grsAddn")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<#list orderAdjItemsList as orderAdjItems>
		                        	<#assign totReqPaymentAdjustments = grandTotMap.get("TOT").get(orderAdjItems.orderAdjustmentTypeId)>
		                            <fo:table-cell >	
		                            	<fo:block font-size="5pt" text-align="right">${totReqPaymentAdjustments?if_exists?string("##0.00")}</fo:block>                               
		                            </fo:table-cell>
		                             </#list>
		                            <fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("grsDed")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("netAmt")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
		                        	<fo:table-cell >
		                        		<fo:block font-size="5pt" text-align="right">${grandTotMap.get("TOT").get("tipAmt")?if_exists?string("##0.00")}</fo:block>
		                        	</fo:table-cell>
	                           </fo:table-row>
				                <fo:table-row>
		           					<fo:table-cell>
		           						<fo:block font-size="5pt" text-align="left">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		           					</fo:table-cell>
		           				</fo:table-row>
                    	</fo:table-body>
                	</fo:table>
               </fo:block>
                <fo:block font-size="6pt">VST_ASCII-012 VST_ASCII-027VST_ASCII-080</fo:block>
           </fo:flow>
        </fo:page-sequence>		 <#else>
			<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 		<fo:block font-size="14pt">
            			${uiLabelMap.OrderNoOrderFound}.
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>	
		</#if> 
     </fo:root>
</#escape>