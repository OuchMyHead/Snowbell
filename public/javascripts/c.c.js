$(window).bind("load", function() {

    jQuery('a.colorbox').colorbox({
        maxHeight: "80%",
        rel:'thumb',
        slideshow: true,
        slideshowSpeed: 4000,
        transition: "none"
    });

    $("#account textarea").css('overflow', 'hidden').autogrow()


    /*
    //smooth scroll
    $(function() {

        var $el, leftPos, newWidth,
        active = "#nav > .pages > li.active",
        $mainNav = $("#nav > ul");

        $mainNav.append("<li id='magic-line'></li>");
        var $magicLine = $("#magic-line");

        initialiseMenu();
        $magicLine
            .width($(".active").width())
            .css("left", $(".active a").position().left)
            .data("origLeft", $magicLine.position().left)
            .data("origWidth", $magicLine.width());

        /**
         * http://css-tricks.com/jquery-magicline-navigation/
         *
        function animateMenu( $el ){
            $(active).removeClass("active");
            leftPos = $el.position().left;
            newWidth = $el.width();
            $magicLine.stop().animate({
                left: leftPos,
                width: newWidth
            },800);
            $el.addClass("active");
        };

        /**
         * http://tympanus.net/codrops/2010/06/02/smooth-vertical-or-horizontal-page-scrolling-with-jquery/
         *
        function slideContent( $anchor ){
            $('html, body').stop().animate({
                scrollLeft: $($anchor.attr('href')).offset().left
            }, 800, function() {
                window.location.hash = $anchor.attr('href');
            });
        };

        function initialiseMenu(){
            if( window.location.hash ){
                $(active).removeClass("active");
                $anchor = $("#nav > .pages > li > a[href='"+window.location.hash+"']")
                $anchor.parent("li").addClass("active");
            }
        }

        $(function() {
            $('#nav > ul a').bind('click',function(event){
                event.preventDefault();
                var $anchor = $(this);
                slideContent( $anchor );
                animateMenu( $anchor.parent("li") );
            });
        });
    });
    */
});