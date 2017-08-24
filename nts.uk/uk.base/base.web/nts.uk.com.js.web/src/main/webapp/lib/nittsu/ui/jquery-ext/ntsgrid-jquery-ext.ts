/// <reference path="../../reference.ts"/>

interface JQuery {
    ntsGrid(options: any, ...params: Array<any>): any;
}

module nts.uk.ui.jqueryExtentions {

    export module ntsGrid {
        $.fn.ntsGrid = function(options: any) {
            var self = this;
            
            if (typeof options === "string") {
                functions.ntsAction($(self), options, [].slice.call(arguments).slice(1));
                return;
            }
            if (options.ntsControls === undefined) {
                $(this).igGrid(options);
                return;
            }
            validation.scanValidators($(self), options.columns); 
            // Cell color
            let cellFormatter = new color.CellFormatter($(this), options.ntsFeatures);
            
            $(this).addClass('compact-grid');
            
            let columnControlTypes = {};
            let columnSpecialTypes = {};
            let formatColumn = function(column: any) {
                // Have column group
                if (!util.isNullOrUndefined(column.group)) {
                    let cols = _.map(column.group, formatColumn);
                    column.group = cols;
                    return column;  
                }
                // Special column types
                specialColumn.ifTrue(columnSpecialTypes, column);
                
                // Control types
                if (column.ntsControl === undefined) {
                    columnControlTypes[column.key] = ntsControls.TEXTBOX;
                    return cellFormatter.format(column);
                }
                if (column.ntsControl === ntsControls.LABEL) {
                    ntsControls.drawLabel($(self), column, cellFormatter);
                    columnControlTypes[column.key] = ntsControls.LABEL;
                    return cellFormatter.format(column);
                }
                
                var controlDef = _.find(options.ntsControls, function(ctl: any) {
                    return ctl.name === column.ntsControl;
                });
                if (!util.isNullOrUndefined(controlDef)) columnControlTypes[column.key] = controlDef.controlType;
                else {
                    columnControlTypes[column.key] = ntsControls.TEXTBOX;
                    return cellFormatter.format(column);
                }
    
                var $self = $(self);
                column.formatter = function(value, rowObj) {
                    if (util.isNullOrUndefined(rowObj)) return value;
                    var rowId = rowObj[$self.igGrid("option", "primaryKey")];
                    var update = (val) => {
                        if (!util.isNullOrUndefined($self.data("igGrid"))) {
                            updating.updateCell($self, rowId, column.key, column.dataType !== 'string' ? val : val.toString());
                            if (options.autoCommit === undefined || options.autoCommit === false) {
                                var updatedRow = $self.igGrid("rowById", rowId, false);
                                $self.igGrid("commit");
                                if (updatedRow !== undefined) $self.igGrid("virtualScrollTo", $(updatedRow).data("row-idx"));
                            }
                        }
                    };
                    var deleteRow = () => {
                        if ($self.data("igGrid") !== null) $self.data("igGridUpdating").deleteRow(rowId);
                    };
    
                    var ntsControl = ntsControls.getControl(controlDef.controlType);
                    var cell = $self.igGrid("cellById", rowId, column.key);
                    var isEnable = $(cell).find("." + ntsControl.containerClass()).data("enable");
                    isEnable = isEnable !== undefined ? isEnable : controlDef.enable === undefined ? true : controlDef.enable;
                    var data = {
                        controlDef: controlDef,
                        update: update,
                        deleteRow: deleteRow,
                        initValue: value,
                        enable: isEnable
                    };
                    var controlCls = "nts-grid-control-" + column.key + "-" + rowId;
                    var $container = $("<div/>").append($("<div/>").addClass(controlCls).css("height", ntsControls.HEIGHT_CONTROL));
                    var $_self = $self;
                    setTimeout(function() {
                        var $self = $_self;
                        let rowId = rowObj[$self.igGrid("option", "primaryKey")];
                        var $gridCell = $self.igGrid("cellById", rowId, column.key);
                        if ($($gridCell.children()[0]).children().length === 0)
                            $("." + controlCls).append(ntsControl.draw(data));
                        ntsControl.$containedGrid = $self;
                        
                        // Cell state color
                        let c = {
                            id: rowId,
                            columnKey: column.key,
                            element: $gridCell[0]    
                        };
                        cellFormatter.style($self, c);
                        color.rememberDisabled($self, c);
                    }, 0);
    
                    return $container.html();
                };
                return column;
            }
            var columns = _.map(options.columns, formatColumn);
            
            options.columns = columns;
            updating.addFeature(options);
            options.autoCommit = true;
            // Decorate editor border
            events.onCellClick($(self));
            
            // Copy&Paste
            copyPaste.ifOn($(self), options);
            events.afterRendered(options);
            columnSize.init($(self), options.columns);
            
            // Group column key and its control type 
            $(this).data(internal.CONTROL_TYPES, columnControlTypes);
            // Group column key and its special type
            $(this).data(internal.SPECIAL_COL_TYPES, columnSpecialTypes);
            // Sheet
            sheet.load.setup($(self), options);
            // Common settings
            settings.build($(self), options);
            if (!onDemand.initial($(self), options)) {
                $(this).igGrid(options);
            }
            // Window resize
            $(window).resize(function() {
                if (options.autoFitWindow) {
                    settings.setGridSize($(self));
                }
                columnSize.load($(self));
            });
        };
        
        module feature {
            export let UPDATING = "Updating";
            export let SELECTION = "Selection";
            export let RESIZING = "Resizing";
            export let COLUMN_FIX = "ColumnFixing";
            export let PAGING = "Paging";
            export let COPY_PASTE = "CopyPaste";
            export let CELL_EDIT = "CellEdit";
            export let CELL_COLOR = "CellColor";
            export let CELL_STATE = "CellState";
            export let ROW_STATE = "RowState";
            export let TEXT_COLOR = "TextColor";
            export let HEADER_STYLES = "HeaderStyles";
            export let HIDING = "Hiding";
            export let SHEET = "Sheet";
            export let DEMAND_LOAD = "LoadOnDemand";
            
            export function replaceBy(options: any, featureName: string, newFeature: any) {
                let replaceId: number;
                _.forEach(options.features, function(feature: any, id: number) {
                    if (feature.name === featureName) {
                        replaceId = id;
                        return false;
                    }
                });
                options.features.splice(replaceId, 1, newFeature);
            }
            
            export function isEnable(features: any, name: string) {
                return _.find(features, function(feature: any) {
                    return feature.name === name;
                }) !== undefined;
            }
            
            export function find(features: any, name: string) {
                return _.find(features, function(feature: any) {
                    return feature.name === name;
                });
            }
        }
        
        module updating {
            
            export function addFeature(options: any) {
                let updateFeature = createUpdateOptions(options); 
                if (!feature.isEnable(options.features, feature.UPDATING)) {
                    options.features.push(updateFeature);
                } else {
                    feature.replaceBy(options, feature.UPDATING, createUpdateOptions(options));
                }
            }
            
            function createUpdateOptions(options: any) {
                let updateFeature: any = { name: feature.UPDATING, enableAddRow: false, enableDeleteRow: false, editMode: 'none' };
                if (feature.isEnable(options.ntsFeatures, feature.CELL_EDIT)) {
                    updateFeature.editMode = "cell";
                    updateFeature.editCellStarting = startEditCell;
                    updateFeature.editCellEnding = beforeFinishEditCell;
                }
                return updateFeature;
            }
            
            export function containsNtsControl($target: any) {
                let td = $target;
                if (!$target.prev().is("td")) td = $target.closest("td");
                return td.find("div[class*='nts-grid-control']").length > 0;
            } 
            
            function startEditCell(evt: any, ui: any) {
                if (containsNtsControl($(evt.currentTarget)) || utils.isEnterKey(evt) || utils.isTabKey(evt)) {
                    let selectedCell = selection.getSelectedCell($(evt.target));
                    if (util.isNullOrUndefined(selectedCell) || !utils.selectable($(evt.target))) return;
                    $(evt.target).igGridSelection("selectCell", selectedCell.rowIndex, selectedCell.index,
                                    utils.isFixedColumnCell(selectedCell, utils.getVisibleColumnsMap($(evt.target))));
                    return false;
                } else if (utils.disabled($(evt.currentTarget))) return false;
                return true; 
            }
            
            /**
             * Validate
             */
            export function onEditCell(evt: any, cell: any) {
                let $grid = fixedColumns.realGridOf($(evt.currentTarget));
                if (!utils.isEditMode($grid)) return;
                let validators: any =  $grid.data(validation.VALIDATORS);
                let fieldValidator = validators[cell.columnKey];
                if (util.isNullOrUndefined(fieldValidator)) return;
                
                let cellValue = $(cell.element).find("input:first").val();
                let result = fieldValidator.probe(cellValue);
                let $cellContainer = $(cell.element);
                errors.clear($grid, cell);
                if (!result.isValid) {
                    errors.set($grid, cell, result.errorMessage);
                }
            }
            
            export function triggerCellUpdate(evt: any, cell: any) {
                var grid = evt.currentTarget;
                let $targetGrid = fixedColumns.realGridOf($(grid));
                
                if (utils.isEditMode($targetGrid) || utils.disabled($(cell.element))) return;
                if (utils.isAlphaNumeric(evt)) {
                    startEdit(evt, cell);
                }
                if (utils.isDeleteKey(evt)) {
                    $targetGrid.one(events.Handler.GRID_EDIT_CELL_STARTED, function(evt: any, ui: any) {
                        $(ui.editor).find("input").val("");
                    });
                    startEdit(evt, cell);
                }
            }
            
            function startEdit(evt: any, cell: any) {
                let $targetGrid = fixedColumns.realGridOf($(evt.currentTarget));
                if (!utils.updatable($targetGrid)) return;
                utils.startEdit($targetGrid, cell);
                // Keep text contents if any, otherwise set input value
//                if ($(cell.element).text().trim() !== "") evt.preventDefault();
                if (!utils.isDeleteKey(evt)) {
                    setTimeout(function() { 
                        let cellValue;
                        let $editor = $targetGrid.igGridUpdating("editorForCell", $(cell.element));
                        if (!util.isNullOrUndefined($editor.data("igTextEditor"))) {
                            let newText = $editor.igTextEditor("value");
                            newText = newText.substr(newText.length - 1);
                            $editor.igTextEditor("value", newText.trim());
                            cellValue = newText;
                        } else if (!util.isNullOrUndefined($editor.data("igNumericEditor"))) {
                            let newValue = $editor.igNumericEditor("value");
                            let numericStr = String(newValue);
                            numericStr = numericStr.substr(numericStr.length - 1);
                            $editor.igNumericEditor("value", parseInt(numericStr));
                            setTimeout(function() {
                                let length = String($editor.igNumericEditor("value")).length;
                                $editor.igNumericEditor("select", length, length); 
                            }, 100);
                            cellValue = numericStr;
                        }
                        
                        // Validate
                        let validators: any =  $targetGrid.data(validation.VALIDATORS);
                        let fieldValidator = validators[cell.columnKey];
                        if (util.isNullOrUndefined(fieldValidator)) return;
                        
                        let result = fieldValidator.probe(cellValue);
                        let $cellContainer = $(cell.element);
                        errors.clear($targetGrid, cell);
                        if (!result.isValid) {
                            errors.set($targetGrid, cell, result.errorMessage);
                        }
                    }, 100);
                }
                evt.stopImmediatePropagation();
            }
            
            /**
             * Interrupt manipulations (e.g. cell navigation) on grid if errors occurred (setting needed).
             */
            function beforeFinishEditCell(evt: any, ui: any) {
                let $grid = $(evt.target);
                let selectedCell = selection.getSelectedCell($grid);
                let settings: any = $grid.data(internal.SETTINGS); 
                if (settings.preventEditInError
                    && utils.isEditMode($grid) && errors.any(selectedCell)) {
                    return false;
                }
                
                if (utils.isEditMode($grid) && (utils.isTabKey(evt) || utils.isEnterKey(evt))) {
                    let gridUpdate: any = $grid.data("igGridUpdating");
                    let origValues = gridUpdate._originalValues;
                    if (!util.isNullOrUndefined(origValues)) {
                        _.forEach(Object.keys(origValues), function(colKey: any, idx: number) {
                            if (idx === 0) {
                                // Skip default update
                                gridUpdate._originalValues[colKey] = ui.value; 
                                return false;
                            }
                        });
                        _.defer(function() {
                            updating.updateCell($grid, selectedCell.id, selectedCell.columnKey, ui.value);
                        });
                    }
                }
                
                // Remove border color of editor
                let $editorContainer = $(selectedCell.element).find(errors.EDITOR_SELECTOR);
                if ($editorContainer.length > 0) $editorContainer.css(errors.NO_ERROR_STL);
                
                specialColumn.tryDo($grid, selectedCell, ui.value); 
                return true;
            }
            
            /**
             * Update row and re-render all controls.
             * @Obsolete
             */
            export function _updateRow($grid: JQuery, rowId: any, visibleColumnsMap: any, updatedRowData: any) {
                if (util.isNullOrUndefined(updatedRowData) || Object.keys(updatedRowData).length === 0) return;
                $grid.igGridUpdating("updateRow", 
                            utils.parseIntIfNumber(rowId, $grid, visibleColumnsMap), updatedRowData);
            }
            
            /**
             * Update cell.
             */
            export function updateCell($grid: JQuery, rowId: any, columnKey: any, cellValue: any, allColumnsMap?: any) {
                let grid: any = $grid.data("igGrid");
                if (!utils.updatable($grid)) return;
                let gridUpdate: any = $grid.data("igGridUpdating");
                let autoCommit = grid.options.autoCommit;
                let columnsMap: any = allColumnsMap || utils.getColumnsMap($grid);
                let rId = utils.parseIntIfNumber(rowId, $grid, columnsMap);
                grid.dataSource.setCellValue(rId, columnKey, cellValue, autoCommit);
                if (!utils.isNtsControl($grid, columnKey)) renderCell($grid, rId, columnKey);
                gridUpdate._notifyCellUpdated(rId);
            }
            
