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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.2in" margin-bottom=".3in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.0in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "TDSReport.pdf")}

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			
			 <fo:static-content flow-name="xsl-region-before">
                       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		   <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Page No.<fo:page-number/> </fo:block>
            </fo:static-content>
			
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	   
<fo:block  keep-together="always" text-align="left" font-size = "10pt" font-family="Arial" white-space-collapse="false" font-weight= "bold">As approved by Income Tax Department</fo:block>
<fo:block  keep-together="always" text-align="center" font-size = "10pt" font-family="Calibri" white-space-collapse="false">"Form No. 26Q</fo:block> 
<fo:block  keep-together="always" text-align="center" font-size = "10pt" font-family="Calibri" white-space-collapse="false">[See section 193, 194, 194A, 194B, 194BB, 194C, 194D, 194EE, 194F, 194G, 194H, 194I, 194J, 194LA, and rule 31A]</fo:block> 
<fo:block  keep-together="always" text-align="center" font-size = "10pt" font-family="Calibri" white-space-collapse="false">Quarterly statement of deduction of tax under sub-section (3) of section 200 of the Income-tax Act in respect of payments other than salary for the quarter ended ..........................(June/September/December/March)..................(Financial Year)</fo:block> 
<fo:block  keep-together="always" text-align="center" font-size = "10pt" font-family="Calibri" white-space-collapse="false"></fo:block> 

<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

 <#assign mainCellCount= 2 />

 <#assign rowCount= 3 />
 <#assign TANEntry= "Tax Deduction and Collection Account Number (TAN)" />
 <#assign PANEntry= "Permanent AccountNumber (PAN) [See Note 1]"/>
 <#assign TANNextCell= "Has the statement been filed earlier for this quarter (Yes/No)"/>
 <#assign PANNextCell= "If answer to (d) is 'yes', then Token No. of original statement"/>
 <#assign addressCount= 11 />
 

 
 
 
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">1.</fo:block>  
 

<fo:block>
<fo:table>
	                 	<fo:table-column column-width="50%"/>
                    	<fo:table-column column-width="50%"/>
	<fo:table-body>
	<#list 1..rowCount as count>
<fo:table-row >
	  <fo:table-cell>
        <fo:block>
                 	<fo:table>
                    	<fo:table-column column-width="6%"/>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-column column-width="80%"/>
                    	<fo:table-body>
                    		 <fo:table-row>
							<fo:table-cell>
							<#if count == 1><fo:block>(a)</fo:block><#elseif count == 2><fo:block>(b)</fo:block><#else><fo:block>(c)</fo:block></#if>
			            		</fo:table-cell>
			       			<fo:table-cell>
			       			<#if count == 1>
			            		<fo:block text-align="left">${TANEntry}</fo:block>
			            	<#elseif count == 2>
			          			<fo:block text-align="left">${PANEntry}</fo:block>
			          		<#else>
			          			<fo:block text-align="left">Financial Year</fo:block>
			          		</#if>
			          		</fo:table-cell>
			       		<fo:table-cell>
			            		<fo:block>
								<fo:table border-style="solid">
                    				<fo:table-column column-width="50%"/>
                    				<fo:table-body>
	                    			<fo:table-row >
	                    				<fo:table-cell border-style="solid">
										<#if count == 1>
			            				<fo:block text-align="center">${tanNumber?if_exists}</fo:block>
			            				<#elseif count == 2>
			          					<fo:block text-align="center">${panNumber?if_exists}</fo:block>
			          					<#else>
			          					<fo:block text-align="center">${customTimePeriodValue?if_exists}</fo:block>
			          					</#if>                       					
      					</fo:table-cell>			
                       			
                       				</fo:table-row >
                       			 </fo:table-body>
                       			</fo:table> 
			            		</fo:block>
			       			</fo:table-cell>
			       		</fo:table-row>
					</fo:table-body>
                </fo:table>
               </fo:block> 
        </fo:table-cell>
        	  <fo:table-cell>
