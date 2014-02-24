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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="5in"
                     margin-left=".2in" margin-right=".3in" margin-top=".4in" margin-bottom=".3in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "payrcpt.txt")}
        <#assign totalPaymentAmount = 0>
        <#assign SNO=0> 
        <#if printPaymentsList?has_content>    
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always">MOTHERDAIRY</fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always">           </fo:block>
        		<fo:block text-align="center" keep-together="always" font-size="10pt" >CASH BILL</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">Payment Location: ${printPaymentsList[0].paymentLocation?if_exists}                          Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>	 	 	  
        		<fo:block text-align="left" keep-together="always" font-size="10pt" white-space-collapse="false">Vendor/Cashier Name:  ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(vendorName?if_exists)),20)}</fo:block>
        		<fo:block text-align="left" keep-together="always" font-size="9.5pt" white-space-collapse="false">Route Id: ${printPaymentsList[0].parentFacilityId?if_exists}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
              <fo:block font-size="10pt">                
                <fo:table >
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>                
                    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="50pt"/>                   
                    <fo:table-header>
		            	<fo:table-cell>
		            		<fo:block>------------------------------------------------------------------------------------</fo:block>
		            		<fo:block  white-space-collapse="false" keep-together="always" font-weight="bold">SNO    Supply Date   Receipt No    Booth No     Amount(Rs)</fo:block>
		            		<fo:block>------------------------------------------------------------------------------------</fo:block>
		            	</fo:table-cell>		                    	                  		            
		           	</fo:table-header>		           
                    <fo:table-body>        
                    		<#list printPaymentsList as paymentListReport>
                       		<#assign SNO=(SNO+1)>
	                        <fo:table-row>
	                        	<fo:table-cell>	
	                            	<fo:block text-align="left">${SNO?if_exists}.</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell>	
	                            	<fo:block text-align="left" keep-together="always"  padding="1pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentListReport.paymentDate, "MMM d,yyyy")}</fo:block>                               
	                            </fo:table-cell>
	                        	 <fo:table-cell>
	                            	<fo:block text-align="right">${paymentListReport.paymentId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell>
	                            	<fo:block text-align="right" font-weight="bold" text-indent="0.02in">${paymentListReport.facilityId?if_exists}</fo:block>	                               
	                            </fo:table-cell>
	                        	<fo:table-cell>
	                            	<#assign totalPaymentAmount =(totalPaymentAmount+ paymentListReport.amount) >
	                            	<fo:block text-align="right" font-weight="bold">${paymentListReport.amount?if_exists?string("#0.00")}</fo:block>	                               
	                            </fo:table-cell>	  	                            	                            
	                        </fo:table-row>
                       </#list>
                     		<fo:table-row>
                       			<fo:table-cell>
                       				<fo:block>------------------------------------------------------------------------------------</fo:block>
	                       		</fo:table-cell>
	               			</fo:table-row>
                       		<fo:table-row  font-weight="bold">	                        	
	                            <fo:table-cell>
	                            	<fo:block text-align="left" font-weight="bold"> Total : </fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell/>	                            	                           
	                            <fo:table-cell/>
	                             <fo:table-cell/>	                            
	                            <fo:table-cell>	                            	
	                            	<fo:block text-align="right" font-weight="bold">${totalPaymentAmount?if_exists}.00</fo:block>	                               
	                            </fo:table-cell>	  	                            	                            
	                        </fo:table-row> 
	                        <fo:table-row>
                       			<fo:table-cell>
                       				<fo:block>------------------------------------------------------------------------------------</fo:block>
	                       		</fo:table-cell>
	               			</fo:table-row>
	               	</fo:table-body>
                </fo:table>
    	 	</fo:block>
         	<fo:block keep-toghether="always" white-space-collapse="false">Login Id: ${userLogin.userLoginId?if_exists}</fo:block>
         	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>         	
         	<fo:block text-align="center" font-weight="bold" font-size="10pt" text-indent="2in" white-space-collapse="false">
				<#if printPaymentsList[0].paymentLocation?exists>         	
         			<#if printPaymentsList[0].paymentLocation =="HO" || printPaymentsList[0].paymentMethodTypeId =="ESEVA_PAYIN" || printPaymentsList[0].paymentMethodTypeId =="APONLINE_PAYIN">
         				Cashier
         			<#else>
         				<#assign zoneDetails = delegator.findOne("Facility", {"facilityId" : printPaymentsList[0].paymentLocation?if_exists}, true)>
         				ZI ${zoneDetails.facilityName?if_exists}
         		</#if>
         		</#if>
         	</fo:block>
           </fo:flow>
        </fo:page-sequence>
        </#if>
     </fo:root>
</#escape>