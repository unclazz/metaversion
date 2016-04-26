module MetaVersion {
    export interface IUser {
        admin : boolean;
        encodedPassword : string;
        id : number;
        name : string;
        password : string;
    }
    export interface IRepository {
        baseUrl : string;
        branchPathPattern : string;
        encodedPassword : string;
        id : number;
        maxRevision :number;
        name : string;
        password : string;
        trunkPathPattern : string;
        username : string;
    }
    export interface IRepositoryCommit {
        
    }
    export interface IRepositoryCommitStats {
        
    }
    export interface IRepositoryCommitProject {
        
    }
    export interface IRepositoryCommitChangedPath {
        
    }
    export interface IProject {
        id :number;
        name :string;
    }
    export interface IProjectStats {
        
    }
    export interface IProjectCommit {
        
    }
    export interface IProjectChangedPath {
        otherMaxCommitDate : string
        otherMaxRevision : number
        otherMinCommitDate : string
        otherMinRevision : number
        otherProjectCode : string
        otherProjectId : number
        otherProjectName : string
        otherProjectResponsiblePerson : string
        parallelType : string
        path : string
        repositoryId : number
        repositoryName : string
        selfMaxCommitDate : string
        selfMaxRevision : number
        selfMinCommitDate : string
        selfMinRevision : number
        selfProjectId : number
    }
    export interface IProjectParallel {
        
    }
    export interface IBatch {
        program :string
    }
    export interface IPaginated<T> {
        list :T[];
        size :number;
        totalSize :number;
        page :number;
    }
    
    export function entitiesFactoryFn($log :ng.ILogService,
        $resource :ng.resource.IResourceService) {
        
		const entities :IEntityService = <IEntityService>{};
		const pagingParams = {page: 1, size: 25};
        const projectsPagingParams = angular.extend({
            like: '', pathbase: false, unlinkedCommitId: 0
        }, pagingParams);
		const suggestParams = {like: '', size: 25};
        
        entities.batches = entityResource<IBatch>("Batches",
            "api/batches/:programId", 
            {programId: "@programId"}, pagingParams);
        entities.projectChangedPaths = entityResource<IProjectChangedPath>("ProjectChangedPath", 
            "api/projects/:projectId/changedpaths",
            {projectId: "@projectId"}, pagingParams);
        entities.projectCommits = entityResource<IProjectCommit>("ProjectCommit", 
            "api/projects/:projectId/commits/:commitId",
            {projectId: "@projectId", commitId: "@commitId"}, pagingParams);
        entities.projectParallels = entityResource<IProjectParallel>("ProjectParallels",
            "api/projects/:projectId/parallels",
            {projectId: "@projectId"}, pagingParams);
        entities.projects = entityResource<IProject>("Project", 
            "api/projects/:id", 
            {id: "@id"}, projectsPagingParams) as IResourceClassResavable<IProject>;
        entities.projectStats = entityResource<IProjectStats>("ProjectStats",
            "api/projectstats/:id", 
            {id: "@id"}, pagingParams);
        entities.repositories = entityResource<IRepository>("Repository", 
            "api/repositories/:id", 
            {id: "@id"}, pagingParams) as IResourceClassResavable<IRepository>;
        entities.repositoryCommitChangedPaths = entityResource<IRepositoryCommitChangedPath>("RepositoryCommitChangedPath",
            "api/repositories/:repositoryId/commits/:commitId/changedpaths", 
			{repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
        entities.repositoryCommitProjects = entityResource<IRepositoryCommitProject>("RepositoryCommitProject", 
            "api/repositories/:repositoryId/commits/:commitId/projects", 
            {repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
        entities.repositoryCommits = entityResource<IRepositoryCommit>("RepositoryCommit", 
            "api/repositories/:repositoryId/commits/:commitId", 
            {repositoryId: "@repositoryId", commitId: "@commitId"},
            angular.extend({unlinked: false}, pagingParams));
        entities.repositoryCommitStats = entityResource<IRepositoryCommitStats>("RepositoryCommitStats", 
            "api/repositories/:repositoryId/commitstats/:commitId", 
			{repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
        entities.users = entityResource<IUser>("User", "api/users/:id", {id: "@id"},
            pagingParams) as IResourceClassResavable<IUser>;
        entities.pathNames = suggestResource("PathName", "api/pathnames", {}, suggestParams);
        entities.projectNames = suggestResource("ProjectName", "api/projectnames", {}, suggestParams);
        
        return entities;
        
		function entityResource<T>(entityName :string, urlPattern :string,
            urlParams :any, queryParams :any)  {
            
			const xformResp = function(data :any) {
				const paginated = angular.fromJson(data);
				if (paginated.list === undefined) {
					return data;
				}
				// paginated.list = paginated.list.map(function (item) {
				// 	return new entities[entityName](item)
				// });
				return paginated;
			};
			const paginatedQueryAction = {
				method: 'GET',
				params: queryParams,
				isArray:false,
				transformResponse : xformResp
			};
			const resaveAction = {
					method: 'PUT',
					isArray:false
				};
			const customActions = {
				'query' : paginatedQueryAction,
				'resave' : resaveAction
			};
			return $resource<T>(urlPattern,urlParams, customActions);
		}
		function suggestResource(entityName :string,
            urlPattern :string, urlParams :any, queryParams :any) {
			const paginatedQueryAction = {
				method: 'GET',
				params: queryParams,
				isArray: true
			};
			const customActions = {
				'query' : paginatedQueryAction
			};
			return $resource<string>(urlPattern,urlParams, customActions);
		}
    }
}