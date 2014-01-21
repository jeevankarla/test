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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "specialOrderDetails.pdf")}
       <#if specialOrderSales?has_content>
       <#assign eachPartySale = specialOrderSales.entrySet()>
       	        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;                    ${uiLabelMap.aavinDairyMsg}</fo:block>
                    	<fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                         MARKETING UNIT, METRO PRODUCTS DIVISON, NANDANAM ,CHENNAI-35.</fo:block>
                    	<fo:block text-align="left" font-size="10pt" keep-together="always"  white-space-collapse="false">&#160;                            SPECIAL ORDER SUPPLIED DETAILS FOR MONTH OF ${month?if_exists}</fo:block>
              			<fo:block font-size="7pt" align-text="left">===================================================================================================================================================================================================</fo:block> 
            			<fo:block text-align="left" font-size="10pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;SNO  SUPPLY DATE   PROD CODE    PRODUCT NAME       QTY     RATE       VALUE   </fo:block>
		        		<fo:block font-size="7pt" align-text="left">===================================================================================================================================================================================================</fo:block> 
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
            	<#assign serial = 1>
            	<#assign grand_total = 0>
            	<#list eachPartySale as eachSpecialParty>
            	<#assign boothDetails = delegator.findOne("Facility", {"facilityId" : eachSpecialParty.getKey()?if_exists}, true)>
                 	<fo:table>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="40pt"/>
            		<fo:table-column column-width="140pt"/> 	
            		<fo:table-column column-width="60pt"/>	
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="70pt"/>
                        <fo:table-body>
                        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block keep-together="always" font-size="9pt" align-text="left" white-space-collapse="false">&#160;                     ${eachSpecialParty.getKey()}         ${boothDetails.get('facilityName')}</fo:block>  
                       		</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
                       		<fo:table-cell>
                           		<fo:block keep-together="always" font-size="9pt" align-text="left" white-space-collapse="false">&#160;              ----------------------------------------------------------------------</fo:block>  
                       		</fo:table-cell>
						</fo:table-row>
						<#assign total_Sale = 0>
						<#assign SOPartyOrderDetails = eachSpecialParty.getValue()>
						<#list SOPartyOrderDetails as eachSODetail>
            			<fo:table-row>
               				<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${serial}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachSODetail.get('saleDate'), "dd.MM.yyyy")}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                           		<fo:block font-size="9pt" align-text="left">${eachSODetail.get('productId')?if_exists}</fo:block>  
                       		</fo:table-cell>
	            			<fo:table-cell>
	            				<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${productNames.get(eachSODetail.get('productId'))?if_exists}</fo:block>  
	            			</fo:table-cell>
               				<fo:table-cell>
		                		<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${eachSODetail.get('quantity')?if_exists}</fo:block>  
		                	</fo:table-cell>
		                	<fo:table-cell>
		                		<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${eachSODetail.get('unitPrice')?if_exists}</fo:block>  
		                	</fo:table-cell>
		                	<#assign totalValue = eachSODetail.get('quantity')*eachSODetail.get('unitPrice')>
	                        <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">${totalValue?if_exists?string("##0.00")}</fo:block>  
				            </fo:table-cell>
							</fo:table-row>
							<#assign total_Sale = total_Sale + totalValue>
							<#assign serial = serial+1>
							</#list>
							<#assign grand_total = grand_total+total_Sale>
							<fo:table-row>
								<fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">===========================================================================================</fo:block>  
				            	</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">&#160;                      &lt;&lt; PARTY WISE TOTAL &gt;&gt;                              Rs. ${total_Sale?if_exists?string("##0.00")} </fo:block>  
				            	</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">===========================================================================================</fo:block>  
				            	</fo:table-cell>
							</fo:table-row>
	                    </fo:table-body>
                	</fo:table>
                	</#list>
                	<fo:table>
                		<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">&#160;                           &lt;&lt; GRAND TOTAL &gt;&gt;                              Rs. ${grand_total?if_exists?string("##0.00")} </fo:block>  
				            	</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false">===========================================================================================</fo:block>  
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