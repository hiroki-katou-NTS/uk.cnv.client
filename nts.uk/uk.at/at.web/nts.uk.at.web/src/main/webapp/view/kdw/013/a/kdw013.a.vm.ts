module nts.uk.ui.at.kdw013.a {
    /**
     * 日別勤怠の編集状態
     */
    enum EditStateSetting {
        /** 手修正（本人） */
        HAND_CORRECTION_MYSELF = 0,
        /** 手修正（他人） */
        HAND_CORRECTION_OTHER = 1,
        /** 申請反映 */
        REFLECT_APPLICATION = 2,
        /** 打刻反映 */
        IMPRINT = 3,
        /** 申告反映 */
        DECLARE_APPLICATION = 4

    };
    let BREAKTIME_COLOR = '#ff99ff';
    let { formatTime, setTimeOfDate, getTimeOfDate, getTask, getBackground, getTitles } = share;
    let { randomId } = nts.uk.util;

    let DATE_FORMAT = 'YYYY-MM-DD';
    let DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:00.000\\Z';
	let TIME_FORMAT = 'HH:mm';

    let API: API = {
        ADD: '/screen/at/kdw013/a/add_confirm',
        // DeleteWorkResultConfirmationCommand
        DELETE: '/screen/at/kdw013/a/delete_confirm',
        // start page query params
        START: '/screen/at/kdw013/a/start',
        // 対象社員を選択する
        // 日付を変更する
        // Chọn nhân viên ở A2_4
        // Chọn ngày ở [画面イメージ]A6_1/[固有部品]A1_1
        CHANGE_DATE: '/screen/at/kdw013/a/changeDate',
        // RegisterWorkContentCommand
        REGISTER: '/screen/at/kdw013/a/register_work_content',

        // POPUP F
        // 作業お気に入り登録を起動する
        START_F: '/screen/at/kdw013/f/start_task_fav_register',
        
        GET_FAV_TASK: '/screen/at/kdw013/a/get-fav-task',
        // 作業お気に入りを新規追加する
        ADD_FAV_TASK_F: '/screen/at/kdw013/f/create_task_fav',
        // 作業お気に入り名称を変更する
        UPDATE_TASK_NAME_F: '/screen/at/kdw013/f/update_task_name',
        GET_FAV_ONE_DAY: '/screen/at/kdw013/a/get-fav-one-day',
        // POPUP G
        // 1日作業お気に入り登録を起動する
        START_G: '/screen/at/kdw013/g/start_task_fav_register',
        // 1日作業お気に入りを新規追加する
        ADD_FAV_TASK_G: '/screen/at/kdw013/g/create_task_fav',
        // 1日作業お気に入り名称を変更する
        UPDATE_TASK_NAME_G: '/screen/at/kdw013/g/update_task_name'
    };

    let initialCache = (): ChangeDateParam => ({
        displayPeriod: {
            end: '',
            start: ''
        },
        employeeId: '',
        refDate: ''
    })
    
    export class StartProcess {
        attItemName: Array<any>;
        dailyAttendanceItem: Array<any>;
        divergenceReasonInputMethods: Array<any>;
        divergenceTimeRoots: Array<any>;
        employeeInfos: Array<any>;
        favTaskDisplayOrders: any
        favTaskItems: Array<any>;
        lstEmployeeInfo: Array<any>;
        manHrInputDisplayFormat: any
        oneDayFavSets: Array<any>;
        oneDayFavTaskDisplayOrders: any;
        taskFrameUsageSetting: any;
        tasks: Array<any>;
        workTimeSettings: Array<any>;
        workTypes: Array<any>;
        workplaceInfos: Array<any>;

        constructor(data) {
            this.attItemName = data.attItemName;
            this.dailyAttendanceItem = data.dailyAttendanceItem;
            this.divergenceReasonInputMethods = data.divergenceReasonInputMethods;
            this.divergenceTimeRoots = data.divergenceTimeRoots;
            this.employeeInfos = data.employeeInfos;
            this.favTaskDisplayOrders = data.favTaskDisplayOrders;
            this.favTaskItems = data.favTaskItems;
            this.lstEmployeeInfo = data.lstEmployeeInfo;
            this.manHrInputDisplayFormat = data.manHrInputDisplayFormat;
            this.oneDayFavSets = data.oneDayFavSets;
            this.oneDayFavTaskDisplayOrders = data.oneDayFavTaskDisplayOrders;
            this.taskFrameUsageSetting = data.taskFrameUsageSetting;
            this.tasks = data.tasks;
            this.workTimeSettings = data.workTimeSettings;
            this.workTypes = data.workTypes;
            this.workplaceInfos = data.workplaceInfos;
        }

        updateFavTask(data) {
            this.favTaskDisplayOrders = data.favTaskDisplayOrders;
            this.favTaskItems = data.favTaskItems;
        }
        updateFavOneday(data) {
            this.oneDayFavSets = data.oneDayFavSets;
            this.oneDayFavTaskDisplayOrders = data.oneDayFavTaskDisplayOrders;
        }
    }

    @handler({
        bindingName: 'kdw-toggle',
        validatable: true,
        virtual: false
    })
    export class KDWToggleBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLButtonElement, valueAccessor: () => KnockoutComputed<boolean>) {
            let toggler = valueAccessor();
            let visible = ko.computed({
                read: () => {
                    let visible = ko.unwrap(toggler);

                    return visible;
                },
                disposeWhenNodeIsRemoved: element
            });
            let disable = ko.computed({
                read: () => {
                    let enable = ko.unwrap(toggler);

                    return !enable;
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.applyBindingsToNode(element, { visible, disable });

            element.removeAttribute('data-bind');
        }
    }

    @bean()
    export class ViewModel extends ko.ViewModel {
        $toggle!: {
            save: KnockoutComputed<boolean>;
            remove: KnockoutComputed<boolean>;
            confirm: KnockoutComputed<boolean>;
        };

        events: KnockoutObservableArray<calendar.EventApi> = ko.observableArray([]);

        businessHours= ko.observableArray([]);

        weekends: KnockoutObservable<boolean> = ko.observable(true);
        editable: KnockoutObservable<boolean> = ko.observable(true);
        firstDay: KnockoutObservable<number> = ko.observable(1);
        scrollTime: KnockoutObservable<number> = ko.observable(420);
        slotDuration: KnockoutObservable<number> = ko.observable(30);
        initialDate: KnockoutObservable<Date> = ko.observable(new Date());
        isShowBreakTime: KnockoutObservable<boolean> = ko.observable(false);
        dateRange: KnockoutObservable<Partial<calendar.DatesSet>> = ko.observable({});
        initialView: KnockoutObservable<string> = ko.observable('fullWeek');
        availableView: KnockoutObservableArray<calendar.InitialView> = ko.observableArray(['oneDay', 'fullWeek']);
        validRange: KnockoutObservable<Partial<calendar.DatesSet>> = ko.observable({end: '9999-12-32'});
        removeList: KnockoutObservableArray<any> = ko.observableArray([]);
        removeBreakList: KnockoutObservableArray<any> = ko.observableArray([]);
        //biến này để phục vụ việc lấy data khi thay đổi ở màn K  
        inputDate: KnockoutObservable<Date> = ko.observable();
    
        employee: KnockoutObservable<string> = ko.observable('');

        confirmers!: KnockoutComputed<calendar.Employee[]>;
        // need map with [KDW013_21, KDW013_22, KDW013_23, KDW013_24] resource
        attendanceTimes!: KnockoutComputed<calendar.AttendanceTime[]>;

        // Change date range data model
        $datas: KnockoutObservable<ChangeDateDto | null> = ko.observable(null);

        // settings (first load data)
        $settings: KnockoutObservable<StartProcess | null> = ko.observable(null);
    
        dataChanged: KnockoutObservable<boolean> = ko.observable(false);
        favTaskName: KnockoutObservable<string> = ko.observable('');
        oneDayFavTaskName: KnockoutObservable<string> = ko.observable('');

        // F画面を起動する
        favoriteTaskItem: KnockoutObservable<FavoriteTaskItemDto | null> = ko.observable(null);

        // F画面: add new 
        taskContents: KnockoutObservableArray<TaskContentDto> = ko.observableArray();

        // G画面を起動する
        oneDayFavoriteSet: KnockoutObservable<OneDayFavoriteSetDto | null> = ko.observable(null);

        // G画面: add new 
        taskBlocks: KnockoutObservableArray<TaskBlockDetailContentDto> = ko.observableArray();

        // 作業枠利用設定
        taskSettings: KnockoutObservableArray<a.TaskFrameSettingDto> = ko.observableArray();
        
        // 作業リスト
        taskDtos: KnockoutObservableArray<TaskDto> =  ko.observableArray();

        // 日別勤怠の応援作業時間
        ouenWorkTimes: KnockoutObservableArray<OuenWorkTimeOfDailyAttendance> =  ko.observableArray();

        // 日別勤怠の応援作業時間帯
        ouenWorkTimeSheets: KnockoutObservableArray<OuenWorkTimeSheetOfDailyAttendance> =  ko.observableArray();

        //対象日
        targetDate: KnockoutObservable<Date> =  ko.observable();
    
        fullCalendar : KnockoutObservable<FullCalendarComponent> = ko.observable();

		popupTitle: KnockoutObservable<String> = ko.observable('');
		btnContent: KnockoutObservable<String> = ko.observable('');
		
		reloadFlag: KnockoutObservable<Boolean> =  ko.observable(false);

        constructor() {
            super();
            let vm = this;
            let $query = vm.getQuery();
            let { employee } = vm;
            let { mode } = $query;
            let cache: ChangeDateParam & { pair: -1 | 0 | 1 | 2 } = { ...initialCache(), pair: 0 };
            let sameCache = (params: ChangeDateParam): -1 | 0 | 1 | 2 => {
            
                if (cache.refDate !== params.refDate) {
                    if (cache.displayPeriod.end === params.displayPeriod.end) {
                        if (cache.displayPeriod.start === params.displayPeriod.start) {
                            return -1;
                        }
                    }
                }

                if (cache.employeeId !== params.employeeId) {
                    if (params.displayPeriod.start && params.displayPeriod.end) {
                        return 0;
                    }
                }

                if (cache.displayPeriod.end === params.displayPeriod.end) {
                    if (cache.displayPeriod.start === params.displayPeriod.start) {
                        if (cache.refDate === params.refDate) {
                            return 2;
                        }

                        return 1;
                    }
                }

                return 0;
            };
            let updateCache = (params: ChangeDateParam) => {
                cache.displayPeriod.end = params.displayPeriod.end;
                cache.displayPeriod.start = params.displayPeriod.start;

                cache.refDate = params.refDate;
            };
            
    
            vm.businessHours = ko.computed({
                read: () => {
                    let datas = ko.unwrap(vm.$datas);

                    if (datas) {

                        let { estimateZones } = datas;

                        return _
                            .chain(estimateZones)
                            .filter(({startTime, endTime}) => { return !!startTime && !!endTime })
                            .map(({startTime, endTime, ymd}) => {
                                return {
                                    dayOfWeek: moment(ymd).toDate().getDay(),
                                    start: startTime,
                                    end: endTime
                                };
                            }).value();

                    }

                    return [];
                }
            });
            

          

            vm.$toggle = {
                save: ko.computed({
                    read: () => {
                        let event = ko.unwrap(vm.events);
                        
                        if (!vm.dataChanged()) {
                            return false;
                        }

                        return true;
                    }
                }),
                remove: ko.computed({
                    read: () => {
                        
                        let confirms = _.get(vm.$datas(),'lstComfirmerDto');
                        let editable = ko.unwrap(vm.editable);
                        let confimer = _.find(confirms, ['confirmSID',vm.$user.employeeId]);
                        return !editable && !!confimer;
                    }
                }),
                confirm: ko.computed({
                    read: () => {
                        let confirms = _.get(vm.$datas(),'lstComfirmerDto');
                        if (!_.isEmpty(confirms) && confirms.length >= 5) {
                            return false;
                        }
                        if (_.find(confirms, { confirmSID: vm.$user.employeeId })) {
                            return false;
                        }
                        let editable = ko.unwrap(vm.editable);
                        let confimer = _.find(_.get(vm.$datas(),'lstComfirmerDto'), ['confirmSID',vm.$user.employeeId]);
                        return !editable && !confimer;
                    }
                }),
            };

            if (mode) {
                // URLの値元に画面モードを判定する
                vm.editable(mode === '0');
            }
            
            vm.inputDate.subscribe((date) => {
                vm.reLoad();
            });

            ko.computed({
                read: () => {
                    let employeeId = ko.unwrap(vm.editable) === false ? ko.unwrap(vm.employee) : vm.$user.employeeId;
                    let date = ko.unwrap(vm.initialDate);
                    let { start, end } = ko.unwrap(vm.dateRange);
                    let setting = ko.unwrap(vm.$settings);

                    if (!employeeId || !setting) {
                        return;
                    }
                    
                    let itemIds = _.map(_.get(setting, 'manHrInputDisplayFormat.displayManHrRecordItems', []), item => { return item.itemId });

                    if (!!start && !!end && moment(date).isBetween(start, end, undefined, '[)')) {
                        let params: ChangeDateParam = {
                            employeeId,
                            refDate: moment(date).startOf('day').format(DATE_TIME_FORMAT),
                            displayPeriod: {
                                start: moment(start).startOf('day').format(DATE_TIME_FORMAT),
                                end: moment(end).subtract(1, 'day').startOf('day').format(DATE_TIME_FORMAT)
                            },
                            itemIds
                        };
                        cache.pair = sameCache(params);

                        if (cache.pair !== 2) {
                            updateCache(params);
                        }

                        if (cache.pair <= 0) {
                            vm.$datas(null);
                            
                            vm
                                .$blockui('grayout')
                                .then(() => vm.$ajax('at', API.CHANGE_DATE, params))
                                .then((data: ChangeDateDto) => {
                                    vm.$datas(data);
                                    vm.dataChanged(false);
                                    vm.removeList([]);
                                    vm.removeBreakList([]);
                                })
                                .always(() => vm.$blockui('clear'));
                        }
                    }
                }
            }).extend({ rateLimit: 250 });

            vm.confirmers = ko.computed({
                read: () => {
                    let datas = ko.unwrap(vm.$datas);
                    let $date = ko.unwrap(vm.initialDate);
                    let $moment = moment($date).format(DATE_FORMAT);

                    if (datas) {
                        let { lstComfirmerDto } = datas;

                        return _
                            .chain(lstComfirmerDto)
                            .map(({
                                confirmSID: id,
                                confirmSCD: code,
                                businessName: name
                            }) => ({
                                id,
                                code,
                                name,
                                selected: false
                            }))
                            .value();
                    }

                    return [] as calendar.Employee[];
                }
            }).extend({ rateLimit: 500 });
    
            vm.breakTime = ko.computed({
                read: () => {
                    let datas = ko.unwrap(vm.$datas);

                    if (datas) {

                        let { estimateZones } = datas;

                        return _
                            .chain(estimateZones)
                            .filter(({breakTimeSheets}) => { return !!breakTimeSheets.length })
                            .map(({breakTimeSheets, ymd}) => {
                                return {
                                    dayOfWeek: moment(ymd).toDate().getDay(),
                                    breakTimes: _.map(breakTimeSheets, ({start, end}) => { return { start, end }; })
                                };
                            }).value();

                    }

                    return [];
                }
            });

            vm.attendanceTimes = ko.computed({
                read: () => {
                    let datas = ko.unwrap(vm.$datas);
                    let setting = ko.unwrap(vm.$settings);
                    
                    let employee = ko.unwrap(vm.employee);
                    let { start,end } = ko.unwrap(vm.dateRange);
                    let dateRanges = () => {
                        let dates: Date[] = [];
                        let begin = moment(start);

                        while (begin.isBefore(end, 'day')) {
                            dates.push(begin.toDate());

                            begin.add(1, 'day');
                        }

                        return dates;
                    };

                    // need update by employId if: mode=1
                    let employeeId = employee || vm.$user.employeeId;

                    if (datas) {
                        
                        
                        
                      return   _.chain(dateRanges())
                            .map(date => {
                                let events: string[] = [];
                                let convert = _.find(_.get(datas, 'convertRes', []), cvr => { return moment(cvr.ymd).isSame(moment(date), 'days'); } )
                                let manHrContents = _.get(convert, 'manHrContents', []);
                                let attItemName = _.get(setting, 'attItemName', []);
                                let workTypes = _.get(setting, 'workTypes');
                                
                                let wkTypeCd = _.get(_.find(manHrContents, hr => { return hr.itemId == 28 }), 'value');
                                    
                                if (wkTypeCd) {
                                    
                                    let name = _.get(_.find(workTypes, wt => { return wt.workTypeCode == wkTypeCd }), 'name');
                                    //PC3_2 PC3_3
                                    events.push({ title: vm.$i18n('KDW013_67'), text: wkTypeCd + ' ' + (name ? name : vm.$i18n('KDW013_40')) , valueType: 0});
                                }

                                let start = _.get(_.find(manHrContents, hr => { return hr.itemId == 31 }), 'value');
                                let end = _.get(_.find(manHrContents, hr => { return hr.itemId == 34 }), 'value');

                                if (convert) {
                                    //PC3_4 PC3_5
                                    events.push({ title: vm.$i18n('KDW013_68'), text: vm.$i18n('KDW013_73', [start ? formatTime(start, 'Time_Short_HM') : '　　', end ? formatTime(end, 'Time_Short_HM') : '']), valueType: 0 });
                                }

                                let rdis = _.sortBy(_.get(setting, 'manHrInputDisplayFormat.recordColumnDisplayItems', []), ['order']);
                                let genControl = (hr, attItem) => {
                                    if (!_.isNaN(Number(hr.value)) && hr.valueType == 1) {
                                        return { text: (formatTime(hr.value, 'Time_Short_HM')), type: 0 };
                                    }
                                    //check box
                                    if (_.get(attItem, 'masterType') == 9 && _.get(attItem, 'dailyAttendanceAtr') == 2 && hr.value == 1) {
                                        return { text: '', type: 3 };
                                    }
                                    if (_.get(attItem, 'masterType') == 9 && _.get(attItem, 'dailyAttendanceAtr') == 2) {
                                        return { text: '', type: 2 };
                                    }

                                    return { text: hr.value, type: 0 };

                                }
                                _.forEach(rdis, rdi => {

                                    let hr = _.find(manHrContents, hr => { return hr.itemId == rdi.attendanceItemId });
                                    let attItem = _.find(_.get(setting, 'dailyAttendanceItem', []), ati => ati.attendanceItemId == rdi.attendanceItemId);
                                    //PC3_6 PC3_7 ☐ ☑
                                    if (!_.isNil(_.get(hr, 'value'))) {
                                        let control  = genControl(hr,attItem);
                                        events.push({ title: rdi.displayName, text: control.text, valueType: control.type });
                                    }

                                });

                                return { date, events };
                            })
                            .value();
                        
                         
                    }
                  
                    return [] as calendar.AttendanceTime[];
                   
                    
                }
            }).extend({ rateLimit: 500 });
            let inputDate = ko.unwrap(vm.inputDate);
            // get settings Msg_1960
            vm
                .$blockui('grayout')
                .then(() => {
                    vm.$window
                    .storage('KDW013_SETTING')
                    .then((value: any) => {
                        if (value) {
                            vm.initialView(value.initialView || 'fullWeek');
                            vm.firstDay(value.firstDay !== undefined ? value.firstDay : 1);
                            vm.scrollTime(value.scrollTime || 420);
                            vm.slotDuration(value.slotDuration || 30);
                        }


                    });
                })
                .then(() => vm.$ajax('at', API.START, { inputDate }))
                .fail((error) => {
                    vm.$dialog.error({ messageId: error.messageId }).then(() => {
                        let errors = ["Msg_2122", "Msg_2253", "Msg_2243", "Msg_1960", "Msg_1961"];
                        if (errors.indexOf(error.messageId) != -1) {
                            nts.uk.request.jumpToTopPage();
                        }
                    });
                })
                .then((response: StartProcessDto) => {

                    
                    vm.$settings(new StartProcess(response));
                })
                .always(() => vm.$blockui('clear'));

            // Init popup
            $(".popup-area-f").ntsPopup({
                trigger: ".popupButton-f",
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".popupButton-f"
                },
                showOnStart: false,
 				dismissible: false
            });

            $(".popup-area-g").ntsPopup({
                trigger: ".popupButton-g",
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".popupButton-g"
                },
                showOnStart: false,
				dismissible: false
            });

            $(".popup-area-i").ntsPopup({
                trigger: ".popupButton-i",
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".popupButton-i"
                },
                showOnStart: false,
                dismissible: true
            });
    
            vm.$datas.subscribe((datas) => { computedEvents(datas, ko.unwrap(vm.$settings)); });

            vm.events.subscribe((datas) => { vm.dataChanged(true); });

            vm.$settings.subscribe((settings) => computedEvents(ko.unwrap(vm.$datas), settings));

            let computedEvents = (data: SelectTargetEmployeeDto | null, settings: StartProcessDto | null) => {
//                if (cache.pair === -1) {
//                    return;
//                }
                let { tasks, taskFrameUsageSetting } = settings;
                if (data) {
                    let events = [];

                    let {lstIntegrationOfDaily} = data;

                    _.forEach(lstIntegrationOfDaily, ld => {

                        let frameNos = _.map(ld.ouenTimeSheet, ot => ot.workNo);

                        let hrTask = _.find(_.get(data, 'dailyManHrTasks', []), dt => moment(dt.date).isSame(moment(ld.ymd), 'days'));

                        let {manHrContents} = _.find(_.get(data, 'convertRes'), cr => moment(cr.ymd).isSame(moment(ld.ymd), 'days'));
                        if (ko.unwrap(vm.isShowBreakTime)) {
                            _.forEach(_.get(ld, 'breakTime.breakTimeSheets', []), bt => {

                                let businessHours = ko.unwrap(vm.businessHours);
                                let start = setTimeOfDate(moment(ld.ymd).toDate(), bt.start);
                                let end = setTimeOfDate(moment(ld.ymd).toDate(), bt.end);

                                let bh = _.find(businessHours, bh => bh.dayOfWeek == start.getDay());
                                let startAsMinites = (moment(start).hour() * 60) + moment(start).minute();
                                let endAsMinites = (moment(end).hour() * 60) + moment(end).minute();

                                if (startAsMinites >= _.get(bh, 'start', 0) && endAsMinites <= _.get(bh, 'end', 1440)) {
                                    events.push(
                                        {
                                            start,
                                            end,
                                            title: vm.$i18n('KDW013_79'),
                                            backgroundColor: BREAKTIME_COLOR,
                                            textColor: '',
                                            extendedProps: {
                                                no: bt.no,
                                                breakTime: bt.breakTime,
                                                id: randomId(),
                                                status: 'normal' as any,
                                                isTimeBreak: true,
                                                isChanged: false,
                                                taskBlock: {
                                                    manHrContents,
                                                    taskDetails: []
                                                }
                                            } as any
                                        }

                                    );
                                }
                            });
                        }

                        _.forEach(_.get(hrTask, 'taskBlocks', []), tb => {
                            let {taskDetails, caltimeSpan} = tb;
                            let ts = _.find(_.get(ld, 'ouenTimeSheet', []), ot => ot.workNo == _.get(taskDetails[0], 'supNo', null));
                            let {start, end} = caltimeSpan;
                            let work = _.get(ts, 'workContent.work');


                            events.push({
                                taskFrameUsageSetting: ko.unwrap((vm.$settings)),
                                period: { start, end },
                                displayManHrRecordItems: _.get(ko.unwrap((vm.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []),
                                employeeId: vm.employee() || vm.$user.employeeId,
                                start: setTimeOfDate(moment(ld.ymd).toDate(), start),
                                end: setTimeOfDate(moment(ld.ymd).toDate(), end),
                                title: work ? getTitles(taskDetails, tasks, taskFrameUsageSetting) : '',
                                backgroundColor: work ? getBackground(work, tasks) : '',
                                textColor: '',
                                extendedProps: {
                                    frameNos,
                                    id: randomId(),
                                    isTimeBreak: false,
                                    isChanged: false,
                                    status: 'update' as any,
                                    taskBlock: {
                                        caltimeSpan: { start, end },
                                        taskDetails: taskDetails
                                    }
                                }
                            });
                        });
                    });

                    vm.events(events);
                    return;
                }

                vm.events([]);
            };
        }

        getTaskValues(){
            let vm = this;
                 let items = [];

                 _.forEach(_.get(ko.unwrap((vm.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []), function(item) {
                     items.push({ itemId: item.itemId, value: null });
                 });
    
                return items;
        }

        reLoad(){
            let vm = this;
            let inputDate = ko.unwrap(vm.inputDate);
            vm
                .$blockui('grayout')
                .then(() => vm.$ajax('at', API.START, { inputDate }))
                .fail(function(error) {
                    vm.$dialog.error({ messageId: error.messageId });
                })
                .then((response: StartProcessDto) => {
        
                    vm.$window
                        .storage('KDW013_SETTING')
                        .then((value: any) => {
                            if (value) {
                                vm.initialView(value.initialView || 'fullWeek');
                                vm.firstDay(value.firstDay !== undefined ? value.firstDay : 1);
                                vm.scrollTime(value.scrollTime || 420);
                                vm.slotDuration(value.slotDuration || 30);
                            }
                        });
        
        
                    vm.$settings(new StartProcess(response));
                })
                .always(() => vm.$blockui('clear'));
        }

        mounted() {
            let vm = this;

            _.extend(window, { vm });
        }

        getQuery(){
            let query = location.search.substring(1);
            if (!query || !query.match(/=/)) {
                return {};
            }
            return JSON.parse('{"' + decodeURI(query).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}');
    
        }

        equipmentInput(){
            let vm = this;
            vm.$jump.blank('com', '/view/oew/001/a/index.xhtml');
        }

        getChangedDates(dates){
    
            let vm = this;
            let $events = ko.unwrap(vm.events);
           return _.chain(dates).map(date => {
                let events = _.filter($events, (e) => { return moment(e.start).isSame(date, 'days') });
                let data = _.find(vm.$datas().dailyManHrTasks, (e) => { return moment(e.date).isSame(date, 'days') });

                if (events.length != _.size(_.get(data, 'taskBlocks'))) {
                    return { date: date, changed: true };
                }
                
                let isChanged = _.find(events, (e) => { return _.get(e, 'extendedProps.isChanged') });

                if (isChanged) {
                    return { date: date, changed: true };
                }

                return { date: date, changed: false };
            }).filter(d => { return d.changed }).map(d => moment(d.date).format(DATE_TIME_FORMAT)).value();
        }

        getManHrlst(dates){
    
            let result = [];
            let vm = this;
            _.forEach(dates, date => {
                let eventInday = _.filter(vm.events(), (e) => { return moment(e.start).isSame(date, 'day') });

                if (eventInday.length) {


                    let listTaskDetails = [];

                    let  manHrContents = _.get(_.find(_.get(vm.$datas(), 'convertRes'), cr => moment(cr.ymd).isSame(moment(date), 'days')), 'manHrContents', []);
                    
                    _.forEach(_.filter(eventInday, e => _.get(e, 'extendedProps.isTimeBreak', false) == false), e => {
                        let {taskDetails} = _.get(e, 'extendedProps.taskBlock');
                        _.forEach(taskDetails, td => {
                            
                            if (taskDetails.length == 1) {
                                _.remove(td.taskItemValues, ti => ti.itemId == 3);
                            }

                            _.forEach(td.taskItemValues, ti => {
                                let start = (moment(e.start).hour() * 60) + moment(e.start).minute();
                                let end = (moment(e.end).hour() * 60) + moment(e.end).minute();
                                if (ti.itemId == 1) { ti.value = taskDetails.length > 1 ? null : start };
                                if (ti.itemId == 2) { ti.value = taskDetails.length > 1 ? null : end };
                            });

                        });

                        listTaskDetails.push(...taskDetails);
                    });

                    result.push({ ymd: date, taskList: listTaskDetails, manHrContents });
                }
            });

            return result;
        }

        saveData() {
            let vm = this;
            let { events, dateRange } = vm;
            let { HAND_CORRECTION_MYSELF, HAND_CORRECTION_OTHER } = EditStateSetting;
            let { start, end } = ko.unwrap(dateRange);

            if (!start || !end) {
                return;
            }

            let $events = ko.unwrap(events);
            let dateRanges = () => {
                let dates: Date[] = [];
                let begin = moment(start);

                while (begin.isBefore(end, 'day')) {
                    dates.push(begin.toDate());

                    begin.add(1, 'day');
                }

                return dates;
            };
            let dates = vm.over20TaskDays(dateRanges());
            if (dates.length) {
                vm.$dialog
                    .error({ messageId: 'Msg_2262', messageParams: dates });
                return;
            }
    
            let deleteAttByTimeZones = vm.createDeleteAttByTimeZones(dateRanges());
    
            let itemIds = _.map(_.get(setting, 'manHrInputDisplayFormat.displayManHrRecordItems', []), item => { return item.itemId });
    
            let employeeId = vm.employee() ? vm.employee() : vm.$user.employeeId;
    
            let editStateSetting = !vm.employee() ? HAND_CORRECTION_MYSELF : vm.employee() == vm.$user.employeeId ? HAND_CORRECTION_MYSELF : HAND_CORRECTION_OTHER;
    
            let mode =  vm.editable() ? 0 : vm.employee() === vm.$user.employeeId ? 0 : 1;

            let changedDates = vm.getChangedDates(dateRanges());
    
            let workDetails = vm.createWorkDetails(changedDates);
    
            let manHrlst = vm.getManHrlst(dateRanges());

            let integrationOfDailys = vm.createIDaily(dateRanges());

            let command: nts.uk.ui.at.kdw013.RegisterWorkContentCommand = {
                deleteAttByTimeZones,
                changedDates,
                editStateSetting,
                employeeId,
                manHrlst,
                integrationOfDailys,
                mode,
                workDetails,
                itemIds
            };

            console.log(command);
            vm
                .$blockui('grayout')
                //作業を登録する
                .then(() => vm.$ajax('at', API.REGISTER, command))
                .then((response: RegisterWorkContentDto) => {
                    let { dataResult, lstOvertimeLeaveTime, alarmMsg_2081 } = response;
					if(dataResult.errorMap.message){
						if(_.includes(dataResult.errorMap.message, 'Msg_')){
							return vm.$dialog.error({ messageId: dataResult.errorMap[0].message });
						}else{
							return vm.$dialog.error(dataResult.errorMap.message);
						}						
					}else{
						
						let messageId = '';
						let messageParams = [];
						
						if (alarmMsg_2081 && alarmMsg_2081.length > 0) {
							messageId = 'Msg_2081';
							messageParams = alarmMsg_2081[0].parameters;
							return nts.uk.ui.dialog.caution({ messageId: messageId, messageParams: messageParams })
								.then(() => {
		                            vm.dataChanged(false);
		                            //trigger reload data
		                            vm.dateRange.valueHasMutated();
		                        })
		                        .then(() => lstOvertimeLeaveTime);
						} else {
							return vm.$dialog.info({ messageId: 'Msg_15'})
							.then(() => {
                                vm.dataChanged(false);
                                //trigger reload data
                                vm.dateRange.valueHasMutated();
                            })
                            .then(() => lstOvertimeLeaveTime);
						}
					}
                })
                .fail((response: ErrorMessage) => {
                    let { messageId, parameterIds } = response;
					if(messageId){
						return vm.$dialog
                        .error({ messageId, messageParams: parameterIds })
                        .then(() => null);	
					}
                })
                .then((data: OvertimeLeaveTime[] | null) => {
                    if (data && data.length) {
                        vm.openDialogCaculationResult(data);
                    }
                })
                .always(() => vm.$blockui('clear'));
        }

        // 日付を変更する
        // UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.日付を変更する
        datesSet(start: Date, end: Date) {
            let vm = this;

            vm.dateRange({ start, end });
        }

        over20TaskDays(dates){
            let vm = this;
            let data = ko.unwrap(vm.$datas())
            let result = [];
            _.forEach(dates, date => {
                let eventsInday = _.filter(vm.events(), e => moment(e.start).isSame(moment(date), 'days'));
                let integrationOfDaily = _.filter(data.lstIntegrationOfDaily, id => moment(id.ymd).isSame(moment(date), 'days'));
                let idNos = _.map(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.workNo);
                let eventNos = _.flattenDeep(_.map(eventsInday, e => _.map(e.extendedProps.taskBlock.taskDetails, td => td.supNo)));
                let removeItemNos = _.get(_.find(vm.removeList(), ri => moment(ri.date).isSame(moment(date), 'days')), 'supNos');
                if (_.uniq([].concat(_.difference(idNos, removeItemNos), eventNos)).length > 20) {
                    result.push(moment(date).format('YYYY/MM/DD'));
                }
            });
            return result;
        }

        createDeleteAttByTimeZones(dates){
            let vm = this;
            let employeeId = ko.unwrap(vm.editable) === false ? ko.unwrap(vm.employee) : vm.$user.employeeId;
            let dailyManHrTasks = _.get(vm.$datas(), 'dailyManHrTasks', []);
            
            let deleteList = [];
    
            _.forEach(dates, date => {
                
                let hrTask = _.find(dailyManHrTasks, hr => moment(hr.date).isSame(moment(date), 'days'));
                let id = _.find(_.get(vm.$datas(), 'lstIntegrationOfDaily', []), id => { return moment(id.ymd).isSame(moment(date), 'days'); });

                let ouenTimeSheet = _.get(id, 'ouenTimeSheet', []);

                let taskBlocks = _.get(hrTask, 'taskBlocks', []);
                
                //① 日別実績の工数実績作業
                let dailyManHrTaskNos = [];
                for (let i = 0; i < ouenTimeSheet.length; i++) {
                    let workNo = _.get(ouenTimeSheet[i], 'workNo');

                    if (!_.find(taskBlocks, tb => _.find(tb.taskDetails, ['supNo', workNo]))) {
                       dailyManHrTaskNos.push(workNo);
                    }
                }                
                //② 工数実績作業ブロック
                let currentScreenNos = [];
                _.forEach(_.filter(vm.events(), e => (moment(e.start).isSame(moment(date), 'days') && _.get(e, 'extendedProps.taskBlock.taskDetails', []).length)), e => {
                    currentScreenNos.push(..._.map(e.extendedProps.taskBlock.taskDetails, td => td.supNo));
                });
                //③ 削除応援作業枠Noリスト
                let removeItemNos = _.get(_.find(vm.removeList(), ri => moment(ri.date).isSame(moment(date), 'days')), 'supNos');

                let overwriteDeletions = _.map(_.uniq(_.intersection([].concat(dailyManHrTaskNos, currentScreenNos), removeItemNos)), no => { return { supNo: no, status: 0 } });

                let completeDeletions = _.map(_.uniq(_.difference(removeItemNos, [].concat(dailyManHrTaskNos, currentScreenNos))), no => { return { supNo: no, status: 1 } });
                
                deleteList.push({ date: new Date(moment(date).format('YYYY-MM-DD')), list: overwriteDeletions.concat(completeDeletions) });

            });    
    
    
            return { employeeId, deleteList };
    
        }

        createWorkDetails(dates){
            let vm = this;
            let result = [];

            _.forEach(dates, date => {
                let lstWorkDetailsParamCommand= []
                
                let eventHas2Task = _
                    .chain(vm.events())
                    .filter(({ start }) => moment(start).isSame(moment(date), 'days'))
                    .filter(({ extendedProps }) => _.get(extendedProps, 'taskBlock.taskDetails', []).length > 1).value();
                    
                _.forEach(eventHas2Task, ({ start, end, extendedProps }) => {
                    let {
                        taskBlock
                    } = extendedProps;
                    
                    let nos =[];
                    _.forEach(_.get(taskBlock, 'taskDetails',[]), td => {
                        nos.push(td.supNo);
                    });
                    
                    lstWorkDetailsParamCommand.push({
                            supportFrameNos: nos,
                            timeZone: {
                                end: getTimeOfDate(end),
                                start: getTimeOfDate(start)
                            }
                        });
                });
                
                if (lstWorkDetailsParamCommand.length) {
                    result.push({
                        date: moment(date).format(DATE_TIME_FORMAT),
                        lstWorkDetailsParamCommand
                    });
                }
            });

            return result;
        }

        createIDaily(dates){
            let vm = this;
            let result = [];

            let ids = _.get(vm.$datas(), 'lstIntegrationOfDaily', []);
    
            let removeBreakList = vm.removeBreakList;
            _.forEach(dates, date => {

                let id = _.find(ids, id => moment(id.ymd).isSame(moment(date), 'days'));

                
                if (id) {
                        
                    let id = _.find(ids, id => moment(id.ymd).isSame(moment(date), 'days'));

                    if (id) {
                        //mapping break time
                        let removeBreak = _.find(removeBreakList(), rb => moment(rb.date).isSame(moment(date), 'days'));
                        let breakTime = _.get(id, 'breakTime');
                        breakTime.breakTimeSheets = _.filter(_.get(id, 'breakTime.breakTimeSheets', []), bt => (_.get(removeBreak, 'nos', []).indexOf(bt.no) == -1));

                        if (vm.isShowBreakTime()) {
                              //mapping break time
                            let breakTimes = _.filter(vm.events(), e => moment(e.start).isSame(date, 'day') && _.get(e, 'extendedProps.isTimeBreak', false) == true);
                            
                            breakTime.breakTimeSheets = _.map(breakTimes, bt => {

                                return {
                                    no: _.get(bt, 'extendedProps.no'),
                                    breakTime: _.get(bt, 'extendedProps.breakTime'),
                                    start: (moment(bt.start).hour() * 60) + moment(bt.start).minute(),
                                    end: (moment(bt.end).hour() * 60) + moment(bt.end).minute(),
                                };
                            });
                        }
                    }
                }
            });

            return ids;
        }
        // 作業実績を確認する
        confirm() {
            let vm = this;
            let { $user, $datas, employee, initialDate } = vm;
            let date = ko.unwrap(initialDate);
            let employeeId = ko.unwrap(employee);

            if (employeeId) {
                let command: AddWorkRecodConfirmationCommand = {
                    //対象者
                    // get from A2_5 control
                    employeeId,
                    //対象日
                    // get from initialDate
                    date: moment(date).toISOString(),
                    //確認者
                    // 作業詳細.作業グループ
                    confirmerId: $user.employeeId
                };

                vm
                    .$blockui('grayout')
                    // 作業実績を確認する
                    .then(() => vm.$ajax('at', API.ADD, command))
                    .then((lstComfirmerDto: ConfirmerDto[]) => {
                        let _datas = ko.unwrap($datas);

                        if (_datas) {
                            _datas.lstComfirmerDto = lstComfirmerDto;

                            // update confirmers
                            $datas.valueHasMutated();
                            vm.dataChanged(false);
                        } else {
                            $datas({ lstComfirmerDto, lstWorkRecordDetailDto: [], workCorrectionStartDate: '', workGroupDtos: [] });
                        }
                    })
                    //.then(() => vm.editable.valueHasMutated())
                    .always(() => vm.$blockui('clear'));
            }
        }

        // 作業実績の確認を解除する
        removeConfirm() {
            let vm = this;
            let { $user, $datas, employee, initialDate } = vm;
            let date = ko.unwrap(initialDate);
            let employeeId = ko.unwrap(employee);

            if (employeeId) {
                let command = {
                    //対象者
                    // get from A2_5 control
                    employeeId,
                    //対象日
                    // get from initialDate
                    date: moment(date).toISOString(),
                    //確認者
                    // 作業詳細.作業グループ
                    confirmerId: $user.employeeId
                };

                vm
                    .$blockui('grayout')
                    // 作業実績の確認を解除する
                    .then(() => vm.$ajax('at', API.DELETE, command))
                    .then((lstComfirmerDto: ConfirmerDto[]) => {
                        let _datas = ko.unwrap($datas);

                        if (_datas) {
                            _datas.lstComfirmerDto = lstComfirmerDto;

                            // update confirmers
                            $datas.valueHasMutated();
                            vm.dataChanged(false);
                        } else {
                            $datas({ lstComfirmerDto, lstWorkRecordDetailDto: [], workCorrectionStartDate: '', workGroupDtos: [] });
                        }
                    })
                    // trigger reload event on child component
                    //.then(() => vm.editable.valueHasMutated())
                    .always(() => vm.$blockui('clear'));
            }
        }

        private openDialogCaculationResult(data: OvertimeLeaveTime[]) {
            let vm = this;

            vm.$window
                .modal('at', '/view/kdw/013/d/index.xhtml', data)
                .then(() => { });
        }

        // Popup F:
        updateFavName() {
            let vm = this;

            let updateFavNameCommand: UpdateFavNameCommand = {
                favId: vm.favoriteTaskItem().favoriteId,
                favName: vm.favTaskName()
            }

            vm.$blockui('show');
            vm.$validate(".input-f").then((valid: boolean) => {
				if (valid) {
                    vm.$ajax('at', API.UPDATE_TASK_NAME_F, updateFavNameCommand)
                    .done(() => {
                        vm.$dialog.info({ messageId: 'Msg_15' }).then(()=>{
							vm.closeFDialog();
                            vm.reloadTaskFav();    
                        }); 
                    }).fail((error: any) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });

                } else {
                    vm.$blockui("clear");
                }
            });

        }
        reloadTaskFav(){
            let vm = this;
    
            vm
                .$blockui('grayout')
                .then(() => vm.$ajax('at', API.GET_FAV_TASK).done(data => {
                    vm.$settings().updateFavTask(data);
                    vm.fullCalendar().computedTaskDragItems(ko.unwrap(vm.$datas), ko.unwrap(vm.$settings));
                }))
                .always(() => vm.$blockui('clear'));
        }

        // Popup G:
        registerOneDayFavTask() {
            let vm = this;
            let favId = _.get(vm.oneDayFavoriteSet(), 'favId', '');

            if (favId =='') {
                vm.addOneDayFavTask();
            } else {
                vm.updateOneDayFavName(favId);
            }
        }

        updateOneDayFavName(favTaskId: string) {
            let vm = this;

            let updateFavNameCommand: UpdateFavNameCommand = {
                favId: favTaskId,
                favName: vm.oneDayFavTaskName()
            }

            vm.$blockui('show');
            vm.$validate(".input-g").then((valid: boolean) => {
				if (valid) {
                    vm.$ajax('at', API.UPDATE_TASK_NAME_G, updateFavNameCommand)
                    .done(() => {
                        vm.$dialog.info({ messageId: 'Msg_15' }).then(()=>{
							vm.closeGDialog();
                            vm.reloadOneDayFav();    
                        }); 
                    }).fail((error: any) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });

                } else {
                    vm.$blockui("clear");
                }
            });

        }

        reloadOneDayFav(){
            let vm = this;
    
            vm
                .$blockui('grayout')
                .then(() => vm.$ajax('at', API.GET_FAV_ONE_DAY).done(data => {
                    vm.$settings().updateFavOneday(data);
                    vm.fullCalendar().computedOnedayDragItems(ko.unwrap(vm.$datas), ko.unwrap(vm.$settings));
                }))
                .always(() => vm.$blockui('clear'));
        }

        addOneDayFavTask(){
            let vm = this;

            let registerFavoriteForOneDayCommand : RegisterFavoriteForOneDayCommand = {
                employeeId: vm.$user.employeeId,
                taskName: vm.oneDayFavTaskName(),
                contents: ko.unwrap(vm.taskBlocks)

            }

            vm.$blockui('show');
            vm.$validate(".input-g").then((valid: boolean) => {
				if (valid) {
                    vm.$ajax('at', API.ADD_FAV_TASK_G, registerFavoriteForOneDayCommand)
                    .done(() => {
                        vm.$dialog.info({ messageId: 'Msg_15' }).then(()=>{
							vm.closeGDialog();
                            vm.reloadOneDayFav();    
                        }); 
                    }).fail((error: any) => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide");
                    });

                } else {
                    vm.$blockui("clear");
                }
            });
        }

           taskInfos(){
                let vm = this;
                let warnings = [];
                _.forEach(vm.ouenWorkTimeSheets(), os => {

                    let wt = ko.utils.arrayFirst(vm.ouenWorkTimes(), function(e) { return e.no == os.workNo });
                    let workCD1 = _.get(os, 'workContent.work.workCD1', null);
                    let workCD2 = _.get(os, 'workContent.work.workCD2', null);
                    let workCD3 = _.get(os, 'workContent.work.workCD3', null);
                    let workCD4 = _.get(os, 'workContent.work.workCD4', null);
                    let workCD5 = _.get(os, 'workContent.work.workCD5', null);
                    let taskName1 = '';
                    let taskName2 = '';
                    let taskName3 = '';
                    let taskName4 = '';
                    let taskName5 = '';

                    _.forEach(vm.taskDtos(), task => {
                        if (task.taskFrameNo == 1 && task.code == workCD1) {
                            taskName1 = task.displayInfo.taskName;
                        }

                        if (task.taskFrameNo == 2 && task.code == workCD2) {
                            taskName2 = task.displayInfo.taskName;
                        }

                        if (task.taskFrameNo == 3 && task.code == workCD3) {
                            taskName3 = task.displayInfo.taskName;
                        }

                        if (task.taskFrameNo == 4 && task.code == workCD4) {
                            taskName4 = task.displayInfo.taskName;
                        }

                        if (task.taskFrameNo == 5 && task.code == workCD5) {
                            taskName5 = task.displayInfo.taskName;
                        }
                    });

                    let taskNames =
                        (taskName1 != '' ? taskName1 + ' ' : '') +
                        (taskName2 != '' ? taskName2 + ' ' : '') +
                        (taskName3 != '' ? taskName3 + ' ' : '') +
                        (taskName4 != '' ? taskName4 + ' ' : '') +
                        taskName5;
                    
                        warnings.push({
                            workNo: os.workNo,
                            name: taskNames,
                            time: formatTime(_.get(wt, 'workTime.totalTime', 0), 'Time_Short_HM')
                        });
                    
                });
                return warnings;
            }

        openEDialog(data: any, vm: any) {
            
            let param = {
                workNo: data.workNo,
                //taskDtos: ko.unwrap(vm.taskDtos),
                ouenWorkTimes: ko.unwrap(vm.ouenWorkTimes),
                ouenWorkTimeSheets: ko.unwrap(vm.ouenWorkTimeSheets),
                taskSettings: ko.unwrap(vm.taskSettings),
                //対象日
                date: vm.targetDate(),
            }
		
            vm.$window.modal('at', '/view/kdw/013/e/index.xhtml', param).then(() => { 
				 vm.dateRange.valueHasMutated();
			});
        }

		closeFDialog() {
			$(".popup-area-f").ntsPopup('hide');
			nts.uk.ui.errors.clearAll();
			setTimeout(() => {
				jQuery('button.btn-error.small.danger').appendTo('#functions-area');									
			}, 100);
		}
		
		closeGDialog() {
			$(".popup-area-g").ntsPopup('hide');
			nts.uk.ui.errors.clearAll();
			setTimeout(() => {
				jQuery('button.btn-error.small.danger').appendTo('#functions-area');									
			}, 100);
		}
    }

    
}
