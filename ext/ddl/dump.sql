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
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
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
-- Name: project_svn_commit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_svn_commit (
    project_id integer NOT NULL,
    svn_commit_id integer NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE project_svn_commit OWNER TO postgres;

--
-- Name: project_svn_repository; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_svn_repository (
    project_id integer NOT NULL,
    svn_repository_id integer NOT NULL,
    last_revision integer DEFAULT 0 NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL,
    update_date timestamp without time zone DEFAULT now() NOT NULL,
    update_user_id integer NOT NULL
);


ALTER TABLE project_svn_repository OWNER TO postgres;

--
-- Name: svn_commit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE svn_commit (
    id integer NOT NULL,
    svn_repository_id integer NOT NULL,
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
    svn_commit_id integer NOT NULL,
    change_type_id integer NOT NULL,
    path text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    create_user_id integer NOT NULL
);


ALTER TABLE svn_commit_path OWNER TO postgres;

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
    ADD CONSTRAINT project_svn_commit_pk PRIMARY KEY (project_id, svn_commit_id);


--
-- Name: project_svn_repository_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_pk PRIMARY KEY (project_id, svn_repository_id);


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
-- Name: online_batch_error_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_error
    ADD CONSTRAINT online_batch_error_fk0 FOREIGN KEY (online_batch_log_id) REFERENCES online_batch_log(id);


--
-- Name: online_batch_error_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_error
    ADD CONSTRAINT online_batch_error_fk1 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: online_batch_lock_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_fk0 FOREIGN KEY (program_id) REFERENCES online_batch_program(id);


--
-- Name: online_batch_lock_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_fk1 FOREIGN KEY (last_lock_user_id) REFERENCES application_user(id);


--
-- Name: online_batch_lock_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_lock
    ADD CONSTRAINT online_batch_lock_fk2 FOREIGN KEY (last_unlock_user_id) REFERENCES application_user(id);


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
-- Name: online_batch_log_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY online_batch_log
    ADD CONSTRAINT online_batch_log_fk2 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


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
    ADD CONSTRAINT project_svn_commit_fk1 FOREIGN KEY (svn_commit_id) REFERENCES svn_commit(id);


--
-- Name: project_svn_commit_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_commit
    ADD CONSTRAINT project_svn_commit_fk2 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: project_svn_repository_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk0 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_svn_repository_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk1 FOREIGN KEY (svn_repository_id) REFERENCES svn_repository(id);


--
-- Name: project_svn_repository_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk2 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: project_svn_repository_fk3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY project_svn_repository
    ADD CONSTRAINT project_svn_repository_fk3 FOREIGN KEY (update_user_id) REFERENCES application_user(id);


--
-- Name: svn_commit_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit
    ADD CONSTRAINT svn_commit_fk0 FOREIGN KEY (svn_repository_id) REFERENCES svn_repository(id);


--
-- Name: svn_commit_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit
    ADD CONSTRAINT svn_commit_fk1 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: svn_commit_path_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_fk0 FOREIGN KEY (svn_commit_id) REFERENCES svn_commit(id);


--
-- Name: svn_commit_path_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_fk1 FOREIGN KEY (change_type_id) REFERENCES change_type(id);


--
-- Name: svn_commit_path_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_commit_path
    ADD CONSTRAINT svn_commit_path_fk2 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: svn_repository_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_repository
    ADD CONSTRAINT svn_repository_fk0 FOREIGN KEY (create_user_id) REFERENCES application_user(id);


--
-- Name: svn_repository_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY svn_repository
    ADD CONSTRAINT svn_repository_fk1 FOREIGN KEY (update_user_id) REFERENCES application_user(id);


--
-- PostgreSQL database dump complete
--
