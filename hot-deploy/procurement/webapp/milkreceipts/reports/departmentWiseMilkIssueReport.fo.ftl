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
${setRequestAttribute("OUTPUT_FILENAME", "deptWiseMilkIssueReport.pdf")}
 <#if allDeptsIssuesMap?has_content> 

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
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">${fromDeptId?if_exists} - MILK ISSUE REGISTER  BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}  </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
     <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="120pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="150pt"/>
            
       	<fo:table-body>
	         <fo:table-row border-style="dotted">
  		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">SI NO	</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">DEPARTMENT</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">ISSUED DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">ISSUED PRODUCT</fo:block></fo:table-cell>       		
  		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">ISSUED QTY</fo:block></fo:table-cell>       		
	         </fo:table-row>
	        <#assign siNo=1>
            <#assign allDeptsIssuesDetails = allDeptsIssuesMap.entrySet()>
             <#list allDeptsIssuesDetails as allDeptsIssuesList>
             <fo:table-row border-style="dotted">
  		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold" font-size="12pt"> ${siNo}</fo:block></fo:table-cell>       		
		          <fo:table-cell number-columns-spanned="3"><fo:block text-align="left" font-weight="bold"  font-size="12pt">${allDeptsIssuesList.getKey()} </fo:block></fo:table-cell>       		
		          <fo:table-cell ><fo:block text-align="center"   font-size="12pt"> </fo:block></fo:table-cell>       		
	         </fo:table-row>
             <#assign eachDateIssuesDetails = allDeptsIssuesList.getValue().entrySet()>
             <#list eachDateIssuesDetails as eachDateIssuesList>
            
        <#--><fo:table-row border-style="dotted">
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"   font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachDateIssuesList.getKey(), "dd-MM-yyyy")} </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">   </fo:block></fo:table-cell>    
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">   </fo:block></fo:table-cell>       		
	         </fo:table-row>-->
	         <#assign eachDateProdDetails = eachDateIssuesList.getValue().entrySet()>
	         <#list eachDateProdDetails as eachDateProdDetailsList>
	          <#assign productNameDetails = delegator.findOne("Product", {"productId" : eachDateProdDetailsList.getKey()}, true)>
	         <fo:table-row border-style="dotted">
  		          <fo:table-cell ><fo:block text-align="left"   font-size="12pt"> </fo:block></fo:table-cell>       		
   		          <fo:table-cell ><fo:block text-align="left"   font-size="12pt"> </fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"   font-size="12pt"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachDateIssuesList.getKey(), "dd-MM-yyyy")}</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt"> <#if productNameDetails?has_content>${productNameDetails.get("productName")?if_exists} </#if>  </fo:block></fo:table-cell>       		
   		          <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"> <#if eachDateProdDetailsList.getValue()?has_content>${eachDateProdDetailsList.getValue()?if_exists?string("##0.00")}<#else>0.00</#if> </fo:block></fo:table-cell>       		
	         </fo:table-row>
	         </#list>
	         </#list>
	         	    <#assign siNo=siNo+1>
	         	    </#list>
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

