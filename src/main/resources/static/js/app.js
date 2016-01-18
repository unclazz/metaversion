(function(angular) {
	// mvCommonモジュールを追加
	var mvCommonModule = angular.module('mvCommon', ['ngResource', 'ui.bootstrap']);
	
	// mvCommonにentitiesファクトリを追加
	mvCommonModule.factory('entities', function($log, $resource) {
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
	.factory('pathvars', function($log, $location) {
		var vars = {};
		var url = $location.absUrl();
		var res = null;
		if (res = /\/users\/(\d+)/.exec(url)) {
			vars.userId = res[1];
		}
		if (res = /\/projects\/(\d+)/.exec(url)) {
			vars.projectId = res[1];
		}
		if (res = /\/repositories\/(\d+)/.exec(url)) {
			vars.repositoryId = res[1];
		}
		if (res = /\/commits\/(\d+)/.exec(url)) {
			vars.commitId = res[1];
		}
		return vars;
	})
	.factory('params', function($log, $location) {
		var parse = function(path) {
			// 読み取った結果を格納するオブジェクト
			var result = {};
			// パス文字列に`?`が含まれているかチェック
			if (path.indexOf('?') == -1) {
				// 含まれていない場合読み取るべき情報はないので処理は終了
				return result;
			}
			// `?`より後の文字列を`&`を区切り文字としてパス断片へと分割
			// 個々の断片ごとに処理を行う
			angular.forEach(path.replace(/^.*[?]/, '').split('&'), function(value) {
				// 断片文字列の長さをチェック
				if (value.length == 0) {
					// 空文字列の場合は処理をスキップ
					return;
				}
				// 一時変数を宣言
				var paramName, paramValue;
				// 断片文字列に`=`が含まれるかチェック
				if (value.indexOf('=') == -1) {
					// 含まれない場合パラメータ値は`true`
					paramName = value;
					paramValue = true;
				} else {
					// 含まれた場合それを区切り文字として断片化
					var nameAndValue = value.split('=', 2);
					// 1つめの要素がパラメータ名で2つめの要素がパラメータ値
					paramName = nameAndValue[0];
					paramValue = decodeURIComponent(nameAndValue[1]);
					// パラメータ値の文字列のフォーマットを確認
					if (paramValue.match(/^[1-9][0-9]*([.][0-9]*)?$/)) {
						// 数値と見做せるものであれば数値化
						paramValue = paramValue - 0;
					} else if (paramValue === 'true') {
						// `true`という文字列であれば真偽値の`true`に変換
						paramValue = true;
					} else if (paramValue === 'false') {
						// `false`という文字列であれば真偽値の`false`に変換
						paramValue = true;
					}
				}
				// 同じパラメータ名が既知のものかどうかチェック
				if (this.hasOwnProperty(paramName)) {
					// 既知の場合
					// そのパラメータが配列かどうかをチェック
					if (angular.isArray(this[paramName])) {
						// 配列であればその末尾に今回のパラメータ値を追加
						this[paramName].push(paramValue);
					} else {
						// 配列でなければ配列化して今回のパラメータ値も追加
						this[paramName] = [this[paramName], paramValue];
					}
				} else {
					// 未知のものであれば単純に追加を行う
					this[paramName] = paramValue;
				}
			}, result);
			return result;
		};
		var load = function(defaultParams) {
			var params = defaultParams === undefined ? {} : angular.copy(defaultParams);
			var search = angular.extend($location.search(), parse($location.absUrl()));
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
		
		return {
			parse: parse,
			load: load,
			watchpage: watch
		};
	});
	
	// mvApiTesterモジュールを追加
	angular.module('mvApiTester', ['mvCommon', 'ngResource', 'ui.bootstrap']);
	angular.module('mvIndex', ['mvCommon', 'ui.bootstrap']);
	angular.module('mvProjects', ['mvCommon', 'ui.bootstrap']);
	
	angular.module('mvIndex')
	.controller('search', function($log, $scope, entities, pathvars) {
		$scope.projectNames = function (partialName) {
			return entities.ProjectName.query({like: partialName}).$promise;
		};
	});
	
	angular.module('mvProjects')
	.controller('list', function($log, $scope, $location, entities, pathvars, params) {
		
		$scope.cond = params.load({pathbase: false, like: ''});
		$scope.projectOrPathNames = function (partialName) {
			if ($scope.cond.pathbase) {
				return entities.PathName.query({like: partialName}).$promise;
			} else {
				return entities.ProjectName.query({like: partialName}).$promise;
			}
		};

		params.watchpage($scope, function(p) {
			$scope.cond.page = p;
			entities.Project.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	});
	
	
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
