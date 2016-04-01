materialAdmin
    .controller('ClassesCtrl', function($scope, $filter, $sce, ngTableParams, MOCK, $q, Restangular) {

        var ctrl = this
        var createTable = function() {
          //Filtering
          ctrl.tableFilter = new ngTableParams({
            page: 1,            // show first page
            count: 10,          // count per page
            sorting: {
              uri: 'asc'        // initial sorting
            }
          }, {
            total: $scope.classes.length, // length of data
            getData: function ($defer, params) {
              // use build-in angular filter
              var orderedData = params.filter() ? $filter('filter')($scope.classes, params.filter()) : $scope.classes;

              orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;

              this.uri = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
              this.name = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
              this.comment = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());

              params.total(orderedData.length); // set total for recalc pagination
              $defer.resolve(this.uri, this.label, this.comment);
            }
          })
        }


        var classes = Restangular.all('classes')

        if (MOCK) {
          classes.getList = function () {
            return $q(function (resolve) {
              resolve([
                {uri: 'uri 1', label: 'label 1', comment: 'comment 1'},
                {uri: 'uri 2', label: 'label 2', comment: 'comment 2'}
              ])
            });
          }
        }

        classes.getList().then(function(classes) {
          $scope.classes = classes;
          createTable()
        })
    })
