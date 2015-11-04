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
specific language governing permissions and limitationsborder-style="solid"border-style="solid"
under the License.
-->

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ProductionsReport.pdf")}
 <#if productionMap?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
		        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">   Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
		
		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> PRODUCTIONS <#if prodType?has_content>${prodType}</#if> REPORT BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
	 <#assign allProductsList = productionMap.entrySet()?if_exists>											
       <#list allProductsList as allProductsListDetails>
       <#assign  prunIssuesMap= allProductsListDetails.getValue().get("prunIssuesMap")>
       <#assign  prunDeclaresMap= allProductsListDetails.getValue().get("prunDeclaresMap")>
       <#assign  prunReturnsMap= allProductsListDetails.getValue().get("prunReturnsMap")>
			    
     <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="180pt"/>
		    <fo:table-column column-width="100pt"/>
		    <fo:table-column column-width="100pt"/>
		    <fo:table-column column-width="100pt"/>
		    
       	<fo:table-body>
       	 <#if prunIssuesMap?has_content> 
	         <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> ISSUED DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">ISSUED PRODUCT</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">QUANTITY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	       	<#assign prunIssuesList = prunIssuesMap.entrySet()?if_exists>											
            <#list prunIssuesList as prunIssues>
              <#assign products = delegator.findOne("Product", {"productId" : prunIssues.getValue().get("productId")}, true)>
				<#assign productName= products.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allProductsListDetails.getKey(), "dd-MM-yyyy")}</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${prunIssues.getValue().get("productId")?if_exists}[${productName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if prunIssues.getValue().get("quantity")?has_content> ${prunIssues.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	       </#list>
	       </#if>
	       
	              	 <#if prunDeclaresMap?has_content> 
	       <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> DECLARE DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">DECLARE PRODUCT</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">QUANTITY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	       	<#assign prunDeclaresList = prunDeclaresMap.entrySet()?if_exists>											
            <#list prunDeclaresList as prunDeclares>
              <#assign declareProducts = delegator.findOne("Product", {"productId" : prunDeclares.getValue().get("productId")}, true)>
				<#assign declareProdName= declareProducts.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allProductsListDetails.getKey(), "dd-MM-yyyy")}</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${prunDeclares.getValue().get("productId")?if_exists}[${declareProdName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if prunDeclares.getValue().get("quantity")?has_content> ${prunDeclares.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	       </#list>
	       </#if>
	       <#if prunReturnsMap?has_content> 
	      <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> RETURN DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">RETURN PRODUCT</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">QUANTITY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	       	<#assign prunReturnsList = prunReturnsMap.entrySet()?if_exists>											
            <#list prunReturnsList as prunReturns>
              <#assign returnProducts = delegator.findOne("Product", {"productId" : prunReturns.getValue().get("productId")}, true)>
				<#assign returnProdName= returnProducts.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allProductsListDetails.getKey(), "dd-MM-yyyy")}</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${prunReturns.getValue().get("productId")?if_exists}[${returnProdName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if prunReturns.getValue().get("quantity")?has_content> ${prunReturns.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	       </#list>
	       	       </#if>
	       
	    
    	</fo:table-body>
    </fo:table>
 </fo:block>	
	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
 
     </#list>
     
     <#-- ALL PRODUCTS TOTALS ---------------------------------------------------->
     
     	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
     	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
         	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">ABSTRACT  </fo:block>
         	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
    
       <#assign  productionTotIssMap= productionTotalsMap.get("productionTotIssMap")>
       <#assign  productionTotDeclaresMap= productionTotalsMap.get("productionTotDeclaresMap")>
       <#assign  productionTotReturnsMap= productionTotalsMap.get("productionTotReturnsMap")>
     <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
            <fo:table-column column-width="120pt"/>               
            <fo:table-column column-width="180pt"/>
		    <fo:table-column column-width="120pt"/>
		    <fo:table-column column-width="100pt"/>
		    <fo:table-column column-width="100pt"/>
		    
       	<fo:table-body>
       	
       	     <#if productionTotIssMap?has_content> 
	         <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> ISSUE TOTALS</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">ISSUED PRODUCTS</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">TOT ISSUE QTY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	         <#assign productionTotIssList = productionTotIssMap.entrySet()?if_exists>											
            <#list productionTotIssList as productionTotIssues>
              <#assign products = delegator.findOne("Product", {"productId" : productionTotIssues.getValue().get("productId")}, true)>
				<#assign productName= products.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt"></fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${productionTotIssues.getValue().get("productId")?if_exists}[${productName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if productionTotIssues.getValue().get("quantity")?has_content> ${productionTotIssues.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	       </#list>
	       </#if>
   	     <#if productionTotDeclaresMap?has_content> 
	       <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> DECLARE TOTALS</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">DECLARE PRODUCTS</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt"> TOT DECLARE QTY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	       	<#assign productionTotDeclareList = productionTotDeclaresMap.entrySet()?if_exists>											
            <#list productionTotDeclareList as productionTotDeclares>
              <#assign declareProducts = delegator.findOne("Product", {"productId" : productionTotDeclares.getValue().get("productId")}, true)>
				<#assign declareProdName= declareProducts.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt"></fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${productionTotDeclares.getValue().get("productId")?if_exists}[${declareProdName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if productionTotDeclares.getValue().get("quantity")?has_content> ${productionTotDeclares.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	                </#list>
	                </#if>
	         <#if productionTotReturnsMap?has_content> 
	        <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left" font-weight="bold"  font-size="12pt"> RETURN TOTALS</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">RETURN PRODUCTS</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">TOT RETURN QTY</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG FAT </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">KG SNF</fo:block></fo:table-cell>       		
	         </fo:table-row>
	       	<#assign productionTotReturnList = productionTotReturnsMap.entrySet()?if_exists>											
            <#list productionTotReturnList as productionTotReturn>
              <#assign returnProducts = delegator.findOne("Product", {"productId" : productionTotReturn.getValue().get("productId")}, true)>
				<#assign returnProdName= returnProducts.productName>
            
            <fo:table-row border-style="dotted">
		          <fo:table-cell  border-style="dotted"><fo:block text-align="left"   font-size="12pt"></fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${productionTotReturn.getValue().get("productId")?if_exists}[${returnProdName?if_exists}] </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if productionTotReturn.getValue().get("quantity")?has_content> ${productionTotReturn.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	       </fo:table-row>
	       </#list>
	       </#if>
	         	</fo:table-body>
    </fo:table>
 </fo:block>	
	                
	         
			 </fo:flow>
			 </fo:page-sequence>
			 <#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			NO RECORDS FOUND
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>  
</fo:root>
</#escape>

	