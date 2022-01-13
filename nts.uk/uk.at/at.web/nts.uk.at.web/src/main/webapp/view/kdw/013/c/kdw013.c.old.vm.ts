module nts.uk.ui.at.kdw013.c.old {
    const COMPONENT_NAME = 'kdp013c';

    const DATE_FORMAT = 'YYYY-MM-DD';
    const DATE_TIME_FORMAT = 'YYYY-MM-DDT00:00:00.000\\Z';

    const style = `.edit-event {
        width: 380px;
    }
    .edit-event .header {
        box-sizing: border-box;
        position: relative;
        padding-bottom: 5px;
        line-height: 35px;
        margin-top: -5px;
    }
    .edit-event .header .actions {
        position: absolute;
        top: 0px;
        right: -5px;
    }
    .edit-event .header .actions button {
        margin: 0;
        padding: 0;
        box-shadow: none;
        border: none;
        border-radius: 50%;
        width: 30px;
    }
    .edit-event>table {
        width: 100%;
    }
    .edit-event>table>tbody>tr>td:first-child {
        vertical-align: top;
        padding-top: 6px;
    }
    .edit-event>table>tbody>tr.functional>td {
        text-align: center;
    }
    .edit-event>table>tbody>tr>td>.ntsControl {
        width: 280px;
        display: block;
        box-sizing: border-box;
        margin-bottom: 10px;
    }
    .edit-event>table>tbody>tr>td>.ntsControl>input {
        width: 100%;
        box-sizing: border-box;
    }
    .edit-event>table>tbody>tr>td>.ntsControl>textarea {
        width: 100%;
        height: 80px;
        display: block;
        box-sizing: border-box;
    }
    .edit-event .time-range-control input.nts-input {
        width: 60px;
        text-align: center;
        padding: 5px 3px;
    }
    .edit-event .time-range-control input.nts-input+span {
        margin-left: 7px;
        margin-right: 7px;
    }
    .edit-event .nts-dropdown .message,
    .edit-event .nts-description .message,
    .edit-event .time-range-control .message {
        display: none;
        color: #ff6666;
        font-size: 12px;
        padding-top: 3px;
    }
    .edit-event .nts-dropdown.error .message,
    .edit-event .nts-description.error .message,
    .edit-event .time-range-control.error .message {
        display: block;
    }
    .edit-event .nts-description.error textarea.nts-input,
    .edit-event .time-range-control.error input.nts-input {
        border: 1px solid #ff6666 !important;
    }
    .edit-event .nts-description:not(.error) textarea.nts-input,
    .edit-event .time-range-control:not(.error) input.nts-input {
        border: 1px solid #999 !important;
    }
    .edit-event table tr td:first-child {    
        max-width: 90px;
        line-break: anywhere;
    }

`;

    const { randomId } = nts.uk.util;
    const { number2String, string2Number, validateNumb, getTimeOfDate, setTimeOfDate, getTitles } = share;

    const API: API = {
        START: '/screen/at/kdw013/c/start',
        SELECT: '/screen/at/kdw013/c/select'
    };

    const defaultModelValue = (): EventModel => ({
        descriptions: ko.observable(''),
        timeRange: ko.observable({
            start: null,
            end: null,
            workingHours: null
        }),
        workplace: ko.observable(''),
        task1: ko.observable(''),
        task2: ko.observable(''),
        task3: ko.observable(''),
        task4: ko.observable(''),
        task5: ko.observable('')
    });

    @handler({
        bindingName: 'kdw-confirm',
        validatable: true,
        virtual: false
    })
    export class ConfirmBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservable<ConfirmContent | null>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const resource = valueAccessor();
            const msgid = $('<div>', { 'class': '' }).appendTo(element).get(0);
            const content = $('<div>', { 'class': 'content' }).prependTo(element).get(0);

            ko.applyBindingsToNode(msgid, {
                text: ko.computed({
                    read: () => {
                        const msg = ko.unwrap(resource);

                        if (msg) {
                            return msg.messageId;
                        }

                        return '';
                    },
                    disposeWhenNodeIsRemoved: element
                })
            }, bindingContext);

            ko.applyBindingsToNode(content, {
                text: ko.computed({
                    read: () => {
                        const msg = ko.unwrap(resource);

                        if (msg) {
                            const { messageId, messageParams } = msg;

                            return nts.uk.resource.getMessage(messageId, messageParams);
                        }

                        return '';
                    },
                    disposeWhenNodeIsRemoved: element
                })
            }, bindingContext);

            element.removeAttribute('data-bind');

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: 'kdw-ttg',
        validatable: true,
        virtual: false
    })
    export class KdwToggleBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservable<boolean>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const accessor = valueAccessor();

            const $if = ko.computed({
                read: () => {
                    return ko.unwrap(accessor);
                },
                disposeWhenNodeIsRemoved: element
            });

            const hidden = ko.computed({
                read: () => {
                    return !ko.unwrap(accessor);
                },
                disposeWhenNodeIsRemoved: element
            });

            ko.applyBindingsToNode(element, { if: $if, css: { hidden } }, bindingContext);

            element.removeAttribute('data-bind');

            return { controlsDescendantBindings: true };
        }
    }

    @handler({
        bindingName: COMPONENT_NAME,
        validatable: true,
        virtual: false
    })
    export class BindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } {
            const name = COMPONENT_NAME;
            const params = valueAccessor();

            ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);

            return { controlsDescendantBindings: true };
        }

    }

    @component({
        name: COMPONENT_NAME,
        template: `
        <div class="edit-event">
            <div class="header">
                <div data-bind="i18n: 'KDW013_26'"></div>
                <div class="actions">
                    <button class="close" tabindex="-1" data-bind="click: $component.close, icon: 202, size: 12"></button>
                </div>
            </div>
            <table>
                <colgroup>
                    <col width="90px" />
                </colgroup>
                <tbody>
                    <tr>
                        <td data-bind="i18n: 'KDW013_27'"></td>
                        <td>
                            <div data-bind="
                                    kdw-timerange: $component.model.timeRange,
                                    update: $component.params.update,
                                    hasError: $component.errors.time,
                                    exclude-times: $component.params.excludeTimes
                                "></div>
                        </td>
                    </tr>
                    <tr data-bind="kdw-ttg: $component.usages.taskUse1">
                        <td data-bind="i18n: $component.labels.taskLbl1"></td>
                        <td><div data-bind="
                                dropdown: $component.model.task1,
                                items: $component.combobox.taskList1,
                                required: true,
                                name: $component.labels.taskLbl1,
                                hasError: $component.errors.dropdown,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr data-bind="kdw-ttg: $component.usages.taskUse2">
                        <td data-bind="i18n: $component.labels.taskLbl2"></td>
                        <td><div data-bind="
                                dropdown: $component.model.task2,
                                name: $component.labels.taskLbl2,
                                items: $component.combobox.taskList2,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr data-bind="kdw-ttg: $component.usages.taskUse3">
                        <td data-bind="i18n: $component.labels.taskLbl3"></td>
                        <td><div data-bind="
                                dropdown: $component.model.task3,
                                name: $component.labels.taskLbl3,
                                items: $component.combobox.taskList3,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr data-bind="kdw-ttg: $component.usages.taskUse4">
                        <td data-bind="i18n: $component.labels.taskLbl4"></td>
                        <td><div data-bind="
                                dropdown: $component.model.task4,
                                name: $component.labels.taskLbl4,
                                items: $component.combobox.taskList4,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr data-bind="kdw-ttg: $component.usages.taskUse5">
                        <td data-bind="i18n: $component.labels.taskLbl5"></td>
                        <td><div data-bind="
                                dropdown: $component.model.task5,
                                name: $component.labels.taskLbl5,
                                items: $component.combobox.taskList5,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr class="workplace">
                        <td data-bind="i18n: 'KDW013_28'"></td>
                        <td><div data-bind="
                                dropdown: $component.model.workplace,
                                items: $component.combobox.workLocations,
                                name: 'WORKPLACE',
                                hasError: $component.errors.workplace,
                                visibleItemsCount:10
                            "></div></td>
                    </tr>
                    <tr class="note">
                        <td data-bind="i18n: 'KDW013_29'"></td>
                        <td>
                            <div data-bind="
                                    description: $component.model.descriptions,
                                    name: 'KDW013_29', 
                                    constraint: 'TaskNote',
                                    hasError: $component.errors.description
                                "></div>
                        </td>
                    </tr>
                    <tr class="functional">
                        <td colspan="2">
                            <button class="proceed" data-bind="i18n: 'KDW013_43', click: function() { $component.save.apply($component, []) }, disable: $component.hasError"></button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <style>
            .message.overlay {
                position: fixed;
                display: none;
                top: 0;
                left:0;
                right: 0;
                bottom: 0;
                background-color: #aaaaaa;
                opacity: 0.3;
            }
            .message.container {
                position: fixed;
                display: none;
                border: 1px solid #767171;
                width: 310px;
                box-sizing: border-box;
                background-color: #fff;
                position: fixed;
                top: calc(50% - 67.5px);
                left: calc(50% - 65px);
            }
            .message.overlay.show,
            .message.container.show {
                display: block;
            }
            .message.overlay+.container .title {
                background-color: #F2F2F2;
                border-bottom: 1px solid #767171;
                padding: 5px 12px;
                box-sizing: border-box;
                font-size: 1rem;
                font-weight: 600;
            }
            .message.overlay+.container .body {
                padding: 20px 10px 10px 10px;
                border-bottom: 1px solid #767171;
                box-sizing: border-box;
            }
            .message.overlay+.container .body>div:last-child {
                text-align: right;
                box-sizing: border-box;
            }
            .message.overlay+.container .foot {
                text-align: center;
                padding: 10px 0;
                box-sizing: border-box;
            }
            .message.overlay+.container .foot button:first-child {
                margin-right: 10px;
            }
        </style>
        `
    })
    export class ViewModel extends ko.ViewModel {
        hasError!: KnockoutComputed<boolean>;

        errors: {
            time: KnockoutObservable<boolean>;
            dropdown: KnockoutObservable<boolean>;
            description: KnockoutObservable<boolean>;
            workplace: KnockoutObservable<boolean>;
        } = {
                time: ko.observable(false),
                dropdown: ko.observable(false),
                workplace: ko.observable(false),
                description: ko.observable(false)
            };

        model: EventModel = defaultModelValue();

        combobox: {
            taskList1: KnockoutObservableArray<DropdownItem>;
            taskList2: KnockoutObservableArray<DropdownItem>;
            taskList3: KnockoutObservableArray<DropdownItem>;
            taskList4: KnockoutObservableArray<DropdownItem>;
            taskList5: KnockoutObservableArray<DropdownItem>;
            workLocations: KnockoutComputed<DropdownItem[]>;
        } = {
                taskList1: ko.observableArray([]),
                taskList2: ko.observableArray([]),
                taskList3: ko.observableArray([]),
                taskList4: ko.observableArray([]),
                taskList5: ko.observableArray([]),
                workLocations: ko.computed(() => [])
            };

        labels: {
            taskLbl1: KnockoutObservable<string>;
            taskLbl2: KnockoutObservable<string>;
            taskLbl3: KnockoutObservable<string>;
            taskLbl4: KnockoutObservable<string>;
            taskLbl5: KnockoutObservable<string>;
        } = {
                taskLbl1: ko.observable('C1_10'),
                taskLbl2: ko.observable('C1_13'),
                taskLbl3: ko.observable('C1_16'),
                taskLbl4: ko.observable('C1_19'),
                taskLbl5: ko.observable('C1_22'),
            };

        usages: {
            taskUse1: KnockoutObservable<boolean>;
            taskUse2: KnockoutObservable<boolean>;
            taskUse3: KnockoutObservable<boolean>;
            taskUse4: KnockoutObservable<boolean>;
            taskUse5: KnockoutObservable<boolean>;
        } = {
                taskUse1: ko.observable(false),
                taskUse2: ko.observable(false),
                taskUse3: ko.observable(false),
                taskUse4: ko.observable(false),
                taskUse5: ko.observable(false)
            };

        taskFrameSettings!: KnockoutComputed<a.TaskFrameSettingDto[]>;

        constructor(public params: Params) {
            super();

            const vm = this;
            const { labels, usages } = vm;
            const { $settings } = params;
        
        
            
            const subscribe = (t: a.TaskFrameSettingDto[]) => {
                const [first, second, thirt, four, five] = t;

                if (first) {
                    labels.taskLbl1(first.frameName);
                    usages.taskUse1(first.useAtr === 1);
                }

                if (second) {
                    labels.taskLbl2(second.frameName);
                    usages.taskUse2(second.useAtr === 1);
                }

                if (thirt) {
                    labels.taskLbl3(thirt.frameName);
                    usages.taskUse3(thirt.useAtr === 1);
                }

                if (four) {
                    labels.taskLbl4(four.frameName);
                    usages.taskUse4(four.useAtr === 1);
                }

                if (five) {
                    labels.taskLbl5(five.frameName);
                    usages.taskUse5(five.useAtr === 1);
                }
            };

            vm.taskFrameSettings = ko.computed({
                read: () => {
                    const settings = ko.unwrap($settings);
                   
                    if (settings) {
                        
                        const { startManHourInputResultDto } = settings;

                        const { taskFrameUsageSetting } = startManHourInputResultDto;
                        const { frameSettingList } = taskFrameUsageSetting;

                        return frameSettingList;
                    }

                    return [];
                }
            });

            vm.combobox.workLocations = ko.computed({
                read: () => {
                    const settings = ko.unwrap($settings);
                    
                    const wkp = ko.unwrap(vm.model.workplace);
                    
                    const notFoundItem = {
                        id: wkp,
                        code: wkp,
                        name: vm.$i18n('KDW013_40'),
                        $raw: null,
                        selected: false
                    };

                    if (settings) {
                        const { startManHourInputResultDto } = settings;

                        const { workLocations } = startManHourInputResultDto;

                        const wlcs = workLocations
                            .map((m) => ({
                                id: m.workLocationCD,
                                code: m.workLocationCD,
                                name: m.workLocationName,
                                selected: false,
                                $raw: m
                            }));
                        
                        let selected = _.find(wlcs, (item) => item.code == wkp);
                        
                        if (!!wkp && !selected) {
                            wlcs = [notFoundItem, ...wlcs];
                        }

                        return [{
                            id: '',
                            code: '',
                            name: vm.$i18n('KDW013_41'),
                            $raw: null,
                            selected: false
                        }, ...wlcs]
                    }

                    return wkp ? [notFoundItem] : [];
                },
                write: (value: DropdownItem[]) => {

                }
            });

            this.taskFrameSettings
                .subscribe(subscribe);

            subscribe(this.taskFrameSettings());

            this.hasError = ko
                .computed({
                    read: () => {
                        const errors = ko.toJS(this.errors);

                        return !!errors.time || !!errors.description || !!errors.dropdown || !!errors.workplace;
                    },
                    write: (value: boolean) => {
                        this.errors.time(value);
                        this.errors.dropdown(value);
                        this.errors.workplace(value);
                        this.errors.description(value);
                    }
                })
                .extend({ rateLimit: 250 });

            // this.model.timeRange.subscribe(({ start, end }) => { console.log(start, end) });
        
            
        
        }

        mounted() {
            const vm = this;
            const { $el, params, model, combobox, hasError } = vm;
            const { taskList1, taskList2, taskList3, taskList4, taskList5 } = combobox;
            const { task1, task2, task3, task4, task5, workplace: workLocation, timeRange, descriptions } = model;
            const { view, position, data, excludeTimes } = params;

            const cache = {
                view: ko.unwrap(view),
                position: ko.unwrap(position)
            };
            const mapper = ($raw: TaskDto): DropdownItem => {
                const { code, displayInfo } = $raw;
                const { taskName } = displayInfo;

                return {
                    id: code,
                    code,
                    name: taskName,
                    selected: false,
                    $raw
                };
            };
            const getmapperList = (tasks: TaskDto[], code?) => {
                const lst: DropdownItem[] = [{
                    id: '',
                    code: '',
                    name: vm.$i18n('KDW013_41'),
                    $raw: null,
                    selected: false
                }];
                
                if (code) {
                    let taskSelected = _.find(tasks, { 'code': code });

                    if (!taskSelected) {
                        lst.push({
                            id: code,
                            code,
                            name: vm.$i18n('KDW013_40'),
                            selected: false,
                            $raw: null
                        });
                    }
                }

                _.each(tasks, (t: TaskDto) => {
                    lst.push(mapper(t));
                });

                return lst;
            };
            const subscribe = (event: FullCalendar.EventApi | null) => {
                $.Deferred()
                    .resolve(true)
                    .then(() => {
                        if (event) {
                            const { extendedProps, start, end } = event as any as calendar.EventRaw;
                            const {
                                remarks,
                                employeeId,
                                workCD1,
                                workCD2,
                                workCD3,
                                workCD4,
                                workCD5,
                                workingHours
                            } = extendedProps;
                            const startTime = getTimeOfDate(start);
                            const endTime = getTimeOfDate(end);

                            model.descriptions(remarks);

                            model.timeRange({ start: startTime, end: endTime });

                            const params: StartWorkInputPanelParam = {
                                refDate: moment(start).toISOString(),
                                employeeId: employeeId,
                                workGroupDto: {
                                    workCD1,
                                    workCD2,
                                    workCD3,
                                    workCD4,
                                    workCD5
                                }
                            };

                            return params;
                        } else {
                            model.descriptions('');
                            model.timeRange({ start: null, end: null });
                        }

                        return null;
                    })
                    .then((params: StartWorkInputPanelParam | null) => !!params ? vm.$ajax('at', API.START, params) : null)
                    .then((response: StartWorkInputPanelDto | null) => {
                        if (response) {
                            vm.params.$share(response);
                            const { taskListDto1, taskListDto2, taskListDto3, taskListDto4, taskListDto5 } = response;
                            // update selected
                            if (event) {
                                const { extendedProps } = event as any as calendar.EventRaw;
                                const {
                                    workCD1,
                                    workCD2,
                                    workCD3,
                                    workCD4,
                                    workCD5,
                                    workLocationCD: wlc
                                } = extendedProps;

                                task1(workCD1);
                                task2(workCD2);
                                task3(workCD3);
                                task4(workCD4);
                                task5(workCD5);
                                workLocation(wlc);
                            } else {
                                task1(null);
                                task2(null);
                                task3(null);
                                task4(null);
                                task5(null);
                                workLocation(null);
                            }
                            taskList1(getmapperList(taskListDto1, task1()));
                            taskList2(getmapperList(taskListDto2, task2()));
                            taskList3(getmapperList(taskListDto3, task3()));
                            taskList4(getmapperList(taskListDto4, task4()));
                            taskList5(getmapperList(taskListDto5, task5()));
                        }

                        return true;
                    })
                    // clear error
                    .then(() => hasError(false));
            };

            data.subscribe(subscribe);

            subscribe(data());

            // focus to first input element
            ko.computed({
                read: () => {
                    const _v = ko.unwrap(view);

                    if (_v === 'edit' && cache.view !== _v) {
                        $($el).find('input:first').focus();
                    }

                    cache.view = _v;
                },
                disposeWhenNodeIsRemoved: $el
            });

            // update popup size
            hasError
                .subscribe((has) => {
                    setTimeout(() => {
                        position.valueHasMutated();
                    }, 200);
                });

            task1
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    settings.isChange = vm.changed('TASK');
                    if (taskCode) {
                        const { employeeId } = vm.$user;
                        const { start } = ko.unwrap(data);

                        const params: SelectWorkItemParam = {
                            refDate: moment(start).format(DATE_TIME_FORMAT),
                            employeeId,
                            taskCode,
                            taskFrameNo: 2
                        };

                        vm
                            .$blockui('grayoutView')
                            .then(() => vm.$ajax('at', API.SELECT, params))
                            .then((data: TaskDto[]) => {
                                taskList2(getmapperList(data, ko.unwrap(task2)));
                            })
                            .always(() => vm.$blockui('clearView'));
                    }
                });

            task2
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if(settings)
                    settings.isChange = vm.changed('TASK');
                    if (taskCode) {
                        const { employeeId } = vm.$user;
                        const { start } = ko.unwrap(data);

                        const params: SelectWorkItemParam = {
                            refDate: moment(start).format(DATE_TIME_FORMAT),
                            employeeId,
                            taskCode,
                            taskFrameNo: 3
                        };

                        vm
                            .$blockui('grayoutView')
                            .then(() => vm.$ajax('at', API.SELECT, params))
                            .then((data: TaskDto[]) => {
                                taskList3(getmapperList(data, ko.unwrap(task3)));
                            })
                            .always(() => vm.$blockui('clearView'));
                    }
                });

            task3
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    
                    if(settings)
                    settings.isChange = vm.changed('TASK');
                    if (taskCode) {
                        const { employeeId } = vm.$user;
                        const { start } = ko.unwrap(data);

                        const params: SelectWorkItemParam = {
                            refDate: moment(start).format(DATE_TIME_FORMAT),
                            employeeId,
                            taskCode,
                            taskFrameNo: 4
                        };

                        vm
                            .$blockui('grayoutView')
                            .then(() => vm.$ajax('at', API.SELECT, params))
                            .then((data: TaskDto[]) => {
                                taskList4(getmapperList(data, ko.unwrap(task4)));
                            })
                            .always(() => vm.$blockui('clearView'));
                    }
                });

            task4
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if(settings)
                    settings.isChange = vm.changed('TASK');
                    if (taskCode) {
                        const { employeeId } = vm.$user;
                        const { start } = ko.unwrap(data);

                        const params: SelectWorkItemParam = {
                            refDate: moment(start).format(DATE_TIME_FORMAT),
                            employeeId,
                            taskCode,
                            taskFrameNo: 5
                        };

                        vm
                            .$blockui('grayoutView')
                            .then(() => vm.$ajax('at', API.SELECT, params))
                            .then((data: TaskDto[]) => {
                                taskList5(getmapperList(data, ko.unwrap(task5)));
                            })
                            .always(() => vm.$blockui('clearView'));
                    }
                });
        
            task5
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if (settings)
                        settings.isChange = vm.changed('TASK');
                });
        
            workLocation
                .subscribe((taskCode: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if (settings)
                        settings.isChange = vm.changed('WORKLOC');
                });
        
          
        
            timeRange
                .subscribe((data: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if(settings)
                    settings.isChange = vm.changed('TIME');
                });

            descriptions
                .subscribe((data: string) => {
                    const { $settings } = params;
                    const settings = ko.unwrap($settings);
                    if (settings)
                        settings.isChange = vm.changed('DES');
                });

           
            

            position
                .subscribe((p: any) => {
                    if (!p) {
                        hasError(false);
                        cache.view = 'view';
                    }

                    if (p && cache.position !== p) {
                        $($el).find('input:first').focus();
                    }

                    cache.position = p;
                });

            const $ctn = $($el);

            $ctn
                // prevent tabable to out of popup control
                .on("keydown", ":tabbable", (evt: JQueryKeyEventObject) => {
                    const fable = $ctn.find(":tabbable:not(.close)").toArray();

                    const last = _.last(fable);
                    const first = _.first(fable);

                    if (evt.keyCode === 9) {
                        if ($(evt.target).is(last) && evt.shiftKey === false) {
                            first.focus();

                            evt.preventDefault();
                        } else if ($(evt.target).is(first) && evt.shiftKey === true) {
                            last.focus();

                            evt.preventDefault();
                        }
                    }
                });

            if (!$(`style#${COMPONENT_NAME}`).length) {
                $('<style>', { id: COMPONENT_NAME, html: style }).appendTo('head');
            }

            _.extend(window, { pp: vm });
        
        
            
        }

        yes() {
            const vm = this;
            const { params } = vm;
            params.close('yes');
        }

        close() {
            const vm = this;
            const { params ,model} = vm;
            const { data } = params;
            const event = ko.unwrap(data);
            const { timeRange, descriptions } = model;
            $.Deferred()
                .resolve(true)
                //.then(() => $(vm.$el).find('input, textarea').trigger('blur'))
                .then(() => {
                    const { title, extendedProps } = ko.unwrap(data);

                    return _.isEmpty(extendedProps) || (!title && extendedProps.status === 'new');
                })
                .then((isNew: boolean | null) => {
                    if (isNew) {
                        vm.$dialog
                            .confirm({ messageId: 'Msg_2094' })
                            .then((v: 'yes' | 'no') => {
                                if (v === 'yes') {
                                    vm.yes();
                                }
                            });
                    } else {
                        
                        if (vm.changed()) {
                            vm.$dialog
                                .confirm({ messageId: 'Msg_2094' })
                                .then((v: 'yes' | 'no') => {
                                    if (v === 'yes') {
                                         params.close();
                                    }
                                });
                        } else {
                            params.close();
                        }
                        
                        
                    }
                });
        }
    
        getTaskInfo(){
            const vm = this;
            const {  model, combobox} = vm;
            const { task1, task2, task3, task4, task5 } = model;
            const { taskList1, taskList2, taskList3, taskList4, taskList5 } = combobox;
            const t1 = ko.unwrap(task1);
            const t2 = ko.unwrap(task2);
            const t3 = ko.unwrap(task3);
            const t4 = ko.unwrap(task4);
            const t5 = ko.unwrap(task5);

            if (t1) {
                const selected = _.find(ko.unwrap(taskList1), ({ id }) => t1 === id);

                if (selected) {
                    return selected.$raw;
                }
            }
            if (t2) {
                const selected = _.find(ko.unwrap(taskList2), ({ id }) => t2 === id);

                if (selected) {
                    return selected.$raw;
                }
            }
            if (t3) {
                const selected = _.find(ko.unwrap(taskList3), ({ id }) => t3 === id);

                if (selected) {
                    return selected.$raw;
                }
            }
            if (t4) {
                const selected = _.find(ko.unwrap(taskList4), ({ id }) => t4 === id);

                if (selected) {
                    return selected.$raw;
                }
            }
            if (t5) {
                const selected = _.find(ko.unwrap(taskList5), ({ id }) => t5 === id);

                if (selected) {
                    return selected.$raw;
                }
            }

            return null;
        }
    
        getTitles(){
            const vm = this;
            const {  model, combobox} = vm;
            const { task1, task2, task3, task4, task5, workplace } = model;
            const { taskList1, taskList2, taskList3, taskList4, taskList5 } = combobox;
            const t1 = ko.unwrap(task1);
            const t2 = ko.unwrap(task2);
            const t3 = ko.unwrap(task3);
            const t4 = ko.unwrap(task4);
            const t5 = ko.unwrap(task5);

            let tastNames = [];
            if (t1) {
                const selected = _.find(ko.unwrap(taskList1), ({ id }) => t1 === id);

                if (selected) {
                    tastNames.push(selected.name);
                }
            }
            if (t2) {
                const selected = _.find(ko.unwrap(taskList2), ({ id }) => t2 === id);

                if (selected) {
                    tastNames.push(selected.name);
                }
            }
            if (t3) {
                const selected = _.find(ko.unwrap(taskList3), ({ id }) => t3 === id);

                if (selected) {
                    tastNames.push(selected.name);
                }
            }
            if (t4) {
                const selected = _.find(ko.unwrap(taskList4), ({ id }) => t4 === id);

                if (selected) {
                    tastNames.push(selected.name);
                }
            }
            if (t5) {
                const selected = _.find(ko.unwrap(taskList5), ({ id }) => t5 === id);

                if (selected) {
                    tastNames.push(selected.name);
                }
            }

            return tastNames.join("\n");
        }
    
    
        isTaskChanged(event){
            const vm = this;
            const {  model, combobox} = vm;
            const {extendedProps} = event;
            const {workCD1, workCD2, workCD3, workCD4, workCD5, workLocationCD} = extendedProps
            const { task1, task2, task3, task4, task5, workplace } = model;
            const t1 = ko.unwrap(task1);
            const t2 = ko.unwrap(task2);
            const t3 = ko.unwrap(task3);
            const t4 = ko.unwrap(task4);
            const t5 = ko.unwrap(task5);
            const wkp = ko.unwrap(workplace);
            if (t1) {
                if (t1 != workCD1) {
                    return true;
                }
            }
            if (t2) {
                if (t2 != workCD2) {
                    return true;
                }
            }
            if (t3) {
                if (t3 != workCD3) {
                    return true;
                }
            }
            if (t4) {
                if (t4 != workCD4) {
                    return true;
                }
            }
            if (t5) {
                if (t5 != workCD5) {
                    return true;
                }
            }
            if (wkp) {
                if (wkp != workLocationCD) {
                    return true;
                }
            }
        }

        save(result?: 'yes' | 'cancel' | null) {
            const vm = this;
            const { params, model, combobox } = vm;
            const { data } = params;
            const event = data();
            const { timeRange, descriptions } = model;
            const { employeeId } = vm.$user;
            const { task1, task2, task3, task4, task5, workplace } = model;
            const { taskList1, taskList2, taskList3, taskList4, taskList5 } = combobox;
            const t1 = ko.unwrap(task1);
            const t2 = ko.unwrap(task2);
            const t3 = ko.unwrap(task3);
            const t4 = ko.unwrap(task4);
            const t5 = ko.unwrap(task5);

            $.Deferred()
                .resolve(true)
                // validate control
                .then(() => $(vm.$el).find('input, textarea').trigger('blur'))
                .then(() => vm.hasError())
                .then((invalid: boolean) => {
                    if (!invalid) {
                        
                        if (event) {
                            const { start } = event;
                            const tr = ko.unwrap(timeRange);
                            const task = vm.getTaskInfo();

                            event.setStart(setTimeOfDate(start, tr.start));
                            event.setEnd(setTimeOfDate(start, tr.end));

                            const { status } = event.extendedProps;

                            if (!event.extendedProps.id) {
                                event.setExtendedProp('id', randomId());
                            }

                            if (['new', 'add'].indexOf(status) === -1) {
                                event.setExtendedProp('status', 'add');
                            } else {
                                event.setExtendedProp('status', 'update');
                            }

                            if (task) {
                                const { displayInfo } = task;

                                if (displayInfo) {
                                    const { color, taskName } = displayInfo;

                                    event.setProp('title', vm.getTitles());
                                    event.setProp('backgroundColor', color);
                                }
                            }

                            event.setExtendedProp('workCD1', ko.unwrap(task1));
                            event.setExtendedProp('workCD2', ko.unwrap(task2));
                            event.setExtendedProp('workCD3', ko.unwrap(task3));
                            event.setExtendedProp('workCD4', ko.unwrap(task4));
                            event.setExtendedProp('workCD5', ko.unwrap(task5));

                            event.setExtendedProp('sId', employeeId);
                            event.setExtendedProp('workLocationCD', ko.unwrap(workplace));
                          

                            event.setExtendedProp('remarks', descriptions());
                            event.setExtendedProp('workingHours', tr.end - tr.start);
                        }

                        // close popup
                        params.close(result);
                    }
                });
        }
    
        changed(pos:'TIME'|'TASK'|'DES'|'ALL'){

            const vm = this;
            const { params, model} = vm;
            const { data } = params;
            const event = ko.unwrap(data);
            const { timeRange, descriptions } = model;


            if (event) {
                const tr = ko.unwrap(timeRange);
                const { start, end } = event;
                const task = vm.getTaskInfo();

                if (pos == 'TIME' || pos == 'ALL') {
                    if (start.getTime() != setTimeOfDate(start, tr.start).getTime()) {
                        event.setExtendedProp('isChanged', true);

                        return true;
                    }

                    if (end.getTime() != setTimeOfDate(start, tr.end).getTime()) {
                        console.log('time changed');
                        return true;
                    }
                }
             
                if (pos == 'TASK'|| pos == 'ALL') {
                    if (task) {
                        const { displayInfo } = task;

                        if (displayInfo) {
                            const { color, taskName } = displayInfo;
                            if (vm.isTaskChanged(event)) {
                                event.setExtendedProp('isChanged', true);

                                return true;
                            }
                        }
                    }
                }

                if (pos == 'WORKLOC'|| pos == 'ALL') {
                    const {workplace} = model;
                    const wkp = ko.unwrap(workplace);
                    if (wkp) {
                        if (wkp != _.get(event,'extendedProps.workLocationCD')) {
                            event.setExtendedProp('isChanged', true);

                            return true;
                        }
                    }
                }

                if (pos == 'DES'|| pos == 'ALL') {
                    if (_.get(event, 'extendedProps.remarks') != descriptions()) {
                         event.setExtendedProp('isChanged', true);

                        return true;
                    }
                }
                
                return false;
            }
            return false;
        }
}

    type Params = {
        close: (result?: 'yes' | 'cancel' | null) => void;
        remove: () => void;
        mode: KnockoutObservable<boolean>;
        view: KnockoutObservable<'view' | 'edit'>;
        data: KnockoutObservable<FullCalendar.EventApi>;
        position: KnockoutObservable<null | any>;
        excludeTimes: KnockoutObservableArray<share.BussinessTime>;
        $settings: KnockoutObservable<a.StartProcessDto | null>;
        $share: KnockoutObservable<StartWorkInputPanelDto | null>;
    }

    type DropdownItem = {
        id: string;
        code: string;
        name: string;
        selected: boolean;
        $raw: any;
    };
}