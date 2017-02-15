var cmm014;
(function (cmm014) {
    var a;
    (function (a) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    var self = this;
                    self.dataSource = ko.observableArray([
                        new viewmodel.model.ClassificationDto('A000001', 'マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000002', '2マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000003', '3マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000004', '4マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000005', '5マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000006', '6マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000007', '7マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000008', '8マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000009', '9マスタ修正ログ'),
                        new viewmodel.model.ClassificationDto('A000010', '10マスタ修正ログ')
                    ]);
                    self.columns = ko.observableArray([
                        { headerText: 'コード', prop: 'classificationCode', width: 100 },
                        { headerText: '名称', prop: 'classificationName', width: 80 }
                    ]);
                    self.currentCode = ko.observable(self.dataSource()[0].classificationCode);
                    self.currentCodeList = ko.observableArray([]);
                    self.currentItem = ko.observable("");
                    self.INP_002_code = ko.observable(self.dataSource()[0].classificationCode);
                    self.INP_002_enable = ko.observable(false);
                    self.INP_003_name = ko.observable(self.dataSource()[0].classificationName);
                    self.INP_004_notes = ko.observable('');
                    self.currentCode.subscribe((function (codeChanged) {
                        self.currentItem(self.findObj(codeChanged));
                        if (self.currentItem() != null) {
                            self.INP_002_code(self.currentItem().classificationCode);
                            self.INP_003_name(self.currentItem().classificationName);
                        }
                    }));
                }
                ScreenModel.prototype.findObj = function (value) {
                    var self = this;
                    var itemModel = null;
                    _.find(self.dataSource(), function (obj) {
                        if (obj.classificationCode == value) {
                            itemModel = obj;
                        }
                    });
                    return itemModel;
                };
                ScreenModel.prototype.initRegisterClassification = function () {
                    var self = this;
                    self.INP_002_enable(true);
                    self.INP_002_code("");
                    self.INP_003_name("");
                    self.currentCode(null);
                };
                ScreenModel.prototype.checkInput = function () {
                    var self = this;
                    if (self.INP_002_code() == '' || self.INP_003_name() == '') {
                        console.log("input is null");
                        return false;
                    }
                    else {
                        return true;
                    }
                };
                ScreenModel.prototype.RegisterClassification = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    if (self.checkInput()) {
                        console.log("Insert(đăng ký mới) or Update Classification");
                        for (var i = 0; i < self.dataSource().length; i++) {
                            if (self.INP_002_code() == self.dataSource()[i].classificationCode) {
                                var classification_old = self.dataSource()[i];
                                var classification_update = new viewmodel.model.ClassificationDto(self.INP_002_code(), self.INP_003_name());
                                classification_update.classificationMemo = self.INP_004_notes();
                                a.service.updateClassification(classification_update).done(function () {
                                    a.service.getAllClassification().done(function (classification_arr) {
                                        self.dataSource(classification_arr);
                                        dfd.resolve();
                                    }).fail(function (res) {
                                        dfd.reject(res);
                                    });
                                    dfd.resolve();
                                }).fail(function (res) {
                                    dfd.reject(res);
                                });
                                //                        self.dataSource.replace(classification_old, classification_update);
                                //                        console.log("update");
                                //                        console.log(classification_update);
                                break;
                            }
                            else if (self.INP_002_code() != self.dataSource()[i].classificationCode && i == self.dataSource().length - 1) {
                                var classification_new = new viewmodel.model.ClassificationDto(self.INP_002_code(), self.INP_003_name());
                                classification_new.classificationMemo = self.INP_004_notes();
                                a.service.addClassification(classification_new).done(function () {
                                    a.service.getAllClassification().done(function (classification_arr) {
                                        self.dataSource(classification_arr);
                                        dfd.resolve();
                                    }).fail(function (res) {
                                        dfd.reject(res);
                                    });
                                    dfd.resolve();
                                }).fail(function (res) {
                                    dfd.reject(res);
                                });
                                //                        self.dataSource.push(classification_new);
                                //                        console.log("dang ky moi");
                                //                        console.log(classification_new);
                                break;
                            }
                        }
                    }
                };
                ScreenModel.prototype.DeleteClassification = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    var item = new model.RemoveClassificationCommand(self.currentItem().classificationCode);
                    a.service.removeClassification(item).done(function () {
                        a.service.getAllClassification().done(function (classification_arr) {
                            self.dataSource(classification_arr);
                            dfd.resolve();
                        }).fail(function (res) {
                            dfd.reject(res);
                        });
                        //   self.dataSource(classification_arr);
                        //  dfd.resolve();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                };
                //   self.dataSource.remove(self.currentItem());
                ScreenModel.prototype.start = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    a.service.getAllClassification().done(function (classification_arr) {
                        self.dataSource(classification_arr);
                        dfd.resolve();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                    dfd.resolve();
                    return dfd.promise();
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var model;
            (function (model) {
                var ClassificationDto = (function () {
                    function ClassificationDto(classificationCode, classificationName) {
                        this.classificationCode = classificationCode;
                        this.classificationName = classificationName;
                    }
                    return ClassificationDto;
                }());
                model.ClassificationDto = ClassificationDto;
                var RemoveClassificationCommand = (function () {
                    function RemoveClassificationCommand(classificationCode) {
                        this.classificationCode = classificationCode;
                    }
                    return RemoveClassificationCommand;
                }());
                model.RemoveClassificationCommand = RemoveClassificationCommand;
            })(model = viewmodel.model || (viewmodel.model = {}));
        })(viewmodel = a.viewmodel || (a.viewmodel = {}));
    })(a = cmm014.a || (cmm014.a = {}));
})(cmm014 || (cmm014 = {}));
