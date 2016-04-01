materialAdmin
    .controller('FilesCtrl', function($scope, Restangular, $q, MOCK) {

        var attachments = Restangular.all('attachments')

        if (MOCK) {
            attachments.getList = function () {
                return $q(function (resolve) {
                    resolve([
                        {name: 'mock1', location: '/a/b/mock1.pdf'},
                        {name: 'mock2', location: '/a/b/mock2.pdf'}
                    ])
                });
            }
        }

        attachments.getList().then(function(files) {
            $scope.files = files;
        })
    })

    .filter('pathName', function(){
        return function(input) {
            return input.slice(input.lastIndexOf('/') + 1)
        }
    })