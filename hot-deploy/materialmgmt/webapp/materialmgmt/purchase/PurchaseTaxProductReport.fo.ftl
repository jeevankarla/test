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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="11in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="2.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "purchaseTaxReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if taxDetails5pt5List?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        <fo:static-content font-size="12pt" flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">VAT Classification Vouchers </fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">VAT INPUT 5.5% </fo:block>
          			<fo:block text-align="center"   keep-together="always"  white-space-collapse="false"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always" >ACCOUNT CODE:${invItemTypeGl.defaultGlAccountId?if_exists} </fo:block>
          			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt" >VOUCHER DATE</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block   text-align="left" font-size="11pt"  >PRODUCT NAME</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >VOUCHER </fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"   > TYPE</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >PDB</fo:block> 
							            	<fo:block   text-align="left" font-size="11pt"   >RefNo</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell> 
							            	<fo:block  text-align="left" font-size="10pt"  >CR/DB</fo:block>  
							            		<fo:block  text-align="center" font-size="10pt"  >ID</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >ASSESSABLE</fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"  >VALUE</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >VAT AMOUNT</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 	
        			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
               </fo:static-content>
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
        			<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                    <#-- catageory wise starts here -->
		                    <#if tax5pt5CatMap?has_content>
		                       <#assign tax5pt5CatList=tax5pt5CatMap.entrySet()>
          						  <#list tax5pt5CatList as tax5pt5Cat>
          						  <#if tax5pt5Cat.getKey()!="discount">
          						   <#assign codeId=tax5pt5Cat.getKey()>
          						   <#assign codeIdMap=tax5pt5Cat.getValue()>
          						    <#assign codeIdList=codeIdMap.get("invoiceList")>
          						    
          						    <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : codeId}, true)?if_exists/>
		   							<fo:table-row>
		   							       <fo:table-cell number-columns-spanned="6">
								            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Analysis Code :${productCategory.description}</fo:block>  
								            </fo:table-cell>
								    </fo:table-row> 
          						   <#list codeIdList as invTaxMap>
          						   <fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							            <#-->
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if> -->
					            			 <#assign  productName="">
					            			<#if invTaxMap.get("productId")?exists>
					            			<#assign productId=invTaxMap.get("productId")>
					            			<#assign product = delegator.findOne("Product", {"productId" : productId}, true)?if_exists/>
					            			<#assign productName =product.description>
					            			</#if>
					                    <fo:table-cell number-columns-spanned="2">
					                    <fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productName?if_exists)),35)}</fo:block>
							            </fo:table-cell>
							            
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						    </#list>
          						    <fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                   <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" font-size="11pt">Analysis Code-Total</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("totalValue")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						   </#if>
          					  </#list>
          					  <fo:table-row>
					                   <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" >TOTAL-Discount For All Analysis Codes</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax5pt5CatMap.get("discount")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          					  <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
          					</#if>
          					 <#-- catageory wise ends here -->
          					 <#-->
		                    <#assign totalRevenue=0>
		                    <#assign totalTaxRevenue=0>
       							<#list taxDetails5pt5List as invTaxMapObj>
       							<#assign invTaxMap= invTaxMapObj.getValue()>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="12pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),28)}</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("tinNumber")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								</#list>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> -->
								<fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="13pt">TOTAL</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax5pt5TotalMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax5pt5TotalMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 		
				</fo:flow>
			</fo:page-sequence>
		    </#if>   
			<#if taxDetails14pt5List?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        <fo:static-content font-size="12pt" flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">VAT Classification Vouchers </fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">VAT INPUT 14.5% </fo:block>
          			<fo:block text-align="center"   keep-together="always"  white-space-collapse="false"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always" >ACCOUNT CODE:${invItemTypeGl.defaultGlAccountId?if_exists} </fo:block>
          			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt" >VOUCHER DATE</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block   text-align="left" font-size="11pt"  >PRODUCT NAME</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >VOUCHER </fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"   > TYPE</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >PDB</fo:block> 
							            	<fo:block   text-align="left" font-size="11pt"   >RefNo</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell> 
							            	<fo:block  text-align="left" font-size="10pt"  >CR/DB</fo:block>  
							            		<fo:block  text-align="center" font-size="10pt"  >ID</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >ASSESSABLE</fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"  >VALUE</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >VAT AMOUNT</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 	
        			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
               </fo:static-content>
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
        			<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                    	   <#-- catageory wise starts here -->
		                    <#if tax14pt5CatMap?has_content>
		                       <#assign tax14pt5CatList=tax14pt5CatMap.entrySet()>
		                    	  
          						  <#list tax14pt5CatList as tax14pt5Cat>
          						  <#if tax14pt5Cat.getKey()!="discount">
          						   <#assign codeId=tax14pt5Cat.getKey()>
          						   <#assign codeIdMap=tax14pt5Cat.getValue()>
          						    <#assign codeIdList=codeIdMap.get("invoiceList")>
          						    
          						    <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : codeId}, true)?if_exists/>
		   							  <fo:table-row> 
		   							 <fo:table-cell number-columns-spanned="6">
								            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Analysis Code :${productCategory.description}</fo:block>  
								      </fo:table-cell>
								      </fo:table-row>
          						   <#list codeIdList as invTaxMap>
          						   <fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							             <#--
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if> -->
					            			 <#assign  productName="">
					            			<#if invTaxMap.get("productId")?exists>
					            			<#assign productId=invTaxMap.get("productId")>
					            			<#assign product = delegator.findOne("Product", {"productId" : productId}, true)?if_exists/>
					            			<#assign productName =product.description>
					            			</#if>
					                    <fo:table-cell  number-columns-spanned="2">
					                    <fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productName?if_exists)),35)}</fo:block>
							            </fo:table-cell>
							            
							             
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						    </#list>
          						    <fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" font-size="11pt">Analysis Code-Total</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("totalValue")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						   </#if>
          					  </#list>
          					  <fo:table-row>
					                   <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" >TOTAL-Discount For All Analysis Codes</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax14pt5CatMap.get("discount")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          					  <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
          					</#if>
          					 <#-- catageory wise ends here -->
          					 <#-->
		                    <#assign totalRevenue=0>
		                    <#assign totalTaxRevenue=0>
       							<#list taxDetails14pt5List as invTaxMapObj>
       							<#assign invTaxMap= invTaxMapObj.getValue()>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="12pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),28)}</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("tinNumber")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								</#list>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> -->
								<fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="13pt">TOTAL</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax14pt5TotalMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${tax14pt5TotalMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 		
				</fo:flow>
			</fo:page-sequence>
			</#if>
			<#-- CST Tax details listed here-->
			<#if taxDetailsCstList?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        <fo:static-content font-size="12pt" flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">TAX Classification Vouchers </fo:block>
          			<fo:block text-align="center"    keep-together="always"  white-space-collapse="false">CST</fo:block>
          			<fo:block text-align="center"   keep-together="always"  white-space-collapse="false"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always" >ACCOUNT CODE:${invItemCstTypeGl.defaultGlAccountId?if_exists} </fo:block>
          			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt" >VOUCHER DATE</fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block   text-align="left" font-size="11pt"  >PRODUCT NAME</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >VOUCHER </fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"   > TYPE</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"   >PDB</fo:block> 
							            	<fo:block   text-align="left" font-size="11pt"   >RefNo</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell> 
							            	<fo:block  text-align="left" font-size="10pt"  >CR/DB</fo:block>  
							            		<fo:block  text-align="center" font-size="10pt"  >ID</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >ASSESSABLE</fo:block>  
							            	<fo:block   text-align="left" font-size="11pt"  >VALUE</fo:block> 
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt"  >CST AMOUNT</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 	
        			<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
               </fo:static-content>
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
        			<fo:block>
        				<fo:table>
        				<fo:table-column column-width="82pt"/>
        				 <fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="82pt"/>                
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="93pt"/> 
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                       <#-- catageory wise starts here -->
		                    <#if taxCstCatMap?has_content>
		                       <#assign taxCstCatList=taxCstCatMap.entrySet()>
          						  <#list taxCstCatList as taxCstCat>
          						  <#if taxCstCat.getKey()!="discount">
          						   <#assign codeId=taxCstCat.getKey()>
          						   <#assign codeIdMap=taxCstCat.getValue()>
          						    <#assign codeIdList=codeIdMap.get("invoiceList")>
          						    
          						    <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : codeId}, true)?if_exists/>
		   							  <fo:table-row> 
		   							 <fo:table-cell number-columns-spanned="6">
								            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Analysis Code :${productCategory.description}</fo:block>  
								      </fo:table-cell>
								      </fo:table-row>
          						   <#list codeIdList as invTaxMap>
          						   <fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							            <#-->
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if> -->
					            			 <#assign  productName="">
					            			<#if invTaxMap.get("productId")?exists>
					            			<#assign productId=invTaxMap.get("productId")>
					            			<#assign product = delegator.findOne("Product", {"productId" : productId}, true)?if_exists/>
					            			<#assign productName =product.description>
					            			</#if>
					                    <fo:table-cell number-columns-spanned="2">
					                    <fo:block text-align="left" font-size="11pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(productName?if_exists)),35)}</fo:block>
							            </fo:table-cell>
							              
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("cstAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						    </#list>
          						    <fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" font-size="11pt">Analysis Code-Total</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("totalValue")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${codeIdMap.get("cstAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          						   </#if>
          					  </#list>
          					  <fo:table-row>
					                   <fo:table-cell number-columns-spanned="3">
					                    <fo:block text-align="left" font-weight="bold" >TOTAL-Discount For All Analysis Codes</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${taxCstCatMap.get("discount")?if_exists}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
          					  <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
          					</#if>
          					 <#-- catageory wise ends here -->
          					 <#--
		                    <#assign totalRevenue=0>
		                    <#assign totalTaxRevenue=0>
       							<#list taxDetailsCstList as invTaxMapObj>
       							<#assign invTaxMap= invTaxMapObj.getValue()>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							            <#assign  partyName="">
					            			<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="12pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),28)}</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("tinNumber")}</fo:block>
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("cstAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								</#list>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						 </fo:table-row> 
          						 -->
								<fo:table-row>
					                   <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="13pt">TOTAL</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="12pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${taxCstTotalMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${taxCstTotalMap.get("cstAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
	    					</fo:table-body>
                		</fo:table>
        			</fo:block> 		
				</fo:flow>
			</fo:page-sequence>
			</#if>
		<#if !(taxDetails14pt5List?has_content) && !(taxDetails5pt5List?has_content) && !(taxDetailsCstList?has_content)>
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	${uiLabelMap.NoOrdersFound}
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>