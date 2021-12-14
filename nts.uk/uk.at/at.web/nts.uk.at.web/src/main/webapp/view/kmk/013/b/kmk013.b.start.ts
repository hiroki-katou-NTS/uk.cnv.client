module nts.uk.at.view.kmk013.b_ref {
    __viewContext.ready(function() {
        var screenModel = new b_ref.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $( "#B3_3" ).focus();
        });
    });
}
