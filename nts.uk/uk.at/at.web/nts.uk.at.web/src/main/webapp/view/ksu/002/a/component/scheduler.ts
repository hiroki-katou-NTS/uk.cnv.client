/// <reference path="../../../../../lib/nittsu/viewcontext.d.ts" />

declare module nts {
    export module uk {
        export module util {
            export module browser {
                export const version: string;
            }
        }
    }
}

module nts.uk.ui.at.ksu002.a {
    import c = nts.uk.ui.calendar;
    import b = nts.uk.util.browser;
    import t = nts.uk.time;

    interface WData<T = string> {
        code: T;
        name: T;
    }

    export interface ScheduleData extends c.DataInfo {
        wtype: WData;
        wtime: WData;
        value: {
            begin: string | number | null;
            finish: string | number | null;
            required: WORKTIME_SETTING;
            validate: boolean;
        };
        state: StateEdit<EDIT_STATE>;
        comfirmed: boolean;
        classification: WORK_STYLE | null;
        achievement: boolean | null;
        need2Work: boolean;
    }

    export interface ObserverScheduleData<R = any> extends c.DataInfo {
        $raw: R;
        wtype: WData<KnockoutObservable<string | null>>;
        wtime: WData<KnockoutObservable<string | null>>;
        value: {
            begin: KnockoutObservable<string | number | null>;
            finish: KnockoutObservable<string | number | null>;
            validate: KnockoutObservable<boolean>;
            required: KnockoutObservable<WORKTIME_SETTING>;
        };
        state: StateEdit;
        confirmed: KnockoutObservable<boolean>;
        classification: KnockoutObservable<WORK_STYLE | null>;
        achievement: KnockoutObservable<boolean | null>;
        need2Work: KnockoutObservable<boolean>;
    }

    export interface StateEdit<T = KnockoutObservable<EDIT_STATE>> {
        wtype: T;
        wtime: T;
        value: {
            begin: T;
            finish: T;
        }
    }

    export enum EDIT_STATE {
        // 手修正（本人）
        HAND_CORRECTION_MYSELF = 0,
        // 手修正（他人）
        HAND_CORRECTION_OTHER = 1,
        // 申請反映
        REFLECT_APPLICATION = 2,
        // 打刻反映
        IMPRINT = 3
    }

    export enum WORK_STYLE {
        // １日出勤系
        FULL_TIME = 3,
        // 午前出勤系
        MORNING = 1,
        // 午後出勤系
        AFTERNOON = 2,
        // １日休日系
        HOLIDAY = 0
    }

    const COMPONENT_NAME = 'scheduler';

    const API_VALID = '/screen/ksu/ksu002/checkTimeIsIncorrect';

    @handler({
        bindingName: COMPONENT_NAME,
        validatable: true,
        virtual: false
    })
    export class SchedulerComponentBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => c.DayData<ObserverScheduleData>[], allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
            const name = COMPONENT_NAME;
            const schedules = valueAccessor();
            const mode = allBindingsAccessor.get('mode');
            const width = allBindingsAccessor.get('width');
            const baseDate = allBindingsAccessor.get('baseDate');
            const clickCell = allBindingsAccessor.get('click-cell');
            const changeCell = allBindingsAccessor.get('change-cell');
            const tabIndex = element.getAttribute('tabindex') || allBindingsAccessor.get('tabindex') || '1';
            const params = { width, baseDate, schedules, clickCell, tabIndex };
            const component = { name, params };

            element.classList.add('cf');
            element.classList.add('scheduler');
            element.removeAttribute('tabindex');

            const binding = bindingContext
                .extend({
                    $change: changeCell,
                    $tabindex: tabIndex,
                    $editable: ko.computed({
                        read: () => {
                            return ko.unwrap(mode) === 'edit';
                        }
                    })
                });

            ko.applyBindingsToNode(element, { component }, binding);

