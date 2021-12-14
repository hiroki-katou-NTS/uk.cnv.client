/// <reference path="../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kdp.share {
	const tabButtonTempate = `
		<!-- ko if: ko.unwrap($component.filteredTabs).length -->
		<!-- ko if: ko.unwrap($component.filteredTabs).length > 1 -->
			<div data-bind="ntsTabPanel: { dataSource: $component.filteredTabs, active: $component.selected }"></div>
		<!-- /ko -->
		<div id="stampBtnContainer" data-bind="foreach: { data: $component.filteredTabs, as: 'group' }">
			<div class="grid-container" data-bind="
				if: ko.toJS($component.currentTab).pageNo === group.pageLayout.pageNo,
				css: 'btn-layout-type-' + group.pageLayout.buttonLayoutType,
				style: {padding: ko.toJS($component.currentTab).pageNo === group.pageLayout.pageNo ? '': '0'}">
				<!-- ko foreach: _.chunk(ko.unwrap(group.pageLayout.buttonSettings), 2) -->
				<div data-bind="foreach: $data" class="cf">
					<button class="stamp-rec-btn"
						data-bind="
							btn-setting: $data,
							click: function() {
								$component.params.click($data, ko.toJS($component.currentTab));
							},
							timeClick: 175">
						</button>
				</div>
				<!-- /ko -->
			</div>
		</div>
		<style>
		#stamp-desc div {
			font-size: 2.5vmin;
		}
	</style>
		<!-- /ko -->
	`;

	const DEFAULT_GRAY = '#E8E9EB';

	export const getIcon = (changeClockArt: any, changeCalArt: any, setPreClockArt: any, changeHalfDay: any, reservationArt: any) => {
		switch (checkType(changeClockArt, changeCalArt, setPreClockArt, changeHalfDay, reservationArt)) {
			case 1:
				return 205;
			case 2:
				return 206;
			case 3:
				return 207;
			case 4:
				return 208;
			case 5:
				return 209;
			case 6:
				return 210;
			case 7:
				return 211;
			case 8:
				return 212;
			case 9:
				return 213;
			case 10:
				return 214;
			case 11:
				return 215;
			case 12:
				return 216;
			case 13:
				return 217;
			case 14:
				return 218;
			case 15:
				return 219;
			case 16:
				return 220;
			case 17:
				return 221;
			case 18:
				return 222;
			case 19:
				return 223;
			case 20:
				return 224;
		}
	}

	export const checkType = (changeClockArt: any, changeCalArt: any, setPreClockArt: any, changeHalfDay: any, reservationArt: any) => {
		if (changeCalArt == 0 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
			if (changeClockArt == 0)
				return 1;

			if (changeClockArt == 1)
				return 5;

			if (changeClockArt == 4)
				return 8;

			if (changeClockArt == 5)
				return 9;

			if (changeClockArt == 2)
				return 10;

			if (changeClockArt == 3)
				return 11;

			if (changeClockArt == 7)
				return 12;

			if (changeClockArt == 9)
				return 13;

			if (changeClockArt == 6)
				return 14;

			if (changeClockArt == 8)
				return 15;

			if (changeClockArt == 12)
				return 16;
		}
		if (changeClockArt == 0 && changeCalArt == 0 && setPreClockArt == 1 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
			return 2;

		if (changeCalArt == 1 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
			if (changeClockArt == 0)
				return 3;

			if (changeClockArt == 6)
				return 17;
		}

		if (changeCalArt == 3 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0) {
			if (changeClockArt == 0)
				return 4;

			if (changeClockArt == 6)
				return 18;
		}

		if (changeClockArt == 1 && changeCalArt == 0 && setPreClockArt == 2 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
			return 6;

		if (changeClockArt == 1 && changeCalArt == 2 && setPreClockArt == 0 && (changeHalfDay == false || changeHalfDay == 0) && reservationArt == 0)
			return 7;

		if ((changeClockArt == "" || changeClockArt == null) && (changeCalArt == "" || changeCalArt == null) && (setPreClockArt == "" || setPreClockArt == null) && (changeHalfDay == "" || changeHalfDay == null) && reservationArt == 1)
			return 19;

		if ((changeClockArt == "" || changeClockArt == null) && (changeCalArt == "" || changeCalArt == null) && (setPreClockArt == "" || setPreClockArt == null) && (changeHalfDay == "" || changeHalfDay == null) && reservationArt == 2)
			return 20;
	}

	@handler({
		bindingName: 'btn-setting'
	})
	export class ButtonSettingBindingHandler implements KnockoutBindingHandler {
		update(element: HTMLButtonElement, valueAccessor: () => ButtonSetting): void {

			const data: ButtonSetting = ko.unwrap(valueAccessor());

			const icon = document.createElement('i');

			ko.applyBindingsToNode(icon, { ntsIcon: { no: getIcon(data.changeClockArt, data.changeCalArt, data.setPreClockArt, data.changeHalfDay, data.btnReservationArt), 'width': '68', 'height': '68' } });

			const text = document.createElement('div');

			const { supportUse, temporaryUse } = data;

			ko.applyBindingsToNode(text, { text: data.btnName });

			let btnType = checkType(data.changeClockArt, data.changeCalArt, data.setPreClockArt, data.changeHalfDay, data.btnReservationArt);

			$(element)
				.append(icon)
				.append(text)
				.css({
					'color': data.btnTextColor,
					'background-color': data.btnBackGroundColor,
					'visibility': data.btnPositionNo === -1 || data.usrArt === 0 || (supportUse === false && _.includes([14, 15, 16, 17, 18], btnType)) 
					|| (temporaryUse === false && _.includes([12, 13], btnType)) ? 'hidden' : 'visible'
				});
			changeHeightBtn(true);
			if (data.btnPositionNo === 1 || data.btnPositionNo === 2) {
				changeFontSize(element, 0);	
			} else {
				changeFontSize(element, 1);
			}
		}
	}
	const COMPONENT_NAME = 'kdp-tab-button-panel';

	@handler({
		bindingName: COMPONENT_NAME,
		validatable: true,
		virtual: false
	})
	export class ButtonSettingComponentBindingHandler implements KnockoutBindingHandler {
		init(element: HTMLElement, valueAccessor: () => any, __ab: KnockoutAllBindingsAccessor, ___vm: nts.uk.ui.vm.ViewModel, bindingContext: KnockoutBindingContext) {
			const name = COMPONENT_NAME;
			const params = valueAccessor();

			element.classList.add('tab-button-panel');

			ko.applyBindingsToNode(element, { component: { name, params } }, bindingContext);

			return { controlsDescendantBindings: true };
		}
	}

	@component({
		name: COMPONENT_NAME,
		template: tabButtonTempate
	})
	export class kdpTabButtonComponent extends ko.ViewModel {
		selected: KnockoutObservable<string> = ko.observable('');

		currentTab!: KnockoutComputed<PageLayout>;

		filteredTabs!: KnockoutComputed<TabLayout[]>;

		buttonSize: KnockoutObservable<number> = ko.observable(0);

		supportUse: KnockoutObservable<boolean> = ko.observable(false);

		temporaryUse: KnockoutObservable<boolean> = ko.observable(false);
		
		constructor(public params: StampParam) {
			super();

			const vm = this;

			if (!params.click) {
				params.click = () => { };
			}
			if (!params.reCalGridWidthHeight) {
				params.reCalGridWidthHeight = () => { };
			}

			if (!params.tabs) {
				params.tabs = ko.observableArray([]);
			}
			
			if(!params.pageComment){
				params.pageComment = ko.observable('');
			}
			
			if(!params.commentColor){
				params.commentColor = ko.observable('');
			}

			if (!params.stampToSuppress) {
				params.stampToSuppress = ko.observable({
					departure: false,
					goOut: false,
					goingToWork: false,
					turnBack: false
				});
			}

			if (!params.marginBottom) {
				params.marginBottom = ko.observable(0);
			}

			ko.computed({
				read: () => {
					const data = ko.unwrap(params.tabs);

					if (!vm.selected()) {
						if (data && data.length) {
							const first = data[0];

							vm.selected(`tab-${first.pageNo}`);
						}
					}
				}
			});

			vm.currentTab = ko.computed({
				read: () => {
					const $el = $(vm.$el);
					const selected = ko.unwrap(vm.selected);
					const filteredTabs = ko.unwrap(vm.filteredTabs);

					vm.$nextTick(() => {
						$el
							.find('button')
							.attr('tabindex', $el.data('tabindex'));
					});
					const exist = _.find(filteredTabs, (d) => `tab-${d.pageLayout.pageNo}` === selected);

					const currentTab = exist && _.clone(exist).pageLayout || {
						pageNo: -1,
						buttonLayoutType: -1,
						buttonSettings: [] as ButtonSetting[],
						stampPageComment: '',
						stampPageCommentColor: '',
						stampPageName: ''
					};

					vm.params.pageComment(currentTab.stampPageComment);
					vm.params.commentColor(currentTab.stampPageCommentColor);
					
					if (vm.$el) {
						if (currentTab.stampPageComment) {
							vm.$el.classList.add('has-comment');
						} else {
							vm.$el.classList.remove('has-comment');
						}
						
						if (filteredTabs.length > 1) {
							vm.$el.classList.add('has-tab');							
						} else {
							vm.$el.classList.remove('has-tab');							
						}
					}
					vm.setSize();
					params.reCalGridWidthHeight();
					return currentTab;
				}
			});

			vm.filteredTabs = ko.computed({
				read: () => {
					const data = ko.unwrap(params.tabs);
					const buttonSize = ko.unwrap(vm.buttonSize);
					const setting: StampToSuppress = ko.unwrap(params.stampToSuppress as any) || {
						goingToWork: false,
						departure: false,
						goOut: false,
						turnBack: false
					};
					const supportUsed = ko.unwrap(vm.supportUse);
					const temporaryUsed = ko.unwrap(vm.temporaryUse);

					const filters: TabLayout[] = [];

					for (let i = 1; i <= 5; i++) {
						const tab = _.find(data, (d) => d.pageNo === i);

						if (tab) {
							const cloned = _.cloneDeep(tab);
							const buttons: ButtonSetting[] = [];
							const { buttonSettings, buttonLayoutType } = cloned;
							const { SMALL_8, LARGE_2_SMALL_4 } = LAYOUT_TYPE;
							const size = (buttonLayoutType === LARGE_2_SMALL_4) ? 6 : 8;

							for (let j = 1; j <= size; j++) {
								const btn = _.find(buttonSettings, (btn) => btn.btnPositionNo === j);

								if (btn) {
									switch (btn.btnDisplayType) {
										default:
										case 1:
											if (setting.goingToWork) {
												btn.btnBackGroundColor = DEFAULT_GRAY;
											}
											break;
										case 2:
											if (setting.departure) {
												btn.btnBackGroundColor = DEFAULT_GRAY;
											}
											break;
										case 3:
											if (setting.goOut) {
												btn.btnBackGroundColor = DEFAULT_GRAY;
											}
											break;
										case 4:
											if (setting.turnBack) {
												btn.btnBackGroundColor = DEFAULT_GRAY;
											}
											break
									}

									const constance = ((buttonLayoutType === LARGE_2_SMALL_4 && j < 3)) ? 3 : 1.5;

									btn.height = Math.max(buttonSize, 70) * constance + (buttonLayoutType === SMALL_8 ? 7 : 0);
									btn.supportUse = supportUsed;
									btn.temporaryUse = temporaryUsed;

									buttons.push(btn);
								} else {
									buttons.push({
										audioType: 0,
										btnBackGroundColor: '',
										btnDisplayType: -1,
										btnName: '',
										btnPositionNo: -1,
										btnReservationArt: -1,
										btnTextColor: '',
										changeCalArt: -1,
										changeClockArt: -1,
										changeHalfDay: -1,
										goOutArt: -1,
										setPreClockArt: -1,
										usrArt: -1,
										height: buttonSize,
										supportUse: supportUsed,
										temporaryUse: temporaryUsed
									});
								}
							}

							cloned.buttonSettings = buttons;

							filters.push({
								content: `.tab-${cloned.pageNo}`,
								enable: ko.observable(true),
								id: `tab-${cloned.pageNo}`,
								pageLayout: cloned,
								title: cloned.stampPageName,
								visible: ko.observable(true)
							});
						}
					}
					return filters;
				}
			});
			
			//打刻入力で共通設定を取得する
			vm.$ajax('at/record/stamp/settings_stamp_common')
				.done((data: ISettingsStampCommon) => {
					vm.supportUse(!!data.supportUse);
					vm.temporaryUse(!!data.temporaryUse);
				});
			window.onresize = function() {vm.setSize();params.reCalGridWidthHeight();}
		}
		
		setSize = function() {
			changeHeightBtn(false);
			$('.btn-layout-type-0>div:first-child button').each(function() {
				changeFontSize(this, 0);	
			});
			$('.btn-layout-type-0>div:not(:first-child) button').each(function() {
				changeFontSize(this, 1);	
			});
			$('.btn-layout-type-1>div button').each(function() {
				changeFontSize(this, 1);	
			});
		}
		mounted() {
			const vm = this;
			const tid = 'tabindex';
			const $el = $(vm.$el);
			const tabindex = $el.attr(tid);

			if (tabindex) {
				$el
					.find('.ui-tabs')
					.attr(tid, tabindex);

				$el
					.find('.ui-tabs-tab')
					.attr(tid, tabindex)
					.on('keydown', (evt: JQueryEventObject) => {
						if (evt.keyCode === 13) {
							$(evt.target).find('a').trigger('click');
						}
					});

				$el
					.find('button')
					.attr(tid, tabindex);

				$el
					.removeAttr(tid)
					.data(tid, tabindex);
			}

			vm.selected.valueHasMutated();

			$(window)
				.on('resize', () => {
					if (vm.$el) {
						const marginBottom = ko.toJS(vm.params.marginBottom);
						const tabs = vm.$el.querySelector('#tab-button-group');

						if (tabs) {
							const bound = tabs.getBoundingClientRect();
							const height = Math.floor((window.innerHeight - bound.top - 150 - marginBottom) / 7);

							vm.buttonSize(height);
						}
					}
				})
				.trigger('resize');
		}
	}

	export enum LAYOUT_TYPE {
		LARGE_2_SMALL_4 = 0,
		SMALL_8 = 1
	}

	export interface StampParam {
		click: () => void;
		reCalGridWidthHeight: () => void;
		tabs: KnockoutObservableArray<PageLayout>;
		stampToSuppress: KnockoutObservable<StampToSuppress>;
		marginBottom: KnockoutObservable<number>;
		pageComment: KnockoutObservable<string>;
		commentColor: KnockoutObservable<string>;
	}

	export interface StampToSuppress {
		departure: boolean;
		goOut: boolean;
		goingToWork: boolean;
		turnBack: boolean;
	}

	export interface TabLayout {
		id: string;
		title: string;
		content: string;
		enable: KnockoutObservable<boolean>;
		visible: KnockoutObservable<boolean>;
		pageLayout: PageLayout;
	}

	export interface PageLayout {
		buttonLayoutType: number;
		pageNo: number;
		stampPageComment: string;
		stampPageCommentColor: string;
		stampPageName: string;
		buttonSettings: ButtonSetting[];
	}

	export interface ButtonSetting {
		audioType: 0 | 1 | 2;
		btnBackGroundColor: string;
		btnDisplayType: number;
		btnName: string;
		btnPositionNo: number;
		btnReservationArt: number;
		btnTextColor: string;
		changeCalArt: number;
		changeClockArt: ChangeClockArt;
		changeHalfDay: number;
		goOutArt: number;
		setPreClockArt: number;
		usrArt: NotUseAtr;
		height: number;
		supportUse: boolean;
		temporaryUse: boolean;
	}

	export enum NotUseAtr {
		/** The use. */
		USE = 1,

		/** The not use. */
		NOT_USE = 0
	}

	export enum ChangeClockArt {
		/** 0. 出勤 */
		GOING_TO_WORK = 0,

		/** 1. 退勤 */
		WORKING_OUT = 1,

		/** 2. 入門 */
		OVER_TIME = 2,

		/** 3. 退門 */
		BRARK = 3,

		/** 4. 外出 */
		GO_OUT = 4,

		/** 5. 戻り */
		RETURN = 5,

		/** 6. 応援開始 */
		FIX = 6,

		/** 7. 臨時出勤 */
		TEMPORARY_WORK = 7,

		/** 8. 応援終了 */
		END_OF_SUPPORT = 8,

		/** 9. 臨時退勤 */
		TEMPORARY_LEAVING = 9,

		/** 10. PCログオン */
		PC_LOG_ON = 10,

		/** 11. PCログオフ */
		PC_LOG_OFF = 11,

		/** 12. 応援出勤 */
		SUPPORT = 12,

		/** 13. 臨時+応援出勤 */
		TEMPORARY_SUPPORT_WORK = 13
	}

	export interface ISettingsStampCommon {
		supportUse: boolean;
		temporaryUse: boolean;
		workUse: boolean;
	}
	let changeFontSize = function(element: HTMLButtonElement, type : number){
		let text = element.innerText.replace(/(\r\n|\n|\r)/gm,"");
		if(text.length < 9){
			if(type == 0 && $(element).parentsUntil($('.btn-layout-type-0')).length === 1) {
				element.style.fontSize = '26px';	
			}else{
				element.style.fontSize = '20px';
			}
			return;
		}
		let maxSize : number = 16;
		if(type == 0 && $(element).parentsUntil($('.btn-layout-type-0')).length === 1) {
			maxSize = 20;	
		} 
		let fontSize = (element.offsetWidth / text.length) + (type == 0 && $(element).parentsUntil($('.btn-layout-type-0')).length === 1
			?	4
			: $(element).parentsUntil($('.btn-layout-type-0')).length === 1 ? 4 : 1);
		if(fontSize > maxSize) fontSize = maxSize;
		element.style.fontSize = fontSize + 'px';
	}
	let changeHeightBtn = function(check :boolean){
		$('.btn-layout-type-0>div:first-child button').css({'height':$('.btn-layout-type-0>div:first-child button').width() - (check ? 10 : 0) +'px'});
		$('.btn-layout-type-0>div:not(:first-child) button').css({'height':$('.btn-layout-type-0>div:not(:first-child) button').width()/2.3 - (check ? 5 : 0) + 'px'});
	}
}