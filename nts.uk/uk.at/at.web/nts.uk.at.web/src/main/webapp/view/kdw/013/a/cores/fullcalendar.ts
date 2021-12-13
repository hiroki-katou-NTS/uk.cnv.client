/// <reference path="../../../../../lib/generic/fullcalendar/index.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/daygrid.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/timegrid.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/interaction.d.ts" />

module nts.uk.ui.at.kdw013.calendar {
    const { randomId } = nts.uk.util;
    const { version } = nts.uk.util.browser;
    const { getTimeOfDate, getTask, getBackground, getTitles } = at.kdw013.share;

    type Calendar = FullCalendar.Calendar;
    export type EventApi = Partial<FullCalendar.EventApi>;
    type EventContentArg = FullCalendar.EventContentArg;
    type CustomButtonInput = FullCalendar.CustomButtonInput;

    type EventClickArg = FullCalendar.EventClickArg;
    type EventDropArg = FullCalendar.EventDropArg;
    type EventDragStopArg = FullCalendar.EventDragStopArg;
    type EventDragStartArg = FullCalendar.EventDragStartArg;

    type EventResizeStartArg = FullCalendar.EventResizeStartArg;
    type EventResizeDoneArg = FullCalendar.EventResizeDoneArg;

    type DateSelectArg = FullCalendar.DateSelectArg;
    type EventRemoveArg = FullCalendar.EventRemoveArg;
    type EventReceiveLeaveArg = FullCalendar.EventReceiveLeaveArg;
    type DayHeaderContentArg = FullCalendar.DayHeaderContentArg;
    type SlotLabelContentArg = FullCalendar.SlotLabelContentArg;
    type DateRangeInput = FullCalendar.DateRangeInput;

    type JQueryEvent = JQueryEventObject & { originalEvent: WheelEvent & { wheelDelta: number; } };

    type EventSlim = {
        start: Date;
        end: Date;
    };

    type Style = 'breaktime' | 'selectday';

    type EventStatus = 'new' | 'add' | 'update' | 'delete' | 'normal';

    export type EventRaw = EventSlim & {
        title: string;
        backgroundColor: string;
        textColor: string;
        extendedProps: Record<string, any> & {
            id: string;
            status: EventStatus;
            description: string;
            sId: string;
            workCD1: string;
            workCD2: string;
            workCD3: string;
            workCD4: string;
            workCD5: string;
            workLocationCD: string;
            remarks: string;
        };
    };

    const CM2KBC = /([a-z0-9]|(?=[A-Z]))([A-Z])/g;
    const toKebabCase = (s: string) => s.replace(CM2KBC, '$1-$2').toLowerCase();

    const GROUP_ID = 'groupId';
    const BORDER_COLOR = 'borderColor';
    const BLACK = '#000';
    const TRANSPARENT = 'transparent';
    const SELECTED = 'selected';
    const BACKGROUND = 'background';
    const DURATION_EDITABLE = 'durationEditable';

    const E_COMP_NAME = 'fc-editor';
    const S_COMP_NAME = 'fc-setting';
    const COMPONENT_NAME = 'fullcalendar';
    const POWNER_CLASS_CPY = 'popup-owner-copy';
    const POWNER_CLASS_EVT = 'popup-owner-event';
    const DEFAULT_STYLES = `
        body.fc-unselectable li.fc-event-dragging{
            list-style: none;
            display: none;
        }
        .fc-container {
            position: relative;
            overflow: hidden;
        }
        .fc-container.resizer {
            cursor: col-resize;
            -webkit-touch-callout: none; /* iOS Safari */
            -webkit-user-select: none; /* Safari */
            -khtml-user-select: none; /* Konqueror HTML */
            -moz-user-select: none; /* Old versions of Firefox */
                -ms-user-select: none; /* Internet Explorer/Edge */
                    user-select: none; /* Non-prefixed version, currently
                                        supported by Chrome, Edge, Opera and Firefox */
        }
        .fc-container .fc-sidebar {
            float: left;
            width: 210px;
            min-width: 210px;
            max-width: calc(100vw - 755px);
            overflow: hidden;
            margin-right: 10px;
            box-sizing: border-box;
            border-right: 1px solid #ccc;
            position: relative;
            padding-right: 1px;
            min-height: calc(100vh - 162px);
        }
        .fc-container .fc-sidebar>div {
            padding: 0 10px;
        }
        .fc-container .fc-sidebar>div>h3 {
            margin-left: -10px;
            font-weight: 700;
        }
        .fc-container.resizer .fc-sidebar {
            padding-right: 0;
            border-right: 2px solid #aaa;
        }
        .fc-container.resizer .fc-sidebar:before,
        .fc-container.resizer .fc-sidebar:after {
            content: '';
            position: absolute;
            display: block;
            width: 1px;
            right: 4px;
            top: calc(50% - 11px);
            height: 22px;
            border-right: 2px solid #aaa;
        }
        .fc-container.resizer .fc-sidebar:after {
            right: 1px;
            top: calc(50% - 16px);
            height: 32px;
        }
        .fc-container .fc-sidebar .fc-events,
        .fc-container .fc-sidebar.view-mode .fc-employees:not(:first-child) {
            margin-top: 5px;
            border-top: 1px solid #ddd;
        }
        .fc-container .fc-sidebar .fc-events>ul,
        .fc-container .fc-sidebar .fc-employees>ul {
            overflow-x: hidden;
        }
        .fc-container .fc-sidebar .fc-events>ul,
        .fc-container .fc-sidebar .fc-employees>ul {
            border: 1px solid #ccc;
            border-radius: 3px;
            height: 112px;
        }
        .fc-container .fc-sidebar .fc-employees>ul.list-employee {
            height: 224px;
        }
        .fc-container .fc-sidebar .fc-events>ul {
            height: 140px;
        }
        .fc-container .fc-sidebar .fc-employees>.ui-igcombo-wrapper {
            width: 100%;
        }
        .fc-container .fc-sidebar .fc-events>ul>li,
        .fc-container .fc-sidebar .fc-employees>ul>li {
            padding: 3px;
            cursor: pointer;
        }
        .fc-container .fc-sidebar .fc-employees>ul>li.selected {
            background-color: #ccc;
        }
        .fc-container .fc-sidebar .fc-events>ul>li>div,
        .fc-container .fc-sidebar .fc-employees>ul>li>div {
            overflow: hidden;
        }
        .fc-container .fc-sidebar .fc-events>ul>li>div {
            line-height: 22px;
        }
        .fc-container .fc-sidebar .fc-employees>ul>li>div {
            line-height: 16px;
        }
        .fc-container .fc-sidebar .fc-events>ul>li>div:first-child {
            float: left;
            width: 22px;
            height: 22px;
            margin-right: 3px;
            border-radius: 50%;
        }
        .fc-container .fc-sidebar .fc-employees>ul>li>div:first-child {
            float: left;
            min-width: 70px;
            padding-right: 7px;
        }
        .fc-container .fc-sidebar .fc-events>ul>li>div:not(:first-child),
        .fc-container .fc-sidebar .fc-employees>ul>li>div:not(:first-child) {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .fc-container .fc-toolbar.fc-header-toolbar {
            min-height: 33px;
            margin-bottom: 10px;
        }
        .fc-container .fc-timegrid thead>tr>td td:first-child {
            font-size: 11px;
            vertical-align: middle;
        }
        .fc-container .fc-timegrid thead>tr>td td:first-child,
        .fc-container .fc-timegrid thead>tr>td tr:last-child td {
            text-align: center;
        }
        .fc-container .fc-timegrid thead>tr>td {
            border-bottom: 3px solid #ddd;
        }
        .fc-container .fc-timegrid-body {
            -webkit-touch-callout: none; /* iOS Safari */
            -webkit-user-select: none; /* Safari */
            -khtml-user-select: none; /* Konqueror HTML */
            -moz-user-select: none; /* Old versions of Firefox */
            -ms-user-select: none; /* Internet Explorer/Edge */
            user-select: none; /* Non-prefixed version, currently
                                        supported by Chrome, Edge, Opera and Firefox */
        }
        .fc-container:not(.ie) .fc-timegrid thead td .fc-scroller {
            overflow: hidden !important;
        }
        .fc-container .fc .fc-list-sticky .fc-list-day>th {
            z-index: 1;
        }
        .fc-container .fc-v-event {
            overflow: hidden;
            border-width: 2px;
        }
        .fc-container .fc-scrollgrid table {
            border-right-style: solid;
            border-bottom-style: hidden;
        }
        .fc-container .fc-button-group button {
            min-width: 32px;
        }
        .fc-container .fc-button-group button:focus {
            z-index: 2;
        }
        .fc-container .fc-button-group button:not(:last-child) {
            border-top-right-radius: 0px;
            border-bottom-right-radius: 0px;
        }
        .fc-container .fc-button-group button:not(:first-child) {
            margin-left: -1px;
            border-top-left-radius: 0px;
            border-bottom-left-radius: 0px;
        }
        .fc-container .fc-button-group>.nts-datepicker-wrapper>input.nts-input {
            width: 110px;
            height: 33px;
            border-radius: 0px;
        }
        .fc-container .fc-header-toolbar .fc-settings-button {
            width: 34px;
        }
        .fc-container .fc-timegrid-slot-label-bold {
            font-weight: bold;
        }
        .fc-container .fc-timegrid-slot-lane-even,
        .fc-container .fc-timegrid-slot-label-even {
            background-color: #d9e3f4;
        }
        
        .fc-container .fc-day-today {
            background-color: #ffff00;
        }

        .fc-timegrid-cols .fc-day-today {
            background-color: white !important;
        }
        .fc-container .fc-day-sat .fc-col-header-cell-cushion{
            color: #0086EA;
        }
        .fc-container .fc-day-sun .fc-col-header-cell-cushion {
            color: #FF2D2D;
        }
        .fc-container .fc-event-title h4 {
            margin: 0;
            padding: 0;
        }
        .fc-container .fc-event-description {
            margin-top: 10px;
        }
        .fc-container .fc-event-note.no-data {
            background-color: #f3f3f3;
        }
        .fc-container .fc-event-note>div {
            padding: 2px;
            min-height: 112px;
            overflow: hidden;
        } 
        .fc-container .fc-event-note>div>div{
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
        }
        .fc-container .fc-popup-editor {
            position: fixed;
            border-radius: 3px;
            -webkit-box-shadow: 1px 1px 5px 2px #888;
            box-shadow: 1px 1px 5px 2px #888;
            width: 0px;
            height: 0px;
            background-color: #fff;
            z-index: 99;
            visibility: hidden;
            -webkit-transition: opacity 200ms ease-in-out;
            transition: opacity 200ms ease-in-out;
            opacity: 0;
            box-sizing: border-box;
        }
        .fc-container .fc-popup-editor.show {
            visibility: visible;
            width: 250px;
            height: 270px;
            padding: 10px;
            opacity: 1;
        }
        .fc-container .fc-popup-editor>div {
            display: inline-block;
        }
        .fc-container .fc-popup-editor .toolbar {
            text-align: right;
        }
        .fc-container .fc-popup-editor .toolbar svg {
            fill: #5f6368;
        }
        .fc-container .fc-popup-editor .toolbar svg:last-child {
            margin-left: 10px;
        }
        .fc-container .fc-popup-editor .toolbar svg:not(:last-child) {
            margin-right: 10px;
        }
        .fc-container .fc-one-day-button,
        .fc-container .fc-five-day-button,
        .fc-container .fc-full-week-button,
        .fc-container .fc-full-month-button,
        .fc-container .fc-list-week-button {
            min-width: 60px !important;
        }
        .fc-container .fc-one-day-button.active,
        .fc-container .fc-five-day-button.active,
        .fc-container .fc-full-week-button.active,
        .fc-container .fc-full-month-button.active,
        .fc-container .fc-list-week-button.active {
            color: #fff;
            background-color: #00B050;
        }
        .fc-container .fc-events.tree-list {
            cursor: pointer;
            height: 240px;
            margin-top: 10px;
            padding: 5px 0 5px 5px;
            overflow: hidden auto;
            box-sizing: border-box;
            border: 1px solid #ccc;
        }
        .fc-container .fc-col-header-cell.fc-day:not(.fc-day-disabled) {
            cursor: pointer;
        }
        .fc-container .fc-col-header-cell.fc-day:not(.fc-day-disabled):hover {
            background-color: #fffadf;
        }`;

