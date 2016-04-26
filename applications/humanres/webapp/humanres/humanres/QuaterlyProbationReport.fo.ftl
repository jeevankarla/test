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
	     					
	       					<fo:table-column column-width="30pt"/>
	       					<fo:table-column column-width="425pt"/>
	       					<fo:table-column column-width="45pt"/>
	       					<fo:table-body>
	       					
	       					    <fo:table-row>
	       						<fo:table-cell>
        							<fo:block-container>
        									<fo:block>&#160;&#160;&#160;&#160;&#160;<fo:external-graphic src="/vasista/complogos/nhdcjpg.jpg" content-height="scale-to-fit" scaling="uniform" height="50" width="50"/>
                                    </fo:block>
                                   </fo:block-container>
                                  </fo:table-cell> 
                                  <fo:table-cell>  
        							<fo:block text-align="left" font-size="16pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LIMITED</fo:block>
        							<fo:block text-align="left" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;(A Govt. of India Undertaking Ministry of Textiles)</fo:block>
        							<fo:block text-align="left" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"Vikas Deep" 10th and 11th Floors, 22 Station Road, Lucknow - 226 001 </fo:block>
        							<fo:block text-align="left" font-size="16pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Department of personal and Administration</fo:block>
	     							<fo:block text-align="left" font-size="14pt" keep-together="always" white-space-collapse="false" font-family="Helvetica,monospace" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;QUATERLY REPORT ON PROBATION &#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						</fo:table-cell>
	       						<fo:table-cell>
	       		                     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
	       						 	
	       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       					</fo:table-body>
	     				</fo:table>
	     				
	     				<fo:table>
	     					
	       					<fo:table-column column-width="30pt"/>
	       					<fo:table-column column-width="400pt"/>
	       					<fo:table-column column-width="300pt"/>
	       					
	       					<fo:table-body>
	       					
	       					     <fo:table-row>
		       						<fo:table-cell>
	        							<fo:block>1</fo:block>
		       						</fo:table-cell>
		       						<fo:table-cell>
		       		                     <fo:block >&#xA;Name of Probationer</fo:block>
		       						 </fo:table-cell>
		       						 <fo:table-cell>
		       		                     <fo:block>&#xA;${probationDetails.getValue().get("firstName")?if_exists}</fo:block>
		       						 </fo:table-cell>
	       						</fo:table-row>
	       						
   								<fo:table-row>
		       						<fo:table-cell>
		       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	        							<fo:block>2</fo:block>
		       						</fo:table-cell>
		       						<fo:table-cell>
		       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		       		                     <fo:block >&#xA;Designation</fo:block>
		       						 </fo:table-cell>
		       						 <fo:table-cell>
		       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       		                     	<fo:block>${probationDetails.getValue().get("designation")?if_exists}</fo:block>
	       						 	</fo:table-cell>
	       						</fo:table-row>
	       						
	       						   <fo:table-row>
			       						<fo:table-cell>
											<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>3</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       							 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Date of Joining the grade</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(probationDetails.getValue().get("AppointmentDate")?if_exists, "dd-MM-yyyy")}</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>

	       						   <fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>4</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Scale of the Post</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>${probationDetails.getValue().get("payScale")?if_exists}</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						   <fo:table-row>
			       						<fo:table-cell>
											<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>5</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Manner of Recruitment</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>............................................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						   <fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>6</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Date of which the Final Probation report is due</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>............................................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						   <fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>7</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Whether Report on verification of Anticidents</fo:block>
											 <fo:block >&#xA;received and found satisfactory</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>.........................................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						<fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>8</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block >&#xA;Assmentment of following factors(whether Outstansding/</fo:block>
											 <fo:block >&#xA;Very Good/Good/Average/Below Average)</fo:block>
											 <fo:block>
											 	<fo:table>
	     					
							       					<fo:table-column column-width="30pt"/>
							       					<fo:table-column column-width="370pt"/>
							       					<fo:table-column column-width="300pt"/>
	       					
	       												<fo:table-body>
	       												
	       													<fo:table-row >
								                     			<fo:table-cell>
								                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>(A)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>
									                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>&#xA;Work Performance</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>	
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>(B)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>	
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>&#xA;Work Knowledge</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>(C)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>	
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>&#xA;Initative</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>

														  <fo:table-row >
								                     			<fo:table-cell>
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>(D)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>&#xA;Sence Of responsibility</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>	
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>(E)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>&#xA;Maturity</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>(F)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>	
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>&#xA;Attitude/Behavior</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>	
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>(G)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>	
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>&#xA;Leadership</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		  
								                		  <fo:table-row >
								                     			<fo:table-cell>	
								                     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>(H)</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell>	
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
									                            	<fo:block>&#xA;Attendance</fo:block>
									                            </fo:table-cell>
									                            <fo:table-cell >
									                            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
									                            	<fo:block>............................................................</fo:block>
									                            </fo:table-cell>
								                		  </fo:table-row>
								                		
								                	</fo:table-body>
								                </fo:table>
								           </fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       		                   <fo:block>............................................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						 <fo:table-row>
			       						<fo:table-cell>
			       						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>9</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       						<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											 <fo:block>&#xA;If performance of employee is below average during the </fo:block >
											 <fo:block>quarter under report againsts any of items under serial no.8</fo:block>
											  <fo:block >above,please indicate the action taken by Reporting officer</fo:block>
											   <fo:block >aimed at improving the performance of the employee by the way of</fo:block>
											   <fo:block > the counselling written warning and training etc</fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>............................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>............................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>............................................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						 <fo:table-row >
	                     			<fo:table-cell>	
		                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell>	
		                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		                            </fo:table-cell>
								</fo:table-row>
	       						
	       						<fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block></fo:block>
		        							<fo:block>&#160;&#160;Date&#160;........................</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block></fo:block>
			       								<fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											    <fo:block></fo:block>
											     <fo:block></fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Signature of Reporting Officer&#160;.......................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Name of Reporting Officer&#160;............................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Designation&#160;..........................................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						<fo:table-row >
	                     			<fo:table-cell>	
		                            	<fo:block>__________________________________________________________________________________________________</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell>	
		                            	<fo:block>__________________________________________________________________________________________________</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block>__________________________________________________________________________________________________</fo:block>
		                            </fo:table-cell>
								</fo:table-row>
	       						
	       						<fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											<fo:block>10</fo:block>
		        							<fo:block></fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block></fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block>Recommendation of the Reviewing Authority </fo:block>
			       								<fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>...................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>....................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>....................................................</fo:block>
			       						 </fo:table-cell>
			       				</fo:table-row>
			       						 
			       				 <fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>&#160;&#160;Date&#160;........................</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block></fo:block>
			       								<fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Signature&#160;.................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Name&#160;......................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Designation&#160;...............................</fo:block>
			       						 </fo:table-cell>
	       						</fo:table-row>
	       						
	       						<fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
											<fo:block>11</fo:block>
		        							<fo:block></fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block></fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block>Final orders of the Approving Authority</fo:block>
			       								<fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>...................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>....................................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>....................................................</fo:block>
			       						 </fo:table-cell>
			       				</fo:table-row>
			       						 
			       				 <fo:table-row>
			       						<fo:table-cell>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        							<fo:block>&#160;&#160;Date&#160;........................</fo:block>
			       						</fo:table-cell>
			       						<fo:table-cell>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       								<fo:block></fo:block>
			       								<fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
											   <fo:block></fo:block>
			       						 </fo:table-cell>
			       						 <fo:table-cell>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       						 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Signature&#160;.................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Name&#160;......................................</fo:block>
			       		                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			       		                   <fo:block>Designation&#160;...............................</fo:block>
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