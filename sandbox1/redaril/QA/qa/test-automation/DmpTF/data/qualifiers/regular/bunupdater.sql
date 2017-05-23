spool bunupdater.log

-- dont expand the oracle & parameter syntax
set scan off;

-- show the commands in the log
set echo on;

-- exit immediately on error
whenever sqlerror exit sql.sqlcode;

-- show output from pl/sql
set serveroutput on;

CREATE OR REPLACE
PROCEDURE BUNUPDATER IS
CURSOR find_constraints IS SELECT CONSTRAINT_NAME, TABLE_NAME FROM user_constraints WHERE CONSTRAINT_TYPE = 'R';

BEGIN

    FOR i IN find_constraints
    LOOP
        EXECUTE IMMEDIATE 'ALTER TABLE ' || i.TABLE_NAME || ' DISABLE CONSTRAINT ' || i.CONSTRAINT_NAME;
    END LOOP;

DELETE FROM NT_QUALIFIER_PHRASE;
DELETE FROM NT_QUALIFIER;
DELETE FROM NC_QUALIFIER_GROUP_INTEREST;
DELETE FROM NC_COMMER_CATEG_AUX_CATEGORY;
DELETE FROM NC_AUX_CATEGORY_INTEREST;
DELETE FROM NC_AUX_CATEGORY;
DELETE FROM NC_COMMERCIAL_CATEGORY;
DELETE FROM NC_INTEREST;

INSERT INTO NC_AUX_CATEGORY SELECT AUX_CATEGORY_ID,NAME||'sts001',THRESHOLD,SET_ID FROM NC_AUX_CATEGORY@BUN;
INSERT INTO NC_COMMERCIAL_CATEGORY SELECT COMMERCIAL_CATEGORY_ID,NAME,FREQUENCY_CAP_ENABLED,FREQUENCY_CAP_LIMIT,FREQUENCY_CAP_TIME_SECONDS,FREQUENCY_CAP_DELAY_SECONDS,SET_ID,PARENT_ID FROM NC_COMMERCIAL_CATEGORY@BUN;
INSERT INTO NC_INTEREST SELECT INTEREST_ID,NAME,PARENT_ID,CATEGORY_IDENTIFIER,EXTERNAL_IDENTIFIER,DATA_SOURCE,EXTERNAL_ID FROM NC_INTEREST@BUN;
INSERT INTO NC_AUX_CATEGORY_INTEREST SELECT * FROM NC_AUX_CATEGORY_INTEREST@BUN;
INSERT INTO NC_COMMER_CATEG_AUX_CATEGORY SELECT * FROM NC_COMMER_CATEG_AUX_CATEGORY@BUN;
INSERT INTO NT_QUALIFIER SELECT * FROM NT_QUALIFIER@BUN;
INSERT INTO NT_QUALIFIER_PHRASE SELECT * FROM NT_QUALIFIER_PHRASE@BUN;
INSERT INTO NC_QUALIFIER_GROUP_INTEREST SELECT * FROM NC_QUALIFIER_GROUP_INTEREST@BUN; 

  FOR i IN find_constraints
  LOOP
        EXECUTE IMMEDIATE 'ALTER TABLE ' || i.TABLE_NAME || ' ENABLE CONSTRAINT ' || i.CONSTRAINT_NAME;
    END LOOP;

END BUNUPDATER;

/

quit;