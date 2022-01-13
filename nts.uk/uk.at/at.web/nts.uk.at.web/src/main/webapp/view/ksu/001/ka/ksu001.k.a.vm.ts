module nts.uk.at.view.ksu001.k.a {
    import setShare = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import character = nts.uk.characteristics;

    const Paths = {
        GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID:"ctx/at/schedule/scheduletable/getall",
        EXPORT: "ctx/at/schedule/personal/by-workplace/export",
        PREVIEW: "ctx/at/schedule/personal/by-workplace/preview"
    };
    @bean()
    class Ksu005aViewModel extends ko.ViewModel {
        itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        selectedCode: KnockoutObservable<string> = ko.observable('');
        enableSetting: KnockoutObservable<boolean> = ko.observable(true);
        name: KnockoutObservable<string> = ko.observable('');
        comments: KnockoutObservable<string>;
        characteristics: Characteristics = {code: null, name: null, comments: null};
        params: any;

        created() {
            const self = this;
            self.$window.shared("dataShareDialogK").done(data => {
                self.params = data;
            });
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
            self.$window.modal('/view/ksu/001/kb/index.xhtml', code).then((returnedData: any) => {
                if (returnedData) {
                    $("#preview-frame")[0].innerHTML = "";
                    let dataList: Array<ItemModel> = [];
                    self.$blockui("invisible");
                    self.$ajax(Paths.GET_SCHEDULE_TABLE_OUTPUT_SETTING_BY_CID).done((data: Array<IScheduleTableOutputSetting>) => {
                        if (data && data.length > 0) {
                            data = _.sortBy(data, ['code']);
                            if (data[0].isAttendance == null) {
                                _.each(data, item => {
                                    dataList.push(new ItemModel(item.code, item.name));
                                });
                                self.itemList(dataList);
                                self.characteristics.code = returnedData.code;
                                self.characteristics.name = returnedData.name;
                                self.characteristics.comments = self.comments();
                                character.save('characterKsu005a', self.characteristics);
                                self.selectedCode(returnedData.code);
                            } else if (data[0].isAttendance) {
                                self.$dialog.info({ messageId: 'Msg_1766' }).then(() => {
                                    self.itemList([]);
                                    $("#A2_3").focus();
                                });
                            } else {
                                self.$dialog.error({ messageId: 'Msg_1970' }).then(() => {
                                    self.closeDialog();
                                });
                            }
                        } else {
                            self.itemList(dataList);
                        }
                        $('#exportExcel').focus();
                    }).always(() => {
                        self.$blockui("hide");
                    });
                }
            });
            $('#exportExcel').focus();
        }

        exportExcel() {
            const vm = this;
            vm.$validate().then(valid => {
                if (valid) {
                    const query: any = {
                        orgUnit: vm.params.orgUnit,
                        orgId: vm.params.orgId,
                        periodStart: vm.params.startDate,
                        periodEnd: vm.params.endDate,
                        employeeIds: vm.params.employeeIds,
                        outputSettingCode: vm.selectedCode(),
                        comment: vm.comments(),
                        excel: true,
                        closureDate: {
                            closureDay: vm.params.closeDate.day,
                            lastDayOfMonth: vm.params.closeDate.lastDay
                        }
                    };
                    vm.$blockui("grayout");
                    nts.uk.request.exportFile(Paths.EXPORT, query).fail(error => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });
                }
            });
        }

        exportPdf() {
            const vm = this;
            vm.$validate().then(valid => {
                if (valid) {
                    const query: any = {
                        orgUnit: vm.params.orgUnit,
                        orgId: vm.params.orgId,
                        periodStart: vm.params.startDate,
                        periodEnd: vm.params.endDate,
                        employeeIds: vm.params.employeeIds,
                        outputSettingCode: vm.selectedCode(),
                        comment: vm.comments(),
                        excel: false,
                        closureDate: {
                            closureDay: vm.params.closeDate.day,
                            lastDayOfMonth: vm.params.closeDate.lastDay
                        }
                    };
                    vm.$blockui("grayout");
                    nts.uk.request.exportFile(Paths.EXPORT, query).fail(error => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });
                }
            });
        }

        previewOutput() {
            const vm = this;
            const query: any = {
                orgUnit: vm.params.orgUnit,
                orgId: vm.params.orgId,
                periodStart: vm.params.startDate,
                periodEnd: vm.params.endDate,
                employeeIds: vm.params.employeeIds,
                outputSettingCode: vm.selectedCode(),
                comment: vm.comments(),
                preview: true,
                closureDate: {
                    closureDay: vm.params.closeDate.day,
                    lastDayOfMonth: vm.params.closeDate.lastDay
                }
            };
            vm.$blockui("grayout");
            $("#preview-frame")[0].innerHTML = "";
            vm.$ajax(Paths.EXPORT, query).then((res: any) => {
                return deferred.repeat(conf => conf
                    .task(() => nts.uk.request.specials.getAsyncTaskInfo(res.taskId))
                    .while(info => info.pending || info.running)
                    .pause(1000));
            }).done((res: any) => {
                if (res.failed || res.status == "ABORTED") {
                    vm.$dialog.error(res.error);
                } else {
                    nts.uk.request.ajax(Paths.PREVIEW + "/" + res.id, null, {dataType: 'text'}).done((data: any) => {
                        const styles = data.substring(data.indexOf("<style>"), data.indexOf("</style>") + 8);
                        const body = data.substring(data.indexOf("<table"), data.indexOf("</body>"));
                        $("#preview-frame")[0].innerHTML = (styles + body);
                    });
                }
            }).fail((res: any) => {
                vm.$dialog.error(res);
            }).always(() => {
                vm.$blockui("hide");
            });
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

    class ItemModel {
        code: string;
        name: string;
    
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
}