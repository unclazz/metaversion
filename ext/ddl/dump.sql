--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

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
    last_unlock_user_id integer NOT NULL,
    system_boot_date timestamp without time zone NOT NULL
);


ALTER TABLE online_batch_lock OWNER TO postgres;

--
-- Name: online_batch_lock_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE online_batch_lock_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE online_batch_lock_seq OWNER TO postgres;

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
    update_user_id integer NOT NULL,
    scheduled_release_date timestamp without time zone
);


ALTER TABLE project OWNER TO postgres;

--
-- Name: project_svn_commit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_svn_commit (
    project_id integer NOT NULL,
    commit_id integer NOT NULL,
    auto_linked boolean DEFAULT true NOT NULL,
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
    create_user_id integer NOT NULL,
    raw_path text NOT NULL,
    base_path_segment text NOT NULL,
    branch_path_segment text NOT NULL
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
    max(c.commit_date) AS max_commit_date,
        CASE
            WHEN (p.scheduled_release_date IS NULL) THEN max(c.commit_date)
            WHEN (p.scheduled_release_date < max(c.commit_date)) THEN max(c.commit_date)
            ELSE p.scheduled_release_date
        END AS potential_max_commit_date
   FROM ((((svn_commit_path cp
     JOIN svn_commit c ON ((cp.commit_id = c.id)))
     JOIN svn_repository r ON ((c.repository_id = r.id)))
     JOIN project_svn_commit pc ON ((pc.commit_id = cp.commit_id)))
     JOIN project p ON ((p.id = pc.project_id)))
  GROUP BY pc.project_id, cp.path, r.id, r.name, p.scheduled_release_date;


ALTER TABLE project_changedpath_view OWNER TO postgres;

--
-- Name: project_stats_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE project_stats_view (
    id integer,
    code text,
    name text,
    responsible_person text,
    commit_sign_pattern text,
    commit_count bigint,
    min_commit_date timestamp without time zone,
    max_commit_date timestamp without time zone,
    potential_max_commit_date timestamp without time zone,
    path_count bigint,
    scheduled_release_date timestamp without time zone
);

ALTER TABLE ONLY project_stats_view REPLICA IDENTITY NOTHING;


ALTER TABLE project_stats_view OWNER TO postgres;

--
-- Name: virtual_changed_path; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE virtual_changed_path (
    id integer NOT NULL,
    project_id integer NOT NULL,
    repository_id integer NOT NULL,
    path text NOT NULL,
    change_type_id integer NOT NULL
);


ALTER TABLE virtual_changed_path OWNER TO postgres;

--
-- Name: project_changedpath_plus_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW project_changedpath_plus_view AS
 SELECT project_changedpath_view.project_id,
    project_changedpath_view.path,
    project_changedpath_view.svn_repository_id,
    project_changedpath_view.svn_repository_name,
    project_changedpath_view.commit_count,
    project_changedpath_view.min_revision,
    project_changedpath_view.max_revision,
    project_changedpath_view.min_commit_date,
    project_changedpath_view.max_commit_date,
    project_changedpath_view.potential_max_commit_date
   FROM project_changedpath_view
UNION ALL
 SELECT vcp.project_id,
    vcp.path,
    vcp.repository_id AS svn_repository_id,
    r.name AS svn_repository_name,
    0 AS commit_count,
    0 AS min_revision,
    0 AS max_revision,
    psv.min_commit_date,
    psv.max_commit_date,
    psv.potential_max_commit_date
   FROM ((project_stats_view psv
     JOIN virtual_changed_path vcp ON ((psv.id = vcp.project_id)))
     JOIN svn_repository r ON ((r.id = vcp.repository_id)))
  WHERE (NOT (EXISTS ( SELECT 1
           FROM ((project_svn_commit pc
             JOIN svn_commit c ON ((pc.commit_id = c.id)))
             JOIN svn_commit_path cp ON ((c.id = cp.commit_id)))
          WHERE ((pc.project_id = vcp.project_id) AND (c.repository_id = vcp.repository_id) AND (cp.path = vcp.path)))));


ALTER TABLE project_changedpath_plus_view OWNER TO postgres;

--
-- Name: project_parallels_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW project_parallels_view AS
 SELECT a_stat.id AS self_project_id,
    a_path.path,
        CASE
            WHEN ((a_path.min_commit_date <= b_path.min_commit_date) AND (b_path.potential_max_commit_date <= a_path.potential_max_commit_date)) THEN '[A...[B...B]...A]'::text
            WHEN ((b_path.min_commit_date <= a_path.min_commit_date) AND (a_path.potential_max_commit_date <= b_path.potential_max_commit_date)) THEN '[B...[A...A]...B]'::text
            WHEN ((a_path.min_commit_date <= b_path.min_commit_date) AND (b_path.min_commit_date <= a_path.potential_max_commit_date)) THEN '[A...[B...A]...B]'::text
            ELSE '[B...[A...B]...A]'::text
        END AS parallel_type,
    a_path.min_revision AS self_min_revision,
    a_path.min_commit_date AS self_min_commit_date,
    a_path.max_revision AS self_max_revision,
    a_path.max_commit_date AS self_max_commit_date,
    a_path.potential_max_commit_date AS self_potential_max_commit_date,
    b_path.project_id AS other_project_id,
    b_proj.name AS other_project_name,
    b_proj.code AS other_project_code,
    b_proj.responsible_person AS other_project_responsible_person,
    b_path.min_revision AS other_min_revision,
    b_path.min_commit_date AS other_min_commit_date,
    b_path.max_revision AS other_max_revision,
    b_path.max_commit_date AS other_max_commit_date,
    b_path.potential_max_commit_date AS other_potential_max_commit_date,
    a_path.svn_repository_id AS repository_id,
    a_path.svn_repository_name AS repository_name
   FROM (((project_changedpath_plus_view a_path
     JOIN project_changedpath_plus_view b_path ON (((a_path.path = b_path.path) AND (a_path.svn_repository_id = b_path.svn_repository_id))))
     JOIN project_stats_view a_stat ON ((a_path.project_id = a_stat.id)))
     JOIN project b_proj ON ((b_path.project_id = b_proj.id)))
  WHERE ((a_path.project_id <> b_path.project_id) AND (((a_path.min_commit_date <= b_path.min_commit_date) AND (b_path.potential_max_commit_date <= a_path.potential_max_commit_date)) OR ((b_path.min_commit_date <= a_path.min_commit_date) AND (a_path.potential_max_commit_date <= b_path.potential_max_commit_date)) OR ((a_path.min_commit_date <= b_path.min_commit_date) AND (b_path.min_commit_date <= a_path.potential_max_commit_date)) OR ((b_path.min_commit_date <= a_path.min_commit_date) AND (a_path.min_commit_date <= b_path.potential_max_commit_date))));


ALTER TABLE project_parallels_view OWNER TO postgres;

--
-- Name: project_path_count_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW project_path_count_view AS
 SELECT x2.project_id,
    count(x2.path) AS path_count
   FROM ( SELECT pc2.project_id,
            cp2.path
           FROM (project_svn_commit pc2
             JOIN svn_commit_path cp2 ON ((pc2.commit_id = cp2.commit_id)))
          GROUP BY pc2.project_id, cp2.path) x2
  GROUP BY x2.project_id;


ALTER TABLE project_path_count_view OWNER TO postgres;

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
    min_project_code text,
    path_count bigint
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
-- Name: system_boot_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE system_boot_log (
    id integer NOT NULL,
    boot_date timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE system_boot_log OWNER TO postgres;

--
-- Name: system_boot_log_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE system_boot_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE system_boot_log_seq OWNER TO postgres;

--
-- Name: virtual_changed_path_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE virtual_changed_path_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE virtual_changed_path_seq OWNER TO postgres;

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
-- Name: system_boot_log_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY system_boot_log
    ADD CONSTRAINT system_boot_log_pk PRIMARY KEY (id);


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
-- Name: virtual_commit_path_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY virtual_changed_path
    ADD CONSTRAINT virtual_commit_path_pk PRIMARY KEY (id);


--
-- Name: virtual_commit_path_uniq0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY virtual_changed_path
    ADD CONSTRAINT virtual_commit_path_uniq0 UNIQUE (project_id, repository_id, path);


--
-- Name: svn_commit_idx01; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX svn_commit_idx01 ON svn_commit USING btree (repository_id, revision DESC, id DESC);


--
-- Name: svn_commit_path_idx01; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX svn_commit_path_idx01 ON svn_commit_path USING btree (commit_id DESC, id);


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
    p.code AS min_project_code,
    ( SELECT count(1) AS count
           FROM svn_commit_path cp
          WHERE (cp.commit_id = c_pc.id)) AS path_count
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
-- Name: _RETURN; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE "_RETURN" AS
    ON SELECT TO project_stats_view DO INSTEAD  SELECT p.id,
    p.code,
    p.name,
    p.responsible_person,
    p.commit_sign_pattern,
    count(c.id) AS commit_count,
    min(c.commit_date) AS min_commit_date,
    max(c.commit_date) AS max_commit_date,
        CASE
            WHEN (p.scheduled_release_date IS NULL) THEN max(c.commit_date)
            WHEN (p.scheduled_release_date < max(c.commit_date)) THEN max(c.commit_date)
            ELSE p.scheduled_release_date
        END AS potential_max_commit_date,
    ( SELECT ppcv.path_count
           FROM project_path_count_view ppcv
          WHERE (ppcv.project_id = p.id)) AS path_count,
    p.scheduled_release_date
   FROM ((project p
     LEFT JOIN project_svn_commit pc ON ((p.id = pc.project_id)))
     LEFT JOIN svn_commit c ON ((pc.commit_id = c.id)))
  GROUP BY p.id, p.code, p.name, p.responsible_person, p.commit_sign_pattern;


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
-- Name: virtual_commit_path_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY virtual_changed_path
    ADD CONSTRAINT virtual_commit_path_fk0 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: virtual_commit_path_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY virtual_changed_path
    ADD CONSTRAINT virtual_commit_path_fk1 FOREIGN KEY (repository_id) REFERENCES svn_repository(id);


--
-- Name: virtual_commit_path_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY virtual_changed_path
    ADD CONSTRAINT virtual_commit_path_fk2 FOREIGN KEY (change_type_id) REFERENCES change_type(id);


--
-- PostgreSQL database dump complete
--

