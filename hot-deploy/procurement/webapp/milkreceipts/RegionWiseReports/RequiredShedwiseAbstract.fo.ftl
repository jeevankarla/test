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
          	<fo:region-body margin-top="1.2in"/>
          	<fo:region-before extent="0.5in"/>
            <fo:region-after extent="0.5in"/>
        </fo:simple-page-master>
   	</fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "RequiredShedwiseAbstract.txt")}
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
			<#assign totalQtyLtrs=0>
        	<#assign totalQtyKgs=0>
        	<#assign totalKgFat=0>
        	<#assign totalKgSnf=0>
        	<#assign shedWiseList = shedWiseTotalsMap.entrySet()>
        	<#list shedWiseList as shedWiseDetails>
             	<#assign facility = delegator.findOne("Facility", {"facilityId" : shedWiseDetails.getKey()}, true)>
        	</#list>
        	<fo:page-sequence master-reference="main">
  				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospae">
  					<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE FEDERATION LIMITED</fo:block>
 					<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;     LALAPET  : HYDERABAD     </fo:block>
 					<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;M.P.F MILK RECEIPTS ON :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd/MM/yyyy")} </fo:block>
 					<fo:block text-align="left" white-space-collapse="false" font-size="10pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;NAME OF THE SHED : ${facility.facilityName}     </fo:block>
            		<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">CODE NAME OF MCC/DAIRY           QUANTITY        QUANTITY       TOTAL            TOTAL          AVG        AVG          </fo:block>
            		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="8pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                      LTS             KGS          KG-FAT           KG-SNF         FAT        SNF          </fo:block>
            		<fo:block font-size="8pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
 				</fo:static-content>
        		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      				<fo:block  font-size="4pt"> 	   
						<fo:table>
		   					<fo:table-column column-width="15pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="50pt"/>
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="35pt"/>
							<fo:table-column column-width="42pt"/>
							<fo:table-column column-width="25pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="30pt"/>
            				<fo:table-body>
             					<#assign shedWiseList = shedWiseTotalsMap.entrySet()>
             					<#list shedWiseList as shedWiseDetails>
             						<#assign unitList = shedWiseDetails.getValue().entrySet()>
             						<#list unitList as unitDetails>
             							<#if (unitDetails.getKey())!="TOTAL">
                							<fo:table-row>
                								<#assign facility = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
                   								<fo:table-cell>
                   									<fo:block text-align="left" font-size="4pt">${facility.mccCode}</fo:block>
                   								</fo:table-cell>
                   								<fo:table-cell>
                        							<fo:block text-align="left" font-size="4pt" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString((facility.get("facilityName")))),20)}</fo:block>
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
                   									<fo:table-cell>
                        								<fo:block font-size="4pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
                   									</fo:table-cell>
               									</fo:table-row>
                								<fo:table-row>
                									<#assign facility = delegator.findOne("Facility", {"facilityId" : shedWiseDetails.getKey()}, true)>
                    								<fo:table-cell>
                        								<fo:block text-align="left" font-size="4pt" keep-together="always">${facility.facilityName}</fo:block>
                   									</fo:table-cell>
                   									<fo:table-cell>
                        								<fo:block text-align="left" font-size="4pt" keep-together="always"></fo:block>
                   									</fo:table-cell>
                    								<fo:table-cell>
                        								<fo:block text-align="right" font-size="4pt">${totQtyLtrs?if_exists?string("##0.00")}</fo:block>
                   									</fo:table-cell>
                    								<fo:table-cell>
                        								<fo:block text-align="right"  font-size="4pt">${totQtyKgs?if_exists?string("##0.00")}</fo:block>
                   									</fo:table-cell>
                   									<fo:table-cell>
                        								<fo:block text-align="right" font-size="4pt">${totKgFat?if_exists?string("##0.00")}</fo:block>
                   									</fo:table-cell>
                   									<fo:table-cell>
                        								<fo:block text-align="right" font-size="4pt">${totKgSnf?if_exists?string("##0.00")}</fo:block>
                   									</fo:table-cell>
                   									<fo:table-cell>
                        								<fo:block text-align="right" font-size="4pt"><#if totQtyKgs !=0>${((totKgFat*100)/totQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                   									</fo:table-cell>
                   									<fo:table-cell>
                        								<fo:block text-align="right" font-size="4pt"><#if totQtyKgs !=0>${((totKgSnf*100)/totQtyKgs)?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
                   									</fo:table-cell>
               									</fo:table-row>
               								</#if>
               							</#if>
               						</#list>
               					</#list>
               					<fo:table-row>
                   					<fo:table-cell>
                        				<fo:block font-size="4pt">----------------------------------------------------------------------------------------------------------------------</fo:block>
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