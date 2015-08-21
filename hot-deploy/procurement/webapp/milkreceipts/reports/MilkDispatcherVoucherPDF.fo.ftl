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
specific language governing permissions and limitationsborder-style="solid"border-style="solid"
under the License.
-->

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="11.69in" page-width="8.27in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "arcOrder.pdf")}
 <#if milkTransferTank1?has_content>
<#if milkTransferTank2?has_content>  

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		
     <fo:static-content flow-name="xsl-region-before">
            </fo:static-content>	
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            
            <fo:block >
		   <fo:table width="100%" border-style="solid"  align="left" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="2%"/>
           <fo:table-column column-width="100%"/>               
                          
           	<fo:table-body>
             <fo:table-row>
             <fo:table-cell>
                    <fo:block text-align="center"   keep-together="always" font-size="12pt" ></fo:block>
             </fo:table-cell>
             
              <fo:table-cell>
           
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="18pt" font-weight="bold" >SONAI CO-OPERATIVE DAIRY LTD.</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >Mouje Gokhali,Baramati Road,Indapur,DIst.Pune </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >Phone/Fax No-(02111)(02111)217002/217003 </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >(FSSAL Lic.No:10012022000159)</fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="15pt" font-weight="bold">Milk Dispatcher Voucher</fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             
 				
 	       <fo:block >
		   <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="74%"/>               
            <fo:table-column column-width="30%"/>               
           	<fo:table-body>
             
             <fo:table-row text-align="justify" text-align-last="left" >
              <fo:table-cell >
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Book NO./S.NO: ${milkTransferTank1.dcNo?if_exists} </fo:block>
              </fo:table-cell>
               <fo:table-cell >
              <fo:block text-align="right"   white-space-collapse="false" keep-together="always" font-size="12pt" >Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy")}</fo:block>
                           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
              
              </fo:table-cell>       		
             </fo:table-row>
                          
             </fo:table-body>
    		</fo:table>
     </fo:block>

     <fo:block >
		   <fo:table width="100%" align="left"   table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="80%"/>               
           <fo:table-column column-width="15%"/>               
           <fo:table-body>
             <fo:table-row  text-align="right" >
              <fo:table-cell >
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Name of Receiving Dairy: <fo:inline text-decoration="underline">${partyName?if_exists}</fo:inline></fo:block>
              </fo:table-cell>
               <fo:table-cell>
                 <#assign vehicleList = delegator.findOne("Vehicle", {"vehicleId" : milkTransferTank1.containerId}, false)>
                 <fo:block text-align="left"  white-space-collapse="false" keep-together="always" font-size="12pt" >Tanker No: <fo:inline text-decoration="underline">${vehicleList.vehicleNumber?if_exists}</fo:inline></fo:block>
                 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
              </fo:table-cell>       		
             </fo:table-row>
    	</fo:table-body>
    		</fo:table>
     </fo:block>


      <fo:block >
		   <fo:table width="100%" align="left"   table-layout="fixed"  font-size="12pt">
           <fo:table-column column-width="35%"/>               
           <fo:table-column column-width="62%"/>               
           <fo:table-body>
              <fo:table-row  >
              <fo:table-cell text-align-last="left" >
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Dispatcher Time: <fo:inline text-decoration="underline">${milkTransferTank1.sendTime?if_exists}</fo:inline></fo:block>
              </fo:table-cell>
               <fo:table-cell>
              <fo:block text-align="right"   keep-together="always" font-size="12pt" >Name of Driver:<fo:inline text-decoration="underline">${milkTransferTank1.driverName?if_exists}</fo:inline></fo:block>
                          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
              </fo:table-cell>       		
             </fo:table-row>
    	</fo:table-body>
    		</fo:table>
     </fo:block>
                       
                       
        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     
       <fo:block>
     <fo:table width="100%" border-style="solid" align="right" table-layout="fixed"  font-size="12pt">
          
           <fo:table-column column-width="8%" />               
           <fo:table-column column-width="32%" />  
           <fo:table-column column-width="28%" />               
            <fo:table-column column-width="28%" />               
                         
           	<fo:table-body>
             <fo:table-row >
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >S.NO </fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >Particulars</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >Tanker1</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >Tanker2</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >1</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Quantity</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.quantity?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.quantity?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >2</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Type Of Milk</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
				
			<#assign productList = delegator.findOne("Product", {"productId" : milkTransferTank1.productId}, false)> 

              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${productList.description?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${productList.description?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >3</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Organoleptic Evaluation</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendOrganoLepticTest?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendOrganoLepticTest?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >4</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Temp.(C)</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendTemparature?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendTemparature?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
              <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >5</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Alcohol</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" ></fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" ></fo:block>
              </fo:table-cell>
             </fo:table-row>
             
              <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >6</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >T.Acidity(as %LA)</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendAcidity?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendAcidity?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
              <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >7</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >C.O.B Test</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendCob?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendCob?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >8</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >M.B.R Time</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendMBRT?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendMBRT?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >9</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Fat %</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.fat?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.fat?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >10</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Snf %</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.snf?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.snf?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >11</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Adulterants Test</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sendSedimentTest?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sendSedimentTest?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >12</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Seal No.(top)</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sealNoTop?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sealNoTop?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
             
             
             <fo:table-row>
               <fo:table-cell border-style="solid">
              <fo:block text-align="center"   keep-together="always" font-size="12pt" ></fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid">
              <fo:block text-align="left"   keep-together="always" font-size="12pt" >Seal No.(bottom)</fo:block>
              </fo:table-cell > 
              <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank1.sealNoTop?if_exists}</fo:block>
              </fo:table-cell>
               <fo:table-cell border-style="solid" >
              <fo:block text-align="center"   keep-together="always" font-size="12pt" >${milkTransferTank2.sealNoBottom?if_exists}</fo:block>
              </fo:table-cell>
             </fo:table-row>
     </fo:table-body>
    		</fo:table>
     </fo:block>
     
     <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
     <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 	 <fo:block text-align="left"   keep-together="always" font-size="12pt" >Milk for further processing. Not for direct loose sale.</fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	 
 	 <fo:block text-align="left" white-space-collapse="false"  keep-together="always" font-size="12pt" >Signature of Driver                            Signature of QA Official</fo:block>
  </fo:table-cell>
             </fo:table-row>
     </fo:table-body>
    		</fo:table>
     </fo:block>
			 </fo:flow>
			 </fo:page-sequence>
			 
			 <#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			NO RECORDS FOUND
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>
			</#if>
</fo:root>
</#escape>

