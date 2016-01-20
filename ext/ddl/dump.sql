--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- Name: application_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE application_user (
    id integer NOT NULL,
    name text NOT NULL,
    password text NOT NULL,
    admin boolean DEFAULT false NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE application_user OWNER TO postgres;

--
-- Name: application_user_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE application_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE application_user_seq OWNER TO postgres;

--
-- Name: change_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE change_type (
    id integer NOT NULL,
    code text NOT NULL,
    name text NOT NULL
);


ALTER TABLE change_type OWNER TO postgres;

--
-- Name: online_batch_error; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE online_batch_error (
    id integer NOT NULL,
    online_batch_log_id integer NOT NULL,
    error_name text NOT NULL,
    error_message text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE online_batch_error OWNER TO postgres;

--
-- Name: online_batch_error_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE online_batch_error_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE online_batch_error_seq OWNER TO postgres;

--
-- Name: online_batch_lock; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE online_batch_lock (
    id integer NOT NULL,
    program_id integer NOT NULL,
    locked boolean DEFAULT false NOT NULL,
    last_lock_date timestamp without time zone DEFAULT now() NOT NULL,
    last_unlock_date timestamp without time zone DEFAULT now() NOT NULL,
    last_lock_user_id integer NOT NULL,
    last_unlock_user_id integer NOT NULL
);


ALTER TABLE online_batch_lock OWNER TO postgres;

--
-- Name: online_batch_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE online_batch_log (
    id integer NOT NULL,
    program_id integer NOT NULL,
    status_id integer NOT NULL,
    start_date timestamp without time zone DEFAULT now() NOT NULL,
    end_date timestamp without time zone DEFAULT now(),
    create_user_id integer NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE online_batch_log OWNER TO postgres;

--
-- Name: online_batch_log_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE online_batch_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE online_batch_log_seq OWNER TO postgres;

--
-- Name: online_batch_program; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE online_batch_program (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE online_batch_program OWNER TO postgres;

--
-- Name: online_batch_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE online_batch_status (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE online_batch_status OWNER TO postgres;

--
-- Name: project; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project (
    id integer NOT NULL,
    code text NOT NULL,
    name text NOT NULL,
    responsible_person text NOT NULL,
    commit_sign_pattern text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE project OWNER TO postgres;

--
-- Name: project_svn_commit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_svn_commit (
    project_id integer NOT NULL,
    commit_id integer NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE project_svn_commit OWNER TO postgres;

--
-- Name: svn_commit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE svn_commit (
    id integer NOT NULL,
    repository_id integer NOT NULL,
    revision integer NOT NULL,
    commit_date timestamp without time zone NOT NULL,
    commit_message text,
    committer_name text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE svn_commit OWNER TO postgres;

--
-- Name: svn_commit_path; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE svn_commit_path (
    id integer NOT NULL,
    commit_id integer NOT NULL,
    change_type_id integer NOT NULL,
    path text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE svn_commit_path OWNER TO postgres;

--
-- Name: svn_repository; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE svn_repository (
    id integer NOT NULL,
    name text NOT NULL,
    base_url text NOT NULL,
    trunk_path_pattern text NOT NULL,
    branch_path_pattern text NOT NULL,
    username text,
    password text,
    max_revision integer DEFAULT 0 NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE svn_repository OWNER TO postgres;

--
-- Name: project_changedpath_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW project_changedpath_view AS
 SELECT pc.project_id,
    cp.path,
    r.id AS svn_repository_id,
    r.name AS svn_repository_name,
    count(1) AS commit_count,
    min(c.revision) AS min_revision,
    max(c.revision) AS max_revision,
    min(c.commit_date) AS min_commit_date,
    max(c.commit_date) AS max_commit_date
   FROM (((svn_commit_path cp
     JOIN svn_commit c ON ((cp.commit_id = c.id)))
     JOIN svn_repository r ON ((c.repository_id = r.id)))
     JOIN project_svn_commit pc ON ((pc.commit_id = cp.commit_id)))
  GROUP BY pc.project_id, cp.path, r.id, r.name;


ALTER TABLE project_changedpath_view OWNER TO postgres;

--
-- Name: project_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE project_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE project_seq OWNER TO postgres;

--
-- Name: project_stats_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW project_stats_view AS
 SELECT p.id,
    p.code,
    p.name,
    p.responsible_person,
    p.commit_sign_pattern,
    count(c.id) AS commit_count,
    min(c.commit_date) AS min_commit_date,
    max(c.commit_date) AS max_commit_date,
    ( SELECT count(1) AS count
           FROM project_changedpath_view pcp
          WHERE (pcp.project_id = p.id)) AS path_count
   FROM ((project p
     LEFT JOIN project_svn_commit pc ON ((p.id = pc.project_id)))
     LEFT JOIN svn_commit c ON ((pc.commit_id = c.id)))
  GROUP BY p.id, p.code, p.name, p.responsible_person, p.commit_sign_pattern;


ALTER TABLE project_stats_view OWNER TO postgres;

--
-- Name: project_svn_repository; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_svn_repository (
    project_id integer NOT NULL,
    repository_id integer NOT NULL,
    last_revision integer DEFAULT 0 NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE project_svn_repository OWNER TO postgres;

--
-- Name: svn_commit_path_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE svn_commit_path_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE svn_commit_path_seq OWNER TO postgres;

--
-- Name: svn_commit_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE svn_commit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE svn_commit_seq OWNER TO postgres;

--
-- Name: svn_commit_stats_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE svn_commit_stats_view (
    id integer,
    repository_id integer,
    revision integer,
    commit_date timestamp without time zone,
    commit_message text,
    committer_name text,
    create_date timestamp without time zone,
    create_user_id integer,
    project_count bigint,
    min_project_id integer,
    min_project_name text,
    min_project_code text
);

ALTER TABLE ONLY svn_commit_stats_view REPLICA IDENTITY NOTHING;


ALTER TABLE svn_commit_stats_view OWNER TO postgres;

--
-- Name: svn_repository_path_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW svn_repository_path_view AS
 SELECT c.repository_id AS svn_repository_id,
    cp.path
   FROM (svn_commit c
     JOIN svn_commit_path cp ON ((c.id = cp.commit_id)))
  GROUP BY c.repository_id, cp.path;


ALTER TABLE svn_repository_path_view OWNER TO postgres;

--
-- Name: svn_repository_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE svn_repository_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE svn_repository_seq OWNER TO postgres;

--
-- Name: svn_repository_stats_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW svn_repository_stats_view AS
 SELECT r.id,
    r.name,
    r.base_url,
    r.max_revision,
    ( SELECT count(c.id) AS count
           FROM svn_commit c
          WHERE (r.id = c.repository_id)) AS commit_count,
    ( SELECT count(rp.path) AS count
           FROM svn_repository_path_view rp
          WHERE (r.id = rp.svn_repository_id)) AS path_count
   FROM svn_repository r;


ALTER TABLE svn_repository_stats_view OWNER TO postgres;

--
-- Name: change_type_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY change_type
    ADD CONSTRAINT change_type_pk PRIMARY KEY (id);


--
-- Name: change_type_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY change_type
    ADD CONSTRAINT change_type_uniq0 UNIQUE (code);


--
-- Name: change_type_uniq1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY change_type
    ADD CONSTRAINT change_type_uniq1 UNIQUE (name);


--
-- Name: online_batch_error_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_error
    ADD CONSTRAINT online_batch_error_pk PRIMARY KEY (id);


--
-- Name: online_batch_lock_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_pk PRIMARY KEY (id);


--
-- Name: online_batch_lock_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_uniq0 UNIQUE (program_id);


--
-- Name: online_batch_log_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_log
    ADD CONSTRAINT online_batch_log_pk PRIMARY KEY (id);


--
-- Name: online_batch_program_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_program
    ADD CONSTRAINT online_batch_program_pk PRIMARY KEY (id);


--
-- Name: online_batch_program_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_program
    ADD CONSTRAINT online_batch_program_uniq0 UNIQUE (name);


--
-- Name: online_batch_status_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_status
    ADD CONSTRAINT online_batch_status_pk PRIMARY KEY (id);


--
-- Name: online_batch_status_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_status
    ADD CONSTRAINT online_batch_status_uniq0 UNIQUE (name);


--
-- Name: project_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pk PRIMARY KEY (id);


--
-- Name: project_svn_commit_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_commit
    ADD CONSTRAINT project_svn_commit_pk PRIMARY KEY (project_id, commit_id);


--
-- Name: project_svn_repository_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_pk PRIMARY KEY (project_id, repository_id);


--
-- Name: project_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_uniq0 UNIQUE (code);


--
-- Name: project_uniq1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_uniq1 UNIQUE (name);


--
-- Name: svn_commit_path_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_pk PRIMARY KEY (id);


--
-- Name: svn_commit_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit
    ADD CONSTRAINT svn_commit_pk PRIMARY KEY (id);


--
-- Name: svn_repository_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_repository
    ADD CONSTRAINT svn_repository_pk PRIMARY KEY (id);


--
-- Name: svn_repository_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_repository
    ADD CONSTRAINT svn_repository_uniq0 UNIQUE (name);


--
-- Name: user_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY application_user
    ADD CONSTRAINT user_pk PRIMARY KEY (id);


--
-- Name: user_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY application_user
    ADD CONSTRAINT user_uniq0 UNIQUE (name);


--
-- Name: _RETURN; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE "_RETURN" AS
    ON SELECT TO svn_commit_stats_view DO INSTEAD  SELECT c_pc.id,
    c_pc.repository_id,
    c_pc.revision,
    c_pc.commit_date,
    c_pc.commit_message,
    c_pc.committer_name,
    c_pc.create_date,
    c_pc.create_user_id,
    c_pc.project_count,
    c_pc.min_project_id,
    p.name AS min_project_name,
    p.code AS min_project_code
   FROM (( SELECT c.id,
            c.repository_id,
            c.revision,
            c.commit_date,
            c.commit_message,
            c.committer_name,
            c.create_date,
            c.create_user_id,
            count(pc.project_id) AS project_count,
            min(pc.project_id) AS min_project_id
           FROM (svn_commit c
             LEFT JOIN project_svn_commit pc ON ((pc.commit_id = c.id)))
          GROUP BY c.id) c_pc
     LEFT JOIN project p ON ((c_pc.min_project_id = p.id)));


--
-- Name: online_batch_error_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_error
    ADD CONSTRAINT online_batch_error_fk0 FOREIGN KEY (online_batch_log_id) REFERENCES online_batch_log(id);


--
-- Name: online_batch_lock_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_fk0 FOREIGN KEY (program_id) REFERENCES online_batch_program(id);


--
-- Name: online_batch_log_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_log
    ADD CONSTRAINT online_batch_log_fk0 FOREIGN KEY (program_id) REFERENCES online_batch_program(id);


--
-- Name: online_batch_log_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_log
    ADD CONSTRAINT online_batch_log_fk1 FOREIGN KEY (status_id) REFERENCES online_batch_status(id);


--
-- Name: project_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_fk0 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: project_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_fk1 FOREIGN KEY (update_user_id) REFERENCES application_user(id);


--
-- Name: project_svn_commit_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_commit
    ADD CONSTRAINT project_svn_commit_fk0 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_svn_commit_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_commit
    ADD CONSTRAINT project_svn_commit_fk1 FOREIGN KEY (commit_id) REFERENCES svn_commit(id);


--
-- Name: project_svn_repository_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk0 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_svn_repository_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk1 FOREIGN KEY (repository_id) REFERENCES svn_repository(id);


--
-- Name: svn_commit_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit
    ADD CONSTRAINT svn_commit_fk0 FOREIGN KEY (repository_id) REFERENCES svn_repository(id);


--
-- Name: svn_commit_path_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_fk0 FOREIGN KEY (commit_id) REFERENCES svn_commit(id);


--
-- Name: svn_commit_path_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_fk1 FOREIGN KEY (change_type_id) REFERENCES change_type(id);


--
-- PostgreSQL database dump complete
--

