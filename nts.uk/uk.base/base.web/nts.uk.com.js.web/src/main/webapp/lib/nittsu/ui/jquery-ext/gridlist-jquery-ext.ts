/// <reference path="../../reference.ts"/>

interface JQuery {
    ntsGridList(action: string, param?: any): any;
    ntsGridListFeature(feature: string, action: string, ...params: any[]): any;
}

module nts.uk.ui.jqueryExtentions {

    module ntsGridList {

        let OUTSIDE_AUTO_SCROLL_SPEED = {
            RATIO: 0.2,
            MAX: 30
        };

        $.fn.ntsGridListFeature = function(feature: string, action: string, ...params: any[]): any {

            var $grid = $(this);

            switch (feature) {
                case 'switch':
                    switch (action) {
                        case 'setValue':
                            return setSwitchValue($grid, params);
                    }
            }
        };

        function setSwitchValue($grid: JQuery, ...params: any[]): any {
            let rowId: any = params[0][0];
            let columnKey: string = params[0][1];
            let selectedValue: any = params[0][2];
            let $row = $($grid.igGrid("rowById", rowId));
            let $parent = $row.find(".ntsControl");
            let currentSelect = $parent.attr('data-value');
            if (selectedValue !== currentSelect) {
                let rowKey = $row.attr("data-id");
                $parent.find(".nts-switch-button").removeClass("selected");
                let element = _.find($parent.find(".nts-switch-button"), function(e) {
                    return selectedValue.toString() === $(e).attr('data-value').toString();
                });
                if (element !== undefined) {
                    let scrollTop = $("#" + $grid.attr("id") + "_scrollContainer").scrollTop();
                    $(element).addClass('selected');
                    $parent.attr('data-value', selectedValue);
                    $grid.igGridUpdating("setCellValue", rowKey, columnKey, selectedValue);
                    $grid.igGrid("commit");
                    $grid.trigger("switchvaluechanged", {columnKey: columnKey, rowKey: rowKey, value: parseInt(selectedValue)});
                    if ($grid.igGrid("hasVerticalScrollbar")) {
//                        let current = $grid.ntsGridList("getSelected");
//                        if(current !== undefined){
//                            $grid.igGrid("virtualScrollTo", (typeof current === 'object' ? current.index : current[0].index) + 1);        
//                        }
                        if(!nts.uk.util.isNullOrUndefined(scrollTop) && scrollTop !== 0){
                            setTimeout(function (){
                                $("#" + $grid.attr("id") + "_scrollContainer").scrollTop(scrollTop);        
                            }, 10);
                        }
                    }
                }
            }
        }
        
        function delegateMethod($grid: JQuery, action: string, param?: any) {
            switch (action) {
                case 'setupSelecting':
                    return setupSelecting($grid);
                case 'unsetupSelecting':
                    return unsetupSelecting($grid);
                case 'getSelected':
                case 'getSelectedValue':
                    return getSelected($grid);
                case 'setSelected':
                    return setSelected($grid, param);
                case 'setSelectedValue':
                    return setSelectedValue($grid, param);
                case 'setDataSource':
                    $grid.data("initValue", null);
                    $grid.data("selectionDisables", null);
                    return setDataSource($grid, param);
                case 'getDataSource':
                    return getDataSource($grid);
                case 'deselectAll':
                    return deselectAll($grid);
                case 'setupDeleteButton':
                    return setupDeleteButton($grid, param);
                case 'setupScrollWhenBinding':
                    return setupScrollWhenBinding($grid);
                case 'scrollToSelected':
                    return scrollToSelect($grid);
            }
        }
        
        function scrollToSelect($grid: JQuery) {
            var row = null;
			
			if ($grid.data('igGrid') === undefined ) {
                return;
            }
			
            var selectedRows = $grid.igGrid("selectedRows");
            if (selectedRows) {
                row = selectedRows[0];
            } else {
                row = $grid.igGrid("selectedRow");
            }
            
            if (row) {
                if($grid.igGrid("option", "virtualization") === true){
                    ui.ig.grid.virtual.expose(row, $grid);
                } else {
                    ui.ig.grid.expose(row, $grid);
                }    
            }
        }
        
