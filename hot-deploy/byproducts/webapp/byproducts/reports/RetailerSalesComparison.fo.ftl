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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "ReatailerAnlysis.txt")}
    <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if facilityCurntSaleMap?has_content>  
		        <fo:page-sequence master-reference="main" font-size="12pt">	 
		    <fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="center" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always">UNIT : MOTHER DAIRY  : G.K.V.K POST : YELAHANKA : BANGALORE - 560065</fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always"> STATEMENT SHOWING AVERAGE MILK SALES / DAY OF  SACHET  AGENTS IN SACHET ROUTES WITH </fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="9pt" font-weight="bold" keep-together="always">COMPARISON BETWEEN  :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pMonthStart, "MMM yyyy")}   TO  :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cMonthStart, "MMM yyyy")}</fo:block>
        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="8pt">	        		
		        		 <fo:block>
		        		 	<fo:table>
		        		 		<fo:table-column column-width="40pt"/>
		        		 		<fo:table-column column-width="60pt"/>
                    			<fo:table-column column-width="110pt"/>   
                    			<fo:table-column column-width="75pt"/>
                    			<fo:table-column column-width="75pt"/>
                    			<fo:table-column column-width="75pt"/>
                    			<fo:table-column column-width="75pt"/>
                    			<fo:table-column column-width="75pt"/>
                    			<fo:table-header>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    				<fo:table-row>
                    				    <fo:table-cell>
                    						<fo:block font-size="8pt">SLNO.</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block font-size="8pt">RETAILER CODE</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" font-size="8pt">RETAILER NAME</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pMonthStart, "MMM yyyy")}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cMonthStart, "MMM yyyy")}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">INCREASE</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">DECREASE</fo:block>
	                    				</fo:table-cell>
                    				</fo:table-row>
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    			</fo:table-header>
                    			<fo:table-body>
                    			<#assign cTot = 0>
                               <#assign pTot = 0>
                                <#assign incr = 0>
                                  <#assign dicr = 0>
                    				<#assign sNo = 0>
            					<#assign facilitiesDataMap = facilityCurntSaleMap.entrySet()>
            					<#list facilitiesDataMap as facData>
            					<#assign boothId = facData.getKey()>
            					<#assign sNo =sNo+1>
            						<#assign thisMonthAvg = 0>
            						<#assign prevMonthAvg = 0>
            						  <#if facilityPrevSaleMap.get(facData.getKey())?has_content>
	                    				<#assign prevMonthMap = facilityPrevSaleMap.get(facData.getKey())>
	                    				  <#if prevMonthMap?has_content>
	                    				<#assign prevMonthAvg = prevMonthMap.get("milkAvgTotal")>
	                    				 </#if>
	                    			 </#if>
	                    				<#assign thisMonthMap = facData.getValue()>
	                    				<#assign thisMonthAvg = thisMonthMap.get("milkAvgTotal")>
	                    					<#assign cTot =cTot+ thisMonthAvg>
	                    					<#assign pTot =pTot+ prevMonthAvg>
                    				<fo:table-row>
                    				    <fo:table-cell>
                    						<fo:block font-size="8pt">${sNo}</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block font-size="8pt">${facData.getKey()}</fo:block>
                    					</fo:table-cell>
                    					<#assign facility = delegator.findOne("Facility", {"facilityId" : facData.getKey()}, true)>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" font-size="8pt">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName"))),20))?if_exists}</fo:block>
                    					</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${prevMonthAvg?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${thisMonthAvg?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				<#if (thisMonthAvg - prevMonthAvg)?has_content>
	                    				<#assign diff = thisMonthAvg - prevMonthAvg>	
	                    				<#else>
	                    				<#assign diff = 0>
	                    				</#if>
	                    				<#if diff &gt; 0 >
	                    					<#assign incr = incr +diff>	
	                    				
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${diff?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				<#else>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right"></fo:block>
	                    				</fo:table-cell>
	                    				</#if>
	                    				<#if diff &lt; 0 >
	                    					<#assign dicr = dicr +diff>	
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${((diff)*-1)?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				<#else>
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right"></fo:block>
	                    				</fo:table-cell>
	                    				</#if>
	                    				
                    				</fo:table-row>
                    			</#list>	
                    			<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row>
                    			
                    				<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block font-size="9pt"></fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" text-align="center" font-size="8pt"></fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
                    						<fo:block keep-together="always" text-align="center" font-size="8pt">Total</fo:block>
                    					</fo:table-cell>
                    					<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${pTot?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${cTot?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${incr?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
	                    				
	                    				<fo:table-cell>
	                    						<fo:block keep-together="always" font-size="8pt" text-align="right">${((-1)*dicr)?string("##0.00")}</fo:block>
	                    				</fo:table-cell>
                    				</fo:table-row>
                    				
                    			<fo:table-row>
                    					<fo:table-cell>
                    						<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
	            	No Orders Found .
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>
	</#if>
 </fo:root>
</#escape>