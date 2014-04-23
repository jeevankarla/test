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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top=".2in"/>
          <fo:region-before extent=".5in"/>
          <fo:region-after extent=".5in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
        <fo:page-sequence master-reference="main">
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block>      
	<fo:table table-layout="fixed" width="100%">
	<fo:table-column column-width="7in"/>
		<fo:table-body>
				<fo:table-row>
					<fo:table-cell>		
						<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center">FORM 1A</fo:block>
						<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center">FAMILY DECLARATION FORM</fo:block>
						<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="center">[Regulation 15A]</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
 					<fo:table-cell><fo:block keep-together="always" text-align="left" >Name of the Insured Person...................................</fo:block><fo:block keep-together="always" text-align="left">Insurance No............................</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block></fo:table-cell>
				</fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<fo:table layout="fixed" width="100%">
   		<fo:table-column column-width="1.1in"/>
   		<fo:table-column column-width="1.7in"/>
   		<fo:table-column column-width="1.4in"/>
   		<fo:table-column column-width="1.4in"/>
   		<fo:table-column column-width="1.5in"/>
   			<fo:table-body border="solid">
   				<fo:table-row border-left="solid" border-width="1pt" white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block><fo:block border-right="solid" text-align="center" >Sl. No.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block><fo:block border-right="solid" text-align="center" >Name</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >  .  </fo:block><fo:block border-right="solid" text-align="center" >Date of Birth</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >Relationship</fo:block><fo:block border-right="solid" text-align="center" >with insured</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" border-top="solid" text-align="center" >*Whether</fo:block><fo:block border-right="solid" text-align="center" >residing with</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row border-left="solid" border-width="1pt" white-space-collapse="false">
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >.</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >person</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border-right="solid" text-align="center" >him / her or not</fo:block></fo:table-cell>
				</fo:table-row>
				<fo:table-row white-space-collapse="false">
    				<fo:table-cell><fo:block border="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border="solid" text-align="center" >  .  </fo:block></fo:table-cell>
					<fo:table-cell><fo:block border="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border="solid" text-align="center" >  .  </fo:block></fo:table-cell>
    				<fo:table-cell><fo:block border="solid" text-align="center" >  .  </fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
	</fo:table>
	
	<fo:table table-layout="fixed" width="100%">
		<fo:table-column column-width="3.5in"/>
		<fo:table-column column-width="4in"/>
			<fo:table-body>
				<fo:table-row>
    				<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block keep-together="always" text-align="left">* My Family declaration Form may kindly be corrected accordingly:</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left" white-space-collapse="false" keep-together="always">.      I hereby that the particulars above have been given by me and are true to the best of my </fo:block><fo:block white-space-collapse="false" keep-together="always" text-align="left">Knowledge and belief. I also under to intimate to the Corporation any changes in the membership of my </fo:block><fo:block white-space-collapse="false" keep-together="always" text-align="left">family within 15 days of such changes having occurred.</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
    				</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="left">Date ………………………………</fo:block></fo:table-cell>
    				<fo:table-cell><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="right">Signature /Thumb-impression of the insured person</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block><fo:block text-align="center">Countersigned ………………………</fo:block><fo:block text-align="center">Date …………………………………</fo:block><fo:block text-align="center">Designation…………………………</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
    				</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
    				<fo:table-cell><fo:block keep-together="always" text-align="left">Name, address and code No., of employer………………………………………………………………</fo:block><fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
    				<fo:block keep-together="always" text-align="left">Note :   According to Section 2, clause (11)  of the Employees’ State Insurance Act, 1948, “family” means</fo:block>
    				<fo:block keep-together="always" text-align="left">all or any of the following relatives of an insured person, (i) a spouse, (ii) a minor legitimate or adopted</fo:block>
    				<fo:block keep-together="always" text-align="left">child dependent upon the IP; (iii) a child who is wholly dependent on the earnings of the IP and who is – </fo:block>
    				<fo:block keep-together="always" text-align="left">(a) receiving education, till he or she attains the age of 21 years, (b) an unmarried daughter;</fo:block>
    				<fo:block keep-together="always" text-align="left">; (iv) a child who is infirm by reason of any physical or mental abnormality or injury and is wholly</fo:block>
    				<fo:block keep-together="always" text-align="left"> dependent on the earning of the IP, so long as the infirmity continues; (v) dependent parents.</fo:block>
    				<fo:block linefeed-treatment="preserve"> &#xA;</fo:block></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
	</fo:table>	
</fo:block>
    </fo:flow>
  </fo:page-sequence>  
 </fo:root>
</#escape>

