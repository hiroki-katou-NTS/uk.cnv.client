module nts.uk.pr.view.kmf001.h {
    export module viewmodel {
        import Enum = service.model.Enum;
        import SubstVacationSettingDto = service.model.SubstVacationSettingDto;
        import EmpSubstVacationDto = service.model.EmpSubstVacationDto;
        import EmploymentSettingDto = service.model.EmploymentSettingDto;
        import EmploymentSettingFindDto = service.model.EmploymentSettingFindDto;

        export class ScreenModel {

            employmentInitialized: boolean;
            //list component option
            selectedItem: KnockoutObservable<string>;
            listComponentOption: KnockoutObservable<any>;
            alreadySettingList: KnockoutObservableArray<any>;

            // Employment name
            selectedName: KnockoutObservable<string>;

            // Service.
            service: service.Service;

            // Switch button data source
            vacationExpirationEnums: KnockoutObservableArray<Enum>;
            applyPermissionEnums: KnockoutObservableArray<Enum>;
            manageDistinctEnums: KnockoutObservableArray<Enum>;
            
            //
            h32 : KnockoutObservableArray<Enum>;
            h34 : KnockoutObservableArray<Enum>;

            // Temp
            employmentList: KnockoutObservableArray<ItemModel>;
            selectedContractTypeCode: KnockoutObservable<string>;

            // Model
            settingModel: KnockoutObservable<SubstVacationSettingModel>;
            empSettingModel: KnockoutObservable<EmpSubstVacationModel>;

            isComManaged: KnockoutObservable<boolean>;
            hasEmp: KnockoutObservable<boolean>;
            isEmpManaged: KnockoutObservable<boolean>;

            deleteEnable: KnockoutObservable<boolean>;

            employmentVisible: KnockoutObservable<boolean>;

            // Dirty checker
            dirtyChecker: nts.uk.ui.DirtyChecker;
            //item H32
            selectedId: KnockoutObservable<any>;
            constructor() {
                var self = this;
                self.employmentInitialized = false;
                self.service = service.instance;
                self.selectedItem = ko.observable('');
                self.selectedName = ko.observable('');
                self.alreadySettingList = ko.observableArray([]);

                self.vacationExpirationEnums = ko.observableArray([]);
                self.applyPermissionEnums = ko.observableArray([]);
                self.manageDistinctEnums = ko.observableArray([]);
                self.h32 =  ko.observableArray([]);
                self.h34 =  ko.observableArray([]);

                self.settingModel = ko.observable(null);
                self.empSettingModel = ko.observable(null);
                self.settingModel = ko.observable(
                    new SubstVacationSettingModel({
                        isManage: 1,
                        expirationDate: 0,
                        allowPrepaidLeave: 1,
                        manageDeadline: 0,
                        linkingManagementATR : 1
                        
                    }));
                self.empSettingModel = ko.observable(
                    new EmpSubstVacationModel({
                        contractTypeCode: "",
                        isManage: 1,
                        expirationDate: 0,
                        allowPrepaidLeave: 1
                    }));

                self.isComManaged = ko.computed(function() {
                    return self.settingModel().isManage() == 1;
                });

                self.hasEmp = ko.observable(true);
                self.saveDisable = ko.observable(true);
                self.deleteEnable = ko.observable(true);
                self.isEmpManaged = ko.computed(function() {
                    return self.hasEmp() && self.empSettingModel().isManage() == 1;
                });

                self.selectedContractTypeCode = ko.observable('');

                //list Emp
                self.listComponentOption = {
                    isShowAlreadySet: true,
                    isMultiSelect: false,
                    listType: ListType.EMPLOYMENT,
                    selectType: SelectType.SELECT_BY_SELECTED_CODE,
                    selectedCode: this.selectedItem,
                    isDialog: false,
                    alreadySettingList: self.alreadySettingList
                };

                self.employmentVisible = ko.observable(self.settingModel().isManage() == 1);
                self.selectedId = ko.observable(0);
            }

            /**
             * Start page.
             */
            private startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();

                self.employmentList = ko.observableArray<ItemModel>([]);
                for (let i = 1; i < 9; i++) {
                    self.employmentList.push(new ItemModel('0' + i, '基本給', i % 3 === 0));
                }

                // Load enums
                $.when(self.loadVacationExpirationEnums(), self.loadApplyPermissionEnums(), self.loadManageDistinctEnums(), self.loadEmploymentList() ,self.h32f() , self.h34f()).done(function() {
                    self.loadComSettingDetails();
                    self.selectedItem(self.employmentList()[0].code);
                    self.selectedName(self.employmentList()[0].name);
                    self.checkDeleteAvailability();
                    $('#company-manage').focus();
                    dfd.resolve();
                });
                return dfd.promise();
            }

            private checkComManaged(): void {
                let self = this;
                let dfd = $.Deferred();
                if (!self.employmentInitialized) {
                    self.employmentInitialized = true;

                    self.selectedItem.subscribe(function(data: string) {
                        if (data) {
                            self.empSettingModel().contractTypeCode(data);
                            self.loadEmpSettingDetails(data);
                            self.hasEmp(true);
                            self.saveDisable(true);
                            self.checkDeleteAvailability();

                            // Set displayed Employee name
                            let employmentList: Array<UnitModel> = $('#left-content').getDataList();
                            let selectedEmp = _.find(employmentList, { 'code': data });
                            self.selectedName(':' + selectedEmp.name);
                        } else {
                            self.selectedName('');
                            self.hasEmp(false);
                            self.saveDisable(false);
                            self.deleteEnable(false);
                        }
                    });

                    $('#left-content').ntsListComponent(this.listComponentOption).done(function() {
                        if (!$('#left-content').getDataList() || $('#left-content').getDataList().length == 0) {
                            self.deleteEnable(false);
                            nts.uk.ui.dialog.info({ messageId: "Msg_146", messageParams: [] }).then(function() {
                                $('a[role="tab-navigator"][href="#tabpanel-1"]').click();
                            });
                            self.hasEmp(false);
                            dfd.resolve();
                        } else {
                            self.selectedItem($('#left-content').getDataList()[0].code);
                        }
                        $('#employment-manage').focus();
                        self.selectedItem.valueHasMutated();
                    });
                } else {
                    dfd.resolve();
                }
            }
            private loadEmploymentList(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred<any>();
                //get list employment
                self.alreadySettingList([]);
                self.service.findAllByEmployment().done((data: Array<string>) => {
                    for (let empContractTypeCode of data) {
                        self.alreadySettingList.push({ code: empContractTypeCode, isAlreadySetting: true });
                    }
                    dfd.resolve();
                });
                return dfd.promise();
            }

            private loadVacationExpirationEnums(): JQueryPromise<Array<Enum>> {
                let self = this;
                let dfd = $.Deferred();
                this.service.getVacationExpirationEnum().done(function(res: Array<Enum>) {
                    self.vacationExpirationEnums(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }

            private loadApplyPermissionEnums(): JQueryPromise<Array<Enum>> {
                let self = this;
                let dfd = $.Deferred();
                this.service.getApplyPermissionEnum().done(function(res: Array<Enum>) {
                    self.applyPermissionEnums(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
              private h32f(): JQueryPromise<Array<Enum>> {
                let self = this;
                let dfd = $.Deferred();
                this.service.getH32().done(function(res: Array<Enum>) {
                    self.h32(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }
            
             private h34f(): JQueryPromise<Array<Enum>> {
                let self = this;
                let dfd = $.Deferred();
                this.service.getH34().done(function(res: Array<Enum>) {
                    self.h34(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }

            private loadManageDistinctEnums(): JQueryPromise<Array<Enum>> {
                let self = this;
                let dfd = $.Deferred();
                this.service.getManageDistinctEnum().done(function(res: Array<Enum>) {
                    self.manageDistinctEnums(res);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                });
                return dfd.promise();
            }

            private loadComSettingDetails(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.clearErrorComSetting();
                nts.uk.ui.block.grayout();
                this.service.findComSetting().done(function(res: SubstVacationSettingDto) {
                    if (res) {
                        self.settingModel().isManage(res.manageDistinct);
                        self.settingModel().expirationDate(res.expirationDate);
                        self.settingModel().allowPrepaidLeave(res.allowPrepaidLeave);
                        self.settingModel().manageDeadline(res.manageDeadline);
                    } else {
                        self.settingModel().isManage(self.manageDistinctEnums()[0].value);
                        self.settingModel().expirationDate(self.vacationExpirationEnums()[0].value);
                        self.settingModel().allowPrepaidLeave(self.applyPermissionEnums()[0].value);
                    }
                    self.employmentVisible(self.settingModel().isManage() == 1);
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });

                return dfd.promise();
            }

            private loadEmpSettingDetails(contractTypeCode: string): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                self.clearErrorEmpSetting();
                nts.uk.ui.block.grayout();
                this.service.findEmpSetting(contractTypeCode).done(function(res: EmpSubstVacationDto) {
                    if (res) {
                        self.empSettingModel().contractTypeCode(res.contractTypeCode);
                        self.empSettingModel().isManage(res.manageDistinct);
                        self.empSettingModel().expirationDate(res.expirationDate);
                        self.empSettingModel().allowPrepaidLeave(res.allowPrepaidLeave);
                    } else {
                        self.empSettingModel().isManage(self.manageDistinctEnums()[0].value);
                        self.empSettingModel().expirationDate(self.vacationExpirationEnums()[0].value);
                        self.empSettingModel().allowPrepaidLeave(self.applyPermissionEnums()[0].value);
                    }
                    self.checkDeleteAvailability();
                    dfd.resolve();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.messageId);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
                return dfd.promise();
            }

            public saveComSetting(): void {
                let self = this;
                if (!self.validateComSetting()) {
                    return;
                }

                nts.uk.ui.block.grayout();

                this.service.saveComSetting(self.settingModel().toSubstVacationSettingDto()).done(function() {
                    // Msg_15
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                    self.loadComSettingDetails();
                }).fail(function(res) {
                    nts.uk.ui.dialog.alertError(res.message);
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            public saveEmpSetting(): void {
                let self = this;
                let dfd = $.Deferred();
                if (!self.validateEmpSetting()) {
                    return;
                }

                nts.uk.ui.block.grayout();

                this.service.saveEmpSetting(self.empSettingModel().toEmpSubstVacationDto()).done(function() {
                    self.alreadySettingList.push({ code: self.selectedItem(), isAlreadySetting: true });
                    self.loadEmpSettingDetails(self.selectedItem());
                    self.checkDeleteAvailability();
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" });
                }).always(() => {
                    nts.uk.ui.block.clear();
                });
            }

            public deleteEmpSetting(): void {
                let self = this;
                let dfd = $.Deferred();
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(function() {
                    self.service.deleteEmpSetting(self.selectedItem()).done(function() {
                        // Remove item from setting list (un-tick)
                        self.alreadySettingList.remove(function(item) { return item.code == self.selectedItem() });

                        // Reload setting (empty out fields)
                        self.loadEmpSettingDetails(self.selectedItem());
                        self.checkDeleteAvailability();
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    });
                });
            }

            private validateComSetting(): boolean {
                let self = this;
                return true;
            }

            private clearErrorComSetting(): void {
                if (!$('.nts-input').ntsError('hasError')) {
                    return;
                }
                $('input').ntsError('clear');
            }

            private validateEmpSetting(): boolean {
                let self = this;
                return true;
            }

            private clearErrorEmpSetting(): void {
                if (!$('.nts-input').ntsError('hasError')) {
                    return;
                }
                $('input').ntsError('clear');
            }

            // check if delete is available
            private checkDeleteAvailability() {
                var self = this;
                var match = ko.utils.arrayFirst(self.alreadySettingList(), function(item) {
                    return item.code == self.selectedItem();
                });
                self.deleteEnable(!!match);
            }
        }

        // Model class
        export class SubstVacationSettingModel {
            isManage: KnockoutObservable<number>;
            expirationDate: KnockoutObservable<number>;
            allowPrepaidLeave: KnockoutObservable<number>;
            manageDeadline:  KnockoutObservable<number>;

            constructor(dto: SubstVacationSettingDto) {
                this.isManage = ko.observable(dto.isManage);
                this.expirationDate = ko.observable(dto.expirationDate);
                this.allowPrepaidLeave = ko.observable(dto.allowPrepaidLeave);
                this.manageDeadline = ko.observable(dto.manageDeadline);
            }

            public toSubstVacationSettingDto(): SubstVacationSettingDto {
                return new SubstVacationSettingDto(this.isManage(), this.expirationDate(), this.allowPrepaidLeave() , this.manageDeadline() , 0);
            }
        }

        export class EmpSubstVacationModel extends SubstVacationSettingModel {
            contractTypeCode: KnockoutObservable<string>;

            constructor(dto: EmpSubstVacationDto) {
                super(new SubstVacationSettingDto(dto.isManage, dto.expirationDate, dto.allowPrepaidLeave));
                this.contractTypeCode = ko.observable(dto.contractTypeCode);
            }

            public toEmpSubstVacationDto(): EmpSubstVacationDto {
                return new EmpSubstVacationDto(this.contractTypeCode(),
                    new SubstVacationSettingDto(this.isManage(), this.expirationDate(), this.allowPrepaidLeave()));
            }
        }
        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }
        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }
        export interface UnitModel {
            code: string;
            name?: string;
            affiliationName?: string;
            isAlreadySetting?: boolean;
        }
        // Temp
        export class ItemModel {
            code: string;
            name: string;
            alreadySet: boolean;

            constructor(code: string, name: string, alreadySet: boolean) {
                this.code = code;
                this.name = name;
                this.alreadySet = alreadySet;
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
}