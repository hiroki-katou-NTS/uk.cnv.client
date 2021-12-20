module nts.uk.at.view.kal001.b {
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import textUK = nts.uk.text;
    import block = nts.uk.ui.block;
    import errors = nts.uk.ui.errors;
    export module viewmodel {
        export class ScreenModel {

            columns: Array<any>;
            currentSelectedRow: KnockoutObservable<any>;
            eralRecord: KnockoutObservable<number>;
            eralRecordText: KnockoutObservable<string>;
            dataSource: Array<model.ValueExtractAlarmDto> = [];
            employeeIds: Array<String>;
            flgActive: KnockoutObservable<boolean>;
            processId: string;

            isTopPage: boolean;
            menuItems: KnockoutObservableArray<any> = ko.observableArray([]);

            constructor(param: any) {
                let self = this;
                self.isTopPage = !!param.isTopPage;
                self.processId = param.processId;
                self.dataSource = param.listAlarmExtraValueWkReDto || [];
                self.employeeIds = param.employeeIds || self.dataSource.map((i: model.ValueExtractAlarmDto) => i.employeeID);
                self.eralRecord = ko.observable(param.totalErAlRecord || 0);
                self.eralRecordText = ko.observable(self.getEralRecordText());
                self.currentSelectedRow = ko.observable(null);
                self.flgActive = ko.observable(true);
                self.columns = [
                    {headerText: '', key: 'guid', width: 1, hidden: true},
                    {headerText: getText('KAL001_20'), key: 'workplaceName', width: 100},
                    {headerText: getText('KAL001_13'), key: 'employeeCode', width: 85},
                    {headerText: getText('KAL001_14'), key: 'employeeName', width: 130},
                    {headerText: getText('KAL001_15'), key: 'alarmValueDate', width: 100},
                    {headerText: getText('KAL001_16'), key: 'categoryName', width: 60},
                    {headerText: getText('KAL001_17'), key: 'alarmItem', width: 115},
                    {headerText: getText('KAL001_18'), key: 'alarmValueMessage', width: 245},
                    {headerText: getText('KAL001_41'), key: 'checkedValue', width: 100},
                    {headerText: getText('KAL001_19'), key: 'comment', width: 260}
                ];
                if (self.isTopPage) {
                    self.columns.push({headerText: getText('KAL001_162'), key: 'menuDisplay', width: 225});
                } else {
                    if (_.isEmpty(self.dataSource)) {
                        nts.uk.ui.dialog.info({ messageId: "Msg_835" });
                        self.flgActive(false);
                    }
                }
            }

            getEralRecordText(): string {
                const self = this;
                if (_.isEmpty(self.dataSource) || self.eralRecord() <= 1000) {
                    return "";
                }
                return nts.uk.resource.getMessage("Msg_1524", [1000]);
            }

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();

                if (self.isTopPage) {
                    block.invisible();
                    service.getExtractAlarmWebMenuData(self.employeeIds).done((res: Array<model.ValueExtractAlarmDto>) => {
                        self.dataSource = res;
                        self.eralRecord = ko.observable(res.length);
                        self.eralRecordText = ko.observable(self.getEralRecordText());
                        self.convertMenuDisplay();
                        self.initGrid();
                        dfd.resolve();
                        if (_.isEmpty(self.dataSource)) {
                            nts.uk.ui.dialog.info({ messageId: "Msg_835" });
                            self.flgActive(false);
                        }
                    }).fail(error => {
                        alertError(error);
                        dfd.reject();
                    }).always(() => {
                        block.clear();
                    });
                } else {
                    self.initGrid();
                    dfd.resolve();
                }

                return dfd.promise();
            }

            initGrid() {
                const self = this;
                $("#grid").ntsGrid({
                    height: '450px',
                    width: '1222px',
                    dataSource: self.dataSource,
                    primaryKey: 'guid',
                    columns: self.columns,
                    virtualization: true,
                    virtualizationMode: 'continuous',
                    features: [
                        {
                            name: 'Paging',
                            type: 'local',
                            pageSize: 20,
                            pageIndexChanged: () => {
                                self.bindingHandleClickLink();
                            }
                        },
                        {
                            name: "Tooltips",
                            columnSettings: [
                                {columnKey: "workplaceName", allowTooltips: true},
                                {columnKey: "menuDisplay", allowTooltips: false}
                            ]
                        },
                        {
                            name: 'Resizing',
                            columnSettings: [
                                {columnKey: 'guid', allowResizing: false},
                                {columnKey: 'workplaceName', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'employeeCode', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'employeeName', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'alarmValueDate', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'categoryName', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'alarmItem', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'alarmValueMessage', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'checkedValue', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'comment', allowResizing: true, minimumWidth: 60},
                                {columnKey: 'menuDisplay', allowResizing: true, minimumWidth: 60}
                            ]
                        }
                    ],
                    enableTooltip: true
                });
                self.bindingHandleClickLink();
            }

            bindingHandleClickLink() {
                const vm = this;
                $("#grid").on("click", ".link-button", (evt1) => {
                    if (evt1.target.classList[1].split('_')[0] == "goto") {
                        const programId = evt1.target.classList[1].split('_')[1];
                        const rowId = evt1.target.classList[1].split('_')[2];
                        const selectedRow: model.ValueExtractAlarmDto = _.find(vm.dataSource, r => r.guid == rowId);
                        $(".popup-area1").ntsPopup("init");
                        vm.menuItems([]);
                        if (selectedRow) {
                            const menu = _.find(selectedRow.menuItems, (i: any) => i.programId == programId);
                            if (menu) {
                                const idx = menu.url.indexOf("/view");
                                const path = _.isNil(menu.queryString) ? menu.url.substring(idx) : menu.url.substring(idx) + "?" + menu.queryString;
                                const webApps = ["com", "at", "pr", "hr"];
                                const webAppId = _.find(webApps, i => menu.url.indexOf(i + ".web") >= 0);
                                if (webAppId)
                                    nts.uk.request.jumpFromDialogOrFrame(webAppId, path);
                                else
                                    nts.uk.request.jumpFromDialogOrFrame(path);
                            }
                        }
                    } else {
                        const rowId = evt1.target.classList[1].split('_')[1];
                        const selectedRow: model.ValueExtractAlarmDto = _.find(vm.dataSource, r => r.guid == rowId);
                        if (selectedRow) {
                            $(".popup-area1").ntsPopup({
                                trigger: ".openpopup_" + rowId,
                                position: {
                                    my: "left top",
                                    at: "left bottom",
                                    of: ".openpopup_" + rowId
                                },
                                showOnStart: false,
                                dismissible: true
                            });

                            vm.menuItems(selectedRow.menuItems);
                            $(".popup-area1").ntsPopup("show");
                        }
                    }
                });
            }

            convertMenuDisplay() {
                const self = this;
                self.dataSource.forEach(row => {
                    row.menuDisplay = `<ul>`;
                    row.menuItems.forEach((item, index) => {
                        if (index < 2) {
                            row.menuDisplay += `<li>
                                <div class="link-button goto_` + item.programId + `_` + row.guid + `">
                                    ` + item.menuName + `
                                </div>
                            </li>`;
                        } else if (index == 2) {
                            row.menuDisplay += `<li>
                                <div class="link-button openpopup_` + row.guid + `">
                                    ...
                                </div>
                            </li>`;
                        }
                    });
                    row.menuDisplay += `</ul>`;
                });
            }

            handleClickLinkPopup(url: string, queryString?: string) {
                const idx = url.indexOf("/view");
                const path = _.isNil(queryString) ? url.substring(idx) : url.substring(idx) + "?" + queryString;
                const webApps = ["com", "at", "pr", "hr"];
                const webAppId = _.find(webApps,i => url.indexOf(i + ".web") >= 0);
                if (webAppId)
                    nts.uk.request.jumpFromDialogOrFrame(webAppId, path);
                else
                    nts.uk.request.jumpFromDialogOrFrame(path);
            }

            exportExcel(): void {
                let self = this;
                block.invisible();
//                let params = {
//                    data: self.dataSource
//                };
                service.exportAlarmData(self.processId, nts.uk.ui.windows.getShared("extractedAlarmData").currentAlarmCode).done(() => {

                }).fail((errExcel) => {
                    alertError(errExcel);
                }).always(() => {
                    block.clear();
                });
            }

            sendEmail(): void {
                let self = this;
                //nts.uk.ui.windows.setShared("employeeList", _.uniqWith(_.filter(shareEmployee,function(x){return x.workplaceID !=null;} ), _.isEqual));
//                let shareEmployee = _.map(self.dataSource, (item) =>{
//                   return {employeeID: item.employeeID, workplaceID: item.workplaceID}; 
//                });
//                nts.uk.ui.windows.setShared("employeeList", _.uniqWith(shareEmployee, _.isEqual));
                nts.uk.ui.windows.setShared("processId", self.processId);
                modal("/view/kal/001/c/index.xhtml").onClosed(() => {

                });

            }

            closeDialog() {
                nts.uk.ui.windows.close();
            }
        }
    }


    export module model {

        export interface ValueExtractAlarmDto {
            guid: string;
            workplaceID: string;
            hierarchyCd: string;
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
            menuDisplay: string;
            menuItems: Array<any>;
        }
    }


}