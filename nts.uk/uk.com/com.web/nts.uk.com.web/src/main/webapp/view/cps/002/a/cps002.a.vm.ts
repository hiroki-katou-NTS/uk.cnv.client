let $: any = window['$'],
    _: any = window['_'],
    ko: any = window['ko'],
    nts: any = window['nts'],
    moment: any = window['moment'],
    __viewContext: any = window['__viewContext'];

module cps002.a.vm {
    import alert = nts.uk.ui.dialog.alert;
    import text = nts.uk.resource.getText;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import dialog = nts.uk.ui.dialog.info;
    import subModal = nts.uk.ui.windows.sub.modal;
    import jump = nts.uk.request.jump;
    import liveView = nts.uk.request.liveView;
    import character = nts.uk.characteristics;
    import block = nts.uk.ui.block;
    import lv = nts.layout.validate;
    import vc = nts.layout.validation;
    import permision = service.getCurrentEmpPermision;
    import alertError = nts.uk.ui.dialog.alertError;
    import alertWarning = nts.uk.ui.dialog.caution;

    export class ViewModel {
        date: KnockoutObservable<Date> = ko.observable(moment().toDate());
        createTypeList: KnockoutObservableArray<any> = ko.observableArray([
            new BoxModel(1, text('CPS002_26')),
            new BoxModel(2, text('CPS002_27')),
            new BoxModel(3, text('CPS002_28'))
        ]);

        categoryList: KnockoutObservableArray<CategoryItem> = ko.observableArray([]);
        initValueList: KnockoutObservableArray<InitSetting> = ko.observableArray([]);
        itemSettingList: KnockoutObservableArray<SettingItem> = ko.observableArray([]);
        createTypeId: KnockoutObservable<number> = ko.observable(3);
        currentEmployee: KnockoutObservable<Employee> = ko.observable(new Employee());
        stampCardEditing: KnockoutObservable<IStampCardEditing> = ko.observable({
            method: EDIT_METHOD.PreviousZero,
            digitsNumber: 20
        });

        categorySelectedCode: KnockoutObservable<string> = ko.observable('');
        empRegHistory: KnockoutObservable<EmpRegHistory> = ko.observable(null);
        currentStep: KnockoutObservable<string> = ko.observable('CPS002_13');
        initSettingSelectedCode: KnockoutObservable<string> = ko.observable('');
        currentInitSetting: KnockoutObservable<InitSetting> = ko.observable(new InitSetting(null));
        copyEmployee: KnockoutObservable<EmployeeCopy> = ko.observable(new EmployeeCopy(null));
        isAllowAvatarUpload: KnockoutObservable<boolean> = ko.observable(false);
        currentUseSetting: KnockoutObservable<UserSetting> = ko.observable(null);
        employeeBasicInfo: KnockoutObservable<IEmployeeBasicInfo> = ko.observable(null);
        layoutData: KnockoutObservableArray<any> = ko.observableArray([]);
        listItemCls: KnockoutObservableArray<any> = ko.observableArray([]);
        defaultImgId: KnockoutObservable<string> = ko.observable("");
        subContraint: KnockoutObservable<string> = ko.observable('StampNumber');
        // check quyen có thể setting copy 
        enaBtnOpenFModal: KnockoutObservable<boolean> = ko.observable(true);
        // check quyen có thể setting giá trị ban đầu nhập vào 
        enaBtnOpenInitModal: KnockoutObservable<boolean> = ko.observable(true);
        wrkPlaceStartDate: KnockoutObservable<string> = ko.observable("");

        ccgcomponent: any = {
            /** Common properties */
            systemType: 1, // システム区分
            showEmployeeSelection: true, // 検索タイプ
            showQuickSearchTab: false, // クイック検索
            showAdvancedSearchTab: true, // 詳細検索
            showBaseDate: false, // 基準日利用
            showClosure: false, // 就業締め日利用
            showAllClosure: false, // 全締め表示
            showPeriod: false, // 対象期間利用
            periodFormatYM: true, // 対象期間精度

            /** Required parame*/
            baseDate: moment.utc().toISOString(), // 基準日
            periodStartDate: moment.utc("1900/01/01", "YYYY/MM/DD").toISOString(), // 対象期間開始日
            periodEndDate: moment.utc("9999/12/31", "YYYY/MM/DD").toISOString(), // 対象期間終了日
            inService: true, // 在職区分
            leaveOfAbsence: true, // 休職区分
            closed: true, // 休業区分
            retirement: false, // 退職区分

            /** Quick search tab options */
            showAllReferableEmployee: true, // 参照可能な社員すべて
            showOnlyMe: true, // 自分だけ
            showSameWorkplace: true, // 同じ職場の社員
            showSameWorkplaceAndChild: true, // 同じ職場とその配下の社員

            /** Advanced search properties */
            showEmployment: true, // 雇用条件
            showWorkplace: true, // 職場条件
            showClassification: true, // 分類条件
            showJobTitle: true, // 職位条件
            showWorktype: false, // 勤種条件
            isMutipleCheck: false, // 選択モード

            /** Return data */
            returnDataFromCcg001: (data: any) => {
                let self: ViewModel = this;

                self.copyEmployee(data.listEmployee[0]);
            }
        };

