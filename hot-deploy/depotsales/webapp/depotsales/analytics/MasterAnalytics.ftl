
<style>
.button1 {
    background-color: grey;
    border: none;
    color: white;
    padding: 5px 5px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 10px;
    margin: 4px 2px;
    cursor: pointer;
}
input[type=button] {
	color: white;
    padding: .5x 7px;
    background:#008CBA;
    border: .8px solid green;
    border:0 none;
    cursor:pointer;
    -webkit-border-radius: 5px;
    border-radius: 5px; 
}
input[type=button]:hover {
    background-color: #3e8e41;
}


ul.tab {
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    border: 1px solid #ccc;
    background-color: lightblue;
    border-radius: 5px;
}

/* Float the list items side by side */
ul.tab li {float: left;}

/* Style the links inside the list items */
ul.tab li a {
    display: inline-block;
    color: black;
    text-align: center;
    padding: 10px 10px;
    text-decoration: none;
    transition: 0.3s;
    font-size: 12px;
}

/* Change background color of links on hover */
ul.tab li a:hover {background-color: #ddd;}

/* Create an active/current tablink class */
ul.tab li a:focus, .active {background-color: #ccc;}

/* Style the tab content */
.tabcontent {
    display: none;
    padding: 6px 12px;
    border: 1px solid #ccc;
    border-top: none;
}

</style>
<script type="text/javascript">

$(document).ready(function(){

$("#branch").val("INT10");

// enter event handle
	$("input").keypress(function(e){
		if (e.which == 13 && e.target.name =="facilityId") {
				$("#getTreeGrid").click();
		}
	});
	
    $('#loader').hide();
	jQuery.ajaxSetup({
  		beforeSend: function() {
     		$('#loader').show();
     		$('#result').hide();
  		},
  		complete: function(){
     		$('#loader').hide();
     		$('#result').show();     
  		}
	});

	$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			onSelect: function( selectedDate ) {					
			    date = $(this).datepicker('getDate');
		        y = date.getFullYear(),
		        m = date.getMonth();
		        d = date.getDate();
		        var maxDate = new Date(y+1, m, d);
				$( "#thruDate" ).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			changeYear: true,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');

});
</script>


 <div id = "firstDiv" style="border-width: 2px;    border-radius: 7px; border-style: solid; border-color: grey; ">
  
			<ul class="tab">
			  <li><a href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'London')">Shipment Analytics</a></li>
			  <li><a href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'Paris')">Supplier Analytics</a></li>
			  <li><a href="javascript:void(0)" class="tablinks" onclick="openCity(event, 'Tokyo')">Indent Analytics</a></li>
			</ul>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			
			<span class='h3'>From Date: </span><input class='h2' type="text" id="fromDate" name="fromDate" value="${defaultEffectiveDate}" readonly="true"/>
			<span class='h3'>Thru Date: </span><input class='h2' type="text" id="thruDate" name="thruDate" value="${defaultEffectiveThruDate}" readonly="true"/>
			<span class='h3'>Days: </span><input  type="text" id="days" name="days" />
			<span class='h3'>Branch: </span><select name="branch" id="branch"><#list formatList as format> <option value="${format.payToPartyId}">${format.productStoreName?if_exists}</option></#list></select>
</div>

