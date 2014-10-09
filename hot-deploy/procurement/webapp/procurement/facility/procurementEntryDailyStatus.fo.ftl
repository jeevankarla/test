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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15.5in" margin-left="0.09in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if shedWiseProcMap?has_content>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="10pt">
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;                     PROCUREMENT ENTRY STATUS :: SHOWING DAY-WISE ENTRIES &#160;&#160;&#160;&#160;&#160;    FROM :${fromDateStr}&#160;&#160;&#160; TO: ${thruDateStr} Page: <fo:page-number/></fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="10pt">
        			<fo:table>
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="90pt"/>
	                   	<#list dayKeysList as dayKey>
							<fo:table-column column-width="60pt"/>
		                 </#list>
		                 <fo:table-body>
	                    <fo:table-row>
	                        	<fo:table-cell>
	                            	<fo:block text-align="left">SHED</fo:block>
	                            	<fo:block text-align="left">CODE</fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block text-align="center" keep-together="always">SHED NAME</fo:block>	                               
	                            </fo:table-cell>
	                            <#list dayKeysList as dayKey>
	                            <fo:table-cell>
	                            	<fo:block text-align="center">${dayKey}</fo:block>
	                            <!--	<fo:block text-align="right">Entries</fo:block> -->
	                            <!--	<fo:block text-align="right">Qty(Kgs)</fo:block> -->	                               
	                            </fo:table-cell>
	                            </#list>
	                    </fo:table-row>
	                    </fo:table-body>
	                </fo:table>         
        		</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
        		<#assign shedProcMapValues = shedWiseProcMap.entrySet()>
        		
		        		<fo:block font-size="10pt">
			        		<fo:table>
				                    <fo:table-column column-width="30pt"/>
				                    <fo:table-column column-width="80pt"/>
				                   	<#list dayKeysList as dayKey>
										<fo:table-column column-width="60pt"/>
					                 </#list>
					                 <fo:table-body>
					                 	<#list shedProcMapValues as shedProcMap>
		        							<#assign facilityId = shedProcMap.getKey()>	
		        							<#assign shedDetails = delegator.findOne("Facility", {"facilityId" : facilityId}, true)>
						                    <fo:table-row>
						                        	<fo:table-cell>
						                            	<fo:block text-align="left">${shedDetails.facilityCode}</fo:block>	                               
						                            </fo:table-cell>
						                        	<fo:table-cell>
						                            	<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shedDetails.get("facilityName").toUpperCase().replace("MILK",""))),15)}</fo:block>	                               
						                            </fo:table-cell>
						                            <#assign dateWiseMap =  shedProcMap.getValue()>
						                            <#list dayKeysList as dayKey>
						                              <#assign quantityMap = dateWiseMap.get(dayKey)> 
							                            <fo:table-cell>
							                            	<fo:block text-align="right">${quantityMap.get("entries")?if_exists?string("#0")}</fo:block>
							                    <!--        	<fo:block text-align="right">${quantityMap.get("quantity")?if_exists?string("#0.00")}</fo:block> -->
							                            </fo:table-cell>
						                            </#list>
						                    </fo:table-row>
						                    <fo:table-row>
						                    	<fo:table-cell>
						                    		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
						                    	</fo:table-cell>
						                    </fo:table-row>	
						                    </#list>
				                    </fo:table-body>
				                </fo:table>
				                </fo:block>	    
			           	</fo:flow>    
		           	       
           	
        </fo:page-sequence>
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