module nts.uk.pr.view.qmm005.a.viewmodel {
    import model     = nts.uk.pr.view.qmm005.share.model;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import block     = nts.uk.ui.block;
    import dialog    = nts.uk.ui.dialog;

    import modal = nts.uk.ui.windows.sub.modal;
    import SetDaySupport = nts.uk.pr.view.qmm005.share.model.SetDaySupport;
    const MAX_NUMBER_SETTING = 5;

    export class ScreenModel {
        //A2_2
        itemTable:ItemTable;
        //A3_4 対象雇用
        targetEmployment: KnockoutObservableArray<number>;
        processCategoryNO: number;
        itemBinding: KnockoutObservableArray<ItemBinding>;

        constructor() {
            var self = this;
             if(/Edge/.test(navigator.userAgent)){
                $("#A2_2").ntsFixedTable({height: 248, width: 1000});
                $("#A3_1").ntsFixedTable({height: 247, width: 400});
             }
             else if (/Chrome/.test(navigator.userAgent)) {
                $("#A2_2").ntsFixedTable({height: 247, width: 1000});
                $("#A3_1").ntsFixedTable({height: 247, width: 400});
             }
             else {
                $("#A2_2").ntsFixedTable({height: 248, width: 1000});
                $("#A3_1").ntsFixedTable({height: 247, width: 400});
             }

            //A3_4 対象雇用
            self.targetEmployment = ko.observable([]);
            self.itemBinding = ko.observableArray([]);
        }

        toCmm008() {
            nts.uk.request.jump("com", "/view/cmm/008/a/index.xhtml");
        }

        showDialogD(processInfomation, mode): void {
            let self = this;
            let param = {
                mode: mode,
                processInfomation: processInfomation
            }
            setShared("QMM005_output_D",ko.toJS( param));
            modal('/view/qmm/005/d/index.xhtml', {title: '',}).onClosed(function (): any {
                let param = getShared("QMM005_output_A");
                let action: number = param.action;
                let processInformationUpdate = param.processInfomationUpdate;
                if (action == 0) {
                    self.itemBinding()[processInformationUpdate.processCateNo-1].processInfomation.processCls(processInformationUpdate.processCls);

                    if(processInformationUpdate.deprecatCate==model.Abolition.NOT_ABOLITION){
                        self.itemBinding()[processInformationUpdate.processCateNo-1].isNotAbolition(true);
                        self.itemBinding()[processInformationUpdate.processCateNo-1].processInfomation.deprecatCate=model.Abolition.NOT_ABOLITION;
                    }

                    if (processInformationUpdate.deprecatCate == model.Abolition.ABOLITION) {
                        self.itemBinding()[processInformationUpdate.processCateNo-1].processInfomation.deprecatCate=model.Abolition.ABOLITION;
                        service.removeEmpTied(processInformationUpdate.processCateNo).done(function () {
                            self.resetEmployee(processInformationUpdate.processCateNo);
                            self.itemBinding()[processInformationUpdate.processCateNo-1].isNotAbolition(false);
                        });
                    }
                    $('#A2_2 #button_register').eq(processInfomation-1).focus();
                }
                if (action == 1) {
                    self.showDialogB(processInformationUpdate.processCateNo).done(function () {
                        self.itemBinding.removeAll();
                        self.startPage().done(function () {
                            setTimeout(function () {
                                $('#A2_2 #processYears').eq(processInfomation.processCateNo-1).focus();
                            },100);
                        })
                    })
                }
            })
        }

        showDialogB(param): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            $('#A2_2 #processYears').eq(param-1).focus();
            setShared("QMM005_output_B", param);
            modal('/view/qmm/005/b/index.xhtml', {title: '',}).onClosed(function (): any {
                let numberOfYearA = self.itemBinding()[param - 1].years().length;
                let numberOfYearB = getShared("QMM005B").paramB;
                if(numberOfYearA == numberOfYearB) return;
                self.itemBinding.removeAll();
                self.startPage().done(function () {
                    setTimeout(function () {
                        $('#A2_2 #processYears').eq(param - 1).focus();
                    },100);
                })
            });
            dfd.resolve();
            return dfd;
        }

