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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".5in">
                <fo:region-body margin-top=".3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "FeedAndOtherRecoveries.txt")}
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	 	<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" font-size="7pt">&#160;              UNIT NAME :${facility.get("facilityName")?if_exists}      DATE OF ENDING :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}        Page:<fo:page-number/>   </fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                 <fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <!--<fo:table-column column-width="1pt"/>
                    <fo:table-column column-width="25pt"/>--> 
                    <fo:table-column column-width="25pt"/>  
               	    <fo:table-column column-width="75pt"/>
               	    <fo:table-column column-width="47pt"/>
               	    <fo:table-column column-width="57pt"/>
               	    <#list orderAdjItemsList as orderAdjType>
                    <fo:table-column column-width="47pt"/>
               		</#list>
                    
		          	<fo:table-header border-width="1pt" border-style="dotted">
		          		<!--<fo:table-cell><fo:block text-align="left" font-size="7pt"></fo:block></fo:table-cell>
		          		<fo:table-cell><fo:block text-align="left" font-size="7pt">Route No</fo:block></fo:table-cell>-->
		          		<fo:table-cell><fo:block text-align="left" font-size="7pt">Center Code</fo:block></fo:table-cell>
		          		<fo:table-cell><fo:block text-align="left" font-size="7pt">Center Name</fo:block></fo:table-cell>
               	    <#list orderAdjItemsList as orderAdjType>
		            	<fo:table-cell><fo:block text-align="right" font-size="7pt">${orderAdjType.description?if_exists}</fo:block><fo:block text-align="right" font-size="7pt">(RS)</fo:block></fo:table-cell>
		            </#list>   	
				    </fo:table-header>		           
                    <fo:table-body>
	                   <fo:table-row>     
                   	     	<fo:table-cell>
	                        		<fo:block text-align="left" font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        </fo:table-cell>
				        </fo:table-row>
                  		<#assign adjustmentItem = adjustments.entrySet()>
	                    <#list adjustmentItem as adjustedAmount>
	                    <#assign adjustmentDetail = adjustedAmount.getValue()>
                   	 	<fo:table-row>
                   	     	<!--<fo:table-cell>
	                            	<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${adjustmentDetail.get("routeNo")?if_exists}</fo:block>        
	                        </fo:table-cell>-->
	                        <fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${adjustmentDetail.get("centerCode")?if_exists}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${adjustmentDetail.get("centerName")?if_exists}</fo:block>        
	                        </fo:table-cell>
	                      <#list orderAdjItemsList as orderAdjType>
	                       	<#assign adjustVal = adjustmentDetail.get(orderAdjType.orderAdjustmentTypeId)>    
	                       	<#if !adjustVal?has_content>
	                       		<#assign adjustVal = 0>
	                       	</#if>
	                           <fo:table-cell><fo:block text-align="right" keep-together="always" font-size="7pt" white-space-collapse="false">${adjustVal?if_exists?string("##0.00")}</fo:block> </fo:table-cell>
	                   		<#assign adjustVal = 0>
	                   		</#list>
	                    </fo:table-row>    	 
              			</#list>
              		 	<fo:table-row>
                   	     	<fo:table-cell>
	                        		<fo:block text-align="left" font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        </fo:table-cell>
				     	</fo:table-row>
				        <fo:table-row>
                   	     	<fo:table-cell/>
                   	     	<!--<fo:table-cell/>
                   	     	<fo:table-cell/>-->
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="7pt">Total:</fo:block>	                               
	                        </fo:table-cell>
	                        <#assign header = adjustmentsTotMap.entrySet()>
               	    		<#list header as head>
	                           <fo:table-cell><fo:block text-align="right" keep-together="always" font-size="7pt" white-space-collapse="false">${head.getValue()?if_exists?string("##0.00")}</fo:block> </fo:table-cell>
	                   		</#list>
				        </fo:table-row>
				         <fo:table-row>
                   	     	<fo:table-cell>
	                        		<fo:block text-align="left" font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                        </fo:table-cell>
				        </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>      
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>