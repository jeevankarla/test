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
<#-- <style type="text/css">
.calendar tr td {
height: 8em;
width: 10em;
vertical-align: top;
padding: 0.5em;
}
.calendar .header-row td {
height: auto;
}
</style>  -->

  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${start?date?string("MMMM yyyy")?cap_first}</li>
      <li><a href='<@ofbizUrl>month?start=${next.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>'>${uiLabelMap.WorkEffortNextMonth}</a></li>
      <li><a href='<@ofbizUrl>month?start=${nowTimestamp.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>'>${uiLabelMap.WorkEffortThisMonth}</a></li>
      <li><a href='<@ofbizUrl>month?start=${prev.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>'>${uiLabelMap.WorkEffortPreviousMonth}</a></li>
    </ul>
    <br class="clear"/>
  </div>
<#if periods?has_content> 
 <#-- <table cellspacing="0" class="basic-table calendar">              
 <tr class="header-row">             
    <td width="1%" colspan="3">&nbsp;</td>
    <#list periods as day>
      <td>${day.start?date?string("EEEE")?cap_first}</td>
      <#if (day_index > 5)><#break></#if>
    </#list>
  </tr>  -->
 <#-- <#list periods as period>
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign indexMod7 = period_index % 7>
    <#if indexMod7 = 0>
      <tr>
        <td >
          <a href='<@ofbizUrl>week?start=${period.start.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>'>${uiLabelMap.CommonWeek} ${period.start?date?string("w")}</a>
        </td>
    </#if>


    <#if !period_has_next && indexMod7 != 6>
    <td colspan='${6 - (indexMod7)}'>&nbsp;</td>
    </#if>
  <#if indexMod7 = 6 || !period_has_next>
  </tr>
  </#if>
  </#list>  -->
<#----------------------------- change below this for attendance screen ------------------------------------------------------     -->


<#--    ---------------- week start 1--------------------------------------------------------------------------   -->
 <script>
  function leapTo (link)
   {
   var new_url=link;
   if (  (new_url != "")  &&  (new_url != null)  )
      window.location=new_url;
   else
      alert("\nYou must make a selection.");
   }


    function empclick(edate)
   {
     var current_date = new Date();
     month_value = current_date.getMonth()+1;
     day_value = current_date.getDate();
     year_value = current_date.getFullYear();

     if(month_value<10 && day_value<10)
     var todaydate = "0"+day_value+"/"+"0"+month_value+"/"+year_value;
     else if(day_value<10)
     var todaydate = "0"+day_value+"/"+month_value+"/"+year_value;
     else if(month_value<10)
     var todaydate = day_value+"/"+"0"+month_value+"/"+year_value;
     else
     var todaydate = day_value+"/"+month_value+"/"+year_value;
      
      if( edate == todaydate)
      document.getElementById('empdate').value=1;
      else
      document.getElementById('empdate').value=0;
    }



 </script>

<script type="text/javascript">

function test1() {
var current_date = new Date();
month_value = current_date.getMonth();
day_value = current_date.getDate();
year_value = current_date.getFullYear();

var todaydate = month_value+"-"+day_value+"-"+year_value;



var test = document.getElementById("sdate").value;
var sep = 1;
for(var i=0; i<5 ; i++)
{
 if( (test[i]=="-") || (test[i]=="/") || (test[i]==" "))
  {
     sep=test[i];
     break;
  }
}
var one = test.split(sep);
var totallen = (one[0].length)+(one[1].length)+(one[2].length)
if((one[0].length<4) && (one[1].length<4) && (one[2].length<4) )
{
alert('Enter 4 digit year OR Select date from calendar');
one[0]=month_value+1;
one[1]=day_value;
one[2]=year_value;
}
if(((one[0].length)<4) && (one[0]>12))
{
   var temp1 = one[0];
    one[0] = one[1];
    one[1]=temp1;
}
if((one[0].length==4) && (one[1]>12))
{
    var temp2 = one[1];
      one[1] = one[2];
      one[2] = temp2;
}
var two = one.join("/");

var res = Date.parse(two);
document.selectdate.start.value=res;


return start;
}
</script> 




<form name="selectdate" onSubmit="test1()"><td><b>DATE</b></td>
<td> 
<td><input type="hidden" name="start" id="sdate"/>
<@htmlTemplate.renderDateTimeField name="x" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="temp" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
<td><input type="submit" value="Search" id="button1" class="smallSubmit" onClick="document.getElementById('sdate').value=document.getElementById('temp').value"/></td>
</form>


<table border="0" style="position:relative;bottom:25px;left:400px;">
<tr>
<td>
  <input type="submit" name="submit" value="Week 1" onClick="leapTo('#week1')"/>
 </td>
<td>
  <input type="submit" name="submit" value="Week 2" onClick="leapTo('#week2')"/>
 </td>
