module nts.uk.at.view.kmf002.e {
    
    import service = nts.uk.at.view.kmf002.e.service;
    import blockUI = nts.uk.ui.block;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    
    export module viewmodel {
        export class ScreenModel {
            commonTableMonthDaySet: KnockoutObservable<nts.uk.at.view.kmf002.viewmodel.CommonTableMonthDaySet>;
            enableSave: KnockoutObservable<boolean>;
            enableDelete: KnockoutObservable<boolean>;
            startMonth: number;
            
            constructor(){
                let _self = this;
                _self.enableSave = ko.observable(true);
                _self.enableDelete= ko.observable(true);
                _self.commonTableMonthDaySet = ko.observable(new nts.uk.at.view.kmf002.viewmodel.CommonTableMonthDaySet());
                _self.commonTableMonthDaySet().fiscalYear.subscribe(function(newValue) {
                    // change year
                    if (!nts.uk.ui.errors.hasError()) {
                        $.when(_self.start_page()).done(function(data: any) {
                        });    
                    }  
                });
            }
            
            public save(): void {
                let _self = this;
                if (!nts.uk.ui.errors.hasError()) {
                    blockUI.invisible();
                    _self.enableSave(false);
                    service.save(_self.commonTableMonthDaySet().fiscalYear(), _self.commonTableMonthDaySet().arrMonth()).done((data) => {
                        _self.enableDelete(true);
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                            _self.enableSave(true);
                        });
                    }).always(()=> blockUI.clear());
                }
            }
            
            public deleteObj(): void {
                let _self = this;
                nts.uk.ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                    service.remove(_self.commonTableMonthDaySet().fiscalYear(), _self.startMonth).done((data) => {
                     $.when(_self.start_page()).done(function() {
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" });
                    });  
                });
                }).ifNo(() => {
                }).ifCancel(() => {
                }).then(() => {
                });   
            }
            
            /**
             * init default data when start page
             */
            public start_page(): JQueryPromise<void> {
                var dfd = $.Deferred<void>();
                var _self = this;
                const currentYear = _self.commonTableMonthDaySet().fiscalYear();
                const nextYear = moment.utc(currentYear, "YYYY").add(1, 'year').format("YYYY");
                $.when(service.find(currentYear), service.find(nextYear), service.findFirstMonth()).done(function(data: any, data1: any, data2: any) {
                    _self.startMonth = data2.startMonth;
                    // Get current year data
                    let currentYearData: model.PublicHolidayMonthSettingDto[] = _.filter(data?.publicHolidayMonthSettings || [], item => item.month >= data2.startMonth);
                    const hasCurrentData = !_.isEmpty(currentYearData);
                    // Add default values if not exists
                    for (let i = data2.startMonth; i <= 12; i++) {
                      if (!_.find(currentYearData, item => item.month === i)) {
                        currentYearData.push(new model.PublicHolidayMonthSettingDto(currentYear, i, 0));
                      }
                    }
                    currentYearData = _.orderBy(currentYearData, "month");
                    // Get next year data
                    let nextYearData: model.PublicHolidayMonthSettingDto[] = _.filter(data1?.publicHolidayMonthSettings || [], item => item.month < data2.startMonth);
                    const hasNextData = !_.isEmpty(nextYearData);
                    // Add default values if not exists
                    for (let i = 1; i < data2.startMonth; i++) {
                      if (!_.find(nextYearData, item => item.month === i)) {
                        nextYearData.push(new model.PublicHolidayMonthSettingDto(nextYear, i, 0));
                      }
                    }
                    nextYearData = _.orderBy(nextYearData, "month");
                    const arr = _.chain(currentYearData).concat(nextYearData)
                    .map(item => {
                      return {
                        'month': ko.observable(item.month),
                        'day': ko.observable(item.inLegalHoliday),
                        'enable': ko.observable(true)
                      };
                    }).value();
                    _self.commonTableMonthDaySet().arrMonth(arr);
                    _self.enableDelete(hasCurrentData || hasNextData);
                    dfd.resolve();       
                });
                return dfd.promise();
            }
            
              // excle
           public opencdl028Dialog2() {
                var self = this;
                let params = {
                    date: moment(new Date()).toDate(),
                    mode: 5
                };
    
                nts.uk.ui.windows.setShared("CDL028_INPUT", params);
    
                nts.uk.ui.windows.sub.modal("com", "/view/cdl/028/a/index.xhtml").onClosed(function() {
                    var params = nts.uk.ui.windows.getShared("CDL028_A_PARAMS");
                    console.log(params);
                    
                    if (params.status) {
                        let startDate = moment.utc(params.startDateFiscalYear ,"YYYY/MM/DD");
                        let endDate = moment.utc(params.endDateFiscalYear ,"YYYY/MM/DD") ;
                        self.exportExcel(params.mode, startDate, endDate);
                     }
                });
            }                                                           
        
            /**
             * Print file excel
             */
            exportExcel(mode: string, startDate: string, endDate: string) : void {
                var self = this;
                nts.uk.ui.block.grayout();
                service.saveAsExcel(mode, startDate, endDate).done(function() {
                }).fail(function(error) {
                    nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                }).always(function() {
                    nts.uk.ui.block.clear();
                });
            }
       }      
    }
}