materialAdmin
  .controller('IndividualsCtrl', function($scope, $filter, $sce, ngTableParams, MOCK, $q, Restangular) {

    var ctrl = this
    var createTable = function() {
      //Filtering
      ctrl.tableFilter = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
          name: 'asc'        // initial sorting
        }
      }, {
        total: $scope.individuals.length, // length of data
        getData: function ($defer, params) {
          // use build-in angular filter
          var orderedData = params.filter() ? $filter('filter')($scope.individuals, params.filter()) : $scope.individuals;

          orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;

          this.uri = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
          this.name = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
          this.comment = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());

          params.total(orderedData.length); // set total for recalc pagination
          $defer.resolve(this.uri, this.label, this.comment);
        }
      })
    }


    var individuals = Restangular.all('individuals')

    if (MOCK) {
      individuals.getList = function () {
        return $q(function (resolve) {
          resolve([
            {uri: 'uri 1', name: 'name 1', user: 'user 1', version: 'version 1', modifier: 'modifier 1', modificationDate: 'date 1'},
            {uri: 'uri 2', name: 'name 2', user: 'user 2', version: 'version 2', modifier: 'modifier 2', modificationDate: 'date 2'}
          ])
        });
      }
    }

    individuals.getList().then(function(individuals) {
      $scope.individuals = individuals;
      createTable()
    })
  })