            /**
             * Update row.
             */
            export function updateRow($grid: JQuery, rowId: any, updatedRowData: any, allColumnsMap?: any, forceRender?: boolean) {
                let grid: any = $grid.data("igGrid");
                if (!utils.updatable($grid)) return;
                let gridUpdate: any = $grid.data("igGridUpdating");
                let autoCommit = grid.options.autoCommit;
                let columnsMap: any = allColumnsMap || utils.getColumnsMap($grid);
                let rId = utils.parseIntIfNumber(rowId, $grid, columnsMap);
                let origData = gridUpdate._getLatestValues(rId); 
                grid.dataSource.updateRow(rId, $.extend({}, origData, updatedRowData), autoCommit);
                _.forEach(Object.keys(updatedRowData), function(key: any) {
                    if (utils.isNtsControl($grid, key) && !forceRender) return;
                    let $vCell = renderCell($grid, rId, key, origData);
                    
                    // Validate
                    let validators: any =  $grid.data(validation.VALIDATORS);
                    let fieldValidator = validators[key];
                    if (util.isNullOrUndefined(fieldValidator)) return;
                    let cellValue = updatedRowData[key];
                    let result = fieldValidator.probe(String(cellValue));
                    let cell = { 
                        id: rowId,
                        columnKey: key,
                        element: $vCell
                    };
                    errors.clear($grid, cell);
                    if (!result.isValid) {
                        errors.set($grid, cell, result.errorMessage);
                    }
                });
                gridUpdate._notifyRowUpdated(rId, null);
            }
            
            export function renderCell($grid: JQuery, rowId: any, columnKey: any, latestValues?: any) {
                let grid: any = $grid.data("igGrid");
                if (!utils.updatable($grid)) return;
                let gridUpdate: any = $grid.data("igGridUpdating");
                let rowData = gridUpdate._getLatestValues(rowId);
                let column: any =  _.find(utils.getVisibleColumns($grid), function(col: any) {
                    return col.key === columnKey;
                });
                let $cell = $grid.igGrid("cellById", rowId, columnKey);
                $cell.html(String(grid._renderCell(rowData[columnKey], column, rowData)));
                return $cell;
            }
        }
        
        module selection {
            
            export function addFeature(options: any) {
                let selection = { name: feature.SELECTION, mode: "cell", multipleSelection: true, wrapAround: false, cellSelectionChanged: selectCellChange };
                if (!feature.isEnable(options.features, feature.SELECTION)) {
                    options.features.push(selection);
                } else {
                    feature.replaceBy(options, feature.SELECTION, selection);
                }
            }
            
            export function selectBefore($grid: JQuery, enterDirection?: string) {
                var enter = enterDirection || "right";
                if (enter === "right") selectPrev($grid);
                else selectAbove($grid);
            }
            
            export function selectPrev($grid: JQuery) {
                var selectedCell: any = getSelectedCell($grid);
                if (util.isNullOrUndefined(selectedCell)) return;
                clearSelection($grid);
                let visibleColumnsMap = utils.getVisibleColumnsMap($grid);
                let isFixed = utils.isFixedColumnCell(selectedCell, visibleColumnsMap);
                if (selectedCell.index > 0) {
                    selectCell($grid, selectedCell.rowIndex, selectedCell.index - 1, isFixed);
                } else if (selectedCell.index === 0) {
                    let columnsGroup = utils.columnsGroupOfCell(selectedCell, visibleColumnsMap);
                    if (util.isNullOrUndefined(columnsGroup) || columnsGroup.length === 0) return;
                    let fixedColumns = utils.getFixedColumns(visibleColumnsMap);
                    let unfixedColumns = utils.getUnfixedColumns(visibleColumnsMap);
                    
                    if ((isFixed || !utils.fixable($grid)) && selectedCell.rowIndex > 0) {
                        selectCell($grid, selectedCell.rowIndex - 1, unfixedColumns.length - 1);
                    } else if (utils.fixable($grid) && !isFixed) {
                        selectCell($grid, selectedCell.rowIndex, fixedColumns.length - 1, true);
                    }
                }
            }
            
            export function selectAbove($grid: JQuery) {
                var selectedCell: any = getSelectedCell($grid);
                if (util.isNullOrUndefined(selectedCell)) return;
                clearSelection($grid);
                let isFixed = utils.isFixedColumnCell(selectedCell, utils.getVisibleColumnsMap($grid));
                let dataSource = $grid.igGrid("option", "dataSource");
                let sourceSize = dataSource.length;
                if (selectedCell.rowIndex > 0) {
                    selectCell($grid, selectedCell.rowIndex - 1, selectedCell.index, isFixed);
                } else if (selectedCell.rowIndex === 0) {
                    let visibleColumnsMap = utils.getVisibleColumnsMap($grid);
                    let columnsGroup = utils.columnsGroupOfCell(selectedCell, visibleColumnsMap);
                    if (util.isNullOrUndefined(columnsGroup) || columnsGroup.length === 0) return;
                    $grid.igGrid("virtualScrollTo", sourceSize);
                    setTimeout(function() {
                        if (selectedCell.index > 0) {
                            selectCell($grid, sourceSize - 1, selectedCell.index - 1, columnsGroup[0].fixed);
                        } else if (selectedCell.index === 0) {
                            if (columnsGroup[0].fixed) {
                                selectCell($grid, sourceSize - 1, visibleColumnsMap["undefined"].length - 1);
                                return;
                            } 
                            let noOfColTypes = Object.keys(visibleColumnsMap).length;
                            if (noOfColTypes === 2) {
                                selectCell($grid, sourceSize - 1, visibleColumnsMap["true"].length - 1, true);
                            } else {
                                selectCell($grid, sourceSize - 1, visibleColumnsMap["undefined"].length - 1);
                            }
                        }
                    }, 1);
                }
            }
            
            export function selectFollow($grid: JQuery, enterDirection?: string) {
                var enter = enterDirection || "right";
                if (enter === "right") selectNext($grid);
                else selectBelow($grid);
            }
            
            function selectNext($grid: JQuery) {
                var selectedCell: any = getSelectedCell($grid);
                if (util.isNullOrUndefined(selectedCell)) return;
                clearSelection($grid);
                let visibleColumnsMap = utils.getVisibleColumnsMap($grid);
                let dataSource = $grid.igGrid("option", "dataSource");
                
                let columnsGroup = utils.columnsGroupOfCell(selectedCell, visibleColumnsMap);
                if (util.isNullOrUndefined(columnsGroup) || columnsGroup.length === 0) return;
                if (selectedCell.index < columnsGroup.length - 1) { 
                    selectCell($grid, selectedCell.rowIndex, selectedCell.index + 1, columnsGroup[0].fixed); 
                } else if (selectedCell.index === columnsGroup.length - 1) {
                    if (columnsGroup[0].fixed) {
                        selectCell($grid, selectedCell.rowIndex, 0);
                    } else if (selectedCell.rowIndex < dataSource.length - 1) {
                        selectCell($grid, selectedCell.rowIndex + 1, 0, true);
                    }
                } 
            }
            
            function selectBelow($grid: JQuery) {
                var selectedCell: any = getSelectedCell($grid);
                if (util.isNullOrUndefined(selectedCell)) return;
                clearSelection($grid);
                let isFixed = utils.isFixedColumnCell(selectedCell, utils.getVisibleColumnsMap($grid));
                let dataSource = $grid.igGrid("option", "dataSource");
                let sourceSize = dataSource.length;
                if (selectedCell.rowIndex < sourceSize - 1) {
                    selectCell($grid, selectedCell.rowIndex + 1, selectedCell.index, isFixed);
                } else if (selectedCell.rowIndex === sourceSize - 1) {
                    let visibleColumnsMap = utils.getVisibleColumnsMap($grid);
                    let columnsGroup = utils.columnsGroupOfCell(selectedCell, visibleColumnsMap);
                    if (util.isNullOrUndefined(columnsGroup) || columnsGroup.length === 0) return;
                    $grid.igGrid("virtualScrollTo", "0px");
                    setTimeout(function() {
                        if (selectedCell.index < columnsGroup.length - 1) {
                            selectCell($grid, 0, selectedCell.index + 1, columnsGroup[0].fixed);
                        } else if (selectedCell.index === columnsGroup.length - 1) {
                            if (columnsGroup[0].fixed) {
                                selectCell($grid, 0, 0);
                            } else {
                                selectCell($grid, 0, 0, Object.keys(visibleColumnsMap).length === 2 ? true : undefined);
                            }
                        }
                    }, 1);
                }
            }
            
            export function getSelectedCell($grid: JQuery) {
                if (!utils.selectable($grid)) { 
                    let $targetGrid = fixedColumns.realGridOf($grid); 
                    if (!util.isNullOrUndefined($targetGrid)) {
                        return $targetGrid.igGridSelection("selectedCells")[0] || $targetGrid.data(internal.SELECTED_CELL); 
                    }
                }
                return $grid.igGridSelection("selectedCells")[0] || $grid.data(internal.SELECTED_CELL);
            }
            
            export function getSelectedCells($grid: JQuery) {
                return utils.selectable($grid) ? $grid.igGridSelection("selectedCells") : undefined;
            }
            
            export function selectCell($grid: JQuery, rowIndex: number, columnIndex: number, isFixed?: boolean) {
                if (!utils.selectable($grid)) return;
                $grid.igGridSelection("selectCell", rowIndex, columnIndex, utils.fixable($grid) ? isFixed : undefined);
                
                // Fire cell selection change
                let ui: any = { owner: $grid.data("igGridSelection"),
                                selectedCells: $grid.igGridSelection("selectedCells") };
                let selectedCells = $grid.igGridSelection("selectedCells");
                if (selectedCells.length > 0) ui.cell = selectedCells[0];
                selectCellChange({ target: $grid[0] }, ui);
                
                // TODO: Focus nts common controls if exists.
                let selectedCell: any = getSelectedCell($grid);
                let ntsCombo = $(selectedCell.element).find(".nts-combo-container"); 
                if (ntsCombo.length > 0) {
                    ntsCombo.find("input").select();
                }
            }
            
            export function selectCellById($grid: JQuery, rowId: any, columnKey: string) {
                return;
            }
            
            function selectCellChange(evt: any, ui: any) {
                if (util.isNullOrUndefined(ui.cell)) return;
                $(evt.target).data(internal.SELECTED_CELL, ui.cell);
            }
            
            export function onCellNavigate(evt: any, enterDirection?: string) {
                var grid = evt.currentTarget;
                let $targetGrid = fixedColumns.realGridOf($(grid));
                
                if (utils.isTabKey(evt)) {
                    if (utils.isEditMode($targetGrid))
                        $targetGrid.igGridUpdating("endEdit");
                    
                    if (evt.shiftKey) {
                        selection.selectPrev($targetGrid);
                    } else {
                        selection.selectFollow($targetGrid);
                    }
                    evt.preventDefault();
                    return;
                }
                 
                if (utils.isEnterKey(evt)) {
                    if (evt.shiftKey) {
                        selection.selectBefore($targetGrid, enterDirection);
                    } else {
                        selection.selectFollow($targetGrid, enterDirection);
                    }
                    evt.stopImmediatePropagation();
                    return;
                }
            }
            
            function clearSelection($grid) {
                if (utils.selectable($grid)) {
                    $grid.igGridSelection("clearSelection");
                    return;
                }
                let $targetGrid = fixedColumns.realGridOf($grid);
                if (!util.isNullOrUndefined($targetGrid) && utils.selectable($targetGrid))
                    $targetGrid.igGridSelection("clearSelection");
            }
            
            export class Direction {
                to: string;
                bind(evt: any) {
                    onCellNavigate(evt, this.to);
                }
            }
        }
        
        module columnSize {
            
            export function init($grid: JQuery, columns: any) {
                if (initValueExists($grid)) return;
                let columnWidths: {[ key: string ]: number } = {};
                _.forEach(columns, function(col: any, index: number) {
                    columnWidths[col.key] = parseInt(col.width);
                });
                saveAll($grid, columnWidths);
            }
            
            export function load($grid: JQuery) {
                let storeKey = getStorageKey($grid);
                uk.localStorage.getItem(storeKey).ifPresent((columns) => {
                    let widthColumns: any = JSON.parse(columns);
                    setWidths($grid, widthColumns);
                    return null;
                });
            }
            
            export function save($grid: JQuery, columnKey: string, columnWidth: number) {
                let storeKey = getStorageKey($grid);
                let columnsWidth = uk.localStorage.getItem(storeKey);
                let widths = {};
                if (columnsWidth.isPresent()) {
                    widths = JSON.parse(columnsWidth.get());
                    widths[columnKey] = columnWidth;
                } else {
                    widths[columnKey] = columnWidth;
                }
                uk.localStorage.setItemAsJson(storeKey, widths);
            }
            
            function saveAll($grid: JQuery, widths: {[ key: string ]: number }) {
                let storeKey = getStorageKey($grid);
                let columnWidths = uk.localStorage.getItem(storeKey);
                if (!columnWidths.isPresent()) {
                    uk.localStorage.setItemAsJson(storeKey, widths);
                }
            }
            
            function initValueExists($grid: JQuery) {
                let storeKey = getStorageKey($grid);
                let columnWidths = uk.localStorage.getItem(storeKey);
                return columnWidths.isPresent();
            }
            
            function getStorageKey($grid: JQuery) {
                return request.location.current.rawUrl + "/" + $grid.attr("id");
            }
            
            export function loadOne($grid: JQuery, columnKey: string) {
                let storeKey = getStorageKey($grid);
                uk.localStorage.getItem(storeKey).ifPresent((columns) => {
                    let widthColumns: any = JSON.parse(columns);
                    setWidth($grid, columnKey, widthColumns[columnKey]);
                    return null;
                });
            }
            export function loadFixedColumns($grid: JQuery) {
                let storeKey = getStorageKey($grid);
                uk.localStorage.getItem(storeKey).ifPresent((columns) => {
                    let fixedColumns = utils.getVisibleFixedColumns($grid);
                    if (util.isNullOrUndefined(fixedColumns) || fixedColumns.length === 0) return;
                    let widthColumns: any = JSON.parse(columns);
                    _.forEach(fixedColumns, function(fixedCol) {
                        setWidth($grid, fixedCol.key, widthColumns[fixedCol.key]);
                    });
                    return null; 
                });
            }
            
            function setWidth($grid: JQuery, columnKey: string, width: number, noCheck?: boolean) {
                if (noCheck !== true && util.isNullOrUndefined($grid.data("igGridResizing"))) return;
                try {
                    $grid.igGridResizing("resize", columnKey, width);
                } catch (e) {}
            }
            
            function setWidths($grid: JQuery, columns: {[ key: string ]: number}) {
                if (util.isNullOrUndefined($grid.data("igGridResizing"))
                    || util.isNullOrUndefined(columns)) return;
                let columnKeys = Object.keys(columns);
                _.forEach(columnKeys, function(key: any, index: number) {
                    setWidth($grid, key, columns[key], true);
                });
            }
        }

        module functions {
            export let UPDATE_ROW: string = "updateRow";
            export let ENABLE_CONTROL: string = "enableNtsControlAt";
            export let ENABLE_ALL_CONTROLS: string = "enableNtsControls";
            export let DISABLE_CONTROL: string = "disableNtsControlAt";
            export let DISABLE_ALL_CONTROLS: string = "disableNtsControls";
            export let DIRECT_ENTER: string = "directEnter";
            
