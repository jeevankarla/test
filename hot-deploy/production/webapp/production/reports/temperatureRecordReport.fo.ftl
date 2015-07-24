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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="1.7in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "temperatureRecordReport.pdf")}
 <#if finalMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                                ${uiLabelMap.KMFDairyHeader}             Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;                          ${uiLabelMap.KMFDairySubHeader}                   Page - <fo:page-number/> </fo:block> 
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace"  font-weight="bold"  white-space-collapse="false">TEMPERATURE RECORDED ON ${reportDate?upper_case?if_exists}</fo:block>
				<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
                    <fo:table>
                    <fo:table-column column-width="10%"/>
				    <fo:table-column column-width="14%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="20%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">SHIFT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">TIME</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">PLANT / SILO</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">TEMPERATURE</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">COMMENTS</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
             <fo:block>
             	<fo:table>
             		<fo:table-column column-width="15%"/>
			        <fo:table-column column-width="80%"/>
             		<fo:table-body>
             		<#assign shiftWiseDetails = finalMap.entrySet()>
             		<#list shiftWiseDetails as shiftWise> 
                		<fo:table-row>
                		<#assign workShiftType = delegator.findOne("WorkShiftType",{"shiftTypeId":shiftWise.getKey()},false)>                     
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${(workShiftType.description?upper_case?if_exists)}</fo:block>  
                			</fo:table-cell> 
                			<#assign tempDetails = shiftWise.getValue()>
                			<fo:table-cell>
                				<fo:block>
                					<fo:table>
                					<fo:table-column column-width="22%"/>
                					<fo:table-column column-width="25%"/>
                					<fo:table-column column-width="15%"/>
                					<fo:table-column column-width="30%"/>
                					<fo:table-body>
                					<#list tempDetails as values>
                					  <fo:table-row>
                					  	<fo:table-cell>
                					  		<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(values.recordDateTime,"HH:mm")}</fo:block>
                					  	</fo:table-cell>
                					  	<#assign facility = delegator.findOne("Facility",{"facilityId":values.facilityId},true)>
                					  	<fo:table-cell>
                					  		<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${facility.facilityName?if_exists}</fo:block>
                					  	</fo:table-cell>
                                        <fo:table-cell>
                					  		<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${values.temperature?if_exists}${"\x00B0"}C</fo:block>
                					  	</fo:table-cell>
                					  	<fo:table-cell>
                					  		<fo:block   text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">${values.comments?if_exists}</fo:block>
                					  	</fo:table-cell>
                					  </fo:table-row>
                                      <fo:table-row>
                                      	<fo:table-cell>
                                      		<fo:block text-align="left"    white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
                                      	</fo:table-cell>
                                      </fo:table-row>
                                    </#list>
                					</fo:table-body>
                					</fo:table>
                				</fo:block>
                			</fo:table-cell> 
                		</fo:table-row>
                		<fo:table-row>
                           <fo:table-cell>
                           		 <fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                           </fo:table-cell>
                        </fo:table-row>
                   </#list>
                	</fo:table-body>
             	</fo:table>
             </fo:block>
			 </fo:flow>
			 </fo:page-sequence>
	<#else>
    	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>