        function setupScrollWhenBinding($grid: JQuery): any {
            let gridId = "#" + $grid.attr("id");
            $(document).delegate(gridId, "iggriddatarendered", function (evt, ui) {
                if (isCheckedAll($grid)) {
                    return;
                }
                let oldSelected = getSelectRow($grid);
                if(!nts.uk.util.isNullOrEmpty(oldSelected)){
                    _.defer(() => { 
                        if (isCheckedAll($grid)) {
                            return;
                        }
                        let selected = getSelectRow($grid);
                        if(!nts.uk.util.isNullOrEmpty(selected)){
                            selected = oldSelected;    
                        } 
                        
                        if($grid.data('igGrid')) {
                            let $scrollContainer: any = $grid.igGrid("scrollContainer");
//                            _.defer(() => {
                                if ($scrollContainer.length > 0) {
                                    let firstRowOffset = $($("#single-list").igGrid("rowAt", 0)).offset().top;
                                    let selectRowOffset = $($("#single-list").igGrid("rowAt", index)).offset().top;
                                    $scrollContainer.scrollTop(selectRowOffset - firstRowOffset);
                                } else if(selected && oldSelected) { 
                                    let index = $(selected["element"]).attr("data-row-idx");
                                    $grid.igGrid("virtualScrollTo", nts.uk.util.isNullOrEmpty(index) ? oldSelected.index : parseInt(index)); //.scrollTop(scrollTop);    
                                }   
//                            });
                        }
                    });    
                }
            });
        }
        
        function isCheckedAll($grid: JQuery){
            if ($grid.data("igGrid") && $grid.igGridSelection('option', 'multipleSelection')) {
                let chk = $grid.closest('.ui-iggrid').find(".ui-iggrid-rowselector-header").find("span[data-role='checkbox']");
                if (chk.attr("data-chk") === "on") {
                    return true;
                }
            }
            
            return false;
        }
        
        function getSelectRow($grid: JQuery) {
            var row = null;
            
            if($grid.data("igGrid")) {
                var selectedRows = $grid.igGrid("selectedRows");
                if (selectedRows) {
                    row = selectedRows[0];
                } else {
                    row = $grid.igGrid("selectedRow");
                }    
            }
            
            return row;
        }

        function getSelected($grid: JQuery): any {
            if ($grid.igGridSelection('option', 'multipleSelection')) {
                var selectedRows: Array<any> = $grid.igGridSelection('selectedRows');
                if (selectedRows)
                    return _.map(selectedRows, convertSelected);
                return [];
            } else {
                var selectedRow: any = $grid.igGridSelection('selectedRow');
                if (selectedRow)
                    return convertSelected(selectedRow);
                return undefined;
            }
        }

        function convertSelected(igGridSelectedRow: any) {
            return {
                id: igGridSelectedRow.id,
                index: igGridSelectedRow.index
            };
        }