            export function ntsAction($grid: JQuery, method: string, params: Array<any>) {
                switch (method) {
                    case UPDATE_ROW:
                        var autoCommit = $grid.data("igGrid") !== null && $grid.igGrid("option", "autoCommit") ? true : false;
                        updateRow($grid, params[0], params[1], autoCommit);
                        break;
                    case ENABLE_CONTROL:
                        enableNtsControlAt($grid, params[0], params[1], params[2]);
                        break;
                    case ENABLE_ALL_CONTROLS:
                        enableNtsControls($grid, params[0], params[1]);
                        break;
                    case DISABLE_CONTROL:
                        disableNtsControlAt($grid, params[0], params[1], params[2]);
                        break;
                    case DISABLE_ALL_CONTROLS:
                        disableNtsControls($grid, params[0], params[1]);
                        break;
                    case DIRECT_ENTER:
                        var direction: selection.Direction = $grid.data(internal.ENTER_DIRECT);
                        direction.to = params[0];
                        if (utils.fixable($grid)) {
                            let fixedTable = fixedColumns.getFixedTable($grid)
                            if (!util.isNullOrUndefined(fixedTable)) {
                                fixedTable.data(internal.ENTER_DIRECT).to = params[0];
                            }
                        }
                        break;
                }
            }
    
            function updateRow($grid: JQuery, rowId: any, object: any, autoCommit: boolean) {
                updating.updateRow($grid, rowId, object, undefined, true);
                if (!autoCommit) {
                    var updatedRow = $grid.igGrid("rowById", rowId, false);
                    $grid.igGrid("commit");
                    if (updatedRow !== undefined) $grid.igGrid("virtualScrollTo", $(updatedRow).data("row-idx"));
                }
            }

            function disableNtsControls($grid: JQuery, columnKey: any, controlType: string) {
                var ds = $grid.igGrid("option", "dataSource");
                var primaryKey = $grid.igGrid("option", "primaryKey");
                for (let i = 0; i < ds.length; i++) {
                    let id = ds[i][primaryKey];
                    disableNtsControlAt($grid, id, columnKey, controlType);
                    color.pushDisable($grid, { id: id, columnKey: columnKey });
                }
            }
            
            function enableNtsControls($grid: JQuery, columnKey: any, controlType: string) {
                var ds = $grid.igGrid("option", "dataSource");
                var primaryKey = $grid.igGrid("option", "primaryKey");
                for (let i = 0; i < ds.length; i++) {
                    let id = ds[i][primaryKey];
                    enableNtsControlAt($grid, id, columnKey, controlType);
                    color.popDisable($grid, { id: id, columnKey: columnKey }); 
                }
            }
            
            function disableNtsControlAt($grid: JQuery, rowId: any, columnKey: any, controlType: string) {
                var cellContainer = $grid.igGrid("cellById", rowId, columnKey);
                var control = ntsControls.getControl(controlType);
                if (util.isNullOrUndefined(control)) return;
                control.disable($(cellContainer));
                if (!$(cellContainer).hasClass(color.Disable)) $(cellContainer).addClass(color.Disable);
                color.pushDisable($grid, { id: rowId, columnKey: columnKey });
            }
    
            function enableNtsControlAt($grid: JQuery, rowId: any, columnKey: any, controlType: string) {
                var cellContainer = $grid.igGrid("cellById", rowId, columnKey);
                var control = ntsControls.getControl(controlType);
                if (util.isNullOrUndefined(control)) return;
                control.enable($(cellContainer));
                $(cellContainer).removeClass(color.Disable);
                color.popDisable($grid, { id: rowId, columnKey: columnKey });
            }
        }
        
        module ntsControls {
            export let LABEL: string = 'Label';
            export let LINK_LABEL: string = 'LinkLabel';
            export let CHECKBOX: string = 'CheckBox';
            export let SWITCH_BUTTONS: string = 'SwitchButtons';
            export let COMBOBOX: string = 'ComboBox'; 
            export let BUTTON: string = 'Button';
            export let DELETE_BUTTON = 'DeleteButton';
            export let TEXTBOX = 'TextBox';
            export let HEIGHT_CONTROL = "27px";
            
            export let COMBO_CLASS = "nts-combo-container";

            export function getControl(name: string): NtsControlBase {
                switch (name) {
                    case CHECKBOX:
                        return new CheckBox();
                    case SWITCH_BUTTONS:
                        return new SwitchButtons();
                    case COMBOBOX:
                        return new ComboBox();
                    case BUTTON:
                        return new Button();
                    case DELETE_BUTTON:
                        return new DeleteButton();
                    case LINK_LABEL:
                        return new LinkLabel();
                }
            }
            
            export function drawLabel($grid: JQuery, column: any, cellFormatter: color.CellFormatter): void {
                column.formatter = function(value, rowObj) {
                    if (util.isNullOrUndefined(rowObj)) return value;
                    var $self = this;
                    var rowId = rowObj[$grid.igGrid("option", "primaryKey")];
                    var controlCls = "nts-grid-control-" + column.key + "-" + rowId;
                    var $container = $("<div/>").append($("<div/>").addClass(controlCls).css("height", HEIGHT_CONTROL));
                    setTimeout(function() {
                        let rId = rowObj[$grid.igGrid("option", "primaryKey")];
                        var $gridCell = $grid.igGrid("cellById", rId, column.key);
                        if ($($gridCell.children()[0]).children().length === 0) {
                            $("." + controlCls).append(new Label().draw({ text: value }));
                            let cellElement = { 
                                id: rId,
                                columnKey: column.key,
                                element: $gridCell[0]
                            };
                            cellFormatter.style($grid, cellElement);
                            cellFormatter.setTextColor($grid, cellElement);
                        }
                    }, 0);

                    return $container.html();
                };
            }
    
            abstract class NtsControlBase {
                $containedGrid: JQuery;
                readOnly: boolean = false;
                abstract containerClass(): string;
                abstract draw(data: any): JQuery;
                abstract enable($container: JQuery): void;
                abstract disable($container: JQuery): void;
            }
    
            class CheckBox extends NtsControlBase {
                containerClass(): string {
                    return "nts-checkbox-container";
                }
    
                draw(data: any): JQuery {
                    var checkBoxText: string;
                    var setChecked = data.update;
                    var initValue = data.initValue;
                    var $wrapper = $("<div/>").addClass(this.containerClass()).data("enable", data.enable);
                    $wrapper.addClass("ntsControl").on("click", (e) => {
                        if ($wrapper.data("readonly") === true) e.preventDefault();
                    });
    
                    var text = data.controlDef.options[data.controlDef.optionsText];
                    if (text) {
                        checkBoxText = text;
                    } else {
                        checkBoxText = $wrapper.text();
                        $wrapper.text('');
                    }
                    var $checkBoxLabel = $("<label class='ntsCheckBox'></label>");
                    var $checkBox = $('<input type="checkbox">').on("change", function() {
                        setChecked($(this).is(":checked"));
                    }).appendTo($checkBoxLabel);
                    var $box = $("<span class='box'></span>").appendTo($checkBoxLabel);
                    if (checkBoxText && checkBoxText.length > 0)
                        var label = $("<span class='label'></span>").text(checkBoxText).appendTo($checkBoxLabel);
                    $checkBoxLabel.appendTo($wrapper);
    
                    var checked = initValue !== undefined ? initValue : true;
                    $wrapper.data("readonly", this.readOnly);
                    var $checkBox = $wrapper.find("input[type='checkbox']");
    
                    if (checked === true) $checkBox.attr("checked", "checked");
                    else $checkBox.removeAttr("checked");
                    if (data.enable === true) $checkBox.removeAttr("disabled");
                    else $checkBox.attr("disabled", "disabled");
                    return $wrapper;
                }
    
                disable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", false);
                    $wrapper.find("input[type='checkbox']").attr("disabled", "disabled");
                }
    
                enable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", true);
                    $wrapper.find("input[type='checkbox']").removeAttr("disabled");
                }
            }
    
            class SwitchButtons extends NtsControlBase {
                containerClass(): string {
                    return "nts-switch-container";
                }
    
                draw(data: any): JQuery {
                    var selectedCssClass = 'selected';
                    var options = data.controlDef.options;
                    var optionsValue = data.controlDef.optionsValue;
                    var optionsText = data.controlDef.optionsText;
                    var selectedValue = data.initValue;
                    var container = $("<div/>").addClass(this.containerClass()).data("enable", data.enable);
    
                    _.forEach(options, function(opt) {
                        var value = opt[optionsValue];
                        var text = opt[optionsText];
    
                        var btn = $('<button>').text(text).css("height", "26px")
                            .addClass('nts-switch-button')
                            .attr('data-swbtn', value)
                            .on('click', function() {
                                var selectedValue = $(this).data('swbtn');
                                $('button', container).removeClass(selectedCssClass);
                                $(this).addClass(selectedCssClass);
                                data.update(selectedValue);
                            });
                        if (value === selectedValue) {
                            btn.addClass(selectedCssClass);
                        }
                        container.append(btn);
                    });
                    (data.enable === true) ? $('button', container).prop("disabled", false)
                        : $('button', container).prop("disabled", true);
                    return container;
                }
    
                enable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", true);
                    $('button', $wrapper).prop("disabled", false);
                }
    
