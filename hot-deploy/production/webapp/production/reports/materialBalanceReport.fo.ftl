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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
 <#if closingBalanceFinalMap?has_content> 
${setRequestAttribute("OUTPUT_FILENAME", "arcOrder.pdf")}
<#-- <#if milkTransferMap?has_content> -->

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
		        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">&#160;&#160;${deptName} MATERIAL BALANCE FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
        <fo:table width="100%" align="left" table-layout="fixed"  font-size="12pt" border-style="solid">
                <fo:table-column column-width="40pt"/>               
                <fo:table-column column-width="200pt"/>               
	            <fo:table-column column-width="100pt"/>               
                <fo:table-column column-width="70pt"/>               
                <fo:table-column column-width="70pt"/>  
                <fo:table-column column-width="90pt"/>               
                <fo:table-column column-width="90pt"/>               
	           <fo:table-body>
	             <#assign openingBal = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign receipts = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign issues = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign closingBal = (Static["java.math.BigDecimal"].ZERO)>
	           
               <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"    font-size="12pt" font-weight="bold">SI NO</fo:block></fo:table-cell>       			
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"    font-size="12pt" font-weight="bold">&#160;DESCRIPTION</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt" font-weight="bold">QUANTITY(Kgs.)</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt" font-weight="bold">FAT %</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt" font-weight="bold">SNF %</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt" font-weight="bold">FAT (Kgs.)</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt" font-weight="bold">SNF (Kgs.)</fo:block></fo:table-cell>       		
              </fo:table-row>
              <#assign sNo=1>
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt" font-weight="bold" >I</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid" number-columns-spanned="6"><fo:block text-align="left"  font-size="14pt" font-weight="bold" >Opening Balance</fo:block></fo:table-cell> 
	          </fo:table-row>
	       <#assign materialSiloDetails = openingBalProductMap.entrySet()>
  	          <#list materialSiloDetails as materialSiloData>
              <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"    >${materialSiloData.getValue().get("description")?if_exists}[${materialSiloData.getValue().get("productId")?if_exists}]</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  ><#if materialSiloData.getValue().get("quantity")?has_content>${materialSiloData.getValue().get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  ><#if materialSiloData.getValue().get("fatPers")?has_content>${materialSiloData.getValue().get("fatPers")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  ><#if materialSiloData.getValue().get("snfPers")?has_content>${materialSiloData.getValue().get("snfPers")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  ><#if materialSiloData.getValue().get("fatKg")?has_content>${materialSiloData.getValue().get("fatKg")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  ><#if materialSiloData.getValue().get("snfKg")?has_content>${materialSiloData.getValue().get("snfKg")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
	          </fo:table-row>
             <#assign sNo=sNo+1>
            </#list>
	          <#if openingBalProductTotalMap?has_content> 
	          	 <#assign  openingBal=openingBal.add(openingBalProductTotalMap.get("quantity"))>
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt"  ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"   font-size="12pt"  font-weight="bold">${openingBalProductTotalMap.get("description")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ><#if openingBalProductTotalMap.get("quantity")?has_content>${openingBalProductTotalMap.get("quantity")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ><#if openingBalProductTotalMap.get("fatKg")?has_content>${openingBalProductTotalMap.get("fatKg")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ><#if openingBalProductTotalMap.get("snfKg")?has_content>${openingBalProductTotalMap.get("snfKg")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block></fo:table-cell>       		
	          </fo:table-row>
	          </#if>
          
            <#--    ==================RECEIPTS ===================         -->
             
             <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt" font-weight="bold" >II</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid" number-columns-spanned="6"><fo:block text-align="left"  font-size="14pt" font-weight="bold" >Receipts</fo:block></fo:table-cell> 
	          </fo:table-row>
             <#if deptId.equalsIgnoreCase("INT7") >
               <#assign sNo=1>
  	             <#assign qtyTotal = (Static["java.math.BigDecimal"].ZERO)>
  	             <#assign fatKgTotal = (Static["java.math.BigDecimal"].ZERO)>
  	             <#assign snfKgTotal = (Static["java.math.BigDecimal"].ZERO)>
             <#if convUnionProductsMap?has_content> 
	             <fo:table-row border-style="solid">
                	 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-weight="bold" font-size="12pt"  >A</fo:block></fo:table-cell>       		
                	 <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold" font-size="12pt"   >Conversion</fo:block></fo:table-cell>       		
	            </fo:table-row>
  	          <#assign convMilkReceiptsList = convUnionProductsMap.entrySet()>
              <#list convMilkReceiptsList as convMilkReceiptsData>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   >${convMilkReceiptsData.getKey()}</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${convMilkReceiptsData.getValue().get("receivedQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${convMilkReceiptsData.getValue().get("receivedFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${convMilkReceiptsData.getValue().get("receivedSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${convMilkReceiptsData.getValue().get("receivedKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${convMilkReceiptsData.getValue().get("receivedKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <#assign sNo=sNo+1>
	            </#list>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"  font-weight="bold" >TOTAL</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${convMilkReceiptsTotal.get("totRecdQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${convMilkReceiptsTotal.get("totRecdKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${convMilkReceiptsTotal.get("totRecdKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	          <#assign  qtyTotal=qtyTotal.add(convMilkReceiptsTotal.get("totRecdQty"))>
	          <#assign  fatKgTotal=fatKgTotal.add(convMilkReceiptsTotal.get("totRecdKgFat"))>
	          <#assign  snfKgTotal=snfKgTotal.add(convMilkReceiptsTotal.get("totRecdKgSnf"))>
            </#if>
             <#if idrUnionProductsMap?has_content> 
             
	             <fo:table-row border-style="solid">
                	 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-weight="bold" font-size="12pt"  >B</fo:block></fo:table-cell>       		
                	 <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold" font-size="12pt"   >Purchase</fo:block></fo:table-cell>       		
	            </fo:table-row>
  	          <#assign purchaseMilkReceiptsList = idrUnionProductsMap.entrySet()>
              <#list purchaseMilkReceiptsList as purchaseMilkReceiptsData>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   >${purchaseMilkReceiptsData.getKey()}</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${purchaseMilkReceiptsData.getValue().get("receivedQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${purchaseMilkReceiptsData.getValue().get("receivedFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${purchaseMilkReceiptsData.getValue().get("receivedSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${purchaseMilkReceiptsData.getValue().get("receivedKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${purchaseMilkReceiptsData.getValue().get("receivedKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <#assign sNo=sNo+1>
	            </#list>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt"  font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   font-weight="bold">TOTAL</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${purchaseMilkReceiptsTotal.get("totRecdQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${purchaseMilkReceiptsTotal.get("totRecdKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${purchaseMilkReceiptsTotal.get("totRecdKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	          <#assign  qtyTotal=qtyTotal.add(purchaseMilkReceiptsTotal.get("totRecdQty"))>
	          <#assign  fatKgTotal=fatKgTotal.add(purchaseMilkReceiptsTotal.get("totRecdKgFat"))>
	          <#assign  snfKgTotal=snfKgTotal.add(purchaseMilkReceiptsTotal.get("totRecdKgSnf"))>
           </#if>
        </#if>
            <#if intReturnsAndReceipts?has_content> 
               <#if deptId.equalsIgnoreCase("INT7") >
	             <fo:table-row border-style="solid">
                	 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-weight="bold" font-size="12pt"  >C</fo:block></fo:table-cell>       		
                	 <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold" font-size="12pt"   >Returns/ Receipts</fo:block></fo:table-cell>       		
	            </fo:table-row>
	          </#if>
  	          <#assign intReturnsAndReceiptsList = intReturnsAndReceipts.entrySet()>
              <#list intReturnsAndReceiptsList as intReturnsAndReceiptsData>
              		   <#assign productNameDetails = delegator.findOne("Product", {"productId" : intReturnsAndReceiptsData.getKey()}, true)>
              
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   >${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${intReturnsAndReceiptsData.getValue().get("receivedQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${intReturnsAndReceiptsData.getValue().get("receivedFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${intReturnsAndReceiptsData.getValue().get("receivedSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${intReturnsAndReceiptsData.getValue().get("receivedKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${intReturnsAndReceiptsData.getValue().get("receivedKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <#assign sNo=sNo+1>
	            </#list>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt"  font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   font-weight="bold">TOTAL</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${intReturnsAndReceiptsTotal.get("totRecdQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${intReturnsAndReceiptsTotal.get("totRecdKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${intReturnsAndReceiptsTotal.get("totRecdKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	           <#if !deptId.equals("INT7") >
         	 		 <#assign  receipts=receipts.add(intReturnsAndReceiptsTotal.get("totRecdQty"))>
       		   </#if>
	          <#assign  qtyTotal=qtyTotal.add(intReturnsAndReceiptsTotal.get("totRecdQty"))>
	          <#assign  fatKgTotal=fatKgTotal.add(intReturnsAndReceiptsTotal.get("totRecdKgFat"))>
	          <#assign  snfKgTotal=snfKgTotal.add(intReturnsAndReceiptsTotal.get("totRecdKgSnf"))>
           </#if>
         <#if deptId.equalsIgnoreCase("INT7") >
         	  <#assign  receipts=receipts.add(qtyTotal)>
         
           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt"  font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   font-weight="bold">TOTAL (A+B+C)</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${qtyTotal?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold"></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${fatKgTotal?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold">${snfKgTotal?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
           </#if>
           
          <#-- Milk Issues============================= -->
         <#if milkIssuesMap?has_content> 
          <#assign sNo=1>
          <#assign milkIssueslist = milkIssuesMap.entrySet()>
	             <fo:table-row border-style="solid">
                	 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-weight="bold" font-size="12pt"  >III</fo:block></fo:table-cell>       		
                	 <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold" font-size="14pt"   >Issues</fo:block></fo:table-cell>       		
	            </fo:table-row>
              <#list milkIssueslist as milkIssuesData>
                    <#assign productNameDetails = delegator.findOne("Product", {"productId" : milkIssuesData.getKey()}, true)>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   >${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${milkIssuesData.getValue().get("issuedQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${milkIssuesData.getValue().get("issuedFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${milkIssuesData.getValue().get("issuedSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${milkIssuesData.getValue().get("issuedKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${milkIssuesData.getValue().get("issuedKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <#assign sNo=sNo+1>
	            </#list>
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"  font-weight="bold" >TOTAL</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${milkIssuesTotalsMap.get("totIssuedQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${milkIssuesTotalsMap.get("totIssuedKgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${milkIssuesTotalsMap.get("totIssuedKgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>  
	                   	 		 <#assign  issues=issues.add(milkIssuesTotalsMap.get("totIssuedQty"))>
	          
            </#if>
       
       <#if closingBalanceFinalMap?has_content> 
          <#assign sNo=1>
          <#assign closingBalanceList = closingBalanceFinalMap.entrySet()>
	             <fo:table-row border-style="solid">
                	 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-weight="bold" font-size="12pt"  >IV</fo:block></fo:table-cell>       		
                	 <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold" font-size="14pt"   >Close Balance</fo:block></fo:table-cell>       		
	            </fo:table-row>
              <#list closingBalanceList as closingBalanceData>
                  <#assign productNameDetails = delegator.findOne("Product", {"productId" : closingBalanceData.getKey()}, true)>
	           <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"   >${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${closingBalanceData.getValue().get("quantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${closingBalanceData.getValue().get("fat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${closingBalanceData.getValue().get("snf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${closingBalanceData.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   >${closingBalanceData.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <#assign sNo=sNo+1>
	            </#list>
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"  font-weight="bold" >TOTAL</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${closingBalanceFinalTotalMap.get("totClosingQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${closingBalanceFinalTotalMap.get("totClosingFatKg")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${closingBalanceFinalTotalMap.get("totClosingSnfKg")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	          	 	<#assign  closingBal=closingBal.add(closingBalanceFinalTotalMap.get("totClosingQty"))>
	            
            </#if>
              <fo:table-row >
                 <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >&#160;</fo:block></fo:table-cell>       		
	          </fo:table-row>
              <#assign openingBal = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign receipts = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign issues = (Static["java.math.BigDecimal"].ZERO)>
	             <#assign closingBal = (Static["java.math.BigDecimal"].ZERO)>
	           
            
               <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"  font-weight="bold" >OB + Receipts</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${obAndReceiptQty?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${obAndReceiptFatKg?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${obAndReceiptSnfKg?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	            <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt"  font-weight="bold" >CB + Disposal</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${cbAndIssueQty?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
  	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${cbAndIssueFatKg?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
  	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${cbAndIssueSnfKg?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	         <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"   font-size="12pt"  font-weight="bold" >Diff</fo:block></fo:table-cell>       		
              	 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${diffQty?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" ></fo:block></fo:table-cell>       		
  	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${diffKgFat?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
  	             <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"   font-weight="bold" >${diffKgSnf?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	          
	            
         	</fo:table-body>
    	</fo:table>
		
			   
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

