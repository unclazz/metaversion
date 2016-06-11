module MetaVersion {
   export function routeConfigurerFn($routeProvider :ng.route.IRouteProvider) {
		$routeProvider.when('/', {
			templateUrl: 'js/templates/index.html'
		}).when('/projects', {
			templateUrl: 'js/templates/projects.html'
		}).when('/projects/new', {
			templateUrl: 'js/templates/projects$projectId$edit.html'
		}).when('/projects/:projectId', {
			templateUrl: 'js/templates/projects$projectId.html'
		}).when('/projects/:projectId/link', {
			templateUrl: 'js/templates/projects$projectId$link.html'
		}).when('/projects/:projectId/edit', {
			templateUrl: 'js/templates/projects$projectId$edit.html'
		}).when('/projects/:projectId/delete', {
			templateUrl: 'js/templates/projects$projectId$delete.html'
		}).when('/projects/:projectId/commits', {
			templateUrl: 'js/templates/projects$projectId$commits.html'
		}).when('/projects/:projectId/virtualchangedpaths', {
			templateUrl: 'js/templates/projects$projectId$virtualchangedpaths.html'
		}).when('/projects/:projectId/virtualchangedpaths/add', {
			templateUrl: 'js/templates/projects$projectId$virtualchangedpaths$add.html'
		}).when('/projects/:projectId/virtualchangedpaths/:virtualChangedPathId/delete', {
			templateUrl: 'js/templates/projects$projectId$virtualchangedpaths$id$delete.html'
		}).when('/projects/:projectId/commits/:commitId/delete', {
			templateUrl: 'js/templates/projects$projectId$commits$commitId$delete.html'
		}).when('/projects/:projectId/changedpaths', {
			templateUrl: 'js/templates/projects$projectId$changedpaths.html'
		}).when('/projects/:projectId/parallels', {
			templateUrl: 'js/templates/projects$projectId$parallels.html'
		}).when('/repositories', {
			templateUrl: 'js/templates/repositories.html'
		}).when('/repositories/new', {
			templateUrl: 'js/templates/repositories$repositoryId$edit.html'
		}).when('/repositories/:repositoryId', {
			templateUrl: 'js/templates/repositories$repositoryId.html'
		}).when('/repositories/:repositoryId/commits', {
			templateUrl: 'js/templates/repositories$repositoryId$commits.html'
		}).when('/repositories/:repositoryId/edit', {
			templateUrl: 'js/templates/repositories$repositoryId$edit.html'
		}).when('/repositories/:repositoryId/delete', {
			templateUrl: 'js/templates/repositories$repositoryId$delete.html'
		}).when('/repositories/:repositoryId/commits/:commitId', {
			templateUrl: 'js/templates/repositories$repositoryId$commits$commitId.html'
		}).when('/repositories/:repositoryId/commits/:commitId/link', {
			templateUrl: 'js/templates/repositories$repositoryId$commits$commitId$link.html'
		}).when('/repositories/:repositoryId/commits/:commitId/changedpaths', {
			templateUrl: 'js/templates/repositories$repositoryId$commits$commitId$changedpaths.html'
		}).when('/users', {
			templateUrl: 'js/templates/users.html'
		}).when('/users/new', {
			templateUrl: 'js/templates/users$new.html'
		}).when('/users/:userId/edit', {
			templateUrl: 'js/templates/users$userId$edit.html'
		}).when('/users/:userId/delete', {
			templateUrl: 'js/templates/users$userId$delete.html'
		}).otherwise({
			redirectTo: '/'
		});
    }
    
    export function logConfigurerFn($logProvider :ng.ILogProvider) {
		$logProvider.debugEnabled(true);
    }
    export function userPasswordValidateFactoryFn() {
        const d :ng.IDirective = {};
        d.require = 'ngModel';
        d.link = function (scope, elm, attrs, ctrl) {
            const c = ctrl as ng.INgModelController;
            c.$parsers.push(function(viewValue :string) {
                const len = viewValue.length;
                if (len == 0 || len >= 8) {
                    c.$setValidity('userPassword', true);
                    return viewValue;
                } else {
                    c.$setValidity('userPassword', false);
                    return undefined;
                }
            });
        };
        return d;
    }

    export function regexValidateDirectiveFactoryFn() {
        const d :ng.IDirective = {};
        d.require = 'ngModel';
        d.link = function(scope, elm, attrs, ctrl) {
            const c = ctrl as ng.INgModelController;
            c.$parsers.push(function(viewValue :string) {
                try {
                    var re = new RegExp(viewValue);
                    c.$setValidity('regex', true);
                    return re.source;
                } catch (e) {
                    c.$setValidity('regex', false);
                    return undefined;
                }
            });
        };
        return d;
    }

    export function excerptFilterFactoryFn() {
        return function(text :string, length :number) {
            if (length === undefined) {
                length = 100;
            }
            if(text !== null && text !== undefined) {
                if(text.length > length) {
                    return text.substring(0, length - 3) + '...';
                } else {
                    return text;
                }
            }
            return undefined;
        };
    }
}