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
     	<fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
            margin-top=".3in" margin-bottom=".3in" margin-left=".2in" margin-right=".1in">
        <fo:region-body margin-top="0.01in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
        </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "smpRegsterReport.pdf")}
  <#if smpRegsterMap?has_content>
      <fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     		<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        	 <fo:block white-space-collapse="false" font-weight="bold" text-align="right" keep-together="always" font-size = "10pt">DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy     HH:mm")}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >${uiLabelMap.KMFDairyHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >${uiLabelMap.KMFDairySubHeader}</fo:block>
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >----------------------------------------------------------------</fo:block>
        	    <#if shiftId?has_content>
	  				<#assign workShiftTypes = delegator.findOne("WorkShiftType", {"shiftTypeId" : shiftId}, true)>
				</#if>
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >&#160;&#160;SMP ISSUE REGISTER FOR MPU FLOOR ON DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")}   <#if shiftId?has_content> ${workShiftTypes.description} </#if></fo:block>
        	    <fo:block white-space-collapse="false"  text-align="center" keep-together="always" >&#160;</fo:block>
        		<fo:block>
        		   <fo:table  >
       					 <fo:table-column column-width="50pt"/>
       					 <fo:table-column column-width="90pt"/>
       					 <fo:table-column column-width="100pt"/>
       					 
       					 <fo:table-body>
       					    <fo:table-row  border-style="solid">
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">SL NO</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">SILO</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">RECD QUANTITY</fo:block>
       					     </fo:table-cell>
       					    </fo:table-row>
       					    <#assign slNo=1>
       					    <#assign smpRegsterList=smpRegsterMap.entrySet()>
  							 <#list smpRegsterList as smpRegster>
  							<fo:table-row >
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">${slNo}</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">${smpRegster.getKey()?if_exists}</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">${smpRegster.getValue()?if_exists?string("##0.00")}</fo:block>
       					     </fo:table-cell>
       					   </fo:table-row>
       					    <#assign slNo=slNo+1>
       					    </#list>
       					   <fo:table-row border-style="solid">
       					      <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold"></fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell >
       					      <fo:block keep-together="always" text-align="left" font-size = "12pt" font-weight="bold">Total</fo:block>
       					     </fo:table-cell>
       					     <fo:table-cell border-style="dotted">
       					      <fo:block keep-together="always" text-align="right" font-size = "12pt" font-weight="bold">${smpTotQty?if_exists?string("##0.00")}</fo:block>
       					     </fo:table-cell>
       					   </fo:table-row>
       					    
				       	</fo:table-body>
       				</fo:table>
       			</fo:block>
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >&#160;&#160;</fo:block>
        	    <fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always" >&#160;&#160;</fo:block>
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