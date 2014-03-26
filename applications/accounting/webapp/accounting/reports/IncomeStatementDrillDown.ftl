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
			<div class="grid-header" style="width:80%">
				<label>Revenue</label>
                <span style="float:right" class="ui-icon ui-icon-search" title="Toggle search panel" onclick="toggleFilterRow()"></span>
			</div>
			<div id="myGrid" style="width:80%;height:300px;">
			      <label>Revenue</label>
			</div>
			
			<div id="pager" style="width:80%;height:20px;"></div>
			<br>
			<!--EXPENCE -->
			<div class="grid-header" style="width:80%">
				<label>Expence</label>
                <span style="float:right" class="ui-icon ui-icon-search" title="Toggle search panel" onclick="toggleFilterRowExp()"></span>
			</div>
			<div id="myGridExp" style="width:80%;height:500px;">
			      <label>Expence</label>
			</div>
			<div id="pagerExp" style="width:80%;height:20px;"></div>
			<br>
			<!--INCOME -->
			<div class="grid-header" style="width:80%">
				<label>Income</label>
                <span style="float:right" class="ui-icon ui-icon-search" title="Toggle search panel" onclick="toggleFilterRowInc()"></span>
			</div>
			<div id="myGridInc" style="width:80%;height:300px;">
			      <label>Income</label>
			</div>
			<div id="pagerInc" style="width:80%;height:20px;"></div>
			<!--Balance Totals -->
			<br/>
			<div class="grid-header" style="width:80%">
				<label>Balance Totals</label>
			</div>
			<div id="myGridBato" style="width:50%;height:150px;">
			      <label>Income</label>
			</div>
		</div>

        <div id="inlineFilterPanel" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGrouping()">Clear grouping</button>
                    <button onclick="groupByParentGlAccount()">Group by ParentGlaccount</button>
        </div>
       <div id="inlineFilterPanelExp" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGroupingExp()">Clear grouping</button>
                    <button onclick="groupByParentGlAccountExp()">Group by ParentGlaccount</button>
        </div>
         <div id="inlineFilterPanelInc" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGroupingInc()">Clear grouping</button>
                    <button onclick="groupByParentGlAccountInc()">Group by ParentGlaccount</button>
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
<br/>
<script type="application/javascript">
      
		var dataView;
		var grid;
		var data = ${StringUtil.wrapString(jsonRevenueData)};
		var parentGlJson = ${StringUtil.wrapString(parentGlJson)};
		var selectedRowIds = [];

		var columns = [
			{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:20, cannotTriggerInsert:true, resizable:false, unselectable:true },
			{id:"glAccountId", name:"Glaccount ID", field:"glAccountId", width:80, minWidth:80, cssClass:"cell-title", sortable:true},
			{id:"parentGlAccountId", name:"Parent GlAccount", field:"parentGlAccountId", width:130, minWidth:130, cssClass:"cell-title",  sortable:true},
			{id:"accountName", name:"Account Name", field:"accountName", width:200, minWidth:200, cssClass:"cell-title", sortable:true},
			{id:"balance", name:"Balance", field:"balance", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
						
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
        function groupByParentGlAccount() {
        
           dataView.groupBy(
                function (row) {
                	return row["parentGlAccountId"];
                },
                function (g) {
                	var rows = g.rows;
                	var total = 0;
                	var currencySymbol;
            		for (var i = 0, l = rows.length; i < l; i++) {
                		total += rows[i]["balance"];
                		currencySymbol = rows[i]["currencySymbol"];
                	}
                	total = total.toFixed(2);
                	glAccName = "";
                	if(parentGlJson[g.value] != undefined){
                	   glAccName = parentGlJson[g.value];
                	}                	
                    return "<span style='color:green'>"+glAccName +"  ("+ g.value +" ) [Total =" + currencySymbol + total + "]</span>";
                },
                function (a, b) {
                    //return a.value - b.value;
                    return (parseInt(a.value) == parseInt(b.value))
                }
            );
            dataView.collapseAllGroups();
        }//endof 
       
        
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
            groupByParentGlAccount();
            dataView.collapseAllGroups();
            dataView.collapseGroup(0);
			dataView.endUpdate();

		})
		
		
		

