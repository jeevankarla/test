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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-top=".3in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      
        <#assign dayWiseEntry = dayWiseEntryMap.entrySet()>
        <#if dayWiseEntry?has_content>          	            	              
        <#list dayWiseEntry as checkListReportValues>      
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="12pt">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<#assign UNIT = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
				<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;                      ${reportHeader.description?if_exists} -${UNIT.get("facilityName")?if_exists}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">CHECK LIST FOR THE PERIOD: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectFromDate, "dd-MM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectThruDate, "dd-MM-yyyy")}   UserLogin : ${userLoginId?if_exists} Page: <fo:page-number/></fo:block>
        		<fo:block>-----------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;  DATE    A/P  B/C  CON  FRM.CODE DESCRIPTION     QTY-LTR    LR    FAT   SNF     KGF.   KGS.</fo:block>
        		<fo:block>-----------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="12pt"> 
        		<#assign checkListReportEntryList = checkListReportValues.getValue()>
		    		<fo:block >
	                 	<fo:table>
	                    <fo:table-column column-width="80pt"/>
	                    <fo:table-column column-width="30pt"/>  
	               	    <fo:table-column column-width="35pt"/>
	                    <fo:table-column column-width="30pt"/>  
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="87pt"/>
	                    <fo:table-column column-width="90pt"/>  
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="47pt"/>
	                    <fo:table-column column-width="47pt"/>  
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="55pt"/>   
	                    <fo:table-body>
	                    	<#assign totQtyLtrs=0>
	                    	<#assign totKgFat=0>
	                    	<#assign totKgSnf=0>
	                    	<#assign totSQtyLtr=0>
	                    	<#assign sTotKgFat =0>
                    		<#list checkListReportEntryList as checkListReport>			           		
			           		<#assign facility = delegator.findOne("Facility", {"facilityId" : checkListReport.originFacilityId}, true)>
	                        <fo:table-row>
	                        	<fo:table-cell>
	                            	<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReportValues.getKey(), "dd-MM-yyyy")}</fo:block>	                               
	                            </fo:table-cell>	                           
	                        	<fo:table-cell>
	                        		<#if parameters.purchaseTime == "AM">
	                            		<fo:block text-align="left">A</fo:block>
	                            	<#else>
	                            		<fo:block text-align="left">P</fo:block>
	                            	</#if>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                        	<#list procurementProductList as procProducts>
	                        		<#if checkListReport.productName == procProducts.productName>
	                            	<fo:block text-align="left" keep-together="always">${procProducts.brandName}</fo:block>
	                            	</#if>
	                            </#list>	
	                            </fo:table-cell>
	                            <fo:table-cell>	                        		
	                            		<fo:block text-align="left">G</fo:block>	                            	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell>	                            
	                            	<fo:block text-align="left">${checkListReport.facilityCode?if_exists}</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >	
	                        		<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),15)}</fo:block>
	                        	</fo:table-cell>         
	                        	<#assign qty= checkListReport.quantityLtrs>           		
	                        	<#assign kgFat =((checkListReport.fat*(checkListReport.quantityKgs))/100)>
	                        	<#assign kgSnf =((checkListReport.snf*(checkListReport.quantityKgs))/100)>
	                        	<#assign totQtyLtrs=totQtyLtrs+qty>
	                        	<#assign totKgFat=totKgFat+kgFat>
	                    		<#assign totKgSnf=totKgSnf+kgSnf>	                    		
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right" keep-together="always">${qty?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="right">${checkListReport.lactoReading?if_exists?string("##0.00")}</fo:block>
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="right">${checkListReport.fat?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>                   
	                            <fo:table-cell >	
	                            	<fo:block text-align="right">${checkListReport.snf?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell> 
	                            <fo:table-cell >	
	                            	<fo:block text-align="right">${kgFat?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block text-align="right">${kgSnf?string("##0.00")}</fo:block>                               
	                            </fo:table-cell> 
	                          </fo:table-row>
	                         <#if checkListReport.sQuantityLtrs?has_content>
	                          <fo:table-row>  
	                          	<fo:table-cell>
	                            	<fo:block text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkListReportValues.getKey(), "dd-MM-yyyy")}</fo:block>	                               
	                            </fo:table-cell>	                           
	                        	<fo:table-cell>
	                        		<#if parameters.purchaseTime == "AM">
	                            		<fo:block text-align="left">A</fo:block>
	                            	<#else>
	                            		<fo:block text-align="left">P</fo:block>
	                            	</#if>	                               
	                            </fo:table-cell>	                            
	                            <fo:table-cell >	
	                        	<#list procurementProductList as procProducts>
	                        		<#if checkListReport.productName == procProducts.productName>
	                            	<fo:block text-align="left" keep-together="always">${procProducts.brandName}</fo:block>
	                            	</#if>
	                            </#list>	
	                            </fo:table-cell>
	                            <fo:table-cell>	                        		
	                            		<fo:block text-align="left">S</fo:block>	                            	                               
	                            </fo:table-cell>	
	                            <#assign sQtyKgs = (checkListReport.sQuantityLtrs*1.03)>                           
	                           
	                            <#assign totSQtyLtr=totSQtyLtr+(checkListReport.sQuantityLtrs)>                 		
	                    		<#assign sKgFat =((sQtyKgs*checkListReport.sFat)/100)>
	                    		<#assign sTotKgFat =sTotKgFat+sKgFat>
	                            <fo:table-cell>	                            
	                            	<fo:block text-align="left">${checkListReport.facilityCode?if_exists}</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >	
	                        		<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("facilityName").toUpperCase())),15)}</fo:block>
	                        	</fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="right">${(checkListReport.sQuantityLtrs?string("##0.00"))?if_exists}</fo:block>                                    
	                            </fo:table-cell>	
	                            <fo:table-cell >	
	                            	<fo:block text-align="right"></fo:block>                               
	                            </fo:table-cell>                                    
			                    <fo:table-cell >
	                            	<fo:block text-align="right">${(checkListReport.sFat?string("##0.00"))?if_exists}</fo:block>                                    
	                            </fo:table-cell>	   
	                            <fo:table-cell/>                    
	                            <fo:table-cell>
	                            	<fo:block text-align="right">${sKgFat?string("##0.000")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell>
	                            
	                            </fo:table-cell>                                                      
				            </fo:table-row>
				            </#if>
				        </#list>
				        <#assign checkListDate=checkListReportValues.getKey()>
				        
				        	<#--<fo:table-row>
			             		<fo:table-cell><fo:block linefeed-treatment="preserve">---------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
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
		                   	     	<fo:table-cell/>
		                   	     	<fo:table-cell>
		                            	<fo:block text-align="left">${procProducts.brandName}</fo:block>	                               
		                            </fo:table-cell>
		                        	<fo:table-cell>
		                            	<fo:block text-align="left" text-indent="7pt">${dayTotalsMap.get(checkListDate).get("QtyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="25pt">${dayTotalsMap.get(checkListDate).get("kgFat"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="45pt">${dayTotalsMap.get(checkListDate).get("kgSnf"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="60pt">${dayTotalsMap.get(checkListDate).get("SqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="70pt">${dayTotalsMap.get(checkListDate).get("SFat"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="70pt">${dayTotalsMap.get(checkListDate).get("CqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
		                            <fo:table-cell>
		                            	<fo:block text-align="left" text-indent="70pt">${dayTotalsMap.get(checkListDate).get("PtcRcyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
		                            </fo:table-cell>
						         </fo:table-row>						         
						     </#list>-->	
						
					 	<fo:table-row>
		             		<fo:table-cell><fo:block>-----------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
		             	</fo:table-row>						
						 <fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                                                             	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right">${totQtyLtrs?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell/>
                            <fo:table-cell>
                            	<fo:block text-align="right"><#if totQtyLtrs !=0>${((totKgFat*100)/(totQtyLtrs*1.03))?string("##0.00")}</#if></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right"><#if totQtyLtrs !=0>${((totKgSnf*100)/(totQtyLtrs*1.03))?string("##0.00")}</#if></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totKgFat?string("##0.000")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totKgSnf?string("##0.000")}</fo:block>	                               
                            </fo:table-cell>
                         </fo:table-row>
                       <#if totSQtyLtr !=0>  
                         <fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>                   	     	
                   	     	<fo:table-cell>
                   	     		<fo:block text-align="left" Keep-together="always" >Sour Total</fo:block>
                   	     	</fo:table-cell>
                   	     	<fo:table-cell/>                                                             	     	
                        	<fo:table-cell>
                            	<fo:block text-align="right">${totSQtyLtr?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell/>
                            <fo:table-cell>
                            	<fo:block text-align="right"><#if totSQtyLtr !=0>${((sTotKgFat*100)/(totSQtyLtr*1.03))?string("##0.00")}</#if></fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell/>
                            <fo:table-cell>
                            	<fo:block text-align="right">${sTotKgFat?string("##0.000")}</fo:block>	                               
                            </fo:table-cell>                            
                         </fo:table-row>
                        </#if>                      
				         <fo:table-row>
			             	<fo:table-cell><fo:block linefeed-treatment="preserve">-----------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
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
	       		 <fo:block>
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
     </#if>   
     </fo:root>
</#escape>