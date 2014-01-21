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

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".03in" margin-right="0.7in">
        <fo:region-body margin-top="1.3in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "${reportTypeFlag}IndentReport.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 56>
<#assign facilityNumberInPage = 0>
<#if routeMap?has_content>
<#assign routeDetails = routeMap.entrySet()>
			<#list routeDetails as eachRoute>
				<#assign routeId = eachRoute.getKey()>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<#assign facilityNumberInPage = 0>
					
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>			
              		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
              		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
              		<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="8pt">&#160;    MILK PRODUCTS MARKETING INDENT FOR ROUTE:${routeId}   DATE:${indentDate}</fo:block>
            	    <fo:block>----------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false"  keep-together="always" font-size="8pt"  font-family="Courier,monospace"  text-align="left">SNO    PCD       PRODUCT NAME        MKTG.INDENT        SPL.ORDER</fo:block>
            	    <fo:block>----------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier">
				<#assign grandTotalMap = eachRoute.getValue()>
				<#assign grandTotal=grandTotalMap.entrySet()>
            			<fo:table> 	
            				<fo:table-column column-width="35pt"/>
				   		 	<fo:table-column column-width="40pt"/>
				    		<fo:table-column column-width="75pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="10pt"/>
				    			<fo:table-body>
				    			<#assign serialNo = 1>
				    			<#list grandTotal as eachEntry>
				    			<#assign entryValue = eachEntry.getValue()>
                				<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block  font-size="8pt"  keep-together="always" white-space-collapse="false" text-align="left">${serialNo}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block   font-size="8pt" keep-together="always" white-space-collapse="false"  text-align="left">${entryValue.get("productId")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block  font-size="8pt" text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(entryValue.get("productName"))),20)}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block   font-size="8pt" text-align="right">${entryValue.get("otherQuantity")}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<#if !(entryValue.get("splQuantity") == 0)>
                            			<fo:block  font-size="8pt" keep-together="always"  white-space-collapse="false" text-align="right">${entryValue.get("splQuantity")}</fo:block>
                            			<#else>
                            			<fo:block   font-size="8pt" text-align="right"></fo:block>
                            			</#if>
                            		</fo:table-cell>	                           
                       			</fo:table-row>
                       			<#assign serialNo = serialNo+1>
                       			</#list>
                       			<fo:table-row>
                   	     			<fo:table-cell>
	    	                        	<fo:block>============================================================================</fo:block>        
			                        </fo:table-cell>
	        		            </fo:table-row>
                       			<fo:table-row>
                   	     			<fo:table-cell>
	    	                        	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
			                        </fo:table-cell>
	        		            </fo:table-row>
            				</fo:table-body>
       					 </fo:table>
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