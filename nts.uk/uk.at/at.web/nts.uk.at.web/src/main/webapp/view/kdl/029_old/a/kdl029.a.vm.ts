module nts.uk.at.view.kdl029_old.a.screenModel {
    import getText = nts.uk.resource.getText;
    import text = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import service = nts.uk.at.view.kdl029.a.service;

    export class ViewModel {

        columnHolidayGrantInfos = ko.observableArray([
            { headerText: 'ID', prop: 'id', hidden: true },
            { headerText: text('KDL029_9') + '(' + text('KDL029_13') + ')', prop: 'dataDate', width: 250 },//付与日
            { headerText: text('KDL029_10'), prop: 'fundedNumber', width: 90 },//付与日数
            { headerText: text('KDL029_11'), prop: 'numberOfUses', width: 90 },//使用数
            { headerText: text('KDL029_12'), prop: 'residualNumber', width: 90 }//残日数 
        ]);
        dataHolidayGrantInfo: KnockoutObservableArray<DataHolidayGrantInfo> = ko.observableArray([]);
        columnSteadyUseInfors = ko.observableArray([
            { headerText: 'ID', prop: 'id', hidden: true },
            { headerText: text('KDL029_17'), prop: 'date', width: 100 },
            { headerText: text('KDL029_18'), prop: 'steadNumberOfUser', width: 90 },
            { headerText: text('KDL029_19'), prop: 'typeOfStead', width: 125 }
        ]);
        dataSteadyUseInfor: KnockoutObservableArray<DataSteadyUseInfor> = ko.observableArray([]);

        value: KnockoutObservable<string> = ko.observable('');
        employeeCode: KnockoutObservable<string> = ko.observable('');
        employeeName: KnockoutObservable<string> = ko.observable('');
        listComponentOption: any;
        employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray([]);
        employeeIDList: KnockoutObservableArray<string> = ko.observableArray([]);
        selectedType: KnockoutObservable<number> = ko.observable(0);
        multiSelect: KnockoutObservable<boolean> = ko.observable(false);
        inputDate: KnockoutObservable<string> = ko.observable('');
        totalRemain: KnockoutObservable<string> = ko.observable('0.0 日');
        displayKCP005: KnockoutObservable<boolean> = ko.observable(false);

        isRetentionManage: KnockoutObservable<boolean> = ko.observable(true);
        lstEmpFull: KnockoutObservableArray<UnitModel> = ko.observableArray([]);
        //ver7
        title2: KnockoutObservable<string> = ko.observable('');

        constructor() {
            let vm = this;
            let param = nts.uk.ui.windows.getShared('KDL029_PARAM');
            vm.isRetentionManage(true)
            vm.employeeIDList(param.employeeIds);
            vm.multiSelect(false);
            vm.inputDate(param.baseDate);
            vm.start().then(() => {
                if (vm.employeeList().length > 1) {
                    vm.displayKCP005(true);
                    vm.employeeCode(vm.employeeList()[0].code);
                    vm.listComponentOption = {
                        isShowAlreadySet: false,
                        isMultiSelect: false,
                        listType: ListType.EMPLOYEE,
                        employeeInputList: vm.employeeList,
                        selectType: SelectType.SELECT_BY_SELECTED_CODE,
                        selectedCode: vm.employeeCode,
                        isDialog: false,
                        isShowNoSelectRow: false,
                        alreadySettingList: ko.observableArray([]),
                        isShowWorkPlaceName: false,
                        isShowSelectAllButton: false,
                        maxRows: 12
                    };

                    $('#component-items-list').ntsListComponent(vm.listComponentOption).then(() => {
                        $('#component-items-list').focusComponent();
                    });
                }

                if (vm.displayKCP005()) {
                    nts.uk.ui.windows.getSelf().setWidth(990);
                    nts.uk.ui.windows.getSelf().$dialog.dialogPositionControl();
                }
            })
        }

        closeDialog() {
            nts.uk.ui.windows.close();
        }

        start(): JQueryPromise<any> {
            block.invisible();
            let vm = this,
                dfd = $.Deferred();
            service.findAllEmploymentSystem({
                inputDate: nts.uk.util.isNullOrEmpty(vm.inputDate()) ? null : moment.utc(vm.inputDate()).format("YYYY/MM/DD"),
                listSID: vm.employeeIDList(),
            }).then(data => {
                block.clear();
                let yearRes = getText('KDL029_15') + data.yearResigName + getText('KDL029_23');
                vm.title2(yearRes);
                vm.employeeName(data.employeeName);
                //create list emp -> kcp005
                _.each(data.employeeInfors, emp => {
                    vm.lstEmpFull().push({ id: emp.sid, code: emp.scd, name: emp.bussinessName });
                    vm.employeeList().push({ code: emp.scd, name: emp.bussinessName });
                });

                vm.employeeCode.subscribe(value => {
                    let empSelected = _.find(vm.lstEmpFull(), emp => {
                        if (emp.code === value) {
                            return emp;
                        }
                    });

                    if (!nts.uk.util.isNullOrEmpty(empSelected)) {
                        let employeeIDs = [];
                        let dfd = $.Deferred();
                        employeeIDs.push(empSelected.id);
                        vm.employeeName(empSelected.name);
                        block.invisible();
                        service.findByEmployee({
                            mode: vm.multiSelect(),
                            inputDate: nts.uk.util.isNullOrEmpty(vm.inputDate()) ? null : moment.utc(vm.inputDate()).format("YYYY/MM/DD"),
                            listSID: employeeIDs,
                        }).then(data => {
                            block.clear();
                            vm.getDataForTable(data);
                            vm.isRetentionManage(data.retentionManage);
                            dfd.resolve();
                        }).fail(() => {
                            block.clear();
                            dfd.reject();
                        });
                        return dfd.promise();
                    }
                });
                vm.employeeCode(data.employeeCode);
                dfd.resolve();
            }).fail(() => {
                block.clear();
                dfd.reject();
            });
            return dfd.promise();

        }

        getDataForTable(data: EmpRsvLeaveInforDto) {
            const vm = this;
            if (!nts.uk.util.isNullOrEmpty(data.rsvLeaManaImport)) {
                let dataHoliday = [];
                let total = 0.0;
                _.each(data.rsvLeaManaImport.grantRemainingList, (rsv, index) => {
                    dataHoliday.push(new DataHolidayGrantInfo(
                        index,
                        rsv.grantDate,
                        rsv.grantNumber,
                        rsv.usedNumber,
                        rsv.remainingNumber,
                        rsv.deadline,
                        rsv.expiredInCurrentMonth
                    ));
                    total = total + rsv.remainingNumber;
                });
                vm.totalRemain(total.toFixed(1) + text('KDL029_22'));
                vm.dataHolidayGrantInfo(_.orderBy(dataHoliday, ["fundedDate"], ["asc"]));

                let dataYearly = [];
                _.each(data.rsvLeaManaImport.tmpManageList, (tmp, index) => {
                    dataYearly.push(new DataSteadyUseInfor(index, tmp.ymd, tmp.useDays, tmp.creatorAtr));
                });
                vm.dataSteadyUseInfor(_.orderBy(dataYearly, ["date"], ["asc"]));
            }
        }
    }

    export class DataHolidayGrantInfo {
        id: any;
        fundedDate: string;
        fundedNumber: string;
        numberOfUses: string;//使用数 - usedNumber
        residualNumber: string;
        deadline: string;//期限日 - deadline
        dataDate: string;
        constructor(id: any, fundedDate: string, fundedNumber: number, numberOfUses: number,
            residualNumber: number, deadline: string, expiredInCurrentMonth: boolean) {
            this.id = id;
            this.fundedDate = fundedDate;
            this.fundedNumber = fundedNumber.toFixed(1) + text('KDL029_14');
            this.numberOfUses = numberOfUses.toFixed(1) + text('KDL029_14');
            this.residualNumber = residualNumber.toFixed(1) + text('KDL029_14');
            if (expiredInCurrentMonth) {
                this.deadline = deadline ? nts.uk.resource.getText("KDL005_38", [deadline]) : '';
            } else {
                this.deadline = deadline ? nts.uk.resource.getText("KDL005_37", [deadline]) : '';
            }
            this.dataDate = this.fundedDate + this.deadline;
        }
    }

    export class DataSteadyUseInfor {
        id: any;
        date: string;
        steadNumberOfUser: string;
        typeOfStead: string;
        constructor(id: any, date: string, steadNumberOfUser: number, typeOfStead: string) {
            this.id = id;
            this.date = date;
            this.steadNumberOfUser = steadNumberOfUser.toFixed(1) + text('KDL029_14');
            this.typeOfStead = typeOfStead;
        }
    }

    export interface UnitModel {
        id?: string;
        code: string;
        name?: string;
        workplaceName?: string;
        isAlreadySetting?: boolean;
        optionalColumn?: any;
    }

    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }

    export interface EmpRsvLeaveInforDto {
        employeeInfors: EmployeeInfoImport[];
        rsvLeaManaImport: RsvLeaManagerImport;
        employeeCode: string;
        employeeName: string;
        yearResigName: string;
        retentionManage: boolean
    }

    export interface EmployeeInfoImport {
        sid: string;
        scd: string;
        bussinessName: string;
    }

    export interface RsvLeaManagerImport {
        reserveLeaveInfo: RsvLeaveInfoImport;
        grantRemainingList: RsvLeaGrantRemainingImport[];
        tmpManageList: TmpRsvLeaveMngImport[];
    }

    export interface RsvLeaveInfoImport {
        befRemainDay: number;
        aftRemainDay: number;
        grantDay: number;
        remainingDays: number;
    }

    export interface RsvLeaGrantRemainingImport {
        grantDate: string;
        deadline: string;
        grantNumber: number;
        usedNumber: number;
        remainingNumber: number;
        expiredInCurrentMonth: boolean;
    }

    export interface TmpRsvLeaveMngImport {
        ymd: string;
        creatorAtr: string;
        useDays: number;
    }
}
