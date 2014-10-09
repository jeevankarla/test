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
<#assign numberOfLines = 62>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
         <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ShedAvgRate.txt")}
        <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence> 
		<#else>
        <fo:page-sequence master-reference="main">
        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always" >&#160;                			    			${reportHeader.description?if_exists}.  </fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always">&#160;SHED WISE AVERAGE AMOUNT  FROM    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="7pt" keep-together="always">&#160;SHED NAME : ${facility.facilityName} 						(${rTypeFlag.toUpperCase()})</fo:block>
        		<fo:block font-size="8pt">-------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        <#assign opcost = 0>
       	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 	
    		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">
    			<fo:table>
    				<fo:table-column column-width="75pt"/>
    				<fo:table-column column-width="75pt" />
    				<fo:table-column column-width="75pt" />
    				<fo:table-column column-width="75pt" />
    				<fo:table-column column-width="75pt" />
    			<fo:table-header>
    				<fo:table-row>
    					<fo:table-cell><fo:block text-align="left"> PERIOD </fo:block></fo:table-cell>
    					<fo:table-cell><fo:block text-align="right"> GROSS AMOUNT </fo:block></fo:table-cell>
    					<fo:table-cell><fo:block text-align="right"> TIP AMOUNT</fo:block></fo:table-cell>
    					<fo:table-cell><fo:block text-align="right"> QTY KGS</fo:block></fo:table-cell>
    					<fo:table-cell><fo:block text-align="right"> AVG RATE</fo:block></fo:table-cell>
    				</fo:table-row>
    			</fo:table-header>
    			<fo:table-body>
    				<fo:table-row>
    					<fo:table-cell>
    						<fo:block font-size="8pt">-------------------------------------------------------------------------------------------</fo:block>
    					</fo:table-cell>
    				</fo:table-row>
    				<#if finalPeriodWiseMap?has_content>
    					<#assign periodValues = finalPeriodWiseMap.entrySet()>
    					<#list periodValues as periodValue>
    						<#assign periodKey = periodValue.getKey()>
    						<#if periodKey == "TOT">
    						<fo:table-row>
    							<fo:table-cell>
    								<fo:block font-size="8pt">-------------------------------------------------------------------------------------------</fo:block>
    							</fo:table-cell>
    						</fo:table-row>
    						</#if>
    						<#assign values = {}>
    						<#assign values = periodValue.getValue()>
    						<fo:table-row>
    							<fo:table-cell><fo:block text-align="left" keep-together="always"> ${periodKey} </fo:block></fo:table-cell>
		    					<fo:table-cell><fo:block text-align="right"> ${(values.grossAmt)?if_exists?string('##0.00')} </fo:block></fo:table-cell>
		    					<fo:table-cell><fo:block text-align="right"> ${(values.tipAmt)?if_exists?string('##0.00')} </fo:block></fo:table-cell>
		    					<fo:table-cell><fo:block text-align="right"> ${(values.qtyKgs)?if_exists?string('##0.00')} </fo:block></fo:table-cell>
		    					<fo:table-cell><fo:block text-align="right"> ${(values.avgRate)?if_exists?string('##0.00')} </fo:block></fo:table-cell>
    						</fo:table-row>
    						
    						<#if periodKey == "TOT">
    						<fo:table-row>
    							<fo:table-cell>
    								<fo:block font-size="8pt">-------------------------------------------------------------------------------------------</fo:block>
    							</fo:table-cell>
    						</fo:table-row>
    						</#if>
    					
    					 </#list>
    				</#if>
    				
    			</fo:table-body>
    			</fo:table>
    		</fo:block>   				
       			
        </fo:flow>		
      </fo:page-sequence>
	</#if>
</fo:root>
</#escape>