<fo:block>
                 	<fo:table>
                    	<fo:table-column column-width="6%"/>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-column column-width="80%"/>
                    	<fo:table-body>
                    		 <fo:table-row>
							<fo:table-cell>
			            		<#if count == 1><fo:block>(d)</fo:block><#elseif count == 2><fo:block>(e)</fo:block><#else><fo:block>(f)</fo:block></#if>
			       			</fo:table-cell>
			       			<fo:table-cell>
			       			<#if count == 1>
			            		<fo:block text-align="left">${TANNextCell}</fo:block>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			            	<#elseif count == 2>
			          			<fo:block text-align="left">${PANNextCell}</fo:block>
			          			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			          		<#else>
			          			<fo:block text-align="left">Type of Deductor (See Note 2)</fo:block>
			          			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			          		</#if>
			          		</fo:table-cell>
			       			<fo:table-cell>
			            		<fo:block>
								<fo:table border-style="solid">
                    				<fo:table-column column-width="50%"/>
                    				<fo:table-body>
	                    			<fo:table-row >
	                    				<fo:table-cell border-style="solid">
                            				<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" font-weight="bold">-----</fo:block>  
                       					</fo:table-cell>
                       				</fo:table-row >
                       			 </fo:table-body>
                       			</fo:table> 
			            		</fo:block>
			       			</fo:table-cell>
			       		</fo:table-row>
					</fo:table-body>
                </fo:table>
               </fo:block>            
             </fo:table-cell>
		</fo:table-row >
		</#list>
 	</fo:table-body>
  </fo:table> 
</fo:block> 
 




 <fo:block>
		<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">2. Particulars of the Deductor</fo:block>  
		<fo:block>
							<fo:table>
                    	<fo:table-column column-width="20%"/>
                    	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    	
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left" font-size="10" font-weight="bold">(a) Name</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${partyGroup}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
		</fo:block>
		<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">(b) If Central/State Government</fo:block>  
	       			<fo:block>
			       			<fo:table>
                    	<fo:table-column column-width="20%"/>
                    	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    	
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">Name(see Note 3)</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                     <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">AIN Code of PAO/TO/CDDO</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
                    </fo:block>
                    
                    <fo:block>
			       			<fo:table>
                    	<fo:table-column column-width="20%"/>
                    	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    <fo:table-row>
			       			<fo:table-cell><fo:block  font-weight="bold" text-align="left">(c) TAN Registration No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${tanNumber}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
                    </fo:block>
                    
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">(d) Address</fo:block>  
					 <fo:block>
			       			<fo:table>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-body>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Flat No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${address1}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Name of the premises/building</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${address2}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Road/Street/Lane</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Area/Location</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Town/City/District</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${city}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;State</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${state}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;PIN Code</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${postalCode}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Telephone No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${telephone}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Alternate telephone No. (See Note 4)</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Email</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >${email}</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Alternate email (See Note 4)</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
                    </fo:block>
                    
			       			
							
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">3. Particulars of the person responsible for deduction of tax:</fo:block>  
					
					<fo:block>
			       			<fo:table>
                    	<fo:table-column column-width="20%"/>
                    	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    <fo:table-row>
			       			<fo:table-cell><fo:block  font-weight="bold" text-align="left">(a) Name</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
                    </fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">(b) Address</fo:block>  
					<fo:block>
			       			<fo:table>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-body>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Flat No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Name of the premises/building</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Road/Street/Lane</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Area/Location</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Town/City/District</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;State</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;PIN Code</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Telephone No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Alternate telephone No. (See Note 4)</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Email</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Alternate email (See Note 4)</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
			       			<fo:table-cell><fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;Mobile No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid">
                            	<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" >-----</fo:block>  
                       		</fo:table-cell>
                    </fo:table-row>
                    </fo:table-body>
                    </fo:table>
                    </fo:block>
</fo:block> 
<fo:block page-break-after="always"></fo:block>


