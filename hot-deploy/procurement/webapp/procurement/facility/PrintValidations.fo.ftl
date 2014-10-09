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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-top=".3in"  margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>
     <#if qtySnfFinalList?has_content>   
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold" text-align="center">VALIDATION ENTRIES  FOR THE PERIOD : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}                       PAGE NO:<fo:page-number/></fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block>
        			<fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="20%"/>	
        				<fo:table-column column-width="15%"/>				
        				<fo:table-header>
        					<fo:table-row border-style="solid">
        						<fo:table-cell>
        							<fo:block text-align="center">SHED</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">UNIT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">CENTER</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">PROC DATE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">TIME</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">MILK</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">QTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">FAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SNF</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SFAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">CQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">PTCQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">STATUS ID</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">VALIDATION TYPE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">Approved By</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-header>
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block></fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">        	
        		<#list qtySnfFinalList as qtySnfFinalValues>        		
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="20%"/>	
        				<fo:table-column column-width="15%"/>
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : shedId}, true)>
        							<fo:block text-align="center">${facilityDetails.get("facilityCode")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign unitDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", qtySnfFinalValues.get("centerId"))).get("unitFacility")>
        							<fo:block text-align="center">${unitDetails.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${qtySnfFinalValues.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(qtySnfFinalValues.get("estimatedDeliveryDate"), "dd-MM-yyyy")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">${qtySnfFinalValues.get("supplyTypeEnumId")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign product = delegator.findOne("Product", {"productId" : qtySnfFinalValues.get("productId")}, true)>
        							<fo:block text-align="center">${product.brandName?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${qtySnfFinalValues.get("quantityKgs")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${qtySnfFinalValues.get("fat")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<fo:block text-align="right">${qtySnfFinalValues.get("snf")?if_exists?string("##0.00")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if qtySnfFinalValues.get("sQuantityLtrs")?has_content>
        							<fo:block text-align="right">${qtySnfFinalValues.get("sQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${qtySnfFinalValues.get("sQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell >
        						<#if qtySnfFinalValues.get("sFat")?has_content>
        							<fo:block text-align="right">${qtySnfFinalValues.get("sFat")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${qtySnfFinalValues.get("sFat")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if qtySnfFinalValues.get("cQuantityLtrs")?has_content>
        							<fo:block text-align="right">${qtySnfFinalValues.get("cQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${qtySnfFinalValues.get("cQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if qtySnfFinalValues.get("ptcQuantity")?has_content>
        							<fo:block text-align="right">${qtySnfFinalValues.get("ptcQuantity")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${qtySnfFinalValues.get("ptcQuantity")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign statusDetails = delegator.findOne("StatusItem", {"statusId" : qtySnfFinalValues.get("statusId")}, true)>
        							<fo:block text-align="center">${statusDetails.get("description")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign validationType = delegator.findOne("Enumeration", {"enumId" : qtySnfFinalValues.get("validationTypeId")}, true)>
        							<fo:block text-align="left" keep-together="always">${validationType.description?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center" keep-together="always">${qtySnfFinalValues.get("approvedByUserLoginId")?if_exists}</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        		</#list> 
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="16%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        					<fo:table-row>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell>
        							<fo:block text-align="left" keep-together="always">Verified By</fo:block>
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
		            	No Quantity Snf Exception records found.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		<#if checkCodeFinalList?has_content>   
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold" text-align="center">VALIDATION ENTRIES  FOR THE PERIOD : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block>
        			<fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="7%"/> 
        				<fo:table-column column-width="13%"/>
        				<fo:table-column column-width="13%"/>				
        				<fo:table-header>
        					<fo:table-row border-style="solid">
        						<fo:table-cell>
        							<fo:block text-align="center">SHED</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">UNIT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">CENTER</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">PROC DATE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">TIME</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">MILK</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">QTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">FAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SNF</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SFAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">CQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">PTCQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">STATUS ID</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">VALIDATION TYPE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">Approved By</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-header>
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block></fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">        	
        		<#list checkCodeFinalList as checkCode>        		
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="13%"/>
        				<fo:table-column column-width="13%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : shedId}, true)>
        							<fo:block text-align="center">${facilityDetails.get("facilityCode")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign unitDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", checkCode.get("centerId"))).get("unitFacility")>
        							<fo:block text-align="center">${unitDetails.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${checkCode.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(checkCode.get("estimatedDeliveryDate"), "dd-MM-yyyy")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">${checkCode.get("supplyTypeEnumId")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign product = delegator.findOne("Product", {"productId" : checkCode.get("productId")}, true)>
        							<fo:block text-align="center">${product.brandName?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${checkCode.get("quantityKgs")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${checkCode.get("fat")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<fo:block text-align="right">${checkCode.get("snf")?if_exists?string("##0.00")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if checkCode.get("sQuantityLtrs")?has_content>
        							<fo:block text-align="right">${checkCode.get("sQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${checkCode.get("sQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell >
        						<#if checkCode.get("sFat")?has_content>
        							<fo:block text-align="right">${checkCode.get("sFat")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${checkCode.get("sFat")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if checkCode.get("cQuantityLtrs")?has_content>
        							<fo:block text-align="right">${checkCode.get("cQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${checkCode.get("cQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if checkCode.get("ptcQuantity")?has_content>
        							<fo:block text-align="right">${checkCode.get("ptcQuantity")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${checkCode.get("ptcQuantity")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign statusDetails = delegator.findOne("StatusItem", {"statusId" : checkCode.get("statusId")}, true)>
        							<fo:block text-align="center">${statusDetails.get("description")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign validationType = delegator.findOne("Enumeration", {"enumId" : checkCode.get("validationTypeId")}, true)>
        							<fo:block text-align="left" keep-together="always">${validationType.description?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center" keep-together="always">${checkCode.get("approvedByUserLoginId")?if_exists}</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        		</#list>
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="6%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="13%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        					<fo:table-row>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell>
        							<fo:block text-align="left" keep-together="always">Verified By</fo:block>
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
		            	No Check Code Exception records Found.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if> 	
       <#if negativeAmtList?has_content>   
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold" text-align="left" text-indent="15pt">VALIDATION ENTRIES  FOR THE PERIOD : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block>
        			<fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="13%"/>        				
        				<fo:table-column column-width="15%"/>	
        				<fo:table-column column-width="15%"/>				
        				<fo:table-header>
        					<fo:table-row border-style="solid">
        						<fo:table-cell>
        							<fo:block text-align="center">SHED</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">UNIT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">CENTER</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">STATUS ID</fo:block>
        						</fo:table-cell>        						
        						<fo:table-cell border-style="solid">
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">TIME PERIOD</fo:block>
        						</fo:table-cell>        						
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">VALIDATION TYPE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">Approved By</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-header>
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block></fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">        	
        		<#list negativeAmtList as negativeAmtValue>        		
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="13%"/> 				
        				<fo:table-column column-width="15%"/>  	
        				<fo:table-column column-width="15%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : shedId}, true)>
        							<fo:block text-align="center">${facilityDetails.get("facilityCode")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign centerDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", negativeAmtValue.get("centerId")))>
        							<fo:block text-align="center">${centerDetails.get("unitFacility").get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<#assign facility = delegator.findOne("Facility", {"facilityId" : negativeAmtValue.get("centerId")}, true)>
        						<fo:table-cell>
        							<fo:block text-align="right">${facility.facilityCode?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign statusDetails = delegator.findOne("StatusItem", {"statusId" : negativeAmtValue.get("statusId")}, true)>
        							<fo:block text-align="center">${statusDetails.get("description")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign timePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : negativeAmtValue.get("customTimePeriodId")}, true)>
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">${timePeriod.periodName?if_exists}</fo:block>
        						</fo:table-cell>        						
        						<fo:table-cell>
        							<#assign validationType = delegator.findOne("Enumeration", {"enumId" : negativeAmtValue.get("validationTypeId")}, true)>
        							<fo:block text-align="left" keep-together="always">${validationType.description?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center" keep-together="always">${negativeAmtValue.get("approvedByUserLoginId")?if_exists}</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        		</#list>
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="13%"/> 				
        				<fo:table-column column-width="15%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        					<fo:table-row>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell>
        							<fo:block text-align="left" keep-together="always">Verified By</fo:block>
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
		            	No Negative Amount Exception records found.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if> 	
      	<#if outLierFinalList?has_content>   
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block keep-together="always" white-space-collapse="false" font-weight="bold" text-align="center">VALIDATION ENTRIES  FOR THE PERIOD : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block>
        			<fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>  
        				<fo:table-column column-width="11%"/>		
        				<fo:table-column column-width="11%"/>				
        				<fo:table-header>
        					<fo:table-row border-style="solid">
        						<fo:table-cell>
        							<fo:block text-align="center">SHED</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">UNIT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">CENTER</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">PROC DATE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">TIME</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">MILK</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">QTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">FAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SNF</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">SFAT</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">CQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">PTCQTY</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center">STATUS ID</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">VALIDATION TYPE</fo:block>
        						</fo:table-cell>
        						<fo:table-cell border-style="solid">
        							<fo:block text-align="center" keep-together="always" white-space-collapse="false">Approved By</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-header>
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block></fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        	</fo:static-content>        	  
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">        	
        		<#list outLierFinalList as outLierFinalValues>        		
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="11%"/>  			
        				<fo:table-column column-width="11%"/>	
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : shedId}, true)>
        							<fo:block text-align="center">${facilityDetails.get("facilityCode")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign unitDetails = Static["in.vasista.vbiz.procurement.ProcurementNetworkServices"].getCenterDtails(dctx ,Static["org.ofbiz.base.util.UtilMisc"].toMap("centerId", outLierFinalValues.get("centerId"))).get("unitFacility")>
        							<fo:block text-align="center">${unitDetails.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${outLierFinalValues.get("facilityCode")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block keep-together="always" white-space-collapse="false" text-align="center">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(outLierFinalValues.get("estimatedDeliveryDate"), "dd-MM-yyyy")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center">${outLierFinalValues.get("supplyTypeEnumId")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<#assign product = delegator.findOne("Product", {"productId" : outLierFinalValues.get("productId")}, true)>
        							<fo:block text-align="center">${product.brandName?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${outLierFinalValues.get("quantityKgs")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="right">${outLierFinalValues.get("fat")?if_exists?string("##0.0")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell >
        							<fo:block text-align="right">${outLierFinalValues.get("snf")?if_exists?string("##0.00")}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if outLierFinalValues.get("sQuantityLtrs")?has_content>
        							<fo:block text-align="right">${outLierFinalValues.get("sQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        						 	<fo:block text-align="right">${outLierFinalValues.get("sQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell >
        						<#if outLierFinalValues.get("sFat")?has_content>
        							<fo:block text-align="right">${outLierFinalValues.get("sFat")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${outLierFinalValues.get("sFat")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if outLierFinalValues.get("cQuantityLtrs")?has_content>
        							<fo:block text-align="right">${outLierFinalValues.get("cQuantityLtrs")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${outLierFinalValues.get("cQuantityLtrs")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        						<#if outLierFinalValues.get("ptcQuantity")?has_content>
        							<fo:block text-align="right">${outLierFinalValues.get("ptcQuantity")?if_exists?string("##0.0")}</fo:block>
        							<#else>
        							<fo:block text-align="right">${outLierFinalValues.get("ptcQuantity")?if_exists}</fo:block>
        						</#if>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign statusDetails = delegator.findOne("StatusItem", {"statusId" : outLierFinalValues.get("statusId")}, true)>
        							<fo:block text-align="center">${statusDetails.get("description")?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<#assign validationType = delegator.findOne("Enumeration", {"enumId" : outLierFinalValues.get("validationTypeId")}, true)>
        							<fo:block text-align="left" keep-together="always">${validationType.description?if_exists}</fo:block>
        						</fo:table-cell>
        						<fo:table-cell>
        							<fo:block text-align="center" keep-together="always">${outLierFinalValues.get("approvedByUserLoginId")?if_exists}</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        				</fo:table-body>
        			</fo:table>
        		</fo:block>
        		</#list>  
        		<fo:block>  
        		  <fo:table width="100%">
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="10%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="4%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="7%"/>
        				<fo:table-column column-width="11%"/>  				
        				<fo:table-body>
        					<fo:table-row>
        						<fo:table-cell>
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        						</fo:table-cell>
        					</fo:table-row>
        					<fo:table-row>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell/>
        						<fo:table-cell>
        							<fo:block text-align="left" keep-together="always">Verified By</fo:block>
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
		            	No Outlier Exception records Found.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</#if>
     </fo:root>
</#escape>