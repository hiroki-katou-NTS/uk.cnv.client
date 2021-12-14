module nts.uk.com.view.cps009.a {

    let __viewContext: any = window["__viewContext"] || {};

    __viewContext.ready(function() {
        __viewContext["viewModel"] = new viewmodel.ViewModel();
        __viewContext.bind(__viewContext["viewModel"]);

        if (window.top != window.self) {
            $("#header").css("display", "none");
            $(".goout").css("display", "none");
            $("#closeBtn").css("visibility", "visible");
        }
        
        $(document).on("keydown", 'input.ntsSearchBox.nts-editor.ntsSearchBox_Component', function(e) {
            if (e.keyCode == 13) {
                $("input.ntsSearchBox.nts-editor.ntsSearchBox_Component").focus();
            }
        });

        $(".ntsControl .nts-input").focusout(() => {
            $(".ntsControl .nts-input").css("padding-top", "5px !important");
            $(".ntsControl .nts-input").css("padding-bottom", "5px !important");
        });
    });
}






