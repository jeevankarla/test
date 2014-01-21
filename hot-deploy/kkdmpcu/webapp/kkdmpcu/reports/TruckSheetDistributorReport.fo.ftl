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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<fo:block text-align="left" white-space-collapse="false">.                ${uiLabelMap.ApDairyMsg}</fo:block>          		
              	<fo:block text-align="left" white-space-collapse="false">.                     DISTRIBUTOR WISE TOTAL MILK SALES ON: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>		               
            	<fo:block>------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" white-space-treatment="preserve" font-size="10pt" font-family="Courier,monospace" text-align="left">TRANSPORTER NAME      ${uiLabelMap.ProductTypeName}      ${uiLabelMap.TypeCredit}    ${uiLabelMap.TypeCard}  	 ${uiLabelMap.TypeSpecialOrder}    ${uiLabelMap.TypeCash}     ${uiLabelMap.CommonTotal}   ${uiLabelMap.Crates}</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">	
${setRequestAttribute("OUTPUT_FILENAME", "trsplst.txt")}			
<#assign lineNumber = 7>
<#assign numberOfLines = 60>
<#if truckSheetReportList?has_content>			
        							
           		<#list truckSheetReportList as truckSheetReport>           			
           			<#assign totalCrates=0>
           			<#assign totalLitres = 0>
           			<#assign facilityTypeId = truckSheetReport.get("facilityType")>
           			<#assign productEntries = (truckSheetReport).entrySet()>                                    	                      		
           			<#if (lineNumber > numberOfLines)> 
           				 	 </fo:flow>						        	
   							</fo:page-sequence>   							
        
        				<#assign lineNumber = 7>		          				
           				<fo:block font-family="Courier,monospace" font-size="10pt" <#if (lineNumber > numberOfLines)>break-before="page"</#if>>           				
           				<#elseif (facilityTypeId == "DISTRIBUTOR") >           					          
           			 		<fo:block font-family="Courier,monospace" font-size="10pt" break-after="page">
           				<#else>
           					<fo:block font-family="Courier,monospace" font-size="10pt">     
           			</#if>         			
           			 
           			<#if (lineNumber > numberOfLines)>
           				<#assign lineNumber = 7>
           			</#if> 	
           			<#if (facilityTypeId == "DISTRIBUTOR")>        	
            			<fo:table width="100%" table-layout="fixed" space-after="0.0in">
            				 <fo:table-column column-width="100%"/>
            				 <fo:table-header>			                	
			                	<fo:table-row>                        
			                        <fo:table-cell column-width="100%"><fo:block>------------------------------------------------------------------------------</fo:block></fo:table-cell>
			                    </fo:table-row>	
			                </fo:table-header>
			                <fo:table-body>
			                <fo:table-row column-width="100%">
			                <fo:table-cell column-width="100%">
            				 <fo:table  table-layout="fixed" >                
				                <fo:table-column column-width="117pt"/>
				                <fo:table-column column-width="153pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>	
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>	
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>
				                 <fo:table-column column-width="50pt"/>
				                <fo:table-column column-width="50pt"/>    		                   
                			<fo:table-body>                      						                      	
                      	<#if productEntries?has_content>                     		                      	                     	
                      
                      	<#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)>
                       <fo:table-row>                            
                            <fo:table-cell>
                                <fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("description"))),15)}</fo:block>
                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityId"))),4)}(${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")})</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block >
                                    
                                	<#list productEntries as productEntry>
                                	
                      		<#if productEntry.getKey() != "facilityId" && productEntry.getKey() != "facilityType">
                      		
                      		<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
		                              <fo:table >
	             						 <fo:table-column column-width="40pt"/>
	             						  <fo:table-column column-width="47pt"/>
	             						   <fo:table-column column-width="47pt"/>
	             						    <fo:table-column column-width="40pt"/>
	             						     <fo:table-column column-width="45pt"/>
	             						      <fo:table-column column-width="55pt"/>
	             						       <fo:table-column column-width="55pt"/>
	             						        <fo:table-column column-width="50pt"/>
				               					 <fo:table-column column-width="50pt"/> 					                 						                        						         
			              						<fo:table-body>                  						 
				              						<fo:table-row>                    
						                            <fo:table-cell>
						                                <fo:block  text-align="right" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
						                                <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						                            </fo:table-cell>								                            
						                      			<#assign typeEntries = (productEntry.getValue()).entrySet()>
						                      			                      	
						                      			<#list typeEntries as typeEntry>
						                      				<#if typeEntry.getKey() == "LITRES">
						                      				<#assign totalLitres=(totalLitres+typeEntry.getValue())>
						                      				</#if>
						                      				<#if typeEntry.getKey() == "CRATES">
						                      				<#assign crates=typeEntry.getValue()>
						                      				<#assign cratesValue=StringUtil.split(crates,".||-")>
						                      				<#assign totalCrates=(totalCrates+Static["java.lang.Integer"].valueOf(cratesValue[0]))>
						                      				</#if>
						                      			<#if (typeEntry.getKey() != "AGNTCS") && (typeEntry.getKey() != "PTCCS") && (typeEntry.getKey() != "LITRES") && (typeEntry.getKey() != "TOTALAMOUNT") && typeEntry.getKey() != "NOPKTS" && typeEntry.getKey() != "NOCRATES" && typeEntry.getKey() != "CARD_AMOUNT"> 
						                      			   <fo:table-cell >
						                      			   		<fo:block  text-align="right">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(typeEntry.getValue().toString())),8)}</fo:block>
						                      			   		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						                      			   	</fo:table-cell>
								                            </#if>                      				
						                            	</#list>
						                         </fo:table-row>								                        
						                         </fo:table-body>
				                         </fo:table> 	                          
                       				 	</#if>
                      				 </#list>
                      				          
                                </fo:block>
                            </fo:table-cell>	                           
                       </fo:table-row>
                       </#if>         
                 
            </fo:table-body>
        </fo:table>          
        </fo:table-cell>
        </fo:table-row>   
        	<fo:table-row>                        
			    <fo:table-cell >			   
			    <#if (facilityTypeId == "DISTRIBUTOR") >
			    <fo:block>------------------------------------------------------------------------------</fo:block>
        		<fo:block white-space-collapse="false" white-space-treatment="preserve" keep-together="always">TOT CRATES:${totalCrates}-0                TOT LTRS:${totalLitres}</fo:block>
        		</#if>
			    </fo:table-cell>
			</fo:table-row>	
        </fo:table-body>
         </fo:table>
         </#if> 	
        </fo:block>        
       </#list> 
    </fo:flow>						        	
   </fo:page-sequence>   
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