        showDialogF(processCateNo, employeeList): void {
            let self = this;

            let existList = [];
            for (let i = 0; i < self.itemBinding().length; i++) {
                existList = existList.concat(self.itemBinding()[i].employeeList());
            }
            let employeeArr = existList.length == 0 ? self.itemTable.empCdNameImports : self.itemTable.empCdNameImports.filter(self.comparer(existList));
            let paramEmployment = {
                processCateNo: processCateNo,
                employeeList: employeeArr,
                employeeSelectedList: employeeList()
            }
            setShared("QMM005_output_F", paramEmployment);
            modal('/view/qmm/005/f/index.xhtml', {title: '',}).onClosed(function (): any {
                let employeeString = '';
                let params = getShared("QMM005F_outParams");
                if (!_.isUndefined(params)) {
                    if(params.returnList) params.returnList = _.orderBy(params.returnList, ['code'],['asc']);
                    for (let i = 0; i < params.returnList.length; i++) {
                        employeeString == '' ? employeeString += params.returnList[i].name : employeeString += (', ' + params.returnList[i].name);
                    }
                    for (let i = 0; i < MAX_NUMBER_SETTING; i++) {
                        if (self.itemBinding()[i].processInfomation.processCateNo == params.processCateNo) {
                            self.itemBinding()[i].employeeString(employeeString);
                            self.itemBinding()[i].employeeList(params.returnList);
                        }
                    }
                }
            });
        }

        comparer(otherArray) {
            return (current => {
                return otherArray.filter(other => {
                    return other.code == current.code && other.name == current.name
                }).length == 0
            })
        }

        getYear(setDaySupportList: Array<SetDaySupport>, i: number): Array<ItemComboBox> {
            let self = this;
            let ArrSetDaySuport: Array<SetDaySupport> = _.sortBy(_.filter(self.itemTable.setDaySupports, function (o) {
                    return o.processCateNo == i + 1;
                }),
                function (o) {
                    return o.processDate;
                });
            let Araybinding: Array<ItemComboBox> = new Array<ItemComboBox>();
            for (let i = 0; i < ArrSetDaySuport.length; i++) {
                Araybinding.push(new ItemComboBox(
                    ArrSetDaySuport[i].processCateNo,
                    parseInt(ArrSetDaySuport[i].processDate / 100),
                    parseInt(ArrSetDaySuport[i].processDate / 100).toString()
                    )
                )
            }
            return _.sortBy(_.uniqBy(Araybinding, function (o) {
                return o.code;
            }), function (o) {
                return o.code;
            })
        }

        getListMonth(setDaySupportList: Array<SetDaySupport>, i: number): Array<ItemComboBox> {
            let self = this;
            let ArrSetDaySuport: Array<SetDaySupport> = _.sortBy(_.filter(self.itemTable.setDaySupports, function (o) {
                    return o.processCateNo == i + 1;
                }),
                function (o) {
                    return o.processDate;
                });
            let Araybinding: Array<ItemComboBox> = new Array<ItemComboBox>();
            for (let i = 0; i < ArrSetDaySuport.length; i++) {
                Araybinding.push(new ItemComboBox(
                    ArrSetDaySuport[i].processCateNo,
                    ArrSetDaySuport[i].processDate,
                    nts.uk.time.applyFormat("Short_YMDW", ArrSetDaySuport[i].paymentDate) + ' | ' + ArrSetDaySuport[i].empExtraRefeDate
                    )
                )
            }
            return Araybinding;
        }

        resetEmployee(processCateNo) {
            let self = this;
            self.itemBinding()[processCateNo - 1].employeeString('');
            self.itemBinding()[processCateNo - 1].employeeList([]);
        }

