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
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#assign pageStart= parameters.pageStart>
        <#assign pageEnd= parameters.pageEnd>
        ${setRequestAttribute("OUTPUT_FILENAME", "ProcCheckList.txt")}
        ${setRequestAttribute("VST_PAGE_START", "${pageStart}")}
        ${setRequestAttribute("VST_PAGE_END", "${pageEnd}")}
      <#if dayWiseEntryMap?has_content>  
        <#assign dayWiseEntry = dayWiseEntryMap.entrySet()>        	            	              
        <#list dayWiseEntry as checkListReportValues>      
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="10pt">
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">CHECK LIST FOR MILK PROCUREMENT PARTICULARS DATED: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReportValues.getKey(), "dd-MM-yyyy")}  <#if parameters.purchaseTime == "AM">MORNING</#if><#if parameters.purchaseTime == "PM">Evening</#if>  UserLogin : ${userLoginId?if_exists} Page: <fo:page-number/></fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<#assign unitDetails = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">UNIT : ${unitDetails.facilityCode?if_exists}       NAME :<#assign UNIT = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)> ${UNIT.get("facilityName")?if_exists}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                          GOOD MILK          SOUR MILK     CURD   PTC RECVRY</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                                     --------------------   -----------  --------- -----------</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">SNO MCC/CODE  CENTER NAME       TYP     QTY    FAT    SNF     QTY    FAT     QTY     QTY</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">&#160;                               MLK     KGS    (%)    (%)     LTS    (%)     LTS     KGS</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-----------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
        		<#assign checkListReportEntryList = checkListReportValues.getValue()>
		    		<fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="40pt"/>  
	               	    <fo:table-column column-width="95pt"/>
	                    <fo:table-column column-width="95pt"/>  
	                    <fo:table-column column-width="20pt"/>
	                    <fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="45pt"/>  
	                    <fo:table-column column-width="47pt"/>
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="40pt"/>  
	                    <fo:table-column column-width="45pt"/>
	                    <fo:table-column column-width="60pt"/>   
	                    <fo:table-body>
                    		<#assign temp =0>	                    	                   		                    	
			           		<#list checkListReportEntryList as checkListReport>			           		
			           		<#assign temp = temp+1>				           		
			           		<#assign facility = delegator.findOne("Facility", {"facilityId" : checkListReport.originFacilityId}, true)>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                            	<fo:block text-align="left">${temp}.</fo:block>	                               
	                            </fo:table-cell>	                           
	                        	<fo:table-cell>
	                            	<fo:block text-align="right">${checkListReport.facilityCode?if_exists}</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >	
	                        		<fo:block text-align="left" keep-together="always" text-indent="15pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),15)}</fo:block>
	                        	</fo:table-cell>	                        	
	                        	<fo:table-cell >	
	                        	<#list procurementProductList as procProducts>
	                        		<#if checkListReport.productName == procProducts.productName>
	                            	<fo:block text-align="center" keep-together="always">${procProducts.brandName}</fo:block>
	                            	</#if>
	                            </#list>	
	                            </fo:table-cell> 
	                            		
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right" keep-together="always">${checkListReport.quantity?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>                                                                      
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right">${checkListReport.fat?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>                   
	                            <fo:table-cell >	
	                            	<fo:block text-align="right">${checkListReport.snf?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>  
	                            <fo:table-cell >
	                            	<fo:block text-align="right"><#if checkListReport.sQuantityLtrs?has_content>${(checkListReport.sQuantityLtrs?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>	                                    
			                    <fo:table-cell >
	                            	<fo:block text-align="right"><#if checkListReport.sFat?has_content>${(checkListReport.sFat?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right"><#if checkListReport.cQuantityLtrs?has_content>${(checkListReport.cQuantityLtrs?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>
	                            <fo:table-cell >	                            	  	
	                            	<fo:block text-align="right"><#if checkListReport.ptcQuantity?has_content>${(checkListReport.ptcQuantity?string("##0.0"))?if_exists}<#else>0.0</#if></fo:block>                                    
	                            </fo:table-cell>                             
				            </fo:table-row>
				            <fo:table-row>
			             		<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			             	</fo:table-row>			           	            
				        </#list>
				        	
			        <fo:table-row>   
			        <fo:table-cell>	  	
			          <fo:block>
	                 	<fo:table>
	                    <fo:table-column column-width="40pt"/>
	                    <fo:table-column column-width="50pt"/>  
	               	    <fo:table-column column-width="95pt"/>
	                    <fo:table-column column-width="95pt"/>  
	                    <fo:table-column column-width="35pt"/>
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="50pt"/>  
	                    <fo:table-column column-width="53pt"/>
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="50pt"/>  
	                    <fo:table-column column-width="55pt"/>
	                    <fo:table-column column-width="60pt"/>   
	                    <fo:table-body>	
			             	<#assign checkListDate=checkListReportValues.getKey()>
			             		 <fo:table-row>
			             		<fo:table-cell><fo:block>---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
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
		                   	     	<fo:table-cell/>		                   	     	
		                   	     	<fo:table-cell>
		                            	<fo:block text-align="left" text-indent="47pt">${procProducts.brandName}</fo:block>	                               
		                            </fo:table-cell>
		                        	<fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("QtyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("kgFat"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("kgSnf"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("SqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("SFat"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("CqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("PtcRcyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
						         </fo:table-row>						         
						     </#list>	
						     <fo:table-row>
		             		<fo:table-cell><fo:block >---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
		             	</fo:table-row>
		             	<fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="left">TOTAL</fo:block>	                               
                            </fo:table-cell>                                     	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("QtyKgs")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("kgFat")?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("kgSnf")?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("SqtyLts")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("SFat")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("CqtyLts")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${dayTotalsMap.get(checkListDate).get("PtcRcyKgs")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
				         </fo:table-row>
				         <fo:table-row>
			             	<fo:table-cell><fo:block>---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			             </fo:table-row>
			             <fo:table-row>
			             	<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			             </fo:table-row>
			             <fo:table-row>
			             <fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="left"></fo:block>	                               
                            </fo:table-cell>                                     	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block white-space-collapse="false" white-space-treatment="preserve" keep-together="always">Verified By</fo:block>                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"></fo:block>	                               
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
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
     </#if>   
     </fo:root>
</#escape>