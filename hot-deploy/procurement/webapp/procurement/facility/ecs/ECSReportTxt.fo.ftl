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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.5in" margin-bottom="0.3in" margin-left=".5in" margin-right=".5in">
          <fo:region-body margin-top="0.6in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    ${setRequestAttribute("OUTPUT_FILENAME", "ECS-SBI.txt")}
 <#if ecsList?has_content>   
  <fo:page-sequence master-reference="main">
  		<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" keep-together="always" font-size="5pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;ANDHRA PRADESH DAIRY DEV. CO-OP. FEDN. LTD -MPF: HYDERABAD</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="5pt"  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;ECS REPORT SBI      PAGE NO:<fo:page-number/></fo:block>
 				<fo:block text-align="left" white-space-collapse="false" font-size="5pt"  keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Period From:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate,"dd-MM-yyyy")} to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate,"dd-MM-yyyy")}</fo:block>
  				<fo:block font-size="7pt" >-------------------------------------------------------------------------------------------</fo:block>
 				<fo:block text-align="left" keep-together="always" font-size="5pt"  white-space-collapse="false" >SNO       		ACCOUNT HOLDER          								&#160;&#160;										BANK A/C       		 	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;					       AMOUNT </fo:block>
 				<fo:block font-size="7pt" >-------------------------------------------------------------------------------------------</fo:block>
 		</fo:static-content>
      <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="5pt" >
      	<fo:block font-size="5pt" >
		<fo:table width="100%" table-layout="fixed">
		   	 <fo:table-column column-width="30pt"/>
		   	 <fo:table-column column-width="80pt"/>
		     <fo:table-column column-width="50pt"/>
		     <fo:table-column column-width="50pt"/>
             <fo:table-body>
             <#assign sno = 1>
             <#list ecsList as ecs>
             <#if ecs.get("centerName")!="TOTAL">
                <fo:table-row>
                   <fo:table-cell>
                        	<fo:block text-align="left" font-size="5pt"  keep-together="always" white-space-collapse="false">${sno}</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="left" font-size="5pt"  keep-together="always">${ecs.get("centerName")?if_exists}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
							<fo:block text-align="left" font-size="5pt"  keep-together="always" white-space-collapse="false">${ecs.get("bankAccNum")?if_exists}</fo:block>                        	
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="5pt"  keep-together="always" white-space-collapse="false">${ecs.get("netAmount")?if_exists}</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <#assign sno = sno + 1>
               <#else>
                <fo:table-row>
                   <fo:table-cell>
                        	<fo:block font-size="7pt" >-------------------------------------------------------------------------------------------</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               <fo:table-row>
                   <fo:table-cell>
                        	<fo:block text-align="left" font-size="5pt"  keep-together="always" white-space-collapse="false">&#160;</fo:block>
                   </fo:table-cell>
                    <fo:table-cell>
                        	<fo:block text-align="left" font-size="5pt"  keep-together="always">${ecs.get("centerName")?if_exists}</fo:block>
                   </fo:table-cell>
                   <fo:table-cell>
							<fo:block text-align="left" font-size="5pt"  keep-together="always" white-space-collapse="false">&#160;</fo:block>                        	
                   </fo:table-cell>
                   <fo:table-cell>
                        	<fo:block text-align="right" font-size="5pt"  keep-together="always" white-space-collapse="false">${ecs.get("netAmount")?if_exists}</fo:block>
                   </fo:table-cell>
               </fo:table-row>
                <fo:table-row>
                   <fo:table-cell>
                        	<fo:block font-size="7pt" >-------------------------------------------------------------------------------------------</fo:block>
                   </fo:table-cell>
               </fo:table-row>
               	</#if>
               </#list>
            </fo:table-body>
        </fo:table> 
     </fo:block>
    </fo:flow>
 </fo:page-sequence>
 <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	</#if>		
	</fo:root>
</#escape>