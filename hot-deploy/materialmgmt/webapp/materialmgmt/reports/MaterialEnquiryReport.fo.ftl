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
            margin-top="0.2in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.1in"/>
        <fo:region-before extent="1.5in"/>
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
			        </fo:static-content>
			        <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
		            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				    <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				    <fo:block text-align="center" keep-together="always"  >&#160;-------------------------------------------------------------------</fo:block>
				    <#--
				    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;TEL NOS:${companyPhone?if_exists}         FAX  :${companyFax?if_exists}                   TIN NO :${companyTinNumber?if_exists} </fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;        ${companyAnotherPhone?if_exists}         Email:${companyMail?if_exists}    KST NO :${kstNumber?if_exists}   </fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;                                     ${companyOtherMail?if_exists}       CST NO :${cstNumber?if_exists}</fo:block>
                    -->
                    <fo:block font-weight="bold" >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="165pt"/>               
					                <fo:table-column column-width="280pt"/>               
						           <fo:table-column column-width="200pt"/>               
						           	<fo:table-body>
				                     <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >TEL NOS:${companyPhone?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >FAX&#160; : ${companyFax?if_exists}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >TIN NO:${companyTinNumber?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${companyAnotherPhone?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >Email:${companyMail?if_exists}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >KST NO:${kstNumber?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
									<fo:table-cell><fo:block text-align="left" font-size="12pt"></fo:block></fo:table-cell>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >&#160;&#160;&#160;&#160;${companyOtherMail?if_exists}</fo:block> </fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >CST NO:${cstNumber?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >----------------------------------------------------------------------------------------- </fo:block>
				    <fo:block text-align="center" keep-together="always" font-weight="bold">                                              MATERIAL ENQUIRY REPORT                                                  </fo:block>
                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;ENQUIRY SEQUENCE NO.:${enquirySequenceNo?if_exists}                           ENQUIRY DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(custReqDate, "dd-MMM-yyyy")?if_exists}</fo:block>  	 			       
				    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" >&#160;INTERNAL ENQUIRY NO.:${custRequestId?if_exists}                                    FAX NO: ${partyEntry.getValue().get("faxNumber")?if_exists}</fo:block>
			        <fo:block  keep-together="always"  text-align="left"  white-space-collapse="false" font-weight="bold">&#160;VENDOR NO.:${partyId?if_exists}                                            </fo:block>
                    <fo:block  keep-together="always" text-align="left"  white-space-collapse="false">&#160;&#160;                                                           </fo:block>                                        
                    <fo:block  keep-together="always" text-align="left"  white-space-collapse="false" font-weight="bold">&#160;To:${partyEntry.getValue().get("partyName")?if_exists}                                           </fo:block>                    
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;&#160; ${partyEntry.getValue().get("address1")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;&#160; ${partyEntry.getValue().get("address2")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;&#160; ${partyEntry.getValue().get("city")?if_exists}</fo:block>				 
				    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;&#160; ${partyEntry.getValue().get("postalCode")?if_exists}</fo:block>
				    <fo:block text-align="left" white-space-collapse="false" >PHONE NO:${partyEntry.getValue().get("contactNumber")?if_exists}</fo:block>	                
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block  font-size="12pt" text-align="left">Dear Sir,                                                                                                     </fo:block> 	             
	                <fo:block  font-size="12pt"  text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;  We Intend to purchase the following items from the regular manufacturer/dealer and</fo:block> 
	                <fo:block  font-size="12pt"  text-align="left">request you to quote your lowest competitive rate subjected to the terms and conditions stipulated below.The sealed quotation duly superscribed mentioning the enquiry no,date and due date should reach this office on or before  <#if dueDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dueDate, "dd-MMM-yyyy")?if_exists}.<#else>.</#if> </fo:block> 
	                <fo:block font-family="Courier,monospace">
	                <fo:table>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="220pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					     <fo:table-body>
					         <fo:table-row >
							   <fo:table-cell >
				                  <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						     </fo:table-row>
					         <fo:table-row>
					            <fo:table-cell>
									<fo:block text-align="center" font-weight="bold">SL.No</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="left" font-weight="bold">ITEM CODE</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="left" font-weight="bold">MATERIAL NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" font-weight="bold">UNIT</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" font-weight="bold">REQURD QTY</fo:block>
								</fo:table-cell>
					    </fo:table-row>
					    <fo:table-row >
							<fo:table-cell >
				                <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
						</fo:table-row>
					 </fo:table-body> 
			       </fo:table>
			       </fo:block>		  	   	   			 
	               <fo:block font-family="Courier,monospace"  font-size="10pt">
	                   <fo:table>
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="90pt"/>
					       <fo:table-column column-width="220pt"/>
					       <fo:table-column column-width="100pt"/>
					       <fo:table-column column-width="100pt"/>
					       <fo:table-column column-width="50pt"/>  
					          <fo:table-body>
					           <#assign sno=1>	
				              <#assign productList = enquiryMap.entrySet()>                                                                 					          
                               <#list productList as productEntry>
                               <#assign productd=productEntry.getKey()> 
                                <#assign productDetails=productEntry.getValue()>                                  
				                  <fo:table-row >
					                 <fo:table-cell>
									    <fo:block text-align="center" keep-together="always" font-size="12pt" >${sno?if_exists}</fo:block>
								     </fo:table-cell>
								     <fo:table-cell >
									    <fo:block text-align="left"  font-size="12pt" >${productDetails.get("itemCode")?if_exists}</fo:block>
								    </fo:table-cell>
								    <fo:table-cell >
									   <fo:block text-align="left"  font-size="12pt" >${productDetails.get("description")?if_exists}</fo:block>
								   </fo:table-cell>
								   <fo:table-cell >
									  <fo:block text-align="center"  font-size="12pt">${productDetails.get("unit")?if_exists}</fo:block>
								   </fo:table-cell>
								   <fo:table-cell >
									  <fo:block text-align="right" keep-together="always" font-size="12pt" >${productDetails.get("requrdqty")?if_exists?string("##0.000")}</fo:block>
								   </fo:table-cell>
					           </fo:table-row>
					           <#assign sno=sno+1> 
                              </#list>  					                                                            
					     </fo:table-body>  
					  </fo:table>
				           <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------------------------</fo:block>
	                </fo:block>	              
	               <fo:block  font-size="12pt"  white-space-collapse="false">&#160; </fo:block>        
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left"> Terms and Conditions :</fo:block>     
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">1.Mother Dairy is not responsible for the premature opening of the quatations.When they  </fo:block> 
	                <fo:block  font-size="12pt"  white-space-collapse="false">are not properly sealed and superscribed.</fo:block>        
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">2.All statutory taxes,duties,levies and other expenditure, should be clearly indicated with breakup.</fo:block>
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">3.The price should be quoted on F.O.R Mother Dairy basis.  </fo:block>
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">4.Please mention your TIN and CST No.</fo:block>
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">5.The validity of your offers should be 60 days from date of due date.</fo:block>
	                <fo:block  font-size="12pt"  white-space-collapse="false"  text-align="left">6.Substandard / material not as per our specifications are liable for rejection.</fo:block>
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">7.Payment will be made within 30 days from date of satisfactory receipt of the materials.</fo:block>
	                <fo:block  font-size="12pt"   white-space-collapse="false"  text-align="left">8.If you are a authorized dealer please send the dealership certificate and manufacturers price list.</fo:block>
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;  Thanking you,                                                      Yours faithfully</fo:block>
	                 <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                     for MOTHER DAIRY</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		               
	              <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left" font-weight="bold">&#160;                                                                <#if signature?has_content>${signature?if_exists}<#else>GENERAL MANAGER(Purchase)</#if></fo:block>	               
	                <fo:block page-break-after="always"></fo:block>
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    <fo:block font-family="Courier,monospace">
                    <fo:block text-align="center" keep-together="always" font-weight="bold">                                              MATERIAL SPECIFICATIONS                                                  </fo:block>				    	                
                    <fo:table>
                    <fo:table-column column-width="70pt"/>   
					 <fo:table-column column-width="80pt"/>
					 <fo:table-column column-width="180pt"/>
					 <fo:table-column column-width="100pt"/>
					     <fo:table-body>
					         <fo:table-row >
							   <fo:table-cell >
				                  <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						     </fo:table-row>
					         <fo:table-row>
                                <fo:table-cell>
									<fo:block text-align="center" font-weight="bold">SL.No</fo:block>
								</fo:table-cell>
					            <fo:table-cell>
									<fo:block text-align="left" font-weight="bold">ITEM CODE</fo:block>
								</fo:table-cell>
                                <fo:table-cell >
									<fo:block text-align="left" font-weight="bold">MATERIAL NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="left" font-weight="bold">SPECIFICATION</fo:block>
								</fo:table-cell>
						    </fo:table-row>
					        <fo:table-row >
							<fo:table-cell >
				                <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
						</fo:table-row>
					 </fo:table-body> 
			       </fo:table>
			       </fo:block>
			       <fo:block font-family="Courier,monospace"  font-size="10pt">
	                   <fo:table>
                          <fo:table-column column-width="70pt"/>
	                      <fo:table-column column-width="80pt"/>
					      <fo:table-column column-width="180pt"/>
					      <fo:table-column column-width="270pt"/>
					          <fo:table-body>
                               <#assign sno=1>  					          				                                                                               					          
                               <#list productList as productEntry>
                               <#assign productdId=productEntry.getKey()> 
                                <#assign productDetails=productEntry.getValue()> 
                                   <fo:table-row >
                                       <fo:table-cell>
									    <fo:block text-align="center" keep-together="always" font-size="12pt" >${sno?if_exists}</fo:block>
								       </fo:table-cell>
                                       <fo:table-cell >
									       <fo:block text-align="left" font-size="12pt"  >${productDetails.get("itemCode")?if_exists}</fo:block>
								       </fo:table-cell>
								       <fo:table-cell >
									       <fo:block text-align="left"  font-size="12pt" >${productDetails.get("description")?if_exists}</fo:block>
								       </fo:table-cell>
								       <fo:table-cell >
									       <fo:block text-align="left"  font-size="12pt"  >${productDetails.get("longDescription")?if_exists}</fo:block>
								       </fo:table-cell>
                                  </fo:table-row>
                                  <#assign sno=sno+1> 
                               </#list>  					                                                            
					     </fo:table-body>  
					  </fo:table>
				           <fo:block text-align="left" keep-together="always"  >----------------------------------------------------------------------------------------------------------</fo:block>
	                </fo:block> 
                    <#if noteList?has_content>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;Notes :</fo:block>     
						<fo:block>
						<fo:table>
	                 	<fo:table-column column-width="5%"/>
                    	<fo:table-column column-width="95%"/>
							<fo:table-body>
						  	 <#assign sno=1> 
                              <#list noteList as noteEntry>
                               <fo:table-row>
								<fo:table-cell ><fo:block text-align="left" >${sno?if_exists}</fo:block></fo:table-cell>
								<fo:table-cell ><fo:block text-align="left">${noteEntry?if_exists}</fo:block></fo:table-cell>
							   </fo:table-row>
					          <#assign sno=sno+1> 
                              </#list>  
                             </fo:table-body>
                          </fo:table>
                        </fo:block>    	
                   </#if>		
			      		  		
	               	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>			   	               
                   <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;  Thanking you,                                                      Yours faithfully</fo:block>
	               <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                     for MOTHER DAIRY</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	               
	              <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left" font-weight="bold">&#160;                                                              <#if signature?has_content>${signature?if_exists}<#else>GENERAL MANAGER(Purchase)</#if></fo:block>
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