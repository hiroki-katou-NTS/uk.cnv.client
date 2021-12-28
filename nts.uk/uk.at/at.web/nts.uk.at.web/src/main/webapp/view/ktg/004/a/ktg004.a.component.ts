module nts.uk.ui.ktg004.a {
    export const STORAGE_KEY_TRANSFER_DATA = "nts.uk.request.STORAGE_KEY_TRANSFER_DATA";

    const KTG004_API = {
        GET_DATA: 'screen/at/ktg004/getData',
        getWidgetInitialDisplayMonth: "screen/at/OptionalWidget/getWidgetInitialDisplayMonth",
        getAttendanceInfor: "screen/at/ktg004/getAttendanceInfor"
    };

    const DIALOGS = {
        KTG004B: '/view/ktg/004/b/index.xhtml'
    };

    const WINDOWS = {
        KTG003A: '/view/kdw/003/a/index.xhtml'
    };

    const convertToTime = (data: string | number): string => {
        if (['0', 0, null, undefined, ''].indexOf(data) > -1) {
            return '00:00';
        } else {
            const numb = Number(data);
            const negative = numb < 0;
            const hour = numb < 0 ? Math.floor((numb < 0 ? numb * -1: numb) / 60)
									: Math.floor(numb / 60);
            const minute = Math.floor(numb % 60);

            return `${negative ? '-' : ''}${Math.abs(hour)}:${_.padStart(Math.abs(minute) + '', 2, '0')}`;
        }
    }

    @component({
        name: 'ktg-004-a',
        template: `
            <div class="widget-title">
                <table style="width: 100%;">
                    <colgroup>
                        <col width="auto" />
                        <col width="41px" />
                    </colgroup>
                    <thead>
                        <!-- A1_1 -->
                        <th class="ktg004-fontsize-larger">
                            <div data-bind="ntsFormLabel: { required: false, text: $component.name }"></div>
                        </th>
                        <!-- A1_2 -->
                        <th data-bind="if: $component.detailedWorkStatusSettings">
                            <button class="icon ktg004-no-border" data-bind="click: $component.setting">
                                <i data-bind="ntsIcon: { no: 5, width: 25, height: 25 }"></i>
                            </button>
                        </th>
                    </thead>
                </table>
            </div>
            <div class="ktg-004-a ktg004-fontsize ktg004-border" data-bind="widget-content: 100">
                <div style="padding: 0px 40px 0px 30px;">
                    <table class="widget-table" style="width: 100%;">
                        <colgroup>
                            <col width="auto" />
                            <col width="auto" />
                        </colgroup>
                        <tbody data-bind="if: isDisplayA15_1">
                          <tr>
                            <td style="text-align: center;">
                              <div class="flex valign-center ktg004-header-row" style="justify-content: space-between;">
                                <div class="flex valign-center" style="flex: 2;">
                                  <div data-bind="if: !isUpdating()">
                                    <div class="label ktg004-header-text" data-bind="text: textA15_1"></div>
                                  </div>
                                  <div data-bind="if: isUpdating" class="on-update">
                                    <div class="label ktg004-header-text" data-bind="text: textA15_2"></div>
                                  </div>
                                </div>
                                <div class="flex valign-center" style="flex: 3;">
                                    <div class="label ktg004-header-text" data-bind="text: periodText"></div>
                                </div>
                                <div data-bind="ntsSwitchButton: {
                                    name: '#[KTG004_32]',
                                    options: displayYearMonths,
                                    optionsValue: 'value',
                                    optionsText: 'name',
                                    value: selectedDisplayYearMonth
                                }"
                                style="flex: 2;"></div>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                        <tbody data-bind="foreach: { data: $component.getGroup(1), as: 'row' }">
                            <tr class="row-show-ktg004">
								<td>
									<div style="display: flex">
		                                <div style="position: relative; width: 50%">
		                                    <div data-bind="if: row.btn" style="float: left; position: relative;">
		                                        <!-- A2_2 -->
		                                        <button class="icon ktg004-no-border" data-bind="
		                                            click: function() { $component.openKDW003() },
		                                            ntsIcon: { no: 201, width: 25, height: 28 },
		                                            enable: row.canClick">
		                                        </button>
		                                        <!-- A2_3 -->
		                                        <i style="position: absolute; left: 13px; bottom: 0px; cursor: pointer;"
		                                            data-bind="visible: row.canClick, ntsIcon: { no: 165, width: 13, height: 13 }, click: function() { $component.openKDW003() }">
		                                        </i>
		                                    </div>
		                                    <div data-bind="ntsFormLabel: { required: false, text: row.name }"></div>
		                                </div>
										<div style="width: 50%; margin-top: 5px ">
			                                <div class="text-right" data-bind="i18n: row.text"></div>										
										</div>
									</div>
									<div data-bind="if: row.name == 'KTG004_9'">
										<div data-bind="ntsFormLabel: { required: false, text: row.grantDay }"></div>									
									</div>
								</td>
                            </tr>
                        </tbody>
                        <tbody data-bind="if: isDisplayA17_1">
                          <tr>
                            <td>
                              <div class="valign-center ktg004-header-row">
                                <label class="ktg004-header-text" data-bind="text: textA17_1"></label>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                        <tbody data-bind="foreach: { data: $component.getGroup(2), as: 'row' }">
                            <tr class="row-show-ktg004">
								<td>
									<div style="display: flex">
		                                <div style="position: relative; width: 50%">
		                                    <div data-bind="if: row.btn" style="float: left; position: relative;">
		                                        <!-- A2_2 -->
		                                        <button class="icon ktg004-no-border" data-bind="
		                                            click: function() { $component.openKDW003() },
		                                            ntsIcon: { no: 201, width: 25, height: 28 },
		                                            enable: row.canClick">
		                                        </button>
		                                        <!-- A2_3 -->
		                                        <i style="position: absolute; left: 13px; bottom: 0px; cursor: pointer;"
		                                            data-bind="visible: row.canClick, ntsIcon: { no: 165, width: 13, height: 13 }, click: function() { $component.openKDW003() }">
		                                        </i>
		                                    </div>
		                                    <div data-bind="ntsFormLabel: { required: false, text: row.name }"></div>
		                                </div>
										<div style="width: 50%; margin-top: 5px ">
			                                <div class="text-right" data-bind="i18n: row.text"></div>										
										</div>
									</div>
									<div data-bind="if: row.name == 'KTG004_9'">
										<div data-bind="ntsFormLabel: { required: false, text: row.grantDay }"></div>									
									</div>
								</td>
                            </tr>
                        </tbody>
                        <tbody data-bind="foreach: { data: $component.specialHolidaysRemainings, as: 'row'}"> 
                            <tr class="row-show-ktg004">
                                <td>
									<div style="display: flex">
	                                    <div style="position: relative; width: 50%" data-bind=" ntsFormLabel: { required: false, text: row.name }"></div>
		                                <div style="width: 50%; margin-top: 5px " class="text-right" data-bind="i18n: row.specialResidualNumber"></div>									
									</div>
                                </td>
                            </tr>
                        </tbody>
                    </table>	
                </div>
            </div>
            <style rel="stylesheet">
                .ktg-004-a table tr {
                    height: 30px !important;
                }
                .ktg-004-a .text-right {
                    text-align: right;
                }
                .text-right span {
                    color: black;
                }
                .ktg004-fontsize div.form-label:not(.ktg004-header-text)>span.text {
                    font-size: 1rem !important;
                    padding-left: 10px;
                }
                .ktg004-fontsize-larger div.form-label>span.text {
                    font-size: 1.2rem !important;
                }
                .ktg004-no-border {
                    border: none !important;
                }
                .ktg004-no-border td {
                    border: none !important;
                }
                .ktg004-border table tr td,
                .ktg004-border table tr th {
                          border-width: 0px;
                          border-bottom: 1px solid #BFBFBF;
                }
                .ktg004-header-text {
                  font-size: smaller;
                  text-align: right;
                }
                .on-update {
                  background: #BCBCBC !important;
                }
                .on-update div {
                  color: #FF0000;
                }
                .ktg004-header-row {
                  margin: 10px 5px 5px 5px;
                }
            </style>
        `
    })
    export class KTG004AComponent extends ko.ViewModel {
        widget: string = 'KTG004A';

        name: KnockoutObservable<string> = ko.observable('');

        detailedWorkStatusSettings = ko.observable(false);

        specialHolidaysRemainings: KnockoutObservableArray<SpecialHolidaysRemaining> = ko.observableArray([]);
        itemsDisplay: KnockoutObservableArray<ItemDisplay> = ko.observableArray([]);

        param: KTG004DisplayYearMonthParam;
        isDisplayA15_1: KnockoutObservable<boolean> = ko.observable(false);
        isDisplayA17_1: KnockoutObservable<boolean> = ko.observable(false);
        periodText: KnockoutComputed<string>;
        processStartDate: KnockoutObservable<string> = ko.observable("");
        processEndDate: KnockoutObservable<string> = ko.observable("");
        displayYearMonths: KnockoutObservableArray<any>;
        selectedDisplayYearMonth: KnockoutObservable<number> = ko.observable(null);
        textA15_1: KnockoutObservable<string>;
        textA15_2: KnockoutObservable<string>;
        textA17_1: KnockoutObservable<string>;
        responseData: ResponseData;
        isUpdating: KnockoutObservable<boolean> = ko.observable(false);

        constructor() {
            super();
            const vm = this;
            vm.displayYearMonths = ko.observableArray([
              { value: 1, name: vm.$i18n("KTG004_33") },
              { value: 2, name: vm.$i18n("KTG004_34") },
            ]);
            vm.periodText = ko.computed(() => vm.$i18n("KTG004_31", [vm.processStartDate(), vm.processEndDate()]));
            vm.textA15_1 = ko.observable(vm.$i18n("KTG004_29"));
            vm.textA15_2 = ko.observable(vm.$i18n("KTG004_30"));
            vm.textA17_1 = ko.observable(vm.$i18n("KTG004_35"));
            vm.selectedDisplayYearMonth.subscribe(value => {
              vm.isUpdating(true);
              vm.updatePeriod(value);
              vm.param.displayYearMonth = value;
              vm.$window.storage(STORAGE_KEY, vm.param);
              vm.$blockui("invisibleView")
                .then(() => vm.getAttendanceInfor(value))
                .then(() => vm.buildData(vm.responseData))
                .then(() => vm.isUpdating(false))
                .always(() => vm.$blockui("clearView"));
            });
        }

        created() {
            const vm = this;
            vm.$blockui('invisibleView')
                .then(() => vm.initScreen())
                .then(() => vm.$ajax("at", KTG004_API.GET_DATA, vm.param))
                .then(function (data: ResponseData) {
                    vm.responseData = data;
                    vm.updatePeriod(vm.param.displayYearMonth);
                    vm.selectedDisplayYearMonth(vm.param.displayYearMonth);
                    vm.buildData(data);
                })
                .always(() => vm.$blockui('clearView'));
        }

        private buildData(data: ResponseData) {
          const vm = this;
          const {
              name,
              detailedWorkStatusSettings,
              itemsSetting,
              attendanceInfor,
              remainingNumberInfor
          } = data;
          const {
              dailyErrors,
              early,
              flexCarryOverTime,
              flexTime,
              holidayTime,
              late,
              nigthTime,
              overTime
          } = attendanceInfor;

          const {
              longTermCareRemainingNumber,
              numberAccumulatedAnnualLeave,
              numberOfAnnualLeaveRemain,
              numberOfSubstituteHoliday,
              nursingRemainingNumberOfChildren,
              remainingHolidays,
              specialHolidaysRemainings,
              grantDate,
              grantDays, 
              subHolidayTimeManage
          } = remainingNumberInfor;

          const itemsDisplay: ItemDisplay[] = [];
          const itemsHolidaysRemainings: SpecialHolidaysRemaining[] = [];

          const items: number[] = _
              .chain(itemsSetting)
              .orderBy(['item'], ['asc'])
              .filter(({ displayType }) => !!displayType)
              .map(({ item }) => item)
              .value();

          vm.name(name || "");
          vm.detailedWorkStatusSettings(detailedWorkStatusSettings);

          _
              .chain(items)
              .each((item: number) => {
                  switch (item) {
                      case 21:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_1',
                                  text: '',
                                  item: item,
                                  btn: true,
                                  canClick: dailyErrors ? true : false
                              })
                          break;
                      case 22:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_2',
                                  item: item,
                                  text: convertToTime(overTime)
                              })
                          break;
                      case 23:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_3',
                                  item: item,
                                  text: `${convertToTime(flexTime)}${vm.$i18n('KTG004_4', [convertToTime(flexCarryOverTime)])}`
                              })
                          break;
                      case 24:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_5',
                                  item: item,
                                  text: convertToTime(nigthTime)
                              })
                          break;
                      case 25:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_6',
                                  item: item,
                                  text: convertToTime(holidayTime)
                              })
                          break;
                      case 26:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_7',
                                  item: item,
                                  text: vm.$i18n('KTG004_8', [late, early])
                              })
                          break;
                      case 27:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_9',
                                  item: item,
                                  text: 
              numberOfAnnualLeaveRemain.time == ZERO_TIME
              ?
              vm.$i18n('KTG004_15', [`${numberOfAnnualLeaveRemain.day}`])
              :
              vm.$i18n('KTG004_28', [`${numberOfAnnualLeaveRemain.day}`, `${numberOfAnnualLeaveRemain.time}`]),
            grantDay:
              _.isNil(grantDate)
              ?
              `${vm.$i18n('KTG004_26')}　${vm.$i18n('KTG004_27')}`
              :
              `${vm.$i18n('KTG004_26')}　${moment(grantDate).format("YYYY/MM/DD")}　${grantDays}日`
              
                
                              })
                          break;
                      case 28:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_10',
                                  item: item,
                                  text: vm.$i18n('KTG004_15', [`${numberAccumulatedAnnualLeave}`])
                              })
                          break;
                      case 29:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_11',
                                  item: item,
                                  text: 
                                  numberOfSubstituteHoliday.day <= 0 && numberOfSubstituteHoliday.time != ZERO_TIME
                                  ?
                                  `${numberOfSubstituteHoliday.time}`
                                  : numberOfSubstituteHoliday.day == 0 && numberOfSubstituteHoliday.time == ZERO_TIME && subHolidayTimeManage == 1 ?
                                  `${numberOfSubstituteHoliday.time}` :
                                  vm.$i18n('KTG004_15', [`${numberOfSubstituteHoliday.day}`])
                              })
                          break;
                      case 30:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_12',
                                  item: item,
                                  text: vm.$i18n('KTG004_15', [`${remainingHolidays}`])
                              })
                          break;
                      case 31:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_13',
                                  item: item,
                                  text: 
              nursingRemainingNumberOfChildren.time == ZERO_TIME
              ?
              vm.$i18n('KTG004_15', [`${nursingRemainingNumberOfChildren.day}`])
              :
              vm.$i18n('KTG004_28', [`${nursingRemainingNumberOfChildren.day}`, `${nursingRemainingNumberOfChildren.time}`])
                              })
                          break;
                      case 32:
                          itemsDisplay
                              .push({
                                  name: 'KTG004_14',
                                  item: item,
                                  text:
              longTermCareRemainingNumber.time == ZERO_TIME
              ? 
              vm.$i18n('KTG004_15', [`${longTermCareRemainingNumber.day}`])
              :
              vm.$i18n('KTG004_28', [`${longTermCareRemainingNumber.day}`, `${longTermCareRemainingNumber.time}`])
                              })
                          break;
                  }
              })
              .value();

          _
              .chain(specialHolidaysRemainings)
              .filter(({ code }) => items.indexOf(code) > -1)
              .each(({
                  code,
                  name,
                  specialResidualNumber
              }) => {
                  itemsHolidaysRemainings
                      .push({
                          code,
                          name,
                          specialResidualNumber: 
            specialResidualNumber.time == ZERO_TIME
            ?
            vm.$i18n('KTG004_15', [`${specialResidualNumber.day}`])
            :
            vm.$i18n('KTG004_28', [`${specialResidualNumber.day}`, `${specialResidualNumber.time}`])
                      });
              })
              .value();

          vm.itemsDisplay(itemsDisplay);
          vm.specialHolidaysRemainings(itemsHolidaysRemainings);
          vm.isDisplayA15_1(vm.getGroup(1)().length > 0);
          vm.isDisplayA17_1(vm.getGroup(2)().length > 0);

          vm.$nextTick(() => {
              $(vm.$el)
                  .find('[data-bind]')
                  .removeAttr('data-bind');
              _.forEach($(".row-show-ktg004 td"), element => $(element).removeClass("ktg004-no-border"));    
              $(".row-show-ktg004").last().addClass("ktg004-no-border");
          });
        }

        public setting() {
            const vm = this;

            vm.$window
                .modal('at', DIALOGS.KTG004B)
                .then(() => {
                    vm.$window
                        .shared("KTG004B")
                        .then((data: any) => {
                            if (data) {
                                vm.created();
                            }
                        });
                });
        }

        public openKDW003() {
            const vm = this;

            vm.$jump('at', WINDOWS.KTG003A);
        }

        // 起動する
        private initScreen(): JQueryPromise<void> {
          const vm = this;
          return vm.$window.storage(STORAGE_KEY).then(result => {
            // KTG004表示年月が保持されていない場合
            if (_.isNil(result)) {
              return vm.$ajax("at", KTG004_API.getWidgetInitialDisplayMonth)
              .then((result: WidgetInitialDisplayMonthDto) => {
                const param = new KTG004DisplayYearMonthParam(result);
                vm.$window.storage(STORAGE_KEY, param);
                vm.param = param;
              });
            } else {
              vm.param = result;
            }
          });
        }

        private getAttendanceInfor(displayYearMonth: number): JQueryPromise<Attendance> {
          const vm = this;
          let currentClosingPeriod: Closing;
          if (displayYearMonth === 1) {
            currentClosingPeriod = {
              processingYm: vm.param.currentProcessingYm,
              startDate: vm.param.currentProcessingYmStartDate,
              endDate: vm.param.currentProcessingYmEndDate
            };
          } else {
            currentClosingPeriod = {
              processingYm: vm.param.nextProcessingYm,
              startDate: vm.param.nextProcessingYmStartDate,
              endDate: vm.param.nextProcessingYmEndDate
            };
          }
          const param = {
            itemsSetting: vm.responseData.itemsSetting,
            currentClosingPeriod: currentClosingPeriod
          };
          param.currentClosingPeriod.startDate = moment.utc(param.currentClosingPeriod.startDate, YYYY_MM_DD).toISOString();
          param.currentClosingPeriod.endDate = moment.utc(param.currentClosingPeriod.endDate, YYYY_MM_DD).toISOString();
          return vm.$ajax("at", KTG004_API.getAttendanceInfor, param)
            .then(result => vm.responseData.attendanceInfor = result);
        }

        private updatePeriod(value: number) {
          const vm = this;
          if (value === 1) {
            vm.processStartDate(moment.utc(vm.param.currentProcessingYmStartDate).format(MM_DD));
            vm.processEndDate(moment.utc(vm.param.currentProcessingYmEndDate).format(MM_DD));
          } else {
            vm.processStartDate(moment.utc(vm.param.nextProcessingYmStartDate).format(MM_DD));
            vm.processEndDate(moment.utc(vm.param.nextProcessingYmEndDate).format(MM_DD));
          }
        }

        public getGroup(groupNo: number): KnockoutComputed<ItemDisplay[]> {
          const vm = this;
          const GROUP_1 = [21, 22, 23, 24, 25, 26];
          const GROUP_2 = [27, 28, 29, 30, 31, 32];
          switch (groupNo) {
            // A15_1
            case 1: 
              return ko.computed(() => _.filter(vm.itemsDisplay(), data => _.includes(GROUP_1, data.item)));
            // A17_1
            case 2:
              return ko.computed(() => _.filter(vm.itemsDisplay(), data => _.includes(GROUP_2, data.item)));
          }
        }
    }
	const ZERO_TIME = `0:00`;
  const YYYY_MM_DD = "YYYY/MM/DD";
  const MM_DD = "MM/DD";
  const STORAGE_KEY = "KTG004_YM_PARAM";

    interface ItemDisplay {
        name: string;
        text: string;
        item: number;
        btn?: boolean;
        canClick?: boolean;
		grantDay?: string;
    }

    interface SpecialHolidaysRemaining {
        //特別休暇コード
        code: number;
        //特別休暇名称
        name: string;
        //特休残数
        specialResidualNumber: string;
    }

    interface ResponseData {
        attendanceInfor: Attendance;
        closingDisplay: Closing;
        closingThisMonth: Closing;
        closureId: number;
        detailedWorkStatusSettings: boolean;
        itemsSetting: ItemSetting[];
        name: any | null;
        nextMonthClosingInformation: any | null;

        remainingNumberInfor: RemainingNumberInfor;
        vacationSetting: VacationSetting;
    }

    interface ItemSetting {
        displayType: boolean;
        item: 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 | 32;
        name: string;
    }

    interface Closing {
        endDate: string;
        processingYm: number;
        startDate: string;
    }

    interface NumberOfShift {
        day: number;
        time: string;
    }

    interface Attendance {
        dailyErrors: boolean;
        early: string;
        flexCarryOverTime: string;
        flexTime: string;
        holidayTime: string
        late: string;
        nigthTime: string;
        overTime: string;
    }

    interface RemainingNumberInfor {
        longTermCareRemainingNumber: NumberOfShift;
        numberAccumulatedAnnualLeave: number;
        numberOfAnnualLeaveRemain: NumberOfShift;
        numberOfSubstituteHoliday: NumberOfShift;
        nursingRemainingNumberOfChildren: NumberOfShift;
        remainingHolidays: number;
        specialHolidaysRemainings: SpecialHolidaysRemainings[];
		grantDate?: string;
		grantDays: number;
        subHolidayTimeManage: number;

    }

    interface VacationSetting {
		// 60H超休残数管理する
		holiday60HManage: boolean;
    
		// 介護残数管理する
		nursingManage: boolean;
		
		// 公休残数管理する
		publicHolidayManage: boolean;
		
		// 子の看護残数管理する
		childCaremanage: boolean;
		
		// 振休残数管理する
		accomoManage: boolean;
		
		// 積立年休残数管理する
		accumAnnualManage: boolean;
		
		// 代休残数管理する
		substituteManage: boolean;
		
		// 代休時間残数管理する
		substituteTimeManage: boolean;
		
		// 年休残数管理する
		annualManage: boolean;
	}

    interface SpecialHolidaysRemainings {
        code: number;
        name: string;
        specialResidualNumber: NumberOfShift;
    }

    interface WidgetInitialDisplayMonthDto {
      // 表示年月
      currentOrNextMonth: number;
      
      // 翌月処理月
      nextMonth: number;
      
      // 当月処理月
      currentMonth: number;
      
      // 翌月処理開始日
      nextMonthStart: string;
      
      // 翌月処理終了日
      nextMonthEnd: string;
      
      // 当月処理開始日
      currentMonthStart: string;
      
      // 当月処理終了日
      currentMonthEnd: string;
      
      // 締めID
      closureId: string;
    }

    class KTG004DisplayYearMonthParam {
    
      // 表示年月
      displayYearMonth: number;
      
      // 翌月処理月
      nextProcessingYm: number;
      
      // 当月処理月
      currentProcessingYm: number;
      
      // 翌月処理開始日
      nextProcessingYmStartDate: string;
      
      // 翌月処理終了日
      nextProcessingYmEndDate: string;
      
      // 当月処理開始日
      currentProcessingYmStartDate: string;
      
      // 当月処理終了日
      currentProcessingYmEndDate: string;
      
      // 締めID
      closureId: string;

      constructor(init: WidgetInitialDisplayMonthDto) {
        this.displayYearMonth = init.currentOrNextMonth;
        this.closureId = init.closureId;
        this.currentProcessingYm = init.currentMonth;
        this.nextProcessingYm = init.nextMonth;
        this.currentProcessingYmStartDate = moment.utc(init.currentMonthStart, YYYY_MM_DD).toISOString();
        this.currentProcessingYmEndDate = moment.utc(init.currentMonthEnd, YYYY_MM_DD).toISOString();
        this.nextProcessingYmStartDate = moment.utc(init.nextMonthStart, YYYY_MM_DD).toISOString();
        this.nextProcessingYmEndDate = moment.utc(init.nextMonthEnd, YYYY_MM_DD).toISOString();
      }
    }
}