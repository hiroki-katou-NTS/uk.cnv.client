module cmm001.a {
    export class ViewModel {

        gridColumns: KnockoutObservableArray<any>;
        currentCompany: KnockoutObservable<CompanyModel>;
        currentCompanyCode: KnockoutObservable<string>;
        sel001Data: KnockoutObservableArray<CompanyModel>;
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        displayAttribute: KnockoutObservable<boolean>;
        isUpdate: KnockoutObservable<boolean> = ko.observable(null);
        dirtyObject: nts.uk.ui.DirtyChecker;
        previousCurrentCode: string = null; //lưu giá trị của currentCode trước khi nó bị thay đổi

        constructor() {
            let self = this;
            self.init();
            self.currentCompanyCode.subscribe(function(newValue) {
                if (nts.uk.text.isNullOrEmpty(newValue)) {
                    return;
                } else {
                    self.isUpdate(true);
                    if (!nts.uk.text.isNullOrEmpty(newValue) && self.currentCompanyCode() !== self.previousCurrentCode) {
                        //goi check isDirty
                        if (self.dirtyObject.isDirty()) {
                            nts.uk.ui.dialog.confirm("変更された内容が登録されていません。\r\nよろしいですか。?").ifYes(function() {
                                self.processWhenCurrentCodeChange(newValue);
                            }).ifCancel(function() {
                                self.currentCompanyCode(self.previousCurrentCode);
                            })
                        } else {
                            self.processWhenCurrentCodeChange(newValue);
                        }
                    }
                }

            });

            self.displayAttribute.subscribe(function(newValue) {
                let $grid = $("#A_LST_001");
                var currentColumns = $grid.igGrid("option", "columns");
                var width = $grid.igGrid("option", "width");

                if (newValue) {
                    $('#A_SEL_001').ntsError('clear');
                    currentColumns[2].hidden = false;
                    $grid.igGrid("option", "width", "400px");
                    self.sel001Data([]);
                    self.reload(undefined);
                } else {
                    self.sel001Data([]);
                    currentColumns[2].hidden = true;
                    $grid.igGrid("option", "width", "400px");
                    service.getAllCompanys().done(function(data: Array<service.model.CompanyDto>) {
                        if (data.length > 0) {
                            _.each(data, function(obj: service.model.CompanyDto) {
                                let companyModel: CompanyModel;
                                companyModel = ko.mapping.fromJS(obj);
                                if (obj.displayAttribute === 1) {
                                    companyModel.displayAttribute('');
                                    self.sel001Data.push(ko.toJS(companyModel));
                                }
                            });
                            let companyCheckExist = _.find(self.sel001Data(), function(obj: CompanyModel) {
                                let newCompanyCode: string = ko.toJS(obj.companyCode);
                                let oldCompanyCode: string = (ko.toJS(self.currentCompanyCode));
                                return newCompanyCode === oldCompanyCode;
                            });
                            if (self.sel001Data().length > 0) {
                                self.isUpdate(true);
                                if (!companyCheckExist) {
                                    self.processWhenCurrentCodeChange(ko.toJS(self.sel001Data()[0].companyCode));
                                    self.currentCompanyCode(ko.toJS(self.sel001Data()[0].companyCode));
                                } else {
                                    self.processWhenCurrentCodeChange(self.currentCompanyCode());

                                }
                            } else {
                                self.resetData();
                                self.isUpdate(false);

                            }
                        }
                    });
                }
                $grid.igGrid("option", "columns", currentColumns);
            });
        }

        processWhenCurrentCodeChange(newValue: string) {
            let self = this;
            service.getCompanyDetail(newValue).done(function(company: service.model.CompanyDto) {
                if (company) {
                    if ($('.nts-editor').ntsError("hasError")) {
                        $('.save-error').ntsError('clear');
                    }
                    self.currentCompany().setDataForCurrentCompany(company);
                    self.previousCurrentCode = newValue;
                    self.dirtyObject.reset();
                } else {
                    self.currentCompany().resetCurrentCompany();
                }
            });
        }

        init(): void {
            let self = this;
            self.tabs = ko.observableArray([
                { id: 'tab-1', title: '会社基本情報', content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'tab-2', title: '会社所在地・連絡先', content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'tab-3', title: 'システム設定', content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) }
            ]);

            self.displayAttribute = ko.observable(true);
            self.selectedTab = ko.observable('tab-1');
            self.gridColumns = ko.observableArray([
                { headerText: '会社コード', prop: 'companyCode', width: 80 },
                { headerText: '名称', prop: 'companyName', width: 200 },
                { headerText: '廃止', prop: 'displayAttribute', width: 50, hidden: false }
            ]);

            self.currentCompany = ko.observable(null);
            self.currentCompanyCode = ko.observable('');

            self.sel001Data = ko.observableArray([]);
        }

        start(currentCode: string) {
            let self = this;
            let dfd = $.Deferred<any>();
            service.getAllCompanys().done(function(data: Array<service.model.CompanyDto>) {
                if (data.length > 0) {
                    self.isUpdate(true);
                    _.each(data, function(obj: service.model.CompanyDto) {
                        let companyModel: CompanyModel;
                        companyModel = ko.mapping.fromJS(obj);
                        if (obj.displayAttribute === 1) {
                            companyModel.displayAttribute('');
                        } else {
                            companyModel.displayAttribute('<i style="margin-left: 15px" class="icon icon-close"></i>');
                        }
                        self.sel001Data.push(ko.toJS(companyModel));
                    });
                    if (currentCode === undefined) {
                        self.currentCompany = ko.observable(new CompanyModel({
                            companyCode: ko.toJS(self.sel001Data()[0].companyCode),
                            address1: '',
                            companyName: '',
                            companyNameGlobal: '',
                            corporateMyNumber: '',
                            depWorkPlaceSet: 0,
                            displayAttribute: '',
                            termBeginMon: 0,
                            companyUseSet: null
                        }));
                        self.dirtyObject = new nts.uk.ui.DirtyChecker(self.currentCompany);
                        self.currentCompanyCode(self.currentCompany().companyCode());
                    } else {
                        self.currentCompanyCode(currentCode);
                    }
                } else {
                    self.isUpdate(false);
                }
                dfd.resolve();
            });
            return dfd.promise();
        }

        reload(currentCode: string) {
            let self = this;
            let dfd = $.Deferred<any>();
            service.getAllCompanys().done(function(data: Array<service.model.CompanyDto>) {
                if (data.length > 0) {
                    self.isUpdate(true);
                    _.each(data, function(obj: service.model.CompanyDto) {
                        let companyModel: CompanyModel;
                        companyModel = ko.mapping.fromJS(obj);
                        if (obj.displayAttribute === 1) {
                            companyModel.displayAttribute('');
                        } else {
                            companyModel.displayAttribute('<i style="margin-left: 15px" class="icon icon-close"></i>');
                        }
                        self.sel001Data.push(ko.toJS(companyModel));
                    });
                    self.dirtyObject = new nts.uk.ui.DirtyChecker(self.currentCompany);
                    if (currentCode == undefined) {
                        self.currentCompanyCode(ko.toJS(self.sel001Data()[0].companyCode));
                    } else {
                        self.currentCompanyCode(currentCode);
                    }
                } else {
                    self.isUpdate(false);
                }
                dfd.resolve();
            });
            return dfd.promise();
        }

        resetData() {
            let self = this;
            self.currentCompanyCode("");
            self.currentCompany().companyCode("");
            self.currentCompany().address1("");
            self.currentCompany().addressKana1("");
            self.currentCompany().address2("");
            self.currentCompany().addressKana2("");
            self.currentCompany().companyName("");
            self.currentCompany().companyNameGlobal("");
            self.currentCompany().companyNameAbb("");
            self.currentCompany().companyNameKana("");
            self.currentCompany().corporateMyNumber("");
            self.currentCompany().companyUseSet(new CompanyUseSet(0, 0, 0));
            self.currentCompany().depWorkPlaceSet(0);
            self.currentCompany().displayAttribute('');
            self.currentCompany().faxNo('');
            self.currentCompany().telephoneNo('');
            self.currentCompany().postal('');
            self.currentCompany().presidentName('');
            self.currentCompany().presidentJobTitle('');
            self.currentCompany().termBeginMon(1);
            self.currentCompany().selectedRuleCode('1');
            self.currentCompany().selectedRuleCode1('1');
            self.currentCompany().selectedRuleCode2('1');
            self.currentCompany().selectedRuleCode3('1');
            self.currentCompany().isDelete(false);
            self.currentCompany().editMode = true;
            self.currentCompany().isEnableCompanyCode(true);
            self.isUpdate(false);
            self.dirtyObject.reset();
            self.currentCompany().hasFocus(true);
        }

        clickRegister() {

            let self = this;
            let dfd = $.Deferred<any>();
            let currentCompany: CompanyModel;
            currentCompany = ko.toJS(self.currentCompany);
            if (!self.validateData()) {
                return;
            }
            if (currentCompany.isDelete) {
                currentCompany.displayAttribute = ko.observable("0");
            } else {
                currentCompany.displayAttribute = ko.observable("1");
            }
            let company: service.model.CompanyDto =
                new service.model.CompanyDto("", "", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "", 0, 0, 0, 0);

            company = self.convertCompanyDto(currentCompany);
            if (self.displayAttribute()) {
                if (self.isUpdate()) {
                    cmm001.a.service.updateData(company).done(function() {
                        self.sel001Data([]);
                        self.reload(company.companyCode);
                    });
                } else {
                    cmm001.a.service.addData(company).done(function() {
                        self.sel001Data([]);
                        self.reload(company.companyCode);
                    })
                }
            } else {
                if (self.isUpdate()) {
                    cmm001.a.service.updateData(company).done(function() {
                        self.sel001Data([]);
                        service.getAllCompanys().done(function(data: Array<service.model.CompanyDto>) {
                            if (data.length > 0) {
                                _.each(data, function(obj: service.model.CompanyDto) {
                                    let companyModel: CompanyModel;
                                    companyModel = ko.mapping.fromJS(obj);
                                    if (obj.displayAttribute === 1) {
                                        companyModel.displayAttribute('');
                                        self.sel001Data.push(ko.toJS(companyModel));
                                    }
                                });
                                self.dirtyObject.reset();
                                if (self.sel001Data().length > 0) {
                                    self.currentCompanyCode(ko.toJS(self.sel001Data()[0].companyCode));
                                }else{
                                    self.resetData();
                                    return;
                                }
                            }
                        });

                    });
                } else {
                    cmm001.a.service.addData(company).done(function() {
                        self.sel001Data([]);
                        service.getAllCompanys().done(function(data: Array<service.model.CompanyDto>) {
                            if (data.length > 0) {
                                _.each(data, function(obj: service.model.CompanyDto) {
                                    let companyModel: CompanyModel;
                                    companyModel = ko.mapping.fromJS(obj);
                                    if (obj.displayAttribute === 1) {
                                        companyModel.displayAttribute('');
                                        self.sel001Data.push(ko.toJS(companyModel));
                                    }
                                });
                                self.dirtyObject.reset();
                                self.currentCompanyCode(ko.toJS(self.sel001Data()[0].companyCode));
                            }
                        });
                    })
                }


            }
        }
        convertCompanyDto(company: CompanyModel): service.model.CompanyDto {
            let companyDto: service.model.CompanyDto =
                new service.model.CompanyDto("", "", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "", 0, 0, 0, 0);
            companyDto.companyCode = ko.toJS(company.companyCode);
            companyDto.companyName = ko.toJS(company.companyName);
            companyDto.companyNameGlobal = ko.toJS(company.companyNameGlobal);
            companyDto.companyNameAbb = ko.toJS(company.companyNameAbb);
            companyDto.companyNameKana = ko.toJS(company.companyNameKana);
            companyDto.corporateMyNumber = ko.toJS(company.corporateMyNumber);
            companyDto.use_Jj_Set = Number(ko.toJS(company.selectedRuleCode));
            companyDto.use_Kt_Set = Number(ko.toJS(company.selectedRuleCode1));
            companyDto.use_Qy_Set = Number(ko.toJS(company.selectedRuleCode2));
            companyDto.depWorkPlaceSet = Number(ko.toJS(company.selectedRuleCode3));
            companyDto.displayAttribute = Number(ko.toJS(company.displayAttribute));
            companyDto.termBeginMon = Number(ko.toJS(company.termBeginMon));
            companyDto.address1 = ko.toJS(company.address1);
            companyDto.address2 = ko.toJS(company.address2);
            companyDto.addressKana1 = ko.toJS(company.addressKana1);
            companyDto.addressKana2 = ko.toJS(company.addressKana2);
            companyDto.telephoneNo = ko.toJS(company.telephoneNo);
            companyDto.faxNo = ko.toJS(company.faxNo);
            companyDto.postal = ko.toJS(company.postal);
            companyDto.presidentJobTitle = ko.toJS(company.presidentJobTitle);
            companyDto.presidentName = ko.toJS(company.presidentName);
            return companyDto;
        }
        validateData(): boolean {
            $(".nts-editor").ntsEditor("validate");
            $("#A_INP_002").ntsEditor("validate");
            $("#A_INP_003").ntsEditor("validate");
            $("#A_INP_004").ntsEditor("validate");
            $("#A_INP_005").ntsEditor("validate");
            $("#B_INP_001").ntsEditor("validate");
            $("#B_INP_002").ntsEditor("validate");
            $("#B_INP_003").ntsEditor("validate");
            $("#C_INP_002").ntsEditor("validate");
            $("#C_INP_003").ntsEditor("validate");
            $("#C_INP_004").ntsEditor("validate");
            $("#C_INP_005").ntsEditor("validate");
            $("#C_INP_006").ntsEditor("validate");
            $("#C_INP_007").ntsEditor("validate");
            $("#D_SEL_001").ntsEditor("validate");
            $("#D_SEL_002").ntsEditor("validate");
            $("#D_SEL_003").ntsEditor("validate");
            $("#D_SEL_004").ntsEditor("validate");
            $("#D_SEL_005").ntsEditor("validate");
            let errorA: boolean = false;
            let errorB: boolean = false;
            let errorC: boolean = false;
            let errorD: boolean = false;

            errorA = $("#A_INP_002").ntsError('hasError') || $("#A_INP_003").ntsError('hasError')
                || $("#A_INP_004").ntsError('hasError')
                || $("#A_INP_005").ntsError('hasError');
            errorB = $("#B_INP_002").ntsError('hasError') || $("#B_INP_001").ntsError('hasError')
                || $("#B_INP_003").ntsError('hasError');
            errorC = $("#C_INP_002").ntsError('hasError') || $("#C_INP_003").ntsError('hasError')
                || $("#C_INP_004").ntsError('hasError') || $("#C_INP_005").ntsError('hasError')
                || $("#C_INP_006").ntsError('hasError') || $("#C_INP_007").ntsError('hasError');
            errorD = $("#D_SEL_001").ntsError('hasError') || $("#D_SEL_002").ntsError('hasError')
                || $("#D_SEL_003").ntsError('hasError') || $("#D_SEL_004").ntsError('hasError')
                || $("#D_SEL_005").ntsError('hasError');

            if ($(".nts-editor").ntsError('hasError') || errorA || errorB || errorC || errorD) {
                return false;
            }
            return true;
        }
    }
    class CompanyModel {
        companyCode: KnockoutObservable<string>;
        isEnableCompanyCode: KnockoutObservable<boolean> = ko.observable(true);
        address1: KnockoutObservable<string>;
        address2: KnockoutObservable<string>;
        addressKana1: KnockoutObservable<string>;
        addressKana2: KnockoutObservable<string>;
        companyName: KnockoutObservable<string>;
        companyNameGlobal: KnockoutObservable<string>;
        companyNameAbb: KnockoutObservable<string>;
        companyNameKana: KnockoutObservable<string>;
        corporateMyNumber: KnockoutObservable<string>;
        depWorkPlaceSet: KnockoutObservable<number>;
        displayAttribute: KnockoutObservable<string>;
        faxNo: KnockoutObservable<string>;
        postal: KnockoutObservable<string>;
        presidentName: KnockoutObservable<string>;
        presidentJobTitle: KnockoutObservable<string>;
        telephoneNo: KnockoutObservable<string>;
        termBeginMon: KnockoutObservable<number>;
        companyUseSet: KnockoutObservable<CompanyUseSet>;
        isDelete: KnockoutObservable<boolean>;
        itemList: KnockoutObservable<any>;
        //switch
        roundingRules: KnockoutObservableArray<RoundingRule>;
        selectedRuleCode: KnockoutObservable<string>;
        selectedRuleCode1: KnockoutObservable<string>;
        selectedRuleCode2: KnockoutObservable<string>;
        roundingRules3: KnockoutObservableArray<RoundingRule>;
        selectedRuleCode3: KnockoutObservable<string>;
        hasFocus: KnockoutObservable<boolean> = ko.observable(true);
        editMode: boolean = true;// mode reset or not reset

        constructor(param: ICompany) {
            let self = this;
            self.init(param);

        }

        setDataForCurrentCompany(company: any) {
            let self = this;
            self.companyCode(company.companyCode);
            self.companyName(company.companyName);
            self.companyNameGlobal(company.companyNameGlobal);
            self.companyNameAbb(company.companyNameAbb);
            self.companyNameKana(company.companyNameKana);
            self.corporateMyNumber(company.corporateMyNumber);
            self.address2(company.address2);
            self.address1(company.address1);
            self.addressKana1(company.addressKana1);
            self.addressKana2(company.addressKana2);
            self.depWorkPlaceSet(company.depWorkPlaceSet);
            self.displayAttribute(company.displayAttribute.toString());
            if (company.displayAttribute === 1) {
                self.isDelete(false);
            } else {
                self.isDelete(true);
            }
            self.faxNo(company.faxNo);
            self.postal(company.postal);
            self.presidentName(company.presidentName);
            self.presidentJobTitle(company.presidentJobTitle);
            self.telephoneNo(company.telephoneNo);
            self.termBeginMon(company.termBeginMon);
            self.companyUseSet(new CompanyUseSet(company.use_Kt_Set, company.use_Qy_Set, company.use_Jj_Set));
            self.selectedRuleCode(company.use_Jj_Set.toString());
            self.selectedRuleCode1(company.use_Kt_Set.toString());
            self.selectedRuleCode2(company.use_Qy_Set.toString());
            self.selectedRuleCode3(company.depWorkPlaceSet.toString());
            self.isEnableCompanyCode(false);
        }

        resetCurrentCompany() {
            let self = this;
            self.editMode = false;
            self.address1('');
            self.address2('');
            self.addressKana1('');
            self.addressKana2('');
            self.companyName('');
            self.companyNameGlobal('');
            self.companyNameAbb('');
            self.companyNameKana('');
            self.corporateMyNumber('');
            self.depWorkPlaceSet(0);
            self.displayAttribute('');
            self.faxNo('');
            self.postal('');
            self.presidentName('');
            self.presidentJobTitle('');
            self.telephoneNo('');
            self.termBeginMon(0);
            self.companyUseSet(new CompanyUseSet(0, 0, 0));
            self.isDelete(false);
        }

        init(param: ICompany) {
            let self = this;
            self.companyCode = ko.observable(param.companyCode);
            self.address1 = ko.observable(param.address1);
            self.address2 = ko.observable(param.address2);
            self.addressKana1 = ko.observable(param.addressKana1);
            self.addressKana2 = ko.observable(param.addressKana2);
            self.companyName = ko.observable(param.companyName);
            self.companyNameGlobal = ko.observable(param.companyNameGlobal);
            self.companyNameAbb = ko.observable(param.companyNameAbb);
            self.companyNameKana = ko.observable(param.companyNameKana);
            self.corporateMyNumber = ko.observable(param.corporateMyNumber);
            self.depWorkPlaceSet = ko.observable(param.depWorkPlaceSet);
            self.displayAttribute = ko.observable(param.displayAttribute);
            self.faxNo = ko.observable(param.faxNo);
            self.postal = ko.observable(param.postal);
            self.presidentName = ko.observable(param.presidentName);
            self.presidentJobTitle = ko.observable(param.presidentJobTitle);
            self.telephoneNo = ko.observable(param.telephoneNo);
            self.termBeginMon = ko.observable(param.termBeginMon);
            self.companyUseSet = ko.observable(param.companyUseSet);
            self.isDelete = ko.observable(param.isDelete || false);
            //SWITCH
            self.roundingRules = ko.observableArray([
                new RoundingRule("1", '利用する'),
                new RoundingRule('0', '利用しない')
            ]);
            self.selectedRuleCode = ko.observable("");
            self.selectedRuleCode1 = ko.observable("");
            self.selectedRuleCode2 = ko.observable('');
            self.roundingRules3 = ko.observableArray([
                new RoundingRule("1", '区別する'),
                new RoundingRule('0', '区別しない')
            ]);
            self.selectedRuleCode3 = ko.observable("");
            self.itemList = ko.observableArray([
                { code: '1', name: '1月' },
                { code: '2', name: '2月' },
                { code: '3', name: '3月' },
                { code: '4', name: '4月' },
                { code: '5', name: '5月' },
                { code: '6', name: '6月' },
                { code: '7', name: '7月' },
                { code: '8', name: '8月' },
                { code: '9', name: '9月' },
                { code: '10', name: '10月' },
                { code: '11', name: '11月' },
                { code: '12', name: '12月' }
            ]);
        }
    }

    interface ICompany {
        companyCode: string;
        address1: string;
        address2?: string;
        addressKana1?: string;
        addressKana2?: string;
        companyName: string;
        companyNameGlobal: string;
        companyNameAbb?: string;
        companyNameKana?: string;
        corporateMyNumber: string;
        depWorkPlaceSet: number;
        displayAttribute: string;
        faxNo?: string;
        postal?: string;
        presidentName?: string;
        presidentJobTitle?: string;
        telephoneNo?: string;
        termBeginMon: number;
        companyUseSet: CompanyUseSet;
        isDelete?: boolean;
    }

    export class Address {
        address1: KnockoutObservable<string>;
        address2: KnockoutObservable<string>;
        addressKana1: KnockoutObservable<string>;
        addressKana2: KnockoutObservable<string>;
        constructor(address1: string, address2: string, addressKana1: string, addressKana2: string) {
            this.address1 = ko.observable(address1);
            this.address2 = ko.observable(address2);
            this.addressKana1 = ko.observable(addressKana1);
            this.addressKana2 = ko.observable(addressKana2);
        }
    }

    export class CompanyUseSet {
        useGrSet: number;
        useKtSet: number;
        useQySet: number;
        useJjSet: number;
        useAcSet: number;
        useGwSet: number;
        useHcSet: number;
        useLcSet: number;
        useBiSet: number;
        useRs01Set: number;
        useRs02Set: number;
        useRs03Set: number;
        useRs04Set: number;
        useRs05Set: number;
        useRs06Set: number;
        useRs07Set: number;
        useRs08Set: number;
        useRs09Set: number;
        useRs10Set: number;
        constructor(useKtSet: number, useQySet: number, useJjSet: number) {
            this.useGrSet = 0;
            this.useKtSet = useKtSet;
            this.useQySet = useQySet;
            this.useJjSet = useJjSet;
            this.useAcSet = 0;
            this.useGwSet = 0;
            this.useHcSet = 0;
            this.useLcSet = 0;
            this.useBiSet = 0;
            this.useRs01Set = 0;
            this.useRs02Set = 0;
            this.useRs03Set = 0;
            this.useRs04Set = 0;
            this.useRs05Set = 0;
            this.useRs06Set = 0;
            this.useRs07Set = 0;
            this.useRs08Set = 0;
            this.useRs09Set = 0;
            this.useRs10Set = 0;

        }
    }
    class RoundingRule {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }


    }

}