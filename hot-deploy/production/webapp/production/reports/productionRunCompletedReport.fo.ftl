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
${setRequestAttribute("OUTPUT_FILENAME", "ss.pdf")}
 <#if productionRunData?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;         Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160; </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">PRODUCTION DETAILS </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">Production Run ID 	 : ${productionRunData.get("workEffortId")?if_exists} (${productionRunData.get("productionRunName")?if_exists})</fo:block>
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">Product ID        	 : ${productionRunData.get("productId")?if_exists} </fo:block>
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160; </fo:block>
		        
  		   <#--><fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt">ProductionRunName 	 : ${productionRunData.get("productionRunName")?if_exists}</fo:block>
  		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt">Description       	 : ${productionRunData.get("description")?if_exists}</fo:block>  -->
           <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="160pt"/>               
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="130pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="100pt"/>
           	<fo:table-body>
             <fo:table-row>
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt" >Qty To Produce</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${productionRunData.get("quantityToProduce")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
        <#--> <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">Produced Qty</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${productionRunData.get("quantityProduced")?if_exists}</fo:block></fo:table-cell>       		
             </fo:table-row>
              <fo:table-row>
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt" > Remaining Qty</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${productionRunData.get("quantityRemaining")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"> Rejected Qty</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${productionRunData.get("quantityRejected")?if_exists}</fo:block></fo:table-cell>       		
           -->
              </fo:table-row>
	          <fo:table-row>
	          <fo:table-cell ><fo:block text-align="left" font-size="5pt">&#160;&#160;</fo:block></fo:table-cell>       		
	          </fo:table-row>
              <fo:table-row>
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt" >Estimated Start Date</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(productionRunData.get("estimatedStartDate"), "dd/MM/yyyy")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">Start Date</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(productionRunData.get("actualStartDate"), "dd/MM/yyyy")}</fo:block></fo:table-cell>       		
             </fo:table-row>
               <fo:table-row>
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt" >Estimated Completion Date</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(productionRunData.get("estimatedCompletionDate"), "dd/MM/yyyy")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">Completion Date</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">&#160;&#160;&#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(productionRunData.get("actualCompletionDate"), "dd/MM/yyyy")}</fo:block></fo:table-cell>       		
             </fo:table-row>
    	</fo:table-body>
    		</fo:table>
     </fo:block>
     <#if issuedProductsMap?has_content> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">PRODUCTION ISSUE DETAILS: </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> &#160; S No    Product Id     Description                       Issued Qty       FAT %      SNF %                     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
         <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="70pt"/>               
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="230pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
           	<fo:table-body>
           	<#assign sno=1>
           	<#assign issuedProductsList = issuedProductsMap.entrySet()>
           <#list issuedProductsList as issuedProducts>
           <#assign productId= issuedProducts.getKey()?if_exists >
		    <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="center"   keep-together="always" font-size="12pt">${sno?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">${issuedProducts.getValue().get("issuedProdId")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"><#if productNameDetails?has_content>${productNameDetails.get("productName")?if_exists}</#if></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${-issuedProducts.getValue().get("issuedQty")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${issuedProducts.getValue().get("fatPercent")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${issuedProducts.getValue().get("snfPercent")?if_exists}</fo:block></fo:table-cell>       		
             </fo:table-row>
              	<#assign sno=sno+1>
               </#list>    
             </fo:table-body>
    		</fo:table>
     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     </#if>
        
     <#if declareProductsList?has_content> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">PRODUCTION DECLARE DETAILS:</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> Batch No   Product Id    Description                      Produced Qty      FAT %      SNF %                     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="80pt"/>               
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="220pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
           	<fo:table-body>
           <#list declareProductsList as declareProducts>
           <#assign productId= declareProducts.declareProdId?if_exists >
		   <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt">${declareProducts.batchNo?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">${declareProducts.declareProdId?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"><#if productNameDetails?has_content>${productNameDetails.get("productName")?if_exists}</#if></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${declareProducts.declareQty?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${declareProducts.fatPercent?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${declareProducts.snfPercent?if_exists}</fo:block></fo:table-cell>       		
             </fo:table-row>
               </#list>    
             </fo:table-body>
    		</fo:table>
     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     </#if>
     <#if returnProductsList?has_content> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">PRODUCTION RETURNS DETAILS:</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> Return No  Product Id    Description                        Return Qty      FAT %      SNF %                     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="80pt"/>               
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="220pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
           	<fo:table-body>
           <#list returnProductsList as returnProducts>
           <#assign productId= returnProducts.returnProdId?if_exists >
		   <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt">${returnProducts.returnId?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">${returnProducts.returnProdId?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt"><#if productNameDetails?has_content>${productNameDetails.get("productName")?if_exists}</#if></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${returnProducts.returnQty?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${returnProducts.fatPercent?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${returnProducts.snfPercent?if_exists}</fo:block></fo:table-cell>       		
             </fo:table-row>
               </#list>    
             </fo:table-body>
    		</fo:table>
     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160; </fo:block>
     </#if>
     <#if qcComponentsList?has_content> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">PRODUCTION QC DETAILS:</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> QC Product Id 	  QC Test Id   Sequence Number   TestComponent                  Test Value                  </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="130pt"/>
            <fo:table-column column-width="100pt"/>               
            <fo:table-column column-width="130pt"/>               
            <fo:table-column column-width="170pt"/>
            <fo:table-column column-width="100pt"/>
           	<fo:table-body>
           <#list qcComponentsList as qcComponents>
           <#assign productQcTest = delegator.findOne("ProductQcTest", {"qcTestId" :qcComponents.qcTestId }, true)>
            <fo:table-row >
		     <#if productQcTest?has_content>
            	 <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt">${productQcTest.productId?if_exists}</fo:block></fo:table-cell>       		
		     </#if>
              <fo:table-cell ><fo:block text-align="left"   keep-together="always" font-size="12pt">${qcComponents.qcTestId?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">${qcComponents.sequenceNumber?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  keep-together="always" font-size="12pt">${qcComponents.testComponent?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  keep-together="always" font-size="12pt">${qcComponents.testValue?if_exists}</fo:block></fo:table-cell>       		
             </fo:table-row>
               </#list>    
             </fo:table-body>
    		</fo:table>
     </fo:block>
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     </#if>
     
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

