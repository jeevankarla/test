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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top=".2in"/>
          <fo:region-before extent=".5in"/>
          <fo:region-after extent=".5in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
        <fo:page-sequence master-reference="main">
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block>      
	<fo:table table-layout="fixed" width="100%">
	<fo:table-column column-width="7in"/>
		<fo:table-body>
				<fo:table-row>
					<fo:table-cell>	
						<fo:block font-size="8pt" text-align="left">             
             			<#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" width="60px" height="60px" content-height="scale-to-fit"/></#if>             
         				</fo:block>	
					</fo:table-cell>
				</fo:table-row>	
				<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="right">FORM-01(A)</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block></fo:table-cell>
				</fo:table-row>	
         		<fo:table-row>
					<fo:table-cell>	
						<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center">FORM OF ANNUAL INFORMATION ON FACTORY/ESTABLISHMENT </fo:block>
						<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center">COVER UNDER ESI ACT</fo:block>
						<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="center">(Regulation 10C)</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
 					<fo:table-cell><fo:block keep-together="always" text-align="left" >Employers Code No. ____________________________</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block></fo:table-cell>
				</fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<fo:table layout="fixed" width="100%">
   		<fo:table-column column-width=".4in"/>
   		<fo:table-column column-width="3.1in"/>
   		<fo:table-column column-width="3.4in"/>
   			<fo:table-body border="solid">
   				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >1.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Name of the Factory/Establishment</fo:block></fo:table-cell>
					<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
   				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >2.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >Complete Postal Address of the Factory/</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Establishmen</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >.</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >Pin</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >3.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >a)Telephone No., if any</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >b)Fax No., if any</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >c)E-mail address, if any</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >4.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >Location of Factory/Establishment</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" > . </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >.</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >a) State</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >b) District</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >c) Municipality/Ward </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >d) Name of Town/Revenue Village</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >(Taluk/Tehsil)</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >e) Police Station </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >f) Revenue Demarcation/Hudbast No. </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >5.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >Details of Bank Account</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >Name of Bank and Branch</fo:block></fo:table-cell>
    			</fo:table-row>	
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >a)  Account No. ____________</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >__________________________</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >b)  Account No. ____________</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >__________________________</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >c)  Account No. ____________</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >__________________________</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >6.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >a)  Income Tax PAN/GIR No</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" > . </fo:block></fo:table-cell>
    			</fo:table-row>	
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >b)  Income Tax Ward/Circle/Area</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false"  border-bottom="solid">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>			
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block break-before="page"/><fo:block border-right="solid" border-top="solid" text-align="center" >7.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >a)  In case of factory whether Licence </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" > . </fo:block></fo:table-cell>
    			</fo:table-row>	
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >issued Under Section 2(m) (i) or 2(m) </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >(ii) of the Factories Act, 1948 </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >b)  Power Connection No.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >No.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Sanctioned Power Load</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Issuing Authority</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >8.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >a)    Whether it is Public or Private Ltd., </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" > . </fo:block></fo:table-cell>
    			</fo:table-row>	
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" > Company/Partnership/Proprietorship/ </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Cooperative Society/Ownership (attach </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >copy of Memorandum and Articles of </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Association/Partnership Deed/Resolution</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false" border="solid">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" white-space-collapse="false" border-left="solid" border-top="solid" text-align="left" >b) Give name, present and permanent residential address of present        Proprietor/Managing Directors,        Director/ Managing Partners,          Partners/Secretary of the Cooperative Society.</fo:block><fo:block border-right="solid" border-left="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell>    				
    					<fo:table table-layout="fixed" width="100%">
    					<fo:table-column column-width=".4in"/>
						<fo:table-column column-width="1in"/>
						<fo:table-column column-width="1in"/>
						<fo:table-column column-width="1in"/>
							<fo:table-body>
								<fo:table-row  border-bottom="solid">	
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >i)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" font-weight="bold" text-align="center" >Name</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" font-weight="bold" text-align="center" >Designation</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" font-weight="bold" text-align="center" >Address</fo:block></fo:table-cell>
    							</fo:table-row>
    							<fo:table-row  border-bottom="solid">
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >ii)</fo:block></fo:table-cell>
	    							<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    							</fo:table-row>
    							<fo:table-row  border-bottom="solid">
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >iii)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
	    						</fo:table-row>
    							<fo:table-row  border-bottom="solid">
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >iv)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
   		 							<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    							</fo:table-row>
    							<fo:table-row  border-bottom="solid">
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >v)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    							</fo:table-row>
	    						<fo:table-row  border-bottom="solid">	
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >vi)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    							</fo:table-row>
    							<fo:table-row >	
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >vii)</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    								<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    							</fo:table-row>	
    						</fo:table-body>
						</fo:table>
					</fo:table-cell>
    			</fo:table-row>	
    			
    			<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >9.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="left" >Address(es) of the Registered Office</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" > . </fo:block></fo:table-cell>
    			</fo:table-row>	
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >/Head Office/Branch Office/Sales Office/</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Administrative Office/other offices if any,</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" > with no. of employees attached with each </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >such office and person responsible for </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >the office.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Address as on Date</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >No. of Employee</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Phone No./Fax No.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Work</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center"   border-top="solid">  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >Person responsible for day to day </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="right"  font-size="8pt" border-top="solid">(Give details on a separate sheet, if required.)</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false" border-bottom="solid">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >functioning of the office</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				
				<fo:table-row white-space-collapse="false" border-top="solid">
    				<fo:table-cell><fo:block break-before="page"/><fo:block border-right="solid" text-align="center" >10.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >a)Whether any work/business carried out </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >through contractor/immediate employer. </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center">  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >b)If yes, give nature of such</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center"  border-top="solid">  .  </fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="left" >work/business</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>		
	</fo:table>
	<fo:table table-layout="fixed" width="100%">
	<fo:table-column column-width=".5in"/>
	<fo:table-column column-width="7in"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell><fo:block></fo:block></fo:table-cell>
    			<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >I hereby declare that the statement given above is correct to the best of my knowledge and</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row>
    			<fo:table-cell><fo:block keep-together="always" text-align="left" >belief.  I also undertake to intimate changes, if any, promptly to the Regional Office/Sub Regional Office,</fo:block><fo:block keep-together="always" text-align="left" >ESI Corporation as soon as such changes take place.</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>		
	</fo:table>
		<fo:table table-layout="fixed" width="100%">
	<fo:table-column column-width="3.3in"/>
	<fo:table-column column-width="5in"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >Date _____________________</fo:block></fo:table-cell>
				<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >Name and Signature _________________________</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row>
    			<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >Place_____________________</fo:block></fo:table-cell>
    			<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >Designation with seal _________________________</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row>
    			<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" ></fo:block></fo:table-cell>
    			<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" >(Should be signed by principal employer u/s 2(17) of ESI Act</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>		
	</fo:table>
</fo:block>
    </fo:flow>
  </fo:page-sequence>  
 </fo:root>
</#escape>

