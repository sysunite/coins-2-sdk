materialAdmin
  .controller('IndividualCtrl', function($scope, $filter, $sce, ngTableParams, MOCK, $q, Restangular, $stateParams) {
    var uri = $stateParams.uri;
    var individual = Restangular.one('individual')

    if (MOCK) {
      individual.get = function () {
        return $q(function (resolve) {
          resolve(
            { uri: 'uri 1',
              name: 'name 1',
              user: 'user 1',
              version: 'version 1',
              modifier: 'modifier 1',
              modificationDate: 'date 1',
              types: [
                {uri: 'classUri', label: 'classLabel', comment: 'classComment'},
                {uri: 'classUri2', label: 'classLabel2', comment: 'classComment2'}
              ],
              properties: [
                {key:'Has Name', datatype: 'string', value:'name 1'},
                {key:'Has URI',  datatype: 'string', value:'uri 1'},
              ],
              incomingRelations: [],
              outgoingRelations: [
                {relation: 'Has Relation', objectUri: 'object 2', objectName: 'object name'},
                {relation: 'Another Relation', objectUri: 'object 3', objectName: 'object name 3'}
              ]
            }
          )
        });
      }
    }

    individual.get({uri: uri}).then(function(result){
      $scope.individual = result;
    })
  })
