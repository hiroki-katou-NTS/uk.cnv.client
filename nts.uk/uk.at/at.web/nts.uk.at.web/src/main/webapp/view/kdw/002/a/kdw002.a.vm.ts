module nts.uk.at.view.kdw002.a {
    export module viewmodel {

        @bean()
        export class ScreenModel extends ko.ViewModel {
            headerColorValue: KnockoutObservable<string>;
            // A2_3
            attendanceItems: KnockoutObservableArray<any>;
            aICurrentCode: KnockoutObservable<any>;
            attendanceItemColumn: KnockoutObservableArray<any>;
            txtItemId: KnockoutObservable<any>;
            txtItemName: KnockoutObservable<any>;
            timeInputCurrentCode: KnockoutObservable<any>;
            linebreak: KnockoutObservable<any>;
            frameCategory: KnockoutObservable<number>;
            isDaily: boolean = true;
            sideBar: KnockoutObservable<number>;
            // ver 8 A8_2, A8_3
            lineBreakPositions: KnockoutObservableArray<any>;
            lineBreakPosition: KnockoutObservable<number> = ko.observable(0);
            //  ver8 A7_2
            displayName: KnockoutObservable<string> = ko.observable("");
            displayNameEnable: KnockoutObservable<boolean> = ko.observable(false);

            gridHeight = window.innerHeight - 250;
            
            constructor() {
                super();
                const self = this;
                nts.uk.sessionStorage.getItemAndRemove(nts.uk.request.STORAGE_KEY_TRANSFER_DATA).ifPresent(value => {
                    const data = JSON.parse(value);
                    self.isDaily = data.ShareObject;
                });
                self.lineBreakPositions = ko.observableArray([
                    new Items(0, self.$i18n('KDW002_68')),
                    new Items(1, self.$i18n('KDW002_69', ['1'])),
                    new Items(2, self.$i18n('KDW002_69', ['2'])),
                    new Items(3, self.$i18n('KDW002_69', ['3'])),
                    new Items(4, self.$i18n('KDW002_69', ['4'])),
                    new Items(5, self.$i18n('KDW002_69', ['5'])),
                    new Items(6, self.$i18n('KDW002_69', ['6'])),
                    new Items(7, self.$i18n('KDW002_69', ['7'])),
                    new Items(8, self.$i18n('KDW002_69', ['8'])),
                    new Items(9, self.$i18n('KDW002_69', ['9']))
                ]);
                self.sideBar = ko.observable(1);
                self.headerColorValue = ko.observable('');
                self.linebreak = ko.observable(0);
                self.timeInputCurrentCode = ko.observable();
                self.txtItemId = ko.observable(null);
                self.txtItemName = ko.observable(''); 
                self.displayName = ko.observable('');// ver8
                self.displayNameEnable = ko.observable(false);// ver8
                self.lineBreakPosition = ko.observable(0);
                self.attendanceItems = ko.observableArray([]);
                self.aICurrentCode = ko.observable(null);
                self.frameCategory = ko.observable(null);
                self.aICurrentCode.subscribe(displayNumber => {
                    if (displayNumber) {
                        let attendanceItem = _.find(self.attendanceItems(), { displayNumber: Number(displayNumber) });
                        self.txtItemName(attendanceItem.oldName);
                        // ver8
                        if(attendanceItem.frameCategory != null && attendanceItem.frameCategory != undefined) {
                            self.displayName(attendanceItem.attendanceItemName);
                            self.displayNameEnable(false);
                        } else {
                            self.displayName(attendanceItem.attendanceItemName);
                            self.displayNameEnable(true);
                        }
                        self.lineBreakPosition(attendanceItem.nameLineFeedPosition); // ver8
                        self.txtItemId(displayNumber);
                        self.frameCategory(attendanceItem.frameCategory);
                        self.timeInputCurrentCode(0);
                        self.linebreak(attendanceItem.nameLineFeedPosition);
                        self.$blockui("show");
                        const getItemService = self.isDaily ? service.getControlOfDailyItem : service.getControlOfMonthlyItem;
                        getItemService(attendanceItem.attendanceItemId).done(cAttendanceItem => {
                            if (!nts.uk.util.isNullOrUndefined(cAttendanceItem)) {
                                self.headerColorValue(self.isDaily ? cAttendanceItem.headerBgColorOfDailyPer : cAttendanceItem.headerBgColorOfMonthlyPer);
                                self.timeInputCurrentCode(cAttendanceItem.inputUnitOfTimeItem);
                            } else {
                                self.headerColorValue(null);
                                self.timeInputCurrentCode(0);
                            }
                        }).fail(error => {
                            self.$dialog.alert(error);
                        }).always(() => {
                            self.$blockui("hide");
                        });
                    } else {
                        $(document).on('click', '.search-btn', function(evt) {
                            self.txtItemId(null);
                            self.txtItemName(null);
                            self.displayName(null);
                            self.lineBreakPosition(0);
                            self.displayNameEnable(false);
                            self.headerColorValue(null);
                            self.aICurrentCode.valueHasMutated();
                        });
                        $(document).on('click', '.clear-btn', function(evt) {
                            self.aICurrentCode(null);
                        });
                    }
                });
                self.attendanceItemColumn = ko.observableArray([
                    { key: 'attendanceItemId', dataType: "number", hidden: true },
                    { headerText: self.$i18n('KDW002_3'), key: 'displayNumber', width: 50, dataType: "number" },
                    { headerText: self.$i18n('KDW002_24'), key: 'attendanceItemName', width: 230, dataType: "string", formatter: _.escape },
                    { key: 'attendanceAtr', dataType: "number", hidden: true },
                    { key: 'nameLineFeedPosition', dataType: "number", hidden: true }
                ]);
                $(".clear-btn").hide();
            }

            created() {
                const self = this;
                self.getAllItems(true);
            }

            getAllItems(startUp: boolean, displayNumber?: number) {
                const self = this;
                self.$blockui("show");
                const getItemService = self.isDaily ? service.getDailyAttdItem : service.getMonthlyAttdItem;
                getItemService().done((data: Array<any>) => {
                    let attendanceItems = data.map(item => ({
                        attendanceItemId: item.attendanceItemId,
                        oldName: item.oldName,
                        attendanceItemName: item.attendanceItemName,
                        attendanceAtr: item.typeOfAttendanceItem,
                        nameLineFeedPosition: item.nameLineFeedPosition,
                        displayNumber: item.attendanceItemDisplayNumber,
                        optionalItemAtr: item.optionalItemAtr,
                        frameCategory: item.frameCategory
                    })).sort((o1, o2) => {
                        if (o1.displayNumber < o2.displayNumber){
                            return -1;
                        }
                        if (o1.displayNumber < o2.displayNumber){
                            return 1;
                        }
                        return 0;
                    });
                    // at start up, grid will not show selected if bind too much data immediately so bind 100 rows first, bind all later
                    self.attendanceItems(startUp ? attendanceItems.slice(0, 100) : attendanceItems);
                    if (attendanceItems.length > 0) {
                        self.aICurrentCode(displayNumber || attendanceItems[0].displayNumber);
                    } else {
                        self.aICurrentCode(null);
                    }
                    _.defer(() => {
                        $("#colorID").focus();
                        if (startUp) self.attendanceItems(attendanceItems);
                    });
                }).fail(error => {
                    self.$dialog.alert(error);
                }).always(() => {
                    self.$blockui("hide");
                });
            }

            submitData(): void {
                let self = this,
                AtItems: any = {};
                self.$validate().then((valid: boolean) => {
                    if (valid) {
                        let attendanceItem = _.find(self.attendanceItems(), { displayNumber: Number(self.aICurrentCode()) });
                        if (self.headerColorValue()) {
                            AtItems.headerBgColorOfDailyPer = self.headerColorValue();
                        }
                        if (self.isDaily) {
                            AtItems.itemDailyID = attendanceItem.attendanceItemId;
                            let command : any = {};
                            command.attendanceItemId = attendanceItem.attendanceItemId;
                            command.displayName = self.displayNameEnable() ? self.displayName() : null;
                            command.nameLineFeedPosition = self.lineBreakPosition();
                            AtItems.updateDailyAttendanceItemCommand = command;
                            if (self.headerColorValue()) {
                                AtItems.headerBgColorOfDailyPer = self.headerColorValue();
                            }
                        } else {
                            AtItems.itemMonthlyID = attendanceItem.attendanceItemId;
                            let command : any = {};
                            command.attendanceItemId = attendanceItem.attendanceItemId;
                            command.displayName = self.displayNameEnable() ? self.displayName() : null;
                            command.nameLineFeedPosition = self.lineBreakPosition();
                            AtItems.updateMonthlyAttendanceItemCommand = command;
                            if (self.headerColorValue()) {
                                AtItems.headerBgColorOfMonthlyPer = self.headerColorValue();
                            }
                        }
                        self.$blockui("show");
                        const updateService = self.isDaily ? service.updateDaily : service.updateMonthly;
                        updateService(AtItems).done(x => {
                            self.$dialog.info({ messageId: 'Msg_15' }).then(() => {
                                self.getAllItems(false, attendanceItem.displayNumber);
                            });
                        }).fail((error) => {
                            self.$dialog.alert(error)
                        }).always(() => {
                            self.$blockui("hide");
                        });
                    }
                });
            }

        }

        class Items {
            code: number;
            name: string;
    
            constructor(code: number, name: string) {
                this.code = code;
                this.name = name;
            }
        }
    }
}