        function setSelected($grid: JQuery, selectedId: any) {
            let baseID = _.map($grid.igGrid("option").dataSource, $grid.igGrid("option", "primaryKey"));
            if(_.isEmpty(baseID)){
                return;
            }
            
            if(baseID.length >= 500){
                let oldSelectedID = _.map(getSelected($grid), "id"), 
                    shouldRemove: Array<string> = _.difference(_.isArray(oldSelectedID) ? oldSelectedID : [ oldSelectedID ], selectedId), 
                    shouldSelect: Array<string> = _.difference(_.isArray(selectedId) ? selectedId : [ selectedId ], oldSelectedID);
                /** When data source large (data source > 500 (?)):
                        if new value for select = half of data source
                            or removed selected value = 1/3 of data source, 
                            should deselect all and loop for select,
                        else if deselect old values that not selected and select new selected only*/
                if(shouldSelect.length < baseID.length / 2 || shouldRemove.length < baseID.length / 3) {
                    shouldRemove.forEach(id => $grid.igGridSelection("deselectRowById", id));
                    shouldSelect.forEach(id => $grid.igGridSelection('selectRowById', id));
                    return;
                }    
            }

            if ($grid.igGridSelection('option', 'multipleSelection')) {
                // for performance when select all
                //let baseID = _.map($grid.igGrid("option").dataSource, $grid.igGrid("option", "primaryKey"));
                if (_.isEqual(selectedId, baseID)) {
                    let chk = $grid.closest('.ui-iggrid').find(".ui-iggrid-rowselector-header").find("span[data-role='checkbox']");
                    if (chk.attr("data-chk") === "off") {
                        chk.click();
                    }
                } else {
                    deselectAll($grid);
                    (<Array<string>>selectedId).forEach(id => {
                        if (_.includes(baseID, id)) {
                            $grid.igGridSelection('selectRowById', id)
                        }
                    });
                }
            } else {
                deselectAll($grid);
                $grid.igGridSelection('selectRowById', selectedId);
            }
        }

        function deselectAll($grid: JQuery) {
            $grid.igGridSelection('clearSelection');
        }

        function setupDeleteButton($grid: JQuery, param) {
            var itemDeletedEvent = new CustomEvent("itemDeleted", {
                detail: {},
            });
            var currentColumns = $grid.igGrid("option", "columns");

            currentColumns.push({
                dataType: "bool", columnCssClass: "delete-column", headerText: "test", key: param.deleteField,
                width: 60, formatter: function createButton(deleteField, row) {
                    var primaryKey = $grid.igGrid("option", "primaryKey");
                    var result = $('<button tabindex="-1" class="small delete-button">Delete</button>');
                    result.attr("data-value", row[primaryKey]);
                    if (deleteField === true && primaryKey !== null && !util.isNullOrUndefined(row[primaryKey]) && $grid.data("enable") !== false) {
                        return result[0].outerHTML;
                    } else {
                        return result.attr("disabled", "disabled")[0].outerHTML;
                    }
                }
            });
            $grid.igGrid("option", "columns", currentColumns);

            $grid.on("click", ".delete-button", function() {
                var key = $(this).attr("data-value");
                var primaryKey = $grid.igGrid("option", "primaryKey");
                var source = _.cloneDeep($grid.igGrid("option", "dataSource"));
                _.remove(source, function(current) {
                    return _.isEqual(current[primaryKey].toString(), key.toString());
                });
                if (!util.isNullOrUndefined(param.sourceTarget) && typeof param.sourceTarget === "function") {
                    param.sourceTarget(source);
                } else {
                    $grid.igGrid("option", "dataSource", source);
                    $grid.igGrid("dataBind");
                }
                itemDeletedEvent.detail["target"] = key;
                document.getElementById($grid.attr('id')).dispatchEvent(itemDeletedEvent);
            });

        }

        function setupSelecting($grid: JQuery) {
            setupDragging($grid);
            setupSelectingEvents($grid);

            return $grid;
        }

        function unsetupSelecting($grid: JQuery) {
            unsetupDragging($grid);
            unsetupSelectingEvents($grid);

            return $grid;
        }

