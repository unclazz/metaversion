(function(angular) {
	// mvCommonモジュールを追加
	var mvCommonModule = angular.module('mvCommon', ['ngResource', 'ui.bootstrap']);
	
	// mvCommonにentitiesファクトリを追加
	mvCommonModule.factory('entities', function($resource) {
		var entities = {};
		var pagingParams = {page: 1, size: 25};
		var suggestParams = {like: "", size: 25};
		var entity = function(entityName, urlPattern, urlParams, queryParams) {
			var xformResp = function(data) {
				var paginated = angular.fromJson(data);
				paginated.list = paginated.list.map(function (item) {
					$log.debug(entityName);
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
		// ProjectSvnCommitエンティティのためのResourceオブジェクトを作成
		entity("ProjectCommit", "api/projects/:projectId/commits/:commitId",
				{projectId: "@projectId", commitId: "@commitId"}, pagingParams);
		// ProjectChagedPathエンティティのためのResourceオブジェクトを作成
		entity("ProjectChangedPath", "api/projects/:projectId/changedpaths",
				{projectId: "@projectId"}, pagingParams);
		
		// サジェスト用のResourceオブジェクトを作成
		entity("PathName", "api/pathnames", {}, suggestParams);
		entity("ProjectName", "api/projectnames", {}, suggestParams);
		
		return entities;
	});
	
	// mvApiTesterモジュールを追加
	angular.module('mvApiTester', ['ngResource', 'ui.bootstrap']);
	
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
