module nts.uk.com.view.kal004.e.viewmodel {
    import getText = nts.uk.resource.getText;
    import share = nts.uk.at.view.kal004.share.model;
    
    export class ScreenModel {
        getCategoryId: KnockoutObservable<number>;
        getCategoryName: KnockoutObservable<string>;
        enable: boolean;
        specifyStartMonth: KnockoutObservableArray<any>;
        specifyEndMonth: KnockoutObservableArray<any>;
        strComboMonth: KnockoutObservableArray<any>;
        endComboMonth: KnockoutObservableArray<any>;
        strComboDay: KnockoutObservableArray<any>;
        endComboDay: KnockoutObservableArray<any>;

        //start
        strSelected: KnockoutObservable<number>;
        strSpecifyMonth: KnockoutObservable<number>;
        strMonth: KnockoutObservable<number>;
        strYearSpecifiedType: KnockoutObservable<number>; 
        strComboYearSpecifiedType: KnockoutObservableArray<any>;

        //End
        endSelected: KnockoutObservable<number>;
        endMonth: KnockoutObservable<number>;
        endFromStrMonth: KnockoutObservable<number>;
        endComboFromStrMonth: KnockoutObservableArray<any>;

        getParam: share.ExtractionEDto;

        constructor() {
            var self = this;
            self.enable = true;
            self.specifyStartMonth = ko.observableArray([
               {value: share.SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH, name: getText("KAL004_63")},
               {value: share.SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE, name: getText("KAL004_32")}
            ]);
            self.specifyEndMonth = ko.observableArray([
               {value: share.SpecifyEndMonth.SPECIFY_PERIOD_FROM_START_MONTH, name: getText("KAL004_63")},
               {value: share.SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH, name: getText("KAL004_32")}
            ]);
            
            self.strComboMonth = ko.observableArray(__viewContext.enums.StandardMonth);
            self.endComboMonth = ko.observableArray(__viewContext.enums.ScheBaseMonth);
            self.endComboFromStrMonth = ko.observableArray(__viewContext.enums.ExtractPeriodFromStrMonth);
            self.strComboYearSpecifiedType = ko.observableArray(__viewContext.enums.YearSpecifiedType);
            
            self.getParam = nts.uk.ui.windows.getShared("extractionScheYearDto");
            self.getCategoryName = nts.uk.ui.windows.getShared("categoryName");
            self.getCategoryId = ko.observable(nts.uk.ui.windows.getShared("categoryId"));
           
            //start
            self.strYearSpecifiedType = ko.observable(self.getParam.strYearSpecifiedType);
            self.strSelected = ko.observable(self.getParam.strSpecify);
            self.strSpecifyMonth = ko.observable(self.getParam.strSpecifyMonth);
            self.strMonth = ko.observable(self.getParam.strMonth);
            
            //End
            self.endSelected = ko.observable(self.getParam.endSpecify);
            self.endMonth = ko.observable(self.getParam.endMonth);
            self.endFromStrMonth = ko.observable(self.getParam.endFromStrMonth);
            
            self.registerClearInputError();
            self.setFocus();
        }
        
        /**
         * Set focus input
         */
        setFocus(): void {
            let self = this; 
            if (self.strSelected() == share.SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH) {
                setTimeout(function() {
                    $(".cbStrMonth").focus();
                }, 20);
            } else {
                setTimeout(function() {
                    $(".cbStrYearSpecifiedType").focus();
                }, 20);    
            }
        }
        
        /**
         * Apply setting
         */
        btnDecide(): any {
            var self = this;
            if (self.strSelected() == share.SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE) {
                $(".input-str").trigger("validate");
            }
            
            if ($(".nts-input").ntsError("hasError")) {
                return;
            }
                
            if (self.checkPeriod()) {
                let dataSetShare = self.getData();
                nts.uk.ui.windows.setShared("extractionScheYear", dataSetShare);
                self.cancel_Dialog();
            }
        }

        getData(): any {
            var self = this;
            var extractionId = self.getParam.extractionId;
            var extractionRange = self.getParam.extractionRange;
            var strSpecify = self.strSelected();
            var strSpecifyMonth = self.strSpecifyMonth();
            var strPreviousMonth = null;
            var strCurrentMonth = null;
            var strMonth = null;
            var strYearSpecifiedType = null;
            var endSpecify = self.endSelected();
            var endPreviousMonth = null;
            var endCurrentMonth = null;
            var endMonth = null;
            var endFromStrMonth = self.endFromStrMonth();
            
            //start
            if (self.strSelected() == share.SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH) {
                strPreviousMonth = 0;
                strMonth = self.strMonth();
                if (strMonth == 0) {
                    strCurrentMonth = 1;
                } else {
                    strCurrentMonth = 0;
                }
            }
        
            //end
            if (self.endSelected() == share.SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH) {
                endPreviousMonth = 0;
                endMonth = self.endMonth();
                if (endMonth == 0) {
                    endCurrentMonth = 1;
                } else {
                    endCurrentMonth = 0;
                }
            }

            return {
                extractionId: extractionId,
                extractionRange: extractionRange,
                strSpecify: strSpecify,
                strSpecifyMonth: strSpecifyMonth,
                strPreviousMonth: strPreviousMonth,
                strCurrentMonth: strCurrentMonth,
                strMonth: strMonth,
                strYearSpecifiedType: self.strYearSpecifiedType(),
                endSpecify: endSpecify,
                endPreviousMonth: endPreviousMonth,
                endCurrentMonth: endCurrentMonth,
                endMonth: endMonth,
                endFromStrMonth: endFromStrMonth,
                processingYm: self.getParam.processingYm
            };
        }
        