        function setupDragging($grid: JQuery) {
            var dragSelectRange = [];

            // used to auto scrolling when dragged above/below grid)
            var mousePos: { x: number, y: number, rowIndex: number } = null;


            $grid.bind('pointerdown', function(e) {

                // グリッド内がマウスダウンされていない場合は処理なしで終了
                var $container = $grid.closest('.ui-iggrid-scrolldiv');
                if ($(e.target).closest('.ui-iggrid-table').length === 0) {
                    return;
                }

                // current grid size
                var gridVerticalRange = new util.Range(
                    $container.offset().top,
                    $container.offset().top + $container.height());

                mousePos = {
                    x: e.pageX,
                    y: e.pageY,
                    rowIndex: ig.grid.getRowIndexFrom($(e.target))
                };

                // set position to start dragging
                dragSelectRange.push(mousePos.rowIndex);

                var $scroller = $('#' + $grid.attr('id') + '_scrollContainer');

                // auto scroll while mouse is outside grid
                var timerAutoScroll = setInterval(() => {
                    var distance = gridVerticalRange.distanceFrom(mousePos.y);
                    if (distance === 0) {
                        return;
                    }

                    var delta = Math.min(distance * OUTSIDE_AUTO_SCROLL_SPEED.RATIO, OUTSIDE_AUTO_SCROLL_SPEED.MAX);
                    var currentScrolls = $scroller.scrollTop();
                    $grid.igGrid('virtualScrollTo', (currentScrolls + delta) + 'px');
                }, 20);

                // handle mousemove on window while dragging (unhandle when mouseup)
                $(window).bind('pointermove.NtsGridListDragging', function(e) {

                    var newPointedRowIndex = ig.grid.getRowIndexFrom($(e.target));

                    // selected range is not changed
                    if (mousePos.rowIndex === newPointedRowIndex) {
                        return;
                    }

                    mousePos = {
                        x: e.pageX,
                        y: e.pageY,
                        rowIndex: newPointedRowIndex
                    };

                    if (dragSelectRange.length === 1 && !e.ctrlKey) {
                        $grid.igGridSelection('clearSelection');
                    }

                    updateSelections();
                });

                // stop dragging
                $(window).one('pointerup', function(e) {
                    mousePos = null;
                    dragSelectRange = [];
                    $(window).unbind('pointermove.NtsGridListDragging');
                    if ($grid.data("selectUpdated") === true) {
                        $grid.triggerHandler('selectionchanged', [true]);
                    }
                    //$grid.triggerHandler('selectionchanged');  
                    clearInterval(timerAutoScroll);
                    $grid.data("selectUpdated", false);
                });
            });

            function updateSelections() {

                // rowIndex is NaN when mouse is outside grid
                if (isNaN(mousePos.rowIndex)) {
                    return;
                }

                // 以前のドラッグ範囲の選択を一旦解除する
                // TODO: probably this code has problem of perfomance when select many rows
                // should process only "differences" instead of "all"
                for (var i = 0, i_len = dragSelectRange.length; i < i_len; i++) {
                    // http://jp.igniteui.com/help/api/2016.2/ui.iggridselection#methods:deselectRow
                    $grid.igGridSelection('deselectRow', dragSelectRange[i]);
                }

                var newDragSelectRange = [];

                if (dragSelectRange[0] <= mousePos.rowIndex) {
                    for (var j = dragSelectRange[0]; j <= mousePos.rowIndex; j++) {
                        // http://jp.igniteui.com/help/api/2016.2/ui.iggridselection#methods:selectRow
                        $grid.igGridSelection('selectRow', j);
                        newDragSelectRange.push(j);
                    }
                } else if (dragSelectRange[0] > mousePos.rowIndex) {
                    for (var j = dragSelectRange[0]; j >= mousePos.rowIndex; j--) {
                        $grid.igGridSelection('selectRow', j);
                        newDragSelectRange.push(j);
                    }
                }

                dragSelectRange = newDragSelectRange;
                $grid.data("selectUpdated", true);
            }
        }

        function setupSelectingEvents($grid: JQuery) {
            $grid.bind('iggridselectioncellselectionchanging', () => {
            });
            $grid.bind('iggridselectionrowselectionchanged', () => {
                $grid.triggerHandler('selectionchanged', [true]);
            });

            //            $grid.on('mouseup', () => {
            //                $grid.triggerHandler('selectionchanged');
            //            });
        }

        function unsetupDragging($grid: JQuery) {

            $grid.unbind('pointerdown');
        }