        licenseCheck: KnockoutObservable<string> = ko.observable("");
        licenseCheckDipslay: KnockoutObservable<boolean> = ko.observable(true);
        classWarning: KnockoutObservable<string> = ko.observable("");

        isFromCPS018: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this,
                employee = self.currentEmployee();
            
            let params = getShared("CPS002A_PARAMS") || { isFromCPS018: false };
            self.isFromCPS018(params.isFromCPS018);
            nts.uk.sessionStorage.removeItem(nts.uk.request.STORAGE_KEY_TRANSFER_DATA);

            self.createTypeId.subscribe((newValue) => {
                self.initValueList([]);
                self.categoryList([]);
                self.itemSettingList([]);
                self.categorySelectedCode('');
                self.initSettingSelectedCode('');
                self.currentInitSetting(new InitSetting(null));

                nts.uk.ui.errors.clearAll();
                (nts.uk.ui._viewModel || {
                    kiban: {
                        errorDialogViewModel: {
                            errors: {
                                removeAll: () => { }
                            }
                        }
                    }
                }).kiban.errorDialogViewModel.errors.removeAll();
            });

            self.employeeBasicInfo.subscribe((data) => {
                if (data) {

                    employee.hireDate(data.jobEntryDate);
                    self.createTypeId(data.employeeCreationMethod);

                    let copyEmployeeId = data.copyEmployeeId;
                    if (copyEmployeeId != "" && copyEmployeeId != self.copyEmployee().employeeId) {
                        let command = {
                            baseDate: moment().toDate(),
                            employeeIds: [data.copyEmployeeId]
                        };

                        service.getEmployeeInfo(command).done((result) => {
                            self.copyEmployee(new EmployeeCopy(result[0]));
                        });
                    }
                }
            });

            self.initSettingSelectedCode.subscribe(initCode => {
                if (!_.isNil(initCode)) {
                    let InitSetting = _.find(self.initValueList(), item => {
                        return item.itemCode == initCode;
                    });

                    if (InitSetting) {
                        let currentCtgCode = self.categorySelectedCode();

                        service.getAllInitValueCtgSetting(InitSetting.itemId).done((result: Array<IInitValueCtgSetting>) => {
                            if (result.length) {
                                self.categoryList(_.map(result, item => {
                                    return new CategoryItem(item);
                                }));

                                if (currentCtgCode === "") {
                                    self.categorySelectedCode(result[0].categoryCd);
                                } else {
                                    let currentCtg = _.find(result, item => {
                                        return item.categoryCd == currentCtgCode;
                                    });
                                    if (currentCtg) {
                                        self.categorySelectedCode.valueHasMutated()
                                    } else {
                                        self.categorySelectedCode(result[0].categoryCd);
                                    }
                                }

                            } else {
                                self.categoryList.removeAll();
                            }
                        });

                        self.currentInitSetting(InitSetting);
                    }
                }
            });

            self.copyEmployee.subscribe((CopyEmployee) => {
                self.loadCopySettingItemData();
            });

            self.categorySelectedCode.subscribe(code => {
                if (!_.isNil(code) && !_.isEmpty(code)) {
                    self.itemSettingList.removeAll();

                    if (self.createTypeId() === 2) {
                        let command = ko.toJS(employee);

                        command = _.omit(command, ['initSettingId', 'baseDate', 'categoryCd']);

                        command = _.extend(command, {
                            categoryCd: code,
                            baseDate: employee.hireDate(),
                            initSettingId: self.currentInitSetting().itemId
                        });

                        service.getAllInitValueItemSetting(command).done((result: Array<SettingItem>) => {
                            if (result.length) {
                                self.itemSettingList(_.map(result, item => {
                                    return new SettingItem(item);
                                }));
                            }
                        });
                    } else {
                        self.loadCopySettingItemData();
                    }
                }
            });

            employee.avatarCropedId.subscribe((avartarId) => {
                let self = this,
                    avartarContent = $("#employeeAvatar");

                if (avartarId != "") {
                    avartarContent.empty()
                        .append($("<img>", {
                            id: "employeeAvatar",
                            src: liveView(avartarId),
                        }));
                    // remove background
                    $(".avatar").removeClass();
                } else {
                    avartarContent.empty();
                }
            });

