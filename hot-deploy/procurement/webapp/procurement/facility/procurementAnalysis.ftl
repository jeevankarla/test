


<script type="application/javascript">  

		var data = [
        <#if procurementEntryList?exists> 
	        <#list procurementEntryList as procurementEntry>				
				{id:"${procurementEntry_index}" , date:"${procurementEntry.date}", supplyType:"${procurementEntry.supplyType}", 
				 product:"${procurementEntry.product}", fat:"${procurementEntry.fat}", snf:"${procurementEntry.snf}", qtyLtrs:"${procurementEntry.qtyLtrs}",
				 qtyKgs:"${procurementEntry.qtyKgs}", price:"${procurementEntry.price}", _collapsed:true} <#if procurementEntry_has_next>,</#if>
			</#list>
		</#if>		
		];
		var dataView; 

var TaskNameFormatter = function (row, cell, value, columnDef, dataContext) {
  var idx = dataView.getIdxById(dataContext.id);
  var spacer = "<span style='display:inline-block;height:1px;width:" + (5) + "px'></span>";  
  if (data[idx] && data[idx].supplyType == "TOT" && data[idx].product == "TOT") {
    if (dataContext._collapsed) {
      return spacer + " <span class='toggle expand'></span>&nbsp;" + value;
    } else {
      return spacer + " <span class='toggle collapse'></span>&nbsp;" + value;
    }
  } else {
   spacer = "<span style='display:inline-block;height:1px;width:" + (15 * 1) + "px'></span>";
    return spacer + " <span class='toggle'></span>&nbsp;" + value;
  }
};

function myFilter(item) {
  if (item["supplyType"] == "TOT" && item["product"] == "TOT") {
    return true;
  }
  for (var i = 0; i < data.length; i++) {
  	var d = data[i];
  	if (item["date"] == d["date"] && d["supplyType"] == "TOT" && d["product"] == "TOT" && d["_collapsed"] == false) {
  		return true;
  	}
  } 
  return false;
}

	function setupGrid1() {
		var grid;		

		var columns = [
			{id:"date", name:"Date", field:"date", width:120, minWidth:70, cssClass:"cell-title", sortable:false, formatter: TaskNameFormatter},	
			{id:"supplyTime", name:"Supply", field:"supplyType", width:100, minWidth:50, cssClass:"cell-title", sortable:false},								
			{id:"product", name:"Product", field:"product", width:100, minWidth:50, cssClass:"cell-title", sortable:false},	
			{id:"fat", name:"Fat", field:"fat", width:70, minWidth:50, cssClass:"cell-title-right", sortable:false},	
			{id:"snf", name:"SNF", field:"snf", width:70, minWidth:50, cssClass:"cell-title-right", sortable:false},
			{id:"qtyLtrs", name:"Quantity (Ltrs)", field:"qtyLtrs", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},
			{id:"quantity", name:"Quantity (Kgs)", field:"qtyKgs", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
			{id:"price", name:"Price (Rs)", field:"price", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: false,
			enableCellNavigation: false,
  			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: false,
		};
		
		dataView = new Slick.Data.DataView({
        	inlineFilters: true 
        });
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
  		dataView.setFilter(myFilter);
		
		dataView.endUpdate();
		        
		grid = new Slick.Grid("#myGrid1", dataView, columns, options);
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);

  		grid.onClick.subscribe(function (e, args) {
    		if ($(e.target).hasClass("toggle")) {
     			var item = dataView.getItem(args.row);
      			if (item) {
        			if (!item._collapsed) {
          				item._collapsed = true;
        			} else {
          				item._collapsed = false;
        			}
        			dataView.updateItem(item.id, item);
      			}
      			e.stopImmediatePropagation();
    		}
  		});		
		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

	}


		setupGrid1();
	
        jQuery(".grid-header .ui-icon")
            .addClass("ui-state-default ui-corner-all")
            .mouseover(function(e) {
                jQuery(e.target).addClass("ui-state-hover")
            })
            .mouseout(function(e) {
                jQuery(e.target).removeClass("ui-state-hover")
            });	 
            
		jQuery.plot($("#graph"), [{label: "Total", data: ${StringUtil.wrapString(listJSON)}}, 
							  {label: "Buffalo Milk", data: ${StringUtil.wrapString(listBMJSON)}},
							  {label: "Cow Milk", data: ${StringUtil.wrapString(listCMJSON)}}], 
				{ 
					series: {
                   		lines: { show: true ,fill:true,
                   		 fillColor: { colors: [{ opacity: 0.7 }, { opacity: 0.1}] }
                   		 },
                   		points: { show: true }
                	},
               		grid: { hoverable: true}, 
               		xaxis: { mode: "time" }, 
        			yaxis: {
            			min: 0,
						axisLabel : 'Qty (Kgs)',
           			    position: 'left'            			
        			}, 
        			legend: {
        				show: true,
    					position: "se"
  					}              		             		            		
				});

    function showTooltip(x, y, contents) {
        $('<div id="tooltip">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 5,
            left: x + 5,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var previousPoint = null;
    $("#graph").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));
        if (item) {
            if (previousPoint != item.dataIndex) {
                previousPoint = item.dataIndex;
                
                $("#tooltip").remove();
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                
                showTooltip(item.pageX, item.pageY,
                            y + " Kgs");
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;            
        }
    });                       

</script>
	<style type="text/css">
		div.graph
		{
			width: 800px;
			height: 300px;
		}
	</style>


<div class="screenlet">
    <div class="screenlet-title-bar">
    <#if facility?has_content>
      <h3>Procurement data for ${facility.facilityName?if_exists} (${parameters.facilityId?if_exists}) [${parameters.fromDate?if_exists} - ${parameters.thruDate?if_exists}] </h3>
	</#if>
    </div>
    <div class="screenlet-body">
   		<div id="graph" class="graph"></div>  
        <br><br><br><br>   		  
		<div id="myGrid1" style="width:800px;height:600px"></div>   		
    </div>
</div>

