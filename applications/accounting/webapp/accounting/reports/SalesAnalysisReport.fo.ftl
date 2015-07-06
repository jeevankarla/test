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

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="1in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "SalesAnalysisReport.pdf")}
 <#if glAccountMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" font-size="10pt" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                        MOTHER DAIRY, YALAHANKA KMF UNIT : GKVK POST.BANGALORE-560 065                        Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold"  font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
               <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SALES ANALYSIS FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
				<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
               <fo:block>
               <#assign glAccounts=glAccountMap.entrySet()>
                    <fo:table>
				    <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="25%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">GL ACCOUNT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">SALES SCHEDULE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">AMOUNT</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                				<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                        <#list glAccounts as glAccount>
                		<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center"   font-size="11pt" white-space-collapse="false">${glAccount.getKey()?if_exists}</fo:block>  
                			</fo:table-cell>
                			<#assign glAccnt=delegator.findOne("GlAccount",{"glAccountId",glAccount.getKey()},true)>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"   font-size="11pt" white-space-collapse="false">${glAccnt.description?if_exists}</fo:block>  
                			</fo:table-cell>
                           <#assign value=0>
                           <#if glAccount.getValue()?has_content>
                           <#assign value=glAccount.getValue()>
                           </#if>
                           <#if value gte 0>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="11pt" white-space-collapse="false">${value?if_exists?string("##0.00")} (Dr)</fo:block>  
                			</fo:table-cell>
                          <#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="11pt" white-space-collapse="false">${((-1)*value)?if_exists?string("##0.00")} (Cr)</fo:block>  
                			</fo:table-cell>
                          </#if>
                		</fo:table-row>
                		</#list>
                        <fo:table-row>
                			<fo:table-cell>
                				<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
               <#if channelWiseMap?has_content>
               <fo:block page-break-before="always" text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SALES ANALYSIS CHANNEL WISE FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
				<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	               <fo:block>
	                 <fo:table>
			        	<fo:table-column column-width="12%"/>
			        	<fo:table-column column-width="10%"/>
			        	<#list roleTypeList as roleType>
			        	<fo:table-column column-width="10%"/>
			        	</#list>
                         <fo:table-column column-width="10%"/>

                    	<fo:table-body>
                    	<#assign channelWise=channelWiseMap.entrySet()>
							<#list channelWise as channel>
                            <#assign glAccnt=delegator.findOne("GlAccount",{"glAccountId",channel.getKey()},true)>
                            <#assign grandTotal=0>
                        <#assign grandWholeSaleValue=0>
                        <#assign grandUnionSaleValue=0>
                        <#assign grandUnitSaleValue=0>
                        <#assign grandEmplSaleValue=0>  
                        <#assign grandDepoSaleValue=0>
                        <#assign grandAmulSaleValue=0>
                        <#assign grandOtherSaleValue=0>
                    		<fo:table-row>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>      
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${glAccnt.description?if_exists}[${channel.getKey()}]</fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>	
                    	   	<fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>	
                			<fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">CATEGORY / PRODUCT</fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">AMOUNT</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#list roleTypeList as roleType>
                                 <#assign roleTypeName = delegator.findOne("RoleType",{"roleTypeId":roleType},true)>
                    	   		<fo:table-cell>
                    	   			<fo:block   text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${(roleTypeName.description)?upper_case?if_exists}</fo:block> 
                    	   		</fo:table-cell> 
                    	   		</#list>
                                <fo:table-cell>
                    	   			<fo:block   text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">OTHERS</fo:block> 
                    	   		</fo:table-cell> 
                    	   	</fo:table-row>	
                    	   	<fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>	
                                 <#if channel.getValue()?has_content>
                                <#assign productCategory=channel.getValue().entrySet()>
                             <#--  <#list categoryWise as category>
                                <#assign productCat=delegator.findOne("ProductCategory",{"productCategoryId":category.getKey()},true)>
                                <#assign productCategory=category.getValue().entrySet()>
                            <fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="9pt" white-space-collapse="false">${productCat.description?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>  -->
                			<#list productCategory as newCategory>
                             <#assign productNewCat=delegator.findOne("ProductCategory",{"productCategoryId":newCategory.getKey()},true)>
                             <#assign products =newCategory.getValue().entrySet()> 
                             <#assign totValue=0>
                			<#assign wholeSaleValue=0>
                            <#assign unionSaleValue=0>
                            <#assign unitSaleValue=0>
                            <#assign emplSaleValue=0>  
                            <#assign depoSaleValue=0>
                            <#assign amulSaleValue=0>
                            <#assign otherSaleValue=0>
                			<fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="9pt" white-space-collapse="false">${productNewCat.description?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                			    <#list products as product>
                                <#assign prod=delegator.findOne("Product",{"productId":product.getKey()},true)>
                                 <#if product.getValue() !=0> 
                                 <#if productMap?has_content>
                                 <#assign saleTypeValues = productMap.get(product.getKey())>
                                 </#if>
                            <fo:table-row>
                    	   		<fo:table-cell>
                    	   			<fo:block   text-align="left"   font-size="10pt" white-space-collapse="false">${prod.productName?if_exists}</fo:block> 
                    	   		</fo:table-cell>
                                <#assign prodValue=0>
                                <#if product.getValue()?has_content>
                                  <#assign prodValue=product.getValue()> 
                                </#if>   
                                <#assign totValue=totValue+prodValue>
                                <#if prodValue gte 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${prodValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#else>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${((-1)*prodValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                                
                                <#list roleTypeList as roleType>
                                 <#assign saleValue=0>
                                 <#if saleTypeValues.get(roleType)?has_content >
                                  <#assign saleValue=saleTypeValues.get(roleType)> 
                                <#if roleType =="IC_WHOLESALE">
									<#assign wholeSaleValue=wholeSaleValue+saleValue>
                                 <#elseif roleType == "UNION">
		                             <#assign unionSaleValue=unionSaleValue+saleValue>
								 <#elseif roleType == "UNITS">
                                      <#assign unitSaleValue=unitSaleValue+saleValue>
								 <#elseif roleType == "EXCLUSIVE_CUSTOMER">
                                      <#assign amulSaleValue=amulSaleValue+saleValue>
								 <#elseif roleType == "DEPOT_CUSTOMER">
                                        <#assign depoSaleValue=depoSaleValue+saleValue> 
								 <#elseif roleType == "EMPLOYEE">
                                      <#assign emplSaleValue=emplSaleValue+saleValue>
                                 </#if> 
                                 </#if>
                                <#if saleValue gt 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${saleValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#elseif saleValue lt 0>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${((-1)*saleValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                <#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                    	   		</#list>
                                <#if saleTypeValues.get("OTHER")?has_content>
                                <#assign  otherValue =saleTypeValues.get("OTHER")> 
                                   <#assign otherSaleValue=otherSaleValue+otherValue>
                                <#if otherValue gt 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${otherValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#elseif otherValue lt 0>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${((-1)*otherValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                    	   		<#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                    	   	</fo:table-row>
                    	   	    </#if>
                                </#list>
                               <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			    </fo:table-row>
                              <#--  </#list> -->
                            <fo:table-row>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">TOTAL   :</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#assign grandTotal=grandTotal+totValue>
                    	   		<#if totValue gte 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${totValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                                <#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${((-1)*totValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                 </#if>
                                <#list roleTypeList as roleType>
                                 <#assign totalValue=0>
                        
                                <#if roleType =="IC_WHOLESALE">
									<#assign totalValue=wholeSaleValue>
                                     <#assign grandWholeSaleValue=grandWholeSaleValue+totalValue>
                                 <#elseif roleType == "UNION">
		                             <#assign totalValue=unionSaleValue>
                                     <#assign grandUnionSaleValue=grandUnionSaleValue+totalValue>  
								 <#elseif roleType == "UNITS">
                                      <#assign totalValue=unitSaleValue>
                                      <#assign grandUnitSaleValue=grandUnitSaleValue+totalValue>
								 <#elseif roleType == "EXCLUSIVE_CUSTOMER">
                                      <#assign totalValue=amulSaleValue>
                                      <#assign grandAmulSaleValue=grandAmulSaleValue+totalValue>
								 <#elseif roleType == "DEPOT_CUSTOMER">
                                        <#assign totalValue=depoSaleValue> 
                                        <#assign grandDepoSaleValue=grandDepoSaleValue+totalValue>
								 <#elseif roleType == "EMPLOYEE">
                                      <#assign totalValue=emplSaleValue>
                                      <#assign grandEmplSaleValue=grandEmplSaleValue+totalValue>
                                 </#if>  
                                 
                                <#if totalValue gt 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${totalValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#elseif totalValue lt 0>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"  font-weight="bold" font-size="10pt" white-space-collapse="false">${((-1)*totalValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                <#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                                </#if>
                    	   		</#list>
                               <#if otherSaleValue?has_content>
                                  <#assign grandOtherSaleValue=grandOtherSaleValue+otherSaleValue>
                                  <#if otherSaleValue gt 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${otherSaleValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#elseif otherSaleValue lt 0>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"  font-weight="bold" font-size="10pt" white-space-collapse="false">${((-1)*otherSaleValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#else> 
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		</#if>
                               <#else> 
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                               </#if> 
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                    	   	    </#list>
                                </#if>
                              <fo:table-row>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">GRAND TOTAL   :</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#if grandTotal gte 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grandTotal?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                                <#else>
                                 <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${((-1)*grandTotal)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                                 </#if>
                                <#list roleTypeList as roleType>
                                 <#assign grandTotalValue=0>
                                 <#if roleType =="IC_WHOLESALE">
                                     <#assign grandTotalValue=grandWholeSaleValue>
                                 <#elseif roleType == "UNION">
                                     <#assign grandTotalValue=grandUnionSaleValue>  
								 <#elseif roleType == "UNITS">
                                      <#assign grandTotalValue=grandUnitSaleValue>
								 <#elseif roleType == "EXCLUSIVE_CUSTOMER">
                                      <#assign grandTotalValue=grandAmulSaleValue>
								 <#elseif roleType == "DEPOT_CUSTOMER">
                                        <#assign grandTotalValue=grandDepoSaleValue>
								 <#elseif roleType == "EMPLOYEE">
                                      <#assign grandTotalValue=grandEmplSaleValue>
                                 </#if>  
                                  <#if grandTotalValue gt 0>
	                    	   		<fo:table-cell>
	                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grandTotalValue?if_exists?string("##0.00")} (Dr)</fo:block> 
	                    	   		</fo:table-cell>
	                                <#elseif grandTotalValue lt 0>
	                                 <fo:table-cell>
	                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${((-1)*grandTotalValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
	                    	   		</fo:table-cell>
	                    	   		<#else>
                                    <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		   </fo:table-cell>
                                 </#if>
                                 </#list> 
                                 <#if grandOtherSaleValue?has_content>
                                  <#if grandOtherSaleValue gt 0>
                    	   		<fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grandOtherSaleValue?if_exists?string("##0.00")} (Dr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#elseif grandOtherSaleValue lt 0>
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right"  font-weight="bold" font-size="10pt" white-space-collapse="false">${((-1)*grandOtherSaleValue)?if_exists?string("##0.00")} (Cr)</fo:block> 
                    	   		</fo:table-cell>
                    	   		<#else> 
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                    	   		</#if>
                               <#else> 
                                <fo:table-cell>
                    	   			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block> 
                    	   		</fo:table-cell>
                               </#if>
                    	   	</fo:table-row>
                            <fo:table-row>
                				<fo:table-cell>
                					<fo:block font-size="10pt">-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                				</fo:table-cell>
                			</fo:table-row>
                                </#list> 
                    	</fo:table-body>   	
	                 </fo:table>
	               </fo:block>
               </#if>
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