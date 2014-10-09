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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "ShedMilkBillDifference.txt")}
       <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
		<#else>
        <#if totMap?has_content>
       <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before">
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       SHED NAME          :  ${(facility.facilityName).toUpperCase()}                                                                  </fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="9pt">&#160;       PERIOD FROM      :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} To ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                                                                                     </fo:block>             
            <fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt"> 
            	<fo:table>
                    <fo:table-column column-width="55pt" />
                    <fo:table-column column-width="10pt" />
	                   <#list productsList as product>
	                        <fo:table-column column-width="45pt" />
	                   </#list> 
                    <fo:table-column column-width="45pt" />               
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="center" keep-together="always" font-size="4pt">Item Name</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="center" keep-together="always" font-size="4pt">&#160;</fo:block>
                            </fo:table-cell>
                            <#list productsList as product>
                    	        <fo:table-cell>
                                    <fo:block text-align="right" keep-together="always" font-size="4pt">${(product.brandName).toUpperCase()}</fo:block>
                                </fo:table-cell>
                            </#list>
                            <fo:table-cell>
                                <fo:block text-align="right" keep-together="always" font-size="4pt">TOTAL</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>    
                </fo:table> 
            </fo:block>
            <fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
        </fo:static-content>
       <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		<fo:block font-size="7pt"><fo:table>
                    <fo:table-column column-width="55pt" />
                    <fo:table-column column-width="10pt" />
	                   <#list productsList as product>
	                        <fo:table-column column-width="45pt" />
	                   </#list> 
                    <fo:table-column column-width="45pt" />               
                    <fo:table-body>
                    	<#assign totValueMap = totMap.get("TOT")>
                       <#assign keysList = keyMap.keySet()>
                       <#list keysList as key>
                       <#if key.equalsIgnoreCase("DIFFERENCE BILL")||key.equalsIgnoreCase("NET MILK VALUE") >
                       		<fo:table-row>
	                            <fo:table-cell>
	                                <fo:block font-size="8pt">--------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
                            </fo:table-row>
                       </#if>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="left" keep-together="always" font-size="4pt">${key}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block white-space-collapse="false" text-align="right" font-size="4pt">:</fo:block>
                            </fo:table-cell>
                           	<#list productsList as product> 
                           		<#assign productMap = totMap.get(product.brandName)>
                    	        <fo:table-cell>
                                    <fo:block text-align="right" font-size="4pt">${(productMap.get(key))?if_exists?string("##0.00")}</fo:block>
                                </fo:table-cell>
                        	</#list>     
                        	
                            <fo:table-cell>
                                <fo:block text-align="right" font-size="4pt">${totValueMap.get(key)?if_exists?string("##0.00")} </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
	                            <fo:table-cell>
	                                <fo:block font-size="4pt">&#160;</fo:block>
	                            </fo:table-cell>
                            </fo:table-row>
                        </#list> 
                        	<fo:table-row>
	                        	<fo:table-cell>
	                           		<fo:block font-size="4pt">--------------------------------------------------------------------------------------</fo:block>
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
              <fo:block font-size="14pt">${uiLabelMap.NoOrdersFound}. </fo:block>
           </fo:flow>
         </fo:page-sequence>
     </#if>
     </#if>
   </fo:root> 
 </#escape>     