/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.at.ksu002.a {
	import m = nts.uk.ui.at.ksu002.memento;
	import c = nts.uk.ui.at.ksu002.calendar;

	const API = {
		UNAME: '/sys/portal/webmenu/username'
	};

	const memento: m.Options = {
		size: 20,
		/*replace: function (data: c.DayData[], replacer: c.DayData[]) {
			_.each(data, (d: c.DayData) => {

				d.
			});
		}*/
	};

	const defaultBaseDate = (): c.DateRange => ({
		begin: moment().startOf('month').toDate(),
		finish: moment().endOf('month').toDate()
	});

	@bean()
	export class ViewModel extends ko.ViewModel {
		currentUser!: KnockoutComputed<string>;

		baseDate: KnockoutObservable<c.DateRange | null> = ko.observable(defaultBaseDate());
		schedules: MementoObservableArray<c.DayData> = ko.observableArray([]).extend({ memento }) as any;

		created() {
			const vm = this;
			const bussinesName: KnockoutObservable<string> = ko.observable('');

			vm.currentUser = ko.computed({
				read: () => {
					const bName = ko.unwrap(bussinesName);

					return `${vm.$i18n('KSU002_1')}&nbsp;&nbsp;&nbsp;&nbsp;${vm.$user.employeeCode}&nbsp;&nbsp;${bName}`;
				},
				owner: vm
			});

			vm.$ajax('com', API.UNAME)
				.then((name: string) => bussinesName(name));

			vm.baseDate
				.subscribe((data) => {
					// call to api and get data
					// vm.schedules.reset(ko.unwrap(vm.schedules));
				});
		}

		mounted() {
			const vm = this;

			vm.$nextTick(() => vm.schedules.reset());

			$(vm.$el).find('[data-bind]').removeAttr('data-bind');
		}

		undoOrRedo(action: 'undo' | 'redo') {
			const vm = this;

			if (action === 'undo') {
				vm.schedules.undo();
			} else if (action === 'redo') {
				vm.schedules.redo();
			}
		}

		clickDayCell(type: string, data: c.DayData) {
			const vm = this;

			const wrap: c.DayData[] = ko.toJS(vm.schedules);

			const exist = _.find(wrap, f => moment(f.date).isSame(data.date, 'date'));

			if (exist) {
				exist.data = Date.now();
			}

			vm.schedules.memento(wrap);
		}
	}
}