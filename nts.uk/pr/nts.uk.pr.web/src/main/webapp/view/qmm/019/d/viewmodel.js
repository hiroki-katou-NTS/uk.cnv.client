var qmm019;
(function (qmm019) {
    var d;
    (function (d) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                /**
                 * Init screen model.
                 */
                function ScreenModel() {
                    var self = this;
                    self.selectLayoutAtr = ko.observable("2");
                    self.itemList = ko.observableArray([]);
                    self.isEnable = ko.observable(true);
                    self.layouts = ko.observableArray([]);
                    self.selectStmtCode = ko.observable(null);
                    self.selectStmtName = ko.observable(null);
                    self.selectStartYm = ko.observable(null);
                    //sau nay gan lai
                    self.layoutSelect = ko.observable("1");
                }
                ScreenModel.prototype.start = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    //list data
                    self.buildItemList();
                    $('#LST_001').on('selectionChanging', function (event) {
                        console.log('Selecting value:' + event.originalEvent.detail);
                    });
                    $('#LST_001').on('selectionChanged', function (event) {
                        console.log('Selected value:' + event.originalEvent.detail);
                    });
                    //fill data to dialog
                    d.service.getLayoutWithMaxStartYm().done(function (layout) {
                        self.layouts(layout);
                        self.startDialog();
                    });
                    dfd.resolve();
                    // Return.
                    return dfd.promise();
                };
                ScreenModel.prototype.buildItemList = function () {
                    var self = this;
                    self.itemList.removeAll();
                    self.itemList.push(new ItemModel('0', 'レーザープリンタ', 'Ａ４', '縦向き', '1人', '最大　30行ｘ9別まで設定可能', ''));
                    self.itemList.push(new ItemModel('1', 'レーザープリンタ', 'Ａ４', '縦向き', '最大2人', '最大　17行ｘ9別まで設定可能', ''));
                    self.itemList.push(new ItemModel('2', 'レーザープリンタ', 'Ａ４', '縦向き', '最大3人', '最大　10行ｘ9別まで設定可能', ''));
                    self.itemList.push(new ItemModel('3', 'レーザープリンタ', 'Ａ４', '横向き', '最大2人', '最大　10行ｘ9別まで設定可能', ''));
                    self.itemList.push(new ItemModel('4', 'レーザー（圧着式）', 'Ａ４', '縦向き', '1人', '最大　17行ｘ9別まで設定可能', '圧着式：　Ｚ折り'));
                    self.itemList.push(new ItemModel('5', 'レーザー（圧着式）', 'Ａ４', '横向き', '1人', '支給、控除、勤怠各52項目', '圧着式：　はがき'));
                    self.itemList.push(new ItemModel('6', 'ドットプリンタ', '連続用紙', '―', '1人', '支給、控除、勤怠各27項目', ''));
                };
                ScreenModel.prototype.startDialog = function () {
                    var self = this;
                    _.forEach(self.layouts(), function (layout) {
                        var stmtCode = layout.stmtCode.trim();
                        if (stmtCode == self.layoutSelect()) {
                            self.selectStmtCode(stmtCode);
                            self.selectStmtName(layout.stmtName);
                            self.selectStartYm(nts.uk.text.formatYearMonth(layout.startYm + 1));
                            return false;
                        }
                    });
                };
                ScreenModel.prototype.createHistoryLayout = function () {
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            /**
         * Class Item model.
         */
            var ItemModel = (function () {
                function ItemModel(stt, printType, paperType, direction, numberPeople, numberDisplayItems, reference) {
                    this.stt = stt;
                    this.printType = printType;
                    this.paperType = paperType;
                    this.direction = direction;
                    this.numberPeople = numberPeople;
                    this.numberDisplayItems = numberDisplayItems;
                    this.reference = reference;
                }
                return ItemModel;
            }());
            viewmodel.ItemModel = ItemModel;
            var ItemCombobox = (function () {
                function ItemCombobox(layoutCode, layoutName) {
                    this.layoutCode = layoutCode;
                    this.layoutName = layoutName;
                }
                return ItemCombobox;
            }());
            viewmodel.ItemCombobox = ItemCombobox;
        })(viewmodel = d.viewmodel || (d.viewmodel = {}));
    })(d = qmm019.d || (qmm019.d = {}));
})(qmm019 || (qmm019 = {}));
