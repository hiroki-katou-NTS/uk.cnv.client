module nts.uk.at.view.ksu005.a {
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import character = nts.uk.characteristics;

    const Paths = {
        GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID:"ctx/at/schedule/scheduletable/getall"
    };
    @bean()
    class Ksu005aViewModel extends ko.ViewModel {
        currentScreen: any = null;
        itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        selectedCode: KnockoutObservable<string> = ko.observable('');
        enableSetting: KnockoutObservable<boolean> = ko.observable(true);
        name: KnockoutObservable<string> = ko.observable('');
        comments: KnockoutObservable<string>;
        characteristics: Characteristics = {};
        constructor() {
            super();
            const self = this; 
            self.comments = ko.observable("");

            self.selectedCode.subscribe((code: string) => { 
               let obj =  _.find(self.itemList(), {'code':code } );
               if(obj && obj != undefined){
                    self.name(obj.name);
               }
            });        

            self.loadScheduleOutputSetting();
        }

        mounted() {
            $('#exportExcel').focus();
        }

        loadScheduleOutputSetting(): void {
            const self = this;
            let dataList: Array<ItemModel> = [];
            self.$blockui("invisible");			
            self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                if(data && data.length > 0){
                    if(data[0].hasAttendance){
                        self.enableSetting(true);
                    } else {
                        self.enableSetting(false);
                    }

                    if(data[0].isAttendance == null){
                        _.each(data, item =>{
                            dataList.push(new ItemModel(item.code, item.name));
                        });
                        character.restore('characterKsu005a').done((obj: Characteristics) => {
                            if(obj){
                                self.selectedCode(obj.code);                                
                                self.comments(obj.comments);
                            }                            
                        })
                    } else if(data[0].isAttendance){ 
                            self.openDialog();                    
                    } else {
                        self.$dialog.info({ messageId: 'Msg_1970'}).then(() => {
                            self.closeDialog();
                        });
                    }                     
                } 
                self.itemList(_.sortBy(dataList, item => item.code));
            }).always(() => {
                self.$blockui("hide");
            });
        }

        closeDialog(): void {
            const self = this;
            self.characteristics.code = self.selectedCode();
            self.characteristics.name = self.name();
            self.characteristics.comments = self.comments();
            character.save('characterKsu005a', self.characteristics);
            self.$window.close();
        }

        openDialog(): void {
            let self = this;
            let code = self.selectedCode();
            setShare('dataShareKSU005a', code);
            self.currentScreen = nts.uk.ui.windows.sub.modal('/view/ksu/005/b/index.xhtml').onClosed(() => {
                let dataList: Array<ItemModel> = [];
                let res = getShared('dataShareCloseKSU005b');
                self.$blockui("invisible");
                self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                    if (data && data.length > 0) {
                        if (data[0].isAttendance == null) {
                            _.each(data, item => {
                                dataList.push(new ItemModel(item.code, item.name));                                
                            });
                            self.characteristics.code = res.code;
                            self.characteristics.name = res.name;
                            self.characteristics.comments = self.comments();
                            character.save('characterKsu005a', self.characteristics);
                            self.loadScheduleOutputSetting();
                        } else if (data[0].isAttendance) {                            
                            self.$dialog.info({ messageId: 'Msg_1766' }).then(() => {
                                self.openDialog();
                            });
                        } else {
                            self.$dialog.error({ messageId: 'Msg_1970' }).then(() => {                                
                                self.closeDialog();
                            });
                        }
                    }
                    self.itemList(dataList);
                    $('#exportExcel').focus();
                }).always(() => {
                    self.$blockui("hide");
                });
            });
            $('#exportExcel').focus();
        }
    }

    interface Characteristics {
        code: string;
        name: string;
        comments: string;
    }

    interface IScheduleTableOutputSetting {
        code: string;
        name: string;
        additionalColumn: number;
        shiftBackgroundColor: number;
        dailyDataDisplay: number;
        personalInfo: Array<number>;
        additionalInfo: Array<number>;
        attendanceItem: Array<number>;
        workplaceCounterCategories: Array<number>;
        personalCounterCategories: Array<number>;
        isAttendance: boolean;
        hasAttendance: boolean;
 
    }
    class ScheduleTableOutputSetting {
        /** コード */
        code: KnockoutObservable<string> = ko.observable('');

        /** 名称ド */
        name: KnockoutObservable<string> = ko.observable('');

        /** 追加列情報 */
        additionalColumn: KnockoutObservable<number> = ko.observable();        

        /** シフト表利用 */
        shiftBackgroundColor: KnockoutObservable<number> = ko.observable();

        /** 勤務情報 */
        dailyDataDisplay: KnockoutObservable<number> = ko.observable();

        /** 個人情報 */
        personalInfo: KnockoutObservableArray<number> = ko.observableArray([]);

        /** 追加列情報_1 */
        additionalInfo: KnockoutObservableArray<number> = ko.observableArray([]);

        /** 表示項目_1 */
        attendanceItem: KnockoutObservableArray<number> = ko.observableArray([]);

        /** 職場計出力設定名称 */
        workplaceCounterCategories: KnockoutObservableArray<number> = ko.observableArray([]);
        
        /** 個人計出力設定名称 */
        personalCounterCtegories: KnockoutObservableArray<number> = ko.observableArray([]);

        isAttendance: KnockoutObservable<boolean> = ko.observable(true);
        hasAttendance: KnockoutObservable<boolean> = ko.observable(true);
        constructor(params?: IScheduleTableOutputSetting) {
            const self = this;
            if(params){
                self.code(params.code);
                self.name(params.name);
                self.additionalColumn(params.additionalColumn);
                self.shiftBackgroundColor(params.shiftBackgroundColor);
                self.dailyDataDisplay(params.dailyDataDisplay);
                self.personalInfo(params.personalInfo);
                self.additionalInfo(params.additionalInfo);
                self.attendanceItem(params.attendanceItem);
                self.workplaceCounterCategories(params.workplaceCounterCategories);
                self.personalCounterCtegories(params.personalInfo);
                self.isAttendance(params.isAttendance);
                self.hasAttendance(params.hasAttendance);
            }
        } 
    }

    class ItemModel {
        code: string;
        name: string;
    
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
}