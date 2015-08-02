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
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".8in" margin-right="1in">
          <fo:region-body margin-top="1.2in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "ShiftWiseReport.txt")}
  <#if ShiftWiseMap?has_content>
  <#assign ShiftWiseList=ShiftWiseMap.entrySet()>
  <#list ShiftWiseList as shift>
     <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "11pt">${uiLabelMap.KMFDairyHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "11pt">${uiLabelMap.KMFDairySubHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "11pt">----------------------------------------------------------------</fo:block>
                <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "11pt">DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy     HH:mm")}</fo:block>
                <#assign shiftType = shift.getKey()>
                <#assign shiftTime = ShiftWiseTimeMap.get(shiftType)>
                <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" font-size = "11pt">MILK RECEIVED DURING ${shiftType} SHIFT(${shiftTime})  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDate, "dd-MMM-yyyy")}</fo:block>
        	</fo:static-content>
        	<#assign ShiftWiseRecordsList = shift.getValue()>
        	      
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">&#160;&#160;MILK FROM CHILLING CENTERS AND OTHER DAIRIES</fo:block>
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block>
        		   <fo:table >
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="55pt"/>
       					 <fo:table-column column-width="105pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-body>
       					    <fo:table-row>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">CODE</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">CONV</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">DCNO</fo:block>
       					     </fo:table-cell>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">DATE</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">TIME</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">TANKERNO</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">QUANTITY AND QUALITY</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">KGFAT</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">KGSNF</fo:block>
       					     </fo:table-cell>
       					    </fo:table-row>
       					    
       					    <fo:table-row>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">-------------------------------</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					    </fo:table-row>
       					 </fo:table-body>
       				</fo:table>
       				<fo:table>
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="55pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-body>
       					    <fo:table-row>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					      <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">WEIGHT</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">FAT</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="center" font-size = "6pt" font-weight="bold">SNF</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell>
       					      <fo:block keep-together="always" text-align="right" font-size = "6pt" font-weight="bold">&#160;</fo:block>
       					     </fo:table-cell>
       					    </fo:table-row>
       					 </fo:table-body>
       				</fo:table>
       				
       			</fo:block>
       			
       			<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
        	<#assign totWeight = 0>
        	<#assign totKgFat = 0>
        	<#assign totKgSnf = 0>
        	<#list ShiftWiseRecordsList as shiftDetail>
           		<fo:block>
           			<fo:table>
       					<fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="55pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="52pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-body>

		                    		<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">${shiftDetail.get("partyId")}</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">IDR</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">${shiftDetail.get("dcNo")}</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDetail.get("receiveDate"), "dd/MM/yyyy")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shiftDetail.get("receiveDate"), "HH:mm")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">${shiftDetail.get("vehicleId")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${shiftDetail.get("receivedQuantity")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totWeight = totWeight + shiftDetail.get("receivedQuantity") >
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${shiftDetail.get("receivedFat")?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${shiftDetail.get("receivedSnf")?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${shiftDetail.get("receivedKgFat")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totKgFat = totKgFat + shiftDetail.get("receivedKgFat") >
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${shiftDetail.get("receivedKgSnf")?if_exists?string("##0.00")}</fo:block>
			                    			<#assign totKgSnf = totKgSnf + shiftDetail.get("receivedKgSnf") >
			                    		</fo:table-cell>
			                    	</fo:table-row>
			                   		

       					 </fo:table-body>
           			</fo:table>
           		</fo:block>
           		</#list>
           		    <fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           			<fo:block>
           			<fo:table>
       					<fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="5pt"/>
       					 <fo:table-column column-width="30pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="55pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="50pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-body>

		                    		<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">TOTAL :</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totWeight?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
			                   		

       					 </fo:table-body>
           			</fo:table>
           		</fo:block>
           		<fo:block white-space-collapse="false" font-weight="bold" text-align="left" keep-together="always" font-size = "10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
           		 <fo:block font-size = "10pt" linefeed-treatment="preserve">&#xA;</fo:block> 
           		<fo:block>
           			<fo:table>
       					<fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="20pt"/>
       					 <fo:table-column column-width="25pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="90pt"/>
       					 <fo:table-column column-width="15pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="35pt"/>
       					 <fo:table-column column-width="45pt"/>
       					 <fo:table-column column-width="40pt"/>
       					 <fo:table-body>

		                    		<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">TOTAL WEIGHT IN KGS :</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totWeight?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
			                    	<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">TOTAL KG FAT :</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
			                    	
			                    	<fo:table-row>   
		                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    			<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>                  
			                    	    <fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">TOTAL KG SNF :</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="center" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    		<fo:table-cell>
			                    			<fo:block text-align="right" font-size = "7pt">&#160;</fo:block>
			                    		</fo:table-cell>
			                    	</fo:table-row>
			                   		

       					 </fo:table-body>
           			</fo:table>
           		</fo:block>
           		<fo:block font-size = "10pt" linefeed-treatment="preserve">&#xA;</fo:block>
           		<fo:block text-align="right">SHIFT INCHARGE(PRODUCTION)&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
           	</fo:flow>
           	         
        </fo:page-sequence>
        </#list>
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