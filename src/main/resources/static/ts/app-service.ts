module MetaVersion {
    export interface IPathService {
            entryToQuery: (key :string, value :any) => void;
            objectToQuery: (data :any) => void;
            pathToIds: () => IEntityIds;
            queryToObject: (defaultParams? : any) => any;
            stringToPath: (path :string, data? :any) => void;
    }

    export interface IEntityIds {
        userId :number;
        projectId :number;
        repositoryId :number;
        commitId :number;
    }
    
    /**
     * RESTful APIにアクセスするためのリソース・クラスのコレクション.
     */
    export interface IEntityService {
        users :ng.resource.IResourceClass<IUser>;
        repositories :ng.resource.IResourceClass<IRepository>;
        repositoryCommits :ng.resource.IResourceClass<IRepositoryCommit>;
        repositoryCommitChangedPaths :ng.resource.IResourceClass<IRepositoryCommitChangedPath>;
        repositoryCommitProjects :ng.resource.IResourceClass<IRepositoryCommitProject>;
        repositoryCommitStats :ng.resource.IResourceClass<IRepositoryCommitStats>;
        projects :IResourceClassResavable<IProject>;
        projectChangedPaths :ng.resource.IResourceClass<IProjectChangedPath>;
        projectCommits :ng.resource.IResourceClass<IProjectCommit>;
        projectParallels :ng.resource.IResourceClass<IProjectParallel>;
        projectStats :ng.resource.IResourceClass<IProjectStats>;
        batches :ng.resource.IResourceClass<IBatch>;
        pathNames :ng.resource.IResourceClass<string>;
        projectNames :ng.resource.IResourceClass<string>;
    }
    
    export interface IResourceClassResavable<T> extends ng.resource.IResourceClass<T> {
        resave(params :any, success :(data :IProject) => void, error? :Function) : T;
    }
    
    export interface IModalService {
        errorModal : (e :IErrorData) => ng.ui.bootstrap.IModalServiceInstance;
        waitingModal : (ms :string[]) => ng.ui.bootstrap.IModalServiceInstance;
    }
    
    export function pathsFactoryFn($log :ng.ILogService,
            $location :ng.ILocationService) {
        const p :IPathService = <IPathService>{};
        p.entryToQuery = function(key, value) {
			var q = $location.search();
			q[key] = value;
			$location.search(q);
		};
        p.objectToQuery = function(data) {
			$location.search(data);
		};
        p.pathToIds = function() {
			const ids = <IEntityIds>{};
			const url = $location.absUrl();
			var res :RegExpExecArray = null;
			if (res = /\/users\/(\d+)/.exec(url)) {
				ids.userId =  + res[1];
			}
			if (res = /\/projects\/(\d+)/.exec(url)) {
				ids.projectId = + res[1];
			}
			if (res = /\/repositories\/(\d+)/.exec(url)) {
				ids.repositoryId = + res[1];
			}
			if (res = /\/commits\/(\d+)/.exec(url)) {
				ids.commitId = + res[1];
			}
			return ids;
		};
        p.queryToObject = function(defaultParams) {
			const params = defaultParams === undefined ? {} : angular.copy(defaultParams);
			const search = $location.search();
			if (search === undefined) {
				return params;
			}
			for (var k in search) {
				if (k in params) {
					const v = params[k];
					if (angular.isNumber(v)) {
						if (search[k] === 'true') {
							params[k] = 1;
						} else if (search[k] === 'false') {
							params[k] = 0;
						} else {
							params[k] = search[k] - 0;
						}
					} else if (angular.isString(v)) {
						params[k] = search[k] + '';
					} else if (v === true || v === false) {
						params[k] = search[k] == 'false' ? false : true;
					} else {
						params[k] = search[k];
					}
				} else {
					params[k] = search[k];
				}
			}
			return params;
		};
        p.stringToPath = function(path, data?) {
			$location.path(path);
			if (data !== undefined) {
				$location.search(data);
			}
		};
        return p;
    }
    
    export function modalsFactoryFn($log :ng.ILogService,
            $uibModal :ng.ui.bootstrap.IModalService,
            $location :ng.ILocationService) {
        
        const m :IModalService = <IModalService>{};
        m.errorModal = function(error) {
			return $uibModal.open({
				templateUrl: 'js/templates/errorModal.html',
				controller: 'errorModal',
				size: 'lg',
				backdrop: 'static',
				resolve: {
					error: function () {
						return error;
					}
				}
			});
		};
        m.waitingModal = function(messages) {
			return $uibModal.open({
				templateUrl: 'js/templates/waitingModal.html',
				controller: 'waitingModal',
				size: 'lg',
				backdrop: 'static',
				resolve: {
					messages: function () {
						return angular.isString(messages) ? [messages] : messages;
					}
				}
			});
		};
        return m;
    }
}