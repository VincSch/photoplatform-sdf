/**
 * AuthService.
 * Login, register and passwordLost and passwordReset.
 */
angular.module('photoplatform').factory('AuthService', ['$http',
    function ($http) {

        var urlBase = '/api/user';

        return {
            /**
             * Login user
             * @param email
             * @param pw
             * @returns {HttpPromise}
             */
            login: function (email, pw) {
                return $http.post(urlBase + '/login', {
                    email: email,
                    password: pw
                });
            },

            /**
             * Register user
             * @param user
             * @returns {HttpPromise}
             */
            register: function (user) {
                return $http.post(urlBase + '/register', user);
            },

            /**
             * Lost password
             * @param email
             */
            passwordLost: function (email) {
                return $http.post(urlBase + '/password/lost', {email: email});
            },

            /**
             * Password reset
             * @param prf password reset form
             */
            passwordReset: function (pwd) {
                return $http.post(urlBase + '/password/reset', pwd);
            }
        }
    }]);