        startPage(): JQueryPromise<any> {
            let self = this,
                dfd = $.Deferred();
            service.findDisplayRegister().done(data => {
                self.itemTable = new ItemTable();
                if (data) {
                    self.itemTable.setData(data);
                }

                let isExists: Array<number> = [];
                for (let i = 0; i < self.itemTable.processInfomations.length; i++) {
                    isExists.push(self.itemTable.processInfomations[i].processCateNo)
                }
                let arrItemBindingProcessinformations: Array<model.ProcessInfomation> = [];
                for (let i: number = 0; i < MAX_NUMBER_SETTING; i++) {
                    if (_.includes(isExists, i + 1)) {
                        let index = _.indexOf(isExists, i + 1);
                        arrItemBindingProcessinformations.push(self.itemTable.processInfomations[index]);
                    }
                    else {
                        arrItemBindingProcessinformations.push(new model.ProcessInfomation({
                                processCateNo: i + 1,
                                processCls: '',
                                deprecatCate: 0
                            }
                        ));
                    }
                }

                for (let i: number = 0; i < MAX_NUMBER_SETTING; i++) {
                    let employeeString = '',
                        employeeList = [],
                        currentProcessDates,
                        processCateNo = i + 1;

                    let employeeSetting:any = _.find(self.itemTable.empTiedProYear, x => {
                        if (x != null) {
                            return x.processCateNo == i + 1;
                        }
                    });

                    if (employeeSetting) {
                        employeeSetting.getEmploymentCodes = _.orderBy(employeeSetting.getEmploymentCodes);
                        for (let j = 0; j < employeeSetting.getEmploymentCodes.length; j++) {
                            let obj = _.find(self.itemTable.empCdNameImports, x => {
                                return x.code == employeeSetting.getEmploymentCodes[j];
                            });
                            employeeList.push(obj);
                            employeeString == '' ? employeeString += obj.name : employeeString += (', ' + obj.name);
                        }
                    }

                    currentProcessDates = _.find(self.itemTable.currentProcessDates, x => {
                        return x.processCateNo == processCateNo;
                    });

                    self.itemBinding.push(new ItemBinding(
                        arrItemBindingProcessinformations[i],
                        _.sortBy(_.filter(self.itemTable.setDaySupports, function (o) {
                                return o.processCateNo == i + 1;
                            }),
                            function (o) {
                                return o.processDate;
                            }),
                        employeeString, _.orderBy(employeeList, ['code'], ['asc']),
                        self.getYear(self.itemTable.setDaySupports, i),
                        self.getListMonth(self.itemTable.setDaySupports, i),
                        currentProcessDates
                    ));
                }
                console.log(self.itemBinding());
                console.log(self.itemTable);

                dfd.resolve();
                $('#A2_2 #button_update').eq(0).focus();
            });
            return dfd.promise();
        }

