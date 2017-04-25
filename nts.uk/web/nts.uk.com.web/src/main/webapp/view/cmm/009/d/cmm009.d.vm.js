var cmm009;
(function (cmm009) {
    var d;
    (function (d) {
        var viewmodel;
        (function (viewmodel) {
            class ScreenModel {
                /**
                 * Init screen model.
                 */
                constructor() {
                    var self = this;
                    self.isEnable = ko.observable(true);
                    self.startDate = ko.observable("1900");
                    self.yearmonthdayeditor = {
                        option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
                            inputFormat: 'date'
                        })),
                    };
                    self.startDateFromA = ko.observable(null);
                    self.valueSel001 = ko.observable("");
                    self.startYmHis = ko.observable(null);
                    self.endDate = ko.observable(null);
                    var data = nts.uk.ui.windows.getShared('itemHist');
                    self.startDate(data.startDate);
                    self.startDateFromA(data.startDate);
                    self.endDate(data.endDate);
                    console.log(data);
                    debugger;
                    //---radio
                    self.itemsRadio = ko.observableArray([
                        { value: 1, text: ko.observable('履歴を削除する') },
                        { value: 2, text: ko.observable('履歴を修正する') }
                    ]);
                    self.isRadioCheck = ko.observable(2);
                    let index = data.index;
                    if (index != "0") {
                        self.isEnable(false);
                    }
                }
                createHistory() {
                    var self = this;
                    var inputYm = $('#INP_001').val();
                    //check YM
                    if (!nts.uk.time.parseYearMonthDate(inputYm).success) {
                        alert(nts.uk.time.parseYearMonthDate(inputYm).msg);
                        return false;
                    }
                    var startYMD = self.startDateFromA();
                    var endYMD = self.endDate();
                    var inputYm2 = inputYm.replace('/', '');
                    inputYm2 = inputYm2.replace('/', '');
                    startYMD = startYMD.replace('/', '');
                    startYMD = startYMD.replace('/', '');
                    endYMD = endYMD.replace('/', '');
                    endYMD = endYMD.replace('/', '');
                    if (+inputYm2 < +startYMD
                        || +inputYm2 == +startYMD
                        || +inputYm2 > +endYMD
                        || +inputYm2 == +endYMD) {
                        alert('履歴の期間が正しくありません。');
                        return false;
                    }
                    else {
                        var self = this;
                        var isRadio = self.isRadioCheck() + "";
                        nts.uk.ui.windows.setShared('newstartDate', self.startDate());
                        nts.uk.ui.windows.setShared('isradio', isRadio);
                        nts.uk.ui.windows.close();
                    }
                }
                start() {
                    var self = this;
                    var dfd = $.Deferred();
                    dfd.resolve();
                    // Return.
                    return dfd.promise();
                }
                closeDialog() {
                    nts.uk.ui.windows.close();
                }
            }
            viewmodel.ScreenModel = ScreenModel;
        })(viewmodel = d.viewmodel || (d.viewmodel = {}));
    })(d = cmm009.d || (cmm009.d = {}));
})(cmm009 || (cmm009 = {}));