<td>
  <input type="submit" name="submit" value="Week 3" onClick="leapTo('#week3')"/>
 </td>
<td>
  <input type="submit" name="submit" value="Week 4" onClick="leapTo('#week4')"/>
 </td>
<#if periods?has_content> 
<#assign monthdays=0/>
<#list periods as period>
<#assign monthdays=(monthdays+1)/>
</#list>
<#if (monthdays>28)>
<td>
  <input type="submit" name="submit" value="Week 5" onClick="leapTo('#week5')"/>
 </td>
</#if>
</#if>
 
</tr>
</table>

<#-- <ul style="display:inline;list-style-type:none;padding-right:20px;position:relative;bottom:25px;left:400px;">
<li style="display:inline;list-style-type:none;padding-right:20px;"><a href="#week1">Week1</a></li>
<li style="display:inline;list-style-type:none;padding-right:20px;"><a href="#week2">Week2</a></li>
<li style="display:inline;list-style-type:none;padding-right:20px;"><a href="#week3">Week3</a></li>
<li style="display:inline;list-style-type:none;padding-right:20px;"><a href="#week4">Week4</a></li>
<#if periods?has_content> 
<#assign monthdays=0/>
<#list periods as period>
<#assign monthdays=(monthdays+1)/>
</#list>
<#if (monthdays>28)>
<li style="display:inline;list-style-type:none;padding-right:20px;"><a href="#week5">Week5</a></li>
</#if>
</#if>
</ul>   -->

<#-- <input name="l" type="text" value="${partyId?if_exists}"/>  -->
<#if periods?has_content> 

  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">    

   <td class="centered" style="background-color:#C0C0C0;"><a name="week1">Week 1</a></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>

  <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#assign currentPeriod = false/>  
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
    
     <#assign weekdays=(weekdays+1)/>

  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 

<#if (weekdays=7)><#break></#if>

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

 <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>     
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
     
       <#assign wc=wc+1>
       <#assign wd=wd+1>
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
        <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,5)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
    <#assign weekdays=(weekdays+1)/>  
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign x=0>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if> onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
     <#if Records.emplPunchId=cellid?string>    
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
 <#if Records.emplPunchId=cellid?string>     
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>    >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>      
      ${Records.punchtime}  
     
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
   
    </td>                                           
  
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
    <#assign weekdays=(weekdays+1)/> 
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
 <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
       <#if Records.emplPunchId=cellid?string>         
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>  >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">    
     <#if Records.emplPunchId=cellid?string>       
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>

<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>  
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wc=wc+1>
       <#assign wd=wd+1>
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
    <#assign weekdays=(weekdays+1)/>  
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">   
      <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
    <#if Records.emplPunchId=cellid?string>
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
     <#assign weekdays=(weekdays+1)/>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
     <#if Records.emplPunchId=cellid?string>          
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">   
      <#if Records.emplPunchId=cellid?string>    
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>        
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>


<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->


 <#assign tempcnt=0>
  <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>  
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
          <#assign wc=wc+1>
       <#assign wd=wd+1>
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>


<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>

     <#assign weekdays=(weekdays+1)/>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">  
       <#if Records.emplPunchId=cellid?string>           
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="Out">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>                 
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">     
       <#if Records.emplPunchId=cellid?string>                   
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   <#assign weekdays=(weekdays+1)/>
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>        
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>            
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

 <#if tempcnt = 0><#break></#if>

</#list>


  </table>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>





<#--    ---------------- week start 2--------------------------------------------------------------------------   -->


<#if periods?has_content> 

  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">    

   <td class="centered" style="background-color:#C0C0C0;"><a name="week2">Week 2</a></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>

  <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
    
    <#assign currentPeriod = false/>  
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>

  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 
</#if>
<#if (weekdays=7)><#break></#if>

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

  <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 7)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wd=wd+1>
       </#if>  
         <#assign wc=wc+1>             
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>


 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
 
 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
      <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if>    
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign x=0>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if> onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>           
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>    >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>          
      ${Records.punchtime}  
     
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
   
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   
    <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>  >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>                    
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>

<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 7)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>          
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>                
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}&emplPunchId=${cellid?string}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>              
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">   
     <#if Records.emplPunchId=cellid?string>               
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>

<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 7)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
      
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>            
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">    
       <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>                  
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>  
  </#if>                                         
  <#if (weekdays=7)><#break></#if>

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>7)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">
     <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
  
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>            
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#if tempcnt = 0><#break></#if>

</#list>



  </table>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>


<#--   -----------------------------weed end ----------------------------------------------------------------    -->


<#--    ---------------- week start 3--------------------------------------------------------------------------   -->


<#if periods?has_content> 

  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">    

   <td class="centered" style="background-color:#C0C0C0;"><a name="week3">Week 3</a></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>

  <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
    
    <#assign currentPeriod = false/>  
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>

  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 
