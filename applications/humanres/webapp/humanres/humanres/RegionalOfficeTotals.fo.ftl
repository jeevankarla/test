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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        margin-top="0.3in" margin-bottom="0.3in" margin-left=".5in" margin-right=".5in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    
        ${setRequestAttribute("OUTPUT_FILENAME", "BankAdviceStatement.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if finalRegionPaySheetMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
				    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				    <#assign nowDate=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, timeZone,locale)>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}                             </fo:block>
				    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" text-indent="50pt">
        			<#if parameters.billingTypeId=="SP_LEAVE_ENCASH">   
        			<#assign timePeriodEnd=basicSalDate?if_exists>
					</#if>                                                  																					Page No: <fo:page-number/></fo:block>	
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold" font-size = "12pt" font-family="Arial">Regional Wise Pay Sheet</fo:block>
          			<#--><fo:block text-align="left"  keep-together="always" font-family="Arial" white-space-collapse="false" font-size="8pt"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                 &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>-->
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
    	<fo:block>
    	<#assign benefitDescList = benefitDescMap.entrySet()>
    	<#assign dedDescList = dedDescMap.entrySet()>
    	<#assign columnKeys = columnMap.entrySet()>
    	
    	
		<fo:table width="100%" table-layout="fixed">
		    <fo:table-header height="14px">
		       	<fo:table-row height="14px" space-start=".15in" text-align="center">
                	<#--><fo:table-cell number-columns-spanned="1" border-style="solid" width="30px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">Sl.No</fo:block>
                    </fo:table-cell>-->
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="80px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">Region</fo:block>
                    </fo:table-cell>
                    <#list columnKeys as columnKey>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">${columnKey.getValue()}</fo:block>
                    </fo:table-cell>
                    </#list>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">Total Benefit</fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">Total Deduction</fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
                    	<fo:block text-align="center" font-weight="bold" font-size="10pt">Net Amt</fo:block>
                    </fo:table-cell>
                   <#--><#list dedDescList as dedEntry>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="36px">
                    	<fo:block text-align="center" font-weight="bold" font-size="6pt">${dedEntry.getValue()}</fo:block>
                    </fo:table-cell>
                    </#list>-->
                    
                </fo:table-row>
            </fo:table-header>
         <fo:table-body font-size="10pt">
         			<#assign slNo=1>
         			<#assign regionPaySheetMapKeys = finalRegionPaySheetMap.keySet()>
         			<#assign totalBenefits = 0>
         			<#assign totalDeductions = 0>
         			<#assign netAmt = 0>
         			<#assign totalNetAmt = 0>
         			
         			<#list regionPaySheetMapKeys as region>
         				<#--><fo:table-row>
		        			<fo:table-cell>
	                    		<fo:block text-align="center"></fo:block>
	                    	</fo:table-cell>
	                    	<fo:table-cell>
	                    		<fo:block text-align="center"></fo:block>
	                    	</fo:table-cell>
	                    	<fo:table-cell>
	                    		<fo:block text-align="center" keep-together="always">${region}</fo:block>
	                    	</fo:table-cell>
	                    </fo:table-row>-->	
	        			<#assign regionPaySheetEntry = finalRegionPaySheetMap.get(region)>
	        			<fo:table-row>
		        			<#--><fo:table-cell border-style="solid">
	                    		<fo:block text-align="center">${slNo}</fo:block>
	                    	</fo:table-cell>-->
	                    	<fo:table-cell border-style="solid">
	                    		<fo:block text-align="left" font-weight="bold">${region}</fo:block>
	                    	</fo:table-cell>
	                    	<#list columnKeys as columnKey>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="right" >${regionPaySheetEntry.get(columnKey.getKey())}</fo:block>
                    		</fo:table-cell>
                    		</#list>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="right" font-weight="bold">${regionPaySheetEntry.get("totalBenifit")}</fo:block>
                    		</fo:table-cell>
                    		<#assign totalBenefits = totalBenefits + regionPaySheetEntry.get("totalBenifit")>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="right" font-weight="bold">${regionPaySheetEntry.get("totalDeduction")}</fo:block>
                    		</fo:table-cell>
                    		<#assign totalDeductions = totalDeductions + regionPaySheetEntry.get("totalDeduction")>
                    		<#assign netAmt = regionPaySheetEntry.get("totalBenifit") - regionPaySheetEntry.get("totalDeduction")>
                    		<#assign totalNetAmt = totalNetAmt + netAmt>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="right" font-weight="bold">${netAmt}</fo:block>
                    		</fo:table-cell>
                    		<#--><#list dedDescList as dedEntry>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="center" >${regionPaySheetEntry.get(dedEntry.getKey())}</fo:block>
                    		</fo:table-cell>
                    		</#list>-->
	                    </fo:table-row>	
	                    <#assign slNo = slNo+1>
	        		</#list>
	        		<fo:table-row><fo:table-cell></fo:table-cell></fo:table-row>
	        		<fo:table-row border-style="solid">
	        			<#--><fo:table-cell>
                    		<fo:block text-align="center" font-weight="bold"></fo:block>
                    	</fo:table-cell>-->
                    	<fo:table-cell>
                    		<fo:block text-align="center" font-weight="bold">TOTAL</fo:block>
                    	</fo:table-cell>
                    	<#list columnKeys as columnKey>
                    		<fo:table-cell border-style="solid">
                    			<fo:block text-align="right" font-weight="bold">${sumMap.get(columnKey.getKey())}</fo:block>
                    		</fo:table-cell>
                		</#list>
                		<fo:table-cell border-style="solid">
                			<fo:block text-align="right" font-weight="bold">${totalBenefits}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell border-style="solid">
                			<fo:block text-align="right" font-weight="bold">${totalDeductions}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell border-style="solid">
                			<fo:block text-align="right" font-weight="bold">${totalNetAmt}</fo:block>
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
			        	${uiLabelMap.NoOrdersFound}.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>