</script>

<!-- EXPENCE -->
 <div id="inlineFilterPanelExp" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGroupingExp()">Clear grouping</button>
                    <button onclick="groupByParentGlAccountExp()">Group by ParentGlaccount</button>
</div>
<br/>
<script type="application/javascript">
		var dataViewExp;
		var gridExp;
		var dataExp = ${StringUtil.wrapString(jsonExpenceData)};
		var selectedRowIdsExp = [];

		var columns = [
			{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:20, cannotTriggerInsert:true, resizable:false, unselectable:true },
			{id:"glAccountId", name:"Glaccount ID", field:"glAccountId", width:80, minWidth:80, cssClass:"cell-title", sortable:true},
			{id:"parentGlAccountId", name:"Parent GlAccount", field:"parentGlAccountId", width:130, minWidth:130, cssClass:"cell-title",  sortable:true},
			{id:"accountName", name:"Account Name", field:"accountName", width:200, minWidth:200, cssClass:"cell-title", sortable:true},
			{id:"balance", name:"Balance", field:"balance", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
						
		];

		var options = {
			forceFitColumns: false,
            secondaryHeaderRowHeight: 25
		};

		var sortcol = "title";
		var sortdir = 1;
		var searchString = "";

        function clearGroupingExp() {
            dataViewExp.groupBy(null);
        }
        function groupByParentGlAccountExp() {
        
           dataViewExp.groupBy(
                function (row) {
                	return row["parentGlAccountId"];
                },
                function (g) {
                	var rows = g.rows;
                	var total = 0;
                	var currencySymbol;
            		for (var i = 0, l = rows.length; i < l; i++) {
                		total += rows[i]["balance"];
                		currencySymbol = rows[i]["currencySymbol"];
                	}
                	total = total.toFixed(2);    
                	glAccName = "";
                	if(parentGlJson[g.value] != undefined){
                	   glAccName = parentGlJson[g.value];
                	}            	
                    return "<span style='color:green'>"+glAccName +"  ("+ g.value +" ) [Total =" + currencySymbol + total + "]</span>";
                },
                function (a, b) {
                    //return a.value - b.value;
                    return (parseInt(a.value) == parseInt(b.value))
                }
            );
           dataViewExp.collapseAllGroups(); 
        }//endof 
              
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
			var item = {"num": dataExp.length, "id": "new_" + (Math.round(Math.random()*10000)), "title":"New task", "duration":"1 day", "percentComplete":0, "start":"01/01/2009", "finish":"01/01/2009", "effortDriven":false};
			$.extend(item,newItem);
			dataViewExp.addItem(item);
		}


        function toggleFilterRowExp() {
            if ($(gridExp.getTopPanel()).is(":visible"))
                gridExp.hideTopPanel();
            else
                gridExp.showTopPanel();
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
			dataViewExp = new Slick.Data.DataView({
                groupItemMetadataProvider: groupItemMetadataProvider
            });
			gridExp = new Slick.Grid("#myGridExp", dataViewExp, columns, options);

            // register the group item metadata provider to add expand/collapse group handlers
            gridExp.registerPlugin(groupItemMetadataProvider);

            gridExp.setSelectionModel(new Slick.CellSelectionModel());

			var pagerExp = new Slick.Controls.Pager(dataViewExp, gridExp, $("#pagerExp"));
			var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);

            // move the filter panel defined in a hidden div into grid top panel
            $("#inlineFilterPanelExp")
                .appendTo(gridExp.getTopPanel())
                .show();

			gridExp.onSort.subscribe(function(e, args) {
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
                    dataViewExp.fastSort((sortcol=="percentComplete")?percentCompleteValueFn:sortcol,args.sortAsc);
                }
                else {
                    // using native sort with comparer
                    // preferred method but can be very slow in IE with huge datasets
                    dataViewExp.sort(comparer, args.sortAsc);
                }
            });

			// wire up model events to drive the grid
			dataViewExp.onRowCountChanged.subscribe(function(e,args) {
				gridExp.updateRowCount();
                gridExp.render();
			});

			dataViewExp.onRowsChanged.subscribe(function(e,args) {
				gridExp.invalidateRows(args.rows);
				gridExp.render();
			});


			var h_runfilters = null;




			// initialize the model after all the events have been hooked up
			dataViewExp.beginUpdate();
			dataViewExp.setItems(dataExp);
            groupByParentGlAccountExp();
            dataViewExp.collapseAllGroups();
            dataViewExp.collapseGroup(0);
			dataViewExp.endUpdate();

			//$("#gridContainer").resizable();
		})
		

