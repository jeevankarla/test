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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ptcTankerWiseReport.pdf")}
 <#if eachcontractorMap?has_content> 

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
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold">TANKER CONTRACTOR<#if contractorId?has_content> ${contractorId}<#else>S WISE</#if> CHARGES BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}  </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
       <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
           <fo:table-column column-width="30pt"/>               
            <fo:table-column column-width="50pt"/>               
            <fo:table-column column-width="170pt"/>
            <fo:table-column column-width="80pt"/>
        <#-->    <fo:table-column column-width="50pt"/> -->
          <#list partyIds as partyId>
            <fo:table-column column-width="50pt"/>
          </#list>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="120pt"/>
            <#if recoveryTypeIds?has_content> 
            <fo:table-column column-width="80pt"/>
             <fo:table-column column-width="80pt"/>
		    <fo:table-column column-width="100pt"/>
		    </#if>
		          
       	<fo:table-body>
	         <fo:table-row border-style="dotted">
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt" >SI NO</fo:block></fo:table-cell>       		
		          <fo:table-cell  border-style="dotted"><fo:block text-align="center" font-weight="bold"  font-size="12pt">TPCD CODE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TRANSPORTERS</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TANKER NO</fo:block></fo:table-cell>       		
		       <#-->   <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">RATE PER KM</fo:block></fo:table-cell>     -->   		
		          <#list partyIds as partyId>
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">${partyId}</fo:block></fo:table-cell>       		
		          </#list>
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TOTAL TRIPS / KM</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TRANSPORTER AMOUNT</fo:block></fo:table-cell>
		          <#if recoveryTypeIds?has_content> 
			
			    <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TOTAL ADDITIONS</fo:block></fo:table-cell>
		        <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TOTAL DEDUCTIONS</fo:block></fo:table-cell>
		        <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TOTAL AMOUNT</fo:block></fo:table-cell>
		          </#if>    		
	         </fo:table-row>
	         <#assign siNo=1>
        <#assign eachcontractorDetails = eachcontractorMap.entrySet()?if_exists>											
            <#list eachcontractorDetails as eachcontractorDetail>
        	         <#assign partyId=eachcontractorDetail.getKey()>
             <fo:table-row border-style="dotted">
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"    font-size="12pt" >${siNo}</fo:block></fo:table-cell>       		
		          <fo:table-cell  border-style="dotted"><fo:block text-align="center" font-size="12pt">${eachcontractorDetail.getKey()}</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"    font-size="12pt">${partyNames.get(partyId)}</fo:block></fo:table-cell>       		
		         <fo:table-cell  border-style="dotted">
		              <fo:block text-align="left" >			   
						 <fo:table >
							  <fo:table-column column-width="80pt"/>
					         <#-->  <fo:table-column column-width="50pt"/> -->
							    <#list partyIds as partyId>
                               <fo:table-column column-width="50pt"/>
                                </#list>
                              <fo:table-column column-width="80pt"/>
                              <fo:table-column column-width="120pt"/>
                              <#if recoveryTypeIds?has_content> 
			                  <fo:table-column column-width="80pt"/>
			             	  <fo:table-column column-width="80pt"/>
			                  <fo:table-column column-width="100pt"/>
		                     </#if>
                              
		                       <fo:table-body>
		                	 <#assign eachcontractorVehiclesData = eachcontractorDetail.getValue()?if_exists>
		                     <#assign eachcontractorVehicles = eachcontractorVehiclesData.entrySet()?if_exists>											
		                 	    <#list eachcontractorVehicles as eachcontractorVehicle>
		                 
							   <fo:table-row>
							       <fo:table-cell border-style="dotted"><fo:block text-align="center"   font-size="12pt">${eachcontractorVehicle.getKey()?if_exists}</fo:block>		</fo:table-cell>
			                      <#-->  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${eachcontractorVehicle.getValue().get("vehicleRate")?if_exists}</fo:block>   </fo:table-cell> -->
			                    <#assign vehiclePartyIds = eachcontractorVehicle.getValue().get("partyIds").entrySet()?if_exists>											
			              		  <#list vehiclePartyIds as vehiclePartyId>
	                                <fo:table-cell border-style="dotted"><fo:block text-align="center"   font-size="12pt">${vehiclePartyId.getValue()}</fo:block></fo:table-cell>       		
	                               </#list>
	                            <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${eachcontractorVehicle.getValue().get("eachPartyTrips")} ,  ${eachcontractorVehicle.getValue().get("totPartyDistance")}  </fo:block>   </fo:table-cell>
	                            <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if eachcontractorVehicle.getValue().get("total")?has_content>${eachcontractorVehicle.getValue().get("total")?if_exists?string("##0.00")} </#if></fo:block></fo:table-cell>       		
			           <#assign fenalities= eachcontractorVehicle.getValue().get("fineRecoveryMap").entrySet()?if_exists>											
			              <#if fenalities?has_content>
                                   <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if eachcontractorVehicle.getValue().get("totAdditionsForVehicle")?has_content>${eachcontractorVehicle.getValue().get("totAdditionsForVehicle")?if_exists?string("##0.00")} </#if></fo:block></fo:table-cell>       		
	                               <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if eachcontractorVehicle.getValue().get("totDeductionsForVehicle")?has_content>${eachcontractorVehicle.getValue().get("totDeductionsForVehicle")?if_exists?string("##0.00")} </#if></fo:block></fo:table-cell>       		
	                               <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt"><#if eachcontractorVehicle.getValue().get("total")?has_content>		         
	                                           <#assign eachVehicleTotal = eachcontractorVehicle.getValue().get("total")+eachcontractorVehicle.getValue().get("totAdditionsForVehicle")-eachcontractorVehicle.getValue().get("totDeductionsForVehicle")> ${eachVehicleTotal?if_exists?string("##0.00")}</#if></fo:block></fo:table-cell>       		
			                 </#if> 
				                </fo:table-row>  
				                    </#list>
				               <#if totContractSubTotMap?has_content>
				                 <#assign totContractSubTotals = totContractSubTotMap.get(partyId)?if_exists>
								  <fo:table-row border-style="dotted">
  								  <fo:table-cell ><fo:block text-align="center"   font-size="12pt"></fo:block>		</fo:table-cell>
			                    <#assign vehiclePartyIds = eachcontractorVehicle.getValue().get("partyIds").entrySet()?if_exists>											
			              		  <#list vehiclePartyIds as vehiclePartyId>
	                                <fo:table-cell ><fo:block text-align="center"   font-size="12pt"></fo:block></fo:table-cell>       		
	                               </#list>							          
	                               <fo:table-cell ><fo:block text-align="center"  font-weight="bold"  font-size="12pt" > Sub Total</fo:block></fo:table-cell>       		
							          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totContractSubTotals.get('contractorAmt')?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
							        <#assign fenalities= eachcontractorVehicle.getValue().get("fineRecoveryMap").entrySet()?if_exists>											
			                        <#if fenalities?has_content>
							          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totContractSubTotals.get('contractorAdditions')?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
							          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totContractSubTotals.get('contractorDedutions')?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
							          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totContractSubTotals.get('contractorTotAmt')?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
						             </#if> 
						           </fo:table-row>
						         </#if> 
   		                            </fo:table-body>   
	 	                   </fo:table>			 
				        </fo:block>
	                 </fo:table-cell>			
	         </fo:table-row>
            
           <#assign siNo=siNo+1>
         </#list>
         
         <#assign totalsForPartiesDetails = totalsForPartiesMap.entrySet()?if_exists>
          <fo:table-row border-style="solid">
		    <fo:table-cell border-style="dotted" number-columns-spanned="4"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">TOTAL</fo:block>		</fo:table-cell>
	      <#list totalsForPartiesDetails as totalsForPartiesDetail>
	        <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">${totalsForPartiesDetail.getValue()?if_exists}</fo:block></fo:table-cell>       		
	       </#list>		                           
	        <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totalTripsParties} , ${totalDistanceParties}</fo:block></fo:table-cell>       		
	        <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totalAmtParties?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
	     <#if recoveryTypeIds?has_content>
	 	       <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totalAdditions?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
      	        <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totalDeductions?if_exists?string("##0.00")}</fo:block></fo:table-cell>       		
                  <#assign grandTotal = totalAmtParties+totalAdditions-totalDeductions>        		
   	        <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${grandTotal?if_exists?string('##0.00')?if_exists}</fo:block></fo:table-cell>       		
            </#if>
	    
	    </fo:table-row> 
	 
    	</fo:table-body>
    		</fo:table>
     </fo:block>	
     
     
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
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

