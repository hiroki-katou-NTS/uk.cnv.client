/// <reference path="../reference.ts"/>

module nts.uk.ui {
    export interface WindowSize {
        width: number;
        height: number;
    }

    export interface RootViewModel {
        kiban: KibanViewModel;
        content: nts.uk.ui.vm.ViewModel;
        errors: {
            isEmpty: KnockoutComputed<boolean>;
        };
    }

    /** Event to notify document ready to initialize UI. */
    export const documentReady = $.Callbacks();

    /** Event to notify ViewModel built to bind. */
    export const viewModelBuilt = $.Callbacks();

    /** Event to notify ViewModel applied bindings. */
    export const viewModelApplied = $.Callbacks();

    // Kiban ViewModel
    class KibanViewModel {
        // deprecate
        title!: KnockoutComputed<string>;

        systemName: KnockoutObservable<string> = ko.observable("").extend({ rateLimit: 500 });
        programName: KnockoutObservable<string> = ko.observable("").extend({ rateLimit: 500 });

        // error model
        errorDialogViewModel!: errors.ErrorsViewModel;

        // set page as view or modal
        mode: KnockoutObservable<'view' | 'modal'> = ko.observable(undefined).extend({ rateLimit: 500 });

        // subscriber windows size
        size: KnockoutObservable<WindowSize> = ko.observable({ width: window.innerWidth, height: window.innerHeight }).extend({ rateLimit: 500 });

        // show or hide header
        header: KnockoutObservable<boolean | null> = ko.observable(null).extend({ rateLimit: 500 });

        // show or hide notification
        notification: KnockoutObservable<string> = ko.observable('').extend({ rateLimit: 500 });

        constructor() {
            const vm = this;

            vm.title = ko.computed({
                read: () => {
                    document.title = ko.unwrap(vm.systemName);

                    return document.title;
                },
                write: (value: string) => {
                    vm.systemName(value);
                }
            });

            vm.mode
                .subscribe((m) => {
                    if (m === 'view') {
                        $('body>div:first-child').addClass('view');
                        $('body>div:first-child').removeClass('modal');
                    } else {
                        $('body>div:first-child').addClass('modal');
                        $('body>div:first-child').removeClass('view');
                    }
                });

            vm.errorDialogViewModel = new errors.ErrorsViewModel();
        }

        public initErrorModel(dialogOptions?: any) {
            const vm = this;

            vm.errorDialogViewModel.initErrorModel(dialogOptions);
        }
    }

    export const _viewModel: RootViewModel | null = null; //{ kiban: null, content: null, errors: null };

    export module init {
        let _start: () => void;

        _.extend(__viewContext, {
            transferred: uk.sessionStorage
                .getItem(uk.request.STORAGE_KEY_TRANSFER_DATA)
                .map(v => JSON.parse(v)),
            ready: (callback: () => void) => _start = callback,
            bind: function (content: any, dialogOptions?: any) {
                const kiban = new KibanViewModel();
                const { systemName } = __viewContext.env;

                // update title of name
                kiban.systemName(systemName);

                // update mode of view
                kiban.mode(!util.isInFrame() ? 'view' : 'modal');

                kiban.initErrorModel(dialogOptions);

                const isEmpty = ko.computed(() => !kiban.errorDialogViewModel.occurs());

                // mock ready function
                _.extend(nts.uk.ui, { _viewModel: { kiban, content, errors: { isEmpty } } });

                $(() => {
                    viewModelBuilt.fire(_viewModel);

                    // bind viewmodel to document body
                    ko.applyBindings(_viewModel, document.body);

                    viewModelApplied.fire(_viewModel);

                    // update header
                    if (kiban.header() === null) {
                        kiban.header(!__viewContext.noHeader || (kiban.mode() === 'view'));
                    }
                    
                    // update notification
                    if (__viewContext.program.operationSetting != undefined) {
                        kiban.notification(__viewContext.program.operationSetting.message);
                    }

                    // off event reset for class reset-not-apply
                    $(".reset-not-apply").find(".reset-element").off("reset");

                    nts.uk.cookie.remove("startfrommenu", { path: "/" });
                });

                // update size
                $(window)
                    .on('wd.resize', () => {
                        kiban.size({
                            width: window.innerWidth,
                            height: window.innerHeight
                        });
                    })
                    .on('resize', () => $(window).trigger('wd.resize'));
            }
        });

