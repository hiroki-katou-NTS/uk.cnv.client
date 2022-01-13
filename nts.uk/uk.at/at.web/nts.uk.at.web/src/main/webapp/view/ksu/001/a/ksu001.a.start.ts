module nts.uk.at.view.ksu001.a {
    let __viewContext: any = window["__viewContext"] || {};
    __viewContext.ready(function() {
        nts.uk.characteristics.restore("ksu001Data").done(function(data: any) {
            __viewContext.viewModel = {
                viewAB: new ksu001.ab.viewmodel.ScreenModel(data),
                viewAC: new ksu001.ac.viewmodel.ScreenModel(data),
                viewA: new ksu001.a.viewmodel.ScreenModel(data)
            };

            nts.uk.ui.block.grayout();
            __viewContext.viewModel.viewA.startPage().done(() => {
				document.getElementById("main-area").style.display = '';
                __viewContext.bind(__viewContext.viewModel);

                __viewContext.viewModel.viewA.setHeightMainArea();
                __viewContext.viewModel.viewA.setPositionButonA13A14A15();
                __viewContext.viewModel.viewA.setExAreaAgency();
                // fix cho truong hop khởi động ở mode height = fixed.
                if(data && data.gridHeightSelection == 2){
                   __viewContext.viewModel.viewA.saveHeightGridToLocal(); 
                }

                if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }
                
                if (__viewContext.viewModel.viewAC.listPageWkpIsEmpty == true) {
                    $('.ntsButtonTableButton').addClass('nowithContent');
                }

                if (!_.isNil(data)) {
                    if (data.updateMode == 'copyPaste') {
                        setTimeout(() => {
                            __viewContext.viewModel.viewA.setStyler();
                        }, 800);
                    }

                    if (data.disPlayFormat == 'shift') {
                        setTimeout(() => {
                            __viewContext.viewModel.viewAC.setStyleBtn();
                            __viewContext.viewModel.viewAC.setLinkSelected(data.shiftPalletUnit == 2 ? (data.shiftPalettePageNumberOrg - 1) : (data.shiftPalettePageNumberCom - 1));
                        }, 100);
                    }
                }
                
                $(window).resize(function() {
                    __viewContext.viewModel.viewA.setHeightMainArea();
                    __viewContext.viewModel.viewA.setPositionButonA14();
                    __viewContext.viewModel.viewA.setPositionButonA15();
                });

                nts.uk.ui.block.clear();
            });

            initEvent();
        });
    });
    
    function initEvent(): void {
        // Fire event.
        $("#multi-list").on('itemDeleted', (function(e: Event) {
            alert("Item is deleted in multi grid is " + e["detail"]["target"]);
        }));

        //A1_7_1 click btn7
        $('#A1_7_1').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom+3',
                of: $('#A1_7')
            }
        });

        $('#A1_7').click(function() {
            $('#A1_7_1').ntsPopup("toggle");
        });
        

        // A1_12_1 click btn12
        $('#A1_12_1').ntsPopup({
            position: {
                my: 'right top',
                at: 'right bottom+3',
                of: $('#A1_12')
            }
        });

        $('#A1_12').click(function() {
            $('#A1_12_1').ntsPopup("toggle");
        });

        $('#A4_1_popup').ntsPopup("init", {
            position: {
                my: 'left top',
                at: 'left bottom+3',
                of: $('#A4')
            },
            dismissible: false
        });
        
        $(window).on("mousedown.popup", function(e) {
            let control = $('#A4_1_popup');
            //let combo = $('.nts-combo-column-0');
            let combo = $('div.nts-column.nts-combo-column-0.name');

            if ($(e.target).is(combo[1]) || $(e.target).is(combo[2])) {
                console.log('not hide');
            } else if (!$(e.target).is(control) // Target isn't Popup
                && control.has(e.target).length === 0) { // Target isn't Trigger element
                hide(control);
            }
        });

        function hide(control: JQuery): JQuery {
            control.css({
                visibility: 'hidden',
                top: "-9999px",
                left: "-9999px"
            });
            return control;
        }
        
        $('#A4').click(function() {
            $('#A4_1_popup').ntsPopup("toggle");
        });
        
        
        //click btnA5
        $('#A5_1').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom+3',
                of: $('#A5')
            }
        });

        $('#A5').click(function() {
            $('#A5_1').ntsPopup("toggle");
        });

        
        //popup setting grid
        $('#A16').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom+3',
                of: $('.settingHeightGrid')
            }
        });

        $('.settingHeightGrid').click(function() {
            $('#A16').ntsPopup("toggle");
        });
    }
}