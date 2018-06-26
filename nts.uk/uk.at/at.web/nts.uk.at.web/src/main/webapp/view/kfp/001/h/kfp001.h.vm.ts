module nts.uk.com.view.kfp001.h.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    export class ScreenModel {
        periodValue: KnockoutObservable<any>;
        listData: KnockoutObservableArray<DataModel>;
        selectedData: KnockoutObservable<any>;
        
        constructor() {
            var self = this;
            self.periodValue = ko.observable({startDate: moment.utc().subtract(1, "y").add(1, "d").format("YYYY/MM/DD"), endDate: moment.utc().format("YYYY/MM/DD")});
            self.listData = ko.observableArray([]);
            self.selectedData = ko.observable(null);
        }
        
        startPage(): JQueryPromise<any> {
            let self = this,
            dfd = $.Deferred(),
            start = moment.utc(self.periodValue().startDate, "YYYY/MM/DD").format("YYYY-MM-DD"), 
            end = moment.utc(self.periodValue().endDate, "YYYY/MM/DD").format("YYYY-MM-DD");
            service.getLogData(start, end).done((result: Array<any>) => {
                if (result && result.length > 0) {
                    let list: Array<DataModel> = _.map(result, log => {
                        return new DataModel(log.id, log.code, log.name, log.executionDt, log.executorCode, log.executorName, log.aggregationStart + getText("KFP001_30") + log.aggregationEnd, log.status, log.targetNum, log.errorNum);
                    });
                    self.listData(list);
                }
                self.initIGrid();
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
                alert(error);
            }).always(() => {
                block.clear();
            });
            return dfd.promise();
        }
        
        search() {
            
        }
        
        private closeDialog() {
            nts.uk.ui.windows.close();
        }

        initIGrid() {
            let self = this;
            $("#list").igGrid({
                height: '415px',
                dataSource: self.listData(),
                primaryKey: 'logId',
                columns: [
                    { headerText: "", key: 'logId', dataType: 'string', hidden: true },
                    { headerText: getText('KFP001_14'), key: 'code', dataType: 'string', width: '60px' },
                    { headerText: getText('KFP001_16'), key: 'name', dataType: 'string', width: '180px' },
                    { headerText: getText('KFP001_55'), key: 'executionDateTime', dataType: 'string', width: '180px' },
                    { headerText: getText('KFP001_56'), key: 'executorCode', dataType: 'string', width: '160px' },
                    { headerText: getText('KFP001_57'), key: 'executorName', dataType: 'string', width: '140px' },
                    { headerText: getText('KFP001_58'), key: 'aggregationPeriod', dataType: 'string', width: '180px' },
                    { headerText: getText('KFP001_47'), key: 'result', dataType: 'string', width: '140px' },
                    { headerText: "", key: 'targetPeopleNum', dataType: 'number', hidden: true },
                    { headerText: getText('KFP001_31'), key: 'dispTargetPeopleNum', dataType: 'string', width: '80px', template: "{{if ${targetPeopleNum} > 0}} <a href=\"javascript:void(0)\" class=\"linkLabel dialogFLink\" logId=\"${logId}\">${dispTargetPeopleNum}</a> {{else}} ${dispTargetPeopleNum} {{/if}}" },
                    { headerText: "", key: 'errorPeopleNum', dataType: 'number', hidden: true },
                    { headerText: getText('KFP001_59'), key: 'dispErrorPeopleNum', dataType: 'string', width: '80px', template: "{{if ${errorPeopleNum} > 0}} <a href=\"javascript:void(0)\" class=\"linkLabel dialogGLink\" logId=\"${logId}\">${dispErrorPeopleNum}</a> {{else}} ${dispErrorPeopleNum} {{/if}}" }
                ],
                features: [
                    {
                        name: 'Paging',
                        pageSize: 15,
                        currentPageIndex: 0,
                        showPageSizeDropDown: false,
                        pageIndexChanged: () => {
                            self.bindLinkClick();
                        },
                        pageCountLimit: 99
                    }
                ]
            });
        }

        openKfp001fDialog(logId: string) {
            let self = this;
//            block.invisible();
//            service.getListEmpId(logId, ATR.ALL).done((result: Array<string>) => {
//                if (result && result.length) {
//                    let tmp = _.find(self.items(), (x: ItemModel) => x.logId == logId);
//                    setShared("Kmw006dParams", { logId: logId, listEmpId: result, closure: tmp.column1, targetYm: tmp.column2, executionDt: tmp.column3 });
                    modal("/view/kfp/001/f/index.xhtml").onClosed(() => {
                    });
//                }
//            }).fail((error) => {
//                alert(error);
//            }).always(() => {
//                block.clear();
//            });
        }

        openKfp001gDialog(logId: string) {
            let self = this;
//            block.invisible();
//            service.getListEmpId(logId, atr).done((result: Array<string>) => {
//                if (result && result.length) {
//                    let tmp = _.find(self.items(), (x: ItemModel) => x.logId == logId);
                    setShared("Kfp001gParams", { logId: logId });
                    modal("/view/kfp/001/g/index.xhtml").onClosed(() => {
                    });
//                }
//            }).fail((error) => {
//                alert(error);
//            }).always(() => {
//                block.clear();
//            });
        }

        bindLinkClick() {
            let self = this;
            $('.dialogFLink').click(function() {
                var logId = $(this).attr("logId");
                self.openKfp001fDialog(logId);
            });
            $('.dialogGLink').click(function() {
                var logId = $(this).attr("logId");
                self.openKfp001gDialog(logId);
            });
        }

    }
    
    class DataModel {
        logId: string;
        code: string;
        name: string;
        executionDateTime: string;
        executorCode: string;
        executorName: string;
        aggregationPeriod: string;
        result: string;
        targetPeopleNum: number;
        errorPeopleNum: number;
        dispTargetPeopleNum: string;
        dispErrorPeopleNum: string;
        
        constructor(logId: string, code: string, name: string, execDT: string, execCode: string, execName: string, period: string, result: string, targetNum: number, errorNum: number) {
            this.logId = logId;
            this.code = code;
            this.name = name;
            this.executionDateTime = execDT;
            this.executorCode = execCode;
            this.executorName = execName;
            this.aggregationPeriod = period;
            this.result = result;
            this.targetPeopleNum = targetNum;
            this.errorPeopleNum = errorNum;
            this.dispTargetPeopleNum = getText('KFP001_23', [targetNum]);
            this.dispErrorPeopleNum = getText('KFP001_23', [errorNum]);
        }
        
    }
    
}