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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "csremit.txt")}
  <fo:page-sequence master-reference="main">
  		<fo:static-content flow-name="xsl-region-before">
				<fo:block text-align="left" white-space-collapse="false">.        ${uiLabelMap.ApDairyMsg}</fo:block>
  				<fo:block text-align="left" white-space-collapse="false">STATEMENT OF BOOTH WISE CASH REALISATION OF LMS ON: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pmShipDate, "dd/MM/yyyy")} E &amp; ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(estimatedDeliveryDateTime, "dd/MM/yyyy")} M</fo:block>
 				<fo:block>---------------------------------------------------------------------------------</fo:block>
 				<fo:block text-align="left" keep-together="always" white-space-collapse="false">RtNo     BOOTH      BOOTH NAME          Cash Value</fo:block>
 				<fo:block>---------------------------------------------------------------------------------</fo:block>
 		</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <#assign totalAmount=0>
      <#if duesReportList?has_content >
      	<#list duesReportList as duesReport>
      	<#if (duesReport.facilityTypeId == "ZONE")>
      		<fo:block   break-after="page">
       <#else>	
      		<fo:block font-size="10pt">
      	</#if>	
		<fo:table width="100%" table-layout="fixed">
		   	 <fo:table-column column-width="40pt"/>
		     <fo:table-column column-width="95pt"/>
		     <fo:table-column column-width="120pt"/>
		     <fo:table-column column-width="140pt"/>
		     <fo:table-column column-width="60pt"/>
		     <fo:table-column column-width="40pt"/>
             <fo:table-body>
                <fo:table-row>
                   <fo:table-cell>
                        <fo:block text-align="left"><#if duesReport.routeNo?has_content>${duesReport.routeNo?if_exists}</#if></fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                   		 <fo:block text-align="left"><#if duesReport.originFacilityId?has_content>${duesReport.originFacilityId}</#if></fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                   		<#if duesReport.facilityTypeId =="ROUTE">
                        	<fo:block keep-together="always">Route Total :</fo:block>
                        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                        <#elseif duesReport.facilityTypeId =="ZONE">
                        	<#assign totalAmount=totalAmount+(duesReport.grandTotal)>
                        	<fo:block keep-together="always">Zone Total :</fo:block>
                        <#else>	
                    		<fo:block text-align="left" keep-together="always"><#if duesReport.facilityName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(duesReport.facilityName)),25)}</#if></fo:block>
                   		</#if>
                   </fo:table-cell>
                   <fo:table-cell>
                   			<fo:block text-align="right"><#if duesReport.grandTotal?has_content>${duesReport.grandTotal}.00 </#if></fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                   	<#if (duesReport.facilityTypeId !="ROUTE")>
                        <fo:block text-align="right">[  ]</fo:block>
                    </#if>
                   </fo:table-cell>
               </fo:table-row>
            </fo:table-body>
        </fo:table> 
     </fo:block>
     </#list>
   </#if>
   <fo:block white-space-collapse="false">.                          FINAL TOTAL AMOUNT :       ${totalAmount}.00</fo:block>
    </fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>
