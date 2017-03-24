var qmm006;
(function (qmm006) {
    var a;
    (function (a) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    var self = this;
                    self.items = ko.observableArray([]);
                    self.currentCode = ko.observable('');
                    self.dataSource = ko.observableArray([]);
                    self.dataSource2 = ko.observableArray([]);
                    self.columns = ko.observableArray([
                        { headerText: 'コード', key: 'lineBankCode', width: 45, formatter: _.escape },
                        { headerText: '名称', key: 'lineBankName', width: 120, formatter: _.escape },
                        { headerText: '口座区分', key: 'accountAtr', width: 110, formatter: _.escape },
                        { headerText: '口座番号', key: 'accountNo', width: 100, formatter: _.escape }
                    ]);
                    self.currentLineBank = ko.observable(new LineBank(null, null, null, null, 0, null, null, null, []));
                    self.dirty = new nts.uk.ui.DirtyChecker(self.currentLineBank);
                    self.roundingRules = ko.observableArray([
                        { code: '0', name: '普通' },
                        { code: '1', name: '当座' },
                    ]);
                    self.isFirstFindAll = ko.observable(true);
                    self.isDeleteEnable = ko.observable(true);
                    self.isEnable = ko.observable(false);
                    self.notLoopAlert = ko.observable(true);
                    self.enableBtn003 = ko.observable(false);
                    self.bankName = ko.observable('');
                    self.branchName = ko.observable('');
                    self.countLineBank = ko.observable(0);
                    self.indexLineBank = ko.observable(0);
                    self.messageList = ko.observableArray([
                        { messageId: "ER001", message: "＊が入力されていません。" },
                        { messageId: "ER005", message: "入力した＊は既に存在しています。\r\n ＊を確認してください。" },
                        { messageId: "ER007", message: "＊が選択されていません。" },
                        { messageId: "ER008", message: "選択された＊は使用されているため削除できません。" },
                        { messageId: "ER010", message: "対象データがありません。" },
                    ]);
                    self.currentCode.subscribe(function (codeChange) {
                        if (!self.isFirstFindAll()) {
                            self.clearError();
                        }
                        if (!self.notLoopAlert()) {
                            self.notLoopAlert(true);
                            return;
                        }
                        if (codeChange == null) {
                            self.setCurrentLineBank(codeChange);
                            return;
                        }
                        if (self.dirty.isDirty()) {
                            nts.uk.ui.dialog.confirm("変更された内容が登録されていません。\r\n よろしいですか。").ifYes(function () {
                                self.setCurrentLineBank(codeChange);
                            }).ifNo(function () {
                                self.notLoopAlert(false);
                                self.currentCode(self.currentLineBank().lineBankCode());
                            });
                        }
                        else {
                            self.setCurrentLineBank(codeChange);
                        }
                    });
                }
                ScreenModel.prototype.startPage = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    self.findAll().done(function () {
                        dfd.resolve();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.setCurrentLineBank = function (codeChange) {
                    var self = this;
                    var lineBank = self.getLineBank(codeChange);
                    self.getNameForBankAndBranch(lineBank);
                    self.currentLineBank(lineBank);
                    self.dirty = new nts.uk.ui.DirtyChecker(self.currentLineBank);
                    self.isDeleteEnable(true);
                    self.isEnable(false);
                    self.indexLineBank(_.findIndex(self.items(), function (x) {
                        return x.lineBankCode === codeChange;
                    }));
                    if (codeChange == null) {
                        self.isDeleteEnable(false);
                        self.isEnable(true);
                    }
                };
                ScreenModel.prototype.findAll = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    qmm006.a.service.findAll()
                        .done(function (data) {
                        if (data.length > 0) {
                            if (data.length > 1) {
                                self.enableBtn003(true);
                            }
                            else {
                                self.enableBtn003(false);
                            }
                            self.items(data);
                            self.countLineBank(data.length);
                            if (self.isFirstFindAll()) {
                                self.dirty = new nts.uk.ui.DirtyChecker(self.currentLineBank);
                                self.currentCode(data[0].lineBankCode);
                                self.isFirstFindAll(false);
                            }
                        }
                        else {
                            self.items([]);
                            self.clearForm();
                        }
                        dfd.resolve();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                    return dfd.promise();
                };
                ScreenModel.prototype.saveData = function () {
                    var self = this;
                    var command = {
                        accountAtr: self.currentLineBank().accountAtr(),
                        accountNo: self.currentLineBank().accountNo(),
                        bankCode: self.currentLineBank().bankCode(),
                        branchCode: self.currentLineBank().branchCode(),
                        consignor: [
                            { "code": self.currentLineBank().consignors()[0].consignorCode(), "memo": self.currentLineBank().consignors()[0].consignorMemo() },
                            { "code": self.currentLineBank().consignors()[1].consignorCode(), "memo": self.currentLineBank().consignors()[1].consignorMemo() },
                            { "code": self.currentLineBank().consignors()[2].consignorCode(), "memo": self.currentLineBank().consignors()[2].consignorMemo() },
                            { "code": self.currentLineBank().consignors()[3].consignorCode(), "memo": self.currentLineBank().consignors()[3].consignorMemo() },
                            { "code": self.currentLineBank().consignors()[4].consignorCode(), "memo": self.currentLineBank().consignors()[4].consignorMemo() },
                        ],
                        lineBankCode: self.currentLineBank().lineBankCode(),
                        lineBankName: self.currentLineBank().lineBankName(),
                        memo: self.currentLineBank().memo(),
                        requesterName: self.currentLineBank().requesterName()
                    };
                    qmm006.a.service.saveData(self.isEnable(), command)
                        .done(function () {
                        $.when(self.findAll()).done(function () {
                            self.dirty = new nts.uk.ui.DirtyChecker(self.currentLineBank);
                            self.currentCode(command.lineBankCode);
                        });
                    })
                        .fail(function (error) {
                        if (error.messageId == self.messageList()[0].messageId) {
                            var message = self.messageList()[0].message;
                            if (!command.lineBankCode) {
                                $('#A_INP_001').ntsError('set', message);
                            }
                            if (!command.lineBankName) {
                                $('#A_INP_002').ntsError('set', message);
                            }
                            if (!command.accountNo) {
                                $('#A_INP_003').ntsError('set', message);
                            }
                        }
                        else if (error.messageId == self.messageList()[2].messageId) {
                            var message = self.messageList()[2].message;
                            if (!command.bankCode) {
                                $('#A_LBL_004').ntsError('set', message);
                            }
                            if (!command.branchCode) {
                                $('#A_LBL_007').ntsError('set', message);
                            }
                        }
                        else if (error.messageId == self.messageList()[1].messageId) {
                            var message = self.messageList()[1].message;
                            $('#A_INP_001').ntsError('set', message);
                        }
                    });
                };
                ScreenModel.prototype.remove = function () {
                    var self = this;
                    nts.uk.ui.dialog.confirm("データを削除します。\r\nよろしいですか？")
                        .ifYes(function () {
                        var command = {
                            lineBankCode: self.currentLineBank().lineBankCode(),
                        };
                        qmm006.a.service.remove(command)
                            .done(function () {
                            self.findAll().done(function () {
                                if (self.countLineBank() > self.indexLineBank() && self.indexLineBank() != -1) {
                                    self.currentCode(self.items()[self.indexLineBank()].lineBankCode);
                                }
                                else if (self.countLineBank() == self.indexLineBank()) {
                                    self.currentCode(self.items()[self.indexLineBank() - 1].lineBankCode);
                                }
                                else if (self.indexLineBank() == -1) {
                                    self.clearForm();
                                }
                            });
                        }).fail(function (error) {
                            nts.uk.ui.dialog.alert(self.messageList()[3].message);
                        });
                    })
                        .ifNo(function () {
                    });
                };
                ScreenModel.prototype.openBDialog = function () {
                    var self = this;
                    qmm006.a.service.checkExistBankAndBranch()
                        .done(function () {
                        var lineBank = self.currentLineBank();
                        nts.uk.ui.windows.sub.modal("/view/qmm/006/b/index.xhtml", { title: "銀行情報一覧", dialogClass: "no-close" }).onClosed(function () {
                            if (nts.uk.ui.windows.getShared("selectedBank") != null) {
                                lineBank.branchCode(nts.uk.ui.windows.getShared("selectedBank").code);
                                self.bankName(nts.uk.ui.windows.getShared("selectedBank").parentName);
                                lineBank.bankCode(nts.uk.ui.windows.getShared("selectedBank").parentCode);
                                self.branchName(nts.uk.ui.windows.getShared("selectedBank").name);
                                $('#A_LBL_004').ntsError('clear');
                                $('#A_LBL_007').ntsError('clear');
                            }
                        });
                    })
                        .fail(function (error) {
                        nts.uk.ui.dialog.alert(self.messageList()[4].message);
                    });
                };
                ScreenModel.prototype.openCDialog = function () {
                    var self = this;
                    if (self.items().length > 1) {
                        if (self.dirty.isDirty()) {
                            nts.uk.ui.dialog.confirm("変更された内容が登録されていません。\r\n よろしいですか。").ifYes(function () {
                                self.afterCloseCDialog();
                            }).ifNo(function () {
                            });
                        }
                        else {
                            self.afterCloseCDialog();
                        }
                    }
                    else {
                        nts.uk.ui.dialog.alert(self.messageList()[4].message);
                        self.enableBtn003(false);
                    }
                };
                ScreenModel.prototype.afterCloseCDialog = function () {
                    var self = this;
                    nts.uk.ui.windows.sub.modal("/view/qmm/006/c/index.xhtml", { title: "振込元銀行の登録　＞　振込元銀行", dialogClass: "no-close" }).onClosed(function () {
                        self.findAll().done(function () {
                            if (nts.uk.ui.windows.getShared("currentCode")) {
                                self.currentCode(nts.uk.ui.windows.getShared("currentCode"));
                            }
                        });
                    });
                };
                ScreenModel.prototype.btn007 = function () {
                };
                ScreenModel.prototype.jumpToQmm002A = function () {
                    var self = this;
                    if (self.dirty.isDirty()) {
                        nts.uk.ui.dialog.confirm("変更された内容が登録されていません。\r\n よろしいですか。").ifYes(function () {
                            nts.uk.request.jump("/view/qmm/002/a/index.xhtml");
                        }).ifNo(function () {
                        });
                    }
                    else {
                        nts.uk.request.jump("/view/qmm/002/a/index.xhtml");
                    }
                };
                ScreenModel.prototype.clearForm = function () {
                    var self = this;
                    if (self.dirty.isDirty()) {
                        nts.uk.ui.dialog.confirm("変更された内容が登録されていません。\r\n よろしいですか。")
                            .ifYes(function () {
                            if (self.currentCode(null)) {
                                self.setCurrentLineBank(null);
                            }
                            else {
                                self.currentCode(null);
                            }
                        })
                            .ifNo(function () { });
                    }
                    else {
                        self.currentCode(null);
                    }
                };
                ScreenModel.prototype.clearError = function () {
                    $('#A_INP_001').ntsError('clear');
                    $('#A_INP_002').ntsError('clear');
                    $('#A_INP_003').ntsError('clear');
                    $('#A_LBL_004').ntsError('clear');
                    $('#A_LBL_007').ntsError('clear');
                    $('#A_INP_004').ntsError('clear');
                };
                ScreenModel.prototype.getLineBank = function (curCode) {
                    var self = this;
                    var data = _.find(self.items(), function (x) {
                        return x.lineBankCode === curCode;
                    });
                    if (!data) {
                        return new LineBank(null, null, null, null, 0, null, null, null, []);
                    }
                    return new LineBank(data.bankCode, data.branchCode, data.lineBankCode, data.lineBankName, data.accountAtr, data.accountNo, data.memo, data.requesterName, data.consignors);
                };
                ScreenModel.prototype.getNameForBankAndBranch = function (lineBank) {
                    var self = this;
                    var tmp = _.find(this.dataSource2(), function (x) {
                        return x.code === lineBank.bankCode();
                    });
                    var tmp1 = _.find(self.dataSource2(), function (x) {
                        return x.code === lineBank.branchCode() && x.parentCode === lineBank.bankCode();
                    });
                    if (tmp != undefined) {
                        self.bankName(tmp.name);
                    }
                    else {
                        self.bankName('');
                    }
                    if (tmp1 != undefined) {
                        self.branchName(tmp1.name);
                    }
                    else {
                        self.branchName('');
                    }
                };
                ScreenModel.prototype.findBankAll = function () {
                    var self = this;
                    var lineBank = self.currentLineBank();
                    var dfd = $.Deferred();
                    qmm006.a.service.findBankAll()
                        .done(function (data) {
                        if (data.length > 0) {
                            var bankData = [];
                            _.forEach(data, function (item) {
                                var childs = _.map(item.bankBranch, function (itemChild) {
                                    return new BankBranch(itemChild.bankBranchCode, itemChild.bankBranchName, item.bankCode, item.bankName, item.bankCode + itemChild.bankBranchCode, []);
                                });
                                bankData.push(new BankBranch(item.bankCode, item.bankName, null, null, item.bankCode, childs));
                            });
                            self.dataSource(bankData);
                            self.dataSource2(nts.uk.util.flatArray(self.dataSource(), "childs"));
                        }
                        dfd.resolve();
                    }).fail(function (res) {
                        dfd.reject(res);
                    });
                    return dfd.promise();
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var LineBank = (function () {
                function LineBank(bankCode, branchCode, lineBankCode, lineBankName, accountAtr, accountNo, memo, requesterName, consignors) {
                    this.bankCode = ko.observable(bankCode);
                    this.branchCode = ko.observable(branchCode);
                    this.lineBankCode = ko.observable(lineBankCode);
                    this.lineBankName = ko.observable(lineBankName);
                    this.accountAtr = ko.observable(accountAtr);
                    this.accountNo = ko.observable(accountNo);
                    this.memo = ko.observable(memo);
                    this.requesterName = ko.observable(requesterName);
                    this.consignors = ko.observableArray([]);
                    for (var i = 0; i <= 4; i++) {
                        var consignorItem = consignors[i];
                        switch (i) {
                            case 0:
                                if (consignorItem) {
                                    this.consignors.push(new Consignor("①", consignorItem.code, consignorItem.memo));
                                }
                                else {
                                    this.consignors.push(new Consignor("①", "", ""));
                                }
                                break;
                            case 1:
                                if (consignorItem) {
                                    this.consignors.push(new Consignor("②", consignorItem.code, consignorItem.memo));
                                }
                                else {
                                    this.consignors.push(new Consignor("②", "", ""));
                                }
                                break;
                            case 2:
                                if (consignorItem) {
                                    this.consignors.push(new Consignor("③", consignorItem.code, consignorItem.memo));
                                }
                                else {
                                    this.consignors.push(new Consignor("③", "", ""));
                                }
                                break;
                            case 3:
                                if (consignorItem) {
                                    this.consignors.push(new Consignor("④", consignorItem.code, consignorItem.memo));
                                }
                                else {
                                    this.consignors.push(new Consignor("④", "", ""));
                                }
                                break;
                            case 4:
                                if (consignorItem) {
                                    this.consignors.push(new Consignor("⑤", consignorItem.code, consignorItem.memo));
                                }
                                else {
                                    this.consignors.push(new Consignor("⑤", "", ""));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                return LineBank;
            }());
            var Consignor = (function () {
                function Consignor(noIcon, consignorCode, consignorMemo) {
                    this.noIcon = ko.observable(noIcon);
                    this.consignorCode = ko.observable(consignorCode);
                    this.consignorMemo = ko.observable(consignorMemo);
                }
                return Consignor;
            }());
            var BankBranch = (function () {
                function BankBranch(code, name, parentCode, parentName, treeCode, childs) {
                    var self = this;
                    self.code = code;
                    self.name = name;
                    self.nodeText = self.code + ' ' + self.name;
                    self.childs = childs;
                    self.parentCode = parentCode;
                    self.parentName = parentName;
                    self.treeCode = treeCode;
                }
                return BankBranch;
            }());
        })(viewmodel = a.viewmodel || (a.viewmodel = {}));
    })(a = qmm006.a || (qmm006.a = {}));
})(qmm006 || (qmm006 = {}));
//# sourceMappingURL=qmm006.a.viewmodel.js.map