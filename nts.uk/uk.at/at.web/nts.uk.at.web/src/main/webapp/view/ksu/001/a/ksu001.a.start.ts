module ksu001.a {
    let __viewContext: any = window["__viewContext"] || {};

    __viewContext.ready(function() {
        __viewContext.viewModel = {
            viewO: new ksu001.o.viewmodel.ScreenModel(),
            viewA: new ksu001.a.viewmodel.ScreenModel()
        };
        __viewContext.bind(__viewContext.viewModel);
        initEvent();
    });

    function initEvent(): void {
        // Fire event.
        $("#multi-list").on('itemDeleted', (function(e: Event) {
            alert("Item is deleted in multi grid is " + e["detail"]["target"]);
        }));

        //popup 1
        $('#popup-area2').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom',
                of: $('.create')
            }
        });

        $('.create').click(function() {
            $('#popup-area2').toggle();
        });

        //popup 2
        $('#popup-area3').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom',
                of: $('.check')
            }
        });

        $('.check').click(function() {
            $('#popup-area3').toggle();
        });

        //popup 3
        $('#popup-area4').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom',
                of: $('.vacation')
            }
        });

        $('.vacation').click(function() {
            $('#popup-area4').toggle();
        });

        //popup 4
        $('#popup-area5').ntsPopup({
            position: {
                my: 'left top',
                at: 'left bottom',
                of: $('.setting')
            }
        });

        $('.setting').click(function() {
            $('#popup-area5').toggle();
        });
    }
}