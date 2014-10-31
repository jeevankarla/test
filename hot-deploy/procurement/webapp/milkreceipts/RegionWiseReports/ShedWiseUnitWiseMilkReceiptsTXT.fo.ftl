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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top="0.5in">
                <fo:region-body margin-top="0.9in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "shedUnitMilkReceipts.txt")}
	<#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		      <fo:block font-size="14pt">
		              ${errorMessage}.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence>        
	<#else> 
		<#if shedWiseTotalsMap?has_content>    
			<fo:page-sequence master-reference="main">
	  			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospae">
	  				<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE FEDERATION LIMITED</fo:block>
		 			<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;LALAPET  : HYDERABAD     </fo:block>
		 			<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;M.P.F MILK RECEIPTS ON :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")} &#160;&#160;PAGE NO:<fo:page-number/></fo:block>
		            <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------</fo:block>
		            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">CODE  NAME OF MCC/DAIRY       QUANTITY      QUANTITY       TOTAL            TOTAL     &#160;&#160;&#160;	       AVG         AVG          </fo:block>
		            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="4pt">.                                LTS          KGS          KG-FAT           KG-SNF     &#160;&#160;&#160;	      FAT         SNF         </fo:block>
		            <fo:block font-size="6pt">------------------------------------------------------------------------------------------------------------------</fo:block>
	 			</fo:static-content>
	      		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			      	<fo:block  font-size="4pt">
			      		<#assign NoofLines=0>
						<fo:table>
						   	<fo:table-column column-width="11pt"/>
							<fo:table-column column-width="38pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="37pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="5pt"/>
	             			<fo:table-body>
	             				
					        	<#assign totalQtyLtrs=0>
					          	<#assign totalQtyKgs=0>
					           	<#assign totalKgFat=0>
					          	<#assign totalKgSnf=0>
		             
					           	<#assign totPrivQtyLtrs=0>
					           	<#assign totPrivQtyKgs=0>
					           	<#assign totPrivKgFat=0>
					           	<#assign totPrivKgSnf=0>
		             
					           	<#assign totOthrQtyLtrs=0>
					           	<#assign totOthrQtyKgs=0>
					           	<#assign totOthrKgFat=0>
					           	<#assign totOthrKgSnf=0>
	             
					           	<#assign shedWiseList = shedWiseTotalsMap.entrySet()>
					           	<#list shedWiseList as shedWiseDetails>
					             	<#assign unitList = shedWiseDetails.getValue().entrySet()>
					             	<#list unitList as unitDetails>
		             					<#if (unitDetails.getKey())!="TOTAL">
							                <fo:table-row>
							                	<#assign NoofLines=NoofLines+1>
							                	<#assign facility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
							                   	<fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt">${facility.mccCode}</fo:block>
							                   	</fo:table-cell>
							                   	<fo:table-cell>
							                        <fo:block text-align="left" font-size="4pt" keep-together="always">&#160;&#160;&#160;&#160;${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
							                   	</fo:table-cell>
							                    <fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt">${unitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
							                   	</fo:table-cell>
							                    <fo:table-cell>
							                        <fo:block text-align="right"  font-size="4pt">${unitDetails.getValue().get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
							                   	</fo:table-cell>
							                   	<fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt">${unitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
							                   	</fo:table-cell>
							                   	<fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt">${unitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
							                   	</fo:table-cell>
							                   	<#assign qtyKgs = unitDetails.getValue().get("qtyKgs")?if_exists>
							                   	<#assign kgFat = unitDetails.getValue().get("kgFat")?if_exists>
							                   	<fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt"><#if qtyKgs !=0>${((kgFat*100)/qtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
							                   	</fo:table-cell>
							                   	<#assign kgSnf = unitDetails.getValue().get("kgSnf")?if_exists>
							                   	<fo:table-cell>
							                        <fo:block text-align="right" font-size="4pt"><#if qtyKgs !=0>${((kgSnf*100)/qtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
							                   	</fo:table-cell>
	               							</fo:table-row>
	               							<#if (NoofLines >50)>
			   									<#assign NoofLines =0>
			   									<fo:table-row>
			       									<fo:table-cell>
			       										<fo:block page-break-after="always"></fo:block>
			       									</fo:table-cell>
			   									</fo:table-row>
											</#if>
	                					<#else>
						               	   	<#assign totQtyLtrs = unitDetails.getValue().get("totQtyLtrs")?if_exists>
						                   	<#assign totQtyKgs = unitDetails.getValue().get("totQtyKgs")?if_exists>
						                   	<#assign totKgFat = unitDetails.getValue().get("totKgFat")?if_exists>
						                   	<#assign totKgSnf = unitDetails.getValue().get("totKgSnf")?if_exists >
						                   	<#assign totalQtyLtrs = totalQtyLtrs+totQtyLtrs>
						                   	<#assign totalQtyKgs = totalQtyKgs+totQtyKgs>
						                   	<#assign totalKgFat = totalKgFat+totKgFat>
						                   	<#assign totalKgSnf = totalKgSnf+totKgSnf>
			                				<#if totQtyLtrs?has_content  && (totQtyLtrs!=0)>
			                					<fo:table-row>
			                						<#assign NoofLines=NoofLines+1>
								                   <fo:table-cell>
								                        <fo:block font-size="4pt">------------------------------------------------------------------------------------------------------------------</fo:block>
								                   </fo:table-cell>
								               	</fo:table-row>
	                							<fo:table-row>
	                								<#assign NoofLines=NoofLines+1>
			                						<#assign facility = delegator.findOne("Facility", {"facilityId" : shedWiseDetails.getKey()}, true)>
			                    					<fo:table-cell>
			                        					<fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">${facility.facilityName}</fo:block>
									                </fo:table-cell>
									                <fo:table-cell>
									               		<fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
									                </fo:table-cell>
									                <fo:table-cell>
									               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${totQtyLtrs?if_exists?string("##0.00")}</fo:block>
									                </fo:table-cell>
									                <fo:table-cell>
									               		<fo:block text-align="right"  font-size="4pt" font-weight="bold">${totQtyKgs?if_exists?string("##0.00")}</fo:block>
									              	</fo:table-cell>
									           		<fo:table-cell>
									               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${totKgFat?if_exists?string("##0.00")}</fo:block>
									              	</fo:table-cell>
									               	<fo:table-cell>
									          			<fo:block text-align="right" font-size="4pt" font-weight="bold">${totKgSnf?if_exists?string("##0.00")}</fo:block>
									        		</fo:table-cell>
									            	<fo:table-cell>
							        					<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if totQtyKgs !=0>${((totKgFat*100)/totQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
							          				</fo:table-cell>
							       					<fo:table-cell>
						        						<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if totQtyKgs !=0>${((totKgSnf*100)/totQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
								           			</fo:table-cell>
									       			<fo:table-cell>
									  					<fo:block text-align="right" font-size="4pt" font-weight="bold">*</fo:block>
									              	</fo:table-cell>
	               								</fo:table-row>
	               								<#if (NoofLines >50)>
				   									<#assign NoofLines =0>
				   									<fo:table-row>
				       									<fo:table-cell>
				       										<fo:block page-break-after="always"></fo:block>
				       									</fo:table-cell>
				   									</fo:table-row>
												</#if>
	               								<fo:table-row>
	               									<#assign NoofLines=NoofLines+1>
								                   <fo:table-cell>
								                        <fo:block font-size="4pt">------------------------------------------------------------------------------------------------------------------</fo:block>
								                   </fo:table-cell>
								               	</fo:table-row>
								               	<#if (NoofLines >50)>
				   									<#assign NoofLines =0>
				   									<fo:table-row>
				       									<fo:table-cell>
				       										<fo:block page-break-after="always"></fo:block>
				       									</fo:table-cell>
				   									</fo:table-row>
												</#if>
	               							</#if>
	               						</#if>
	               					</#list>
	               				</#list>
	               				<fo:table-row>
				               		<fo:table-cell>
				                        <fo:block text-align="right">
				                        	<fo:table>
												<fo:table-column column-width="15pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="30pt"/>
		             							<fo:table-body>
		             								<fo:table-row>
		             									<#assign NoofLines=NoofLines+1>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
									                   </fo:table-cell>
									               	</fo:table-row>
								                	<fo:table-row>
								                		<#assign NoofLines=NoofLines+1>
								                  	   <fo:table-cell>
				                        					<fo:block text-align="left" font-size="4pt" keep-together="always" font-weight="bold">FEDERN TOTAL:</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${totalQtyLtrs?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right"  font-size="4pt" font-weight="bold">${totalQtyKgs?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${totalKgFat?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${totalKgSnf?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if totalQtyKgs !=0>${((totalKgFat*100)/totalQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if totalQtyKgs !=0>${((totalKgSnf*100)/totalQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
									                   </fo:table-cell>
						               				</fo:table-row>
						               				<#if (NoofLines >50)>
					   									<#assign NoofLines =0>
					   									<fo:table-row>
					       									<fo:table-cell>
					       										<fo:block page-break-after="always"></fo:block>
					       									</fo:table-cell>
					   									</fo:table-row>
													</#if>
						               				<fo:table-row>
						               					<#assign NoofLines=NoofLines+1>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
									                   </fo:table-cell>
									               	</fo:table-row>
									               	<#if (NoofLines >50)>
					   									<#assign NoofLines =0>
					   									<fo:table-row>
					       									<fo:table-cell>
					       										<fo:block page-break-after="always"></fo:block>
					       									</fo:table-cell>
					   									</fo:table-row>
													</#if>
							            		</fo:table-body>
		           				   			</fo:table> 
		           						</fo:block>
		                   			</fo:table-cell>
	               				</fo:table-row>
				               	<fo:table-row>
	               					<fo:table-cell>
				                        <fo:block text-align="right">
				                        	<fo:table>
												<fo:table-column column-width="11pt"/>
												<fo:table-column column-width="38pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="5pt"/>
												<fo:table-body>
													<#assign uniontotalQtyLtrs=0>
										          	<#assign uniontotalQtyKgs=0>
										           	<#assign uniontotalKgFat=0>
										          	<#assign uniontotalKgSnf=0>
							             
													<#assign unionshedWiseList = unionShedWiseTotalsMap.entrySet()>
										           	<#list unionshedWiseList as unionshedWiseDetails>
										             	<#assign unionunitList = unionshedWiseDetails.getValue().entrySet()>
										             	<#list unionunitList as unionunitDetails>
							             					<#if (unionunitDetails.getKey())!="TOTAL">
								                				<fo:table-row>
								                					<#assign NoofLines=NoofLines+1>
								                					<#assign facility = delegator.findOne("Facility", {"facilityId" : unionunitDetails.getKey()}, true)>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${facility.mccCode}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="left" font-size="4pt" keep-together="always">&#160;&#160;&#160;&#160;${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
												                   	</fo:table-cell>
												                    <fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${unionunitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                    <fo:table-cell>
												                        <fo:block text-align="right"  font-size="4pt">${unionunitDetails.getValue().get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${unionunitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${unionunitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<#assign unionqtyKgs = unionunitDetails.getValue().get("qtyKgs")?if_exists>
												                   	<#assign unionkgFat = unionunitDetails.getValue().get("kgFat")?if_exists>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt"><#if unionqtyKgs !=0>${((unionkgFat*100)/unionqtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
												                   	</fo:table-cell>
												                   	<#assign unionkgSnf = unionunitDetails.getValue().get("kgSnf")?if_exists>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt"><#if unionqtyKgs !=0>${((unionkgSnf*100)/unionqtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
												                   	</fo:table-cell>
						               							</fo:table-row>
						               							<#if (NoofLines>50)>
								   									<#assign NoofLines =0>
								   									<fo:table-row>
								       									<fo:table-cell>
								       										<fo:block page-break-after="always"></fo:block>
								       									</fo:table-cell>
								   									</fo:table-row>
																</#if>
						               						<#else>
											               	   	<#assign uniontotQtyLtrs = unionunitDetails.getValue().get("totQtyLtrs")?if_exists>
											                   	<#assign uniontotQtyKgs = unionunitDetails.getValue().get("totQtyKgs")?if_exists>
											                   	<#assign uniontotKgFat = unionunitDetails.getValue().get("totKgFat")?if_exists>
											                   	<#assign uniontotKgSnf = unionunitDetails.getValue().get("totKgSnf")?if_exists >
											                   	<#assign uniontotalQtyLtrs = uniontotalQtyLtrs+uniontotQtyLtrs>
											                   	<#assign uniontotalQtyKgs = uniontotalQtyKgs+uniontotQtyKgs>
											                   	<#assign uniontotalKgFat = uniontotalKgFat+uniontotKgFat>
											                   	<#assign uniontotalKgSnf = uniontotalKgSnf+uniontotKgSnf>
											                   	<#if uniontotQtyLtrs?has_content  && (uniontotQtyLtrs!=0)>
												                   	<fo:table-row>
												                   		<#assign NoofLines=NoofLines+1>
													                   <fo:table-cell>
													                        	<fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
													                   </fo:table-cell>
													               	</fo:table-row>
						                							<fo:table-row>
						                								<#assign NoofLines=NoofLines+1>
								                						<#assign facility = delegator.findOne("Facility", {"facilityId" : unionshedWiseDetails.getKey()}, true)>
								                    					<fo:table-cell>
								                        					<fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">${facility.facilityName}</fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotQtyLtrs?if_exists?string("##0.00")}</fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="right"  font-size="4pt" font-weight="bold">${uniontotQtyKgs?if_exists?string("##0.00")}</fo:block>
														              	</fo:table-cell>
														           		<fo:table-cell>
														               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotKgFat?if_exists?string("##0.00")}</fo:block>
														              	</fo:table-cell>
														               	<fo:table-cell>
														          			<fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotKgSnf?if_exists?string("##0.00")}</fo:block>
														        		</fo:table-cell>
														            	<fo:table-cell>
												        					<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if uniontotQtyKgs !=0>${((uniontotKgFat*100)/uniontotQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
												          				</fo:table-cell>
												       					<fo:table-cell>
											        						<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if uniontotQtyKgs !=0>${((uniontotKgSnf*100)/uniontotQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													           			</fo:table-cell>
														       			<fo:table-cell>
														  					<fo:block text-align="right" font-size="4pt" font-weight="bold">*</fo:block>
														              	</fo:table-cell>
						               								</fo:table-row>
						               								<#if (NoofLines >50)>
									   									<#assign NoofLines =0>
									   									<fo:table-row>
									       									<fo:table-cell>
									       										<fo:block page-break-after="always"></fo:block>
									       									</fo:table-cell>
									   									</fo:table-row>
																	</#if>
						               								<fo:table-row>
						               									<#assign NoofLines=NoofLines+1>
													                   <fo:table-cell>
													                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
													                   </fo:table-cell>
													               	</fo:table-row>
													               	<#if (NoofLines>50)>
									   									<#assign NoofLines =0>
									   									<fo:table-row>
									       									<fo:table-cell>
									       										<fo:block page-break-after="always"></fo:block>
									       									</fo:table-cell>
									   									</fo:table-row>
																	</#if>
													         	</#if>
						               						</#if>
						               					</#list>
						               				</#list>
						               			</fo:table-body>
		           				   			</fo:table> 
		           						</fo:block>
		                   			</fo:table-cell>
	               				</fo:table-row>
	               				<fo:table-row>
				               		<fo:table-cell>
				                        <fo:block text-align="right">
				                        	<fo:table>
												<fo:table-column column-width="15pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="30pt"/>
		             							<fo:table-body>
		             								<#if uniontotalQtyLtrs?has_content  && (uniontotalQtyLtrs!=0)>
		             									<fo:table-row>
		             										<#assign NoofLines=NoofLines+1>
										                   <fo:table-cell>
										                        	<fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
										                   </fo:table-cell>
										               	</fo:table-row>
									                	<fo:table-row>
									                		<#assign NoofLines=NoofLines+1>
									                  	   <fo:table-cell>
					                        					<fo:block text-align="left" font-size="4pt" keep-together="always" font-weight="bold">UNION TOTAL:</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotalQtyLtrs?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right"  font-size="4pt" font-weight="bold">${uniontotalQtyKgs?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotalKgFat?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${uniontotalKgSnf?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if uniontotalQtyKgs !=0>${((uniontotalKgFat*100)/uniontotalQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if uniontotalQtyKgs !=0>${((uniontotalKgSnf*100)/uniontotalQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										                   </fo:table-cell>
							               				</fo:table-row>
							               				<#if (NoofLines >50)>
						   									<#assign NoofLines =0>
						   									<fo:table-row>
						       									<fo:table-cell>
						       										<fo:block page-break-after="always"></fo:block>
						       									</fo:table-cell>
						   									</fo:table-row>
														</#if>
							               				<fo:table-row>
							               					<#assign NoofLines=NoofLines+1>
										                   <fo:table-cell>
										                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
										                   </fo:table-cell>
										               	</fo:table-row>
										               	<#if (NoofLines >50)>
						   									<#assign NoofLines =0>
						   									<fo:table-row>
						       									<fo:table-cell>
						       										<fo:block page-break-after="always"></fo:block>
						       									</fo:table-cell>
						   									</fo:table-row>
														</#if>
							               			</#if>
							            		</fo:table-body>
		           				   			</fo:table> 
		           						</fo:block>
		                   			</fo:table-cell>
	               				</fo:table-row>
				               	<fo:table-row>
	               					<fo:table-cell>
				                        <fo:block text-align="right">
				                        	<fo:table>
												<fo:table-column column-width="11pt"/>
												<fo:table-column column-width="38pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="5pt"/>
												<fo:table-body>
													<#assign otherstotalQtyLtrs=0>
										          	<#assign otherstotalQtyKgs=0>
										           	<#assign otherstotalKgFat=0>
										          	<#assign otherstotalKgSnf=0>
							             
													<#assign othersshedWiseList = othersShedWiseTotalsMap.entrySet()>
										           	<#list othersshedWiseList as othersshedWiseDetails>
										             	<#assign othersunitList = othersshedWiseDetails.getValue().entrySet()>
										             	<#list othersunitList as othersunitDetails>
							             					<#if (othersunitDetails.getKey())!="TOTAL">
								                				<fo:table-row>
								                					<#assign NoofLines=NoofLines+1>
								                					<#assign facility = delegator.findOne("Facility", {"facilityId" : othersunitDetails.getKey()}, true)>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${facility.mccCode}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="left" font-size="4pt" keep-together="always">&#160;&#160;&#160;&#160;${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
												                   	</fo:table-cell>
												                    <fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${othersunitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                    <fo:table-cell>
												                        <fo:block text-align="right"  font-size="4pt">${othersunitDetails.getValue().get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${othersunitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt">${othersunitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
												                   	</fo:table-cell>
												                   	<#assign othersqtyKgs = othersunitDetails.getValue().get("qtyKgs")?if_exists>
												                   	<#assign otherskgFat = othersunitDetails.getValue().get("kgFat")?if_exists>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt"><#if othersqtyKgs !=0>${((otherskgFat*100)/othersqtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
												                   	</fo:table-cell>
												                   	<#assign otherskgSnf = othersunitDetails.getValue().get("kgSnf")?if_exists>
												                   	<fo:table-cell>
												                        <fo:block text-align="right" font-size="4pt"><#if othersqtyKgs !=0>${((otherskgSnf*100)/othersqtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
												                   	</fo:table-cell>
						               							</fo:table-row>
						               							<#if (NoofLines >50)>
								   									<#assign NoofLines =0>
								   									<fo:table-row>
								       									<fo:table-cell>
								       										<fo:block page-break-after="always"></fo:block>
								       									</fo:table-cell>
								   									</fo:table-row>
																</#if>
						               						<#else>
											               	   	<#assign otherstotQtyLtrs = othersunitDetails.getValue().get("totQtyLtrs")?if_exists>
											                   	<#assign otherstotQtyKgs = othersunitDetails.getValue().get("totQtyKgs")?if_exists>
											                   	<#assign otherstotKgFat = othersunitDetails.getValue().get("totKgFat")?if_exists>
											                   	<#assign otherstotKgSnf = othersunitDetails.getValue().get("totKgSnf")?if_exists >
											                   	<#assign otherstotalQtyLtrs = otherstotalQtyLtrs+otherstotQtyLtrs>
											                   	<#assign otherstotalQtyKgs = otherstotalQtyKgs+otherstotQtyKgs>
											                   	<#assign otherstotalKgFat = otherstotalKgFat+otherstotKgFat>
											                   	<#assign otherstotalKgSnf = otherstotalKgSnf+otherstotKgSnf>
											                   	<#if otherstotQtyLtrs?has_content  && (otherstotQtyLtrs!=0)>
												                   	<fo:table-row>
												                   		<#assign NoofLines=NoofLines+1>
													                   <fo:table-cell>
													                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
													                   </fo:table-cell>
													               	</fo:table-row>
						                							<fo:table-row>
						                								<#assign NoofLines=NoofLines+1>
								                						<#assign facility = delegator.findOne("Facility", {"facilityId" : othersshedWiseDetails.getKey()}, true)>
								                    					<fo:table-cell>
								                        					<fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">${facility.facilityName}</fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotQtyLtrs?if_exists?string("##0.00")}</fo:block>
														                </fo:table-cell>
														                <fo:table-cell>
														               		<fo:block text-align="right"  font-size="4pt" font-weight="bold">${otherstotQtyKgs?if_exists?string("##0.00")}</fo:block>
														              	</fo:table-cell>
														           		<fo:table-cell>
														               		<fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotKgFat?if_exists?string("##0.00")}</fo:block>
														              	</fo:table-cell>
														               	<fo:table-cell>
														          			<fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotKgSnf?if_exists?string("##0.00")}</fo:block>
														        		</fo:table-cell>
														            	<fo:table-cell>
												        					<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if otherstotQtyKgs !=0>${((otherstotKgFat*100)/otherstotQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
												          				</fo:table-cell>
												       					<fo:table-cell>
											        						<fo:block text-align="right" font-size="4pt" font-weight="bold"><#if otherstotQtyKgs !=0>${((otherstotKgSnf*100)/otherstotQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
													           			</fo:table-cell>
														       			<fo:table-cell>
														  					<fo:block text-align="right" font-size="4pt" font-weight="bold">*</fo:block>
														              	</fo:table-cell>
						               								</fo:table-row>
						               								<#if (NoofLines >50)>
									   									<#assign NoofLines =0>
									   									<fo:table-row>
									       									<fo:table-cell>
									       										<fo:block page-break-after="always"></fo:block>
									       									</fo:table-cell>
									   									</fo:table-row>
																	</#if>
						               								<fo:table-row>
						               									<#assign NoofLines=NoofLines+1>
													                   <fo:table-cell>
													                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
													                   </fo:table-cell>
													               	</fo:table-row>
													               	<#if (NoofLines >50)>
									   									<#assign NoofLines =0>
									   									<fo:table-row>
									       									<fo:table-cell>
									       										<fo:block page-break-after="always"></fo:block>
									       									</fo:table-cell>
									   									</fo:table-row>
																	</#if>
													         	</#if>
						               						</#if>
						               					</#list>
						               				</#list>
						               			</fo:table-body>
		           				   			</fo:table> 
		           						</fo:block>
		                   			</fo:table-cell>
	               				</fo:table-row>
	               				<fo:table-row>
				               		<fo:table-cell>
				                        <fo:block text-align="right">
				                        	<fo:table>
												<fo:table-column column-width="15pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="30pt"/>
		             							<fo:table-body>
		             								<#if otherstotalQtyLtrs?has_content  && (otherstotalQtyLtrs!=0)>
		             									<fo:table-row>
		             										<#assign NoofLines=NoofLines+1>
										                   <fo:table-cell>
										                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
										                   </fo:table-cell>
										               	</fo:table-row>
									                	<fo:table-row>
									                		<#assign NoofLines=NoofLines+1>
									                  	   <fo:table-cell>
					                        					<fo:block text-align="left" font-size="4pt" keep-together="always" font-weight="bold">OTHERS TOTAL:</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
										                   </fo:table-cell>
										                    <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotalQtyLtrs?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                    <fo:table-cell>
										                        <fo:block text-align="right"  font-size="4pt" font-weight="bold">${otherstotalQtyKgs?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotalKgFat?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${otherstotalKgSnf?if_exists?string("##0.00")}</fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if otherstotalQtyKgs !=0>${((otherstotalKgFat*100)/otherstotalQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
										                   </fo:table-cell>
										                   <fo:table-cell>
										                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if otherstotalQtyKgs !=0>${((otherstotalKgSnf*100)/otherstotalQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										                   </fo:table-cell>
							               				</fo:table-row>
							               				<#if (NoofLines >50)>
						   									<#assign NoofLines =0>
						   									<fo:table-row>
						       									<fo:table-cell>
						       										<fo:block page-break-after="always"></fo:block>
						       									</fo:table-cell>
						   									</fo:table-row>
														</#if>
							               				<fo:table-row>
							               					<#assign NoofLines=NoofLines+1>
										                   <fo:table-cell>
										                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
										                   </fo:table-cell>
										               	</fo:table-row>
										               	<#if (NoofLines>50)>
						   									<#assign NoofLines =0>
						   									<fo:table-row>
						       									<fo:table-cell>
						       										<fo:block page-break-after="always"></fo:block>
						       									</fo:table-cell>
						   									</fo:table-row>
														</#if>
							               			</#if>
							            		</fo:table-body>
		           				   			</fo:table> 
		           						</fo:block>
		                   			</fo:table-cell>
	               				</fo:table-row>
	               				<fo:table-row>
				               		<fo:table-cell>
				                        <fo:block text-align="right">
				                        	 <fo:table>
											   	<fo:table-column column-width="15pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="50pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="35pt"/>
												<fo:table-column column-width="37pt"/>
												<fo:table-column column-width="33pt"/>
												<fo:table-column column-width="30pt"/>
												<fo:table-column column-width="30pt"/>
				             					<fo:table-body>
				             						<fo:table-row>
				             							<#assign NoofLines=NoofLines+1>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
									                   </fo:table-cell>
									               	</fo:table-row>
									                <fo:table-row>
									                	<#assign NoofLines=NoofLines+1>
										                <#assign grandTotalQtyLtrs = (totalQtyLtrs+uniontotalQtyLtrs+otherstotalQtyLtrs)>
										                <#assign grandTotalQtyKgs = (totalQtyKgs+uniontotalQtyKgs+otherstotalQtyKgs)>
										                <#assign grandTotalKgFat = (totalKgFat+uniontotalKgFat+otherstotalKgFat)>
										                <#assign grandTotalKgSnf = (totalKgSnf+uniontotalKgSnf+otherstotalKgSnf)>
								                  	   <fo:table-cell>
				                        					<fo:block text-align="left" font-size="4pt" keep-together="always" font-weight="bold">GRAND TOTAL:</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" keep-together="always" font-weight="bold"></fo:block>
									                   </fo:table-cell>
									                    <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${grandTotalQtyLtrs?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                    <fo:table-cell>
									                        <fo:block text-align="right"  font-size="4pt" font-weight="bold">${grandTotalQtyKgs?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${grandTotalKgFat?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold">${grandTotalKgSnf?if_exists?string("##0.00")}</fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if grandTotalQtyKgs !=0>${((grandTotalKgFat*100)/grandTotalQtyKgs)?if_exists?string("##0.0")}0<#else>0.00</#if></fo:block>
									                   </fo:table-cell>
									                   <fo:table-cell>
									                        <fo:block text-align="right" font-size="4pt" font-weight="bold"><#if grandTotalQtyKgs !=0>${((grandTotalKgSnf*100)/grandTotalQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
									                   </fo:table-cell>
									               </fo:table-row>
									               <#if (NoofLines >50)>
					   									<#assign NoofLines =0>
					   									<fo:table-row>
					       									<fo:table-cell>
					       										<fo:block page-break-after="always"></fo:block>
					       									</fo:table-cell>
					   									</fo:table-row>
													</#if>
									               <fo:table-row>
									               		<#assign NoofLines=NoofLines+1>
									                   <fo:table-cell>
									                        <fo:block text-align="left" font-size="4pt" font-weight="bold" keep-together="always">------------------------------------------------------------------------------------------------------------------</fo:block>
									                   </fo:table-cell>
									               	</fo:table-row>
									               	<#if (NoofLines >50)>
					   									<#assign NoofLines =0>
					   									<fo:table-row>
					       									<fo:table-cell>
					       										<fo:block page-break-after="always"></fo:block>
					       									</fo:table-cell>
					   									</fo:table-row>
													</#if>
									            </fo:table-body>
				           				   </fo:table> 
				           				</fo:block>
				                   </fo:table-cell>
	               				</fo:table-row> 
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block font-size="4pt" keep-together="always">Copy submitted to the Managing Director,</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block font-size="4pt" keep-together="always">Copy submitted to the Executive Director,            &#160;&#160;&#160;SENIOR SYSTEMS ANALYST</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block font-size="4pt" keep-together="always">Copy submitted to the General Manager(PI),</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block font-size="4pt" keep-together="always">Copy submitted to the General Manager(MPF),</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>
				               <fo:table-row>
				                   <fo:table-cell>
				                        <fo:block font-size="4pt" keep-together="always">Copy submitted to the Chief Quality Control,</fo:block>
				                   </fo:table-cell>
				               </fo:table-row>  
	            			</fo:table-body>
	        			</fo:table> 
	     			</fo:block>
	    		</fo:flow>
	 		</fo:page-sequence>
	 	<#else>
			<fo:page-sequence master-reference="main">
		    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospae">
		       		 <fo:block font-size="14pt">
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>	
		</#if>
	</#if>
</fo:root>
</#escape>