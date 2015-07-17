$(function() {

	changeRowColor();
});

// we have to use color scheme for vehcileStATus List
function changeRowColor() {

	var tableObj = $('#_col table tr');
	$(tableObj).each(function(index, element) {
		
		var resultEnter = $(this).find("td:contains('ENTRY')");
		var compText = $.trim($(resultEnter).text());
		if (compText == ("ENTRY")) {
			$(this).css("background-color", "#A9A9A9");
		}
		
		var resultGrw = $(this).find("td:contains('GROSS WEIGHT')");
		var compTextGrw = $.trim($(resultGrw).text());
		if (compTextGrw == ("GROSS WEIGHT")) {
			$(this).css("background-color", "#F0E68C");
		}
		var resultQc = $(this).find("td:contains('QC')");
		var compTextQc = $.trim($(resultQc).text());
		if (compTextQc == ("QC")) {
			$(this).css("background-color", "#FF7F50");
		}
		var resultCip = $(this).find("td:contains('UNLOAD')");
		var compTextCip = $.trim($(resultCip).text());
		if (compTextCip == ("UNLOAD")) {
			$(this).css("background-color", "#6495ED");
		}
		var resultCip = $(this).find("td:contains('CIP')");
		var compTextCip = $.trim($(resultCip).text());
		if (compTextCip == ("CIP")) {
			$(this).css("background-color", "#88FAFA");
		}
		var resultTw = $(this).find("td:contains('TARE WEIGHT')");
		var compTextTw = $.trim($(resultTw).text());
		if (compTextTw == ("TARE WEIGHT")) {
			//#8A2BE2
			$(this).css("background-color", "#FFEBCD");
		}
		
		var resultOUT = $(this).find("td:contains('EXIT')");
		var compTextOUT = $.trim($(resultOUT).text());
		if (compTextOUT == ("EXIT")) {
			$(this).css("background-color", "#90EE90");
		}

	});

	// 
}
