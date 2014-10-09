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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".5in" margin-top="0.5in">
                <fo:region-body margin-top="0.4in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "YearWiseAnalysis.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else> 
	<#if shedTotValuesMap?has_content> 
	<#assign grandTotal = 0>
   <fo:page-sequence master-reference="main">
  	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="5pt">
  			<fo:block text-align="left" white-space-collapse="false" font-size="5pt" keep-together="always">&#160;&#160;&#160;&#160;						YEAR-WISE MPF.HYDERABAD MILK RECEIPTS (QUANTITY-LTS)PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} (FIGURES IN LAKHS)</fo:block>
            <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block font-size="5pt">
			<fo:table font-size="5pt">
				<fo:table-column column-width="15pt"/>
				<fo:table-column column-width="50pt"/>
				<#list yearKeyList as yearKey>
				<fo:table-column column-width="40pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">CODE</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="left" font-size="5pt" keep-together="always">SHED/UNION NAME</fo:block>
						</fo:table-cell>
						<#list yearKeyList as yearKey>
							<fo:table-cell>
								<fo:block text-align="right" font-size="5pt" keep-together="always">${yearKey}</fo:block>
							</fo:table-cell>
						</#list>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		    </fo:block>
         <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
 	</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt">
	 	<fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="15pt"/>
				<fo:table-column column-width="50pt"/>
				<#list yearKeyList as yearKey>
				<fo:table-column column-width="40pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
					<#assign mccShedWiseDetailList = mccTypeShedMap.entrySet()>
		        	<fo:table-body>
				        <#list mccShedWiseDetailList as mccShedWiseList>
				        <#assign mccShedsList = mccShedWiseList.getValue()>
            				<#list mccShedsList as shed>		
		            			<fo:table-row>
		            				<#assign facility = delegator.findOne("Facility", {"facilityId" : shed}, true)>
			            			<fo:table-cell>
			            				<fo:block text-align="left" font-size="5pt" keep-together="always">${facility.facilityCode?if_exists}</fo:block>
			            			</fo:table-cell>
			            			<fo:table-cell>
			            				<fo:block text-align="left" font-size="5pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),15)}</fo:block>
			            			</fo:table-cell>
			            			<#list yearKeyList as yearKey>
			            				<#assign yearAnalysisList = shedTotValuesMap.get(yearKey)>
			            				<#assign shedQty = 0>
			            				<#assign shedQty = yearAnalysisList.get(shed)>
										<fo:table-cell>
				            				<fo:block text-align="right" font-size="5pt" keep-together="always">${shedQty?if_exists?string('##0.00')}</fo:block>
				            			</fo:table-cell>
			            			</#list>
					            	<fo:table-cell>
					            		<fo:block text-align="right" font-size="5pt" keep-together="always"></fo:block>
					            	</fo:table-cell>
						   	    	<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always"></fo:block>
						   	    	</fo:table-cell>
					   	    		<fo:table-cell>
										<fo:block text-align="right" font-size="5pt" keep-together="always"></fo:block>
									</fo:table-cell>
				   	    		</fo:table-row>
				   	    	</#list>
				   	    	<fo:table-row>
		   	    				<fo:table-cell>
									 <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
			   	    		</fo:table-row>
			   	    </fo:table-body>
				</fo:table>
		    </fo:block>
		    <fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="15pt"/>
				<fo:table-column column-width="50pt"/>
				<#list yearKeyList as yearKey>
				<fo:table-column column-width="40pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
		        	<fo:table-body>
				   	    <#assign mccKey= mccShedWiseList.getKey()>
				   	    	<fo:table-row>
			   	    			<fo:table-cell>
									<fo:block text-align="left" font-size="5pt" keep-together="always">${mccKey} TOTAL:</fo:block>
								</fo:table-cell>
		            			<fo:table-cell>
		            				<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
		            			</fo:table-cell>
		            			<#list yearKeyList as yearKey>
		            				<#assign yearAnalysisList = shedTotValuesMap.get(yearKey)>
		            				<#assign shedQtyVal = 0>
		            				<#assign shedQtyVal = yearAnalysisList.get(mccKey)>
									<fo:table-cell>
			            				<fo:block text-align="right" font-size="5pt" keep-together="always">${shedQtyVal?if_exists?string('##0.00')}</fo:block>
			            			</fo:table-cell>
		            			</#list>
		            			<fo:table-cell>
		            				<fo:block text-align="right" font-size="5pt" keep-together="always"></fo:block>
		            			</fo:table-cell>
				   	    	</fo:table-row>
					   	    <fo:table-row>
		   	    				<fo:table-cell>
									 <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
			   	    		</fo:table-row>				
				   	    </#list>
		        	</fo:table-body>
		    </fo:table> 
		</fo:block>
		 <fo:block  font-size="5pt">
			<fo:table>
				<fo:table-column column-width="15pt"/>
				<fo:table-column column-width="50pt"/>
				<#list yearKeyList as yearKey>
				<fo:table-column column-width="40pt"/>
				</#list>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
				<fo:table-column column-width="34pt"/>
		        	<fo:table-body>
		        		<fo:table-row>
		   	    			<fo:table-cell>
								<fo:block text-align="left" font-size="5pt" keep-together="always">GRAND TOTAL:</fo:block>
							</fo:table-cell>
	            			<fo:table-cell>
	            				<fo:block text-align="left" font-size="5pt" keep-together="always"></fo:block>
	            			</fo:table-cell>
	            			<#list yearKeyList as yearKey>
	            				<#assign yearAnalysisList = shedTotValuesMap.get(yearKey)>
	            				<#assign shedQtyVal = 0>
	            				<#assign shedQtyVal = yearAnalysisList.get("dayTotals")>
								<fo:table-cell>
		            				<fo:block text-align="right" font-size="5pt" keep-together="always">${shedQtyVal?if_exists?string('##0.00')}</fo:block>
		            			</fo:table-cell>
		            		</#list>
	           			 </fo:table-row>
	           			 <fo:table-row>
	   	    				<fo:table-cell>
								 <fo:block font-size="5pt">-------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
		   	    		</fo:table-row>					
		        	</fo:table-body>
		       </fo:table> 	
		   </fo:block>    		
    </fo:flow>
 </fo:page-sequence>
 	</#if>
	</#if>
	</fo:root>
</#escape>