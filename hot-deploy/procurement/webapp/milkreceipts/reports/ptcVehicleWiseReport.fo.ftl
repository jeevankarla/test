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
${setRequestAttribute("OUTPUT_FILENAME", "ptcVehicleWiseReport.pdf")}
 <#if ptcMap?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
		        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;         Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >PTC TANKER REPORT  </fo:block>
	<#assign allDetailsRegister = ptcMap.entrySet()>
     <#assign countPrint =0>
     <#list allDetailsRegister as allDetailsRegisterDetails>
               <#if countPrint !=0>
               		<fo:block font-size="8pt" page-break-before="always"/>
               </#if>
               <#assign countPrint = countPrint+1>
               <#assign vehicleDataMap = allDetailsRegisterDetails.getValue().get("vehicleDataMap")?if_exists>                   
               <#assign abstractPartyMap = allDetailsRegisterDetails.getValue().get("abstractPartyMap")?if_exists>  
               <#assign totPartiesMap = allDetailsRegisterDetails.getValue().get("totPartiesMap")?if_exists>                   
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">QUANTITY OF MILK RECEIVED THROUGH TANKER ${allDetailsRegisterDetails.getKey()?if_exists} BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >(Receipt Details for Procurement Transport Contract Bill)</fo:block>
     	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
      	    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > -------- -------- ------- -------- -------- -------- ------- ------- -------- ------- ------- -------- ------- ------- ------- ------- ------- ------- ------- ------ ------ ------ ------ ------ ------ -------- -------- --------</fo:block>
            <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold">&#160;&#160;DATE         PROC      DC           	     DESPATCH         RECEIVED        DIFF      DISTANCE          AMOUNT          </fo:block>
            <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold">&#160;&#160;RECEIVED     CENTER    NUMBER        	    QUANTITY         QUANTITY        QTY                                                  </fo:block>
            <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > -------- -------- ------- -------- -------- -------- ------- ------- -------- ------- ------- -------- ------- ------- ------- ------- ------- ------- ------- ------ ------ ------ ------ ------ ------ -------- -------- --------</fo:block>
       <fo:block >
		 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="90pt"/>               
            <fo:table-column column-width="60pt"/>               
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="80pt"/>
       <#-- <fo:table-column column-width="50pt"/> -->
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="100pt"/>
           	<fo:table-body>
           	<#assign vehicleDataMapDetails = vehicleDataMap.entrySet()>
            <#list vehicleDataMapDetails as vehicleDataMapDetail>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="left"  font-weight="bold" keep-together="always" font-size="12pt" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vehicleDataMapDetail.getValue().get("receiveDate"), "dd-MMM-yyyy")}</fo:block></fo:table-cell>       		
              <fo:table-cell  ><fo:block text-align="left" font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("partyId")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("dcNo")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("sendQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("receivedQuantity")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("diffQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
            <#-->  <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt"><#if vehicleDataMapDetail.getValue().get("rateAmount")?has_content>${vehicleDataMapDetail.getValue().get("rateAmount")?if_exists?string("##0.00")} <#else>0.00</#if></fo:block></fo:table-cell>   -->
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${vehicleDataMapDetail.getValue().get("partyDistance")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt"><#if vehicleDataMapDetail.getValue().get("amount")?has_content>${vehicleDataMapDetail.getValue().get("amount")?if_exists?string("##0.00")} <#else>0.00</#if></fo:block></fo:table-cell>       		
             </fo:table-row>
            </#list>
             <fo:table-row >
              <fo:table-cell ><fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > -------- -------- ------- -------- -------- -------- ------- ------- -------- ------- ------- -------- ------- ------- ------- ------- ------- ------- ------- ------ ------ ------ ------ ------ ------ -------- -------- --------</fo:block>
              </fo:table-cell>
             </fo:table-row>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="left"  font-weight="bold" keep-together="always" font-size="12pt" ></fo:block></fo:table-cell>       		
              <fo:table-cell  ><fo:block text-align="left" font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
                <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${totPartiesMap.get("totSendQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${totPartiesMap.get("totReceivedQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		    		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
             <#-->  <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell>   -->    		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${totPartiesMap.get("totAmount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		    		
             
             </fo:table-row>
    	</fo:table-body>
    		</fo:table>
     </fo:block>	
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > -------- -------- ------- -------- -------- -------- ------- ------- -------- ------- ------- -------- ------- ------- ------- ------- ------- ------- ------- ------ ------ ------ ------ ------ ------ -------- -------- --------</fo:block>
     			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
     			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
     			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
     
	   <fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >ABSTRACT </fo:block>
       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt">
            <fo:table-column column-width="120pt"/>               
            <fo:table-column column-width="120pt"/>
            <fo:table-column column-width="120pt"/>
            <fo:table-column column-width="120pt"/>
           	<fo:table-body>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt" >PROC CENTER</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt" >NO OF TRIPS</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt" >TOTAL QTY</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt" >AMOUNT</fo:block></fo:table-cell>       		
             </fo:table-row >
              <fo:table-row >
              <fo:table-cell >  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              </fo:table-cell>       		
             </fo:table-row >
            <#assign abstractPartyDetails = abstractPartyMap.entrySet()>
            <#list abstractPartyDetails as abstractPartyDetail>
             <fo:table-row >
              <fo:table-cell ><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt" >${abstractPartyDetail.getKey()}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt" >${abstractPartyDetail.getValue().get("trips")?if_exists}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt" >${abstractPartyDetail.getValue().get("tQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt" >${abstractPartyDetail.getValue().get("tAmt")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
             </fo:table-row >
            </#list>
             <fo:table-row >
              <fo:table-cell >  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              </fo:table-cell>       		
             </fo:table-row >
          <fo:table-row >
              <fo:table-cell ><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt">Grand Toatal</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="left"  font-weight="bold" keep-together="always" font-size="12pt"></fo:block></fo:table-cell> 
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${totPartiesMap.get("totReceivedQty")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
              <fo:table-cell ><fo:block text-align="right"  font-weight="bold" keep-together="always" font-size="12pt">${totPartiesMap.get("totAmount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
             </fo:table-row>
    	</fo:table-body>
    		</fo:table>
     </fo:block>	
       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
     	
     
      </#list>
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