</script>
<br/>

<!-- INCOME -->

 <div id="inlineFilterPanelInc" style="display:none;background:#dddddd;padding:3px;color:black;">
                    <button onclick="clearGroupingInc()">Clear grouping</button>
                    <button onclick="groupByParentGlAccountInc()">Group by ParentGlaccount</button>
</div>
<script type="application/javascript">
		var dataViewInc;
		var gridInc;
		var dataInc = ${StringUtil.wrapString(jsonIncomeData)};
		var selectedRowIdsInc = [];

		var columns = [
			{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:20, cannotTriggerInsert:true, resizable:false, unselectable:true },
			{id:"glAccountId", name:"Glaccount ID", field:"glAccountId", width:80, minWidth:80, cssClass:"cell-title", sortable:true},
			{id:"parentGlAccountId", name:"Parent GlAccount", field:"parentGlAccountId", width:130, minWidth:130, cssClass:"cell-title",  sortable:true},
			{id:"accountName", name:"Account Name", field:"accountName", width:200, minWidth:200, cssClass:"cell-title", sortable:true},
			{id:"balance", name:"Balance", field:"balance", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
						
		];

		var options = {
			forceFitColumns: false,
            secondaryHeaderRowHeight: 25
		};

		var sortcol = "title";
		var sortdir = 1;
		var searchString = "";

        function clearGroupingInc() {
            dataViewInc.groupBy(null);
        }
        function groupByParentGlAccountInc() {
        
           dataViewInc.groupBy(
                function (row) {
                	return row["parentGlAccountId"];
                },
                function (g) {
                	var rows = g.rows;
                	var total = 0;
                	var currencySymbol;
            		for (var i = 0, l = rows.length; i < l; i++) {
                		total += rows[i]["balance"];
                		currencySymbol = rows[i]["currencySymbol"];
                	}
                	total = total.toFixed(2); 
                	glAccName = "";
                	if(parentGlJson[g.value] != undefined){
                	   glAccName = parentGlJson[g.value];
                	}               	
                    return "<span style='color:green'>"+ glAccName +"  ("+ g.value +" ) [Total =" + currencySymbol + total + "]</span>";
                },
                function (a, b) {
                    //return a.value - b.value;
                    return (parseInt(a.value) == parseInt(b.value))
                }
            );
             dataViewInc.collapseAllGroups();
        }//endof 
              
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
			var item = {"num": dataInc.length, "id": "new_" + (Math.round(Math.random()*10000)), "title":"New task", "duration":"1 day", "percentComplete":0, "start":"01/01/2009", "finish":"01/01/2009", "effortDriven":false};
			$.extend(item,newItem);
			dataViewInc.addItem(item);
		}


        function toggleFilterRowInc() {
            if ($(gridInc.getTopPanel()).is(":visible"))
                gridInc.hideTopPanel();
            else
                gridInc.showTopPanel();
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
			dataViewInc = new Slick.Data.DataView({
                groupItemMetadataProvider: groupItemMetadataProvider
            });
			gridInc = new Slick.Grid("#myGridInc", dataViewInc, columns, options);

            // register the group item metadata provider to add Expand/collapse group handlers
            gridInc.registerPlugin(groupItemMetadataProvider);

            gridInc.setSelectionModel(new Slick.CellSelectionModel());

			var pagerInc = new Slick.Controls.Pager(dataViewInc, gridInc, $("#pagerInc"));
			var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);

            // move the filter panel defined in a hidden div into grid top panel
            $("#inlineFilterPanelInc")
                .appendTo(gridInc.getTopPanel())
                .show();

			gridInc.onSort.subscribe(function(e, args) {
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
                    dataViewInc.fastSort((sortcol=="percentComplete")?percentCompleteValueFn:sortcol,args.sortAsc);
                }
                else {
                    // using native sort with comparer
                    // preferred method but can be very slow in IE with huge datasets
                    dataViewInc.sort(comparer, args.sortAsc);
                }
            });

			// wire up model events to drive the grid
			dataViewInc.onRowCountChanged.subscribe(function(e,args) {
				gridInc.updateRowCount();
                gridInc.render();
			});

			dataViewInc.onRowsChanged.subscribe(function(e,args) {
				gridInc.invalidateRows(args.rows);
				gridInc.render();
			});


			var h_runfilters = null;




			// initialize the model after all the events have been hooked up
			dataViewInc.beginUpdate();
			dataViewInc.setItems(dataInc);
            groupByParentGlAccountInc();
            dataViewInc.collapseAllGroups();
            dataViewInc.collapseGroup(0);
			dataViewInc.endUpdate();
		})
		

