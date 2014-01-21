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
            <fo:simple-page-master master-name="main" page-height="10in" page-width="12in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>       
        ${setRequestAttribute("OUTPUT_FILENAME", "chlst.txt")}       
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">.           CHECK LIST FOR MILK PROCUREMENT PARTICULARS AS ON:   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate, "dd-MM-yyyy")}                                          Page:<fo:page-number/>   </fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">.                                                                                                                                                                       GOOD MILK                SOUR MILK             CURD     PTC RECVRY</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">.                                                                                                                                                              ------------------------------    --------------------------     -----------    -----------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">   		           
                 <fo:block font-size="10pt">
                 	<fo:table border-width="1pt" border-style="dotted">
                 	 <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="30pt"/>  
                    <fo:table-column column-width="40pt"/>
               	    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="50pt"/>   
		          	<fo:table-header border-width="1pt" border-style="dotted">	
		          		<fo:table-cell><fo:block text-align="left" font-size="10pt">S.NO</fo:block></fo:table-cell>	            	
		          		<fo:table-cell><fo:block text-align="left" font-size="10pt">USERLOGIN</fo:block></fo:table-cell>
		          		<fo:table-cell><fo:block text-align="left" font-size="10pt">CREATED </fo:block><fo:block text-align="left" font-size="10pt">DATE TIME</fo:block></fo:table-cell>		  
		          		<fo:table-cell><fo:block text-align="left" font-size="10pt">UNIT</fo:block></fo:table-cell>                  	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="10pt">MCC/</fo:block><fo:block text-align="left" font-size="10pt">CODE</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block text-align="left" white-space-collapse="false" font-size="10pt">CENTER NAME</fo:block></fo:table-cell>
		            	<fo:table-cell ><fo:block text-align="right" font-size="9pt">TIME</fo:block></fo:table-cell>
		           		<fo:table-cell ><fo:block text-align="right" font-size="10pt">TYPE</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">QTY</fo:block><fo:block text-align="right">KGS</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">FAT</fo:block><fo:block text-align="right">(%)</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">SNF</fo:block><fo:block text-align="right">(%)</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">QTY</fo:block><fo:block text-align="right">LTS</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">FAT</fo:block><fo:block text-align="right">(%)</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">QTY</fo:block><fo:block text-align="right">LTS</fo:block></fo:table-cell>
		                <fo:table-cell ><fo:block text-align="right" font-size="10pt">QTY</fo:block><fo:block text-align="right">KGS</fo:block></fo:table-cell>
				    </fo:table-header>		           
                    <fo:table-body>
                    	<#assign number=0>
                    	<#if orderItemsList?has_content>
                    		<#list orderItemsList as orderItems>
                    			<#assign number=number+1>
                    			<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : orderItems.originFacilityId}, true)>
                    			<#assign agentDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", orderItems.originFacilityId)).get("unitFacility")>
		                    	<fo:table-row>		                    	
		                    		<fo:table-cell>
		                    			<fo:block>${number}.</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block>${orderItems.changeByUserLoginId?if_exists}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block>${orderItems.changeDatetime?if_exists}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block>${agentDetails.facilityCode}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block>${facilityDetails.facilityCode?if_exists}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("facilityName").toUpperCase())),15)}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block text-indent="30pt">${orderItems.supplyTypeEnumId?if_exists}</fo:block>
		                    		</fo:table-cell>
		                    		<#list procurementProductList as procProducts>
		                    			<#if procProducts.productId==orderItems.productId>
				                    		<fo:table-cell>
				                    			<fo:block text-indent="30pt">${procProducts.brandName}</fo:block>
				                    		</fo:table-cell>
				                    	</#if>	
		                    		</#list>	
		                    		<fo:table-cell>
		                    			<fo:block text-align="right">${orderItems.quantity?if_exists?string("##0.0")}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block text-align="right">${orderItems.fat?if_exists?string("##0.0")}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>
		                    			<fo:block text-align="right">${orderItems.snf?if_exists?string("##0.00")}</fo:block>
		                    		</fo:table-cell>
		                    		<fo:table-cell>		                    			
		                    			<fo:block text-align="right">${orderItems.sQuantityLtrs?if_exists}</fo:block>		                    			
		                    		</fo:table-cell>
		                    		<fo:table-cell>		                    			
		                    			<fo:block text-align="right">${orderItems.sFat?if_exists}</fo:block>		                    			
		                    		</fo:table-cell>
		                    		<fo:table-cell>		                    			
		                    			<fo:block text-align="right">${orderItems.cQuantityLtrs?if_exists}</fo:block>		                    			
		                    		</fo:table-cell>
		                    		<fo:table-cell>		                    			
		                    			<fo:block text-align="right">${orderItems.ptcQuantity?if_exists}</fo:block>		                    			
		                    		</fo:table-cell>		                    		
		                    	</fo:table-row>
		                    </#list>	
                    	</#if>   
                    	<fo:table-row>
                    		<fo:table-cell><fo:block>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                    	</fo:table-row>
                    	<#list procurementProductList as procProducts>
                   	     <fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="right">${procProducts.brandName}</fo:block>	                               
                            </fo:table-cell>
                        	<fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totQtyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totFat"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSng"+procProducts.brandName)?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSqtyFat"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totCqtyLts"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totPtcRcyKgs"+procProducts.brandName)?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
				         </fo:table-row>
				         </#list>   
                    	<#if totMap?has_content>
                    		<fo:table-row border-style="dotted">
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell/>
                   	     	<fo:table-cell>
                            	<fo:block text-align="right">TOT</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell/>
                   	     	<fo:table-cell/>
                        	<fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totQtyKgsTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totFatTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSngTot")?if_exists?string("##0.00")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSqtyLtsTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totSqtyFatTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totCqtyLtsTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
                            <fo:table-cell>
                            	<fo:block text-align="right">${totMap.get("totPtcRcyKgsTot")?if_exists?string("##0.0")}</fo:block>	                               
                            </fo:table-cell>
				         </fo:table-row>
                    	</#if>              	
                    </fo:table-body>
                </fo:table>
               </fo:block>      
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>