<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">4. Details of tax deducted and paid to the credit of the Central Government:</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block>
<fo:table border-style="solid">
                    	<fo:table-column column-width="3%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="7%"/>
                    	<fo:table-column column-width="6%"/>
                    	<fo:table-column column-width="6%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="11%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="10%"/>
                    	
                    	<fo:table-body>
                    	
                    	<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Sl. No.</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Internal Reference Number</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Tax</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Interest</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Fee (SeeNote 5)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Penalty /Others</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Total amount deposited as per challan/Book Adjustment(402 + 403+ 404+ 405)(See Note 6)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Mode of deposit through Challan (C)/Book Adjustment(B)(See Note 7)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">BSR code/Receipt Number of	Form No.24G(See Note 8)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Challan Serial No./DDO Serial no.of Form No.24G(See Note 8)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Date on which amount deposited through challan/ Date of transfer (dd/mm/yyyy)(See Note 8) </fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Minor Head of Challan (See Note 9)</fo:block></fo:table-cell>
			       		</fo:table-row>
			       	<#assign rcount = 0>
			       		<#list listTaxPaid as taxPaid>
			       		<#assign rcount = rcount+1>
			       		<fo:table-row>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" >${rcount}</fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" >${taxPaid.get("invoiceId")}</fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if taxPaid.get("tax")?has_content>${taxPaid.get("tax")?string("#.00")}</#if></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if taxPaid.get("interest")?has_content>${taxPaid.get("interest")?string("#.00")}</#if></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if taxPaid.get("fee")?has_content>${taxPaid.get("fee")?string("#.00")}</#if></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if taxPaid.get("penalty")?has_content>${taxPaid.get("penalty")?string("#.00")}</#if></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10">${taxPaid.get("total")?string("#.00")}</fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10"></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10"></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10"></fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${taxPaid.get("paidDate")}</fo:block> </fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10"></fo:block> </fo:table-cell>
			       		</fo:table-row>
			       		</#list>
			       </fo:table-body>
        	</fo:table>
</fo:block> 
<fo:block page-break-after="always"></fo:block>

<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">5. Details of amount paid and tax deducted thereon from the deductees (see Annexure)</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="12" white-space-collapse="false" font-weight="bold">Verification</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">I, ................................................................, hereby certify that all the particulars furnished above are correct and complete</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">Place: ...................												 																																																									Signature of the person responsible for deducting tax at source</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">Date: ....................												 																																																									Name and designation of the person responsible for deducting tax at source</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false" font-weight="bold">Notes:</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">1.It is mandatory for non-Government deductors to quote PAN. In case of Government deductors,"PANNOTREQD" should be mentioned.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">2.Indicate deductor category as per Annexure 1. </fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">3.In case of Central Government, please mention name of Ministry/Department. In case of State Government, please mention name of the State.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">4.In alternate telephone number and alternate email, please furnish the telephone number and email of a person who can be contacted in the absence of deductor </fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">&#160; or person responsible for deduction of tax.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">5.Fee paid under section 234 E for late filling of TDS statement to be mentioned in separate column of 'Fee' (column 404).</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">6.In column 406, Government DDOs to mention the amount remitted by the PAO/CDDO/DTO. Other deductors to write the exact amount deposited through challan. </fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">7.In column 308, mention “N”. In case of nil challan, do not mention any value.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">8.Challan / Transfer Voucher (CIN / BIN) particulars , i.e. 408, 409, 410 should be exactly the same as available at Tax Information Network.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false"> &#160; In case of nil challan, mention last date of the respective quarter for which statement is being filed.</fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">9.In column 411, mention minor head as marked on the challan. </fo:block>  
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">10.All the amount columns are mandatory, if not applicable mention as 0.00.</fo:block>  

<fo:block page-break-after="always"></fo:block>

<fo:block  keep-together="always" text-align="center" font-size="13" white-space-collapse="false" font-weight="bold">ANNEXURE : DEDUCTEE WISE BREAK UP OF TDS (Section Code:${sectionCode?if_exists})</fo:block>  
<fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false" font-weight="bold">(Please use separate Annexure for each line-item in Table at Sl. No. 04 of main Form 26Q)</fo:block>  
<fo:block  keep-together="always" text-align="center" font-size="10" white-space-collapse="false">Details of amount paid/credited during the quarter ended........... (dd/mm/yyyy) and of tax deducted at source</fo:block>
<fo:block  keep-together="always" text-align="center" font-size="11" white-space-collapse="false" font-weight="bold">    From :: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromMonthTime, "MMMM-yyyy")}  TO:: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruMonthTime, "MMMM-yyyy")}</fo:block>


<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

