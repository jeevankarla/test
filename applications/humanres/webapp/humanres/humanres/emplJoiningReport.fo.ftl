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
            margin-top="0.4in" margin-bottom=".7in" margin-left="0.1in" margin-right="0.04in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
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
	

 			
 			
 	    <#if FinalMap?has_content> 
     	<#assign EmplJoiningDetailsList=FinalMap.entrySet()>
 		<#if EmplJoiningDetailsList?has_content>  
 		  
			<fo:page-sequence master-reference="main">
			    <fo:static-content flow-name="xsl-region-before">
		             <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
                     <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;(A GOVT. OF INDIA ENTERPRISE)</fo:block> 
  	                  <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;"Vikas Deep" 10th and 11th floor,22 Station Road Lucknow - 226 001</fo:block> 
  	                  <fo:block text-align="right" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>  
  	                 </fo:static-content>  	
				    <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				   		<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">NEW EMPLOYEE JOINING REPORT</fo:block>	
				   		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				    <fo:block font-family="Courier,monospace">
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="10pt">
					       <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="100pt"/>
					       <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="150pt"/>
					       <fo:table-column column-width="80pt"/>
					       <fo:table-column column-width="120pt"/>
					       <fo:table-column column-width="100pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell>
									     <fo:block text-align="center" font-weight="bold" border-style="solid">Sl.No <fo:block linefeed-treatment="preserve">&#xA;</fo:block> </fo:block>
								     </fo:table-cell>
					                 <fo:table-cell>
					                     <fo:block text-align="left" border-style="solid" font-weight="bold">Emp Code <fo:block linefeed-treatment="preserve">&#xA;</fo:block> </fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell>
					                     <fo:block text-align="left" border-style="solid" font-weight="bold">Emp Name <fo:block linefeed-treatment="preserve">&#xA;</fo:block> </fo:block>
					                  </fo:table-cell> 
					                 <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold">Gender (M/F) </fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold">Designation   <fo:block linefeed-treatment="preserve">&#xA;</fo:block> </fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold">Date  of Joining </fo:block>
					                 </fo:table-cell>
					                  <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold">Place of Joining <fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:block>
					                 </fo:table-cell>
					                  <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold">Place of Posting <fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:block>
					                 </fo:table-cell>
					             </fo:table-row>
					        </fo:table-body>
					     </fo:table>      
				   </fo:block>
			       <fo:block font-family="Courier,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="10pt">
					        <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="100pt"/>
					       <fo:table-column column-width="50pt"/>
					       <fo:table-column column-width="150pt"/>
					       <fo:table-column column-width="80pt"/>
					       <fo:table-column column-width="120pt"/>
					       <fo:table-column column-width="100pt"/>
					        
 			
					          <fo:table-body>
	       					   <#assign sno = 0>
					              <#list EmplJoiningDetailsList as EmplJoiningDetails> 
 		                            <#assign sno = sno+1>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="10pt">${sno}</fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="10pt">${EmplJoiningDetails.getValue().get("Emplcode")?if_exists}</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" keep-together="always" font-size="10pt">${EmplJoiningDetails.getValue().get("firstName")?if_exists}</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="10pt">${EmplJoiningDetails.getValue().get("gender")?if_exists}</fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="10pt">${EmplJoiningDetails.getValue().get("designation")?if_exists}</fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="10pt"><#if EmplJoiningDetails.getValue().get("JoiningDate")?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(EmplJoiningDetails.getValue().get("JoiningDate")?if_exists, "dd-MM-yyyy")}<#else>&#160;</#if></fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="10pt"><#if EmplJoiningDetails.getValue().get("JoiningDate")?has_content>${EmplJoiningDetails.getValue().get("PlaceOfJoining")?if_exists}<#else>&#160;</#if></fo:block>
					                 </fo:table-cell>
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="10pt">${EmplJoiningDetails.getValue().get("EmplPosting")?if_exists}</fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					            </#list>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block>  
				 
	           </fo:flow> 	          
	      </fo:page-sequence>
          
	      </#if>
	      </#if>  
	      
    </#if>	
     </fo:root>
</#escape>	    