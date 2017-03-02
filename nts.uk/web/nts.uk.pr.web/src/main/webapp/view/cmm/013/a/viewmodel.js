var cmm013;
(function (cmm013) {
    var a;
    (function (a) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    var self = this;
                    self.label_002 = ko.observable(new Labels());
                    self.label_003 = ko.observable(new Labels());
                    self.label_004 = ko.observable(new Labels());
                    self.label_005 = ko.observable(new Labels());
                    self.label_006 = ko.observable(new Labels());
                    self.radiobox_2 = ko.observable(new RadioBox2());
                    self.inp_002 = ko.observable(null);
                    self.inp_002_enable = ko.observable(false);
                    self.inp_003 = ko.observable(null);
                    self.inp_004 = ko.observable(new TextEditor_3());
                    self.inp_005 = ko.observable(null);
                    self.sel_0021 = ko.observable(new SwitchButton);
                    self.sel_0022 = ko.observable(new SwitchButton);
                    self.sel_0023 = ko.observable(new SwitchButton);
                    self.sel_0024 = ko.observable(new SwitchButton);
                    self.test = ko.observable('2');
                    //lst_001
                    self.listbox = ko.observableArray([]);
                    self.selectedCode = ko.observable('');
                    self.isEnable = ko.observable(true);
                    self.itemHist = ko.observable(null);
                    self.itemName = ko.observable('');
                    self.index_selected = ko.observable('');
                    //lst_002
                    self.dataSource = ko.observableArray([]);
                    self.currentItem = ko.observable(null);
                    self.itemName = ko.observable('');
                    self.currentCode = ko.observable();
                    self.currentCodeList = ko.observableArray([]);
                    self.columns = ko.observableArray([
                        { headerText: 'コード', key: 'jobCode', width: 80 },
                        { headerText: '名称', key: 'jobName', width: 100 }
                    ]);
                    //A_SEL_001
                    self.itemList = ko.observableArray([
                        new BoxModel(0, '全員参照可能'),
                        new BoxModel(1, '全員参照不可'),
                        new BoxModel(2, 'ロール毎に設定')
                    ]);
                    self.selectedId = ko.observable();
                    self.enable = ko.observable(true);
                    self.selectedCode.subscribe((function (codeChanged) {
                        self.itemHist(self.findHist(codeChanged));
                        if (self.itemHist() != null) {
                            self.index_selected(self.itemHist().historyId);
                            console.log(self.index_selected());
                            //get position by historyId
                            var dfd = $.Deferred();
                            a.service.findAllPosition(self.index_selected())
                                .done(function (position_arr) {
                                self.dataSource(position_arr);
                                self.currentCode(self.dataSource()[0].jobCode);
                                self.inp_002(self.dataSource()[0].jobCode);
                                self.inp_003(self.dataSource()[0].jobName);
                                self.inp_005(self.dataSource()[0].memo);
                                self.selectedId(self.dataSource()[0].presenceCheckScopeSet);
                            }).fail(function (error) {
                                alert(error.message);
                            });
                            dfd.resolve();
                            return dfd.promise();
                        }
                    }));
                    //inp_x - get detail position
                    self.currentCode.subscribe((function (codeChanged) {
                        self.currentItem(self.findPosition(codeChanged));
                        if (self.currentItem() != null) {
                            self.inp_002(self.currentItem().jobCode);
                            self.inp_003(self.currentItem().jobName);
                            self.inp_005(self.currentItem().memo);
                            self.selectedId(self.currentItem().presenceCheckScopeSet);
                        }
                    }));
                }
                //start
                ScreenModel.prototype.startPage = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    // get all history.     
                    a.service.getAllHistory().done(function (history_arr) {
                        self.listbox(history_arr);
                        self.selectedCode(self.listbox()[0].startDate);
                    }).fail(function (error) {
                        alert(error.message);
                    });
                    dfd.resolve();
                    return dfd.promise();
                };
                //        find history need to show position
                ScreenModel.prototype.findHist = function (value) {
                    var self = this;
                    var itemModel = null;
                    _.find(self.listbox(), function (obj) {
                        if (obj.startDate == value) {
                            itemModel = obj;
                        }
                    });
                    return itemModel;
                };
                //        find position need to show detail
                ScreenModel.prototype.findPosition = function (value) {
                    var self = this;
                    var itemModel = null;
                    _.find(self.dataSource(), function (obj) {
                        if (obj.jobCode == value) {
                            itemModel = obj;
                        }
                    });
                    return itemModel;
                };
                //delete position is selected
                ScreenModel.prototype.deletePosition = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    var item = new a.service.model.DeletePositionCommand(self.currentItem().jobCode, self.currentItem().historyId);
                    console.log(self.currentItem().presenceCheckScopeSet);
                    self.index_of_itemDelete = self.dataSource().indexOf(self.currentItem());
                    console.log(self.index_of_itemDelete);
                    a.service.deletePosition(item).done(function (res) {
                        self.getPositionList_aftefDelete();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                };
                ScreenModel.prototype.getPositionList_aftefDelete = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    a.service.findAllPosition(self.index_selected()).done(function (position_arr) {
                        self.dataSource(position_arr);
                        if (self.dataSource().length > 0) {
                            if (self.index_of_itemDelete === self.dataSource().length) {
                                self.currentCode(self.dataSource()[self.index_of_itemDelete - 1].jobCode);
                                self.inp_002(self.dataSource()[self.index_of_itemDelete - 1].jobCode);
                                self.inp_003(self.dataSource()[self.index_of_itemDelete - 1].jobName);
                                self.inp_005(self.dataSource()[self.index_of_itemDelete - 1].memo);
                            }
                            else {
                                self.currentCode(self.dataSource()[self.index_of_itemDelete].jobCode);
                                self.inp_002(self.dataSource()[self.index_of_itemDelete].jobCode);
                                self.inp_003(self.dataSource()[self.index_of_itemDelete].jobName);
                                self.inp_005(self.dataSource()[self.index_of_itemDelete].memo);
                            }
                        }
                        else {
                        }
                        dfd.resolve();
                    }).fail(function (error) {
                        alert(error.message);
                    });
                    dfd.resolve();
                    return dfd.promise();
                };
                //get position 
                ScreenModel.prototype.getPositionList = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    a.service.findAllPosition(self.index_selected()).done(function (position_arr) {
                        self.dataSource(position_arr);
                        self.inp_002(self.dataSource()[0].jobCode);
                        self.inp_003(self.dataSource()[0].jobName);
                        self.inp_005(self.dataSource()[0].memo);
                        if (self.dataSource().length > 1) {
                            self.currentCode(self.dataSource()[0].jobCode);
                        }
                        dfd.resolve();
                    }).fail(function (error) {
                        alert(error.message);
                    });
                    dfd.resolve();
                    return dfd.promise();
                };
                ScreenModel.prototype.openBDialog = function () {
                    nts.uk.ui.windows.sub.modal('/view/cmmhoa/013/b/index.xhtml', { title: '画面ID：B', });
                };
                ScreenModel.prototype.openCDialog = function () {
                    nts.uk.ui.windows.sub.modal('/view/cmmhoa/013/c/index.xhtml', { title: '画面ID：c', });
                };
                ScreenModel.prototype.openDDialog = function () {
                    nts.uk.ui.windows.sub.modal('/view/cmmhoa/013/d/index.xhtml', { title: '画面ID：D', });
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var Labels = (function () {
                function Labels() {
                    this.constraint = 'LayoutCode';
                    var self = this;
                    self.inline = ko.observable(true);
                    self.required = ko.observable(true);
                    self.enable = ko.observable(true);
                }
                return Labels;
            }());
            viewmodel.Labels = Labels;
            var RadioBox2 = (function () {
                function RadioBox2() {
                    var self = this;
                    self.itemList = ko.observableArray([
                        new BoxModel(1, '対象外'),
                        new BoxModel(2, '看護師'),
                        new BoxModel(3, '準看護師'),
                        new BoxModel(4, '看護補助師')
                    ]);
                    self.selectedId = ko.observable(1);
                    self.enable = ko.observable(true);
                }
                return RadioBox2;
            }());
            var BoxModel = (function () {
                function BoxModel(id, name) {
                    var self = this;
                    self.id = id;
                    self.name = name;
                }
                return BoxModel;
            }());
            viewmodel.BoxModel = BoxModel;
            var TextEditor_3 = (function () {
                function TextEditor_3() {
                    var self = this;
                    self.texteditor = {
                        value: ko.observable(''),
                        constraint: 'ResidenceCode',
                        option: ko.mapping.fromJS(new nts.uk.ui.option.TextEditorOption({
                            textmode: "text",
                            placeholder: "1",
                            width: "10px",
                            textalign: "center"
                        })),
                        required: ko.observable(true),
                        enable: ko.observable(false),
                        readonly: ko.observable(false)
                    };
                }
                return TextEditor_3;
            }());
            viewmodel.TextEditor_3 = TextEditor_3;
            var SwitchButton = (function () {
                function SwitchButton() {
                    var self = this;
                    self.roundingRules = ko.observableArray([
                        { code: '1', name: '可能' },
                        { code: '2', name: '不可' },
                    ]);
                    self.selectedRuleCode = ko.observable(1);
                }
                return SwitchButton;
            }());
            viewmodel.SwitchButton = SwitchButton;
        })(viewmodel = a.viewmodel || (a.viewmodel = {}));
    })(a = cmm013.a || (cmm013.a = {}));
})(cmm013 || (cmm013 = {}));
