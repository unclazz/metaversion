SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

SET search_path = public, pg_catalog;
SET default_with_oids = false;

ALTER TABLE project ADD COLUMN scheduled_release_date timestamp without time zone;

CREATE OR REPLACE VIEW project_path_count_view AS 
 SELECT x2.project_id,
    count(x2.path) AS path_count
   FROM ( SELECT pc2.project_id,
            cp2.path
           FROM project_svn_commit pc2
             JOIN svn_commit_path cp2 ON pc2.commit_id = cp2.commit_id
          GROUP BY pc2.project_id, cp2.path) x2
  GROUP BY x2.project_id;

ALTER TABLE project_path_count_view
  OWNER TO postgres;

DROP VIEW project_parallels_view;
DROP VIEW project_changedpath_plus_view;
DROP VIEW project_changedpath_view;
DROP VIEW project_stats_view;

CREATE OR REPLACE VIEW project_changedpath_view AS 
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
            WHEN p.scheduled_release_date IS NULL THEN max(c.commit_date)
            WHEN p.scheduled_release_date < max(c.commit_date) THEN max(c.commit_date)
            ELSE p.scheduled_release_date
        END AS potential_max_commit_date
   FROM svn_commit_path cp
     JOIN svn_commit c ON cp.commit_id = c.id
     JOIN svn_repository r ON c.repository_id = r.id
     JOIN project_svn_commit pc ON pc.commit_id = cp.commit_id
     JOIN project p ON p.id = pc.project_id
  GROUP BY pc.project_id, cp.path, r.id, r.name, p.scheduled_release_date;

ALTER TABLE project_changedpath_view
  OWNER TO postgres;

CREATE OR REPLACE VIEW project_stats_view AS 
 SELECT p.id,
    p.code,
    p.name,
    p.responsible_person,
    p.commit_sign_pattern,
    count(c.id) AS commit_count,
    min(c.commit_date) AS min_commit_date,
    max(c.commit_date) AS max_commit_date,
        CASE
            WHEN p.scheduled_release_date IS NULL THEN max(c.commit_date)
            WHEN p.scheduled_release_date < max(c.commit_date) THEN max(c.commit_date)
            ELSE p.scheduled_release_date
        END AS potential_max_commit_date,
    ( SELECT ppcv.path_count
           FROM project_path_count_view ppcv
          WHERE ppcv.project_id = p.id) AS path_count,
    p.scheduled_release_date
   FROM project p
     LEFT JOIN project_svn_commit pc ON p.id = pc.project_id
     LEFT JOIN svn_commit c ON pc.commit_id = c.id
  GROUP BY p.id, p.code, p.name, p.responsible_person, p.commit_sign_pattern;

ALTER TABLE project_stats_view
  OWNER TO postgres;

CREATE OR REPLACE VIEW project_changedpath_plus_view AS 
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
   FROM project_stats_view psv
     JOIN virtual_changed_path vcp ON psv.id = vcp.project_id
     JOIN svn_repository r ON r.id = vcp.repository_id
  WHERE NOT (EXISTS ( SELECT 1
           FROM project_svn_commit pc
             JOIN svn_commit c ON pc.commit_id = c.id
             JOIN svn_commit_path cp ON c.id = cp.commit_id
          WHERE pc.project_id = vcp.project_id AND c.repository_id = vcp.repository_id AND cp.path = vcp.path));

ALTER TABLE project_changedpath_plus_view
  OWNER TO postgres;

CREATE OR REPLACE VIEW project_parallels_view AS 
 SELECT a_stat.id AS self_project_id,
    a_path.path,
        CASE
            WHEN a_path.min_commit_date <= b_path.min_commit_date AND b_path.potential_max_commit_date <= a_path.potential_max_commit_date THEN '[A...[B...B]...A]'::text
            WHEN b_path.min_commit_date <= a_path.min_commit_date AND a_path.potential_max_commit_date <= b_path.potential_max_commit_date THEN '[B...[A...A]...B]'::text
            WHEN a_path.min_commit_date <= b_path.min_commit_date AND b_path.min_commit_date <= a_path.potential_max_commit_date THEN '[A...[B...A]...B]'::text
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
   FROM project_changedpath_plus_view a_path
     JOIN project_changedpath_plus_view b_path ON a_path.path = b_path.path AND a_path.svn_repository_id = b_path.svn_repository_id
     JOIN project_stats_view a_stat ON a_path.project_id = a_stat.id
     JOIN project b_proj ON b_path.project_id = b_proj.id
  WHERE a_path.project_id <> b_path.project_id AND (a_path.min_commit_date <= b_path.min_commit_date AND b_path.potential_max_commit_date <= a_path.potential_max_commit_date OR b_path.min_commit_date <= a_path.min_commit_date AND a_path.potential_max_commit_date <= b_path.potential_max_commit_date OR a_path.min_commit_date <= b_path.min_commit_date AND b_path.min_commit_date <= a_path.potential_max_commit_date OR b_path.min_commit_date <= a_path.min_commit_date AND a_path.min_commit_date <= b_path.potential_max_commit_date);

ALTER TABLE project_parallels_view
  OWNER TO postgres;
