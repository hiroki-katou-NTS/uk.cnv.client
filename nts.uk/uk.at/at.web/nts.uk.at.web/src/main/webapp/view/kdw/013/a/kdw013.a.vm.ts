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

    const { formatTime, setTimeOfDate, getTimeOfDate, getTask, getBackground, getTitles } = share;
    const { randomId } = nts.uk.util;

    const DATE_FORMAT = 'YYYY-MM-DD';
    const DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:00.000\\Z';

    const API: API = {
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
        REGISTER: '/screen/at/kdw013/a/register_work_content'
    };

    const initialCache = (): ChangeDateParam => ({
        displayPeriod: {
            end: '',
            start: ''
        },
        employeeId: '',
        refDate: ''
    })

    @handler({
        bindingName: 'kdw-toggle',
        validatable: true,
        virtual: false
    })
    export class KDWToggleBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLButtonElement, valueAccessor: () => KnockoutComputed<boolean>) {
            const toggler = valueAccessor();
            const visible = ko.computed({
                read: () => {
                    const visible = ko.unwrap(toggler);

                    return visible;
                },
                disposeWhenNodeIsRemoved: element
            });
            const disable = ko.computed({
                read: () => {
                    const enable = ko.unwrap(toggler);

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

        breakTime: KnockoutObservable<calendar.BreakTime> = ko.observable();

        businessHours: KnockoutObservableArray<calendar.BussinessHour> = ko.observableArray([]);

        weekends: KnockoutObservable<boolean> = ko.observable(true);
        editable: KnockoutObservable<boolean> = ko.observable(true);
        firstDay: KnockoutObservable<number> = ko.observable(1);
        scrollTime: KnockoutObservable<number> = ko.observable(420);
        slotDuration: KnockoutObservable<number> = ko.observable(30);
        initialDate: KnockoutObservable<Date> = ko.observable(new Date());
        dateRange: KnockoutObservable<Partial<calendar.DatesSet>> = ko.observable({});
        initialView: KnockoutObservable<string> = ko.observable('oneDay');
        availableView: KnockoutObservableArray<calendar.InitialView> = ko.observableArray(['oneDay', 'fullWeek']);
        validRange: KnockoutObservable<Partial<calendar.DatesSet>> = ko.observable({});

        employee: KnockoutObservable<string> = ko.observable('');

        confirmers!: KnockoutComputed<calendar.Employee[]>;
        // need map with [KDW013_21, KDW013_22, KDW013_23, KDW013_24] resource
        attendanceTimes!: KnockoutComputed<calendar.AttendanceTime[]>;

        // Change date range data model
        $datas: KnockoutObservable<ChangeDateDto | null> = ko.observable(null);

        // settings (first load data)
        $settings: KnockoutObservable<StartProcessDto | null> = ko.observable(null);
    
        dataChanged: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            super();

            const vm = this;
            let $query = vm.getQuery();
            const { employee } = vm;
            const { mode } = $query;
            const cache: ChangeDateParam & { pair: -1 | 0 | 1 | 2 } = { ...initialCache(), pair: 0 };
            const sameCache = (params: ChangeDateParam): -1 | 0 | 1 | 2 => {
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
            const updateCache = (params: ChangeDateParam) => {
                cache.displayPeriod.end = params.displayPeriod.end;
                cache.displayPeriod.start = params.displayPeriod.start;

                cache.refDate = params.refDate;
            };
            const computedEvents = (data: SelectTargetEmployeeDto | null, settings: StartProcessDto | null) => {
                if (cache.pair === -1) {
                    return;
                }

                if (data && settings) {
                    const { lstWorkRecordDetailDto } = data;
                    const { startManHourInputResultDto } = settings;

                    if (lstWorkRecordDetailDto && startManHourInputResultDto) {
                        const { tasks } = startManHourInputResultDto;
                        const events = _
                            .chain(lstWorkRecordDetailDto)
                            .map(({ date, employeeId, lstWorkDetailsParamDto }) => {
                                const events: calendar.EventRaw[] =
                                    _.chain(lstWorkDetailsParamDto)
                                        .map(({
                                            remarks,
                                            supportFrameNo,
                                            timeZone,
                                            workGroup,
                                            workLocationCD,
                                        }) => {
                                            const $date = moment(date, DATE_FORMAT).toDate();

                                            const { end, start } = timeZone;
                                            const {
                                                workCD1,
                                                workCD2,
                                                workCD3,
                                                workCD4,
                                                workCD5
                                            } = workGroup;
                                            const task = getTask(workGroup, tasks) || { displayInfo: {} } as any as c.TaskDto;
                                            
                                            const wg = {
                                                workCD1,
                                                workCD2,
                                                workCD3,
                                                workCD4,
                                                workCD5
                                            }

                                            const { timeWithDay: startTime } = start;
                                            const { timeWithDay: endTime } = end;
                                            
                                            return {
                                                start: setTimeOfDate($date, startTime),
                                                end: setTimeOfDate($date, endTime || (startTime + 60)),
                                                title: getTitles(wg, tasks),
                                                backgroundColor : getBackground(wg, tasks),
                                                textColor: '',
                                                extendedProps: {
                                                    id: randomId(),
                                                    status: 'normal' as any,
                                                    remarks,
                                                    employeeId,
                                                    supportFrameNo,
                                                    workCD1,
                                                    workCD2,
                                                    workCD3,
                                                    workCD4,
                                                    workCD5,
                                                    workLocationCD
                                                } as any
                                            };
                                        })
                                        .value();

                                return events;
                            })
                            .flatten()
                            .value();

                        vm.events(events);

                        return;
                    }
                }

                vm.events([]);
            };

            vm.$datas
                .subscribe((datas) => computedEvents(datas, ko.unwrap(vm.$settings)));
    
            vm.events.subscribe((datas) => vm.dataChanged(true));

            vm.$settings
                .subscribe((settings) => computedEvents(ko.unwrap(vm.$datas), settings));

            vm.$toggle = {
                save: ko.computed({
                    read: () => {
                        const $settings = ko.unwrap(vm.$settings);
                        
                        if (!vm.dataChanged()) {
                            return false;
                        }

                        if (!$settings) {
                            return true;
                        }

                        const { startManHourInputResultDto } = $settings;

                        if (!startManHourInputResultDto) {
                            return true;
                        }

                        const { taskFrameUsageSetting } = startManHourInputResultDto;

                        if (!taskFrameUsageSetting) {

                            return true;
                        }
                        

//                        const { frameSettingList } = taskFrameUsageSetting;

//                        if (frameSettingList && frameSettingList.length) {
//                            return !!_.find(frameSettingList, ({ useAtr, frameNo }) => frameNo === 2 && useAtr === 1);
//                        }

                        return true;
                    }
                }),
                remove: ko.computed({
                    read: () => {
                        const editable = ko.unwrap(vm.editable);
                        let confimer = _.find(_.get(vm.$datas(),'lstComfirmerDto'), ['confirmSID',vm.$user.employeeId]);
                        return !editable && !!confimer;
                    }
                }),
                confirm: ko.computed({
                    read: () => {
                        const editable = ko.unwrap(vm.editable);
                        let confimer = _.find(_.get(vm.$datas(),'lstComfirmerDto'), ['confirmSID',vm.$user.employeeId]);
                        return !editable && !confimer;
                    }
                }),
            };

            if (mode) {
                // URLの値元に画面モードを判定する
                vm.editable(mode === '0');
            }

            ko.computed({
                read: () => {
                    const employeeId = ko.unwrap(vm.editable) === false ? ko.unwrap(vm.employee) : vm.$user.employeeId;
                    const date = ko.unwrap(vm.initialDate);
                    const dateRange = ko.unwrap(vm.dateRange);
                    const { start, end } = dateRange;

                    if (!employeeId) {
                        return;
                    }

                    if (!!start && !!end && moment(date).isBetween(start, end)) {
                        const params: ChangeDateParam = {
                            employeeId,
                            refDate: moment(date).startOf('day').format(DATE_TIME_FORMAT),
                            displayPeriod: {
                                start: moment(start).startOf('day').format(DATE_TIME_FORMAT),
                                end: moment(end).subtract(1, 'day').startOf('day').format(DATE_TIME_FORMAT)
                            }
                        };
                        cache.pair = sameCache(params);

                        if (cache.pair !== 2) {
                            updateCache(params);
                        }

                        if (cache.pair <= 0) {
                            // s.h.i.t
                            // vm.$datas(null);

                            vm
                                .$blockui('grayout')
                                .then(() => vm.$ajax('at', API.CHANGE_DATE, params))
                                .then((data: ChangeDateDto) => {
                                    vm.$datas(data);
                                    vm.dataChanged(false);
                                })
                                .always(() => vm.$blockui('clear'));
                        }
                    }
                }
            }).extend({ rateLimit: 250 });

            vm.confirmers = ko.computed({
                read: () => {
                    const datas = ko.unwrap(vm.$datas);
                    const $date = ko.unwrap(vm.initialDate);
                    const $moment = moment($date).format(DATE_FORMAT);

                    if (datas) {
                        const { lstComfirmerDto } = datas;

                        return _
                            .chain(lstComfirmerDto)
                            .filter(({ confirmDateTime }) => (confirmDateTime || "").indexOf($moment) === 0)
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

            vm.attendanceTimes = ko.computed({
                read: () => {
                    const datas = ko.unwrap(vm.$datas);
                    const employee = ko.unwrap(vm.employee);

                    // need update by employId if: mode=1
                    const employeeId = employee || vm.$user.employeeId;

                    if (datas) {
                        const { lstWorkRecordDetailDto } = datas;

                        return _
                            .chain(lstWorkRecordDetailDto)
                            // .orderBy(['date'])
                            .filter(({ employeeId }) => employeeId === employeeId)
                            .map(({
                                date: strDate,
                                actualContent,
                            }) => {
                                const events: string[] = [];
                                const date = moment(strDate, DATE_FORMAT).toDate();
                                const { breakHours, end, start, totalWorkingHours } = actualContent;

                                if (start) {
                                    const { timeWithDay } = start;

                                    if (_.isNumber(timeWithDay)) {
                                        events.push(vm.$i18n('KDW013_21', [formatTime(timeWithDay, 'Time_Short_HM')]));
                                    }
                                }

                                if (end) {
                                    const { timeWithDay } = end;

                                    if (_.isNumber(timeWithDay)) {
                                        events.push(vm.$i18n('KDW013_22', [formatTime(timeWithDay, 'Time_Short_HM')]));
                                    }
                                }

                                if (_.isNumber(breakHours)) {
                                    events.push(vm.$i18n('KDW013_23', [formatTime(breakHours, 'Time_Short_HM')]));
                                }

                                if (_.isNumber(totalWorkingHours)) {
                                    events.push(vm.$i18n('KDW013_24', [formatTime(totalWorkingHours, 'Time_Short_HM')]));
                                }

                                return { date, events, };
                            })
                            .value();
                    }

                    return [] as calendar.AttendanceTime[];
                }
            }).extend({ rateLimit: 500 });

            // get settings Msg_1960
            vm
                .$blockui('grayout')
                .then(() => vm.$ajax('at', API.START))
                .then((response: StartProcessDto) => {
                    // 作業利用設定チェック
                    if (!response) {
                        return vm.$dialog.error({ messageId: 'Msg_1960' });
                    }

                    const { startManHourInputResultDto } = response;

                    if (!startManHourInputResultDto) {
                        return vm.$dialog.error({ messageId: 'Msg_1960' });
                    }

                    const { taskFrameUsageSetting, tasks } = startManHourInputResultDto;

                    if (!taskFrameUsageSetting) {
                        return vm.$dialog.error({ messageId: 'Msg_1960' });
                    }

                    const { frameSettingList } = taskFrameUsageSetting;
                    
                    frameSettingList  = _.filter(frameSettingList, ['useAtr', 1]);

                    if (!frameSettingList || frameSettingList.length === 0) {
                        return vm.$dialog.error({ messageId: 'Msg_1960' });
                    }

                    // 作業マスタチェック
                    if (!tasks || tasks.length === 0) {
                        return vm.$dialog.error({ messageId: 'Msg_1961' });
                    }
                    
                    vm.$window
                        .storage('KDW013_SETTING')
                        .then((value: any) => {
                            if (value) {
                                vm.initialView(value.initialView);
                            }
                        });
                

                    vm.$settings(response);
                })
                .always(() => vm.$blockui('clear'));
        }

        mounted() {
            const vm = this;

            _.extend(window, { vm });
        }

        getQuery(){
            let query = location.search.substring(1);
            if (!query || !query.match(/=/)) {
                return {};
            }
            return JSON.parse('{"' + decodeURI(query).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}');
    
        }

        saveData() {
            const vm = this;
            const { events, dateRange } = vm;
            const { HAND_CORRECTION_MYSELF } = EditStateSetting;
            const { start, end } = ko.unwrap(dateRange);

            if (!start || !end) {
                return;
            }

            const $events = ko.unwrap(events);
            const dateRanges = () => {
                const dates: Date[] = [];
                const begin = moment(start);

                while (begin.isBefore(end, 'day')) {
                    dates.push(begin.toDate());

                    begin.add(1, 'day');
                }

                return dates;
            };
    
            let sid = vm.employee() ? vm.employee() : vm.$user.employeeId;
    
            let mode =  vm.editable() ? 0 : vm.employee() === vm.$user.employeeId ? 0 : 1;

            const command: RegisterWorkContentCommand = {
                changedDate: moment().format(DATE_TIME_FORMAT),
                editStateSetting: HAND_CORRECTION_MYSELF,
                employeeId: sid,
                mode,
                workDetails: dateRanges().map((date) => {
                    const lstWorkDetailsParamCommand = _
                        .chain($events)
                        .filter(({ start }) => moment(start).isSame(date, 'day'))
                        .map(({ start, end, extendedProps }) => {
                            const {
                                workCD1,
                                workCD2,
                                workCD3,
                                workCD4,
                                workCD5,
                                workLocationCD,
                                remarks,
                                supportFrameNo
                            } = extendedProps;
                           
                            return {
                                remarks,
                                supportFrameNo,
                                workGroup: {
                                    workCD1: !_.isEmpty(workCD1) ? workCD1 : undefined,
                                    workCD2: !_.isEmpty(workCD2) ? workCD2 : undefined,
                                    workCD3: !_.isEmpty(workCD3) ? workCD3 : undefined,
                                    workCD4: !_.isEmpty(workCD4) ? workCD4 : undefined,
                                    workCD5: !_.isEmpty(workCD5) ? workCD5 : undefined,
                                },
                                workLocationCD: workLocationCD == "" ? null :workLocationCD,
                                timeZone: {
                                    end: getTimeOfDate(end),
                                    start: getTimeOfDate(start)
                                }
                            };
                        })
                        .value();

                    return {
                        date: moment(date).format(DATE_TIME_FORMAT),
                        lstWorkDetailsParamCommand
                    };
                })
            };

            vm
                .$blockui('grayout')
                // 作業を登録する
                .then(() => vm.$ajax('at', API.REGISTER, command))
                .then((response: RegisterWorkContentDto) => {

                    if (response) {
                        const { lstErrorMessageInfo, lstOvertimeLeaveTime } = response;

                        if (!lstErrorMessageInfo || lstErrorMessageInfo.length === 0) {
                            return vm.$dialog
                                .info({ messageId: 'Msg_15' })
                                .then(() => lstOvertimeLeaveTime);
                        }
                    }

                    return $
                        .Deferred()
                        .resolve()
                        .then(() => null);
                })
                .fail((response: ErrorMessage) => {
                    const { messageId, parameterIds } = response;

                    return vm.$dialog
                        // Msg_2066, Msg_2080
                        .error({ messageId, messageParams: parameterIds })
                        .then(() => null);
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
            const vm = this;

            vm.dateRange({ start, end });
        }

        // 作業実績を確認する
        confirm() {
            const vm = this;
            const { $user, $datas, employee, initialDate } = vm;
            const date = ko.unwrap(initialDate);
            const employeeId = ko.unwrap(employee);

            if (employeeId) {
                const command: AddWorkRecodConfirmationCommand = {
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
                        const _datas = ko.unwrap($datas);

                        if (_datas) {
                            _datas.lstComfirmerDto = lstComfirmerDto;

                            // update confirmers
                            $datas.valueHasMutated();
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
            const vm = this;
            const { $user, $datas, employee, initialDate } = vm;
            const date = ko.unwrap(initialDate);
            const employeeId = ko.unwrap(employee);

            if (employeeId) {
                const command = {
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
                        const _datas = ko.unwrap($datas);

                        if (_datas) {
                            _datas.lstComfirmerDto = lstComfirmerDto;

                            // update confirmers
                            $datas.valueHasMutated();
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
            const vm = this;

            vm.$window
                .modal('at', '/view/kdw/013/d/index.xhtml', data)
                .then(() => { });
        }
    }

    export module department {
        type EmployeeDepartmentParams = {
            mode: KnockoutObservable<boolean>;
            employee: KnockoutObservable<string>;
            $settings: KnockoutObservable<a.StartProcessDto | null>;
        };

        @component({
            name: 'kdw013-department',
            template: `<h3 data-bind="i18n: 'KDW013_4'"></h3>
            <div data-bind="ntsComboBox: {
                name: $component.$i18n('KDW013_5'),
                options: $component.departments,
                visibleItemsCount: 20,
                value: $component.department,
                editable: true,
                selectFirstIfNull: true,
                optionsValue: 'workplaceId',
                optionsText: 'wkpDisplayName',
                columns: [
                    { prop: 'workplaceCode', length: 4 },
                    { prop: 'wkpDisplayName', length: 10 }
                ]
            }"></div>
            <ul class="list-employee" data-bind="foreach: { data: $component.employees, as: 'item' }">
                <li class="item" data-bind="
                    click: function() { $component.selectEmployee(item.employeeId) },
                    timeClick: -1,
                    css: {
                        'selected': ko.computed(function() { return item.employeeId === ko.unwrap($component.params.employee); })
                    }">
                    <div data-bind="text: item.employeeCode"></div>
                    <div data-bind="text: item.employeeName"></div>
                </li>
            </ul>`
        })
        export class EmployeeDepartmentComponent extends ko.ViewModel {
            department: KnockoutObservable<string> = ko.observable('');

            employees!: KnockoutComputed<EmployeeBasicInfoDto[]>;
            departments!: KnockoutComputed<WorkplaceInfoDto[]>;

            constructor(private params: EmployeeDepartmentParams) {
                super();

                const vm = this;
                const { $settings, mode } = params;

                vm.employees = ko.computed({
                    read: () => {
                        const loaded = ko.unwrap(mode);
                        const $sets = ko.unwrap($settings);
                        const $dept = ko.unwrap(vm.department);

                        if ($sets) {
                            const { refWorkplaceAndEmployeeDto } = $sets;

                            if (refWorkplaceAndEmployeeDto) {
                                const { employeeInfos, lstEmployeeInfo } = refWorkplaceAndEmployeeDto;
                                let emps = _.filter(employeeInfos,{'workplaceId': $dept });
                                // updating
                                return loaded ? [] : _.filter(lstEmployeeInfo, (o) => {
                                   return !!_.find(emps, { 'employeeId': o.employeeId });
                                });
                            }
                        }

                        return [];
                    },
                    write: (value: any) => {

                    }
                });

                vm.departments = ko.computed({
                    read: () => {
                        const loaded = ko.unwrap(mode);
                        const $sets = ko.unwrap($settings);

                        if ($sets) {
                            const { refWorkplaceAndEmployeeDto } = $sets;

                            if (refWorkplaceAndEmployeeDto) {
                                const { workplaceInfos } = refWorkplaceAndEmployeeDto;

                                return loaded ? [] : workplaceInfos;
                            }
                        }

                        return [];
                    },
                    write: (value: any) => {

                    }
                });
    
                vm.departments
                    .subscribe((deps) => {
                        if (!_.isEmpty(deps)) {

                            let empInfo = _.find(vm.params.$settings().refWorkplaceAndEmployeeDto.employeeInfos, { 'employeeId': vm.$user.employeeId });

                            if (empInfo) {

                                let selectedWkp = _.find(deps, { 'workplaceId': empInfo.workplaceId });

                                if (selectedWkp) {

                                    vm.department(selectedWkp.workplaceId);
                                }
                            }

                        }
                    });

                vm.employees
                    .subscribe((emps: EmployeeBasicInfoDto[]) => {
                        if (emps.length && !ko.unwrap(vm.params.employee)) {

                            let emp = _.find(emps, { 'employeeId': vm.$user.employeeId });
                            if (emp) {
                                vm.params.employee(emp.employeeId);
                            } else {
                                const [first] = emps;
                                vm.params.employee(first.employeeId);
                            }

                        }
                    });
            }

            mounted() {
                const vm = this;
                const { $el } = vm;

                $($el)
                    .removeAttr('data-bind')
                    .find('[data-bind]')
                    .removeAttr('data-bind');
            }

            public selectEmployee(id: string) {
                const vm = this;
                const { department } = vm;
                const { employee } = vm.params;

                employee(id);

                department.valueHasMutated();
            }
        }
    }

    export module approved {
        type Kdw013ApprovedParams = {
            mode: KnockoutObservable<boolean>;
            confirmers: KnockoutObservableArray<calendar.Employee>;
            $settings: KnockoutObservable<a.StartProcessDto | null>;
        };

        @component({
            name: 'kdw013-approveds',
            template: `<h3 data-bind="i18n: 'KDW013_6'"></h3>
            <ul data-bind="foreach: { data: $component.params.confirmers, as: 'item' }">
                <li class="item">
                    <div data-bind="text: item.code"></div>
                    <div data-bind="text: item.name"></div>
                </li>
            </ul>`
        })
        export class Kdw013ApprovedComponent extends ko.ViewModel {
            constructor(public params: Kdw013ApprovedParams) {
                super();
            }
        }
    }

    export module event {
        @component({
            name: 'kdw013-events',
            template: `<h3 data-bind="i18n: 'KDW013_7'"></h3>
            <ul data-bind="foreach: { data: $component.params.items, as: 'item' }">
                <li class="title" data-bind="attr: {
                    'data-id': _.get(item.extendedProps, 'relateId', ''),
                    'data-color': item.backgroundColor
                }">
                    <div data-bind="style: {
                        'background-color': item.backgroundColor
                    }"></div>
                    <div>
                        <label  class='limited-label' style='width:100%'  data-bind='text: item.title'>
                        </label>
                    </div>
                </li>
            </ul>`
        })
        export class Kdw013EventComponent extends ko.ViewModel {
            constructor(public params: EventParams) {
                super();
            }
        }

        type EventParams = {
            items: KnockoutObservableArray<any>;
            mode: KnockoutComputed<boolean>;
        };
    }
}