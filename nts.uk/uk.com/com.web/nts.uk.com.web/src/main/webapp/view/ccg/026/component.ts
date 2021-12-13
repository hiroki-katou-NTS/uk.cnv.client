module nts.uk.com.view.ccg026.component {
    import ajax = nts.uk.request.ajax;
    import text = nts.uk.resource.getText;

    const fetch = {
        com_mngr: (roleId: string) => ajax('com', 'sys/auth/workplace/get-list', roleId),
        person_info: (roleId: string) => ajax('com', 'ctx/pereg/functions/auth/find-with-role', roleId)
    };

    ko.components.register('ccg026-component', {
        template: `
        <!-- ko let: { text: nts.uk.resource.getText } -->
        <!-- ko if: ko.toJS(label.text) -->
        <div data-bind="ntsFormLabel: {
                required: label.require,
                text: text(ko.toJS(label.text))
            },
            style: {
                'margin-bottom': '10px'
            }"></div>
        <!-- /ko -->
        <style type="text/css">
            [id^="permision_grid"] td,
            [id^="permision_grid"][id$="_headers"] th {
                border-top: 0;
                border-right: 0;
                line-height: 24px;
            }

            [id^="permision_grid"] td:first-child,
            [id^="permision_grid"][id$="_headers"] th:first-child {
                border-left: 0;
            }
            
            [id^="permision_grid"] tr:last-child td {
                border-bottom: 0;
            }
    
            [id^="permision_grid"] td .ntsCheckBox {
                display: block;
                text-align: center;
            }

            [id^="container_permision_grid"] {
                border: 1px solid #ccc;
            }

            [id^="permision_grid"] .ntsCheckBox-label {
                outline: none;
                display: inline;
            }

            [id^="permision_grid"] .ntsCheckBox-label:focus span.box {
                border-color: #0096f2;
                box-shadow: 0 0 1px 1px #0096f2;
            }

            [id^="permision_grid"] td[role="gridcell"] {
                white-space: nowrap;
            }

            [id^="container_permision_grid"] .ui-tooltip {
                border: none;
                background: none;
                box-shadow: none;
                width: auto !important;
                max-width: 800px !important;
            }

            [id^="container_permision_grid"] .ui-tooltip .ui-tooltip-content {
                white-space: nowrap;
                background: #ffc;
                border: solid 1px #b85;
                padding: 5px;
                width: auto !important;
                max-width: 800px !important;
            }
        </style>
        <div id="container_permision_grid">
            <table id='permision_grid'></table>
        </div>
        <!-- /ko -->
    `,
        viewModel: {
            createViewModel: (params: IParam, componentInfo: IComponentInfo) => {
                let defaultGridId = 'permision_grid',
                    defaultContainerId = 'container_permision_grid',
                    compId = componentInfo.element.id,
                    finalGridId = _.isEmpty(compId) ? defaultGridId : defaultGridId + '_' + compId,
                    $element = $(componentInfo.element),
                    $grid = $element.find('#' + defaultGridId),
                    $container = $element.find('#' + defaultContainerId);
                if (!_.isEmpty(compId)) {
                    $grid.attr('id', finalGridId);
                    $container.attr('id', defaultContainerId + '_' + compId);
                }
                let requestDone = (data: Array<IPermision>, rechange: CHANGED = CHANGED.LOAD_AND_CHANGE) => {
                        //Get
                        let scrollTop = $(`#${$grid.attr('id')}_scroll`).scrollTop();

                        data = _.orderBy(data, ['orderNumber']);

                        // fire changeDate for first action
                        if (rechange == CHANGED.CHANGE) {
                            params.changeData(data);
                        } else if (rechange == CHANGED.LOAD) {
                            // change grid dataSource
                            $grid = $element.find('#' + finalGridId);
                            $grid.igGrid("option", "dataSource", ko.toJS(data));

                            setTimeout(() => {
                                $(`#${$grid.attr('id')}_scroll`).scrollTop(scrollTop);
                            }, 0);
                        } else {
                            params.changeData(data);
                            // change grid dataSource
                            $grid = $element.find('#' + finalGridId);
                            $grid.igGrid("option", "dataSource", ko.toJS(data));
                        }
                    };

                _.extend(params, {
                    label: {
                        text: ((params || { label: { text: '' } }).label || { text: '' }).text,
                        require: ((params || { label: { require: false } }).label || { require: false }).require
                    }
                });

                // extend roleId & roleType
                if (params.roleId && params.roleType) {
                    if (!ko.isObservable(params.roleId)) {
                        _.extend(params, {
                            roleId: ko.observable(params.roleId)
                        });
                    }

                    if (!ko.isObservable(params.roleType)) {
                        _.extend(params, {
                            roleType: ko.observable(params.roleType)
                        });
                    }
                } else {
                    _.extend(params, {
                        roleId: ko.observable(''),
                        roleType: ko.observable('')
                    });
                }

                params.roleId.subscribe(v => {
                    params.roleType.valueHasMutated();
                });

                // fetch data when subscribe
                params.roleType.subscribe(v => {
                    let roleId = ko.toJS(params.roleId),
                        oldData = $element.data('OLD_DATA') || { roleId: undefined, roleType: undefined },
                        compare = {
                            roleId: roleId,
                            roleType: v
                        };

                    if (!_.isEmpty(roleId) && !_.isEqual(oldData, compare) && !ko.isObservable(params.changeData)) {
                        switch (v) {
                            case ROLE_TYPE.COMPANY_MANAGER:
                                fetch.com_mngr(roleId).done(data => {
                                    requestDone(data, CHANGED.LOAD_AND_CHANGE);
                                });
                                break;
                            case ROLE_TYPE.PERSONAL_INFO:
                                fetch.person_info(roleId).done(data => {
                                    requestDone(data, CHANGED.LOAD_AND_CHANGE);
                                });
                                break;
                            default:
                                requestDone([], CHANGED.LOAD_AND_CHANGE);
                                break;
                        }
                    }

                    $element.data('OLD_DATA', compare);
                });
                params.roleType.valueHasMutated();

                $grid
                    .igGrid({
                        width: "100%",
                        height: `${(ko.toJS(params.maxRow) || 10) * 32 + 27}px`,
                        primaryKey: "functionNo",
                        enableHoverStyles: false,
                        features: [
                            {
                                name: "Tooltips"
                            }
                        ],
                        columns: [
                            { headerText: 'コード', key: 'functionNo', hidden: true },
                            { headerText: text('CCG026_2'), key: 'functionName', width: "240px" },
                            {
                                headerText: text('CCG026_3'), key: 'available', width: "70px",
                                template: '<div class="ntsControl ntsCheckBox">\
                                            <label class="ntsCheckBox-label">\
                                                <input type="checkbox" value="${functionNo}">\
                                                <span class="box"></span>\
                                            </label>\
                                       </div>'
                            },
                            { headerText: text('CCG026_4'), key: 'description' }
                        ],
                        dataRendered: (evt, ui) => {
                            // remove all tabindex of control
                            $container.find('[tabindex=0]').removeAttr('tabindex');

                            // set tabindex for checkbox
                            $container.find('label.ntsCheckBox-label')
                                .attr('tabindex', params.tabindex || 0)
                                .on('keypress', (evt) => {
                                    if ([13, 32].indexOf(evt.keyCode) > -1) {
                                        $(evt.target).find('input[type="checkbox"]').trigger('click');
                                        return false;
                                    }
                                });

                            // check and bind change event to checkbox
                            $container.find('input[type="checkbox"]')
                                .each((i, input: HTMLInputElement) => {
                                    $grid = $element.find('#' + finalGridId);

                                    let data = $grid.igGrid("option", "dataSource"),
                                        row = _.find(data, r => _.isEqual(Number(r['functionNo']), Number(input.value)));

                                    if (row && row['available']) {
                                        input.checked = true;
                                    }

                                    input.disabled = row && row['disabled'] == true ? true : false;

                                    $(input)
                                        .on('change', (evt) => {
                                            // change
                                            row['available'] = !!input.checked;

                                            $element.data('nts_focus', input.value);

                                            // push data to viewModel
                                            requestDone(data, CHANGED.CHANGE);
                                        });
                                });
                        },
                        dataSource: []
                    });

                if (ko.isObservable(params.changeData)) {
                    (params.changeData as KnockoutObservableArray<any>).subscribe(data => {// change grid dataSource
                        $grid = $element.find('#' + finalGridId);
                        if (!_.isEqual($grid.igGrid("option", "dataSource"), data)) {
                            requestDone(data, CHANGED.LOAD);
                        }
                        // set weight of grid
                        $grid.igGrid("option", "height", `${_.min([ko.toJS(params.maxRow) || 0, _.size(data)]) * 32 + 27}px`);
                    });
                }

                return params;
            }
        }
    });

    export enum CHANGED {
        LOAD = 0,
        CHANGE = 1,
        LOAD_AND_CHANGE = 2
    }

    export interface IPermision {
        available: boolean;
        description: string;
        functionName: string;
        functionNo: number;
        orderNumber: number;
        disabled: boolean;
    }

    interface IComponentInfo {
        element: HTMLElement,
        templateNodes: Array<Node>
    }

    export interface IParam {
        label?: ILabel;
        tabindex?: number | string;
        roleId: KnockoutObservable<string>;
        roleType: KnockoutObservable<ROLE_TYPE>;
        changeData?: (data?: any) => void;
        maxRow?: KnockoutObservable<number> | number;
    }

    export interface ILabel {
        text: string;
        require?: boolean;
    }

    export enum ROLE_TYPE {
        /** The system manager. */
        // システム管理者
        SYSTEM_MANAGER = 0,
        /** The company manager. */
        // 会社管理者
        COMPANY_MANAGER = 1,
        /** The group comapny manager. */
        // グループ会社管理者
        GROUP_COMAPNY_MANAGER = 2,
        /** The employment. */
        // 就業
        EMPLOYMENT = 3,
        /** The salary. */
        // 給与
        SALARY = 4,
        /** The human resource. */
        // 人事
        HUMAN_RESOURCE = 5,
        /** The office helper. */
        // オフィスヘルパー
        OFFICE_HELPER = 6,
        /** The my number. */
        // マイナンバー
        MY_NUMBER = 7,
        /** The personal info. */
        // 個人情報
        PERSONAL_INFO = 8
    }
}