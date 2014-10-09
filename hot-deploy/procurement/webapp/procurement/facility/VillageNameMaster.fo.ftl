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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-top=".3in"  margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "VillageNameMaster.txt")}
      <#if centerMasterList?has_content>
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                                 VILLAGE NAME MASTER LIST                                                              </fo:block>
        	            <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------</fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160; UNIT CODE   :  <#if unitDetails?has_content>${unitDetails.facilityCode?if_exists}</#if>                                             UNIT NAME   : <#if unitDetails?has_content>${unitDetails.facilityName?if_exists}</#if>                                   </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;                                                                                                    PAGE NO  :    <fo:page-number/>                       </fo:block>
                        <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------</fo:block> 
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;CENTER   CENTER                PRODUCER                  R  M   COMN   CART   BANK   BR.            BANK   INCEN-                   </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;CODE      NAME                  NAME                     N  C   (PS)   (PS)   CODE  CODE           A/C NO   TIVE                    </fo:block>
                        <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;                                                         O  T                                                                                </fo:block>
                        <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        		<fo:block>
        	  		<fo:table>
	        	        <fo:table-column column-width="25pt"/>
	                    <fo:table-column column-width="80pt"/> 
	               	    <fo:table-column column-width="90pt"/>
	            		<fo:table-column column-width="15pt"/> 	
	            		<fo:table-column column-width="12pt"/>	
	            		<fo:table-column column-width="27pt"/>	
	            		<fo:table-column column-width="20pt"/>
	            		<fo:table-column column-width="30pt"/>
	            		<fo:table-column column-width="15pt"/>
	            		<fo:table-column column-width="75pt"/>
	            		<fo:table-column column-width="20pt"/>	
	            		<fo:table-column column-width="20pt"/>	
	            		<fo:table-column column-width="50pt"/>
	            		<fo:table-body>
	            		<#list centerMasterList as centerMaster>
	            		<fo:table-row>
		        			<fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${centerMaster.get("CCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerMaster.get("CNAME"))),17)}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(centerMaster.get("PNAME"))),17)}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${centerMaster.get("RNO")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                     <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${centerMaster.get("MCCTYP")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${centerMaster.get("COMN")?if_exists?string("#0.00")}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${centerMaster.get("CART")?if_exists?string("#0.00")}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right" keep-together="always" white-space-collapse="false" text-indent="5pt">${centerMaster.get("GBCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                     <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right" keep-together="always" white-space-collapse="false" text-indent="5pt">${centerMaster.get("BCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right" keep-together="always" white-space-collapse="false">${centerMaster.get("BANO")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${centerMaster.get("INCENTIVE")?string(".T.", ".F.")}</fo:block>                               
		                    </fo:table-cell>
						</fo:table-row>
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
		            	${uiLabelMap.NoOrdersFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
     </fo:root>
</#escape>