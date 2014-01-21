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
       <#if parlourSalesMap?has_content>  
       <#assign grand_total = 0>
       	<#assign eachEntry = parlourSalesMap.entrySet()>
			<#list eachEntry as eachParlour>
			<#assign parlour_total = 0>
			<#assign lineNumber = 5>
			<#assign numberOfLines = 65>       	      
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always"  >&#160;                                 ${uiLabelMap.aavinDairyMsg}</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always">&#160;                                     MARKETING UNIT, METRO PRODUCTS-NANDANAM, CHENNAI-35</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">&#160;                                               PRODUCT <#if reportFlag == 'ParlourSales'>SALES<#else>RECEIPTS</#if> STATEMENT</fo:block>
		        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">Party Code :${eachParlour.getKey()?if_exists}                  Party Name :${parlourDescription.get(eachParlour.getKey())?if_exists}                 Month &amp; YEAR:  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMM-yyyy")}</fo:block>	 	 	  
		        		<fo:block font-size="9pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">	        		
		        		 <fo:block>
		        		 	<fo:table>
		        		 		<fo:table-column column-width="40pt"/>
                    			<fo:table-column column-width="45pt"/>                    			
                    			<#list daysList as activeDay>
                    				<fo:table-column column-width="25pt"/>
                    			</#list>
                    			<fo:table-column column-width="40pt"/>
                    			<fo:table-column column-width="40pt"/>
                    			<fo:table-header>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block font-weight="bold">PRCD</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" font-weight="bold">Product Name</fo:block>
                    					</fo:table-cell>
                    					<#assign dayOfMonth = 0>
                    					<#list daysList as activeDay>                   					
	                    					<fo:table-cell>	                    						
	                    						<fo:block keep-together="always" text-align ="left" text-indent="70pt" font-weight="bold">D${activeDay}</fo:block>
	                    					</fo:table-cell>	                    					
                    					</#list>	
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">TotQty</fo:block>
	                    					</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">SalesAmount</fo:block>
	                    				</fo:table-cell>	
                    				</fo:table-row>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    			</fo:table-header>
                    			<fo:table-body>
                    			<#assign eachParlourSaleProducts = eachParlour.getValue().entrySet()>
                    			<#list eachParlourSaleProducts as eachProduct>
                    				<#assign eachProductQuantity = eachProduct.getValue().entrySet()>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						 <fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160; ${eachProduct.getKey()?if_exists} </fo:block> 
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						 <fo:block keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((productDescription.get(eachProduct.getKey())))),17)}</fo:block> 
                    					</fo:table-cell>
                    					<#assign total_Quantity = 0>
                    					<#list eachProductQuantity as dayWiseQuantity>
                    						<#if dayWiseQuantity.getValue() != 0>
	                    						<fo:table-cell>
                    								<fo:block keep-together="always" text-align ="left" text-indent="70pt">${dayWiseQuantity.getValue()?if_exists}</fo:block> 
                    							</fo:table-cell>
                    							<#assign total_Quantity = total_Quantity+dayWiseQuantity.getValue()>
                    						<#else>
                    							<fo:table-cell>
                    								<fo:block keep-together="always" text-align ="left" text-indent="70pt"></fo:block> 
                    							</fo:table-cell>
                    						</#if>
                    					</#list>
                    					<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">${total_Quantity?if_exists}</fo:block>
	                    				</fo:table-cell>
	                    				<#assign total_Value = total_Quantity * priceMap.get(eachProduct.getKey()).get("totalAmount")>
	                    				<#assign parlour_total = parlour_total+total_Value>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" text-align="left" text-indent="70pt" font-weight="bold">${total_Value?if_exists?string("#0.00")}</fo:block>
	                    				</fo:table-cell>		
                    				</fo:table-row>
                    				<#assign lineNumber = lineNumber + 1>
                    				<#if (lineNumber >= numberOfLines)>
                    				<#assign lineNumber = 5>
			                    		<fo:table-row>
			                   	     		<fo:table-cell>
				                            	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
				                        	</fo:table-cell>
				                    	</fo:table-row>
			                    	<#else>
			                    	</#if>
                    				</#list>
                    				<#assign grand_total = grand_total+parlour_total>
                    		</fo:table-body>
		        		 </fo:table>
		        	</fo:block>
	        	  <fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        	  <fo:block text-align="left" white-space-collapse="false"  keep-together="always" font-size="10pt" font-weight="bold" >&#160;                                                                                                                                                   TOTAL:  ${parlour_total?if_exists?string("#0.00")}</fo:block>       
	        	  <fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        	         
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