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
     	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top=".3in" margin-bottom=".3in" margin-left=".2in" margin-right=".1in">
        <fo:region-body margin-top="0.01in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
        </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "ShiftWiseReport.txt")}
  <#if ShiftWiseMap?has_content>
  <#assign ShiftWiseList=ShiftWiseMap.entrySet()>
     <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
               
        	</fo:static-content>
        	      
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        	 <fo:block white-space-collapse="false" font-weight="bold" text-align="right" keep-together="always" font-size = "10pt">DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy     HH:mm")}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >${uiLabelMap.KMFDairyHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >${uiLabelMap.KMFDairySubHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >----------------------------------------------------------------</fo:block>
             <#list ShiftWiseList as shift>
              	<#assign ShiftWiseRecordsList = shift.getValue()>
                <#assign shiftType = shift.getKey()>
                <#assign shiftTime = ShiftWiseTimeMap.get(shiftType)>
                <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >MILK RECEIVED DURING ${shiftType} SHIFT(${shiftTime})  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDate, "dd-MMM-yyyy")}</fo:block>
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >&#160;&#160;MILK FROM CHILLING CENTERS AND OTHER DAIRIES</fo:block>
        	    <fo:block white-space-collapse="false"  text-align="center" keep-together="always" >&#160;</fo:block>
        		<fo:block>
        		   <fo:table  border-style="solid">
       					 <fo:table-column column-width="65pt"/>
       					 <fo:table-column column-width="50pt"/>
       					 <fo:table-column column-width="60pt"/>
       					 <fo:table-column column-width="70pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-column column-width="70pt"/>
       					 <fo:table-column column-width="80pt"/>
       					 <fo:table-column column-width="65pt"/>
       					 <fo:table-column column-width="65pt"/>
       					 <fo:table-column column-width="65pt"/>
       					 <fo:table-column column-width="65pt"/>
       					 
       					 <fo:table-body>
       					    <fo:table-row  border-style="solid">
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">PARTY</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">PUR-CON</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">&#160;DCNO</fo:block>
       					     </fo:table-cell>
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="center" font-size = "12pt" font-weight="bold">DATE</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">TIME</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">TANKERNO</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">QUANTITY</fo:block>
       					     </fo:table-cell>
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">FAT%</fo:block>
       					     </fo:table-cell>
 							<fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">SNF%</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">KGFAT</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">KGSNF</fo:block>
       					     </fo:table-cell>
       					    </fo:table-row>
				       		<#assign totWeight = 0>
				        	<#assign totKgFat = 0>
				        	<#assign totKgSnf = 0>
				        	<#list ShiftWiseRecordsList as shiftDetail>
				        	<#assign unionName = partyReference.get(shiftDetail.get("partyId"))>
				        	<#assign idrConv = shiftDetail.get("purposeTypeId")?if_exists>
       					    <#assign enumeration = delegator.findOne("Enumeration", {"enumId" :idrConv}, true)>
       					    	<fo:table-row  border-style="dotted">   
		                    			<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt">${shiftDetail.get("partyId")}<#if unionName?has_content>[${unionName}]</#if></fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt"><#if enumeration?has_content> ${enumeration.get("enumCode")?substring(0,3)}</#if> </fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt">${shiftDetail.get("dcNo")}</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDetail.get("receiveDate"), "dd-MM-yyyy")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDetail.get("receiveDate"), "HH:mm")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="left" font-size = "11pt">${shiftDetail.get("vehicleId")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="right" font-size = "11pt">${shiftDetail.get("receivedQuantity")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totWeight = totWeight + shiftDetail.get("receivedQuantity") >
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="right" font-size = "11pt">${shiftDetail.get("receivedFat")?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="right" font-size = "11pt">${shiftDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="right" font-size = "11pt">${shiftDetail.get("receivedKgFat")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totKgFat = totKgFat + shiftDetail.get("receivedKgFat") >
			                    		</fo:table-cell>
			                    		<fo:table-cell border-style="dotted">
			                    			<fo:block text-align="right" font-size = "11pt">${shiftDetail.get("receivedKgSnf")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totKgSnf = totKgSnf + shiftDetail.get("receivedKgSnf") >
			                    		</fo:table-cell>
			                    	</fo:table-row>
       					           </#list>
       					           	<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="right" font-weight="bold" font-size = "11pt">TOTAL :</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-weight="bold" font-size = "11pt">${totWeight?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "11pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-weight="bold" font-size = "11pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-weight="bold" font-size = "11pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
       				 </fo:table-body>
       				</fo:table>
       			</fo:block>
       	 </#list>
           	</fo:flow>
           	         
        </fo:page-sequence>
        <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Records found for given shift Date
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>		
    </#if>  
  </fo:root>
</#escape>      