module nts.uk.at.view.ksu001.jc.viewmodel {
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import formatById = nts.uk.time.format.byId;
    import alertError = nts.uk.ui.dialog.alertError;
    import getText = nts.uk.resource.getText;

    export class ScreenModel {
        // listWorkType: KnockoutObservableArray<any> = ko.observableArray(nts.uk.ui.windows.container.windows["MAIN_WINDOW"].globalContext.nts.uk.ui._viewModel.content.viewO.listWorkType());
        listWorkType: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedShiftMasterCode: KnockoutObservable<string> = ko.observable('');
        selectedShiftMaster: KnockoutObservable<any> = ko.observable(null);

        selectedTab: KnockoutObservable<string> = ko.observable(getShared('dataForJC'))().selectedTab;
        workplaceId: KnockoutObservable<string> = ko.observable(getShared('dataForJC'))().workplaceId;
        textName: KnockoutObservable<string> = ko.observable(getShared('dataForJC').text || null);
        arrTooltip: any[] = [];

        source: KnockoutObservableArray<any> = ko.observableArray(getShared('dataForJC').data || []);
        dataSource: KnockoutObservableArray<any> = ko.observableArray([]);
        listCheckNeededOfWorkTime: any[] = getShared('dataForJC').listCheckNeededOfWorkTime;
        nashi: string = getText("KSU001_98");

        // List display Shift
        listShift: KnockoutObservableArray<any> = ko.observableArray([]);
        isListShiftFull: KnockoutObservable<boolean> = ko.observable(false);
        visibleArrow: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            let self = this;
            // Combo box selected
            self.selectedShiftMasterCode.subscribe((newValue) => {
                let shiftMaster = _.find(self.listWorkType(), ['shiftMasterCode', newValue]);
                self.selectedShiftMaster(shiftMaster);
            });

            this.initDataFromBScreen();

            // Datasource thay đổi theo listShift
            this.dataSource = ko.computed(function() {
                let tempArr = _.map(self.listShift(), (item, index) => {
                    return { index: index, value: item.shiftMasterName, data: item };
                });
                return tempArr;
            });
            this.isListShiftFull = ko.computed(function() {
                return self.listShift().length >= 31;
            });
            this.visibleArrow = ko.computed(function() {
                return self.listShift().length >= 31 || self.listShift().length == 0;
            });
        }

        clearData(): void {
            let self = this;
            $("#table-date td").html('');
            self.dataSource([]);
        }
        addDay(): void {
            if (!this.isListShiftFull())
                this.listShift.push(this.selectedShiftMaster());
        }

        cellClick(e, d): void {
            this.listShift()[e()] = this.selectedShiftMaster();
            this.listShift.valueHasMutated();
            //            let id = "#cell" + e();
            //            $(id).text(this.selectedShiftMaster().shiftMasterName);
        }

        /** Delete by index */
        deleteDay(indexObs): void {
            var index = indexObs();
            var newArray = this.listShift.slice(0, index).concat(this.listShift.slice(index + 1));
            this.listShift(newArray);
        }

        /** Decision */
        decision(): void {
            let self = this;
            $("#test2").trigger("validate");
            if (nts.uk.ui.errors.hasError()) {
                return;
            }
            if (self.dataSource().length == 0) {
                nts.uk.ui.dialog.alertError({ messageId: "Msg_1591" });
                return;
            }

            // sort dataSoucre (tooltip) ASC
            let dataSourceOrder = _.orderBy(_.remove(self.dataSource(), function(item) { return item.value != 'なし'; }), ['index'], ['asc']);
            let arrTooltip = _.map(dataSourceOrder, (data) => {
                //return '[' + data.value + ']';
                return data.value;
            });
            let arrTooltipClone = _.clone(arrTooltip);

            for (let i = 7; i < arrTooltipClone.length; i += 7) {
                arrTooltip.splice(i, 0, 'lb');
                i++;
            }

            let tooltip: string = arrTooltip.join('→');
            tooltip = tooltip.replace(/→lb/g, '\n');

            // sap xep cho mang lien mach
            let index = 0;
            let arrData = _.map(dataSourceOrder, (dataS) => {
                index++;
                return {
                    index: index,
                    value: dataS.value,
                    data: dataS.data
                };
            });

            console.log(arrData);
            setShared("dataFromJB", {
                text: self.textName(),
                tooltip: tooltip,
                data: arrData
            });

            nts.uk.ui.windows.close();
        }

        /** Close dialog */
        closeDialog(): void {
            nts.uk.ui.windows.close();
        }

        /** Query */
        initShiftWork() {
            let self = this;

            let taisho = {
                targetUnit: 0,
                workplaceId: '',
                workplaceGroupId: ''
            }
            if (self.selectedTab == 'company') {
                taisho.targetUnit = null;
                taisho.workplaceId = null;
                taisho.workplaceGroupId = null;
            }
            if (self.selectedTab == 'workplace') {
                taisho.workplaceId = self.workplaceId;
                taisho.targetUnit = 0
            }
            if (self.selectedTab == 'workplaceGroup') {
                taisho.workplaceGroupId = self.workplaceId;
                taisho.targetUnit = 1
            }

            service.getShiftMasterWorkInfo(taisho).done((data) => {
                data.unshift({ shiftMasterName: nts.uk.resource.getText("KSU001_98"), shiftMasterCode: "", workTime1: "", workTime2: "", remark: "" });
                if (data) {
                    for (let i = 0; i < data.length; i++) {
                        data[i].workTime1 = data[i].workTime1 + " " + data[i].workTime2;
                    }
                }
                self.listWorkType(_.sortBy(data, ['shiftMasterCode']));
                self.initDataFromBScreen();
            }).fail((res: any) => {
                nts.uk.ui.dialog.alert({ messageId: res.messageId });
            });
        }

        /** listShift init data từ màn B */
        initDataFromBScreen() {
            var self = this;
            console.log(self.source());
            _.forEach(self.source(), (item) => {
                let code = item.shiftMasterCode ? item.shiftMasterCode : item.data.shiftMasterCode;
                let matchShift = _.find(self.listWorkType(), ['shiftMasterCode', code]);
                if (matchShift)
                    self.listShift.push(matchShift);
            })
        }

    }
}