        function unsetupSelectingEvents($grid: JQuery) {
            $grid.unbind('iggridselectionrowselectionchanged');

            //            $grid.off('mouseup');
        }
        
        $.fn.ntsGridList = function(options: any): any {
            let self = this;
            let $grid = $(self);
            
            if (typeof options === "string") {
                return delegateMethod($grid, options, arguments[1]);
            }
            
            let HEADER_HEIGHT = 27;
            let ROW_HEIGHT = 23;
            let DIFF_NUMBER = 2;
            
            $grid.addClass("nts-gridlist");
            let gridId = $grid.attr('id');
            if (nts.uk.util.isNullOrUndefined(gridId)) {
                throw new Error('the element NtsGridList must have id attribute.');
            }
            
            let optionsValue: string = options.primaryKey !== undefined ? options.primaryKey : options.optionsValue;
            var dataSource = options.dataSource;
            var deleteOptions = options.deleteOptions;
            var observableColumns = _.cloneDeep(options.columns);
            let selectionDisables = options.selectionDisables;
            let showNumbering = options.showNumbering === true ? true : false;
            let columnResize = options.columnResize;
            let enable = options.enable;
            let value = options.value;
            
            let rows = options.rows;
            $grid.data("init", true);
            $grid.data("selectionDisables", selectionDisables);
            $grid.data("initValue", value); 
            
            if (options.multiple) {
                ROW_HEIGHT = 24;
                // Internet Explorer 6-11
                let _document: any = document;
                let isIE = /*@cc_on!@*/false || !!_document.documentMode;
                
                // Edge 20+
                let _window: any = window;
                let isEdge = !isIE && !!_window.StyleMedia; 
                if (isIE || isEdge) {
                    DIFF_NUMBER = -2;    
                }
            }
            
            let features = [];
            features.push({ name: 'Selection', multipleSelection: options.multiple });
            if (options.multiple || showNumbering) { 
                features.push({
                    name: 'RowSelectors',
                    enableCheckBoxes: options.multiple,
                    enableRowNumbering: false,
                    rowSelectorColumnWidth: 25
                });    
            }
            
            if (columnResize) {
                features.push({
                    name: "Resizing"
                });
            }
            
            let tabIndex = $grid.attr("tabindex");
            $grid.data("tabindex", nts.uk.util.isNullOrEmpty(tabIndex) ? "0" : tabIndex);
            $grid.attr("tabindex", "-1");
            let gridFeatures = options.features;
            var iggridColumns = _.map(observableColumns, c => {
                c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                c["dataType"] = 'string';
                let formatter = c["formatter"];
                if (c["controlType"] === "switch") {
                    let switchF = _.find(gridFeatures, function(s) { 
                        return s["name"] === "Switch";
                    });
                    if (!util.isNullOrUndefined(switchF)) {
                        features.push({name: 'Updating', enableAddRow: false, enableDeleteRow: false, editMode: 'none'});
                        let switchOptions = switchF['options'];
                        let switchValue = switchF['optionsValue']; 
                        let switchText = switchF['optionsText'];
                        c["formatter"] = function createButton(val, row) {
                            let result: JQuery = $('<div class="ntsControl"/>');
                            let rVal = nts.uk.util.isNullOrUndefined(formatter) ? val : formatter(val, row);
                            result.attr("data-value", rVal);
                            _.forEach(switchOptions, function(opt) {
                                let value = opt[switchValue];
                                let text = opt[switchText]; 
                                let btn = $('<button class="nts-switch-button" tabindex="-1"/>').text(text);
                                if ($grid.data("enable") === false){
                                    btn.attr("disabled", "disabled");      
                                }
                                btn.attr('data-value', value);
                                if (rVal == value) {
                                    btn.addClass('selected');
                                }
                                btn.appendTo(result);
                            });
                            return result[0].outerHTML;
                        };
                           
                        $grid.on("click", ".nts-switch-button", function(evt, ui) {
                            let $element = $(this);
                            let selectedValue = $element.attr('data-value');
                            let $tr = $element.closest("tr");  
                            $grid.ntsGridListFeature('switch', 'setValue', $tr.attr("data-id"), c["key"], selectedValue);
                        });  
                        
                        ROW_HEIGHT = 30;
                    }       
                } else {
                    let formatter = c.formatter;
                    c.formatter = function(val, row) {
                        if (row) {
                            setTimeout(() => {
                                let id = row[optionsValue];
                                let disables = $grid.data("selectionDisables");
                                if (!disables) return;
                                _.forEach(disables, d => {
                                    if (id === d) {
                                        let $row = $grid.igGrid("rowById", id, false);
                                        if (!$row.hasClass("row-disable")) $row.addClass("row-disable");
                                        return false;
                                    }
                                });
                            }, 0);
                        }
                        return nts.uk.util.isNullOrUndefined(formatter) ? val : formatter(val, row);
                    };
                }
                return c; 
            });
            
            let isDeleteButton = !util.isNullOrUndefined(deleteOptions) && !util.isNullOrUndefined(deleteOptions.deleteField)
                && deleteOptions.visible === true;
            
            let height = options.height;
            if(!nts.uk.util.isNullOrEmpty(rows)){
                if (isDeleteButton){
                    ROW_HEIGHT = 30;        
                }
                height = rows * ROW_HEIGHT + HEADER_HEIGHT - DIFF_NUMBER;   
                
                let colSettings = [];
                _.forEach(iggridColumns, function (c){
                    if(c["hidden"] === undefined || c["hidden"] === false){
                        colSettings.push({ columnKey: c["key"], allowTooltips: true }); 
                        if (nts.uk.util.isNullOrEmpty(c["columnCssClass"])) { 
                            c["columnCssClass"] = "text-limited";             
                        } else {
                            c["columnCssClass"] += " text-limited";
                        }
                    }      
                });
                
                features.push({
                    name: "Tooltips",
                    columnSettings: colSettings,
                    visibility: "overflow",
                    showDelay: 200,
                    hideDelay: 200
                });
                
                $grid.addClass("row-limited");
            }

            $grid.igGrid({
                width: options.width,
                height: height,
                primaryKey: optionsValue,
                columns: iggridColumns,
                virtualization: true,
                virtualizationMode: 'continuous',
                features: features,
                tabIndex: -1
            });
            
            $grid.closest('.ui-iggrid').addClass('nts-gridlist').height(height).attr("tabindex", $grid.data("tabindex"));
            
//            if (options.itemDraggable) {
//                new swap.SwapHandler().setModel(new swap.GridSwapList($grid, optionsValue)).enableDragDrop(options.dataSource);
//            }

            if (isDeleteButton) {
                $grid.ntsGridList("setupDeleteButton", {
                    deleteField: deleteOptions.deleteField,
                    sourceTarget: options.dataSource
                });
            }

            $grid.ntsGridList('setupSelecting');
            
            if (options.multiple) {
                $grid.bind('iggridrowselectorscheckboxstatechanging', (eventObject: JQueryEventObject, data: any) => {
                    if (String($grid.data("enable")) === "false") return false;
                    let disables = $grid.data("selectionDisables");
                    if (disables && !util.isNullOrUndefined(_.find(disables, d => data.rowKey === d))) {
                        return false;
                    }
                    return true;
                });
            }
            
            $grid.bind('iggridselectionrowselectionchanging', (eventObject: JQueryEventObject, ui: any) => {
                if (String($grid.data("enable")) === "false") return false;
                let disables = $grid.data("selectionDisables");
                if (disables && util.isNullOrUndefined(ui.startIndex)
                    && !util.isNullOrUndefined(_.find(disables, d => ui.row.id === d))) {
                    return false;
                }
                
                if (disables && util.isNullOrUndefined(ui.startIndex) 
                    && util.isNullOrUndefined(ui.row.id)) {
                    setTimeout(() => {
                        _.forEach(_.intersection(disables, value), iv => {
                            $grid.igGridSelection("selectRowById", iv);
                        });
                        
                        $grid.trigger("selectionchanged", [true]);
                    }, 0);
                }
                return true;
            });

            let $oselect, $iselect;
            let checkAll = function() {
                if ($oselect && $iselect && $oselect.attr("data-chk") === "off") {
                    $oselect.attr("data-chk", "on");
                    $iselect.removeClass("ui-igcheckbox-normal-off");
                    $iselect.addClass("ui-igcheckbox-normal-on");
                }
            };
            
            $grid.bind('selectionchanged', () => {
                if (options.multiple) {
                    let selected: Array<any> = $grid.ntsGridList('getSelected');
                    
                    let disables = $grid.data("selectionDisables");
                    let disableIds = [];
                    if (disables) {
                        _.forEach(selected, (s, i) => {
                            _.forEach(disables, (d) => {
                                if (d === s.id && util.isNullOrUndefined(_.find(value, iv => iv === d))) {
                                    $grid.igGridSelection("deselectRowById", d);
                                    disableIds.push(i);
                                    return false;
                                }
                            });
                        });
                        
                        disableIds.sort((i1, i2) => i2 - i1).forEach(d => {
                            selected.splice(d, 1);
                        });
                        
                        let valueCount = _.intersection(disables, value).length; 
                        let ds = $grid.igGrid("option", "dataSource");
                        if (selected.length === ds.length - disables.length + valueCount) {
                            checkAll();
                        }
                    }
                    if (!nts.uk.util.isNullOrEmpty(selected)) {
                        let newValue = _.map(selected, s => s.id);
                        newValue = _.union(_.intersection(disables, value), newValue);
                        setValue($grid, newValue);
                    } else {
                        setValue($grid, []);
                    }
                } else {
                    let selected = $grid.ntsGridList('getSelected');
                    if (!nts.uk.util.isNullOrEmpty(selected)) {
                        setValue($grid, [ selected.id ]);
                    } else {
                        setValue($grid, []);
                    }
                }
            });
            
            $grid.on("iggridvirtualrecordsrender", function(evt, ui) {
                let disables = $grid.data("selectionDisables");
                let $header = ui.owner._headerParent;
                if (!disables || disables.length === 0 || !$header) return;
                let data = ui.owner.dataSource._data;
                let selected = $grid.ntsGridList('getSelected');
                let valueCount = _.intersection(disables, value).length;
                let selector = $header.find(".ui-iggrid-rowselector-header span");
                
                if (selector.length > 1) {
                    $oselect = $(selector[0]); 
                    $iselect = $(selector[1]);
                }
                
                if (selected && (data.length - disables.length + valueCount) === selected.length) {
                    checkAll();
                }
            });
            
            $grid.setupSearchScroll("igGrid", true);
            $grid.ntsGridList("setupScrollWhenBinding");  
            
            $grid.on("switchvaluechanged", function(evt, dataX) {
                setTimeout(function() {
                    let source = _.cloneDeep(options.dataSource);
                    _.forEach(source, function(o) {
                        if (o[optionsValue] === dataX.rowKey) {
                            o[dataX.columnKey] = dataX.value;
                            return true;
                        }
                    });
                    
                    setDataSource($grid, source, options); 
                }, 100);
            });
            
            $grid.on("checknewitem", function(evt){
                return false;
            });
            
            setDataSource($grid, options.dataSource, options);
            if (!_.isNil(options.value) && !_.isEmpty(options.value)) {
                setValue($grid, options.value.constructor === Array ? options.value : [ options.value ]);
            }
        };
        