        const startP = function () {
            setTimeout(() => {
                if (!cantCall()) {
                    _start.apply(__viewContext, [__viewContext]);
                } else {
                    loadEmployeeCodeConstraints()
                        .always(() => _start.apply(__viewContext, [__viewContext]));
                }
            }, 1);
        };

        const noSessionWebScreens = [
            "/view/sample/",
            "/view/common/error/",
            "/view/spr/index.xhtml",
            "/view/ccg/007/",
            "/view/kdw/003/a/index.xhtml",
            "/view/ccg/033/index.xhtml",
            "/view/kdp/003/a/index.xhtml",
            "/view/kdp/003/f/index.xhtml",
            "/view/kdp/004/a/index.xhtml",
            "/view/kdp/005/a/index.xhtml",
            "/view/sample/component/editor/text-editor.xhtml"
        ];

        const cantCall = function () {
            const { location } = request;
            const { current } = location;
            const { rawUrl } = current;

            return !_.some(noSessionWebScreens, (w: string) => rawUrl.indexOf(w) > -1);
        };

        const loadEmployeeCodeConstraints = function () {
            const getEmployeeSetting = function () {
                const EMP_SESSION = 'nts.uk.session.EMPLOYEE_SETTING';

                let dfd = $.Deferred(),
                    es = nts.uk.sessionStorage.getItem(EMP_SESSION);

                if (es.isPresent()) {
                    dfd.resolve(JSON.parse(es.get()));
                } else {
                    request
                        .ajax("com", "/bs/employee/setting/code/find")
                        .done((constraints: any) => {
                            nts.uk.sessionStorage.setItemAsJson(EMP_SESSION, constraints);

                            dfd.resolve(constraints);
                        });
                }

                return dfd.promise();
            };

            return getEmployeeSetting()
                .done((res: any) => {
                    const { ceMethodAttr, numberOfDigits } = res;
                    const { primitiveValueConstraints } = __viewContext;

                    const formatOption: any = {
                        autofill: true,
                        filldirection: '',
                        fillcharacter: ''
                    };

                    if (ceMethodAttr === 0) {
                        formatOption.filldirection = "left";
                        formatOption.fillcharacter = "0";
                    } else if (ceMethodAttr === 1) {
                        formatOption.filldirection = "right";
                        formatOption.fillcharacter = "0";
                    } else if (ceMethodAttr === 2) {
                        formatOption.filldirection = "left";
                        formatOption.fillcharacter = " ";
                    } else {
                        formatOption.filldirection = "right";
                        formatOption.fillcharacter = " ";
                    }

                    // if not have primitive, create new
                    if (!primitiveValueConstraints) {
                        _.extend(__viewContext, {
                            primitiveValueConstraints: {
                                EmployeeCode: {
                                    valueType: "String",
                                    charType: "AlphaNumeric",
                                    maxLength: numberOfDigits,
                                    formatOption
                                }
                            }
                        });
                    } else {
                        // extend primitive constraint
                        _.extend(primitiveValueConstraints, {
                            EmployeeCode: {
                                valueType: "String",
                                charType: "AlphaNumeric",
                                maxLength: numberOfDigits,
                                formatOption
                            }
                        });
                    }
                });
        };

        $(function () {
            documentReady.fire();

            if ($(".html-loading").length <= 0) {
                startP();
                return;
            }

            let dfd: JQueryDeferred<any>[] = [];

            _.forEach($(".html-loading").toArray(), (e: HTMLElement) => {
                let $container = $(e);
                let dX = $.Deferred();

                $container
                    .load($container.attr("link"), () => dX.resolve());

                dfd.push(dX);
                dX.promise();
            });

            $.when(...dfd)
                .then(function (data, textStatus, jqXHR) {
                    $('.html-loading').contents().unwrap();
                    startP();
                });
        });


        $(function () {
            let lastPause: number = Date.now();

            $(window)
                .keydown((e: JQueryEventObject) => {
                    if (e.keyCode === 19) {
                        const now: number = Date.now();

                        if (now - lastPause < 500) {
                            ui.dialog.version();
                        }

                        lastPause = now;
                    }
                });
        });
    }
}
