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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
		<#if partyAddressMap?has_content>         
        <#assign partyList = partyAddressMap.entrySet()> 
           <#if enquiryMap?has_content>
         <#list partyList as partyEntry>   
          <#assign partyId=partyEntry.getKey()> 
          <#assign addressDetails=partyEntry.getValue()>
         
          <fo:page-sequence master-reference="main">
			    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				    <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				    <fo:block text-align="center" keep-together="always"  >&#160;--------------------------------------------------------------</fo:block>
				    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;        TEL NOS:22179004 /41        FAX :   080-20462652                    TIN:  ${companyTinNumber?if_exists} </fo:block>
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >       &#160; 22179074 /55        Email: purchase@motherdairykmf.in       KST NO: 90700065  </fo:block>
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >       &#160;                               enggpur@motherdairykmf.in     CST NO: 90750060 </fo:block>
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >------------------------------------------------------------------------------------------------------- </fo:block>
				    <fo:block text-align="center" keep-together="always">                                               ENQUIRY                                                  </fo:block>				    
			        </fo:static-content>	
			        <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	 			       
			        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   
				    <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;&#160; ENQUIRY NO.: ${custRequestId?if_exists}                                                          ENQUIRY DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(custReqDate, "dd-MMM-yyyy")?if_exists}</fo:block>
			        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;VENDOR NO. :${partyId?if_exists}                                                          FAX NO: ${partyEntry.getValue().get("faxNumber")?if_exists}</fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;To: ${partyEntry.getValue().get("partyName")?if_exists}                                           </fo:block>                    
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160; ${partyEntry.getValue().get("address1")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160; ${partyEntry.getValue().get("address2")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160; ${partyEntry.getValue().get("city")?if_exists}</fo:block>				 
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160; ${partyEntry.getValue().get("postalCode")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160; PHONE NO: ${partyEntry.getValue().get("contactNumber")?if_exists}</fo:block>	                
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block  font-size="12pt" text-align="left">&#160;&#160;   Dear Sir,                                                                                                     </fo:block> 	             
	                <fo:block  font-size="12pt" keep-together="always" text-align="center">&#160;&#160;          We Intend to purchase the following items from the regular manufacturer/dealer and requested to</fo:block>   
	                <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">&#160;&#160; quote your lowest competitive rate subjected to the terms and conditions stipulated below.The sealed</fo:block>     
	                <fo:block  font-size="12pt" keep-together="always" white-space-collapse="false">&#160;&#160; quotation duly superscribed mentioning the enquiry no,date and due date should reach this office on or </fo:block>      	                
                    <fo:block  font-size="12pt" keep-together="always" white-space-collapse="false">&#160;&#160; before <#if dueDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dueDate, "dd-MMM-yyyy")?if_exists}<#else>.</#if> </fo:block>
	                <fo:block font-family="Courier,monospace">
	                <fo:table>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="120pt"/>
					<fo:table-column column-width="220pt"/>
					<fo:table-column column-width="180pt"/>
					<fo:table-column column-width="120pt"/>
					     <fo:table-body>
					         <fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always"  >&#160;&#160;-----------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					         <fo:table-row>
					            <fo:table-cell>
									<fo:block text-align="center" >SL.No</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="left"  >ITEM CODE</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="left" >DESCRIPTION</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" >UNIT</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >REQURD QTY</fo:block>
								</fo:table-cell>
					    </fo:table-row>
					    <fo:table-row >
							<fo:table-cell >
							    <fo:block text-align="left" keep-together="always"  >&#160;&#160;-----------------------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
						</fo:table-row>
					 </fo:table-body> 
			       </fo:table>
			       </fo:block>		             	   			 
	               <fo:block font-family="Courier,monospace"  font-size="10pt">
	                   <fo:table>
					      <fo:table-column column-width="80pt"/>
					     <fo:table-column column-width="120pt"/>
					     <fo:table-column column-width="220pt"/>
					     <fo:table-column column-width="180pt"/>
					     <fo:table-column column-width="120pt"/>
					          <fo:table-body>					          
					          <#assign sno=1>
				                  <fo:table-row >
					                 <fo:table-cell>
									    <fo:block text-align="center" keep-together="always" >${sno?if_exists}</fo:block>
								     </fo:table-cell>
								     <fo:table-cell >
									    <fo:block text-align="left"  >${enquiryMap.get("itemCode")?if_exists}</fo:block>
								    </fo:table-cell>
								    <fo:table-cell >
									   <fo:block text-align="left"  >${enquiryMap.get("description")?if_exists}</fo:block>
								   </fo:table-cell>
								   <fo:table-cell >
									  <fo:block text-align="center" keep-together="always" >${enquiryMap.get("unit")?if_exists}</fo:block>
								   </fo:table-cell>
								   <fo:table-cell >
									  <fo:block text-align="center" keep-together="always" >${enquiryMap.get("requrdqty")?if_exists}</fo:block>
								   </fo:table-cell>
					           </fo:table-row>
					      <#assign sno=sno+1>                              
					     </fo:table-body>  
					  </fo:table>
					 <fo:block text-align="left" keep-together="always"  >&#160;&#160;--------------------------------------------------------------------------------------------------------------------------</fo:block>
	               </fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;Terms and Conditions :</fo:block>     
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;1.Mother Dairy is not responsible for the premature opening of the quatations.When they are not properly </fo:block> 
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;sealed and superscribed.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;2.All statutory taxes,duties,levies and other expenditure, should be clearly indicated with breakup.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;3.The price should be quoted on F.O.R Mother Dairy basis.  </fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;4.Please mention your TIN and CST No.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;5.The validity of your offers should be 60 days from date of due date.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;6.Substandard / material not as per our specifications are liable for rejection.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;7.Payment will be made within 30 days from date of satisfactory receipt of the materials.</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;8.If you are a authorized dealer please send the dealership certificate and manufacturers price list.</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;  Thanking you,                                                                  Yours faithfully</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                                 for MOTHER DAIRY</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	              <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                                 MANAGER(PURCHASE)</fo:block>
	           </fo:flow>   
	      </fo:page-sequence>          
         </#list>
          </#if> 
         <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
     </fo:root>
</#escape>	        