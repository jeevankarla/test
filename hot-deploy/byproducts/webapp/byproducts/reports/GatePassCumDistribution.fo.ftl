<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing ,
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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right=".3in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "GatePassCumDistri.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
			 <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="center" white-space-collapse="false"> KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>	
					<fo:block text-align="center" white-space-collapse="false"> UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>	
					<fo:block text-align="center" white-space-collapse="false"> GATEPASS CUM DISTRIBUTION ROUTESHEET :SACHETS</fo:block>
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/> &#160;                        GATE PASS ON, ${estimatedDeliveryDate}</fo:block>
                    <fo:block font-family="Courier,monospace" font-size="6pt" >
                         <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                                    <fo:table-column column-width="100pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="70pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="15pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="55pt"/>
					            	<fo:table-column column-width="65pt"/>
					            	<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				              <fo:table-body>
				                   <fo:table-row>
			                          <fo:table-cell>
			                             <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                       </fo:table-row>
			                        <fo:table-row>
						            	<fo:table-cell >
						            	       <fo:block keep-together="always" white-space-treatment="preserve" white-space-collapse="false" text-align="left"> RETAILER-CODE and NAME</fo:block></fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center" >PROD</fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center" >CODE </fo:block>
				                       	</fo:table-cell>                       		
				                      	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">SUB</fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">MILK </fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-indent="12pt" text-align="center">DESP  /  CRATES </fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false"  text-indent="15pt" text-align="left">QTY</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"   white-space-collapse="false" text-align="center">RETURNS</fo:block>
				                       	</fo:table-cell>
				                       		<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"   white-space-collapse="false" text-align="center"></fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">BASIC</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">VAT%</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">VAT</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-indent="5pt" text-align="center">AMOUNT</fo:block>
				                       	</fo:table-cell>
				                        <fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">AMOUNT</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">PAYBLE</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">CHEQUE/DD</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">NUMBER</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"   white-space-treatment="preserve"  white-space-collapse="false" text-align="center">CHEQUE/DD</fo:block>
				                       			<fo:block keep-together="always"   white-space-treatment="preserve"  white-space-collapse="false" text-align="center">AMOUNT</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">RETAILER </fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">SIGNATURE</fo:block>
				                       	</fo:table-cell>
					                </fo:table-row>
						            <fo:table-row>
			                          <fo:table-cell>
			                               <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                       </fo:table-row>
					              </fo:table-body>
					           </fo:table>
                    </fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
			           <fo:block font-family="Courier,monospace" font-size="6pt" >
			            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			                          <fo:table-column column-width="100pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="35pt"/>
					            	<fo:table-column column-width="10pt"/>
					            	<fo:table-column column-width="24pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="55pt"/>
					            	<fo:table-column column-width="65pt"/>
					            	<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				            	 <fo:table-body>
           		   <#list truckSheetReportList as truckSheetReport>
           			<#assign facilityGrandTotal = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign totalLitres = (Static["java.math.BigDecimal"].ZERO)>
           			<#assign facilityTypeId = truckSheetReport.get("facilityType")>
           			
           				 <#if (facilityTypeId == "BOOTH" )>
           				 	<#assign facilityTotal = (Static["java.math.BigDecimal"].ZERO)>
           				 	 <#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)>
			               <fo:table-row>
			               	  <fo:table-cell>
			                  <fo:block text-align="left" keep-together="always" white-space-collapse="false"> ${truckSheetReport.get("facilityId")}-${facility.get("facilityName")}</fo:block>
			                   </fo:table-cell>
			              </fo:table-row>
			                <#assign totalRevenue =(Static["java.math.BigDecimal"].ZERO)>
			                <#list productSet as productId>
			               	<#assign productTotalMap =truckSheetReport.get(productId)?if_exists>
			               	 <#if productTotalMap?has_content>
			              <fo:table-row>
			              	<#assign total =productTotalMap.get("TOTAL")?if_exists>
			               	<#assign crates =productTotalMap.get("CRATES")?if_exists>
			               	<#assign totalAmount =productTotalMap.get("TOTALAMOUNT")?if_exists>
			               	<#assign totalRevenue =totalRevenue+productTotalMap.get("TOTALAMOUNT")?if_exists>
			               	  <fo:table-cell>
			                  <fo:block></fo:block>
			                   </fo:table-cell>
			               	  <fo:table-cell>
			                   		    <fo:block  text-align="center" keep-together="always" white-space-collapse="false" >${productId?if_exists}</fo:block>
			                  </fo:table-cell>
			                   <fo:table-cell>
			                   		    <fo:block  text-align="right" keep-together="always" white-space-collapse="false" ></fo:block>
			                  </fo:table-cell>
			                  <fo:table-cell>
			                   		    <fo:block text-align="right" keep-together="always" white-space-collapse="false" >${productTotalMap.get("TOTAL")?if_exists}</fo:block> 
			                     </fo:table-cell>
			                      <fo:table-cell>
			                   		    <fo:block text-align="right" keep-together="always"  white-space-collapse="false" >/</fo:block> 
			                     </fo:table-cell>
			                     <fo:table-cell>
			                   		  <fo:block text-align="right" keep-together="always" white-space-collapse="false" >${productTotalMap.get("CRATES")?if_exists?string("##0.0#")} </fo:block>
			                     </fo:table-cell>
			                   <fo:table-cell>
			                   		    <fo:block  text-align="right" keep-together="always" white-space-collapse="false" ></fo:block>
			                  </fo:table-cell>
			                  <fo:table-cell>
			                   		    <fo:block text-align="right" keep-together="always" white-space-collapse="false">${productTotalMap.get("BASIC")?if_exists}</fo:block>
			                  </fo:table-cell>
			                  <fo:table-cell>
			                   		    <fo:block text-align="right" keep-together="always" white-space-collapse="false">${productTotalMap.get("VAT")?if_exists}</fo:block>
			                   </fo:table-cell>
					            <fo:table-cell ><fo:block keep-together="always" white-space-collapse="false" text-align="right" >${productTotalMap.get("VAT_AMOUNT")?if_exists}</fo:block></fo:table-cell>
					            <fo:table-cell><fo:block keep-together="always" white-space-collapse="false" text-align="right" >${totalAmount?if_exists}</fo:block></fo:table-cell>
					            <fo:table-cell><fo:block keep-together="always" white-space-collapse="false"  text-align="right" ></fo:block></fo:table-cell>
					            <fo:table-cell><fo:block keep-together="always" white-space-collapse="false" text-align="right" ></fo:block></fo:table-cell>
					            <fo:table-cell><fo:block keep-together="always" white-space-collapse="false" text-align="right" ></fo:block></fo:table-cell>
			                  </fo:table-row>
			                      </#if>
			                    </#list>
			                    <fo:table-row>
				               	   <fo:table-cell>
				                      <fo:block></fo:block>
				                   </fo:table-cell>
				               	   <fo:table-cell>
				                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false" text-indent="10pt"></fo:block>
				                   </fo:table-cell>
				                   <fo:table-cell>
				                   		    <fo:block></fo:block>
				                   </fo:table-cell>
				                   <fo:table-cell>
				                   		    <fo:block></fo:block>
				                     </fo:table-cell>
				                   <fo:table-cell>
				                   		    <fo:block></fo:block>
				                   </fo:table-cell>
				                   <fo:table-cell>
				                   		    <fo:block></fo:block>
				                   </fo:table-cell>
				                   <fo:table-cell>
				                   		    <fo:block></fo:block>
				                    </fo:table-cell>
				                     <fo:table-cell>
			                   		    <fo:block text-align="right" keep-together="always"  white-space-collapse="false" ></fo:block> 
			                     </fo:table-cell>
			                     <fo:table-cell>
			                   		  <fo:block text-align="right" keep-together="always" white-space-collapse="false" > </fo:block>
			                     </fo:table-cell>
						            <fo:table-cell><fo:block text-align="right" ></fo:block></fo:table-cell>
						            <fo:table-cell><fo:block>&#160;</fo:block><fo:block text-align="right" >${totalRevenue?if_exists}</fo:block></fo:table-cell>
						            <fo:table-cell><fo:block>&#160;</fo:block><fo:block text-align="right" >Challan</fo:block></fo:table-cell>
						            <fo:table-cell><fo:block text-align="right" ></fo:block></fo:table-cell>
						            <fo:table-cell><fo:block text-align="right" ></fo:block></fo:table-cell>
			                   </fo:table-row>
			                    <fo:table-row>
			                      <fo:table-cell>
			                              <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                       </fo:table-cell>
			                     </fo:table-row>
			                  </#if>
			                  <#if (facilityTypeId == "ROUTE")> 
			                     </fo:table-body>
			                 </fo:table>
			        </fo:block>
			       <fo:block font-family="Courier,monospace" font-size="10pt" break-after="page"></fo:block> 
        </fo:flow>						        	
     </fo:page-sequence>		
     <fo:page-sequence master-reference="main" force-page-count="no-force">	
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
                    <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" >&#160;                                                                    KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>	
					<fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always">&#160;                                                                        UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>	
					<fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always">&#160;                                                                        GATEPASS CUM DISTRIBUTION ROUTESHEET :SACHETS</fo:block>
                    <fo:block font-family="Courier,monospace" font-size="6pt" keep-together="always">${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
			            <fo:block>
			            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			             <fo:table-column column-width="300pt"/>
			              <fo:table-column column-width="300pt"/>
			               <fo:table-column column-width="300pt"/>
			               <fo:table-body>
			               <#assign facilityTotal = (Static["java.math.BigDecimal"].ZERO)>
           				 	 <#assign facility = delegator.findOne("Facility", {"facilityId" : truckSheetReport.get("facilityId")}, true)>
           				   <fo:table-row>
			                    <fo:table-cell>
			                  <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                    </fo:table-cell>
			                </fo:table-row>
			                 <fo:table-row>
				                   <fo:table-cell>
				                         <fo:block  text-indent="15pt" >ROUTE NUMBER:${truckSheetReport.get("facilityId")}</fo:block>
				                         <fo:block  text-indent="15pt">VEH NUMBER:${truckSheetReport.get("facilityId")}</fo:block>
				                         <fo:block  text-indent="15pt">CONTRACTOR:${facility.get("facilityName")}</fo:block>
				                     </fo:table-cell>
				                      <fo:table-cell>
				                         <fo:block>SHIFT/TRIP:${parameters.shipmentTypeId}</fo:block>
				                          <fo:block>DESPATCH DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDate, "dd/MM/yyyy")}</fo:block>
				                          <fo:block>DESPATCH TIME:</fo:block>
				                  </fo:table-cell>
				                    <fo:table-cell>
				                         <fo:block>G.P.NUMBER:</fo:block>
				                         <fo:block>G.P.DATE:${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd/MM/yyyy")}</fo:block>
				                          <fo:block>G.P.TIME:${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()}</fo:block>
				                   </fo:table-cell>
			                     </fo:table-row>
			                     <fo:table-row>
			                        <fo:table-cell>
			                              <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                      </fo:table-row>
			                          <#assign totalRevenue =(Static["java.math.BigDecimal"].ZERO)>
			                      <fo:table-row>
			               	         <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >PRODUCT NAME                                      SUB MILK                          LOOSE SUBMILK                        DESPQTY                               LOOSE(PCKTS)</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      <fo:table-row>
			               	         <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >&#160;</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                <#list productSet as productId>
			               	<#assign productTotalMap =truckSheetReport.get(productId)?if_exists>
			               	
			              <#if productTotalMap?has_content>
			              <fo:table-row>
			              	<#assign total =productTotalMap.get("TOTAL")?if_exists>
			               	<#assign crates =productTotalMap.get("CRATES")?if_exists>
			               	<#assign totalAmount =productTotalMap.get("TOTALAMOUNT")?if_exists>
			               	<#assign totalRevenue =totalRevenue+productTotalMap.get("TOTALAMOUNT")?if_exists>
			               	<#assign product = delegator.findOne("Product", {"productId" : productId}, true)>
			               	  <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >${product.get("brandName")?if_exists}</fo:block>
			                  </fo:table-cell>
			                  <fo:table-cell>
			                   		    <fo:block text-align="center" keep-together="always" white-space-collapse="false" text-indent="25pt" >${productTotalMap.get("TOTAL")?if_exists} / ${productTotalMap.get("NOCRATES")?if_exists?string("##0.0#")}</fo:block> 
			                     </fo:table-cell>
			                     <fo:table-cell>
			                   		  <fo:block text-align="left" keep-together="always" white-space-collapse="false"  >${productTotalMap.get("NOPKTS")?if_exists}/1 </fo:block>
			                     </fo:table-cell>
			                  </fo:table-row>
			                  <fo:table-row>
			               	         <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >&#160;</fo:block>
			                        </fo:table-cell>
			                   </fo:table-row>
			                     </#if>
			                    </#list>
			                    <fo:table-row>
			               	         <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >&#160;</fo:block>
			                        </fo:table-cell>
			                   </fo:table-row>
			                   <fo:table-row>
			               	         <fo:table-cell>
			                   		    <fo:block  text-align="left" keep-together="always" white-space-collapse="false"  text-indent="15pt" >&#160;</fo:block>
			                        </fo:table-cell>
			                   </fo:table-row>
			                     </fo:table-body>
			                 </fo:table>
			                  </fo:block>
			                  	<fo:block font-family="Courier,monospace" font-size="10pt" break-after="page"> </fo:block> 
			      </fo:flow>						        	
   </fo:page-sequence>
   <fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
			 <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block text-align="center" white-space-collapse="false"> KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION  LTD</fo:block>	
					<fo:block text-align="center" white-space-collapse="false"> UNIT: MOTHER DAIRY: G.K.V.K POST,YELAHANKA,BENGALORE:560065</fo:block>	
					<fo:block text-align="center" white-space-collapse="false"> GATEPASS CUM DISTRIBUTION ROUTESHEET :SACHETS</fo:block>
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/> &#160;                        GATE PASS ON, ${estimatedDeliveryDate}</fo:block>
                    <fo:block font-family="Courier,monospace" font-size="6pt" >
                         <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                                    <fo:table-column column-width="100pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="70pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="15pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="55pt"/>
					            	<fo:table-column column-width="65pt"/>
					            	<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				              <fo:table-body>
				                   <fo:table-row>
			                          <fo:table-cell>
			                              <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                       </fo:table-row>
			                        <fo:table-row>
						            	<fo:table-cell >
						            	       <fo:block keep-together="always" white-space-treatment="preserve" white-space-collapse="false" text-align="left"> RETAILER-CODE and NAME</fo:block></fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center" >PROD</fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center" >CODE </fo:block>
				                       	</fo:table-cell>                       		
				                      	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">SUB</fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">MILK </fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-indent="12pt" text-align="center">DESP  /  CRATES </fo:block>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false"  text-indent="15pt" text-align="left">QTY</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"   white-space-collapse="false" text-align="center">RETURNS</fo:block>
				                       	</fo:table-cell>
				                       		<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"   white-space-collapse="false" text-align="center"></fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always" white-space-treatment="preserve"  white-space-collapse="false" text-align="center">BASIC</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">VAT%</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">VAT</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-indent="5pt" text-align="center">AMOUNT</fo:block>
				                       	</fo:table-cell>
				                        <fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">AMOUNT</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve"  white-space-collapse="false" text-align="center">PAYBLE</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">CHEQUE/DD</fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">NUMBER</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"   white-space-treatment="preserve"  white-space-collapse="false" text-align="center">CHEQUE/DD</fo:block>
				                       			<fo:block keep-together="always"   white-space-treatment="preserve"  white-space-collapse="false" text-align="center">AMOUNT</fo:block>
				                       	</fo:table-cell>
				                       	<fo:table-cell>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">RETAILER </fo:block>
				                       			<fo:block keep-together="always"  white-space-treatment="preserve" white-space-collapse="false" text-align="center">SIGNATURE</fo:block>
				                       	</fo:table-cell>
					                </fo:table-row>
						            <fo:table-row>
			                          <fo:table-cell>
			                              <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                       </fo:table-row>
					              </fo:table-body>
					           </fo:table>
                    </fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
			           <fo:block font-family="Courier,monospace" font-size="6pt" >
			            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
			                          <fo:table-column column-width="100pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="50pt"/>
					            	<fo:table-column column-width="35pt"/>
					            	<fo:table-column column-width="10pt"/>
					            	<fo:table-column column-width="24pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="45pt"/>
					            	<fo:table-column column-width="55pt"/>
					            	<fo:table-column column-width="65pt"/>
					            	<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				           			<fo:table-column column-width="65pt"/>
				            	 <fo:table-body>
				            	 <fo:table-row>
			                          <fo:table-cell>
			                             <fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			                          </fo:table-cell>
			                       </fo:table-row>
        					</#if> 
			                 </#list>
			                 </fo:table-body>
			                 </fo:table>
			        </fo:block>
    </fo:flow>						        	
   </fo:page-sequence>		
</fo:root>
</#escape>