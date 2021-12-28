/// <reference path="../../../../../lib/generic/fullcalendar/index.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/daygrid.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/timegrid.d.ts" />
/// <reference path="../../../../../lib/generic/fullcalendar/interaction.d.ts" />

module nts.uk.ui.at.kdw013.calendar {
    let { randomId } = nts.uk.util;
    let { version } = nts.uk.util.browser;
    let { getTimeOfDate, getTask, getBackground, getTitles ,getBackgroundColor } = at.kdw013.share;

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
		isTimeBreak: boolean;
		employeeId: string;
		extendedProps: {
			id: string;
			status: EventStatus;
			////作業枠利用設定
			taskFrameUsageSetting: any,
	            //社員ID
	        employeeId: string,
	        //年月日
	        period: { start: Date; end: Date; },
	        //現在の応援勤務枠
	        frameNos:number[],                                
	        //工数実績作業ブロック
	        taskBlock: {
	            caltimeSpan: { start: Date, end: Date; },
	
	            taskDetails: [{ supNo: number, taskItemValues: ITaskItemValue[] }]
	        },
	        //作業内容入力ダイアログ表示項目一覧
	        displayManHrRecordItems: { itemId: number; order: any; }[]
		}
	};

    let CM2KBC = /([a-z0-9]|(?=[A-Z]))([A-Z])/g;
    let toKebabCase = (s: string) => s.replace(CM2KBC, '$1-$2').toLowerCase();
    let BREAKTIME_COLOR = '#ff99ff';
    let GROUP_ID = 'groupId';
    let BORDER_COLOR = 'borderColor';
    let BLACK = '#000';
    let TRANSPARENT = 'transparent';
    let SELECTED = 'selected';
    let BACKGROUND = 'background';
    let DURATION_EDITABLE = 'durationEditable';

    let E_COMP_NAME = 'fc-editor';
    let S_COMP_NAME = 'fc-setting';
    let COMPONENT_NAME = 'fullcalendar';
    let POWNER_CLASS_CPY = 'popup-owner-copy';
    let POWNER_CLASS_EVT = 'popup-owner-event';
    let DEFAULT_STYLES = `
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
        .fc .fc-col-header-cell-cushion{
            padding: 0px;
            line-height: 20px;
            font-size: 12px;
        }
        .fc-container .fc-sidebar {
            float: left;
            width: 255px;
            min-width: 210px;
            max-width: calc(100vw - 755px);
            overflow: hidden;
            margin-right: 10px;
            box-sizing: border-box;
            border-right: 1px solid #ccc;
            position: relative;
            padding-right: 1px;
            min-height: calc(100vh - 148px);
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
            height: auto;
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
            margin-bottom: 2px;
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
        .fc-container .fc-button-group>.nts-datepicker-wrapper>input.nts-input {
            width: 110px;
            height: 33px;
            border-radius: 0px;
        }
        .fc-container .fc-header-toolbar .fc-settings-button {
            width: 34px;
        }
        .fc-settings-button {
            margin-top: 2px;
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
        .fc-container .fc-col-header-cell{
            position: relative;
        }
        .fc-container .fc-event-title h4 {
            margin: 0;
            padding: 0;
        }
        .fc-container .fc-event-title pre{
            white-space: pre-wrap;
        }
        .fc-container .fc-event-description {
            margin-top: 10px;
        }
        .fc-container .fc-event-note.no-data {
            background-color: #f3f3f3;
        }
        .fc-container .fc-event-note>div {
            padding: 2px;
            min-height: 50px;
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
            width: 285px;
-           height: 270px;
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
        }
        .fc-container .fc-timegrid-event-harness{
            width: 100%;
            left: 0% !important;
        }
        .fc-current-day-button{
            width: 72px;
        }
        .fc-preview-day-button,
        .fc-next-day-button{
            margin-left: .75em;
        }
        .ui-accordion .ui-accordion-header,
        .ui-accordion .ui-accordion-content
        {
            border:none  !important;
        }
        .fc-toolbar-chunk{
            display: flex;
        }
        .favIcon{
            position: absolute;
            left: calc(100% - 22px);
            bottom: calc(100% - 18px);
        }
        .favIcon:hover{
                background-color: rgb(229, 242, 255);
        }
        .border-dashed{
            border-style: dashed !important;    
        }
        .otTime {
            background-color: #FFFF99;
        }
        .fc-view-harness{
            background-color: white;
        }
        .fc-current-day-button,
        .fc-preview-day-button,
        .fc-next-day-button,
        .fc-one-day-button,
        .fc-full-week-button,
        .fc-settings-button{
            height: 25px;
            font-size: 12px;
            padding: 0 4px;
        }
        .fc .fc-non-business
        {
            background: var(--fc-non-business-color,rgba(215,215,215,.7));
        }
        
`;

    @handler({
        bindingName: COMPONENT_NAME,
        validatable: false,
        virtual: false
    })
    export class FullCalendarBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => KnockoutObservableArray<EventRaw>, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
            let name = COMPONENT_NAME;

            let events = valueAccessor();

            let params = { events, ...allBindingsAccessor(), viewModel };
            let component = { name, params };

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
    let durations: SlotDuration[] = [5, 10, 15, 30];

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
        screenA:nts.uk.ui.at.kdw013.a.ViewModel,
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

    let DATE_FORMAT = 'YYYY-MM-DD';
    let ISO_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:00';

    let formatDate = (date: Date, format: string = ISO_DATE_FORMAT) => moment(date).format(format);
    let formatTime = (time: number) => {
        let f = Math.floor;
        let times = [f(time / 60), f(time % 60), 0]

        return times
            .map((m) => m.toString())
            .map((m) => _.padStart(m, 2, '0'))
            .join(':')
    };
    let activeClass = (t: EventTarget) => {
        $(t)
            .closest('.fc-button-group')
            .find('button')
            .removeClass('active');

        $(t).addClass('active');
    };

    let defaultDEvent = (): DataEventStore => ({
        alt: ko.observable(false),
        ctrl: ko.observable(false),
        shift: ko.observable(false),
        mouse: ko.observable(false),
        delete: ko.observable(false),
        target: ko.observable(null),
        pointer: ko.observable({ screenX: -1, screenY: -1 })
    });

    let defaultPopupData = (): PopupData => ({
        event: ko.observable(null),
        setting: {
            firstDay: ko.observable(1),
            scrollTime: ko.observable(420),
            slotDuration: ko.observable(30),
            initialView : ko.observable('fullWeek')
        },
        excludeTimes: ko.observableArray([])
    });

    let defaultPPosition = (): PopupPosition => ({
        setting: ko.observable(null),
        event: ko.observable(null)
    });

    let dayOfView = (view: InitialView) => {
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

    let storeSetting = (setting?: SettingStore): JQueryPromise<SettingStore | undefined> => {
        let vm = new ko.ViewModel();

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
                $settings: $component.params.$settings,
                screenA:$component.params.screenA
            "></div>
        <div data-bind="
                fc-setting: $component.popupData.setting,
                position: $component.popupPosition.setting
            "></div>
        <div class="fc-sidebar" data-bind="css: {
                    'edit-mode': ko.unwrap($component.params.editable),
                    'view-mode': !ko.unwrap($component.params.editable)
                }">
            <div class="fc-date-picker" data-bind="
                    kdw013-date-picker: 'kdw013-date-picker',
                    initialDate: $component.params.initialDate,
                    firstDay: $component.params.firstDay
                "></div>
            <div class="fc-employees department" data-bind="
                    kdw013-department: 'kdw013-department',
                    mode: $component.params.editable,
                    employee: $component.params.employee,
                    initialDate: $component.params.initialDate,
                    $settings: $component.params.$settings,
                    screenA:$component.params.screenA
                "></div>
            <div class="fc-employees confirmer" data-bind="
                    kdw013-approveds: 'kdw013-approveds',
                    mode: $component.params.editable,
                    confirmers: $component.params.confirmers,
                    initialDate: $component.params.initialDate,
                    $settings: $component.params.$settings
                "></div>
            <div class="fc-task-events" data-bind="
                    kdw013-task-events: 'kdw013-task-events',
                    mode: $component.params.editable,
                    items: $component.taskDragItems,
                    $settings: $component.params.$settings,
                    screenA:$component.params.screenA
                "></div>
            <div class="fc-oneday-events" data-bind="
                    kdw013-oneday-events: 'kdw013-oneday-events',
                    mode: $component.params.editable,
                    items: $component.onedayDragItems,
                    $settings: $component.params.$settings,
                    screenA:$component.params.screenA
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

        public onedayDragItems: KnockoutObservableArray<EventRaw> = ko.observableArray([]);
        
        public taskDragItems: KnockoutObservableArray<EventRaw> = ko.observableArray([]);

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
                    initialView: ko.observable('fullWeek'),
                    availableView: ko.observableArray([]),
                    initialDate: ko.observable(new Date()),
                    events: ko.observableArray([]),
                    employee: ko.observable(''),
                    confirmers: ko.observableArray([]),
                    attendanceTimes: ko.observableArray([]),
                    breakTime: ko.observable(null),
                    businessHours: ko.observableArray([]),
                    validRange: ko.observable({ start: null, end: null }),
                    event: {
                        datesSet: (__: Date, ___: Date) => { }
                    },
                    components: {
                        view: 'kdp013b',
                        editor: 'kdp013c'
                    },
                    $datas: ko.observable(null),
                    $settings: ko.observable(null),
                    screenA: new ViewModel()
                };
            }

            let { setting } = this.popupData;

            let {
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
                $settings,
                screenA
            } = this.params;
    
            if(screenA === undefined){
                this.params.screenA = new ViewModel();
            }

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
                this.params.initialView = ko.observable('fullWeek');
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

            let { datesSet } = this.params.event;

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
            
            this.params.employee
                .subscribe((value) => {
                    this.popupPosition.event(null);
                    this.popupPosition.setting(null);
                });
            
        }

        computedTaskDragItems(datas: a.ChangeDateDto | null, settings: a.StartProcess | null){
                let vm =this;
                $( "#task-fav" ).html('');
                if (datas && settings) {
                    let { tasks ,favTaskItems ,favTaskDisplayOrders } = settings;

                    if (favTaskItems && tasks && favTaskDisplayOrders) {
                        
                        if (tasks && tasks.length) {
                            let taskOrders = _.get(favTaskDisplayOrders, 'displayOrders', []);
                            let draggers: EventRaw[] = 
                                _.chain(taskOrders)
                                .sortBy([(o) => { return o.order; }])
                                .filter((o) => {
                                        let task = _.find(favTaskItems, ['favoriteId', o.favId]);
                                        return !_.isEmpty(_.get(task, 'favoriteContents'));
                                    })
                                .map((o) => {
                                    let task = _.find(favTaskItems, ['favoriteId', o.favId]);
                                    let relateId = randomId();
                                    let  [first] = task.favoriteContents;
                                    return {
                                        start: new Date(),
                                        end: new Date(),
                                        title: task.taskName,
                                        backgroundColor: getBackgroundColor(first.taskCode, tasks),
                                        textColor: '',
                                        extendedProps: {
                                            favId: task.favoriteId,
                                            relateId,
                                            order: o.order,
                                            status: 'new',
                                            remarks: '',
                                            dropInfo: {
                                                favoriteContents: task.favoriteContents
                                            }
                                            
                                        } as any
                                    };
                                })
                                .filter((m) => !!m)
                                .value();

                            // update dragger items
                            vm.taskDragItems(draggers);
                            if (!$('#task-fav').hasClass("ui-sortable")) {
                                $('#task-fav').sortable({
                                    forcePlaceholderSize: true,
                                    axis: "y",
                                    update: (event, ui) => {
                                        $("#task-fav").sortable("destroy");
                                        let rows = $(event.target).find('li.title');
                                        let sortedList = [];
                                        for (let i = 1; i <= rows.length; i++) {
                                            let element = rows[i - 1];
                                            sortedList.push({ favId: $(element).attr("data-favId"), order: i });
                                        }

                                        let item = _.find(sortedList, ['favId', $(ui.item).attr('data-favId')]);

                                        let command = { reorderedId: $(ui.item).attr('data-favId'), beforeOrder: $(ui.item).attr('data-order'), afterOrder: item.order };
                                        vm.taskDragItems([]);
                                        vm.$blockui('grayout').then(() => vm.$ajax('at', '/screen/at/kdw013/a/update_task_dis_order', command))
                                            .done(() => {

                                                vm.params.screenA.reloadTaskFav();
                                            }).always(() => vm.$blockui('clear'));
                                    },
                                    out: (event, ui) => {
                                        $("#task-fav").sortable("cancel");
                                    }
                                });
                            }
                            
                            return;
                        }
                    }
                }

                vm.taskDragItems([]);
            }

            computedOnedayDragItems(datas: a.ChangeDateDto | null, settings: a.StartProcessDto | null){
                let vm =this;
                $( "#one-day-fav" ).html('');
                if (datas && settings) {
                    let { workGroupDtos } = datas;
                    let { tasks, oneDayFavSets, oneDayFavTaskDisplayOrders} = settings;

                    if (oneDayFavSets && tasks && oneDayFavTaskDisplayOrders) {
                        
                        if (tasks && tasks.length) {
                            let dos = _.get(oneDayFavTaskDisplayOrders, 'displayOrders', []);
                            let draggers: EventRaw[] = 
                                _.chain(dos)
                                .sortBy([(o) => { return o.order; }])
                                    .filter((o) => {
                                        let oneDay = _.find(oneDayFavSets, ['favId', o.favId]);
                                        return !_.isEmpty(_.get(oneDay, 'taskBlockDetailContents'));
                                    })
                                .map((o) => {
                                    let oneDay = _.find(oneDayFavSets, ['favId', o.favId]);
                                    let relateId = randomId();
                                    let  [first] = oneDay.taskBlockDetailContents;
                                    return {
                                        start: new Date(),
                                        end: new Date(),
                                        title: oneDay.taskName,
                                        backgroundColor: getBackgroundColor(first.taskContents[0].taskContent.taskCode, tasks),
                                        textColor: '',
                                        extendedProps: {
                                            favId: oneDay.favId,
                                            relateId,
                                            status: 'new',
                                            remarks: '',
                                            order: o.order,
                                            dropInfo: {
                                                taskBlockDetailContents: oneDay.taskBlockDetailContents
                                            }
                                            
                                        } as any
                                    };
                                })
                                .filter((m) => !!m)
                                .value();

                            // update dragger items
                            vm.onedayDragItems(draggers);
                            if (!$('#one-day-fav').hasClass("ui-sortable")) {
                                $('#one-day-fav').sortable({
                                    forcePlaceholderSize: true,
                                    axis: "y",
                                    update: function(event, ui) {
                                        $("#one-day-fav").sortable("destroy");
                                        let rows = $(event.target).find('li.title');
                                        let sortedList = [];
                                        for (let i = 1; i <= rows.length; i++) {
                                            let element = rows[i - 1];
                                            sortedList.push({ favId: $(element).attr("data-favId"), order: i });
                                        }

                                        let item = _.find(sortedList, ['favId', $(ui.item).attr('data-favId')]);

                                        let command = { reorderedId: $(ui.item).attr('data-favId'), beforeOrder: $(ui.item).attr('data-order'), afterOrder: item.order };

                                        vm.$blockui('grayout').then(() => vm.$ajax('at', '/screen/at/kdw013/a/update_one_day_dis_order', command))
                                            .done(() => {
                                                vm.params.screenA.reloadOneDayFav();
                                            }).always(() => vm.$blockui('clear'));
                                    },
                                    out: function(event, ui) {
                                        $("#one-day-fav").sortable("cancel");
                                    }
                                });
                            }
                            
                            return;
                        }
                    }
                }
                
                vm.onedayDragItems([]);
            }

        enableBreakTime(){
            let vm = this;
            let data = ko.unwrap(vm.params.$datas);
            if (data) {
                let {estimateZones} = data;
                return !!_.find(estimateZones, ets => { return moment(etz.ymd).isSame(moment(vm.params.initialDate()), 'days') });;
            }
            return false;
        }

        public mounted() {
            let vm = this;
            vm.params.screenA.fullCalendar(vm);
            let {
                params,
                dataEvent,
                onedayDragItems,
                taskDragItems,
                popupData,
                popupPosition,
                subscribeEvent,
            } = vm;
            let {
                locale,
                event,
                events,
                scrollTime,
                firstDay,
                editable,
                initialDate,
                isShowBreakTime,
                initialView,
                availableView,
                viewModel,
                validRange,
                attendanceTimes,
                $datas,
                $settings
            } = params;
            let $caches: {
                new: KnockoutObservable<EventApi | null>;
                drag: KnockoutObservable<EventApi | null>;
            } = {
                new: ko.observable(null),
                drag: ko.observable(null)
            };

            let $el = $(vm.$el);
            let $dgOne = $el.find('div.fc-oneday-events').get(0);
            let $dgTask = $el.find('div.fc-task-events').get(0);
            let $fc = $el.find('div.fc-calendar').get(0);
            let FC: FullCalendar.FullCalendar | null = _.get(window, 'FullCalendar', null);
            let updateActive = () => {
                $el
                    .find('.fc-header-toolbar button')
                    .each((__, e) => {
                        e.classList.remove('active');
                        e.classList.remove('fc-button');
                        e.classList.remove('fc-button-primary');

                        let view = ko.unwrap(initialView);

                        if (e.className.match(toKebabCase(view))) {
                            e.classList.add('active');
                        }
                    });

                let current = moment().startOf('day');
                let { start, end } = ko.unwrap(datesSet) || { start: new Date(), end: new Date() };

                let $btn = $el.find('.fc-current-day-button');

//                if (!current.isBetween(start, end, 'date', '[)')) {
//                    $btn.removeAttr('disabled');
//                } else {
//                    $btn.attr('disabled', 'disabled');
//                }
            };

            let weekends: KnockoutObservable<boolean> = ko.observable(true);
            let datesSet: KnockoutObservable<DatesSet | null> = ko.observable(null).extend({ deferred: true, rateLimit: 100 });

            // emit date range to viewmodel
            datesSet.subscribe((ds) => {
                let { start, end } = ds;

                event.datesSet.apply(viewModel, [start, end]);
            });

            // calculate time on header
            let timesSet: KnockoutComputed<({ date: string | null; value: number | null; })[]> = ko.computed({
                read: () => {
                    let ds = ko.unwrap(datesSet);
                    let wd = ko.unwrap(firstDay);
                    let iv = ko.unwrap(initialView);
                    let evts = ko.unwrap<EventRaw[]>(events);
                    let cache = ko.unwrap<EventApi>($caches.new);
                    let { start, end } = cache || { start: null, end: null };
                    let nkend = moment(start).clone().add(1, 'hour').toDate();
                    let duration = moment(end || nkend).diff(start, 'minute');
                    let nday = dayOfView(iv);

                    if (ds) {
                        let { start, end } = ds;

                        let first = moment(start);
                        let diff: number = moment(end).diff(start, 'day');
                        let mkend = first.clone().add(1, 'hour').toDate();

                        let days = _.range(0, diff, 1)
                            .map(m => {
                                let date = first.clone().add(m, 'day');
                                let exists = _.filter([...evts], (d: EventApi) => {
                                    return !d.allDay &&
                                        d.display !== 'background' &&
                                        date.isSame(d.start, 'date');
                                });

                                return {
                                    date: date.format(DATE_FORMAT),
                                    value: exists.reduce((p, c) => p += moment(c.end || mkend).diff(c.start, 'minute'), date.isSame(nkend, 'date') ? duration : 0)
                                };
                            });

                        let sow = first.clone().isoWeekday(wd).isSame(start, 'date');

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

            let attendancesSet: KnockoutComputed<({ date: string | null; events: string[]; })[]> = ko.computed({
                read: () => {
                    let ds = ko.unwrap<DatesSet>(datesSet);
                    let wd = ko.unwrap(firstDay);
                    let iv = ko.unwrap(initialView);
                    let nday = dayOfView(iv);
                    let ads = ko.unwrap<AttendanceTime[]>(attendanceTimes);

                    if (!ds) {
                        return [];
                    }

                    let { start, end } = ds;

                    let first = moment(start);
                    let diff: number = moment(end).diff(start, 'day');

                    let days = _.range(0, diff, 1)
                        .map(m => {
                            let date = first.clone().add(m, 'day');
                            let exist = _.find(ads, (d: AttendanceTime) => date.isSame(d.date, 'date'));

                            if (exist) {
                                let { events } = exist;

                                return { date: date.format(DATE_FORMAT), events };
                            }

                            return { date: date.format(DATE_FORMAT), events: [] };
                        });

                    let sow = first.clone().isoWeekday(wd).isSame(start, 'date');

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

            let computedView = ko.computed({
                read: () => {
                    let iv = ko.unwrap(initialView);

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
            

            // update drag item
            $datas
                .subscribe((data: a.ChangeDateDto | null) => {
                    vm.computedOnedayDragItems(data, ko.unwrap($settings));
                    vm.computedTaskDragItems(data, ko.unwrap($settings));
                });

            isShowBreakTime.subscribe(value => {
                    if(!value){
                        events(_.chain(events())
                            .filter((evn) => { return !evn.extendedProps.isTimeBreak  })
                            .value());

                        updateEvents();

                        if (!_.find(vm.params.events(), (e) => { return _.get(e, 'extendedProps.isChanged') }) && !vm.params.screenA.removeList().length ) {
                            
                            setTimeout(() => {vm.params.screenA.dataChanged(false)}, 100);
                        }
                        return;
                    }
                    let data =  ko.unwrap(vm.params.$datas);
                    let {estimateZones} = data;
                    
                    _.forEach(estimateZones, etz => {
                            let {breakTimeSheets} = etz;
                            _.forEach(breakTimeSheets, bts => {
                                let start = moment(etz.ymd).set('hour', bts.start / 60).set('minute', bts.start % 60).toDate();
                                let end = moment(etz.ymd).set('hour', bts.end / 60).set('minute', bts.end % 60).toDate();
                                
                                let { manHrContents} = _.find(_.get(vm.params.$datas(), 'convertRes'), cr => moment(cr.ymd).isSame(moment(etz.ymd), 'days'));
                                let {no, breakTime} = bts;
                                let businessHours = ko.unwrap(vm.params.businessHours);

                                let bh = _.find(businessHours, bh => bh.dayOfWeek == start.getDay());
                                let startAsMinites = (moment(start).hour() * 60) + moment(start).minute();
                                let endAsMinites = (moment(end).hour() * 60) + moment(end).minute();
                                
                                if (startAsMinites >= _.get(bh, 'start', 0) && endAsMinites <= _.get(bh, 'end', 1440)) {
                                    events.push({
                                        id: randomId(),
                                        title: vm.$i18n('KDW013_79'),
                                        start,
                                        end,
                                        textColor: '',
                                        backgroundColor: BREAKTIME_COLOR,
                                        extendedProps: {
                                            no,
                                            breakTime,
                                            id: randomId(),
                                            status: 'normal',
                                            isTimeBreak: true,
                                            isChanged: false,
                                            taskBlock: {
                                                manHrContents,
                                                taskDetails: []
                                            }
                                        } as any
                                    });
                                }
                               
                            });
                        
                    });
                
                updateEvents();
                if (!_.find(vm.params.events(), (e) => { return _.get(e, 'extendedProps.isChanged') }) && !vm.params.screenA.removeList().length ) {
                    setTimeout(() => {vm.params.screenA.dataChanged(false)}, 100);
                }
            });

            // update drag item
            $settings
                .subscribe((settings: a.StartProcessDto | null) => {
                    vm.computedOnedayDragItems(ko.unwrap($datas), settings);
                    vm.computedTaskDragItems(ko.unwrap($datas), settings);
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
                let pre = document.createElement('pre');
                let prettify = 'xml';
                let code = '<com:stylefile set="FULLCALENDAR" />\n<com:scriptfile set="FULLCALENDAR" />';

                $el.append('Please add 2 tag at below to htmlHead ui defined:');

                // pretty message as html
                ko.applyBindingsToNode(pre, { prettify, code });

                // append message to root element
                $el.append(pre);

                return;
            }

            let clearSelection = () => {
                vm.selectedEvents = [];

                // clear selections
                _.each(vm.calendar.getEvents(), (e: EventApi) => {
                    if (e.borderColor === BLACK) {
                        e.setProp(GROUP_ID, '');

                        e.setProp(BORDER_COLOR, TRANSPARENT);
                    }
                });
            };

            let checkEditDialog = () => {
                let dfd = $.Deferred();
                let eventNotSave = _.find(vm.calendar.getEvents(), (e) => !_.get(e, 'extendedProps.id'));
                if ((vm.$view() == "edit" && vm.params.$settings().isChange) || (vm.$view() == "edit" && eventNotSave)) {
                    vm.$dialog
                        .confirm({ messageId: 'Msg_2094' })
                        .then((v: 'yes' | 'no') => {
                            if (v === 'yes') {
                                if (eventNotSave) {
                                    eventNotSave.remove();
                                }
                                popupPosition.event(null);
                                popupPosition.setting(null);
                            }
                            dataEvent.delete(false);
                            dfd.resolve(v);
                        });
                } else {
                    if (eventNotSave) {
                        eventNotSave.remove();
                    }
                    popupPosition.event(null);
                    popupPosition.setting(null);
                    dfd.resolve('yes');
                }
                return dfd.promise();
            }

            let viewButtons: ButtonSet = {
                'one-day': {
                    text: vm.$i18n('KDW013_10'),
                    click: (evt) => {
                        clearSelection();

                        if (vm.calendar.view.type !== 'timeGridDay') {

                            weekends(true);
                            $.Deferred()
                                .resolve(true)
                                .then(() => {
                                    
                                    checkEditDialog().done((v) => {
                                        if (v == 'yes') {
                                            activeClass(evt.target);

                                            if (ko.isObservable(initialView)) {
                                                initialView('oneDay');
                                            } else {
                                                vm.calendar.changeView('timeGridDay');
                                            }
                                            
                                        }
                                    });
                                      
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
                        
                        checkEditDialog().done((v) => {
                            if (v == 'yes') {
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
                        });


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
            let customButtons: ButtonSet = {
                'current-day': {
                    text: vm.$i18n('今日'),
                    click: () => {
                        clearSelection();
                        
                        if (moment(initialDate()).isSame(moment(new Date()), 'day')) {
                            return;
                        }
                        if (ko.isObservable(initialDate)) {
                            initialDate(new Date());
                        } else {
                            vm.calendar.gotoDate(formatDate(new Date()));
                        }
                        let sc = ko.unwrap(scrollTime);
                        vm.calendar.scrollToTime(formatTime(sc));
                    }
                },
                'next-day': {
                    text: vm.$i18n('▶'),
                    click: () => {
                        
                        checkEditDialog().done((v) => {
                            if (v == 'yes') {
                                 clearSelection();

                        if (ko.isObservable(initialDate)) {
                            let date = ko.unwrap(initialDate);
                            let view = ko.unwrap(initialView);
                            let vrange = ko.unwrap(validRange);
                            let { end } = vrange;

                            switch (view) {
                                case 'oneDay':
                                    let day = moment(date).add(1, 'day');

                                    if (end) {
                                        if (end == '9999-12-32') { end = '9999-12-31' }
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
                                    let nextDay = moment(date).add(1, 'week');

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
                                    let nextMonth = moment(date).add(1, 'month');

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
                        let sc = ko.unwrap(scrollTime);

                        vm.calendar.scrollToTime(formatTime(sc));
                            }
                        });
                       
                    }
                },
                'preview-day': {
                    text: vm.$i18n('◀'),
                    click: () => {
                        
                        checkEditDialog().done((v) => {
                            if (v == 'yes') {
                                 clearSelection();

                        if (ko.isObservable(initialDate)) {
                            let date = ko.unwrap(initialDate);
                            let view = ko.unwrap(initialView);
                            let vrange = ko.unwrap(validRange);
                            let { start } = vrange;

                            switch (view) {
                                case 'oneDay':
                                    let day = moment(date).subtract(1, 'day');

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
                                    let prevDay = moment(date).subtract(1, 'week');

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
                                    let prevMonth = moment(date).subtract(1, 'month');

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
                        let sc = ko.unwrap(scrollTime);

                        vm.calendar.scrollToTime(formatTime(sc));
                            }
                        });
                       
                    }
                   
                },
                'settings': {
                    text: '',
                    click: (evt) => {
                        
                        checkEditDialog().done((v) => {
                                if (v == 'yes') {
                                   let tg: HTMLElement = evt.target as any;
    
                                if (tg) {
                                    tg.classList.add(POWNER_CLASS_CPY);
        
                                    popupPosition.setting(tg);
                                } 
                            }
                        });
                        
                    }
                }
            };

            let headerToolbar: FullCalendar.ToolbarInput = {
                left: 'current-day,preview-day,next-day',
                center: '',
                right: 'settings'
            };
            let mapExProps = (ext) => {
                return {...ext,
                    taskBlock: {
                        caltimeSpan: { start: ext.taskBlock.start, end: ext.taskBlock.end },
                        taskDetails: _.map(ext.taskBlock.taskDetails, td => {
                            return {
                                taskItemValues: _.map(td.taskItemValues, tiv => { return { itemId: tiv.itemId, value: tiv.value } }),
                                supNo: td.supNo
                            };
                        });
                    }
                }
            }
            let getEvents = (): EventRaw[] => vm.calendar
                // get all event from calendar
                .getEvents()
                // except background event
                .filter(f => f.display !== 'background')
                // map EventApi to EventRaw
                .map((e) => ({
                    start: e.start,
                    end: e.end,
                    title: e.title,
                    backgroundColor: e.backgroundColor,
                    textColor: e.textColor,
                    extendedProps: {
                        ...mapExProps(e.extendedProps),
                id:e.id} 
                }));
            let mutatedEvents = () => {
                if (ko.isObservable(events)) {
                    // emit event except new event (no data)
                    events(getEvents().filter(({ extendedProps }) => !!extendedProps.id && extendedProps.status !== 'delete'));
                }
            };
            let updateEvents = () => {
                let sltds = vm.selectedEvents;
                let isSelected = (m: EventSlim) => _.some(sltds, (e: EventSlim) => (formatDate(_.get(e,'start')) === formatDate(_.get(m,'start')) && (formatDate(_.get(e,'end')) === formatDate(_.get(m,'end')) ) ));
                let data = ko.unwrap(params.$datas);
                
                let isLock = (lockStatus) => {
                    if (_.get(lockStatus, 'lockDailyResult', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockWpl', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockApprovalMontｈ', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockConfirmMonth', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockApprovalDay', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockConfirmDay', 1) == 0) { return true; }
                    if (_.get(lockStatus, 'lockPast', 1) == 0) { return true; }
                    return false;
                }
               
                let getEditable = (date, isTimeBreak) => {
                    let startDate = moment(_.get(data, 'workStartDate'));
                    let lockStatus = _.find(_.get(data, 'lockInfos'), li => { return moment(li.date).isSame(moment(date), 'days'); });
                    return startDate.isAfter(date) ? false : !(isTimeBreak && isLock(lockStatus));
                };
                let events = ko.unwrap<EventRaw[]>(params.events);
                
                //set editable
                _.forEach(events, e => {
                    e.editable = getEditable(formatDate(_.get(e, 'start')), e.extendedProps.isTimeBreak);
                });
                
                
                
                let isDuplicated = _.uniqBy(events, 'extendedProps.supportFrameNo').length < events.length;
                if (isDuplicated) {
                    let selectedEvent = events[0];
                    let {extendedProps } = selectedEvent;
                    let {employeeId,
                        id,
                        remarks,
                        status,
                        supportFrameNo,
                        workCD1,
                        workCD2,
                        workCD3,
                        workCD4,
                        workCD5,
                        workLocationCD,
                        workingHours,
                        isTimeBreak
                        } = extendedProps;
                    selectedEvent.extendedProps = {
                        ...extendedProps,
                        supportFrameNo : null
                    };

                }
                let mapppedEvents =    _.chain(events)
                    .filter(({ extendedProps }) => extendedProps.status !== 'delete')
                    .map((e) => ({
                        ...e,
                        id: randomId(),
                        start: formatDate(_.get(e,'start')),
                        end: formatDate(_.get(e,'end')),
                        [GROUP_ID]: isSelected(e) ? SELECTED : '',
                        [BORDER_COLOR]: isSelected(e) ? BLACK : TRANSPARENT,
                        extendedProps: {
                            ...e.extendedProps,
                            status: e.extendedProps.status || 'normal'
                        }
                    })).value();
                
                // clear old events
                vm.calendar.removeAllEvents();
                vm.calendar.removeAllEventSources();
                // set new events
                vm.calendar.setOption('events', mapppedEvents);
                // _.each(events, (e: EventRaw) => vm.calendar.addEvent(e));

                

                vm.selectedEvents = [];
            };
            let removeNewEvent = (event: EventApi | null) => {
                _.each(vm.calendar.getEvents(), (e: EventApi) => {
                    // remove new event (no save)
                    if (!e.title && e.extendedProps.status === 'new' && (!event || e.id !== event.id)) {
                        e.remove();
                    }
                });
            }

            let removeNotSaveEvents = (event: EventApi | null) => {

                _.each(vm.calendar.getEvents(), (e: EventApi) => {
                    // remove new event (empty data)
                    if (!e.extendedProps.id) {
                        e.remove();
                        dataEvent.delete(false);
                        popupPosition.event(null);
                        popupPosition.setting(null);
                    } else if (e.groupId === SELECTED) {
                        e.setProp(GROUP_ID, '');
                        // unselect events
                        e.setProp(BORDER_COLOR, TRANSPARENT);
                    }
                });
            }

            let draggerOne = new FC.Draggable($dgOne, {
                itemSelector: '.title',
                eventData: (el) => {
                    let id = el.getAttribute('data-id');
                    let unwraped = ko.unwrap<EventRaw[]>(onedayDragItems);

                    _.each(vm.calendar.getEvents(), (e: EventApi) => {
                        if (e.extendedProps.status === 'new' && !e.extendedProps.id) {
                            e.remove();
                        } else if (e.groupId === SELECTED) {
                            e.setProp(GROUP_ID, '');

                            e.setProp(BORDER_COLOR, TRANSPARENT);
                        }
                    });

                    if (id) {
                        let exist = _.find(unwraped, (e: EventRaw) => e.extendedProps.relateId === id);

                        if (exist) {
                            let { title, backgroundColor, extendedProps } = exist;

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

            let draggerTask = new FC.Draggable($dgTask, {
                itemSelector: '.title',
                eventData: (el) => {
                    let id = el.getAttribute('data-id');
                    let unwraped = ko.unwrap<EventRaw[]>(taskDragItems);

                    _.each(vm.calendar.getEvents(), (e: EventApi) => {
                        if (e.extendedProps.status === 'new' && !e.extendedProps.id) {
                            e.remove();
                        } else if (e.groupId === SELECTED) {
                            e.setProp(GROUP_ID, '');

                            e.setProp(BORDER_COLOR, TRANSPARENT);
                        }
                    });

                    if (id) {
                        let exist = _.find(unwraped, (e: EventRaw) => e.extendedProps.relateId === id);

                        if (exist) {
                            let { title, backgroundColor, extendedProps } = exist;

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
                eventOverlap: true,
                selectOverlap: false,
                eventLimit: true,
                views: {
                    timeGrid: {
                        eventLimit: 20
                    }
                },
                // rerenderDelay: 500,
                dateClick: (info) => {
                    let events = vm.calendar.getEvents();
                    let data = ko.unwrap(params.$datas);
                    let startDate = moment(_.get(data, 'workStartDate'));
                    let {lstIntegrationOfDaily} = data;
                    let hasEventNotSave = _.find(events, (e) => !_.get(e, 'extendedProps.id'));
                    
                    if (vm.$view() == "edit" && vm.params.$settings().isChange) {
                        vm.$dialog
                            .confirm({ messageId: 'Msg_2094' })
                            .then((v: 'yes' | 'no') => {
                                if (v === 'yes') {
                                    dataEvent.delete(false);
                                    popupPosition.event(null);
                                    popupPosition.setting(null);
                                    if (hasEventNotSave) {
                                        removeNotSaveEvents();
                                    }
                                }
                                dataEvent.delete(false);
                            });
                        return;
                    }

                    if (hasEventNotSave) {
                        removeNotSaveEvents();
                    }
                    let editableDay = vm.getEditableDay(info.date);
                    if (startDate.isAfter(formatDate(info.date)) || !editableDay) {
                        return;
                    }

                    let events = vm.calendar.getEvents();

                    let isHasTask = _.find(events, (e) => { return moment(e.start).isSameOrBefore(moment(info.date)) && moment(e.end).isAfter(moment(info.date)) });

                    if (isHasTask) {
                        return;
                    }

                     let eventInDay = _.chain(events)
                            .filter((evn) => { return moment(info.date).isSame(evn.start, 'days'); })
                            .sortBy('end')
                            .value();
                    
                     let frameNos =[];                    
                     _.forEach(eventInDay, e => _.forEach(e.extendedProps.taskBlock.taskDetails, td => { frameNos.push(td.supNo); }));
                    
                     let integrationOfDaily = _.find(lstIntegrationOfDaily, (id) => { return moment(info.date).isSame(moment(id.ymd), 'days'); });
                     _.forEach(_.filter(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.timeSheet.start.timeWithDay == null && ot.timeSheet.end.timeWithDay == null), ot => {
                                frameNos.push(ot.workNo);
                     })
                            
                            
                    
                    let newEvent = {
                            id: randomId(),
                            start: info.date,
                            end: moment(info.date).add(vm.params.slotDuration(), 'm').toDate(),
                            [BORDER_COLOR]: BLACK,
                            [GROUP_ID]: SELECTED,
                            extendedProps: {
                                status: 'new',
                                isChanged: true,
                                //作業枠利用設定
                                taskFrameUsageSetting: ko.unwrap((vm.params.$settings)),
                                //社員ID
                                employeeId: vm.params.employee() || vm.$user.employeeId,
                                //年月日
                                period: { start: info.date, end: moment(info.date).add(vm.params.slotDuration(), 'm').toDate() },
                                //現在の応援勤務枠
                                frameNos,                                
                                //工数実績作業ブロック
                                taskBlock: {
                                    caltimeSpan: { start: info.date, end: moment(info.date).add(vm.params.slotDuration(), 'm').toDate() },

                                    taskDetails: [{ supNo: null, taskItemValues : vm.getTaskValues() }]
                                },
                                //作業内容入力ダイアログ表示項目一覧
                                displayManHrRecordItems: _.get(ko.unwrap((vm.params.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []),
                                
                            }
                        };
                    
                    let event = vm.calendar
                        .addEvent(newEvent);

                    $caches.new(event);
                    let el: HTMLElement = vm.$el.querySelector(`[event-id="${event.id}"]`);

                    if (el) {
                        let { view } = vm.calendar;

                        vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view, noCheckSave: true });
                    }


                },
                dropAccept: () => !!ko.unwrap(true),
                dayHeaderContent: (opts: DayHeaderContentArg) => moment(opts.date).format('DD(ddd)'),
                selectAllow: ({ start, end }) => start.getDate() === end.getDate(),
                slotLabelContent: (opts: SlotLabelContentArg) => {
                    let { milliseconds } = opts.time;

                    let min = milliseconds / 60000;
                    let hour = Math.floor(min / 60);
                    let minite = Math.floor(min % 60);

                    return !minite ? `${hour}:00` : `${minite}`;
                },
                slotLabelClassNames: ({ time }) => {
                    let { milliseconds } = time;

                    let min = milliseconds / 60000;
                    let hour = Math.floor(min / 60);
                    let minite = Math.floor(min % 60);

                    return `${!minite ? 'fc-timegrid-slot-label-bold' : ''} fc-timegrid-slot-label-${hour}`;
                },
                slotLaneClassNames: ({ time }) => {
                    let { milliseconds } = time;
                    let min = milliseconds / 60000;
                    let hour = Math.floor(min / 60);
                    let brkt = ko.unwrap(params.breakTime);
                    let minite = Math.floor(min % 60);
                    let className = [`fc-timegrid-slot-lane-${hour}`];

                    // add breaktime class
                    if (brkt) {
                        let { startTime, endTime } = ko.unwrap(params.breakTime);

                        if (startTime <= min && min < endTime) {
                            className.push('fc-timegrid-slot-lane-breaktime');
                        }
                    }

                    return className.join(' ');
                },
                eventContent: (args: EventContentArg) => {
                    let { type } = args.view;
                    let { title, extendedProps } = args.event;
                    let { remarks } = extendedProps;

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
                dayHeaderDidMount : (arg, createElement) => {
                    if(vm.params.editable()){
                        let className = 'fav-' + $(arg.el).find('.fc-scrollgrid-sync-inner').parent().attr('data-date');
                        
                            $($(arg.el).find('.fc-scrollgrid-sync-inner')[0]).append(`<i class='favIcon ` + className + `' ></i>`);
                            setTimeout(function() { ko.applyBindingsToNode($('.favIcon'), { ntsIcon: { no: 229, size: '16px', width: 16, height: 16 } }); }, 300);
                    }
                }
                ,
                viewDidMount: ({ el, view }) => {
                    // render attendence time & total time by ko binding
                    if (['timeGridDay', 'timeGridWeek'].indexOf(view.type) > -1) {
                        let header = $(el).find('thead tbody');

                        if (header.length) {
                            let $days = header.find('tr:first');
                            let _events = document.createElement('tr');
                            let __times = document.createElement('tr');

                            header.append(_events);
                            header.append(__times);
                            $.Deferred()
                                .resolve(true)
                                .then(() => {
                                    $days.on('mousedown', (evt: JQueryEvent) => {
                                        if ($(evt.target).closest('.fc-col-header-cell.fc-day .favIcon').length > 0) {
                                            
                                            let event = _.find(vm.params.events(), d => moment(d.start).isSame(moment(evt.target.classList[1].replace("fav-", "")), 'days'));
                                            if (event) {
                                                let className = evt.target.classList[1];
                                                $(".popup-area-g").ntsPopup({
                                                    trigger: '.' + className,
                                                    position: {
                                                        my: "left top",
                                                        at: "left bottom",
                                                        of: '.' + className
                                                    },
                                                    showOnStart: false,
                                                    dismissible: false
                                                });
                                            } else {
                                                $(".popup-area-g").ntsPopup("destroy");
                                            }
                                           
                                        }
                                    });
                                    $days
                                        // select day event
                                        .on('click', (evt: JQueryEvent) => {
                                            
                                            if ($(evt.target).closest('.fc-col-header-cell.fc-day .favIcon').length > 0) {
                                                //click mở màn G
                                                let date =  evt.target.classList[1].replace("fav-", "");
                                                let eventInDay = _.chain(vm.params.screenA.events())
                                                    .filter((evn) => { return moment(date).isSame(evn.start, 'days'); })
                                                    .filter((evn) => { return !evn.extendedProps.isTimeBreak})
                                                    .filter((evn) => { return evn.start && evn.end })
                                                    .filter((evn) => { return getTimeOfDate(evn.start) && (evn.end) })
                                                    .sortBy('end')
                                                    .value();
                                                
                                                let taskBlocks = _.map(eventInDay, e => {
                                                    let taskContents = []
                                                    _.forEach(_.get(e, 'extendedProps.taskBlock.taskDetails', []), td => {
                                                        let tcs = [],
                                                            attendanceTime = null;
                                                        _.forEach(td.taskItemValues, ti => {
                                                            if (ti.itemId == 3) {
                                                                attendanceTime = ti.value;
                                                            }
                                                            tcs.push({ itemId: ti.itemId, taskCode: ti.value });
                                                        });
                                                        taskContents.push({ frameNo: td.supNo, attendanceTime, taskContents: tcs });
                                                    });
                                                   
                                                    return { startTime: getTimeOfDate(e.start), endTime: getTimeOfDate(e.end), taskContents };
                                                });
                                                setTimeout(() => { $('.input-g').focus(); }, 100);
												setTimeout(() => {
													nts.uk.ui.errors.clearAll();
													jQuery('button.btn-error.small.danger').appendTo('.popup-area-g .textEditor.pb10');
												}, 100)

                                                //set lại phần update để nó không bị ảnh hưởng
                                                vm.params.screenA.oneDayFavoriteSet(null);
                                                vm.params.screenA.oneDayFavTaskName('');
                                                vm.params.screenA.taskBlocks(taskBlocks);
												vm.params.screenA.popupTitle('KDW013_72');
												vm.params.screenA.btnContent('KDW013_1');
                                            } else {
                                                
                                                let target = $(evt.target).closest('.fc-col-header-cell.fc-day').get(0) as HTMLElement;

                                                if (target && target.tagName === 'TH' && target.dataset['date']) {
                                                    let date = moment.utc(target.dataset['date'], 'YYYY-MM-DD').toDate();

                                                    if (_.isDate(date) && ko.isObservable(initialDate)) {
                                                        initialDate(date);
                                                    }
                                                }
                                            }
                                        });
                                    
                                })
                                .then(() => {
                                    // binding sum of work time within same day
                                    ko.applyBindingsToNode(__times, { component: { name: 'fc-times', params: { timesSet: timesSet, screenA: vm.params.screenA } } }, vm);
                                    // binding note for same day
                                    ko.applyBindingsToNode(_events, { component: { name: 'fc-event-header', params: { screenA: vm.params.screenA,  data: attendancesSet, setting: $settings } } }, vm);
                                })
                                .then(() => vm.calendar.setOption('height', '100px'))
                                .then(() => {
                                    // update height
                                    let fce = vm.calendar.el.getBoundingClientRect();

                                    if (fce) {
                                        let { top } = fce;
                                        let  innerHeight  = $('.fc-sidebar').height();

                                        vm.calendar.setOption('height', `${innerHeight }px`);

                                        let sidebar = $('.fc-sidebar').get(0);

                                        if (sidebar) {
                                            ko.applyBindingsToNode(sidebar, { 'sb-resizer': vm.calendar }, vm);
                                        }
                                    }
                                    
                                    //add check button 
                                    let checkBtn =  $('<div class="fc-ckb-break-time">').insertBefore('.fc-settings-button').get(0);
                                    if (checkBtn) {
                                       
                                        
                                        ko.applyBindingsToNode(checkBtn, { ntsCheckBox: { checked: isShowBreakTime, text: vm.$i18n('KDW013_66') } });
                                        
                                    }

                                    let setting = $('.fc-settings-button').get(0);

                                    if (setting) {
                                        ko.applyBindingsToNode(setting, { icon: 3, size: '20px' }, vm);
                                    }
                                })
                                .then(() => {
                                    let sc = ko.unwrap(scrollTime);

                                    vm.calendar.scrollToTime(formatTime(sc));
                                })
                                .then(() => {
                                    $(vm.$el)
                                        .find('.fc-sidebar')
                                        .css({ 'width': '255px', 'height': 'calc(100vh - 148px)', 'overflow-y': 'auto' });

                                    vm.calendar.updateSize();
                                });
                        }
                    }
                    let sc = ko.unwrap(scrollTime);
                    vm.calendar.scrollToTime(formatTime(sc));
                },
                slotLabelClassNames: (arg) => {
                    let result = moment(arg.date).minutes() % 60 != 0 ? 'border-dashed' : '';

                    return result ;
                },
                slotLaneClassNames: (arg) => {
                    let result = moment(arg.date).minutes() % 60 != 0 ? 'border-dashed' : '';

                    return result + ' ' + (vm.isOTTime(arg.date) ? 'otTime' : '');
                }
                ,
                eventClick: ({ el, event, jsEvent, noCheckSave}) => {
                    let shift = ko.unwrap<boolean>(dataEvent.shift);
                    /**
                     * Note: remove group id before change other prop
                     */
                    
                    let events = vm.calendar.getEvents();
                    
                    let hasEventNotSave = _.find(events, (e) => !_.get(e, 'extendedProps.id'));
                    
                    let data = ko.unwrap(params.$datas);
                    let startDate = moment(_.get(data, 'workStartDate'));
                    
                    if (vm.$view() == "edit" && vm.params.$settings().isChange) {
                        vm.$dialog
                            .confirm({ messageId: 'Msg_2094' })
                            .then((v: 'yes' | 'no') => {
                                if (v === 'yes') {
                                    if (hasEventNotSave) {
                                        removeNotSaveEvents();
                                    } else {
                                        dataEvent.delete(false);
                                        popupPosition.event(null);
                                        popupPosition.setting(null);
                                    }
                                }

                                dataEvent.delete(false);

                            });
                        return;
                    }
                    
                    if (hasEventNotSave && !noCheckSave && !moment(event.start).isSame(popupData.event().start)) {
                        removeNotSaveEvents();
                        //return;
                    }
                    
                    let editableDay = vm.getEditableDay(event.start);
                    let ev = _.find(vm.params.events(), e => e.extendedProps.id == event.extendedProps.id);
                    if (startDate.isAfter(formatDate(event.start)) || !editableDay || !_.get(ev, 'editable', true)) {
                        return;
                    }

                    // remove new event (with no data) & background event
                    removeNewEvent(event);

                    // get all event with border is black
                    let seletions = () => _.filter(vm.calendar.getEvents(), (e: EventApi) => e.borderColor === BLACK);
                    
                    // single select
                    if (!shift) {
                        _.each(seletions(), (e: EventApi) => {
                            e.setProp(GROUP_ID, '');
                            e.setProp(BORDER_COLOR, TRANSPARENT);
                        });

                        event.setProp(BORDER_COLOR, BLACK);

                        let { status } = event.extendedProps;

                        if (status === 'new') {
                            vm.$view('edit');
                        } else {
                            vm.$view('view');
                        }
                        if (!event.extendedProps.isTimeBreak) {
                            let frameNos = _.get(_.maxBy(_.filter(events, (e) => (moment(e.start).isSame(moment(event.start), 'days') && !e.extendedProps.isTimeBreak)), function(e) { return _.last(e.extendedProps.taskBlock.taskDetails).supNo; }), 'extendedProps.frameNos', []);
                            event.setExtendedProp('frameNos',frameNos);
                            popupData.event(event);
                        }
                        // update exclude-times
                        let sameDayEvent = _
                            .chain(vm.calendar.getEvents())
                            .filter(({ start, id }) => id !== event.id && moment(start).isSame(event.start, 'day'))
                            .map(({ start, end }) => ({ startTime: getTimeOfDate(start), endTime: getTimeOfDate(end) }))
                            .value();

                        popupData.excludeTimes(sameDayEvent);

                        if (!event.extendedProps.isTimeBreak) {
                            // show popup on edit mode
                            popupPosition.event(el);

                        }

                        // update mouse pointer
                        let { screenX, screenY } = jsEvent;
                        
                     
                        if (vm.params.initialView() === "oneDay" && screenX === 0 && screenY === 0) {
                            let width = window.innerWidth;
                            let height = window.innerHeight;
                            dataEvent.pointer({ screenX: width / 2, screenY: height / 2 });
                        } else {
                            dataEvent.pointer({ screenX, screenY });
                        }
                      
                    } else {
                        // multi select
                        let [first] = seletions();

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

                    let selecteds = seletions();

                    if (!!selecteds.length) {
                        // group selected event & disable resizeable
                        _.each(selecteds, (e: EventApi) => {
                            e.setProp(GROUP_ID, SELECTED);
                        });
                    }
                    //$('#edit').focus();
                }
                ,
                eventDragStart: (arg: EventDragStartArg) => {
                    let { event } = arg;
                    let {
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
                    let { event, relatedEvents } = arg;
                    let { start, end, id, title, extendedProps, borderColor, groupId } = event;
                    let rels = relatedEvents.map(({ start, end }) => ({ start, end }));

                    vm.selectedEvents = [{ start, end }, ...rels];

                    event.setExtendedProp('isChanged', true);
                    let data = ko.unwrap(vm.params.$datas);
                    let startDate = moment(_.get(data, 'workStartDate'));
                    let editableDay =  vm.getEditableDay(start);
                    if (startDate.isAfter(formatDate(start)) || !editableDay) {
                        arg.revert();
                        return;
                    }
                    
                    // update data sources
                    //mutatedEvents();

                    // add new event (no save) if new event is dragging
                    if (!title && extendedProps.status === 'new' && !rels.length) {
                        let event = vm.calendar
                            .addEvent({
                                id,
                                start,
                                end,
                                borderColor,
                                groupId,
                                extendedProps: {
                                    ...extendedProps,
                            isChanged:true
                                }
                            });

                        $caches.new(event);
                        let el: HTMLElement = vm.$el.querySelector(`[event-id="${event.id}"]`);

                        if (el) {
                            let { view } = vm.calendar;

                            vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view, noCheckSave: true});
                        }
                    }
                    
                    //check for resize fit with space
                    
                   
                    
                    //check override events
                    
                     let IEvents = _.chain(events())
                            .filter((evn) => { return moment(start).isSame(evn.start, 'days'); })
                            .filter((evn) => { return evn.extendedProps.id != extendedProps.id })
                            .sortBy('end')
                            .value();

                     let ovrEvent = _.find(IEvents, e => !!_.find([].concat(event, relatedEvents), ce =>
                         (moment(ce.start).isSameOrAfter(e.start) && moment(ce.start).isBefore(e.end)) ||
                         (moment(ce.end).isAfter(e.start) && moment(ce.end).isSameOrBefore(e.end))
                     ));

                     if ((ovrEvent && !extendedProps.isTimeBreak && !ovrEvent.extendedProps.isTimeBreak)  ||  (extendedProps.isTimeBreak && _.get(ovrEvent,'isTimeBreak',false))) {
                         arg.revert();
                         return;
                     }
                    
                    let selecteds = _.filter(vm.calendar.getEvents(), (e: EventApi) => e.borderColor === BLACK);
                    let relBk = _.find(relatedEvents,re => re.extendedProps.isTimeBreak);
                    if (extendedProps.isTimeBreak || relBk) {
                        if (arg.delta.days != 0) {
                            arg.revert();
                            return;
                        }
                        
                        let breakInday = _.filter(events(), e => moment(e.start).isSame(start, 'days') && e.extendedProps.isTimeBreak);
                        let orverideBreak = _.filter(breakInday, br => moment(br.start).isSameOrBefore(start) && moment(br.end).isAfter(start) && (br.extendedProps.id != extendedProps.id));
                        if (orverideBreak.length) {
                            vm.revertEvent([arg.oldEvent], $caches);
                            return;
                        }
                        
                        let businessHours = ko.unwrap(vm.params.businessHours);
                        
                        let bh = _.find(businessHours, bh => bh.dayOfWeek == start.getDay());
                        
                        let startAsMinites = (moment(start).hour() * 60) + moment(start).minute();
                        let endAsMinites = (moment(end).hour() * 60) + moment(end).minute();
                        if (startAsMinites < _.get(bh, 'start', 0) || endAsMinites > _.get(bh, 'end', 1440) || !moment(start).isSame(end,'days')){
                            vm.revertEvent([arg.oldEvent], $caches);
                            return;
                        }
                        vm.params.screenA.dataChanged(true);
                        mutatedEvents();
                        return;
                    }
                    
                    if (arg.relatedEvents.length == 0) {
                        let oEvents = [];

                        if (IEvents.length > 1) {
                            for (let i = 0; i < IEvents.length - 1; i++) {
                                let cEvent = IEvents[i];
                                let nEvent = IEvents[i + 1];

                                let isEndOverrideBetween =
                                    moment(end).isAfter(moment(nEvent.start)) &&
                                    moment(end).isSameOrBefore(moment(nEvent.end)) &&
                                    moment(start).isBefore(moment(nEvent.start)) &&
                                    moment(start).isSameOrAfter(moment(cEvent.end));

                                if (isEndOverrideBetween)
                                    oEvents.push({ start: nEvent.start, end: nEvent.end });
                            }
                        }
                        if (oEvents.length) {
                            let [first] = oEvents;
                            let currentEvent = _.find(vm.calendar.getEvents(), ['extendedProps.id', extendedProps.id]);
                            currentEvent.setEnd(first.start);
                            vm.params.screenA.dataChanged(true);
                        } else {
                            oEvents = _.chain(IEvents)
                                .filter((evn) => {
                                    let isStartOverride = moment(start).isSameOrAfter(moment(evn.start)) && moment(start).isBefore(moment(evn.end));
                                    let isEmbrace = moment(start).isSameOrBefore(moment(evn.start)) && moment(end).isSameOrAfter(moment(evn.end));
                                    let isEndOverride = moment(end).isAfter(moment(evn.start)) && moment(end).isSameOrBefore(moment(evn.end)) && moment(start).isSameOrBefore(moment(evn.start));
                                    let isEmbraced = moment(start).isSameOrAfter(moment(evn.start)) && moment(end).isSameOrBefore(moment(evn.end));
                                    let isNotTimeBreak = !evn.extendedProps.isTimeBreak;
                                    return (isStartOverride || isEmbrace || isEndOverride || isEmbraced) && isNotTimeBreak;

                                })
                                .value();

                            if (oEvents.length) {
                                
                                 vm.revertEvent([arg.oldEvent], $caches);
                                return;
                            }
                        }
                    }else{
                        
                        let { dataEvent } = vm;
                        
                        let [secondEvent] = arg.relatedEvents;
                        
                        if ( ko.unwrap<boolean>(dataEvent.shift) && arg.relatedEvents.length == 1 && moment(end).isSame(moment(secondEvent.start))) {
                            
                            IEvents = _.chain(events())
                            .filter((evn) => { return moment(start).isSame(evn.start, 'days'); })
                            .filter((evn) => { return evn.extendedProps.id != secondEvent.extendedProps.id })
                            .sortBy('end')
                            .value();
                            
                            let oEvents = [];
                            for (let i = 0; i < IEvents.length - 1; i++) {
                                let cEvent = IEvents[i];
                                let nEvent = IEvents[i + 1];

                                let isEndOverrideBetween =
                                    moment(secondEvent.end).isAfter(moment(nEvent.start)) &&
                                    moment(secondEvent.end).isSameOrBefore(moment(nEvent.end)) &&
                                    moment(secondEvent.start).isBefore(moment(nEvent.start)) &&
                                    moment(secondEvent.start).isSameOrAfter(moment(cEvent.end));

                                if (isEndOverrideBetween)
                                    oEvents.push({ start: nEvent.start, end: nEvent.end });
                            }

                            if (oEvents.length) {
                                let [first] = oEvents;
                                let sEvent = _.find(vm.calendar.getEvents(), { 'groupId': 'selected', 'start': secondEvent.start, 'end': secondEvent.end });
                                sEvent.remove();
                                
                                 let newEvent = vm.calendar
                                    .addEvent({
                                        id: randomId(),
                                        backgroundColor:sEvent.backgroundColor,
                                        title:sEvent.title,
                                        start: sEvent.start,
                                        end: first.start,
                                        borderColor:sEvent.borderColor,
                                        groupId:sEvent.groupId,
                                        extendedProps:sEvent.extendedProps
                                    });
                                $caches.new(newEvent);

                            }                            
                        }
                    }

                    
                    vm.params.screenA.dataChanged(true);
                    mutatedEvents();
                    let ids = [].concat(event.id, _.map(arg.relatedEvents, re => re.id));              

                        let getFrameNos = (events) => {
                            let vm = this;
                            let data = ko.unwrap(vm.params.$datas());
                            let {lstIntegrationOfDaily} = data;
                            let maxNo = 20;
                            let resultNos = [];
                            for (let i = 1; i <= maxNo; i++) {
                                let event = _.find(events, e => _.find(_.get(e, 'extendedProps.taskBlock.taskDetails', []), ['supNo', i]));
                                let integrationOfDaily = _.find(lstIntegrationOfDaily, (id) => { return moment(start).isSame(moment(id.ymd), 'days'); });
                                let ouenTime = _.find(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.timeSheet.start.timeWithDay == null && ot.timeSheet.end.timeWithDay == null && ot.workNo == i)
                                if (!event && !ouenTime) {
                                    resultNos.push(i);
                                }
                            }
                            return resultNos;
                        };
                        let tempEs = events();
                        let eventInDay = _.chain(events())
                                    .filter((e) => { return moment(start).isSame(e.start, 'days'); })
                                    .filter((e) => { return ids.indexOf(e.extendedProps.id) == -1 })
                                    .sortBy('end')
                                    .value();
                        let frameNos = getFrameNos(eventInDay);
                        
                        _.forEach(ids, id => {
                            let evn = _.find(tempEs, e => e.extendedProps.id == id);
                            let tds = evn.extendedProps.taskBlock.taskDetails;
                            let startMinutes = (moment(evn.start).hour() * 60) + moment(evn.start).minute();
                            let endMinutes = (moment(evn.end).hour() * 60) + moment(evn.end).minute();
                            _.forEach(tds, td => {
                                td.supNo = frameNos[0];
                                frameNos.shift();
                                _.forEach(td.taskItemValues, tiv => {
                                    if (tiv.itemId == 1) {
                                        tiv.value = startMinutes;
                                    }
                                    if (tiv.itemId == 2) {
                                        tiv.value = endMinutes;
                                    }
                                });
                            });
                            evn.extendedProps.taskBlock.taskDetails = tds;
                            evn.extendedProps.isChanged = true;
                            evn.extendedProps.taskBlock.caltimeSpan = { start: evn.start, end: evn.end };
                            evn.extendedProps.period = { start: evn.start, end: evn.end };
                            tempEs[_.findIndex(tempEs, e => e.extendedProps.id == id)] = evn;
                        });
                        events(tempEs);
                        updateEvents();
                        if (arg.delta.days != 0 && !ko.unwrap<boolean>(dataEvent.shift)) {
                            _.forEach([].concat(arg.oldEvent, relatedEvents), e => {

                                let removeList = vm.params.screenA.removeList;
                                let removeDate = _.find(removeList(), (ri) => moment(ri.date).isSame(moment(arg.oldEvent.start), 'days'));
                                let supNos = _.map(_.get(e, 'extendedProps.taskBlock.taskDetails', []), td => td.supNo);
                                if (removeDate) {
                                    removeDate.supNos.push(...supNos);
                                } else {
                                    removeList.push({ date: moment(arg.oldEvent.start).startOf('day').toDate(), supNos });
                                }
                            });
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
                    let { event } = arg;
                    let { start, end, title, backgroundColor, extendedProps, id, borderColor, groupId } = event;

                    vm.selectedEvents = [{ start, end }];
					event.setExtendedProp('isChanged', true);
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
                                extendedProps:{
									...extendedProps,
									isChanged:true
								}
                            }));
                    }
                    
                    if (extendedProps.isTimeBreak) {
                        
                        //valid another day
                        if (!moment(arg.oldEvent.end).isSame(end, 'days')) {
                            vm.revertEvent([arg.oldEvent], $caches);
                            return;
                        }
                        let breakInday = _.filter(events(), e => moment(e.start).isSame(start, 'days') && e.extendedProps.isTimeBreak);
                        let orverideBreak = _.filter(breakInday, br => moment(br.start).isBefore(end) && (br.extendedProps.id != extendedProps.id));
                        if (orverideBreak.length) {
                            vm.revertEvent([arg.oldEvent], $caches);
                            return;
                        }
                        
                        let businessHours = ko.unwrap(vm.params.businessHours);
                        
                        let bh = _.find(businessHours, bh => bh.dayOfWeek == start.getDay());
                        
                        let endAsMinites = (moment(end).hour() * 60) + moment(end).minute();
                        if (endAsMinites > _.get(bh, 'end', 1440)){
                            vm.revertEvent([arg.oldEvent], $caches);
                            return;
                        }
                        return;
                    }
                    
                    
                    //check override
                    
                  let oEvents =  
                  _.chain(events())
                  .filter((evn)=>{ return moment(start).isBefore(evn.start) && moment(evn.end).isSameOrBefore(end); })
                  .sortBy('end')
                  .value();
                  
                  if (oEvents.length) {
                      let [first] = oEvents;
                      //set end time for min start event
                      let currentEvent = _.find(vm.calendar.getEvents(), ['extendedProps.id', extendedProps.id]);
                      currentEvent.setEnd(first.start);

                      let last = _.last(oEvents);
                      //check if end > lastOverridedEvent end
                      if (moment(last.end).isBefore(moment(end))) {
                          
                          vm.calendar
                              .addEvent({
                                  id: randomId(),
                                  backgroundColor,
                                  title,
                                  start: last.end,
                                  end,
                                  borderColor,
                                  groupId,
                                  extendedProps
                              });
                      }

                      //if oEvents.length >=2, need create event between it 
                      if (oEvents.length >= 2) {
                          
                        //get space between
                          let spaces = [];
                          for (i = 0; i < oEvents.length -1 ; i++) {
                              let cEvent = oEvents[i];
                              let nEvent = oEvents[i + 1];
                              if (moment(cEvent.end).isBefore(moment(nEvent.start)))
                                  spaces.push({ start: cEvent.end, end: nEvent.start });
                              
                          }
                          
                          //after get space, create event
                          _.forEach(spaces, ({start,end}) => {
                              vm.calendar
                                  .addEvent({
                                      id: randomId(),
                                      backgroundColor,
                                      title,
                                      start,
                                      end,
                                      borderColor,
                                      groupId,
                                      extendedProps
                                  });
                          });
                      }

                  }
				  
                   let tempEs = [...events()];
                    _.forEach(tempEs, (evn) => {
                        if (evn.extendedProps.id == extendedProps.id) {
                            evn.extendedProps.isChanged = true;
                            evn.extendedProps.taskBlock.caltimeSpan = { start: evn.start, end: evn.end };
                            evn.extendedProps.period = { start: evn.start, end: evn.end };
                        };
                    });
                    events(tempEs);
                    updateEvents();

                },
                eventResizeStop: ({ el, event }) => {
                    console.log('stop', event.extendedProps);
                },
                select: ({ start, end }) => {
                    
                    let data = ko.unwrap(params.$datas);
                    let startDate = moment(_.get(data, 'workStartDate'));
                    let {lstIntegrationOfDaily} = data;
                    let editableDay =  vm.getEditableDay(start);
                    if (startDate.isAfter(formatDate(start)) || !editableDay) {
                        vm.calendar.unselect();
                        return;
                    }

                    // clean selection
                    vm.calendar.unselect();

                    // rerender event (deep clean selection)
                    updateEvents();
                     let eventInDay = _.chain(events())
                            .filter((evn) => { return moment(evn.start).isSame(moment(start), 'days'); })
                            .sortBy('end')
                            .value();
                    let frameNos = [];
                    _.forEach(eventInDay, e => _.forEach(e.extendedProps.taskBlock.taskDetails, td => { frameNos.push(td.supNo); }));
                    let integrationOfDaily = _.find(lstIntegrationOfDaily, (id) => { return moment(start).isSame(moment(id.ymd), 'days'); });
                    _.forEach(_.filter(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.timeSheet.start.timeWithDay == null && ot.timeSheet.end.timeWithDay == null), ot => {
                        frameNos.push(ot.workNo);
                    });
                    let newEvent = {
                        id: randomId(),
                        start: start,
                        end: end,
                        [BORDER_COLOR]: BLACK,
                        [GROUP_ID]: SELECTED,
                        extendedProps: {
                            status: 'new',
                            //作業枠利用設定
                            taskFrameUsageSetting: ko.unwrap((vm.params.$settings)),
                            //社員ID
                            employeeId: vm.params.employee() || vm.$user.employeeId,
                            //年月日
                            period: { start, end },
                            //現在の応援勤務枠
                            frameNos,
                            //工数実績作業ブロック
                            taskBlock: {
                                caltimeSpan: { start: start, end: end },

                                taskDetails: [{ supNo: null, taskItemValues: vm.getTaskValues() }]
                            },
                            //作業内容入力ダイアログ表示項目一覧
                            displayManHrRecordItems: _.get(ko.unwrap((vm.params.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []),

                        }
                    };

                    // add new event from selected data
                    let event = vm.calendar
                        .addEvent(newEvent);

                    $caches.new(newEvent);
                    let el: HTMLElement = vm.$el.querySelector(`[event-id="${newEvent.id}"]`);

                    if (el) {
                        let { view } = vm.calendar;

                        vm.calendar.trigger('eventClick', { el, event, jsEvent: new MouseEvent('click'), view , noCheckSave: true});
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
                    let {
                        title,
                        start,
                        backgroundColor,
                        textColor,
                        extendedProps
                    } = event;
                    let data = ko.unwrap(params.$datas);
                    let startDate = moment(_.get(data, 'workStartDate'));
                    let {lstIntegrationOfDaily} = data;
                    let editableDay = vm.getEditableDay(start);
                    if (startDate.isAfter(formatDate(start)) || !editableDay) {
                        event.remove();
                        return;
                    }
                    let sd = ko.unwrap(params.slotDuration);
                    let end = moment(start).add(sd, 'minute').toDate();
                
                    // remove drop event
                    event.remove();

                    vm.selectedEvents = [{ start, end }];
                  
                    // add cloned event to datasources
                    
                    let isTaskDrop = _.find(vm.taskDragItems(), task => task.extendedProps.favId == extendedProps.favId);
                    
                   
                    
                    if (isTaskDrop) {
                        let taskItemValues = _.map(_.get(extendedProps, 'dropInfo.favoriteContents', []), ({itemId, taskCode}) => { return { itemId, value: taskCode } });
                        let isHasEventInDropZone = _.chain(events())
                            .filter((evn) => { return moment(evn.start).isSameOrBefore(start) && moment(evn.end).isAfter(start)})
                            .filter((evn) => { return evn.extendedProps.id != extendedProps.id })
                            .sortBy('end')
                            .value();
                        
                        if (isHasEventInDropZone.length) {
                            return;
                        }
                        
                        let wg = {
                            workCD1: _.get(extendedProps, 'dropInfo.favoriteContents[0].taskCode', null),
                            workCD2: _.get(extendedProps, 'dropInfo.favoriteContents[1].taskCode', null),
                            workCD3: _.get(extendedProps, 'dropInfo.favoriteContents[2].taskCode', null),
                            workCD4: _.get(extendedProps, 'dropInfo.favoriteContents[3].taskCode', null),
                            workCD5: _.get(extendedProps, 'dropInfo.favoriteContents[4].taskCode', null),
                        }
                        
                        let eventInDay = _.chain(events())
                            .filter((evn) => { return moment(start).isSame(evn.start, 'days'); })
                            .filter((evn) => { return evn.extendedProps.id != extendedProps.id })
                            .sortBy('end')
                            .value();
                        
                        let frameNos = [];
                        _.forEach(eventInDay, e => _.forEach(e.extendedProps.taskBlock.taskDetails, td => { frameNos.push(td.supNo); }));
                        let integrationOfDaily = _.find(lstIntegrationOfDaily, (id) => { return moment(start).isSame(moment(id.ymd), 'days'); });
                        _.forEach(_.filter(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.timeSheet.start.timeWithDay == null && ot.timeSheet.end.timeWithDay == null), ot => {
                            frameNos.push(ot.workNo);
                        })
                        let startMinutes = (moment(start).hour() * 60) + moment(start).minute();
                        let endMinutes = (moment(end).hour() * 60) + moment(end).minute();
                        let getFrameNo = (events) => {
                            let vm = this;
                            let data = ko.unwrap(vm.params.$datas());
                            let {lstIntegrationOfDaily} = data;
                            let maxNo = 20;
                            let resultNo;
                            for (let i = 1; i <= maxNo; i++) {
                                let event = _.find(events, e => _.find(_.get(e, 'extendedProps.taskBlock.taskDetails', []), ['supNo', i]));
                                let integrationOfDaily = _.find(lstIntegrationOfDaily, (id) => { return moment(start).isSame(moment(id.ymd), 'days'); });
                                let ouenTime = _.find(_.get(integrationOfDaily, 'ouenTimeSheet', []), ot => ot.timeSheet.start.timeWithDay == null && ot.timeSheet.end.timeWithDay == null && ot.workNo == i)
                                if (!event && !ouenTime) {
                                    resultNo = i;
                                    break;
                                }
                            }
                            return resultNo;
                        };

                        taskItemValues.push({ itemId: 1, value: startMinutes });
                        taskItemValues.push({ itemId: 2, value: endMinutes });
                        taskItemValues.push({ itemId: 3, value: endMinutes - startMinutes });
                        let taskDetails = [{ supNo: _.isEmpty(eventInDay) ? 1 : getFrameNo(eventInDay), taskItemValues }];
                            events.push({
                                title: getTitles(taskDetails, vm.params.$settings().tasks, vm.params.$settings().taskFrameUsageSetting),
                                start,
                                end,
                                textColor,
                                backgroundColor,
                                extendedProps: {
                                ...extendedProps,
                                id: randomId(),
                                isTimeBreak:false,
                                status: 'update',
                                //作業枠利用設定
                                taskFrameUsageSetting: ko.unwrap((vm.params.$settings)),
                                //社員ID
                                employeeId: vm.params.employee() || vm.$user.employeeId,
                                //年月日
                                period: {  start,  end },
                                //現在の応援勤務枠
                                frameNos,                                
                                //工数実績作業ブロック
                                taskBlock: {
                                    caltimeSpan: { start,  end },

                                    taskDetails
                                },
                                //作業内容入力ダイアログ表示項目一覧
                                displayManHrRecordItems: _.get(ko.unwrap((vm.params.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []),
                            } as any
                        });
                    }else {
                        //drop by day
                        //remove event in day
                        events(_.chain(events())
                            .filter((evn) => { return !moment(start).isSame(evn.start, 'days') || (moment(start).isSame(evn.start, 'days') && evn.extendedProps.isTimeBreak) })
                            .filter((evn) => { return evn.extendedProps.id != extendedProps.id })
                            .value());
                        
                        
                        // add event   
                        _.each( _.get(extendedProps, 'dropInfo.taskBlockDetailContents', []), task => {
                            let timeStart = moment(start).set('hour', task.startTime / 60).set('minute', task.startTime % 60).toDate();
                            let timeEnd = moment(start).set('hour', task.endTime / 60).set('minute', task.endTime % 60).toDate();
                            let workCDs = _.chain(task.taskContents).map(task => task.taskContent.taskCode).value();
                            let wg = {
                                workCD1: _.get(_.find(_.get(task, 'taskContents[0].taskContent', []), tc => tc.itemId == 4), 'taskCode', null),
                                workCD2: _.get(_.find(_.get(task, 'taskContents[0].taskContent', []), tc => tc.itemId == 5), 'taskCode', null),
                                workCD3: _.get(_.find(_.get(task, 'taskContents[0].taskContent', []), tc => tc.itemId == 6), 'taskCode', null),
                                workCD4: _.get(_.find(_.get(task, 'taskContents[0].taskContent', []), tc => tc.itemId == 7), 'taskCode', null),
                                workCD5: _.get(_.find(_.get(task, 'taskContents[0].taskContent', []), tc => tc.itemId == 8), 'taskCode', null),
                            }
                            let taskDetails = []
                            _.forEach(_.get(task, 'taskContents'), tc => {
                                
                                let taskdetail = _.map(tc.taskContent, tcont => { return { itemId: tcont.itemId, value: tcont.taskCode }; });
                                taskdetail.push({ itemId: 3, value: tc.attendanceTime });
                                taskDetails.push({ supNo: tc.frameNo, taskItemValues: taskdetail });
                            });
                            //map item start , end between
                            _.forEach(taskDetails, td => {
                                td.taskItemValues.push({ itemId: 1, value: task.startTime });
                                td.taskItemValues.push({ itemId: 2, value: task.endTime });
                            });
                            events.push({
                                title: getTitles(taskDetails, vm.params.$settings().tasks, vm.params.$settings().taskFrameUsageSetting),
                                start : timeStart,
                                end : timeEnd,
                                textColor,
                                backgroundColor: getBackgroundColor(wg.workCD1, vm.params.$settings().tasks),
                                extendedProps: {
                                ...extendedProps,
                                id: randomId(),
                                status: 'update',
                                isTimeBreak:false,
                                isChanged: true,
                                //作業枠利用設定
                                taskFrameUsageSetting: ko.unwrap((vm.params.$settings)),
                                //社員ID
                                employeeId: vm.params.employee() || vm.$user.employeeId,
                                //年月日
                                period: { start: timeStart, end: timeEnd },
                                //現在の応援勤務枠
                                frameNos:_.map(taskDetails, td => td.supNo),
                                //工数実績作業ブロック
                                taskBlock: {
                                    caltimeSpan: { start: timeStart, end: timeEnd },

                                    taskDetails
                                },
                                //作業内容入力ダイアログ表示項目一覧
                                displayManHrRecordItems: _.get(ko.unwrap((vm.params.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []),
                            } as any
                        });
                        
                        });
                        updateEvents();
                    }
                },
                datesSet: ({ start, end }) => {
                    let current = moment().startOf('day');
                    let { start: vrs, end: vre } = ko.unwrap(validRange);
                    let isValidRange = () => {
                        let validRange = ko.unwrap<DateRangeInput>(params.validRange);

                        if (validRange) {
                            let { start, end } = validRange;

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

                    let $curt = $el.find('.fc-current-day-button');
                    let $prev = $el.find('.fc-preview-day-button');
                    let $next = $el.find('.fc-next-day-button');

                    datesSet({ start, end });

                    // enable, disable today button when change dateRange
//                    if (!current.isBetween(start, end, 'date', '[)') && isValidRange()) {
//                        $curt.removeAttr('disabled');
//                    } else {
//                        $curt.attr('disabled', 'disabled');
//                    }

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
                        
                        let validEnd = vre == '9999-12-32' ? '9999-12-31' : vre;
                        if (moment(end).isBefore(validEnd, 'day')) {
                            $next.removeAttr('disabled');
                        } else {
                            $next.attr('disabled', 'disabled');
                        }
                    } else {
                        $next.removeAttr('disabled');
                    }
                },
                eventClassNames: (arg) =>{
                    if (arg.event.extendedProps.isTimeBreak) {
                        return 'time-break';
                    }
                    return '';
                },
                eventDidMount: ({ el, event }) => {
                    el.setAttribute('event-id', event.id);
                    $(".fc-timegrid-event-harness:has('.time-break')").css('z-index', 10);
                },
                windowResize: () => {
                    // update height
                    let fce = vm.calendar.el.getBoundingClientRect();

                    if (fce) {
                        let { top } = fce;
                        let { innerHeight } = window;

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
                        let { setting } = popupData;
                        let { firstDay, scrollTime, slotDuration } = value;

                        if (firstDay !== undefined)
                            setting.firstDay(firstDay);
                        if (scrollTime)
                            setting.scrollTime(scrollTime);
                        if (slotDuration)
                            setting.slotDuration(slotDuration);
                    }
                })
                // render calendar after restore charactergistic domain to model
                .then(() => {
                    vm.calendar.render();
                });

            ko.computed({
                read: () => {
                    let wk = ko.unwrap<boolean>(weekends);

                    vm.calendar.setOption('weekends', wk);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // change view
            ko.computed({
                read: () => {
                    let cv = ko.unwrap<string>(computedView);

                    vm.calendar.changeView(cv);
                    vm.calendar.scrollToTime(formatTime(ko.unwrap(scrollTime)));
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set locale
            ko.computed({
                read: () => {
                    let lc = ko.unwrap<string>(locale);

                    vm.calendar.setOption('locale', lc);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set editable
            ko.computed({
                read: () => {
                    let ed = true;

                    vm.calendar.setOption('editable', ed);
                    vm.calendar.setOption('droppable', ed);
                    vm.calendar.setOption('selectable', ed);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set firstDay
            ko.computed({
                read: () => {
                    let fd = ko.unwrap<number>(firstDay);

                    vm.calendar.setOption('firstDay', fd);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set scrollTime
            ko.computed({
                read: () => {
                    let sc = ko.unwrap<number>(scrollTime);
                    let fd = ko.unwrap<number>(firstDay);
                    let sd = ko.unwrap<number>(params.slotDuration);
                    
                    setTimeout(() => {
                        vm.calendar.scrollToTime(formatTime(sc));
                    }, 500);
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set initialDate
            ko.computed({
                read: () => {
                    let id = ko.unwrap<Date>(initialDate);
                    let sc = ko.unwrap(scrollTime);

                    clearSelection();

                    vm.calendar.gotoDate(formatDate(id));
                    vm.calendar.scrollToTime(formatTime(sc));

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
                    let { left, right, center, start, end } = headerToolbar;
                    let avs: string[] = ko.unwrap<InitialView[]>(availableView).map(toKebabCase);
                    let buttons = avs.map((m: ButtonView) => ({ [m]: viewButtons[m] }))

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
                    let slotdr = ko.unwrap(params.slotDuration);
                    let time = !slotdr ? '00:15:00' : formatTime(slotdr);
                    let updateOption = () => {
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
                    let businessHours = ko.unwrap<BussinessHour[]>(params.businessHours);
                    if (!businessHours.length) {
                        vm.calendar.setOption('businessHours', {
                                    daysOfWeek: [0,1,2,3,4,5,6],
                                    startTime: formatTime(10, false),
                                    endTime: formatTime(10, false)
                                });
                        
                        //vm.updateStyle('breaktime', '');
                    } else {
                        let breakTimes = ko.unwrap<BreakTime[]>(params.breakTime);

                        let bhs = [];
                        for (let i = 0; i < businessHours.length; i++) {
                            let cbh = businessHours[i];
                            let breakOfDay = _.find(breakTimes, { 'dayOfWeek': cbh.dayOfWeek });
                            if (breakOfDay && breakOfDay.breakTimes.length) {
                                for (let j = 0; j < breakOfDay.breakTimes.length; j++) {
                                    let brTime = breakOfDay.breakTimes[j];
                                    let brBeforeTime = breakOfDay.breakTimes[j - 1];
                                    let end = cbh.end;
                                    let start = cbh.start;
                                    if (brTime.end < end && brTime.end > start) {
                                        bhs.push(
                                            {
                                                daysOfWeek: [cbh.dayOfWeek],
                                                startTime: formatTime(brTime.end, false),
                                                endTime: formatTime(end, false)
                                            },
                                            {
                                            daysOfWeek: [cbh.dayOfWeek],
                                            startTime: !brBeforeTime ? formatTime(start, false) : formatTime(brBeforeTime.end, false),
                                            endTime: !brBeforeTime ?  formatTime(brTime.start, false) : formatTime(brTime.start, false)
                                            }
                                        );
                                    } else {
                                        if (!_.find(bhs, bh => bh.daysOfWeek.indexOf(cbh.dayOfWeek) != -1)) {
                                            bhs.push({
                                                daysOfWeek: [cbh.dayOfWeek],
                                                startTime: formatTime(start, false),
                                                endTime: formatTime(end, false)
                                            });
                                        }
                                    }

                                }
                            } else {
                                bhs.push({
                                    daysOfWeek: [cbh.dayOfWeek],
                                    startTime: formatTime(cbh.start, false),
                                    endTime: formatTime(cbh.end, false)
                                });

                            }
                        }
                        
                        vm.calendar.setOption('businessHours', bhs );

                        //vm.updateStyle('breaktime', `.fc-timegrid-slot-lane-breaktime { background-color: ${backgroundColor || 'transparent'} }`);
                    }
                    
                },
                disposeWhenNodeIsRemoved: vm.$el
            });

            // set validRange
            ko.computed({
                read: () => {
                    let validRange = ko.unwrap<DateRangeInput>(params.validRange);
                    //lỗi của calendar, nếu set '9999-12-31' thì sẽ không hiện ngày '9999-12-31' nên phải set là '9999-12-32'
                    vm.calendar.setOption('validRange', {end: '9999-12-32'});
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
        }
        

        
        public getEditableDay(date){
                let vm = this;
                let datas = vm.params.$datas();
                let convert = _.find(_.get(datas, 'convertRes', []), cvr => { return moment(cvr.ymd).isSame(moment(date), 'days'); } );
                return !!convert;
        }
       
            
         public isOTTime(date){
            let vm = this;
            let datas = vm.params.$datas();
            let etz = _.find(_.get(datas, 'estimateZones', []), es => moment(es.ymd).isSame(moment(date), 'days'));
            let overTimeZones = _.get(etz, 'overTimeZones', []);
            return false;
        }

        

          public  getTaskValues(){
                 let vm = this;
                 let items = [];

                 _.forEach(_.get(ko.unwrap((vm.params.$settings)), 'manHrInputDisplayFormat.displayManHrRecordItems', []), function(item) {
                     items.push({ itemId: item.itemId, value: null });
                 });
    
                return items;
            }

           public revertEvent(oldEvents, caches){
              let vm = this;
              _.forEach(oldEvents, oldEvent => {

                  _.each(vm.calendar.getEvents(), (e: EventApi) => {

                      if (e.extendedProps.id === oldEvent.extendedProps.id) {
                          e.setExtendedProp('status', 'delete');
                          e.remove();
                          caches.new(null);
                      }
                  });
                  let newEvent = vm.calendar
                      .addEvent({
                          id: randomId(),
                          backgroundColor: oldEvent.backgroundColor,
                          title: oldEvent.title,
                          start: oldEvent.start,
                          end: oldEvent.end,
                          borderColor: oldEvent.borderColor,
                          groupId: oldEvent.groupId,
                          extendedProps: oldEvent.extendedProps
                      });
                  caches.new(newEvent);
              });
          }

        public destroyed() {
            let vm = this;
            let { events } = vm;

            _.each(events, (evts: J_EVENT[], k: string) => {
                _.each(evts, (h: J_EVENT) => $(window).off(k, h))
            });
        }

        public removeEvent(id: string) {

        }

        private updateStyle(key: Style, style: string) {
            let vm = this;
            let styles = ko.unwrap(vm.$styles);

            _.extend(styles, { [key]: style });

            vm.$styles(styles);
        }



        checkEditDialog() {
            let dfd = $.Deferred();
            let vm = this;
            let eventNotSave = _.find(vm.calendar.getEvents(), (e) => !_.get(e, 'extendedProps.id'));
            if ((vm.$view() == "edit" && vm.params.$settings().isChange) || (vm.$view() == "edit" && eventNotSave)) {
                vm.$dialog
                    .confirm({ messageId: 'Msg_2094' })
                    .then((v: 'yes' | 'no') => {
                        if (v == 'yes' && eventNotSave)
                            eventNotSave.remove();
                        dfd.resolve(v);
                    });
            } else {
                if (eventNotSave)
                    eventNotSave.remove();
                dfd.resolve('yes');
            }
            return dfd.promise();
        }

        private initalEvents() {
            let vm = this;
            let { $el, params, calendar, dataEvent, popupPosition } = vm;

            $($el)
                .on('mousewheel', (evt: JQueryEvent) => {
                    if (ko.unwrap(dataEvent.shift) === true) {
                        evt.preventDefault();

                        let { deltaY, wheelDelta } = evt.originalEvent;
                        let slotDuration = ko.unwrap(params.slotDuration);

                        if (!version.match(/IE/) && ['timeGridDay', 'timeGridWeek'].indexOf(calendar.view.type) !== -1 && ko.isObservable(params.slotDuration)) {
                            let index = durations.indexOf(slotDuration);

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
                    
                  
                            let $tg = $(evt.target);

                            let iown = $tg.hasClass('popup-owner-copy');
                            let cown = $tg.closest('.popup-owner-copy').length > 0;
                            let ipov = $tg.hasClass('fc-popup-editor');
                            let cpov = $tg.closest('.fc-popup-editor').length > 0;
                            let ipkr = $tg.hasClass('datepicker-container') && $tg.not('datepicker-inline');
                            let cpkr = $tg.closest('.datepicker-container').length > 0 && $tg.closest('.datepicker-inline').length === 0;
                            let event = $tg.closest('.fc-timegrid-event.fc-v-event.fc-event').length;
                            let dig = $tg.closest('.ui-dialog-buttons').length > 0;
                            let cd = $tg.hasClass('fc-next-day-button') || $tg.hasClass('fc-preview-day-button') ;
                            let st = $tg.hasClass('fc-settings-button');
                            let cv = $tg.hasClass('fc-one-day-button') || $tg.hasClass('fc-full-week-button') ;
                            let ts = $tg.hasClass('fc-timegrid-slot');
                            let ovl = $tg.hasClass('ui-widget-overlay');
                            let ede = $tg.closest('.fc-oneday-events li').length > 0;
                            let tde = $tg.closest('.fc-task-events li').length > 0;
                            let cala = $tg.closest('.fc-scrollgrid-section-body').length > 0;
                            

                            if (!ede) {
                                $('.fc-oneday-events .edit-popup').removeClass('show');
                            }

                            if (!tde) {
                                $('.fc-task-events .edit-popup').removeClass('show');
                            }
                            
                    
                            if (ovl) {
                                return;
                            }

                            dataEvent.mouse(true);

                            let targ = $tg
                                .closest('.fc-timegrid-event.fc-v-event.fc-event').length ? 'event' :
                                ($tg.hasClass('fc-non-business') || $tg.hasClass('fc-timegrid-slot')) ? 'date' : null;
    
                            dataEvent.target(targ);



                            // close popup if target isn't owner & poper.
                             if (!iown && !cown && !ipov && !cpov && !ipkr && !cpkr && !dig && !cd && !st && !cv && !ts && !event &&!cala) {
                                vm.checkEditDialog().done((v) => {
                                    if (v == 'yes') {
										$('.edit-event .nts-input').ntsError('clear');
                                        popupPosition.event(null);
                                        popupPosition.setting(null);
                                    }
                                });
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
                            let selecteds = _.filter(vm.calendar.getEvents(), (e: EventApi) => e.groupId === SELECTED);

                            if (selecteds.length) {
                                dataEvent.delete(true);
                                let starts = selecteds.map(({ start }) => formatDate(start));

                                if (ko.isObservable(vm.params.events)) {
                                    vm.params.events.remove((e: EventRaw) => {
                                        let canRemove = e.editable && starts.indexOf(formatDate(e.start)) !== -1;
                                        
                                        if (canRemove) {
                                            if (e.extendedProps.isTimeBreak) {
                                                let removeList = vm.params.screenA.removeBreakList;
                                                let removeDate = _.find(removeList(), (ri) => moment(ri.date).isSame(moment(e.start), 'days'));
                                                if (removeDate) {
                                                    removeDate.nos.push(_.get(e, 'extendedProps.no', []));
                                                } else {
                                                    removeList.push({ date: moment(e.start).startOf('day').toDate(), nos: [_.get(e, 'extendedProps.no', [])] });
                                                }
                                            } else {
                                                let removeList = vm.params.screenA.removeList;
                                                let removeDate = _.find(removeList(), (ri) => moment(ri.date).isSame(moment(e.start), 'days'));
                                                let supNos = _.map(_.get(e, 'extendedProps.taskBlock.taskDetails', []), td => td.supNo);
                                                if (removeDate) {
                                                    removeDate.supNos.push(...supNos);
                                                } else {
                                                    removeList.push({ date: moment(e.start).startOf('day').toDate(), supNos });
                                                }
                                            }
                                        }

                                        return canRemove;
                                    });
                                }
                                dataEvent.delete(false);
                                popupPosition.event(null);
                                popupPosition.setting(null);
//                                vm.$dialog
//                                    .confirm({ messageId: 'DELETE_CONFIRM' })
//                                    .then((v: 'yes' | 'no') => {
//                                        if (v === 'yes') {
//                                            let starts = selecteds.map(({ start }) => formatDate(start));
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
            let vm = this;
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
                let name = E_COMP_NAME;
                let data = valueAccessor();
                let mode = allBindingsAccessor.get('mode');
                let view = allBindingsAccessor.get('view');
                let mutated = allBindingsAccessor.get('mutated');
                let position = allBindingsAccessor.get('position');
                let components = allBindingsAccessor.get('components');
                let excludeTimes = allBindingsAccessor.get('exclude-times');
                let mousePointer = allBindingsAccessor.get('mouse-pointer');
                let $settings = allBindingsAccessor.get('$settings');
                let screenA = allBindingsAccessor.get('screenA');

                let component = { name, params: { data, position, components, mode, view, mutated, excludeTimes, mousePointer, $settings ,screenA } };

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
                let vm = this;
                let { $el, params } = vm;
                let { components, data, position, mode, view, excludeTimes, mousePointer, $settings } = params;
                let $ctn = $('<div>');
                let $view = document.createElement('div');
                let $edit = document.createElement('div');

                $($el)
                    .html('')
                    .append($ctn);

                $ctn
                    .append($view)
                    .append($edit);

                ko.computed({
                    read: () => {
                        let _view = ko.unwrap(view);

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
                    let close = (result?: 'yes' | 'cancel' | null) => vm.close(result);
                    let remove = () => vm.remove();
                    let update = () => {
                        if (view() !== 'edit') {
                            view('edit');
							data.valueHasMutated();
                        } else {
                            view.valueHasMutated();
                        }
						
                        // rebind size of popup
                        position.valueHasMutated();
                    };

                    // mock data
                    let $share = ko.observable(null);

                    ko.applyBindingsToNode($view, { component: { name: components.view, params: { update, remove, close, data, mode, $settings, $share, screenA: params.screenA  } } });
                    ko.applyBindingsToNode($edit, { component: { name: components.editor, params: { remove, close, data, mode, view, position, excludeTimes, $settings, $share } } });
                }

                ko.computed({
                    read: () => {
                        let pst = ko.unwrap(position);
                        let pot = ko.unwrap(mousePointer);

                        if (!pst) {
                            $el.removeAttribute('style');
                            $el.classList.remove('show');
                        } else {
                            let { innerWidth, innerHeight } = window;
                            let { screenX, screenY } = pot;
                            let { top, left, width: wd, height: hg } = pst.getBoundingClientRect();

                            let first = $el.querySelector('div');

                            $el.classList.add('show');

                            if (!first) {
                                $el.style.top = `${top || 0}px`;
                                $el.style.left = `${(left || 0) + wd + 3}px`;
                            } else {
                                let { width, height } = first.getBoundingClientRect();

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
								
								let w = width + 20;
								if($($el).children().children().get(1).style.display == "block"){
									//w += 35;
								}
								
                                $el.style.width = `${w}px`;
                                $el.style.height = `${height + 20}px`;
                            }
                        }
                    },
                    disposeWhenNodeIsRemoved: $el
                });
            }

            remove() {
                let vm = this;
                let { params } = vm;
                let { data, position, view, mutated } = params;

                $.Deferred()
                    .resolve(true)
                    .then(() => {
                        let event = ko.unwrap(data);

                        // change status for subscribe & rebind
                        event.setExtendedProp('status', 'delete');

                        // remove???
                        event.remove();
                        let removeList = vm.params.screenA.removeList;
                        let removeDate = _.find(removeList(), (ri) => moment(ri.date).isSame(moment(event.start), 'days'));
                        let supNos = _.map(_.get(event, 'extendedProps.taskBlock.taskDetails', []), td => td.supNo);
                        if (removeDate) {
                            removeDate.supNos.push(...supNos);
                        } else {
                            removeList.push({ date: moment(event.start).startOf('day').toDate(), supNos });
                        }
                        // trigger update from parent view
                        mutated.valueHasMutated();
                    })
                    .then(() => data(null))
                    .then(() => position(null))
                    .then(() => view('view'));
            }

            close(result ?: 'yes' | 'cancel' | 'save' | null) {
                let vm = this;
                let { params } = vm;
                let { data, position, view, mutated } = params;

                $.Deferred()
                    .resolve(true)
                    .then(() => {
                        if (result === 'yes') {
                            let event = ko.unwrap(data);

                            if (event) {
                                event.remove();
                                
                            }
                        }
                        if(result === 'save'){
                              // trigger update from parent view
                              mutated.valueHasMutated();
                        }
                    
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
        export class FullCalendarSettingBindingHandler implements KnockoutBindingHandler {
            init(element: HTMLElement, valueAccessor: () => EventApi, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
                let name = S_COMP_NAME;
                let data = valueAccessor();
                let position = allBindingsAccessor.get('position');
                let component = { name, params: { data, position } };

                element.removeAttribute('data-bind');

                element.classList.add('ntsControl');
                element.classList.add('fc-popup-editor');
                element.classList.add('fc-popup-setting');

                ko.applyBindingsToNode(element, { component }, bindingContext);

                return { controlsDescendantBindings: true };
            }
        }

        type SETTING_PARAMS = {
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

            constructor(private params: SETTING_PARAMS) {
                super();
            }

            mounted() {
                let vm = this;
                let { $el, params } = vm;
                let { data, position } = params;

                $el.innerHTML = '';

                // apply binding setting panel
                ko.applyBindingsToNode($el, { component: { name: 'fc-setting-panel', params: { ...data, position } } });

                ko.computed({
                    read: () => {
                        let pst = ko.unwrap(position);

                        if (!pst) {
                            $el.removeAttribute('style');
                            $el.classList.remove('show');
                        } else {
                            let { top, left } = pst.getBoundingClientRect();
                            let first = $el.querySelector('div');

                            $el.classList.add('show');

                            $el.style.top = `${top}px`;

                            if (first) {
                                let { width, height } = first.getBoundingClientRect();

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

                    let tg = evt.target as HTMLElement;
                    //chỉ khi click vào vùng màn hình riêng của KDW013 mới preventDefault
                    let clickOnMaster = $(tg).closest('#master-content').length > 0 ;
                    let notClickOnbreakTime = !$(tg).closest('.fc-ckb-break-time').length > 0;
                    let notClickOnEventNote = !$(tg).closest('.fc-event-note').length > 0;
                    
                    if (clickOnMaster  && notClickOnbreakTime && notClickOnEventNote)
                        evt.preventDefault();

                    if (tg && !!ko.unwrap(position)) {
                        if (!tg.classList.contains(POWNER_CLASS_CPY) && !$(tg).closest(`.${POWNER_CLASS_CPY}`).length && !$(tg).closest('.fc-popup-setting').length) {
                            position(null);
                        }
                    }
                };

                $(document).on('click', vm.event);

                let $ctn = $($el);

                $ctn
                    // prevent tabable to out of popup control
                    .on("keydown", ":tabbable", (evt: JQueryKeyEventObject) => {
                        let fable = $ctn.find(":tabbable:not(.close)").toArray();

                        let last = _.last(fable);
                        let first = _.first(fable);

                        if (evt.keyCode === 9) {
                            if ($(evt.target).is(last) && evt.shiftKey === false) {
                                first.focus();

                                evt.preventDefault();
                            } else if ($(evt.target).is(first) && evt.shiftKey === true) {
                                last.focus();

                                evt.preventDefault();
                            }
                        } else if (evt.keyCode === 27) {
                            let fabl = position();

                            // close setting popup
                            position(null);

                            // focus to setting button
                            $(fabl).focus();
                        }
                    });
                $('#btn_register').focus();
            }

            destroyed() {
                let vm = this;

                $(document).off('click', vm.event);
            }
        }

       
    }
}
