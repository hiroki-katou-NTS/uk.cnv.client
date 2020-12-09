module nts.uk.at.kal011.c {

    const PATH_API = {
        //TODO wrote api path
        sendEmail: "at/function/alarm/alarm-list/send-email",
        getWorkPlace: "",
    }

    @bean()
    export class Kal011CViewModel extends ko.ViewModel {

        /**
         * nts.uk.ui.NtsGridListColumn
         */
        columns: Array<any>;
        workPlaceList: Array<WorkPlace> = [];
        processId: string;

        constructor(props: any) {
            super();
            const vm = this;
        }

        created() {
            const vm = this;
            _.extend(window, {vm});
            vm.$window.storage('KAL011CModalData').done((data) => {
                vm.processId = data.processId;
                vm.getWorkPlaceList();
            });
            //mock data
            for (let i = 1; i < 100; i++) {
                vm.workPlaceList.push(new WorkPlace(false, 'id' + i, 'code' + i, 'name' + i));
            }
            vm.startPage();
        }

        /**
         * This function is responsible to close the modal         *
         * @return type void         *
         */
        closeDialog(): any {
            nts.uk.ui.windows.close();
        }

        /**
         *
         * function to get table data
         * @return JQueryPromise
         */
        getWorkPlaceList(): JQueryPromise<any> {
            let vm = this;
            let dfd = $.Deferred();
            vm.$blockui("invisible");
            let command = {
                processId: vm.processId,
            };
            vm.$ajax(PATH_API.getWorkPlace, command).then((data) => {
                //TODO map data with vm.workPlaceList
            }).fail(function (error) {
                vm.$dialog.error({messageId: error.messageId});
            }).always(() => {
                vm.$blockui("clear");
            });
            return dfd.promise();
        }

        /**
         *
         * function start pagea
         * @return JQueryPromise
         */
        startPage(): JQueryPromise<any> {
            let vm = this;
            let dfd = $.Deferred();
            vm.columns = [
                {headerText: '', key: 'GUID', width: 1, hidden: true},
                {
                    headerText: vm.$i18n('KAL011_24'),
                    dataType: 'boolean',
                    key: 'isSendTo',
                    showHeaderCheckbox: true,
                    width: 150,
                    ntsControl: 'isSendTo'
                },
                {headerText: vm.$i18n('KAL011_25'), key: 'wpCode', width: 150},
                {headerText: vm.$i18n('KAL011_26'), key: 'wpName', width: 383}
            ];
            $("#grid").ntsGrid({
                width : 700,
                height: 380,
                dataSource: vm.workPlaceList,
                hidePrimaryKey: true,
                primaryKey: 'GUID',
                virtualization: true,
                rowVirtualization: true,
                virtualizationMode: 'continuous',
                columns: vm.columns,
                features: [],
                ntsControls: [
                    {
                        name: 'isSendTo',
                        options: {value: 1, text: ''},
                        optionsValue: 'value',
                        optionsText: 'text',
                        controlType: 'CheckBox',
                        enable: true
                    }
                ]
            });
            return dfd.promise();
        }

        /**
         *
         * function to send mail
         * @return JQueryPromise
         */
        doSendEmail() {
            const vm = this;
            vm.$blockui("invisible");
            let command = {
                workplaceIds: [
                    "00000000-0000-0000-0000-000000000110"
                ],
                listValueExtractAlarmDto: [
                    {
                        guid: "dc3c8915-b1d2-411a-b8ad-e106751d81bc",
                        workplaceID: "1b70774e-8e1c-4b4d-a71d-153261886c74",
                        hierarchyCd: "",
                        workplaceName: "第一開発部",
                        employeeID: "0117dcfc-200b-46e8-a734-d87f1d563b82",
                        employeeCode: "000001",
                        employeeName: "0110の社員1",
                        alarmValueDate: "2019/01/01",
                        category: 5,
                        categoryName: "日次",
                        alarmItem: "6666",
                        alarmValueMessage: "勤務種類:土曜休日,日曜休日 　計画所定労働時間 + 早退控除時間1 ≠ 323:43",
                        comment: "24",
                        checkedValue: "00:00"
                    }
                ],
                currentAlarmCode : null
            };
            vm.$ajax(PATH_API.sendEmail, command).done((data) => {
                vm.$dialog.info({messageId: "Msg_207"})
                    .then(() => {
                        //TODO write business logic
                        vm.closeDialog();
                    });
            }).fail(function (error) {
                vm.$dialog.error({messageId: error.messageId});
            }).always(() => {
                vm.closeDialog();
                vm.$blockui("clear");
            });
        }
    }

    class WorkPlace {
        GUID: string;
        isSendTo: boolean;
        wpId: string;
        wpCode: string;
        wpName: string;

        constructor(isSendTo: boolean, wpId: string, wpCode: string, wpName: string) {
            this.GUID = nts.uk.util.randomId().replace(/-/g, "_");
            this.isSendTo = isSendTo;
            this.wpId = wpId;
            /* 職場コード*/
            this.wpCode = wpCode;
            /* 職場名*/
            this.wpName = wpName;
        }
    }
}