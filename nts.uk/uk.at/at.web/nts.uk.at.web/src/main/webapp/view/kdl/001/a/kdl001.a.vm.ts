module nts.uk.at.view.kdl001.a {
    export module viewmodel {

      const KEYWORDS_MAX_COUNT = 10;
        export class ScreenModel {
            columns: KnockoutObservableArray<NtsGridListColumn>;
            multiSelectMode: KnockoutObservable<boolean>;
            isSelection: KnockoutObservable<boolean> = ko.observable(false);
            workingHoursItemLists: KnockoutObservableArray<WorkTimeSet>; //all
            selectableWorkingHours: KnockoutObservableArray<WorkTimeSet> = ko.observableArray([]); //custom
            selectAbleItemList: KnockoutObservableArray<WorkTimeSet>;
            selectAbleCodeList: KnockoutObservableArray<string>;
            selectedCodeList: KnockoutObservableArray<string>;
            selectedCode: KnockoutObservable<string>;
            searchOption: KnockoutObservable<number>;
            startTimeOption: KnockoutObservable<number>;
            startTime: KnockoutObservable<number>;
            endTimeOption: KnockoutObservable<number>;
            endTime: KnockoutObservable<number>;
            isEnableSwitchButton: KnockoutObservable<boolean> = ko.observable(false);
            gridHeight: number = 375;
            initialWorkTimeCodes: Array<String>;
            searchCode: KnockoutObservable<string> = ko.observable(null);

            //ver 8
            workPlaceId: KnockoutObservable<string> = ko.observable(null);
            baseDate: KnockoutObservable<string> = ko.observable(null);
            isEnableCheckStatus: KnockoutObservable<boolean> = ko.observable(false);
            isAllCheckShow: KnockoutObservable<boolean> = ko.observable(false);
            isCheckAllStatus: KnockoutObservable<boolean> = ko.observable(false);
            selectAbleCodeListBk: KnockoutObservableArray<string> = ko.observableArray([]);
            selectedCodeListBk: KnockoutObservableArray<string> = ko.observableArray([]);
            selectedCodeBk: KnockoutObservable<string> = ko.observable(null);
            showNoSelectionRow: boolean = false;

            // ver9
            keywords: KnockoutObservableArray<WorkHoursFilterConditionDto> = ko.observableArray([]);
            defaultKeywords: KnockoutObservableArray<IWorkHoursFilterConditionDto> = ko.observableArray([]);
            searchQuery: KnockoutObservable<string> = ko.observable(null);
            allWorkHours: WorkTimeSet[];
            isAttendanceRole = __viewContext.user.role.isInCharge.attendance;

            constructor() {
                var self = this;
                self.columns = ko.observableArray([]);
                self.multiSelectMode = nts.uk.ui.windows.getShared('kml001multiSelectMode');
                self.isSelection = nts.uk.ui.windows.getShared('kml001isSelection');
                self.showNoSelectionRow = nts.uk.ui.windows.getShared('kdl00showNoSelectionRow');
                if (!self.multiSelectMode) {
                    self.gridHeight = 400;
                } else {
                    self.gridHeight = 400;
                }
                self.selectAbleCodeList = ko.observableArray(<Array<string>>nts.uk.ui.windows.getShared('kml001selectAbleCodeList'));
                self.selectAbleCodeListBk(_.cloneDeep(self.selectAbleCodeList()));

                self.selectedCodeList = ko.observableArray(<Array<string>>nts.uk.ui.windows.getShared('kml001selectedCodeList'));
                self.selectedCodeListBk(_.cloneDeep(self.selectedCodeList()));

                self.selectedCode = ko.observable(null);
                self.selectedCodeBk = ko.observable(null);
                self.searchOption = ko.observable(0);
                self.startTimeOption = ko.observable(1);
                self.startTime = ko.observable('');
                self.endTimeOption = ko.observable(1);
                self.endTime = ko.observable('');
                self.selectAbleItemList = ko.observableArray([]);
                self.initialWorkTimeCodes = ko.observableArray([]);
                self.workingHoursItemLists = ko.observableArray([]);//ver8
                //ver 8
                self.workPlaceId(nts.uk.ui.windows.getShared('kml001WorkPlaceId'));
                self.baseDate(nts.uk.ui.windows.getShared('kml001BaseDate'));

                self.isAllCheckShow.subscribe((isCheck) => {

                    self.startTime(null);
                    self.endTime(null);
                    self.searchCode('');

                    if (isCheck)
                        self.selectAbleItemList(_.cloneDeep(self.workingHoursItemLists()));
                    else
                        self.selectAbleItemList(_.cloneDeep(self.selectableWorkingHours()));
                });
            }

            startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                nts.uk.ui.block.grayout();

                const params = {
                    "baseDate": self.baseDate(),
                    "codes": self.selectAbleCodeList(),
                    "workPlaceId": self.workPlaceId()
                };

                kdl001.a.service.findByCodeList(params) //old version: self.selectAbleCodeList()
                    .done(function (data) {
                        //1 -> not show
                        //0 -> show
                        let isAllShow = data.allCheckStatus === 0 ? true : false;
                        self.isEnableCheckStatus(isAllShow); //allCheckStatus     

                        if (data.useATR === 1) {
                            self.columns([
                                { headerText: nts.uk.resource.getText('KDL001_12'), prop: 'code', width: 50 },
                                { headerText: nts.uk.resource.getText('KDL001_13'), prop: 'name', width: 130 },
                                { headerText: nts.uk.resource.getText('KDL001_14'), prop: 'workTime1', width: 180 },
                                { headerText: nts.uk.resource.getText('KDL001_15'), prop: 'workTime2', width: 180 }, //tam thoi comment theo yeu cau cua oohashi san
                                { headerText: nts.uk.resource.getText('KDL001_16'), prop: 'workAtr', width: 80 },
                                { headerText: nts.uk.resource.getText('KDL001_17'), prop: 'remark', template: '<span class="limited-label">${remark}</span>' }
                            ]);
                        } else {
                            self.columns([
                                { headerText: nts.uk.resource.getText('KDL001_12'), prop: 'code', width: 50 },
                                { headerText: nts.uk.resource.getText('KDL001_13'), prop: 'name', width: 130 },
                                { headerText: nts.uk.resource.getText('KDL001_14'), prop: 'workTime1', width: 180 },
                                //{ headerText: nts.uk.resource.getText('KDL001_15'), prop: 'workTime2', width: 200 }, //tam thoi comment theo yeu cau cua oohashi san
                                { headerText: nts.uk.resource.getText('KDL001_16'), prop: 'workAtr', width: 80 },
                                { headerText: nts.uk.resource.getText('KDL001_17'), prop: 'remark', template: '<span class="limited-label">${remark}</span>' }
                            ]);
                        }

                        //data = _.sortBy(data, (item: any) => { return item.code; });
                        self.bindItemList(data);
                        if (!nts.uk.util.isNullOrEmpty(self.selectAbleItemList())) {
                            if (nts.uk.util.isNullOrEmpty(self.selectedCodeList())) {
                                self.selectedCodeList([_.first(self.selectAbleItemList()).code]);
                                self.selectedCode(_.first(self.selectAbleItemList()).code);
                                self.selectedCodeBk(_.first(self.selectAbleItemList()).code);
                            } else {
                                let valueSelect = _.find(self.selectAbleItemList(), data => {
                                    return data.code == _.first(self.selectedCodeList());
                                });

                                let selectedCode = valueSelect != undefined ? _.first(self.selectedCodeList()) : ""
                                self.selectedCode(selectedCode);
                                self.selectedCodeBk(selectedCode);
                            }
                        } else {
                            self.selectedCodeList([]);
                            self.selectedCodeListBk([]);
                            self.selectedCode(null);
                            self.selectedCodeBk(null);
                        }
                        self.getKeywords(data.filterConditions);
                        nts.uk.ui.block.clear();
                        dfd.resolve();
                    })
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId }).then(function () { nts.uk.ui.block.clear(); });
                    });
                self.initPopup();
                return dfd.promise();
            }

            bindItemList(data) {
                let self = this,
                    selectAbleItemList: Array<WorkTimeSet> = [];
                //let isCheckAllStatus = self.selectAbleCodeList().length > 0 ? false : data.allCheckStatus;
                let isChecked = _.isNil(data.allCheckStatus) ? true : data.allCheckStatus;
                self.isCheckAllStatus(isChecked);
                self.isAllCheckShow(isChecked);

                self.workingHoursItemLists.removeAll();
                self.workingHoursItemLists.push(new WorkTimeSet());

                self.allWorkHours = data.allWorkHours;

                if (self.selectAbleCodeList().length > 0) { //check all                    
                    selectAbleItemList = data.availableWorkingHours;//data.availableWorkingHours.length > 0 ? data.availableWorkingHours : data.allWorkHours;
                } else if (!nts.uk.util.isNullOrEmpty(self.workPlaceId()) && !nts.uk.util.isNullOrEmpty(self.baseDate())) {
                    selectAbleItemList = data.workingHoursByWorkplace;//data.workingHoursByWorkplace.length > 0 ? data.workingHoursByWorkplace : data.allWorkHours;
                } else {
                    selectAbleItemList = data.allWorkHours;
                }

                //to restore
                if (!nts.uk.util.isNullOrEmpty(data)) {
                    self.workingHoursItemLists(self.workingHoursItemLists().concat(_.map(data.allWorkHours, item => { return new WorkTimeSet(item) })));
                    self.workingHoursItemLists(_.orderBy(self.workingHoursItemLists(), 'code', 'asc'));
                    //send from parent screen
                    let selectableItemList = (self.selectAbleCodeList().length > 0) ? data.availableWorkingHours : data.workingHoursByWorkplace;
                    self.selectableWorkingHours.removeAll();
                    self.selectableWorkingHours.push(new WorkTimeSet());
                    self.selectableWorkingHours(self.selectableWorkingHours().concat(_.map(selectableItemList, item => { return new WorkTimeSet(item) })));
                    self.selectableWorkingHours(_.orderBy(self.selectableWorkingHours(), 'code', 'asc'));
                }

                self.selectAbleItemList.removeAll();
                if (self.showNoSelectionRow) {
                    selectAbleItemList.unshift(new WorkTimeSet());
                }
                self.selectAbleItemList(_.orderBy(selectAbleItemList, 'code', 'asc'));
                // Set initial work time list.
                self.initialWorkTimeCodes = _.map(self.selectAbleItemList(), function (item) { return item.code })
            }

            //Old version
            search() {
                nts.uk.ui.block.invisible();
                var self = this;
                if (nts.uk.util.isNullOrEmpty(self.startTime()) && nts.uk.util.isNullOrEmpty(self.endTime())) {
                    nts.uk.ui.dialog.alertError({ messageId: 'Msg_307' }).then(() => {
                        //$('#inputStartTime').focus();
                        nts.uk.ui.block.clear();
                    });
                    return;
                }

                if ($('#inputEndTime').ntsError('hasError') ||
                    $('#inputStartTime').ntsError('hasError')) {
                    return;
                }

                let command = {
                    codelist: self.initialWorkTimeCodes,
                    startTime: nts.uk.util.isNullOrEmpty(self.startTime()) ? null : self.startTime(),
                    endTime: nts.uk.util.isNullOrEmpty(self.endTime()) ? null : self.endTime()
                }
                kdl001.a.service.findByTime(command)
                    .done(function (data) {
                        data = _.sortBy(data, (item: any) => { return item.code; });
                        self.selectAbleItemList(data);
                        if (!nts.uk.util.isNullOrEmpty(self.selectAbleItemList())) {
                            self.selectedCodeList([_.first(self.selectAbleItemList()).code]);
                            self.selectedCode(_.first(self.selectAbleItemList()).code);
                        } else {
                            self.selectedCodeList([]);
                            self.selectedCode(null);
                        }
                        nts.uk.ui.block.clear();
                    })
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId }).then(function () { nts.uk.ui.block.clear(); });
                    });
            }

            returnData() {
                nts.uk.ui.block.invisible();
                var self = this;
                self.startTimeOption(1);
                self.startTime('');
                self.endTimeOption(1);
                self.endTime('');
                kdl001.a.service.findByCodeList(self.selectAbleCodeList())
                    .done(function (data) {
                        data = _.sortBy(data, (item: any) => { return item.code; });
                        self.bindItemList(data);
                        if (!nts.uk.util.isNullOrEmpty(self.selectAbleItemList())) {
                            if (nts.uk.util.isNullOrEmpty(self.selectedCodeList())) {
                                self.selectedCodeList([_.first(self.selectAbleItemList()).code]);
                            }
                            if (nts.uk.util.isNullOrEmpty(self.selectedCode())) {
                                self.selectedCode(_.first(self.selectAbleItemList()).code);
                            }
                        } else {
                            self.selectedCodeList([]);
                            self.selectedCode(null);
                        }
                        self.getKeywords(data.filterConditions);
                        nts.uk.ui.block.clear();
                    })
                    .fail(function (res) {
                        nts.uk.ui.dialog.alertError({ messageId: res.messageId }).then(function () { nts.uk.ui.block.clear(); });
                    });
                $("#inputStartTime").focus();
            }

            submitAndCloseDialog() {
                nts.uk.ui.block.invisible();
                var self = this;
                if (!self.multiSelectMode)
                    self.selectedCodeList(nts.uk.util.isNullOrUndefined(self.selectedCode()) ? [] : [self.selectedCode()]);
                if (self.selectedCodeList().length == 0) {
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_29" }).then(function () {
                        nts.uk.ui.block.clear();
                        $("#day-list-tbl").igGrid("container").focus();
                    });
                } else {
                    nts.uk.ui.windows.setShared('kml001selectedCodeList', self.selectedCodeList());
                    nts.uk.ui.windows.setShared('kml001selectedTimes', self.getSelectedTimeItems(self.selectedCodeList()));
                    nts.uk.ui.block.clear();
                    nts.uk.ui.windows.setShared('KDL001_IsCancel', false);
                    nts.uk.ui.windows.close();
                }
            }

            getSelectedTimeItems(codes) {
                let self = this;
                var timeItems = _.filter(self.selectAbleItemList(), item => { return codes.indexOf(item.code) > -1 });
                var mappedItems = _.map(timeItems, item => { return new TimeItem(item) });
                return mappedItems;
            }

            closeDialog() {
                nts.uk.ui.windows.setShared('KDL001_IsCancel', true);
                nts.uk.ui.windows.close();
            }

            clearSearch() {
                const self = this;
                nts.uk.ui.block.invisible();
                self.resetAllData().then(() => {
                    nts.uk.ui.block.clear();
                });

            }

            resetAllData(): JQueryPromise<any> {
                const self = this;
                const dfd = $.Deferred();
                self.selectAbleItemList.removeAll();
                self.startTime(null);
                self.endTime(null);
                self.searchCode(null);
                self.isAllCheckShow(self.isCheckAllStatus());
                self.selectAbleItemList(_.cloneDeep(self.isCheckAllStatus() ? self.workingHoursItemLists() : self.selectableWorkingHours()));
                self.resetConditionSelected();
                $('#inputStartTime').focus();
                nts.uk.ui.errors.clearAll();
                dfd.resolve();
                return dfd.promise();
            }

            resetConditionSelected() {
                const self = this;
                self.selectedCode(self.selectedCodeBk());
                self.selectAbleCodeList(_.cloneDeep(self.selectAbleCodeListBk()));
                self.selectedCodeList(_.cloneDeep(self.selectedCodeListBk()));
            }

            addShowNone(data: Array<any>): Array<any> {
                const self = this;

                let findItem = _.some(data, (x) => x.code === '');
                if (findItem)
                    return data;
                else
                    data.unshift(new WorkTimeSet());
                return data;
            }

            // Add blank keywords button if needed
            private getKeywords(filterConditions: IWorkHoursFilterConditionDto[]) {
              const vm = this;
              const length = filterConditions.length;
              for (let i = length; i < KEYWORDS_MAX_COUNT; i++) {
                filterConditions.push({
                  no: i + 1,
                  notUseAtr: false,
                  name: ''
                });
              }
              const keywords = _.map(filterConditions, data => new WorkHoursFilterConditionDto({
                no: data.no,
                notUseAtr: ko.observable(data.notUseAtr),
                name: ko.observable(data.name)
              }));
              vm.defaultKeywords(filterConditions);
              vm.keywords(keywords);
            }

            public executeSearchByKeyword(data: IWorkHoursFilterConditionDto) {
              const vm = this;
              (nts.uk.ui as any).block.grayout();
              const param = {
                keyword: data.name,
                workTimes: vm.allWorkHours
              }
              service.searchByKeyword(param).then(result => vm.selectAbleItemList(result))
              .fail(err => (nts.uk.ui.dialog as any).alertError({ messageId: err.messageId }))
              .always(() => (nts.uk.ui as any).block.clear());
            }

            public executeSearchByQuery() {
              const vm = this;
              if (nts.uk.ui.errors.hasError()) {
                return;
              }
              (nts.uk.ui as any).block.grayout();
              const param = {
                startTime: vm.startTime(),
                endTime: vm.endTime(),
                searchQuery: vm.searchQuery(),
                workTimes: vm.allWorkHours
              }
              service.searchByDetails(param).then(result => vm.selectAbleItemList(result))
              .fail(err => (nts.uk.ui.dialog as any).alertError({ messageId: err.messageId }))
              .always(() => (nts.uk.ui as any).block.clear());
            }

            public executeSaveKeywords() {
              const vm = this;
              (nts.uk.ui as any).block.grayout();
              const param = {
                conditions: ko.toJS(vm.keywords)
              };
              service.registerKeyword(param).then(() => nts.uk.ui.dialog.info({ messageId: "Msg_15" })
                .then(() => {
                  vm.defaultKeywords(_.cloneDeep(ko.toJS(vm.keywords)));
                  $("#A6_1").ntsPopup("hide");
                }))
              .fail(err => (nts.uk.ui.dialog as any).alertError({ messageId: err.messageId }))
              .always(() => (nts.uk.ui as any).block.clear());
            }

            private initPopup() {
              $("#A6_1").ntsPopup({
                trigger: "#A2_23",
                position: {
                  my: "left top",
                  at: "left bottom",
                  of: "#A2_23"
                },
                showOnStart: false,
                dismissible: true
              });
            }
        }

        export class TimeItem {
            selectedWorkTimeCode: string;
            selectedWorkTimeName: string;
            first: Time;
            second: Time;
            remark: string;
            constructor(data?) {
                if (data) {
                    this.selectedWorkTimeCode = data.code;
                    this.selectedWorkTimeName = data.name;
                    this.first = new Time(data.firstStartTime, data.firstEndTime);
                    this.second = new Time(data.secondStartTime, data.secondEndTime);
                    this.remark = data.remark;
                }
            }
        }
        export class Time {
            start: number;
            end: number;
            constructor(startTime, endTime) {
                this.start = startTime;
                this.end = endTime;
            }
        }

        export class WorkTimeSet {
            code: string = "";
            name: string = "選択なし";
            workTime1: string = "";
            workTime2: string = "";
            workAtr: string = "";
            remark: string = "";
            firstStartTime: number = null;
            firstEndTime: number = null;
            secondStartTime: number = null;
            secondEndTime: number = null;
            constructor(data?) {
                if (data) {
                    this.code = data.code;
                    this.name = data.name;
                    this.workTime1 = data.workTime1;
                    this.workTime2 = data.workTime2;
                    this.workAtr = data.workAtr;
                    this.remark = data.remark;
                    this.firstStartTime = data.firstStartTime;
                    this.firstEndTime = data.firstEndTime;
                    this.secondStartTime = data.secondStartTime;
                    this.secondEndTime = data.secondEndTime;
                }
            }

        }

        interface IWorkTimeSet {
            code: string;
            name: string;
            workTime1: string;
            //workTime2: string;
            workAtr: string;
            remark: string;
        }

        export class WorkHoursFilterConditionDto {
          // NO
          no: number;
          // 使用区分
          notUseAtr: KnockoutObservable<boolean>;
          // 名称
          name: KnockoutObservable<string>;

          constructor(init?: Partial<WorkHoursFilterConditionDto>) {
            $.extend(this, init)
          }
        }

        export interface IWorkHoursFilterConditionDto {
          // NO
          no: number;
          // 使用区分
          notUseAtr: boolean;
          // 名称
          name: string;
        }
    }
}