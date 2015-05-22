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
 <#if allSiloDetailsMap?has_content> 
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
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">&#160;&#160;MATERIAL BALANCE FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
        <fo:table width="100%" align="left" table-layout="fixed"  font-size="12pt" border-style="solid">
                <fo:table-column column-width="40pt"/>               
                <fo:table-column column-width="180pt"/>               
	            <fo:table-column column-width="120pt"/>               
                <fo:table-column column-width="80pt"/>               
                <fo:table-column column-width="80pt"/>  
                <fo:table-column column-width="90pt"/>               
                <fo:table-column column-width="90pt"/>               
	           <fo:table-body>
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
              <#assign allSiloDetails = allSiloDetailsMap.entrySet()>
              <#list allSiloDetails as allSiloDetailsData>
              <#assign materialSiloDetails = allSiloDetailsData.getValue()?if_exists>  
     
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt" font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid" number-columns-spanned="6"><fo:block text-align="left"  font-size="14pt" font-weight="bold" >${allSiloDetailsData.getKey()?if_exists}</fo:block></fo:table-cell> 
                 <#assign sNo=1>
	          </fo:table-row>
  	          <#list materialSiloDetails as materialSiloData>
	          <#if materialSiloData.get("description")!="Total"> 
              <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt"  >${sNo?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"  font-size="12pt" >${materialSiloData.get("description")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  >${materialSiloData.get("quantity")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  >${materialSiloData.get("fatPers")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" >${materialSiloData.get("snfPers")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  >${materialSiloData.get("fatKg")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt"  >${materialSiloData.get("snfKg")?if_exists}</fo:block></fo:table-cell>       		
                <#assign sNo=sNo+1>
	          </fo:table-row>
   	          </#if>
	           <#if materialSiloData.get("description")=="Total"> 
	          <fo:table-row border-style="solid">
                 <fo:table-cell  border-style="solid"><fo:block text-align="center" font-size="12pt"  ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="left"   font-size="12pt"  font-weight="bold">${materialSiloData.get("description")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" >${materialSiloData.get("quantity")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" ></fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" >${materialSiloData.get("fatKg")?if_exists}</fo:block></fo:table-cell>       		
                 <fo:table-cell  border-style="solid"><fo:block text-align="right"  font-size="12pt" font-weight="bold" >${materialSiloData.get("snfKg")?if_exists}</fo:block></fo:table-cell>       		
	          </fo:table-row>
	          </#if>
              </#list>
            </#list>
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

