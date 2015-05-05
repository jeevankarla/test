$(function() {

	changeRowColor();
});

// we have to use color scheme for vehcileStATus List
function changeRowColor() {

	var tableObj = $('#_col table tr');
	$(tableObj).each(function(index, element) {
		
		var resultEnter = $(this).find("td:contains('VEHICLE ENTERED')");
		var compText = $.trim($(resultEnter).text());
		if (compText == ("VEHICLE ENTERED")) {
			$(this).css("background-color", "#A9A9A9");
		}
		
		var resultGrw = $(this).find("td:contains('VEHICLE AT GROSSWEIGHT')");
		var compTextGrw = $.trim($(resultGrw).text());
		if (compTextGrw == ("VEHICLE AT GROSSWEIGHT")) {
			$(this).css("background-color", "#F0E68C");
		}
		var resultQc = $(this).find("td:contains('VEHICLE AT QUALITY CONTROL')");
		var compTextQc = $.trim($(resultQc).text());
		if (compTextQc == ("VEHICLE AT QUALITY CONTROL")) {
			$(this).css("background-color", "#FF7F50");
		}
		var resultCip = $(this).find("td:contains('VEHICLE AT CIP')");
		var compTextCip = $.trim($(resultCip).text());
		if (compTextCip == ("VEHICLE AT CIP")) {
			$(this).css("background-color", "#6495ED");
		}
		var resultTw = $(this).find("td:contains('VEHICLE AT TAREWEIGHT')");
		var compTextTw = $.trim($(resultTw).text());
		if (compTextTw == ("VEHICLE AT TAREWEIGHT")) {
			//#8A2BE2
			$(this).css("background-color", "#FFEBCD");
		}
		
		var resultOUT = $(this).find("td:contains('VEHICLE OUT')");
		var compTextOUT = $.trim($(resultOUT).text());
		if (compTextOUT == ("VEHICLE OUT")) {
			$(this).css("background-color", "#90EE90");
		}

	});

	// 
}