                disable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", false);
                    $('button', $wrapper).prop("disabled", true);
                }
    
            }
    
            class ComboBox extends NtsControlBase {
                containerClass(): string {
                    return "nts-combo-container";
                }
    
                draw(data: any): JQuery {
                    var self = this;
                    // Default values.
                    var distanceColumns = '     ';
                    // Character used fill to the columns.
                    var fillCharacter = ' ';
                    var maxWidthCharacter = 15;
                    var container = $("<div/>").addClass(this.containerClass()).data("enable", data.enable);
                    var columns: Array<any> = data.controlDef.columns;
    
                    // Set attribute for multi column.
                    var itemTemplate: string = undefined;
                    var haveColumn = columns && columns.length > 0;
                    if (haveColumn) {
                        itemTemplate = '<div class="nts-combo-item">';
                        _.forEach(columns, function(item, i) {
                            // Set item template.
                            itemTemplate += '<div class="nts-column nts-combo-column-' + i + '">${' + item.prop + '}</div>';
                        });
                        itemTemplate += '</div>';
                    }
    
                    // Display full code name
                    if (data.controlDef.displayMode === "codeName") {
                        data.controlDef.options = data.controlDef.options.map((option) => {
                            var newOptionText: string = '';
                            if (haveColumn) {
                                _.forEach(columns, function(item, i) {
                                    var prop: string = option[item.prop];
                                    var length: number = item.length;
        
                                    if (i === columns.length - 1) {
                                        newOptionText += prop;
                                    } else {
                                        newOptionText += text.padRight(prop, fillCharacter, length) + distanceColumns;
                                    }
                                });
        
                            } else {
                                newOptionText = option[data.controlDef.optionsText];
                            }
                            option['nts-combo-label'] = newOptionText;
                            return option;
                        });
                    }
    
                    var comboMode: string = data.controlDef.editable ? 'editable' : 'dropdown';
                    container.igCombo({
                        dataSource: data.controlDef.options,
                        valueKey: data.controlDef.optionsValue,
                        textKey: data.controlDef.displayMode === 'codeName' 
                                    ? 'nts-combo-label' : data.controlDef.optionsText,
                        mode: comboMode,
                        disabled: !data.enable,
                        placeHolder: '',
                        enableClearButton: false,
                        initialSelectedItems: [
                            { value: data.initValue }
                        ],
                        itemTemplate: itemTemplate,
                        selectionChanging: function(evt: any, ui: any) {
                            var __self = self; 
                            let $gridControl = $(evt.target).closest("div[class*=nts-grid-control]");
                            if (util.isNullOrUndefined($gridControl)) return;
                            let cls = $gridControl.attr("class");
                            let classNameParts = cls.split("-");
                            let rowId = classNameParts.pop();
                            let columnKey = classNameParts.pop();
                            let targetCell: any = __self.$containedGrid.igGrid("cellById", rowId, columnKey);
                            let $comboContainer = $(targetCell).find("." + __self.containerClass());
                            // Clear error if any
                            let comboInput = $($comboContainer.find("input")[1]);
                            comboInput.ntsError("clear");
                            nts.uk.ui.errors.removeByElement(comboInput);
                            comboInput.parent().removeClass("error");
                        },
                        selectionChanged: function(evt: any, ui: any) {
                            var _self = self;
                            if (ui.items.length > 0) {
                                let selectedValue = ui.items[0].data[data.controlDef.optionsValue];
                                data.update(selectedValue);
                                
                                setTimeout(function() {
                                    var __self = _self; 
                                    let $gridControl = $(evt.target).closest("div[class*=nts-grid-control]");
                                    if (util.isNullOrUndefined($gridControl)) return;
                                    let cls = $gridControl.attr("class");
                                    let classNameParts = cls.split("-");
                                    let rowId = classNameParts.pop();
                                    let columnKey = classNameParts.pop();
                                    let targetCell: any = __self.$containedGrid.igGrid("cellById", rowId, columnKey);
                                    let $comboContainer = $(targetCell).find("." + __self.containerClass());
                                    // Save selected item
                                    $comboContainer.data(internal.COMBO_SELECTED, selectedValue);
                                }, 0);
                            }
                        }
                    });
                    // Save init value
                    container.data(internal.COMBO_SELECTED, data.initValue);
    
                    // Set width for multi columns.
                    if (haveColumn) {
                        var totalWidth = 0;
                        var $dropDownOptions = $(container.igCombo("dropDown"));
                        _.forEach(columns, function(item, i) {
                            var charLength: number = item.length;
                            var width = charLength * maxWidthCharacter + 10;
                            $dropDownOptions.find('.nts-combo-column-' + i).width(width);
                            if (i !== columns.length - 1) {
                                $dropDownOptions.find('.nts-combo-column-' + i).css({ 'float': 'left' });
                            }
                            totalWidth += width + 10;
                        });
                        $dropDownOptions.find('.nts-combo-item').css({ 'min-width': totalWidth });
                        container.css({ 'min-width': totalWidth });
                    }
    
                    container.data("columns", columns);
                    container.data("comboMode", comboMode);
                    return container;
                }
    
                enable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass());
                    $wrapper.data("enable", true);
                    $wrapper.igCombo("option", "disabled", false);
                }
                disable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass());
                    $wrapper.data("enable", false);
                    $wrapper.igCombo("option", "disabled", true);
                }
            }
    
            class Button extends NtsControlBase {
                containerClass(): string {
                    return "nts-button-container";
                }
    
                draw(data: any): JQuery {
                    var $container = $("<div/>").addClass(this.containerClass());
                    var $button = $("<button/>").addClass("ntsButton").css("height", "25px").appendTo($container).text(data.controlDef.text || data.initValue)
                        .data("enable", data.enable).on("click", data.controlDef.click);
                    $button.prop("disabled", !data.enable);
                    return $container;
                }
    
                enable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", true);
                    $wrapper.find(".ntsButton").prop("disabled", false);
                }
                disable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", false);
                    $wrapper.find(".ntsButton").prop("disabled", true);
                }
            }
    
            class DeleteButton extends Button {
                draw(data: any): JQuery {
                    var btnContainer = super.draw(data);
                    var btn = btnContainer.find("button");
                    btn.off("click", data.controlDef.click);
                    btn.on("click", data.deleteRow);
                    return btn;
                }
            }
            
            class Label extends NtsControlBase {
                containerClass(): string {
                    return "nts-label-container";
                }
                
                draw(data: any): JQuery {
                    var $container = $("<div/>").addClass(this.containerClass());
                    $("<label/>").addClass("ntsLabel").css("padding-left", "0px").text(data.text).appendTo($container);
                    return $container;
                }
                
                enable($container: JQuery): void {
                    return;
                }
                disable($container: JQuery): void {
                    return;
                }
            }
            
            class LinkLabel extends NtsControlBase {
                containerClass(): string {
                    return "nts-link-container";
                }
                
                draw(data: any): JQuery {
                    return $('<div/>').addClass(this.containerClass()).append($("<a/>")
                                        .addClass("link-button").css({ backgroundColor: "inherit", color: "deepskyblue" })
                                        .text(data.initValue).on("click", data.controlDef.click)).data("click", data.controlDef.click);
                }
                
                enable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", true);
                    $wrapper.find("a").css("color", "deepskyblue").on("click", $wrapper.data("click"));
                }
                disable($container: JQuery): void {
                    var $wrapper = $container.find("." + this.containerClass()).data("enable", false);
                    $wrapper.find("a").css("color", "#AAA").off("click");
                }
            }
            
            export module comboBox {
                
                export function getCopiedValue(cell: any, copiedText: string) {
                    let copiedValue;
                    let $comboBox = utils.comboBoxOfCell(cell);
                    if ($comboBox.length > 0) {
                        let items = $comboBox.igCombo("items");                   
                        let textKey = $comboBox.igCombo("option", "textKey");
                        let valueKey = $comboBox.igCombo("option", "valueKey");
                        _.forEach(items, function(item: any) {
                            if (item.data[textKey] === copiedText.trim()) {
                                copiedValue = item.data[valueKey];
                                return false;
                            }
                        });
                    }
                    return copiedValue;
                }
            }
        }
        
        module specialColumn {
            export let CODE: string = "code";
            export let COMBO_CODE: string = "comboCode";
            
            export function ifTrue(columnSpecialTypes: any, column: any) {
                if (util.isNullOrUndefined(column.ntsType)) return;
                if (column.ntsType === CODE) {
                    columnSpecialTypes[column.key] = { type: column.ntsType,
                                                       onChange: column.onChange };
                } else if (column.ntsType === COMBO_CODE) {
                    columnSpecialTypes[column.key] = { type: column.ntsType,
                                                       onChange: identity };
                }
            }
            
            export function tryDo($grid: JQuery, cell: any, pastedText: any, visibleColumnsMap?: any) {
                let columnTypes = $grid.data(internal.SPECIAL_COL_TYPES);
                let specialColumn;
                let columnKey = cell.columnKey;
                for (let key in columnTypes) {
                    if (key === columnKey) {
                        specialColumn = columnTypes[key];
                        break;
                    }
                }
                
                if (util.isNullOrUndefined(specialColumn)) return;
                visibleColumnsMap = !util.isNullOrUndefined(visibleColumnsMap) ? visibleColumnsMap : utils.getVisibleColumnsMap($grid);
                let isFixedColumn = utils.isFixedColumn(columnKey, visibleColumnsMap);
                let nextColumn = utils.nextColumnByKey(visibleColumnsMap, columnKey, isFixedColumn);
                if (util.isNullOrUndefined(nextColumn) || nextColumn.index === 0) return;
                
                specialColumn.onChange(pastedText).done(function(res: any) {
                    let updatedRow = {};
                    let $gridRow = utils.rowAt(cell);
                    if (specialColumn.type === COMBO_CODE) {
                        let $nextCell = $grid.igGrid("cellById", $gridRow.data("id"), nextColumn.options.key);
                        let $comboContainer = $nextCell.find("." + ntsControls.COMBO_CLASS);
                        let ds = $comboContainer.igCombo("option", "dataSource");
                        let vKey = $comboContainer.igCombo("option", "valueKey");
                        if (util.isNullOrUndefined(ds)) return;
                        let valueExists;
                        _.forEach(ds._data, function(item: any) {
                            if (item[vKey].toString() === String(res.toString().trim())) {
                                valueExists = true;
                                return false;
                            } 
                        });
                        if (!valueExists) return;
                    }
                    if (nextColumn.options.dataType === "number") {
                        updatedRow[nextColumn.options.key] = parseInt(res.toString().trim());
                    } else {
                        updatedRow[nextColumn.options.key] = String(res.toString().trim());
                    } 
                    updating.updateRow($grid, $gridRow.data("id"), updatedRow, undefined, true);
                }).fail(function(res: any) {
                    
                });
                return true;
            }
            
            function identity(value) {
                let dfd = $.Deferred();
                dfd.resolve(value);
                return dfd.promise();  
            }
        }
        
        module copyPaste {
            
            enum CopyMode {
                SINGLE,
                MULTIPLE
            }
            
            enum PasteMode {
                NEW,
                UPDATE
            }
            
            export class Processor {
                $grid: JQuery;
                options: any;
                pasteInMode: PasteMode = PasteMode.UPDATE;
                copyMode: CopyMode;
                visibleColumnsMap: any;
            
                constructor(options?: any) {
                    this.options = options;
                }
                static addFeatures(options: any) {
                    selection.addFeature(options);
                    return new Processor(options);
                }
                
                /**
                 * $grid to handle copy paste
                 * $target to bind events to
                 */
                chainEvents($grid: JQuery, $target?: JQuery) {
                    var self = this;
                    self.$grid = $grid;
                    let target = !util.isNullOrUndefined($target) ? $target : $grid; 
                    events.Handler.pull(target).focusInWith(self).ctrlCxpWith(self);
                }
                
                copyHandler(cut?: boolean) {
                    let selectedCells: Array<any> = selection.getSelectedCells(this.$grid);
                    let copiedData;
                    let checker = cut ? utils.isCuttableControls : utils.isCopiableControls;
                    if (selectedCells.length === 1) {
                        this.copyMode = CopyMode.SINGLE;
                        if (!checker(this.$grid, selectedCells[0].columnKey)) return;
                        if (utils.isComboBox(this.$grid, selectedCells[0].columnKey)) {
                            let $comboBox = utils.comboBoxOfCell(selectedCells[0]);
                            if ($comboBox.length > 0) {
                                copiedData = $comboBox.igCombo("text");
                            }
                        } else {
                            copiedData = selectedCells[0].element.text();
                        }
                    } else {
                        this.copyMode = CopyMode.MULTIPLE;
                        copiedData = this.converseStructure(selectedCells, cut);
                    }
                    $("#copyHelper").val(copiedData).select();
                    document.execCommand("copy");
                    return selectedCells;
                }
                
                converseStructure(cells: Array<any>, cut: boolean): string {
                    let self = this;
                    let maxRow = 0;
                    let minRow = 0;
                    let maxColumn = 0;
                    let minColumn = 0;
                    let structure = [];
                    let structData: string = "";
                    let checker = cut ? utils.isCuttableControls : utils.isCopiableControls;
                    _.forEach(cells, function(cell: any, index: number) {
                        let rowIndex = cell.rowIndex;
                        let columnIndex = utils.getDisplayColumnIndex(self.$grid, cell);
                        if (index === 0) {
                            minRow = maxRow = rowIndex;
                            minColumn = maxColumn = columnIndex;
                        }
                        if (rowIndex < minRow) minRow = rowIndex;
                        if (rowIndex > maxRow) maxRow = rowIndex;
                        if (columnIndex < minColumn) minColumn = columnIndex;
                        if (columnIndex > maxColumn) maxColumn = columnIndex;
                        if (util.isNullOrUndefined(structure[rowIndex])) {
                            structure[rowIndex] = {};
                        }
                        if (!checker(self.$grid, cell.columnKey)) return;
                        if (utils.isComboBox(self.$grid, cell.columnKey)) {
                            let $comboBox = utils.comboBoxOfCell(cell);
                            if ($comboBox.length > 0) {
                                structure[rowIndex][columnIndex] = $comboBox.igCombo("text");
                            }
                        } else {
                            structure[rowIndex][columnIndex] = cell.element.text();
                        }
                    });
                    
                    for (var i = minRow; i <= maxRow; i++) {
                        for (var j = minColumn; j <= maxColumn; j++) {
                            if (util.isNullOrUndefined(structure[i]) || util.isNullOrUndefined(structure[i][j])) {
                                structData += "null";
                            } else {
                                structData += structure[i][j];
                            }
                            
                            if (j === maxColumn) structData += "\n";
                            else structData += "\t";
                        }
                    }
                    return structData;
                }
                
                cutHandler() {
                    var self = this;
                    var selectedCells = this.copyHandler(true);
                    var cellsGroup = _.groupBy(selectedCells, "rowIndex");
                    _.forEach(Object.keys(cellsGroup), function(rowIdx: any) {
                        var $row = utils.rowAt(cellsGroup[rowIdx][0]);
                        var updatedRowData = {};
                        _.forEach(cellsGroup[rowIdx], function(cell: any) {
                            if (!utils.isCuttableControls(self.$grid, cell.columnKey)) return;
                            updatedRowData[cell.columnKey] = "";
                        });
                        updating.updateRow(self.$grid, $row.data("id"), updatedRowData);
                    });
                }
                
                
                pasteHandler(evt: any) {
                    if (this.copyMode === CopyMode.SINGLE) {
                        this.pasteSingleCellHandler(evt);
                    } else {
                        this.pasteRangeHandler(evt);
                    }
                }
                
                pasteSingleCellHandler(evt: any) {
                    let self = this;
                    let cbData = this.getClipboardContent(evt);
                    let selectedCells = selection.getSelectedCells(this.$grid);
                    let visibleColumnsMap = utils.getVisibleColumnsMap(self.$grid);
                    _.forEach(selectedCells, function(cell: any, index: number) {
                        if (!utils.isPastableControls(self.$grid, cell.columnKey)
                            || utils.isDisabled($(cell.element))) return;
                        let rowIndex = cell.rowIndex;
                        let columnIndex = cell.index;
                        let $gridRow = utils.rowAt(cell);
                        let updatedRow = {};
                        let columnsGroup = utils.columnsGroupOfCell(cell, visibleColumnsMap);
                        let columnKey = columnsGroup[columnIndex].key;
                        
                        // When pasted cell is combox
                        if (utils.isComboBox(self.$grid, cell.columnKey)) {
                            let copiedValue = ntsControls.comboBox.getCopiedValue(cell, cbData);
                            if (!util.isNullOrUndefined(copiedValue)) {
                                updatedRow[columnKey] = columnsGroup[columnIndex].dataType === "number" 
                                    ? parseInt(copiedValue) : copiedValue;
                            } else {
                                // TODO: Handle if texts in item list not map pasted text.
                                let $combo = cell.element.find(".nts-combo-container")
                                let $comboInput = $($combo.find("input")[1]);
                                $comboInput.ntsError("set", "Pasted text not valid");
                                $combo.igCombo("text", "");
                                return;
                            }
                        } else {
                            specialColumn.tryDo(self.$grid, cell, cbData, visibleColumnsMap);
                            if (columnsGroup[columnIndex].dataType === "number") {
                                updatedRow[columnKey] = parseInt(cbData);
                            } else {
                                updatedRow[columnKey] = cbData;
                            }
                        }
                        updating.updateRow(self.$grid, $gridRow.data("id"), updatedRow);
                    });
                }
                
                pasteRangeHandler(evt: any) {
                    var cbData = this.getClipboardContent(evt);
                    if (utils.isEditMode(this.$grid)) {
                        cbData = this.processInEditMode(cbData);
                        this.updateInEditMode(cbData);
                    } else {
                        cbData = this.process(cbData);
                        this.pasteInMode === PasteMode.UPDATE ? this.updateWith(cbData) : this.addNew(cbData);
                    }
                }
                
                getClipboardContent(evt: any) {
                    if (window.clipboardData) {
                        window.event.returnValue = false;
                        return window.clipboardData.getData("text");
                    } else {
                        return evt.originalEvent.clipboardData.getData("text/plain");
                    }
                }
                
                private processInEditMode(data: string) {
                    if (util.isNullOrUndefined(data)) return;
                    return data.split("\n")[0];
                }
                
                private updateInEditMode(data: string) {
                    let selectedCell = selection.getSelectedCell(this.$grid);
                    let rowIndex = selectedCell.rowIndex;
                    let columnIndex = selectedCell.index;
                    let visibleColumnsMap = utils.getVisibleColumnsMap(this.$grid);
                    let updateRow = {};
                    let columnsGroup = utils.columnsGroupOfCell(selectedCell, visibleColumnsMap);
                    let columnKey = columnsGroup[columnIndex].key;
                    updateRow[columnKey] = data;
                    let $gridRow = utils.rowAt(selectedCell);
                    updating.updateRow(this.$grid, $gridRow.data("id"), updateRow);
                }
                
                private process(data: string) {
                    var dataRows = _.map(data.split("\n"), function(row) {
                        return row.split("\t");
                    });
                    
                    var rowsCount = dataRows.length;
                    if ((dataRows[rowsCount - 1].length === 1 && dataRows[rowsCount - 1][0] === "")
                        || dataRows.length === 1 && dataRows[0].length === 1 
                            && (dataRows[0][0] === "" || dataRows[0][0] === "\r")) {
                        dataRows.pop();
                    }
                    return dataRows;
                }
                
                private updateWith(data: any) {
                    var self = this;
                    if (!utils.selectable(this.$grid) || !utils.updatable(this.$grid)) return;
                    var selectedCell: any = selection.getSelectedCell(this.$grid);
                    if (selectedCell === undefined) return;
                    selectedCell.element.focus();
                    
                    var visibleColumnsMap = utils.getVisibleColumnsMap(self.$grid);
                    var visibleColumns = utils.visibleColumnsFromMap(visibleColumnsMap);
                    var columnIndex = selectedCell.index;
                    var rowIndex = selectedCell.rowIndex;
//                    if (!this.pasteable(columnIndex + data[0].length - visibleColumns.length)) return;
                    
                    let targetCol = _.find(visibleColumns, function(column: any) {
                        return column.key === selectedCell.columnKey;
                    });
                    if (util.isNullOrUndefined(targetCol)) return;
                    
                    _.forEach(data, function(row: any, idx: number) {
                        var $gridRow;
                        if (idx === 0) $gridRow = utils.rowAt(selectedCell);
                        else $gridRow = utils.nextNRow(selectedCell, idx);
                        if (util.isNullOrUndefined($gridRow)) return;
                        var rowData = {};
                        let targetIndex = columnIndex;
                        let targetCell = selectedCell;
                        let targetColumn = targetCol;
                        
                        // Errors
                        let comboErrors = [];
                        for (var i = 0; i < row.length; i++) {
                            let nextColumn;
                            let columnKey = targetColumn.key;
                            let cellElement = self.$grid.igGrid("cellById", $gridRow.data("id"), columnKey);
                            if ((!util.isNullOrUndefined(row[i]) && row[i].trim() === "null")
                                || !utils.isPastableControls(self.$grid, columnKey)
                                || utils.isDisabled(cellElement)) {
                                // Go to next column
                                nextColumn = utils.nextColumn(visibleColumnsMap, targetIndex, targetColumn.fixed);
                                targetColumn = nextColumn.options;
                                targetIndex = nextColumn.index;
                                continue;
                            }
                            let columnsGroup = utils.columnsGroupOfColumn(targetColumn, visibleColumnsMap);
                            if (targetIndex > columnsGroup.length - 1) break;
                            
                            if (utils.isComboBox(self.$grid, columnKey)) {
                                let cellContent = row[i].trim();
                                let copiedValue = ntsControls.comboBox.getCopiedValue({ element: cellElement[0] }, cellContent);
                                if (!util.isNullOrUndefined(copiedValue)) {
                                    rowData[columnKey] = targetColumn.dataType === "number" ? parseInt(copiedValue) : copiedValue;
                                } else {
                                    // TODO: Handle if copied text not match any item in combobox list
                                    comboErrors.push({ cell: cellElement, content: cellContent });
                                    
                                    // Go to next column
                                    nextColumn = utils.nextColumn(visibleColumnsMap, targetIndex, targetColumn.fixed);
                                    targetColumn = nextColumn.options;
                                    targetIndex = nextColumn.index;
                                    continue;
                                }
                            } else {
                                let cell: any = {};
                                cell.columnKey = columnKey;
                                cell.element = cellElement;
                                cell.id = $gridRow.data("id");
                                cell.index = targetIndex;
                                cell.row = $gridRow;
                                cell.rowIndex = $gridRow.data("rowIdx");
                                specialColumn.tryDo(self.$grid, cell, row[i].trim(), visibleColumnsMap);
                                
                                if (targetColumn.dataType === "number") {
                                    rowData[columnKey] = parseInt(row[i]);
                                } else {
                                    rowData[columnKey] = row[i];
                                }
                            }
                            // Go to next column
                            nextColumn = utils.nextColumn(visibleColumnsMap, targetIndex, targetColumn.fixed);
                            targetColumn = nextColumn.options;
                            targetIndex = nextColumn.index;
                        }
                        updating.updateRow(self.$grid, $gridRow.data("id"), rowData);    
                        _.forEach(comboErrors, function(combo: any) {
                            setTimeout(function() {
                                let $container = combo.cell.find(".nts-combo-container");
                                let $comboInput = $($container.find("input")[1]);
                                $comboInput.ntsError("set", "Pasted text not valid");
                                $container.igCombo("text", combo.content);
                            }, 0);
                        });
                    });
                }
                
                private addNew(data: any) {
                    var self = this;
//                    var visibleColumns = this.getVisibleColumns();
                    var visibleColumns = null;
                    if (!this.pasteable(data[0].length - visibleColumns.length)) return;
                    
                    _.forEach(data, function(row: any, idx: number) {
                        var rowData = {};
                        for (var i = 0; i < visibleColumns.length; i++) {
                            var columnKey = visibleColumns[i].key;
                            if (visibleColumns[i].dataType === "number") {
                                rowData[columnKey] = parseInt(row[i]);
                            } else {
                                rowData[columnKey] = row[i];
                            }
                        }
                        self.$grid.igGridUpdating("addRow", rowData);
                    });
                }
                
                private pasteable(excessColumns) {
                    if (excessColumns > 0) {
                        nts.uk.ui.dialog.alert("Copied table structure doesn't match.");
                        return false;
                    }
                    return true; 
                } 
            }
            
            export function ifOn($grid: JQuery, options: any) {
                if (options.ntsFeatures === undefined) return;
                _.forEach(options.ntsFeatures, function(f: any) {
                    if (f.name === feature.COPY_PASTE) {
                        Processor.addFeatures(options).chainEvents($grid);
                        return false;
                    }    
                });
            }   
        }
        
        module events {
            export class Handler {
                static KEY_DOWN: string = "keydown";
                static KEY_UP: string = "keyup";
                static FOCUS_IN: string = "focusin";
                static CLICK: string = "click";
                static MOUSE_DOWN: string = "mousedown";
                static SCROLL: string = "scroll";
                static GRID_EDIT_CELL_STARTED: string = "iggridupdatingeditcellstarted";
                static COLUMN_RESIZING: string = "iggridresizingcolumnresizing";
                static CELL_CLICK: string = "iggridcellclick";
                static PAGE_INDEX_CHANGE: string = "iggridpagingpageindexchanging";
                static PAGE_SIZE_CHANGE: string = "iggridpagingpagesizechanging";
                $grid: JQuery;
                options: any;
                preventEditInError: boolean;
                
                constructor($grid: JQuery, options: any) {
                    this.$grid = $grid;
                    this.options = options;
                    this.preventEditInError = !util.isNullOrUndefined(options) ? options.preventEditInError : undefined;
                }
                
                static pull($grid: JQuery, options?: any): Handler {
                    return new Handler($grid, options);
                }
                
                turnOn($mainGrid?: JQuery) {
                    if (feature.isEnable(this.options.ntsFeatures, feature.CELL_EDIT)) {
                        this.filter($mainGrid).onCellUpdate().onCellUpdateKeyUp();
                    }
                    if (!util.isNullOrUndefined(this.options.enter) 
                        && (utils.selectable(this.$grid) || utils.selectable($mainGrid))) {
                        this.onDirectEnter();
                    }
                    if (utils.selectable(this.$grid) || utils.selectable($mainGrid)) {
                        this.onSpacePress();
                    }
                    if (feature.isEnable(this.options.features, feature.RESIZING)) {
                        this.onColumnResizing();
                    }
                }
                
                /**
                 * Handle enter direction.
                 */
                onDirectEnter() {
                    // Enter direction
                    var direction: selection.Direction = new selection.Direction();
                    this.$grid.on(Handler.KEY_DOWN, $.proxy(direction.bind, direction));
                    this.$grid.data(internal.ENTER_DIRECT, direction);
                    return this;
                }
                
                /**
                 * Handle cell edit.
                 */
                onCellUpdate() {
                    var self = this;
                    this.$grid.on(Handler.KEY_DOWN, function(evt: any) {
                        if (evt.ctrlKey) return;
                        let selectedCell: any = selection.getSelectedCell(self.$grid);
                        updating.triggerCellUpdate(evt, selectedCell);
                    });
                    return this;
                }
                
                /**
                 * Handle validation.
                 */
                onCellUpdateKeyUp() {
                    var self = this;
                    this.$grid.on(Handler.KEY_UP, function(evt: any) {
                        if (evt.ctrlKey) return;
                        let selectedCell: any = selection.getSelectedCell(self.$grid);
                        updating.onEditCell(evt, selectedCell);
                    });
                    return this;
                }
                
                /**
                 * Handle press space key on combobox.
                 */
                onSpacePress() {
                    var self = this;
                    self.$grid.on(Handler.KEY_DOWN, function(evt: any) {
                        if (!utils.isSpaceKey(evt)) return;
                        var selectedCell: any = selection.getSelectedCell(self.$grid);
                        if (util.isNullOrUndefined(selectedCell)) return;
                        var checkbox = $(selectedCell.element).find(".nts-checkbox-container");
                        if (checkbox.length > 0) {
                            checkbox.find("input[type='checkbox']").click();
                        }
                    });
                    return this;
                }
                
                /**
                 * Support copy paste.
                 */
                focusInWith(processor: copyPaste.Processor) {
                    this.$grid.on(Handler.FOCUS_IN, function(evt: any) {
                        if ($("#pasteHelper").length > 0 && $("#copyHelper").length > 0) return;
                        var pasteArea = $("<textarea id='pasteHelper'/>").css({ "opacity": 0, "overflow": "hidden" })
                                            .on("paste", $.proxy(processor.pasteHandler, processor));
                        var copyArea = $("<textarea id='copyHelper'/>").css({ "opacity": 0, "overflow": "hidden" });
                        $("<div/>").css({ "position": "fixed", "top": -10000, "left": -10000 })
                                    .appendTo($(document.body)).append(pasteArea).append(copyArea);
                    });
                    return this;
                }
                
                /**
                 * Copy, cut, paste events.
                 */
                ctrlCxpWith(processor: copyPaste.Processor) {
                    this.$grid.on(Handler.KEY_DOWN, function(evt: any) {
                        if (evt.ctrlKey && utils.isPasteKey(evt)) {
                            $("#pasteHelper").focus();
                        } else if (evt.ctrlKey && utils.isCopyKey(evt)) {
                            processor.copyHandler();
                        } else if (evt.ctrlKey && utils.isCutKey(evt)) {
                            processor.cutHandler();   
                        }
                    });
                    return this;
                }
                
                /**
                 * Prevent forwarding events in particular cases.
                 */
                filter($target?: JQuery) {
                    var self = this;
                    let $mainGrid = !util.isNullOrUndefined($target) ? $target : self.$grid; 
                    
                    self.$grid.on(Handler.KEY_DOWN, function(evt: any) {
                        if (utils.isAlphaNumeric(evt) || utils.isDeleteKey(evt)) {
                            let cell = selection.getSelectedCell($mainGrid);
                            if (cell === undefined || updating.containsNtsControl($(evt.target)))  
                                evt.stopImmediatePropagation();
                            return;
                        }
                        
                        if (utils.isTabKey(evt) && utils.isErrorStatus($mainGrid)) {
                            evt.preventDefault();
                            evt.stopImmediatePropagation();
                        }
                    });
                    
                    if (this.preventEditInError) {
                        self.$grid[0].addEventListener(Handler.MOUSE_DOWN, function(evt: any) {
                            if (utils.isNotErrorCell($mainGrid, evt)) {
                                evt.preventDefault();
                                evt.stopImmediatePropagation();
                            }
                        }, true);
                        self.$grid[0].addEventListener(Handler.CLICK, function(evt: any) {
                            if (utils.isNotErrorCell($mainGrid, evt)) {
                                evt.preventDefault();
                                evt.stopImmediatePropagation();
                            }
                        }, true);
                    }
                    
                    return this;
                }
                
                onColumnResizing() {
                    var self = this;
                    // Not fired on fixed table but main grid (table)
                    self.$grid.on(Handler.COLUMN_RESIZING, function(evt: any, args: any) {
                        columnSize.save(self.$grid, args.columnKey, args.desiredWidth);
                    });
                    return this;
                }
            }
            
            export function afterRendered(options: any) {
                options.rendered = function(evt: any, ui: any) {
                    let $grid = $(evt.target);
                    events.Handler.pull($grid, options).turnOn();   
                    
                    // Bind events for fixed table part
                    let $fixedTbl = fixedColumns.getFixedTable($grid);
                    if ($fixedTbl.length > 0) {
                        if (feature.isEnable(options.ntsFeatures, feature.COPY_PASTE))
                            new copyPaste.Processor().chainEvents($grid, $fixedTbl);
                        events.Handler.pull($fixedTbl, options).turnOn($grid);
                    }
                    // Sheet scroll
                    let sheetConfig: any = $grid.data(internal.SHEETS);
                    sheet.onScroll($grid);
                    if (!util.isNullOrUndefined(sheetConfig) && !util.isNullOrUndefined(sheetConfig.currentPosition)) {
//                        let displayPos = sheetConfig.displayScrollTop;
                        $grid.igGrid("virtualScrollTo", sheetConfig.currentPosition);
//                        utils.getScrollContainer($grid).scrollTop(parseInt(sheetConfig.currentPosition));
//                        _.defer(function() {
//                            utils.getDisplayContainer($grid).scrollTop(displayPos);
//                        });
                    }
                    
                    // Set selected cell if any
                    let selectedCell = $grid.data(internal.SELECTED_CELL);
                    if (!util.isNullOrUndefined(selectedCell)) {
                        let fixedColumns = utils.getVisibleFixedColumns($grid);
                        if (_.find(fixedColumns, function(col) {
                                return col.key === selectedCell.columnKey;
                            }) !== undefined) {
                            setTimeout(function() {
                                selection.selectCell($grid, selectedCell.rowIndex, selectedCell.index, true);
                            }, 1);
                        }
                    }
                    // Mark errors
                    errors.mark($grid);
                    color.styleHeaders($grid, options);
                    if (options.autoFitWindow) {
                        // Resize grid
                        settings.setGridSize($grid);
                    }
                    // Load columns size
                    columnSize.load($grid);
                };
            }
            
            export function onCellClick($grid: JQuery) {
                $grid.on(Handler.CELL_CLICK, function(evt: any, ui: any) {
                    if (!utils.isEditMode($grid) && errors.any({ element: ui.cellElement })) {
                        _.defer(function() {
                            let $editor = $(ui.cellElement).find(errors.EDITOR_SELECTOR);
                            if ($editor.length === 0) return;
                            $editor.css(errors.ERROR_STL);
                        });
                    }
                });
            }
        }
        
        module validation {
            export let VALIDATORS: string = "ntsValidators"; 
            export class ColumnFieldValidator {
                name: string;
                primitiveValue: string;
                options: any;
                constructor(name: string, primitiveValue: string, options: any) {
                    this.name = name;
                    this.primitiveValue = primitiveValue;
                    this.options = options;
                }
                
                probe(value: string) {
                    let constraint = nts.uk.ui.validation.getConstraint(this.primitiveValue);
                    switch (constraint.valueType) {
                        case "String":
                            return new nts.uk.ui.validation.StringValidator(this.name, this.primitiveValue, this.options)
                                    .validate(value, this.options);
                        case "Integer":
                        case "Decimal":
                        case "HalfInt":
                            return new nts.uk.ui.validation.NumberValidator(this.name, this.primitiveValue, this.options)
                                    .validate(value);
                        case "Time":
                            this.options.mode = "time";
                            return new nts.uk.ui.validation.TimeValidator(this.name, this.primitiveValue, this.options)
                                    .validate(value);
                        case "Clock":
                            this.options.outputFormat = "time";
                            return new nts.uk.ui.validation.TimeValidator(this.name, this.primitiveValue, this.options)
                                    .validate(value);
                    }
                }
            }
            
            function getValidators(columnsDef: any) : { [columnKey: string]: ColumnFieldValidator } {
                var validators: any = {};
                _.forEach(columnsDef, function(def: any) {
                    if (def.constraint === undefined) return;
                    validators[def.key] = new ColumnFieldValidator(def.headerText, def.constraint.primitiveValue, def.constraint); 
                });
                return validators;
            }
            
            export function scanValidators($grid: JQuery, columnsDef: any) {
                $grid.data(VALIDATORS, getValidators(utils.analyzeColumns(columnsDef)));
            }
        }
        
        module errors {
            export let HAS_ERROR = "hasError";
            export let ERROR_STL = { "border-color": "#ff6666" };
            export let NO_ERROR_STL = { "border-color": "" };
            export let EDITOR_SELECTOR = "div.ui-igedit-container";
            
            export function mark($grid: JQuery) {
                let errorsLog = $grid.data(internal.ERRORS_LOG);
                if (util.isNullOrUndefined(errorsLog)) return;
                let sheets: any = $grid.data(internal.SHEETS);
                let sheetErrors = errorsLog[sheets.currentSheet];
                if (util.isNullOrUndefined(sheetErrors)) return;
                _.forEach(sheetErrors, function(cell: any) {
                    let $cell = $grid.igGrid("cellById", cell.id, cell.columnKey);
                    decorate($cell);
                });
            }
            
            function decorate($cell: any) {
                $cell.addClass(HAS_ERROR);
                $cell.css(ERROR_STL);
                let $editor = $cell.find(EDITOR_SELECTOR);
                if ($editor.length > 0) $editor.css(ERROR_STL); 
            }
            
            export function set($grid: JQuery, cell: any, message: string) {
                if (any(cell)) return;
                let $cell = $(cell.element);
                decorate($cell);
                let errorDetails = createErrorInfos($grid, cell, message);
                ui.errors.addCell(errorDetails);
                addErrorInSheet($grid, cell);
            }
            
            function createErrorInfos($grid: JQuery, cell: any, message: string): any {
                let record: any = $grid.igGrid("findRecordByKey", cell.id);
                let error: any = {
                    grid: $grid,
                    rowId: cell.id,
                    columnKey: cell.columnKey,
                    message: message
                };
                // Error column headers
                let headers = ko.toJS(ui.errors.errorsViewModel().option.headers);
                _.forEach(headers, function(header: any) {
                    if (util.isNullOrUndefined(record[header.name]) 
                        || !util.isNullOrUndefined(error[header.name])) return;
                    error[header.name] = record[header.name];
                });
                return error;
            } 
            
            export function clear($grid: JQuery, cell: any) {
                if (!any(cell)) return;
                let $cell = $(cell.element);
                $cell.removeClass(HAS_ERROR);
                $cell.css(NO_ERROR_STL);
                let $editor = $cell.find(EDITOR_SELECTOR);
                if ($editor.length > 0) $editor.css(NO_ERROR_STL);
                ui.errors.removeCell($grid, cell.id, cell.columnKey);
                removeErrorFromSheet($grid, cell);
            }
            
            export function any(cell: any) {
                return $(cell.element).hasClass(HAS_ERROR);
            }
            
            function addErrorInSheet($grid: JQuery, cell: any) {
                let errorsLog = $grid.data(internal.ERRORS_LOG) || {};
                let sheets: any = $grid.data(internal.SHEETS);
                if (util.isNullOrUndefined(errorsLog[sheets.currentSheet])) {
                    errorsLog[sheets.currentSheet] = [];
                } 
                errorsLog[sheets.currentSheet].push(cell);
                $grid.data(internal.ERRORS_LOG, errorsLog);
            }
            
            function removeErrorFromSheet($grid: JQuery, cell: any) {
                let errorsLog = $grid.data(internal.ERRORS_LOG);
                if (util.isNullOrUndefined(errorsLog)) return;
                let sheets: any = $grid.data(internal.SHEETS);
                let sheetErrors = errorsLog[sheets.currentSheet];
                if (util.isNullOrUndefined(sheetErrors)) return;
                let cellErrorIdx;
                _.forEach(sheetErrors, function(errorCell: any, i: number) {
                    if (cellEquals(errorCell, cell)) {
                        cellErrorIdx = i;
                        return false;
                    }
                });
                if (!util.isNullOrUndefined(cellErrorIdx)) {
                    errorsLog[sheets.currentSheet].splice(cellErrorIdx, 1);
                }
            }
            
            export function markIfError($grid: JQuery, cell: any) {
                let errorsLog = $grid.data(internal.ERRORS_LOG);
                if (util.isNullOrUndefined(errorsLog)) return;
                let sheets: any = $grid.data(internal.SHEETS);
                let sheetErrors = errorsLog[sheets.currentSheet];
                if (util.isNullOrUndefined(sheetErrors)) return;
                _.forEach(sheetErrors, function(c: any) {
                    if (cellEquals(c, cell)) {
                        decorate($(cell.element));
                        return false;
                    }
                });
            }
            
            function cellEquals(one: any, other: any) {
                if (one.columnKey !== other.columnKey) return false;
                if (one.id !== other.id) return false;
//                if (one.rowIndex !== other.rowIndex) return false;
                return true;
            }
        }
        
        export module color {
            export let Error: string = "ntsgrid-error";
            export let Alarm: string = "ntsgrid-alarm";
            export let ManualEditTarget: string = "ntsgrid-manual-edit-target";
            export let ManualEditOther: string = "ntsgrid-manual-edit-other";
            export let Reflect: string = "ntsgrid-reflect";
            export let Calculation: string = "ntsgrid-calc";
            export let Disable: string = "ntsgrid-disable";
            
            export class CellFormatter {
                $grid: JQuery;
                // Cell
                cellStateFeatureDef: any;
                statesTable: Array<any>;
                rowStates: any;
                // Row
                rowDisableFeatureDef: any;
                disableRows: any;
                // Text color
                textColorFeatureDef: any;
                textColorsTable: any;
                
                constructor($grid, features) {
                    this.$grid = $grid;
                    // Cell
                    this.cellStateFeatureDef = feature.find(features, feature.CELL_STATE);
                    this.setStatesTable(features);
                    // Row
                    this.rowDisableFeatureDef = feature.find(features, feature.ROW_STATE);
                    if (!util.isNullOrUndefined(this.rowDisableFeatureDef) 
                        && !util.isNullOrUndefined(this.rowDisableFeatureDef.rows)) {
                        this.disableRows = _.groupBy(this.rowDisableFeatureDef.rows, "rowId");
                    }
                    // Text color
                    this.textColorFeatureDef = feature.find(features, feature.TEXT_COLOR);
                    this.setTextColorsTableMap(features);
                }
                
                private setStatesTable(features: any) {
                    if (util.isNullOrUndefined(this.cellStateFeatureDef)) return;
                    let rowIdName = this.cellStateFeatureDef.rowId;
                    let columnKeyName = this.cellStateFeatureDef.columnKey;
                    let stateName = this.cellStateFeatureDef.state;
                    this.statesTable = this.cellStateFeatureDef.states;
                    this.rowStates = _.groupBy(this.statesTable, rowIdName);
                    _.forEach(this.rowStates, (value, key) => {
                        this.rowStates[key] = _.groupBy(this.rowStates[key], (item) => {
                            return item[columnKeyName];
                        });
                    });
                }
                
                private setTextColorsTableMap(features: any) {
                    if (util.isNullOrUndefined(this.textColorFeatureDef)) return;
                    let rowIdName = this.textColorFeatureDef.rowId;
                    let columnKeyName = this.textColorFeatureDef.columnKey;
                    let colorName = this.textColorFeatureDef.color;
                    let colorsTable = this.textColorFeatureDef.colorsTable;
                    this.textColorsTable = _.groupBy(colorsTable, rowIdName);
                    _.forEach(this.textColorsTable, (value, key) => {
                        this.textColorsTable[key] = _.groupBy(this.textColorsTable[key], (item) => {
                            return item[columnKeyName];
                        });
                    });
                }
                
                /**
                 * Format textbox.
                 */
                format(column: any) {
                    var self = this;
                    if (util.isNullOrUndefined(this.cellStateFeatureDef) 
                        || column.formatter !== undefined) return column;
                    let rowIdName: string = this.cellStateFeatureDef.rowId;
                    let columnKeyName: string = this.cellStateFeatureDef.columnKey;
                    let stateName: string = this.cellStateFeatureDef.state;
                    let statesTable: any = this.cellStateFeatureDef.states;
                    
                    column.formatter = function(value, rowObj) {
                        if (util.isNullOrUndefined(rowObj)) return value;
                        var _self = self;
                        setTimeout(function() {
                            let $gridCell = self.$grid.igGrid("cellById", rowObj[self.$grid.igGrid("option", "primaryKey")], column.key);
                            let $tr = $gridCell.closest("tr");
                            let cell = {
                                columnKey: column.key,
                                element: $gridCell[0],
                                rowIndex: $tr.data("rowIdx"),
                                id: $tr.data("id")
                            };
                            // If cell has error, mark it
                            errors.markIfError(self.$grid, cell);
                            
//                            let aColumn = _.find(_self.colorFeatureDef, function(col: any) {
//                                return col.key === column.key;
//                            });
//                            
//                            if (util.isNullOrUndefined(aColumn)) return;
//                            let cellColor = aColumn.map(aColumn.parse(value));
//                            $gridCell.css("background-color", cellColor);
                            
                            // Disable row
                            if (!util.isNullOrUndefined(self.disableRows)) {
                                let disableRow = self.disableRows[cell.id];
                                if (!util.isNullOrUndefined(disableRow) && disableRow.length > 0 && disableRow[0].disable)
                                    $gridCell.addClass(color.Disable);
                            }
                            // Set cell states
                            if (!util.isNullOrUndefined(statesTable) && !util.isNullOrUndefined(rowIdName) 
                                && !util.isNullOrUndefined(columnKeyName) && !util.isNullOrUndefined(stateName)
                                && !util.isNullOrUndefined(self.rowStates[cell.id])) {
                                let cellState = self.rowStates[cell.id][column.key];
                                if (util.isNullOrUndefined(cellState) || cellState.length === 0) return;
                                _.forEach(cellState[0][stateName], function(stt: any) {
                                    $gridCell.addClass(stt);
                                });
                            }
                        }, 0);
                        return value;
                    };
                    return column;
                }
                
                /**
                 * Style common controls.
                 */
                style($grid: JQuery, cell: any) {
                    if (util.isNullOrUndefined(this.cellStateFeatureDef)) return;
                    let rowIdName: string = this.cellStateFeatureDef.rowId;
                    let columnKeyName: string = this.cellStateFeatureDef.columnKey;
                    let stateName: string = this.cellStateFeatureDef.state;
                    let statesTable: any = this.cellStateFeatureDef.states;
                    
                    // Disable row
                    let controlType = utils.getControlType($grid, cell.columnKey);
                    if (!util.isNullOrUndefined(this.disableRows)) {
                        let disableRow = this.disableRows[cell.id];
                        if (!util.isNullOrUndefined(disableRow) && disableRow.length > 0 && disableRow[0].disable) {
                            $(cell.element).addClass(color.Disable);
                            $grid.ntsGrid(functions.DISABLE_CONTROL, cell.id, cell.columnKey, controlType);
                        }
                    }
                    // Set cell states
                    if (!util.isNullOrUndefined(statesTable) && !util.isNullOrUndefined(rowIdName) 
                        && !util.isNullOrUndefined(columnKeyName) && !util.isNullOrUndefined(stateName)
                        && !util.isNullOrUndefined(this.rowStates[cell.id])) {
                        let cellState = this.rowStates[cell.id][cell.columnKey];
                        if (util.isNullOrUndefined(cellState) || cellState.length === 0) return;
                        _.forEach(cellState[0][stateName], function(stt: any) {
                            if (stt === Disable && !$(cell.element).hasClass(Disable)) {
                                $grid.ntsGrid(functions.DISABLE_CONTROL, cell.id, cell.columnKey, controlType);
                            }
                            $(cell.element).addClass(stt);
                        });
                    }
                }
                
                setTextColor($grid: JQuery, cell: any) {
                    if (util.isNullOrUndefined(this.textColorFeatureDef)) return;
                    let rowIdName: string = this.textColorFeatureDef.rowId;
                    let columnKeyName: string = this.textColorFeatureDef.columnKey;
                    let colorName: string = this.textColorFeatureDef.color;
                    let colorsTable: any = this.textColorFeatureDef.colorsTable;
                    
                    if (!util.isNullOrUndefined(colorsTable) && !util.isNullOrUndefined(rowIdName)
                        && !util.isNullOrUndefined(columnKeyName) && !util.isNullOrUndefined(colorName)
                        && !util.isNullOrUndefined(this.textColorsTable[cell.id])) {
                        let textColor = this.textColorsTable[cell.id][cell.columnKey];
                        if (util.isNullOrUndefined(textColor) || textColor.length === 0) return;
                            $(cell.element).addClass(textColor[0][colorName]);
                    }
                }
            }
            
            export function styleHeaders($grid: JQuery, options: any) {
                let headerStyles = feature.find(options.ntsFeatures, feature.HEADER_STYLES);
                if (util.isNullOrUndefined(headerStyles)) return;
                setHeadersColor($grid, headerStyles.columns);
            }
            
            function setHeadersColor($grid: JQuery, columns: Array<any>) {
                let headersTable: any = $grid.igGrid("headersTable");
                let fixedHeadersTable: any = $grid.igGrid("fixedHeadersTable");
                fixedHeadersTable.find("th").each(function() {
                    let columnId = $(this).attr("id");
                    if (util.isNullOrUndefined(columnId)) return;
                    let key = columnId.split("_")[1];
                    let targetColumn;
                    _.forEach(columns, function(col: any) {
                        if (col.key === key) { 
                            targetColumn = col;
                            return false;
                        }
                    });
                    if (!util.isNullOrUndefined(targetColumn)) $(this).addClass(targetColumn.color);
                });
                
                headersTable.find("th").each(function() {
                    let columnId = $(this).attr("id");
                    if (util.isNullOrUndefined(columnId)) return;
                    let key = columnId.split("_")[1];
                    let targetColumn;
                    _.forEach(columns, function(col: any) {
                        if (col.key === key) { 
                            targetColumn = col;
                            return false;
                        }
                    });
                    if (!util.isNullOrUndefined(targetColumn)) $(this).addClass(targetColumn.color);
                });
            }
            
            export function rememberDisabled($grid: JQuery, cell: any) {
                let settings = $grid.data(internal.SETTINGS);
                if (!settings) return;
                let disables = settings.disables;
                if (!disables) return;
                let controlType = utils.getControlType($grid, cell.columnKey);
                let row = disables[cell.id];
                if (!row) return;
                _.forEach(row, function(c, i) {
                    if (c === cell.columnKey) {
                        $grid.ntsGrid(functions.DISABLE_CONTROL, cell.id, cell.columnKey, controlType);
                        $(cell.element).addClass(Disable);
                        return false;
                    }
                });
            }
            
            export function pushDisable($grid: JQuery, cell: any) {
                let settings = $grid.data(internal.SETTINGS);
                if (!settings) return;
                let disables = settings.disables;
                if (!disables) {
                    settings.disables = {};
                }
                if (!settings.disables[cell.id] || settings.disables[cell.id].length === 0) {
                    settings.disables[cell.id] = [ cell.columnKey ];
                    return;
                }
                let found = false;
                _.forEach(settings.disables[cell.id], function(c, i) {
                    if (c === cell.columnKey) {
                        found = true;
                        return false;
                    }
                });
                if (!found) settings.disables[cell.id].push(cell.columnKey);
            }
            export function popDisable($grid: JQuery, cell: any) {
                let settings = $grid.data(internal.SETTINGS);
                if (!settings) return;
                let disables = settings.disables;
                if (!disables || !disables[cell.id] || disables[cell.id].length === 0) return;
                let index = -1;
                _.forEach(disables[cell.id], function(c: any, i: number) {
                    if (c === cell.columnKey) {
                        index = i;
                        return false;
                    }
                });
                if (index !== -1) {
                    disables[cell.id].splice(index, 1);
                }
            }
        }
        
        module fixedColumns {
            export function getFixedTable($grid: JQuery): JQuery {
                return $("#" + $grid.attr("id") + "_fixed");
            }
            
            export function realGridOf($grid: JQuery) {
                if (utils.isIgGrid($grid)) return $grid;
                let gridId = $grid.attr("id");
                if (util.isNullOrUndefined(gridId)) return; 
                let endIdx = gridId.indexOf("_fixed"); 
                if (endIdx !== -1) {
                    let referGrid = $("#" + gridId.substring(0, endIdx)); 
                    if (!util.isNullOrUndefined(referGrid) && utils.fixable(referGrid))
                        return referGrid; 
                }
            }
        }
        
        module sheet {
            let normalStyles = { backgroundColor: '', color: '' };
            let selectedStyles = { backgroundColor: '#00B050', color: '#fff' };
            export class Configurator {
                currentSheet: string;
                currentPosition: string;
                blockId: any;
                displayScrollTop: any;
                sheets: any;
                
                constructor(currentSheet: string, sheets: any) {
                    this.currentSheet = currentSheet;
                    this.sheets = sheets;
                }
                
                static load($grid: JQuery, sheetFeature: any) {
                    let sheetConfig: any = $grid.data(internal.SHEETS);
                    if (util.isNullOrUndefined(sheetConfig)) {
                        let config = new Configurator(sheetFeature.initialDisplay, sheetFeature.sheets);
                        $grid.data(internal.SHEETS, config);
                    }
                }
            }
            
            export function onScroll($grid: JQuery) {
                let $scrollContainer = $("#" + $grid.attr("id") + "_scrollContainer");
                let $displayContainer = $("#" + $grid.attr("id") + "_displayContainer");
                if ($scrollContainer.length === 0 || $displayContainer.length === 0) return;
                
                let scrollListener = function(evt: any) {
                    let sheetConfig: any = $grid.data(internal.SHEETS);
                    if (util.isNullOrUndefined(sheetConfig)) return;
                    sheetConfig.currentPosition = $scrollContainer.scrollTop() + "px";
                    sheetConfig.displayScrollTop = $displayContainer.scrollTop();
                    sheetConfig.blockId = $grid.find("tbody tr:first").data("id");
                }
                $scrollContainer.on(events.Handler.SCROLL, scrollListener);  
            }
            
            /**
             * Unused
             */
            export function setup($grid: JQuery, options: any) {
                let sheetFeature = feature.find(options.ntsFeatures, feature.SHEET);
                if (util.isNullOrUndefined(sheetFeature)) return;
                let hidingFeature: any = { name: 'Hiding' };
                if (feature.isEnable(options.features, feature.HIDING)) {
                    feature.replaceBy(options, feature.HIDING, hidingFeature);
                } else {
                    options.features.push(hidingFeature);
                }
                
                Configurator.load($grid, sheetFeature);
                configButtons($grid, sheetFeature.sheets);
            }
            
            /**
             * Unused
             */
            function configButtons($grid: JQuery, sheets: any) {
                let gridWrapper = $("<div class='nts-grid-wrapper'/>");
                $grid.wrap($("<div class='nts-grid-container'/>")).wrap(gridWrapper);
                let gridContainer = $grid.closest(".nts-grid-container");
                let sheetButtonsWrapper = $("<div class='nts-grid-sheet-buttons'/>").appendTo(gridContainer);
                
                let sheetMng: any = $grid.data(internal.SHEETS);
                _.forEach(sheets, function(sheet: any) {
                    let btn = $("<button/>").addClass(sheet.name).text(sheet.text).appendTo(sheetButtonsWrapper);
                    if (sheetMng.currentSheet === sheet.name) btn.css(selectedStyles);
                    btn.on("click", function(evt: any) {
                        if (!utils.hidable($grid) || utils.isErrorStatus($grid)) return;
                        updateCurrentSheet($grid, sheet.name);
                        utils.showColumns($grid, sheet.columns);
                        hideOthers($grid);
                        
                        // Styles
                        sheetButtonsWrapper.find("button").css(normalStyles);
                        $(this).css(selectedStyles);
                    });
                });
            }
            
            export function hideOthers($grid: JQuery) {
                let sheetMng: any = $grid.data(internal.SHEETS);
                if (util.isNullOrUndefined(sheetMng)) return;
                let displayColumns;
                _.forEach(sheetMng.sheets, function(sheet: any) {
                    if (sheet.name !== sheetMng.currentSheet) {
                        utils.hideColumns($grid, sheet.columns);
                    } else {
                        displayColumns = sheet.columns;
                    }
                });
                
                // Resize displaying columns
                setTimeout(function() {
                    _.forEach(displayColumns, function(column: any) {
                         columnSize.loadOne($grid, column);
                    });
                }, 0);                
            }
            
            function updateCurrentSheet($grid: JQuery, name: string) {
                let sheetMng: any = $grid.data(internal.SHEETS);
                if (util.isNullOrUndefined(sheetMng)) return;
                sheetMng.currentSheet = name;
                $grid.data(internal.SHEETS, sheetMng);
            }
            
            export module load {
                
                export function setup($grid: JQuery, options: any) {
                    let sheetFeature = feature.find(options.ntsFeatures, feature.SHEET);
                    if (util.isNullOrUndefined(sheetFeature)) return;
                    Configurator.load($grid, sheetFeature);
                    configButtons($grid, sheetFeature.sheets);
                    if (!util.isNullOrUndefined($grid.data(internal.GRID_OPTIONS))) return; 
                    $grid.data(internal.GRID_OPTIONS, _.cloneDeep(options));
                    // Initial sheet
                    let sheetMng: any = $grid.data(internal.SHEETS);
                    let sheet: any = _.filter(sheetMng.sheets, function(sheet: any) {
                        return sheet.name === sheetMng.currentSheet;
                    });
                    
                    let columns = getSheetColumns(options.columns, sheet[0], options.features);
                    options.columns = columns;
                }
                
                function configButtons($grid: JQuery, sheets: any) {
                    if ($grid.closest(".nts-grid-container").length > 0) return;
                    let gridWrapper = $("<div class='nts-grid-wrapper'/>");
                    $grid.wrap($("<div class='nts-grid-container'/>")).wrap(gridWrapper);
                    let gridContainer = $grid.closest(".nts-grid-container");
                    let sheetButtonsWrapper = $("<div class='nts-grid-sheet-buttons'/>").appendTo(gridContainer);
                    
                    let sheetMng: any = $grid.data(internal.SHEETS);
                    _.forEach(sheets, function(sheet: any) {
                        let btn = $("<button/>").addClass(sheet.name).text(sheet.text).appendTo(sheetButtonsWrapper);
                        if (sheetMng.currentSheet === sheet.name) btn.css(selectedStyles);
                        btn.on("click", function(evt: any) {
                            if (utils.isErrorStatus($grid)) return;
                            updateCurrentSheet($grid, sheet.name);
                            let options = $grid.data(internal.GRID_OPTIONS);
                            let columns = getSheetColumns(options.columns, sheet, options.features);
                            let clonedOpts = _.cloneDeep(options);
                            clonedOpts.columns = columns;
                            clonedOpts.dataSource = $grid.igGrid("option", "dataSource");
                            $grid.igGrid("destroy");
                            $grid.off();
                            
                            let pagingFt = feature.find(clonedOpts.features, feature.PAGING);
                            let loader = $grid.data(internal.LOADER);
                            if (pagingFt && loader) {
                                if (!util.isNullOrUndefined(loader.pageIndex)) {
                                    pagingFt.currentPageIndex = loader.pageIndex;
                                }
                                if (!util.isNullOrUndefined(loader.pageSize)) {
                                    pagingFt.pageSize = loader.pageSize;
                                }
                                feature.replaceBy(clonedOpts, feature.PAGING, pagingFt); 
                            }
                            $grid.ntsGrid(clonedOpts);
                            
                            // Styles
                            sheetButtonsWrapper.find("button").css(normalStyles);
                            $(this).css(selectedStyles);
                        });
                    });
                }
                
                function getSheetColumns(allColumns: any, displaySheet: any, features: any) {
                    return _.filter(allColumns, function(column: any) {
                        if (column.group !== undefined && _.find(displaySheet.columns, function(col) {
                                return col === column.group[0].key;
                            }) !== undefined)
                            return true; 
                        
                        let belongToSheet = _.find(displaySheet.columns, function(col) {
                            return col === column.key;
                        }) !== undefined;
                        
                        let columnFixFeature = feature.find(features, feature.COLUMN_FIX);
                        if (!util.isNullOrUndefined(columnFixFeature)) {
                            return _.find(columnFixFeature.columnSettings, function(s: any) {
                                return s.columnKey === column.key;
                            }) !== undefined || belongToSheet;
                        }
                        return belongToSheet;
                    });
                }
            }
        }
        
        module onDemand {
            
            export class Loader {
                allKeysPath: any;
                pageRecordsPath: any;
                keys: Array<any>;
                pageIndex: any;
                pageSize: number;
                constructor(allKeysPath: any, pageRecordsPath: any, pageSize: number) {
                    this.allKeysPath = allKeysPath;
                    this.pageRecordsPath = pageRecordsPath;
                    this.pageSize = pageSize;
                }
            }
            
            export function hidePageSizeDD($grid: JQuery, options?: any) {
                if (options && !feature.find(options.ntsFeatures, feature.DEMAND_LOAD)) return; 
                let $gridContainer = $($grid.igGrid("container"));
                if ($gridContainer.length > 0) {
                    $gridContainer.find("div[class*='ui-iggrid-pagesizedropdowncontainer']").hide();
                }
            }
            
            export function loadKeys($grid: JQuery, path: any) {
                let dfd = $.Deferred();
                request.ajax(path).done(function(keys) {
                    let loader = $grid.data(internal.LOADER);
                    if (!loader.keys || loader.keys.length === 0) loader.keys = keys;
                    dfd.resolve(loader.keys);
                }).fail(function() {
                    dfd.reject();
                });
                return dfd.promise();
            }
            
            export function loadLazy(path: any, keys: Array<any>, startIndex: number, endIndex: number, 
                dataSource: any, primaryKey: any) {
                let dfd = $.Deferred();
                request.ajax(path, keys).done(function(data) {
                    _.forEach(data, function(rData, index) {
                        for (let i = startIndex; i < endIndex; i++) {
                            if (dataSource[i] && dataSource[i][primaryKey] === rData[primaryKey]) {
                                rData.loaded = true;
                                dataSource.splice(i, 1, rData); 
                            }
                        }
                    });
                    dfd.resolve(dataSource);
                }).fail(function() {;
                    dfd.reject();
                });
                return dfd.promise();
            }
            
            export function initial($grid: JQuery, options: any) {
                if (!options) return false;
                let pagingFt = feature.find(options.features, feature.PAGING);
                if (!pagingFt) return false;
                let demandLoadFt = feature.find(options.ntsFeatures, feature.DEMAND_LOAD);
                if (!demandLoadFt) return false;
                let pageSize = pagingFt.pageSize;
                let loader = $grid.data(internal.LOADER);
                if (!loader) {
                    $grid.data(internal.LOADER, new Loader(demandLoadFt.allKeysPath, demandLoadFt.pageRecordsPath, pagingFt.pageSize));
                } else if (loader.keys) { // Switch sheet
                    pageSize = loader.pageSize;
                    bindPageChange($grid);
                    return false;
                }
                loadKeys($grid, demandLoadFt.allKeysPath).done(function(keys: Array<any>) {
                    let primaryKey = options.primaryKey;
                    let ds = keys.map((key, index) => {
                        let obj = {};
                        obj[primaryKey] = key;
                        obj["loaded"] = false;
                        return obj;
                    });
                    let firstRecordIndex = (pagingFt.currentPageIndex || 0) * pageSize;
                    let lastRecordIndex = firstRecordIndex + pageSize;
                    let firstPageItems = keys.slice(firstRecordIndex, lastRecordIndex);
                    loadLazy(demandLoadFt.pageRecordsPath, firstPageItems, firstRecordIndex, lastRecordIndex, 
                        ds, primaryKey).done(function(data) {
                        options.dataSource = data;
                        $grid.igGrid(options);
                        bindPageChange($grid);
                    });
                }).fail(function() {
                    
                });
                return true;
            }
            
            function bindPageChange($grid: JQuery) {
                $grid.on(events.Handler.PAGE_INDEX_CHANGE, function(evt: any, ui: any) {
                    let newPageIndex = ui.newPageIndex;
                    let pageSize = ui.owner.pageSize();
                    let startIndex = newPageIndex * pageSize;
                    let endIndex = startIndex + pageSize;
                    let loader = $grid.data(internal.LOADER);
                    if (!loader || !loader.keys) return;
                    let dataSource = $grid.igGrid("option", "dataSource");
                    let primaryKey = $grid.igGrid("option", "primaryKey");
                    loader.pageIndex = ui.newPageIndex;
                    let newKeys = loader.keys.slice(startIndex, endIndex);
                    for (let i = endIndex - 1; i >= startIndex; i--) {
                        if (dataSource[i] && dataSource[i].loaded) {
                            newKeys.splice(i - startIndex, 1);
                        }
                    }
                    if (newKeys.length === 0) return;
                    loadLazy(loader.pageRecordsPath, newKeys, startIndex, endIndex, dataSource, primaryKey).done(function(data) {
                        $grid.igGrid("option", "dataSource", data);
                        ui.owner.pageIndex(ui.newPageIndex);
                    });
                    return false;
                });
                
                $grid.on(events.Handler.PAGE_SIZE_CHANGE, function(evt: any, ui: any) {
                    let loader = $grid.data(internal.LOADER);
                    if (!loader) return;
                    loader.pageSize = ui.newPageSize;
                    loader.pageIndex = 0;
                    let currentPageIndex = 0;
                    let startIndex = currentPageIndex * ui.newPageSize;
                    let endIndex = startIndex + ui.newPageSize;
                    let newKeys = loader.keys.slice(startIndex, endIndex);
                    let dataSource = $grid.igGrid("option", "dataSource");
                    let primaryKey = $grid.igGrid("option", "primaryKey");
                    for (let i = endIndex - 1; i >= startIndex; i--) {
                        if (dataSource[i] && dataSource[i].loaded) {
                            newKeys.splice(i - startIndex, 1);
                        }
                    }
                    if (newKeys.length === 0) return;
                    loadLazy(loader.pageRecordsPath, newKeys, startIndex, endIndex, dataSource, primaryKey).done(function(data) {
                        $grid.igGrid("option", "dataSource", data);
                        ui.owner.pageSize(ui.newPageSize);
                    });
                    return false;
                });
            }
        }
        
        module settings {
            export function build($grid: JQuery, options: any) {
                let data: any = {};
                data.preventEditInError = options.preventEditInError;
                $grid.data(internal.SETTINGS, data);
            }
            
            export function setGridSize($grid: JQuery) {
                var height = window.innerHeight;
                var width = window.innerWidth;
                $grid.igGrid("option", "width", width - 240);
                $grid.igGrid("option", "height", height - 90);
            }
        }
        
        module internal {
            export let CONTROL_TYPES = "ntsControlTypesGroup";
            export let COMBO_SELECTED = "ntsComboSelection";
            // Full columns options
            export let GRID_OPTIONS = "ntsGridOptions";
            export let SELECTED_CELL = "ntsSelectedCell";
            export let SHEETS: string = "ntsGridSheets";
            export let SPECIAL_COL_TYPES = "ntsSpecialColumnTypes";
            export let ENTER_DIRECT = "enter";
            export let SETTINGS = "ntsSettings";
            export let ERRORS_LOG = "ntsErrorsLog";
            export let LOADER = "ntsLoader";
        }
        
        module utils {
            
            export function isArrowKey(evt: any) {
                return evt.keyCode >= 37 && evt.keyCode <= 40;
            }
            export function isAlphaNumeric(evt: any) {
                return evt.keyCode >= 48 && evt.keyCode <= 90;
            }
            export function isTabKey(evt: any) {
                return evt.keyCode === 9;
            }
            export function isEnterKey(evt: any) {
                return evt.keyCode === 13;
            }
            export function isSpaceKey(evt: any) {
                return evt.keyCode === 32;
            }
            export function isDeleteKey(evt: any) {
                return evt.keyCode === 46;
            }
            export function isPasteKey(evt: any) {
                return evt.keyCode === 86;
            }
            export function isCopyKey(evt: any) {
                return evt.keyCode === 67;
            }
            export function isCutKey(evt: any) {
                return evt.keyCode === 88;
            }
            
            export function isErrorStatus($grid: JQuery) {
                let cell = selection.getSelectedCell($grid);
                return isEditMode($grid) && errors.any(cell);
            }
            /**
             * Only used in edit mode
             */
            export function isNotErrorCell($grid: JQuery, evt: any) {
                let cell = selection.getSelectedCell($grid);
                let $target = $(evt.target);
                let td = $target;
                if (!$target.prev().is("td")) td = $target.closest("td"); 
                return isEditMode($grid) && td.length > 0 && td[0] !== cell.element[0]
                        && errors.any(cell);
            }
            export function isEditMode($grid: JQuery) {
                return (updatable($grid) && $grid.igGridUpdating("isEditing"));
            }
            
            export function isIgGrid($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGrid"));
            }
            export function selectable($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGridSelection"));
            }
            export function updatable($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGridUpdating"));
            }
            export function fixable($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGridColumnFixing"));
            }
            export function hidable($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGridHiding"));
            }
            export function pageable($grid: JQuery) {
                return !util.isNullOrUndefined($grid.data("igGridPaging"));
            }
            export function disabled($cell: JQuery) {
                return $cell.hasClass(color.Disable);
            }
            
            export function dataTypeOfPrimaryKey($grid: JQuery, columnsMap: any) : string {
                if (util.isNullOrUndefined(columnsMap)) return;
                let columns = columnsMap["undefined"];
                if (Object.keys(columnsMap).length > 1) {
                    columns = _.concat(columnsMap["true"], columnsMap["undefined"]);
                }
                let primaryKey = $grid.igGrid("option", "primaryKey");
                let keyColumn: Array<any> =  _.filter(columns, function(column: any) {
                    return column.key === primaryKey;
                });
                if (!util.isNullOrUndefined(keyColumn) && keyColumn.length > 0) return keyColumn[0].dataType;
                return;
            }
            export function parseIntIfNumber(value: any, $grid: JQuery, columnsMap: any) {
                if (dataTypeOfPrimaryKey($grid, columnsMap) === "number") {
                    return parseInt(value);
                }
                return value;
            }
            
            export function isCopiableControls($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                switch (columnControlTypes[columnKey]) {
                    case ntsControls.LINK_LABEL:
                    case ntsControls.TEXTBOX:
                    case ntsControls.LABEL:
                        return true;
                }
                return false;
            }
            export function isCuttableControls($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                switch (columnControlTypes[columnKey]) {
                    case ntsControls.TEXTBOX:
                        return true;
                }
                return false;
            }
            export function isPastableControls($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                switch (columnControlTypes[columnKey]) {
                    case ntsControls.LABEL:
                    case ntsControls.CHECKBOX:
                    case ntsControls.LINK_LABEL:
                    case ntsControls.COMBOBOX:
                        return false;
                }
                return true;
            }
            export function isDisabled($cell: JQuery) {
                return $cell.hasClass(color.Disable);
            }
            export function isComboBox($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                if (columnControlTypes[columnKey] === ntsControls.COMBOBOX) return true;
                return false;
            }
            export function isNtsControl($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                switch (columnControlTypes[columnKey]) {
                    case ntsControls.LABEL:
                    case ntsControls.CHECKBOX:
                    case ntsControls.SWITCH_BUTTONS:
                    case ntsControls.COMBOBOX:
                    case ntsControls.BUTTON:
                    case ntsControls.DELETE_BUTTON:
                        return true;
                }
                return false;
            }
            export function getControlType($grid: JQuery, columnKey: string) {
                let columnControlTypes = $grid.data(internal.CONTROL_TYPES);
                if (util.isNullOrUndefined(columnControlTypes)) return;
                return columnControlTypes[columnKey];
            }
            
            export function comboBoxOfCell(cell: any) {
                return $(cell.element).find(".nts-combo-container");
            }
            
            export function getColumns($grid: JQuery) {
                if (isIgGrid($grid)) {
                    return $grid.igGrid("option", "columns");
                }
                let referGrid = fixedColumns.realGridOf($grid);
                if (!util.isNullOrUndefined(referGrid)) return referGrid.igGrid("option", "columns");
            }
            export function getColumnsMap($grid: JQuery) {
                let columns = getColumns($grid);
                return _.groupBy(columns, "fixed");
            }
            export function getVisibleColumns($grid: JQuery) {
                return _.filter(getColumns($grid), function(column: any) {
                    return column.hidden !== true;
                }); 
            }
            
            export function getVisibleColumnsMap($grid: JQuery) {
                let visibleColumns = getVisibleColumns($grid);
                return _.groupBy(visibleColumns, "fixed");
            }
            export function getVisibleFixedColumns($grid: JQuery) {
                return _.filter(getColumns($grid), function(column: any) {
                    return column.hidden !== true && column.fixed === true;
                });
            }
            
            export function isFixedColumn(columnKey: any, visibleColumnsMap: any) {
                return _.find(visibleColumnsMap["true"], function(column: any) {
                    return column.key === columnKey;
                }) !== undefined;
            }
            export function isFixedColumnCell(cell: any, visibleColumnsMap: any) {
                return _.find(visibleColumnsMap["true"], function(column: any) {
                    return column.key === cell.columnKey;
                }) !== undefined;
            }
            export function columnsGroupOfColumn(column: any, visibleColumnsMap: any) {
                return visibleColumnsMap[column.fixed ? "true" : "undefined" ];
            }
            export function columnsGroupOfCell(cell: any, visibleColumnsMap: any) {
                if (isFixedColumnCell(cell, visibleColumnsMap)) return visibleColumnsMap["true"];
                return visibleColumnsMap["undefined"];
            }
            export function visibleColumnsFromMap(visibleColumnsMap: any) {
                return _.concat(visibleColumnsMap["true"], visibleColumnsMap["undefined"]);
            }
            export function noOfVisibleColumns(visibleColumnsMap: any) {
                return visibleColumnsMap["true"].length + visibleColumnsMap["undefined"].length;
            }
            export function getFixedColumns(visibleColumnsMap: any) {
                return visibleColumnsMap["true"];
            }
            export function getUnfixedColumns(visibleColumnsMap: any) {
                return visibleColumnsMap["undefined"];
            }
            
            export function nextColumn(visibleColumnsMap: any, columnIndex: number, isFixed: boolean) {
                if (util.isNullOrUndefined(visibleColumnsMap)) return;
                let nextCol: any = {};
                let mapKeyName = isFixed ? "true" : "undefined";
                let reverseKeyName = isFixed ? "undefined" : "true";
                if (columnIndex < visibleColumnsMap[mapKeyName].length - 1) {
                    return {
                                options: visibleColumnsMap[mapKeyName][columnIndex + 1],
                                index: columnIndex + 1
                           };
                } else if (columnIndex === visibleColumnsMap[mapKeyName].length - 1) {
                    return {
                                options: visibleColumnsMap[reverseKeyName][0],
                                index: 0
                           };
                }
            }
            export function nextColumnByKey(visibleColumnsMap: any, columnKey: string, isFixed: boolean) {
                if (util.isNullOrUndefined(visibleColumnsMap)) return;
                let currentColumnIndex;
                let currentColumn;
                let fixedColumns = visibleColumnsMap["true"];
                let unfixedColumns = visibleColumnsMap["undefined"];
                
                if (isFixed && fixedColumns.length > 0) {
                    _.forEach(fixedColumns, function(col: any, index: number) {
                        if (col.key === columnKey) {
                            currentColumnIndex = index;
                            currentColumn = col;
                            return false;
                        }
                    });
                    if (util.isNullOrUndefined(currentColumn) || util.isNullOrUndefined(currentColumnIndex)) return;
                    if (currentColumnIndex === fixedColumns.length - 1) {
                        return {
                            options: unfixedColumns[0],
                            index: 0
                        };
                    }
                    return {
                        options: fixedColumns[currentColumnIndex + 1],
                        index: currentColumnIndex + 1
                    }
                } 
                
                if (!isFixed && unfixedColumns.length > 0) {
                    _.forEach(unfixedColumns, function(col: any, index: number) {
                        if (col.key === columnKey) {
                            currentColumnIndex = index;
                            currentColumn = col;
                            return false;
                        }
                    });
                    if (util.isNullOrUndefined(currentColumn) || util.isNullOrUndefined(currentColumnIndex)) return;
                    if (currentColumnIndex === unfixedColumns.length - 1) {  
                        return {
                            options: fixedColumns.length > 0 ? fixedColumns[0] : unfixedColumns[0],
                            index: 0  
                        };
                    }
                    return {
                        options: unfixedColumns[currentColumnIndex + 1],
                        index: currentColumnIndex + 1
                    }
                }
            }
            
            export function rowAt(cell: any) {
                if (util.isNullOrUndefined(cell)) return;
                return $(cell.element).closest("tr");
            }
            export function nextNRow(cell: any, noOfNext: number) {
                return $(cell.element).closest("tr").nextAll("tr:eq(" + (noOfNext - 1) + ")");
            }
            export function getDisplayColumnIndex($grid: JQuery, cell: any) {
                let columns = $grid.igGrid("option", "columns");
                for (let i = 0; i < columns.length; i++) {
                    if (columns[i].key === cell.columnKey)
                        return i;
                }
                return -1;
            }
            export function getDisplayContainer($grid: JQuery) {
                return $("#" + $grid.attr("id") + "_displayContainer");
            }
            export function getScrollContainer($grid: JQuery) {
                return $("#" + $grid.attr("id") + "_scrollContainer");
            }
            
            export function startEdit($grid: JQuery, cell: any) {
                let visibleColumns = getVisibleColumns($grid);
                for (let i = 0; i < visibleColumns.length; i++) {
                    if (visibleColumns[i].key === cell.columnKey) {
                        $grid.igGridUpdating("startEdit", cell.id, i);
                        break;
                    }
                }
            }
            
            export function hideColumns($grid: JQuery, columns: Array<any>) {
                $grid.igGridHiding("hideMultiColumns", columns);
            }
            export function showColumns($grid: JQuery, columns: Array<any>) {
                $grid.igGridHiding("showMultiColumns", columns);
            }
            
            export function analyzeColumns(columns: any) {
                let flatCols = [];
                flatColumns(columns, flatCols);
                return flatCols;
            }
            
            function flatColumns(columns: any, flatCols: Array<any>) {
                _.forEach(columns, function(column) {
                    if (util.isNullOrUndefined(column.group)) {
                        flatCols.push(column);
                        return;
                    }
                    flatColumns(column.group, flatCols);
                });
            }
        }
    }
}