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
            margin-top="0.5in" margin-bottom="1in">
        <fo:region-body margin-top="1.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "VNDR_Daily.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 70>
<#assign facilityNumberInPage = 0>
<#assign WMBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign DTBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign TMBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign STDBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign GOLDBulk =(Static["java.math.BigDecimal"].ZERO)>
<#assign receivedAmt =(Static["java.math.BigDecimal"].ZERO)>
<#assign dues =(Static["java.math.BigDecimal"].ZERO)>
<#assign grossAmt =(Static["java.math.BigDecimal"].ZERO)>
<#assign totalQty =(Static["java.math.BigDecimal"].ZERO)>
<#assign avg =(Static["java.math.BigDecimal"].ZERO)>
<#assign DTM200 =(Static["java.math.BigDecimal"].ZERO)>
<#assign maintenance = (Static["java.math.BigDecimal"].ZERO)>
            
<#list masterList as routeWiseDetail>
	<#assign vendorAbstractReportEntries = (routeWiseDetail).entrySet()>
    <#if vendorAbstractReportEntries?has_content>
    	<#list vendorAbstractReportEntries as tempVendorAbstractReportEntrie>
        	<#assign route = (tempVendorAbstractReportEntrie).getKey()>
        	<#assign vendorAbstractReportList=tempVendorAbstractReportEntrie.getValue() >
        	<#if tempVendorAbstractReportEntrie.getKey()  !=  "grandTotals">
                  <#assign vendorAbstractFacilityWise = (vendorAbstractReportList).entrySet()>
                  <#list vendorAbstractFacilityWise as vendorAbstractFacility><#--<boothwise>-->        		 	
            			<fo:page-sequence master-reference="main" >
            				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
								<fo:block text-align="center" keep-together="always"> KRISHNAVENI  KKDMPCU  LTD                             VIJAYAWADA</fo:block>
								<fo:block text-align="center"  keep-together="always" white-space-collapse="false">Vendor Margin Report for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MM/yyyy")}</fo:block>
								<fo:block text-align="left"  keep-together="always" white-space-collapse="false">Route No: ${route}   Booth No: ${vendorAbstractFacility.getKey()}   </fo:block>
								<fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
            					<fo:block white-space-collapse="false" keep-together="always">DATE          WHM    GOLD     TM500      STD500    DTM500    DTM200     WMBLK   TMBLK   DTMBLK   STDBLK    GOLDBLK     GROSS      RECD       DUE</fo:block>
            					<fo:block keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
           					</fo:static-content>                                   
            			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
               				<fo:block  font-size="10pt">
                    			<fo:table width="100%" table-layout="fixed" space-after="0.0in">
                        			<fo:table-column column-width="40pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="58pt"/>
                            		<fo:table-column column-width="63pt"/>
                            		<fo:table-column column-width="63pt"/>
                            		<fo:table-column column-width="63pt"/>
                            			<fo:table-body> 
                                        
                                        	<#assign boothWiseData = (vendorAbstractFacility).getValue()>
                                            <#if vendorAbstractFacility.getKey() != "routeTotals">
                                                <#assign facility = delegator.findOne("Facility", {"facilityId" : vendorAbstractFacility.getKey()}, true)>
                                            	<#assign vendorAbstractDayWise = (boothWiseData).entrySet()>
                                            	<#list vendorAbstractDayWise as boothDayWiseData>
                                                   <#if (boothDayWiseData.getKey() != "boothTotals") && (boothDayWiseData.getKey() != "boothRecovery")>
                                                   		<#assign eachDayData = boothDayWiseData.getValue()>
            								    		<fo:table-row>
                                                			<fo:table-cell><fo:block>${boothDayWiseData.getKey()}</fo:block></fo:table-cell>
                                              				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("18").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("20").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("14").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("16").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("12").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("11").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("19").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("15").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("13").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("17").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("21").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("totalRev")?string("##0.00")?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               		 	     <#assign grossAmt = eachDayData.get("totalRev")>
                                               			     <#assign dues = eachDayData.get("DUES")>
                            	                  			 <#assign receivedAmt = grossAmt - dues>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${receivedAmt?string("##0.00")?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">0.00</fo:block>
                                               				</fo:table-cell>
                                          				</fo:table-row>
                                          				
                                          			<#elseif (boothDayWiseData.getKey() == "boothRecovery")>	
                                          				<#assign recoveryAmt = boothDayWiseData.getValue()>
                                          			<#elseif (boothDayWiseData.getKey() == "boothTotals")>
                                          				<#assign eachDayData = boothDayWiseData.getValue()>
                                          				<fo:table-row>
                                          		 			<fo:table-cell><fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                                            			</fo:table-row>
                                          				<fo:table-row>
                                                			<fo:table-cell><fo:block>TOTAL:</fo:block></fo:table-cell>
                                              				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("18").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("20").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("14").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("16").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("12").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("11").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("19").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("15").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("13").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("17").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("21").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${eachDayData.get("totalRev").toEngineeringString()?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               		 	    <#assign grossAmt = eachDayData.get("totalRev")>
                                               			     <#assign dues = eachDayData.get("DUES")>
                            	                  			 <#assign receivedAmt = grossAmt - dues>
                            	                  			 <#assign maintenance = eachDayData.get("MAINTENANCE")>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${receivedAmt?string("##0.00")?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<fo:table-cell>
                                               					<fo:block text-align="right">${dues?string("##0.00")?if_exists}</fo:block>
                                               				</fo:table-cell>
                                               				<#assign netAmt = eachDayData.get("NET")>
                                          				</fo:table-row>
                                          				<fo:table-row>
                                          		 			<fo:table-cell><fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
                                            			</fo:table-row>
                                            			<fo:table-row>
                                          		 			<fo:table-cell>
                                          		 			    <fo:block>
                                          		 			        <fo:table width="100%" table-layout="fixed" space-after="0.0in">
                        			     								<fo:table-column column-width="100pt"/>
                            											<fo:table-column column-width="70pt"/>
                            											<fo:table-column column-width="120pt"/>
                            											<fo:table-column column-width="90pt"/>
                            											<fo:table-column column-width="60pt"/>
                            											    <fo:table-body> 
                            											        <fo:table-row>
                                          			    							<fo:table-cell> <fo:block text-align="left">TOTAL QTY:</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="right">${eachDayData.get("TOTAL").toEngineeringString()?if_exists}</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="right">OPENING BAL:</fo:block> </fo:table-cell>
                                          			 		   						<fo:table-cell> <fo:block text-align="right">0.00</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                           		 	 							</fo:table-row>
                                            		 							<fo:table-row>
                                          			    							<fo:table-cell> <fo:block text-align="left">AVG.PER DAY:</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="right">${eachDayData.get("AVG")?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="right">GROSS AMT:</fo:block> </fo:table-cell>
                                          			 								<fo:table-cell> <fo:block text-align="right">${grossAmt?string("##0.00")}</fo:block> </fo:table-cell>
                                          									 		<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                          		  							 	</fo:table-row>
                                          		  	 							<fo:table-row>
                                          			 							   	<fo:table-cell> <fo:block text-align="left">TOTAL BULK:</fo:block> </fo:table-cell>
                                          									 		<fo:table-cell> <fo:block text-align="right">${(((eachDayData.get("19")) + (eachDayData.get("15")) + (eachDayData.get("13"))))?string("##0.0")}</fo:block> </fo:table-cell>
                                          									 		<fo:table-cell> <fo:block text-align="right">RECEIPTS:</fo:block> </fo:table-cell>
                                          									 		<fo:table-cell> <fo:block text-align="right">${receivedAmt?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                          									 		<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                            								 	</fo:table-row>
                                            							 	<fo:table-row>
                                          							 		   	<fo:table-cell> <fo:block text-align="left">TOTAL DTM200:</fo:block> </fo:table-cell>
                                          								 		<fo:table-cell> <fo:block text-align="right">${eachDayData.get("11").toEngineeringString()?if_exists}</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">MAINTENANCE:</fo:block> </fo:table-cell>
                                         					 		 			<fo:table-cell> <fo:block text-align="right">${maintenance?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                         					 		 			<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                        					    	 		</fo:table-row>
                                            						 		<fo:table-row>
                                          							  		  	<fo:table-cell> <fo:block text-align="left">TOT.QTY SOLD:</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">${eachDayData.get("TOTAL").toEngineeringString()?if_exists}</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">DUE AMT:</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">${dues?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                            						 		</fo:table-row>
                                            							 	<fo:table-row>
                                          								    	<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                          								 		<fo:table-cell> <fo:block text-align="right"></fo:block> </fo:table-cell>
                                          								 		<fo:table-cell> <fo:block text-align="right">RECOVERY:</fo:block> </fo:table-cell>
                                          								 		<fo:table-cell> <fo:block text-align="right">${recoveryAmt?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                            						 		</fo:table-row>
                                            	 							<fo:table-row>
                                          								    	<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right"></fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">NET PAYMENT:</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="right">${netAmt?string("##0.00")?if_exists}</fo:block> </fo:table-cell>
                                          							 			<fo:table-cell> <fo:block text-align="left"></fo:block> </fo:table-cell>
                                            						 		</fo:table-row>
                            											</fo:table-body>
                         			 								 </fo:table>
                                          		 			    </fo:block>
                                          		 			</fo:table-cell>
                                            			</fo:table-row>
                                            	 		<fo:table-row>
                                            	 			<fo:table-cell>
												       			<fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
            								     			</fo:table-cell>
                                            			</fo:table-row>
                                            			<fo:table-row>
                                            	 			<fo:table-cell>
                                            	   				<#assign lineNumber = 5>
           														<#assign facilityNumberInPage = 0>           					          
           			 											<fo:block  font-size="8pt" break-after="page"> </fo:block>
           			 										</fo:table-cell>
           			 									</fo:table-row>
                                          		   </#if>
            								  </#list> 
                                    	 <#else>
                                            <fo:table-row>
                                               <fo:table-cell>
                                               	  <fo:block>RouteTot:</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("18").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("20").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("14").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("16").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("12").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("11").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("19").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("15").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("13").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${boothWiseData.get("17").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               		<fo:block text-align="right">${boothWiseData.get("21").toEngineeringString()?if_exists}</fo:block>
                                               	</fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("totalRev").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${((boothWiseData.get("totalRev"))-(boothWiseData.get("DUES")))?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${boothWiseData.get("DUES").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                            </fo:table-row>
                                         	<fo:table-row>
                                              	<fo:table-cell><fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
             							    </fo:table-row>
                                            <fo:table-row>
                                          	  <fo:table-cell>
													<fo:block  keep-together="always" white-space-collapse="false">.                                     ${uiLabelMap.ApDairyMsg}</fo:block>
													<fo:block keep-together="always" white-space-collapse="false">.	                                   Abstract of Vendor Margin for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MM/yyyy")}</fo:block>
													<fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
            										<fo:block white-space-collapse="false" keep-together="always">DATE          WHM     GOLD       T500       STD       D500      D200      WMBLK    TMBLK    DTMBLK    STDBLK     GOLDBLK    GROSS      RECD     DUE</fo:block>
            										<fo:block keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            								  </fo:table-cell>
            							   </fo:table-row>
                                      </#if>
                                    	</fo:table-body>
                         			 </fo:table> 
                      			</fo:block>
                			</fo:flow>
           				</fo:page-sequence>
                                  </#list>
                               <#else>
                               <fo:page-sequence master-reference="main" >
            						<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
										<fo:block text-align="center" keep-together="always"> KRISHNAVENI  KKDMPCU  LTD                             VIJAYAWADA</fo:block>
										<fo:block text-align="center"  keep-together="always" white-space-collapse="false">Abstract of Vendor Margin for the month of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MM/yyyy")}</fo:block>
										<fo:block text-align="left"  keep-together="always" white-space-collapse="false">Route No: ${route}   Booth No:   </fo:block>
										<fo:block keep-together="always" >------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	   	
            							<fo:block white-space-collapse="false" keep-together="always">DATE          WHM     GOLD       T500       STD       D500      D200      WMBLK    TMBLK    DTMBLK    STDBLK     GOLDBLK    GROSS      RECD     DUE</fo:block>
            							<fo:block keep-together="always" white-space-collapse="false">------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
           							</fo:static-content>                                   
            					<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
               						<fo:block  font-size="10pt">
                    					<fo:table width="100%" table-layout="fixed" space-after="0.0in">
                        					<fo:table-column column-width="40pt"/>
                          				    <fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                           					<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                           					<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            				<fo:table-column column-width="65pt"/>
                            			<fo:table-body> 
                                    	  <#assign grandTotalsList = tempVendorAbstractReportEntrie.getValue()>
                                           <fo:table-row>
                                               <fo:table-cell>
                                               	<fo:block>GRND TOT</fo:block>
                                               </fo:table-cell>
                                               
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("18").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("20").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("14").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("16").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("12").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	 <fo:block text-align="right">${vendorAbstractReportList.get("11").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	 <fo:block text-align="right">${vendorAbstractReportList.get("19").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	 <fo:block text-align="right">${vendorAbstractReportList.get("15").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	 <fo:block text-align="right">${vendorAbstractReportList.get("13").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${vendorAbstractReportList.get("17").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	  <fo:block text-align="right">${vendorAbstractReportList.get("21").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("totalRev").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${((vendorAbstractReportList.get("totalRev"))-(vendorAbstractReportList.get("DUES")))?if_exists}</fo:block>
                                               </fo:table-cell>
                                               <fo:table-cell>
                                               	<fo:block text-align="right">${vendorAbstractReportList.get("DUES").toEngineeringString()?if_exists}</fo:block>
                                               </fo:table-cell>
                                          </fo:table-row>
                                          </fo:table-body>
                          				</fo:table> 
                     				 </fo:block>
                					</fo:flow>
           						</fo:page-sequence>
                          	 </#if>
         				</#list>    
      				 </#if>
  				</#list> 
		</fo:root>
</#escape>