            return { controlsDescendantBindings: true };
        }
    }

    @component({
        name: COMPONENT_NAME,
        template: `<div data-bind="
                calendar: $component.data.schedules,
                baseDate: $component.data.baseDate,
                width: $component.data.width,
                tabindex: $component.data.tabIndex,
                click-cell: $component.data.clickCell
            "></div>
            <div class="calendar cf">
                <div class="filter cf">&nbsp;</div>
                <div class="calendar-container">
                    <div class="month title">
                        <div class="week cf">
                            <div class="day">
                                <div class="status" data-bind="i18n: 'KSU002_24'"></div>
                            </div>
                        </div>
                    </div>
                    <div class="month">
                        <div class="week cf">
                            <div class="day">
                                <div class="status wk-hours">
                                    <span data-bind="i18n: 'KSU002_25'"></span>
                                    <span data-bind="i18n: 'KSU002_35'"></span>
                                </div>
                                <div class="data-info">&nbsp;</div>
                            </div>
                            <div class="day">
                                <div class="status wk-hours full-height">
                                    <span data-bind="i18n: 'KSU002_26'"></span>
                                </div>
                                <div class="data-info">&nbsp;</div>
                            </div>
                        </div>
                        <!-- ko foreach: [1, 2, 3, 4, 5] -->
                        <div class="week cf">
                            <div class="day">
                                <div class="status">
                                    <span>&nbsp;</span>
                                </div>
                                <div class="data-info">&nbsp;</div>
                            </div>
                            <div class="day">
                                <div class="status">
                                    <span>&nbsp;</span>
                                </div>
                                <div class="data-info">&nbsp;</div>
                            </div>
                        </div>
                        <!-- /ko -->
                    </div>
                </div>
            </div>
            <style type="text/css" rel="stylesheet">
                .scheduler .data-info {
                    min-height: 48px !important;
                }
                .scheduler .data-info .work-type .join,
                .scheduler .data-info .work-type .leave {
                    overflow: hidden;
                    border-bottom: 1px dashed #b9b9b9;
                }
                .scheduler .data-info .work-type .join,
                .scheduler .data-info .work-time .join {
                    border-right: 1px dashed #b9b9b9;
                }
                .scheduler .data-info .work-type .join,
                .scheduler .data-info .work-type .leave,
                .scheduler .data-info .work-time .join,
                .scheduler .data-info .work-time .leave {
                    color: #000;
                    float: left;
                    width: 50%;
                    height: 24px;
                    line-height: 23px;
                    font-size: 12px;
                    text-align: center;
                    box-sizing: border-box;
                    white-space: nowrap;
                    outline: none;
                }
                .scheduler .data-info .join *,
                .scheduler .data-info .leave *{
                    display: block;
                    width: 100%;
                    height: 100%;
                    box-sizing: border-box;                    
                }
                .scheduler .join input,
                .scheduler .leave input {
                    color: #000;
                    padding: 0 !important;
                    text-align: center !important;
                    font-size: 13px !important;
                    border-radius: 0 !important;
                    border: 0 !important;
                    cursor: pointer;
                    background-color: transparent;
                    position: relative;
                    z-index: 2;
                }
                .scheduler .join:focus input,
                .scheduler .leave:focus input,
                .scheduler .join[data-click="1"] input,
                .scheduler .leave[data-click="1"] input {
                    color: #fff;
                    box-shadow: 0px 0px 0px 2px #000 !important;
                    background-color: #007fff !important;
                    z-index: 3;
                }
                .scheduler .ntsControl input:focus {
                    z-index: 3;
                    color: #000;
                    box-shadow: 0px 0px 0px 2px #000 !important;
                    background-color: #fff !important;
                }
                .scheduler .ntsControl.error input {
                    color: #000;
                    box-shadow: 0px 0px 0px 2px #ff6666 !important;
                    background-color: transparent !important;
                }
                .scheduler .ntsControl.error input:focus,
                .scheduler .join:focus .ntsControl.error input,
                .scheduler .leave:focus .ntsControl.error input,
                .scheduler .join[data-click="1"] .ntsControl.error input,
                .scheduler .leave[data-click="1"] .ntsControl.error input {
                    z-index: 3;
                    color: #000;
                    box-shadow: 0px 0px 0px 2px #ff6666 !important;
                    background-color: #007fff !important;                    
                }
                .scheduler .calendar {
                    float: left;
                    display: block;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.reflected-wtype:not(.confirmed):not(.achievement) .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.reflected-wtime:not(.confirmed):not(.achievement) .data-info .work-type .leave,
                .scheduler .calendar .calendar-container .month .week .day.same-month.reflected-wtime-begin:not(.confirmed):not(.achievement) .data-info .work-time .join input,
                .scheduler .calendar .calendar-container .month .week .day.same-month.reflected-wtime-finish:not(.confirmed):not(.achievement) .data-info .work-time .leave input {
                    background-color: #bfea60;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.other-alter-wtype:not(.confirmed):not(.achievement) .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.other-alter-wtime:not(.confirmed):not(.achievement) .data-info .work-type .leave,
                .scheduler .calendar .calendar-container .month .week .day.same-month.other-alter-wtime-begin:not(.confirmed):not(.achievement) .data-info .work-time .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.other-alter-wtime-finish:not(.confirmed):not(.achievement) .data-info .work-time .leave {
                    background-color: #cee6ff;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.self-alter-wtype:not(.confirmed):not(.achievement) .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.self-alter-wtime:not(.confirmed):not(.achievement) .data-info .work-type .leave,
                .scheduler .calendar .calendar-container .month .week .day.same-month.self-alter-wtime-begin:not(.confirmed):not(.achievement) .data-info .work-time .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.self-alter-wtime-finish:not(.confirmed):not(.achievement) .data-info .work-time .leave {
                    background-color: #94b7fe;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.confirmed .data-info {
                    background-color: #eccefb;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.need-2work .data-info {
                    background-color: #ddddd2;                    
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.achievement .data-info {
                    background-color: #ddddd2;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-holiday .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-holiday .data-info .work-type .leave {
                    color: #f00;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-morning .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-morning .data-info .work-type .leave,
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-afternoon .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-afternoon .data-info .work-type .leave {
                    color: #FF7F27;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-fulltime .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.classification-fulltime .data-info .work-type .leave {
                    color: #0000ff;
                }
                .scheduler .calendar .calendar-container .month .week .day.same-month.achievement .data-info .work-type .join,
                .scheduler .calendar .calendar-container .month .week .day.same-month.achievement .data-info .work-type .leave,
                .scheduler .calendar .calendar-container .month .week .day.same-month.achievement .data-info .work-time input {
                    color: #008000;
                }
                .scheduler .calendar+.calendar {
                    width: 201px;
                }
                .scheduler .calendar+.calendar .calendar-container {
                    border-left: 0;
                }
                .scheduler .calendar+.calendar .calendar-container .month .week:not(:first-child) .day {
                    border-right: 0;
                }
                .scheduler .calendar+.calendar .calendar-container .month .week:not(:first-child) .day:not(:last-child) .data-info {
                    border-right: 1px solid #808080;
                }
                .scheduler .calendar+.calendar .filter {
                    line-height: 35px;
                }
                .scheduler .calendar+.calendar .month.title .day {
                    width: 100% !important;
                }
                .scheduler .calendar+.calendar .month+.month .day {
                    height: 86px !important;
                }
                .scheduler .calendar+.calendar .month+.month .day .status {
                    display: block;
                    height: 38px;
                    background-color: #d9d9d9;
                    box-sizing: border-box;
                    border-bottom: 1px solid #808080;
                    padding: 0 20px;
                }
                .scheduler .calendar+.calendar .month+.month .day .status.wk-hours {
                    overflow: hidden;
                    background-color: #FFC91D;
                }
                .scheduler .calendar+.calendar .month+.month .day .status.full-height>span {
                    font-size: 12px;
                    line-height: 37px;
                }
            </style>`
    })
    export class ShedulerComponent extends ko.ViewModel {

        constructor(private data: c.Parameter) {
            super();
        }

        created() {
            const vm = this;
            const { data } = vm;

            data.schedules
                .subscribe((days) => {
                    days
                        .forEach((c) => {
                            const b: any = c.binding;

                            c.binding = {
                                ...(b || {}),
                                daisy: 'scheduler-event',
                                dataInfo: 'scheduler-data-info'
                            };
                        });
                });
        }
    }

    export module controls {
        const CLBC = 'clearByCode';
        const MSG_439 = 'Msg_439';
        const MSG_1811 = 'Msg_1811';
        const MSG_1772 = 'Msg_1772';
        const MSG_2058 = 'Msg_2058';
        const VALIDATE = 'validate';
        const COMPONENT_NAME = 'scheduler-data-info';

        @handler({
            bindingName: COMPONENT_NAME,
            validatable: true,
            virtual: false
        })
        export class DataInfoComponentBindingHandler implements KnockoutBindingHandler {
            init(element: HTMLElement, valueAccessor: () => c.DayData<ObserverScheduleData>, _allBindingsAccessor: KnockoutAllBindingsAccessor, _viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
                const name = COMPONENT_NAME;
                const dayData = valueAccessor();
                const params = { dayData, context: bindingContext };
                const component = { name, params };

                const { data, className } = dayData;

                if (data) {
                    const { state } = data;

                    ko.computed({
                        read: () => {
                            const comfirmed = ko.unwrap(data.confirmed);

                            if (comfirmed) {
                                className.push(c.COLOR_CLASS.CONFIRMED);
                            } else {
                                className.remove(c.COLOR_CLASS.CONFIRMED);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const achievement = ko.unwrap(data.achievement);

                            if (achievement) {
                                className.push(c.COLOR_CLASS.ACHIEVEMENT);
                            } else {
                                className.remove(c.COLOR_CLASS.ACHIEVEMENT);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const need2Work = ko.unwrap(data.need2Work);

                            if (!need2Work) {
                                className.push(c.COLOR_CLASS.NEED2WORK);
                            } else {
                                className.remove(c.COLOR_CLASS.NEED2WORK);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const classification = ko.unwrap(data.classification);

                            if (classification === WORK_STYLE.AFTERNOON) {
                                className.push(c.COLOR_CLASS.CLASSIFICATION_AFTERNOON);
                            } else {
                                className.remove(c.COLOR_CLASS.CLASSIFICATION_AFTERNOON);
                            }

                            if (classification === WORK_STYLE.FULL_TIME) {
                                className.push(c.COLOR_CLASS.CLASSIFICATION_FULLTIME);
                            } else {
                                className.remove(c.COLOR_CLASS.CLASSIFICATION_FULLTIME);
                            }

                            if (classification === WORK_STYLE.HOLIDAY) {
                                className.push(c.COLOR_CLASS.CLASSIFICATION_HOLIDAY);
                            } else {
                                className.remove(c.COLOR_CLASS.CLASSIFICATION_HOLIDAY);
                            }

                            if (classification === WORK_STYLE.MORNING) {
                                className.push(c.COLOR_CLASS.CLASSIFICATION_MORNING);
                            } else {
                                className.remove(c.COLOR_CLASS.CLASSIFICATION_MORNING);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const wtype = ko.unwrap(state.wtype);

                            if (wtype === EDIT_STATE.IMPRINT) {
                                className.push(c.COLOR_CLASS.IMPRINT_WTYPE);
                            } else {
                                className.remove(c.COLOR_CLASS.IMPRINT_WTYPE);
                            }

                            if (wtype === EDIT_STATE.REFLECT_APPLICATION) {
                                className.push(c.COLOR_CLASS.REFLECTED_WTYPE);
                            } else {
                                className.remove(c.COLOR_CLASS.REFLECTED_WTYPE);
                            }

                            if (wtype === EDIT_STATE.HAND_CORRECTION_MYSELF) {
                                className.push(c.COLOR_CLASS.SELF_ALTER_WTYPE);
                            } else {
                                className.remove(c.COLOR_CLASS.SELF_ALTER_WTYPE);
                            }

                            if (wtype === EDIT_STATE.HAND_CORRECTION_OTHER) {
                                className.push(c.COLOR_CLASS.OTHER_ALTER_WTYPE);
                            } else {
                                className.remove(c.COLOR_CLASS.OTHER_ALTER_WTYPE);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const wtime = ko.unwrap(state.wtime);

                            if (wtime === EDIT_STATE.IMPRINT) {
                                className.push(c.COLOR_CLASS.IMPRINT_WTIME);
                            } else {
                                className.remove(c.COLOR_CLASS.IMPRINT_WTIME);
                            }

                            if (wtime === EDIT_STATE.REFLECT_APPLICATION) {
                                className.push(c.COLOR_CLASS.REFLECTED_WTIME);
                            } else {
                                className.remove(c.COLOR_CLASS.REFLECTED_WTIME);
                            }

                            if (wtime === EDIT_STATE.HAND_CORRECTION_MYSELF) {
                                className.push(c.COLOR_CLASS.SELF_ALTER_WTIME);
                            } else {
                                className.remove(c.COLOR_CLASS.SELF_ALTER_WTIME);
                            }

                            if (wtime === EDIT_STATE.HAND_CORRECTION_OTHER) {
                                className.push(c.COLOR_CLASS.OTHER_ALTER_WTIME);
                            } else {
                                className.remove(c.COLOR_CLASS.OTHER_ALTER_WTIME);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const begin = ko.unwrap(state.value.begin);

                            if (begin === EDIT_STATE.IMPRINT) {
                                className.push(c.COLOR_CLASS.IMPRINT_WTIME_BEGIN);
                            } else {
                                className.remove(c.COLOR_CLASS.IMPRINT_WTIME_BEGIN);
                            }

                            if (begin === EDIT_STATE.REFLECT_APPLICATION) {
                                className.push(c.COLOR_CLASS.REFLECTED_WTIME_BEGIN);
                            } else {
                                className.remove(c.COLOR_CLASS.REFLECTED_WTIME_BEGIN);
                            }

                            if (begin === EDIT_STATE.HAND_CORRECTION_MYSELF) {
                                className.push(c.COLOR_CLASS.SELF_ALTER_WTIME_BEGIN);
                            } else {
                                className.remove(c.COLOR_CLASS.SELF_ALTER_WTIME_BEGIN);
                            }

                            if (begin === EDIT_STATE.HAND_CORRECTION_OTHER) {
                                className.push(c.COLOR_CLASS.OTHER_ALTER_WTIME_BEGIN);
                            } else {
                                className.remove(c.COLOR_CLASS.OTHER_ALTER_WTIME_BEGIN);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });

                    ko.computed({
                        read: () => {
                            const finish = ko.unwrap(state.value.finish);

                            if (finish === EDIT_STATE.IMPRINT) {
                                className.push(c.COLOR_CLASS.IMPRINT_WTIME_FINISH);
                            } else {
                                className.remove(c.COLOR_CLASS.IMPRINT_WTIME_FINISH);
                            }

                            if (finish === EDIT_STATE.REFLECT_APPLICATION) {
                                className.push(c.COLOR_CLASS.REFLECTED_WTIME_FINISH);
                            } else {
                                className.remove(c.COLOR_CLASS.REFLECTED_WTIME_FINISH);
                            }

                            if (finish === EDIT_STATE.HAND_CORRECTION_MYSELF) {
                                className.push(c.COLOR_CLASS.SELF_ALTER_WTIME_FINISH);
                            } else {
                                className.remove(c.COLOR_CLASS.SELF_ALTER_WTIME_FINISH);
                            }

                            if (finish === EDIT_STATE.HAND_CORRECTION_OTHER) {
                                className.push(c.COLOR_CLASS.OTHER_ALTER_WTIME_FINISH);
                            } else {
                                className.remove(c.COLOR_CLASS.OTHER_ALTER_WTIME_FINISH);
                            }
                        },
                        owner: dayData,
                        disposeWhenNodeIsRemoved: element
                    });
                }

                ko.applyBindingsToNode(element, { component }, bindingContext);

                return { controlsDescendantBindings: true };
            }
        }

        @component({
            name: COMPONENT_NAME,
            template: `
            <div class="work-type cf">
                <div class="join" data-bind="i18n: text.wtype"></div>
                <div class="leave" data-bind="i18n: text.wtime"></div>
            </div>
            <div class="work-time cf">
                <div class="join">
                    <input class="begin" tabindex="-1" data-bind="
                        ntsTimeWithDayEditor: {
                            name: $component.$i18n('KSU002_28'),
                            constraint: 'TimeWithDayAttr',
                            mode: 'time',
                            inputFormat: 'time',
                            value: $component.model.begin,
                            required: $component.model.required,
                            option: {
                                timeWithDay: false
                            }
                        }" />
                </div>
                <div class="leave">
                    <input class="finish" tabindex="-1" data-bind="
                        ntsTimeWithDayEditor: {
                            name: $component.$i18n('KSU002_29'),
                            constraint: 'TimeWithDayAttr',
                            mode: 'time',
                            inputFormat: 'time',
                            value: $component.model.finish,
                            required: $component.model.required,
                            option: {
                                timeWithDay: false
                            }
                        }" />
                </div>
            </div>
            `
        })
        export class DataInfoComponent extends ko.ViewModel {
            model: WorkTimeRange & {
                required: KnockoutObservable<boolean>;
            } = {
                    begin: ko.observable(null),
                    finish: ko.observable(null),
                    required: ko.observable(false)
                };

            text: {
                wtype: KnockoutObservable<string>;
                wtime: KnockoutObservable<string>;
            } = {
                    wtime: ko.observable(''),
                    wtype: ko.observable('')
                };

            enable!: KnockoutComputed<boolean>;

            constructor(private data: { dayData: c.DayData<ObserverScheduleData>; context: BindingContext }) {
                super();

                this.enable = ko.computed({
                    read: () => {
                        const { dayData, context } = data;

                        return context.$editable()
                            && !!dayData.data.wtime.code()
                            && !(dayData.data.confirmed() || dayData.data.achievement() || !dayData.data.need2Work())
                            && dayData.data.classification() !== WORK_STYLE.HOLIDAY
                            && dayData.data.value.required() === WORKTIME_SETTING.REQUIRED;
                    },
                    owner: this
                });
            }

            created() {
                const vm = this;
                const { data, model, text } = vm;
                const { context, dayData } = data;
                const cache: { begin: string | number | null; finish: string | number | null; } = { begin: null, finish: null };

                if (dayData.data) {
                    const { data } = dayData;
                    const { wtype, wtime, value } = data;

                    text.wtype = wtype.name;
                    text.wtime = wtime.name;

                    ko.computed({
                        read: () => {
                            model.required(ko.unwrap(value.required) === WORKTIME_SETTING.REQUIRED || !!ko.unwrap(wtime.code));
                        },
                        owner: vm
                    });

                    value.begin
                        .subscribe((b) => {
                            const $begin = $(vm.$el).find('input.begin');
                            const $finish = $(vm.$el).find('input.finish');

                            $.Deferred()
                                .resolve(true)
                                .then(() => $begin.ntsError('clear'))
                                .then(() => {
                                    if (model.begin() !== b) {
										try {
                                        	model.begin(b);
										} catch (e) {
											$begin.val(b);
											$begin.trigger(VALIDATE);
										}
                                    } else {
										$begin.trigger(VALIDATE);
                                        model.begin.valueHasMutated();
                                    }

                                    cache.begin = b;
                                })
                                .then(() => $begin.trigger(VALIDATE))
                                .then(() => {
                                    if ($finish.ntsError('hasError')) {
                                        $finish.trigger(VALIDATE);
                                    }
                                })
                                .then(() => value.validate(!$begin.ntsError('hasError') && !$finish.ntsError('hasError')))
                                .then(() => {
                                    $begin
                                        .prop('disabled', true)
                                        .attr('disabled', 'disabled');

                                    $finish
                                        .prop('disabled', true)
                                        .attr('disabled', 'disabled');
                                });
                        });

                    value.begin.valueHasMutated();

                    value.finish
                        .subscribe((f) => {
                            const $begin = $(vm.$el).find('input.begin');
                            const $finish = $(vm.$el).find('input.finish');

                            $.Deferred()
                                .resolve(true)
                                .then(() => $finish.ntsError('clear'))
                                .then(() => {
                                    if (model.finish() !== f) {
										try {
                                        	model.finish(f);
										} catch (e) {
											$finish.val(f);
											$finish.trigger(VALIDATE);
										}
                                    } else {
										$finish.trigger(VALIDATE);
                                        model.finish.valueHasMutated();
                                    }

                                    cache.finish = f;
                                })
                                .then(() => $finish.trigger(VALIDATE))
                                .then(() => {
                                    if ($begin.ntsError('hasError')) {
                                        $begin.trigger(VALIDATE);
                                    }
                                })
                                .then(() => value.validate(!$begin.ntsError('hasError') && !$finish.ntsError('hasError')))
                                .then(() => {
                                    $begin
                                        .prop('disabled', true)
                                        .attr('disabled', 'disabled');

                                    $finish
                                        .prop('disabled', true)
                                        .attr('disabled', 'disabled');
                                });
                        });

                    value.finish.valueHasMutated();

                    model.begin
                        .subscribe((b: number | string | null) => {
                            if (cache.begin !== b && ko.unwrap(value.begin) !== b) {
                                cache.begin = b;

                                const clone: c.DayData<ScheduleData> = ko.toJS(dayData);

                                clone.data.value.begin = b;

                                context.$change.apply(context.$vm, [clone]);
                            }
                        });

                    model.finish
                        .subscribe((f: number | string | null) => {
                            if (cache.finish !== f && ko.unwrap(value.finish) !== f) {
                                cache.finish = f;
                                value.validate(true);

                                const clone: c.DayData<ScheduleData> = ko.toJS(dayData);

                                clone.data.value.finish = f;

                                context.$change.apply(context.$vm, [clone]);
                            }
                        });
                }
            }

            mounted() {
                const vm = this;

                vm.initValidate();

                $(vm.$el).find('[data-bind]').removeAttr('data-bind');
            }

            initValidate() {
                const vm = this;
                const { model, enable, data } = vm;
                // get fullText of TimeWithDay
                const twd = (t as any).minutesBased.clock.dayattr.create;

                if (!data || !data.dayData || !data.dayData.data) {
                    return;
                }

                const { wtype, wtime } = data.dayData.data;

                const $join = $(vm.$el).find('.work-time div.join');
                const $begin = $(vm.$el).find('.work-time input.begin');

                const $leave = $(vm.$el).find('.work-time div.leave');
                const $finish = $(vm.$el).find('.work-time input.finish');

                const validate = () => {
                    const b = ko.unwrap(model.begin);
                    const f = ko.unwrap(model.finish);
                    const workTypeCode = ko.unwrap(wtype.code);
                    const workTimeCode = ko.unwrap(wtime.code);

                    if (ko.unwrap(enable)) {
                        if (_.isNumber(b) && _.isNumber(f)) {
                            if (b >= f) {
                                if (!$begin.ntsError('hasError')) {
                                    $begin.ntsError('set', { messageId: MSG_1811 });
                                }

                                if (!$finish.ntsError('hasError')) {
                                    $finish.ntsError('set', { messageId: MSG_1811 });
                                }
                            } else {
                                const command = {
                                    workTypeCode,
                                    workTimeCode,
                                    startTime: b,
                                    endTime: f
                                };

                                vm.$ajax(API_VALID, command)
                                    .then((resp: ContaintError[]) => {
                                        const [start, end] = resp;

                                        $.Deferred()
                                            .resolve(true)
                                            .then(() => {
                                                $begin
                                                    .ntsError(CLBC, MSG_439)
                                                    .ntsError(CLBC, MSG_1772)
                                                    .ntsError(CLBC, MSG_1811)
                                                    .ntsError(CLBC, MSG_2058);

                                                $finish
                                                    .ntsError(CLBC, MSG_439)
                                                    .ntsError(CLBC, MSG_1772)
                                                    .ntsError(CLBC, MSG_1811)
                                                    .ntsError(CLBC, MSG_2058);
                                            })
                                            .then(() => {
                                                if (start) {
                                                    const { check, timeSpan } = start;

                                                    if (!check) {
                                                        if (!timeSpan) {
                                                            $begin.ntsError('set', { messageId: MSG_439, messageParams: [vm.$i18n('KDL045_12')] });
                                                        } else {
                                                            const { endTime, startTime } = timeSpan;

                                                            if (startTime === endTime) {
                                                                $begin.ntsError('set', { messageId: MSG_2058, messageParams: [vm.$i18n('KSU001_54'), twd(startTime).fullText] });
                                                            } else {
                                                                $begin.ntsError('set', { messageId: MSG_1772, messageParams: [vm.$i18n('KSU001_54'), twd(startTime).fullText, twd(endTime).fullText] });
                                                            }
                                                        }
                                                    }
                                                }

                                                if (end) {
                                                    const { check, timeSpan } = end;

                                                    if (!check) {
                                                        if (!timeSpan) {
                                                            $finish.ntsError('set', { messageId: MSG_439, messageParams: [vm.$i18n('KDL045_12')] });
                                                        } else {
                                                            const { endTime, startTime } = timeSpan;

                                                            if (startTime === endTime) {
                                                                $finish.ntsError('set', { messageId: MSG_2058, messageParams: [vm.$i18n('KSU001_55'), twd(startTime).fullText] });
                                                            } else {
                                                                $finish.ntsError('set', { messageId: MSG_1772, messageParams: [vm.$i18n('KSU001_55'), twd(startTime).fullText, twd(endTime).fullText] });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                    });
                            }
                        } else {
                            $begin.ntsError(CLBC, MSG_1811);
                            $finish.ntsError(CLBC, MSG_1811);
                        }
                    } else {
                        $begin.ntsError('clear');
                        $finish.ntsError('clear');
                    }
                };

                $begin.on(VALIDATE, validate);

                $finish.on(VALIDATE, validate);

                setTimeout(() => {
                    vm.bindingEvent($begin, $join);
                    vm.bindingEvent($finish, $leave);
                }, 300);
            }

            bindingEvent($input: JQuery, $container: JQuery) {
                const vm = this;
                const { data, enable } = vm;
                const { context } = data;
                const { $tabindex } = context;

                $container
                    .on('mousedown', () => {
                        $container.attr('data-mouse-down', 'true');
                    })
                    .on('keyup', (evt: JQueryEventObject) => {
                        if ($container.attr('data-focus') === 'true') {
                            return;
                        }

                        if ([13, 37, 39].indexOf(evt.keyCode) > -1) {
                            const $focusables = $container
                                .closest('.calendar-container')
                                .find('.work-time div.join[tabindex], .work-time div.leave[tabindex]');

                            const [current] = $focusables
                                .toArray()
                                .map((e: HTMLElement, i: number) => $container.is(e) ? i : -1)
                                .filter((v) => v !== -1);

                            if (evt.keyCode === 37 || (evt.shiftKey && evt.keyCode === 13)) {
                                if (current > 0) {
                                    $($focusables.get(current - 1)).focus();
                                } else {
                                    $focusables.last().focus();
                                }

                                return;
                            }

                            if (current < $focusables.length - 1) {
                                $($focusables.get(current + 1)).focus();
                            } else {
                                $focusables.first().focus();
                            }

                            return;
                        }

                        // F2 key press (edit mode)
                        if (evt.keyCode === 113) {
                            $container
                                .attr('data-click', 2)
                                .removeAttr('tabindex')
                                .attr('data-focus', 'true');

                            // continues
                            $.Deferred()
                                .resolve($input)
                                .then(() => {
                                    $input
                                        .removeAttr('disabled')
                                        .prop('disabled', false);
                                })
                                .then(() => {
                                    $input.focus();
                                })
                                .then(() => {
                                    $input.select();
                                });

                            return;
                        }

                        // [0-9] key press (edit mode with value)
                        if ((48 <= evt.keyCode && evt.keyCode <= 57) || (96 <= evt.keyCode && evt.keyCode <= 105)) {
                            $container
                                .attr('data-click', 2)
                                .removeAttr('tabindex')
                                .attr('data-focus', 'true');

                            // continues
                            $.Deferred()
                                .resolve($input)
                                .then(() => {
                                    $input
                                        .removeAttr('disabled')
                                        .prop('disabled', false);
                                })
                                .then(() => {
                                    $input.focus();
                                })
                                .then(() => $input.val(evt.key));

                            return;
                        }
                    })
                    .on('focus', () => {
                        if ($container.attr('data-mouse-down') !== 'true') {
                            if ($container.attr('data-click') === '0') {
                                $container.attr('data-click', 1);
                            }
                        }

                        $container.removeAttr('data-mouse-down');
                    })
                    .on('click', () => {
                        $container.removeAttr('data-mouse-down');

                        const click: string = $container.attr('data-click');

                        if (!enable()) {
                            return;
                        }

                        $container
                            .closest('.calendar-container')
                            .find('.join[data-click="1"], .leave[data-click="1"], .join[data-click="2"], .leave[data-click="2"]')
                            .each((__: number, e: HTMLElement) => {
                                if ($container.not(e)) {
                                    $(e).trigger('blur');
                                }
                            });

                        if (click === '1') {
                            $container
                                .attr('data-click', 2)
                                .removeAttr('tabindex')
                                .attr('data-focus', 'true');

                            // continues
                            $input
                                .removeAttr('disabled')
                                .prop('disabled', false)
                                .focus();

                            return;
                        }

                        if (click === '0') {
                            $container
                                .attr('data-click', 1);

                            if (b.version.match(/IE/)) {
                                // fix focus in IE
                                $container
                                    .focus();
                            }
                        }

                        $input
                            .prop('disabled', true)
                            .attr('disabled', 'disabled');
                    })
                    .on('blur', () => {
                        const fc = $container.attr('data-focus');
                        const md = $container.attr('data-mouse-down');

                        // if $input isn't focus
                        if (md !== 'true' && fc === 'false') {
                            $input
                                .prop('disabled', true)
                                .attr('disabled', 'disabled');

                            $container
                                .attr('data-click', '0');

                            if (!ko.unwrap(enable)) {
                                $container
                                    .removeAttr('tabindex');
                            } else {
                                $container
                                    .attr('tabindex', $tabindex);
                            }
                        }
                    })
                    .attr('data-click', 0)
                    .attr('data-focus', 'false');

                $input
                    .on('keydown', (evt) => {
                        if (evt.keyCode === 13) {
                            $.Deferred()
                                .resolve()
                                .then(() => {
                                    $input
                                        .trigger('blur');
                                })
                                .then(() => {
                                    $container.focus();
                                    /*const $focusables = $container
                                        .closest('.calendar-container')
                                        .find('.work-time div.join[tabindex], .work-time div.leave[tabindex]');

                                    const [current] = $focusables
                                        .toArray()
                                        .map((e: HTMLElement, i: number) => $container.is(e) ? i : -1)
                                        .filter((v) => v !== -1);

                                    if (evt.shiftKey) {
                                        if (current > 0) {
                                            const prev = $focusables.get(current - 1);

                                            if (prev) {
                                                $(prev).focus();
                                            } else {
                                                $container.focus();
                                            }
                                        }
                                    } else {
                                        if (current < $focusables.length - 1) {
                                            const next = $focusables.get(current + 1);

                                            if (next) {
                                                $(next).focus();
                                            } else {
                                                $container.focus();
                                            }
                                        }
                                    }*/
                                });

                            return;
                        }

                        $container
                            .attr('data-key', evt.key)
                            .attr('data-shift', evt.shiftKey + '')
                            .attr('data-key-code', evt.keyCode);
                    })
                    .on('blur', () => {
                        $input
                            .disableSelection()
                            .prop('disabled', true)
                            .attr('disabled', 'disabled')
                            .enableSelection();

                        $container
                            .attr('data-click', 0)
                            .attr('data-focus', 'false');

                        if (!ko.unwrap(enable)) {
                            $container
                                .removeAttr('tabindex');
                        } else {
                            $container
                                .attr('tabindex', $tabindex);
                        }

                        // fix tab in IE
                        if ($container.attr('data-key-code') === '9') {
                            const $focusables = $container
                                .closest('.calendar-container')
                                .find('.work-time div.join[tabindex], .work-time div.leave[tabindex]');

                            const [current] = $focusables
                                .toArray()
                                .map((e: HTMLElement, i: number) => $container.is(e) ? i : -1)
                                .filter((v) => v !== -1);

                            if ($container.attr('data-shift') === 'true') {
                                if (current > 0) {
                                    const prev = $focusables.get(current - 1);

                                    if (prev) {
                                        $(prev).focus();
                                    } else {
                                        $focusables.last().focus();
                                    }
                                }
                            } else {
                                if (current < $focusables.length - 1) {
                                    const next = $focusables.get(current + 1);

                                    if (next) {
                                        $(next).focus();
                                    } else {
                                        $focusables.first().focus();
                                    }
                                }
                            }
                        }

                        $container
                            .removeAttr('data-key')
                            .removeAttr('data-shift')
                            .removeAttr('data-key-code');
                    })
                    .on('click', (evt) => {
                        evt.stopPropagation();
                    })
                    .prop('disabled', true)
                    .attr('disabled', 'disabled');

                ko.computed(() => {
                    const editable = vm.enable();

                    $input
                        .prop('disabled', true)
                        .attr('disabled', 'disabled');

                    if (!editable) {
                        $container.removeAttr('tabindex');
                    } else {
                        $container.attr('tabindex', $tabindex);
                    }
                });
            }
        }

        type INPUT_TYPE = 'begin' | 'finish';

        interface WorkTimeRange<T = string | number | null> {
            begin: KnockoutObservable<T>;
            finish: KnockoutObservable<T>;
        }

        interface BindingContext extends KnockoutBindingContext {
            $vm: any,
            $change: Function,
            $tabindex: string | number;
            $editable: KnockoutReadonlyComputed<boolean>;
        }

        interface ContaintError {
            // 含まれているか
            check: boolean;
            nameError: string;
            timeInput: string;
            // 時間帯
            timeSpan: {
                startTime: number;
                endTime: number;
            };
            endTime: number;
            startTime: number;
            workNo1: boolean;
        }
    }
}