        function setDataSource($grid: JQuery, sources: any, options?: any) {
            if (!sources) return;
            if (!options) {
                options = $grid.igGrid("option");
            }
            let optionsValue: string = options.primaryKey !== undefined ? options.primaryKey : options.optionsValue;
            let gridSource = $grid.igGrid('option', 'dataSource');
            
            if (String($grid.attr("filtered")) === "true") {
                let filteredSource = [];
                _.forEach(gridSource, function(item) {
                    let itemX = _.find(sources, function (s){
                        return s[optionsValue] === item[optionsValue];        
                    });
                    if(!nts.uk.util.isNullOrUndefined(itemX)){ 
                        filteredSource.push(itemX);
                    }     
                });     
                
                if(!_.isEqual(filteredSource, gridSource)){
                    $grid.igGrid('option', 'dataSource', _.cloneDeep(filteredSource));
                    $grid.igGrid("dataBind");    
                }
            } else {
                let currentSources = sources.slice();
                let observableColumns = _.filter(options.columns, function(c) {
                    c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                    return !_.isNil(c["isDateColumn"]) && c["isDateColumn"] === true;
                });
                
                if (!nts.uk.util.isNullOrEmpty(observableColumns)) {
                    _.forEach(currentSources, function(s) {
                        _.forEach(observableColumns, function(c) {
                            let key = c["key"] === undefined ? c["prop"] : c["key"];
                            s[key] = moment(s[key]).format(c["format"]);
                        });
                    });    
                }
                if (!_.isEqual(currentSources, gridSource)) {
                    $grid.igGrid('option', 'dataSource', _.cloneDeep(currentSources));
                    $grid.igGrid("dataBind");
                }
            }
        }
        
