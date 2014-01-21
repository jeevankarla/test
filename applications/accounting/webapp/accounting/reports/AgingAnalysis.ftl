
		<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
        <link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
		<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
	<style type="text/css">

		.cell-title {
			font-weight: normal;
		}

		.cell-effort-driven {
			text-align: center;
		}


	</style>

		<div style="width:960px;float:left;">
			<div class="grid-header" style="width:100%">
				<label>Aging Analysis for Receivables</label>
                <span style="float:right" class="ui-icon ui-icon-search" title="Toggle search panel" onclick="toggleFilterRow()"></span>
			</div>
			<div id="myGrid" style="width:100%;height:500px;"></div>
			<div id="pager" style="width:100%;height:20px;"></div>
		</div>




        <div id="inlineFilterPanel" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGrouping()">Clear grouping</button>
                    <button onclick="groupByOverDue()">Group by Over Due</button>
        </div>

		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>

		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>

		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
		<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
		var dataView;
		var grid;
		var data = ${StringUtil.wrapString(jsonData)};
		var selectedRowIds = [];

		var columns = [
			{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:20, cannotTriggerInsert:true, resizable:false, unselectable:true },
			{id:"invoiceId", name:"Invoice ID", field:"invoiceId", width:80, minWidth:80, cssClass:"cell-title", sortable:true},
			{id:"invoiceType", name:"Invoice Type", field:"invoiceType", width:130, minWidth:130, cssClass:"cell-title",  sortable:true},
			{id:"party", name:"Party", field:"party", width:200, minWidth:200, cssClass:"cell-title", sortable:true},
			{id:"invoiceDate", name:"Invoice Date", field:"invoiceDate", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
			{id:"dueDate", name:"Due Date", field:"dueDate", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
			{id:"overDue", name:"Over Due", field:"overDue", width:70, minWidth:70, cssClass:"cell-title", sortable:true},
			{id:"total", name:"Total", field:"total", width:120, minWidth:120, cssClass:"cell-title", sortable:true},
			{id:"outstandingAmount", name:"Outstanding", field:"outstandingAmount", width:120, minWidth:120, cssClass:"cell-title", sortable:true},			
		];

		var options = {
			forceFitColumns: false,
            secondaryHeaderRowHeight: 25
		};

		var sortcol = "title";
		var sortdir = 1;
		var searchString = "";

        function clearGrouping() {
            dataView.groupBy(null);
        }

        function groupByOverDue() {
            dataView.groupBy(
                function (row) {
                	return parseInt(row["overDue"]/30) + 1;
                },
                function (g) {
                	var rows = g.rows;
                	var total = 0;
                	var currencySymbol;
            		for (var i = 0, l = rows.length; i < l; i++) {
                		total += rows[i]["outstandingAmountRaw"];
                		currencySymbol = rows[i]["currencySymbol"];
                	}
                	total = total.toFixed(2);                	
                    return "<span style='color:red'>Over Due:  " + (g.value - 1) * 30 + " - " + g.value*30 + " days  [Invoices=" + g.count + "; Total Outstanding=" + currencySymbol + total + "]</span>";
                },
                function (a, b) {
                    //return a.value - b.value;
                    return (parseInt(a.value/30) == parseInt(b.value/30))
                }
            );
        }
        
		function requiredFieldValidator(value) {
			if (value == null || value == undefined || !value.length)
				return {valid:false, msg:"This is a required field"};
			else
				return {valid:true, msg:null};
		}


		function comparer(a,b) {
			var x = a[sortcol], y = b[sortcol];
			return (x == y ? 0 : (x > y ? 1 : -1));
		}

		function addItem(newItem,columnDef) {
			var item = {"num": data.length, "id": "new_" + (Math.round(Math.random()*10000)), "title":"New task", "duration":"1 day", "percentComplete":0, "start":"01/01/2009", "finish":"01/01/2009", "effortDriven":false};
			$.extend(item,newItem);
			dataView.addItem(item);
		}


        function toggleFilterRow() {
            if ($(grid.getTopPanel()).is(":visible"))
                grid.hideTopPanel();
            else
                grid.showTopPanel();
        }


        $(".grid-header .ui-icon")
            .addClass("ui-state-default ui-corner-all")
            .mouseover(function(e) {
                $(e.target).addClass("ui-state-hover")
            })
            .mouseout(function(e) {
                $(e.target).removeClass("ui-state-hover")
            });

		$(function()
		{
            var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
			dataView = new Slick.Data.DataView({
                groupItemMetadataProvider: groupItemMetadataProvider
            });
			grid = new Slick.Grid("#myGrid", dataView, columns, options);

            // register the group item metadata provider to add expand/collapse group handlers
            grid.registerPlugin(groupItemMetadataProvider);

            grid.setSelectionModel(new Slick.CellSelectionModel());

			var pager = new Slick.Controls.Pager(dataView, grid, $("#pager"));
			var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);

            // move the filter panel defined in a hidden div into grid top panel
            $("#inlineFilterPanel")
                .appendTo(grid.getTopPanel())
                .show();

			grid.onSort.subscribe(function(e, args) {
                sortdir = args.sortAsc ? 1 : -1;
                sortcol = args.sortCol.field;

                if ($.browser.msie && $.browser.version <= 8) {
                    // using temporary Object.prototype.toString override
                    // more limited and does lexicographic sort only by default, but can be much faster

                    var percentCompleteValueFn = function() {
                        var val = this["percentComplete"];
                        if (val < 10)
                            return "00" + val;
                        else if (val < 100)
                            return "0" + val;
                        else
                            return val;
                    };

                    // use numeric sort of % and lexicographic for everything else
                    dataView.fastSort((sortcol=="percentComplete")?percentCompleteValueFn:sortcol,args.sortAsc);
                }
                else {
                    // using native sort with comparer
                    // preferred method but can be very slow in IE with huge datasets
                    dataView.sort(comparer, args.sortAsc);
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


			var h_runfilters = null;




			// initialize the model after all the events have been hooked up
			dataView.beginUpdate();
			dataView.setItems(data);
            dataView.groupBy(
                function (row) {
                	return parseInt(row["overDue"]/30) + 1;
                },
                function (g) {
                	var rows = g.rows;
                	var total = 0;
                	var currencySymbol;
            		for (var i = 0, l = rows.length; i < l; i++) {
                		total += rows[i]["outstandingAmountRaw"];
                		currencySymbol = rows[i]["currencySymbol"];
                	}
                	total = total.toFixed(2);
                    return "<span style='color:red'>Over Due:  " + (g.value - 1) * 30 + " - " + g.value*30 + " days  [Invoices=" + g.count + "; Total Outstanding=" + currencySymbol + total + "]</span>";
                },
                function (a, b) {
                    //return a.value - b.value;
                    return (parseInt(a.value/30) == parseInt(b.value/30))
                }
            );
            dataView.collapseGroup(0);
			dataView.endUpdate();

			$("#gridContainer").resizable();
		})

</script>