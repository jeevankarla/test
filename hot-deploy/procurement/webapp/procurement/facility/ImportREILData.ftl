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

<div class="page-title"><span>${uiLabelMap.WebtoolsImportToDataSource}</span></div>
<p>${uiLabelMap.WebtoolsXMLImportInfo}</p>
<hr />

  <form method="post" >
    ${uiLabelMap.WebtoolsAbsoluteFileNameOrUrl}:<br />
    <input type="text" size="60" name="filename" value="${filename?if_exists}"/><br />
    ${uiLabelMap.WebtoolsAbsoluteFTLFilename}:<br />
    <input type="text" size="40" name="fmfilename" value="${fmfilename?if_exists}"/><br />
    <div class="button-bar"><input type="submit" value="${uiLabelMap.WebtoolsImportFile}"/></div>
  </form>
  <form method="post">
    ${uiLabelMap.WebtoolsCompleteXMLDocument}:<br />
    <textarea rows="20" cols="85" name="fulltext">${fulltext?default("<entity-engine-xml>\n</entity-engine-xml>")}</textarea>
    <div class="button-bar"><input type="submit" value="${uiLabelMap.WebtoolsImportText}"/></div>
  </form>
  <#if messages?exists>
      <hr />
      <h3>${uiLabelMap.WebtoolsResults}:</h3>
      <#list messages as message>
          <p>${message}</p>
      </#list>
  </#if>
