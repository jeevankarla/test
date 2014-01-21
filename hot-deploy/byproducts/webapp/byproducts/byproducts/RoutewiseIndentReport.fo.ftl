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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in">
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<#if reportTypeFlag?exists>
	<#if reportTypeFlag == "CD-UNION" || (reportTypeFlag == "CD-15_UNION")>
		<#assign routeMap = cd15RouteMap>
		${setRequestAttribute("OUTPUT_FILENAME", "CD15UnionProducts.txt")}
	<#elseif reportTypeFlag == "CD-DAIRY" || (reportTypeFlag == "CD-15_DAIRY")>
		<#assign routeMap = cd15RouteMap>
		${setRequestAttribute("OUTPUT_FILENAME", "CD15DairyProducts.txt")}
	</#if>
	<#else>
		<#assign routeMap = routeMap>
		${setRequestAttribute("OUTPUT_FILENAME", "TotalIndentReport.txt")}
	</#if>
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<#assign facilityNumberInPage = 0>
				   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>	
                                                                       
					<fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>
					<#if reportTypeFlag?exists>
					<#if reportTypeFlag == "CD-UNION" || (reportTypeFlag == "CD-15_UNION")>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                                       MILK PRODUCTS CD-15 UNION STATEMENT                                    DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
					<#elseif reportTypeFlag == "CD-DAIRY" || (reportTypeFlag == "CD-15_DAIRY")>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                                       MILK PRODUCTS CD-15 DAIRY STATEMENT                                    DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
					</#if>
					<#else>
						<fo:block keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                                       MILK PRODUCTS TOTAL INDENT STATEMENT                                   DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(effectiveDate, "dd/MM/yyyy")}</fo:block>
					</#if>
					<fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">		
            	<fo:block>																						 
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="60pt"/>
               	    <#list routesHeader as eachRoute>
            			<fo:table-column column-width="30pt"/> 		
            		</#list>
            		<fo:table-column column-width="60pt"/>
            		<fo:table-column column-width="75pt"/>
            		
		         <fo:table-body>
		         	 <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
		             <fo:table-row>
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">SNO</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">PCD</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block text-align="left" font-size="7pt">PRODUCT NAME</fo:block></fo:table-cell>
		            	<#assign loopNo = 0>
        				<#list routesHeader as eachRoute>
        					<#assign loopNo =loopNo+1>
        					<fo:table-cell ><fo:block text-align="right" font-size="7pt">${eachRoute}</fo:block></fo:table-cell>
        					<#if loopNo &gt;30>
        					 </fo:table-row> 
        					 <fo:table-row>
        					 <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  
		            	     <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  		            
		            	     <fo:table-cell ><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>
		            	     <#assign loopNo = 0>
        					</#if>
        				</#list>
            			<fo:table-cell><fo:block text-align="right" font-size="7pt">TOTAL</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="right" font-size="7pt" keep-together="always">QTY/KGS/LTR</fo:block></fo:table-cell>
				    </fo:table-row>
				    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
                    	<#assign serialNo = 1/>
                    	<#list productList as product>
                    	<#assign productEnt = delegator.findOne("Product", {"productId" : product}, true)>
                    	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${serialNo}</fo:block>        
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${product}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">${(productEnt.brandName)?replace("MILK SHAKE", "MS")?replace("FLAVOURED MILK", "FM")?replace("PROBIOTIC", "PROBTIC")}</fo:block>        
	                        </fo:table-cell>
	                        <#assign loopNo = 0>
                    		<#list routesHeader as eachRoute>
                    		<#assign loopNo =loopNo+1>
                    	       <#assign prodData = 0> 
                    			<#if routeMap.get(eachRoute)?exists>
                    				<#assign routeData = routeMap.get(eachRoute)>
                    				<#if routeData?exists>
                    				<#assign prodData = routeData.get(product)?if_exists>
                    				</#if>
                    			<#else>	
                    				<#assign prodData = 0>
                    			</#if>
                    			<fo:table-cell>
            						<fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false">${prodData}</fo:block>        
        						</fo:table-cell>
        						<#if loopNo &gt;30>
        					      </fo:table-row> 
	        					   <fo:table-row>
	        					   <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  
			            	       <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  		            
			            	       <fo:table-cell ><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>
			            	       <#assign loopNo = 0>
			            	       <#assign lineNumber = lineNumber + 1>
        					     </#if>
                    		</#list>
                    		 <#assign prodGrandTot = 0>
                    		 <#assign prodGrandTot = grandTotmap.get(product)?if_exists>
                    		<fo:table-cell>
	                            <fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false">${prodGrandTot}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="right" font-size="7pt" keep-together="always" white-space-collapse="false"><#if prodGrandTot &gt; 0>${((prodGrandTot)*(productEnt.quantityIncluded))?string("##0.000")}<#else>0.000</#if></fo:block>        
	                        </fo:table-cell>
                    	</fo:table-row> 
                    	<fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block font-size="7pt" >&#160;</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
                    	<#assign lineNumber = lineNumber + 1>
                    	<#if (lineNumber >= numberOfLines)>
                    	<#assign lineNumber = 5>
                    	<fo:table-row>
                   	     	<fo:table-cell>
	                            	<#--fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>-->
	                            	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
		             <fo:table-row>
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">SNO</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">PCD</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block text-align="left" font-size="7pt">PRODUCT NAME</fo:block></fo:table-cell>
		            	<#assign loopNo = 0>
        				<#list routesHeader as eachRoute>
        					<#assign loopNo =loopNo+1>
        					<fo:table-cell ><fo:block text-align="right" font-size="7pt">${eachRoute}</fo:block></fo:table-cell>
        					<#if loopNo &gt;30>
        					 </fo:table-row> 
        					 <fo:table-row>
        					 <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  
		            	     <fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>		                    	                  		            
		            	     <fo:table-cell ><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>
		            	     <#assign loopNo = 0>
        					</#if>
        				</#list>
            			<fo:table-cell><fo:block text-align="right" font-size="7pt">TOTAL</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="right" font-size="7pt" keep-together="always">QTY/KGS/LTR</fo:block></fo:table-cell>
				    </fo:table-row>
				     <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
                    	<#else>
                    	</#if>
                    	<#assign serialNo = serialNo+1>	
                    	</#list>
                   	 	     <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>	 
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                          				MATERIALS UTILISE				</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                 		        			-----------------				</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">TOTAL NO OF SPOONS - WOODEN  ::</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                  - PLASTIC ::                               TOTAL NO OF PAPER PLATES ::                                DRY ICE IN KGS  :: </fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">DY.MANAGER (MARKETING) 	 			        				          		     MANAGER / DY.MANAGER (DAIRYING)/AGM (DAIRY)                		  DY.GENERAL MANAGER(ENGG)</fo:block>
			
			 </fo:flow>
			 </fo:page-sequence>	

				
</fo:root>
</#escape>