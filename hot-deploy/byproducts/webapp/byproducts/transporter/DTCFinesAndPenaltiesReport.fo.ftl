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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if contractorRoutesMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">RECOVERIES/FINES AND PENALTIES - DTC</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">From :: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMM dd, yyyy")} To:: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "MMMM dd, yyyy")}</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="9pt">SINO     &#160;CONTRACTOR  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;ROUTE &#160;&#160;CRATES    &#160;&#160;&#160;                CANS         &#160;&#160;  SECURITY   &#160; CASH  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;TRANSPORT &#160;&#160;&#160;FINES AND&#160;&#160;&#160;&#160;&#160;SUB &#160;&#160;&#160;&#160;&#160;&#160;&#160;TOTALS</fo:block>
              		<fo:block font-weight="bold" font-size="9pt">&#160;&#160;&#160;&#160;&#160; NAME	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;ID&#160;	&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160; FINES&#160;&#160;&#160;&#160;&#160;  SHORT&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;COST&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;PENALTIES&#160;&#160;&#160;&#160;&#160;TOTAL</fo:block>
              		<fo:block font-weight="bold" font-size="9pt">&#160;&#160;&#160;&#160;&#160;	&#160;&#160;&#160;&#160; &#160;&#160;&#160;	&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; REMITANCE&#160;&#160;&#160;&#160;RECOVERY&#160;&#160;&#160;&#160;&#160;OTHERS&#160;</fo:block>
            		<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="4%"/>
			        <fo:table-column column-width="13%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="11%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="9%"/>
			        <fo:table-column column-width="10%"/>
                    <fo:table-body>
                    <#assign serialNo = 1>
                        <#assign total=0>
                        <#assign grandTotal=0>
	                    <#assign cratesGTot=0>
	                    <#assign cansGTot=0>
	                    <#assign finesGTot=0>
	                    <#assign transportGTot=0>
	                    <#assign securityGTot=0>
	                    <#assign remitGTot=0>
                    	<#assign contractorMapDetails = contractorRoutesMap.entrySet()>
                  <#list contractorMapDetails as contractorDetails>
                    	<#assign total=0>
	                    <#assign cratesFineTot=0>
	                    <#assign cansFineTot=0>
	                    <#assign finesTot=0>
	                    <#assign transportFineTot=0>
	                    <#assign securityFineTot=0>
	                    <#assign remitFineTot=0>
                    	       <fo:table-row>
                    	       <fo:table-cell>
                    				<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="9pt" white-space-collapse="false">${serialNo}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="9pt" white-space-collapse="false">${contractorNamesMap.get(contractorDetails.getKey()?if_exists)}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
                    		<#assign facilityDetails = contractorDetails.getValue().entrySet()>
                    	<#list facilityDetails as prod>
                    	    <#assign total=total+prod.getValue().get("subTotal")>
                    	    <#assign cratesFineTot = cratesFineTot+prod.getValue().get("cratesFine")>
                    	    <#assign cansFineTot = cansFineTot+prod.getValue().get("cansFine")>
                    	    <#assign finesTot = finesTot+prod.getValue().get("finesAmount")>
                    	    <#assign transportFineTot = transportFineTot+prod.getValue().get("transportAmount")>
                    	    <#assign securityFineTot = securityFineTot+prod.getValue().get("securityFineAmount")>
                    	    <#assign remitFineTot = remitFineTot+prod.getValue().get("remitFinesAmount")>
                			<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="9pt" white-space-collapse="false"></fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always"  text-align="right" font-size="9pt" white-space-collapse="false">${prod.getKey()?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("cratesFine")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("cansFine")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("finesAmount")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("transportAmount")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("securityFineAmount")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("remitFinesAmount")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${prod.getValue().get("subTotal")?if_exists}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
                        </#list>
                            <#assign cratesGTot=cratesGTot+cratesFineTot>
	                        <#assign cansGTot=cansGTot+cansFineTot>
	                        <#assign finesGTot=finesGTot+finesTot>
	                        <#assign transportGTot=transportGTot+transportFineTot>
	                        <#assign securityGTot=securityGTot+securityFineTot>
	                        <#assign remitGTot=remitGTot+remitFineTot>
                            <fo:table-row>
                                 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                   <fo:block font-size="10pt">-----------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
	                         <fo:table-row>
	                             <fo:table-cell>
                    			      <fo:block text-align="left"   keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"   keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${cratesFineTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right" font-size="9pt"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${cansFineTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${finesTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${transportFineTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${securityFineTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${remitFineTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
		                           <fo:table-cell>
	                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${total}</fo:block>  
	                    			</fo:table-cell>
	                    	</fo:table-row>
                    	    <fo:table-row>
                    	        <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                   <fo:block font-size="10pt">-----------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
	                        <#assign serialNo =serialNo+1>
	               </#list>
	                <#assign grandTotal=grandTotal+cratesGTot+cansGTot+finesGTot+transportGTot+securityGTot+remitGTot>
                	
	                <fo:table-row>
                                 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                   <fo:block font-size="10pt">-----------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
	                         <fo:table-row>
	                             <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${cratesGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${cansGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right" font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${finesGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right" font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${transportGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right" font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${securityGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right" font-size="9pt" keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">${remitGTot}</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="right"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
		                           <fo:table-cell>
	                        			<fo:block  keep-together="always" text-align="right" font-size="9pt" white-space-collapse="false">${grandTotal}</fo:block>  
	                    			</fo:table-cell>
	                    	</fo:table-row>
                    	    <fo:table-row>
                    	        <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">-----------------------------------------------------------------------------------------------</fo:block>
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