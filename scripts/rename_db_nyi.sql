-- =============================================================================
-- rename_db_nyi.sql — Sprint 0 database identity migration (NYI_Sprint_Plan.md,
-- step 3: "rename the database/schema if it's currently named after [the old
-- brand]; if already in use with data, this needs a migration script, not a
-- manual rename — write it, don't hand-edit").
--
-- WHY THIS IS A STANDALONE SCRIPT AND NOT A LIQUIBASE CHANGESET
--   `ALTER DATABASE ... RENAME TO` cannot be executed from inside the database
--   being renamed — PostgreSQL rejects it with "source database is being
--   accessed by other users". Liquibase connects to exactly that database, so
--   a changeset could never perform the rename. It must be run once, out of
--   band, by an operator connected to a different database (e.g. `postgres`).
--
--   Schema-level changes (tables, columns, indexes) are NOT touched. Only the
--   database name and the owning role name change. Table ownership follows the
--   role rename automatically.
--
-- USAGE
--
--   STEP 0 — bootstrap a separate superuser (needed once per environment).
--
--   PostgreSQL refuses `ALTER ROLE ... RENAME TO` when the session user is that
--   same role ("session user cannot be renamed"). In the docker-compose setup
--   the application's role IS the cluster superuser, and the postgres image
--   provisions no separate `postgres` role when POSTGRES_USER is overridden.
--   So create a throwaway superuser, run the migration as it, then drop it:
--
--     psql -U jemil -d postgres -c "CREATE ROLE nyi_bootstrap WITH LOGIN SUPERUSER PASSWORD 'pick-a-temporary-password';"
--
--   STEP 1 — run the migration as that bootstrap superuser:
--
--     psql -U nyi_bootstrap -d postgres -f scripts/rename_db_nyi.sql
--
--   STEP 2 — drop the throwaway superuser:
--
--     psql -U nyi -d nyi_db -c "DROP ROLE nyi_bootstrap;"
--
--   Run it ONCE per environment. It is idempotent in the sense that it will
--   report and skip anything already renamed — but review the NOTICE output
--   rather than assuming it was a no-op.
--
--   IMPORTANT: update the matching application configuration at the same time:
--     - docker-compose.yml  (POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD)
--     - .env / .env.example (DB_NAME / DB_USER / DB_PASSWORD)
--     - src/main/resources/application*.yml defaults
--
--   The e2e and integration suites are NOT affected: they provision their own
--   ephemeral Testcontainers database per run and never touch this database.
-- =============================================================================

\set ON_ERROR_STOP on

-- -----------------------------------------------------------------------------
-- Safety check 1: refuse to run if we are connected to the database or the role
-- being renamed. Both are rejected by PostgreSQL, so fail early with a clear
-- message instead of a cryptic mid-script error.
-- -----------------------------------------------------------------------------
DO $$
DECLARE
    current_db   text := current_database();
    current_user text := current_user;
BEGIN
    IF current_db = 'jemil_db' THEN
        RAISE EXCEPTION
            'Connected to the database being renamed. Re-run connected to another '
            'database, e.g. psql -d postgres. Aborting.';
    END IF;

    IF current_user = 'jemil' THEN
        RAISE EXCEPTION
            'Connected as the role being renamed. PostgreSQL will not rename the '
            'session user''s own role. Re-run as a different superuser, e.g. '
            'psql -U postgres -d postgres. Aborting.';
    END IF;

    RAISE NOTICE 'Connected to database "%" as role "%". Safe to proceed.',
        current_db, current_user;
END
$$;

-- -----------------------------------------------------------------------------
-- Step 1 — rename the owning role.
--
-- Done first, and separately from the database, because object ownership in
-- PostgreSQL is tied to the role OID, not the role name. Renaming the role
-- preserves every owned object with no reassignment needed.
--
-- The password is NOT set here. Rotating credentials is an operational decision
-- and must go through your secret manager, not a file in the repository.
-- -----------------------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'jemil') THEN
        EXECUTE 'ALTER ROLE jemil RENAME TO nyi';
        RAISE NOTICE 'Role renamed: jemil -> nyi';
    ELSIF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'nyi') THEN
        RAISE NOTICE 'Role "nyi" already exists. Skipping role rename.';
    ELSE
        RAISE EXCEPTION
            'Neither role "jemil" nor role "nyi" exists. Nothing to migrate.';
    END IF;
END
$$;

-- -----------------------------------------------------------------------------
-- Step 2 — rename the database.
--
-- Any remaining sessions are terminated first. A stopped application must be
-- stopped for real before running this, otherwise it will reconnect after the
-- rename and fail with a stale connection pool.
--
-- template1 is deliberately NOT renamed: it is a cluster-wide system database,
-- owned by the bootstrap superuser, and has nothing to do with this project.
-- -----------------------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'jemil_db') THEN
        IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'nyi_db') THEN
            RAISE EXCEPTION
                'Both jemil_db and nyi_db exist. Refusing to guess — resolve by hand.';
        END IF;

        EXECUTE 'SELECT pg_terminate_backend(pid) FROM pg_stat_activity '
             || 'WHERE datname = ''jemil_db'' AND pid <> pg_backend_pid()';

        EXECUTE 'ALTER DATABASE jemil_db RENAME TO nyi_db';
        RAISE NOTICE 'Database renamed: jemil_db -> nyi_db';
    ELSIF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'nyi_db') THEN
        RAISE NOTICE 'Database "nyi_db" already exists. Skipping database rename.';
    ELSE
        RAISE EXCEPTION
            'Neither database "jemil_db" nor database "nyi_db" exists. Nothing to migrate.';
    END IF;
END
$$;

-- -----------------------------------------------------------------------------
-- Step 3 — report the result so the operator can verify without guessing.
-- -----------------------------------------------------------------------------
\echo ''
\echo '=== Post-migration state ==='
SELECT datname AS database, pg_get_userbyid(datdba) AS owner
FROM pg_database
WHERE datname NOT IN ('postgres', 'template0', 'template1')
ORDER BY datname;

SELECT rolname AS role
FROM pg_roles
WHERE rolcanlogin
ORDER BY rolname;

\echo ''
\echo 'Reminder: update docker-compose.yml, .env, .env.example and'
\echo 'application*.yml in the SAME change, or the application will not connect.'
\echo 'Liquibase changeset ids and authors are deliberately NOT renamed —'
\echo 'renaming them would make Liquibase re-run applied migrations.'