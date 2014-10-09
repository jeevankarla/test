
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-top=".3in"  margin-left=".3in" margin-right=".3in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#assign pageStart= parameters.pageStart>
        <#assign pageEnd= parameters.pageEnd>
        ${setRequestAttribute("OUTPUT_FILENAME", "ProcCheckListForLR.txt")}
        ${setRequestAttribute("VST_PAGE_START", "${pageStart}")}
        ${setRequestAttribute("VST_PAGE_END", "${pageEnd}")}
      <#if dayWiseEntryMap?has_content>  
        <#assign dayWiseEntry = dayWiseEntryMap.entrySet()>        	            	              
        <#list dayWiseEntry as checkListReportValues>      
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always" font-size="5pt">CHECK LIST FOR MILK PROCUREMENT PARTICULARS DATED: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReportValues.getKey(), "dd-MM-yyyy")} <#if parameters.purchaseTime == "AM">MORNING</#if><#if parameters.purchaseTime == "PM">Evening</#if>UserLogin :${userLoginId?if_exists}Page:<fo:page-number/></fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">-----------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always" font-size="5pt">UNIT : ${unitDetails.facilityCode?if_exists}       NAME :<#assign UNIT = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)> ${UNIT.get("facilityName")?if_exists}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                          GOOD MILK            SOUR MILK   CURD  PTC RCVRY</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                                  -----------------------------   ---------   ----  ---</fo:block>
    			<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">SNO MCC/CODE  CENTER NAME     TYP     QTY     FAT    SNF    LR    QTY   FAT   QTY   QTY</fo:block>
    		    <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">&#160;                             MLK     KGS     (%)    (%)          LTS   (%)   LTS   KGS</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="5pt">-------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" > 
        		<#assign checkListReportEntryList = checkListReportValues.getValue()>
		    		<fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="20pt"/>  
	               	    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="10pt"/>  
	                    <fo:table-column column-width="25pt"/>
	                    <fo:table-column column-width="21pt"/>
	                    <fo:table-column column-width="22pt"/>  
	                    <fo:table-column column-width="23pt"/>
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="20pt"/>  
	                    <fo:table-column column-width="18pt"/>
	                    <fo:table-column column-width="20pt"/>   
	                    <fo:table-body>
                    		<#assign temp =0>	                    	                   		                    	
			           		<#list checkListReportEntryList as checkListReport>			           		
			           		<#assign temp = temp+1>				           		
			           		<#assign facility = delegator.findOne("Facility", {"facilityId" : checkListReport.originFacilityId}, true)>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                            	<fo:block font-size="5pt" text-align="left">${temp}.</fo:block>	                               
	                            </fo:table-cell>	                           
	                        	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="5pt">${checkListReport.facilityCode?if_exists}</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >	
	                        		<fo:block text-align="left" font-size="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),15)}</fo:block>
	                        	</fo:table-cell>	                        	
	                        	<fo:table-cell >	
	                        	<#list procurementProductList as procProducts>
	                        		<#if checkListReport.productName == procProducts.productName>
	                            	<fo:block text-align="center" font-size="5pt" >${procProducts.brandName}</fo:block>
	                            	</#if>
	                            </#list>	
	                            </fo:table-cell> 
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right" font-size="5pt" >${checkListReport.quantity?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>                                                                      
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right" font-size="5pt">${checkListReport.fat?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>                   
	                            <fo:table-cell >	
	                            	<fo:block text-align="right" font-size="5pt">${checkListReport.snf?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell> 
	                            <fo:table-cell >
	                            <#if (tenantConfigSettings.enableLR =='Y')>
	                            		<fo:block text-align="right" font-size="5pt">${checkListReport.lactoReading?if_exists?string("##0.00")}</fo:block>
	                            </#if>
	                            </fo:table-cell> 
	                            <fo:table-cell >
	                            	<fo:block text-align="right" font-size="5pt"><#if checkListReport.sQuantityLtrs?has_content>${(checkListReport.sQuantityLtrs?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>	                                    
			                    <fo:table-cell >
	                            	<fo:block text-align="right" font-size="5pt"><#if checkListReport.sFat?has_content>${(checkListReport.sFat?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right" font-size="5pt"><#if checkListReport.cQuantityLtrs?has_content>${(checkListReport.cQuantityLtrs?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>
	                            <fo:table-cell >	                            	  	
	                            	<fo:block text-align="right" font-size="5pt"><#if checkListReport.ptcQuantity?has_content>${(checkListReport.ptcQuantity?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>                             
				            </fo:table-row>
				             <fo:table-row>
			             	<fo:table-cell><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>
			             </fo:table-row>		           	            
				        </#list>
			        <fo:table-row>   
			        <fo:table-cell>	  	
			          <fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="52pt"/>  
	               	    <fo:table-column column-width="17pt"/>
	                    <fo:table-column column-width="15pt"/>  
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="22pt"/>  
	                    <fo:table-column column-width="24pt"/>
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="20pt"/>  
	                    <fo:table-column column-width="18pt"/>
	                    <fo:table-column column-width="20pt"/>   
	                    <fo:table-body>	
			             	<#assign checkListDate=checkListReportValues.getKey()>
			             	<fo:table-row>
			             		<fo:table-cell><fo:block font-size="5pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			             	</fo:table-row>
				        	<fo:table-row>
			             		<fo:table-cell/>
	                   	     	<fo:table-cell/>                            		
	                   	     	<fo:table-cell/>
			             		<fo:table-cell>			             			
			             			<fo:block font-weight="bold"></fo:block>
			             		</fo:table-cell>
			             	</fo:table-row>
			             	 <#list procurementProductList as procProducts>				             	 	 
		                   	     <fo:table-row>
		                   	     	<fo:table-cell/>
		                   	     	<fo:table-cell/>
		                   	     	<fo:table-cell>
		                            	<fo:block text-align="left" font-size="5pt">${procProducts.brandName}</fo:block>	                               
		                            </fo:table-cell>
		                        	<fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("QtyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("kgFat"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("kgSnf"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("SqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("SFat"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("CqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("PtcRcyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
						         </fo:table-row>						         
						     </#list>	
						     <fo:table-row>
		             		<fo:table-cell><fo:block font-size="5pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
		             	</fo:table-row>
		             	<fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="left" font-size="5pt">TOTAL</fo:block>	                               
                            </fo:table-cell>                      	     	
                   	     	<fo:table-cell>
                            	<fo:block text-align="left" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>                                     	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("QtyKgs")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("kgFat")?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("kgSnf")?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
		                        <fo:block text-align="right" font-size="5pt"></fo:block>	                               
		                    </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("SqtyLts")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("SFat")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("CqtyLts")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt">${dayTotalsMap.get(checkListDate).get("PtcRcyKgs")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
				         </fo:table-row>
				         <fo:table-row>
			             	<fo:table-cell><fo:block font-size="5pt">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			             </fo:table-row>
			             <fo:table-row>
			             	<fo:table-cell><fo:block linefeed-treatment="preserve" font-size="5pt">&#xA;</fo:block></fo:table-cell>
			             </fo:table-row>
			             <fo:table-row>
			             <fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="left" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>                                     	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block white-space-collapse="false" font-size="5pt" white-space-treatment="preserve" keep-together="always">Verified By</fo:block>                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right" font-size="5pt"></fo:block>	                               
                            </fo:table-cell>
				         </fo:table-row>
						 </fo:table-body>
						</fo:table>
					</fo:block>	 
					</fo:table-cell>
					</fo:table-row>					
	                    </fo:table-body>
	                </fo:table>
               </fo:block>	
               <fo:block></fo:block>	    
           	</fo:flow>           
        </fo:page-sequence>
        </#list>
     <#else>
     	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 <fo:block font-size="5pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
     </#if>   
     </fo:root>
</#escape>