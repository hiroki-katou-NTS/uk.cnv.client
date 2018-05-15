var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var ui;
        (function (ui_1) {
            var koExtentions;
            (function (koExtentions) {
                var NtsGridListBindingHandler = (function () {
                    function NtsGridListBindingHandler() {
                    }
                    NtsGridListBindingHandler.prototype.init = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var HEADER_HEIGHT = 27;
                        var ROW_HEIGHT = 23;
                        var DIFF_NUMBER = 2;
                        var $grid = $(element).addClass("nts-gridlist");
                        var gridId = $grid.attr('id');
                        if (nts.uk.util.isNullOrUndefined(gridId)) {
                            throw new Error('the element NtsGridList must have id attribute.');
                        }
                        var data = valueAccessor();
                        var optionsValue = data.primaryKey !== undefined ? data.primaryKey : data.optionsValue;
                        var options = ko.unwrap(data.dataSource !== undefined ? data.dataSource : data.options);
                        var deleteOptions = ko.unwrap(data.deleteOptions);
                        var observableColumns = ko.unwrap(data.columns);
                        var showNumbering = ko.unwrap(data.showNumbering) === true ? true : false;
                        var enable = ko.unwrap(data.enable);
                        var value = ko.unwrap(data.value);
                        var virtualization = true;
                        var rows = ko.unwrap(data.rows);
                        $grid.data("init", true);
                        if (data.multiple) {
                            ROW_HEIGHT = 24;
                            var _document = document;
                            var isIE = false || !!_document.documentMode;
                            var _window = window;
                            var isEdge = !isIE && !!_window.StyleMedia;
                            if (isIE || isEdge) {
                                DIFF_NUMBER = -2;
                            }
                        }
                        var features = [];
                        features.push({ name: 'Selection', multipleSelection: data.multiple });
                        if (data.multiple || showNumbering) {
                            features.push({
                                name: 'RowSelectors',
                                enableCheckBoxes: data.multiple,
                                enableRowNumbering: false,
                                rowSelectorColumnWidth: 25
                            });
                        }
                        var tabIndex = $grid.attr("tabindex");
                        $grid.data("tabindex", nts.uk.util.isNullOrEmpty(tabIndex) ? "0" : tabIndex);
                        $grid.attr("tabindex", "-1");
                        var gridFeatures = ko.unwrap(data.features);
                        var iggridColumns = _.map(observableColumns, function (c) {
                            c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                            c["dataType"] = 'string';
                            if (c["controlType"] === "switch") {
                                var switchF = _.find(gridFeatures, function (s) {
                                    return s["name"] === "Switch";
                                });
                                if (!uk.util.isNullOrUndefined(switchF)) {
                                    features.push({ name: 'Updating', enableAddRow: false, enableDeleteRow: false, editMode: 'none' });
                                    var switchOptions_1 = ko.unwrap(switchF['options']);
                                    var switchValue_1 = switchF['optionsValue'];
                                    var switchText_1 = switchF['optionsText'];
                                    c["formatter"] = function createButton(val, row) {
                                        var result = $('<div class="ntsControl"/>');
                                        result.attr("data-value", val);
                                        _.forEach(switchOptions_1, function (opt) {
                                            var value = opt[switchValue_1];
                                            var text = opt[switchText_1];
                                            var btn = $('<button class="nts-switch-button" tabindex="-1"/>').text(text);
                                            if ($grid.data("enable") === false) {
                                                btn.attr("disabled", "disabled");
                                            }
                                            btn.attr('data-value', value);
                                            if (val == value) {
                                                btn.addClass('selected');
                                            }
                                            btn.appendTo(result);
                                        });
                                        return result[0].outerHTML;
                                    };
                                    $grid.on("click", ".nts-switch-button", function (evt, ui) {
                                        var $element = $(this);
                                        var selectedValue = $element.attr('data-value');
                                        var $tr = $element.closest("tr");
                                        $grid.ntsGridListFeature('switch', 'setValue', $tr.attr("data-id"), c["key"], selectedValue);
                                    });
                                    ROW_HEIGHT = 30;
                                }
                            }
                            return c;
                        });
                        var isDeleteButton = !uk.util.isNullOrUndefined(deleteOptions) && !uk.util.isNullOrUndefined(deleteOptions.deleteField)
                            && deleteOptions.visible === true;
                        var height = data.height;
                        if (!nts.uk.util.isNullOrEmpty(rows)) {
                            if (isDeleteButton) {
                                ROW_HEIGHT = 30;
                            }
                            height = rows * ROW_HEIGHT + HEADER_HEIGHT - DIFF_NUMBER;
                            var colSettings_1 = [];
                            _.forEach(iggridColumns, function (c) {
                                if (c["hidden"] === undefined || c["hidden"] === false) {
                                    colSettings_1.push({ columnKey: c["key"], allowTooltips: true });
                                    if (nts.uk.util.isNullOrEmpty(c["columnCssClass"])) {
                                        c["columnCssClass"] = "text-limited";
                                    }
                                    else {
                                        c["columnCssClass"] += " text-limited";
                                    }
                                }
                            });
                            features.push({
                                name: "Tooltips",
                                columnSettings: colSettings_1,
                                visibility: "overflow",
                                showDelay: 200,
                                hideDelay: 200
                            });
                            $grid.addClass("row-limited");
                        }
                        $grid.data("height", height);
                        $grid.igGrid({
                            width: data.width,
                            height: height,
                            primaryKey: optionsValue,
                            columns: iggridColumns,
                            virtualization: virtualization,
                            virtualizationMode: 'continuous',
                            features: features,
                            tabIndex: -1
                        });
                        if (data.itemDraggable) {
                            new SwapHandler().setModel(new GridSwapList($grid, optionsValue)).enableDragDrop(data.dataSource);
                        }
                        if (isDeleteButton) {
                            var sources = (data.dataSource !== undefined ? data.dataSource : data.options);
                            $grid.ntsGridList("setupDeleteButton", {
                                deleteField: deleteOptions.deleteField,
                                sourceTarget: sources
                            });
                        }
                        $grid.ntsGridList('setupSelecting');
                        if (data.multiple) {
                            $grid.bind('iggridrowselectorscheckboxstatechanging', function (eventObject) {
                                return (String($grid.data("enable")) === "false") ? false : true;
                            });
                        }
                        $grid.bind('iggridselectionrowselectionchanging', function (eventObject) {
                            return (String($grid.data("enable")) === "false") ? false : true;
                        });
                        $grid.bind('selectionchanged', function () {
                            $grid.data("ui-changed", true);
                            if (data.multiple) {
                                var selected = $grid.ntsGridList('getSelected');
                                if (!nts.uk.util.isNullOrEmpty(selected)) {
                                    data.value(_.map(selected, function (s) { return s.id; }));
                                }
                                else {
                                    data.value([]);
                                }
                            }
                            else {
                                var selected = $grid.ntsGridList('getSelected');
                                if (!nts.uk.util.isNullOrEmpty(selected)) {
                                    data.value(selected.id);
                                }
                                else {
                                    data.value('');
                                }
                            }
                        });
                        $grid.setupSearchScroll("igGrid", virtualization);
                        $grid.ntsGridList("setupScrollWhenBinding");
                        $grid.bind("switchvaluechanged", function (evt, dataX) {
                            setTimeout(function () {
                                var source = _.cloneDeep(data.dataSource !== undefined ? data.dataSource() : data.options());
                                _.forEach(source, function (o) {
                                    if (o[optionsValue] === dataX.rowKey) {
                                        o[dataX.columnKey] = dataX.value;
                                        return true;
                                    }
                                });
                                $grid.data("ui-changed", true);
                                if (data.dataSource !== undefined) {
                                    data.dataSource(source);
                                }
                                else {
                                    data.options(source);
                                }
                            }, 100);
                        });
                        $grid.bind("checknewitem", function (evt) {
                            return false;
                        });
                    };
                    NtsGridListBindingHandler.prototype.update = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var $grid = $(element);
                        var data = valueAccessor();
                        var enable = ko.unwrap(data.enable);
                        var optionsValue = data.primaryKey !== undefined ? data.primaryKey : data.optionsValue;
                        var gridSource = $grid.igGrid('option', 'dataSource');
                        var sources = (data.dataSource !== undefined ? data.dataSource() : data.options());
                        if ($grid.data("enable") !== enable) {
                            if (!enable) {
                                $grid.ntsGridList('unsetupSelecting');
                                $grid.addClass("disabled");
                            }
                            else {
                                $grid.ntsGridList('setupSelecting');
                                $grid.removeClass("disabled");
                            }
                        }
                        $grid.data("enable", enable);
                        if (String($grid.attr("filtered")) === "true") {
                            var filteredSource_1 = [];
                            _.forEach(gridSource, function (item) {
                                var itemX = _.find(sources, function (s) {
                                    return s[optionsValue] === item[optionsValue];
                                });
                                if (!nts.uk.util.isNullOrUndefined(itemX)) {
                                    filteredSource_1.push(itemX);
                                }
                            });
                            if (!_.isEqual(filteredSource_1, gridSource)) {
                                $grid.igGrid('option', 'dataSource', _.cloneDeep(filteredSource_1));
                                $grid.igGrid("dataBind");
                            }
                        }
                        else {
                            var currentSources = sources.slice();
                            var observableColumns = _.filter(ko.unwrap(data.columns), function (c) {
                                c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                                return c["isDateColumn"] !== undefined && c["isDateColumn"] !== null && c["isDateColumn"] === true;
                            });
                            if (!nts.uk.util.isNullOrEmpty(observableColumns)) {
                                _.forEach(currentSources, function (s) {
                                    _.forEach(observableColumns, function (c) {
                                        var key = c["key"] === undefined ? c["prop"] : c["key"];
                                        s[key] = moment(s[key]).format(c["format"]);
                                    });
                                });
                            }
                            if (!_.isEqual(currentSources, gridSource)) {
                                $grid.igGrid('option', 'dataSource', _.cloneDeep(currentSources));
                                $grid.igGrid("dataBind");
                            }
                        }
                        var currentSelectedItems = $grid.ntsGridList('getSelected');
                        var isEqual = _.isEqualWith(currentSelectedItems, data.value(), function (current, newVal) {
                            if ((current === undefined && newVal === undefined) || (current !== undefined && current.id === newVal)) {
                                return true;
                            }
                        });
                        if (!isEqual) {
                            _.defer(function () { $grid.trigger("selectChange"); });
                            $grid.ntsGridList('setSelected', data.value());
                        }
                        $grid.data("ui-changed", false);
                        $grid.closest('.ui-iggrid').addClass('nts-gridlist').height($grid.data("height")).attr("tabindex", $grid.data("tabindex"));
                    };
                    return NtsGridListBindingHandler;
                }());
                ko.bindingHandlers['ntsGridList'] = new NtsGridListBindingHandler();
                var SwapHandler = (function () {
                    function SwapHandler() {
                    }
                    SwapHandler.prototype.setModel = function (model) {
                        this.model = model;
                        return this;
                    };
                    Object.defineProperty(SwapHandler.prototype, "Model", {
                        get: function () {
                            return this.model;
                        },
                        enumerable: true,
                        configurable: true
                    });
                    SwapHandler.prototype.handle = function (value) {
                        var self = this;
                        var model = this.model;
                        var options = {
                            items: "tbody > tr",
                            containment: this.model.$grid,
                            cursor: "move",
                            connectWith: this.model.$grid,
                            placeholder: "ui-state-highlight",
                            helper: this._createHelper,
                            appendTo: this.model.$grid,
                            start: function (evt, ui) {
                                self.model.transportBuilder.setList(self.model.$grid.igGrid("option", "dataSource"));
                            },
                            beforeStop: function (evt, ui) {
                                self._beforeStop.call(this, model, evt, ui);
                            },
                            update: function (evt, ui) {
                                self._update.call(this, model, evt, ui, value);
                            }
                        };
                        this.model.$grid.sortable(options).disableSelection();
                    };
                    SwapHandler.prototype._createHelper = function (evt, ui) {
                        var selectedRowElms = $(evt.currentTarget).igGrid("selectedRows");
                        selectedRowElms.sort(function (one, two) {
                            return one.index - two.index;
                        });
                        var $helper;
                        if ($(evt.currentTarget).hasClass("multiple-drag") && selectedRowElms.length > 1) {
                            $helper = $("<div><table><tbody></tbody></table></div>").addClass("select-drag");
                            var rowId = ui.data("row-idx");
                            var selectedItems = selectedRowElms.map(function (elm) { return elm.element; });
                            var height = 0;
                            $.each(selectedItems, function () {
                                $helper.find("tbody").append($(this).clone());
                                height += $(this).outerHeight();
                                if (rowId !== this.data("row-idx"))
                                    $(this).hide();
                            });
                            $helper.height(height);
                            $helper.find("tr").first().children().each(function (idx) {
                                $(this).width(ui.children().eq(idx).width());
                            });
                        }
                        else {
                            $helper = ui.clone();
                            $helper.children().each(function (idx) {
                                $(this).width(ui.children().eq(idx).width());
                            });
                        }
                        return $helper[0];
                    };
                    SwapHandler.prototype._beforeStop = function (model, evt, ui) {
                        model.transportBuilder.toAdjacent(model.neighbor(ui)).target(model.target(ui));
                        if (ui.helper.hasClass("select-drag")) {
                            var rowsInHelper = ui.helper.find("tr");
                            var rows = rowsInHelper.toArray();
                            $(this).sortable("cancel");
                            for (var idx in rows) {
                                model.$grid.find("tbody").children().eq($(rows[idx]).data("row-idx")).show();
                            }
                        }
                    };
                    SwapHandler.prototype._update = function (model, evt, ui, value) {
                        if (ui.item.closest("table").length === 0)
                            return;
                        model.transportBuilder.update();
                        model.$grid.igGrid("option", "dataSource", model.transportBuilder.getList());
                        value(model.transportBuilder.getList());
                        setTimeout(function () { model.dropDone(); }, 0);
                    };
                    SwapHandler.prototype.enableDragDrop = function (value) {
                        this.model.enableDrag(this, value, this.handle);
                    };
                    return SwapHandler;
                }());
                var SwapModel = (function () {
                    function SwapModel($grid, primaryKey) {
                        this.$grid = $grid;
                        this.primaryKey = primaryKey;
                        this.transportBuilder = new ListItemTransporter().primary(this.primaryKey);
                    }
                    return SwapModel;
                }());
                var GridSwapList = (function (_super) {
                    __extends(GridSwapList, _super);
                    function GridSwapList() {
                        _super.apply(this, arguments);
                    }
                    GridSwapList.prototype.target = function (opts) {
                        if (opts.helper !== undefined && opts.helper.hasClass("select-drag")) {
                            return opts.helper.find("tr").map(function () {
                                return $(this).data("id");
                            });
                        }
                        return [opts.item.data("id")];
                    };
                    GridSwapList.prototype.neighbor = function (opts) {
                        return opts.item.prev().length === 0 ? "ceil" : opts.item.prev().data("id");
                    };
                    GridSwapList.prototype.dropDone = function () {
                        var self = this;
                        self.$grid.igGridSelection("clearSelection");
                        setTimeout(function () {
                            self.$grid.igGrid("virtualScrollTo", self.transportBuilder.incomeIndex);
                        }, 0);
                    };
                    GridSwapList.prototype.enableDrag = function (ctx, value, cb) {
                        var self = this;
                        this.$grid.on("iggridrowsrendered", function (evt, ui) {
                            cb.call(ctx, value);
                        });
                    };
                    return GridSwapList;
                }(SwapModel));
                var ListItemTransporter = (function () {
                    function ListItemTransporter() {
                    }
                    ListItemTransporter.prototype.primary = function (primaryKey) {
                        this.primaryKey = primaryKey;
                        return this;
                    };
                    ListItemTransporter.prototype.target = function (targetIds) {
                        this.targetIds = targetIds;
                        return this;
                    };
                    ListItemTransporter.prototype.toAdjacent = function (adjId) {
                        if (adjId === null)
                            adjId = "ceil";
                        this.adjacentIncomeId = adjId;
                        return this;
                    };
                    ListItemTransporter.prototype.indexOf = function (list, targetId) {
                        var _this = this;
                        return _.findIndex(list, function (elm) { return elm[_this.primaryKey].toString() === targetId.toString(); });
                    };
                    ListItemTransporter.prototype.update = function () {
                        for (var i = 0; i < this.targetIds.length; i++) {
                            this.outcomeIndex = this.indexOf(this.list, this.targetIds[i]);
                            if (this.outcomeIndex === -1)
                                return;
                            var target = this.list.splice(this.outcomeIndex, 1);
                            this.incomeIndex = this.indexOf(this.list, this.adjacentIncomeId) + 1;
                            if (this.incomeIndex === 0) {
                                if (this.adjacentIncomeId === "ceil")
                                    this.incomeIndex = 0;
                                else if (target !== undefined) {
                                    this.list.splice(this.outcomeIndex, 0, target[0]);
                                    return;
                                }
                            }
                            this.list.splice(this.incomeIndex + i, 0, target[0]);
                        }
                    };
                    ListItemTransporter.prototype.getList = function () {
                        return this.list;
                    };
                    ListItemTransporter.prototype.setList = function (list) {
                        this.list = list;
                    };
                    return ListItemTransporter;
                }());
            })(koExtentions = ui_1.koExtentions || (ui_1.koExtentions = {}));
        })(ui = uk.ui || (uk.ui = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
//# sourceMappingURL=gridlist-ko-ext.js.map