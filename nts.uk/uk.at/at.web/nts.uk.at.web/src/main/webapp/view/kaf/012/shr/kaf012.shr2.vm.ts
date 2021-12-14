module nts.uk.at.view.kaf012.shr.viewmodel2 {
    import getText = nts.uk.resource.getText;
    import LeaveType = nts.uk.at.view.kaf012.shr.viewmodel1.LeaveType;
    import ReflectSetting = nts.uk.at.view.kaf012.shr.viewmodel1.ReflectSetting;
    import TimeLeaveManagement = nts.uk.at.view.kaf012.shr.viewmodel1.TimeLeaveManagement;
    import TimeLeaveRemaining = nts.uk.at.view.kaf012.shr.viewmodel1.TimeLeaveRemaining;

    const API = {
        changeSpecialFrame: "at/request/application/timeLeave/changeSpecialFrame",
        calculateTime: "at/request/application/timeLeave/calculateTime",
    };

    const template = `
    <div id="kaf012-share-component2">
        <div class="control-group table" style="margin-bottom: 13px">
            <div class="cell valign-center" style="padding-right: 3px" data-bind="ntsFormLabel: {required:true , text: $i18n('KAF012_5')}"></div>
            <div class="cell valign-center" id="leave-type-switch"
                data-bind="ntsSwitchButton: {
						name: $i18n('KAF012_5'),
						options: switchOptions,
						optionsValue: 'code',
						optionsText: 'name',
						value: leaveType,
						enable: true,
						required: true }">
			</div>
        </div>
        <div class="control-group valign-center" data-bind="if: displaySpecialLeaveFrames">
            <div style="padding-right: 0" data-bind="ntsFormLabel: {required:true , text: $i18n('KAF012_47')}"/>
            <div data-bind="ntsComboBox: {
                    name: $i18n('KAF012_47'),
                    options: specialLeaveFrames,
                    optionsValue: 'specialHdFrameNo',
                    optionsText: 'specialHdFrameName',
                    value: specialLeaveFrame,
                    columns: [{ prop: 'specialHdFrameName', length: 20 }],
                    required: true,
                    enable: !viewMode()}">  
            </div>
        </div>
        <div class="control-group table" style="margin-bottom: -3px">
            <div class="cell" style="padding-right: 3px; vertical-align: top" data-bind="ntsFormLabel: {required:true , text: $i18n('KAF012_6')}"></div>
            <div class="cell valign-center" style="display: inline-flex;">
                <div class="pull-left" >
                    <table id="kaf012-input-table">
                        <thead>
                            <tr data-bind="if: leaveType() == 6">
                                <th style="border: 0; padding-bottom: 6px" colspan="2"/>
                            </tr>
                            <tr class="bg-green">
                                <th>
                                <th data-bind="text: $i18n('KAF012_7')"/>
                            </tr>
                        </thead>
                        <tbody data-bind="foreach: applyTimeData">
                            <tr data-bind="if: display, attr: {height: !display() ? '0' : appTimeType < 4 ? '85px' : displayShowMore() ? '192px' : '464px'}">
                                <td class="bg-green" data-bind="text: appTimeTypeName"/>
                                <td style="vertical-align: baseline">
                                    <div class="control-group valign-center">
                                        <span data-bind="text: scheduledTimeLabel"/>
                                        <span data-bind="text: scheduledTimeLabel2" style="font-weight: bold;"/>
                                    </div>
                                    <div>
                                        <div data-bind="foreach: timeZones">
                                            <div data-bind="if: display">
                                                <div data-bind="if: $parent.appTimeType < 4" class="control-group valign-center">
                                                    <input class="time-input"
                                                            data-bind="ntsTimeEditor: {
                                                                            name: $parent.appTimeType < 4 ? $parent.appTimeTypeName : $vm.$i18n('KAF012_29'), 
                                                                            constraint: 'AttendanceClock', 
                                                                            value: startTime, 
                                                                            inputFormat: 'time', 
                                                                            mode: 'time',
                                                                            enable: !$parents[1].viewMode()
                                                                        }"/>
                                                    <span data-bind="text: $parent.attendLeaveLabel"/>
                                                    <span data-bind="text: $parent.lateTimeLabel"/>
                                                    <span data-bind="text: $parent.lateTime" style="font-weight: bold;"/>
                                                </div>
                                                <div data-bind="ifnot: $parent.appTimeType < 4" class="control-group valign-center">
                                                    <input class="time-input"
                                                            data-bind="ntsTimeEditor: {
                                                                            name: $parent.appTimeType < 4 ? $parent.appTimeTypeName : $vm.$i18n('KAF012_29'), 
                                                                            constraint: 'AttendanceClock', 
                                                                            value: startTime, 
                                                                            inputFormat: 'time', 
                                                                            mode: 'time',
                                                                            enable: !$parents[1].viewMode() &amp;&amp; enableInput(),
                                                                            required: startTimeRequired
                                                                        }, attr: {id: 'startTime-' + workNo}"/>
                                                    <span class="label" data-bind="text: $vm.$i18n('KAF012_30')"/>
                                                    <input class="time-input"
                                                            data-bind="ntsTimeEditor: {
                                                                            name: $parent.appTimeType < 4 ? $parent.appTimeTypeName : $vm.$i18n('KAF012_31'), 
                                                                            constraint: 'AttendanceClock', 
                                                                            value: endTime, 
                                                                            inputFormat: 'time', 
                                                                            mode: 'time',
                                                                            enable: !$parents[1].viewMode() &amp;&amp; enableInput(),
                                                                            required: endTimeRequired
                                                                        }, attr: {id: 'endTime-' + workNo}"/>
                                                    <div style="display: inline-flex;" data-bind="if: displayCombobox() &amp;&amp; appTimeType() != 1 &amp;&amp; appTimeType() != 2">
                                                        <div style="width: 80px" data-bind="ntsComboBox: {
                                                                                name: $vm.$i18n('KAF012_32'),
                                                                                options: ko.observableArray([
                                                                                    {value: 0, name: $vm.$i18n('KAF012_33')},
                                                                                    {value: 3, name: $vm.$i18n('KAF012_34')}
                                                                                ]),
                                                                                optionsValue: 'value',
                                                                                optionsText: 'name',
                                                                                value: appTimeType,
                                                                                columns: [{ prop: 'name', length: 4 }],
                                                                                required: false,
                                                                                editable: false,
                                                                                visibleItemsCount: 2,
                                                                                enable: !$parents[1].viewMode()
                                                                            }"/>
                                                    </div>
                                                    <span data-bind="text: $vm.$i18n('KAF012_33'), visible: appTimeType() == 0 &amp;&amp; !enableInput()"/><!--私用-->
                                                    <span data-bind="text: $vm.$i18n('KAF012_52'), visible: appTimeType() == 1"/><!--公用-->
                                                    <span data-bind="text: $vm.$i18n('KAF012_51'), visible: appTimeType() == 2"/><!--有償-->
                                                    <span data-bind="text: $vm.$i18n('KAF012_34'), visible: appTimeType() == 3 &amp;&amp; !enableInput()"/><!--組合-->
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <a class="hyperlink" href="" data-bind="text: $vm.$i18n('KAF012_37'), click: showMore, css: { hidden: !displayShowMore() }"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div id="cal-button-wrapper" class="pull-left">
                    <div data-bind="style: {height: leaveType() == 6 ? '33px' : '0px'}" />
                    <div>
                        <button id="time-calc-button" 
                                class="proceed caret-right" 
                                data-bind="text: $i18n('KAF012_38'), click: handleCalculate, enable: !viewMode(), style: {height: calcButtonHeight() + 'px'}"/>
                    </div>
                </div>
                <div class="pull-left">
                    <table id="kaf012-calc-table">
                        <thead>
                            <tr data-bind="if: leaveType() == 6">
                                <th style="border: 0;"/>
                                <th colspan="6" class="bg-green" data-bind="text: $i18n('KAF012_8')"/>
                            </tr>
                            <tr>
                                <th class="bg-yellow" data-bind="css: {hidden: leaveType() != 6}">
                                    <span data-bind="text: $i18n('KAF012_40')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 0 &amp;&amp; leaveType() != 6) &#124;&#124; !displaySubCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('KAF012_3') : $i18n('KAF012_8')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 1 &amp;&amp; leaveType() != 6) &#124;&#124; !displayAnnCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('KAF012_4') : $i18n('KAF012_8')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 2 &amp;&amp; leaveType() != 6) &#124;&#124; !displayChiCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('Com_ChildNurseHoliday') : $i18n('KAF012_8')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 3 &amp;&amp; leaveType() != 6) &#124;&#124; !displayCarCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('Com_CareHoliday') : $i18n('KAF012_8')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 4 &amp;&amp; leaveType() != 6) &#124;&#124; !display60hCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('Com_ExsessHoliday') : $i18n('KAF012_8')"/>
                                </th>
                                <th class="bg-green" data-bind="css: {hidden: (leaveType() != 5 &amp;&amp; leaveType() != 6) &#124;&#124; !displaySpeCol()}">
                                    <span data-bind="text: leaveType() == 6 ? $i18n('KAF012_46') : $i18n('KAF012_8')"/>
                                </th>
                            </tr>
                        </thead>
                        <tbody data-bind="foreach: applyTimeData">
                            <tr data-bind="if: display, attr: {height: !display() ? '0' : appTimeType < 4 ? '85px' : displayShowMore() ? '192px' : '464px'}">
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: $parent.leaveType() != 6}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex; padding-bottom: 5px;" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                        css: {hidden: appTimeType < 4}"/>
                                        <span style="display: block; padding-bottom: 4px;" data-bind="text: calculatedTimeDisplay"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 0 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.displaySubCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                            css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: substituteAppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; substituteAppTime() > 0)
                                                            }"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 1 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.displayAnnCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                                css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: annualAppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; annualAppTime() > 0)
                                                            }"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 2 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.displayChiCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                            css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: childCareAppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; childCareAppTime() > 0)
                                                            }"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 3 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.displayCarCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                            css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: careAppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; careAppTime() > 0)
                                                            }"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 4 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.display60hCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                            css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: super60AppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; super60AppTime > 0)
                                                            }"/>
                                    </div>
                                </td>
                                <td data-bind="style: {'vertical-align': appTimeType < 4 ? 'middle' : 'initial'}, css: {hidden: ($parent.leaveType() != 5 &amp;&amp; $parent.leaveType() != 6) &#124;&#124; !$parent.displaySpeCol()}, foreach: applyTime">
                                    <div data-bind="if: display, attr: {class: appTimeType < 4 || !display() ? '' : 'control-group'}">
                                        <span style="display: flex" data-bind="text: appTimeType == 4 ? $vm.$i18n('KAF012_33') : $vm.$i18n('KAF012_34'), 
                                                            css: {hidden: appTimeType < 4}"/>
                                        <input class="time-input"
                                                data-bind="ntsTimeEditor: {
                                                                name: inputName, 
                                                                constraint: 'AttendanceTime', 
                                                                value: specialAppTime, 
                                                                inputFormat: 'time', 
                                                                mode: 'time',
                                                                option: {defaultValue: '0:00'},
                                                                enable: !$parents[1].viewMode() &amp;&amp; ($parents[1].leaveType() != 6 &#124;&#124; calculatedTime() > 0 &#124;&#124; specialAppTime() > 0)
                                                            }"/>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    `;

    @component({
        name: 'kaf012-share-component2',
        template: template
    })
    class Kaf012Share2ViewModel extends ko.ViewModel {
        viewMode: KnockoutObservable<boolean>;
        reflectSetting: KnockoutObservable<ReflectSetting>;
        timeLeaveManagement: KnockoutObservable<TimeLeaveManagement>;
        timeLeaveRemaining: KnockoutObservable<TimeLeaveRemaining>;
        appDispInfoStartupOutput: KnockoutObservable<any>;
        application: KnockoutObservable<any>;

        switchOptions: KnockoutObservableArray<any> = ko.observableArray([]);
        leaveType: KnockoutObservable<number>;
        specialLeaveFrame: KnockoutObservable<number>;
        specialLeaveFrames: KnockoutObservableArray<any> = ko.observableArray([]);
        displaySpecialLeaveFrames: KnockoutObservable<boolean>;

        applyTimeData: KnockoutObservableArray<DataModel>;

        calcButtonHeight: KnockoutObservable<number>;
        displaySubCol: KnockoutObservable<boolean>;
        displayAnnCol: KnockoutObservable<boolean>;
        displayChiCol: KnockoutObservable<boolean>;
        displayCarCol: KnockoutObservable<boolean>;
        display60hCol: KnockoutObservable<boolean>;
        displaySpeCol: KnockoutObservable<boolean>;

        created(params: Params) {
            const vm = this;
            vm.reflectSetting = params.reflectSetting;
            vm.timeLeaveManagement = params.timeLeaveManagement;
            vm.timeLeaveRemaining = params.timeLeaveRemaining;
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.application = params.application;
            vm.leaveType = params.leaveType;
            vm.specialLeaveFrame = params.specialLeaveFrame;
            vm.viewMode = ko.computed(() => {
                return !!vm.appDispInfoStartupOutput()
                    && !!vm.appDispInfoStartupOutput().appDetailScreenInfo
                    && vm.appDispInfoStartupOutput().appDetailScreenInfo.outputMode != 1;
            });
            vm.displaySpecialLeaveFrames = ko.computed(() => {
                return vm.leaveType() == LeaveType.SPECIAL
                    || (
                        vm.leaveType() == LeaveType.COMBINATION
                        && !!vm.timeLeaveManagement()
                        && vm.timeLeaveManagement().timeSpecialLeaveMng.timeSpecialLeaveMngAtr
                        && !!vm.reflectSetting()
                        && vm.reflectSetting().condition.specialVacationTime == 1
                    );
            });
            vm.applyTimeData = params.applyTimeData;
            if (params.eventCalc)
                params.eventCalc(vm.handleCalculate.bind(vm));

            vm.calcButtonHeight = ko.computed(() => {
                let h = 100;
                if (vm.applyTimeData().filter(i => i.appTimeType < 4 && i.display()).length > 0)
                    h = h + (vm.applyTimeData().filter(i => i.display()).length - 1) * 60;
                if (vm.applyTimeData()[4].display()) {
                    if (vm.applyTimeData()[4].displayShowMore()) {
                        if (vm.applyTimeData().filter(i => i.appTimeType < 4 && i.display()).length > 0) {
                            h += 120;
                        } else {
                            h += 80;
                        }
                    } else {
                        h += 300;
                    }
                }
                return h;
            });
            vm.displaySubCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 0).length > 0;
            });
            vm.displayAnnCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 1).length > 0;
            });
            vm.displayChiCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 2).length > 0;
            });
            vm.displayCarCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 3).length > 0;
            });
            vm.display60hCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 4).length > 0;
            });
            vm.displaySpeCol = ko.computed(() => {
                return vm.switchOptions().filter(o => o.code == 5).length > 0;
            });
        }

        mounted() {
            const vm = this;
            vm.leaveType.subscribe(value => {
                vm.$errors("clear");
                //change width
                if (value < 6 ) { // small table
                    $('.left-panel').css('width', '872px');
                    $('.two-panel').css('width', '1260px');
                } else {
                    let hiddenCols = $('#kaf012-calc-table').find('th:hidden').length * 85;
                    $('.left-panel').css('width', 1305 - hiddenCols + 'px');
                    $('.two-panel').css('width', 1693 - hiddenCols + 'px');
                }
            });
            vm.timeLeaveManagement.subscribe(value => {
                if (value) {
                    if (value.timeSpecialLeaveMng) {
                        vm.specialLeaveFrames(value.timeSpecialLeaveMng.listSpecialFrame || []);
                    }
                    const checkData = (leaveType: number) => {
                        for (let i = 0; i < 5; i++) {
                            for (let j = 0; j < vm.applyTimeData()[i].applyTime.length; j++) {
                                switch(leaveType) {
                                    case LeaveType.SUBSTITUTE:
                                        if (vm.applyTimeData()[i].applyTime[j].substituteAppTime() > 0) return true;
                                        break;
                                    case LeaveType.ANNUAL:
                                        if (vm.applyTimeData()[i].applyTime[j].annualAppTime() > 0) return true;
                                        break;
                                    case LeaveType.CHILD_NURSING:
                                        if (vm.applyTimeData()[i].applyTime[j].childCareAppTime() > 0) return true;
                                        break;
                                    case LeaveType.NURSING:
                                        if (vm.applyTimeData()[i].applyTime[j].careAppTime() > 0) return true;
                                        break;
                                    case LeaveType.SUPER_60H:
                                        if (vm.applyTimeData()[i].applyTime[j].super60AppTime() > 0) return true;
                                        break;
                                    case LeaveType.SPECIAL:
                                        if (vm.applyTimeData()[i].applyTime[j].specialAppTime() > 0) return true;
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        return false;
                    };
                    const switchOptions = [
                        {
                            code: 0,
                            name: vm.$i18n('KAF012_3'),
                            display: (value.timeSubstituteLeaveMng.timeSubstituteLeaveMngAtr && !!vm.reflectSetting() && vm.reflectSetting().condition.substituteLeaveTime == 1)
                                        || vm.leaveType() == LeaveType.SUBSTITUTE
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(0))
                        },
                        {
                            code: 1,
                            name: vm.$i18n('KAF012_55'),
                            display: (value.timeAnnualLeaveMng.timeAnnualLeaveMngAtr && !!vm.reflectSetting() && vm.reflectSetting().condition.annualVacationTime == 1)
                                        || vm.leaveType() == LeaveType.ANNUAL
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(1))
                        },
                        {
                            code: 2,
                            name: vm.$i18n('Com_ChildNurseHoliday'),
                            display: (!!vm.reflectSetting() && vm.reflectSetting().condition.childNursing == 1)
                                        || vm.leaveType() == LeaveType.CHILD_NURSING
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(2))
                        },
                        {
                            code: 3,
                            name: vm.$i18n('Com_CareHoliday'),
                            display: (!!vm.reflectSetting() && vm.reflectSetting().condition.nursing == 1)
                                        || vm.leaveType() == LeaveType.NURSING
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(3))
                        },
                        {
                            code: 4,
                            name: vm.$i18n('Com_ExsessHoliday'),
                            display: (value.super60HLeaveMng.super60HLeaveMngAtr && !!vm.reflectSetting() && vm.reflectSetting().condition.superHoliday60H == 1)
                                        || vm.leaveType() == LeaveType.SUPER_60H
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(4))
                        },
                        {
                            code: 5,
                            name: vm.$i18n('KAF012_46'),
                            display: (value.timeSpecialLeaveMng.timeSpecialLeaveMngAtr && !!vm.reflectSetting() && vm.reflectSetting().condition.specialVacationTime == 1)
                                        || vm.leaveType() == LeaveType.SPECIAL
                                        || (vm.leaveType() == LeaveType.COMBINATION && checkData(5))
                        }
                    ];
                    const result = switchOptions.filter(i => i.display);
                    if (result.length > 1) {
                        result.push({
                            code: 6,
                            name: vm.$i18n('KAF012_39'),
                            display: true
                        });
                        vm.switchOptions(result);
                    } else {
                        vm.switchOptions(result);
                    }
                    if (result.length > 0 && !vm.leaveType()) vm.leaveType(vm.switchOptions()[0].code);
                    vm.leaveType.valueHasMutated();
                }
            });
            vm.specialLeaveFrame.subscribe(value => {
                vm.handleChangeSpecialLeaveFrame(value);
            });
        }

        handleChangeSpecialLeaveFrame(value: any) {
            const vm = this;
            const params = {
                specialLeaveFrameNo: value,
                timeLeaveAppDisplayInfo: {
                    appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                    timeLeaveManagement: vm.timeLeaveManagement(),
                    timeLeaveRemaining: vm.timeLeaveRemaining(),
                    reflectSetting: vm.reflectSetting()
                }
            };
            params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingStart).toISOString();
            params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd = new Date(params.timeLeaveAppDisplayInfo.timeLeaveRemaining.remainingEnd).toISOString();
            return vm.$blockui("show")
                .then(() => vm.$ajax(API.changeSpecialFrame, params))
                .done((res: any) => {
                    if (res) {
                        vm.timeLeaveRemaining(res.timeLeaveRemaining);
                    }
                }).fail((error: any) => {
                    vm.$dialog.error(error);
                }).always(() => vm.$blockui("hide"));
        }

        handleCalculate() {
            const vm = this;
            vm.$validate(['#kaf000-a-component4 .nts-input', '#kaf012-input-table .nts-input']).then((valid: boolean) => {
                if (valid) {
                    const command = {
                        timeLeaveType: vm.leaveType(),
                        appDate: new Date(vm.application().appDate()).toISOString(),
                        appDisplayInfo: {
                            appDispInfoStartupOutput: vm.appDispInfoStartupOutput(),
                            timeLeaveManagement: vm.timeLeaveManagement(),
                            timeLeaveRemaining: vm.timeLeaveRemaining(),
                            reflectSetting: vm.reflectSetting()
                        },
                        timeZones: [
                            {
                                workNo: 1,
                                startTime: vm.applyTimeData()[0].timeZones[0].startTime() || vm.applyTimeData()[0].scheduledTime(),
                                endTime: vm.applyTimeData()[1].timeZones[0].startTime() || vm.applyTimeData()[1].scheduledTime(),
                            },
                            {
                                workNo: 2,
                                startTime: vm.applyTimeData()[2].timeZones[0].startTime() || vm.applyTimeData()[2].scheduledTime(),
                                endTime: vm.applyTimeData()[3].timeZones[0].startTime() || vm.applyTimeData()[3].scheduledTime(),
                            }
                        ],
                        outingTimeZones: vm.applyTimeData()[4].display() ? vm.applyTimeData()[4].timeZones.filter(i => i.enableInput() && i.startTime() != null && i.endTime() != null)
                            .map(i => ({
                                frameNo: i.workNo,
                                outingAtr: i.appTimeType() == GoingOutReason.PRIVATE ? AppTimeType.PRIVATE : AppTimeType.UNION,
                                startTime: i.startTime(),
                                endTime: i.endTime()
                            })) : []
                    };
                    command.appDisplayInfo.timeLeaveRemaining.remainingStart = new Date(command.appDisplayInfo.timeLeaveRemaining.remainingStart).toISOString();
                    command.appDisplayInfo.timeLeaveRemaining.remainingEnd = new Date(command.appDisplayInfo.timeLeaveRemaining.remainingEnd).toISOString();
                    vm.$blockui("show").then(() => {
                        return vm.$ajax(API.calculateTime, command);
                    }).done((data: any) => {
                        vm.applyTimeData().filter(row => row.display()).forEach(row => {
                            switch (vm.leaveType()) {
                                case LeaveType.SUBSTITUTE:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].substituteAppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].substituteAppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].substituteAppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].substituteAppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].substituteAppTime(data.privateOutingTime);
                                            row.applyTime[1].substituteAppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case LeaveType.ANNUAL:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].annualAppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].annualAppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].annualAppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].annualAppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].annualAppTime(data.privateOutingTime);
                                            row.applyTime[1].annualAppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case LeaveType.CHILD_NURSING:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].childCareAppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].childCareAppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].childCareAppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].childCareAppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].childCareAppTime(data.privateOutingTime);
                                            row.applyTime[1].childCareAppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case LeaveType.NURSING:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].careAppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].careAppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].careAppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].careAppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].careAppTime(data.privateOutingTime);
                                            row.applyTime[1].careAppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case LeaveType.SUPER_60H:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].super60AppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].super60AppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].super60AppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].super60AppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].super60AppTime(data.privateOutingTime);
                                            row.applyTime[1].super60AppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case LeaveType.SPECIAL:
                                    switch (row.appTimeType) {
                                        case 0:
                                            row.applyTime[0].specialAppTime(data.timeBeforeWork1);
                                            break;
                                        case 1:
                                            row.applyTime[0].specialAppTime(data.timeAfterWork1);
                                            break;
                                        case 2:
                                            row.applyTime[0].specialAppTime(data.timeBeforeWork2);
                                            break;
                                        case 3:
                                            row.applyTime[0].specialAppTime(data.timeAfterWork2);
                                            break;
                                        case 4:
                                            row.applyTime[0].specialAppTime(data.privateOutingTime);
                                            row.applyTime[1].specialAppTime(data.unionOutingTime);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            switch (row.appTimeType) {
                                case 0:
                                    row.applyTime[0].calculatedTime(data.timeBeforeWork1);
                                    break;
                                case 1:
                                    row.applyTime[0].calculatedTime(data.timeAfterWork1);
                                    break;
                                case 2:
                                    row.applyTime[0].calculatedTime(data.timeBeforeWork2);
                                    break;
                                case 3:
                                    row.applyTime[0].calculatedTime(data.timeAfterWork2);
                                    break;
                                case 4:
                                    row.applyTime[0].calculatedTime(data.privateOutingTime);
                                    row.applyTime[1].calculatedTime(data.unionOutingTime);
                                    break;
                                default:
                                    break;
                            }
                        });
                    }).fail(error => {
                        vm.$dialog.error(error);
                    }).always(() => {
                        vm.$blockui("hide")
                    });
                }
            });
        }

    }
    
    interface Params {
        reflectSetting: KnockoutObservable<ReflectSetting>,
        timeLeaveManagement: KnockoutObservable<TimeLeaveManagement>,
        timeLeaveRemaining: KnockoutObservable<TimeLeaveRemaining>,
        leaveType: KnockoutObservable<number>,
        appDispInfoStartupOutput: KnockoutObservable<any>,
        application: KnockoutObservable<any>,
        applyTimeData: KnockoutObservableArray<DataModel>,
        specialLeaveFrame: KnockoutObservable<number>,
        eventCalc?: (a: any) => void
    }

    export enum AppTimeType {
        ATWORK = 0, /**	出勤前 */
        OFFWORK = 1, /**	退勤後 */
        ATWORK2 = 2, /**	出勤前2 */
        OFFWORK2 = 3, /**	退勤後2 */
        PRIVATE = 4, /**	私用外出 */
        UNION = 5 /**	組合外出 */
    }

    export enum GoingOutReason {
        PRIVATE = 0,        /* 私用 */
        PUBLIC = 1,         /* 公用 */
        COMPENSATION = 2,   /* 有償 */
        UNION = 3           /* 組合 */
    }
    
    export class TimeZone {
        appTimeType: KnockoutObservable<number>;
        workNo: number;
        startTime: KnockoutObservable<number>;
        endTime: KnockoutObservable<number>;
        display: KnockoutObservable<boolean>;
        displayCombobox: KnockoutObservable<boolean>;
        startTimeRequired: KnockoutObservable<boolean>;
        endTimeRequired: KnockoutObservable<boolean>;
        enableInput: KnockoutComputed<boolean>;

        constructor(appTimeType: number, workNo: number, reflectSetting?: KnockoutObservable<ReflectSetting>) {
            this.appTimeType = ko.observable(appTimeType < 4 ? appTimeType : GoingOutReason.PRIVATE);
            this.workNo = workNo;
            this.startTime = ko.observable(null);
            this.endTime = ko.observable(null);
            this.display = ko.observable(workNo < 4);
            this.displayCombobox = ko.computed(() => {
                if (!!reflectSetting
                    && !!reflectSetting()
                    && reflectSetting().destination.privateGoingOut == 0
                    && reflectSetting().destination.unionGoingOut == 1) {
                    this.appTimeType(GoingOutReason.UNION);
                } else if (!!reflectSetting
                    && !!reflectSetting()
                    && reflectSetting().destination.privateGoingOut == 1
                    && reflectSetting().destination.unionGoingOut == 0) {
                    this.appTimeType(GoingOutReason.PRIVATE);
                }
                return !!reflectSetting
                    && !!reflectSetting()
                    && reflectSetting().destination.privateGoingOut == 1
                    && reflectSetting().destination.unionGoingOut == 1;
            });

            this.startTimeRequired = ko.computed(() => {
                if (this.appTimeType() < 4) return false;
                return _.isNumber(this.endTime());
            });
            this.endTimeRequired = ko.computed(() => {
                if (this.appTimeType() < 4) return false;
                return _.isNumber(this.startTime());
            });

            this.startTime.subscribe((value) => {
                if (this.appTimeType() >= 4) {
                    this.validateTime(value, this.endTime());
                }
            });
            this.endTime.subscribe(value => {
                if (this.appTimeType() >= 4) {
                    this.validateTime(this.startTime(), value);
                }
            });

            this.enableInput = ko.computed(() => {
                if (this.appTimeType() == GoingOutReason.PUBLIC || this.appTimeType() == GoingOutReason.COMPENSATION)
                    return false;
                if (!this.displayCombobox()) {
                    if (!!reflectSetting
                        && !!reflectSetting()
                        && reflectSetting().destination.privateGoingOut == 1
                        && this.appTimeType() == GoingOutReason.UNION)
                        return false;
                    if (!!reflectSetting
                        && !!reflectSetting()
                        && reflectSetting().destination.unionGoingOut == 1
                        && this.appTimeType() == GoingOutReason.PRIVATE)
                        return false;
                }
                return true;
            });
        }

        validateTime(start: any, end: any) {
            $("#endTime-" + this.workNo).ntsError("clear");
            if (nts.uk.ntsNumber.isNumber(start) && nts.uk.ntsNumber.isNumber(end)) {
                if (end < start) {
                    setTimeout(() => {
                        if (nts.uk.ui.errors.getErrorByElement($("#endTime-" + this.workNo)).filter((e: any) => e.errorCode == "Msg_857").length == 0)
                            $("#endTime-" + this.workNo).ntsError("set", {messageId: "Msg_857"});
                    }, 100);
                }
            } else if (_.isEmpty(start) || _.isEmpty(end)) {
                $("#startTime-" + this.workNo).trigger("validate");
                $("#endTime-" + this.workNo).trigger("validate");
            }
        }
    }

    export class ApplyTime {
        appTimeType: number;
        inputName: string;
        display: KnockoutObservable<boolean>;
        calculatedTime: KnockoutObservable<number>;
        calculatedTimeDisplay: KnockoutObservable<string>;
        substituteAppTime: KnockoutObservable<number>;
        annualAppTime: KnockoutObservable<number>;
        childCareAppTime: KnockoutObservable<number>;
        careAppTime: KnockoutObservable<number>;
        super60AppTime: KnockoutObservable<number>;
        specialAppTime: KnockoutObservable<number>;

        constructor(appTimeType: number, reflectSetting?: KnockoutObservable<ReflectSetting>, timeZones?: Array<TimeZone>) {
            this.appTimeType = appTimeType;
            this.calculatedTime = ko.observable(0);
            this.calculatedTimeDisplay = ko.computed(() => {
                if (this.calculatedTime()) return nts.uk.time.format.byId("Clock_Short_HM", this.calculatedTime());
                return "0:00";
            });
            this.substituteAppTime = ko.observable(0);
            this.annualAppTime = ko.observable(0);
            this.childCareAppTime = ko.observable(0);
            this.careAppTime = ko.observable(0);
            this.super60AppTime = ko.observable(0);
            this.specialAppTime = ko.observable(0);
            switch (this.appTimeType) {
                case AppTimeType.ATWORK:
                    this.inputName = getText("KAF012_14");
                    break;
                case AppTimeType.OFFWORK:
                    this.inputName = getText("KAF012_20");
                    break;
                case AppTimeType.ATWORK2:
                    this.inputName = getText("KAF012_23");
                    break;
                case AppTimeType.OFFWORK2:
                    this.inputName = getText("KAF012_26");
                    break;
                case AppTimeType.PRIVATE:
                    this.inputName = getText("KAF012_35");
                    break;
                case AppTimeType.UNION:
                    this.inputName = getText("KAF012_36");
                    break;
                default:
                    this.inputName = "";
                    break;
            }
            this.display = ko.computed(() => {
                if (appTimeType >= 4) {
                    if (appTimeType == AppTimeType.PRIVATE)
                        return (!!reflectSetting
                            && !!reflectSetting()
                            && reflectSetting().destination.privateGoingOut == 1
                            && reflectSetting().destination.unionGoingOut != 1)
                            || timeZones.filter(i => i.appTimeType() == GoingOutReason.PRIVATE && i.enableInput()).length > 0;
                    else if (appTimeType == AppTimeType.UNION)
                        return  (!!reflectSetting
                            && !!reflectSetting()
                            && reflectSetting().destination.unionGoingOut == 1
                            && reflectSetting().destination.privateGoingOut != 1)
                            || timeZones.filter(i => i.appTimeType() == GoingOutReason.UNION && i.enableInput()).length > 0;
                    else return false;
                }
                return true;
            });
        }
    }

    export class DataModel {
        appTimeType: number;
        appTimeTypeName: string;
        scheduledTimeLabel: KnockoutObservable<string>;
        scheduledTime: KnockoutObservable<number>;
        scheduledTimeLabel2: KnockoutObservable<string>;
        attendLeaveLabel: KnockoutObservable<string>;
        lateTimeLabel: KnockoutObservable<string>;
        lateTime: KnockoutObservable<string>;
        timeZones: Array<TimeZone>;
        applyTime: Array<ApplyTime>;
        display: KnockoutObservable<boolean>;
        displayShowMore: KnockoutObservable<boolean>;

        constructor(appTimeType: number, reflectSetting: KnockoutObservable<ReflectSetting>, appDispInfoStartupOutput: KnockoutObservable<any>, application: KnockoutObservable<any>) {
            this.appTimeType = appTimeType;
            switch (appTimeType) {
                case AppTimeType.ATWORK:
                    this.appTimeTypeName = getText('KAF012_9');
                    this.timeZones = [new TimeZone(AppTimeType.ATWORK, 1)];
                    this.applyTime = [new ApplyTime(AppTimeType.ATWORK)];
                    this.display = ko.computed(() => {
                        return reflectSetting() && reflectSetting().destination.firstBeforeWork == 1;
                    });
                    this.displayShowMore = ko.observable(false);
                    break;
                case AppTimeType.OFFWORK:
                    this.appTimeTypeName = getText('KAF012_15');
                    this.timeZones = [new TimeZone(AppTimeType.OFFWORK, 1)];
                    this.applyTime = [new ApplyTime(AppTimeType.OFFWORK)];
                    this.display = ko.computed(() => {
                        return reflectSetting() && reflectSetting().destination.firstAfterWork == 1;
                    });
                    this.displayShowMore = ko.observable(false);
                    break;
                case AppTimeType.ATWORK2:
                    this.appTimeTypeName = getText('KAF012_21');
                    this.timeZones = [new TimeZone(AppTimeType.ATWORK2, 2)];
                    this.applyTime = [new ApplyTime(AppTimeType.ATWORK2)];
                    this.display = ko.computed(() => {
                        return appDispInfoStartupOutput()
                            && appDispInfoStartupOutput().appDispInfoNoDateOutput.managementMultipleWorkCycles
                            && reflectSetting()
                            && reflectSetting().destination.secondBeforeWork == 1;
                    });
                    this.displayShowMore = ko.observable(false);
                    break;
                case AppTimeType.OFFWORK2:
                    this.appTimeTypeName = getText('KAF012_24');
                    this.timeZones = [new TimeZone(AppTimeType.OFFWORK2, 2)];
                    this.applyTime = [new ApplyTime(AppTimeType.OFFWORK2)];
                    this.display = ko.computed(() => {
                        return appDispInfoStartupOutput()
                            && appDispInfoStartupOutput().appDispInfoNoDateOutput.managementMultipleWorkCycles
                            && reflectSetting()
                            && reflectSetting().destination.secondAfterWork == 1;
                    });
                    this.displayShowMore = ko.observable(false);
                    break;
                case 4: // PRIVATE and UNION
                    this.appTimeTypeName = getText('KAF012_27');
                    this.timeZones = [];
                    for (let i = 1; i <= 10; i++) {
                        this.timeZones.push(new TimeZone(AppTimeType.PRIVATE, i, reflectSetting));
                    }
                    this.applyTime = [
                        new ApplyTime(AppTimeType.PRIVATE, reflectSetting, this.timeZones),
                        new ApplyTime(AppTimeType.UNION, reflectSetting, this.timeZones)
                    ];
                    this.display = ko.computed(() => {
                        return reflectSetting() && reflectSetting().destination.privateGoingOut == 1
                            || reflectSetting() && reflectSetting().destination.unionGoingOut == 1;
                    });
                    this.displayShowMore = ko.observable(true);
                    break;
                default:
                    break;
            }
            this.scheduledTime = ko.computed(() => {
                let value = null;
                if (appDispInfoStartupOutput
                    && appDispInfoStartupOutput()
                    && !_.isEmpty(appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst)
                    && appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail) {
                    switch (appTimeType) {
                        case AppTimeType.ATWORK:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime1;
                            break;
                        case AppTimeType.OFFWORK:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime1;
                            break;
                        case AppTimeType.ATWORK2:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheAttendanceTime2;
                            break;
                        case AppTimeType.OFFWORK2:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.achievementEarly.scheDepartureTime2;
                            break;
                        default:
                            break;
                    }
                }
                return value;
            });
            this.scheduledTimeLabel = ko.computed(() => {
                switch (appTimeType) {
                    case AppTimeType.ATWORK:
                    case AppTimeType.ATWORK2:
                        return getText("KAF012_10");
                    case AppTimeType.OFFWORK:
                    case AppTimeType.OFFWORK2:
                        return getText("KAF012_16");
                    case 4:
                        return getText('KAF012_28');
                    default:
                        return "";
                }
            });
            this.scheduledTimeLabel2 = ko.computed(() => {
                return this.scheduledTime() ? nts.uk.time.format.byId("Clock_Short_HM", this.scheduledTime()) : ""
            });
            this.attendLeaveLabel = ko.computed(() => {
                switch (appTimeType) {
                    case AppTimeType.ATWORK:
                    case AppTimeType.ATWORK2:
                        if (application && application().prePostAtr() == 0)
                            return getText("KAF012_11");
                        if (application && application().prePostAtr() == 1)
                            return getText("KAF012_12");
                        return "";
                    case AppTimeType.OFFWORK:
                    case AppTimeType.OFFWORK2:
                        if (application && application().prePostAtr() == 0)
                            return getText("KAF012_17");
                        if (application && application().prePostAtr() == 1)
                            return getText("KAF012_18");
                        return "";
                    default:
                        return "";
                }
            });
            this.lateTimeLabel = ko.computed(() => {
                if (application && application().prePostAtr() == 1
                    && appDispInfoStartupOutput
                    && appDispInfoStartupOutput()
                    && !_.isEmpty(appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst)
                    && appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail) {
                    switch (appTimeType) {
                        case AppTimeType.ATWORK:
                            return appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.lateTime > 0 ? getText("KAF012_13") : "";
                        case AppTimeType.OFFWORK:
                            return appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.earlyLeaveTime > 0 ? getText("KAF012_19") : "";
                        case AppTimeType.ATWORK2:
                            return appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.lateTime2 > 0 ? getText("KAF012_22") : "";
                        case AppTimeType.OFFWORK2:
                            return appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.earlyLeaveTime2 > 0 ? getText("KAF012_25") : "";
                        default:
                            break;
                    }
                }
                return "";
            });
            this.lateTime = ko.computed(() => {
                if (application && application().prePostAtr() == 1
                    && appDispInfoStartupOutput
                    && appDispInfoStartupOutput()
                    && !_.isEmpty(appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst)
                    && appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail) {
                    let value = null;
                    switch (appTimeType) {
                        case AppTimeType.ATWORK:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.lateTime;
                            break;
                        case AppTimeType.OFFWORK:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.earlyLeaveTime;
                            break;
                        case AppTimeType.ATWORK2:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.lateTime2;
                            break;
                        case AppTimeType.OFFWORK2:
                            value = appDispInfoStartupOutput().appDispInfoWithDateOutput.opActualContentDisplayLst[0].opAchievementDetail.timeContentOutput.earlyLeaveTime2;
                            break;
                        default:
                            break;
                    }
                    return value > 0 ? nts.uk.time.format.byId("Clock_Short_HM", value) : "";
                }
                return "";
            });
        }

        showMore() {
            const vm = this;
            vm.timeZones.forEach(i => {
                i.display(true);
            });
            vm.displayShowMore(false);
        }
    }
}