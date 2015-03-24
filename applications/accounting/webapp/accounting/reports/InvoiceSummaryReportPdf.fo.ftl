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
            <fo:simple-page-master master-name="main" page-width="16.69in" page-height="8.27in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
        <fo:region-body margin-top="0.5in" margin-bottom="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>     
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <#if finalInvoiceItemList?has_content>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">
              <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;- <fo:page-number/></fo:block>
               </fo:static-content>						
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            			<fo:block text-align="center" white-space-collapse="false"  font-size="12pt" keep-together="always" >&#160;KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
		       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                  <fo:block text-align="center" font-size="11pt">Gl account summary for period : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
		                  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       			   
		                  <fo:block>
		                        <fo:table>
		                        	<fo:table-column column-width="3%"/>
		                            <fo:table-column column-width="6%"/>
		                            <fo:table-column column-width="16%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-column column-width="6.3%"/>
		                            <fo:table-body>
		                            	<fo:table-row>
		                            		<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                            	<fo:table-row>
		                            		<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">S.NO            </fo:block>
			                                </fo:table-cell>
		                            		<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">GL Account            </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">Invoice Item Type   </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Posted Amount (Dr)    </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (a)     </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Unposted Amount (Dr)    </fo:block>
			                                    <fo:block text-align="right" font-size="11pt">(b)    </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Posted Amount (Cr)   </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (c)   </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Unposted Amount (Cr) </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (d) </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">JV Posted Amount (Dr)     </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (e)      </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">JV Unposted Amount (Dr)   </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (f)    </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">JV Posted Amount (Cr) </fo:block>
			                                    <fo:block text-align="right" font-size="11pt">(g)  </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">JV Unposted Amount (Cr) </fo:block>
			                                    <fo:block text-align="right" font-size="11pt">(h)  </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Tot Posted Amount (Dr)    </fo:block>
			                                    <fo:block text-align="right" font-size="11pt">(a+e)     </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"> Tot Unposted Amount (Dr)    </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (b+f)    </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Tot Posted Amount (Cr)  </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (c+g) </fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">Tot Unposted Amount (Cr)  </fo:block>
			                                    <fo:block text-align="right" font-size="11pt"> (d+h)  </fo:block>
			                                </fo:table-cell>
		                  				</fo:table-row>
		                  				<fo:table-row>
		                  					<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                                
		                                <#assign postedDrTot = 0>
		                  				<#assign unPostedDrTot = 0>
		                  				<#assign postedCrTot = 0>
		                  				<#assign unPostedCrTot = 0>
		                  				<#assign postedJDrTot = 0>
		                  				<#assign unPostedJDrTot = 0>
		                  				<#assign postedJCrTot = 0>
		                  				<#assign unPostedJCrTot = 0>
		                                
		                                <#assign srno = 0>
		                                
		                  				<#list glSummaryFinalList as eachGlSummary>
		                  				<#assign srno = srno + 1>
		                  				<#if eachGlSummary.get("invoiceItemTypeId") == "TOTAL">
		                  				
		                  				<#assign postedDrTot = eachGlSummary.get("postedDrAmount")>
		                  				<#assign unPostedDrTot = eachGlSummary.get("unPostedDrAmount")>
		                  				<#assign postedCrTot = eachGlSummary.get("postedCrAmount")>
		                  				<#assign unPostedCrTot = eachGlSummary.get("unPostedCrAmount")>
		                  				<#assign postedJDrTot = eachGlSummary.get("JpostedDrAmount")>
		                  				<#assign unPostedJDrTot = eachGlSummary.get("JunPostedDrAmount")>
		                  				<#assign postedJCrTot = eachGlSummary.get("JpostedCrAmount")>
		                  				<#assign unPostedJCrTot = eachGlSummary.get("JunPostedCrAmount")>
		                  				
		                  				<fo:table-row>
		                  					<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                               <fo:table-row>
		                               		<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">TOTAL</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("postedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("unPostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("postedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("unPostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JpostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JunPostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JpostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JunPostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("pstDrAmount")?has_content>${eachGlSummary.get("pstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("unpstDrAmount")?has_content>${eachGlSummary.get("unpstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("pstCrAmount")?has_content>${eachGlSummary.get("pstCrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("unpstDrAmount")?has_content>${eachGlSummary.get("unpstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                                 <fo:table-row>
		                                 	<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-----------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">---------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">-------------------------------------</fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                  				<#else>
		                  				
		                  				<fo:table-row>
		                  					<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">${srno}</fo:block>
			                                </fo:table-cell>
		                  					<fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">${eachGlSummary.get("glAccountId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="left" font-size="11pt">${eachGlSummary.get("invoiceItemTypeId")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("postedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("unPostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("postedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JunPostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JpostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JunPostedDrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JpostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt">${eachGlSummary.get("JunPostedCrAmount")?if_exists}</fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("pstDrAmount")?has_content>${eachGlSummary.get("pstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("unpstDrAmount")?has_content>${eachGlSummary.get("unpstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("pstCrAmount")?has_content>${eachGlSummary.get("pstCrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
			                                <fo:table-cell >
			                                    <fo:block text-align="right" font-size="11pt"><#if eachGlSummary.get("unpstDrAmount")?has_content>${eachGlSummary.get("unpstDrAmount")?if_exists}<#else>0</#if></fo:block>
			                                </fo:table-cell>
		                                </fo:table-row>
		                  				</#if>
		                  				</#list>
		                  		</fo:table-body>
		                  	</fo:table>	
		                  </fo:block>
               			
               			<fo:block break-before="page"/>
               			
                  		<fo:block text-align="center" font-size="11pt">INVOICE SUMMARY REPORT</fo:block>
                  		<#--<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                  		<fo:block text-align="left" font-size="11pt" white-space-collapse="false">Internal Organization							: ${intOrgName?if_exists} - ${partyId?if_exists}</fo:block>
                  		<fo:block text-align="left" font-size="11pt" white-space-collapse="false">From Date									 													: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} 		 To Date:         ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}</fo:block>
                  		<fo:block text-align="left" font-size="11pt" white-space-collapse="false">Invoice type					     											: ${parentTypeId}</fo:block>
                  		-->
       			  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                  		<fo:block>
                        <fo:table>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="13%"/>
                            <fo:table-column column-width="16%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="9%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-column column-width="8%"/>
                            <fo:table-body>
                            	<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">-------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                            	<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">Date</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">InvoiceType</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">invoiceStatus</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">invoiceItemType</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">glAccount</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">PostedDr</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">UnPostedDr</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">PostedCr</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">UnPostedCr</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">-------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                            
                            <#assign prevDayTotal = 0>
                            
                            <#list finalInvoiceItemList as finalInvoiceItem>
                            
                            <#if finalInvoiceItem.get("statusId") == "TOTAL">
                               <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">Total</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                
                                </fo:table-row>
                                 <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">-------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           <#elseif finalInvoiceItem.get("statusId") == "SUBTOTAL"> 
                           		 <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">-------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>   
                           		<fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">SubTotal</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">&#160;</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                                 <fo:table-row>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">-------------------------</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">--------------------------------------</fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">----------------------------------</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           
                           <#else>
                           		<fo:table-row>
	                                <fo:table-cell >
	                                	<fo:block text-align="left" font-size="11pt"><#if finalInvoiceItem.get("invoiceDate")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((finalInvoiceItem.get("invoiceDate")), "dd/MM/yyyy")}</#if></fo:block>
	                                    
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("statusId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("invoiceItemTypeId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">${finalInvoiceItem.get("glAccountId")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedDrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                  <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("postedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="right" font-size="11pt">${finalInvoiceItem.get("unPostedCrAmount")?if_exists}</fo:block>
	                                </fo:table-cell>
                                </fo:table-row>
                           </#if> 
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
	            	No Records Found For The Given Duration!
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
    </fo:root>
</#escape>
