SET foreign_key_checks = 0;


truncate table identifier;
truncate table identifier_namespace;
truncate table identifier_occurrence;
truncate table org;
truncate table org_role;
truncate table platform;
truncate table refdata_category;
truncate table refdata_value;
truncate table title_instance;
truncate table title_instance_platform;

drop table identifier;
drop table identifier_namespace;
drop table identifier_occurrence;
drop table org;
drop table org_role;
drop table platform;
drop table refdata_category;
drop table refdata_value;
drop table title_instance;
drop table title_instance_platform;

SET foreign_key_checks = 1;
