
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".1in" margin-right=".1in" margin-top=".1in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "pendingShipmentsReport.pdf")}
        <#if finalList?has_content>
        <fo:page-sequence master-reference="main" font-size="10pt">	
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
				<fo:block  keep-together="always" text-align="center"  font-weight="bold"   font-size="12pt" white-space-collapse="false">${reportHeader.description?if_exists} </fo:block>
				<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${BOAddress?if_exists}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="right"    font-size="10pt" >Page - <fo:page-number/></fo:block>
                <#-- <fo:block text-align="left"    font-size="10pt" >STATEMENT SHOWING THE AGENCYWISE DETAILS OF COTTON/SILK/JUTE YARN SUPPLIED BY NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED UNDER THE SCHEME FOR SUPPLY OF YARN AT :<fo:inline font-weight="bold" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd-MMM-yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd-MMM-yyyy")?if_exists} </fo:inline></fo:block> -->
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >PENDING SHIPMENTS REPORT </fo:block>
				<fo:block text-align="center" font-size="10pt" font-weight="bold" >FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(daystart, "dd/MM/yyyy")?if_exists} To:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayend, "dd/MM/yyyy")?if_exists} </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       
        		<fo:block>
             		<fo:table >
             			
             		    <fo:table-column column-width="11.5%"/>
             		    <fo:table-column column-width="8%"/>
             		    <fo:table-column column-width="6.5%"/>
             		    <fo:table-column column-width="6.5%"/>
             		    <fo:table-column column-width="10%"/>
             		    <fo:table-column column-width="12%"/>
             		    <fo:table-column column-width="8.5%"/>
             		    <fo:table-column column-width="14%"/>
             		    <fo:table-column column-width="8%"/>
             		    <fo:table-column column-width="6.5%"/>
             		    <fo:table-column column-width="5%"/>
             		    <fo:table-column column-width="4%"/>
             		              		    
			            
			            <fo:table-body>
			            	
			                <fo:table-row>
								
			                    <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Indent</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">No</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="center" font-size="10pt"  font-weight="bold" white-space-collapse="false">Indent</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Indent</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Unit</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Price</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Indent</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Value</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">PO No</fo:block>
					            	
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">PO</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell>
								 <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Supplier</fo:block>
					            	
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Ship</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ment</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Date</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Ship</fo:block>
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">ment</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Qty</fo:block>
					            </fo:table-cell>
 								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Dur B/W SO</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">And PO</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">Dur B/W PO</fo:block>
					            	<fo:block  text-align="center" font-size="10pt" font-weight="bold" white-space-collapse="false">And Ship</fo:block>
					            </fo:table-cell>
							</fo:table-row>
						  <#assign totalIdnQty1 = 0>  
						<#assign totalindentValue1 = 0>
							<#assign totalshipQty1 = 0>  
			                  <#list finalList as eachList>
	                             <fo:table-row>
								
			                    <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false">${eachList.IndentNo?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block  text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.IndentDate?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.indQty?if_exists}</fo:block>
					            </fo:table-cell >
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.indUnitPrice?if_exists}</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.indentValue?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.PoNo?if_exists}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.PoDate?if_exists}</fo:block>
					            </fo:table-cell>
								 <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.supplier?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="left" font-size="9pt"  white-space-collapse="false">${eachList.shipmentDate?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt" white-space-collapse="false">${eachList.shipQty?if_exists}</fo:block>
					            </fo:table-cell>
 								<fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.DurBWSoAndPo?if_exists}</fo:block>
					            </fo:table-cell>
					             <fo:table-cell border-style="solid">
					            	<fo:block   text-align="right" font-size="9pt"  white-space-collapse="false">${eachList.DurBwSoAndShip?if_exists}</fo:block>
					            </fo:table-cell>
							</fo:table-row>
                             <#assign totalIdnQty = eachList.indQty>
							<#assign totalindentValue= eachList.indentValue>
							<#assign totalshipQty = eachList.shipQty>

                              	<#assign  totalIdnQty1= totalIdnQty1+totalIdnQty>
								 <#assign  totalindentValue1= totalindentValue1+totalindentValue>
								<#assign  totalshipQty1= totalshipQty1+totalshipQty>
							</#list>
 			 			 <fo:table-row>
  								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" font-weight="bold" white-space-collapse="false">TOTAL</fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
								
			                    <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" font-weight="bold" white-space-corighe="false">${totalIdnQty1?if_exists?string("##0.00")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" font-weight="bold" white-space-corighe="false"></fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" font-weight="bold" white-space-corighe="false">${totalindentValue1?if_exists?string("##0.00")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" font-weight="bold" white-space-colleft="false"></fo:block>
					            </fo:table-cell>				            
								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="right" font-size="9pt" font-weight="bold" white-space-collapse="false">${totalshipQty1?if_exists?string("##0.00")}</fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
								<fo:table-cell border-style="solid"> 
					            	<fo:block  text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>
					            </fo:table-cell>
							</fo:table-row>
						
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