var qmm034;
(function (qmm034) {
    var a;
    (function (a) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    this.countStartDateChange = 1;
                    var self = this;
                    self.init();
                    self.date = ko.observable("");
                    self.startDate = ko.observable(null);
                    self.startDate.subscribe(function (dateChange) {
                        if (self.countStartDateChange === 1) {
                            // code vao duoi day
                            $("#A_INP_003").ntsError('clear');
                            a.service.checkStartDate(dateChange).done(function (result) {
                                if (!result) {
                                    $("#A_INP_003").ntsError('set', "Invalid date.");
                                }
                            });
                        }
                        else {
                            self.countStartDateChange = 1;
                        }
                    });
                    self.currentCode.subscribe(function (codeChanged) {
                        if (nts.uk.text.isNullOrEmpty(codeChanged)) {
                            self.refreshLayout();
                        }
                        else {
                            self.countStartDateChange += 1;
                            self.currentEra(self.getEra(codeChanged));
                            self.date(self.currentEra().startDate().toString());
                            self.startDate(self.currentEra().startDate());
                        }
                        self.isDeleteEnable(true);
                        self.isEnableCode(false);
                        self.isUpdate(true);
                        //                self.dirty = new nts.uk.ui.DirtyChecker(self.currentEra);
                        //                if (self.dirty.isDirty()) {
                        //                    if (self.dirty.isDirty()) {
                        //                        alert("Data is changed.");
                        //                    } else {
                        //                        alert("Data isn't changed.");
                        //                    }
                        //
                        //                }
                    });
                    //convert to Japan Emprise year
                    self.dateTime = ko.observable(nts.uk.time.yearInJapanEmpire(self.currentEra().startDate()).toString());
                    //            self.date.subscribe(function(valueChange) {
                    //                if (nts.uk.text.isNullOrEmpty(valueChange)) {
                    //                    return;
                    //                }
                    //                qmm034.a.service.checkStartDate(valueChange).done(function(data: boolean) {
                    //                    if (data) {
                    //                        $("#A_INP_003").ntsError('clear');
                    //                        return;
                    //                    }
                    //           $("#A_INP_003").ntsError('set', "StartDate is not lastest.");
                    //                });
                    //            });
                }
                ScreenModel.prototype.init = function () {
                    var self = this;
                    self.items = ko.observableArray([]);
                    self.columns = ko.observableArray([
                        { headerText: '元号', key: 'eraName', width: 50 },
                        { headerText: '記号', key: 'eraMark', width: 50 },
                        { headerText: '開始年月日', key: 'startDate', width: 80 },
                    ]);
                    self.currentEra = ko.observable((new EraModel('大明', 'S', new Date("1926/12/25"), 1, '95F5047A-5065-4306-A6B7-184AA676A1DE', new Date("1929/12/25"))));
                    self.currentCode = ko.observable(null);
                    self.date = ko.observable('');
                    self.dateTime = ko.observable('');
                    self.isDeleteEnable = ko.observable(false);
                    self.isEnableCode = ko.observable(false);
                    self.isUpdate = ko.observable(false);
                };
                ScreenModel.prototype.insertData = function () {
                    var self = this;
                    var eraName;
                    eraName = $('#A_INP_001').val();
                    var eraMark;
                    eraMark = $('#A_INP_002').val();
                    var startDate = self.currentEra().startDate();
                    var endDate;
                    var eraHist = self.currentEra().eraHist();
                    var fixAttribute;
                    var dfd = $.Deferred();
                    var node;
                    node = new qmm034.a.service.model.EraDto(eraName, eraMark, startDate, endDate, fixAttribute, eraHist);
                    if (node.eraName == "") {
                        $("#A_INP_001").ntsError("set", "the era name must require");
                        return false;
                    }
                    if (node.eraMark == "") {
                        $("#A_INP_002").ntsError("set", "the era mark must require");
                        return false;
                    }
                    qmm034.a.service.addData(self.isUpdate(), node).done(function (result) {
                        self.reload().done(function () {
                            self.currentCode(eraName);
                            dfd.resolve();
                            self.isDeleteEnable = ko.observable(false);
                            self.isEnableCode = ko.observable(false);
                            self.isUpdate = ko.observable(true);
                            var lastStartDate = _.maxBy(self.items(), function (o) {
                                return o.startDate;
                                //console.log(startDate);
                            });
                        });
                        //                if (nts.uk.ui._viewModel.errors.isEmpty()) {
                        //                    $("#A_INP_003").ntsError('clear');
                        //                }
                    }).fail(function (res) {
                        //alert(res.message);
                        $("#A_INP_003").ntsError("set", res.message);
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.alertDelete = function () {
                    var self = this;
                    if (confirm("do you wanna delete") === true) {
                        self.deleteData();
                    }
                    else {
                        alert("you didnt delete!");
                    }
                };
                ScreenModel.prototype.reload = function () {
                    var dfd = $.Deferred();
                    var self = this;
                    $.when(qmm034.a.service.getAllEras()).done(function (data) {
                        if (data.length > 0) {
                            self.items(data);
                            self.date(self.currentEra().startDate().toString());
                            self.currentCode(self.currentEra().eraName());
                            self.isDeleteEnable(true);
                        }
                        dfd.resolve(data);
                    }).fail(function (res) {
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.deleteData = function () {
                    var self = this;
                    var eraName;
                    eraName = $('#A_INP_001').val();
                    var eraMark;
                    eraMark = $('#A_INP_002').val();
                    var eraHist = self.currentEra().eraHist();
                    var startDate = self.currentEra().startDate();
                    var dfd = $.Deferred();
                    var node;
                    node = new qmm034.a.service.model.EraDtoDelete(eraHist);
                    var rowIndex = _.findIndex(self.items(), function (item) {
                        return item.eraName == self.currentEra().eraName();
                    });
                    qmm034.a.service.deleteData(node).done(function (result) {
                        self.reload().done(function (data) {
                            self.items(data);
                            if (self.items().length === 0) {
                                self.isUpdate(false);
                                self.refreshLayout();
                            }
                            else if (self.items().length === rowIndex) {
                                self.currentCode(self.items()[rowIndex - 1].eraName);
                            }
                            else {
                                self.currentCode(self.items()[rowIndex].eraName);
                            }
                        });
                        dfd.resolve();
                    }).fail(function (error) {
                        alert(error.message);
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.getEra = function (codeNew) {
                    var self = this;
                    var era = _.find(self.items(), function (item) {
                        return item.eraName === codeNew;
                    });
                    // let startDate = new Date(era.startDate.substring(0, 10));
                    //let endDate = new Date(era.endDate.substring(0, 10));
                    if (era) {
                        return new EraModel(era.eraName, era.eraMark, new Date(era.startDate), era.fixAttribute, era.eraHist, new Date(era.endDate));
                    }
                    else {
                        return new EraModel("", "", new Date(), 0, "", new Date());
                    }
                };
                ScreenModel.prototype.startPage = function () {
                    var self = this;
                    // Page load dfd.
                    var dfd = $.Deferred();
                    // Resolve start page dfd after load all data.
                    $.when(qmm034.a.service.getAllEras()).done(function (data) {
                        if (data.length > 0) {
                            self.items(data);
                            self.currentEra(self.items()[0]);
                            self.dirty = new nts.uk.ui.DirtyChecker(self.currentEra);
                            self.date(new Date(self.currentEra().startDate.toString()));
                            self.currentCode(self.currentEra().eraName);
                            self.isUpdate(false);
                            self.isDeleteEnable(true);
                            self.isEnableCode(false);
                        }
                        else {
                            self.refreshLayout();
                        }
                        dfd.resolve();
                    }).fail(function (res) {
                        $("#A_INP_001").ntsError("set", res.message);
                        //alert(res.message);
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.refreshLayout = function () {
                    var self = this;
                    //            if (self.dirty.isDirty()) {
                    //                alert("Data is changed.");
                    //            } else {
                    //                alert("Data isn't changed.");
                    //            }
                    self.currentCode('');
                    self.currentEra(new EraModel('', '', new Date("2017/03/21"), 1, '95F5047A-5065-4306-A6B7-184AA676A1DE', new Date("")));
                    self.isDeleteEnable(false);
                    self.isEnableCode(true);
                    self.isUpdate(false);
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var EraModel = (function () {
                function EraModel(eraName, eraMark, startDate, fixAttribute, eraHist, endDate) {
                    this.eraName = ko.observable(eraName);
                    this.eraMark = ko.observable(eraMark);
                    this.startDate = ko.observable(startDate);
                    this.endDate = ko.observable(endDate);
                    this.fixAttribute = ko.observable(fixAttribute);
                    this.eraHist = ko.observable(eraHist);
                }
                return EraModel;
            }());
            viewmodel.EraModel = EraModel;
        })(viewmodel = a.viewmodel || (a.viewmodel = {}));
    })(a = qmm034.a || (qmm034.a = {}));
})(qmm034 || (qmm034 = {}));
