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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
     <#if ProductCatMap?has_content> 
    <fo:page-sequence master-reference="main">
       <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
           <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
		   <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block> 
           <fo:block text-align="center" keep-together="always"  >&#160;-------------------------------------------------------------------</fo:block>
           <fo:block text-align="center" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;   PRODUCT PRICE REVISION REPORT BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>                
       </fo:static-content> 
          <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">				        
			 <fo:block font-family="Courier,monospace">
			    <fo:table  border-style="solid">
                    <fo:table-column column-width="70pt"/>
				    <fo:table-column column-width="90pt"/>
				    <fo:table-column column-width="90pt"/>
				    <fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="90pt"/>
				       <fo:table-body>
				          <fo:table-row height="20pt">
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="left" font-weight="bold" font-size="11pt" >ITEM CODE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >DEFAULT_PRICE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >MRP_PRICE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >MAXIMUM_PRICE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >UTP_PRICE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >MRP_IS</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >MRP_OS</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >WSD_PRICE</fo:block>
				             </fo:table-cell>
				             <fo:table-cell border-style="solid">
				                <fo:block text-align="center" font-weight="bold" font-size="11pt" >VAT_SALE</fo:block>
				             </fo:table-cell>
				        </fo:table-row>
				    </fo:table-body> 
				</fo:table>    
		    </fo:block>
		   <fo:block font-family="Courier,monospace">
		   <#assign productCatList = ProductCatMap.entrySet()>
              <#list productCatList as productList> 
                <#assign productCatId = productList.getKey()>
                <#assign productDetails = productList.getValue()>
                <#assign productPriceList = productDetails.entrySet()>
                <#list productPriceList as productsList> 
                   <#assign productId = productsList.getKey()?if_exists>
                   <#assign product = delegator.findOne("Product", {"productId" : productId}, true)?if_exists/>
                   <#assign prodDetails = productsList.getValue()>
                    <#list prodDetails as products> 
			   <fo:table  border-style="solid">
	              <fo:table-column column-width="70pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="90pt"/>
				     <fo:table-body>
					    <fo:table-row height="20pt">
			                <fo:table-cell border-style="solid">
			                    <fo:block text-align="left" font-size="11pt" >${product.internalName?if_exists}</fo:block>
				            </fo:table-cell>
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("DEFAULT_PRICE")?if_exists}</fo:block>
                           </fo:table-cell>   
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("MRP_PRICE")?if_exists}</fo:block>
                           </fo:table-cell>   
			                <fo:table-cell border-style="solid">
                               <fo:block text-align="right" font-size="11pt" >${products.get("MAXIMUM_PRICE")?if_exists}</fo:block>
                            </fo:table-cell>
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("UTP_PRICE")?if_exists}</fo:block>
                           </fo:table-cell>   
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("MRP_IS")?if_exists}</fo:block>
                           </fo:table-cell>   
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("MRP_OS")?if_exists}</fo:block>
                           </fo:table-cell>   
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("WSD_PRICE")?if_exists}</fo:block>
                           </fo:table-cell>  
                           <fo:table-cell border-style="solid"> 
			                  <fo:block text-align="right" font-size="11pt" >${products.get("VAT_SALE")?if_exists}</fo:block>
                           </fo:table-cell> 
				       </fo:table-row>
					</fo:table-body> 
			    </fo:table> 
	           </#list>
	         </#list>
	        </#list>                       
	     </fo:block>
	 </fo:flow>	
</fo:page-sequence>	
	<#else>
		<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 		<fo:block font-size="14pt" text-align="center">
            			 No Records Found....!
       		 		</fo:block>
    			</fo:flow>
		</fo:page-sequence>
	  </#if>
	</fo:root>
</#escape>		   		     	 