        /**
         * Check pattern setting
         */
        checkPeriod(): boolean {
            var self = this;
            
            let checkPatternScheYear = self.checkPatternScheduleYear();
            if (!_.isNil(checkPatternScheYear)) {
                nts.uk.ui.dialog.alertError({ messageId: checkPatternScheYear });
                return false;
            }
            
            return true;
        }

        /**
         * 期間選択エラーチェック一覧
         * <CATEGORY=SCHEDULE_YEAR>
         */
        checkPatternScheduleYear() {
            let self = this;
            // (a）開始区分＝「本年月」　AND　終了区分＝「月数」
            if (self.strSelected() == share.SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE && self.endSelected() == share.SpecifyEndMonth.SPECIFY_PERIOD_FROM_START_MONTH) {
                return null;
            }
            
            // (b）開始区分＝「開始月」　AND　終了区分＝「終了月」 AND　開始の月　＋　終了の月　＞　12
            //      （例）１ヶ月前～11ヶ月先⇒NG
            //          １ヶ月前～10ヶ月先⇒OK
            let sumMonth = self.strMonth() + self.endMonth();
            if (self.strMonth() >= 0) {
                sumMonth = sumMonth + 1;
            }
            if (self.strSelected() == share.SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH && self.endSelected() == share.SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH && sumMonth > 12) {
                return "Msg_814";
            }
            
            // (c）開始区分＝「開始月」　AND　終了区分＝「月数」
            if (self.strSelected() == share.SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH && self.endSelected() == share.SpecifyEndMonth.SPECIFY_PERIOD_FROM_START_MONTH) {
                return null;
            }
            
            // (e）開始区分＝「本年」OR 開始区分＝「本年度」AND月＝未来　AND　終了区分＝「当月」
            if (self.strSelected() == share.SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE  && self.endSelected() == share.SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH) {
                let startMonthly = null;
                let endMonthly = null;
                let sysDate = moment();
                let numberStrMonth = self.strSpecifyMonth() - 1;
                
                if (self.strYearSpecifiedType() == share.YearSpecifiedType.FISCAL_YEAR) {
                   startMonthly = moment().set({'year': sysDate.year(), 'month': numberStrMonth, 'date': 1});
                } else {
                   startMonthly = moment(self.getParam.processingYm, "YYYYMM").set({'month': numberStrMonth, 'date': 1});
                }
                
                let numberEndMonth = self.endMonth();
                endMonthly = moment(self.getParam.processingYm, "YYYYMM").add(numberEndMonth, 'months').set('date', 1);
                
                var strYearMonth = startMonthly.format('YYYY-MM-01');
                var endYearMonth = endMonthly.format('YYYY-MM-01');
                if (startMonthly.year() > endMonthly.year()) {
                    return "Msg_812";
                }
                
                if (startMonthly.year() == endMonthly.year() && startMonthly.month() > endMonthly.month()) {
                    return "Msg_812";
                }
            }
            
            // (d）開始区分＝「本年月」　AND　終了区分＝「終了月」 comment by #ver22
            //if (self.strSelected() == share.SpecifyStartMonth.SPECIFY_FIXED_MOON_DEGREE && self.endSelected() == share.SpecifyEndMonth.SPECIFY_CLOSE_END_MONTH) {
            //    return null;
            //}
            
            return null;
        }
        
        /**
         * Clear error input when change radio
         */
        registerClearInputError() {
            let self = this;
            self.strSelected.subscribe((value) => {                
               $(".input-str").ntsError('clear');
            });
            
            self.endSelected.subscribe((value) => {                
               $(".input-end").ntsError('clear');
            });
        }

        cancel_Dialog(): any {
            nts.uk.ui.windows.close();
        }
    }
    
    /**
     * アラームリストで指定する年の種類
     */
    export enum YearSpecifiedType {
         // 本年
        FISCAL_YEAR = 0,
        // 本年度
        CURRENT_YEAR = 1    
    }
}
