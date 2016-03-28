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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
      	margin-left="0.4in" margin-right="0.4in"  margin-top="0.2in" margin-bottom="0.2in">
        	<fo:region-body margin-top="0.3in"/>
        	<fo:region-before extent="1in"/>
        	<fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#if !FinalMap?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Helvetica,monospace">
		      <fo:block font-size="14pt">
		             No Records Found.
		   	  </fo:block>
		   </fo:flow>
		</fo:page-sequence> 
	<#else>
 		
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
 			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
 			<#assign SNo=1>
 			
 			
 			 <#if FinalMap?has_content> 
     	<#assign probationDetailsList=FinalMap.entrySet()>
 		<#if probationDetailsList?has_content>  
 		
 			<#list probationDetailsList as probationDetails> 
 			
     		<fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     			      	 	 	  	 	
	     		 <fo:flow flow-name="xsl-region-body" font-family="Helvetica,monospace">
	     			<fo:block font-family="Helvetica,monospace">
	     				<fo:table>
	     					
	       					<fo:table-column column-width="300pt"/>
	       					<fo:table-column column-width="425pt"/>
	       					<fo:table-body>
	       					
	       					    <fo:table-row>
	       						<fo:table-cell>
	       						 	
        							<fo:block text-align="left" font-size="16pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED</fo:block>
        							<fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(A Govt. of India Undertaking)</fo:block>
        							<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"Vikas Deep" 10th and 11th floor,22 Station Road Lucknow - 226 001 </fo:block>
        							<fo:block text-align="left" font-size="16pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Department of personal and Administration</fo:block>
	     							<fo:block text-align="left" font-size="14pt" keep-together="always" white-space-collapse="false" font-family="Helvetica,monospace" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;FINAL REPORT ON PROBATION &#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">1.&#160;Name of the Probationer</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						    <fo:block text-align="left" font-size="13pt">2.&#160;Designation</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						    <fo:block text-align="left" font-size="13pt">3.&#160;Date of Joining the grade</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">4.&#160;Scale of the Post</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">5.&#160;Manner of Recruitment</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">6.&#160;Date of which the Final Probation report is due</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">7.&#160;Whether Report on verification of Antecedents received&#160;and found satisfactory</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">8.&#160;Whether any disciplinary action comtemplated/pending </fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">9.&#160;Whether any test or examination or training to be successsfully undergone before confirmation ?If so, what is the position in this case</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">10.&#160;Whether any caution note or warning was issued in case performance is found not to be as per required standard</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">11.&#160;Short description about his initiative, integrety and overall performance and potential for growth</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block text-align="left" font-size="13pt">12.&#160;Specific recommendation to be effect whether the employee is to be confirmed or given extension reverted or to be discharged on basis of overall assessment as indicated in item no. 10 above(under the existing rules an employee can be kept under probation for a maximum period of 2 years)</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	
	       						 	<fo:block text-align="left" font-size="13pt">Date&#160;.....................</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block text-align="left" font-size="13pt">&#160;_____________________________________________________________________________________</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block text-align="left" font-size="13pt">13.&#160;Recommendation of the Reviewing Authority(The Report is to be specific as indicated in item no. 12 above)</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block text-align="left" font-size="13pt">Date&#160;.......................</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						    <fo:block text-align="left" font-size="13pt">&#160;______________________________________________________________________________________</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block text-align="left" font-size="13pt">14.&#160;Final orders of the Approving Authority</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	<fo:block text-align="left" font-size="13pt">Date&#160;.......................</fo:block>
	       						 	
	       						 
	       						</fo:table-cell>
	       						    
	       						<fo:table-cell>
	       		                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;${probationDetails.getValue().get("firstName")?if_exists}</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       		                     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;${probationDetails.getValue().get("designation")?if_exists}</fo:block>
	       			                 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(probationDetails.getValue().get("AppointmentDate")?if_exists, "dd-MM-yyyy")}</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;${probationDetails.getValue().get("payScale")?if_exists}</fo:block>
	       		                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       		                     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Signature of Reporting Officer&#160;..................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Name of Reporting Officer&#160;.......................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Designation&#160;.....................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Signature&#160;.....................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Name&#160;..........................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Designation&#160;...................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;............................................................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Signature&#160;.....................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Name&#160;..........................................</fo:block>
	       						     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						     <fo:block text-align="left" font-size="13pt">&#160;&#160;&#160;&#160;Designation&#160;...................................</fo:block>
	       						     
	       						
	       						</fo:table-cell>
	       						
	       						
	       						
	       						</fo:table-row>
	       						
	       					</fo:table-body>
	     				</fo:table>
	     			</fo:block>
           			
	          </fo:flow>          
	        </fo:page-sequence> 
	        
	       </#list>
	      </#if>
	      </#if>  
	      
    </#if>
  </fo:root>
</#escape>