            employee.employeeCode.subscribe((employeeCode) => {
                let self = this;
                self.autoUpdateCardNo(employeeCode);
            });

            employee.cardNo.subscribe((cardNo) => {
                let ce = ko.toJS(self.stampCardEditing);
                let emp = employee;
                if (!!nts.uk.text.allHalfAlphanumeric(cardNo).probe) {
                    if (cardNo && cardNo.length <= ce.digitsNumber) {
                        switch (ce.method) {
                            case EDIT_METHOD.PreviousZero: {
                                emp.cardNo(_.padStart(cardNo, ce.digitsNumber, '0'));
                                break;
                            }
                            case EDIT_METHOD.AfterZero: {
                                emp.cardNo(_.padEnd(cardNo, ce.digitsNumber, '0'));
                                break;
                            }
                            case EDIT_METHOD.PreviousSpace: {
                                emp.cardNo(_.padStart(cardNo, ce.digitsNumber, ' '));
                                break;
                            }
                            case EDIT_METHOD.AfterSpace: {
                                emp.cardNo(_.padEnd(cardNo, ce.digitsNumber, ' '));
                                break;
                            }
                        }
                    }
                }
            });

            // check quyen có thể setting copy hoặc setting init
            permision().done((data: Array<IPersonAuth>) => {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].functionNo == FunctionNo.No9_Allow_SetCoppy) {
                            if (data[i].available == false) {
                                self.enaBtnOpenFModal(false);
                            }
                        }
                        if (data[i].functionNo == FunctionNo.No10_Allow_SetInit) {
                            if (data[i].available == false) {
                                self.enaBtnOpenInitModal(false);
                            }
                        }
                    }
                }
            });
            self.checkLicense();

            self.currentStep.subscribe((step: string) => {
                // clear all error when step change
                nts.uk.ui.errors.clearAll();
                (nts.uk.ui._viewModel || {
                    kiban: {
                        errorDialogViewModel: {
                            errors: {
                                removeAll: () => { }
                            }
                        }
                    }
                }).kiban.errorDialogViewModel.errors.removeAll();

                switch (step) {
                    case 'CPS002_13':
                        $('#pg-name').text('CPS002A' + ' ' + text('CPS002_1'));
                        self.layoutData.removeAll();
                        self.listItemCls.removeAll();
                        break;
                    case 'CPS002_14':
                        if (ko.toJS(self.createTypeId) === 1) {
                            //start Screen B
                            //Set name Screen B　#CPS002_2
                            $('#pg-name').text('CPS002B' + ' ' + text('CPS002_2'));
                            // init ccg component
                            let sto = setTimeout(() => {
                                $('#ccgcomponent').ntsGroupComponent(self.ccgcomponent).done(() => {
                                    self.loadCopySettingCtgData();
                                });
                                clearTimeout(sto);
                            }, 100);
                        } else {
                            //start Screen C
                            //Set name Screen C　#CPS002_3
                            $('#pg-name').text('CPS002C' + ' ' + text('CPS002_3'));
                            self.loadInitSettingData();
                        }

                        self.layoutData.removeAll();
                        self.listItemCls.removeAll();

                        $('#combo-box').focus();
                        break;
                    case 'CPS002_15':
                        let command = ko.toJS(employee);

                        $('#pg-name').text('CPS002D' + ' ' + text('CPS002_4'));

                        //add atr
                        command.employeeCopyId = self.copyEmployee().employeeId;
                        command.initSettingId = self.currentInitSetting().itemId;
                        command.createType = self.createTypeId();

                        service.getLayoutByCreateType(command).done((data: ILayout) => {
                            self.listItemCls(data.itemsClassification || []);
                            self.wrkPlaceStartDate(data.wrkPlaceStartDate);
                            if (self.listItemCls().length > 0) {
                                new vc(self.listItemCls());
                            }
                        });

                        // check quyen có thể upload Avatar duoc khong
                        permision().done((data: Array<IPersonAuth>) => {
                            if (data) {
                                for (var i = 0; i < data.length; i++) {
                                    if (data[i].functionNo == FunctionNo.No2_Allow_UploadAva) {
                                        self.isAllowAvatarUpload(!!data[i].available);
                                    }
                                }
                            }
                        });
                        break;
                }
            });
            self.currentStep.valueHasMutated();

            self.start();
        }

        loadCopySettingItemData() {
            let self = this,
                step = ko.toJS(self.currentStep),
                currentCopyEmployeeId = self.copyEmployee().employeeId,
                categorySelectedCode = self.categorySelectedCode(),
                baseDate = self.currentEmployee().hireDate();

            if (step == 'CPS002_14') {
                if (!_.isNil(currentCopyEmployeeId) && !_.isNil(categorySelectedCode)) {
                    service.getAllCopySettingItem(currentCopyEmployeeId, categorySelectedCode, baseDate).done((result: Array<SettingItem>) => {
                        if (result.length) {
                            self.itemSettingList(_.map(result, item => {
                                return new SettingItem(item);
                            }));
                        }
                    }).fail(error => {
                        dialog({ messageId: error.message });
                    });
                }
            }
        }

        logMouseOver() {
            let self = this;
            self.autoUpdateCardNo(self.currentEmployee().employeeCode());
        }

        autoUpdateCardNo(employeeCode) {
            let self = this,
                employee = self.currentEmployee(),
                userSetting = self.currentUseSetting(),
                maxLengthCardNo = ko.toJS(self.stampCardEditing).digitsNumber;

            if (!userSetting || !(ko.toJS(employee) || {}).cardNo) {
                return;
            }

            switch (userSetting.cardNumberType) {
                case CardNoValType.SAME_AS_EMP_CODE:
                    if (employeeCode.length <= maxLengthCardNo) {
                        employee.cardNo(employeeCode);
                    }
                    break;
                case CardNoValType.CPC_AND_EMPC:
                    let newCardNo = __viewContext.user.companyCode + (ko.toJS(employee) || {}).employeeCode;
                    if (newCardNo.length <= maxLengthCardNo) {
                        employee.cardNo(newCardNo);
                    }
                    break;
                default:
                    break;
            }
        }

        start() {
            let self = this;
            const vm = new ko.ViewModel();

            self.currentEmployee().clearData();

            setShared("imageId", null);
            setShared("CPS002A", null);
            self.defaultImgId("");

            service.getStamCardEdit().done(data => {
                self.stampCardEditing(data);
                _.set(__viewContext, 'primitiveValueConstraints.StampNumber.maxLength', data.digitsNumber);

                self.subContraint.valueHasMutated();

                vm.$window.storage('NewEmployeeBasicInfo')
                    .then((data: any) => {{
                        // self.employeeBasicInfo(data);
                        $('#contents-area').removeClass('hidden');
                        self.getLayout();
                        self.employeeBasicInfo(data);
                    }})
            });
        }

        getLayout() {
            let self = this;
            service.getLayout().done((layout) => {
                if (layout) {
                    service.getUserSetting().done(userSetting => {
                        if (userSetting) {
                            service.getInitEmployeeCode().done((empCode) => {
                                // get employee code
                                self.currentEmployee().employeeCode(empCode);
                                // get card number
                                self.initStampCard(empCode);
                            }).fail((error) => {
                                self.initStampCard("");
                            });
                        }
                        self.currentUseSetting(new UserSetting(userSetting));
                        self.getLastRegHistory(userSetting);
                        $("#hireDate").focus();
                    }).always(() => {
                        // show content
                        $('#contents-area').css('visibility', 'visible');
                    });
                } else {
                    dialog({ messageId: "Msg_344" }).then(() => {
                        //move to toppage
                        jump('/view/cps/007/a/index.xhtml');
                    });
                }
            });
        }

        getLastRegHistory(userSetting: IUserSetting) {
            let self = this,
                showHistory = !userSetting ? true : userSetting.recentRegistrationType === 1 ? true : false;

            if (showHistory) {
                service.getLastRegHistory().done((result: IEmpRegHistory) => {
                    if (result) {

                        self.empRegHistory(new EmpRegHistory(result));
                    }
                });
            } else {
                self.empRegHistory(null);

            }
        }

        initStampCard(newEmployeeCode: string) {
            let self = this,
                ce = ko.toJS(self.stampCardEditing);
            service.getInitCardNumber(newEmployeeCode).done((value) => {
                if (value && value.length <= ce.digitsNumber) {
                    self.currentEmployee().cardNo(value);
                } else {
                    self.currentEmployee().cardNo("");
                }
            });
        }

        isError() {
            let self = this;
            if (self.currentStep() == 'CPS002_15') {
                let controls = self.listItemCls();
                lv.checkError(controls);
            } else {
                $(".form_step1").trigger("validate");

            }
            if (nts.uk.ui.errors.hasError()) {
                return true;
            }
            return false;
        }

        completeStep0() {
            let self = this,
                ctyp = ko.toJS(self.createTypeId),
                employee = self.currentEmployee(),
                command = {
                    EmployeeCode: employee.employeeCode(),
                    cardNo: employee.cardNo(),
                    LoginId: employee.loginId(),
                    employeeName: employee.employeeName(),
                    password: employee.password()
                };

            if (!self.isError()) {
                service.validateEmpInfo(command).done(() => {
                    if ([1, 2].indexOf(ctyp) > -1) {
                        self.currentStep('CPS002_14');
                    } else {
                        self.currentStep('CPS002_15');
                    }
                }).fail((error) => {
                    switch (error.messageId) {
                        case "Msg_345":
                            $('#employeeCode').ntsError('set', error);
                            break;
                        case "Msg_924":
                            $('#employeeName').ntsError('set', error);
                            break;
                        case "Msg_757":
                            $('#loginId').ntsError('set', error);
                            break;
                        case "Msg_346":
                            $('#cardNumber').ntsError('set', error);
                            break;
                        default:
                            if (error.errors && error.errors.length > 1) {
                                error.errors.forEach(err => {
                                    $('#password').ntsError('set', err);
                                });
                            } else {
                                $('#password').ntsError('set', error);
                            }      
                            break;
                    }
                });
            }
        }

        getUserSetting(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();

            service.getUserSetting().done((result: IUserSetting) => {
                if (!result) {
                    self.currentEmployee().employeeCode("");
                    self.currentEmployee().cardNo("");
                }

                dfd.resolve();
            });

            return dfd.promise();
        }

        completeStep1() {
            let self = this,
                cep = ko.toJS(self.copyEmployee),
                iui = ko.toJS(self.createTypeId() === 2),
                issc = ko.toJS(self.initSettingSelectedCode);

            if (cep.employeeId === '' && !iui) {
                dialog({ messageId: "Msg_349" });
                return;
            }

            if (iui && _.isNil(issc)) {
                dialog({ messageId: "Msg_356" });
                return;
            }

            self.currentStep('CPS002_15');
        }

        loadCopySettingCtgData() {
            let self = this;

            self.categoryList.removeAll();

            service.getCopySetting().done((result: Array<ICopySetting>) => {
                if (result.length > 0) {
                    self.categoryList(_.map(result, item => {
                        return new CategoryItem(item);
                    }));

                    self.categorySelectedCode(result[0].code);
                }
            }).fail((error) => {
                dialog({ messageId: error.message }).then(() => {
                    self.currentStep('CPS002_13');
                });
            });
        }

        loadInitSettingData() {
            let self = this;

            self.initValueList.removeAll();
            service.getAllInitValueSetting().done((result: Array<IInitValueSetting>) => {
                if (result.length) {
                    self.initValueList(_.map(result, item => {
                        return new InitSetting(item);
                    }));

                    self.initSettingSelectedCode.valueHasMutated();

                    if (self.initSettingSelectedCode() == '') {
                        if (self.employeeBasicInfo() && _.find(result, ['settingCode', self.employeeBasicInfo().initialValueCode])) {
                            self.initSettingSelectedCode(self.employeeBasicInfo().initialValueCode);
                        } else {
                            self.initSettingSelectedCode(result[0].settingCode);
                        }
                    } else {
                        if (!_.find(result, ctg => { return self.initSettingSelectedCode() == ctg.settingCode; })) {
                            self.initSettingSelectedCode(result[0].settingCode);
                        } else {
                            self.initSettingSelectedCode.valueHasMutated();
                        }
                    }

                    $("#initSearchBox input").focus();
                }
            }).fail((error) => {
                dialog({ messageId: error.message }).then(() => {
                    self.currentStep('CPS002_13');
                });
            }).always(() => {
                $('#search_box').show();
            });
        }

        prev() {
            let self = this,
                step = ko.toJS(self.currentStep),
                ctyp = ko.toJS(self.createTypeId);

            nts.uk.ui.errors.clearAll();
            (nts.uk.ui._viewModel || {
                kiban: {
                    errorDialogViewModel: {
                        errors: {
                            removeAll: () => { }
                        }
                    }
                }
            }).kiban.errorDialogViewModel.errors.removeAll();

            self.listItemCls.removeAll();

            setShared("CPS002A", null);
            setShared("imageId", null);
            self.defaultImgId("");

            if (['CPS002_14'].indexOf(step) > -1) {
                self.currentStep('CPS002_13');
            } else {
                if ([1, 2].indexOf(ctyp) > -1) {
                    self.currentStep('CPS002_14');
                } else {
                    self.currentStep('CPS002_13');
                }
            }
        }

        saveBasicInfo(command, employeeId) {
            let self = this,
                isInit = self.createTypeId() === 2,
                currentEmpInfo = self.employeeBasicInfo(),
                newEmpInfo = {
                    copyEmployeeId: command.employeeCopyId,
                    jobEntryDate: command.hireDate,
                    initialValueCode: self.initSettingSelectedCode(),
                    employeeID: employeeId,
                    employeeCreationMethod: self.createTypeId()
                };

            if (currentEmpInfo) {
                if (isInit) {
                    newEmpInfo.copyEmployeeId = newEmpInfo.copyEmployeeId == '' ? currentEmpInfo.copyEmployeeId : newEmpInfo.copyEmployeeId;
                } else {
                    newEmpInfo.initialValueCode = newEmpInfo.initialValueCode == '' ? currentEmpInfo.initialValueCode : newEmpInfo.initialValueCode;
                }
            }

            character.save('NewEmployeeBasicInfo', newEmpInfo);
        }


        finish() {
            let self = this,
                command = ko.toJS(self.currentEmployee());

            //add atr
            command.employeeCopyId = self.copyEmployee().employeeId;
            command.initSettingId = self.currentInitSetting().itemId;

            // reload data
            self.layoutData.refresh();
            command.inputs = self.layoutData();
            command.createType = self.createTypeId();
            command.categoryName = nts.uk.resource.getText("CPS001_152");
            command.itemName = nts.uk.resource.getText("CPS001_150");

            if (!self.isError()) {
                service.addNewEmployee(command).done((employeeId) => {
                    self.saveBasicInfo(command, employeeId);

                    nts.uk.ui.windows.sub.modal('/view/cps/002/h/index.xhtml', { dialogClass: "finish", title: '' }).onClosed(() => {
                        if (getShared('isContinue')) {
                            self.checkLicense();
                            self.currentStep('CPS002_13');
                            self.start();
                            self.getUserSetting();
                        } else {
                            jump('/view/cps/001/a/index.xhtml', { employeeId: employeeId });
                        }
                    });
                }).fail(error => {
                    alertError({ messageId: error.messageId, messageParams: error.parameterIds });
                })
            }
        }

        openEModal(param, data) {
            let self: ViewModel = __viewContext['viewModel'],
                isCardNoMode = param === 'true' ? true : false,
                useSetting = self.currentUseSetting(),
                employee = self.currentEmployee();

            setShared("empCodeMode", isCardNoMode);

            subModal('/view/cps/002/e/index.xhtml', { title: '' }).onClosed(() => {
                let result = getShared("CPS002_PARAM_MODE_EMP_CODE"),
                    currentEmp = self.currentEmployee();

                if (result) {
                    $("#employeeCode").ntsError("clear");
                    if (param === isCardNoMode) {
                        currentEmp.cardNo(result);
                        currentEmp.cardNo.valueHasMutated();
                    } else {
                        currentEmp.employeeCode(result);
                        currentEmp.employeeCode.valueHasMutated();
                    }
                }
            });
        }

        openJModal(param, data) {
            let self: ViewModel = __viewContext['viewModel'],
                isCardNoMode = param === 'true' ? true : false,
                useSetting = self.currentUseSetting(),
                employee = self.currentEmployee();
            setShared("cardNoMode", isCardNoMode);

            subModal('/view/cps/002/j/index.xhtml', { title: '' }).onClosed(() => {

                let result = getShared("CPS002_PARAM_MODE_CARDNO"),
                    currentEmp = self.currentEmployee();
                if (result) {
                    $("#cardNumber").ntsError("clear");
                    currentEmp.cardNo(result);
                    currentEmp.cardNo.valueHasMutated();
                }
            });
        }

        openFModal() {
            subModal('/view/cps/002/f/index.xhtml', { title: '' }).onClosed(() => { });
        }

        openGModal() {
            let self = this;

            subModal('/view/cps/002/g/index.xhtml', { title: '' }).onClosed(() => {
                if (getShared("userSettingStatus")) {
                    service.getUserSetting().done((result: IUserSetting) => {
                        self.currentUseSetting(new UserSetting(result));
                        self.getLastRegHistory(result);
                    });
                }
            });
        }

        openIModal() {
            let self = this,
                avatarId = self.defaultImgId(),
                employee = self.currentEmployee();

            if (avatarId != "") {
                setShared("CPS002A", avatarId);
            }

            if (self.isAllowAvatarUpload()) {
                setShared("openIDialog", employee.avatarOrgId());

                subModal('/view/cps/002/i/index.xhtml', { title: '' }).onClosed(() => {
                    let dataShare = getShared("imageId");

                    if (dataShare) {
                        employee.avatarOrgId(dataShare.imageOriginalId);
                        employee.avatarCropedId(dataShare.imageCropedId);
                        employee.fileName(dataShare.fileName);
                        self.defaultImgId(dataShare.imageOriginalId);
                    }
                });
            }
        }

        openInitModal() {
            subModal('/view/cps/009/a/index.xhtml', { title: '', height: 680, width: 1250 }).onClosed(() => { });
        }

        checkLicense() {
            let self = this;

            service.licenseCheck().done((data: ILicensenCheck) => {
                self.licenseCheck(text("CPS001_154", [data.registered, data.maxRegistered]));
                self.licenseCheckDipslay(data.display);

                if (!!data.message) {
                    self.classWarning('color-schedule-error');
                    alertWarning({ messageId: data.message, messageParams: [data.canBeRegistered] }).then(() => {
                        if (data.message === 'Msg_1370') {
                            jump('/view/ccg/008/a/index.xhtml');
                        }
                    });
                } else {
                    self.classWarning('');
                }
            });
        }
    }

    class BoxModel {
        id: number;
        name: string;
        constructor(id, name) {
            var self = this;
            self.id = id;
            self.name = name;
        }
    }

    class Employee {
        employeeName: KnockoutObservable<string> = ko.observable("");
        employeeCode: KnockoutObservable<string> = ko.observable("");
        hireDate: KnockoutObservable<Date> = ko.observable(moment().toDate());
        cardNo: KnockoutObservable<string> = ko.observable("");
        loginId: KnockoutObservable<string> = ko.observable("");
        password: KnockoutObservable<string> = ko.observable("");
        avatarOrgId: KnockoutObservable<string> = ko.observable("");
        avatarCropedId: KnockoutObservable<string> = ko.observable("");
        categoryName: KnockoutObservable<string> = ko.observable("");
        itemName: KnockoutObservable<string> = ko.observable("");
        fileName: KnockoutObservable<string> = ko.observable("");

        clearData() {
            let self = this;
            self.employeeName("");
            self.employeeCode("");
            self.loginId("");
            self.cardNo("");
            self.password("");
            self.avatarOrgId("");
            self.avatarCropedId("");
            self.categoryName("");
            self.itemName("");
            self.fileName("");
        }
    }

    class EmployeeCopy {
        employeeId: string;
        employeeName: string;
        employeeCode: string;
        workplaceCode: string;
        workplaceId: string;
        workplaceName: string;

        constructor(param?: any) {
            this.employeeId = param ? param.employeeId : '';
            this.employeeName = param ? param.employeeName : '';
            this.employeeCode = param ? param.employeeCode : '';
            this.workplaceCode = param ? param.workplaceCode : '';
            this.workplaceId = param ? param.workplaceId : '';
            this.workplaceName = param ? param.workplaceName : '';
        }
    }

    interface IUserSetting {
        employeeCodeType: number;
        recentRegistrationType: number;
        cardNumberType: number;
        employeeCodeLetter: string;
        cardNumberLetter: string;
    }

    interface IEmpRegHistory {
        lastRegEmployee: IRegEmployee;
        lastRegEmployeeOfCompany: IRegEmployee;
    }

    interface IRegEmployee {
        employeeCd: string;
        employeeName: string;
    }


    interface IInitValueSetting {
        settingId: string;
        settingCode: string;
        settingName: string;
    }

    class InitSetting {
        itemId: string = '';
        itemCode: string = '';
        itemName: string = '';

        constructor(param?: any) {
            this.itemId = param ? param.settingId ? param.settingId : param.employeeId : '';
            this.itemCode = param ? param.settingCode ? param.settingCode : param.employeeCode : '';
            this.itemName = param ? param.settingName ? param.settingName : param.employeeName : '';
        }
    }

    interface IInitValueCtgSetting {
        categoryCd: string;
        categoryName: string;
    }

    interface ICopySetting {
        code: string;
        name: string;
    }

    class CategoryItem {
        code: string;
        name: string;
        constructor(param?: any) {
            this.code = param ? param.categoryCd ? param.categoryCd : param.code : '';
            this.name = param ? param.categoryName ? param.categoryName : param.name : '';

        }
    }

    class UserSetting {
        employeeCodeType: number;
        recentRegistrationType: number;
        cardNumberType: number;
        employeeCodeLetter: string;
        cardNumberLetter: string;

        constructor(param?: IUserSetting) {
            this.employeeCodeType = param ? param.employeeCodeType : 0;
            this.recentRegistrationType = param ? param.recentRegistrationType : 0;
            this.cardNumberType = param ? param.cardNumberType : 0;
            this.employeeCodeLetter = param ? param.employeeCodeLetter : "";
            this.cardNumberLetter = param ? param.cardNumberLetter : "";
        }
    }

    class SettingItem {
        itemCode: string
        itemName: string;
        isRequired: number;
        saveData: any;
        dataType: string;
        dateType: string;

        constructor(param?: any) {
            this.itemCode = param ? param.itemCode : '';
            this.itemName = param ? param.itemName : '';
            this.isRequired = param ? param.isRequired : 0;
            this.saveData = param ? param.saveData : null;
            this.dataType = param ? param.dataType : '';
            this.dateType = param ? param.dateType : '';
            this.saveData.value = this.genString(this);
        }

        genString(item: SettingItem) {
            if (item.dataType === "DATE" && item.saveData.value) {
                return this.genDateString(item.saveData.value, item.dateType);
            }

            if (item.dataType === "TIME" && item.saveData.value) {
                return nts.uk.time.parseTime(item.saveData.value, true).format();
            }

            if (item.dataType === "TIMEPOINT" && item.saveData.value) {
                return window['nts']['uk']['time']['minutesBased']['clock']['dayattr']['create'](item.saveData.value).fullText;
            }

            return item.saveData.value;
        }

        genDateString(value, dateType) {
            let formatString = "yyyy/MM/dd";

            switch (dateType) {
                case "YEARMONTHDAY":
                    formatString = "yyyy/MM/dd";
                    break;
                case "YEARMONTH":
                    formatString = "yyyy/MM";
                    break;
                case "YEAR":
                    formatString = "yyyy";
                    break;
            }

            return nts.uk.time.formatDate(new Date(value), formatString);
        }
    }

    interface ILayout {
        layoutCode?: string;
        layoutName?: string;
        maintenanceLayoutID: string;
        itemsClassification?: Array<any>;
        classificationItems?: Array<any>;
        standardDate?: string;
        wrkPlaceStartDate?: string;
    }

    class EmpRegHistory {
        lastRegEmployee: KnockoutObservable<RegEmployee> = ko.observable(null);
        lastRegEmployeeOfCompany: KnockoutObservable<RegEmployee> = ko.observable(null);

        constructor(param: IEmpRegHistory) {
            this.lastRegEmployee(param ? param.lastRegEmployee : null);
            this.lastRegEmployeeOfCompany(param ? param.lastRegEmployeeOfCompany : null);
        }
    }

    class IEmployeeBasicInfo {
        copyEmployeeId: string;
        jobEntryDate: Date;
        initialValueCode: string;
        employeeID: string;
        employeeCreationMethod: number;
    }

    class RegEmployee {
        employeeCd: string;
        employeeName: string;

        constructor(employeeCd: string, employeeName: string) {
            this.employeeCd = employeeCd;
            this.employeeName = employeeName;
        }
    }

    interface IRoleAuth {
        allowMapUpload: number;
        allowMapBrowse: number;
        allowDocRef: number;
        allowDocUpload: number;
        allowAvatarUpload: number;
        allowAvatarRef: number;

    }

    interface IStampCardEditing {
        method: EDIT_METHOD;
        digitsNumber: number;
    }

    enum EDIT_METHOD {
        PreviousZero = 1,
        AfterZero = 2,
        PreviousSpace = 3,
        AfterSpace = 4
    }

    enum CardNoValType {
        //頭文字指定 (InitialDesignation)
        INIT_DESIGNATION = 1,
        //空白 (Blank)
        BLANK = 2,
        //社員コードと同じ (SameAsEmployeeCode)
        SAME_AS_EMP_CODE = 3,
        //最大値 (MaxValue)
        MAXVALUE = 4,
        //会社コード＋社員コード (CompanyCodeAndEmployeeCode)
        CPC_AND_EMPC = 5
    }

    enum POSITION {
        Previous = 0,
        After = 1
    }

    interface IPersonAuth {
        functionNo: number;
        functionName: string;
        available: boolean;
        description: string;
        orderNumber: number;
    }

    enum FunctionNo {
        No1_Allow_DelEmp = 1, // có thể delete employee ở đăng ký thông tin cá nhân
        No2_Allow_UploadAva = 2, // có thể upload ảnh chân dung employee ở đăng ký thông tin cá nhân
        No3_Allow_RefAva = 3,// có thể xem ảnh chân dung employee ở đăng ký thông tin cá nhân
        No4_Allow_UploadMap = 4, // có thể upload file bản đồ ở đăng ký thông tin cá nhân
        No5_Allow_RefMap = 5, // có thể xem file bản đồ ở đăng ký thông tin cá nhân
        No6_Allow_UploadDoc = 6,// có thể upload file điện tử employee ở đăng ký thông tin cá nhân
        No7_Allow_RefDoc = 7,// có thể xem file điện tử employee ở đăng ký thông tin cá nhân
        No8_Allow_Print = 8,  // có thể in biểu mẫu của employee ở đăng ký thông tin cá nhân
        No9_Allow_SetCoppy = 9,// có thể setting copy target item khi tạo nhân viên mới ở đăng ký mới thông tin cá nhân
        No10_Allow_SetInit = 10, // có thể setting giá trị ban đầu nhập vào khi tạo nhân viên mới ở đăng ký mới thông tin cá nhân
        No11_Allow_SwitchWpl = 11  // Lọc chọn lựa phòng ban trực thuộc/workplace trực tiếp theo bộ phận liên kết cấp dưới tại đăng ký thông tin cá nhân
    }

    interface ILicensenCheck {
        display: boolean;
        registered: number;
        canBeRegistered: number;
        maxRegistered: number;
        message: string;
        licenseKey: string;
        status: string;
    }
}