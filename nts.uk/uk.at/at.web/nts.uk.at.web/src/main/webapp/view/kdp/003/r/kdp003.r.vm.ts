/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.kdp003.r {

    const API = {

        // 打刻入力(共有)でお知らせメッセージを表示する
		DISPLAY_NOTICE: '/at/record/stamp/notice/displayNoticeMessage',
	};

    

    @bean()
	export class ViewModel extends ko.ViewModel {
        // ver50
        // R5 利用停止前内容
        // beforeStopNoticeList: KnockoutObservableArray<any> = ko.observableArray([]);
        
        // R1 本部見内容
        headOfficeNoticeList: KnockoutObservableArray<DisplayResult> = ko.observableArray([]);

        // R2 職場メッセージ
        workplaceNoticeList: KnockoutObservableArray<DisplayResult> = ko.observableArray([]);

        // ver50
        // R6 利用停止中内容
		// stoppingNotice: KnockoutObservable<string> = ko.observable('現在システムはメンテナンスの為、停止されいています。メンテナンス終了予定は１５：００となります。');

        noticeSetting: NoticeSet = new NoticeSet();
        screen: String = '';

        constructor(private params: IParam) {
			super();

            const vm = this;
            if (params) {
	            vm.noticeSetting = params.setting;
	            vm.screen = params.screen;
            }
            
        }

        created() {

        }

        mounted() {
            const vm = this;

			$(document).ready(() => {
				$('#closeBtn').focus();
			});

            if (vm.screen === 'KDP003') {
                vm.displayNotice('loginKDP003');
            }

            if (vm.screen === 'KDP004') {
                vm.displayNotice('loginKDP004');
            }

            if (vm.screen === 'KDP005') {
                vm.displayNotice('loginKDP005');
            }

        }

        closeDialog() {
			const vm = this;
			vm.$window.close();
		}

        displayNotice (cacheNm: String) {
            const vm = this;
            nts.uk.characteristics.restore(cacheNm).done((cache:any) =>{

                const noticeParam: NoticeParam = {
                    //システム日付～システム日付
                    periodDto: new DatePeriod({startDate: moment().toDate(),
                    endDate: moment().toDate()}),
    
                    //「localStorage.選択職場ID」(List)
                    wkpIds: []
                    
                };

                if (cacheNm === 'loginKDP003') {
                    noticeParam.wkpIds = cache.WKPID;
                }

                if (cacheNm === 'loginKDP004' || cacheNm === 'loginKDP005') {
                    noticeParam.wkpIds = cache.selectedWP;
                }
                
                vm.$blockui('show');
                vm.$ajax('at', API.DISPLAY_NOTICE, noticeParam)
                    .then((noticeList: Array<MsgNoticeDto>) => {
                        if (noticeList) {
    
                            let headOfficeNoticeList = _.filter(noticeList, n => n.message.targetInformation.destination == DestinationClassification.ALL);
                            let workplaceNoticeList = _.filter(noticeList, n => n.message.targetInformation.destination == DestinationClassification.WORKPLACE);
                        
                            let headOfficeNotices = headOfficeNoticeList.map((h) => {
                                return new DisplayResult(h);
                            });
    
                            let workplaceNotices = workplaceNoticeList.map((w) => {
                                return new DisplayResult(w);
                            });
    
                            vm.headOfficeNoticeList(headOfficeNotices);
                            vm.workplaceNoticeList(workplaceNotices);
    
                    }
                    })
                    .fail(error => vm.$dialog.error(error))
                    .always(() => vm.$blockui('hide'));
                });
        }
    }

    enum DestinationClassification {
		// 0 全社員
		ALL = 0,
		// 1 職場選択
		WORKPLACE = 1,
		// 2 社員選択
        EMPLOYEE = 2
	}

    export interface NoticeParam {
        periodDto: DatePeriod; //期間
        wkpIds: Array<String>; //職場ID
    }

    export class DatePeriod {
        startDate: Date;
        endDate: Date;
        constructor(init?: Partial<DatePeriod>) {
			$.extend(this, init);
		}
    }

    export class NoticeSet {
        comMsgColor: ColorSettingDto ; //会社メッセージ色
        companyTitle: string; //会社宛タイトル
        wkpMsgColor: ColorSettingDto //職場メッセージ色
        wkpTitle: string //職場宛タイトル
        constructor() {
			this.comMsgColor = new ColorSettingDto();
			this.companyTitle = '';
			this.wkpMsgColor = new ColorSettingDto();
			this.wkpTitle = '';
		}
    }

    export class ColorSettingDto {
		textColor: string; //文字色
        backGroundColor: string //背景色
		constructor() {
			this.textColor = '';
			this.backGroundColor = '';
		}
	}

    export interface MsgNoticeDto {
        message: MessageNotice;
        scd: string;
        bussinessName: string
    }

    export interface MessageNotice {
        creatorID: string; //作成者ID
        inputDate: string; //入力日
        modifiedDate: string; //変更日
        targetInformation: TargetInformationDto; //対象情報
        startDate: string; //開始日
        endDate: string;  //終了日
        employeeIdSeen: Array<String>; //見た社員ID
        notificationMessage: string; //メッセージの内容
    }

    export class DisplayResult {
       displayMessageNotice: DisplayMessageNotice;
       scd: string;
       bussinessName: string;
       constructor(m: MsgNoticeDto ) {
           this.displayMessageNotice = new DisplayMessageNotice(m.message);
           this.scd = m.scd;
           this.bussinessName = m.bussinessName;
        }
    }

    export class DisplayMessageNotice {
        creatorID: string; //作成者ID
        inputDate: string; //入力日
        modifiedDate: string; //変更日
        destination: number; //宛先区分
        startDate: string; //開始日
        endDate: string;  //終了日
        notificationMessage: string; //メッセージの内容
        constructor(h: MessageNotice) {
            this.creatorID = h.creatorID,
            this.inputDate = h.inputDate,
            this.modifiedDate = h.modifiedDate,
            this.destination = h.targetInformation.destination,
            this.startDate = nts.uk.time.applyFormat("Short_MD", h.startDate),
            this.endDate = nts.uk.time.applyFormat("Short_MD", h.endDate),
            this.notificationMessage = h.notificationMessage
		}
    }

    export class TargetInformationDto {
        targetSIDs: Array<String>; //対象社員ID
        targetWpids: Array<String>; //対象職場ID 
        destination: number; //宛先区分
        constructor(init?: Partial<TargetInformationDto>) {
			$.extend(this, init);
		}
    }

    export interface IParam {
        setting: NoticeSet;
        screen: String;
    }
}