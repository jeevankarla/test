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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if errorMessage?exists>
	<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${errorMessage}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main" >
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<fo:block text-align="center" keep-together="always"> ${uiLabelMap.ApDairyMsg}</fo:block>
				<fo:block text-align="center"  keep-together="always" white-space-collapse="false">Abstract of Vendor Margin for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MM/yyyy")}</fo:block>
				<fo:block keep-together="always" white-space-collapse="false">--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>				    		
            	<fo:block white-space-collapse="false" keep-together="always">Sl.    Znrt      VndrId      Location          VendorName              TotQty      MthAvg           Amount        Deducted         Balance</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
					<fo:block  font-size="10pt">
            			<fo:table width="100%" table-layout="fixed">
            				<fo:table-column column-width="100%"/>
            					<fo:table-body>
			                		<fo:table-row column-width="100%">
			                			<fo:table-cell>
            				 				<fo:table width="100%" table-layout="fixed">                
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="55pt"/>
				                				<fo:table-column column-width="35pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="90pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="30pt"/>
				                				<fo:table-column column-width="30pt"/>
				                				<fo:table-column column-width="40pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="50pt"/>
				                			<fo:table-header>
			                    			<fo:table-row>                        
			                        			<fo:table-cell column-width="100%"><fo:block>--------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			                    			</fo:table-row>				                     		                     
			                			</fo:table-header>	                   
                						<fo:table-body>
                							<#assign temp=0>
                							<#if masterList?has_content>
											<#list masterList as vendorAbstractReportEntry>
											<#assign vendorAbstractReportEntries = (vendorAbstractReportEntry).entrySet()>
											<#if vendorAbstractReportEntries?has_content>
											<#list vendorAbstractReportEntries as tempVendorAbstractReportEntrie>
											<#assign vendorAbstractReportList=tempVendorAbstractReportEntrie.getValue() >
                							<#assign cashDue =0>
                							<#list vendorAbstractReportList as vendorAbstractReport>
											<#assign temp=(temp+1)>
											<#assign facilityId = vendorAbstractReport.get("facilityId")>
											<#assign totalQty=Static["java.lang.Math"].round(vendorAbstractReport.Tot.get("TOTAL"))>        									  			                      	                     	
        									<#assign facility = delegator.findOne("Facility", {"facilityId" : facilityId}, true)> 
											<#if  (totalQty != (Static["java.math.BigDecimal"].ZERO))>
											<#assign cashDue=(vendorAbstractReport.Tot.get("CASH_DUE")) />
											<#assign totalMargin=Static["java.lang.Math"].round(vendorAbstractReport.Tot.get("TOTAL_MR"))>
								            <#if totalMargin &lt; cashDue >
								            <#assign NetMargin = totalMargin - cashDue>
								            
                					        <fo:table-row width="100%">
                					        	<fo:table-cell >
								            		<fo:block text-align="left" keep-together="always">${temp?if_exists}</fo:block>
								                </fo:table-cell>
                					        	<fo:table-cell >
								            		<fo:block text-align="left" keep-together="always">${facility.parentFacilityId?if_exists}</fo:block>
								                </fo:table-cell>
								                <fo:table-cell >
								          			<fo:block text-align="left" keep-together="always">${facility.facilityId?if_exists}</fo:block>
								                </fo:table-cell>                            
                            					<fo:table-cell >
								          			<fo:block text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.get("description"))),16)}</fo:block>
								                </fo:table-cell>
								                <fo:table-cell />
								                <fo:table-cell />
								                <fo:table-cell >
								                	<fo:block text-align="left" keep-together="always" white-space-collapse="false">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, facility.ownerPartyId, false)}</fo:block>
								                </fo:table-cell>
								                <fo:table-cell />
								                <fo:table-cell >
								          			<fo:block text-align="right" keep-together="always">${vendorAbstractReport.Tot.get("TOTAL")?if_exists}</fo:block>
								                </fo:table-cell>
								                <fo:table-cell />
								                <fo:table-cell >
								          			<fo:block text-align="center" keep-together="always">${Static["java.lang.Math"].round((vendorAbstractReport.Tot.get("TOTAL")/totalDays)?if_exists)}</fo:block>
								                </fo:table-cell>
								                <fo:table-cell />
								                <fo:table-cell >
								          			<fo:block text-align="right" keep-together="always">${totalMargin?if_exists}</fo:block>
								                </fo:table-cell>
								                 <fo:table-cell >
								                 	<fo:block ></fo:block>
								          		</fo:table-cell>
								                 <fo:table-cell >
								                  	<fo:block text-align="right" keep-together="always">${cashDue?if_exists}</fo:block>
								          		</fo:table-cell>
								          		 <fo:table-cell >
								                 	<fo:block ></fo:block>
								          		</fo:table-cell>	
								          		<fo:table-cell >
								                  	<fo:block text-align="right" keep-together="always">${NetMargin?if_exists}</fo:block>
								          		</fo:table-cell>                           
                       					</fo:table-row>
                       					</#if>
                       					</#if>
                       					</#list>
                       					</#list>	
   										</#if>
   										</#list>
  										</#if>
                      				</fo:table-body>
        								</fo:table>
      	  							</fo:table-cell>
     	 						</fo:table-row>	
							</fo:table-body>
	 					</fo:table>	
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
</#if>			
 	    </fo:root>
</#escape>