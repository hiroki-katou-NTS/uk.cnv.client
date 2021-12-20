module nts.uk.at.kal011.c {

    import ExtractAlarm =  nts.uk.at.kal011.B.ExtractAlarmDto;
    import info = nts.uk.ui.dialog.info;

    const PATH_API = {
        //TODO wrote api path
        sendEmail: "at/function/alarm-workplace/alarm-list/send-email",
        getWorkPlace: "",
    };

    @bean()
    export class Kal011CViewModel extends ko.ViewModel {

        /**
         * nts.uk.ui.NtsGridListColumn
         */
        columns: Array<any>;
        workPlaceList: KnockoutObservableArray<WorkPlace> = ko.observableArray([]);
        alarmExtractList: KnockoutObservableArray<WorkPlace> = ko.observableArray([]);
        processId: string;
        currentCode: string;


        created(params : {data : Array<ExtractAlarm>} ) {
            const vm = this;
            _.extend(window, {vm});
            vm.$window.storage('KAL011CModalData').done((res) => {
                if (res.data && res.data.length) {
                    let lstItem = _.map(res.data, function (i: ExtractAlarm) {
                       // return new WorkPlace(i, false);
                        return new WorkPlace(i);
                    });
                    vm.alarmExtractList(lstItem);
                    vm.workPlaceList(_.uniqBy(lstItem, i => i.workplaceID));
                    vm.currentCode = res.currentCode;
                    vm.startPage();
                };
            });



        }

        mounted() {
            const vm = this;


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
         * function start pagea
         * @return JQueryPromise
         */
        startPage(): JQueryPromise<any> {
            let vm = this;
            let dfd = $.Deferred();
            vm.columns = [

            ];
            $("#grid").ntsGrid({
                width : 700,
                height: 380,
                dataSource: vm.workPlaceList(),
                hidePrimaryKey: true,
                primaryKey: 'guid',
                virtualization: true,
                rowVirtualization: true,
                virtualizationMode: 'continuous',
                columns: [
                    {
                        headerText: "",
                        key: "guid",
                        width: 1,
                        hidden: true
                    },
                    {
                        headerText: vm.$i18n('KAL011_24'),
                        key: "isSendTo",
                        dataType: "boolean",
                        width: "150px",
                        ntsControl: "Checkbox",
                        showHeaderCheckbox: true,
                    },
                    {
                        headerText: vm.$i18n('KAL011_25'),
                        key: "workplaceCode",
                        width: 150
                    },
                    {
                        headerText: vm.$i18n('KAL011_26'),
                        key: "workplaceName",
                        width: 383
                    }
                ],
                features: [],
                ntsControls: [
                    {
                        name: "Checkbox",
                        options: { value: 1, text: "" },
                        optionsValue: "value",
                        optionsText: "text",
                        controlType: "CheckBox",
                        enable: true,
                    },
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
            let lstSelected = _.filter(vm.workPlaceList(), i => i.isSendTo === true);
            let listValueExtractAlarmDto = ko.toJS(vm.alarmExtractList);
            let currentAlarmCode = vm.currentCode;
            let command = {
                workplaceIds: _.uniq(_.map(lstSelected, function (i: any) {
                    return i.workplaceID;
                })),
                listValueExtractAlarmDto,
                currentAlarmCode
            };
            vm.$ajax(PATH_API.sendEmail, command).done((data) => {
                if (data.length > 0) {
                    //info({ message: data });
                    info({message: data, messageId: 'Msg_965'});
                } else {
                    vm.$dialog.info({messageId: "Msg_207"})
                        .then(() => {
                            //vm.closeDialog();
                        });
                }

            }).fail(function (error) {
                vm.$dialog.error({messageId: error.messageId});
            }).always(() => {
                vm.$blockui("clear");
            });
        }
    }

    class WorkPlace {
        guid: string;
        isSendTo: boolean;
        workplaceID: string;
        workplaceCode: string;
        workplaceName: string;
        employeeID: string;
        employeeCode: string;
        employeeName: string;
        alarmValueDate: string;
        category: number;
        categoryName: string;
        alarmItem: string;
        alarmValueMessage: string;
        comment: string;
        checkedValue: string;
        hierarchyCd: string;

        constructor(data: ExtractAlarm) {
            this.guid = nts.uk.util.randomId().replace(/-/g, "_");
            this.isSendTo = false;
            this.workplaceID = data.workplaceId;
            /* 職場コード*/
            this.workplaceCode = data.workplaceCode;
            /* 職場名*/
            this.workplaceName = data.workplaceName;
            this.employeeID = null;
            this.employeeCode = null;
            this.employeeName = null;
            this.alarmValueDate  = data.alarmValueDate;
            this.category = data.category;
            this.categoryName = data.categoryName;
            this.alarmItem = data.alarmItemName;
            this.alarmValueMessage = data.alarmValueMessage;
            this.comment = data.comment;
            this.checkedValue = data.checkTargetValue;
            this.hierarchyCd = data.hierarchyCode;

        }
    }

}