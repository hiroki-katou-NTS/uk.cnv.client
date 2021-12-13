module nts.uk.at.view.kmk013.b_ref {
    __viewContext.ready(function() {
        var screenModel = new b_ref.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $(".tabs-list").ready(() => $("#B4_9").appendTo(".tabs-list"));
            $( "#B3_3" ).focus();
        });
    });
}
