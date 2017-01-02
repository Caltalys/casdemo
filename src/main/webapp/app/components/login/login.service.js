(function() {
    'use strict';

    angular
        .module('casdemoApp')
        .factory('LoginService', LoginService);

    LoginService.$inject = ['$uibModal','$window'];

    function LoginService ($uibModal,$window) {
        var service = {
            open: open
        };

        var modalInstance = null;
        var resetModal = function () {
            modalInstance = null;
        };

        return service;

        function open () {
            $window.location.href = 'app/login?postMessage';
        }
    }
})();
