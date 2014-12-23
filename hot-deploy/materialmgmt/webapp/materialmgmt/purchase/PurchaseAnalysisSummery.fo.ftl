<#-- Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright ownership. The
ASF licenses this file to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License. -->


<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main" page-height="12in"
			page-width="10in" margin-left=".3in" margin-right=".3in"
			margin-top=".5in">
			<fo:region-body margin-top=".1in" />
			<fo:region-before extent="1in" />
			<fo:region-after extent="1in" />
		</fo:simple-page-master>
	</fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "prAnalysis.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
		</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if prchaseCategorySummeryMap?has_content>
         	<#if parameters.reportNameFlag?exists && "ProductWise"==parameters.reportNameFlag>
	    <fo:page-sequence master-reference="main" font-size="12pt">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block keep-together="always" text-align="right"
				font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number />
			</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
			<fo:block text-align="center" font-weight="bold"
				keep-together="always" white-space-collapse="false">PURCHASE ANALYSIS -REPORT(Product Wise) - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left" keep-together="always"
				font-family="Courier,monospace" font-weight="bold"
				white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="82pt" />
						<fo:table-column column-width="170pt" />
						<fo:table-column column-width="80pt" />
						<fo:table-column column-width="82pt" />
						<fo:table-column column-width="50pt" />
						<fo:table-column column-width="30pt" />
						<fo:table-column column-width="93pt" />
						<fo:table-column column-width="80pt" />
					<fo:table-body>
					<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">VOUCHER</fo:block>
									<fo:block text-align="left" font-size="11pt">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">PRODUCT NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">VOUCHER </fo:block>
									<fo:block text-align="left" font-size="11pt"> TYPE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">PDB</fo:block>
									<fo:block text-align="left" font-size="11pt">RefNo</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="10pt">CR/DB</fo:block>
									<fo:block text-align="center" font-size="10pt">ID</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" font-size="11pt">ASSESSABLE</fo:block>
									<fo:block text-align="center" font-size="11pt">VALUE</fo:block>
								</fo:table-cell>
							     </fo:table-row>
							     	</fo:table-body>
				</fo:table>
				</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="82pt" />
						<fo:table-column column-width="170pt" />
						<fo:table-column column-width="80pt" />
						<fo:table-column column-width="82pt" />
						<fo:table-column column-width="50pt" />
						<fo:table-column column-width="30pt" />
						<fo:table-column column-width="93pt" />
						<fo:table-column column-width="80pt" />
					<fo:table-body>
                   <#assign prchaseCategorySummeryList = prchaseCategorySummeryMap.entrySet()>
                   <#assign totalRevenue=0>
					<#list prchaseCategorySummeryList as prchaseCategorySummery>
					<#-- product Category starts here-->
					  <#assign purchaseProdCatMap=purchaseSumCatDetaildMap.get(prchaseCategorySummery.getKey())?if_exists>
					 <#if purchaseProdCatMap?has_content>
					  <#assign purchaseProdCatList=purchaseProdCatMap.entrySet()>
					   <#if purchaseProdCatList.size()!= 1 >
							       <#-- catageory wise starts here -->
		                     <#assign totalAssCstRevenue=0>
		                      
          						  <#list purchaseProdCatList as purchaseProdCat>
          						  <#if purchaseProdCat.getKey()!="discount">
          						   <#assign codeId=purchaseProdCat.getKey()>
          						   <#assign codeIdMap=purchaseProdCat.getValue()>
          						    <#assign codeIdList=codeIdMap.get("invoiceList")>
          						    
          						    <#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : codeId}, true)?if_exists/>
		   					  <fo:table-row>
								<fo:table-cell number-columns-spanned="6">
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"
										font-weight="bold">Analysis Code :${productCategory.description}</fo:block>
								</fo:table-cell>
							 </fo:table-row>
          					<#list codeIdList as invTaxMap>
          					<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"
										font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>
								</fo:table-cell>
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
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false">${invTaxMap.get("vchrType")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false">${invTaxMap.get("invoiceId")}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="center"
										font-size="12pt" white-space-collapse="false">${invTaxMap.get("crOrDbId")}</fo:block>
								</fo:table-cell>
					              <#assign assableValue=invTaxMap.get("invTotalVal")>
					            <#assign totalAssCstRevenue=totalAssCstRevenue+assableValue>
							    <fo:table-cell>
									<fo:block keep-together="always" text-align="right"
										font-size="11pt" white-space-collapse="false">${assableValue?string("#0.00")}</fo:block>
								</fo:table-cell>
							     </fo:table-row>
          						    </#list>
          						    
          				     <fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"
										font-weight="bold"></fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="3">
									<fo:block text-align="left" font-weight="bold" font-size="11pt">Analysis Code-Total</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="center"
										font-size="12pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="right"
										font-size="11pt" white-space-collapse="false">${(codeIdMap.get("totalValue"))?string("#0.00")}</fo:block>
								</fo:table-cell>
							     </fo:table-row>
          						   </#if>
          					  </#list>
          					  <fo:table-row>
								<fo:table-cell number-columns-spanned="3">
									<fo:block text-align="left" font-weight="bold">TOTAL-Discount For All Analysis Codes</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="left"
										font-size="12pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="center"
										font-size="12pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
							             <#assign totalAssCstRevenue=totalAssCstRevenue+purchaseProdCatMap.get("discount")>
							     <fo:table-cell>
									<fo:block keep-together="always" text-align="right"
										font-size="11pt" white-space-collapse="false">${purchaseProdCatMap.get("discount")?if_exists}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block keep-together="always" text-align="right"
										font-size="11pt" white-space-collapse="false"></fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell>
									<fo:block>&#160;</fo:block>
								</fo:table-cell>
							</fo:table-row>
			  </#if>
	        </#if>
	        <#-- product Category ends here-->
	         <fo:table-row>
				<fo:table-cell number-columns-spanned="3">
					<fo:block text-align="left" font-weight="bold">${prchaseCategorySummery.getKey()}-Total</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="left"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="left"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="center"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
			     <fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="11pt" white-space-collapse="false">${prchaseCategorySummery.getValue().get("total")?string("#0.00")}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="11pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
	          </fo:table-row>
			<fo:table-row>
	          <fo:table-cell number-columns-spanned="3">
					<fo:block keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">*************</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false">*************</fo:block>
				</fo:table-cell>
			</fo:table-row>
				</#list>
	    		</fo:table-body>
				</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
		<#-- productWise Ends here -->
		<#elseif parameters.reportNameFlag?exists && "Detailed"==parameters.reportNameFlag>
         	
	     <fo:page-sequence master-reference="main" font-size="12pt">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block keep-together="always" text-align="right"
				font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number />
			</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
			<fo:block text-align="center" font-weight="bold"
				keep-together="always" white-space-collapse="false">PURCHASE ANALYSIS -REPORT(Detailed) - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left" keep-together="always"
				font-family="Courier,monospace" font-weight="bold"
				white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="82pt" />
						<fo:table-column column-width="170pt" />
						<fo:table-column column-width="80pt" />
						<fo:table-column column-width="82pt" />
						<fo:table-column column-width="50pt" />
						<fo:table-column column-width="30pt" />
						<fo:table-column column-width="93pt" />
						<fo:table-column column-width="80pt" />
					<fo:table-body>
					<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">VOUCHER</fo:block>
									<fo:block text-align="left" font-size="11pt">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">PARTY NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt"></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">VOUCHER </fo:block>
									<fo:block text-align="left" font-size="11pt"> TYPE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="11pt">PDB</fo:block>
									<fo:block text-align="left" font-size="11pt">RefNo</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" font-size="10pt">CR/DB</fo:block>
									<fo:block text-align="center" font-size="10pt">ID</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center" font-size="11pt">ASSESSABLE</fo:block>
									<fo:block text-align="center" font-size="11pt">VALUE</fo:block>
								</fo:table-cell>
							     </fo:table-row>
							     	</fo:table-body>
				</fo:table>
				</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="82pt" />
						<fo:table-column column-width="170pt" />
						<fo:table-column column-width="80pt" />
						<fo:table-column column-width="82pt" />
						<fo:table-column column-width="50pt" />
						<fo:table-column column-width="30pt" />
						<fo:table-column column-width="93pt" />
						<fo:table-column column-width="80pt" />
					<fo:table-body>
                   <#assign prchaseCategorySummeryList = prchaseCategorySummeryMap.entrySet()>
                   <#assign totalRevenue=0>
					<#list prchaseCategorySummeryList as prchaseCategorySummery>
					<#-- product Category starts here-->
					  <#assign prchaseCategoryDetaildList=purchaseSumInvDetaildMap.get(prchaseCategorySummery.getKey())?if_exists>
					 <#if prchaseCategoryDetaildList?has_content>
					  <#list prchaseCategoryDetaildList as invTaxMap>
       							<fo:table-row>
       							       <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")}</fo:block>  
							            </fo:table-cell>
							              <#assign  partyName="">
							           <#if InvoicePartyAnalysisMap.get(invTaxMap.get("invoiceId"))?exists>
							                <#assign partyId=InvoicePartyAnalysisMap.get(invTaxMap.get("invoiceId"))>
							                <#if partyId?exists>
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)></#if>
					            		<#else>
							            	<#if invTaxMap.get("partyId")?exists>
					            			<#assign partyId=invTaxMap.get("partyId")>
					            			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
					            			</#if>
					            		</#if>
					                    <fo:table-cell>
					                    <fo:block text-align="left" font-size="10pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(partyName?if_exists)),28)}</fo:block>
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" ></fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" >${invTaxMap.get("vchrType")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false" >${invTaxMap.get("invoiceId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="10pt" white-space-collapse="false" >${invTaxMap.get("crOrDbId")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="10pt" white-space-collapse="false" >${invTaxMap.get("invTotalVal")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <#-->
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invTaxMap.get("vatAmount")?string("#0.00")}</fo:block>  
							            </fo:table-cell>-->
							     </fo:table-row>
						</#list>
						<fo:table-row>
								<fo:table-cell>
									<fo:block>&#160;</fo:block>
								</fo:table-cell>
					</fo:table-row>
					 
	        </#if>
	        <#-- product Category ends here-->
	        <fo:table-row>
	          <fo:table-cell number-columns-spanned="3">
					<fo:block keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${prchaseCategorySummery.getKey()}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false">${prchaseCategorySummery.getValue().get("total")?string("#0.00")}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
	          <fo:table-cell number-columns-spanned="3">
					<fo:block keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">*************************************</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false"></fo:block>
				</fo:table-cell>
			</fo:table-row>
				</#list>
	    		</fo:table-body>
				</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
		<#--  Detaild Ends here-->
		<#else>
		<fo:page-sequence master-reference="main" font-size="12pt">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block keep-together="always" text-align="right"
				font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number />
			</fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
			<fo:block keep-together="always" text-align="center"
				font-family="Courier,monospace" white-space-collapse="false"
				font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
			<fo:block text-align="center" font-weight="bold"
				keep-together="always" white-space-collapse="false">PURCHASE ANALYSIS - SUMMARY REPORT - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>-->
          			<fo:block text-align="left" keep-together="always"
				font-family="Courier,monospace" font-weight="bold"
				white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block text-align="left" font-weight="bold" font-size="12pt"
				keep-together="always" font-family="Courier,monospace"
				white-space-collapse="false">      Category                         &#160;&#160;&#160;&#160;&#160;&#160;      DR              CR           TOTAL</fo:block>
			<fo:block>------------------------------------------------------------------------------------------------</fo:block>
			<fo:block>
				<fo:table>
					<fo:table-column column-width="230pt" />
					<fo:table-column column-width="110pt" />
					<fo:table-column column-width="110pt" />
					<fo:table-column column-width="130pt" />
					<fo:table-body>
                   <#assign prchaseCategorySummeryList = prchaseCategorySummeryMap.entrySet()>
                   <#assign totalRevenue=0>
					<#list prchaseCategorySummeryList as prchaseCategorySummery>
	        <fo:table-row>
			   <fo:table-cell>
					<fo:block keep-together="always" text-align="left" font-size="12pt"
						white-space-collapse="false" font-weight="bold">${prchaseCategorySummery.getKey()}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false">${prchaseCategorySummery.getValue().get("DR")?string("#0.00")}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false">${prchaseCategorySummery.getValue().get("CR")?string("#0.00")}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block keep-together="always" text-align="right"
						font-size="12pt" white-space-collapse="false">${prchaseCategorySummery.getValue().get("total")?string("#0.00")}</fo:block>
				</fo:table-cell>
			</fo:table-row>
				</#list>
	    		</fo:table-body>
				</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
		</#if>
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
