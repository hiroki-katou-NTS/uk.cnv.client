module nts.uk.com.view.cmm007.b {
    import SystemResourceDto = model.SystemResourceDto;
    import SystemResourceCommand = model.SystemResourceSaveCommand;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import blockUI = nts.uk.ui.block;

    export module viewmodel {
        export class ScreenModel {
            tabs: KnockoutObservableArray<any>;
            selectedTab: KnockoutObservable<string>;

            com_person:  KnockoutObservable<string>;
            com_employment:  KnockoutObservable<string>;
            com_class:  KnockoutObservable<string>;
            com_jobtitle:  KnockoutObservable<string>;
            com_department:  KnockoutObservable<string>;
            com_workplace:  KnockoutObservable<string>;
            com_equipmentClass: KnockoutObservable<string>;
            com_equipmentItem: KnockoutObservable<string>;
            com_office:  KnockoutObservable<string>;
            com_company:  KnockoutObservable<string>;
            com_contract:  KnockoutObservable<string>;
            com_user:  KnockoutObservable<string>;
            com_project:  KnockoutObservable<string>;
            com_adHocWork:  KnockoutObservable<string>;
            com_bindingTime:  KnockoutObservable<string>;
            com_attendanceDays:  KnockoutObservable<string>;
            com_absenceDays:  KnockoutObservable<string>;
            com_paidHoliday:  KnockoutObservable<string>;
            com_fundedPaidHoliday:  KnockoutObservable<string>;
            com_substituteWork:  KnockoutObservable<string>;
            com_substituteHoliday:  KnockoutObservable<string>;
            com_compensationHoliday:  KnockoutObservable<string>;
            com_exsessHoliday:  KnockoutObservable<string>;
            com_planedPaidHoliday:  KnockoutObservable<string>;
            workplace_Group: KnockoutObservable<string>;

            constructor(){
                let _self = this;

                _self.tabs = ko.observableArray([
                    {id: 'tab-1', title: nts.uk.resource.getText("CMM007_7"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true)},
                    {id: 'tab-2', title: nts.uk.resource.getText("CMM007_8"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true)},
                    {id: 'tab-3', title: nts.uk.resource.getText("CMM007_9"), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true)}
                ]);
                _self.selectedTab = ko.observable('tab-1');

                _self.com_person = ko.observable("");
                _self.com_employment = ko.observable("");
                _self.com_class = ko.observable("");
                _self.com_jobtitle = ko.observable("");
                _self.com_department = ko.observable("");
                _self.com_workplace = ko.observable("");
                _self.com_equipmentClass = ko.observable("");
                _self.com_equipmentItem = ko.observable("");
                _self.com_office = ko.observable("");
                _self.com_company = ko.observable("");
                _self.com_contract = ko.observable("");
                _self.com_user = ko.observable("");
                _self.com_project = ko.observable("");
                _self.com_adHocWork = ko.observable("");
                _self.com_bindingTime = ko.observable("");
                _self.com_attendanceDays = ko.observable("");
                _self.com_absenceDays = ko.observable("");
                _self.com_paidHoliday = ko.observable("");
                _self.com_fundedPaidHoliday = ko.observable("");
                _self.com_substituteWork = ko.observable("");
                _self.com_substituteHoliday = ko.observable("");
                _self.com_compensationHoliday = ko.observable("");
                _self.com_exsessHoliday = ko.observable("");
                _self.com_planedPaidHoliday = ko.observable("");
                _self.workplace_Group = ko.observable("");
            }

            /**
             * init default data when start page
             */
            public start_page(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                var _self = this;

                service.findListSystemResource().done(function(data: Array<SystemResourceDto>){
                    data.forEach(function(e){
                        switch (e.resourceId){
                            case SystemResourceId.Com_Person:
                               _self.com_person(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Employment:
                               _self.com_employment(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Class:
                               _self.com_class(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Jobtitle:
                               _self.com_jobtitle(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Department:
                               _self.com_department(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Workplace:
                               _self.com_workplace(e.resourceContent);
                               break;
                            case SystemResourceId.Com_EquipmentClass:
                               _self.com_equipmentClass(e.resourceContent);
                               break;
                            case SystemResourceId.Com_EquipmentItem:
                               _self.com_equipmentItem(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Office:
                               _self.com_office(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Company:
                               _self.com_company(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Contract:
                               _self.com_contract(e.resourceContent);
                               break;
                            case SystemResourceId.Com_User:
                               _self.com_user(e.resourceContent);
                               break;
                            case SystemResourceId.Com_Project:
                               _self.com_project(e.resourceContent);
                               break;
                            case SystemResourceId.Com_AdHocWork:
                               _self.com_adHocWork(e.resourceContent);
                               break;
                            case SystemResourceId.Com_BindingTime:
                               _self.com_bindingTime(e.resourceContent);
                               break;
                            case SystemResourceId.Com_AttendanceDays:
                               _self.com_attendanceDays(e.resourceContent);
                               break;
                            case SystemResourceId.Com_AbsenceDays:
                               _self.com_absenceDays(e.resourceContent);
                               break;
                            case SystemResourceId.Com_PaidHoliday:
                               _self.com_paidHoliday(e.resourceContent);
                               break;
                            case SystemResourceId.Com_FundedPaidHoliday:
                               _self.com_fundedPaidHoliday(e.resourceContent);
                               break;
                            case SystemResourceId.Com_SubstituteWork:
                               _self.com_substituteWork(e.resourceContent);
                               break;
                            case SystemResourceId.Com_SubstituteHoliday:
                               _self.com_substituteHoliday(e.resourceContent);
                               break;
                            case SystemResourceId.Com_CompensationHoliday:
                               _self.com_compensationHoliday(e.resourceContent);
                               break;
                            case SystemResourceId.Com_ExsessHoliday:
                               _self.com_exsessHoliday(e.resourceContent);
                               break;
                             case SystemResourceId.Com_PlanedPaidHoliday:
                               _self.com_planedPaidHoliday(e.resourceContent);
                               break;
                           case SystemResourceId.Workplace_Group:
                              _self.workplace_Group(e.resourceContent);
                              break;
                        }
                    });
                    dfd.resolve();
                });

                return dfd.promise();
            }

            /**
             * Save System Resource setting
             */
            public saveSysResourceSetting(): JQueryPromise<void> {
                let _self = this;

                 // Validate
                if (_self.hasError()) {
                    return;
                }

                blockUI.invisible();

                var dfd = $.Deferred<void>();

                var params = new SystemResourceCommand(_self.prepareDataToSave());
                nts.uk.ui.block.grayout();
                service.saveSysResourceSetting(params).done(function(){
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        dfd.resolve();
                        blockUI.clear();
                        $('#com_company').focus();
                    });
                }).always(() => {
                    nts.uk.ui.block.clear();
                });

                return dfd.promise();
            }

            /**
             * Prepare data for saving.
             */
            private prepareDataToSave(): Array<SystemResourceDto>{
                let _self = this;

                let data: Array<SystemResourceDto> = [ new SystemResourceDto(SystemResourceId.Com_Person, _self.com_person()),
                                         new SystemResourceDto(SystemResourceId.Com_Employment, _self.com_employment()),
                                         new SystemResourceDto(SystemResourceId.Com_Class, _self.com_class()),
                                         new SystemResourceDto(SystemResourceId.Com_Jobtitle, _self.com_jobtitle()),
                                         new SystemResourceDto(SystemResourceId.Com_Department, _self.com_department()),
                                         new SystemResourceDto(SystemResourceId.Com_Workplace, _self.com_workplace()),
                                         new SystemResourceDto(SystemResourceId.Com_EquipmentClass, _self.com_equipmentClass()),
                                         new SystemResourceDto(SystemResourceId.Com_EquipmentItem, _self.com_equipmentItem()),
                                         new SystemResourceDto(SystemResourceId.Com_Office, _self.com_office()),
                                         new SystemResourceDto(SystemResourceId.Com_Company, _self.com_company()),
                                         new SystemResourceDto(SystemResourceId.Com_Contract,_self.com_contract()),
                                         new SystemResourceDto(SystemResourceId.Com_User, _self.com_user()),
                                         new SystemResourceDto(SystemResourceId.Com_Project, _self.com_project()),
                                         new SystemResourceDto(SystemResourceId.Com_AdHocWork, _self.com_adHocWork()),
                                         new SystemResourceDto(SystemResourceId.Com_BindingTime, _self.com_bindingTime()),
                                         new SystemResourceDto(SystemResourceId.Com_AttendanceDays, _self.com_attendanceDays()),
                                         new SystemResourceDto(SystemResourceId.Com_AbsenceDays, _self.com_absenceDays()),
                                         new SystemResourceDto(SystemResourceId.Com_PaidHoliday, _self.com_paidHoliday()),
                                         new SystemResourceDto(SystemResourceId.Com_FundedPaidHoliday, _self.com_fundedPaidHoliday()),
                                         new SystemResourceDto(SystemResourceId.Com_SubstituteWork, _self.com_substituteWork()),
                                         new SystemResourceDto(SystemResourceId.Com_SubstituteHoliday, _self.com_substituteHoliday()),
                                         new SystemResourceDto(SystemResourceId.Com_CompensationHoliday, _self.com_compensationHoliday()),
                                         new SystemResourceDto(SystemResourceId.Com_ExsessHoliday, _self.com_exsessHoliday()),
                                         new SystemResourceDto(SystemResourceId.Com_PlanedPaidHoliday, _self.com_planedPaidHoliday()),
                                         new SystemResourceDto(SystemResourceId.Workplace_Group, _self.workplace_Group())];
                return data;
            }

            /**
             * Check Errors all input.
             */
            private hasError(): boolean {
                let _self = this;
                _self.clearErrors();
                $('#com_person').ntsEditor("validate");
                $('#com_employment').ntsEditor("validate");
                $('#com_class').ntsEditor("validate");
                $('#com_jobtitle').ntsEditor("validate");
                $('#com_department').ntsEditor("validate");
                $('#com_workplace').ntsEditor("validate");
                $('#com_equipmentClass').ntsEditor("validate");
                $('#com_equipmentItem').ntsEditor("validate");
                $('#com_office').ntsEditor("validate");
                $('#com_company').ntsEditor("validate");
                $('#com_contract').ntsEditor("validate");
                $('#com_user').ntsEditor("validate");
                $('#com_project').ntsEditor("validate");
                $('#com_adHocWork').ntsEditor("validate");
                $('#com_bindingTime').ntsEditor("validate");
                $('#com_attendanceDays').ntsEditor("validate");
                $('#com_absenceDays').ntsEditor("validate");
                $('#com_paidHoliday').ntsEditor("validate");
                $('#com_fundedPaidHoliday').ntsEditor("validate");
                $('#com_substituteWork').ntsEditor("validate");
                $('#com_substituteHoliday').ntsEditor("validate");
                $('#com_compensationHoliday').ntsEditor("validate");
                $('#com_exsessHoliday').ntsEditor("validate");
                $('#com_planedPaidHoliday').ntsEditor("validate");
                $('#workplace_Group').ntsEditor("validate");

                if ($('.nts-input').ntsError('hasError')) {
                    return true;
                }
                return false;
            }

            /**
             * Clear Errors
             */
            private clearErrors(): void {

                 // Clear errors
                $('#com_person').ntsEditor("clear");
                $('#com_employment').ntsEditor("clear");
                $('#com_class').ntsEditor("clear");
                $('#com_jobtitle').ntsEditor("clear");
                $('#com_department').ntsEditor("clear");
                $('#com_workplace').ntsEditor("clear");
                $('#com_equipmentClass').ntsEditor("clear");
                $('#com_equipmentItem').ntsEditor("clear");
                $('#com_office').ntsEditor("clear");
                $('#com_company').ntsEditor("clear");
                $('#com_contract').ntsEditor("clear");
                $('#com_user').ntsEditor("clear");
                $('#com_project').ntsEditor("clear");
                $('#com_adHocWork').ntsEditor("clear");
                $('#com_bindingTime').ntsEditor("clear");
                $('#com_attendanceDays').ntsEditor("clear");
                $('#com_absenceDays').ntsEditor("clear");
                $('#com_paidHoliday').ntsEditor("clear");
                $('#com_fundedPaidHoliday').ntsEditor("clear");
                $('#com_substituteWork').ntsEditor("clear");
                $('#com_substituteHoliday').ntsEditor("clear");
                $('#com_compensationHoliday').ntsEditor("clear");
                $('#com_exsessHoliday').ntsEditor("clear");
                $('#com_planedPaidHoliday').ntsEditor("clear");
                $('#com_workplaceGroup').ntsEditor("clear");
                // Clear error inputs
                $('.nts-input').ntsError('clear');
            }
       }

    }

    module SystemResourceId {
        export const Com_Person = "Com_Person";
        export const Com_Employment = "Com_Employment";
        export const Com_Class = "Com_Class";
        export const Com_Jobtitle = "Com_Jobtitle";
        export const Com_Department = "Com_Department";
        export const Com_Workplace = "Com_Workplace";
        export const Com_EquipmentClass = "Com_EquipmentClass";
        export const Com_EquipmentItem = "Com_EquipmentItem";
        export const Com_Office = "Com_Office";
        export const Com_Company = "Com_Company";
        export const Com_Contract = "Com_Contract";
        export const Com_User = "Com_User";
        export const Com_Project = "Com_Project";
        export const Com_AdHocWork = "Com_AdHocWork";
        export const Com_BindingTime = "Com_BindingTime";
        export const Com_AttendanceDays = "Com_AttendanceDays";
        export const Com_AbsenceDays = "Com_AbsenceDays";
        export const Com_PaidHoliday = "Com_PaidHoliday";
        export const Com_FundedPaidHoliday = "Com_FundedPaidHoliday";
        export const Com_SubstituteWork = "Com_SubstituteWork";
        export const Com_SubstituteHoliday = "Com_SubstituteHoliday";
        export const Com_CompensationHoliday = "Com_CompensationHoliday";
        export const Com_ExsessHoliday = "Com_ExsessHoliday";
        export const Com_PlanedPaidHoliday = "Com_PlanedPaidHoliday";
        export const Workplace_Group = "Workplace_Group";
    }
}