var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var pr;
        (function (pr) {
            var view;
            (function (view) {
                var qpp005;
                (function (qpp005) {
                    var option = nts.uk.ui.option;
                    var viewmodel;
                    (function (viewmodel) {
                        var ScreenModel = (function () {
                            function ScreenModel() {
                                var self = this;
                                self.isHandInput = ko.observable(true);
                                self.paymentDataResult = ko.observable(new PaymentDataResultViewModel());
                                self.categories = ko.observable(new CategoriesList());
                                self.option = ko.mapping.fromJS(new option.TextEditorOption());
                                self.employee = ko.observable();
                                var employeeMocks = [
                                    { personId: 'A00000000000000000000000000000000001', code: 'A000000000001', name: '日通　社員1' },
                                    { personId: 'A00000000000000000000000000000000002', code: 'A000000000002', name: '日通　社員2' },
                                    { personId: 'A00000000000000000000000000000000003', code: 'A000000000003', name: '日通　社員3' },
                                    { personId: 'A00000000000000000000000000000000004', code: 'A000000000004', name: '日通　社員4' },
                                    { personId: 'A00000000000000000000000000000000005', code: 'A000000000005', name: '日通　社員5' },
                                    { personId: 'A00000000000000000000000000000000006', code: 'A000000000006', name: '日通　社員6' },
                                    { personId: 'A00000000000000000000000000000000007', code: 'A000000000007', name: '日通　社員7' },
                                    { personId: 'A00000000000000000000000000000000008', code: 'A000000000008', name: '日通　社員8' },
                                    { personId: 'A00000000000000000000000000000000009', code: 'A000000000009', name: '日通　社員9' },
                                    { personId: 'A00000000000000000000000000000000010', code: 'A000000000010', name: '日通　社員10' }
                                ];
                                var employees = _.map(employeeMocks, function (employee) {
                                    return new Employee(employee.personId, employee.code, employee.name);
                                });
                                self.employeeList = ko.observableArray(employees);
                                self.employee(employees[0]);
                                // グリッド設定
                                self.switchButton = ko.observable(new SwitchButton());
                                self.visible = ko.observable(self.switchButton().selectedRuleCode() == 'vnext');
                                self.switchButton().selectedRuleCode.subscribe(function (newValue) {
                                    self.visible(newValue == 'vnext');
                                    qpp005.utils.gridSetup(self.switchButton().selectedRuleCode());
                                });
                            }
                            ScreenModel.prototype.startPage = function () {
                                var self = this;
                                var dfd = $.Deferred();
                                qpp005.service.getPaymentData(self.employee().personId, self.employee().code).done(function (res) {
                                    ko.mapping.fromJS(res, {}, self.paymentDataResult());
                                    var categoryPayment = self.paymentDataResult().categories()[0];
                                    var categoryDeduct = self.paymentDataResult().categories()[1];
                                    var categoryArticle = self.paymentDataResult().categories()[3];
                                    self.calcTotal(categoryPayment, categoryDeduct, categoryArticle, true);
                                    self.calcTotal(categoryDeduct, categoryPayment, categoryArticle, false);
                                    dfd.resolve();
                                }).fail(function (res) {
                                    $('.tb-category').css('display', 'none');
                                    alert(res.message);
                                });
                                // Return.
                                return dfd.promise();
                            };
                            /** Event click: 計算/登録*/
                            ScreenModel.prototype.register = function () {
                                var self = this;
                                // TODO: Check error input
                                qpp005.service.register(self.paymentDataResult()).done(function (res) {
                                });
                            };
                            /** Event click: 対象者*/
                            ScreenModel.prototype.openEmployeeList = function () {
                                var _this = this;
                                var self = this;
                                nts.uk.ui.windows.sub.modal('/view/qpp/005/c/index.xhtml', { title: '社員選択' }).onClosed(function () {
                                    var employee = nts.uk.ui.windows.getShared('employee');
                                    self.employee(employee);
                                    self.startPage();
                                    return _this;
                                });
                            };
                            /** Event: Previous employee */
                            ScreenModel.prototype.prevEmployee = function () {
                                var self = this;
                                var eIdx = _.indexOf(self.employeeList(), self.employee());
                                var eSize = self.employeeList().length;
                                if (eIdx === 0) {
                                    return;
                                }
                                self.employee(self.employeeList()[eIdx - 1]);
                                self.startPage();
                            };
                            /** Event: Next employee */
                            ScreenModel.prototype.nextEmployee = function () {
                                var self = this;
                                var eIdx = _.indexOf(self.employeeList(), self.employee());
                                var eSize = self.employeeList().length;
                                if (eIdx === eSize - 1) {
                                    return;
                                }
                                self.employee(self.employeeList()[eIdx + 1]);
                                self.startPage();
                            };
                            ScreenModel.prototype.openColorSettingGuide = function () {
                                var _this = this;
                                var self = this;
                                nts.uk.ui.windows.sub.modal('/view/qpp/005/d/index.xhtml', { title: '入力欄の背景色について' }).onClosed(function () {
                                    var employee = nts.uk.ui.windows.getShared('employee');
                                    self.employee(employee);
                                    self.startPage();
                                    return _this;
                                });
                            };
                            ScreenModel.prototype.openGridSetting = function () {
                                var self = this;
                                $('#pơpup-orientation').ntsPopup('show');
                            };
                            ScreenModel.prototype.openSetupTaxItem = function () {
                                var _this = this;
                                var self = this;
                                nts.uk.ui.windows.sub.modal('/view/qpp/005/d/index.xhtml', { title: '入力欄の背景色について' }).onClosed(function () {
                                    var employee = nts.uk.ui.windows.getShared('employee');
                                    self.employee(employee);
                                    self.startPage();
                                    return _this;
                                });
                            };
                            /**
                             * auto Calculate item total
                             */
                            ScreenModel.prototype.calcTotal = function (source, tranfer, destinate, isPayment) {
                                var detailsPayment = _.flatMap(source.lines(), function (l) { return l.details(); });
                                var totalPayment = _.last(detailsPayment);
                                var inputtingsDetailsPayment = _.reject(detailsPayment, totalPayment);
                                var updateTotalPayment = function () {
                                    var total = _(inputtingsDetailsPayment).map(function (d) { return Number(uk.util.orDefault(d.value(), 0)); }).sum();
                                    totalPayment.value(total);
                                    var detailsTranfer = _.flatMap(tranfer.lines(), function (l) { return l.details(); });
                                    var totalValueTranfer = _.last(detailsTranfer).value();
                                    var detailsDestinate = _.flatMap(destinate.lines(), function (l) { return l.details(); });
                                    if (isPayment) {
                                        _.last(detailsDestinate).value(total - totalValueTranfer);
                                    }
                                    else {
                                        _.last(detailsDestinate).value(totalValueTranfer - total);
                                    }
                                };
                                inputtingsDetailsPayment.forEach(function (detail) {
                                    detail.value.subscribe(function () { return updateTotalPayment(); });
                                });
                            };
                            return ScreenModel;
                        }());
                        viewmodel.ScreenModel = ScreenModel;
                        ;
                        var SwitchButton = (function () {
                            function SwitchButton() {
                                var self = this;
                                self.roundingRules = ko.observableArray([
                                    { code: 'vnext', name: '縦方向' },
                                    { code: 'hnext', name: '横方向' }
                                ]);
                                self.selectedRuleCode = ko.observable('hnext');
                            }
                            return SwitchButton;
                        }());
                        viewmodel.SwitchButton = SwitchButton;
                        var Employee = (function () {
                            function Employee(personId, code, name) {
                                var self = this;
                                self.personId = personId;
                                self.code = code;
                                self.name = name;
                            }
                            return Employee;
                        }());
                        viewmodel.Employee = Employee;
                        /**
                          * Model namespace.
                       */
                        var PaymentDataResultViewModel = (function () {
                            function PaymentDataResultViewModel() {
                            }
                            return PaymentDataResultViewModel;
                        }());
                        viewmodel.PaymentDataResultViewModel = PaymentDataResultViewModel;
                        // header
                        var PaymentDataHeaderViewModel = (function () {
                            function PaymentDataHeaderViewModel() {
                            }
                            return PaymentDataHeaderViewModel;
                        }());
                        viewmodel.PaymentDataHeaderViewModel = PaymentDataHeaderViewModel;
                        var PrintPositionCategoryViewModel = (function () {
                            function PrintPositionCategoryViewModel() {
                            }
                            return PrintPositionCategoryViewModel;
                        }());
                        viewmodel.PrintPositionCategoryViewModel = PrintPositionCategoryViewModel;
                        // categories
                        var LayoutMasterCategoryViewModel = (function () {
                            function LayoutMasterCategoryViewModel() {
                            }
                            return LayoutMasterCategoryViewModel;
                        }());
                        viewmodel.LayoutMasterCategoryViewModel = LayoutMasterCategoryViewModel;
                        // item
                        var DetailItemViewModel = (function () {
                            function DetailItemViewModel(categoryAtr, itemAtr, itemCode, itemName, value, columnPosition, linePosition, deductAtr, displayAtr, taxAtr, isCreated) {
                                var self = this;
                                self.categoryAtr = categoryAtr;
                                self.itemAtr = itemAtr;
                                self.itemCode = itemCode;
                                self.itemName = itemName;
                                self.value = ko.observable(value);
                                self.columnPosition = columnPosition;
                                self.linePosition = linePosition;
                                self.deductAtr = deductAtr;
                                self.displayAtr = displayAtr;
                                self.taxAtr = taxAtr;
                                self.isCreated = isCreated;
                            }
                            return DetailItemViewModel;
                        }());
                        viewmodel.DetailItemViewModel = DetailItemViewModel;
                        var CategoriesList = (function () {
                            function CategoriesList() {
                                this.selectionChangedEvent = $.Callbacks();
                                var self = this;
                                self.items = ko.observableArray([]);
                                self.selectedCode = ko.observable();
                                self.selectedCode.subscribe(function (selectedCode) {
                                    self.selectionChangedEvent.fire(selectedCode);
                                });
                            }
                            return CategoriesList;
                        }());
                        viewmodel.CategoriesList = CategoriesList;
                        /**
                        * View Model Category
                        */
                        var Category = (function () {
                            /**
                             * Constructor
                             * @param: code
                             * @param: name
                             */
                            function Category(code, name) {
                                var self = this;
                                self.code = code;
                                self.name = name;
                            }
                            return Category;
                        }());
                        viewmodel.Category = Category;
                    })(viewmodel = qpp005.viewmodel || (qpp005.viewmodel = {}));
                })(qpp005 = view.qpp005 || (view.qpp005 = {}));
            })(view = pr.view || (pr.view = {}));
        })(pr = uk.pr || (uk.pr = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
