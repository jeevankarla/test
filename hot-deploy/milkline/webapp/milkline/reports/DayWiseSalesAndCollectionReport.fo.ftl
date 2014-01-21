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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left=".5in" margin-right=".5in" >
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
             
            </fo:simple-page-master>
     </fo:layout-master-set>
     	    	
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">SUPRAJA DAIRY PRIVATE LIMITED</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false" >DAY WISE SALES AND COLLECTION REPORT</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">&#160;         FROM DATE : ${parameters.fromDate}          TO DATE : ${parameters.thruDate}</fo:block>
            	<fo:block>---------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160;      ROUTE   TotalMilkQTY   ButterMilk   Curd-200gr   Curd-500gr  Curd-cup   Curd-Bulk   Panner   FlavredMilk   SALE-AMT   RECEIPTS   </fo:block>
        		<fo:block>---------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica" font-size="8pt">        				
     				<#if zoneTotalMap?has_content>
     			<#assign zoneWiseTotalList =zoneTotalMap.entrySet()>
     				 <#list zoneWiseTotalList as zoneWiseTotal>
     				<#if allRoutesList?has_content> 
     				<fo:block > 
     					<fo:table>
                   			<fo:table-column column-width="80pt"/>                
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/> 
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                   		    <fo:table-body>                   		    
                   		    	
                   		    	<#list allRoutesList as eachRoute>
                   		    	<#assign zone =zoneWiseTotal.getKey()>
                   		    	<#if eachRoute.ZONE==zone>
                   		    	<#assign routeDayWiseSales =eachRoute.entrySet()>
                   		    	<fo:table-row> 
                   		    	
                    				 <#list routeDayWiseSales as dayWiseSales>
                    				<#if dayWiseSales.getKey()!="ZONE">
                    				<fo:table-cell>
                    					<fo:block keep-together="always"   text-align="right" white-space-collapse="false" >${dayWiseSales.getValue()}</fo:block>
                    				</fo:table-cell>
                    				</#if>
                    				</#list>
                    			</fo:table-row>
                    				
                    			</#if>
                    			</#list>
                    			<fo:table-row> 
                    		    <fo:table-cell>
                    			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			 </fo:table-cell>
                    			 </fo:table-row>
                    			<fo:table-row> 
                    			<#assign zoneDayWiseSales =zoneWiseTotal.getValue().entrySet()>
                    			<#list zoneDayWiseSales as zoneDaySales>
                    			   <fo:table-cell>
                    				 <fo:block keep-together="always"   text-align="right" white-space-collapse="false" >${zoneDaySales.getValue()}</fo:block>
                    			  </fo:table-cell>
                    			</#list>
                    			</fo:table-row>
                    			<fo:table-row> 
                    		    <fo:table-cell>
                    			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			 </fo:table-cell>
                    			 </fo:table-row>
                    		</fo:table-body>
                		</fo:table>
     				  </fo:block>
     			      </#if>
                   </#list>
                 </#if>
                 
                 <#-- populating grandTotal here-->
                 <#if zoneGrandTotalMap?has_content>
     				<fo:block > 
     					<fo:table>
                   			<fo:table-column column-width="80pt"/>                
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/> 
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                    		<fo:table-column column-width="70pt"/>
                   		    <fo:table-body> 
                    			<fo:table-row> 
                    		    <fo:table-cell>
                    			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			 </fo:table-cell>
                    			 </fo:table-row>
                    			<fo:table-row> 
                    			<fo:table-cell>
                    				 <fo:block keep-together="always"   font-weight="bold"  text-align="center" white-space-collapse="false" >GrandTotal:</fo:block>
                    			  </fo:table-cell>
                    			<#assign zoneGrandTotalList =zoneGrandTotalMap.entrySet()>
                    			<#list zoneGrandTotalList as zoneGrandTotal>
                    			   <fo:table-cell>
                    				 <fo:block keep-together="always" font-weight="bold"  text-align="right" white-space-collapse="false" >${zoneGrandTotal.getValue()}</fo:block>
                    			  </fo:table-cell>
                    			</#list>
                    			</fo:table-row>
                    			<fo:table-row> 
                    		    <fo:table-cell>
                    			<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                    			 </fo:table-cell>
                    			 </fo:table-row>
                    		</fo:table-body>
                		</fo:table>
     				  </fo:block>
     			      </#if>
                   
     	    </fo:flow>
    	</fo:page-sequence> 
</fo:root>
</#escape>