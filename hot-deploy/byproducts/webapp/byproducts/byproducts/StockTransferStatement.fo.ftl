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
       <#if transferMap?has_content>
       <#assign eachPartyTransfer = transferMap.entrySet()> 
       <#list eachPartyTransfer as eachParlour>
       <#assign prices = facilityPriceMap.get(eachParlour.getKey())>       
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always"  >&#160;                                 ${uiLabelMap.aavinDairyMsg}</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always">&#160;                                     MARKETING UNIT, METRO PRODUCTS-NANDANAM, CHENNAI-35</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">&#160;                                               PRODUCT <#if parameters.transferType == 'transferIn'>TRANSFER IN<#else>TRANSFER OUT</#if> STATEMENT</fo:block>
		        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        		<#assign parlourDetails = delegator.findOne("Facility", {"facilityId" : eachParlour.getKey()?if_exists}, true)>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">Party Code : ${parlourDetails.get('facilityId')?if_exists}              Party Name : ${parlourDetails.get('facilityName')?if_exists}                        Month &amp; YEAR:  ${reportForMonth?if_exists}</fo:block>	 	 	  
		        		<fo:block font-size="9pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="10pt">	        		
		        		 <fo:block>
		        		 	<fo:table>
		        		 		<fo:table-column column-width="30pt"/>
		        		 		<fo:table-column column-width="40pt"/>
                    			<fo:table-column column-width="40pt"/>                    			
                    			<#list daysList as activeDay>
                    				<fo:table-column column-width="25pt"/>
                    			</#list>
                    			<fo:table-column column-width="40pt"/>
                    			<fo:table-column column-width="40pt"/>  
                    			<fo:table-header>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block font-weight="bold">SLNO</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block font-weight="bold">PRCD</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" font-weight="bold">PRODUCT NAME</fo:block>
                    					</fo:table-cell>
                    					<#assign dayOfMonth = 0>
                    					<#list daysList as activeDay>                   					
	                    					<fo:table-cell>	                    						
	                    						<fo:block keep-together="always" text-align ="left" text-indent="70pt" font-weight="bold">D${activeDay?if_exists}</fo:block>
	                    					</fo:table-cell>	                    					
                    					</#list>	
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">TOTAL</fo:block>
                    					</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">SALES</fo:block>
	                    				</fo:table-cell>	
                    				</fo:table-row>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" white-space-collapse="false" text-align="left" text-indent="935pt" font-weight="bold">QTY   AMOUNT</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    			</fo:table-header>
                    			<fo:table-body>
                    			<#assign serial = 1>
                    			<#assign totalSalesValue = 0>
                    			<#assign parlourXferProduct = eachParlour.getValue().entrySet()>
                    			<#list parlourXferProduct as eachXferProduct>
                    			<#assign totalQty = 0>
                    				<fo:table-row>
                    					<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="5pt">${serial?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="5pt">${eachXferProduct.getKey()?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="5pt">${productDesc.get(eachXferProduct.getKey())?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<#assign dayTransfer = eachXferProduct.getValue().entrySet()>
	                    				<#list daysList as eachDay>
	                    					<#assign checkFlag = false>
	                    					<#list dayTransfer as eachDayXfer>
	                    						<#if eachDay == eachDayXfer.getKey()>
	                    							<fo:table-cell>
	                    								<fo:block keep-together="always" text-align="left" text-indent="70pt">${eachDayXfer.getValue()?if_exists}</fo:block>
	                    							</fo:table-cell>
	                    							<#assign totalQty = totalQty+eachDayXfer.getValue()>
	                    							<#assign checkFlag = true>
	                    						</#if>
	                    					</#list>
	                    					<#if checkFlag == false>
                       							<fo:table-cell>
				                					<fo:block  keep-together="always" text-align="left" white-space-collapse="false" text-indent="70pt"></fo:block>  
				                				</fo:table-cell>
                       						</#if>	
	                    				</#list>
	                    				<fo:table-cell>
				                			<fo:block  keep-together="always" text-align="left" white-space-collapse="false" text-indent="70pt">${totalQty?if_exists}</fo:block>  
				                		</fo:table-cell>
				                		<#assign productPrice = prices.get(eachXferProduct.getKey())>
				                		<#assign saleAmount = (productPrice.get('totalAmount')*totalQty) >
				                		<#assign totalSalesValue = totalSalesValue+saleAmount>
				                		<fo:table-cell>
				                			<fo:block  keep-together="always" text-align="left" white-space-collapse="false" text-indent="70pt">${saleAmount?if_exists?string("##0.00")}</fo:block>  
				                		</fo:table-cell>
                    				</fo:table-row>
                    				<#assign serial = serial+1>
                    		</#list>
                    		</fo:table-body>
		        		 </fo:table>
		        	</fo:block>
	        	  <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        	  <fo:block text-align="left" white-space-collapse="false"  keep-together="always" font-size="10pt" font-weight="bold" >&#160;                                                                                                                                              TOTAL:            ${totalSalesValue?if_exists?string("##0.00")} </fo:block>       
	        	  <fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>       
			</fo:flow>
		</fo:page-sequence>
		</#list>
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>
 </fo:root>
</#escape>