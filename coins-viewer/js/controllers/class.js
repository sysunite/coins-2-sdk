materialAdmin
  .controller('ClassCtrl', function($scope, $filter, $sce, ngTableParams, MOCK, $q, Restangular, $stateParams) {
    var uri = $stateParams.uri;
    var classObject = Restangular.one('class')

    if (MOCK) {
      classObject.get = function () {
        return $q(function (resolve) {
          resolve(
            { uri: 'uri 1',
              label: 'name 1',
              comment: 'comment 1',
              superclasses: [
                {uri: 'classUri', label: 'classLabel'},
                {uri: 'classUri2', label: 'classLabel2'}
              ],
              subclasses: [
                {uri: 'classUri', label: 'classLabel'},
                {uri: 'classUri2', label: 'classLabel2'}
              ],
              individuals: [
                {uri: 'uri 1',name: 'name 1', user: 'user 1', version: 'version 1', modifier: 'modifier 1', modificationDate: 'date 1'},
                {uri: 'uri 2',name: 'name 2', user: 'user 2', version: 'version 2', modifier: 'modifier 2', modificationDate: 'date 2'}
              ]
            }
          )
        });
      }
    }

    classObject.get({uri: uri}).then(function(result){
      $scope.class = result;
    })
  })
