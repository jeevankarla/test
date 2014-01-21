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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<#assign basicValGtot = 0>
<#assign vatValGtot = 0>
<#assign totValGtot = 0>
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                              MARKETING UNIT: METRO PRODUCTS: NANDANAM: CHENNAI=35</fo:block>				
	              	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                             FREE GIFT ABSTRACT FOR THE MONTH OF: ${parameters.customTimePeriodId}             ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
	            	<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
	              	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SL.NO    SUPPLY         PARTY NAME            QUANTITY     RATE          BASIC           VAT            TOTAL</fo:block>
	              	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160;         DATE                                                           VALUE           VALUE          VALUE </fo:block>
              		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="60pt"/> 
               	    <fo:table-column column-width="67pt"/>
            		<fo:table-column column-width="55pt"/> 		
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="62pt"/>
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="72pt"/>
                    <fo:table-body>
                    <#assign temp = 0>
                    <#list prodList as prod><#assign product = delegator.findOne("Product", {"productId" :prod}, true)>
                    <fo:table-row>
               	     	<fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${prod?if_exists}    ${product.get("productName")?if_exists}</fo:block>  
	                        </fo:table-cell>
                	</fo:table-row>
                    <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                    	<#assign currentProd = productsPrice.get(prod)>
                    	<#assign prodVat = currentProd.get("VAT")>
                    	<#assign prodRate = currentProd.get("totalAmount")>
                    	<#assign prodBasicPrice = currentProd.get("basicPrice")+currentProd.get("BEDCESS")+currentProd.get("BEDSECCESS")+currentProd.get("BED")>
                    	<#assign prodDataList = totalsMap.get(prod)>
                    	<#list prodDataList as prodData>
                    	<#assign temp = temp+1>
                    	<#assign basicVal = 0>
                    	<#assign totVal = 0>
                    	<#assign vatVal = 0>
						<fo:table-row>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${temp?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(prodData.get("supplyDate"), "dd/MM/yyyy")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${prodData.get("partyName")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${prodData.get("quantity")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                       <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${prodRate?if_exists?string("#0.00")}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#assign basicVal = prodBasicPrice*prodData.get("quantity")>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicVal?if_exists?string("#0.00")}</fo:block>  
	                        	<#assign basicValGtot = basicValGtot+basicVal>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#assign vatVal = prodVat*prodData.get("quantity")>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${vatVal?if_exists?string("#0.00")}</fo:block> 
	                            <#assign vatValGtot = vatValGtot+vatVal> 
	                        </fo:table-cell>
	                         <fo:table-cell>
	                         	<#assign totVal = prodRate*prodData.get("quantity")>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totVal?if_exists?string("#0.00")}</fo:block>  
	                        	<#assign totValGtot = totValGtot+totVal>
	                        </fo:table-cell>
	                    </fo:table-row> 
	                    </#list>
	                     <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
	                    <fo:table-row>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt"  white-space-collapse="false">&lt; &lt;  PRODUCTWISE TOTAL - (${prod})  &gt; &gt; </fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${prodTotalMap.get(prod)?if_exists}</fo:block>  
	                        </fo:table-cell>
	                       <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#assign basicVal = prodBasicPrice*prodTotalMap.get(prod)>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicVal?if_exists?string("#0.00")}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#assign vatVal = prodVat*prodTotalMap.get(prod)>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${vatVal?if_exists?string("#0.00")}</fo:block> 
	                        </fo:table-cell>
	                         <fo:table-cell>
	                         	<#assign totVal = prodRate*prodTotalMap.get(prod)>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totVal?if_exists?string("#0.00")}</fo:block>  
	                        </fo:table-cell>
	                    </fo:table-row> 
	                     <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
	                 </#list>   
                	<fo:table-row>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">&lt; &lt; GRAND TOTAL &gt; &gt; </fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false"></fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${basicValGtot?if_exists?string("#0.00")}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${vatValGtot?if_exists?string("#0.00")}</fo:block> 
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totValGtot?if_exists?string("#0.00")}</fo:block>  
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>