    @handler({
        bindingName: COMPONENT_NAME,
        validatable: false,
        virtual: false
    })
    export class FullCalendarBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservableArray<EventRaw>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
            const name = COMPONENT_NAME;

            const events = valueAccessor();

            const params = { events, ...allBindingsAccessor(), viewModel };
            const component = { name, params };

            ko.applyBindingsToNode(element, { component }, bindingContext);

            element.classList.add('fc-container');
            element.classList.add('cf');
            element.removeAttribute('data-bind');

            return { controlsDescendantBindings: true };
        }
    }

    type PopupPosition = {
        event: KnockoutObservable<null | HTMLElement>;
        setting: KnockoutObservable<null | HTMLElement>;
    }

    type DataEventStore = {
        alt: KnockoutObservable<boolean>;
        ctrl: KnockoutObservable<boolean>;
        shift: KnockoutObservable<boolean>;
        mouse: KnockoutObservable<boolean>;
        delete: KnockoutObservable<boolean>;
        target: KnockoutObservable<TargetElement>;
        pointer: KnockoutObservable<{ screenX: number; screenY: number; }>;
    };

    type TargetElement = 'event' | 'date' | null;

    type J_EVENT = (evt: JQueryEventObject) => void;
    type GlobalEvent = { [key: string]: J_EVENT[]; };

    type CalendarLocale = 'en' | 'ja' | 'vi';

    type SlotDuration = 5 | 10 | 15 | 30;
    const durations: SlotDuration[] = [5, 10, 15, 30];

    enum DayOfWeek {
        SUN = 0,
        MON = 1,
        TUE = 2,
        WED = 3,
        THU = 4,
        FRI = 5,
        SAT = 6
    }

    export type BussinessTime = {
        startTime: number;
        endTime: number;
    };

    export type BreakTime = BussinessTime & {
        backgroundColor: string;
    };

    export type BussinessHour = BussinessTime & {
        daysOfWeek: DayOfWeek[];
    };

    export type AttendanceTime = {
        date: Date;
        events: string[];
    };

    export type Employee = {
        id: string;
        code: string;
        name: string;
        selected: boolean;
    };

    export type InitialView = 'oneDay' | 'fiveDay' | 'listWeek' | 'fullWeek' | 'fullMonth';
    type ButtonView = 'one-day' | 'five-day' | 'list-week' | 'full-week' | 'full-month';

    type ButtonSet = { [name: string]: CustomButtonInput; };

    type ComponentParameters = {
        viewModel: any;
        events: EventRaw[] | KnockoutObservableArray<EventRaw>;
        employee: KnockoutObservable<string>;
        confirmers: KnockoutObservableArray<Employee>;
        locale: CalendarLocale | KnockoutObservable<CalendarLocale>;
        initialView: InitialView | KnockoutObservable<InitialView>;
        availableView: InitialView[] | KnockoutObservableArray<InitialView>;
        initialDate: Date | KnockoutObservable<Date>;
        scrollTime: number | KnockoutObservable<number>;
        slotDuration: SlotDuration | KnockoutObservable<SlotDuration>;
        editable: boolean | KnockoutObservable<boolean>;
        firstDay: DayOfWeek | KnockoutObservable<DayOfWeek>;
        attendanceTimes: AttendanceTime[] | KnockoutObservableArray<AttendanceTime>;
        breakTime: BreakTime | KnockoutObservable<undefined | BreakTime>;
        businessHours: BussinessHour[] | KnockoutObservableArray<BussinessHour>;
        validRange: Partial<DatesSet> | KnockoutObservable<Partial<DatesSet>>;
        event: {
            datesSet: (start: Date, end: Date) => void;
        };
        components: {
            view: string;
            editor: string;
        }
        $datas: KnockoutObservable<a.ChangeDateDto | null>;
        $settings: KnockoutObservable<a.StartProcessDto | null>;
    };

    type PopupData = {
        event: KnockoutObservable<null | EventApi>;
        setting: SettingApi;
        excludeTimes: KnockoutObservableArray<BussinessTime>;
    };

    type SettingApi = {
        firstDay: KnockoutObservable<DayOfWeek>;
        scrollTime: KnockoutObservable<number>;
        slotDuration: KnockoutObservable<SlotDuration>;
    };

    export type DatesSet = {
        start: Date;
        end: Date;
    };

    type SettingStore = {
        firstDay: DayOfWeek;
        scrollTime: number;
        slotDuration: SlotDuration;
        InitialView:string;
    };

    const DATE_FORMAT = 'YYYY-MM-DD';
    const ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:00';

    const formatDate = (date: Date, format: string = ISO_DATE_FORMAT) => moment(date).format(format);
    const formatTime = (time: number) => {
        const f = Math.floor;
        const times = [f(time / 60), f(time % 60), 0]

        return times
            .map((m) => m.toString())
            .map((m) => _.padStart(m, 2, '0'))
            .join(':')
    };
    const activeClass = (t: EventTarget) => {
        $(t)
            .closest('.fc-button-group')
            .find('button')
            .removeClass('active');

        $(t).addClass('active');
    };

    const defaultDEvent = (): DataEventStore => ({
        alt: ko.observable(false),
        ctrl: ko.observable(false),
        shift: ko.observable(false),
        mouse: ko.observable(false),
        delete: ko.observable(false),
        target: ko.observable(null),
        pointer: ko.observable({ screenX: -1, screenY: -1 })
    });

    const defaultPopupData = (): PopupData => ({
        event: ko.observable(null),
        setting: {
            firstDay: ko.observable(0),
            scrollTime: ko.observable(420),
            slotDuration: ko.observable(30),
            initialView : ko.observable('oneDay')
        },
        excludeTimes: ko.observableArray([])
    });

    const defaultPPosition = (): PopupPosition => ({
        setting: ko.observable(null),
        event: ko.observable(null)
    });

    const dayOfView = (view: InitialView) => {
        switch (view) {
            case 'fiveDay':
                return 5;
            default:
            case 'fullMonth':
            case 'listWeek':
                return 0;
            case 'fullWeek':
                return 7;
            case 'oneDay':
                return 1;
        }
    };

    const storeSetting = (setting?: SettingStore): JQueryPromise<SettingStore | undefined> => {
        const vm = new ko.ViewModel();

        return vm.$window
            .storage('KDW013_SETTING', setting)
            .then((value: any) => value);
    };

    @component({
        name: COMPONENT_NAME,
        template: `
        <div data-bind="
                mode: $component.params.editable,
                view: $component.$view,
                mutated: $component.subscribeEvent,
                fc-editor: $component.popupData.event,
                position: $component.popupPosition.event,
                components: $component.params.components,
                exclude-times: $component.popupData.excludeTimes,
                mouse-pointer: $component.dataEvent.pointer,
                $settings: $component.params.$settings
            "></div>
        <div data-bind="
                fc-setting: $component.popupData.setting,
                position: $component.popupPosition.setting
            "></div>
        <div class="fc-sidebar" data-bind="css: {
                    'edit-mode': ko.unwrap($component.params.editable),
                    'view-mode': !ko.unwrap($component.params.editable)
                }">
            <div class="fc-employees" data-bind="
                    kdw013-department: 'kdw013-department',
                    mode: $component.params.editable,
                    employee: $component.params.employee,
                    initialDate: $component.params.initialDate,
                    $settings: $component.params.$settings
                "></div>
            <div class="fc-employees confirmer" data-bind="
                    kdw013-approveds: 'kdw013-approveds',
                    mode: $component.params.editable,
                    confirmers: $component.params.confirmers,
                    initialDate: $component.params.initialDate,
                    $settings: $component.params.$settings
                "></div>
            <div class="fc-events" data-bind="
                    kdw013-events: 'kdw013-events',
                    mode: $component.params.editable,
                    items: $component.dragItems,
                    $settings: $component.params.$settings
                "></div>
        </div>
        <div class="fc-calendar"></div>
        <style>${DEFAULT_STYLES}</style>
        <style data-bind="html: $component.$style"></style>`
    })
    export class FullCalendarComponent extends ko.ViewModel {
        // Fullcalendar instance
        public calendar!: Calendar;

        // stored all global events
        public events: GlobalEvent = {};

        // stored glodal date events
        public dataEvent: DataEventStore = defaultDEvent();

        // store popup data
        public popupData: PopupData = defaultPopupData();

        // store popup state
        public popupPosition: PopupPosition = defaultPPosition();

        public selectedEvents: EventSlim[] = [];

        public dragItems: KnockoutObservableArray<EventRaw> = ko.observableArray([]);

        // view or edit popup
        public $view: KnockoutObservable<'view' | 'edit'> = ko.observable('view');

        public $style!: KnockoutReadonlyComputed<string>;

        public $styles: KnockoutObservable<{ [key: string]: string }> = ko.observable({});

        public subscribeEvent: KnockoutObservable<null> = ko.observable(null);

        constructor(private params: ComponentParameters) {
            super();

            /**
             * Prevent exception of params (merge params with default options)
             */
            if (!params) {
                this.params = {
                    viewModel: this,
                    scrollTime: ko.observable(360),
                    locale: ko.observable('ja'),
                    firstDay: ko.observable(1),
                    slotDuration: ko.observable(30),
                    editable: ko.observable(false),
                    initialView: ko.observable('oneDay'),
                    availableView: ko.observableArray([]),
                    initialDate: ko.observable(new Date()),
                    events: ko.observableArray([]),
                    employee: ko.observable(''),
                    confirmers: ko.observableArray([]),
                    attendanceTimes: ko.observableArray([]),
                    breakTime: ko.observable(null),
                    businessHours: ko.observableArray([]),
                    validRange: ko.observable({}),
                    event: {
                        datesSet: (__: Date, ___: Date) => { }
                    },
                    components: {
                        view: 'kdp013b',
                        editor: 'kdp013c'
                    },
                    $datas: ko.observable(null),
                    $settings: ko.observable(null)
                };
            }

            const { setting } = this.popupData;

            const {
                locale,
                event,
                components,
                events,
                employee,
                confirmers,
                scrollTime,
                initialDate,
                initialView,
                availableView,
                editable,
                firstDay,
                slotDuration,
                attendanceTimes,
                breakTime,
                businessHours,
                $datas,
                $settings
            } = this.params;

            if (locale === undefined) {
                this.params.locale = ko.observable('ja');
            }

            if (scrollTime === undefined) {
                this.params.scrollTime = ko.observable(360);
            }

            if (initialDate === undefined) {
                this.params.initialDate = ko.observable(new Date());
            }

            if (initialView === undefined) {
                this.params.initialView = ko.observable('oneDay');
            }

            if (availableView === undefined) {
                this.params.availableView = ko.observableArray([]);
            }

            if (editable === undefined) {
                this.params.editable = ko.observable(false);
            }

            if (firstDay === undefined) {
                this.params.firstDay = ko.observable(1);
            }

            if (slotDuration === undefined) {
                this.params.slotDuration = ko.observable(30);
            }

            if (events === undefined) {
                this.params.events = ko.observableArray([]);
            }

            if (employee === undefined) {
                this.params.employee = ko.observable('');
            }

            if (confirmers === undefined) {
                this.params.confirmers = ko.observableArray([]);
            }

            if (attendanceTimes === undefined) {
                this.params.attendanceTimes = ko.observableArray([]);
            }

            if (breakTime === undefined) {
                this.params.breakTime = ko.observable(undefined);
            }

            if (businessHours === undefined) {
                this.params.businessHours = ko.observableArray([]);
            }

            if (event === undefined) {
                this.params.event = {
                    datesSet: (__: Date, ___: Date) => { }
                };
            }

            const { datesSet } = this.params.event;

            if (datesSet === undefined) {
                this.params.event.datesSet = (__: Date, ___: Date) => { };
            }

            if (components === undefined) {
                this.params.components = {
                    view: 'kdp013b',
                    editor: 'kdp013c'
                };
            }

            if (this.params.components.view === undefined) {
                this.params.components.view = 'kdp013b';
            }

            if (this.params.components.editor === undefined) {
                this.params.components.editor = 'kdp013c';
            }

            if (!ko.isObservable(this.params.firstDay)) {
                setting.firstDay(this.params.firstDay);
            } else {
                setting.firstDay = this.params.firstDay;
            }

            if (!ko.isObservable(this.params.scrollTime)) {
                setting.scrollTime(this.params.scrollTime);
            } else {
                setting.scrollTime = this.params.scrollTime;
            }

            if (!ko.isObservable(this.params.slotDuration)) {
                setting.slotDuration(this.params.slotDuration);
            } else {
                setting.slotDuration = this.params.slotDuration;
            }

            this.$style = ko.computed({
                read: () => {
                    return _.values(ko.unwrap(this.$styles)).join('\n');
                }
            });
        }

        public mounted() {
            const vm = this;
            const {
                params,
                dataEvent,
                dragItems,
                popupData,
                popupPosition,
                subscribeEvent,
            } = vm;
            const {
                locale,
                event,
                events,
                scrollTime,
                firstDay,
                editable,
                initialDate,
                initialView,
                availableView,
                viewModel,
                validRange,
                attendanceTimes,
                $datas,
                $settings
            } = params;
            const $caches: {
                new: KnockoutObservable<EventApi | null>;
                drag: KnockoutObservable<EventApi | null>;
            } = {
                new: ko.observable(null),
                drag: ko.observable(null)
            };

            const $el = $(vm.$el);
            const $dg = $el.find('div.fc-events').get(0);
            const $fc = $el.find('div.fc-calendar').get(0);
            const FC: FullCalendar.FullCalendar | null = _.get(window, 'FullCalendar', null);
            const updateActive = () => {
                $el
                    .find('.fc-header-toolbar button')
                    .each((__, e) => {
                        e.classList.remove('active');
                        e.classList.remove('fc-button');
                        e.classList.remove('fc-button-primary');

                        const view = ko.unwrap(initialView);

                        if (e.className.match(toKebabCase(view))) {
                            e.classList.add('active');
                        }
                    });

                const current = moment().startOf('day');
                const { start, end } = ko.unwrap(datesSet) || { start: new Date(), end: new Date() };

                const $btn = $el.find('.fc-current-day-button');

                if (!current.isBetween(start, end, 'date', '[)')) {
                    $btn.removeAttr('disabled');
                } else {
                    $btn.attr('disabled', 'disabled');
                }
            };

            const weekends: KnockoutObservable<boolean> = ko.observable(true);
            const datesSet: KnockoutObservable<DatesSet | null> = ko.observable(null).extend({ deferred: true, rateLimit: 100 });

            // emit date range to viewmodel
            datesSet.subscribe((ds) => {
                const { start, end } = ds;

                event.datesSet.apply(viewModel, [start, end]);
            });

            // calculate time on header
            const timesSet: KnockoutComputed<({ date: string | null; value: number | null; })[]> = ko.computed({
                read: () => {
                    const ds = ko.unwrap(datesSet);
                    const wd = ko.unwrap(firstDay);
                    const iv = ko.unwrap(initialView);
                    const evts = ko.unwrap<EventRaw[]>(events);
                    const cache = ko.unwrap<EventApi>($caches.new);
                    const { start, end } = cache || { start: null, end: null };
                    const nkend = moment(start).clone().add(1, 'hour').toDate();
                    const duration = moment(end || nkend).diff(start, 'minute');
                    const nday = dayOfView(iv);

                    if (ds) {
                        const { start, end } = ds;

                        const first = moment(start);
                        const diff: number = moment(end).diff(start, 'day');
                        const mkend = first.clone().add(1, 'hour').toDate();

                        const days = _.range(0, diff, 1)
                            .map(m => {
                                const date = first.clone().add(m, 'day');
                                const exists = _.filter([...evts], (d: EventApi) => {
                                    return !d.allDay &&
                                        d.display !== 'background' &&
                                        date.isSame(d.start, 'date');
                                });

                                return {
                                    date: date.format(DATE_FORMAT),
                                    value: exists.reduce((p, c) => p += moment(c.end || mkend).diff(c.start, 'minute'), date.isSame(nkend, 'date') ? duration : 0)
                                };
                            });

                        const sow = first.clone().isoWeekday(wd).isSame(start, 'date');

                        while (days.length < nday) {
                            if (sow) {
                                days.push({ date: null, value: null });
                            } else {
                                days.unshift({ date: null, value: null })
                            }
                        }

                        return days;
                    }

                    return [];
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            const attendancesSet: KnockoutComputed<({ date: string | null; events: string[]; })[]> = ko.computed({
                read: () => {
                    const ds = ko.unwrap<DatesSet>(datesSet);
                    const wd = ko.unwrap(firstDay);
                    const iv = ko.unwrap(initialView);
                    const nday = dayOfView(iv);
                    const ads = ko.unwrap<AttendanceTime[]>(attendanceTimes);

                    if (!ds) {
                        return [];
                    }

                    const { start, end } = ds;

                    const first = moment(start);
                    const diff: number = moment(end).diff(start, 'day');

                    const days = _.range(0, diff, 1)
                        .map(m => {
                            const date = first.clone().add(m, 'day');
                            const exist = _.find(ads, (d: AttendanceTime) => date.isSame(d.date, 'date'));

                            if (exist) {
                                const { events } = exist;

                                return { date: date.format(DATE_FORMAT), events };
                            }

                            return { date: date.format(DATE_FORMAT), events: [] };
                        });

                    const sow = first.clone().isoWeekday(wd).isSame(start, 'date');

                    while (days.length < nday) {
                        if (sow) {
                            days.push({ date: null, events: [] });
                        } else {
                            days.unshift({ date: null, events: [] });
                        }
                    }

                    return days;
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            const computedView = ko.computed({
                read: () => {
                    const iv = ko.unwrap(initialView);

                    updateActive();

                    switch (iv) {
                        case 'oneDay':
                            weekends(true);
                            return 'timeGridDay';
                        default:
                        case 'fiveDay':
                            weekends(false);
                            return 'timeGridWeek';
                        case 'fullWeek':
                            weekends(true);
                            return 'timeGridWeek';
                        case 'fullMonth':
                            weekends(true);
                            return 'dayGridMonth';
                        case 'listWeek':
                            weekends(true);
                            return 'listWeek';
                    }
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            const computedDragItems = (datas: a.ChangeDateDto | null, settings: a.StartProcessDto | null) => {
                if (datas && settings) {
                    const { workGroupDtos } = datas;
                    const { startManHourInputResultDto } = settings;

                    if (workGroupDtos && startManHourInputResultDto) {
                        const { tasks } = startManHourInputResultDto;

                        if (tasks && tasks.length) {
                            const draggers: EventRaw[] = _
                                .chain(workGroupDtos)
                                .map((wg) => {
                                    const task = getTask(wg, tasks);
                                    const { workCD1, workCD2, workCD3, workCD4, workCD5 } = wg;

                                    if (!task) {
                                        return null;
                                    }

                                    const relateId = randomId();
                                    const {
                                        taskFrameNo,
                                        code,
                                        cooperationInfo,
                                        displayInfo,
                                        expirationEndDate,
                                        expirationStartDate,
                                        childTaskList
                                    } = task;

                                    return {
                                        start: new Date(),
                                        end: new Date(),
                                        title: getTitles(wg, tasks, '/'),
                                        backgroundColor: getBackground(wg, tasks),
                                        textColor: '',
                                        extendedProps: {
                                            relateId,
                                            status: 'new',
                                            taskFrameNo,
                                            code,
                                            cooperationInfo,
                                            displayInfo,
                                            expirationEndDate,
                                            expirationStartDate,
                                            childTaskList,
                                            workCD1,
                                            workCD2,
                                            workCD3,
                                            workCD4,
                                            workCD5,
                                            remarks: ''
                                        } as any
                                    };
                                })
                                .filter((m) => !!m)
                                .value();

                            // update dragger items
                            vm.dragItems(draggers);

                            return;
                        }
                    }
                }

                vm.dragItems([]);
            }

            dataEvent.alt
                .subscribe((c) => $el.attr('alt', +c));

            dataEvent.ctrl
                .subscribe((c) => $el.attr('ctrl', +c));

            dataEvent.shift
                .subscribe((c) => $el.attr('shift', +c));

            dataEvent.alt.valueHasMutated();
            dataEvent.ctrl.valueHasMutated();
            dataEvent.shift.valueHasMutated();

            popupData.event
                .subscribe((evt) => { });

            // hide popup event day
            popupPosition.event
                .subscribe(e => {
                    if (!e) {
                        vm.$view('view');
                        $(`.${POWNER_CLASS_EVT}`).removeClass(POWNER_CLASS_EVT);
                    }
                });

            // hide popup setting day
            popupPosition.setting
                .subscribe(c => {
                    if (!c) {
                        $(`.${POWNER_CLASS_CPY}`).removeClass(POWNER_CLASS_CPY);
                    }
                });

            $

            // update drag item
            $datas
                .subscribe((data: a.ChangeDateDto | null) => {
                    computedDragItems(data, ko.unwrap($settings));
                });

            // update drag item
            $settings
                .subscribe((settings: a.StartProcessDto | null) => {
                    computedDragItems(ko.unwrap($datas), settings);
                });
            //update initialView to storage
            initialView.subscribe(view => {

                storeSetting()
                .then((value) => {
                    value = value ? value : { initialView: view };
                    value.initialView = view;
                storeSetting(value);
                });
            });
            // fix ie display
            if (version.match(/IE/)) {
                $el.addClass('ie');
            }

            // check instance of fullcalendar
            if (!FC || !FC.Calendar) {
                const pre = document.createElement('pre');
                const prettify = 'xml';
                const code = '<com:stylefile set="FULLCALENDAR" />\n<com:scriptfile set="FULLCALENDAR" />';

                $el.append('Please add 2 tag at below to htmlHead ui defined:');

                // pretty message as html
                ko.applyBindingsToNode(pre, { prettify, code });

                // append message to root element
                $el.append(pre);

                return;
            }

            const clearSelection = () => {
                vm.selectedEvents = [];

                // clear selections
                _.each(vm.calendar.getEvents(), (e: EventApi) => {
                    if (e.borderColor === BLACK) {
                        e.setProp(GROUP_ID, '');

                        e.setProp(BORDER_COLOR, TRANSPARENT);
                        e.setProp(DURATION_EDITABLE, true);
                    }
                });
            };

            const viewButtons: ButtonSet = {
                'one-day': {
                    text: vm.$i18n('KDW013_10'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'timeGridDay') {
                            activeClass(evt.target);

                            weekends(true);
                            $.Deferred()
                                .resolve(true)
                                .then(() => {
                                    if (ko.isObservable(initialView)) {
                                        initialView('oneDay');
                                    } else {
                                        vm.calendar.changeView('timeGridDay');
                                    }
                                })
                                .then(() => {
                                    if (version.match(/IE/)) {
                                        vm.calendar.destroy();
                                    }
                                })
                                .then(() => {
                                    if (version.match(/IE/)) {
                                        vm.calendar.render();
                                    }
                                });
                        }
                    }
                },
                'five-day': {
                    text: vm.$i18n('稼働日'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'timeGridWeek' || ko.unwrap(weekends) !== false) {
                            activeClass(evt.target);

                            weekends(false);
                            if (ko.isObservable(initialView)) {
                                initialView('fiveDay');
                            } else {
                                vm.calendar.changeView('timeGridWeek');
                            }
                        }
                    }
                },
                'full-week': {
                    text: vm.$i18n('KDW013_11'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'timeGridWeek' || ko.unwrap(weekends) !== true) {
                            activeClass(evt.target);

                            weekends(true);
                            if (ko.isObservable(initialView)) {
                                initialView('fullWeek');
                            } else {
                                vm.calendar.changeView('timeGridWeek');
                            }
                        }
                    }
                },
                'full-month': {
                    text: vm.$i18n('月'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'dayGridMonth') {
                            activeClass(evt.target);

                            weekends(true);
                            if (ko.isObservable(initialView)) {
                                initialView('fullMonth');
                            } else {
                                vm.calendar.changeView('dayGridMonth');
                            }
                        }
                    }
                },
                'list-week': {
                    text: vm.$i18n('一覧表'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'listWeek') {
                            activeClass(evt.target);

                            weekends(true);
                            if (ko.isObservable(initialView)) {
                                initialView('listWeek');
                            } else {
                                vm.calendar.changeView('listWeek');
                            }
                        }
                    }
                }
            };
            const customButtons: ButtonSet = {
                'current-day': {
                    text: vm.$i18n('今日'),
                    click: () => {
                        clearSelection();

                        if (ko.isObservable(initialDate)) {
                            initialDate(new Date());
                        } else {
                            vm.calendar.gotoDate(formatDate(new Date()));
                        }
                    }
                },
                'next-day': {
                    text: vm.$i18n('▶'),
                    click: () => {
                        clearSelection();

                        if (ko.isObservable(initialDate)) {
                            const date = ko.unwrap(initialDate);
                            const view = ko.unwrap(initialView);
                            const vrange = ko.unwrap(validRange);
                            const { end } = vrange;

                            switch (view) {
                                case 'oneDay':
                                    const day = moment(date).add(1, 'day');

                                    if (end) {
                                        if (day.isBefore(end, 'date')) {
                                            initialDate(day.toDate());
                                        }
                                    } else {
                                        initialDate(day.toDate());
                                    }
                                    break;
                                default:
                                case 'fiveDay':
                                case 'fullWeek':
                                case 'listWeek':
                                    const nextDay = moment(date).add(1, 'week');

                                    if (end) {
                                        if (nextDay.isSameOrAfter(end, 'date')) {
                                            initialDate(moment(end).subtract(1, 'day').toDate());
                                        } else {
                                            initialDate(nextDay.toDate());
                                        }
                                    } else {
                                        initialDate(nextDay.toDate());
                                    }
                                    break;
                                case 'fullMonth':
                                    const nextMonth = moment(date).add(1, 'month');

                                    if (end) {
                                        if (nextMonth.isSameOrAfter(end, 'date')) {
                                            initialDate(moment(end).subtract(1, 'day').toDate());
                                        } else {
                                            initialDate(nextMonth.toDate());
                                        }
                                    } else {
                                        initialDate(nextMonth.toDate());
                                    }
                                    break;
                            }
                        }
                    }
                },
                'preview-day': {
                    text: vm.$i18n('◀'),
                    click: () => {
                        clearSelection();

                        if (ko.isObservable(initialDate)) {
                            const date = ko.unwrap(initialDate);
                            const view = ko.unwrap(initialView);
                            const vrange = ko.unwrap(validRange);
                            const { start } = vrange;

                            switch (view) {
                                case 'oneDay':
                                    const day = moment(date).subtract(1, 'day');

                                    if (start) {
                                        if (day.clone().add(1, 'day').isAfter(start, 'date')) {
                                            initialDate(day.toDate());
                                        }
                                    } else {
                                        initialDate(day.toDate());
                                    }
                                    break;
                                default:
                                case 'fiveDay':
                                case 'fullWeek':
                                case 'listWeek':
                                    const prevDay = moment(date).subtract(1, 'week');

                                    if (start) {
                                        if (prevDay.isSameOrBefore(start, 'date')) {
                                            initialDate(moment(start).toDate());
                                        } else {
                                            initialDate(prevDay.toDate());
                                        }
                                    } else {
                                        initialDate(prevDay.toDate());
                                    }
                                    break;
                                case 'fullMonth':
                                    const prevMonth = moment(date).subtract(1, 'month');

                                    if (start) {
                                        if (prevMonth.isSameOrAfter(start, 'date')) {
                                            initialDate(moment(start).toDate());
                                        } else {
                                            initialDate(prevMonth.toDate());
                                        }
                                    } else {
                                        initialDate(prevMonth.toDate());
                                    }
                                    break;
                            }
                        }
                    }
                },
                'settings': {
                    text: '',
                    click: (evt) => {
                        const tg: HTMLElement = evt.target as any;

                        if (tg) {
                            tg.classList.add(POWNER_CLASS_CPY);

                            popupPosition.setting(tg);
                        }
                    }
                }
            };

            const headerToolbar: FullCalendar.ToolbarInput = {
                left: 'preview-day,next-day',
                center: 'title',
                right: 'settings'
            };

            const getEvents = (): EventRaw[] => vm.calendar
                // get all event from calendar
                .getEvents()
                // except background event
                .filter(f => f.display !== 'background')
                // map EventApi to EventRaw
                .map(({
                    start,
                    end,
                    title,
                    backgroundColor,
                    textColor,
                    extendedProps
                }) => ({
                    start,
                    end,
                    title,
                    backgroundColor,
                    textColor,
                    extendedProps: extendedProps as any
                }));
            const mutatedEvents = () => {
                if (ko.isObservable(events)) {
                    // emit event except new event (no data)
                    events(getEvents().filter(({ extendedProps }) => !!extendedProps.id && extendedProps.status !== 'delete'));
                }
            };
            const updateEvents = () => {
                const sltds = vm.selectedEvents;
                const isSelected = (m: EventSlim) => _.some(sltds, (e: EventSlim) => formatDate(e.start) === formatDate(m.start));

                const events = ko
                    .unwrap<EventRaw[]>(params.events)
                    .filter(({ extendedProps }) => extendedProps.status !== 'delete')
                    .map((m) => ({
                        ...m,
                        id: randomId(),
                        start: formatDate(m.start),
                        end: formatDate(m.end),
                        [GROUP_ID]: isSelected(m) ? SELECTED : '',
                        [BORDER_COLOR]: isSelected(m) ? BLACK : TRANSPARENT,
                        [DURATION_EDITABLE]: isSelected(m) ? sltds.length < 2 : true,
                        extendedProps: {
                            ...m.extendedProps,
                            status: m.extendedProps.status || 'normal'
                        }
                    }));

                // clear old events
                vm.calendar.removeAllEvents();
                vm.calendar.removeAllEventSources();

                // set new events
                vm.calendar.setOption('events', events);
                // _.each(events, (e: EventRaw) => vm.calendar.addEvent(e));

                vm.selectedEvents = [];
            };
            const removeNewEvent = (event: EventApi | null) => {
                _.each(vm.calendar.getEvents(), (e: EventApi) => {
                    // remove new event (no save)
                    if (!e.title && e.extendedProps.status === 'new' && (!event || e.id !== event.id)) {
                        e.remove();
                    }
                });
            }

            const dragger = new FC.Draggable($dg, {
                itemSelector: '.title',
                eventData: (el) => {
                    const id = el.getAttribute('data-id');
                    const unwraped = ko.unwrap<EventRaw[]>(dragItems);

                    _.each(vm.calendar.getEvents(), (e: EventApi) => {
                        if (e.extendedProps.status === 'new' && !e.extendedProps.id) {
                            e.remove();
                        } else if (e.groupId === SELECTED) {
                            e.setProp(GROUP_ID, '');

                            e.setProp(BORDER_COLOR, TRANSPARENT);
                            e.setProp(DURATION_EDITABLE, true);
                        }
                    });

                    if (id) {
                        const exist = _.find(unwraped, (e: EventRaw) => e.extendedProps.relateId === id);

                        if (exist) {
                            const { title, backgroundColor, extendedProps } = exist;

                            return {
                                title,
                                backgroundColor,
                                borderColor: 'transparent',
                                extendedProps: {
                                    ...extendedProps,
                                    id: randomId(),
                                    status: 'new',
                                    employeeId: vm.$user.employeeId
                                }
                            };
                        }
                    }

                    return null;
                }
            });

            vm.calendar = new FC.Calendar($fc, {
                height: '100px',
                themeSystem: 'default',
                selectMirror: true,
                selectMinDistance: 4,
                nowIndicator: true,
                dayHeaders: true,
                allDaySlot: false,
                slotEventOverlap: false,
                eventOverlap: false,
                selectOverlap: false,
                eventLimit: true, 
                views: {
                    timeGrid: {
                        eventLimit: 20
                    }
                },
                // rerenderDelay: 500,
                dateClick: (info) => {
                    const events = vm.calendar.getEvents();


                    let hasEventNotSave = _.find(events, (e) => !_.get(e, 'extendedProps.id'));

                    if (!hasEventNotSave) {
                        const event = vm.calendar
                            .addEvent({
                                id: randomId(),
                                start: formatDate(info.date),
                                end: formatDate(moment(info.date).add(vm.params.slotDuration(), 'm').toDate()),
                                [BORDER_COLOR]: BLACK,
                                [GROUP_ID]: SELECTED,
                                extendedProps: {
                                    status: 'new'
                                }
                            });

                        $caches.new(event);
                        const el: HTMLElement = vm.$el.querySelector(`[event-id="${event.id}"]`);

                        if (el) {
                            const { view } = vm.calendar;

                            vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view });
                        }
                    } else {
                        _.each(events, (e: EventApi) => {
                            // remove new event (empty data)
                            if (!e.extendedProps.id) {

                                vm.$dialog
                                    .confirm({ messageId: 'Msg_2094' })
                                    .then((v: 'yes' | 'no') => {
                                        if (v === 'yes') {
                                            e.remove();
                                            dataEvent.delete(false);
                                            popupPosition.event(null);
                                            popupPosition.setting(null);
                                        }

                                        dataEvent.delete(false);
                                    });
                            } else if (e.groupId === SELECTED) {
                                e.setProp(GROUP_ID, '');
                                // unselect events
                                e.setProp(BORDER_COLOR, TRANSPARENT);
                                e.setProp(DURATION_EDITABLE, true);
                            }
                        });


                    }
                },
                dropAccept: () => !!ko.unwrap(true),
                dayHeaderContent: (opts: DayHeaderContentArg) => moment(opts.date).format('DD(ddd)'),
                selectAllow: ({ start, end }) => start.getDate() === end.getDate(),
                slotLabelContent: (opts: SlotLabelContentArg) => {
                    const { milliseconds } = opts.time;

                    const min = milliseconds / 60000;
                    const hour = Math.floor(min / 60);
                    const minite = Math.floor(min % 60);

                    return !minite ? `${hour}:00` : `${minite}`;
                },
                slotLabelClassNames: ({ time }) => {
                    const { milliseconds } = time;

                    const min = milliseconds / 60000;
                    const hour = Math.floor(min / 60);
                    const minite = Math.floor(min % 60);

                    return `${!minite ? 'fc-timegrid-slot-label-bold' : ''} fc-timegrid-slot-label-${hour}`;
                },
                slotLaneClassNames: ({ time }) => {
                    const { milliseconds } = time;
                    const min = milliseconds / 60000;
                    const hour = Math.floor(min / 60);
                    const brkt = ko.unwrap(params.breakTime);
                    const className = [`fc-timegrid-slot-lane-${hour}`];

                    // add breaktime class
                    if (brkt) {
                        const { startTime, endTime } = ko.unwrap(params.breakTime);

                        if (startTime <= min && min < endTime) {
                            className.push('fc-timegrid-slot-lane-breaktime');
                        }
                    }

                    return className.join(' ');
                },
                eventContent: (args: EventContentArg) => {
                    const { type } = args.view;
                    const { title, extendedProps } = args.event;
                    const { remarks } = extendedProps;

                    if (['timeGridDay', 'timeGridWeek'].indexOf(type) !== -1) {
                        return {
                            html: `<div class="fc-event-title-container">
                                <div class="fc-event-title fc-sticky"><pre>${title}</pre></div>
                                ${_.isString(remarks) ? remarks.split('\n').map((m: string) => `<div class="fc-event-description fc-sticky">${m}</div>`).join('') : ''}
                            </div>`
                        };
                    }

                    if (type === 'dayGridMonth') {
                        return {
                            html: `<div class="fc-daygrid-event-dot"></div>
                            <div class="fc-event-title"><pre>${title}</pre></div>`
                        };
                    }

                    if (type === 'listWeek') {
                        return {
                            html: `<pre>${title}</pre>
                            ${_.isString(remarks) ? remarks.split('\n').map((m: string) => `<div class="fc-event-description fc-sticky">${m}</div>`).join('') : ''}`
                        };
                    }

                    return undefined;
                },
                viewDidMount: ({ el, view }) => {
                    // render attendence time & total time by ko binding
                    if (['timeGridDay', 'timeGridWeek'].indexOf(view.type) > -1) {
                        const header = $(el).find('thead tbody');

                        if (header.length) {
                            const $days = header.find('tr:first');
                            const _events = document.createElement('tr');
                            const __times = document.createElement('tr');

                            header.append(_events);
                            header.append(__times);

                            $.Deferred()
                                .resolve(true)
                                .then(() => {
                                    $days
                                        // select day event
                                        .on('click', (evt: JQueryEvent) => {
                                            const target = $(evt.target).closest('.fc-col-header-cell.fc-day').get(0) as HTMLElement;

                                            if (target && target.tagName === 'TH' && target.dataset['date']) {
                                                const date = moment.utc(target.dataset['date'], 'YYYY-MM-DD').toDate();

                                                if (_.isDate(date) && ko.isObservable(initialDate)) {
                                                    initialDate(date);
                                                }
                                            }
                                        });
                                })
                                .then(() => {
                                    // binding sum of work time within same day
                                    ko.applyBindingsToNode(__times, { component: { name: 'fc-times', params: timesSet } }, vm);
                                    // binding note for same day
                                    ko.applyBindingsToNode(_events, { component: { name: 'fc-events', params: attendancesSet } }, vm);
                                })
                                .then(() => vm.calendar.setOption('height', '100px'))
                                .then(() => {
                                    // update height
                                    const fce = vm.calendar.el.getBoundingClientRect();

                                    if (fce) {
                                        const { top } = fce;
                                        const  innerHeight  = $('.fc-sidebar').height();

                                        vm.calendar.setOption('height', `${innerHeight }px`);

                                        const sidebar = $('.fc-sidebar').get(0);

                                        if (sidebar) {
                                            ko.applyBindingsToNode(sidebar, { 'sb-resizer': vm.calendar }, vm);
                                        }
                                    }

                                    // add date picker to both next/prev button
                                    const dpker = $('<div>').insertAfter('.fc-preview-day-button').get(0);

                                    if (dpker) {
                                        const startDate = ko.computed({
                                            read: () => {
                                                const { start } = ko.unwrap(validRange);

                                                return start || null;
                                            }
                                        });
                                        const endDate = ko.computed({
                                            read: () => {
                                                const { end } = ko.unwrap(validRange);

                                                if (end) {
                                                    return moment(end).subtract(1, 'day').toDate();
                                                }

                                                return null;
                                            }
                                        });

                                        const value = ko.observable(ko.unwrap(initialDate) || new Date());

                                        value.subscribe((v: Date | null) => {
                                            if (ko.isObservable(initialDate)) {
                                                if (_.isDate(v)) {
                                                    if (!moment(v).isSame(ko.unwrap(initialDate), 'date')) {
                                                        initialDate(v);
                                                    }
                                                } else {
                                                    value(ko.unwrap(initialDate) || new Date());
                                                }
                                            }
                                        });

                                        if (ko.isObservable(initialDate)) {
                                            initialDate.subscribe((d: Date | null) => {
                                                if (_.isDate(d)) {
                                                    if (!moment(d).isSame(ko.unwrap(value), 'date')) {
                                                        value(d);
                                                    }
                                                } else {
                                                    value(new Date());
                                                }
                                            });
                                        }

                                        ko.applyBindingsToNode(dpker, { ntsDatePicker: { value, startDate, endDate } }, vm);
                                    }

                                    const setting = $('.fc-settings-button').get(0);

                                    if (setting) {
                                        ko.applyBindingsToNode(setting, { icon: 3, size: '20px' }, vm);
                                    }
                                })
                                .then(() => {
                                    const sc = ko.unwrap(scrollTime);

                                    vm.calendar.scrollToTime(formatTime(sc));
                                })
                                .then(() => {
                                    $(vm.$el)
                                        .find('.fc-sidebar')
                                        .css({ 'width': '220px' });

                                    vm.calendar.updateSize();
                                });
                        }
                    }
                },
                eventClick: ({ el, event, jsEvent }) => {
                    const shift = ko.unwrap<boolean>(dataEvent.shift);
                    /**
                     * Note: remove group id before change other prop
                     */

                    // remove new event (with no data) & background event
                    removeNewEvent(event);

                    // get all event with border is black
                    const seletions = () => _.filter(vm.calendar.getEvents(), (e: EventApi) => e.borderColor === BLACK);

                    // single select
                    if (!shift) {
                        _.each(seletions(), (e: EventApi) => {
                            e.setProp(GROUP_ID, '');
                            e.setProp(BORDER_COLOR, TRANSPARENT);
                        });

                        event.setProp(BORDER_COLOR, BLACK);

                        const { status } = event.extendedProps;

                        if (status === 'new') {
                            vm.$view('edit');
                        } else {
                            vm.$view('view');
                        }

                        popupData.event(event);

                        // update exclude-times
                        const sameDayEvent = _
                            .chain(vm.calendar.getEvents())
                            .filter(({ start, id }) => id !== event.id && moment(start).isSame(event.start, 'day'))
                            .map(({ start, end }) => ({ startTime: getTimeOfDate(start), endTime: getTimeOfDate(end) }))
                            .value();

                        popupData.excludeTimes(sameDayEvent);

                        // show popup on edit mode
                        popupPosition.event(el);

                        // update mouse pointer
                        const { screenX, screenY } = jsEvent;
                        
                     
                        if (vm.params.initialView() === "oneDay" && screenX === 0 && screenY === 0) {
                            const width = window.innerWidth;
                            const height = window.innerHeight;
                            dataEvent.pointer({ screenX: width / 2, screenY: height / 2 });
                        } else {
                            dataEvent.pointer({ screenX, screenY });
                        }
                      
                    } else {
                        // multi select
                        const [first] = seletions();

                        // no selections
                        if (!first) {
                            event.setProp(BORDER_COLOR, BLACK);
                        } else {
                            // odd select
                            if (event.borderColor === BLACK) {
                                event.setProp(GROUP_ID, '');
                                event.setProp(BORDER_COLOR, TRANSPARENT);
                            } else
                                // add new select
                                if (moment(first.start).isSame(event.start, 'date')) {
                                    event.setProp(BORDER_COLOR, BLACK);
                                }
                        }
                    }

                    const selecteds = seletions();

                    if (!!selecteds.length) {
                        // group selected event & disable resizeable
                        _.each(selecteds, (e: EventApi) => {
                            e.setProp(DURATION_EDITABLE, selecteds.length === 1);
                            e.setProp(GROUP_ID, SELECTED);
                        });
                    }
                    $('#edit').focus();
                },
                eventDragStart: (arg: EventDragStartArg) => {
                    const { event } = arg;
                    const {
                        id,
                        start,
                        end,
                        borderColor,
                        groupId,
                        extendedProps
                    } = event;

                    // remove new event (with no data) & background event
                    removeNewEvent(arg.event);

                    // cache drag event
                    $caches.drag({ id, start, end, groupId, borderColor, extendedProps });

                    // copy event by drag
                    if (ko.unwrap<boolean>(dataEvent.shift)) {
                        updateEvents();
                    }

                    vm.selectedEvents = [];

                    // clear all old selection
                    _.each(vm.calendar.getEvents(), (e: EventApi) => {
                        e.setProp(GROUP_ID, '');

                        e.setProp(BORDER_COLOR, TRANSPARENT);
                    });

                    arg.event.setProp(BORDER_COLOR, BLACK);
                },
                eventDragStop: (arg: EventDragStopArg) => {
                    // clear drag cache
                    $caches.drag(null);
                },
                eventDrop: (arg: EventDropArg) => {
                    const { event, relatedEvents } = arg;
                    const { start, end, id, title, extendedProps, borderColor, groupId } = event;
                    const rels = relatedEvents.map(({ start, end }) => ({ start, end }));

                    vm.selectedEvents = [{ start, end }, ...rels];

                    // update data sources
                    mutatedEvents();

                    // add new event (no save) if new event is dragging
                    if (!title && extendedProps.status === 'new' && !rels.length) {
                        const event = vm.calendar
                            .addEvent({
                                id,
                                start,
                                end,
                                borderColor,
                                groupId,
                                extendedProps
                            });

                        $caches.new(event);
                        const el: HTMLElement = vm.$el.querySelector(`[event-id="${event.id}"]`);

                        if (el) {
                            const { view } = vm.calendar;

                            vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view });
                        }
                    }
                },
                eventResizeStart: (arg: EventResizeStartArg) => {
                    // remove new event (with no data) & background event
                    removeNewEvent(arg.event);

                    vm.selectedEvents = [];

                    // clear all oll selection
                    _.each(vm.calendar.getEvents(), (e: EventApi) => {
                        e.setProp(GROUP_ID, '');

                        e.setProp(BORDER_COLOR, TRANSPARENT);
                    });

                    arg.event.setProp(BORDER_COLOR, BLACK);
                    arg.event.setProp(GROUP_ID, SELECTED);

                    popupPosition.event(null);
                },
                eventResize: (arg: EventResizeDoneArg) => {
                    const { event } = arg;
                    const { start, end, title, extendedProps, id, borderColor, groupId } = event;

                    vm.selectedEvents = [{ start, end }];

                    // update data sources
                    mutatedEvents();

                    // add new event (no save) if new event is dragging
                    if (!title && extendedProps.status === 'new') {
                        $caches.new(vm.calendar
                            .addEvent({
                                id,
                                start,
                                end,
                                borderColor,
                                groupId,
                                extendedProps
                            }));
                    }
                },
                eventResizeStop: ({ el, event }) => {
                    console.log('stop', event.extendedProps);
                },
                select: ({ start, end }) => {

                    // clean selection
                    vm.calendar.unselect();

                    // rerender event (deep clean selection)
                    updateEvents();

                    // add new event from selected data
                    const event = vm.calendar
                        .addEvent({
                            id: randomId(),
                            start: formatDate(start),
                            end: formatDate(end),
                            [BORDER_COLOR]: BLACK,
                            [GROUP_ID]: SELECTED,
                            extendedProps: {
                                status: 'new'
                            }
                        });

                    $caches.new(event);
                    const el: HTMLElement = vm.$el.querySelector(`[event-id="${event.id}"]`);

                    if (el) {
                        const { view } = vm.calendar;

                        vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view });
                    }
                },
                eventRemove: ({ event }) => {
                    // remove event from event sources
                    _.forEach(vm.calendar.getEvents(), (e: EventApi) => {
                        if (e.id === event.id) {
                            e.remove();
                        }
                    });

                    // emit data out if event isn't new
                    if (event.title && event.extendedProps.status !== 'new') {
                        mutatedEvents();
                    } else if (!event.title && event.extendedProps.status === 'new') {
                        $caches.new(null);
                    }
                },
                eventReceive: ({ event }) => {  
                    //drag event from used list
                    const {
                        title,
                        start,
                        backgroundColor,
                        textColor,
                        extendedProps
                    } = event;
                    const sd = ko.unwrap(params.slotDuration);
                    const end = moment(start).add(sd, 'minute').toDate();
                
                    // remove drop event
                    event.remove();

                    vm.selectedEvents = [{ start, end }];
                    const wg = {
                        workCD1,
                        workCD2,
                        workCD3,
                        workCD4,
                        workCD5
                    } = extendedProps;
                    // add cloned event to datasources
                    events.push({
                        title: getTitles(wg, vm.params.$settings().startManHourInputResultDto.tasks),
                        start,
                        end,
                        textColor,
                        backgroundColor,
                        extendedProps: {
                            ...extendedProps,
                            id: randomId(),
                            status: 'update'
                        } as any
                    });
                },
                datesSet: ({ start, end }) => {
                    const current = moment().startOf('day');
                    const { start: vrs, end: vre } = ko.unwrap(validRange);
                    const isValidRange = () => {
                        const validRange = ko.unwrap<DateRangeInput>(params.validRange);

                        if (validRange) {
                            const { start, end } = validRange;

                            if (start && end) {
                                return current.isBetween(start, end, 'date', '[)');
                            }

                            if (start) {
                                return current.isSameOrAfter(start, 'date');
                            }

                            if (end) {
                                return current.isSameOrBefore(end, 'date');
                            }
                        }

                        return true;
                    };

                    const $curt = $el.find('.fc-current-day-button');
                    const $prev = $el.find('.fc-preview-day-button');
                    const $next = $el.find('.fc-next-day-button');

                    datesSet({ start, end });

                    // enable, disable today button when change dateRange
                    if (!current.isBetween(start, end, 'date', '[)') && isValidRange()) {
                        $curt.removeAttr('disabled');
                    } else {
                        $curt.attr('disabled', 'disabled');
                    }

                    // enable, disable preview button with validRange
                    if (vrs) {
                        if (moment(start).isAfter(vrs, 'day')) {
                            $prev.removeAttr('disabled');
                        } else {
                            $prev.attr('disabled', 'disabled');
                        }
                    } else {
                        $prev.removeAttr('disabled');
                    }

                    // enable, disable next button with validRange
                    if (vre) {
                        if (moment(end).isBefore(vre, 'day')) {
                            $next.removeAttr('disabled');
                        } else {
                            $next.attr('disabled', 'disabled');
                        }
                    } else {
                        $next.removeAttr('disabled');
                    }
                },
                eventDidMount: ({ el, event }) => {
                    el.setAttribute('event-id', event.id);
                },
                windowResize: () => {
                    // update height
                    const fce = vm.calendar.el.getBoundingClientRect();

                    if (fce) {
                        const { top } = fce;
                        const { innerHeight } = window;

                        vm.calendar.setOption('height', `${innerHeight - top - 10}px`);
                    }
                },
                windowResizeDelay: 100,
                handleWindowResize: true
            });

            storeSetting()
                // update setting from domain charactorgistic
                .then((value) => {
                    if (value) {
                        const { setting } = popupData;
                        const { firstDay, scrollTime, slotDuration } = value;

                        setting.firstDay(firstDay);
                        setting.scrollTime(scrollTime);
                        setting.slotDuration(slotDuration);
                    }
                })
                // render calendar after restore charactergistic domain to model
                .then(() => {
                    vm.calendar.render();
                });

            // change weekends 
            ko.computed({
                read: () => {
                    const wk = ko.unwrap<boolean>(weekends);

                    vm.calendar.setOption('weekends', wk);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // change view
            ko.computed({
                read: () => {
                    const cv = ko.unwrap<string>(computedView);

                    vm.calendar.changeView(cv);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set locale
            ko.computed({
                read: () => {
                    const lc = ko.unwrap<string>(locale);

                    vm.calendar.setOption('locale', lc);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set editable
            ko.computed({
                read: () => {
                    const ed = true;

                    vm.calendar.setOption('editable', ed);
                    vm.calendar.setOption('droppable', ed);
                    vm.calendar.setOption('selectable', ed);

                    if (ed !== false) {
                        $el.find('.fc-copy-day-button').removeAttr('disabled');
                    } else {
                        $el.find('.fc-copy-day-button').attr('disabled', 'disabled');
                    }
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set firstDay
            ko.computed({
                read: () => {
                    const fd = ko.unwrap<number>(firstDay);

                    vm.calendar.setOption('firstDay', fd);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set scrollTime
            ko.computed({
                read: () => {
                    const sc = ko.unwrap<number>(scrollTime);
                    setTimeout(() => {
                        vm.calendar.scrollToTime(formatTime(sc));
                    }, 500);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set initialDate
            ko.computed({
                read: () => {
                    const id = ko.unwrap<Date>(initialDate);

                    clearSelection();

                    vm.calendar.gotoDate(formatDate(id));

                    // update selected header color
                    
                    if (vm.params.initialView() == "oneDay") {

                        vm.updateStyle('selectday', `.fc-container .fc-timegrid.fc-timeGridDay-view th.fc-day[data-date='${formatDate(id, 'YYYY-MM-DD')}'] { background-color: #ffffcc; }`);
                    } else {
                        vm.updateStyle('selectday', `.fc-container .fc-timegrid.fc-timeGridWeek-view th.fc-day[data-date='${formatDate(id, 'YYYY-MM-DD')}'] { background-color: #ffffcc; }`);

                    }
                    
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set events to calendar
            ko.computed({
                read: updateEvents,
                disposeWhenNodeIsRemoved: vm.$el
            });

            // calculate button show on view
            ko.computed({
                read: () => {
                    const { left, right, center, start, end } = headerToolbar;
                    const avs: string[] = ko.unwrap<InitialView[]>(availableView).map(toKebabCase);
                    const buttons = avs.map((m: ButtonView) => ({ [m]: viewButtons[m] }))

                    vm.calendar.setOption('customButtons', {
                        ...customButtons,
                        ...buttons.reduce((p, c) => ({ ...p, ...c }), {})
                    });

                    vm.calendar.setOption('headerToolbar', {
                        left: avs.length ? `${left} ${avs.join(',')}` : left,
                        center, right, start, end
                    });

                    updateActive();
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set slotDuration
            ko.computed({
                read: () => {
                    const slotdr = ko.unwrap(params.slotDuration);
                    const time = !slotdr ? '00:15:00' : formatTime(slotdr);
                    const updateOption = () => {
                        vm.calendar.setOption('slotDuration', time);
                        // slot label by slotDuration
                        vm.calendar.setOption('slotLabelInterval', time);
                        // set eventDuration default by slotDuration 
                        vm.calendar.setOption('defaultTimedEventDuration', time);
                    };

                    if (!version.match(/IE/)) {
                        updateOption();
                    } else {
                        // on ie, scroll body render not good
                        // destroy calendar & render it again
                        $.Deferred()
                            .resolve(true)
                            .then(() => vm.calendar.destroy())
                            .then(() => vm.calendar.render())
                            .then(() => updateOption());
                    }
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set businessHours
            ko.computed({
                read: () => {
                    const breakTime = ko.unwrap<BreakTime>(params.breakTime);
                    const businessHours = ko.unwrap<BussinessHour[]>(params.businessHours);

                    if (!breakTime) {
                        vm.calendar.setOption('businessHours', businessHours.map((m) => ({
                            ...m,
                            startTime: formatTime(m.startTime),
                            endTime: formatTime(m.endTime)
                        })));

                        vm.updateStyle('breaktime', '');
                    } else {
                        const { startTime, endTime, backgroundColor } = breakTime;

                        if (businessHours.length) {
                            const starts = businessHours.map((m) => ({
                                ...m,
                                startTime: formatTime(m.startTime),
                                endTime: m.startTime !== 0 && m.endTime !== 0 ? formatTime(startTime) : formatTime(0)
                            }));
                            const ends = businessHours.map((m) => ({
                                ...m,
                                startTime: m.startTime !== 0 && m.endTime !== 0 ? formatTime(endTime) : formatTime(0),
                                endTime: formatTime(m.endTime)
                            }));

                            vm.calendar.setOption('businessHours', [...starts, ...ends]);
                        } else {
                            vm.calendar.setOption('businessHours', [{
                                daysOfWeek: [0, 1, 2, 3, 4, 5, 6],
                                startTime: formatTime(0),
                                endTime: formatTime(startTime)
                            }, {
                                daysOfWeek: [0, 1, 2, 3, 4, 5, 6],
                                startTime: formatTime(endTime),
                                endTime: formatTime(24 * 60)
                            }]);
                        }

                        vm.updateStyle('breaktime', `.fc-timegrid-slot-lane-breaktime { background-color: ${backgroundColor || 'transparent'} }`);
                    }
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set validRange
            ko.computed({
                read: () => {
                    const validRange = ko.unwrap<DateRangeInput>(params.validRange);

                    vm.calendar.setOption('validRange', validRange);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // register all global event
            vm.initalEvents()
                .registerEvent('mousemove', () => {
                    // clear new event when start select
                    if (ko.unwrap(dataEvent.target) === 'date') {
                        removeNewEvent(null);
                    }
                });

            $el
                .removeAttr('data-bind')
                .find('[data-bind]')
                .removeAttr('data-bind');

            // update datasource when event change
            subscribeEvent
                .subscribe(() => {
                    $caches.new(null);
                    mutatedEvents();
                });

            vm.$nextTick(() => {
                vm.calendar.updateSize();
            });

            // test item
            _.extend(window, { dragger, calendar: vm.calendar, params, popupPosition });
        }

        public destroyed() {
            const vm = this;
            const { events } = vm;

            _.each(events, (evts: J_EVENT[], k: string) => {
                _.each(evts, (h: J_EVENT) => $(window).off(k, h))
            });
        }

        public removeEvent(id: string) {

        }

        private updateStyle(key: Style, style: string) {
            const vm = this;
            const styles = ko.unwrap(vm.$styles);

            _.extend(styles, { [key]: style });

            vm.$styles(styles);
        }

        private initalEvents() {
            const vm = this;
            const { $el, params, calendar, dataEvent, popupPosition } = vm;

            $($el)
                .on('mousewheel', (evt: JQueryEvent) => {
                    if (ko.unwrap(dataEvent.shift) === true) {
                        evt.preventDefault();

                        const { deltaY, wheelDelta } = evt.originalEvent;
                        const slotDuration = ko.unwrap(params.slotDuration);

                        if (!version.match(/IE/) && ['timeGridDay', 'timeGridWeek'].indexOf(calendar.view.type) !== -1 && ko.isObservable(params.slotDuration)) {
                            const index = durations.indexOf(slotDuration);

                            if ((wheelDelta || deltaY) < 0) {
                                params.slotDuration(durations[Math.max(index - 1, 0)]);
                            } else {
                                params.slotDuration(durations[Math.min(index + 1, durations.length - 1)]);
                            }
                        }
                    }
                });

            vm
                .registerEvent('mouseup', () => {
                    dataEvent.mouse(false);
                    dataEvent.target(null);
                })
//                .registerEvent('mousewheel', ({ target }) => {
//                    if (!$(target).closest('.fc-popup-editor.show').length) {
//                        popupPosition.event(null);
//                        popupPosition.setting(null);
//                    }
//                })
                .registerEvent('mousedown', (evt) => {
                    const $tg = $(evt.target);

                    const iown = $tg.hasClass('.popup-owner-copy');
                    const cown = $tg.closest('.popup-owner-copy').length > 0;
                    const ipov = $tg.hasClass('.fc-popup-editor');
                    const cpov = $tg.closest('.fc-popup-editor').length > 0;
                    const ipkr = $tg.hasClass('.datepicker-container') && $tg.not('.datepicker-inline');
                    const cpkr = $tg.closest('.datepicker-container').length > 0 && $tg.closest('.datepicker-inline').length === 0;

                    dataEvent.mouse(true);

                    const targ = $tg
                        .closest('.fc-timegrid-event.fc-v-event.fc-event').length ? 'event' :
                        ($tg.hasClass('fc-non-business') || $tg.hasClass('fc-timegrid-slot')) ? 'date' : null;

                    dataEvent.target(targ);

                    // close popup if target isn't owner & poper.
                    if (!iown && !cown && !ipov && !cpov && !ipkr && !cpkr) {
//                        popupPosition.event(null);
//                        popupPosition.setting(null);
                    }
                })
                .registerEvent('mousemove', () => {
                    if (ko.unwrap(dataEvent.mouse)) {
                        // popupPosition.event(null);
                        // popupPosition.setting(null);
                    }
                })
                // store data keyCode
                .registerEvent('keydown', (evt: JQueryEventObject) => {
                    if (evt.keyCode === 16 || evt.shiftKey || evt.which === 16) {
                        dataEvent.shift(true);
                    }

                    if (evt.keyCode === 17 || evt.ctrlKey || evt.which === 17) {
                        dataEvent.ctrl(true);
                    }

                    if (evt.keyCode === 18 || evt.altKey || evt.which === 18) {
                        dataEvent.alt(true);
                    }
                })
                // remove data keyCode
                .registerEvent('keyup', (evt: JQueryEventObject) => {
                    if (evt.keyCode === 16 || evt.shiftKey || evt.which === 16) {
                        dataEvent.shift(false);
                    }

                    if (evt.keyCode === 17 || evt.ctrlKey || evt.which === 17) {
                        dataEvent.ctrl(false);
                    }

                    if (evt.keyCode === 18 || evt.altKey || evt.which === 18) {
                        dataEvent.alt(false);
                    }

                    if (evt.keyCode === 46 && !ko.unwrap(dataEvent.delete)) {
                        if (vm.calendar) {
                            const selecteds = _.filter(vm.calendar.getEvents(), (e: EventApi) => e.groupId === SELECTED);

                            if (selecteds.length) {
                                dataEvent.delete(true);
                                const starts = selecteds.map(({ start }) => formatDate(start));

                                if (ko.isObservable(vm.params.events)) {
                                    vm.params.events.remove((e: EventRaw) => starts.indexOf(formatDate(e.start)) !== -1);
                                }
                                dataEvent.delete(false);
                                popupPosition.event(null);
                                popupPosition.setting(null);
//                                vm.$dialog
//                                    .confirm({ messageId: 'DELETE_CONFIRM' })
//                                    .then((v: 'yes' | 'no') => {
//                                        if (v === 'yes') {
//                                            const starts = selecteds.map(({ start }) => formatDate(start));
//
//                                            if (ko.isObservable(vm.params.events)) {
//                                                vm.params.events.remove((e: EventRaw) => starts.indexOf(formatDate(e.start)) !== -1);
//                                            }
//                                        }
//
//                                        dataEvent.delete(false);
//                                    });
                            }
                        }
                    }
                })
                .registerEvent('resize', () => {
                    popupPosition.event(null);
                    popupPosition.setting(null);
                });

            return vm;
        }

        private registerEvent(name: string, cb: (evt: JQueryEventObject) => void) {
            const vm = this;
            let hook = vm.events[name];

            if (!hook) {
                hook = vm.events[name] = [];
            }

            hook.push(cb);

            $(window).on(name, cb);

            return vm;
        }
    }

    export module components {

        type EVENT_PARAMS = {
            mode: KnockoutComputed<boolean>;
            view: KnockoutObservable<'view' | 'edit'>;
            data: KnockoutObservable<null | EventApi>;
            position: KnockoutObservable<null | HTMLElement>;
            components: { view: string, editor: string; };
            mutated: KnockoutObservable<null>;
            excludeTimes: KnockoutObservableArray<BussinessTime>;
            mousePointer: KnockoutObservable<{ screenX: number; screenY: number; }>;
            $settings: KnockoutObservable<any | null>;
        };

        @handler({
            bindingName: E_COMP_NAME,
            validatable: true,
            virtual: false
        })
        export class FullCalendarEditorBindingHandler implements KnockoutBindingHandler {
            init(element: HTMLElement, valueAccessor: () => EventApi, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
                const name = E_COMP_NAME;
                const data = valueAccessor();
                const mode = allBindingsAccessor.get('mode');
                const view = allBindingsAccessor.get('view');
                const mutated = allBindingsAccessor.get('mutated');
                const position = allBindingsAccessor.get('position');
                const components = allBindingsAccessor.get('components');
                const excludeTimes = allBindingsAccessor.get('exclude-times');
                const mousePointer = allBindingsAccessor.get('mouse-pointer');
                const $settings = allBindingsAccessor.get('$settings');

                const component = { name, params: { data, position, components, mode, view, mutated, excludeTimes, mousePointer, $settings } };

                element.removeAttribute('data-bind');
                element.classList.add('fc-popup-editor');
                element.classList.add('fc-popup-event');

                ko.applyBindingsToNode(element, { component }, bindingContext);

                return { controlsDescendantBindings: true };
            }
        }

        @component({
            name: E_COMP_NAME,
            template: `DETAIL_EVENT`
        })
        export class FullCalendarEditorComponent extends ko.ViewModel {
            event!: (evt: JQueryEventObject) => void;

            constructor(private params: EVENT_PARAMS) {
                super();

            }

            mounted() {
                const vm = this;
                const { $el, params } = vm;
                const { components, data, position, mode, view, excludeTimes, mousePointer, $settings } = params;
                const $ctn = $('<div>');
                const $view = document.createElement('div');
                const $edit = document.createElement('div');

                $($el)
                    .html('')
                    .append($ctn);

                $ctn
                    .append($view)
                    .append($edit);

                ko.computed({
                    read: () => {
                        const _view = ko.unwrap(view);

                        if (_view === 'edit') {
                            $view.style.display = 'none';
                            $edit.style.display = 'block';
                        } else {
                            $edit.style.display = 'none';
                            $view.style.display = 'block';
                        }
                    },
                    disposeWhenNodeIsRemoved: $el
                });

                if (components) {
                    const close = (result?: 'yes' | 'cancel' | null) => vm.close(result);
                    const remove = () => vm.remove();
                    const update = () => {
                        if (view() !== 'edit') {
                            view('edit');
                        } else {
                            view.valueHasMutated();
                        }

                        // rebind size of popup
                        position.valueHasMutated();
                    };

                    // mock data
                    const $share = ko.observable(null);

                    ko.applyBindingsToNode($view, { component: { name: components.view, params: { update, remove, close, data, mode, $settings, $share } } });
                    ko.applyBindingsToNode($edit, { component: { name: components.editor, params: { remove, close, data, mode, view, position, excludeTimes, $settings, $share } } });
                }

                ko.computed({
                    read: () => {
                        const pst = ko.unwrap(position);
                        const pot = ko.unwrap(mousePointer);

                        if (!pst) {
                            $el.removeAttribute('style');
                            $el.classList.remove('show');
                        } else {
                            const { innerWidth, innerHeight } = window;
                            const { screenX, screenY } = pot;
                            const { top, left, width: wd, height: hg } = pst.getBoundingClientRect();

                            const first = $el.querySelector('div');

                            $el.classList.add('show');

                            if (!first) {
                                $el.style.top = `${top || 0}px`;
                                $el.style.left = `${(left || 0) + wd + 3}px`;
                            } else {
                                const { width, height } = first.getBoundingClientRect();

                                if (top + height < innerHeight - 20) {
                                    $el.style.top = `${top || 0}px`;
                                } else {
                                    $el.style.top = `${innerHeight - 30 - (height || 0)}px`;
                                }

                                if (left + wd + width < innerWidth - 20) {
                                    $el.style.left = `${(left || 0) + wd + 3}px`;
                                } else if ((left || 0) - width - 23 < 0) {
                                    if (screenX + width < innerWidth - 20) {
                                        $el.style.left = `${screenX - 55}px`;
                                    } else {
                                        $el.style.left = `${screenX - width - 75}px`;
                                    }
                                } else {
                                    $el.style.left = `${(left || 0) - width - 23}px`;
                                }

                                $el.style.width = `${width + 20}px`;
                                $el.style.height = `${height + 20}px`;
                            }
                        }
                    },
                    disposeWhenNodeIsRemoved: $el
                });
            }

            remove() {
                const vm = this;
                const { params } = vm;
                const { data, position, view, mutated } = params;

                $.Deferred()
                    .resolve(true)
                    .then(() => {
                        const event = ko.unwrap(data);

                        // change status for subscribe & rebind
                        event.setExtendedProp('status', 'delete');

                        // remove???
                        event.remove();

                        // trigger update from parent view
                        mutated.valueHasMutated();
                    })
                    .then(() => data(null))
                    .then(() => position(null))
                    .then(() => view('view'));
            }

            close(result?: 'yes' | 'cancel' | null) {
                const vm = this;
                const { params } = vm;
                const { data, position, view, mutated } = params;

                $.Deferred()
                    .resolve(true)
                    .then(() => {
                        if (result === 'yes') {
                            const event = ko.unwrap(data);

                            if (event) {
                                event.remove();
                            }
                        }

                        // trigger update from parent view
                        mutated.valueHasMutated();
                    })
                    .then(() => data(null))
                    .then(() => position(null))
                    .then(() => view('view'));
            }
        }

        @handler({
            bindingName: S_COMP_NAME,
            validatable: false,
            virtual: false
        })
        export class FullCalendarCopyBindingHandler implements KnockoutBindingHandler {
            init(element: HTMLElement, valueAccessor: () => EventApi, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
                const name = S_COMP_NAME;
                const data = valueAccessor();
                const position = allBindingsAccessor.get('position');
                const component = { name, params: { data, position } };

                element.removeAttribute('data-bind');

                element.classList.add('ntsControl');
                element.classList.add('fc-popup-editor');
                element.classList.add('fc-popup-setting');

                ko.applyBindingsToNode(element, { component }, bindingContext);

                return { controlsDescendantBindings: true };
            }
        }

        type COPY_PARAMS = {
            name: string;
            data: SettingApi;
            position: KnockoutObservable<null | HTMLElement>;
        };

        @component({
            name: S_COMP_NAME,
            template: `FC_SETTING`
        })
        export class FullCalendarSettingComponent extends ko.ViewModel {
            event!: (evt: JQueryEventObject) => void;

            constructor(private params: COPY_PARAMS) {
                super();
            }

            mounted() {
                const vm = this;
                const { $el, params } = vm;
                const { data, position } = params;

                $el.innerHTML = '';

                // apply binding setting panel
                ko.applyBindingsToNode($el, { component: { name: 'fc-setting-panel', params: { ...data, position } } });

                ko.computed({
                    read: () => {
                        const pst = ko.unwrap(position);

                        if (!pst) {
                            $el.removeAttribute('style');
                            $el.classList.remove('show');
                        } else {
                            const { top, left } = pst.getBoundingClientRect();
                            const first = $el.querySelector('div');

                            $el.classList.add('show');

                            $el.style.top = `${top}px`;

                            if (first) {
                                const { width, height } = first.getBoundingClientRect();

                                $el.style.left = `${left - width - 23}px`;

                                $el.style.width = `${width + 30}px`;
                                $el.style.height = `${height + 20}px`;
                            } else {
                                $el.style.left = `${left}px`;
                            }

                            // focus first input element
                            $($el).find('select:first').focus();
                        }
                    },
                    disposeWhenNodeIsRemoved: $el
                });

                vm.event = (evt: JQueryEventObject) => {
                    evt.preventDefault();

                    const tg = evt.target as HTMLElement;

                    if (tg && !!ko.unwrap(position)) {
                        if (!tg.classList.contains(POWNER_CLASS_CPY) && !$(tg).closest(`.${POWNER_CLASS_CPY}`).length && !$(tg).closest('.fc-popup-setting').length) {
                            position(null);
                        }
                    }
                };

                $(document).on('click', vm.event);

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
                        } else if (evt.keyCode === 27) {
                            const fabl = position();

                            // close setting popup
                            position(null);

                            // focus to setting button
                            $(fabl).focus();
                        }
                    });
                $('#btn_register').focus();
            }

            destroyed() {
                const vm = this;

                $(document).off('click', vm.event);
            }
        }

        @handler({
            bindingName: 'sb-resizer',
            validatable: true,
            virtual: false
        })
        export class SidebarResizerHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, valueAccessor: () => FullCalendar.Calendar, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } => {
                const calendar = valueAccessor();
                const cache = { md: -1, cw: 0 };
                const ctn = $('.fc-container.cf').get(0);

                $(element)
                    .on('mousemove', (e: JQueryEvent) => {
                        const oe = e.originalEvent as MouseEvent;
                        const bound = element.getBoundingClientRect();

                        if (bound.right - 7 <= oe.clientX && bound.right >= oe.clientX) {
                            ctn.classList.add('resizer');
                        } else {
                            ctn.classList.remove('resizer');
                        }
                    })
                    .on('mouseout', () => {
                        if (cache.md === -1) {
                            ctn.classList.remove('resizer');
                        }
                    })
                    .on('mousedown', (evt: JQueryEvent) => {
                        const oe = evt.originalEvent as MouseEvent;

                        if (ctn.classList.contains('resizer')) {
                            cache.md = oe.clientX;
                            cache.cw = element.offsetWidth;
                        }
                    })
                    .on('mouseup', () => {
                        cache.md = -1;
                        cache.cw = 0;
                    });

                $(window)
                    .on('mousemove', (evt: JQueryEvent) => {
                        const { cw, md } = cache;
                        const oe = evt.originalEvent as MouseEvent;

                        if (md !== -1) {
                            element.style.width = `${cw + oe.clientX - md}px`;

                            calendar.updateSize();
                        }
                    })
                    .on('mouseup', () => {
                        cache.md = -1;
                        cache.cw = 0;

                        ctn.classList.remove('resizer');
                    });

                return { controlsDescendantBindings: true };
            }

        }

        @component({
            name: 'fc-events',
            template:
                `<td data-bind="i18n: 'KDW013_20'"></td>
                <!-- ko foreach: { data: $component.data, as: 'day' } -->
                <td class="fc-event-note fc-day" data-bind="css: { 'no-data': !day.events.length }, attr: { 'data-date': day.date }">
                    <div data-bind="foreach: { data: day.events, as: 'note' }">
                        <div class="text-note limited-label" data-bind="text: note"></div>
                    </div>
                </td>
                <!-- /ko -->`
        })
        export class FullCalendarEventHeaderComponent extends ko.ViewModel {
            today: string = moment().format(DATE_FORMAT);

            constructor(private data: KnockoutComputed<string[][]>) {
                super();

                if (!this.data) {
                    this.data = ko.computed(() => []);
                }
            }

            mounted() {
                const vm = this;
                const { $el, data } = vm;

                ko.computed({
                    read: () => {
                        const ds = ko.unwrap(data);

                        if (ds.length) {
                            $el.style.display = null;
                        } else {
                            $el.style.display = 'none';
                        }

                        $($el).find('[data-bind]').removeAttr('data-bind');
                    },
                    disposeWhenNodeIsRemoved: $el
                });

                // fix display on ie
                vm.$el.removeAttribute('style');
            }
        }

        @component({
            name: 'fc-times',
            template:
                `<td data-bind="i18n: 'KDW013_25'"></td>
                <!-- ko foreach: { data: $component.data, as: 'time' } -->
                <td class="fc-day" data-bind="html: $component.formatTime(time.value), attr: { 'data-date': time.date }"></td>
                <!-- /ko -->`
        })
        export class FullCalendarTimesHeaderComponent extends ko.ViewModel {
            today: string = moment().format(DATE_FORMAT);

            constructor(private data: KnockoutComputed<{ date: string; value: number | null; }[]>) {
                super();

                if (!this.data) {
                    this.data = ko.computed(() => []);
                }
            }

            mounted() {
                const vm = this;
                const { $el, data } = vm;

                ko.computed({
                    read: () => {
                        const ds = ko.unwrap(data);

                        if (ds.length) {
                            $el.style.display = null;
                        } else {
                            $el.style.display = 'none';
                        }

                        $($el).find('[data-bind]').removeAttr('data-bind');
                    },
                    disposeWhenNodeIsRemoved: $el
                });

                // fix display on ie
                vm.$el.removeAttribute('style');
            }

            formatTime(time: number | null) {
                if (!time) {
                    return '&nbsp;';
                }

                const hour = Math.floor(time / 60);
                const minute = Math.floor(time % 60);

                return `${hour}:${_.padStart(`${minute}`, 2, '0')}`;
            }
        }

        @component({
            name: 'fc-setting-panel',
            template: `
                <div id='fc'>
                    <table>
                        <tbody>
                            <tr>
                                <td colspan="2">
                                    <div data-bind="ntsFormLabel: { text: $component.$i18n('KDW013_12') }"></div>
                                </td>
                            </tr>
                            <tr>
                                <td style='width:' data-bind="i18n: 'KDW013_13'"></td>
                                <td>
                                    <select class="nts-input" data-bind="
                                            value: $component.params.firstDay,
                                            options: $component.firstDays,
                                            optionsText: 'title',
                                            optionsValue: 'id'
                                        ">
                                        <option></option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td data-bind="i18n: 'KDW013_14'"></td>
                                <td>
                                    <input class="nts-input" type="text" data-bind="input-time: $component.params.scrollTime" />
                                </td>
                            </tr>
                            <tr>
                                <td data-bind="i18n: 'KDW013_15'"></td>
                                <td>
                                    <select class="nts-input" data-bind="
                                            value: $component.params.slotDuration,
                                            options: $component.slotDurations,
                                            optionsText: 'title',
                                            optionsValue: 'id'
                                        ">
                                        <option></option>
                                    </select>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <style rel="stylesheet">
                    .fc-popup-setting tr {
                        height: 34px;
                    }
                    .fc-popup-setting tr:not(:first-child) td {
                        padding-top: 5px;
                    }
                    .fc-popup-setting tr input,
                    .fc-popup-setting tr select {
                        width: 85px;
                        height: 34px;
                        margin-left: 15px;
                        box-sizing: border-box;
                    }
                    .fc-popup-setting tr input {
                        text-align: right;
                    }
                </style>
            `
        })
        export class FullCalendarSettingViewmodel extends ko.ViewModel {
            firstDays: KnockoutObservableArray<{ id: number; title: string; }> = ko.observableArray([]);
            slotDurations: KnockoutObservableArray<{ id: number; title: string; }> = ko.observableArray([]);

            constructor(private params: SettingApi & { position: KnockoutObservable<any | null> }) {
                super();

                const vm = this;
                // resource for slotDuration
                const resource = [
                    'KDW013_16',
                    'KDW013_17',
                    'KDW013_18',
                    'KDW013_19'
                ];

                const startDate = moment().isoWeekday(1);
                const listDates = _.range(0, 7)
                    .map(m => startDate.clone().add(m, 'day'))
                    .map(d => ({
                        id: d.get('day'),
                        title: d.format('dddd')
                    }));

                vm.firstDays(listDates);

                vm.slotDurations(durations.map((id: number, index: number) => ({ id, title: vm.$i18n(resource[index]) })));
            }

            mounted() {
                const vm = this;
                const { params } = vm;
                const state = { open: false };
                const { firstDay, scrollTime, slotDuration, position, initialView} = params;

                // store all value to charactorgistic domain
                ko.computed({
                    read: () => {
                        const ps = ko.unwrap(position);
                        const fd = ko.unwrap(firstDay);
                        const sc = ko.unwrap(scrollTime);
                        const sd = ko.unwrap(slotDuration);
                        const iv = ko.unwrap(initialView);
                        // store when popup opened
                        if (state.open) {
                            storeSetting().then((value) => {
                                value = value ? value :{
                                firstDay: fd,
                                scrollTime: sc,
                                slotDuration: sd,
                                initialView: iv
                            };
                                value.firstDay = fd;
                                value.scrollTime = sc;
                                value.slotDuration = sd;
                                value.initialView = value.initialView;
                                
                                storeSetting(value);
                            });
                        } else if (ps) {
                            state.open = true;
                        }
                    },
                    disposeWhenNodeIsRemoved: vm.$el
                });

                $(vm.$el)
                    .removeAttr('data-bind')
                    .find('[data-bind]')
                    .removeAttr('data-bind');
            }
        }
    }

    export module department {
        @handler({
            bindingName: 'kdw013-department'
        })
        export class Kdw013DepartmentBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
                const name = componentName();
                const mode: KnockoutObservable<boolean> = allBindingsAccessor.get('mode');
                const params = { ...allBindingsAccessor() };
                const subscribe = (mode: boolean) => {

                    if (mode) {
                        ko.cleanNode(element);

                        element.innerHTML = '';
                    } else {
                        ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);
                    }
                };

                mode.subscribe(subscribe);

                subscribe(mode());

                return { controlsDescendantBindings: true };
            }
        }
    }

    export module approved {
        @handler({
            bindingName: 'kdw013-approveds'
        })
        export class Kdw013ApprovedBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
                const name = componentName();
                const params = { ...allBindingsAccessor() };

                ko.applyBindingsToNode(element, { component: { name, params } });

                return { controlsDescendantBindings: true };
            }
        }
    }

    export module events {
        @handler({
            bindingName: 'kdw013-events'
        })
        export class Kdw013EventBindingHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
                const name = componentName();
                const mode = allBindingsAccessor.get('mode');
                const items = allBindingsAccessor.get('items');
                const params = { mode, items };

                ko.applyBindingsToNode(element, { component: { name, params } });

                return { controlsDescendantBindings: true };
            }
        }
    }
}
