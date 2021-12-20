module FullCalendar {
    interface FullCalendar {
        Calendar: {
            new(el: HTMLElement, optionOverrides?: CalendarOptions): Calendar;
        };
        Draggable: {
            new(el: HTMLElement, optionOverrides?: ThirdPartyDraggableSettings): any;
        }
    };

    class Calendar extends CalendarApi {
        currentData: CalendarData;
        renderRunner: DelayedRunner;
        el: HTMLElement;
        isRendering: boolean;
        isRendered: boolean;
        currentClassNames: string[];
        customContentRenderId: number;
        get view(): ViewApi;
        handleAction: (action: Action) => void;
        handleData: (data: CalendarData) => void;
        handleRenderRequest: () => void;
        render(): void;
        destroy(): void;
        updateSize(): void;
        batchRendering(func: any): void;
        pauseRendering(): void;
        resumeRendering(): void;
        resetOptions(optionOverrides: any, append?: any): void;
        setClassNames(classNames: string[]): void;
        setHeight(height: CssDimValue): void;
    }

    type DurationInput = DurationObjectInput | string | number;

    interface DurationObjectInput {
        years?: number;
        year?: number;
        months?: number;
        month?: number;
        weeks?: number;
        week?: number;
        days?: number;
        day?: number;
        hours?: number;
        hour?: number;
        minutes?: number;
        minute?: number;
        seconds?: number;
        second?: number;
        milliseconds?: number;
        millisecond?: number;
        ms?: number;
    }

    interface Duration {
        years: number;
        months: number;
        days: number;
        milliseconds: number;
        specifiedWeeks?: boolean;
    }

    function createDuration(input: DurationInput, unit?: string): Duration | null;
    function asCleanDays(dur: Duration): number;
    function addDurations(d0: Duration, d1: Duration): {
        years: number;
        months: number;
        days: number;
        milliseconds: number;
    };

    function multiplyDuration(d: Duration, n: number): {
        years: number;
        months: number;
        days: number;
        milliseconds: number;
    };

    function asRoughMinutes(dur: Duration): number;
    function asRoughSeconds(dur: Duration): number;
    function asRoughMs(dur: Duration): number;
    function wholeDivideDurations(numerator: Duration, denominator: Duration): number;

    function greatestDurationDenominator(dur: Duration): {
        unit: string;
        value: number;
    };


    type ClassNamesInput = string | string[];
    function parseClassNames(raw: ClassNamesInput): string[];

    function addWeeks(m: Date, n: number): Date;
    function addDays(m: Date, n: number): Date;
    function addMs(m: Date, n: number): Date;
    function diffWeeks(m0: any, m1: any): number;
    function diffDays(m0: any, m1: any): number;
    function diffDayAndTime(m0: Date, m1: Date): Duration;
    function diffWholeWeeks(m0: Date, m1: Date): number;
    function diffWholeDays(m0: Date, m1: Date): number;
    function startOfDay(m: Date): Date;
    function isValidDate(m: Date): boolean;

    interface CalendarSystem {
        getMarkerYear(d: Date): number;
        getMarkerMonth(d: Date): number;
        getMarkerDay(d: Date): number;
        arrayToMarker(arr: number[]): Date;
        markerToArray(d: Date): number[];
    }

    interface DateRangeInput {
        start?: DateInput;
        end?: DateInput;
    }

    interface OpenDateRange {
        start: Date | null;
        end: Date | null;
    }

    interface DateRange {
        start: Date;
        end: Date;
    }

    function intersectRanges(range0: OpenDateRange, range1: OpenDateRange): OpenDateRange;
    function rangesEqual(range0: OpenDateRange, range1: OpenDateRange): boolean;
    function rangesIntersect(range0: OpenDateRange, range1: OpenDateRange): boolean;
    function rangeContainsRange(outerRange: OpenDateRange, innerRange: OpenDateRange): boolean;
    function rangeContainsMarker(range: OpenDateRange, date: Date | number): boolean;

    interface PointerDragEvent {
        origEvent: UIEvent;
        isTouch: boolean;
        subjectEl: EventTarget;
        pageX: number;
        pageY: number;
        deltaX: number;
        deltaY: number;
    }

    interface EventInteractionState {
        affectedEvents: EventStore;
        mutatedEvents: EventStore;
        isEvent: boolean;
    }

    function guid(): string;
    function disableCursor(): void;
    function enableCursor(): void;
    function preventSelection(el: HTMLElement): void;
    function allowSelection(el: HTMLElement): void;
    function preventContextMenu(el: HTMLElement): void;
    function allowContextMenu(el: HTMLElement): void;

    interface OrderSpec<Subject> {
        field?: string;
        order?: number;
        func?: FieldSpecInputFunc<Subject>;
    }

    type FieldSpecInput<Subject> = string | string[] | FieldSpecInputFunc<Subject> | FieldSpecInputFunc<Subject>[];
    type FieldSpecInputFunc<Subject> = (a: Subject, b: Subject) => number;
    function parseFieldSpecs<Subject>(input: FieldSpecInput<Subject>): OrderSpec<Subject>[];
    function compareByFieldSpecs<Subject>(obj0: Subject, obj1: Subject, fieldSpecs: OrderSpec<Subject>[]): number;
    function compareByFieldSpec<Subject>(obj0: Subject, obj1: Subject, fieldSpec: OrderSpec<Subject>): number;
    function flexibleCompare(a: any, b: any): number;
    function padStart(val: any, len: any): string;
    function compareNumbers(a: any, b: any): number;
    function isInt(n: any): boolean;
    function computeSmallestCellWidth(cellEl: HTMLElement): number;

    interface EventMutation {
        datesDelta?: Duration;
        startDelta?: Duration;
        endDelta?: Duration;
        standardProps?: any;
        extendedProps?: any;
    }

    function applyMutationToEventStore(eventStore: EventStore, eventConfigBase: EventUiHash, mutation: EventMutation, context: CalendarContext): EventStore;
    type eventDefMutationApplier = (eventDef: EventDef, mutation: EventMutation, context: CalendarContext) => void;

    interface DateSelectionApi extends DateSpanApi {
        jsEvent: UIEvent;
        view: ViewApi;
    }

    type DatePointTransform = (dateSpan: DateSpan, context: CalendarContext) => any;
    type DateSpanTransform = (dateSpan: DateSpan, context: CalendarContext) => any;
    type CalendarInteraction = {
        destroy(): any;
    };
    type CalendarInteractionClass = {
        new(context: CalendarContext): CalendarInteraction;
    };
    type OptionChangeHandler = (propValue: any, context: CalendarContext) => void;
    type OptionChangeHandlerMap = {
        [propName: string]: OptionChangeHandler;
    };

    interface DateSelectArg extends DateSpanApi {
        jsEvent: MouseEvent | null;
        view: ViewApi;
    }

    function triggerDateSelect(selection: DateSpan, pev: PointerDragEvent | null, context: CalendarContext & {
        viewApi?: ViewApi;
    }): void;

    interface DateUnselectArg {
        jsEvent: MouseEvent;
        view: ViewApi;
    }

    function getDefaultEventEnd(allDay: boolean, marker: Date, context: CalendarContext): Date;

    interface RenderHookProps<ContentArg> {
        hookProps: ContentArg;
        classNames: ClassNamesGenerator<ContentArg>;
        content: CustomContentGenerator<ContentArg>;
        defaultContent?: DefaultContentGenerator<ContentArg>;
        didMount: DidMountHandler<MountArg<ContentArg>>;
        willUnmount: WillUnmountHandler<MountArg<ContentArg>>;
        children: RenderHookPropsChildren;
        elRef?: Ref<any>;
    }

    type RenderHookPropsChildren = (rootElRef: Ref<any>, classNames: string[], innerElRef: Ref<any>, innerContent: ComponentChildren) => ComponentChildren;
    interface ContentTypeHandlers {
        [contentKey: string]: () => (el: HTMLElement, contentVal: any) => void;
    }
    class RenderHook<HookProps> extends BaseComponent<RenderHookProps<HookProps>> {
        private rootElRef;
        render(): createElement.JSX.Element;
        handleRootEl: (el: HTMLElement | null) => void;
    }
    interface ObjCustomContent {
        html: string;
        domNodes: any[];
        [custom: string]: any;
    }
    type CustomContent = ComponentChildren | ObjCustomContent;
    type CustomContentGenerator<HookProps> = CustomContent | ((hookProps: HookProps) => CustomContent);
    type DefaultContentGenerator<HookProps> = (hookProps: HookProps) => ComponentChildren;
    const CustomContentRenderContext: Context<number>;
    interface ContentHookProps<HookProps> {
        hookProps: HookProps;
        content: CustomContentGenerator<HookProps>;
        defaultContent?: DefaultContentGenerator<HookProps>;
        children: (innerElRef: Ref<any>, innerContent: ComponentChildren) => ComponentChildren;
        backupElRef?: RefObject<any>;
    }
    function ContentHook<HookProps>(props: ContentHookProps<HookProps>): createElement.JSX.Element;
    type MountArg<ContentArg> = ContentArg & {
        el: HTMLElement;
    };
    type DidMountHandler<MountArg extends {
        el: HTMLElement;
    }> = (mountArg: MountArg) => void;
    type WillUnmountHandler<MountArg extends {
        el: HTMLElement;
    }> = (mountArg: MountArg) => void;
    interface MountHookProps<ContentArg> {
        hookProps: ContentArg;
        didMount: DidMountHandler<MountArg<ContentArg>>;
        willUnmount: WillUnmountHandler<MountArg<ContentArg>>;
        children: (rootElRef: Ref<any>) => ComponentChildren;
        elRef?: Ref<any>;
    }
    class MountHook<ContentArg> extends BaseComponent<MountHookProps<ContentArg>> {
        rootEl: HTMLElement;
        render(): ComponentChildren;
        componentDidMount(): void;
        componentWillUnmount(): void;
        private handleRootEl;
    }
    function buildClassNameNormalizer<HookProps>(): (generator: ClassNamesGenerator<HookProps>, hookProps: HookProps) => string[];
    type ClassNamesGenerator<HookProps> = ClassNamesInput | ((hookProps: HookProps) => ClassNamesInput);


    type ViewComponentType = ComponentType<ViewProps>;
    type ViewConfigInput = ViewComponentType | ViewOptions;
    type ViewConfigInputHash = {
        [viewType: string]: ViewConfigInput;
    };
    interface SpecificViewContentArg extends ViewProps {
        nextDayThreshold: Duration;
    }
    type SpecificViewMountArg = MountArg<SpecificViewContentArg>;


    abstract class Interaction {
        component: DateComponent<any>;
        constructor(settings: InteractionSettings);
        destroy(): void;
    }
    type InteractionClass = {
        new(settings: InteractionSettings): Interaction;
    };
    interface InteractionSettingsInput {
        el: HTMLElement;
        useEventCenter?: boolean;
    }
    interface InteractionSettings {
        component: DateComponent<any>;
        el: HTMLElement;
        useEventCenter: boolean;
    }
    type InteractionSettingsStore = {
        [componenUid: string]: InteractionSettings;
    };
    function interactionSettingsToStore(settings: InteractionSettings): {
        [x: string]: InteractionSettings;
    };
    let interactionSettingsStore: InteractionSettingsStore;


    class DelayedRunner {
        private drainedOption?;
        private isRunning;
        private isDirty;
        private pauseDepths;
        private timeoutId;
        constructor(drainedOption?: () => void);
        request(delay?: number): void;
        pause(scope?: string): void;
        resume(scope?: string, force?: boolean): void;
        isPaused(): number;
        tryDrain(): void;
        clear(): void;
        private clearTimeout;
        protected drained(): void;
    }

    interface CalendarContentProps extends CalendarData {
        forPrint: boolean;
        isHeightAuto: boolean;
    }
    class CalendarContent extends PureComponent<CalendarContentProps> {
        context: never;
        private buildViewContext;
        private buildViewPropTransformers;
        private buildToolbarProps;
        private handleNavLinkClick;
        private headerRef;
        private footerRef;
        private interactionsStore;
        private calendarInteractions;
        render(): createElement.JSX.Element;
        componentDidMount(): void;
        componentDidUpdate(prevProps: CalendarContentProps): void;
        componentWillUnmount(): void;
        _handleNavLinkClick(ev: VUIEvent, anchorEl: HTMLElement): void;
        buildAppendContent(): VNode;
        renderView(props: CalendarContentProps): createElement.JSX.Element;
        registerInteractiveComponent: (component: DateComponent<any>, settingsInput: InteractionSettingsInput) => void;
        unregisterInteractiveComponent: (component: DateComponent<any>) => void;
        resizeRunner: DelayedRunner;
        handleWindowResize: (ev: UIEvent) => void;
    }


    interface Point {
        left: number;
        top: number;
    }
    interface Rect {
        left: number;
        right: number;
        top: number;
        bottom: number;
    }
    function pointInsideRect(point: Point, rect: Rect): boolean;
    function intersectRects(rect1: Rect, rect2: Rect): Rect | false;
    function translateRect(rect: Rect, deltaX: number, deltaY: number): Rect;
    function constrainPoint(point: Point, rect: Rect): Point;
    function getRectCenter(rect: Rect): Point;
    function diffPoints(point1: Point, point2: Point): Point;


    interface Hit {
        component: DateComponent<any>;
        dateSpan: DateSpan;
        dayEl: HTMLElement;
        rect: Rect;
        layer: number;
    }


    type eventDragMutationMassager = (mutation: EventMutation, hit0: Hit, hit1: Hit) => void;
    type EventDropTransformers = (mutation: EventMutation, context: CalendarContext) => Dictionary;
    type eventIsDraggableTransformer = (val: boolean, eventDef: EventDef, eventUi: EventUi, context: CalendarContext) => boolean;


    type dateSelectionJoinTransformer = (hit0: Hit, hit1: Hit) => any;


    type EventResizeJoinTransforms = (hit0: Hit, hit1: Hit) => false | Dictionary;


    const DRAG_META_REFINERS: {
        startTime: typeof createDuration;
        duration: typeof createDuration;
        create: BooleanConstructor;
        sourceId: StringConstructor;
    };
    type DragMetaInput = RawOptionsFromRefiners<typeof DRAG_META_REFINERS> & {
        [otherProp: string]: any;
    };
    interface DragMeta {
        startTime: Duration | null;
        duration: Duration | null;
        create: boolean;
        sourceId: string;
        leftoverProps: Dictionary;
    }
    function parseDragMeta(raw: DragMetaInput): DragMeta;

    type ExternalDefTransform = (dateSpan: DateSpan, dragMeta: DragMeta) => any;


    class Theme {
        classes: any;
        iconClasses: any;
        rtlIconClasses: any;
        baseIconClass: string;
        iconOverrideOption: any;
        iconOverrideCustomButtonOption: any;
        iconOverridePrefix: string;
        constructor(calendarOptions: CalendarOptionsRefined);
        setIconOverride(iconOverrideHash: any): void;
        applyIconOverridePrefix(className: any): any;
        getClass(key: any): any;
        getIconClass(buttonName: any, isRtl?: boolean): string;
        getCustomButtonIconClass(customButtonProps: any): string;
    }
    type ThemeClass = {
        new(calendarOptions: any): Theme;
    };


    type EventSourceFunc = (arg: {
        start: Date;
        end: Date;
        startStr: string;
        endStr: string;
        timeZone: string;
    }, successCallback: (events: EventInput[]) => void, failureCallback: (error: EventSourceError) => void) => (void | PromiseLike<EventInput[]>);

    const JSON_FEED_EVENT_SOURCE_REFINERS: {
        method: StringConstructor;
        extraParams: Identity<Record<string, any> | (() => Dictionary)>;
        startParam: StringConstructor;
        endParam: StringConstructor;
        timeZoneParam: StringConstructor;
    };


    const EVENT_SOURCE_REFINERS: {
        id: StringConstructor;
        defaultAllDay: BooleanConstructor;
        url: StringConstructor;
        events: Identity<EventInput[] | EventSourceFunc>;
        eventDataTransform: Identity<EventInputTransformer>;
        success: Identity<EventSourceSuccessResponseHandler>;
        failure: Identity<EventSourceErrorResponseHandler>;
    };
    type BuiltInEventSourceRefiners = typeof EVENT_SOURCE_REFINERS & typeof JSON_FEED_EVENT_SOURCE_REFINERS;
    interface EventSourceRefiners extends BuiltInEventSourceRefiners {
    }
    type EventSourceInputObject = EventUiInput & RawOptionsFromRefiners<Required<EventSourceRefiners>>;
    type EventSourceInput = EventSourceInputObject |
        EventInput[] | EventSourceFunc |
        string;
    type EventSourceRefined = EventUiRefined & RefinedOptionsFromRefiners<Required<EventSourceRefiners>>;

    interface EventSourceDef<Meta> {
        ignoreRange?: boolean;
        parseMeta: (refined: EventSourceRefined) => Meta | null;
        fetch: EventSourceFetcher<Meta>;
    }


    interface ZonedMarker {
        marker: Date;
        timeZoneOffset: number;
    }
    interface ExpandedZonedMarker extends ZonedMarker {
        array: number[];
        year: number;
        month: number;
        day: number;
        hour: number;
        minute: number;
        second: number;
        millisecond: number;
    }

    interface VerboseFormattingArg {
        date: ExpandedZonedMarker;
        start: ExpandedZonedMarker;
        end?: ExpandedZonedMarker;
        timeZone: string;
        localeCodes: string[];
        defaultSeparator: string;
    }
    type CmdFormatterFunc = (cmd: string, arg: VerboseFormattingArg) => string;
    interface DateFormattingContext {
        timeZone: string;
        locale: Locale;
        calendarSystem: CalendarSystem;
        computeWeekNumber: (d: Date) => number;
        weekText: string;
        cmdFormatter?: CmdFormatterFunc;
        defaultSeparator: string;
    }
    interface DateFormatter {
        format(date: ZonedMarker, context: DateFormattingContext): string;
        formatRange(start: ZonedMarker, end: ZonedMarker, context: DateFormattingContext, betterDefaultSeparator?: string): string;
    }


    interface ParsedRecurring<RecurringData> {
        typeData: RecurringData;
        allDayGuess: boolean | null;
        duration: Duration | null;
    }
    interface RecurringType<RecurringData> {
        parse: (refined: EventRefined, dateEnv: DateEnv) => ParsedRecurring<RecurringData> | null;
        expand: (typeData: any, framingRange: DateRange, dateEnv: DateEnv) => Date[];
    }

    abstract class NamedTimeZoneImpl {
        timeZoneName: string;
        constructor(timeZoneName: string);
        abstract offsetForArray(a: number[]): number;
        abstract timestampToArray(ms: number): number[];
    }
    type NamedTimeZoneImplClass = {
        new(timeZoneName: string): NamedTimeZoneImpl;
    };


    interface HandlerFuncTypeHash {
        [eventName: string]: (...args: any[]) => any;
    }
    class Emitter<HandlerFuncs extends HandlerFuncTypeHash> {
        private handlers;
        private options;
        private thisContext;
        setThisContext(thisContext: any): void;
        setOptions(options: Partial<HandlerFuncs>): void;
        on<Prop extends keyof HandlerFuncs>(type: Prop, handler: HandlerFuncs[Prop]): void;
        off<Prop extends keyof HandlerFuncs>(type: Prop, handler?: HandlerFuncs[Prop]): void;
        trigger<Prop extends keyof HandlerFuncs>(type: Prop, ...args: Parameters<HandlerFuncs[Prop]>): void;
        hasHandlers(type: keyof HandlerFuncs): number | Partial<HandlerFuncs>[keyof HandlerFuncs];
    }


    abstract class ElementDragging {
        emitter: Emitter<any>;
        constructor(el: HTMLElement, selector?: string);
        destroy(): void;
        abstract setIgnoreMove(bool: boolean): void;
        setMirrorIsVisible(bool: boolean): void;
        setMirrorNeedsRevert(bool: boolean): void;
        setAutoScrollEnabled(bool: boolean): void;
    }
    type ElementDraggingClass = {
        new(el: HTMLElement, selector?: string): ElementDragging;
    };


    type CssDimValue = string | number;
    interface ColProps {
        width?: CssDimValue;
        minWidth?: CssDimValue;
        span?: number;
    }
    interface SectionConfig {
        outerContent?: VNode;
        type: 'body' | 'header' | 'footer';
        className?: string;
        maxHeight?: number;
        liquid?: boolean;
        expandRows?: boolean;
        syncRowHeights?: boolean;
        isSticky?: boolean;
    }
    type ChunkConfigContent = (contentProps: ChunkContentCallbackArgs) => VNode;
    type ChunkConfigRowContent = VNode | ChunkConfigContent;
    interface ChunkConfig {
        outerContent?: VNode;
        content?: ChunkConfigContent;
        rowContent?: ChunkConfigRowContent;
        scrollerElRef?: Ref<HTMLDivElement>;
        elRef?: Ref<HTMLTableCellElement>;
        tableClassName?: string;
    }
    interface ChunkContentCallbackArgs {
        tableColGroupNode: VNode;
        tableMinWidth: CssDimValue;
        clientWidth: number | null;
        clientHeight: number | null;
        expandRows: boolean;
        syncRowHeights: boolean;
        rowSyncHeights: number[];
        reportRowHeightChange: (rowEl: HTMLElement, isStable: boolean) => void;
    }
    function computeShrinkWidth(chunkEls: HTMLElement[]): number;
    interface ScrollerLike {
        needsYScrolling(): boolean;
        needsXScrolling(): boolean;
    }
    function getSectionHasLiquidHeight(props: {
        liquid: boolean;
    }, sectionConfig: SectionConfig): boolean;
    function getAllowYScrolling(props: {
        liquid: boolean;
    }, sectionConfig: SectionConfig): boolean;
    function renderChunkContent(sectionConfig: SectionConfig, chunkConfig: ChunkConfig, arg: ChunkContentCallbackArgs): VNode;
    function isColPropsEqual(cols0: ColProps[], cols1: ColProps[]): boolean;
    function renderMicroColGroup(cols: ColProps[], shrinkWidth?: number): VNode;
    function sanitizeShrinkWidth(shrinkWidth?: number): number;
    function hasShrinkWidth(cols: ColProps[]): boolean;
    function getScrollGridClassNames(liquid: boolean, context: ViewContext): any[];
    function getSectionClassNames(sectionConfig: SectionConfig, wholeTableVGrow: boolean): string[];
    function renderScrollShim(arg: ChunkContentCallbackArgs): createElement.JSX.Element;
    function getStickyHeaderDates(options: BaseOptionsRefined): boolean;
    function getStickyFooterScrollbar(options: BaseOptionsRefined): boolean;


    interface ScrollGridProps {
        colGroups?: ColGroupConfig[];
        sections: ScrollGridSectionConfig[];
        liquid: boolean;
        elRef?: Ref<any>;
    }
    interface ScrollGridSectionConfig extends SectionConfig {
        key: string;
        chunks?: ScrollGridChunkConfig[];
    }
    interface ScrollGridChunkConfig extends ChunkConfig {
        key: string;
    }
    interface ColGroupConfig {
        width?: CssDimValue;
        cols: ColProps[];
    }
    type ScrollGridImpl = {
        new(props: ScrollGridProps, context: ViewContext): Component<ScrollGridProps>;
    };


    interface PluginDefInput {
        deps?: PluginDef[];
        reducers?: ReducerFunc[];
        contextInit?: (context: CalendarContext) => void;
        eventRefiners?: GenericRefiners;
        eventDefMemberAdders?: EventDefMemberAdder[];
        eventSourceRefiners?: GenericRefiners;
        isDraggableTransformers?: eventIsDraggableTransformer[];
        eventDragMutationMassagers?: eventDragMutationMassager[];
        eventDefMutationAppliers?: eventDefMutationApplier[];
        dateSelectionTransformers?: dateSelectionJoinTransformer[];
        datePointTransforms?: DatePointTransform[];
        dateSpanTransforms?: DateSpanTransform[];
        views?: ViewConfigInputHash;
        viewPropsTransformers?: ViewPropsTransformerClass[];
        isPropsValid?: isPropsValidTester;
        externalDefTransforms?: ExternalDefTransform[];
        eventResizeJoinTransforms?: EventResizeJoinTransforms[];
        viewContainerAppends?: ViewContainerAppend[];
        eventDropTransformers?: EventDropTransformers[];
        componentInteractions?: InteractionClass[];
        calendarInteractions?: CalendarInteractionClass[];
        themeClasses?: {
            [themeSystemName: string]: ThemeClass;
        };
        eventSourceDefs?: EventSourceDef<any>[];
        cmdFormatter?: CmdFormatterFunc;
        recurringTypes?: RecurringType<any>[];
        namedTimeZonedImpl?: NamedTimeZoneImplClass;
        initialView?: string;
        elementDraggingImpl?: ElementDraggingClass;
        optionChangeHandlers?: OptionChangeHandlerMap;
        scrollGridImpl?: ScrollGridImpl;
        contentTypeHandlers?: ContentTypeHandlers;
        listenerRefiners?: GenericListenerRefiners;
        optionRefiners?: GenericRefiners;
        propSetHandlers?: {
            [propName: string]: (val: any, context: CalendarData) => void;
        };
    }
    interface PluginHooks {
        reducers: ReducerFunc[];
        contextInit: ((context: CalendarContext) => void)[];
        eventRefiners: GenericRefiners;
        eventDefMemberAdders: EventDefMemberAdder[];
        eventSourceRefiners: GenericRefiners;
        isDraggableTransformers: eventIsDraggableTransformer[];
        eventDragMutationMassagers: eventDragMutationMassager[];
        eventDefMutationAppliers: eventDefMutationApplier[];
        dateSelectionTransformers: dateSelectionJoinTransformer[];
        datePointTransforms: DatePointTransform[];
        dateSpanTransforms: DateSpanTransform[];
        views: ViewConfigInputHash;
        viewPropsTransformers: ViewPropsTransformerClass[];
        isPropsValid: isPropsValidTester | null;
        externalDefTransforms: ExternalDefTransform[];
        eventResizeJoinTransforms: EventResizeJoinTransforms[];
        viewContainerAppends: ViewContainerAppend[];
        eventDropTransformers: EventDropTransformers[];
        componentInteractions: InteractionClass[];
        calendarInteractions: CalendarInteractionClass[];
        themeClasses: {
            [themeSystemName: string]: ThemeClass;
        };
        eventSourceDefs: EventSourceDef<any>[];
        cmdFormatter?: CmdFormatterFunc;
        recurringTypes: RecurringType<any>[];
        namedTimeZonedImpl?: NamedTimeZoneImplClass;
        initialView: string;
        elementDraggingImpl?: ElementDraggingClass;
        optionChangeHandlers: OptionChangeHandlerMap;
        scrollGridImpl: ScrollGridImpl | null;
        contentTypeHandlers: ContentTypeHandlers;
        listenerRefiners: GenericListenerRefiners;
        optionRefiners: GenericRefiners;
        propSetHandlers: {
            [propName: string]: (val: any, context: CalendarData) => void;
        };
    }
    interface PluginDef extends PluginHooks {
        id: string;
        deps: PluginDef[];
    }
    type ViewPropsTransformerClass = new () => ViewPropsTransformer;
    interface ViewPropsTransformer {
        transform(viewProps: ViewProps, calendarProps: CalendarContentProps): any;
    }
    type ViewContainerAppend = (context: CalendarContext) => ComponentChildren;


    interface ViewSpec {
        type: string;
        component: ViewComponentType;
        duration: Duration;
        durationUnit: string;
        singleUnit: string;
        optionDefaults: ViewOptions;
        optionOverrides: ViewOptions;
        buttonTextOverride: string;
        buttonTextDefault: string;
    }
    type ViewSpecHash = {
        [viewType: string]: ViewSpec;
    };

    interface CalendarDataManagerState {
        dynamicOptionOverrides: CalendarOptions;
        currentViewType: string;
        currentDate: Date;
        dateProfile: DateProfile;
        businessHours: EventStore;
        eventSources: EventSourceHash;
        eventUiBases: EventUiHash;
        loadingLevel: number;
        eventStore: EventStore;
        renderableEventStore: EventStore;
        dateSelection: DateSpan | null;
        eventSelection: string;
        eventDrag: EventInteractionState | null;
        eventResize: EventInteractionState | null;
        selectionConfig: EventUi;
    }
    interface CalendarOptionsData {
        localeDefaults: CalendarOptions;
        calendarOptions: CalendarOptionsRefined;
        toolbarConfig: any;
        availableRawLocales: any;
        dateEnv: DateEnv;
        theme: Theme;
        pluginHooks: PluginHooks;
        viewSpecs: ViewSpecHash;
    }
    interface CalendarCurrentViewData {
        viewSpec: ViewSpec;
        options: ViewOptionsRefined;
        viewApi: ViewApi;
        dateProfileGenerator: DateProfileGenerator;
    }
    type CalendarDataBase = CalendarOptionsData & CalendarCurrentViewData & CalendarDataManagerState;
    interface CalendarData extends CalendarDataBase {
        viewTitle: string;
        calendarApi: CalendarApi;
        dispatch: (action: Action) => void;
        emitter: Emitter<CalendarListeners>;
        getCurrentData(): CalendarData;
    }

    class ViewApi {
        type: string;
        private getCurrentData;
        private dateEnv;
        constructor(type: string, getCurrentData: () => CalendarData, dateEnv: DateEnv);
        get calendar(): CalendarApi;
        get title(): string;
        get activeStart(): Date;
        get activeEnd(): Date;
        get currentStart(): Date;
        get currentEnd(): Date;
        getOption(name: string): any;
    }


    interface ScrollRequest {
        time?: Duration;
        [otherProp: string]: any;
    }
    type ScrollRequestHandler = (request: ScrollRequest) => boolean;
    class ScrollResponder {
        private execFunc;
        private emitter;
        private scrollTime;
        queuedRequest: ScrollRequest;
        constructor(execFunc: ScrollRequestHandler, emitter: Emitter<CalendarListeners>, scrollTime: Duration);
        detach(): void;
        update(isDatesNew: boolean): void;
        private fireInitialScroll;
        private handleScrollRequest;
        private drain;
    }


    const ViewContextType: Context<ViewContext>;
    type ResizeHandler = (force: boolean) => void;
    interface ViewContext extends CalendarContext {
        options: ViewOptionsRefined;
        theme: Theme;
        isRtl: boolean;
        dateProfileGenerator: DateProfileGenerator;
        viewSpec: ViewSpec;
        viewApi: ViewApi;
        addResizeHandler: (handler: ResizeHandler) => void;
        removeResizeHandler: (handler: ResizeHandler) => void;
        createScrollResponder: (execFunc: ScrollRequestHandler) => ScrollResponder;
        registerInteractiveComponent: (component: DateComponent<any>, settingsInput: InteractionSettingsInput) => void;
        unregisterInteractiveComponent: (component: DateComponent<any>) => void;
    }

    function filterHash(hash: any, func: any): {};
    function mapHash<InputItem, OutputItem>(hash: {
        [key: string]: InputItem;
    }, func: (input: InputItem, key: string) => OutputItem): {
        [key: string]: OutputItem;
    };
    function buildHashFromArray<Item, ItemRes>(a: Item[], func: (item: Item, index: number) => [string, ItemRes]): {
        [key: string]: ItemRes;
    };
    function isPropsEqual(obj0: any, obj1: any): boolean;
    function getUnequalProps(obj0: any, obj1: any): string[];
    type EqualityFunc<T> = (a: T, b: T) => boolean;
    type EqualityThing<T> = EqualityFunc<T> | true;
    type EqualityFuncs<ObjType> = {
        [K in keyof ObjType]?: EqualityThing<ObjType[K]>;
    };
    function compareObjs(oldProps: any, newProps: any, equalityFuncs?: EqualityFuncs<any>): boolean;
    function collectFromHash<Item>(hash: {
        [key: string]: Item;
    }, startIndex?: number, endIndex?: number, step?: number): Item[];


    abstract class PureComponent<Props = Dictionary, State = Dictionary> extends Component<Props, State> {
        static addPropsEquality: typeof addPropsEquality;
        static addStateEquality: typeof addStateEquality;
        static contextType: Context<ViewContext>;
        context: ViewContext;
        propEquality: EqualityFuncs<Props>;
        stateEquality: EqualityFuncs<State>;
        debug: boolean;
        shouldComponentUpdate(nextProps: Props, nextState: State): boolean;
    }
    abstract class BaseComponent<Props = Dictionary, State = Dictionary> extends PureComponent<Props, State> {
        static contextType: Context<ViewContext>;
        context: ViewContext;
    }
    function addPropsEquality(this: {
        prototype: {
            propEquality: any;
        };
    }, propEquality: any): void;
    function addStateEquality(this: {
        prototype: {
            stateEquality: any;
        };
    }, stateEquality: any): void;
    function setRef<RefType>(ref: Ref<RefType> | void, current: RefType): void;

    interface EventInstance {
        instanceId: string;
        defId: string;
        range: DateRange;
        forcedStartTzo: number | null;
        forcedEndTzo: number | null;
    }
    type EventInstanceHash = {
        [instanceId: string]: EventInstance;
    };
    function createEventInstance(defId: string, range: DateRange, forcedStartTzo?: number, forcedEndTzo?: number): EventInstance;


    interface Seg {
        component?: DateComponent<any, any>;
        isStart: boolean;
        isEnd: boolean;
        eventRange?: EventRenderRange;
        [otherProp: string]: any;
        el?: never;
    }
    interface EventSegUiInteractionState {
        affectedInstances: EventInstanceHash;
        segs: Seg[];
        isEvent: boolean;
    }
    abstract class DateComponent<Props = Dictionary, State = Dictionary> extends BaseComponent<Props, State> {
        uid: string;
        largeUnit: any;
        prepareHits(): void;
        queryHit(positionLeft: number, positionTop: number, elWidth: number, elHeight: number): Hit | null;
        isInteractionValid(interaction: EventInteractionState): boolean;
        isDateSelectionValid(selection: DateSpan): boolean;
        isValidSegDownEl(el: HTMLElement): boolean;
        isValidDateDownEl(el: HTMLElement): boolean;
        isPopover(): boolean;
        isInPopover(el: HTMLElement): boolean;
    }


    interface NativeFormatterOptions extends Intl.DateTimeFormatOptions {
        week?: 'short' | 'narrow' | 'numeric';
        meridiem?: 'lowercase' | 'short' | 'narrow' | boolean;
        omitZeroMinute?: boolean;
        omitCommas?: boolean;
        separator?: string;
    }

    type FuncFormatterFunc = (arg: VerboseFormattingArg) => string;

    type FormatterInput = NativeFormatterOptions | string | FuncFormatterFunc;
    function createFormatter(input: FormatterInput): DateFormatter;


    type BusinessHoursInput = boolean | EventInput | EventInput[];
    function parseBusinessHours(input: BusinessHoursInput, context: CalendarContext): EventStore;


    interface NowIndicatorRootProps {
        isAxis: boolean;
        date: Date;
        children: RenderHookPropsChildren;
    }
    interface NowIndicatorContentArg {
        isAxis: boolean;
        date: Date;
        view: ViewApi;
    }
    type NowIndicatorMountArg = MountArg<NowIndicatorContentArg>;
    const NowIndicatorRoot: (props: NowIndicatorRootProps) => createElement.JSX.Element;


    interface WeekNumberRootProps {
        date: Date;
        defaultFormat: DateFormatter;
        children: RenderHookPropsChildren;
    }
    interface WeekNumberContentArg {
        num: number;
        text: string;
        date: Date;
    }
    type WeekNumberMountArg = MountArg<WeekNumberContentArg>;
    const WeekNumberRoot: (props: WeekNumberRootProps) => createElement.JSX.Element;


    interface DateMeta {
        dow: number;
        isDisabled: boolean;
        isOther: boolean;
        isToday: boolean;
        isPast: boolean;
        isFuture: boolean;
    }
    function getDateMeta(date: Date, todayRange?: DateRange, nowDate?: Date, dateProfile?: DateProfile): DateMeta;
    function getDayClassNames(meta: DateMeta, theme: Theme): string[];
    function getSlotClassNames(meta: DateMeta, theme: Theme): string[];


    interface SlotLaneContentArg extends Partial<DateMeta> {
        time?: Duration;
        date?: Date;
        view: ViewApi;
    }
    type SlotLaneMountArg = MountArg<SlotLaneContentArg>;
    interface SlotLabelContentArg {
        level: number;
        time: Duration;
        date: Date;
        view: ViewApi;
        text: string;
    }
    type SlotLabelMountArg = MountArg<SlotLabelContentArg>;
    interface AllDayContentArg {
        text: string;
        view: ViewApi;
    }
    type AllDayMountArg = MountArg<AllDayContentArg>;
    interface DayHeaderContentArg extends DateMeta {
        date: Date;
        view: ViewApi;
        text: string;
        [otherProp: string]: any;
    }
    type DayHeaderMountArg = MountArg<DayHeaderContentArg>;


    interface DayCellHookPropsInput {
        date: Date;
        dateProfile: DateProfile;
        todayRange: DateRange;
        dateEnv: DateEnv;
        viewApi: ViewApi;
        showDayNumber?: boolean;
        extraProps?: Dictionary;
    }
    interface DayCellContentArg extends DateMeta {
        date: Date;
        view: ViewApi;
        dayNumberText: string;
        [extraProp: string]: any;
    }
    type DayCellMountArg = MountArg<DayCellContentArg>;
    interface DayCellRootProps {
        elRef?: Ref<any>;
        date: Date;
        dateProfile: DateProfile;
        todayRange: DateRange;
        showDayNumber?: boolean;
        extraHookProps?: Dictionary;
        children: (rootElRef: Ref<any>, classNames: string[], rootDataAttrs: any, isDisabled: boolean) => ComponentChildren;
    }
    class DayCellRoot extends BaseComponent<DayCellRootProps> {
        refineHookProps: (arg: DayCellHookPropsInput) => DayCellContentArg;
        normalizeClassNames: (generator: ClassNamesGenerator<DayCellContentArg>, hookProps: DayCellContentArg) => string[];
        render(): createElement.JSX.Element;
    }
    interface DayCellContentProps {
        date: Date;
        dateProfile: DateProfile;
        todayRange: DateRange;
        showDayNumber?: boolean;
        extraHookProps?: Dictionary;
        defaultContent?: (hookProps: DayCellContentArg) => ComponentChildren;
        children: (innerElRef: Ref<any>, innerContent: ComponentChildren) => ComponentChildren;
    }
    class DayCellContent extends BaseComponent<DayCellContentProps> {
        render(): createElement.JSX.Element;
    }

    interface ViewRootProps {
        viewSpec: ViewSpec;
        children: (rootElRef: Ref<any>, classNames: string[]) => ComponentChildren;
        elRef?: Ref<any>;
    }
    interface ViewContentArg {
        view: ViewApi;
    }
    type ViewMountArg = MountArg<ViewContentArg>;
    class ViewRoot extends BaseComponent<ViewRootProps> {
        normalizeClassNames: (generator: ClassNamesGenerator<ViewContentArg>, hookProps: ViewContentArg) => string[];
        render(): createElement.JSX.Element;
    }


    interface EventClickArg {
        el: HTMLElement;
        event: EventApi;
        jsEvent: MouseEvent;
        view: ViewApi;
    }

    interface EventHoveringArg {
        el: HTMLElement;
        event: EventApi;
        jsEvent: MouseEvent;
        view: ViewApi;
    }

    interface ToolbarInput {
        left?: string;
        center?: string;
        right?: string;
        start?: string;
        end?: string;
    }
    interface CustomButtonInput {
        text?: string;
        icon?: string;
        themeIcon?: string;
        bootstrapFontAwesome?: string;
        click?(ev: MouseEvent, element: HTMLElement): void;
    }
    interface ButtonIconsInput {
        prev?: string;
        next?: string;
        prevYear?: string;
        nextYear?: string;
        today?: string;
        [viewOrCustomButton: string]: string | undefined;
    }
    interface ButtonTextCompoundInput {
        prev?: string;
        next?: string;
        prevYear?: string;
        nextYear?: string;
        today?: string;
        month?: string;
        week?: string;
        day?: string;
        [viewOrCustomButton: string]: string | undefined;
    }


    type DatesSetArg = RangeApiWithTimeZone & {
        view: ViewApi;
    };

    interface EventAddArg {
        event: EventApi;
        relatedEvents: EventApi[];
        revert: () => void;
    }
    interface EventChangeArg {
        oldEvent: EventApi;
        event: EventApi;
        relatedEvents: EventApi[];
        revert: () => void;
    }
    interface EventRemoveArg {
        event: EventApi;
        relatedEvents: EventApi[];
        revert: () => void;
    }

    class EventApi {
        _context: CalendarContext;
        _def: EventDef;
        _instance: EventInstance | null;
        constructor(context: CalendarContext, def: EventDef, instance?: EventInstance);
        setProp(name: string, val: string | boolean | Date): void;
        setExtendedProp(name: string, val: any): void;
        setStart(startInput: DateInput, options?: {
            granularity?: string;
            maintainDuration?: boolean;
        }): void;
        setEnd(endInput: DateInput | null, options?: {
            granularity?: string;
        }): void;
        setDates(startInput: DateInput, endInput: DateInput | null, options?: {
            allDay?: boolean;
            granularity?: string;
        }): void;
        moveStart(deltaInput: DurationInput): void;
        moveEnd(deltaInput: DurationInput): void;
        moveDates(deltaInput: DurationInput): void;
        setAllDay(allDay: boolean, options?: {
            maintainDuration?: boolean;
        }): void;
        formatRange(formatInput: FormatterInput): string;
        mutate(mutation: EventMutation): void;
        remove(): void;
        get source(): EventSourceApi | null;
        get start(): Date | null;
        get end(): Date | null;
        get startStr(): string;
        get endStr(): string;
        get id(): string;
        get groupId(): string;
        get allDay(): boolean;
        get title(): string;
        get url(): string;
        get display(): string;
        get startEditable(): boolean;
        get durationEditable(): boolean;
        get constraint(): string | EventStore;
        get overlap(): boolean;
        get allow(): AllowFunc;
        get backgroundColor(): string;
        get borderColor(): string;
        get textColor(): string;
        get classNames(): string[];
        get extendedProps(): Record<string, any>;
        toPlainObject(settings?: {
            collapseExtendedProps?: boolean;
            collapseColor?: boolean;
        }): Dictionary;
        toJSON(): Record<string, any>;
    }
    function buildEventApis(eventStore: EventStore, context: CalendarContext, excludeInstance?: EventInstance): EventApi[];


    interface EventRenderRange extends EventTuple {
        ui: EventUi;
        range: DateRange;
        isStart: boolean;
        isEnd: boolean;
    }
    function sliceEventStore(eventStore: EventStore, eventUiBases: EventUiHash, framingRange: DateRange, nextDayThreshold?: Duration): {
        bg: EventRenderRange[];
        fg: EventRenderRange[];
    };
    function hasBgRendering(def: EventDef): boolean;
    function setElSeg(el: HTMLElement, seg: Seg): void;
    function getElSeg(el: HTMLElement): Seg | null;
    function sortEventSegs(segs: any, eventOrderSpecs: OrderSpec<EventApi>[]): Seg[];
    function buildSegCompareObj(seg: Seg): {
        id: string;
        start: number;
        end: number;
        duration: number;
        allDay: number;
        _seg: Seg;
        defId: string;
        sourceId: string;
        publicId: string;
        groupId: string;
        hasEnd: boolean;
        recurringDef: {
            typeId: number;
            typeData: any;
            duration: Duration;
        };
        title: string;
        url: string;
        ui: EventUi;
        extendedProps: Record<string, any>;
    };
    interface EventContentArg {
        event: EventApi;
        timeText: string;
        backgroundColor: string;
        borderColor: string;
        textColor: string;
        isDraggable: boolean;
        isStartResizable: boolean;
        isEndResizable: boolean;
        isMirror: boolean;
        isStart: boolean;
        isEnd: boolean;
        isPast: boolean;
        isFuture: boolean;
        isToday: boolean;
        isSelected: boolean;
        isDragging: boolean;
        isResizing: boolean;
        view: ViewApi;
    }
    type EventMountArg = MountArg<EventContentArg>;
    function computeSegDraggable(seg: Seg, context: ViewContext): boolean;
    function computeSegStartResizable(seg: Seg, context: ViewContext): boolean;
    function computeSegEndResizable(seg: Seg, context: ViewContext): boolean;
    function buildSegTimeText(seg: Seg, timeFormat: DateFormatter, context: ViewContext, defaultDisplayEventTime?: boolean,
        defaultDisplayEventEnd?: boolean,
        startOverride?: Date, endOverride?: Date): string;
    function getSegMeta(seg: Seg, todayRange: DateRange, nowDate?: Date): {
        isPast: boolean;
        isFuture: boolean;
        isToday: boolean;
    };
    function getEventClassNames(props: EventContentArg): string[];
    function buildEventRangeKey(eventRange: EventRenderRange): string;


    interface OpenDateSpanInput {
        start?: DateInput;
        end?: DateInput;
        allDay?: boolean;
        [otherProp: string]: any;
    }
    interface DateSpanInput extends OpenDateSpanInput {
        start: DateInput;
        end: DateInput;
    }
    interface OpenDateSpan {
        range: OpenDateRange;
        allDay: boolean;
        [otherProp: string]: any;
    }
    interface DateSpan extends OpenDateSpan {
        range: DateRange;
    }
    interface RangeApi {
        start: Date;
        end: Date;
        startStr: string;
        endStr: string;
    }
    interface DateSpanApi extends RangeApi {
        allDay: boolean;
    }
    interface RangeApiWithTimeZone extends RangeApi {
        timeZone: string;
    }
    interface DatePointApi {
        date: Date;
        dateStr: string;
        allDay: boolean;
    }
    function isDateSpansEqual(span0: DateSpan, span1: DateSpan): boolean;

    type Action = {
        type: 'NOTHING';
    } |
    {
        type: 'SET_OPTION';
        optionName: string;
        rawOptionValue: any;
    } |
    {
        type: 'PREV';
    } | {
        type: 'NEXT';
    } | {
        type: 'CHANGE_DATE';
        Date: Date;
    } | {
        type: 'CHANGE_VIEW_TYPE';
        viewType: string;
        Date?: Date;
    } | {
        type: 'SELECT_DATES';
        selection: DateSpan;
    } | {
        type: 'UNSELECT_DATES';
    } | {
        type: 'SELECT_EVENT';
        eventInstanceId: string;
    } | {
        type: 'UNSELECT_EVENT';
    } | {
        type: 'SET_EVENT_DRAG';
        state: EventInteractionState;
    } | {
        type: 'UNSET_EVENT_DRAG';
    } | {
        type: 'SET_EVENT_RESIZE';
        state: EventInteractionState;
    } | {
        type: 'UNSET_EVENT_RESIZE';
    } | {
        type: 'ADD_EVENT_SOURCES';
        sources: EventSource<any>[];
    } | {
        type: 'REMOVE_EVENT_SOURCE';
        sourceId: string;
    } | {
        type: 'REMOVE_ALL_EVENT_SOURCES';
    } | {
        type: 'FETCH_EVENT_SOURCES';
        sourceIds?: string[];
    } |
    {
        type: 'RECEIVE_EVENTS';
        sourceId: string;
        fetchId: string;
        fetchRange: DateRange | null;
        rawEvents: EventInput[];
    } | {
        type: 'RECEIVE_EVENT_ERROR';
        sourceId: string;
        fetchId: string;
        fetchRange: DateRange | null;
        error: EventSourceError;
    } |
    {
        type: 'ADD_EVENTS';
        eventStore: EventStore;
    } | {
        type: 'MERGE_EVENTS';
        eventStore: EventStore;
    } | {
        type: 'REMOVE_EVENTS';
        eventStore: EventStore;
    } | {
        type: 'REMOVE_ALL_EVENTS';
    };


    interface CalendarDataManagerProps {
        optionOverrides: CalendarOptions;
        calendarApi: CalendarApi;
        onAction?: (action: Action) => void;
        onData?: (data: CalendarData) => void;
    }

    type ReducerFunc = (currentState: Dictionary | null, action: Action | null, context: CalendarContext & CalendarDataManagerState) => Dictionary;

    class CalendarDataManager {
        private computeOptionsData;
        private computeCurrentViewData;
        private organizeRawLocales;
        private buildLocale;
        private buildPluginHooks;
        private buildDateEnv;
        private buildTheme;
        private parseToolbars;
        private buildViewSpecs;
        private buildDateProfileGenerator;
        private buildViewApi;
        private buildViewUiProps;
        private buildEventUiBySource;
        private buildEventUiBases;
        private parseContextBusinessHours;
        private buildTitle;
        emitter: Emitter<Required<RefinedOptionsFromRefiners<Required<CalendarListenerRefiners>>>>;
        private actionRunner;
        private props;
        private state;
        private data;
        currentCalendarOptionsInput: CalendarOptions;
        private currentCalendarOptionsRefined;
        private currentViewOptionsInput;
        private currentViewOptionsRefined;
        currentCalendarOptionsRefiners: any;
        constructor(props: CalendarDataManagerProps);
        getCurrentData: () => CalendarData;
        dispatch: (action: Action) => void;
        resetOptions(optionOverrides: CalendarOptions, append?: boolean): void;
        _handleAction(action: Action): void;
        updateData(): void;
        _computeOptionsData(optionOverrides: CalendarOptions, dynamicOptionOverrides: CalendarOptions, calendarApi: CalendarApi): CalendarOptionsData;
        processRawCalendarOptions(optionOverrides: CalendarOptions, dynamicOptionOverrides: CalendarOptions): {
            rawOptions: CalendarOptions;
            refinedOptions: CalendarOptionsRefined;
            pluginHooks: PluginHooks;
            availableLocaleData: RawLocaleInfo;
            localeDefaults: CalendarOptionsRefined;
            extra: {};
        };
        _computeCurrentViewData(viewType: string, optionsData: CalendarOptionsData, optionOverrides: CalendarOptions, dynamicOptionOverrides: CalendarOptions): CalendarCurrentViewData;
        processRawViewOptions(viewSpec: ViewSpec, pluginHooks: PluginHooks, localeDefaults: CalendarOptions, optionOverrides: CalendarOptions, dynamicOptionOverrides: CalendarOptions): {
            rawOptions: ViewOptions;
            refinedOptions: ViewOptionsRefined;
            extra: {};
        };
    }


    class CalendarApi {
        currentDataManager?: CalendarDataManager;
        getCurrentData(): CalendarData;
        dispatch(action: Action): void;
        get view(): ViewApi;
        batchRendering(callback: () => void): void;
        updateSize(): void;
        setOption<OptionName extends keyof CalendarOptions>(name: OptionName, val: CalendarOptions[OptionName]): void;
        getOption<OptionName extends keyof CalendarOptions>(name: OptionName): CalendarOptions[OptionName];
        getAvailableLocaleCodes(): string[];
        on<ListenerName extends keyof CalendarListeners>(handlerName: ListenerName, handler: CalendarListeners[ListenerName]): void;
        off<ListenerName extends keyof CalendarListeners>(handlerName: ListenerName, handler: CalendarListeners[ListenerName]): void;
        trigger<ListenerName extends keyof CalendarListeners>(handlerName: ListenerName, ...args: Parameters<CalendarListeners[ListenerName]>): void;
        changeView(viewType: string, dateOrRange?: DateRangeInput | DateInput): void;
        zoomTo(Date: Date, viewType?: string): void;
        private getUnitViewSpec;
        prev(): void;
        next(): void;
        prevYear(): void;
        nextYear(): void;
        today(): void;
        gotoDate(zonedDateInput: any): void;
        incrementDate(deltaInput: any): void;
        getDate(): Date;
        formatDate(d: DateInput, formatter: any): string;
        formatRange(d0: DateInput, d1: DateInput, settings: any): string;
        formatIso(d: DateInput, omitTime?: boolean): string;
        select(dateOrObj: DateInput | any, endDate?: DateInput): void;
        unselect(pev?: PointerDragEvent): void;
        addEvent(eventInput: EventInput, sourceInput?: EventSourceApi | string | boolean): EventApi | null;
        private triggerEventAdd;
        getEventById(id: string): EventApi | null;
        getEvents(): EventApi[];
        removeAllEvents(): void;
        getEventSources(): EventSourceApi[];
        getEventSourceById(id: string): EventSourceApi | null;
        addEventSource(sourceInput: EventSourceInput): EventSourceApi;
        removeAllEventSources(): void;
        refetchEvents(): void;
        scrollToTime(timeInput: DurationInput): void;
    }


    interface DateProfile {
        currentRange: DateRange;
        currentRangeUnit: string;
        isRangeAllDay: boolean;
        validRange: OpenDateRange;
        activeRange: DateRange | null;
        renderRange: DateRange;
        slotMinTime: Duration;
        slotMaxTime: Duration;
        isValid: boolean;
        dateIncrement: Duration;
    }
    interface DateProfileGeneratorProps extends DateProfileOptions {
        dateProfileGeneratorClass: DateProfileGeneratorClass;
        duration: Duration;
        durationUnit: string;
        usesMinMaxTime: boolean;
        dateEnv: DateEnv;
        calendarApi: CalendarApi;
    }
    interface DateProfileOptions {
        slotMinTime: Duration;
        slotMaxTime: Duration;
        showNonCurrentDates?: boolean;
        dayCount?: number;
        dateAlignment?: string;
        dateIncrement?: Duration;
        hiddenDays?: number[];
        weekends?: boolean;
        nowInput?: DateInput | (() => DateInput);
        validRangeInput?: DateRangeInput | ((this: CalendarApi, nowDate: Date) => DateRangeInput);
        visibleRangeInput?: DateRangeInput | ((this: CalendarApi, nowDate: Date) => DateRangeInput);
        monthMode?: boolean;
        fixedWeekCount?: boolean;
    }
    type DateProfileGeneratorClass = {
        new(props: DateProfileGeneratorProps): DateProfileGenerator;
    };
    class DateProfileGenerator {
        protected props: DateProfileGeneratorProps;
        nowDate: Date;
        isHiddenDayHash: boolean[];
        constructor(props: DateProfileGeneratorProps);
        buildPrev(currentDateProfile: DateProfile, currentDate: Date, forceToValid?: boolean): DateProfile;
        buildNext(currentDateProfile: DateProfile, currentDate: Date, forceToValid?: boolean): DateProfile;
        build(currentDate: Date, direction?: any, forceToValid?: boolean): DateProfile;
        buildValidRange(): OpenDateRange;
        buildCurrentRangeInfo(date: Date, direction: any): {
            duration: any;
            unit: any;
            range: any;
        };
        getFallbackDuration(): Duration;
        adjustActiveRange(range: DateRange): {
            start: Date;
            end: Date;
        };
        buildRangeFromDuration(date: Date, direction: any, duration: Duration, unit: any): any;
        buildRangeFromDayCount(date: Date, direction: any, dayCount: any): {
            start: Date;
            end: Date;
        };
        buildCustomVisibleRange(date: Date): DateRange;
        buildRenderRange(currentRange: DateRange, currentRangeUnit: any, isRangeAllDay: any): DateRange;
        buildDateIncrement(fallback: any): Duration;
        refineRange(rangeInput: DateRangeInput | undefined): DateRange | null;
        initHiddenDays(): void;
        trimHiddenDays(range: DateRange): DateRange | null;
        isHiddenDay(day: any): boolean;
        skipHiddenDays(date: Date, inc?: number, isExclusive?: boolean): Date;
    }


    interface ViewProps {
        dateProfile: DateProfile;
        businessHours: EventStore;
        eventStore: EventStore;
        eventUiBases: EventUiHash;
        dateSelection: DateSpan | null;
        eventSelection: string;
        eventDrag: EventInteractionState | null;
        eventResize: EventInteractionState | null;
        isHeightAuto: boolean;
        forPrint: boolean;
    }
    function sliceEvents(props: ViewProps & {
        dateProfile: DateProfile;
        nextDayThreshold: Duration;
    }, allDay?: boolean): EventRenderRange[];

    const BASE_OPTION_REFINERS: {
        navLinkDayClick: Identity<string | ((this: CalendarApi, date: Date, jsEvent: VUIEvent) => void)>;
        navLinkWeekClick: Identity<string | ((this: CalendarApi, weekStart: Date, jsEvent: VUIEvent) => void)>;
        duration: typeof createDuration;
        bootstrapFontAwesome: Identity<false | ButtonIconsInput>;
        buttonIcons: Identity<false | ButtonIconsInput>;
        customButtons: Identity<{
            [name: string]: CustomButtonInput;
        }>;
        defaultAllDayEventDuration: typeof createDuration;
        defaultTimedEventDuration: typeof createDuration;
        nextDayThreshold: typeof createDuration;
        scrollTime: typeof createDuration;
        slotMinTime: typeof createDuration;
        slotMaxTime: typeof createDuration;
        dayPopoverFormat: typeof createFormatter;
        slotDuration: typeof createDuration;
        snapDuration: typeof createDuration;
        headerToolbar: Identity<false | ToolbarInput>;
        footerToolbar: Identity<false | ToolbarInput>;
        defaultRangeSeparator: StringConstructor;
        titleRangeSeparator: StringConstructor;
        forceEventDuration: BooleanConstructor;
        dayHeaders: BooleanConstructor;
        dayHeaderFormat: typeof createFormatter;
        dayHeaderClassNames: Identity<ClassNamesGenerator<DayHeaderContentArg>>;
        dayHeaderContent: Identity<CustomContentGenerator<DayHeaderContentArg>>;
        dayHeaderDidMount: Identity<DidMountHandler<MountArg<DayHeaderContentArg>>>;
        dayHeaderWillUnmount: Identity<WillUnmountHandler<MountArg<DayHeaderContentArg>>>;
        dayCellClassNames: Identity<ClassNamesGenerator<DayCellContentArg>>;
        dayCellContent: Identity<CustomContentGenerator<DayCellContentArg>>;
        dayCellDidMount: Identity<DidMountHandler<MountArg<DayCellContentArg>>>;
        dayCellWillUnmount: Identity<WillUnmountHandler<MountArg<DayCellContentArg>>>;
        initialView: StringConstructor;
        aspectRatio: NumberConstructor;
        weekends: BooleanConstructor;
        weekNumberCalculation: Identity<WeekNumberCalculation>;
        weekNumbers: BooleanConstructor;
        weekNumberClassNames: Identity<ClassNamesGenerator<WeekNumberContentArg>>;
        weekNumberContent: Identity<CustomContentGenerator<WeekNumberContentArg>>;
        weekNumberDidMount: Identity<DidMountHandler<MountArg<WeekNumberContentArg>>>;
        weekNumberWillUnmount: Identity<WillUnmountHandler<MountArg<WeekNumberContentArg>>>;
        editable: BooleanConstructor;
        viewClassNames: Identity<ClassNamesGenerator<ViewContentArg>>;
        viewDidMount: Identity<DidMountHandler<MountArg<ViewContentArg>>>;
        viewWillUnmount: Identity<WillUnmountHandler<MountArg<ViewContentArg>>>;
        nowIndicator: BooleanConstructor;
        nowIndicatorClassNames: Identity<ClassNamesGenerator<NowIndicatorContentArg>>;
        nowIndicatorContent: Identity<CustomContentGenerator<NowIndicatorContentArg>>;
        nowIndicatorDidMount: Identity<DidMountHandler<MountArg<NowIndicatorContentArg>>>;
        nowIndicatorWillUnmount: Identity<WillUnmountHandler<MountArg<NowIndicatorContentArg>>>;
        showNonCurrentDates: BooleanConstructor;
        lazyFetching: BooleanConstructor;
        startParam: StringConstructor;
        endParam: StringConstructor;
        timeZoneParam: StringConstructor;
        timeZone: StringConstructor;
        locales: Identity<LocaleInput[]>;
        locale: Identity<LocaleSingularArg>;
        themeSystem: Identity<string>;
        dragRevertDuration: NumberConstructor;
        dragScroll: BooleanConstructor;
        allDayMaintainDuration: BooleanConstructor;
        unselectAuto: BooleanConstructor;
        dropAccept: Identity<string | ((this: CalendarApi, draggable: any) => boolean)>;
        eventOrder: typeof parseFieldSpecs;
        handleWindowResize: BooleanConstructor;
        windowResizeDelay: NumberConstructor;
        longPressDelay: NumberConstructor;
        eventDragMinDistance: NumberConstructor;
        expandRows: BooleanConstructor;
        height: Identity<string | number>;
        contentHeight: Identity<string | number>;
        direction: Identity<"ltr" | "rtl">;
        weekNumberFormat: typeof createFormatter;
        eventResizableFromStart: BooleanConstructor;
        displayEventTime: BooleanConstructor;
        displayEventEnd: BooleanConstructor;
        weekText: StringConstructor;
        progressiveEventRendering: BooleanConstructor;
        businessHours: Identity<BusinessHoursInput>;
        initialDate: Identity<DateInput>;
        now: Identity<string | number | Date | number[] | ((this: CalendarApi) => DateInput)>;
        eventDataTransform: Identity<EventInputTransformer>;
        stickyHeaderDates: Identity<boolean | "auto">;
        stickyFooterScrollbar: Identity<boolean | "auto">;
        viewHeight: Identity<string | number>;
        defaultAllDay: BooleanConstructor;
        eventSourceFailure: Identity<(this: CalendarApi, error: any) => void>;
        eventSourceSuccess: Identity<(this: CalendarApi, eventsInput: EventInput[], xhr?: XMLHttpRequest) => EventInput[] | void>;
        eventDisplay: StringConstructor;
        eventStartEditable: BooleanConstructor;
        eventDurationEditable: BooleanConstructor;
        eventOverlap: Identity<boolean | OverlapFunc>;
        eventConstraint: Identity<ConstraintInput>;
        eventAllow: Identity<AllowFunc>;
        eventBackgroundColor: StringConstructor;
        eventBorderColor: StringConstructor;
        eventTextColor: StringConstructor;
        eventColor: StringConstructor;
        eventClassNames: Identity<ClassNamesGenerator<EventContentArg>>;
        eventContent: Identity<CustomContentGenerator<EventContentArg>>;
        eventDidMount: Identity<DidMountHandler<MountArg<EventContentArg>>>;
        eventWillUnmount: Identity<WillUnmountHandler<MountArg<EventContentArg>>>;
        selectConstraint: Identity<ConstraintInput>;
        selectOverlap: Identity<boolean | OverlapFunc>;
        selectAllow: Identity<AllowFunc>;
        droppable: BooleanConstructor;
        unselectCancel: StringConstructor;
        slotLabelFormat: Identity<string | NativeFormatterOptions | FuncFormatterFunc | FormatterInput[]>;
        slotLaneClassNames: Identity<ClassNamesGenerator<SlotLaneContentArg>>;
        slotLaneContent: Identity<CustomContentGenerator<SlotLaneContentArg>>;
        slotLaneDidMount: Identity<DidMountHandler<MountArg<SlotLaneContentArg>>>;
        slotLaneWillUnmount: Identity<WillUnmountHandler<MountArg<SlotLaneContentArg>>>;
        slotLabelClassNames: Identity<ClassNamesGenerator<SlotLabelContentArg>>;
        slotLabelContent: Identity<CustomContentGenerator<SlotLabelContentArg>>;
        slotLabelDidMount: Identity<DidMountHandler<MountArg<SlotLabelContentArg>>>;
        slotLabelWillUnmount: Identity<WillUnmountHandler<MountArg<SlotLabelContentArg>>>;
        dayMaxEvents: Identity<number | boolean>;
        dayMaxEventRows: Identity<number | boolean>;
        dayMinWidth: NumberConstructor;
        slotLabelInterval: typeof createDuration;
        allDayText: StringConstructor;
        allDaySlot: BooleanConstructor;
        allDayClassNames: Identity<ClassNamesGenerator<AllDayContentArg>>;
        allDayContent: Identity<CustomContentGenerator<AllDayContentArg>>;
        allDayDidMount: Identity<DidMountHandler<MountArg<AllDayContentArg>>>;
        allDayWillUnmount: Identity<WillUnmountHandler<MountArg<AllDayContentArg>>>;
        slotMinWidth: NumberConstructor;
        navLinks: BooleanConstructor;
        eventTimeFormat: typeof createFormatter;
        rerenderDelay: NumberConstructor;
        moreLinkText: Identity<string | ((this: CalendarApi, num: number) => string)>;
        selectMinDistance: NumberConstructor;
        selectable: BooleanConstructor;
        selectLongPressDelay: NumberConstructor;
        eventLongPressDelay: NumberConstructor;
        selectMirror: BooleanConstructor;
        eventMinHeight: NumberConstructor;
        slotEventOverlap: BooleanConstructor;
        plugins: Identity<PluginDef[]>;
        firstDay: NumberConstructor;
        dayCount: NumberConstructor;
        dateAlignment: StringConstructor;
        dateIncrement: typeof createDuration;
        hiddenDays: Identity<number[]>;
        monthMode: BooleanConstructor;
        fixedWeekCount: BooleanConstructor;
        validRange: Identity<DateRangeInput | ((this: CalendarApi, nowDate: Date) => DateRangeInput)>;
        visibleRange: Identity<DateRangeInput | ((this: CalendarApi, currentDate: Date) => DateRangeInput)>;
        titleFormat: Identity<FormatterInput>;
        noEventsText: StringConstructor;
    };

    type BuiltInBaseOptionRefiners = typeof BASE_OPTION_REFINERS;

    interface BaseOptionRefiners extends BuiltInBaseOptionRefiners {
    }

    type BaseOptions = RawOptionsFromRefiners<Required<BaseOptionRefiners>>;

    const BASE_OPTION_DEFAULTS: {
        eventDisplay: string;
        defaultRangeSeparator: string;
        titleRangeSeparator: string;
        defaultTimedEventDuration: string;
        defaultAllDayEventDuration: {
            day: number;
        };
        forceEventDuration: boolean;
        nextDayThreshold: string;
        dayHeaders: boolean;
        initialView: string;
        aspectRatio: number;
        headerToolbar: {
            start: string;
            center: string;
            end: string;
        };
        weekends: boolean;
        weekNumbers: boolean;
        weekNumberCalculation: WeekNumberCalculation;
        editable: boolean;
        nowIndicator: boolean;
        scrollTime: string;
        slotMinTime: string;
        slotMaxTime: string;
        showNonCurrentDates: boolean;
        lazyFetching: boolean;
        startParam: string;
        endParam: string;
        timeZoneParam: string;
        timeZone: string;
        locales: any[];
        locale: string;
        themeSystem: string;
        dragRevertDuration: number;
        dragScroll: boolean;
        allDayMaintainDuration: boolean;
        unselectAuto: boolean;
        dropAccept: string;
        eventOrder: string;
        dayPopoverFormat: {
            month: string;
            day: string;
            year: string;
        };
        handleWindowResize: boolean;
        windowResizeDelay: number;
        longPressDelay: number;
        eventDragMinDistance: number;
        expandRows: boolean;
        navLinks: boolean;
        selectable: boolean;
    };

    type BaseOptionsRefined = DefaultedRefinedOptions<RefinedOptionsFromRefiners<Required<BaseOptionRefiners>>, keyof typeof BASE_OPTION_DEFAULTS>;

    const CALENDAR_LISTENER_REFINERS: {
        datesSet: Identity<(arg: DatesSetArg) => void>;
        eventsSet: Identity<(events: EventApi[]) => void>;
        eventAdd: Identity<(arg: EventAddArg) => void>;
        eventChange: Identity<(arg: EventChangeArg) => void>;
        eventRemove: Identity<(arg: EventRemoveArg) => void>;
        windowResize: Identity<(arg: {
            view: ViewApi;
        }) => void>;
        eventClick: Identity<(arg: EventClickArg) => void>;
        eventMouseEnter: Identity<(arg: EventHoveringArg) => void>;
        eventMouseLeave: Identity<(arg: EventHoveringArg) => void>;
        select: Identity<(arg: DateSelectArg) => void>;
        unselect: Identity<(arg: DateUnselectArg) => void>;
        loading: Identity<(isLoading: boolean) => void>;
        _unmount: Identity<() => void>;
        _beforeprint: Identity<() => void>;
        _afterprint: Identity<() => void>;
        _noEventDrop: Identity<() => void>;
        _noEventResize: Identity<() => void>;
        _resize: Identity<(forced: boolean) => void>;
        _scrollRequest: Identity<(arg: any) => void>;
    };
    type BuiltInCalendarListenerRefiners = typeof CALENDAR_LISTENER_REFINERS;
    interface CalendarListenerRefiners extends BuiltInCalendarListenerRefiners { }
    type CalendarListenersLoose = RefinedOptionsFromRefiners<Required<CalendarListenerRefiners>>;
    type CalendarListeners = Required<CalendarListenersLoose>;
    const CALENDAR_OPTION_REFINERS: {
        buttonText: Identity<ButtonTextCompoundInput>;
        views: Identity<{
            [viewId: string]: ViewOptions;
        }>;
        plugins: Identity<PluginDef[]>;
        initialEvents: Identity<EventSourceInput>;
        events: Identity<EventSourceInput>;
        eventSources: Identity<EventSourceInput[]>;
    };
    type BuiltInCalendarOptionRefiners = typeof CALENDAR_OPTION_REFINERS;
    interface CalendarOptionRefiners extends BuiltInCalendarOptionRefiners { }

    type CalendarOptions = BaseOptions & CalendarListenersLoose & RawOptionsFromRefiners<Required<CalendarOptionRefiners>>;

    type CalendarOptionsRefined = BaseOptionsRefined & CalendarListenersLoose & RefinedOptionsFromRefiners<Required<CalendarOptionRefiners>>;

    const VIEW_OPTION_REFINERS: {
        type: StringConstructor;
        component: Identity<ComponentType<ViewProps>>;
        buttonText: StringConstructor;
        buttonTextKey: StringConstructor;
        dateProfileGeneratorClass: Identity<DateProfileGeneratorClass>;
        usesMinMaxTime: BooleanConstructor;
        classNames: Identity<ClassNamesGenerator<SpecificViewContentArg>>;
        content: Identity<CustomContentGenerator<SpecificViewContentArg>>;
        didMount: Identity<DidMountHandler<MountArg<SpecificViewContentArg>>>;
        willUnmount: Identity<WillUnmountHandler<MountArg<SpecificViewContentArg>>>;
    };

    type BuiltInViewOptionRefiners = typeof VIEW_OPTION_REFINERS;
    interface ViewOptionRefiners extends BuiltInViewOptionRefiners { }
    type ViewOptions = BaseOptions & CalendarListenersLoose & RawOptionsFromRefiners<Required<ViewOptionRefiners>>;
    type ViewOptionsRefined = BaseOptionsRefined & CalendarListenersLoose & RefinedOptionsFromRefiners<Required<ViewOptionRefiners>>;
    function refineProps<Refiners extends GenericRefiners, Raw extends RawOptionsFromRefiners<Refiners>>(input: Raw, refiners: Refiners): {
        refined: RefinedOptionsFromRefiners<Refiners>;
        extra: Dictionary;
    };
    type GenericRefiners = {
        [propName: string]: (input: any) => any;
    };
    type GenericListenerRefiners = {
        [listenerName: string]: Identity<(this: CalendarApi, ...args: any[]) => void>;
    };
    type RawOptionsFromRefiners<Refiners extends GenericRefiners> = {
        [Prop in keyof Refiners]?: Refiners[Prop] extends ((input: infer RawType) => infer RefinedType) ? (any extends RawType ? RefinedType : RawType) : never;
    };
    type RefinedOptionsFromRefiners<Refiners extends GenericRefiners> = {
        [Prop in keyof Refiners]?: Refiners[Prop] extends ((input: any) => infer RefinedType) ? RefinedType : never;
    };
    type DefaultedRefinedOptions<RefinedOptions extends Dictionary, DefaultKey extends keyof RefinedOptions> = Required<Pick<RefinedOptions, DefaultKey>> & Partial<Omit<RefinedOptions, DefaultKey>>;
    type Dictionary = Record<string, any>;
    type Identity<T = any> = (raw: T) => T;
    function identity<T>(raw: T): T;

    type LocaleCodeArg = string | string[];
    type LocaleSingularArg = LocaleCodeArg | LocaleInput;
    interface Locale {
        codeArg: LocaleCodeArg;
        codes: string[];
        week: {
            dow: number;
            doy: number;
        };
        simpleNumberFormat: Intl.NumberFormat;
        options: CalendarOptionsRefined;
    }

    interface LocaleInput extends CalendarOptions {
        code: string;
    }

    type LocaleInputMap = {
        [code: string]: LocaleInput;
    };

    interface RawLocaleInfo {
        map: LocaleInputMap;
        defaultCode: string;
    }

    type WeekNumberCalculation = 'local' | 'ISO' | ((m: Date) => number);
    interface DateEnvSettings {
        timeZone: string;
        namedTimeZoneImpl?: NamedTimeZoneImplClass;
        calendarSystem: string;
        locale: Locale;
        weekNumberCalculation?: WeekNumberCalculation;
        firstDay?: number;
        weekText?: string;
        cmdFormatter?: CmdFormatterFunc;
        defaultSeparator?: string;
    }
    type DateInput = Date | string | number | number[];
    interface DateMeta {
        marker: Date;
        isTimeUnspecified: boolean;
        forcedTzo: number | null;
    }
    class DateEnv {
        timeZone: string;
        namedTimeZoneImpl: NamedTimeZoneImpl;
        canComputeOffset: boolean;
        calendarSystem: CalendarSystem;
        locale: Locale;
        weekDow: number;
        weekDoy: number;
        weekNumberFunc: any;
        weekText: string;
        cmdFormatter?: CmdFormatterFunc;
        defaultSeparator: string;
        constructor(settings: DateEnvSettings);
        createMarker(input: DateInput): Date;
        createNowMarker(): Date;
        createMarkerMeta(input: DateInput): DateMeta;
        parse(s: string): {
            marker: Date;
            isTimeUnspecified: boolean;
            forcedTzo: any;
        };
        getYear(marker: Date): number;
        getMonth(marker: Date): number;
        add(marker: Date, dur: Duration): Date;
        subtract(marker: Date, dur: Duration): Date;
        addYears(marker: Date, n: number): Date;
        addMonths(marker: Date, n: number): Date;
        diffWholeYears(m0: Date, m1: Date): number;
        diffWholeMonths(m0: Date, m1: Date): number;
        greatestWholeUnit(m0: Date, m1: Date): {
            unit: string;
            value: number;
        };
        countDurationsBetween(m0: Date, m1: Date, d: Duration): number;
        startOf(m: Date, unit: string): Date;
        startOfYear(m: Date): Date;
        startOfMonth(m: Date): Date;
        startOfWeek(m: Date): Date;
        computeWeekNumber(marker: Date): number;
        format(marker: Date, formatter: DateFormatter, dateOptions?: {
            forcedTzo?: number;
        }): string;
        formatRange(start: Date, end: Date, formatter: DateFormatter, dateOptions?: {
            forcedStartTzo?: number;
            forcedEndTzo?: number;
            isEndExclusive?: boolean;
            defaultSeparator?: string;
        }): string;
        formatIso(marker: Date, extraOptions?: any): string;
        timestampToMarker(ms: number): Date;
        offsetForMarker(m: Date): number;
        toDate(m: Date, forcedTzo?: number): Date;
    }

    interface CalendarContext {
        dateEnv: DateEnv;
        options: BaseOptionsRefined;
        pluginHooks: PluginHooks;
        emitter: Emitter<CalendarListeners>;
        dispatch(action: Action): void;
        getCurrentData(): CalendarData;
        calendarApi: CalendarApi;
    }

    const EVENT_UI_REFINERS: {
        display: StringConstructor;
        editable: BooleanConstructor;
        startEditable: BooleanConstructor;
        durationEditable: BooleanConstructor;
        constraint: Identity<any>;
        overlap: Identity<boolean>;
        allow: Identity<AllowFunc>;
        className: typeof parseClassNames;
        classNames: typeof parseClassNames;
        color: StringConstructor;
        backgroundColor: StringConstructor;
        borderColor: StringConstructor;
        textColor: StringConstructor;
    };
    type BuiltInEventUiRefiners = typeof EVENT_UI_REFINERS;
    interface EventUiRefiners extends BuiltInEventUiRefiners {
    }
    type EventUiInput = RawOptionsFromRefiners<Required<EventUiRefiners>>;
    type EventUiRefined = RefinedOptionsFromRefiners<Required<EventUiRefiners>>;
    interface EventUi {
        display: string | null;
        startEditable: boolean | null;
        durationEditable: boolean | null;
        constraints: Constraint[];
        overlap: boolean | null;
        allows: AllowFunc[];
        backgroundColor: string;
        borderColor: string;
        textColor: string;
        classNames: string[];
    }
    type EventUiHash = {
        [defId: string]: EventUi;
    };
    function createEventUi(refined: EventUiRefined, context: CalendarContext): EventUi;
    function combineEventUis(uis: EventUi[]): EventUi;

    interface EventDef {
        defId: string;
        sourceId: string;
        publicId: string;
        groupId: string;
        allDay: boolean;
        hasEnd: boolean;
        recurringDef: {
            typeId: number;
            typeData: any;
            duration: Duration | null;
        } | null;
        title: string;
        url: string;
        ui: EventUi;
        extendedProps: Dictionary;
    }
    type EventDefHash = {
        [defId: string]: EventDef;
    };

    interface EventStore {
        defs: EventDefHash;
        instances: EventInstanceHash;
    }
    function eventTupleToStore(tuple: EventTuple, eventStore?: EventStore): EventStore;
    function getRelevantEvents(eventStore: EventStore, instanceId: string): EventStore;
    function createEmptyEventStore(): EventStore;
    function mergeEventStores(store0: EventStore, store1: EventStore): EventStore;
    function filterEventStoreDefs(eventStore: EventStore, filterFunc: (eventDef: EventDef) => boolean): EventStore;

    interface SplittableProps {
        businessHours: EventStore | null;
        dateSelection: DateSpan | null;
        eventStore: EventStore;
        eventUiBases: EventUiHash;
        eventSelection: string;
        eventDrag: EventInteractionState | null;
        eventResize: EventInteractionState | null;
    }

    abstract class Splitter<PropsType extends SplittableProps = SplittableProps> {
        private getKeysForEventDefs;
        private splitDateSelection;
        private splitEventStore;
        private splitIndividualUi;
        private splitEventDrag;
        private splitEventResize;
        private eventUiBuilders;
        abstract getKeyInfo(props: PropsType): {
            [key: string]: {
                ui?: EventUi;
                businessHours?: EventStore;
            };
        };
        abstract getKeysForDateSpan(dateSpan: DateSpan): string[];
        abstract getKeysForEventDef(eventDef: EventDef): string[];
        splitProps(props: PropsType): {
            [key: string]: SplittableProps;
        };
        private _splitDateSpan;
        private _getKeysForEventDefs;
        private _splitEventStore;
        private _splitIndividualUi;
        private _splitInteraction;
    }

    type ConstraintInput = 'businessHours' | string | EventInput | EventInput[];
    type Constraint = 'businessHours' | string | EventStore | false;
    type OverlapFunc = ((stillEvent: EventApi, movingEvent: EventApi | null) => boolean);
    type AllowFunc = (span: DateSpanApi, movingEvent: EventApi | null) => boolean;
    type isPropsValidTester = (props: SplittableProps, context: CalendarContext) => boolean;

    const EVENT_REFINERS: {
        extendedProps: Identity<Record<string, any>>;
        start: Identity<DateInput>;
        end: Identity<DateInput>;
        date: Identity<DateInput>;
        allDay: BooleanConstructor;
        id: StringConstructor;
        groupId: StringConstructor;
        title: StringConstructor;
        url: StringConstructor;
    };
    type BuiltInEventRefiners = typeof EVENT_REFINERS;
    interface EventRefiners extends BuiltInEventRefiners {
    }
    type EventInput = EventUiInput & RawOptionsFromRefiners<Required<EventRefiners>> &
    {
        [extendedProp: string]: any;
    };
    type EventRefined = EventUiRefined & RefinedOptionsFromRefiners<Required<EventRefiners>>;
    interface EventTuple {
        def: EventDef;
        instance: EventInstance | null;
    }
    type EventInputTransformer = (input: EventInput) => EventInput;
    type EventDefMemberAdder = (refined: EventRefined) => Partial<EventDef>;
    function refineEventDef(raw: EventInput, context: CalendarContext, refiners?: {
        extendedProps: Identity<Record<string, any>>;
        start: Identity<DateInput>;
        end: Identity<DateInput>;
        date: Identity<DateInput>;
        allDay: BooleanConstructor;
        id: StringConstructor;
        groupId: StringConstructor;
        title: StringConstructor;
        url: StringConstructor;
        display: StringConstructor;
        editable: BooleanConstructor;
        startEditable: BooleanConstructor;
        durationEditable: BooleanConstructor;
        constraint: Identity<any>;
        overlap: Identity<boolean>;
        allow: Identity<AllowFunc>;
        className: typeof parseClassNames;
        classNames: typeof parseClassNames;
        color: StringConstructor;
        backgroundColor: StringConstructor;
        borderColor: StringConstructor;
        textColor: StringConstructor;
    }): {
        refined: RefinedOptionsFromRefiners<{
            extendedProps: Identity<Record<string, any>>;
            start: Identity<DateInput>;
            end: Identity<DateInput>;
            date: Identity<DateInput>;
            allDay: BooleanConstructor;
            id: StringConstructor;
            groupId: StringConstructor;
            title: StringConstructor;
            url: StringConstructor;
            display: StringConstructor;
            editable: BooleanConstructor;
            startEditable: BooleanConstructor;
            durationEditable: BooleanConstructor;
            constraint: Identity<any>;
            overlap: Identity<boolean>;
            allow: Identity<AllowFunc>;
            className: typeof parseClassNames;
            classNames: typeof parseClassNames;
            color: StringConstructor;
            backgroundColor: StringConstructor;
            borderColor: StringConstructor;
            textColor: StringConstructor;
        }>;
        extra: Record<string, any>;
    };
    function parseEventDef(refined: EventRefined, extra: Dictionary, sourceId: string, allDay: boolean, hasEnd: boolean, context: CalendarContext): EventDef;

    type EventSourceError = {
        message: string;
        response?: any;
        [otherProp: string]: any;
    };
    type EventSourceSuccessResponseHandler = (this: CalendarApi, rawData: any, response: any) => EventInput[] | void;
    type EventSourceErrorResponseHandler = (error: EventSourceError) => void;
    interface EventSource<Meta> {
        _raw: any;
        sourceId: string;
        sourceDefId: number;
        meta: Meta;
        publicId: string;
        isFetching: boolean;
        latestFetchId: string;
        fetchRange: DateRange | null;
        defaultAllDay: boolean | null;
        eventDataTransform: EventInputTransformer;
        ui: EventUi;
        success: EventSourceSuccessResponseHandler | null;
        failure: EventSourceErrorResponseHandler | null;
        extendedProps: Dictionary;
    }
    type EventSourceHash = {
        [sourceId: string]: EventSource<any>;
    };
    type EventSourceFetcher<Meta> = (arg: {
        eventSource: EventSource<Meta>;
        range: DateRange;
        context: CalendarContext;
    }, success: (res: {
        rawEvents: EventInput[];
        xhr?: XMLHttpRequest;
    }) => void, failure: (error: EventSourceError) => void) => (void | PromiseLike<EventInput[]>);

    class EventSourceApi {
        private context;
        internalEventSource: EventSource<any>;
        constructor(context: CalendarContext, internalEventSource: EventSource<any>);
        remove(): void;
        refetch(): void;
        get id(): string;
        get url(): string;
    }

    interface FormatDateOptions extends NativeFormatterOptions {
        locale?: string;
    }
    interface FormatRangeOptions extends FormatDateOptions {
        separator?: string;
        isEndExclusive?: boolean;
    }
    function formatDate(dateInput: DateInput, options?: FormatDateOptions): string;
    function formatRange(startInput: DateInput, endInput: DateInput, options: FormatRangeOptions): string;

    function computeVisibleDayRange(timedRange: OpenDateRange, nextDayThreshold?: Duration): OpenDateRange;
    function isMultiDayRange(range: DateRange): boolean;
    function diffDates(date0: Date, date1: Date, dateEnv: DateEnv, largeUnit?: string): Duration;

    function removeExact(array: any, exactVal: any): number;
    function isArraysEqual(a0: any, a1: any, equalityFunc?: (v0: any, v1: any) => boolean): boolean;


    function memoize<Args extends any[], Res>(workerFunc: (...args: Args) => Res, resEquality?: (res0: Res, res1: Res) => boolean, teardownFunc?: (res: Res) => void): (...args: Args) => Res;
    function memoizeObjArg<Arg extends Dictionary, Res>(workerFunc: (arg: Arg) => Res, resEquality?: (res0: Res, res1: Res) => boolean, teardownFunc?: (res: Res) => void): (arg: Arg) => Res;
    function memoizeArraylike<Args extends any[], Res>(
        workerFunc: (...args: Args) => Res, resEquality?: (res0: Res, res1: Res) => boolean, teardownFunc?: (res: Res) => void): (argSets: Args[]) => Res[];
    function memoizeHashlike<Args extends any[], Res>(
        workerFunc: (...args: Args) => Res, resEquality?: (res0: Res, res1: Res) => boolean, teardownFunc?: (res: Res) => void): (argHash: {
            [key: string]: Args;
        }) => {
            [key: string]: Res;
        };

    function removeElement(el: HTMLElement): void;
    function elementClosest(el: HTMLElement, selector: string): HTMLElement;
    function elementMatches(el: HTMLElement, selector: string): HTMLElement;
    function findElements(container: HTMLElement[] | HTMLElement | NodeListOf<HTMLElement>, selector: string): HTMLElement[];
    function findDirectChildren(parent: HTMLElement[] | HTMLElement, selector?: string): HTMLElement[];
    function applyStyle(el: HTMLElement, props: Dictionary): void;
    function applyStyleProp(el: HTMLElement, name: string, val: any): void;

    function getCanVGrowWithinCell(): boolean;

    function buildNavLinkData(date: Date, type?: string): string;

    function preventDefault(ev: any): void;
    function listenBySelector(container: HTMLElement, eventType: string, selector: string, handler: (ev: Event, matchedTarget: HTMLElement) => void): () => void;
    function whenTransitionDone(el: HTMLElement, callback: (ev: Event) => void): void;

    interface EdgeInfo {
        borderLeft: number;
        borderRight: number;
        borderTop: number;
        borderBottom: number;
        scrollbarLeft: number;
        scrollbarRight: number;
        scrollbarBottom: number;
        paddingLeft?: number;
        paddingRight?: number;
        paddingTop?: number;
        paddingBottom?: number;
    }
    function computeEdges(el: HTMLElement, getPadding?: boolean): EdgeInfo;
    function computeInnerRect(el: any, goWithinPadding?: boolean, doFromWindowViewport?: boolean): {
        left: any;
        right: number;
        top: any;
        bottom: number;
    };
    function computeRect(el: any): Rect;
    function computeHeightAndMargins(el: HTMLElement): number;
    function getClippingParents(el: HTMLElement): HTMLElement[];

    function unpromisify(func: any, success: any, failure?: any): void;

    class PositionCache {
        els: HTMLElement[];
        originClientRect: ClientRect;
        lefts: any;
        rights: any;
        tops: any;
        bottoms: any;
        constructor(originEl: HTMLElement, els: HTMLElement[], isHorizontal: boolean, isVertical: boolean);
        buildElHorizontals(originClientLeft: number): void;
        buildElVerticals(originClientTop: number): void;
        leftToIndex(leftPosition: number): any;
        topToIndex(topPosition: number): any;
        getWidth(leftIndex: number): number;
        getHeight(topIndex: number): number;
    }

    abstract class ScrollController {
        abstract getScrollTop(): number;
        abstract getScrollLeft(): number;
        abstract setScrollTop(top: number): void;
        abstract setScrollLeft(left: number): void;
        abstract getClientWidth(): number;
        abstract getClientHeight(): number;
        abstract getScrollWidth(): number;
        abstract getScrollHeight(): number;
        getMaxScrollTop(): number;
        getMaxScrollLeft(): number;
        canScrollVertically(): boolean;
        canScrollHorizontally(): boolean;
        canScrollUp(): boolean;
        canScrollDown(): boolean;
        canScrollLeft(): boolean;
        canScrollRight(): boolean;
    }
    class ElementScrollController extends ScrollController {
        el: HTMLElement;
        constructor(el: HTMLElement);
        getScrollTop(): number;
        getScrollLeft(): number;
        setScrollTop(top: number): void;
        setScrollLeft(left: number): void;
        getScrollWidth(): number;
        getScrollHeight(): number;
        getClientHeight(): number;
        getClientWidth(): number;
    }
    class WindowScrollController extends ScrollController {
        getScrollTop(): number;
        getScrollLeft(): number;
        setScrollTop(n: number): void;
        setScrollLeft(n: number): void;
        getScrollWidth(): number;
        getScrollHeight(): number;
        getClientHeight(): number;
        getClientWidth(): number;
    }

    interface CalendarDataProviderProps {
        optionOverrides: any;
        calendarApi: CalendarApi;
        children?: (data: CalendarData) => ComponentChildren;
    }
    class CalendarDataProvider extends Component<CalendarDataProviderProps, CalendarData> {
        dataManager: CalendarDataManager;
        constructor(props: CalendarDataProviderProps);
        handleData: (data: CalendarData) => void;
        render(): ComponentChildren;
        componentDidUpdate(prevProps: CalendarDataProviderProps): void;
    }

    interface ViewDef {
        type: string;
        component: ViewComponentType;
        overrides: ViewOptions;
        defaults: ViewOptions;
    }

    function formatDayString(marker: Date): string;
    function formatIsoTimeString(marker: Date): string;

    function parse(str: any): {
        marker: Date;
        isTimeUnspecified: boolean;
        timeZoneOffset: any;
    };

    const config: any;

    const globalLocales: LocaleInput[];

    function createPlugin(input: PluginDefInput): PluginDef;

    interface CalendarRootProps {
        options: CalendarOptions;
        theme: Theme;
        emitter: Emitter<CalendarListeners>;
        children: (classNames: string[], height: CssDimValue, isHeightAuto: boolean, forPrint: boolean) => ComponentChildren;
    }
    interface CalendarRootState {
        forPrint: boolean;
    }
    class CalendarRoot extends BaseComponent<CalendarRootProps, CalendarRootState> {
        state: {
            forPrint: boolean;
        };
        render(): ComponentChildren;
        componentDidMount(): void;
        componentWillUnmount(): void;
        handleBeforePrint: () => void;
        handleAfterPrint: () => void;
    }

    interface DayHeaderProps {
        dateProfile: DateProfile;
        dates: Date[];
        datesRepDistinctDays: boolean;
        renderIntro?: () => VNode;
    }
    class DayHeader extends BaseComponent<DayHeaderProps> {
        createDayHeaderFormatter: (explicitFormat: DateFormatter, datesRepDistinctDays: any, dateCnt: any) => DateFormatter;
        render(): createElement.JSX.Element;
    }

    function computeFallbackHeaderFormat(datesRepDistinctDays: boolean, dayCnt: number): DateFormatter;

    interface TableDateCellProps {
        date: Date;
        dateProfile: DateProfile;
        todayRange: DateRange;
        colCnt: number;
        dayHeaderFormat: DateFormatter;
        colSpan?: number;
        isSticky?: boolean;
        extraDataAttrs?: Dictionary;
        extraHookProps?: Dictionary;
    }
    class TableDateCell extends BaseComponent<TableDateCellProps> {
        render(): createElement.JSX.Element;
    }
    interface TableDowCellProps {
        dow: number;
        dayHeaderFormat: DateFormatter;
        colSpan?: number;
        isSticky?: boolean;
        extraHookProps?: Dictionary;
        extraDataAttrs?: Dictionary;
        extraClassNames?: string[];
    }
    class TableDowCell extends BaseComponent<TableDowCellProps> {
        render(): createElement.JSX.Element;
    }

    interface DaySeriesSeg {
        firstIndex: number;
        lastIndex: number;
        isStart: boolean;
        isEnd: boolean;
    }
    class DaySeriesModel {
        cnt: number;
        dates: Date[];
        indices: number[];
        constructor(range: DateRange, dateProfileGenerator: DateProfileGenerator);
        sliceRange(range: DateRange): DaySeriesSeg | null;
        private getDateDayIndex;
    }

    interface DayTableSeg extends Seg {
        row: number;
        firstCol: number;
        lastCol: number;
    }
    interface DayTableCell {
        key: string;
        date: Date;
        extraHookProps?: Dictionary;
        extraDataAttrs?: Dictionary;
        extraClassNames?: string[];
    }
    class DayTableModel {
        rowCnt: number;
        colCnt: number;
        cells: DayTableCell[][];
        headerDates: Date[];
        private daySeries;
        constructor(daySeries: DaySeriesModel, breakOnWeeks: boolean);
        private buildCells;
        private buildCell;
        private buildHeaderDates;
        sliceRange(range: DateRange): DayTableSeg[];
    }

    interface SliceableProps {
        dateSelection: DateSpan;
        businessHours: EventStore;
        eventStore: EventStore;
        eventDrag: EventInteractionState | null;
        eventResize: EventInteractionState | null;
        eventSelection: string;
        eventUiBases: EventUiHash;
    }
    interface SlicedProps<SegType extends Seg> {
        dateSelectionSegs: SegType[];
        businessHourSegs: SegType[];
        fgEventSegs: SegType[];
        bgEventSegs: SegType[];
        eventDrag: EventSegUiInteractionState | null;
        eventResize: EventSegUiInteractionState | null;
        eventSelection: string;
    }
    abstract class Slicer<SegType extends Seg, ExtraArgs extends any[] = []> {
        private sliceBusinessHours;
        private sliceDateSelection;
        private sliceEventStore;
        private sliceEventDrag;
        private sliceEventResize;
        abstract sliceRange(dateRange: DateRange, ...extraArgs: ExtraArgs): SegType[];
        protected forceDayIfListItem: boolean;
        sliceProps(props: SliceableProps, dateProfile: DateProfile, nextDayThreshold: Duration | null, context: CalendarContext, ...extraArgs: ExtraArgs): SlicedProps<SegType>;
        sliceNowDate(
            date: Date, context: CalendarContext, ...extraArgs: ExtraArgs): SegType[];
        private _sliceBusinessHours;
        private _sliceEventStore;
        private _sliceInteraction;
        private _sliceDateSpan;
        private sliceEventRanges;
        private sliceEventRange;
    }

    function isInteractionValid(interaction: EventInteractionState, context: CalendarContext): boolean;
    function isPropsValid(state: SplittableProps, context: CalendarContext, dateSpanMeta?: {}, filterConfig?: any): boolean;

    function requestJson(method: string, url: string, params: Dictionary, successCallback: any, failureCallback: any): void;

    type OverflowValue = 'auto' | 'hidden' | 'scroll' | 'visible';
    interface ScrollerProps {
        overflowX: OverflowValue;
        overflowY: OverflowValue;
        overcomeLeft?: number;
        overcomeRight?: number;
        overcomeBottom?: number;
        maxHeight?: CssDimValue;
        liquid?: boolean;
        liquidIsAbsolute?: boolean;
        children?: ComponentChildren;
        elRef?: Ref<HTMLElement>;
    }
    class Scroller extends BaseComponent<ScrollerProps> implements ScrollerLike {
        private el;
        render(): createElement.JSX.Element;
        handleEl: (el: HTMLElement) => void;
        needsXScrolling(): boolean;
        needsYScrolling(): boolean;
        getXScrollbarWidth(): number;
        getYScrollbarWidth(): number;
    }

    class RefMap<RefType> {
        masterCallback?: (val: RefType | null, key: string) => void;
        currentMap: {
            [key: string]: RefType;
        };
        private depths;
        private callbackMap;
        constructor(masterCallback?: (val: RefType | null, key: string) => void);
        createRef(key: string | number): (val: RefType) => void;
        handleValue: (val: RefType | null, key: string) => void;
        collect(startIndex?: number, endIndex?: number, step?: number): RefType[];
        getAll(): RefType[];
    }

    interface SimpleScrollGridProps {
        cols: ColProps[];
        sections: SimpleScrollGridSection[];
        liquid: boolean;
        height?: CssDimValue;
    }
    interface SimpleScrollGridSection extends SectionConfig {
        key: string;
        chunk?: ChunkConfig;
    }
    interface SimpleScrollGridState {
        shrinkWidth: number | null;
        forceYScrollbars: boolean;
        scrollerClientWidths: {
            [index: string]: number;
        };
        scrollerClientHeights: {
            [index: string]: number;
        };
    }
    class SimpleScrollGrid extends BaseComponent<SimpleScrollGridProps, SimpleScrollGridState> {
        processCols: (a: any) => any;
        renderMicroColGroup: (cols: ColProps[], shrinkWidth?: number) => VNode;
        scrollerRefs: RefMap<Scroller>;
        scrollerElRefs: RefMap<HTMLElement>;
        state: SimpleScrollGridState;
        render(): VNode;
        renderSection(sectionConfig: SimpleScrollGridSection, sectionI: number, microColGroupNode: VNode): createElement.JSX.Element;
        renderChunkTd(sectionConfig: SimpleScrollGridSection, sectionI: number, microColGroupNode: VNode, chunkConfig: ChunkConfig): createElement.JSX.Element;
        _handleScrollerEl(scrollerEl: HTMLElement | null, key: string): void;
        handleSizing: () => void;
        componentDidMount(): void;
        componentDidUpdate(): void;
        componentWillUnmount(): void;
        computeShrinkWidth(): number;
        computeScrollerDims(): {
            forceYScrollbars: boolean;
            scrollerClientWidths: {
                [index: string]: number;
            };
            scrollerClientHeights: {
                [index: string]: number;
            };
        };
    }

    interface ScrollbarWidths {
        x: number;
        y: number;
    }
    function getScrollbarWidths(): ScrollbarWidths;

    function getIsRtlScrollbarOnLeft(): boolean;

    interface NowTimerProps {
        unit: string;
        children: (now: Date, todayRange: DateRange) => ComponentChildren;
    }
    interface NowTimerState {
        nowDate: Date;
        todayRange: DateRange;
    }
    class NowTimer extends Component<NowTimerProps, NowTimerState> {
        static contextType: Context<ViewContext>;
        context: ViewContext;
        initialNowDate: Date;
        initialNowQueriedMs: number;
        timeoutId: any;
        constructor(props: NowTimerProps, context: ViewContext);
        render(): ComponentChildren;
        componentDidMount(): void;
        componentDidUpdate(prevProps: NowTimerProps): void;
        componentWillUnmount(): void;
        private computeTiming;
        private setTimeout;
        private clearTimeout;
    }

    let globalPlugins: PluginDef[];

    interface MinimalEventProps {
        seg: Seg;
        isDragging: boolean;
        isResizing: boolean;
        isDateSelecting: boolean;
        isSelected: boolean;
        isPast: boolean;
        isFuture: boolean;
        isToday: boolean;
    }

    interface EventRootProps extends MinimalEventProps {
        timeText: string;
        disableDragging?: boolean;
        disableResizing?: boolean;
        defaultContent: (hookProps: EventContentArg) => ComponentChildren;
        children: (rootElRef: Ref<any>, classNames: string[], innerElRef: Ref<any>, innerContent: ComponentChildren, hookProps: EventContentArg) => ComponentChildren;
    }

    class EventRoot extends BaseComponent<EventRootProps> {
        elRef: RefObject<HTMLElement>;
        render(): createElement.JSX.Element;
        componentDidMount(): void;
        componentDidUpdate(prevProps: EventRootProps): void;
    }

    interface StandardEventProps extends MinimalEventProps {
        extraClassNames: string[];
        defaultTimeFormat: DateFormatter;
        defaultDisplayEventTime?: boolean;
        defaultDisplayEventEnd?: boolean;
        disableDragging?: boolean;
        disableResizing?: boolean;
        defaultContent?: (hookProps: EventContentArg) => ComponentChildren;
    }

    class StandardEvent extends BaseComponent<StandardEventProps> {
        render(): createElement.JSX.Element;
    }

    function renderFill(fillType: string): createElement.JSX.Element;

    interface BgEventProps {
        seg: Seg;
        isPast: boolean;
        isFuture: boolean;
        isToday: boolean;
    }

    const BgEvent: (props: BgEventProps) => createElement.JSX.Element;

    const version: string;
}

declare const FullCalendar: FullCalendar.FullCalendar;