(function(angular) {
	var app = angular.module('app', ['ngResource', 'ui.bootstrap', 'ngRoute']);
	
	app.factory('entities', function($log, $resource) {
		var entities = {};
		var pagingParams = {page: 1, size: 25};
		var suggestParams = {like: '', size: 25};
		var entity = function(entityName, urlPattern, urlParams, queryParams) {
			var xformResp = function(data) {
				var paginated = angular.fromJson(data);
				paginated.list = paginated.list.map(function (item) {
					return new entities[entityName](item)
				});
				return paginated;
			};
			var paginatedQuery = {
				method: 'GET',
				params: queryParams,
				isArray:false,
				transformResponse : xformResp
			};
			var customActions = {
				'query' : paginatedQuery
			};
			entities[entityName] = $resource(urlPattern,urlParams, customActions);
		};
		var suggest = function(entityName, urlPattern, urlParams, queryParams) {
			var paginatedQuery = {
				method: 'GET',
				params: queryParams,
				isArray: true
			};
			var customActions = {
				'query' : paginatedQuery
			};
			entities[entityName] = $resource(urlPattern,urlParams, customActions);
		};
		
		// UserエンティティのためのResourceオブジェクトを作成
		entity("User", "api/users/:id", {id: "@id"}, pagingParams);

		// SvnRepositoryエンティティのためのResourceオブジェクトを作成
		entity("Repository", "api/repositories/:id", {id: "@id"}, pagingParams);
		// SvnCommitエンティティのためのResourceオブジェクトを作成
		// ＊クエリ用パラメータのデフォルトとしてunlinkedプロパティを追加している
		entity("RepositoryCommit", "api/repositories/:repositoryId/commits", 
				{repositoryId: "@repositoryId"}, angular.extend({unlinked: false}, pagingParams));
		// SvnCommitPathエンティティのためのResourceオブジェクトを作成
		entity("RepositoryCommitChangedPath", "api/repositories/:repositoryId/commits/:commitId/changedpaths", 
				{repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
		
		// ProjectエンティティのためのResourceオブジェクトを作成
		entity("Project", "api/projects/:id", {id: "@id"}, pagingParams);
		entity("ProjectStats", "api/projectstats/:id", {id: "@id"}, pagingParams);
		// ProjectSvnCommitエンティティのためのResourceオブジェクトを作成
		entity("ProjectCommit", "api/projects/:projectId/commits/:commitId",
				{projectId: "@projectId", commitId: "@commitId"}, pagingParams);
		// ProjectChagedPathエンティティのためのResourceオブジェクトを作成
		entity("ProjectChangedPath", "api/projects/:projectId/changedpaths",
				{projectId: "@projectId"}, pagingParams);
		
		// サジェスト用のResourceオブジェクトを作成
		suggest("PathName", "api/pathnames", {}, suggestParams);
		suggest("ProjectName", "api/projectnames", {}, suggestParams);
		
		return entities;
	})
	.factory('paths', function($log, $location) {
		var queryToObject = function(defaultParams) {
			var params = defaultParams === undefined ? {} : angular.copy(defaultParams);
			var search = $location.search();
			if (search === undefined) {
				return params;
			}
			for (var k in params) {
				if (k in search) {
					var v = params[k];
					if (angular.isNumber(v)) {
						params[k] = search[k] - 0;
					} else if (angular.isString(v)) {
						params[k] = search[k] + '';
					} else if (v === true || v === false) {
						params[k] = search[k] ? true : false;
					} else {
						params[k] = search[k];
					}
				}
			}
			return params;
		};
		var watch = function(scopeInstance, callback) {
			scopeInstance.$watch(function() {
				return $location.search().page;
			}, function(page) {
				callback(page === undefined ? 1 : page - 0);
			});
		}
		var stringToPath = function(path, data) {
			$location.path(path);
			$location.search(data);
		};
		var objectToQuery = function(data) {
			$location.search(data);
		};
		var entryToQuery = function(key, value) {
			var q = $location.search();
			q[key] = value;
			$location.search(q);
		};
		var pathToIds = function() {
			var vars = {};
			var url = $location.absUrl();
			var res = null;
			if (res = /\/users\/(\d+)/.exec(url)) {
				vars.userId = res[1] - 0;
			}
			if (res = /\/projects\/(\d+)/.exec(url)) {
				vars.projectId = res[1] - 0;
			}
			if (res = /\/repositories\/(\d+)/.exec(url)) {
				vars.repositoryId = res[1] - 0;
			}
			if (res = /\/commits\/(\d+)/.exec(url)) {
				vars.commitId = res[1] - 0;
			}
			return vars;
		};
		
		return {
			queryToObject: queryToObject,
			objectToQuery: objectToQuery,
			entryToQuery: entryToQuery,
			stringToPath: stringToPath,
			pathToIds: pathToIds,
			watchPage: watch
		};
	});
	
	angular.module('app')
	.config(function($routeProvider){
		$routeProvider.when('/', {
			templateUrl: 'js/templates/index.html'
		}).when('/projects', {
			templateUrl: 'js/templates/projects.html'
		}).when('/projects/:projectId', {
			templateUrl: 'js/templates/projects$projectId.html'
		}).when('/repositories', {
			templateUrl: 'js/templates/repositories.html'
		}).when('/repositories/:repositoryId', {
			templateUrl: 'js/templates/repositories$repositoryId.html'
		}).otherwise({
			redirectTo: '/'
		});
	})
	.config(function($logProvider) {
		$logProvider.debugEnabled(true);
	})
	.controller('parent', function($scope, $location, $log) {
		$scope.navItems = {
				projects: false,
				repositories: false,
				users: false
		};
		$scope.$watch(function() {
			return $location.path();
		}, function(path) {
			for (var k in $scope.navItems) {
				$scope.navItems[k] = path.endsWith(k);
			}
		});
	})
	.controller('index', function($log, $scope, entities, paths) {
		$scope.projectNames = function (partialName) {
			return entities.ProjectName.query({like: partialName}).$promise;
		};
		$scope.submit = function() {
			paths.go('projects', {like: $scope.like});
		};
	})
	.controller('projects', function($log, $scope, $location, entities, paths) {
		
		$scope.cond = paths.queryToObject({pathbase: false, like: ''});
		$scope.projectOrPathNames = function (partialName) {
			if ($scope.cond.pathbase) {
				return entities.PathName.query({like: partialName}).$promise;
			} else {
				return entities.ProjectName.query({like: partialName}).$promise;
			}
		};

		paths.watchPage($scope, function(p) {
			$scope.cond.page = p;
			entities.Project.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	.controller('projects$rojectId', function($log, $scope, $location, entities, paths) {
		$scope.project = entities.ProjectStats.get({id: paths.pathToIds().projectId});
	})
	.controller('repositories', function($log, $scope, $location, entities, paths) {
		
		$scope.cond = paths.queryToObject();
		paths.watchPage($scope, function(p) {
			$scope.cond.page = p;
			entities.Repository.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	.controller('repositories$repositoryId', function($log, $scope, $location, entities, paths) {
		$scope.repository = entities.Repository.get({id: paths.pathToIds().repositoryId});
	});
	
	
	// mvApiTesterモジュールを追加
	angular.module('mvApiTester', ['mvCommon', 'ngResource', 'ui.bootstrap']);
	
	// mvApiTesterにmainコントローラを追加
	angular.module('mvApiTester')
	.controller("main", function($scope, $location, $http, $filter, $log){
		var REQUEST_METHODS = ['GET', 'POST', 'PUT', 'DELETE'];
		var REQUEST_PREFIX = null;
		
		if ($location.protocol() === 'file') {
			var absUrl = $location.absUrl();
			var restapiEndpointIndex = absUrl.indexOf('/templates/restapi-tester.html');
			REQUEST_PREFIX = $location.absUrl().slice(0, restapiEndpointIndex) + '/static';
		} else {
			var absUrl = $location.absUrl();
			var restapiEndpointIndex = absUrl.indexOf('/apitester');
			REQUEST_PREFIX = $location.absUrl().slice(0, restapiEndpointIndex);
		}
		
		$scope.requestPrefix = REQUEST_PREFIX;
		$scope.requestMethod = REQUEST_METHODS[0];
		$scope.requestPath = '';
		$scope.requestBody = '';
		$scope.responseBody = '';
		
		$scope.requestSubmitClick = function() {
			var conf = {};
			conf.method = $scope.requestMethod;
			conf.url = $scope.requestPath;
			if (conf.method === 'POST' || conf.method === 'PUT') {
				conf.data = $scope.requestBody;
			}
			
			$http(conf).success(function(data, status, headers, conf){
				$scope.responseStatus = status;
				if (typeof data === 'string') {
					$scope.responseBody = data;
				} else {
					$scope.responseBody = $filter('json')(data);
				}
			}).error(function(data, status, headers, conf){
				$scope.responseStatus = status;
				if (typeof data === 'string') {
					$scope.responseBody = data;
				} else {
					$scope.responseBody = $filter('json')(data);
				}
			});
		};
		
		$scope.requestPathChange = function() {
			if ($scope.requestPath.length > 0 && $scope.requestPath.slice(0,1) === '/') {
				$scope.submitDisabled = false;
			} else {
				$scope.submitDisabled = true;
			}
		};
		
		$scope.requestPathChange();
	});

})(angular);
