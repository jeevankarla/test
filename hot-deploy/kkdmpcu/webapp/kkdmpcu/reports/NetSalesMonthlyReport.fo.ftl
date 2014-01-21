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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "netsales.txt")}
<#if summaryDetailsMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
	
	<fo:static-content flow-name="xsl-region-before" font-family="Courier">
    	<fo:block text-align="left" keep-together="always" white-space-collapse="false">.                           KRISHNAVENI KKDMPCU  LTD: VIJAYAWADA</fo:block>			
		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false"> ${uiLabelMap.CommonPage}:<fo:page-number/>                MILK DESPATCHES/NETSLS   FORM  ${parameters.fromDate}  TO  ${parameters.thruDate}</fo:block>
	  	<fo:block>-------------------------------------------------------------------------------------------------------------------------------</fo:block>
        <fo:block text-align="left" keep-together="always" white-space-collapse="false">.              DTM200    DTM500    DTM-BLK    TM500    TM-BLK    STD500    STDBLK    WM500    WM-BLK    GOLD500   GOLD-BLK     </fo:block>
        <fo:block>-------------------------------------------------------------------------------------------------------------------------------</fo:block>  
     </fo:static-content>
	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">
	<#assign totalPmLiters = 0>
	<#assign totalPmProdLiters = 0>
	<#assign totalPmProdNetsale = 0>
	<#assign totalPmNetsale = 0>
	<#assign totalAmLiters = 0>	
	<#assign totalAmProdLiters = 0>
	<#assign totalAmProdNetsale = 0>	
	<#assign totalAmNetsale = 0>
	<#assign grandTotalProdNetsale = 0>	
	<#assign grandTotalNetsale = 0>
	<#assign grandTotalProdLiters = 0>
	<#assign grandTotalLiters = 0>
		<#assign summaryDetailEntries = (summaryDetailsMap.get("PM").entrySet())?if_exists>	
		<#list summaryDetailEntries as summaryDetailEntry>			
		<#assign summaryDetails = summaryDetailEntry.getValue()>
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >  
							<#assign productEntries = summaryDetails.entrySet()>
                       		<#assign  totalLtrs=0>     
                       		<#list productEntries as productEntry>         
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
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		</#list>
				    		<fo:table-body>
                				<fo:table-row> 
                					<fo:table-cell>
                            			<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(summaryDetailEntry.getKey(), "dd/MM")}</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>	
                       			<fo:table-row>
                       			    <fo:table-cell>
                            		         <fo:block text-align="right">CR</fo:block>
                            		</fo:table-cell>
                       					<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("CREDIT")}</fo:block>
                            		           </fo:table-cell>
                                		</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                    <fo:table-cell>
                            		         <fo:block text-align="right">CD</fo:block>
                            		</fo:table-cell>
                       					<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("CARD")}</fo:block>
                            		           </fo:table-cell>
                                		</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                               	 	<fo:table-cell>
                            		     <fo:block text-align="right">S.O</fo:block>
                            		</fo:table-cell>
                       					<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		           </fo:table-cell>
                                		</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                <fo:table-cell>
                            		     <fo:block text-align="right">CASH</fo:block>
                            	</fo:table-cell>
                       				<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("CASH")}</fo:block>
                            		           </fo:table-cell>
                                	</#list>	
                                </fo:table-row>
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">Litres</fo:block>
                            		</fo:table-cell>
                       					<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("totalQuantity")}</fo:block>
                            		           </fo:table-cell>
                            		           <#assign totalPmProdLiters = productEntry.getValue().get("totalQuantity")>
                            		           <#assign totalPmLiters = totalAmLiters+totalAmProdLiters>
                                		</#list>
                                	<fo:table-cell/>
                                	<fo:table-cell><fo:block>${totalPmLiters?if_exists}*</fo:block></fo:table-cell>
                                	<#assign totalPmProdLiters = 0>
                                	<#assign totalPmLiters = 0>
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">NET SALES</fo:block>
                            		</fo:table-cell>
                       				<#list productEntries as productEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${productEntry.getValue().get("totalQuantity")}</fo:block>
                            		           </fo:table-cell>
                            		      <#assign totalPmProdNetsale = productEntry.getValue().get("totalQuantity")>
                            		      <#assign totalPmNetsale = totalPmNetsale+totalPmProdNetsale>  
                                	</#list>	
                                	<fo:table-cell/>
                                	<fo:table-cell><fo:block>${totalPmNetsale?if_exists}*</fo:block></fo:table-cell>
                                	<#assign totalPmProdNetsale = 0>
                                	<#assign totalPmNetsale = 0>
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block>	</fo:table-cell>	
                                </fo:table-row>						
							</fo:table-body>
						</fo:table>
					</fo:table-cell>					
				</fo:table-row>				
				</fo:table-body>
			</fo:table>
		</fo:block>		
		</#list>		
		<#assign AMSummaryDetailEntries = (summaryDetailsMap.get("AM").entrySet())?if_exists>
		<#list AMSummaryDetailEntries as AMsummaryDetailEntry>
		<#assign AMsummaryDetails = AMsummaryDetailEntry.getValue()>
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">        	
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >       
            			<#assign AMproductEntries = AMsummaryDetails.entrySet()>
                       		<#assign  totalAmLtrs=0>    
                       		<#list AMproductEntries as AMproductEntry>
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
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		</#list> 
				    		<fo:table-body>
                				<fo:table-row> 
                					<fo:table-cell>
                            			<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(AMsummaryDetailEntry.getKey(), "dd/MM")}</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>	
                       			<fo:table-row>
                       			    <fo:table-cell>
                            		        <fo:block text-align="right">CR</fo:block>
                            		</fo:table-cell>
                       			<#list AMproductEntries as AMproductEntry>
									<fo:table-cell>
                            		        <fo:block text-align="right">${AMproductEntry.getValue().get("CREDIT")}</fo:block>
                            		</fo:table-cell>
                                </#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                    <fo:table-cell>
                            		         <fo:block text-align="right">CD</fo:block>
                            		</fo:table-cell>
                       			<#list AMproductEntries as AMproductEntry>
                            		<fo:table-cell>
                            		       <fo:block text-align="right">${AMproductEntry.getValue().get("CARD")}</fo:block>
                            		 </fo:table-cell>
                                </#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">S.O</fo:block>
                            		</fo:table-cell>
                       			<#list AMproductEntries as AMproductEntry>
                            		<fo:table-cell>
                            		       <fo:block text-align="right">${AMproductEntry.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		 </fo:table-cell>
                                </#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">CASH</fo:block>
                            		</fo:table-cell>
                       			<#list AMproductEntries as AMproductEntry>
                            		<fo:table-cell>
                            		      <fo:block text-align="right">${AMproductEntry.getValue().get("CASH")}</fo:block>
                            		</fo:table-cell>
                                </#list>	
                                </fo:table-row>
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">Litres</fo:block>
                            		</fo:table-cell>
                       			<#list AMproductEntries as AMproductEntry>
                            		<fo:table-cell>
                            		      <fo:block text-align="right">${AMproductEntry.getValue().get("totalQuantity")}</fo:block>
                            		</fo:table-cell>
                            		<#assign totalAmProdLiters = AMproductEntry.getValue().get("totalQuantity")>
                            		<#assign totalAmLiters = totalAmLiters+totalAmProdLiters>
                                </#list>
                                	<fo:table-cell/>	
                                	<fo:table-cell><fo:block>${totalAmLiters?if_exists}*</fo:block></fo:table-cell>
                                	<#assign totalAmProdLiters = 0>
                                	<#assign totalAmLiters = 0>
                                </fo:table-row>	
                                <fo:table-row>
                                <fo:table-cell>
                            		     <fo:block text-align="right">NET SALES</fo:block>
                            	</fo:table-cell>
                       				<#list AMproductEntries as AMproductEntry>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${AMproductEntry.getValue().get("totalQuantity")}</fo:block>
                            		           </fo:table-cell>
                            		      <#assign totalAmProdNetsale = AMproductEntry.getValue().get("totalQuantity")>
                            		      <#assign totalAmNetsale = totalAmNetsale+totalAmProdNetsale>  
                                	</#list>	
                                	<fo:table-cell/>
                                	<fo:table-cell><fo:block>${totalAmNetsale?if_exists}*</fo:block></fo:table-cell>
                                	<#assign totalAmProdNetsale = 0>
                                	<#assign totalAmNetsale = 0>
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block>	</fo:table-cell>	
                                </fo:table-row>					
							</fo:table-body>
						</fo:table>
					</fo:table-cell>
				</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>	
		</#list>
		<#assign GrtotalValues = GrTotalMap.entrySet()>		
		<fo:block>
       		<fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="115pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="40pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
            	<fo:table-body>
				<fo:table-row column-width="100%">
			    	<fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" >                
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
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-column column-width="70pt"/>
				    		<fo:table-body>
                				<fo:table-row> 
                					<fo:table-cell>
                            			<fo:block text-align="left">GRTot</fo:block>
                       				</fo:table-cell>
                       			</fo:table-row>
                       			<#assign  GrtotalLtrs=0>
                       			<fo:table-row>
                       			    <fo:table-cell>
                            		         <fo:block text-align="right">CR</fo:block>
                            		</fo:table-cell>
                       					<#list GrtotalValues as GrTotalEntries>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${GrTotalEntries.getValue().get("CREDIT")}</fo:block>
                            		           </fo:table-cell>
                                		</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                    <fo:table-cell>
                            		         <fo:block text-align="right">CD</fo:block>
                            		</fo:table-cell>
                       				<#list GrtotalValues as GrTotalEntries>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${GrTotalEntries.getValue().get("CARD")}</fo:block>
                            		           </fo:table-cell>
                                	</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">S.O</fo:block>
                            		</fo:table-cell>
                       					<#list GrtotalValues as GrTotalEntries>
                            		        <fo:table-cell>
                            		            <fo:block text-align="right">${GrTotalEntries.getValue().get("SPECIAL_ORDER")}</fo:block>
                            		        </fo:table-cell>
                                	</#list>	
                                </fo:table-row>	
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">CASH</fo:block>
                            		</fo:table-cell>
                       					<#list GrtotalValues as GrTotalEntries>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${GrTotalEntries.getValue().get("CASH")}</fo:block>
                            		           </fo:table-cell>
                                		</#list>	
                                </fo:table-row>
                                <fo:table-row>
                                	<fo:table-cell>
                            		     <fo:block text-align="right">Litres</fo:block>
                            		</fo:table-cell>
                       					<#list GrtotalValues as GrTotalEntries>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${GrTotalEntries.getValue().get("totalQuantity")}</fo:block>
                            		           </fo:table-cell>
                            		           
                                		<#assign grandTotalProdLiters = GrTotalEntries.getValue().get("totalQuantity")>
                            		    <#assign grandTotalLiters = grandTotalLiters+grandTotalProdLiters>  
                                	</#list>	
                                	<fo:table-cell/>
                                	<fo:table-cell><fo:block>${grandTotalLiters?if_exists}*</fo:block></fo:table-cell>
                                </fo:table-row>	
                                <fo:table-row>
                                <fo:table-cell>
                            		     <fo:block text-align="right">NET SALES</fo:block>
                            	</fo:table-cell>
                       				<#list GrtotalValues as GrTotalEntries>
                            		          <fo:table-cell>
                            		              <fo:block text-align="right">${GrTotalEntries.getValue().get("totalQuantity")}</fo:block>
                            		           </fo:table-cell>
                                		<#assign grandTotalProdNetsale = GrTotalEntries.getValue().get("totalQuantity")>
                            		    <#assign grandTotalNetsale = grandTotalNetsale+grandTotalProdNetsale>  
                                	</#list>	
                                	<fo:table-cell/>
                                	<fo:table-cell><fo:block>${grandTotalNetsale?if_exists}*</fo:block></fo:table-cell>
                                </fo:table-row>	                           							
							</fo:table-body>
						</fo:table>
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
	            	${uiLabelMap.OrderNoOrderFound}.
	             </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
    </#if>	
</fo:root>
</#escape>