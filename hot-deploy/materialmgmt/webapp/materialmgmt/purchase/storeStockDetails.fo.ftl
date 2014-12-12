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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="9in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="2.5in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
				<#--	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>   -->
					<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.  </fo:block>
			       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;                                                                </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
			        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
					 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
			
		          
                    <fo:block text-align="center" font-size="10pt"   font-family="Helvetica"  keep-together="always"  white-space-collapse="false" >&#160;&#160;STATEMENT SHOWING THE DETAILS OF STORES STOCK POSITION <#-- (${parameters.stockId})-->       ON : <#--${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "dd/MM/yy ")}-->  </fo:block>
  			        <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
                    <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" > &#160;&#160;                                                                                                                                PRINT DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy")}  </fo:block>
                    
              		<#-- <#assign stockDetails = delegator.findOne("Stock", {"stockId" :parameters.stockId }, true)>
		   		          <#if stockDetails?has_content>  -->
			        	   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160; &#160;&#160; &#160;&#160;                STORE CODE:<#-- (${parameters.stockId})-->&#160;    &#160;     &#160;  &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; &#160;&#160; DESCRIPTION: <#--${stockDetails.get("description")?if_exists})--></fo:block>
			          <#--     </#if>
              		   -->
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >_____________________________________________________________________________________________</fo:block>
              	    <fo:block  font-size="10pt" keep-together="always"  font-family="Helvetica"  white-space-collapse="false" >&#160;&#160;LEDGER          ITEM               DESCRIPTION                                   UNIT                     BOOK                  CLOSING  </fo:block>
              		<fo:block  font-size="10pt" keep-together="always"  font-family="Helvetica"  white-space-collapse="false" >&#160;&#160;FOLIO              CODE                                                                                                     STOCK                     STOCK   </fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >_____________________________________________________________________________________________</fo:block>               
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table >
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="170pt"/>  
               	    <fo:table-column column-width="50pt"/>
               	    <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="90pt"/> 		
                    <fo:table-body>
                  	 <fo:table-row>
                	   <fo:table-cell ><fo:block text-align="center" font-weight="bold"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>     
  				  	   <fo:table-cell ><fo:block text-align="center" font-weight="bold"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="left" font-weight="bold"  font-size="10pt" keep-together="always">&#160;</fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="center" font-weight="bold"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="right" font-weight="bold"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="right" font-weight="bold"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>     
  				     </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
                <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block>
               	<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-size="10pt" >  CASE WORKER                                                                                                                                   STORE OFFICER </fo:block>
               		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>