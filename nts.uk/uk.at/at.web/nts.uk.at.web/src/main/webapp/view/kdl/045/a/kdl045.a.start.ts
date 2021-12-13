module nts.uk.at.view.kdl045.a {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            setTimeout(() => {
                screenModel.workStyle(screenModel.informationStartup.workStyle);
                screenModel.workStyle.valueHasMutated();
            }, 200);

            $(".popup-area1").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup1"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup1').click(function() {
                $('.popup-area1').ntsPopup("toggle");
            });

            //atWork 1
            $(".popup-atwork1").ntsPopup({
                position: {
//                    my: "left+10 top-120",
//                    at: "right top",
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-atwork1"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-atwork1').hover(function() {
                $('.popup-atwork1').ntsPopup("toggle");
            });

            //atWork 2
            $(".popup-atwork2").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-atwork2"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-atwork2').hover(function() {
                $('.popup-atwork2').ntsPopup("toggle");
            });

            //offWork 1
            $(".popup-offwork1").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-offwork1"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-offwork1').hover(function() {
                $('.popup-offwork1').ntsPopup("toggle");
            });

            //offWork 2
            $(".popup-offwork2").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-offwork2"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-offwork2').hover(function() {
                $('.popup-offwork2').ntsPopup("toggle");
            });

            //privatetime 
            $(".popup-privatetime").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-privatetime"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-privatetime').hover(function() {
                $('.popup-privatetime').ntsPopup("toggle");
            });

            //uniontime 
            $(".popup-uniontime").ntsPopup({
                position: {
                    my: "left top",
                    at: "left bottom",
                    of: ".show-popup-uniontime"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-uniontime').hover(function() {
                $('.popup-uniontime').ntsPopup("toggle");
            });

            //privatetime A8_6_7
            $(".popup-privatetimea867").ntsPopup({
                position: {
                    my: "right top",
                    at: "right bottom",
                    of: ".show-popup-privatetimea867"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-privatetimea867').hover(function() {
                $('.popup-privatetimea867').ntsPopup("toggle");
            });

            //uniontime A8_6_7
            $(".popup-uniontimea867").ntsPopup({
                position: {
                    my: "right top",
                    at: "right bottom",
                    of: ".show-popup-uniontimea867"
                },
                showOnStart: false,
                dismissible: true
            });

            $('.show-popup-uniontimea867').hover(function() {
                $('.popup-uniontimea867').ntsPopup("toggle");
            });
            setTimeout(function() {
                if(screenModel.canModified){
                    $('#a4-2').focus();    
                }else{
                    $('#closeDialog-2').focus();    
                }
            }, 100);

        });//end screenModel
    });
}
