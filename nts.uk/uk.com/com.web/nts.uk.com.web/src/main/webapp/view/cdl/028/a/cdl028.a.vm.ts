module nts.uk.com.view.cdl028.a.viewmodel {

    import setShared = nts.uk.ui.windows.setShared;

    export class ScreenModel {
        required: KnockoutObservable<boolean>;
        standardDate: KnockoutObservable<number> =ko.observable(null);
        enable: KnockoutObservable<boolean>;
        yearValue: KnockoutObservable<any> = ko.observable({startDate: moment.utc().format("YYYY"), endDate: moment.utc().format("YYYY")
        });
        startDateString: KnockoutObservable<string>;
        endDateString: KnockoutObservable<string>;
        modeScreen : KnockoutObservable<number> = ko.observable(null);

        firstMonth : KnockoutObservable<number> =ko.observable(null);
        financialYear: KnockoutObservable<number> =ko.observable(null);
        standardDate: KnockoutObservable<number> =ko.observable(null);

        startDateFiscalYear: KnockoutObservable<number> =ko.observable(null);
        endDateFiscalYear: KnockoutObservable<number> =ko.observable(null);
        endDateDay: KnockoutObservable<number> =ko.observable(null);
        constructor() {
            var self = this;
            let params: any = nts.uk.ui.windows.getShared("CDL028_INPUT");
            if (params == null || params === undefined) {
                return;
            }
            self.modeScreen(params.mode);
            self.standardDate(params.date == null ? parseInt(moment().format("YYYYMMDD")) : nts.uk.time.parseYearMonthDate(params.date).toValue());
        }
        /**
         * startPage
         */
        public startPage(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            var now = moment();
            let newDate : string = now.format("YYYY/MM/DD");
            self.required = ko.observable(false);
            self.startDateString = ko.observable();
            self.endDateString = ko.observable("");
            self.standardDate() == null ? newDate : self.standardDate();
            let startDateTemp:number = self.convertYearToInt(self.standardDate());
            let startMonthDB: number;
            switch (self.modeScreen()){

                case MODE_SCREEN.BASE_DATE:
                    self.standardDate();
                    break;

                case MODE_SCREEN.ALL:
                    self.standardDate(self.convertYearToInt(self.standardDate())+"0101");
                    service.getStartMonth().done(function(response: IStartMonth) {
                        if(response.startMonth != null){
                            startMonthDB = response.startMonth;
                            if(( startMonthDB) >= self.getMonthToInt(self.standardDate())){
                                self.financialYear(startDateTemp+""+startMonthDB +"01");
                            } else {
                                self.financialYear((startDateTemp - 1)+""+startMonthDB+"01");
                            }
                            self.firstMonth(startMonthDB);
                        }
                    }).fail(function() {
                        setShared('CDL028_A_PARAMS', {
                            status: false
                        });
                        nts.uk.ui.windows.close();
                    });
                    break;

                case MODE_SCREEN.YEAR_PERIOD_FINANCE:
                    service.getStartMonth().done(function(response: IStartMonth) {
                        if(response.startMonth != null){
                            startMonthDB = response.startMonth;
                            if(( startMonthDB) >= self.getMonthToInt(self.standardDate())){
                                self.financialYear(startDateTemp+""+startMonthDB +"01");
                            } else {
                                self.financialYear((startDateTemp - 1)+""+startMonthDB+"01");
                            }
                            self.firstMonth(startMonthDB);
                        }
                    }).fail(function() {
                        setShared('CDL028_A_PARAMS', {
                            status: false
                        });
                        nts.uk.ui.windows.close();
                    });

                    break;

                case MODE_SCREEN.YEAR_PERIOD:
                    self.standardDate(self.convertYearToInt(self.standardDate())+"0101");

                    break;
            }

            dfd.resolve();
            return dfd.promise();
        }

        /**
         * proceed
         */
        proceed(){
            let self = this, dfd = $.Deferred();
            switch (self.modeScreen()){

                case MODE_SCREEN.BASE_DATE:
                    self.standardDate();
                    self.startDateFiscalYear(null);
                    self.endDateFiscalYear(null);
                    break;
                case MODE_SCREEN.ALL:
                case MODE_SCREEN.YEAR_PERIOD_FINANCE:
                    self.standardDate();
                    self.startDateFiscalYear(self.yearValue().startDate+""+ self.getFullMonth(self.firstMonth()) +"01");
                    if( self.firstMonth()!= 1){
                        self.endDateDay (moment((parseInt(self.yearValue().endDate)+1)+"-"+ self.getFullMonth(self.firstMonth()) , "YYYY-MM").daysInMonth() -1);
                        self.endDateFiscalYear((self.convertYearToInt(self.yearValue().endDate)+1)+""+ self.getFullMonth(self.firstMonth() - 1) +""+self.endDateDay());
                    } else {
                        self.endDateFiscalYear(self.convertYearToInt((self.yearValue().endDate)) + "1231");
                    }
                    break;

                case MODE_SCREEN.YEAR_PERIOD:
                    self.standardDate();
                    self.startDateFiscalYear(self.convertYearToInt(self.yearValue().startDate)+"0101");
                    self.endDateFiscalYear(self.convertYearToInt(self.yearValue().endDate)+"1231");
                    break;
            }
               /**
               * share param
               * status,standardDate,startDateFiscalYear,endDateFiscalYear
               */
                setShared('CDL028_A_PARAMS', {
                 status: true,
                 mode: self.modeScreen() == MODE_SCREEN.YEAR_PERIOD ? MODE_SCREEN.YEAR_PERIOD_FINANCE : self.modeScreen(),
                 standardDate: moment(self.standardDate() + "").format("YYYY/MM/DD"),
                 startDateFiscalYear: moment(self.startDateFiscalYear() + "").format("YYYY/MM/DD"),
                 endDateFiscalYear: moment(self.endDateFiscalYear() + "").format("YYYY/MM/DD")
                });
                 nts.uk.ui.windows.close();
                 dfd.resolve();
                 return dfd.promise();
        };

        /**
         * cancel
         */
        cancel(){
            setShared('CDL028_A_PARAMS', {
                status: false
            });
            nts.uk.ui.windows.close();
        };

        convertYearToInt(standardDate: any) {
            let year: string;
            standardDate = standardDate+"";
            year = standardDate.slice(0, 4);
            return parseInt(year);
        }

        getMonthToInt(standardDate: any){
            let month: string;
            standardDate = standardDate+"";
            month = standardDate.slice(4, 6);
            return parseInt(month,10);
        }

        getFullMonth(month: number): string {
            if(month < 10) {
                return "0" + month;
            } else {
                return "" + month;
            }
        }
    }
    export enum MODE_SCREEN {
        //mode standard date
        BASE_DATE = 1,

        //YEAR PERIOD FINANCE
        YEAR_PERIOD_FINANCE = 2,

        //All
        ALL = 3,

        //YEAR PERIOD
        YEAR_PERIOD = 5
    }

    interface  IStartMonth{
        startMonth: number;
        companyId : string;
    }
    class StartMonth {
        startMonth: number;
        companyId : string;
        constructor(startMonth: IStartMonth) {
            this.startMonth = startMonth.startMonth;
            this.companyId = startMonth.companyId;

        }
    }
}

