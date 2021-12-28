module nts.uk.com.view.cdl027.a.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import modal = nts.uk.ui.windows.sub.modal;

    export class ScreenModel {
        items: KnockoutObservableArray<DataCorrectionLog>;
        columnsByDate: Array<any> = [
            { headerText: getText('CDL027_7'), key: 'targetDate', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_4'), key: 'targetUser', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_8'), key: 'item', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_9'), key: 'valueBefore', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_10'), key: 'arrow', dataType: 'string', width: '20px' },
            { headerText: getText('CDL027_11'), key: 'valueAfter', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_12'), key: 'modifiedPerson', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_13'), key: 'modifiedDateTime', dataType: 'string', width: '170px' },
            { headerText: getText('CDL027_14'), key: 'correctionAttr', dataType: 'string', width: '70px' }
        ];
        columnsByIndividual: Array<any> = [
            { headerText: getText('CDL027_4'), key: 'targetUser', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_7'), key: 'targetDate', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_8'), key: 'item', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_9'), key: 'valueBefore', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_10'), key: 'arrow', dataType: 'string', width: '20px' },
            { headerText: getText('CDL027_11'), key: 'valueAfter', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_12'), key: 'modifiedPerson', dataType: 'string', width: '120px' },
            { headerText: getText('CDL027_13'), key: 'modifiedDateTime', dataType: 'string', width: '170px' },
            { headerText: getText('CDL027_14'), key: 'correctionAttr', dataType: 'string', width: '70px' }
        ];
        params: any;
        targetStart: string;
        targetEnd: string;

        constructor() {
            var self = this;
            self.items = ko.observableArray([]);
            self.params = getShared("CDL027Params");
            self.targetStart = self.formatTargetDate(self.params.functionId, self.params.period.startDate);
            self.targetEnd = self.params.functionId == FUNCTION_ID.Monthly ? null : self.formatTargetDate(self.params.functionId, self.params.period.endDate);
            // self.targetEnd = self.formatTargetDate(self.params.functionId, self.params.period.endDate);
            switch (self.params.functionId) {
                case FUNCTION_ID.Daily: 
                case FUNCTION_ID.Monthly: 
                case FUNCTION_ID.Salary:
                case FUNCTION_ID.Bonus:
                case FUNCTION_ID.Year_end_adjustment:
                    break;
                default: 
                    self.columnsByDate.pop();
                    self.columnsByIndividual.pop();
                    break;
            }
        }

        startPage(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            block.grayout();
            service.getLogInfor(self.formatParams()).done((result: Array<any>) => {
                if (result && result.length) {
					result = _.sortBy(result, [function(o) { return o.modifiedDateTime; }]).reverse();
                    for (var i = 0; i < result.length; i++) {
                        let r = result[i];
                        self.items.push(new DataCorrectionLog(
                                            self.formatTargetDate(self.params.functionId, r.targetDate), 
                                            r.targetUser, 
                                            r.item, 
                                            r.valueBefore, 
                                            r.valueAfter, 
                                            r.modifiedPerson, 
                                            r.modifiedDateTime, 
                                            r.correctionAttr));
                    }
                }
            }).fail((error) => {
                alertError(error);
            }).always(() => {
                self.initIGrid();
                dfd.resolve();
                block.clear();
            });
            return dfd.promise();
        }
        
        private exportCsv(): void {
            let self = this, l = self.items().length;
            if (l > 0) {
                block.grayout();
                service.exportCsv(self.formatParams()).always(() => {
                    block.clear();
                });
            } else {
                alertError({messageId: "Msg_37"});
            }
        }

        private closeDialog() {
            nts.uk.ui.windows.close();
        }
        
        private formatParams(): any {
            let self = this;
            let _params = {
                pgid: self.params.pgid,
                functionId: self.params.functionId, 
                listEmployeeId: self.params.listEmployeeId, 
                displayFormat: self.params.displayFormat, 
                startYmd: null, endYmd: null, 
                startYm: null, endYm: null, 
                startY: null, endY: null
            };
//            if (self.params.functionId == null) {
                switch (self.params.functionId) {
                    case FUNCTION_ID.Schedule:
                    case FUNCTION_ID.Daily:
                        _params.startYmd = moment.utc(self.params.period.startDate, "YYYY/MM/DD").toISOString();
                        _params.endYmd = moment.utc(self.params.period.endDate, "YYYY/MM/DD").toISOString();
                        return _params;
                    case FUNCTION_ID.Any_period:
                    case FUNCTION_ID.Salary:
                    case FUNCTION_ID.Bonus:
                    case FUNCTION_ID.Monthly_calculation:
                    case FUNCTION_ID.Raising_rising_back:
                        _params.startYm = parseInt(moment.utc(self.params.period.startDate, "YYYY/MM").format("YYYYMM"), 10);
                        _params.endYm = parseInt(moment.utc(self.params.period.endDate, "YYYY/MM").format("YYYYMM"), 10);
                        return _params;
                    case FUNCTION_ID.Monthly:
                        _params.endYmd = moment.utc(self.params.period.endDate, "YYYY/MM/DD").toISOString();
                        _params.startYm = parseInt(moment.utc(self.params.period.startDate, "YYYY/MM").format("YYYYMM"), 10);
                        return _params;
                    default:
                        _params.startY = parseInt(self.params.period.startDate, 10);
                        _params.endY = parseInt(self.params.period.endDate, 10);
                        return _params;
                }
//            } else {
                
//            }
        }
        
        private formatTargetDate(functionId: number, date: string): string {
            switch (functionId) {
                case FUNCTION_ID.Schedule:
                case FUNCTION_ID.Daily:
                    return date;
                case FUNCTION_ID.Monthly:
                case FUNCTION_ID.Any_period:
                case FUNCTION_ID.Salary:
                case FUNCTION_ID.Bonus:
                case FUNCTION_ID.Monthly_calculation:
                case FUNCTION_ID.Raising_rising_back:
                    return moment.utc(date, "YYYY/MM/DD").format("YYYY/MM");
                default:
                    return moment.utc(date, "YYYY/MM/DD").format("YYYY");
            }
        }
        
        private initIGrid() {
            let self = this;
            $("#list").igGrid({
                height: '375px',
                width: '1000px',
                dataSource: self.items(),
                columns: self.params.displayFormat == DISPLAY_FORMAT.BY_DATE ? self.columnsByDate : self.columnsByIndividual,
                features: [
                    {
                        name: 'Paging',
                        type: "local",
                        pageSize: 100,
                        currentPageIndex: 0,
                        showPageSizeDropDown: true,
                        pageCountLimit: 20
                    },
                    {
                        name: "Filtering",
                        type: "local",
                        mode: "simple",
                        columnSettings: [
                            {columnKey: "arrow", allowFiltering: false},
                            {columnKey: "modifiedPerson", allowFiltering: false}
                        ]
                    },
                    {
                        name : 'Resizing',
                        columnSettings: [
                            { columnKey: "arrow", allowResizing: false }
                        ],
                    },
                    {
                        name: "Tooltips"
                    }
                ]
            });
        }
        
    }

    class DataCorrectionLog {
        targetDate: string;
        targetUser: string;
        item: string;
        valueBefore: string;
        arrow: string = getText('CDL027_10');
        valueAfter: string;
        modifiedPerson: string;
        modifiedDateTime: string;
        correctionAttr: string;

        constructor(targetDate, targetUser, item, valueBefore, valueAfter, modifiedPerson, modifiedDateTime, correctionAttr: number) {
            this.targetDate = targetDate;
            this.targetUser = targetUser;
            this.item = item;
            this.valueBefore = valueBefore;
            this.valueAfter = valueAfter;
            this.modifiedPerson = modifiedPerson;
            this.modifiedDateTime = modifiedDateTime == null ? "" : moment.utc(modifiedDateTime).format("YYYY/MM/DD HH:mm:ss");
            switch (correctionAttr) {
                case CORRECTION_ATTR.EDIT: 
                    this.correctionAttr = getText("Enum_CorrectionAttr_EDIT");
                    break;
                case CORRECTION_ATTR.CALCULATE: 
                    this.correctionAttr = getText("Enum_CorrectionAttr_CALCULATE");
                    break;
                case CORRECTION_ATTR.REFLECT: 
                    this.correctionAttr = getText("Enum_CorrectionAttr_REFLECT");
                    break;
                default:
                    break;
            }
        }
    }

    enum DISPLAY_FORMAT {
        BY_DATE = 0,
        BY_INDIVIDUAL = 1
    }

    enum FUNCTION_ID {
        Schedule = 1,
        Daily = 2,
        Monthly = 3,
        Any_period = 4,
        Salary = 5,
        Bonus = 6,
        Year_end_adjustment = 7,
        Monthly_calculation = 8,
        Raising_rising_back = 9
    }

    enum CORRECTION_ATTR {
        EDIT = 0,
        CALCULATE = 1,
        REFLECT = 2
    }
    
}

