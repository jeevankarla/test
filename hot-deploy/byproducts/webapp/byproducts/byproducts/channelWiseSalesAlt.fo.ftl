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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "channelWiseDespatch.txt")}
       <#if productMap?has_content>
       	        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              ${uiLabelMap.aavinDairyMsg}</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                                 MARKETING UNIT, METRO PRODUCTS DIVISON, NANDANAM ,CHENNAI-35.</fo:block>
                    	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                         PRODUCTS CHANNELWISE SALES STATEMENT FOR THE MONTH OF :: ${month?if_exists}</fo:block>
              			<fo:block font-size="7pt" align-text="left">===================================================================================================================================================================================================</fo:block> 
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;SL PROD   PRODUCT                    WSD              FRO               MCCS              DEWS             PARLOUR           CREDIT           AVM FRO           OTHER            TOTAL </fo:block>
            			<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;NO CODE    NAME                   QTY   VALUE     QTY    VALUE      QTY     VALUE      QTY    VALUE       QTY   VALUE      QTY   VALUE     QTY   VALUE      QTY     VALUE     QTY     VALUE  </fo:block>	 	 	  
		        		<fo:block font-size="7pt" align-text="left">===================================================================================================================================================================================================</fo:block> 
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="30pt"/> 
               	    <fo:table-column column-width="85pt"/>
            		<fo:table-column column-width="30pt"/> 	
            		<fo:table-column column-width="45pt"/>	
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/> 
               	    <fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/> 	
            		<fo:table-column column-width="30pt"/>	
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="45pt"/>
            		<fo:table-column column-width="30pt"/> 
               	    <fo:table-column column-width="45pt"/>
                        <fo:table-body>
                        <#assign WSD_Total = 0>
                        <#assign FRO_Total = 0>
                        <#assign MCC_Total = 0>
                        <#assign DEW_Total = 0>
                        <#assign PAR_Total = 0>
                        <#assign CRD_Total = 0>
                        <#assign AVM_Total = 0>
                        <#assign OTH_Total = 0>
                        <#assign TOT_Total = 0>
                        <#assign eachProdSale = productMap.entrySet()>
       					<#assign serial = 1>
       					<#list eachProdSale as productSaleDetail>
                        <#assign productSaleQty = productSaleDetail.getValue().entrySet()>
                        <#assign productTotalQuant = 0>
                        <#assign productTotalValue = 0>
            			<fo:table-row>
               				<fo:table-cell>
                           		<fo:block font-size="7pt" align-text="left">${serial?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="7pt" align-text="left">${productSaleDetail.getKey()?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="7pt" align-text="left">${productNames.get(productSaleDetail.getKey())?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<#list facilityCategoryList as eachCat>
                       			<#assign checkFlag = false>
                       			<#list productSaleQty as qtyValueMap>
                       				<#if qtyValueMap.getKey() == eachCat>
                       					<#assign qtyValue = qtyValueMap.getValue()>
                       					<fo:table-cell>
				            				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${qtyValue.get("quantity")?if_exists}</fo:block>  
				            			</fo:table-cell>
				            			<fo:table-cell>
				            				<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${qtyValue.get("value")?if_exists}</fo:block>  
				            			</fo:table-cell>
				            			<#if eachCat == 'MCCS'>
				            				<#assign MCC_Total = MCC_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'FROS'>
				            				<#assign FRO_Total = FRO_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'AVM_FROS'>
				            				<#assign AVM_Total = AVM_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'PARLOUR'>
				            				<#assign PAR_Total = PAR_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'INSTITUTIONS'>
				            				<#assign CRD_Total = CRD_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'DEWS_PARLOURS'>
				            				<#assign DEW_Total = DEW_Total+qtyValue.get("value")>
				            			<#elseif eachCat == 'WHOLESALE_DEALERS'>
				            				<#assign WSD_Total = WSD_Total+qtyValue.get("value")>
				            			<#else>
				            				<#assign OTH_Total = OTH_Total+qtyValue.get("value")>
				            			</#if>
				            			<#assign productTotalQuant = productTotalQuant+ qtyValue.get('quantity')>
				            			<#assign productTotalValue = productTotalValue+ qtyValue.get('value')>
				            			<#assign checkFlag = true>
                       				</#if>
                       			</#list>
                       			<#if checkFlag == false>
                       				<fo:table-cell>
				                		<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">0</fo:block>  
				                	</fo:table-cell>
				                	<fo:table-cell>
				                		<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">0</fo:block>  
				                	</fo:table-cell>
                       			</#if>
	                        </#list>
	                        <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${productTotalQuant?if_exists}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${productTotalValue?if_exists}</fo:block>  
				            </fo:table-cell>
				            <#assign TOT_Total = TOT_Total + productTotalValue>
							</fo:table-row>
							<#assign serial = serial+1>
							</#list>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block  keep-together="always">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
				            	</fo:table-cell>
				            </fo:table-row>
				            <fo:table-row>
				            	<fo:table-cell>
				            		<fo:block  keep-together="always" white-space-collapse="false">&#160;          TOTALS:        ${WSD_Total}          ${FRO_Total?if_exists}         ${MCC_Total?if_exists}         ${DEW_Total?if_exists}           ${PAR_Total?if_exists}     ${CRD_Total?if_exists}   ${AVM_Total?if_exists}          ${OTH_Total?if_exists}          ${TOT_Total?if_exists}</fo:block>  
				            	</fo:table-cell>
				            </fo:table-row>
				            <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block  keep-together="always">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>  
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