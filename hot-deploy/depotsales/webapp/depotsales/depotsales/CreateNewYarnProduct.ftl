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

<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/newCrseJquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


	<script type="text/javascript">
			
		$(document).ready(function(){
			
			$(".datepick").datepicker({ 
				dateFormat: "dd-mm-yy"
			});
			
			$( "#fromDate" ).datepicker({
				dateFormat:'MM d, yy',
				changeMonth: true,
				numberOfMonths: 2,
				onSelect: function( selectedDate ) {
					$( "#thruDate" ).datepicker( "option", "minDate", selectedDate );
					go();
				}
			});
			$( "#thruDate" ).datepicker({
				dateFormat:'MM d, yy',
				changeMonth: true,
				numberOfMonths: 2,
				onSelect: function( selectedDate ) {
					$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
				}
			});
			$('#ui-datepicker-div').css('clip', 'auto');
			
		});
			 
			 
			 
		function go(){
			var courseId = $('[name=courseId]').val();
     		courseBatchs(courseId);
     		courseTerms(courseId);
		}	
		function courseTerms(courseId) {
			 var paramName = 'termId';
			 var courseId = courseId;
			 
			 $.ajax({
				 type: "POST",
	             url: 'getCourseSemistersAjax',
	             data: {courseId : courseId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available batches');
					}else{
						semisterList = result["courseSemisters"];
						var optionList = "<option value = '' >Select Term</option>";    		
						var list= semisterList;
						if (list) {		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];
				                optionList += "<option value = " + innerList.courseItemSeqId + " >" + innerList.courseItemName +"</option>";          			
				      		}
				      	}		
				      	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
						
					}								 
				},
				error: function(){
					alert("No Batchs Found found");
				}							
			});
			return false;
		} 
		function courseBatchs(courseId) {
			 var paramName = 'batchId';
			 var courseId = courseId;
			 var priceType = $("#priceType").find(":selected").val();
			 
			 $('#courseSubjects tr').not(function(){if ($(this).has('th').length){return true}}).remove();
			 
			 $.ajax({
				 type: "POST",
	             url: 'getCourseBatchesAjax',
	             data: {courseId : courseId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available batches');
					}else{
						batchList = result["availableBatchList"];
						var optionList = "<option value = '' >Select Batch</option>";   		
						var list= batchList;
						if (list) {		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.batchId + " >" + innerList.startDate +" to "+ innerList.endDate + "(" + innerList.batchId + ")</option>";          			
				      		}
				      	}		
				      	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
						
					}								 
				},
				error: function(){
					alert("No Batchs Found found");
				}							
			});
			
			return false;
		}
		
		function populateSubjects() {
			 var courseId = $('[name=courseId]').val();
			 var semister = $("#termId").find(":selected").val();
			 $('#courseSubjects tr').not(function(){if ($(this).has('th').length){return true}}).remove();
			 
			 var minDate = $("#fromDate").datepicker( 'getDate' );
			 var maxDate = $("#thruDate").datepicker( 'getDate' );
			 
			 if(semister.length > 0){
			 	 $.ajax({
					 type: "POST",
		             url: 'getCourseSubjectsAjax',
		             data: {courseId : courseId,
		             		courseItemSeqId : semister
		             		},
		             dataType: 'json',
			            
					 success:function(result){
						if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
		                    alert('Error Fetching available batches');
						}else{
							courseSubjectList = result["courseSubjectList"];
							if (courseSubjectList) {
							
								$("#courseSubjects").find('tbody')
									.append($('<tr>')
								        .append($('<td>')
								        	.append("Id")
								        ).css({backgroundColor: "#BADEDD", color: "#000099", "font-size" : "12px", "font-weight" : "bold" })
								        .append($('<td>')
								        	.append("Subject Name")
								        )
								        .append($('<td>')
								            .append("Examination Date")
								        )
								        .append($('<td>')
								            .append("Max Marks")
								        )
									);
											       				        	
					        	for(var i=0 ; i<courseSubjectList.length ; i++){
									var eachSubject=courseSubjectList[i];	
									
									$("#courseSubjects").find('tbody')
									    .append($('<tr>')
									        .append($('<td>')
									        	.append(eachSubject.subjectId)
									        ).css({backgroundColor: "#CCE1EE", color: "#000099"})
									        .append($('<td>')
									        	.append(eachSubject.subjectName)
									        )
									        .append($('<td>')
									            .append($('<input type="text"/>')
									            	.attr("id", eachSubject.subjectId)
									            	.attr("name", "examDates[]")
									            )
									        )
									        .append($('<td>')
									            .append($('<input type="text"/>')
									            	.attr("id", eachSubject.subjectId+"_Marks")
									            	.attr("name", "maxMarks[]")
									            )
									        )
										);
										
										//$("#examDetails").css('border', '2px solid blue');
										$("#courseSubjects").css('border', '2px solid lightblue');
										$("#courseSubjects").css('backgroundColor', '#82BEE1');
									
									
									$("#"+eachSubject.subjectId).datepicker({ 
										dateFormat: "dd-mm-yy", minDate: minDate, maxDate: maxDate
									});
					      		}
					      	}		
					      	
					      	
							
						}								 
					},
					error: function(){
						alert("No Subjects Found");
					}							
				});
			 }
			
			return false;
		}
		
		function appendExamDates() {
			
		    examDatesList = $('input[name="examDates[]"]').map(function() {
		    	var subjectId = this.id;
		    	var dateVal = this.value;
		    	var marks = $("#"+subjectId+"_Marks").val();
		    	var subjectDateStr = '';
		    	if(dateVal.length > 1){
		    		subjectDateStr = subjectId + ":" + dateVal + ":" + marks;
		    		return subjectDateStr
		    	}
		    }).get()
		    
			var input = $("<input>")
			               .attr("type", "hidden")
			               .attr("name", "examDatesList").val(examDatesList);
			$("#CreateExamForm").append($(input));
			
			return true;
		}
		
	</script>
      
        <form id="CreateExamForm" name="CreateExamForm" method="post" action="<@ofbizUrl>createExamination</@ofbizUrl>" onSubmit="return appendExamDates();">
           <fieldset>
			  <table id="examDetails" cellpadding="5" cellspacing="10" border="3px">
       		  	<tr>
					<td width="10%" ><FONT COLOR="#000099"><b> Course Id</b></FONT></td>
					<td width="80%">
						 <@htmlTemplate.lookupField value='${requestParameters.courseId?if_exists}' formName="CreateExamForm" name="courseId" id="courseId" fieldFormName="LookupCourseId"/>
					</td>
				</tr>
				<tr>
					<td width="10%" ><FONT COLOR="#000099"><b> Start Date: </b></FONT></td>
	            	<td width="20%"><input class='h2' type="text" id="fromDate" name="fromDate"/></td>
	            </tr>
	            <tr>
	            	<td width="10%" ><FONT COLOR="#000099"><b>End Date: </b></FONT></td>
					<td width="20%"><input class='h2' type="text" id="thruDate" name="thruDate"/></td></tr>
	            </tr>	
				<tr>
					<td width="10%"><FONT COLOR="#000099"><b> Batch Id</b></FONT></td>
					<td width="80%">
				         <select name="batchId" id="batchId">
					     </select>
					</td>
				</tr>
				<tr>
					<td width="10%" ><FONT COLOR="#000099"><b> Exam Name: </b></FONT></td>
	            	<td width="20%"><input class='h2' type="text" id="examName" name="examName"/></td>
	            </tr>
	            <tr>
					<td width="10%" ><FONT COLOR="#000099"><b> Term Id: </b></FONT></td>
					<td width="80%">
				         <select name="termId" id="termId" onchange="populateSubjects()">
				         	
					     </select>
					</td>
	            </tr>
      		  </table>
	  	   </fieldset>
	  	   
	  	   <fieldset>
				<table class="form-table" id="courseSubjects" cellpadding="0" cellspacing="10" border="3">
					<tbody>   
				    </tbody>
				</table>
		   </fieldset>
	  	   
		   <table class="form-table">
			  	<tr>
			    	<td>&nbsp;&#160;&#160;&#160;</td>
			      	<td>&nbsp;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<input type="submit" name="Create" value="Create"  id="submit"/>
			      	<td>&nbsp;&#160;&#160;&#160;</td>
			  	</tr>
		  	</table>
 		</form>







