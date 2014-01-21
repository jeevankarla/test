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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="0.5in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>

	<#if parameters.reportTypeFlag =="GATE_PASS_DAIRY">
		${setRequestAttribute("OUTPUT_FILENAME", "Dairy_GatePass.txt")}
	<#else>
		${setRequestAttribute("OUTPUT_FILENAME", "Union_GatePass.txt")}
	</#if>
	<#assign lineNumber = 5>
	<#assign numberOfLines = 60>
	<#assign facilityNumberInPage = 0>
<#if routeList?has_content>	
<#list routeList as eachRoute>
<#assign routeDetail = eachRoute.entrySet()>
<#list routeDetail as eachDetail>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                              ${uiLabelMap.aavinDairyMsg}</fo:block>				
              		<fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                          PRODUCTS GATE PASS         SL.NO: ${shipmentRouteMap.get(eachDetail.getKey())?if_exists}   </fo:block>
              		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false" >ROUTE-CODE        :${eachDetail.getKey()}	                                       										DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")} 											TIME :	</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false" >SCHEDULE NUMBER    : ${shipmentRouteMap.get(eachDetail.getKey())}                             																	VEHICLE NO:</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false" >VAN STAFF NAME     :</fo:block>
              		<#if parameters.reportTypeFlag =="GATE_PASS_DAIRY">
						<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                  DAIRY GATE PASS</fo:block>
					<#else>
						<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&#160;                                                  UNION GATE PASS</fo:block>
					</#if>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">		
            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false" >---------------------------------------------------------------------------</fo:block>
            	<fo:block>
            		<#assign productList = eachDetail.getValue().entrySet()>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>  
               	    <fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="50pt"/>
                     
		          	<fo:table-header border-width="1pt" border-style="dotted">
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">SNO</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">PCD</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block text-align="left" font-size="7pt">PRODUCT NAME</fo:block></fo:table-cell>
            				
            			<fo:table-cell><fo:block text-align="right" font-size="7pt">QUANTITY</fo:block></fo:table-cell>		                    	                  
            		   
				    </fo:table-header>		           
                    <fo:table-body>
                    	<fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >---------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <#assign sNo = 1>
	                    <#list productList as eachProd> 
	                    <#assign product = delegator.findOne("Product", {"productId" : eachProd.getKey()}, true)>
                    	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${sNo}</fo:block>        
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${product.productId?if_exists}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${product.productName?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="right" keep-together="always" font-size="7pt" white-space-collapse="false">${eachProd.getValue()}</fo:block>
	                        </fo:table-cell>
	                        <#assign sNo = sNo + 1>
                    	</fo:table-row> 	
                   		</#list>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">---------------------------------------------------------------------------</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">CHECKED AS PER DETAILS FURNISHED ABOVE AND FOUND CORRECT</fo:block> 				
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">EMPTIES 	     DESP 	     RET	    EMPTIES	    DESP  	   RET	   	EMPTIES 	 DESP</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~</fo:block>
              	<fo:block>
              	<fo:table border-width="1pt" border-style="dotted">
                    <fo:table-column column-width="67pt"/>
                    <fo:table-column column-width="50pt"/>  
               	    <fo:table-column column-width="44pt"/>
                    <fo:table-column column-width="70pt"/> 
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="10pt"/> 
                    <fo:table-body>
                    	<fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >PLASTIC TRAY</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >GLY.BIG SINTEX</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >SPOON</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row> 
	                    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >JUMBO TRAY</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >GLY.MEDIUM SINTEX</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >P.PLATE</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >ALUMINIUM CAN</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >SMALL SINTEX</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >STRAW</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >SMALL CAN</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >BIG SHIPPER</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >DRY ICE</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >PLASTIC CAN</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false" >FRPT</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="center" font-size="7pt" keep-together="always" white-space-collapse="false" >-:</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row> 
               		</fo:table-body>
                </fo:table>
               </fo:block> 
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~</fo:block>
               	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false"> VAN STAFF 			        																											DY.MANAGER(MARKETING)</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">VERIFIED BY :</fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">.						SECURITY OFFICER 	 			        															MANAGER(DAIRYING)</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
            	<fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">.				        DY.MANAGER(MARKETING)/MANAGER(MARKETING)</fo:block>
			 </fo:flow>
			 </fo:page-sequence>
		</#list>	
		</#list>	
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