</script>
<br/>
<script type="application/javascript">
		var dataViewBato;
		var gridBato;
		var dataBato = ${StringUtil.wrapString(jsonBalanceTotalData)};
		var selectedRowIdsBato = [];

		var columnsBato = [
			{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:20, cannotTriggerInsert:true, resizable:false, unselectable:true },
			{id:"totalName", name:"Total", field:"totalName", width:180, minWidth:80, cssClass:"cell-title", sortable:true},
			{id:"balance", name:"Balance", field:"balance", width:100, minWidth:100, cssClass:"cell-title", sortable:true},
						
		];

		var options = {
			forceFitColumns: false,
            secondaryHeaderRowHeight: 25
		};

		var sortcol = "title";
		var sortdir = 1;
		var searchString = "";

       
              
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
			var item = {"num": dataBato.length, "id": "new_" + (Math.round(Math.random()*10000)), "title":"New task", "duration":"1 day", "percentComplete":0, "start":"01/01/2009", "finish":"01/01/2009", "effortDriven":false};
			$.extend(item,newItem);
			dataViewBato.addItem(item);
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
			dataViewBato = new Slick.Data.DataView({
                groupItemMetadataProvider: groupItemMetadataProvider
            });
			gridBato = new Slick.Grid("#myGridBato", dataViewBato, columnsBato, options);

            // register the group item metadata provider to add Expand/collapse group handlers
            gridBato.registerPlugin(groupItemMetadataProvider);

            gridBato.setSelectionModel(new Slick.CellSelectionModel());

			var columnpicker = new Slick.Controls.ColumnPicker(columns, gridBato, options);

            // move the filter panel defined in a hidden div into grid top panel
           

			gridBato.onSort.subscribe(function(e, args) {
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
                    dataViewBato.fastSort((sortcol=="percentComplete")?percentCompleteValueFn:sortcol,args.sortAsc);
                }
                else {
                    // using native sort with comparer
                    // preferred method but can be very slow in IE with huge datasets
                    dataViewBato.sort(comparer, args.sortAsc);
                }
            });

			// wire up model events to drive the grid
			dataViewBato.onRowCountChanged.subscribe(function(e,args) {
				gridBato.updateRowCount();
                gridBato.render();
			});

			dataViewBato.onRowsChanged.subscribe(function(e,args) {
				gridBato.invalidateRows(args.rows);
				gridBato.render();
			});


			var h_runfilters = null;
			// initialize the model after all the events have been hooked up
			dataViewBato.beginUpdate();
			dataViewBato.setItems(dataBato);
            dataViewBato.collapseGroup(0);
			dataViewBato.endUpdate();
		})
		

</script>