        function getDataSource($grid: JQuery) {
            return $grid.igGrid("option", "dataSource");
        }
        
        function setValue($grid: JQuery, value: any) {
            if (!value) return;
            let sources = $grid.igGrid("option", "dataSource");
            let optionsValue = $grid.igGrid("option", "primaryKey");
            let multiple = $grid.igGridSelection('option', 'multipleSelection');
            let currentSelectedItems = $grid.ntsGridList('getSelected');
            let isEqual = _.isEqualWith(currentSelectedItems, value, function(current, newVal) {
                if ((current === undefined && newVal === undefined) || (current !== undefined && current.id === newVal)) {
                    return true;
                }
            });
            
            if (!isEqual) {
                let clickCheckBox = false,
                    isSameSource = true,
                    sortedValue = _.sortBy(value),
                    sortedSource = _.sortBy(sources, [optionsValue]);
                if (sortedValue.length === sortedSource.length) {
                    _.forEach(sortedValue, (v, i) => {
                        if (v !== sortedSource[i][optionsValue]) {
                            isSameSource = false;
                            return false;
                        }
                    });
                } else {
                    isSameSource = false;
                }
                
                if (isSameSource && value.length == sources.length) {
                    if (multiple) {
                        let features = _.find($grid.igGrid("option", "features"), function (f){
                            return f.name === "RowSelectors";     
                        });
                        clickCheckBox = !nts.uk.util.isNullOrUndefined(features.enableCheckBoxes) && features.enableCheckBoxes;
                    }
                }
                
                if (clickCheckBox) {
                    let chk = $grid.closest('.ui-iggrid').find(".ui-iggrid-rowselector-header").find("span[data-role='checkbox']");
                    if (chk.attr("data-chk") === "off") {
                        chk.click();
                    }
                } else {
                    $grid.ntsGridList('setSelected', value.length === 0 ? (!multiple ? undefined : value) : value);    
                }
                
                let initVal = $grid.data("initValue");
                let disables = $grid.data("selectionDisables");
                if (!disables || !initVal || _.intersection(disables, initVal).length === 0) { 
                    _.defer(() => { $grid.trigger("selectChange"); });
                }
            }
        }
        
        function setSelectedValue($grid: JQuery, value: any) {
            let multiple = $grid.igGridSelection('option', 'multipleSelection');
            if (multiple) {
                let initVal = $grid.data("initValue");
                let disables = $grid.data("selectionDisables");
                setValue($grid, _.union(_.intersection(disables, initVal), value));
            } else {
                setValue($grid, value);
            }
        }
    }
}
