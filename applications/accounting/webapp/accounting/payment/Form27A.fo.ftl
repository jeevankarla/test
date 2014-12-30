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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
            margin-top="0.2in" margin-bottom=".3in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.0in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "TDSReport.pdf")}

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			
            <fo:flow flow-name="xsl-region-body"   font-family="Arial">	    
            <fo:block>
            <fo:table border-style="solid">
                    	<fo:table-column column-width="100%"/>
                    	<fo:table-body>
                    	
                    	<fo:table-row><fo:table-cell>
		<fo:block  keep-together="always" text-align="center" font-size="15" white-space-collapse="false" font-weight="bold">Form No. 27A</fo:block>  
    					</fo:table-cell></fo:table-row>
    					<fo:table-row><fo:table-cell>
    <fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false">Form for furnishing information with the statement of deduction / collection of tax at source ( tick whichever is applicable )filed on computer media for the period</fo:block>
    					</fo:table-cell></fo:table-row>
    					<fo:table-row><fo:table-cell>  
    <fo:block  keep-together="always" text-align="center" font-size="12" white-space-collapse="false">(From _ _ / _ _ / _ _ to _ _ / _ _ / _ _ (dd/mm/yy)#</fo:block>  
            			</fo:table-cell></fo:table-row>
       <fo:table-row><fo:table-cell>
       <fo:block  keep-together="always" text-align="left" font-size="12" white-space-collapse="false">1.</fo:block>  
       </fo:table-cell></fo:table-row>
	<fo:table-row>
			<fo:table-cell>
		       		<fo:table>
		       				<fo:table-column column-width="50%"/>
				       		<fo:table-column column-width="50%"/>
				       		<fo:table-body>
				        		<fo:table-row>
				        		<fo:table-cell>
		        		       		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(a)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block keep-together="always">Tax Deduction Account No.</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				</fo:table-body>
				            		</fo:table>
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(d)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block keep-together="always">Financial year</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				<fo:table-row>
			            		<fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>
			            	   <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
                               <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
			            		</fo:table-row>
				            </fo:table-body>
				          </fo:table>
			            		</fo:table-cell>
			            		</fo:table-row>	
			            		<fo:table-row>
				        		<fo:table-cell>
		        		       		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(b)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block keep-together="always">Permanenet Account No:</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				</fo:table-body>
				            		</fo:table>
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(e)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block keep-together="always">Assessment Year</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				<fo:table-row>
			            		<fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>
			            	   <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
                               <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
			            		</fo:table-row>
				            				</fo:table-body>
				            		</fo:table>
			            		</fo:table-cell>
			            		</fo:table-row>	
			            		
			            		<fo:table-row>
				        		<fo:table-cell>
		        		       		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(c)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block keep-together="always">Form No:</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				</fo:table-body>
				            		</fo:table>
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:table cell-spacing="5" cell-padding="5">
							       		<fo:table-column column-width="5%"/>
							       		<fo:table-column column-width="40%"/>
							       		<fo:table-column column-width="40%"/>
							        		<fo:table-body>
							        		<fo:table-row>
							        			<fo:table-cell><fo:block>(f)</fo:block></fo:table-cell>
							        			<fo:table-cell><fo:block >Previous Receipt Number          (in case return/statement has been filed earlier)</fo:block></fo:table-cell>
							        			<fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>	
				            				</fo:table-row>
				            				<fo:table-row>
			            		<fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>
			            	   <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
                               <fo:table-cell column-height="4pt">
                                             <fo:block font-size="4pt" >&#160;</fo:block>
                               </fo:table-cell>	
			            		</fo:table-row>
				            				</fo:table-body>
				            		</fo:table>
			            		</fo:table-cell>
			            		</fo:table-row>	
			            		
			            	</fo:table-body>
				    </fo:table>
          
 <fo:block>
 	<fo:table>
		<fo:table-column column-width="50%"/>
   		<fo:table-column column-width="50%"/>
   			<fo:table-body>
   				<fo:table-row>     
				<fo:table-cell column-height="4pt"><fo:block font-size="10pt" >&#160;</fo:block></fo:table-cell>
				<fo:table-cell column-height="4pt"><fo:block font-size="10pt" >&#160;</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row>     
				<fo:table-cell><fo:block text-align="left">2. Particulars of the deductor / collector </fo:block></fo:table-cell>
				<fo:table-cell><fo:block text-align="left">3. Name of the person responsible for deduction/collection of tax</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row>
				<fo:table-cell>
					<fo:table border-style="solid">
					<fo:table-column column-width="30%"/>
   					<fo:table-column column-width="30%"/>
   					<fo:table-body>
   						<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(a) Name</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(b)Type of Deductor</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(c)Branch/division (if any)</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(d)Address</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block> &#160; Flat No.</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>  &#160; Name of the premises building</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Road/Street/Lane</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Area/Location</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Town/City/District</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>State</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Pin Code</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Telephone No.</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row><fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Email</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        </fo:table-body>
			        </fo:table>
			      </fo:table-cell>
			      <fo:table-cell>
					<fo:table border-style="solid">
					<fo:table-column column-width="30%"/>
   					<fo:table-column column-width="30%"/>
   					<fo:table-body>
   						<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(a) Name</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>(b)Address</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Flat No.</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Name of the premises building</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Road/Street/Lane</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Area/Location</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Town/City/District</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>State</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Pin Code</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Telephone No.</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
		        			<fo:table-cell border-style="solid"><fo:block>Email</fo:block></fo:table-cell>
			        		<fo:table-cell border-style="solid"><fo:block ></fo:block></fo:table-cell>
			        	</fo:table-row>
			        </fo:table-body>
			        </fo:table>
			      </fo:table-cell>
			      </fo:table-row>
		</fo:table-body>
	</fo:table>
</fo:block>			        			
           			
<fo:block linefeed-treatment="preserve">&#xA;</fo:block>

<fo:block font-size="12pt" >4. Control Totals</fo:block>  
          			<fo:table>
		       				<fo:table-column column-width="10%"/>
				       		<fo:table-column column-width="20%"/>
				       		<fo:table-column column-width="20%"/>
				       		<fo:table-column column-width="20%"/>
				       		<fo:table-column column-width="30%"/>
				       		<fo:table-body>
				       			<fo:table-row >
	                    				<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">Sr. No. </fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">No.of Deductee/Party Records </fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">Amount Paid  Rs.</fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">Tax Deducted/Collection Rs.</fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">Tax Deposited(Total challan amount)Rs.</fo:block>  
                       					</fo:table-cell>
                       			</fo:table-row >
                       			<fo:table-row >
	                    				<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">1</fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       			</fo:table-row >
                       			<fo:table-row >
	                    				<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false">Total</fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       					<fo:table-cell border-style="solid">
                            				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
                       					</fo:table-cell>
                       			</fo:table-row >
           		</fo:table-body>
           		</fo:table>
           		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
           		

<fo:block>
	<fo:table>
		<fo:table-column column-width="30%"/>
		<fo:table-column column-width="50%"/>
		<fo:table-body>
   			<fo:table-row >
			<fo:table-cell>
				<fo:block  text-align="left" font-size="12" white-space-collapse="false">5. Total Number of Annextures enclosed</fo:block>  
			</fo:table-cell>
			<fo:table-cell border-style="solid">
				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
			</fo:table-cell>
			</fo:table-row>
			<fo:table-row>     
				<fo:table-cell column-height="4pt"><fo:block font-size="4pt" >&#160;</fo:block></fo:table-cell>
				<fo:table-cell column-height="4pt"><fo:block font-size="4pt" >&#160;</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row >
			<fo:table-cell>
				<fo:block  text-align="left" font-size="12" white-space-collapse="false">6. Other Information</fo:block>  
			</fo:table-cell>
			<fo:table-cell border-style="solid">
				<fo:block  text-align="center" font-size="12" white-space-collapse="false"></fo:block>  
			</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</fo:block>
		           		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block  text-align="center" font-size="12" white-space-collapse="false" font-weight="bold">VERIFICATION</fo:block>
            <fo:block  text-align="left" font-size="12" white-space-collapse="false">I, 	__________________________________________hereby certify that all the particulars furnished above are correct and complete</fo:block>
            <fo:block  text-align="left" font-size="12" white-space-collapse="false">Place:                               			     							 Signature of person responsible for deducting/collecting tax at source_____________________________</fo:block>
            <fo:block  text-align="left" font-size="12" white-space-collapse="false">Date:                                						      				Name of the designation of person responsible for deductiong/collecting tax at source_______________________</fo:block>
            <fo:block  text-align="left" font-size="12" white-space-collapse="false">* Mention type of deductor - Government or Others</fo:block>
            <fo:block  text-align="left" font-size="12" white-space-collapse="false"># dd/mm/yy :- date/month/year</fo:block>


            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			
            			</fo:table-cell>
			       </fo:table-row>			
              </fo:table-body>
        	</fo:table>
        	</fo:block>
            
            
            
            
             </fo:flow>
			 </fo:page-sequence>	
   </fo:root>
</#escape>