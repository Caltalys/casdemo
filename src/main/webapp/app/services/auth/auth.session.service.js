(function() {
    'use strict';

    angular
        .module('casdemoApp')
        .factory('AuthServerProvider', AuthServerProvider);

    AuthServerProvider.$inject = ['$http', '$localStorage' ];

    function AuthServerProvider ($http, $localStorage ) {
        var service = {
            getToken: getToken,
            hasValidToken: hasValidToken,
            login: login,
            logout: logout
        };

        return service;

        function getToken () {
            var token = $localStorage.authenticationToken;
            return token;
        }

        function hasValidToken () {
            var token = this.getToken();
            return !!token;
        }

        function login (credentials) {
            var data = 'j_username=' + encodeURIComponent(credentials.username) +
                '&j_password=' + encodeURIComponent(credentials.password) +
                '&remember-me=' + credentials.rememberMe + '&submit=Login';

            return $http.jsonp('app/login?callback=JSON_CALLBACK')
                .success(function (response, status) {
                    //console.log("try login with callback request " + status);
                    //Principal.authenticate(response.data);
                    return response;
                }).error(function () {
                    console.log("simple login failed - start window.open + postMessage");
                    //$rootScope.modalOpened = $modal.open({ templateUrl: 'scripts/app/account/login/loginModal.html', controller:
                    // 'LoginModalController', backdrop: false });
                    return $q.reject();
                });
        }

        function logout () {


            // logout from the server
            $http.post('api/logout').success(function (response) {
                delete $localStorage.authenticationToken;
                // to get a new csrf token call the api
                $http.get('api/account');
                return response;
            });

        }
    }
})();
