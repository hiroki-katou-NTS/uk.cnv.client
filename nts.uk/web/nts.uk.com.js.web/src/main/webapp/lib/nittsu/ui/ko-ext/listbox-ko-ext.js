var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var ui;
        (function (ui_1) {
            var koExtentions;
            (function (koExtentions) {
                var ListBoxBindingHandler = (function () {
                    function ListBoxBindingHandler() {
                    }
                    ListBoxBindingHandler.prototype.init = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var data = valueAccessor();
                        var options = ko.unwrap(data.options);
                        var optionValue = ko.unwrap(data.primaryKey === undefined ? data.optionsValue : data.primaryKey);
                        var optionText = ko.unwrap(data.primaryText === undefined ? data.optionsText : data.primaryText);
                        var selectedValue = ko.unwrap(data.value);
                        var isMultiSelect = ko.unwrap(data.multiple);
                        var enable = ko.unwrap(data.enable);
                        var columns = data.columns;
                        var elementId = $(element).attr("id");
                        var gridId = elementId;
                        if (nts.uk.util.isNullOrUndefined(gridId)) {
                            gridId = nts.uk.util.randomId();
                        }
                        else {
                            gridId += "_grid";
                        }
                        $(element).append("<table id='" + gridId + "' class='ntsListBox ntsControl'/>");
                        var container = $(element).find("#" + gridId);
                        container.data("options", options.slice());
                        container.data("init", true);
                        container.data("enable", enable);
                        var changeEvent = new CustomEvent("selectionChange", {
                            detail: {},
                        });
                        container.data("selectionChange", changeEvent);
                        var features = [];
                        features.push({ name: 'Selection', multipleSelection: isMultiSelect });
                        var maxWidthCharacter = 15;
                        var gridFeatures = ko.unwrap(data.features);
                        var width = 0;
                        var iggridColumns = [];
                        if (nts.uk.util.isNullOrUndefined(columns)) {
                            iggridColumns.push({ "key": optionValue, "width": 10 * maxWidthCharacter + 20, "headerText": '', "columnCssClass": 'nts-column', 'hidden': true });
                            iggridColumns.push({ "key": optionText, "width": 10 * maxWidthCharacter + 20, "headerText": '', "columnCssClass": 'nts-column' });
                            width += 10 * maxWidthCharacter + 20;
                            container.data("fullValue", true);
                        }
                        else {
                            var isHaveKey_1 = false;
                            iggridColumns = _.map(columns, function (c) {
                                c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                                c["width"] = c["length"] * maxWidthCharacter + 20;
                                c["headerText"] = '';
                                c["columnCssClass"] = 'nts-column';
                                width += c["length"] * maxWidthCharacter + 20;
                                if (optionValue === c["key"]) {
                                    isHaveKey_1 = true;
                                }
                                return c;
                            });
                            if (!isHaveKey_1) {
                                iggridColumns.push({ "key": optionValue, "width": 10 * maxWidthCharacter + 20, "headerText": '', "columnCssClass": 'nts-column', 'hidden': true });
                            }
                        }
                        var gridHeaderHeight = 24;
                        container.igGrid({
                            width: width + "px",
                            height: (data.rows * 28 + gridHeaderHeight) + "px",
                            primaryKey: optionValue,
                            columns: iggridColumns,
                            virtualization: true,
                            virtualizationMode: 'continuous',
                            features: features
                        });
                        container.ntsGridList('setupSelecting');
                        container.bind('iggridselectionrowselectionchanging', function (evt, ui) {
                            if (container.data("enable") === false) {
                                return false;
                            }
                            var itemSelected = ui.row.id;
                            var dataSource = container.igGrid('option', "dataSource");
                            if (container.data("fullValue")) {
                                itemSelected = _.find(dataSource, function (d) {
                                    return d[optionValue].toString() === itemSelected.toString();
                                });
                            }
                            var changingEvent = new CustomEvent("selectionChanging", {
                                detail: itemSelected,
                                bubbles: true,
                                cancelable: true,
                            });
                            container.data("chaninged", true);
                            document.getElementById(elementId).dispatchEvent(changingEvent);
                            if (changingEvent.returnValue !== undefined && changingEvent.returnValue === false) {
                                return false;
                            }
                        });
                        container.bind('selectionchanged', function () {
                            var itemSelected;
                            if (container.igGridSelection('option', 'multipleSelection')) {
                                var selected = container.ntsGridList('getSelected');
                                if (selected) {
                                    itemSelected = _.map(selected, function (s) { return s.id; });
                                }
                                else {
                                    itemSelected = [];
                                }
                            }
                            else {
                                var selected = container.ntsGridList('getSelected');
                                if (selected) {
                                    itemSelected = selected.id;
                                }
                                else {
                                    itemSelected = ('');
                                }
                            }
                            container.data("selected", itemSelected);
                            var isMultiOld = container.igGridSelection('option', 'multipleSelection');
                            if (container.data("fullValue")) {
                                var dataSource = container.igGrid('option', "dataSource");
                                if (isMultiOld) {
                                    itemSelected = _.filter(dataSource, function (d) {
                                        itemSelected.indexOf(d[optionValue].toString()) >= 0;
                                    });
                                }
                                else {
                                    itemSelected = _.find(dataSource, function (d) {
                                        return d[optionValue].toString() === itemSelected.toString();
                                    });
                                }
                            }
                            if (container.data("chaninged") !== true) {
                                var changingEvent = new CustomEvent("selectionChanging", {
                                    detail: itemSelected,
                                    bubbles: true,
                                    cancelable: true,
                                });
                                document.getElementById(container.attr('id')).dispatchEvent(changingEvent);
                                if (changingEvent.returnValue === undefined || !changingEvent.returnValue) {
                                    var oldSelected = container.data("selected");
                                    container.ntsGridList("setSelected", oldSelected);
                                    return false;
                                }
                            }
                            container.data("chaninged", false);
                            if (!_.isEqual(itemSelected, data.value())) {
                                data.value(itemSelected);
                            }
                        });
                        container.setupSearchScroll("igGrid", true);
                        container.data("multiple", isMultiSelect);
                        $("#" + gridId + "_container").find("#" + gridId + "_headers").closest("tr").hide();
                        $("#" + gridId + "_container").height($("#" + gridId + "_container").height() - gridHeaderHeight);
                    };
                    ListBoxBindingHandler.prototype.update = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var data = valueAccessor();
                        var options = ko.unwrap(data.options);
                        var optionValue = ko.unwrap(data.primaryKey === undefined ? data.optionsValue : data.primaryKey);
                        var optionText = ko.unwrap(data.primaryText === undefined ? data.optionsText : data.primaryText);
                        var selectedValue = ko.unwrap(data.value);
                        var isMultiSelect = ko.unwrap(data.multiple);
                        var enable = ko.unwrap(data.enable);
                        var columns = data.columns;
                        var rows = data.rows;
                        var container = $(element).find(".ntsListBox");
                        if (container.data("enable") !== enable) {
                            if (!enable) {
                                container.ntsGridList('unsetupSelecting');
                                container.addClass("disabled");
                            }
                            else {
                                container.ntsGridList('setupSelecting');
                                container.removeClass("disabled");
                            }
                        }
                        container.data("enable", enable);
                        var currentSource = container.igGrid('option', 'dataSource');
                        if (!_.isEqual(currentSource, options)) {
                            var currentSources = options.slice();
                            var observableColumns = _.filter(ko.unwrap(data.columns), function (c) {
                                c["key"] = c["key"] === undefined ? c["prop"] : c["key"];
                                return c["isDateColumn"] !== undefined && c["isDateColumn"] !== null && c["isDateColumn"] === true;
                            });
                            _.forEach(currentSources, function (s) {
                                _.forEach(observableColumns, function (c) {
                                    var key = c["key"] === undefined ? c["prop"] : c["key"];
                                    s[key] = moment(s[key]).format(c["format"]);
                                });
                            });
                            container.igGrid('option', 'dataSource', currentSources);
                            container.igGrid("dataBind");
                        }
                        var dataValue = data.value();
                        var isMultiOld = container.igGridSelection('option', 'multipleSelection');
                        if (container.data("fullValue")) {
                            if (isMultiOld) {
                                dataValue = _.map(dataValue, optionValue);
                            }
                            else {
                                dataValue = dataValue[optionValue];
                            }
                        }
                        if (isMultiOld !== isMultiSelect) {
                            container.igGridSelection('option', 'multipleSelection', isMultiSelect);
                            if (isMultiOld && !nts.uk.util.isNullOrUndefined(dataValue) && dataValue.length > 0) {
                                data.value(data.value()[0]);
                            }
                            else if (!isMultiOld && !nts.uk.util.isNullOrUndefined(dataValue)) {
                                data.value([data.value()]);
                            }
                            container.ntsGridList('setSelected', dataValue);
                        }
                        else {
                            var currentSelectedItems = container.ntsGridList('getSelected');
                            if (isMultiOld) {
                                if (currentSelectedItems) {
                                    currentSelectedItems = _.map(currentSelectedItems, function (s) { return s.id; });
                                }
                                else {
                                    currentSelectedItems = [];
                                }
                                if (dataValue) {
                                    dataValue = _.map(dataValue, function (s) { return s.toString(); });
                                }
                            }
                            else {
                                if (currentSelectedItems) {
                                    currentSelectedItems = currentSelectedItems.id;
                                }
                                else {
                                    currentSelectedItems = ('');
                                }
                                if (dataValue) {
                                    dataValue = dataValue.toString();
                                }
                            }
                            var isEqual = _.isEqual(currentSelectedItems, dataValue);
                            if (!isEqual) {
                                container.ntsGridList('setSelected', dataValue);
                            }
                        }
                        container.closest('.ui-iggrid').addClass('nts-gridlist').height(data.height);
                    };
                    return ListBoxBindingHandler;
                }());
                ko.bindingHandlers['ntsListBox'] = new ListBoxBindingHandler();
            })(koExtentions = ui_1.koExtentions || (ui_1.koExtentions = {}));
        })(ui = uk.ui || (uk.ui = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
//# sourceMappingURL=listbox-ko-ext.js.map