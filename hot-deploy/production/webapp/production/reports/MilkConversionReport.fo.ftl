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
	<fo:simple-page-master master-name="main" page-height="15in" page-width="12in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "MilkConversionReport.pdf")}
 <#if milkConversionMap?has_content>

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                            Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">&#160;&#160;DETAILS OF MILK RECEIVED FROM DIFFERENT UNIONS FOR CONVERSION BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="40pt"/>               
					                <fo:table-column column-width="100pt"/>               
						           <fo:table-column column-width="60pt"/>               
					                <fo:table-column column-width="100pt"/>               
					                <fo:table-column column-width="100pt"/>  
					                <fo:table-column column-width="10pt"/>               
					                <fo:table-column column-width="100pt"/>               
					                <fo:table-column column-width="100pt"/>               
					                <fo:table-column column-width="10pt"/>               
					                <fo:table-column column-width="100pt"/>               
					                <fo:table-column column-width="100pt"/>               

						           	<fo:table-body>
				                   <fo:table-row>
                                <fo:table-cell>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >------------------------------------------------------------------------------------------------------------------ </fo:block>  </fo:table-cell>
				                   </fo:table-row>
				                     <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >SL NO</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >&#160;UNION NAME</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  >TOTAL</fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >SKIM MILK</fo:block></fo:table-cell>       		
  				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >RAW MILK</fo:block></fo:table-cell>       		
   				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >TOTAL</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  >NO OF</fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >&#160;-------------------------</fo:block></fo:table-cell>       		
   				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >&#160;-------------------------</fo:block></fo:table-cell>       		
  				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="center"  font-size="12pt"  >&#160;--------------------------</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                   <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  >TANKERS</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >EXPECTED QTY(kg)</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >RECEIVED QTY(kg)</fo:block></fo:table-cell>       		
   				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >EXPECTED QTY(kg)</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >RECEIVED QTY(kg)</fo:block></fo:table-cell>       		
  				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >EXPECTED QTY(kg)</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >RECEIVED QTY(kg)</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                <fo:table-row>
                                <fo:table-cell>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >------------------------------------------------------------------------------------------------------------------ </fo:block>  </fo:table-cell>
				                 </fo:table-row>
				                 <#assign SNO=1>
				                 <#assign milkConversionMapDetails = milkConversionMap.entrySet()>
                              <#list milkConversionMapDetails as milkConversionMapDetail>
                                  <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="center" font-size="12pt"  >${SNO?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"   font-size="12pt"  >${milkConversionMapDetail.getKey()}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  >${milkConversionMapDetail.getValue().get("unionTankers")}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ><#if milkConversionMapDetail.getValue().get("receivedSkimQty")?has_content>${milkConversionMapDetail.getValue().get("receivedSkimQty")?if_exists?string("##0.00")}<#else>0.00 </#if></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt" ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ><#if milkConversionMapDetail.getValue().get("receivedRawQty")?has_content>${milkConversionMapDetail.getValue().get("receivedRawQty")?if_exists?string("##0.00")}<#else>0.00 </#if></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt" ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ><#if milkConversionMapDetail.getValue().get("receivedUnionQty")?has_content>${milkConversionMapDetail.getValue().get("receivedUnionQty")?if_exists?string("##0.00")}<#else>0.00 </#if></fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <#assign SNO=SNO+1>
                                  </#list>
                                  <fo:table-row>
                                <fo:table-cell>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >------------------------------------------------------------------------------------------------------------------ </fo:block>  </fo:table-cell>
				                 </fo:table-row>
  				                 <#assign receivedtotalsMapDetails = receivedtotalsMap.entrySet()>
				                 <fo:table-row>
				                     <fo:table-cell  number-columns-spanned="2"><fo:block text-align="left" font-size="12pt"  >TOTAL           :</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt"  >${receivedtotalsMap.get("totalTankers")}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >${receivedtotalsMap.get("totReceivedSkimQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
   				                     <fo:table-cell  ><fo:block text-align="center"  font-size="12pt" ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >${receivedtotalsMap.get("totReceivedRawQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
   				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  ></fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >${receivedtotalsMap.get("totReceivedQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  
				                <fo:table-row>
                                <fo:table-cell>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >------------------------------------------------------------------------------------------------------------------ </fo:block>  </fo:table-cell>
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

