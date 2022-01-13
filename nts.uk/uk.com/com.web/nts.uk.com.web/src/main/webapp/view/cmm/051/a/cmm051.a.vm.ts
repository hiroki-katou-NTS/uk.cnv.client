////// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cmm051.a {
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import block = nts.uk.ui.block;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;
    const API = {

        deleteWkpManager: "at/auth/cmm051/workplace/manager/delete",
        deleteWkpHist: "at/auth/cmm051/workplace/manager/delete-history",
        addWkpManager: "at/auth/cmm051/workplace/manager/add",
        addHistWkpManager: "at/auth/cmm051/workplace/manager/register",


        getDataInit: "com/screen/cmm051/get-data-init",
        getListEmpByWpid: "com/screen/cmm051/get-employee-list-by-wplid",
        getListEmpInf: "com/screen/cmm051/get-data-init/employee-mode",
        getListHist: "com/screen/cmm051/get-wpl-manager"

    };

    @bean()
    class ViewModel extends ko.ViewModel {
        langId: KnockoutObservable<string> = ko.observable('ja');
        ntsHeaderColumns: KnockoutObservableArray<any> = ko.observableArray([]);
        historyId: KnockoutObservable<any> = ko.observable(null);
        // Screen mode
        isNewMode: KnockoutObservable<boolean> = ko.observable(false);
        isNewModeHist: KnockoutObservable<boolean> = ko.observable(true);
        isUpdateModeHist: KnockoutObservable<boolean> = ko.observable(true);
        isDeleteModeHist: KnockoutObservable<boolean> = ko.observable(true);
        isClicked: KnockoutObservable<boolean> = ko.observable(false);
        isDelete: KnockoutObservable<boolean> = ko.observable(true);
        //Date Range Picker
        dateValue: KnockoutObservable<any> = ko.observable("");
        selectedWkpId: KnockoutObservable<string>;
        headers: any;

        // start declare KCP005
        listComponentOption: any;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingPersonal: KnockoutObservableArray<any>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection: KnockoutObservable<boolean>;
        employeeList: KnockoutObservableArray<any>;
        baseDate: KnockoutObservable<Date>;
        employInfors: KnockoutObservableArray<any> = ko.observableArray([]);
        listEmployee: KnockoutObservableArray<any> = ko.observableArray([]);
        dateHistoryList: KnockoutObservableArray<any> = ko.observableArray([]);
        dateHistoryListFull: KnockoutObservableArray<any> = ko.observableArray([]);
        workPlaceIdList: KnockoutObservableArray<string> = ko.observableArray([]);
        workPlaceList: KnockoutObservableArray<any> = ko.observableArray([]);
        mode: KnockoutObservable<number> = ko.observable(1);
        //A8
        workplaceCode: KnockoutObservable<string> = ko.observable("");
        workplaceName: KnockoutObservable<string> = ko.observable("");
        //A3
        employeeCode: KnockoutObservable<string> = ko.observable(null);
        employeeName: KnockoutObservable<string> = ko.observable("");

        selectedEmCode: KnockoutObservable<string> = ko.observable(null);
        columns: KnockoutObservableArray<NtsGridListColumn>;
        count: number;
        idAddOrUpdate: KnockoutObservable<string> = ko.observable(null);
        startDate: KnockoutObservable<any> = ko.observable(null);
        endDate: KnockoutObservable<any> = ko.observable(null);
        workPlaceId: KnockoutObservable<any> = ko.observable(null);
        employeeId: KnockoutObservable<any> = ko.observable(null);

        constructor(params: any) {
            super();
            let vm = this;
            vm.selectedWkpId = ko.observable('');
            vm.count = 1;
            vm.setDataDefaultMode();
            vm.ntsHeaderColumns = ko.observableArray([
                {headerText: '', key: 'code', hidden: true},
                {headerText: '', key: 'display', formatter: _.escape}
            ]);
            vm.columns = ko.observableArray([
                {headerText: '', key: 'id', hidden: true},
                {headerText: nts.uk.resource.getText("CMM051_44"), key: 'code', width: 110},
                {headerText: nts.uk.resource.getText("CMM051_45"), key: 'name', width: 170}
            ]);

        }

        setDataDefaultMode() {
            let vm = this;
            let workplaceManagerList: any[] = [];
            let listEmployee: any[] = [];
            let personList: any[] = [];
            vm.KCP005_load();
            block.invisible();
            vm.$ajax('com', API.getDataInit).done((data) => {
                if (!isNullOrUndefined(data)) {
                    if (!isNullOrUndefined(data.workplaceInfoImport)) {
                        vm.workPlaceId(data.workplaceInfoImport.workplaceId);
                        vm.workplaceCode(data.workplaceInfoImport.workplaceCode);
                        vm.workplaceName(data.workplaceInfoImport.workplaceName);
                    }
                    workplaceManagerList = data.employeeInformation.workplaceManagerList;
                    listEmployee = data.employeeInformation.listEmployee;
                    personList = data.employeeInformation.personList;
                    vm.setData(workplaceManagerList, listEmployee, personList, vm.employeeId(), vm.workPlaceId(), vm.historyId());
                } else {
                    vm.setModeNoData();
                }

            }).always(() => {
                block.clear();
            }).fail((error) => {
                nts.uk.ui.block.clear();
                nts.uk.ui.dialog.alertError({messageId: error.messageId}).then(() => {
                    vm.backToTopPage();
                });
            });
        }

        initScreen(mode: number, sid: string, wplId: string, histId: string) {
            let vm = this;
            if (!isNullOrUndefined(sid) && !isNullOrUndefined(wplId)) {
                let param = {
                    workplaceId: wplId,
                    sid: sid
                };
                block.invisible();
                vm.$ajax("com", API.getListHist, param).done((data) => {
                    if (!isNullOrEmpty(data)) {
                        vm.dateHistoryListFull(data);
                        vm.setDataHist(vm.employeeId(), vm.dateHistoryListFull(), histId, wplId);
                    }else {
                        vm.isNewModeHist(true);
                        vm.startDate(null);
                        vm.endDate(null);
                        vm.historyId(null);
                        vm.dateHistoryList([]);
                    }
                    // if(isNullOrEmpty(vm.dateHistoryList())){
                    //
                    // }

                }).always(() => {
                    block.clear();
                }).fail((error) => {
                    nts.uk.ui.block.clear();
                    nts.uk.ui.dialog.alertError({messageId: error.messageId}).then(() => {
                        vm.backToTopPage();
                    });
                });
            }else {
                vm.dateHistoryList([]);
            }

        }

        backToTopPage() {
            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
        }

        setData(workplaceManagerList: any[],
                listEmployee: any[],
                personList: any[],
                sid: string,
                wplId: string,
                histId: string): void {

            let vm = this;
            let mode = vm.mode();
            vm.dateHistoryListFull(workplaceManagerList);
            if (mode == Mode.WPL) {
                let emps: any = [];
                if (!isNullOrEmpty(personList) && !isNullOrEmpty(listEmployee)) {
                    for (let i = 0; i < listEmployee.length; i++) {
                        let em = listEmployee[i];
                        let info = _.find(personList, (e) => e.pid == em.personId);
                        if (!isNullOrUndefined(info)) {
                            emps.push({
                                id: em.employeeId,
                                code: em.employeeCode,
                                name: info.businessName,
                            });
                        }

                    }
                }
                if (isNullOrEmpty(emps)) {
                    vm.employeeCode(null);
                    vm.employeeName(null);
                    vm.setModeNoData();
                }else {
                    vm.isNewMode(false);
                }
                emps = _.orderBy(emps, ['code'], ['asc']);
                vm.employInfors(emps);
                let info = _.find(vm.employInfors(), (e) => e.id == sid);
                if (isNullOrUndefined(info)) {
                    if (!isNullOrEmpty(emps)) {
                        vm.selectedEmCode(emps[0].code);
                        vm.selectedEmCode.valueHasMutated();
                    }
                } else {
                    vm.selectedEmCode(info.code);
                    vm.selectedEmCode.valueHasMutated();
                }
            }
            if (mode == Mode.EMPLOYMENT) {
                let info = _.find(vm.workPlaceList(), (e) => e.id == wplId);
                if (!isNullOrUndefined(info)) {
                    vm.workPlaceId(info.id);
                } else {
                    if (!isNullOrEmpty(vm.workPlaceList())) {
                        if (!vm.isNewMode()) {
                            vm.workPlaceId(vm.workPlaceList()[0].id);
                            vm.workPlaceId.valueHasMutated();
                        }
                    }
                }
            }

        }
        setModeNoData(): void{
            let vm = this;
            vm.isDelete(false);
            vm.isNewMode(true);
            vm.isNewModeHist(true);
            vm.isUpdateModeHist(false);
            vm.isDeleteModeHist(false);
            $('#swa_22').first().focus();

        }
        setDataHist(sid: string, workplaceManagerList: any[], histId: string, wplId: string): void {
            let vm = this;
            let listDatePeriod: any[] = [];
            let listHist: any[] = [];
            if (!isNullOrEmpty(workplaceManagerList)) {
                listHist = _.filter(workplaceManagerList, (e) => e.workplaceId == wplId && e.employeeId == sid);

                for (let i = 0; i < listHist.length; i++) {
                    let wpl = listHist[i];
                    let id = wpl.workplaceManagerId;
                    let display = wpl.startDate + nts.uk.resource.getText("CMM051_62") + wpl.endDate;
                    listDatePeriod.push({
                        id: id,
                        sid: wpl.employeeId,
                        display: display,
                        startDate: wpl.startDate,
                        endDate: wpl.endDate
                    })
                }
            }
            listDatePeriod = _.orderBy(listDatePeriod, ['startDate'], ['desc']);
            vm.dateHistoryList(listDatePeriod);
            if (!isNullOrEmpty(listDatePeriod)) {
                let hist = _.find(vm.dateHistoryList(), (e) => e.id == histId);
                if (isNullOrUndefined(hist)) {
                    vm.historyId(listDatePeriod[0].id);
                    vm.startDate(listDatePeriod[0].startDate);
                    vm.endDate(listDatePeriod[0].endDate);
                } else {
                    vm.historyId(hist.id);
                    vm.startDate(hist.startDate);
                    vm.endDate(hist.endDate);
                }
                vm.isNewModeHist(true);
                vm.isUpdateModeHist(true);
                vm.isDeleteModeHist(true);
            } else {
                vm.startDate(null);
                vm.endDate(null);
                vm.historyId(null);
                vm.dateHistoryList([]);

            }
        }

        KCP005_load() {
            let vm = this;
            vm.baseDate = ko.observable(new Date());
            vm.isShowAlreadySet = ko.observable(false);
            vm.alreadySettingPersonal = ko.observableArray([]);
            vm.isDialog = ko.observable(false);
            vm.isShowNoSelectRow = ko.observable(false);
            vm.isMultiSelect = ko.observable(false);
            vm.isShowWorkPlaceName = ko.observable(false);
            vm.isShowSelectAllButton = ko.observable(false);
            vm.disableSelection = ko.observable(false);
            vm.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: false,
                listType: ListType.EMPLOYEE,
                employeeInputList: vm.employInfors,
                selectType: SelectType.SELECT_ALL,
                selectedCode: vm.selectedEmCode,
                isDialog: false,
                alreadySettingList: vm.alreadySettingPersonal,
                isShowSelectAllButton: false,
                showOptionalColumn: false,
                maxWidth: 420,
                maxRows: 13,
            };
            $('#kcp005').ntsListComponent(vm.listComponentOption)
        }

        // Start page
        created() {

        }

        mounted() {
            let vm = this;
            $('#swa_22').first().focus();
            vm.mode.subscribe((mode) => {
                console.log("MODE :" + mode);
                if (mode == Mode.WPL) {
                    vm.employeeId(null);
                    vm.employeeCode(null);
                    vm.employeeName(null);
                    vm.workplaceCode(null);
                    vm.workplaceName(null);
                    vm.selectedEmCode(null);
                    vm.historyId(null);
                    vm.dateHistoryList([]);
                    vm.setDataDefaultMode();
                } else if (mode == Mode.EMPLOYMENT) {
                    vm.employeeId(null);
                    vm.selectedEmCode(null);
                    vm.employeeCode(null);
                    vm.employeeName(null);
                    vm.workplaceCode(null);
                    vm.workplaceName(null);
                    vm.historyId(null);
                    vm.dateHistoryList([]);
                    let sidLogin = vm.$user.employeeId;
                    let sid: any[] = [];
                    sid.push(sidLogin);
                    vm.getEmployeeInfo(sid, null)
                }
            });
            vm.selectedEmCode.subscribe((e) => {
                if (!isNullOrUndefined(e)) {
                    let eminfo = _.find(vm.employInfors(), (i) => i.code == e);
                    if (!isNullOrUndefined(eminfo)) {
                        vm.employeeName(eminfo.name);
                        vm.employeeCode(e);
                        vm.employeeId(eminfo.id);
                    } else {
                        vm.employeeCode(null);
                        vm.employeeName(null);
                    }
                }
            });


            vm.employeeId.subscribe((e) => {
                if (!isNullOrUndefined(e) ) {
                    let eminfo = _.find(vm.employInfors(), (i) => i.id == e);
                    if (!isNullOrUndefined(eminfo)) {
                        vm.selectedEmCode(eminfo.code);
                        vm.isDelete(true);
                        vm.isNewMode(false);
                    } else {
                        vm.employeeCode(null);
                        vm.employeeName(null)
                    }
                    vm.initScreen(vm.mode(), vm.employeeId(), vm.workPlaceId(), vm.historyId())
                }
            });
            vm.workPlaceId.subscribe((e) => {
                vm.selectedEmCode(null);
                if (!isNullOrUndefined(e)) {
                    let info = _.find(vm.workPlaceList(), (i) => i.id == e);
                    if (!isNullOrEmpty(info)) {
                        vm.workplaceName(info.name);
                        vm.workplaceCode(info.code);
                        vm.isDelete(true);
                        vm.isNewMode(false);
                    } else {
                        vm.workplaceName(null);
                        vm.workplaceCode(null);
                    }
                    vm.initScreen(vm.mode(), vm.employeeId(), e, vm.historyId())
                }

            });
            vm.historyId.subscribe((id) => {
                if(!isNullOrUndefined(id)){
                    let idAddOrUpdate = vm.idAddOrUpdate();
                    let hist = _.find(vm.dateHistoryList(), (e) => e.id == id);
                    if (!isNullOrUndefined(hist)) {
                        vm.startDate(hist.startDate);
                        vm.endDate(hist.endDate);
                    }
                    if (!isNullOrUndefined(idAddOrUpdate)) {
                        if (id == idAddOrUpdate) {
                            vm.isNewModeHist(false);
                            vm.isDeleteModeHist(false);
                            vm.isUpdateModeHist(true);
                        } else {
                            vm.isNewModeHist(false);
                            vm.isDeleteModeHist(false);
                            vm.isUpdateModeHist(false);
                        }
                        if (id != idAddOrUpdate && !vm.isNewMode()) {
                            vm.isNewModeHist(false);
                            vm.isDeleteModeHist(false);
                            vm.isUpdateModeHist(false);
                        }
                    }else {
                        vm.isNewModeHist(true);
                        vm.isDeleteModeHist(true);
                        vm.isUpdateModeHist(true);
                    }
                }
            })
        }

        private initManager() {
            let vm = this;
            nts.uk.ui.errors.clearAll();
            if (vm.isNewMode() == true) {
                if(vm.mode() == Mode.WPL){
                    vm.selectedEmCode(null);
                    vm.employeeId(null);
                    vm.employeeCode(null);
                    vm.employeeName(null);
                    vm.openDialogCDL009();
                }
                if(vm.mode() == Mode.EMPLOYMENT){
                    vm.workPlaceId(null);
                    vm.workplaceCode(null);
                    vm.workplaceName(null);
                    vm.openCDL008Dialog();
                }
                vm.dateHistoryList([]);
                vm.historyId(null);
                vm.startDate(null);
                vm.endDate(null);

            }
        }

        /**
         * Button on screen
         */
        // 新規 button
        createWkpManager() {
            let vm = this;
            nts.uk.ui.errors.clearAll();
            vm.isNewMode(true);
            vm.isUpdateModeHist(false);
            vm.isDeleteModeHist(false);
            vm.isNewModeHist(true);
            vm.isDelete(false);
            vm.initManager();
        }

        // 登録 button
        saveWkpManager() {
            let vm = this;
            let mode = vm.mode();
            // validate
            if(vm.validate()){
                return;
            };
            let workplaceId = vm.workPlaceId();
            let sid = vm.employeeId();
            let startDate = nts.uk.time.parseMoment(vm.startDate()).format();
            let endDate = nts.uk.time.parseMoment(vm.endDate()).format();
            let command = {
                "workPlaceId": workplaceId,
                "sid": sid,
                "startDate": startDate,
                "endDate": endDate
            };
            block.invisible();
            if (vm.isNewMode()) {
                vm.$ajax("com", API.addWkpManager, command).done(() => {
                    vm.$dialog.info({ messageId: "Msg_15" }).then(()=>{

                        vm.initScreen(mode, sid, workplaceId, null);
                        if (mode == Mode.WPL) {
                            vm.getListWpl(workplaceId);
                        } else {
                            let sids: any[] = [];
                            sids.push(vm.employeeId());
                            vm.getEmployeeInfo(sids, workplaceId);
                        }
                        vm.isNewMode(false);
                        vm.idAddOrUpdate(null);
                        vm.isDelete(true);
                    });
                    }
                ).always(() => {
                    block.clear();
                }).fail((res) => {
                    nts.uk.ui.block.clear();
                    vm.showMessageError(res);
                })
            } else {
                let commandHist = {
                    "wkpManagerId": vm.historyId(),
                    "startDate": startDate,
                    "endDate": endDate
                };
                vm.$ajax("com", API.addHistWkpManager, commandHist).done(() => {
                    vm.$dialog.info({ messageId: "Msg_15" }).then(()=>{
                        vm.initScreen(mode, sid, workplaceId, vm.historyId());
                        vm.isNewMode(false);
                        vm.isDelete(true);
                        vm.idAddOrUpdate(null);
                    }); }
                ).always(() => {
                    block.clear();
                }).fail((res) => {
                    nts.uk.ui.block.clear();
                    vm.showMessageError(res);
                })
            }

        }

        /**
         * validate
         */
        private validate() {
            let vm = this;
            let hasError = false;
            // clear error
            vm.clearError();
            if(vm.mode() == Mode.WPL){
                if (isNullOrUndefined(vm.workplaceCode())) {
                    nts.uk.ui.dialog.alertError({messageId: 'Msg_218', messageParams: [nts.uk.resource.getText("CMM051_46")]});
                    $('#a8_2').focus();
                    hasError = true;
                    return hasError;
                }
                if(isNullOrUndefined(vm.employeeCode())){
                    nts.uk.ui.dialog.alertError({messageId: 'Msg_218', messageParams: [nts.uk.resource.getText("CMM051_41")]});
                    $('#a10_2').focus();
                    hasError = true;
                    return hasError;
                }

            }else {
                if (isNullOrUndefined(vm.employeeCode())) {
                    nts.uk.ui.dialog.alertError({messageId: 'Msg_218', messageParams: [nts.uk.resource.getText("CMM051_41")]});
                    $('#a3_2').focus();
                    hasError = true;
                    return hasError;
                }
                if(isNullOrUndefined(vm.workplaceCode())){
                    nts.uk.ui.dialog.alertError({messageId: 'Msg_218', messageParams: [nts.uk.resource.getText("CMM051_46")]});
                    $('#a6_2').focus();
                    hasError = true;
                    return hasError;
                }
            }
            if (isNullOrUndefined(vm.startDate()) || isNullOrUndefined(vm.endDate())) {
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_2201', messageParams:[]});
                $('#new_date').focus();
                hasError = true;
                return hasError;
            }
            return hasError;
        }

        /**
         * clearError
         */
        private clearError() {
            nts.uk.ui.errors.clearAll();
        }

        // 削除 button
        remove() {
            let vm = this;
            let mode = vm.mode();

            // show message confirm
            nts.uk.ui.dialog.confirm({messageId: 'Msg_18'}).ifYes(() => {
                    let workplaceId = vm.workPlaceId();
                    let command = {
                        "workplaceId": workplaceId,
                        "sid": vm.employeeId()
                    };
                    block.invisible();
                    vm.$ajax("com", API.deleteWkpManager, command).done(() => {
                        vm.$dialog.info({ messageId: "Msg_16" }).then(()=>{
                            if (mode == Mode.WPL) {
                                let indexRemove = _.findIndex(vm.employInfors(), (e) => e.id == vm.employeeId());
                                let emifId: any = null;
                                if ( indexRemove == (vm.employInfors().length - 1)) {
                                    emifId = indexRemove >=1 ? vm.employInfors()[indexRemove - 1].id : null;
                                    $('#swa_22').first().focus();
                                } else {
                                    emifId = vm.employInfors()[indexRemove + 1].id;
                                }
                                vm.employeeId(emifId);
                                vm.getListWpl(workplaceId);
                            } else {
                                let indexRemoveWP = _.findIndex(vm.workPlaceList(), (e) => e.id == vm.workPlaceId());
                                let wpId: any = null;
                                if (indexRemoveWP == (vm.workPlaceList().length - 1)) {
                                    wpId = indexRemoveWP >=1 ? vm.workPlaceList()[indexRemoveWP - 1].id : null;
                                    $('#swa_22').first().focus();
                                } else {
                                    wpId = vm.workPlaceList()[indexRemoveWP + 1].id;
                                }
                                vm.workPlaceId(wpId);
                                let sids: any[] = [];
                                sids.push(vm.employeeId());
                                vm.getEmployeeInfo(sids, wpId);
                            }
                            vm.initScreen(mode, vm.employeeId(), vm.workPlaceId(), vm.historyId());
                        });}
                    ).always(() => {
                        block.clear();
                    }).fail((res) => {
                        nts.uk.ui.block.clear();
                        vm.showMessageError(res);
                    })
                }
            )

        }

        openDialogA3282() {
            let vm = this;
            let mode = vm.mode();
            if (mode == 1) {
                vm.openCDL008Dialog()
            } else if (mode == 0) {
                vm.openDialogCDL009()
            }

        }

        openDialogA62102() {
            let vm = this;
            let mode = vm.mode();
            if (mode == 1) {
                vm.openDialogCDL009()
            } else if (mode == 0) {
                vm.openCDL008Dialog()
            }

        }

        // 社員選択 button
        openDialogCDL009() {
            let vm = this;

            setShared('CDL009Params', {
                isMultiSelect: false,
                baseDate: moment(new Date()).toDate(),
                selectedEmCode: vm.employeeCode(),
                target: 1
            }, true);

            modal("/view/cdl/009/a/index.xhtml").onClosed(function () {
                let isCancel = getShared('CDL009Cancel');
                if (isCancel) {
                    return;
                }
                let employeeId = getShared('CDL009Output');
                vm.employeeId(employeeId);
                let sids: any[] = [];
                if (!isNullOrUndefined(employeeId)) {
                    sids.push(employeeId);
                    vm.getEmployeeInfo(sids, null);
                }
            });
        }

        private getEmployeeInfo(empId: string[], wplId: string) {
            let vm = this;
            if (!isNullOrEmpty(empId)) {
                let param: any = {
                    "employIds": empId
                };
                block.invisible();
                vm.$ajax("com", API.getListEmpInf, param).done((data) => {
                    if (!isNullOrUndefined(data)) {
                        let wpl = data.workplaceInfors;
                        let eminfos: any[] = data.listEmployee;
                        let personList: any[] = data.personList;
                        if (vm.mode() == Mode.WPL) {
                            if (!isNullOrEmpty(eminfos) && !isNullOrEmpty(personList)) {
                                let eminfo = eminfos[0];
                                let info = _.find(personList, (e) => e.pid == eminfo.personId);
                                vm.employeeId(eminfo.employeeId);
                                vm.employeeCode(eminfo.employeeCode);
                                vm.employeeName(info.businessName);
                            }
                        }
                        if (vm.mode() == Mode.EMPLOYMENT) {
                            if (!isNullOrEmpty(empId)) {
                                let eInfor = _.find(eminfos, (e) => e.employeeId == empId[0]);
                                if (!isNullOrUndefined(eInfor)) {
                                    let per = _.find(personList, (e) => e.pid == eInfor.personId);
                                    vm.employeeId(eInfor.employeeId);
                                    vm.employeeCode(eInfor.employeeCode);
                                    vm.employeeName(per.businessName);
                                }
                                let wplIf: any[] = [];

                                if (!isNullOrEmpty(wpl)) {
                                    for (let i = 0; i < wpl.length; i++) {
                                        let item = wpl[i];
                                        wplIf.push({
                                            id: item.workplaceId,
                                            code: item.workplaceCode,
                                            name: item.workplaceName
                                        })
                                    }
                                } else {
                                    vm.workplaceName(null);
                                    vm.workplaceCode(null);
                                    vm.workPlaceId(null);
                                    vm.dateHistoryList([]);
                                    vm.workPlaceList([]);
                                }
                                wplIf = _.orderBy(wplIf, ['code'], ['asc']);
                                vm.workPlaceList(wplIf);
                                if (!isNullOrEmpty(vm.workPlaceList())) {
                                    let wp = _.find(vm.workPlaceList(), (i) => i.id == wplId);
                                    if (isNullOrUndefined(wp)) {
                                        vm.workPlaceId(vm.workPlaceList()[0].id);
                                        vm.workPlaceId.valueHasMutated();
                                    } else {
                                        vm.workPlaceId(wplId);
                                    }
                                    vm.isDelete(true);
                                    vm.isNewMode(false);
                                }else {
                                    vm.setModeNoData();
                                }
                            }
                        }
                    } else {
                        vm.dateHistoryList([]);
                        vm.workPlaceList([]);
                    }
                }).always(() => {
                    block.clear();
                }).fail((res) => {
                    nts.uk.ui.block.clear();
                    vm.showMessageError(res);
                })
            }
        }

        /**
         * Common
         */
        /**
         * showMessageError
         */
        private showMessageError(res: any) {
            if (res.businessException) {
                nts.uk.ui.dialog.alertError({messageId: res.messageId, messageParams: res.parameterIds});
            }
        }

        /**
         * Screen D - openAddHistoryDialog
         */
        public openAddHistoryDialog() {
            let vm = this;
            let dataToScreenB = {
                isCreate: true,
                isUpdate: false,
            };
            nts.uk.ui.windows.setShared("dataToScreenB", dataToScreenB);
            nts.uk.ui.windows.sub.modal('/view/cmm/051/b/index.xhtml').onClosed(() => {
                let prams = getShared('dataToScreenA');
                if (!isNullOrUndefined(prams)) {
                    vm.isNewMode(true);
                    vm.isNewModeHist(false);
                    vm.isUpdateModeHist(true);
                    vm.isDeleteModeHist(false);

                    let display = prams.startDate + nts.uk.resource.getText("CMM051_62") + prams.endDate;
                    vm.startDate(prams.startDate);
                    vm.endDate(prams.endDate);
                    let idNew = "idNew";
                    vm.idAddOrUpdate(idNew);
                    let hist = {
                        id: idNew,
                        sid: "",
                        display: display,
                        startDate: prams.startDate,
                        endDate: prams.endDate
                    };
                    let hists: any[] = vm.dateHistoryList();
                    hists.push(hist);
                    hists = _.orderBy(hists, ['startDate'], ['desc']);
                    vm.dateHistoryList(hists);
                    vm.historyId(idNew);
                }
            });
        }

        /**
         * Screen E - openUpdateHistoryDialog
         */
        public openUpdateHistoryDialog() {
            let vm = this;
            let id = vm.historyId();
            let info = _.find(vm.dateHistoryList(), (e) => e.id == id);
            let startDate: any = "";
            let endDate: any = "";
            if (!isNullOrUndefined(info)) {
                startDate = info.startDate;
                endDate = info.endDate;
            }
            let dataToScreenC = {
                isCreate: false,
                isUpdate: true,
                startDate: startDate,
                endDate: endDate
            };
            nts.uk.ui.windows.setShared("dataToScreenC", dataToScreenC);
            nts.uk.ui.windows.sub.modal('/view/cmm/051/c/index.xhtml').onClosed(() => {
                let prams = getShared('dataToScreenA');
                if (!isNullOrUndefined(prams)) {
                    vm.isClicked(false);
                    let id = vm.historyId();
                    if (id == "idNew") {
                        vm.isNewMode(true);
                    } else {
                        vm.isNewMode(false);
                    }
                    if (!isNullOrUndefined(prams)) {
                        let hists: any[] = vm.dateHistoryList();
                        let index = _.findIndex(hists, (e) => e.id == id);
                        if (index >= 0) {
                            let display = prams.startDate + nts.uk.resource.getText("CMM051_62") + prams.endDate;
                            vm.startDate(prams.startDate);
                            vm.endDate(prams.endDate);
                            let hist = {
                                id: id,
                                sid: hists[index].sid,
                                display: display,
                                startDate: prams.startDate,
                                endDate: prams.endDate
                            };
                            hists[index] = hist;
                            hists = _.orderBy(hists, ['startDate'], ['desc']);
                            vm.dateHistoryList(hists);
                            vm.isNewModeHist(false);
                            if (!vm.isNewMode()) {
                                vm.isDeleteModeHist(true);
                            }
                            if (id == vm.idAddOrUpdate()) {
                                vm.isDeleteModeHist(false);
                            }
                            vm.idAddOrUpdate(id);

                        }
                    }
                }
            });
        }

        public removeHistory(): void {
            let vm = this;
            if(vm.dateHistoryList().length == 1){
                nts.uk.ui.dialog.alertError({ messageId: 'Msg_57', messageParams:[]});
            }
            else{
                nts.uk.ui.dialog.confirm({messageId: "Msg_18"})
                    .ifYes(() => {
                        let id = vm.historyId();
                        if (!isNullOrUndefined(id)) {
                            block.invisible();
                            let command = {
                                "wkpManagerId": id
                            };
                            vm.$ajax("com", API.deleteWkpHist, command).done(() => {
                                vm.$dialog.info({ messageId: "Msg_16" }).then(()=>{
                                    let indexRemove = _.findIndex(vm.dateHistoryList(), (e) => e.id == id);
                                    let idHist: any = null;
                                    if ((indexRemove == ( vm.dateHistoryList().length) -1)) {
                                        idHist = indexRemove >=1 ? vm.dateHistoryList()[indexRemove - 1].id : null;
                                    } else {
                                        idHist = vm.dateHistoryList()[indexRemove + 1].id;
                                    }
                                    vm.dateHistoryList().splice(indexRemove, 1);
                                    vm.historyId(idHist);
                                    if(vm.mode() == Mode.WPL){
                                        vm.employeeId(vm.employeeId());
                                        vm.getListWpl(vm.workPlaceId());
                                    }else {
                                        let em : any[] = [];
                                        em.push(vm.employeeId());
                                        vm.getEmployeeInfo(em, vm.workPlaceId());
                                    }
                                    vm.initScreen(vm.mode(), vm.employeeId(), vm.workPlaceId(), vm.historyId());
                                    vm.isNewMode(false);
                                }
                                );}
                            ).always(() => {
                                block.clear();
                            }).fail((res) => {
                                nts.uk.ui.block.clear();
                                vm.showMessageError(res);
                            })
                        }
                    })
                    .ifNo(() => {
                    });
            }

        }
        openCDL008Dialog(): void {
            const vm = this;
            const inputCDL008: any = {
                startMode: StartMode.WORKPLACE,//  起動モード : 職場 (WORKPLACE = 0)
                isMultiple: false,////選択モード : 単一選択
                showNoSelection: false,// //未選択表示 : 非表示
                selectedCodes: vm.workPlaceId(),
                isShowBaseDate: true,//基準日表示区分 : 表示
                baseDate: moment.utc().toISOString(),
                selectedSystemType: SystemType.EMPLOYMENT,// //システム区分 : 就業
                isrestrictionOfReferenceRange: true//参照範囲の絞込: する
            };
            setShared('inputCDL008', inputCDL008);
            modal('/view/cdl/008/a/index.xhtml').onClosed(() => {
                const isCancel = getShared('CDL008Cancel');
                if (isCancel) {
                    return;
                }
                let wid = getShared('outputCDL008');
                let workplaceInfor = getShared('workplaceInfor');
                if (!isNullOrUndefined(wid) && !isNullOrEmpty(workplaceInfor)) {
                    vm.workPlaceId(workplaceInfor[0].id);
                    vm.workplaceCode(workplaceInfor[0].code);
                    vm.workplaceName(workplaceInfor[0].name);
                    if(vm.mode() == Mode.WPL){
                        vm.employeeId(null);
                    }
                    vm.getListWpl(wid);
                }
            });
        }

        getListWpl(wid: string): void {
            let vm = this;
            let wplParam: any = {
                "workPlaceId": wid
            }, workplaceManagerList = [], listEmployee = [], personList = [], mode = vm.mode();
            block.invisible();
            vm.$ajax('com', API.getListEmpByWpid, wplParam).done((data) => {
                if (!isNullOrEmpty(data)) {
                    workplaceManagerList = data.workplaceManagerList;
                    listEmployee = data.listEmployee;
                    personList = data.personList;
                    vm.setData(workplaceManagerList, listEmployee, personList, vm.employeeId(), vm.workPlaceId(), null);
                } else {
                    vm.dateHistoryList([]);
                    vm.workPlaceList([]);
                }
            }).always(() => {
                block.clear();
            }).fail((res) => {
                nts.uk.ui.block.clear();
                vm.showMessageError(res);
            })
        }


    }


    enum SystemType {
        PERSONAL_INFORMATION = 1,
        EMPLOYMENT = 2,
        SALARY = 3,
        HUMAN_RESOURCES = 4,
        ADMINISTRATOR = 5
    }

    enum StartMode {
        WORKPLACE = 0,
        DEPARTMENT = 1
    }


    export interface UnitModel {
        code: string;
        name?: string;
        id?: string;
    }

    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }

    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    export class Mode {
        static EMPLOYMENT = 0;
        static WPL = 1;
    }

}