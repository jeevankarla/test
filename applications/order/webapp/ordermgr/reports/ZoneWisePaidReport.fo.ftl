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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom=".5in" margin-left=".5in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>

<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
				<fo:block text-align="left" keep-together="always" font-size="11pt" white-space-collapse="false">.            ${uiLabelMap.ApDairyMsg}</fo:block>
				<fo:block text-align="left" keep-together="always" font-size="13pt" white-space-collapse="false">.                 Zone Wise Cash Receivable Report ON : ${paymentDate}</fo:block>
				<fo:block font-size="10pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="10pt" linefeed-treatment="preserve" font-family="Courier,monospace">SNO     TRANSPORTER     ZONE      RECBLE       ZONE        MPF       E-SEVA    AP-ONLINE     TOTAL    DUE</fo:block>
            	<fo:block white-space-collapse="false" font-size="10pt" linefeed-treatment="preserve" font-family="Courier,monospace">.        NAME                     AMOUNT       Recd        Recd      Recd       Recd         Recd     AMT</fo:block> 
            	<fo:block font-size="10pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			
		<fo:block  font-size="10pt"  font-family="Times" font-style="normal" font-weight="normal">
            <fo:table width="100%" table-layout="fixed" space-after="0.0in">
        		<fo:table-column column-width="50pt"/>
				<fo:table-column column-width="25pt"/>
				<fo:table-column column-width="30pt"/>
				<fo:table-column column-width="45pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="65pt"/>
				<fo:table-column column-width="55pt"/>
				<fo:table-column column-width="55pt"/>
				<fo:table-body>
				<#assign distributorsValue = distributorMap.entrySet()>
				<#list distributorsValue as distributor>
					<#if  distributor.getKey() != "Distributors" && distributor.getKey() != "Franchisees" && distributor.getKey() != "H-O Trade sample" && distributor.getKey() != "Rsm -Parlours">
					<fo:table-row>
						<fo:table-cell></fo:table-cell>
						<fo:table-cell><fo:block>${distributor.getKey()}</fo:block></fo:table-cell>
					</fo:table-row>
					</#if>
					<#assign distributorZones= (distributor.getValue())>
						<#list distributorZones as zoneName>
					<fo:table-row>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<#if  zoneName != "FR" && zoneName != "HO" && zoneName != "CD">
						<fo:table-cell><fo:block font-size="10pt">${zoneName}</fo:block> <fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
						</#if>
					</fo:table-row>	
					</#list>
				</#list>
					<#assign sno=0>
					<#assign zoneWisePaidAmt=zoneWisePaidMap.entrySet()>
					<#assign total =0>
				<#list zoneWisePaidAmt as zoneWisePayment>
					<#assign sno=sno+1>
					<#assign zoneWisePaymentValue = (zoneWisePayment.getValue())>
                	<#assign totalRecd = (zoneWisePaymentValue.get("ZONE_PAYIN")+zoneWisePaymentValue.get("CASH_HO_PAYIN")+zoneWisePaymentValue.get("ESEVA_PAYIN")+zoneWisePaymentValue.get("APONLINE_PAYIN"))>
					<#assign dueAmt = (zoneWisePaymentValue.get("TOTAL_RECBLE")- totalRecd)>
					<fo:table-row> 
						<fo:table-cell><fo:block>${sno}</fo:block></fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<fo:table-cell><fo:block></fo:block></fo:table-cell>
						<fo:table-cell><fo:block text-align="right">${zoneWisePayment.getKey()}</fo:block></fo:table-cell>
						<fo:table-cell><fo:block text-align="right" keep-together="always">${zoneWisePaymentValue.get("TOTAL_RECBLE")?if_exists}</fo:block></fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${zoneWisePaymentValue.get("ZONE_PAYIN")?if_exists}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${zoneWisePaymentValue.get("CASH_HO_PAYIN")?if_exists}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${zoneWisePaymentValue.get("ESEVA_PAYIN")?if_exists}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${zoneWisePaymentValue.get("APONLINE_PAYIN")?if_exists}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${totalRecd?if_exists}</fo:block>
                		</fo:table-cell>
                		<fo:table-cell>
                				<fo:block text-align="right" keep-together="always">${dueAmt?if_exists}</fo:block>
                		</fo:table-cell>
                   </fo:table-row>
                   <fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell></fo:table-row>
                 </#list>
				</fo:table-body>
			</fo:table>
		</fo:block>	
		<fo:block></fo:block>
	  </fo:flow>						        	
   </fo:page-sequence>
   </fo:root>
</#escape>