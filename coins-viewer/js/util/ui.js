materialAdmin

    // =========================================================================
    // Malihu Scroll - Custom Scroll bars
    // =========================================================================

    .service('scrollService', function() {
      var ss = {};
      ss.malihuScroll = function scrollBar(selector, theme, mousewheelaxis) {
        $(selector).mCustomScrollbar({
          theme: theme,
          scrollInertia: 100,
          axis:'yx',
          mouseWheel: {
            enable: true,
            axis: mousewheelaxis,
            preventDefault: true
          }
        });
      }

      return ss;
    })

    //On Custom Class
    .directive('cOverflow', ['scrollService', function(scrollService){
        return {
            restrict: 'C',
            link: function(scope, element) {

                if (!$('html').hasClass('ismobile')) {
                    scrollService.malihuScroll(element, 'minimal-dark', 'y');
                }
            }
        }
    }])

    // =========================================================================
    // WAVES
    // =========================================================================

    // For .btn classes
    .directive('btn', function(){
        return {
            restrict: 'C',
            link: function(scope, element) {
                if(element.hasClass('btn-icon') || element.hasClass('btn-float')) {
                    Waves.attach(element, ['waves-circle']);
                }

                else if(element.hasClass('btn-light')) {
                    Waves.attach(element, ['waves-light']);
                }

                else {
                    Waves.attach(element);
                }

                Waves.init();
            }
        }
    })
