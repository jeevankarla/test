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
                <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "AcknowledgementReport.txt")}
  <fo:page-sequence master-reference="main">
  		<fo:static-content flow-name="xsl-region-before">
 			<#assign fromFacilityDetails = delegator.findOne("Facility", {"facilityId" : parameters.facilityId}, true)>
 			<#assign toFacilityDetails 	 = delegator.findOne("Facility", {"facilityId" : parameters.facilityIdTo}, true)>	
 			<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">ACKNOWLEDGEMENT FOR THE MILK RECEIVED    PAGE NO:<fo:page-number/></fo:block>
 			<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">From : ${fromFacilityDetails.get("facilityName")?if_exists}   To : ${toFacilityDetails.get("facilityName")?if_exists}  Period From:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateStart,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd,"dd-MM-yyyy")}</fo:block>
            <fo:block font-size="8pt">-----------------------------------------------------------------</fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="7pt">Date  	   		&#160;&#160;QtyKgs    	   	Fat%     Snf%     Kg.Fat    Kg.Snf</fo:block>
            <fo:block font-size="8pt">-----------------------------------------------------------------</fo:block>
 		</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      	<fo:block  font-size="4pt">
		<fo:table width="295pt" table-layout="fixed">
		   	 <fo:table-column column-width="20pt"/>
		     <fo:table-column column-width="34pt"/>
		     <fo:table-column column-width="30pt"/>
		     <fo:table-column column-width="23pt"/>
		     <fo:table-column column-width="30pt"/>
		     <fo:table-column column-width="30pt"/>
		     <fo:table-column column-width="38pt"/>
		     <fo:table-column column-width="32pt"/>
			 <fo:table-column column-width="33pt"/>
             <fo:table-body>
             <#assign totalQtyLtrs=0>
             <#assign totalFat=0>
             <#assign totalSnf=0>
             <#assign totalQtyKgs=0>
             <#assign totalKgFat=0>
             <#assign totalKgSnf=0>
             
             <#list dayWiseList as dayWiseSalesMap>
             <#assign dayWiseSales = dayWiseSalesMap>
             		<#if (dayWiseSales.get("date")?if_exists)!="TOTAL">
                <fo:table-row>
                   <fo:table-cell>
                        	<fo:block text-align="left" font-size="4pt">${dayWiseSales.get("date")?if_exists}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("fat")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("snf")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
               </fo:table-row>
                 <#else>
                <fo:table-row>
                   <fo:table-cell>
                        	<fo:block font-size="8pt">-----------------------------------------------------------------</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               	<fo:table-row>
                   <fo:table-cell>
                        	<fo:block text-align="left" font-weight="bold">TOTAL</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                    		<#assign totalQtyLtrs=totalQtyLtrs+dayWiseSales.get("qtyLtrs")>
                    		<#assign totalFat=totalFat+dayWiseSales.get("fat")>
                    		<#assign totalSnf=totalSnf+dayWiseSales.get("snf")>
                    		<#assign totalQtyKgs=totalQtyKgs+dayWiseSales.get("qtyKgs")>
                    		<#assign totalKgFat=totalKgFat+dayWiseSales.get("kgFat")>
                    		<#assign totalKgSnf=totalKgSnf+dayWiseSales.get("kgSnf")>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("qtyKgs")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt"></fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt"></fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("kgFat")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${dayWiseSales.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt" keep-together="always">MILLBILLAMOUNT</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="4pt">${grossAmount?if_exists?string("##0.00")}</fo:block>
                   </fo:table-cell>
               </fo:table-row>
            	 </#if>
               </#list>
               <fo:table-row>
               		<fo:table-cell>
                        <fo:block text-align="right">
                        	 <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
					             <fo:table-body>
					             <#list outputDayWiseList as outputDayWiseListMap>
             							<#assign outputDayWiseSales = outputDayWiseListMap>
             								<#if (outputDayWiseSales.get("outputDate")?if_exists)="OPTOTAL">
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-size="4pt" font-weight="bold">LESS-CB</fo:block>
				                   		</fo:table-cell>
				                   		<#assign totalQtyLtrs=totalQtyLtrs+outputDayWiseSales.get("quantityLtrs")>
			                    		<#assign totalFat=totalFat+outputDayWiseSales.get("fat")>
			                    		<#assign totalSnf=totalSnf+outputDayWiseSales.get("snf")>
			                    		<#assign totalQtyKgs=totalQtyKgs+outputDayWiseSales.get("qty")>
			                    		<#assign totalKgFat=totalKgFat+outputDayWiseSales.get("kgFat")>
			                    		<#assign totalKgSnf=totalKgSnf+outputDayWiseSales.get("kgSnf")>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${outputDayWiseSales.get("qty")?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${outputDayWiseSales.get("fat")?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${outputDayWiseSales.get("snf")?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${outputDayWiseSales.get("kgFat")?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${outputDayWiseSales.get("kgSnf")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">FATVALUE@55%</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${fatSnfMap.get("fat55")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
					               </fo:table-row>
					                 </#if>
					                 </#list>
					            </fo:table-body>
           				   </fo:table> 
           			</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
               		<fo:table-cell>
                        <fo:block text-align="right">
                             <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
								 <fo:table-body>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold">ADD-OB</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${totQty?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${totFat?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${totSnf?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">KGFAT</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${procMilkBillMap.get("milkBillKgFat")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
					               </fo:table-row>
					            </fo:table-body>
           				   </fo:table> 
           			</fo:block>
                   </fo:table-cell>
               </fo:table-row>
                <fo:table-row>
               		<fo:table-cell>
                        <fo:block text-align="right">
                        	 <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
					             <fo:table-body>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold" keep-together="always" font-size="4pt">Net Total</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                    	<#assign netTotalQtyKgs = (totalQtyKgs-totQty)?if_exists> 
				                        	<fo:block text-align="right" font-size="4pt">${(netTotalQtyKgs)?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right"  font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                   	   <#assign netTotalKgFat = (totalKgFat-totKgFat)?if_exists>
				                        	<fo:block text-align="right" font-size="4pt">${(netTotalKgFat)?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                      <#assign netTotalKgSnf = (totalKgSnf-totKgSnf)?if_exists>
				                        	<fo:block text-align="right" font-size="4pt">${(netTotalKgSnf)?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">FATRATEKG</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${fatSnfMap.get("kgFatRate")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
					               </fo:table-row>
					            </fo:table-body>
           				   </fo:table> 
           			</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
               		<fo:table-cell>
                        <fo:block text-align="right">
                             <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
								 <fo:table-body>
					              <#if procMilkBillMap?has_content>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold" keep-together="always">Milk Bill</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right">${procMilkBillMap.get("milkBillQtyKgs")?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right">${procMilkBillMap.get("milkBillKgFat")?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right">${procMilkBillMap.get("milkBillKgSnf")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">SNFVALUE@45%</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${fatSnfMap.get("snf45")?if_exists?string("##0.00")}</fo:block>
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
                             <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
								 <fo:table-body>
					              <#if procMilkBillMap?has_content>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold">Difference</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right">${(netTotalQtyKgs-(procMilkBillMap.get("milkBillQtyKgs")))?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right">${(netTotalKgFat-(procMilkBillMap.get("milkBillKgFat")))?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right">${(netTotalKgSnf-(procMilkBillMap.get("milkBillKgSnf")))?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">KGSNF</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${procMilkBillMap.get("milkBillKgSnf")?if_exists?string("##0.00")}</fo:block>
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
                             <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
								 <fo:table-body>
					              <#if fatSnfMap?has_content>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold" keep-together="always">RecoveryRate</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right"></fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right"  font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right">${fatSnfMap.get("kgFatRate")?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right">${fatSnfMap.get("kgSnfRate")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">SNFRATEKG</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt">${fatSnfMap.get("kgSnfRate")?if_exists?string("##0.00")}</fo:block>
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
                             <fo:table width="295pt" table-layout="fixed">
							   	 <fo:table-column column-width="20pt"/>
							     <fo:table-column column-width="34pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="23pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="30pt"/>
							     <fo:table-column column-width="36pt"/>
							     <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="33pt"/>
								 <fo:table-column column-width="41pt"/>
								 <fo:table-body>
					              <#if shortageMap?has_content>
					                <fo:table-row>
				                  		 <fo:table-cell>
				                        	<fo:block text-align="left" font-weight="bold" keep-together="always">RecoveryAmount</fo:block>
				                   		</fo:table-cell>
				                    	<fo:table-cell>
				                        	<fo:block text-align="right">${shortageMap.get("shortageQtyKgs")?if_exists?string("##0.00")}</fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   		<fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                   		</fo:table-cell>
				                   	   <fo:table-cell>
				                        	<fo:block text-align="right">${shortageMap.get("shortageKgFatAmt")?if_exists?string("##0.00")}</fo:block>
				                   	   </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right">${shortageMap.get("shortageKgSnfAmt")?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                        	<fo:block text-align="right" font-size="4pt"></fo:block>
				                      </fo:table-cell>
				                      <fo:table-cell>
				                      <#assign totalSum  = ((shortageMap.get("shortageKgFatAmt"))+(shortageMap.get("shortageKgSnfAmt")))?if_exists> 
				                        	<fo:block text-align="right" font-size="4pt">${totalSum?if_exists?string("##0.00")}</fo:block>
				                      </fo:table-cell>
					               </fo:table-row>
					                </#if>
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
</fo:root>
</#escape>
