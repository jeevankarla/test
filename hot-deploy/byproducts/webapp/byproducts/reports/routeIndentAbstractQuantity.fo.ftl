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
            margin-top="0.5in" margin-bottom=".5in" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trabs.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#if routeWiseIndentMap?has_content>	
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace"> <#assign lineNumber = 5> 
			<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                           UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
				<#assign facilityNumberInPage = 0>
				<#if parameters.subscriptionTypeId=="AM" >          		
              		<fo:block text-align="left" white-space-collapse="false">&#160;                           ABSTRACT INDENT FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(effectiveDate, "dd-MMMM-yyyy")}- MORNING SHIFT </fo:block>  
              	<#elseif parameters.subscriptionTypeId=="PM">
              		<fo:block text-align="left" white-space-collapse="false">&#160;                           ABSTRACT INDENT FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(effectiveDate, "dd-MMMM-yyyy")}- EVENING SHIFT </fo:block>
              	<#else>	
              	<fo:block text-align="left" white-space-collapse="false">&#160;                           ABSTRACT INDENT FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(effectiveDate, "dd-MMMM-yyyy")}- MORNING &amp; EVENING </fo:block>
              	</#if> 
              	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">ROUTE  PRODUCT  PRODUCT                                          QTY     CRATES       CANS </fo:block>
                <fo:block white-space-collapse="false" font-size="9pt"  font-family="Courier,monospace"  text-align="left">CODE   CODE     NAME                                                         </fo:block>
            	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">	

           <#assign routeIndentList=routeWiseIndentMap.entrySet()>
				<#list routeIndentList as routeIndent>
           			<#assign facilityGrandTotal = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign totalLitres = (Static["java.math.BigDecimal"].ZERO)>
           			
           			<#assign productEntries = (routeIndent.getValue()).entrySet()>           			       		        			
           			<#if (lineNumber > numberOfLines)> 
	           			<#assign lineNumber = 5>
	           			<#assign facilityNumberInPage = 0>	           				          				
           				<fo:block font-size="8pt" break-before="page">
           				<#elseif (facilityNumberInPage == 4)>           					
           					<#assign lineNumber = 5>
           					<#assign facilityNumberInPage = 0>           					          
           			 		<fo:block  font-size="8pt" break-after="page"> 
           				<#else>           					         					
           					<fo:block  font-size="8pt">
           					<#assign lineNumber = 5>           					         					  
           			</#if>         			
           			 	<#assign lineNumber = lineNumber + productEntries.size()+3>
           			 	<#assign facilityNumberInPage = (facilityNumberInPage+1)>               			 	      		
            				 <fo:table  table-layout="fixed">                
				                <fo:table-column column-width="33pt"/>
 						  		<fo:table-column column-width="55pt"/>
 						  		<fo:table-column column-width="180pt"/>
 						   	    <fo:table-column column-width="85pt"/>
 						   	    <fo:table-column column-width="70pt"/>
 						   	    <fo:table-column column-width="60pt"/>
				                <fo:table-body>
                				    <#if productEntries?has_content>                     		                      	                     	
                      					<#assign facility = delegator.findOne("Facility", {"facilityId" : routeIndent.getKey()}, true)>
	                                		<#assign totalcrates = 0>
	                                			<#list productEntries as productEntry>
	                                			<#if productEntry.getKey()!="Total">
	                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
		              							          <fo:table-row >  
		              							             <fo:table-cell>
                        			                            <fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityId"))),5)}</fo:block>
                        		                             </fo:table-cell>                  
					                            		     <fo:table-cell>
					                                			<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					                            			 </fo:table-cell>
					                            	         <fo:table-cell>
					                                	        <fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("productName")))),37)}</fo:block>
					                            	          </fo:table-cell>	
							                                  <fo:table-cell>
					                                	         <fo:block  text-align="right">${productEntry.getValue().get("qty")}</fo:block>
					                            	         </fo:table-cell>
					                            	         <#assign crateMap = productEntry.getValue().get("crateMap")?if_exists>
								                            	<#if crateMap?has_content>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${crateMap.get("crates")}+${crateMap.get("loosePkts")}</fo:block>
						                            	         </fo:table-cell>
						                            	         <#else>
						                            	         <fo:table-cell>
						                                	         <fo:block  text-align="right">-</fo:block>
						                            	         </fo:table-cell>
								                            	</#if>
								                            	 <#assign cansMap = productEntry.getValue().get("cansMap")?if_exists>
								                            	<#if cansMap?has_content>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${cansMap.get("cans")}</fo:block>
						                            	         </fo:table-cell>
								                            	</#if>
					                         	         </fo:table-row>
					                         	         </#if>
					                         	         <#if productEntry.getKey() =="Total">
					                         	          <fo:table-row >  
		              							          <fo:table-cell>
					                         	         <fo:block font-family="Courier,monospace" font-size="9pt">-------------------------------------------------------------------------------------------------</fo:block>
					                         	         </fo:table-cell>
					                         	         </fo:table-row>
					                         	          <fo:table-row >  
		              							             <fo:table-cell>
                        			                            <fo:block text-align="left" keep-together="always">&#160;</fo:block>
                        		                             </fo:table-cell>                  
					                            		     <fo:table-cell>
					                                			<fo:block  text-align="left" keep-together="always">&#160;</fo:block>
					                            			 </fo:table-cell>
					                            	         <fo:table-cell>
					                                	        <fo:block  text-align="right" keep-together="always">FulCrates:${productEntry.getValue().get("rtCrates")}</fo:block>
					                            	          </fo:table-cell>	
							                                  <fo:table-cell>
					                                	         <fo:block  text-align="right">LooseCrates:</fo:block>
					                            	         </fo:table-cell>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="">${productEntry.getValue().get("rtLooseCrates")}(${productEntry.getValue().get("rtExcessPkts")})</fo:block>
						                            	         </fo:table-cell>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">&#160;</fo:block>
						                            	         </fo:table-cell>
					                         	         </fo:table-row>
		              							          <fo:table-row >  
		              							             <fo:table-cell>
                        			                            <fo:block text-align="left" keep-together="always">&#160;</fo:block>
                        		                             </fo:table-cell>                  
					                            		     <fo:table-cell>
					                                			<fo:block  text-align="left" keep-together="always">Total</fo:block>
					                            			 </fo:table-cell>
					                            	         <fo:table-cell>
					                                	        <fo:block  text-align="left" keep-together="always"></fo:block>
					                            	          </fo:table-cell>	
							                                  <fo:table-cell>
					                                	         <fo:block  text-align="right">${productEntry.getValue().get("routeTotQty")}</fo:block>
					                            	         </fo:table-cell>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${(productEntry.getValue().get("rtCrates"))+(productEntry.getValue().get("rtLooseCrates"))}</fo:block>
						                            	         </fo:table-cell>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${productEntry.getValue().get("rtCans")}</fo:block>
						                            	         </fo:table-cell>
					                         	         </fo:table-row>
					                         	         </#if>
	                      				 		</#list>
	                      				 		 <fo:table-row >  
												            <fo:table-cell>
												                <fo:block  text-align="right" keep-together="always"></fo:block> 
														    </fo:table-cell>
												</fo:table-row>
	                      				 		<#-- 
	                      				 		<fo:table >
	                                              <fo:table-column column-width="380pt"/>
	                                                <fo:table-body> 
												       <fo:table-row >  
												            <fo:table-cell>
												                <fo:block  text-align="right" keep-together="always">${totalcrates}</fo:block> 
														    </fo:table-cell>
													   </fo:table-row>
												    </fo:table-body>
										        </fo:table>  -->             				 		                    
 				            </#if>
	                     </fo:table-body>
                      </fo:table>
                 <fo:block font-family="Courier,monospace" font-size="9pt">-------------------------------------------------------------------------------------------------</fo:block> 
       </fo:block>
   </#list> 
     <fo:block  font-size="8pt">  &#160;     &#160;   &#160;  &#160;    &#160;  </fo:block>
     <fo:block  font-size="8pt">  &#160;     &#160;   &#160;  &#160;    &#160; Grand Total </fo:block>
   <fo:block  font-size="8pt">
       <fo:table  table-layout="fixed">                
				                <fo:table-column column-width="33pt"/>
 						  		<fo:table-column column-width="55pt"/>
 						  		<fo:table-column column-width="180pt"/>
 						   	    <fo:table-column column-width="85pt"/>
 						   	    <fo:table-column column-width="70pt"/>
 						   	    <fo:table-column column-width="60pt"/>
				                <fo:table-body>
				               <#assign finalGrandProdList= GrandTotalProdMap.entrySet()>
				                <#list finalGrandProdList as productEntry>
	                      								<#assign product = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)> 	                              
		              							          <fo:table-row >  
		              							             <fo:table-cell>
                        			                            <fo:block text-align="left" keep-together="always"></fo:block>
                        		                             </fo:table-cell>                  
					                            		     <fo:table-cell>
					                                			<fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("brandName")))),7)}</fo:block>
					                            			 </fo:table-cell>
					                            	         <fo:table-cell>
					                                	        <fo:block  text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((product.get("productName")))),37)}</fo:block>
					                            	          </fo:table-cell>	
							                                  <fo:table-cell>
					                                	         <fo:block  text-align="right">${productEntry.getValue().get("qty")}</fo:block>
					                            	         </fo:table-cell>
					                            	         <#assign crateVal = productEntry.getValue().get("crates")?if_exists>
								                            	<#if crateVal?has_content>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${productEntry.getValue().get("crates")?if_exists}+${productEntry.getValue().get("loosePkts")?if_exists}</fo:block>
						                            	         </fo:table-cell>
						                            	         <#else>
						                            	         <fo:table-cell>
						                                	         <fo:block  text-align="right">-</fo:block>
						                            	         </fo:table-cell>
								                            	</#if>
								                            	 <#assign cans = productEntry.getValue().get("cans")?if_exists>
								                            	<#if cans?has_content>
									                      		<fo:table-cell>
						                                	         <fo:block  text-align="right">${productEntry.getValue().get("cans")?if_exists}</fo:block>
						                            	         </fo:table-cell>
								                            	</#if>
					                         	         </fo:table-row>
					                         	         </#list>
				                  </fo:table-body>
				                  </fo:table> 
				                   </fo:block>
  </fo:flow>						        	
</fo:page-sequence>
 <#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 <fo:block font-size="9pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>						
</fo:root>
</#escape>