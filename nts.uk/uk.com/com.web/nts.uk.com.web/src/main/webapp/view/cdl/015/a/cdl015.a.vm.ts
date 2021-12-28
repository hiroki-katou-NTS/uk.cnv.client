/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.cdl015.a {

	const API = {
		START: 'screen/com/cdl015/start',
		REGISTER: 'screen/com/cdl015/register',
	};

	@bean()
	export class ViewModel extends ko.ViewModel {

		roundingRules: KnockoutObservableArray<any>;
		selectedRuleCode: any = ko.observable(2);

		constructor() {
			super();
			var vm = this;
			vm.roundingRules = ko.observableArray([
				{ code: 1, name: vm.$i18n('CDL015_11') },
				{ code: 0, name: vm.$i18n('CDL015_12') }
			]);
			
			vm.$ajax(API.START).done((result: EmojiStateMngDto) => {

				if (result.manageEmojiState) {
					vm.selectedRuleCode(result.manageEmojiState);
				} else {
					vm.selectedRuleCode(0);
				}
			});
		}

		mounted() {
		  setTimeout(() => {
		    $('.switchbox-wrappers').focus();
		    }, 200);
			 
		}

		register() {
			var vm = this;

			var param: RegisterEmotionalSettingCommand = {
				cid: __viewContext.user.companyId,
				manageEmojiState: vm.selectedRuleCode(),
				emojiStateSettings: [
					{
						emijiName: vm.$i18n('CDL015_5'),
						emojiType: 4
					},
					{
						emijiName: vm.$i18n('CDL015_6'),
						emojiType: 3
					},
					{
						emijiName: vm.$i18n('CDL015_7'),
						emojiType: 2
					},
					{
						emijiName: vm.$i18n('CDL015_8'),
						emojiType: 1
					},
					{
						emijiName: vm.$i18n('CDL015_9'),
						emojiType: 0
					},

				]
			}
			vm.$ajax(API.REGISTER, param).done(() => {
				nts.uk.ui.dialog.info({ messageId: "Msg_15" })
					.then(() => {
						vm.closeDialog();
					});
			});
		}

		closeDialog() {
			const vm = this;
			vm.$window.close();
		}

	}

	interface EmojiStateMngDto {
		manageEmojiState: number;
	}

	interface RegisterEmotionalSettingCommand {
		cid: string;
		manageEmojiState: number;
		emojiStateSettings: EmojiStateDetailCommand[];
	}

	interface EmojiStateDetailCommand {
		emijiName: string;
		emojiType: number;
	}
}