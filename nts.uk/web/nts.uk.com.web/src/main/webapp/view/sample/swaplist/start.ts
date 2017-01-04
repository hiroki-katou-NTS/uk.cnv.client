__viewContext.ready(function() {

    class ScreenModel {
        itemsSwap: KnockoutObservableArray<ItemModel>;
        columns: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
        currentCodeListSwap: KnockoutObservableArray<any>;
        textArea: KnockoutObservable<any>;
        divValue: KnockoutObservable<any>;

        constructor() {

            this.itemsSwap = ko.observableArray([
                new ItemModel('001', '基本給', "description 1"),
                new ItemModel('150', '役職手当', "description 2"),
                new ItemModel('ABC', '基12本ghj給', "description 3"),
                new ItemModel('002', '基本給', "description 4"),
                new ItemModel('153', '役職手当', "description 5"),
                new ItemModel('AB4', '基12本ghj給', "description 6"),
                new ItemModel('003', '基本給', "description 7"),
                new ItemModel('155', '役職手当', "description 8"),
                new ItemModel('AB5', '基12本ghj給', "description 9")
            ]);

            this.columns = ko.observableArray([
                { headerText: 'コード', prop: 'code', width: 100 },
                { headerText: '名称', prop: 'name', width: 200 },
                { headerText: '説明', prop: 'description', width: 200 }
            ]);
            this.textArea = ko.observable("");
            this.divValue = ko.observable("");
            $("#input-text").keypress((event) => {
                var count = $("#input-content-area").attr("span-count");
                var index = 0;
                var lineIndex = 0;
                var appendCount = false;
                var startLine = "";
                if (count !== undefined && count !== null) {
                    index = parseInt(count) + 1;
                }else{
                    startLine = "<span class='editor-line'></span>";
                    index = 1;    
                }
                if(nts.uk.text.isNullOrEmpty(startLine) && nts.uk.text.isNullOrEmpty(startLine)
                    && event.keyCode === 13){
                    startLine = "<span class='editor-line'></span>";
                }
                this.divValue(this.divValue() + startLine);
                var start = $("#input-text")[0].selectionStart;
                var end = $("#input-text")[0].selectionEnd;
                if (nts.uk.text.allHalfAlphanumeric(event.key)) {
                    var lastSpan = $("#input-content-area .editor-line span:last-child");
                    if (lastSpan.length === 0) {
                        var y = splice(this.divValue(), "<span id='span-" + index + "'>" + event.key + "</span>", 1);
                        appendCount = true;
                        this.divValue(y);
                    } else if (nts.uk.text.allHalfAlphanumeric(lastSpan.html())) {
                        var y = $("#input-content-area").html();
                        y = splice(y, event.key, 2);
                        this.divValue(y);
                    } else {
                        var y = splice(this.divValue(), "<span id='span-" + index + "'>" + event.key + "</span>", 1);
                        appendCount = true;
                        this.divValue(y);
                    }
                } else {
                    var lastSpan = $("#input-content-area .editor-line span:last-child");
                    if (lastSpan.length === 0) {
                        var y = splice(this.divValue(), "<span id='span-" + index + "' class='special-char'>" + event.key + "</span>", 1);
                        appendCount = true;
                        this.divValue(y);
                    } else if (!nts.uk.text.allHalfAlphanumeric(lastSpan.html())) {
                        var y = $("#input-content-area").html();
                        y = splice(y, event.key, 2);
                        this.divValue(y);
                    } else {
                        var y = splice(this.divValue(), "<span id='span-" + index + "' class='special-char'>" + event.key + "</span>", 1);
                        appendCount = true;
                        this.divValue(y);
                    }
                    appendCount = true;
                    this.divValue(y);
                }
                if(appendCount){
                    $("#input-content-area").attr("span-count", index);   
                    //http://www.w3schools.com/jsref/event_key_keycode.asp 
                }
            });
            this.currentCodeListSwap = ko.observableArray([]);
        }

    }
    function splice (original, str, i) {
        var idx = (original.length - 7*i);
        return original.slice(0, idx) + str + original.substr(idx, 7*i);
    };

    class ItemModel {
        code: string;
        name: string;
        description: string;
        constructor(code: string, name: string, description: string) {
            this.code = code;
            this.name = name;
            this.description = description;
        }
    }


    this.bind(new ScreenModel());

});
