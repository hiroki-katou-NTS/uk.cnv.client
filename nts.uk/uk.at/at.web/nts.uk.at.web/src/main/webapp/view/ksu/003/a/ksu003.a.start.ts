module nts.uk.at.view.ksu003.a {
	let __viewContext: any = window["__viewContext"] || {};
	
	__viewContext.ready(function() {
		nts.uk.characteristics.restore("USER_KSU003_INFOR").done(function(data : any) {
			__viewContext.viewModel = {
			viewmodelA: new viewmodel.ScreenModel(data),
            viewmodelAb: new nts.uk.at.view.ksu003.ab.viewmodel.ScreenModel()
			}
			nts.uk.ui.block.grayout();
			__viewContext.viewModel.viewmodelA.startPage().done(function() {
				__viewContext.bind(__viewContext.viewModel);
				$('#ui-area').css('display','');
				$(window).resize(function() {
					__viewContext.viewModel.viewmodelA.setPositionButonDownAndHeightGrid();
				});
				
				__viewContext.viewModel.viewmodelAb.startPage().done(function() {
					$('#screen-Ab1').css('display','');
					$('#screen-Ab2').css('display','');
				});
			});
			initEvent();
			initEvent2();
			initEvent3();
		});
	});
	
	function initEvent(): void {
		//click btnA5
		$('#A5_1').ntsPopup({
			position: {
				my: 'left top',
				at: 'left bottom+3',
				of: $('#note')
			}
		});

		$('#note').click(function() {
			$('#A5_1').ntsPopup("toggle");
		});
	}
	
	function initEvent3(): void {
		//click btnA5
		$('#A14').ntsPopup({
			position: {
				my: 'left top',
				at: 'left bottom+3',
				of: $('#settingTimeGrid')
			},
			showOnStart: false,
			dismissible: false
		});

		$('#settingTimeGrid').click(function() {
			$('#A14').ntsPopup("toggle");
		});
		
		$(".ui-igcombo-list").click(function() {
			$('#A14').ntsPopup("hide");
		});
		
		//$(window).click(function(e) {
			//if (e.target.classList[1] != "nts-combo-column-0" && e.target.classList[1] != "nts-combo-column-1" && e.target.id != "settingTimeGrid") {
			//	$('#A14').ntsPopup("hide");
			//}
		//});
	}

	function initEvent2(): void {
		//click btnA5
		$('#A3_4').ntsPopup({
			position: {
				my: 'left top',
				at: 'left bottom+3',
				of: $('#note-sort'),
			},
			showOnStart: false,
			dismissible: false
		});

		$('#note-sort').click(function() {
			$('#A3_4').ntsPopup("toggle");
		});

		$(".ui-igcombo-list").click(function() {
			$('#A3_4').ntsPopup("hide");
		});

		$(window).click(function(e) {
			if (e.target.classList[1] != "nts-combo-column-0" && e.target.classList[1] != "nts-combo-column-1" && e.target.id != "note-sort") {
				$('#A3_4').ntsPopup("hide");
			}
		});

	}
	$(window).resize(function() {
		let self = this;
		if (window.innerHeight < 700) {
			$(".close").css({ "margin-right": 45 + 'px !important' });
			$("#note-color").css({ "margin-right": 57 + 'px !important' });
		} else {
			$("hr-row2").css({ "width": 1237 + 'px' });
		}
	});
}