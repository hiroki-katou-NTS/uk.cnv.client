var qpp021;
(function (qpp021) {
    var d;
    (function (d) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    var self = this;
                    self.zeroItemSetting = ko.observableArray([
                        new ItemModel(1, "項目名の登録の設定を優先する"),
                        new ItemModel(2, "個別にッ設定する")
                    ]);
                    self.zeroItemSettingCode = ko.observable(2);
                    self.zeroAmountOutput = ko.observableArray([
                        new ItemModel(1, "する"),
                        new ItemModel(2, "しない")
                    ]);
                    self.zeroAmountOutputCode = ko.observable(2);
                    self.zeroTimeClassification = ko.observableArray([
                        new ItemModel(1, "する"),
                        new ItemModel(2, "しない")
                    ]);
                    self.zeroTimeClassificationCode = ko.observable(1);
                    self.selectPrintYearMonth = ko.observableArray([
                        new ItemModel(1, "現在処理年月の2ヶ月前"),
                        new ItemModel(2, "現在処理年月の1か月前"),
                        new ItemModel(3, "現在処理年月"),
                        new ItemModel(4, "現在処理年月の翌月"),
                        new ItemModel(5, "現在処理年月の2ヶ月後")
                    ]);
                    self.selectPrintYearMonthCode = ko.observable(3);
                }
                ScreenModel.prototype.startPage = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    dfd.resolve();
                    return dfd.promise();
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var RadioBoxGroupModel = (function () {
                function RadioBoxGroupModel(rbCode, rbName) {
                    this.rbCode = rbCode;
                    this.rbName = rbName;
                }
                return RadioBoxGroupModel;
            }());
            var SwitchButtonModel = (function () {
                function SwitchButtonModel(sbCode, sbName) {
                    this.sbCode = sbCode;
                    this.sbName = sbName;
                }
                return SwitchButtonModel;
            }());
            var ItemModel = (function () {
                function ItemModel(code, name) {
                    this.code = code;
                    this.name = name;
                }
                return ItemModel;
            }());
        })(viewmodel = d.viewmodel || (d.viewmodel = {}));
    })(d = qpp021.d || (qpp021.d = {}));
})(qpp021 || (qpp021 = {}));
//# sourceMappingURL=qpp021.d.vm.js.map