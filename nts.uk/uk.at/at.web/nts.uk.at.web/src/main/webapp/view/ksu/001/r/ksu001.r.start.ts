module nts.uk.at.view.ksu001.r {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            setTimeout(function() {
                $("table tbody tr td:nth-child(1):contains(土)").css("background-color", "#8bd8ff");
                $("table tbody tr td:nth-child(1):contains(日)").css("background-color", "#fabf8f");
                $("table tbody tr td:nth-child(1)").css("color", "#404040");
                $("table tbody tr td:nth-child(1):contains(土)").css("color", "#0000ff");
                $("table tbody tr td:nth-child(1):contains(日)").css("color", "#ff0000");
            }, 200);

        }).then(function() {
            $('[tabindex= 5]').focus();
        });
    });
}