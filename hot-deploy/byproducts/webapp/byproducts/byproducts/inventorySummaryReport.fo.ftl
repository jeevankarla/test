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
        ${setRequestAttribute("OUTPUT_FILENAME", "inventorySummaryReport.pdf")}
       <#if inventorySummReport?has_content>
       	        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;                    ${uiLabelMap.aavinDairyMsg}</fo:block>
                    	<fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                         MARKETING UNIT, METRO PRODUCTS DIVISON, NANDANAM ,CHENNAI-35.</fo:block>
                    	<fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                            INVENTORY SUMMARY REPORT ${month?if_exists}</fo:block>
              			<fo:block font-size="7pt" align-text="left">=========================================================================================================================================================================================================================================</fo:block> 
            			<fo:block text-align="left" font-size="10pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;SNO  DATE      PARTY CODE  PROD CODE   PRODUCT NAME     OPENING BALANCE   SALES  RECEIPTS  TRANSFER-IN   TRANSFER-OUT  ADJUSTMENTS   CLOSING BALANCE</fo:block>
		        		<fo:block font-size="7pt" align-text="left">=========================================================================================================================================================================================================================================</fo:block> 
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
            	<#assign serial = 1>
                 	<fo:table>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="50pt"/> 
               	    <fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="80pt"/> 	
            		<fo:table-column column-width="90pt"/>	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="60pt"/> 	
            		<fo:table-column column-width="80pt"/>	
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="100pt"/>
            		<fo:table-column column-width="70pt"/>
                     <fo:table-body>
                     <#list inventorySummReport as invSummDetail>
                        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${serial}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invSummDetail.get('saleDate'), "dd.MM.yyyy")?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('facilityId')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('productId')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${productNames.get(invSummDetail.get('productId'))}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('openingBalQty')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('salesQty')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('receipts')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('xferIn')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('xferOut')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('adjustments')?if_exists}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${invSummDetail.get('quantityOnHandTotal')?if_exists}</fo:block>  
                       		</fo:table-cell>
							</fo:table-row>
							<#assign serial = serial+1>
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
            	${uiLabelMap.NoOrdersFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>	
  </#if>   
 </fo:root>
</#escape>