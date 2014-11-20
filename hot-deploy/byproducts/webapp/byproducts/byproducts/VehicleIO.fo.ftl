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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom=".1in" margin-left=".1in" margin-right=".3in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "salesReport.txt")}

<fo:page-sequence master-reference="main" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Courier,monospace" white-space-collapse="false">      ${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block text-align="center"  font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">        Vehicle_InTime_OutTime From :: ${effectiveDateStr?if_exists}  To:: ${thruEffectiveDateStr?if_exists}</fo:block>
                     <fo:block  keep-together="always"  text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block  font-weight="bold" font-size="12pt">Vehicle_Number	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Vehicle_Load / Truck_Sheet_Generated_Time  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Vehicle_Dispatch_Time &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Crates_Return_Time</fo:block>
            		<fo:block>-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:table border-style="solid">
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="300pt"/>
               	    <fo:table-column column-width="300pt"/>
               	    <fo:table-column column-width="300pt"/>
                    <fo:table-body>
                    <#assign vehicleInOutList = vehicleMap.entrySet()>        
                    	<#list vehicleInOutList as vehicleEach>
                     		<fo:table-row border-style="solid" align="center">	
                     				<#assign vehicleId = vehicleEach.getValue().get("vehicleNum")?if_exists>
                     				<#assign outTime = vehicleEach.getValue().get("outTime")?if_exists>
                     				<#assign dispatchTime =vehicleEach.getValue().get("dispatchedTime")?if_exists>
                     				<#assign returnTime = vehicleEach.getValue().get("returnTime")?if_exists>
                     				<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${vehicleId}</fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${outTime}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${dispatchTime}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
										<fo:block  text-align="right"  font-size="12pt" white-space-collapse="false">${returnTime}</fo:block> 			       					
									</fo:table-cell>
	                         </fo:table-row>	
	                      </#list>		
	                </fo:table-body>
                </fo:table>
			 </fo:flow> 
		</fo:page-sequence>	
	</fo:root>
</#escape>