<fo:block>
<fo:table>
                    	<fo:table-column column-width="75%"/>
                    	<fo:table-column column-width="25%"/>
                    <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
                    
    				<fo:table>
  					<fo:table-column column-width="60%"/>
   	  			 	<fo:table-column column-width="30%"/>
                    <fo:table-body>
                    
                    
                    	<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >BSR Code of branch/Receipt Number of Form No. 24G</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>
						<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >Date on which challan deposited/Transfer voucher date (dd/mm/yyyy)</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>
                    	<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >Challan Serial Number / DDO Serial No. of Form No. 24G</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>
                    	<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >Amount as per ChallanG</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>
                    	<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >Total tax to be allocated among deductees as in the vertical total of Col. 421</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>
                    	<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" >Total interest to be allocated among the deductees mentioned below</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" ></fo:block></fo:table-cell>
                    	</fo:table-row>    
                   </fo:table-body>
                   </fo:table>
                  </fo:table-cell>
                  
                  <fo:table-cell >
                   <fo:table>
  								<fo:table-column column-width="50%"/>
      							<fo:table-column column-width="60%"/>
                    <fo:table-body>
                    			<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" font-weight="bold">Name of the Deductor</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${deductorName}</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			<fo:table-row>
                    	<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="10" font-weight="bold">TAN</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${TAN}</fo:block></fo:table-cell>
                    			</fo:table-row>
                    			</fo:table-body>
                   </fo:table>
                   </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
         </fo:table>
         </fo:block>
         
         <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
         
         