</#if>
<#if (weekdays=7)><#break></#if>

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

  <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 14)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>


 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
 
 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
      <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if>    
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign x=0>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if> onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>           
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>    >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>          
      ${Records.punchtime}  
     
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
   
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   
    <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>  >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>                    
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>

<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 14)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>

       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>          
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>                
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}&emplPunchId=${cellid?string}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>              
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">   
     <#if Records.emplPunchId=cellid?string>               
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>

<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->

 <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 14)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>            
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">    
       <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>                  
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>  
  </#if>                                         
  <#if (weekdays=7)><#break></#if>

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>14)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">
     <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
  
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>            
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#if tempcnt = 0><#break></#if>

</#list>



  </table>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>


<#--   -----------------------------weed end ----------------------------------------------------------------    -->

<#--    ---------------- week start 4--------------------------------------------------------------------------   -->


<#if periods?has_content> 

  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">    

<td class="centered" style="background-color:#C0C0C0;"><a name="week4">Week 4</a></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>

  <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
    
    <#assign currentPeriod = false/>  
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>

  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 
</#if>
<#if (weekdays=7)><#break></#if>

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

  <#assign tempcnt=0>
    <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc > 21)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>


 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
 
 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
      <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if>    
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign x=0>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if> onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>           
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>    >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>          
      ${Records.punchtime}  
     
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
   
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   
    <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>  >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>                    
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>

<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 21)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
          <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
      
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>          
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>                
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}&emplPunchId=${cellid?string}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>              
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">   
     <#if Records.emplPunchId=cellid?string>               
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>

<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 21)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
        <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>            
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">    
       <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>                  
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>  
  </#if>                                         
  <#if (weekdays=7)><#break></#if>

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>21)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">
     <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>            
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#if tempcnt = 0><#break></#if>

</#list>



  </table>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>


<#--   -----------------------------weed end ----------------------------------------------------------------    -->

<#--    ---------------- week start 5--------------------------------------------------------------------------   -->


<#if periods?has_content> 

<#assign monthdays=0/>
<#list periods as period>
<#assign monthdays=(monthdays+1)/>
</#list>
<#if (monthdays>28)>

  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">    

<td class="centered" style="background-color:#C0C0C0;"><a name="week5">Week 5</a></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>

  <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
    
    <#assign currentPeriod = false/>  
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>

  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 
</#if>
<#if (weekdays=7)><#break></#if>

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

  <#assign tempcnt=0>
  <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 28)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
        </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>


 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
 
 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
      <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if>    
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   <#assign x=0>
   <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if> onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>           
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>    >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>          
      ${Records.punchtime}  
     
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
   
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   
    <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
      <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>             
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>  >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>                    
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>

<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 28)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
       </#if>
        <#assign wc=wc+1>
      
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
            <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>          
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>                
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}&emplPunchId=${cellid?string}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  </#if>
<#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 

 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:white;cursor:pointer"<#else>style="background-color:white;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>              
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">   
     <#if Records.emplPunchId=cellid?string>               
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 
<#if tempcnt = 0><#break></#if>

</#list>

<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#assign wc=0>
    <#assign wd=0>
   <#list periods as period>      
    <#if (wc >= 28)>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
         <#assign wd=wd+1>
        </#if>
        <#assign wc=wc+1>
       
       <#if (wd>7)><#break></#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>

<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
     <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>            
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">    
       <#if Records.emplPunchId=cellid?string>               
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>                  
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>  
  </#if>                                         
  <#if (weekdays=7)><#break></#if>

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 


 <#assign weekcontrol=0/>
  <#assign weekdays=0/>
  <#list periods as period>
   <#assign cellidt1=period.start?date>
         <#assign cellid2=cellidt1?substring(4,6)>
           <#if cellid2?ends_with(",")>
                <#assign cellid2=cellidt1?substring(4,5)>  
            </#if> 
         <#assign cellid = (cellid2?number % 7)>
         <#if cellid=0>
          <#assign cellid=7>
         </#if>
         <#if (i>1)>
           <#list 1..(i-1) as li>
             <#assign cellid=cellid+7>
           </#list>
          </#if>
   
     
      
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign weekcontrol=(weekcontrol+1)/>
    <#if (weekcontrol>28)>
     <#assign weekdays=(weekdays+1)/>
  <td class="centered" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>style="background-color:#F0F8FF;cursor:pointer"<#else>style="background-color:#F0F8FF;"</#if>  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>         
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> <#list data as Records> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"<#break></#list>
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">
     <#if Records.emplPunchId=cellid?string>                
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>            
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  </#if>
 <#if (weekdays=7)><#break></#if>
  </#list>
  </tr> 

<#if tempcnt = 0><#break></#if>

</#list>



  </table>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>
</#if>

<#--   -----------------------------weed end ----------------------------------------------------------------    -->


<#--  </table>  -->

<#else> 
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
