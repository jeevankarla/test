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
			<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;    -----------------------------------------------------------</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;   STORE ISUE REPORT BETWEN 1-AUG-2014 AND 20-AUG-2014  
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="left" keep-together="always"  >&#160;------------------------------------------------------------------------------------------------------</fo:block>				
				<fo:table>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="250pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-body>
					       <fo:table-row >
					            <fo:table-cell>
									<fo:block text-align="center" keep-together="always">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">INDENT</fo:block>
									<fo:block text-align="left" keep-together="always">NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">ITEM</fo:block>
									<fo:block text-align="left" keep-together="always">CODE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always" >MATERIAL</fo:block>
									<fo:block text-align="left" keep-together="always" >NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">UNIT</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">REQD.</fo:block>
									<fo:block text-align="left" keep-together="always">QTY</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">ISS.</fo:block>
									<fo:block text-align="left" keep-together="always">QTY</fo:block>
								</fo:table-cell>
								<fo:table-cell>
								    <fo:block text-align="left" keep-together="always">TOT</fo:block>
								    <fo:block text-align="left" keep-together="always">VALUE</fo:block>
								</fo:table-cell>
						 </fo:table-row>
					     <fo:table-row >
							 <fo:table-cell >
								<fo:block text-align="left" keep-together="always" >&#160;------------------------------------------------------------------------------------------------------</fo:block>
						     </fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
	   </fo:static-content>
	   <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	       <fo:block></fo:block> 	 		
	   </fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>	    