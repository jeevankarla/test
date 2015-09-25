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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ChannelWiseProductWiseSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if prodCatWiseMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        	    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
	        	    
	        		<fo:block text-align="center" font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">${reportHeader.description?if_exists}.</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}.</fo:block>
                	<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> Channel Wise Product Wise Sales Report- ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
            		<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> SALES ${categoryType} </fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="140pt"/> 
		               	    <fo:table-column column-width="140pt"/>
		            		<fo:table-column column-width="140pt"/> 		
		            		<fo:table-column column-width="140pt"/>
		            		<fo:table-column column-width="140pt"/>
		            		<fo:table-column column-width="95pt"/>
		            		<fo:table-column column-width="90pt"/>
		                 <fo:table-body>
		                  <fo:table-row>
		                    <fo:table-cell>   						
							    <fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          				   </fo:table-cell>
          					</fo:table-row>
		                   <fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false" ></fo:block>  
	                       			</fo:table-cell>
	                       			 <#list roleTypeList as roleType>
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false" font-weight="bold">${roleType}</fo:block>  
	                       			</fo:table-cell>
	                       			</#list>
	                       			<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false" font-weight="bold">Qty</fo:block>  
	                       			</fo:table-cell>
	                       	</fo:table-row>
	                       	<fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false" ></fo:block>  
	                       			</fo:table-cell>
	                       			 <#list roleTypeList as roleType>
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false" font-weight="bold">(Qty/value)</fo:block>  
	                       			</fo:table-cell>
	                       			</#list>
	                       	</fo:table-row>
	                       	  <fo:table-row>
		                    <fo:table-cell>   						
							    <fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          				   </fo:table-cell>
          					</fo:table-row>
          					 <#assign totUnitQty=0> 
		                     <#assign totUnionQty=0> 
		                     <#assign totRetailerQty=0>
		                     <#assign totTradeQty=0>
		                     <#assign totQty=0>

		                     <#assign totUnitAmt=0> 
		                     <#assign totUnionAmt=0> 
		                     <#assign totRetailerAmt=0>
		                     <#assign totTradeAmt=0>
		                     <#assign totAmt=0>
		                     
          					 <#assign productCatDetails = prodCatWiseMap.entrySet()>
	                	     <#list productCatDetails as prodCat>
	                	      <#assign unitQty=0> 
		                      <#assign unionQty=0> 
		                      <#assign retailerQty=0>
		                      <#assign tradeQty=0>
		                      <#assign subQty=0>
		                      
		                      <#assign unitAmt=0> 
		                      <#assign unionAmt=0> 
		                      <#assign retailerAmt=0>
		                      <#assign tradeAmt=0>
		                      <#assign subAmt=0>
		                       <fo:table-row>
                    				<fo:table-cell>
	                            		<fo:block  text-align="left"   font-size="12pt" white-space-collapse="false" font-weight="bold">${prodCat.getKey()}</fo:block>  
	                       			</fo:table-cell>
	                       			  <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                       		</fo:table-row>
	                       		<#assign productDtls = prodCat.getValue().entrySet()>
	                       		<#list productDtls as prod>
	                       		  <#assign prodUnitQty=0> 
			                      <#assign prodUnionQty=0> 
			                      <#assign prodRetailerQty=0>
			                      <#assign prodTradeQty=0>
			                      
			                      <#assign prodUnitAmt=0> 
			                      <#assign prodUnionAmt=0> 
			                      <#assign prodRetailerAmt=0>
			                      <#assign prodTradeAmt=0>
	                       		  <#assign qty=0>
	                       		  <#assign amt=0>
	                       		 <#assign product=delegator.findOne("Product",{"productId":prod.getKey()},true)>
	                       		 <#if prod.getValue().get("UNITS")?has_content>
			                       			<#assign prodUnitQty=prod.getValue().get("UNITS").get("quantity")>
			                       		    <#assign unitQty=unitQty+prod.getValue().get("UNITS").get("quantity")> 
			                       		    <#assign prodUnitAmt=prod.getValue().get("UNITS").get("amount")> 
			                       		    <#assign unitAmt=unitAmt+prod.getValue().get("UNITS").get("amount")> 
		                       		   </#if>
		                       		  <#if prod.getValue().get("UNION")?has_content>
		                       		   <#assign prodUnionQty=prod.getValue().get("UNION").get("quantity")> 
	                       		       <#assign unionQty=unionQty+prod.getValue().get("UNION").get("quantity")> 
	                       		       <#assign prodUnionAmt=prod.getValue().get("UNION").get("amount")> 
			                       	   <#assign unionAmt=unionAmt+prod.getValue().get("UNION").get("amount")> 
	                       		     </#if>
	                       		     
	                       		     <#if prod.getValue().get("Retailer")?has_content>
	                       		       <#assign prodRetailerQty=prod.getValue().get("Retailer").get("quantity")> 
	                       		       <#assign retailerQty=retailerQty+prod.getValue().get("Retailer").get("quantity")> 
	                       		       <#assign prodRetailerAmt=prod.getValue().get("Retailer").get("amount")> 
			                       	   <#assign retailerAmt=retailerAmt+prod.getValue().get("Retailer").get("amount")> 
	                       		     </#if>
	                       		     
	                       		     <#if prod.getValue().get("TRADE_CUSTOMER")?has_content>
	                       		        <#assign prodTradeQty=prod.getValue().get("TRADE_CUSTOMER").get("quantity")> 
	                       		        <#assign tradeQty=tradeQty+prod.getValue().get("TRADE_CUSTOMER").get("quantity")>
	                       		        <#assign prodTradeAmt=prod.getValue().get("TRADE_CUSTOMER").get("amount")> 
	                       		        <#assign tradeAmt=tradeAmt+prod.getValue().get("TRADE_CUSTOMER").get("amount")>  
	                       		     </#if>
	                       		      <#assign  qty= qty+ prodTradeQty+prodRetailerQty+prodUnionQty+prodUnitQty>
	                       		      <#assign  totQty= totQty+qty>
	                       		      <#assign subQty=subQty+qty>
	                       		      <#assign  amt= amt+ prodTradeAmt+prodRetailerAmt+prodUnionAmt+prodUnitAmt>
	                       		      <#assign  totAmt= totAmt+amt>
	                       		      <#assign subAmt=subAmt+amt>
	                       		     
		                       		 <fo:table-row>
	                    				<fo:table-cell>
		                            		<fo:block  text-align="left"   font-size="12pt" white-space-collapse="false" >${product.brandName}</fo:block>  
		                       			</fo:table-cell>
		                        	  <#list roleTypeList as roleType>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"><#if prod.getValue().get(roleType)?has_content>${prod.getValue().get(roleType).get("quantity")?string("#0.00")}/${prod.getValue().get(roleType).get("amount")?string("#0.00")}</#if></fo:block>  
		                       			</fo:table-cell>
		                       			</#list>
		                       			<fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${qty?string("#0.00")}</fo:block>  
		                       			</fo:table-cell>
		                       		</fo:table-row>
	                       		 </#list>
	                       		<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
		                        <fo:table-row>
                    				<fo:table-cell >
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">subTotal</fo:block>  
	                       			</fo:table-cell>
	                       			<#list roleTypeList as roleType>
	                       			<#if roleType == "UNITS">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"><#if unitQty &gt; 0>${unitQty?string("#0.00")}/${unitAmt?string("#0.00")}</#if></fo:block>  
	                       			</fo:table-cell>
	                       			</#if>
	                       			<#if roleType == "UNION">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"><#if unionQty &gt; 0>${unionQty?string("#0.00")}/${unionAmt?string("#0.00")}</#if></fo:block>  
	                       			</fo:table-cell>
	                       			</#if>
	                       			<#if roleType == "Retailer">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"><#if retailerQty &gt; 0>${retailerQty?string("#0.00")}/${retailerAmt?string("#0.00")}</#if></fo:block>  
	                       			 </fo:table-cell>
	                       			 </#if>
	                       			 <#if roleType == "TRADE_CUSTOMER">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false"><#if tradeQty &gt; 0>${tradeQty?string("#0.00")}/${tradeAmt?string("#0.00")}</#if></fo:block>  
	                       			 </fo:table-cell>
	                       			  </#if>
	                       			</#list>
	                       			  <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${subQty}</fo:block>  
	                       			 </fo:table-cell>
	                       		</fo:table-row>
	                       		  <#assign totUnitQty=totUnitQty+unitQty> 
	                       		  <#assign totUnionQty=totUnionQty+unionQty> 
	                       		  <#assign totRetailerQty=totRetailerQty+retailerQty> 
	                       		  <#assign totTradeQty=totTradeQty+tradeQty> 
	                       		 <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>  
		                    </#list>
		                        <fo:table-row>
                    				<fo:table-cell >
	                            		<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">Total</fo:block>  
	                       			</fo:table-cell>
	                       			<#list roleTypeList as roleType>
	                       			<#if roleType == "UNITS">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${totUnitQty}</fo:block>  
	                       			</fo:table-cell>
	                       			</#if>
	                       			<#if roleType == "UNION">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${totUnionQty}</fo:block>  
	                       			</fo:table-cell>
	                       			 </#if>
	                       			<#if roleType == "Retailer">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${totRetailerQty}</fo:block>  
	                       			 </fo:table-cell>
	                       			 </#if>
	                       			<#if roleType == "TRADE_CUSTOMER">
	                       			 <fo:table-cell>
	                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${totTradeQty}</fo:block>  
	                       			 </fo:table-cell>
	                       			  </#if>
	                       			  </#list>
	                       			 <fo:table-cell>
		                            		<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${totQty}</fo:block>  
		                       			</fo:table-cell>
	                       		</fo:table-row>
	                       		 <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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