        registerProcessing() {
            let self = this;
            $('#A2_2 #button_update').eq(0).focus();
            let commandData = {currProcessDateCommand: [], empTiedProYearCommand: []};
            for (let i = 0; i < MAX_NUMBER_SETTING; i++) {
                if (self.itemBinding()[i].isNotAbolition()) {
                    commandData.currProcessDateCommand.push({giveCurrTreatYear: self.itemBinding()[i].monthsSelectd()});
                    let codeList = _.map(self.itemBinding()[i].employeeList(), "code");
                    if (codeList.length > 0) {
                        commandData.empTiedProYearCommand.push({employmentCodes: codeList});
                    } else {
                        nts.uk.ui.dialog.alertError({
                            messageId: "MsgQ_217",
                            message: nts.uk.resource.getMessage("MsgQ_217", [i + 1])
                        });
                        return;
                    }
                } else {
                    commandData.currProcessDateCommand.push({giveCurrTreatYear: null});
                    commandData.empTiedProYearCommand.push({employmentCodes: []});
                }
            }

            block.invisible();
            service.registerProcessing(commandData).done(function (data) {
                nts.uk.ui.dialog.info({messageId: "Msg_15"});
            }).fail(function (error) {
                dialog.alertError(error.message);
            }).always(function () {
                block.clear();
            });
        }
    }

    export class ItemBinding {
        processInfomation: model.ProcessInfomation;
        setDaySupports: KnockoutObservableArray<model.SetDaySupport>;
        setDaySupportsSelectedCode: KnockoutObservable<number>;
        employeeString: KnockoutObservable<string>;
        employeeList: KnockoutObservableArray<any>;
        years: KnockoutObservableArray<ItemComboBox>;
        yaersSelected: KnockoutObservable<number>;
        months: KnockoutObservableArray<ItemComboBox>;
        monthsSubcriceYear: KnockoutObservableArray<ItemComboBox> = ko.observableArray([]);
        monthsSelectd: KnockoutObservable<number>;
        currentYaerMonthSelected:KnockoutObservable<number>=ko.observable(0);
        isNotAbolition:KnockoutObservable<boolean>=ko.observable(false);

        constructor(processInfomation: model.ProcessInfomation, setDaySupports: Array<model.SetDaySupport>, employeeString: string, employeeList: Array, years: Array<ItemComboBox>, months: Array<ItemComboBox>,currentProcessDate:model.CurrentProcessDate ) {
            let selfItemBinding = this;
            selfItemBinding.processInfomation = processInfomation;
            selfItemBinding.setDaySupports = ko.observableArray(setDaySupports);
            selfItemBinding.setDaySupportsSelectedCode = ko.observable(0);
            selfItemBinding.employeeString = ko.observable(employeeString);
            selfItemBinding.employeeList = ko.observableArray(employeeList);
            selfItemBinding.years = ko.observableArray(years);
            selfItemBinding.yaersSelected = ko.observable(0);
            selfItemBinding.months = ko.observableArray(months);
            selfItemBinding.monthsSelectd = ko.observable(0);
            if(currentProcessDate){ selfItemBinding.currentYaerMonthSelected(currentProcessDate.giveCurrTreatYear)}

            selfItemBinding.yaersSelected.subscribe(function (data) {
                selfItemBinding.monthsSubcriceYear.removeAll();
                selfItemBinding.monthsSubcriceYear(
                    _.filter(selfItemBinding.months(), function (o) {
                        return parseInt(o.code / 100) == data;
                    })
                )
            });

             let currentYear =new Date().getFullYear();
            let startYearSelected=parseInt((selfItemBinding.currentYaerMonthSelected())/100);
            let startMonthsSelected=selfItemBinding.currentYaerMonthSelected();
            selfItemBinding.yaersSelected(startYearSelected);
            selfItemBinding.monthsSelectd(startMonthsSelected);

             if(selfItemBinding.processInfomation.processCls() != '' && selfItemBinding.processInfomation.deprecatCate == model.Abolition.NOT_ABOLITION ){
                 selfItemBinding.isNotAbolition(true);
             }


        }
    }

    export interface IitemTable {
        informationDto: Array<model.ProcessInfomation>,
        setDaySupportDto: Array<model.SetDaySupport>,
        currProcessDateDto: Array<model.CurrentProcessDate>,
        empTiedProYearDto: Array<model.EmpTiedProYear>,
        empCdNameImports: Array<model.EmpCdNameImport>
    }

    export class ItemTable {

        processInfomations: Array<model.ProcessInfomation> = [];
        setDaySupports: Array<model.SetDaySupport> = [];
        currentProcessDates: Array<model.CurrentProcessDate> = [];
        empCdNameImports: Array<model.EmpCdNameImport> = [];
        empTiedProYear: Array<model.EmpTiedProYear> = [];

        constructor() {

        }


        setData(param:IitemTable){
            let self=this;
            if (param) {
                param.informationDto.forEach(function (item) {
                    self.processInfomations.push(new model.ProcessInfomation(item));
                });
                self.currentProcessDates = param.currProcessDateDto;
                self.setDaySupports = param.setDaySupportDto;
                self.empCdNameImports = param.empCdNameImports;
                self.empTiedProYear = param.empTiedProYearDto;
            }
        }
    }

    export class ItemComboBox {
        processNO: number;
        code: number;
        name: string;

        constructor(processNO: number, code: number, name: string) {
            this.processNO = processNO;
            this.code = code;
            this.name = name;
        }
    }
}