<fo:block>
<fo:table border-style="solid">
                    	<fo:table-column column-width="3%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="10%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="8%"/>
                    	<fo:table-column column-width="8%"/>
                    	
                    	<fo:table-body>
                    	
                    	<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Sl. No.</fo:block></fo:table-cell>
			       			<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Deductee Reference Number provided by The deductor, if Available</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Deductee code (01 Company 02 Other than company)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">PAN of The deductee</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Name of the deductee</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" font-weight="bold">Section code (see Note 1)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Date of payment or credit(dd/mm/yyyy)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Amount paid or credited</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Total tax deducted</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Total tax deposited</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Date of deduction(dd/mm/yyyy)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Rate at which deducted</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Reason for non-deduction/lower deduction/Higher Deduction/Threshold/Transporter/(see note2) </fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block  text-align="center" font-size="10" font-weight="bold">Number of thecertificate under section 197 issued by the Assessing Officer for non deduction/lower Deduction(see note 3)</fo:block></fo:table-cell>
			       		</fo:table-row>   
			       			<#assign rcount = 0>
			       		<#list listAnnexure as mList>
			       		<#assign rcount = rcount+1>
			       		<fo:table-row>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" >${rcount}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("partyId")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("code")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("panNo")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("partyName")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("section")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("paidDate")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10">${mList.get("invoiceAmount")?string("#.00")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if mList.get("amount")?has_content>${mList.get("amount")?string("#.00")}</#if></fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="right" font-size="10"><#if mList.get("amount")?has_content>${mList.get("amount")?string("#.00")}</#if></fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10">${mList.get("invoiceDate")}</fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell border-style="solid"><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		
			       		</fo:table-row >
			       		</#list>
			       		<fo:table-row border-style="solid">
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-weight="bold" font-size="10" >TOTAL: </fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block  font-weight="bold" text-align="right" font-size="10">${invoiceTotal?string("#.00")}</fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block  font-weight="bold" text-align="right" font-size="10">${total?string("#.00")}</fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block  font-weight="bold" text-align="right" font-size="10">${total?string("#.00")}</fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10"></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		<fo:table-cell><fo:block   text-align="center" font-size="10" ></fo:block></fo:table-cell>
			       		
			       		</fo:table-row>
			       </fo:table-body>
        	</fo:table>
</fo:block>       

<fo:block page-break-after="always"></fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="12" white-space-collapse="false" font-weight="bold">Verification</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">I, ................................................................, hereby certify that all the particulars furnished above are correct and complete</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">Place: 		BANGALORE												 																																																									Signature of the person responsible for deducting tax at source</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">Date: ....................												 																																																									Name and designation of the person responsible for deducting tax at source</fo:block>  
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">Notes:</fo:block>

<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">1.Mention section code as per Annexure 2:</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">2.Mention remarks for lower/ no/ higher deduction as per Annexure 3</fo:block>
<fo:block  keep-together="always" text-align="left" font-size="10" white-space-collapse="false">3.Mandatory to mention certificate no. in case of lower or no deductionas per column no. 424</fo:block>
<fo:block page-break-after="always"></fo:block>

<fo:block>
							<fo:block   text-align="left" font-size="12" font-weight="bold">Annexure 1-Deductor category</fo:block>

<fo:table border-style="solid">
                    	<fo:table-column column-width="25%"/>
                    	<fo:table-body>
                    	
	                	<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Deductor category</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Statutory body (Central Govt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Statutory body (State Govt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Autonomous body (CentralGovt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Autonomous body (State Govt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Local Authority (Central Govt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Local Authority (State Govt.)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Branch / Division of Company</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Association of Person (AOP)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Association of Person (Trust)</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Artificial Juridical Person</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Body of Individuals</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Individual/HUF</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Firm</fo:block></fo:table-cell>
						</fo:table-row>
				
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		
		<fo:block>
							<fo:block   text-align="left" font-size="12" font-weight="bold">Annexure 2-Section Code</fo:block>
							<fo:table border-style="solid">
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="30%"/>
                    	<fo:table-column column-width="5%"/>

                    	<fo:table-body>
                    	
	                	<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Section</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Nature of Payment</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Section Code</fo:block></fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">193</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Interest on Securities</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">193</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Dividend</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194</fo:block></fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194A</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Interest on than interest on securities</fo:block></fo:table-cell>	
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94A</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194B</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Winnings from lotteries and crossword puzzles</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94B</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194BB</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Winnings from horse race</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">4BB</fo:block></fo:table-cell>
							
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194C</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Payment of conctractor and sub-Contractor</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94C</fo:block></fo:table-cell>
						
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194EE</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Payment in respect of deposit under national savings scheme</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">4EE</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194F</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Payments on account of re-purchase of Units by Mutual Funds or UTI</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94F</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194G</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Commission, prize etc., on sale of lottery tickets</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94G</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194H</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Commission or Brokerage</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94H</fo:block></fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194I (a)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Rent</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">4IA</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194I (b)</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Rent</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">4IB</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194J</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Fees for Professional or Technical Services</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94J</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">194LA</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Payment of Compensation on acquisition of certain immovable property</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">94L</fo:block></fo:table-cell>
						</fo:table-row>
				
				</fo:table-body>
			</fo:table>
		</fo:block>
						
						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		
		<fo:block>
							<fo:block   text-align="left" font-size="12" font-weight="bold">Annexure 3-–Remarks for lower/ no / higher deduction</fo:block>
							<fo:table border-style="solid">
                    	<fo:table-column column-width="50%"/>
                    	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="7%"/>

                    	<fo:table-body>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Particulars</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Code</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Whether PAN mandatory</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">In case of lower deduction/no deduction on account of certificate under section 197</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">A</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">In case of no deduction on account of declaration under section 197A. Allowed only for section194, 194A, 194EE and 193</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">B</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
							
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">In case of deduction of tax at higher rate due to non-availability of PAN</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">C</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">NO</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">In caseof Transporter transaction and valid PAN is provided [section 194C(6)]</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">T</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Transaction where tax not been deducted as amount paid/credited to the vendor/party has not exceeded the threshold limit (as per the provisions of income tax act). Applicable for sections193,194, 194A, 194B, 194BB, 194C, 194D, 194EE, 194G, 194H, 194I, 194J, 194LA.</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Y</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">For software acquired under section 194J (Notification 21/2012). Applicable from FY 2012-13onwards.</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">S</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">In case of no deduction on account of payment under section 197A</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">Z</fo:block></fo:table-cell>
							<fo:table-cell border-style="solid"><fo:block   text-align="left" font-size="12">YES</fo:block></fo:table-cell>
						</fo:table-row>
						
				
				</fo:table-body>
			</fo:table>
		</fo:block>
		 
		
  </fo:flow>
 			
            
  
  
			 </fo:page-sequence>	
   </fo:root>
</#escape>