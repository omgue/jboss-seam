insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (1, 'Action');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (2, 'Adult');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (3, 'Children');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (4, 'Comedy');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (5, 'Documentary');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (6, 'Classics');
insert into CATEGORIES (CATEGORY, CATEGORYNAME) values (7, 'Drama');

insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (1, 2, 'Indahouse', 'Ali G', 12.0);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (2, 3, 'The Lion King', '', 12.50);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (3, 5, 'March of the Penguins', '', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (4, 1, 'Indiana Jones and the Temple of Doom', 'Harisson Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (5, 1, 'Clear and Present Danger', 'Harisson Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (6, 6, 'Roman Holiday', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (7, 6, 'Breakfast at Tiffany''s', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (8, 6, 'Sabrina', 'Audrey Hepburn', 12.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (9, 4, 'Sabrina', 'Harrison Ford', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (10, 1, 'Kill Bill Vol. 1', 'Uma Thurman', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (11, 1, 'Kill Bill Vol. 2', 'Uma Thurman', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (12, 7, 'Lost in Translation', 'Bill Murray', 19.99);
insert into PRODUCTS (PROD_ID, CATEGORY, TITLE, ACTOR, PRICE) values (13, 7, 'Broken Flowers', 'Bill Murray', 19.99);

insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (1, 1, 10, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (2, 2, 15, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (3, 3, 77, 2);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (4, 4, 67, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (5, 5, 57, 1);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (6, 6, 44, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (7, 7, 88, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (8, 8, 99, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (9, 9, 56, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (10, 10, 102, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (11, 11, 994, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (12, 12, 12, 0);
insert into INVENTORY (INV_ID, PROD_ID, QUAN_IN_STOCK, SALES) values (13, 13, 77, 0);


INSERT INTO USERS (USERID,TYPE,USERNAME,PASSWORD,FIRSTNAME,LASTNAME) VALUES (1,'admin','manager','password','Albus', 'Dumblebore')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (2,'customer','Harry','Potter','4608499546 Dell Way','','QSDPAGD','SD',24101,'US',1,'ITHOMQJNYX@dell.com','4608499546',1,'1979279217775911',03,2012,'user1','password',55,100000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (3,'customer','Hermione','Granger','5119315633 Dell Way','','YNCERXJ','AZ',11802,'US',1,'UNUKXHJVXB@dell.com','5119315633',1,'3144519586581737',11,2012,'user2','password',80,40000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (4,'customer','Ron','Weasley','6297761196 Dell Way','','LWVIFXJ','OH',96082,'US',1,'LYYSHTQJRE@dell.com','6297761196',4,'8728086929768325',12,2010,'user3','password',47,100000,'M')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (5,'customer','Neville','Longbottom','9862764981 Dell Way','','HOKEXCD','MS',78442,'US',1,'WQLQHUHLFE@dell.com','9862764981',5,'7160005148965866',09,2009,'user4','password',44,40000,'F')
INSERT INTO USERS (USERID,TYPE,FIRSTNAME,LASTNAME,ADDRESS1,ADDRESS2,CITY,STATE,ZIP,COUNTRY,REGION,EMAIL,PHONE,CREDITCARDTYPE,CC_NUM,CC_MONTH,CC_YEAR,USERNAME,PASSWORD,AGE,INCOME,GENDER) VALUES (6,'customer','Ginny','Weasley','2841895775 Dell Way','','RZQTCDN','AZ',16291,'US',1,'ETBYBNEGUT@dell.com','2841895775',3,'8377095518168063',10,2010,'user5','password',21,20000,'M')

insert into ORDERS (ORDERID,ORDERDATE,USERID,NETAMOUNT,TAX,TOTALAMOUNT,TRACKING,STATUS) VALUES (1,now(),2,14,3,17,'TR7901' ,3)
insert into ORDERS (ORDERID,ORDERDATE,USERID,NETAMOUNT,TAX,TOTALAMOUNT,TRACKING,STATUS) VALUES (2,now(),2,24,5,29,'TR8331' ,3)

insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (1,1,1,1,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (2,2,1,2,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (3,1,2,3,2)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (4,2,2,4,1)
insert into ORDERLINES (ORDERLINEID,POS,ORDERID,PROD_ID,QUANTITY) VALUES (5,3,2,5,1)
