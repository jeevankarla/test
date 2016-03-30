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
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
			<fo:page-sequence master-reference="main">
			    <fo:static-content flow-name="xsl-region-before">
		             <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
                     <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;(A GOVT. OF INDIA ENTERPRISE)</fo:block> 
                     <fo:block text-align="center" font-size="14pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica"><fo:inline text-decoration="underline">&#160;10th and 11th floor,Vikas Deep Building, 22 Station Road, Lucknow - 226 001</fo:inline>  </fo:block>"Vikas Deep" 10th and 11th floor,22 Station Road Lucknow - 226 001 
  	                 </fo:static-content>  	
				    <fo:flow flow-name="xsl-region-body"   font-family="Helvetica,monospace">	
				     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
				      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	    
			        <fo:block text-align="center" keep-together="always" font-family="Helvetica" font-weight="bold">                                  Establishment Code :                                                 </fo:block>				    
			     	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	        				        			        
				    <fo:block text-align="left" keep-together="always" lwhite-space-collapse="false"></fo:block>				   				    
                    <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Helvetica" font-weight="bold"><fo:inline text-decoration="underline">Settlement of Claims.:</fo:inline>                                                                                                                 REGION : ..........................</fo:block>				   
 				    <fo:block text-align="right" keep-together="always" white-space-collapse="false"  font-weight="bold">   INCLUDING ALL RO'S  &#160;&#160;&#160;&#160;</fo:block>	
                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                
	               	<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Times New Roman" font-weight="bold"><fo:inline text-decoration="underline">Provident Fund claims settled by exempted establishment during the year  </fo:inline></fo:block>	
	               	  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	             	   			 
	               
				   		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Times New Roman" font-weight="bold">TABLE-9</fo:block>	
				   				          
				   <fo:block font-family="Helvetica,monospace">
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					     
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell>
									     <fo:block text-align="center" font-weight="bold" border-style="solid">SNO</fo:block>
								     </fo:table-cell>
					                 <fo:table-cell>ll>
					                     <fo:block text-align="left" border-style="solid" font-weight="bold" keep-together="always" >Category</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell>
					                     <fo:block text-align="left" border-style="solid" font-weight="bold" keep-together="always" >Cases</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell>
					                    <fo:block text-align="center" border-style="solid" font-weight="bold" keep-together="always">Amount(Rs. in Lakh) approx.</fo:block>
					                 </fo:table-cell>
					                 
					             </fo:table-row>
					        </fo:table-body>
					     </fo:table>      
				   </fo:block>
			       <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 1</fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Death Cases</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block>  
				 
	              <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 2 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Resignation/Termination</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
				   
				   <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 3 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Retrenchment</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	          
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 4 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Superannuation</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	           
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 5 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Permanent Invalidation</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	           
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 6 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Dismissal</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	               
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 7 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Migration</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	           
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> 8 </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="left"  keep-together="always" font-size="12pt">Others</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	           
	                <fo:block font-family="Helvetica,monospace">
				      
	                   <fo:table width="100%" align="center" table-layout="fixed"  font-size="13pt">
					       <fo:table-column column-width="70pt"/>
					       <fo:table-column column-width="170pt"/>
					       <fo:table-column column-width="140pt"/>
					       <fo:table-column column-width="220pt"/>
					          <fo:table-body>
					             <fo:table-row>
					                <fo:table-cell border-style="solid">
									     <fo:block text-align="center" keep-together="always" font-size="12pt"> </fo:block>
								    </fo:table-cell>
					                <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt">Total:</fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="12pt"></fo:block>
					                 </fo:table-cell> 
					                 <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  keep-together="always" font-size="12pt"></fo:block>
					                 </fo:table-cell>
					                
 					             </fo:table-row>
 					          </fo:table-body>      
					   </fo:table> 					  
				   </fo:block> 
	           
	           </fo:flow> 	          
	      </fo:page-sequence>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>			
     </fo:root>
</#escape>	    