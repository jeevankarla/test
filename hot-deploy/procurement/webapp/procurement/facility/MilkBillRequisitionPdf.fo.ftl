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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        margin-top="0.2in" margin-bottom="0.3in" margin-left=".2in" margin-right=".2in">
          <fo:region-body margin-top=".8in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "milkBillRequisition.pdf")}
    <#assign unitTotalEntries = unitTotalsMap.entrySet()>
    <#assign shedDetails = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
  <fo:page-sequence master-reference="main">
  		<fo:static-content flow-name="xsl-region-before">
			<fo:block text-align="center" keep-together="always" font-size="12pt" white-space-collapse="false">STATEMENT SHOWING THE REQUISITION OF MILK FUNDS FOR THE ${shedDetails.facilityName?if_exists}   FORNIGHT OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
 		</fo:static-content>
	      <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	      	<fo:block>
				<fo:table width="100%" table-layout="fixed" font-size="9pt">		   	 
			    	<fo:table-column column-width="50pt"/>
			     	<fo:table-column column-width="100pt"/>
			     	<fo:table-column column-width="100pt"/>
			     	<fo:table-header>
			     		<fo:table-cell>
	                        	<fo:block>
	                        		<fo:table>
		           						 <fo:table-column column-width="20pt"/>
									     <fo:table-column column-width="120pt"/>
									     <fo:table-column column-width="380pt"/>
									     <fo:table-column column-width="380pt"/>
									     <fo:table-column column-width="140pt"/>
									     <fo:table-body>
								                <fo:table-row font-size="9pt">
		                        					<fo:table-cell border-style="solid" >
								                        	<fo:block text-align="center" ></fo:block>
								                   </fo:table-cell> 
								                   <fo:table-cell border-style="solid">
								                        	<fo:block text-align="center"> </fo:block>
								                   </fo:table-cell> 
								                   <fo:table-cell border-style="solid">
								                        	<fo:block text-align="center">BUFFALOE MILK</fo:block>
								                   </fo:table-cell> 
								                   <fo:table-cell border-style="solid">
								                        	<fo:block text-align="center">COW MILK</fo:block>
								                   </fo:table-cell> 
								                   <fo:table-cell border-style="solid">
								                        	<fo:block text-align="center">TOTAL</fo:block>
								                   </fo:table-cell> 
		                        				</fo:table-row>
		                        		</fo:table-body>
		                        	</fo:table>
	                        	</fo:block>
	                   </fo:table-cell>
			     	</fo:table-header>
			     	
	             	<fo:table-body>
	                	<fo:table-row>
	                		<fo:table-cell>
	                        	<fo:block>
	                        		<fo:table>
		           						 <fo:table-column column-width="20pt"/>
									     <fo:table-column column-width="120pt"/>		     
									     <fo:table-column column-width="43pt"/>
									     <fo:table-column column-width="43pt"/>
									     <fo:table-column column-width="43pt"/>
									     <fo:table-column column-width="25pt"/><!--fat-->
									     <fo:table-column column-width="44pt"/>
									     <fo:table-column column-width="25pt"/>
									     <fo:table-column column-width="50pt"/>
									     <fo:table-column column-width="50pt"/>
									     <fo:table-column column-width="58pt"/>    
										 <fo:table-column column-width="42.2pt"/>
									     <fo:table-column column-width="42.2pt"/>
									     <fo:table-column column-width="42.2pt"/>
									     <fo:table-column column-width="25pt"/>
									     <fo:table-column column-width="44pt"/>
									     <fo:table-column column-width="25pt"/>
									     <fo:table-column column-width="50pt"/>
									     <fo:table-column column-width="50pt"/>
									     <fo:table-column column-width="58pt"/>	     
									     <fo:table-column column-width="70pt"/>
									     <fo:table-column column-width="70pt"/>
									     <fo:table-body>				     
											<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SNO</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">PARTICULARS</fo:block>
							                   </fo:table-cell> 
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">QTY IN LTRS</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">FAT IN KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">FAT %</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SNF IN KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SNF%</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">AMOUNT</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">DEDUCTIONS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">NET AMOUNT</fo:block>
							                     </fo:table-cell>
							                    <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">QTY IN LTRS</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">FAT IN KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">FAT %</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SNF IN KGS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SNF%</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">AMOUNT</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">DEDUCTIONS</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">NET AMOUNT</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">QTY</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">AMOUNT</fo:block>
							                     </fo:table-cell>								                     
		                    				</fo:table-row>						     
							                <fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">1</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">Milk Procured</fo:block>
							                   </fo:table-cell> 
							                   <#assign totBm = requisitionTotMap.get("BM")>
							                   <#assign bmLtrs=(totBm.get("QTY IN LTS"))>
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${bmLtrs?if_exists}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("QTY IN KGS")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("QTY KGFAT")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("BULK FAT(%)")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("QTY KGSNF")?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("BULK SNF(%)")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("MILK VALUE")}</fo:block>
							                     </fo:table-cell>
							                     <#assign bmSnfDedValue= Static["java.lang.Math"].round(totBm.get("SNF DED VALUE"))>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${bmSnfDedValue?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <#assign bmNetAmt=(totBm.get("MILK VALUE")+bmSnfDedValue)>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${bmNetAmt?if_exists}</fo:block>
							                     </fo:table-cell>
							                     
							                     
							                     <#assign totCm = requisitionTotMap.get("CM")>
							                     <#assign cmLtrs=(totCm.get("QTY IN LTS"))>
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${cmLtrs?if_exists}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("QTY IN KGS")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("QTY KGFAT")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("BULK FAT(%)")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("QTY KGSNF")?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("BULK SNF(%)")?if_exists?string("##0.00")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("MILK VALUE")}</fo:block>
							                     </fo:table-cell>
							                     <#assign cmSnfDedValue= Static["java.lang.Math"].round(totCm.get("SNF DED VALUE"))>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${cmSnfDedValue?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <#assign cmNetAmt=(totCm.get("MILK VALUE")+cmSnfDedValue)>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${cmNetAmt?if_exists}</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(bmLtrs+cmLtrs)}</fo:block>
							                     </fo:table-cell>				
							                     <#assign totNetAmt=Static["java.lang.Math"].round(bmNetAmt+cmNetAmt)>				                     
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totNetAmt?if_exists}</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    				 <fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SOUR Milk PTC</fo:block>
							                   </fo:table-cell> 
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sourPtcQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sourPtcQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     
							                     
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sourPtcQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sourPtcQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    			
		                    				<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">2</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">SOUR Milk Procured</fo:block>
							                   </fo:table-cell> 
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sQtyKgFat")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("sFat")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     
							                     
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sQtyKgFat")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("sFat")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    				<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">Curld Milk PTC</fo:block>
							                   </fo:table-cell> 
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("curdPtcQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("curdPtcQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     
							                     
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("curdPtcQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("curdPtcQtyKgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(totBm.get("curdPtcQtyLtrs")+totCm.get("curdPtcQtyLtrs"))}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    				 <fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">3</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">CURLD Milk Procured</fo:block>
							                   </fo:table-cell> 
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("cQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("cQtykgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     
							                     
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("cQtyLtrs")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("cQtykgs")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(totBm.get("cQtyLtrs")+totCm.get("cQtyLtrs"))}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    				<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">4</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">GOOD Milk </fo:block>
							                   </fo:table-cell> 
							                   	<#assign goodMlkBMTotLtrs= (bmLtrs-(totBm.get("cQtyLtrs")+totBm.get("curdPtcQtyLtrs")+totBm.get("sQtyLtrs")+totBm.get("sourPtcQtyLtrs")))>
							                   	<#assign goodMlkBMTotKgs= (totBm.get("QTY IN KGS")-(totBm.get("cQtykgs")+totBm.get("curdPtcQtyKgs")+totBm.get("sQtyKgs")+totBm.get("sourPtcQtyKgs")))>
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${goodMlkBMTotLtrs?if_exists}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${goodMlkBMTotKgs?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("QTY KGFAT")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("BULK FAT(%)")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("QTY KGSNF")?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("BULK SNF(%)")?if_exists?string("##0.00")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totBm.get("MILK VALUE")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${bmSnfDedValue?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(totBm.get("MILK VALUE")+totBm.get("SNF DED VALUE"))?if_exists?string("##0.00")}</fo:block>
							                     </fo:table-cell>								                     
							                     
							                     <#assign totCm = requisitionTotMap.get("CM")>
							                     <#assign goodMlkCMTotLtrs= (cmLtrs-(totCm.get("cQtyLtrs")+totCm.get("curdPtcQtyLtrs")+totCm.get("sQtyLtrs")+totCm.get("sourPtcQtyLtrs")))>
							                     <#assign goodMlkCMTotKgs= (totCm.get("QTY IN KGS")-(totCm.get("cQtykgs")+totCm.get("curdPtcQtyKgs")+totCm.get("sQtyKgs")+totCm.get("sourPtcQtyKgs")))>
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${goodMlkCMTotLtrs?if_exists}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${goodMlkCMTotKgs?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("QTY KGFAT")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("BULK FAT(%)")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("QTY KGSNF")?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("BULK SNF(%)")?if_exists?string("##0.00")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totCm.get("MILK VALUE")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${cmSnfDedValue?if_exists}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(totCm.get("MILK VALUE")+totCm.get("SNF DED VALUE"))?if_exists?string("##0.00")}</fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${(goodMlkBMTotLtrs+goodMlkCMTotLtrs)}</fo:block>
							                     </fo:table-cell>				
							                     <#assign totNetAmt=Static["java.lang.Math"].round(bmNetAmt+cmNetAmt)>				                     
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totNetAmt?if_exists}</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                    				<fo:table-row>
		                    					<fo:table-cell>
		                    						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                    					</fo:table-cell>
		                    				</fo:table-row>
		                    				<#assign obQty = shedWiseTotMap.get("OpeningBalace").get("qty")>
											<#assign obQtyLtr = shedWiseTotMap.get("OpeningBalace").get("quantityLtrs")>
											<#assign obKgFat = shedWiseTotMap.get("OpeningBalace").get("kgFat")>
											<#assign obKgSnf = shedWiseTotMap.get("OpeningBalace").get("kgSnf")>
		                    				<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">5</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">Opening Balance</fo:block>
							                   </fo:table-cell> 
							                  
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${obQtyLtr?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${obQty?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${obKgFat?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if obQty !=0>${((obKgFat*100)/obQty)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${obKgSnf?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if obQty !=0>${((obKgSnf*100)/obQty)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>								                     
							                     
							                    
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${obQty?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
		                					<#assign totInputLtr = 0>
											<#assign totInputQty = 0>
											<#assign totInputKgFat = 0>
											<#assign totInputKgSnf = 0>
											<#assign totInputLtr = totInputLtr+obQtyLtr>
											<#assign totInputQty = totInputQty+obQty>
											<#assign totInputKgFat = totInputKgFat+obKgFat>
											<#assign totInputKgSnf = totInputKgSnf+obKgSnf>
											
											<#if inputEntriesMap?has_content>
											<#assign inputEntries =inputEntriesMap.entrySet()>
												<#list inputEntries as inputEntry>
													<#assign key = inputEntry.getKey()>
														<#assign value= inputEntry.getValue()>
														<#if (value.get("qtyKgs"))!=0>
															<#assign totInputLtr = totInputLtr+value.get("qtyLtrs")>
															<#assign totInputQty = totInputQty+value.get("qtyKgs")>
															<#assign totInputKgFat = totInputKgFat+value.get("kgFat")>
															<#assign totInputKgSnf = totInputKgSnf+value.get("kgSnf")>
															
															<fo:table-row font-size="9pt">
											                	<fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">6</fo:block>
											                   </fo:table-cell> 
											                   <fo:table-cell border-style="solid">
											                        	<fo:block text-align="center">${key.replace("_"," ")}</fo:block>
											                   </fo:table-cell> 
											                  
					                        					 <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyLtrs")?string("##0.0")}</fo:block>
											                     </fo:table-cell> 
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyKgs")?string("##0.0")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("kgFat")?string("##0.000")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("fat")?string("##0.00")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("kgSnf")?string("##0.000")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("snf")?string("##0.00")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>								                     
											                     
											                    
					                        					 <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell> 
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyLtrs")?string("##0.0")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">0</fo:block>
											                     </fo:table-cell>
					                        				</fo:table-row>
														</#if>
												</#list>									
											</#if>
											<#assign iutQtyKgs =0>
											<#assign iutQtyLtrs =0>
											<#assign iutKgFat =0>
											<#assign iutKgSnf =0>
											<#if IutTotalsMap?has_content>
												<#assign iutQtyKgs = IutTotalsMap.get("iutQty")>
												<#assign iutQtyLtrs =  ((IutTotalsMap.get("iutQtyLtrs")))>
												<#assign iutKgFat = (IutTotalsMap.get("iutKgFat"))>
												<#assign iutKgSnf = (IutTotalsMap.get("iutKgSnf"))>
											</#if>		
										<#if iutQtyKgs !=0>	
											<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">7</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">IUT RECIEVED(sheds)</fo:block>
							                   </fo:table-cell> 
							                  
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${iutQtyLtrs?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${iutQtyKgs?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${iutKgFat?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgFat*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${iutKgSnf?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if iutQtyKgs !=0>${((iutKgSnf*100)/iutQtyKgs)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>								                     
							                     
							                    
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                      <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${iutQtyLtrs?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
										</#if>
										<#if shedTotals?has_content>	
				                        	<#assign shedTranferTotals = shedTotals.get("periodTransferTotalsMap")>
												<#assign shedTransfers = shedTranferTotals.entrySet()>	
											<#list shedTransfers as shedTransferValues>		
											<#assign shedProcurementTotals = shedTransferValues.getValue().get("transfers").get("procurementPeriodTotals").get("dayTotals")>			
												<#assign TotQtyLtrs =0>
												<#assign TotQtyKgs = 0>
												<#assign TotKgFat = 0>
												<#assign TotKgSnf = 0>
												<#assign TotFat = 0>
												<#assign TotSnf = 0>	
											<#if shedProcurementTotals?has_content>			
												<#assign procumentTotals = shedProcurementTotals.get("TOT")>	
												<#if procumentTotals?has_content>
													<#assign TotQtyLtrs = (procumentTotals.get("qtyLtrs"))+(procumentTotals.get("sQtyLtrs"))>
													<#assign TotQtyKgs = (procumentTotals.get("qtyKgs")) +(procumentTotals.get("sQtyLtrs")*1.03)>
													<#assign TotKgFat = (procumentTotals.get("kgFat"))>
													<#assign TotKgSnf = (procumentTotals.get("kgSnf"))>	
													<#assign TotFat = (procumentTotals.get("fat"))>
													<#assign TotSnf = (procumentTotals.get("snf"))>								
												</#if>
											</#if>		
											<#assign totInputLtr = (totInputLtr+iutQtyLtrs+TotQtyLtrs)>
											<#assign totInputQty = (totInputQty+iutQtyKgs+TotQtyKgs)>
											<#assign totInputKgFat = (totInputKgFat+iutKgFat+TotKgFat)>
											<#assign totInputKgSnf = (totInputKgSnf+iutKgSnf+TotKgSnf)>
										</#list>	
											
											<fo:table-row font-size="9pt">
							                	<fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">8</fo:block>
							                   </fo:table-cell> 
							                   <fo:table-cell border-style="solid">
							                        	<fo:block text-align="center">Total good Milk Available</fo:block>
							                   </fo:table-cell> 
							                  
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totInputLtr?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totInputQty?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totInputKgFat?if_exists?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if totInputQty !=0>${((totInputKgFat*100)/totInputQty)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totInputKgSnf?string("##0.000")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"><#if totInputQty !=0>${((totInputKgSnf*100)/totInputQty)?string("##0.00")}<#else>0.00</#if></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>								                     
							                     
							                    
		                    					 <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell> 
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right"></fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">${totInputLtr?if_exists?string("##0.0")}</fo:block>
							                     </fo:table-cell>
							                     <fo:table-cell border-style="solid">
							                        	<fo:block text-align="right">0</fo:block>
							                     </fo:table-cell>
		                    				</fo:table-row>
										</#if>		
										<#assign totOutputLtr = 0>
										<#assign totOutputQty =0>
										<#assign totOutputKgFat = 0>
										<#assign totOutputKgSnf =0>
										<#if outputEntriesMap?has_content>
											<#assign outputEntries =outputEntriesMap.entrySet()>
												<#list outputEntries as outputEntry>
													<#assign key = outputEntry.getKey()>
													
													<#if key!="CLOSING_BALANCE">
													<#assign value= outputEntry.getValue()>
														<#if (value.get("qtyKgs"))!=0>
															<#assign totOutputLtr = totOutputLtr+(value.get("qtyLtrs"))>
															<#assign totOutputQty = totOutputQty+(value.get("qtyKgs"))>
															<#assign totOutputKgFat = totOutputKgFat+(value.get("kgFat"))>
															<#assign totOutputKgSnf = totOutputKgSnf+(value.get("kgSnf"))>
															
															
															<fo:table-row font-size="9pt">
											                	<fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">9</fo:block>
											                   </fo:table-cell> 
											                   <fo:table-cell border-style="solid">
											                        	<fo:block text-align="center">${key.replace("_"," ")}</fo:block>
											                   </fo:table-cell> 
											                  
					                        					 <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
											                     </fo:table-cell> 
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyKgs")?if_exists?string("##0.0")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("kgFat")?if_exists?string("##0.000")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("fat")?if_exists?string("##0.00")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("kgSnf")?if_exists?string("##0.000")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("snf")?if_exists?string("##0.00")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>								                     
											                     
											                    
					                        					 <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell> 
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right"></fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">${value.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
											                     </fo:table-cell>
											                     <fo:table-cell border-style="solid">
											                        	<fo:block text-align="right">0</fo:block>
											                     </fo:table-cell>
					                        				</fo:table-row>
														</#if>
													</#if>
												</#list>
												<#if (closingBalanceMap.get("qtyKgs"))!=0>
													<#assign totOutputLtr = totOutputLtr+(closingBalanceMap.get("qtyLtrs"))>
													<#assign totOutputQty = totOutputQty+(closingBalanceMap.get("qtyKgs"))>
													<#assign totOutputKgFat = totOutputKgFat+(closingBalanceMap.get("kgFat"))>
													<#assign totOutputKgSnf = totOutputKgSnf+(closingBalanceMap.get("kgSnf"))>														
													<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">11</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">CLOSING BALANCE</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("qtyKgs")?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("kgFat")?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("fat")?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("kgSnf")?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("snf")?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${closingBalanceMap.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0</fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
												</#if>													
											</#if>		
												<#if shedTotals?has_content>			
		
													<#assign mpfRecQty =0> 
													<#assign mpfRecLtrs =0>
													<#assign mpfRecKgFat =0>
													<#assign mpfRecKgSnf =0>
														<#list unitTotalEntries as unitTotalEntry> 
															<#assign unitTotalValues = unitTotalEntry.getValue().get("periodTransferTotalsMap")> 
															<#assign unitTotals= unitTotalValues.entrySet()>
															<#list unitTotals as unitTotalEntryValues>
															<#assign transferValues =unitTotalEntryValues.getValue().get("transfers")>
															<#assign outputValue = transferValues.get("output")>
															<#assign outPutEntries = transferValues.get("outputEntries")>		
															<#if outputValue?has_content>					
																<#assign output = outputValue.get("mpfReciepts")>					
																<#assign mpfRecLtrs = mpfRecLtrs + (output.get("qtyLts"))> 
																<#assign mpfRecQty = mpfRecQty + (output.get("qtyKgs"))>
																<#assign mpfRecKgFat = mpfRecKgFat + (output.get("kgFat"))>
																<#assign mpfRecKgSnf = mpfRecKgSnf + (output.get("kgSnf"))>					
															</#if>		
															</#list>
														</#list>
													</#if>															
													<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">12</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Milk sent to MPF- Hyderabad</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${mpfRecLtrs?string("##0.0")}</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${mpfRecQty?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${mpfRecKgFat?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                     		<#assign mpfRcpFat=0>
									                        	<fo:block text-align="right">
									                        		<#if mpfRecQty !=0>
									                        			<#assign mpfRcpFat=((mpfRecKgFat*100)/mpfRecQty)>
									                        			${((mpfRecKgFat*100)/mpfRecQty)?string("##0.00")}
									                        		<#else>0.00</#if>
									                        	</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${mpfRecKgSnf?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                     		<#assign mpfRcpSnf=0>
									                        	<fo:block text-align="right">
									                        		<#if mpfRecQty !=0>
									                        			<#assign mpfRcpSnf=((mpfRecKgSnf*100)/mpfRecQty)>
									                        			${((mpfRecKgSnf*100)/mpfRecQty)?string("##0.00")}
									                        		<#else>0.00</#if>
									                        	</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${mpfRecLtrs?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0</fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<#assign totOutputLtr = totOutputLtr+mpfRecLtrs>
													<#assign totOutputQty = totOutputQty+mpfRecQty>
													<#assign totOutputKgFat = totOutputKgFat+mpfRecKgFat>
													<#assign totOutputKgSnf = totOutputKgSnf+mpfRecKgSnf>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">13</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Milk total Output</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${totOutputLtr?string("##0.0")}</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${totOutputQty?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${totOutputKgFat?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"><#if totOutputQty !=0>${((totOutputKgFat*100)/totOutputQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${totOutputKgSnf?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"><#if totOutputKgSnf !=0>${((totOutputKgSnf*100)/totOutputQty)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>							
													<#assign avgOutPutFat =0>
													<#assign avgInPutFat =0>
													<#assign avgOutPutSnf =0>
													<#assign avgInPutSnf =0>
													<#if (totOutputQty) !=0>
														<#assign avgOutPutFat =((totOutputKgFat*100)/totOutputQty)>
														<#assign avgOutPutSnf =((totOutputKgSnf*100)/totOutputQty)>
													</#if>
													<#if (totInputQty) !=0>
														<#assign avgInPutFat =((totInputKgFat*100)/totInputQty)>
														<#assign avgInPutSnf =((totInputKgSnf*100)/totInputQty)>
													</#if>
													
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">14</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Difference</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(totOutputLtr-totInputLtr)?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(totOutputQty-totInputQty)?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(totOutputKgFat-totInputKgFat)?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(avgOutPutFat-avgInPutFat)?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(totOutputKgSnf-totInputKgSnf)?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(avgOutPutSnf-avgInPutSnf)?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">15</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">MPF ACKNOWLEDGMENT</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRecLtrs+totOutputLtr-totInputLtr)?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRecQty+totOutputQty-totInputQty)?if_exists?string("##0.0")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRecKgFat+totOutputKgFat-totInputKgFat)?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRcpFat+avgOutPutFat-avgInPutFat)?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRecKgSnf+totOutputKgSnf-totInputKgSnf)?if_exists?string("##0.000")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">${(mpfRcpSnf+avgOutPutSnf-avgInPutSnf)?if_exists?string("##0.00")}</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">16</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">CLBAL OF CREAM</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.000</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">17</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">TM Sales</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">18</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Diet Milk</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">19</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Sour-PTC</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">20</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Sour-Producer</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">21</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Sour-Fed</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">22</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Curled-PTC</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">23</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Curled-Producer</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
			                        				<fo:table-row font-size="9pt">
									                	<fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">24</fo:block>
									                   </fo:table-cell> 
									                   <fo:table-cell border-style="solid">
									                        	<fo:block text-align="center">Curled-Fed</fo:block>
									                   </fo:table-cell> 
									                  
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.0</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right">0.00</fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>								                     
									                     
									                    
			                        					 <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell> 
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                      <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
									                     <fo:table-cell border-style="solid">
									                        	<fo:block text-align="right"></fo:block>
									                     </fo:table-cell>
			                        				</fo:table-row>
													
		                        			</fo:table-body>
		                        		</fo:table>
	                        		</fo:block>	                        			
	                        		<fo:block font-size="9pt">
	                        			<fo:table width="100%" table-layout="fixed">
	                        			 <fo:table-column column-width="40pt"/>
	                        			 <fo:table-column column-width="100pt"/>
	                        			 <fo:table-column column-width="180pt"/>
	                        			 <fo:table-column column-width="180pt"/>
	                        			 <fo:table-column column-width="180pt"/>
	                        			 <fo:table-body>
	                        			 	<fo:table-row>
	                        			 		<fo:table-cell></fo:table-cell>
	                        			 		<fo:table-cell>
	                        			 			<fo:block keep-together="always" text-align="left">ELIGIBILE  AMOUNT </fo:block>
								                	<fo:block text-align="left">
								                           <fo:table width = "170pt" border-style="solid">
								                                <fo:table-column size = "90pt"/>
								                                <fo:table-column size = "20pt"/>
								                                <fo:table-column size = "30pt"/>
								                                <fo:table-body>								                                    
							                                        <fo:table-row border-style="solid">
							                                              <fo:table-cell> 
							                                                 <fo:block text-align="left" keep-together="always">MILK AMOUNT</fo:block>
							                                              </fo:table-cell>
							                                              <fo:table-cell> 
							                                                 <fo:block text-align="right"> :</fo:block>
							                                              </fo:table-cell>
							                                              <fo:table-cell> 
							                                                 <fo:block text-align="right"> ${(totNetAmt)?if_exists?string("##0.00")}</fo:block>							                                            
							                                              </fo:table-cell>
							                                        </fo:table-row>
								                                     
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always">COMMISSION/OP-COST </fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <#--here we are displaying commission amount as op-cost -->   
								                                         <#assign opCostAmt = ((totAmountsMap.get("opCost"))+(totAmountsMap.get("commAmt")))>
								                                         
								                                         <fo:block text-align="right">${(opCostAmt)?if_exists?string("##0.00")} </fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always">CARTAGE </fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > ${((totAmountsMap.get("cartage")))?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> TOTAL ADDITIONS</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > ${((totAmountsMap.get("addnAmt")))?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    
								                                   <#assign totalAmt=(totNetAmt+totAmountsMap.get("cartage")+totAmountsMap.get("addnAmt"))>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                      </fo:table-cell>
								                                      <fo:table-cell><fo:block></fo:block> 
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" ></fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left"  keep-together="always"> TOTAL AMOUNT</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > ${totalAmt?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>								                                   
								                                </fo:table-body>
								                           </fo:table> 
								                    </fo:block>   
	                        			 		</fo:table-cell>     
	                        			 		<fo:table-cell/>             			 		
	                        			 		<fo:table-cell>
	                        			 			<fo:block keep-together="always" text-align="left">AS PER MILK BILLS AMOUNT </fo:block>
								                	<fo:block font-size="9pt">
								                            <#assign products = productsBrandMap.entrySet()>
								                           <fo:table width = "170pt" border-style="solid">
								                                <fo:table-column size = "90pt"/>
								                                <fo:table-column size = "20pt"/>
								                                <fo:table-column size = "30pt"/>
								                                <fo:table-body>
								                                     <#assign grossAmt = 0>
								                                     <#assign products = productsBrandMap.entrySet()>
								                                      <#list products as product>
								                                      	<#assign productKey = product.getKey()>
								                                        <fo:table-row border-style="solid">
								                                              <fo:table-cell> 
								                                                 <fo:block text-align="left" keep-together="always" > ${productKey} TOTAL MILK AMOUNT</fo:block>
								                                              </fo:table-cell>
								                                              <fo:table-cell> 
								                                                 <fo:block text-align="right" > :</fo:block>
								                                              </fo:table-cell>
								                                              <fo:table-cell> 
								                                                 <fo:block text-align="right"> ${(totAmountsMap.get(productKey))?if_exists?string("##0.00")}</fo:block>
								                                                 <#assign grossAmt = grossAmt+((totAmountsMap.get(productKey)))>
								                                              </fo:table-cell>
								                                        </fo:table-row>
								                                      </#list>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always" > TOTAL OP-COST </fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <#--here we are displaying commission amount as op-cost -->   
								                                         <#assign opCostAmt = ((totAmountsMap.get("opCost"))+(totAmountsMap.get("commAmt")))>
								                                         <#assign grossAmt = grossAmt+opCostAmt>
								                                         
								                                         <fo:block text-align="right">${(opCostAmt)?if_exists?string("##0.00")} </fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> TOTAL CARTAGE </fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                            <#assign grossAmt = grossAmt+((totAmountsMap.get("cartage")))>
								                                         <fo:block text-align="right"> ${((totAmountsMap.get("cartage")))?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> TOTAL ADDITIONS</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" > :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                            <#assign grossAmt = grossAmt+((totAmountsMap.get("addnAmt")))>
								                                         <fo:block text-align="right"> ${((totAmountsMap.get("addnAmt")))?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> TOTAL TIP AMOUNT</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                            <#assign grossAmt = grossAmt+((totAmountsMap.get("tipAmt")))>
								                                         <fo:block text-align="right" > ${((totAmountsMap.get("tipAmt")))?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> TOTAL DIF AMOUNT</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                            <#assign grossAmt = grossAmt+difAmt>
								                                         <fo:block text-align="right"> ${(difAmt)?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                      </fo:table-cell>
								                                      <fo:table-cell><fo:block></fo:block> 
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right" ></fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>
								                                    <fo:table-row border-style="solid">
								                                      <fo:table-cell> 
								                                         <fo:block text-align="left" keep-together="always"> Net AMOUNT</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> :</fo:block>
								                                      </fo:table-cell>
								                                      <fo:table-cell> 
								                                         <fo:block text-align="right"> ${grossAmt?if_exists?string("##0.00")}</fo:block>
								                                      </fo:table-cell>
								                                    </fo:table-row>	                                  
								                                </fo:table-body>
								                           </fo:table> 
								                    </fo:block>   
	                        			 		</fo:table-cell>
	                        			 		<fo:table-cell>
	                        			 			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        			 			<fo:block white-space-collapse="false" keep-together="always">Certified that a ${(bmLtrs+cmLtrs)?if_exists?string("#0.0")}  Qty.of milk was procured ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} In the MilkShed and an amount of</fo:block>
	                        			 			<fo:block white-space-collapse="false" keep-together="always">Rs. ${grossAmt?if_exists?string("##0.00")} payment has to be arranged. The acknowledged milk Quantity Fat &amp; SNF</fo:block>
	                        			 			<fo:block white-space-collapse="false" keep-together="always">enclosed and total sales quatity......lts during the fortnight.</fo:block>
	                        			 			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        			 			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        			 			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        			 			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
													<fo:block keep-together="always" white-space-collapse="false" text-indent="30pt">&#160;                                                                          Deputy Director(Dairy Dev.,)</fo:block>
													<fo:block keep-together="always" white-space-collapse="false" text-indent="30pt">&#160;                                	 	                                            ${shedDetails.facilityName?if_exists}</fo:block>
	                        			 		</fo:table-cell>
	                        			 	</fo:table-row>
	                        			 </fo:table-body>
	                        		</fo:table>
	                        	</fo:block>              
                   </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table> 
     </fo:block>
	</fo:flow>
 </fo:page-sequence>
 <fo:page-sequence master-reference="main">  	 	
    <fo:flow flow-name="xsl-region-body" font-size="15pt">       		
      		
    <fo:block text-aling="center" text-indent="140pt" font-weight="bold"><fo:inline font-weight="bold" text-decoration="underline" >CERTIFICATE</fo:inline></fo:block>
	<fo:block font-size="15pt">
		<fo:table>
			<fo:table-column column-width="50pt"/>
			<fo:table-column column-width="150pt"/>
			<fo:table-column column-width="150pt"/>
			<fo:table-column column-width="50pt"/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">1.Certify that closing balance and opening balance of sale remittances and milk account verified.</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">2.Certify that quantity ${(mpfRecLtrs+totOutputLtr-totInputLtr)?if_exists?string("##0.0")} lt.s of whole milk was actually despatched from the milk shed and acknowledge the</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">3.Certify that Feed amount Rs.${feedAmt?if_exists} was recovered during this fortnight.</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">4.Certify the cess amount Rs.${cessOnSaleAmt?if_exists} was recovered during this fortnight.</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">5.Certify that the quantity of ------- Its of milk sold the Rs.-------------- as remitted in the bank.</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">6.Certify the Stortage of Kg.fat and Kg.SNF under Mahabubnagar Milk Shed Rs.------------------- recovered in this bill.</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always" font-weight="bold">GROSS AMOUNT TO BE RELEASED      RS: ${grossAmt?if_exists?string("##0.00")}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">Total recoveries in this bill                   Rs.----------------</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">Net Amount Payable                   Rs.----------------</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">Recoveries</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">1.Feed Amount       = ${feedAmt?if_exists}/-</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">2.Cess Amount       = ${cessOnSaleAmt?if_exists}/-</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell>
						<fo:block text-align="left" keep-together="always">&#160;   Forwerded to General Manager(F&amp;A)H.O with a request to release Rs-----------------------</fo:block>
						<fo:block text-align="left" keep-together="always">(Rupees------------------------------------- towards Milk bills and cash payment for ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}  as a</fo:block>
						<fo:block keep-together="always"> request by the DY.Director(DD), ${shedDetails.facilityName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
               </fo:table>
		</fo:block>
    </fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>
