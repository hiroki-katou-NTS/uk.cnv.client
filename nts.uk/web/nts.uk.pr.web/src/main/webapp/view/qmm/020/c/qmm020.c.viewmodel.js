var qmm020;
(function (qmm020) {
    var c;
    (function (c) {
        var viewmodel;
        (function (viewmodel) {
            var ScreenModel = (function () {
                function ScreenModel() {
                    var self = this;
                    var dfd = $.Deferred();
                    self.itemList = ko.observableArray([]);
                    self.selectedCode = ko.observable('');
                    self.isEnable = ko.observable(true);
                    self.selectedList = ko.observableArray([]);
                    self.itemHist = ko.observable(null);
                    self.itemTotalList = ko.observableArray([]);
                    self.itemListDetail = ko.observableArray([]);
                    self.histId = ko.observable(null);
                    self.maxItem = ko.observable(null);
                    self.currentItem = ko.observable(new TotalModel({
                        historyId: '',
                        employeeCode: '',
                        paymentDetailCode: '',
                        bonusDetailCode: '',
                        startYm: '',
                        endYm: ''
                    }));
                    self.selectedCode.subscribe(function (codeChange) {
                        c.service.getAllEmployeeAllotSetting(ko.toJS(codeChange)).done(function (data) {
                            self.itemListDetail([]);
                            if (data && data.length > 0) {
                                _.map(data, function (item) {
                                    self.itemListDetail.push(new EmployeeAllotSettingDto(item.companyCode, item.historyId, item.employeeCode, item.employeeName, item.paymentDetailCode, item.paymentDetailName, item.bonusDetailCode, item.bonusDetailName));
                                });
                                $("#C_LST_001").igGrid({
                                    columns: [
                                        { headerText: "", key: "NO", dataType: "string", width: "20px" },
                                        { headerText: "コード", key: "employeeCode", dataType: "string", width: "100px" },
                                        { headerText: "名称", key: "employeeName", dataType: "string", width: "200px" },
                                        { headerText: "", key: "paymentDetailCode", dataType: "string", hidden: true },
                                        { headerText: "", key: "paymentDetailName", dataType: "string", hidden: true },
                                        { headerText: "", key: "bonusDetailCode", dataType: "string", hidden: true },
                                        { headerText: "", key: "bonusDetailName", dataType: "string", hidden: true },
                                        {
                                            headerText: "給与明細書", key: "paymentDetailCode", dataType: "string", width: "250px", unbound: true,
                                            template: "<input type='button' id='" + "C_BTN_001" + "' value='選択'/><label style='margin-left:5px;'>${paymentDetailCode}</label><label style='margin-left:15px;'>${paymentDetailName}</label>"
                                        },
                                        {
                                            headerText: "賞与明細書", key: "bonusDetailCode", dataType: "string", width: "20%", unbound: true,
                                            template: "<input type='button' id='" + "C_BTN_002" + "' value='選択'/><label style='margin-left:5px;'>${bonusDetailCode}</label><label style='margin-left:15px;'>${bonusDetailName}</label>"
                                        },
                                    ],
                                    features: [{
                                            name: 'Selection',
                                            mode: 'row',
                                            multipleSelection: true,
                                            activation: false,
                                        }],
                                    virtualization: true,
                                    virtualizationMode: 'continuous',
                                    width: "800px",
                                    height: "240px",
                                    primaryKey: "ID",
                                    dataSource: ko.mapping.toJS(self.itemListDetail)
                                });
                            }
                            dfd.resolve();
                        }).fail(function (res) {
                            // Alert message
                            alert(res);
                        });
                        dfd.promise();
                    });
                    // Array Data 1 
                    var employment1 = ko.mapping.fromJS([
                        { "NO": 1, "ID": "000000001", "Name": "正社員", "PaymentDocID": "K001", "PaymentDocName": "給与明細書001", "BonusDocID": "S001", "BonusDocName": "賞与明細書001" },
                        { "NO": 2, "ID": "000000002", "Name": "DucPham社員", "PaymentDocID": "K002", "PaymentDocName": "給与明細書002", "BonusDocID": "S001", "BonusDocName": "賞与明細書002" },
                        { "NO": 3, "ID": "000000003", "Name": "HoangMai社員", "PaymentDocID": "K003", "PaymentDocName": "給与明細書003", "BonusDocID": "S001", "BonusDocName": "賞与明細書003" }
                    ]);
                    self.dataSource = ko.mapping.toJS(employment1());
                    //console.log(self.dataSource);
                    //Build IgGrid
                    //SCREEN C
                    //Event : Click to button Sentaku on igGrid
                    var openPaymentDialog = function (evt, ui) {
                        if (ui.colKey === "PaymentDocID") {
                            //Gọi hàm open SubWindow
                            //Khi close Subwindow, get dc cái new object(ID, Name... )
                            var row = _.find(employment1(), function (item) {
                                //return item.ID() === ui.rowKey;
                            });
                        }
                    };
                    self.start();
                    /**
                     * find maxItem by endate
                     */
                }
                //find histId to subscribe
                ScreenModel.prototype.getHist = function (value) {
                    var self = this;
                    return _.find(self.itemList(), function (item) {
                        return item.historyId === value;
                    });
                };
                //Selected changed
                ScreenModel.prototype.selectionChanged = function (evt, ui) {
                    //console.log(evt.type);
                    var selectedRows = ui.selectedRows;
                    var arr = [];
                    for (var i = 0; i < selectedRows.length; i++) {
                        arr.push("" + selectedRows[i].id);
                    }
                    this.selectedList(arr);
                };
                ;
                // start function
                ScreenModel.prototype.start = function () {
                    var self = this;
                    var dfd = $.Deferred();
                    //Get list startDate, endDate of History  
                    c.service.getEmployeeAllotHeaderList().done(function (data) {
                        if (data.length > 0) {
                            _.forEach(data, function (item) {
                                self.itemList.push(new ItemModel(item.historyId, item.startYm + ' ~ ' + item.endYm, item.endYm));
                            });
                            var max = _.maxBy(self.itemList(), function (item) { return item.endYm; });
                        }
                        else {
                            dfd.resolve();
                        }
                    }).fail(function (res) {
                        // Alert message
                        alert(res);
                    });
                    // Return.
                    return dfd.promise();
                };
                //Open dialog Add History
                ScreenModel.prototype.openJDialog = function () {
                    var self = this;
                    var historyScreenType = "1";
                    var valueShareJDialog = historyScreenType + "~" + "201701";
                    nts.uk.ui.windows.setShared('valJDialog', valueShareJDialog);
                    nts.uk.ui.windows.sub.modal('/view/qmm/020/j/index.xhtml', { title: '明細書の紐ずけ＞履歴追加' })
                        .onClosed(function () {
                        var returnJDialog = nts.uk.ui.windows.getShared('returnJDialog');
                        var modeRadio = returnJDialog.split("~")[0];
                        var returnValue = returnJDialog.split("~")[1];
                        if (returnValue != '') {
                            //                        let employeeAllotSettings = new Array<EmployeeAllotSettingDto>();
                            var items = self.itemTotalList();
                            var addItem = new TotalModel({
                                historyId: '',
                                employeeCode: '',
                                paymentDetailCode: '',
                                bonusDetailCode: '',
                                startYm: returnValue,
                                endYm: '999912'
                            });
                            items.push(addItem);
                            if (modeRadio === "2") {
                                self.currentItem().historyId('');
                                self.currentItem().startYm(returnValue);
                                self.currentItem().endYm('999912');
                                self.currentItem().paymentDetailCode('');
                                self.currentItem().bonusDetailCode('');
                            }
                            self.itemTotalList([]);
                            self.itemTotalList(items);
                        }
                    });
                };
                //Open dialog Edit History
                ScreenModel.prototype.openKDialog = function () {
                    var self = this;
                    //var singleSelectedCode = self.singleSelectedCode().split(';');
                    //nts.uk.ui.windows.setShared('stmtCode', singleSelectedCode[0]);
                    nts.uk.ui.windows.sub.modal('/view/qmm/020/k/index.xhtml', { title: '明細書の紐ずけ＞履歴編集' }).onClosed(function () {
                        //self.start(self.singleSelectedCode());
                    });
                };
                return ScreenModel;
            }());
            viewmodel.ScreenModel = ScreenModel;
            var ItemModel = (function () {
                function ItemModel(histId, startEnd, endYm) {
                    var self = this;
                    self.histId = (histId);
                    self.startEnd = startEnd;
                    self.endYm = endYm;
                }
                return ItemModel;
            }());
            viewmodel.ItemModel = ItemModel;
            var EmployeeAllotSettingDto = (function () {
                function EmployeeAllotSettingDto(companyCode, historyId, employeeCode, employeeName, paymentDetailCode, paymentDetailName, bonusDetailCode, bonusDetailName) {
                    this.companyCode = ko.observable(companyCode);
                    this.historyId = ko.observable(historyId);
                    this.employeeCode = ko.observable(employeeCode);
                    this.employeeName = ko.observable(employeeName);
                    this.paymentDetailCode = ko.observable(paymentDetailCode);
                    this.paymentDetailName = ko.observable(paymentDetailName);
                    this.bonusDetailCode = ko.observable(bonusDetailCode);
                    this.bonusDetailName = ko.observable(bonusDetailName);
                }
                return EmployeeAllotSettingDto;
            }());
            var EmployeeSettingHeaderModel = (function () {
                function EmployeeSettingHeaderModel(companyCode, startYm, endYm, historyId) {
                    this.companyCode = companyCode;
                    this.startYm = startYm;
                    this.endYm = endYm;
                    this.historyId = historyId;
                }
                return EmployeeSettingHeaderModel;
            }());
            viewmodel.EmployeeSettingHeaderModel = EmployeeSettingHeaderModel;
            var EmployeeSettingDetailModel = (function () {
                function EmployeeSettingDetailModel(companyCode, historyId, employeeCode, paymentDetailCode, bonusDetailCode) {
                    this.companyCode = companyCode;
                    this.historyId = historyId;
                    this.employeeCode = employeeCode;
                    this.paymentDetailCode = paymentDetailCode;
                    this.bonusDetailCode = bonusDetailCode;
                }
                return EmployeeSettingDetailModel;
            }());
            viewmodel.EmployeeSettingDetailModel = EmployeeSettingDetailModel;
            var TotalModel = (function () {
                function TotalModel(param) {
                    this.companyCode = ko.observable(param.companyCode);
                    this.historyId = ko.observable(param.historyId);
                    this.employeeCode = ko.observable(param.employeeCode);
                    this.employeeName = ko.observable(param.employeeName);
                    this.paymentDetailCode = ko.observable(param.paymentDetailCode);
                    this.paymentDetailName = ko.observable(param.paymentDetailName);
                    this.bonusDetailCode = ko.observable(param.bonusDetailCode);
                    this.bonusDetailName = ko.observable(param.bonusDetailName);
                    this.startYm = ko.observable(param.startYm);
                    this.endYm = ko.observable(param.endYm);
                }
                return TotalModel;
            }());
        })(viewmodel = c.viewmodel || (c.viewmodel = {}));
    })(c = qmm020.c || (qmm020.c = {}));
})(qmm020 || (qmm020 = {}));
