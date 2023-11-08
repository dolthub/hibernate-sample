

------------------
Setting up your DB

Start a dolt sql-server:

`dolt sql-server -u root -p r00tr00t`

(password matters - it's coded into the hibernate config)

Create your tables:

```
CREATE DATABASE petridish;

USE petridish;

CREATE TABLE `species` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `color` char(7),
  `tick_health_impact` double,
  PRIMARY KEY (`id`)
);

CREATE TABLE `damage` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `attacker` char(36) NOT NULL,
  `victim` char(36) NOT NULL,
  `damage` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attacker` (`attacker`),
  KEY `victim` (`victim`),
  CONSTRAINT `gnv45qu2` FOREIGN KEY (`victim`) REFERENCES `species` (`id`),
  CONSTRAINT `mllvpsu8` FOREIGN KEY (`attacker`) REFERENCES `species` (`id`),
  CONSTRAINT `damage_chk_os841eva` CHECK (((`Damage` >= 0) AND (`Damage` <= 0.1)))
);
```

Create a couple 'species' and their damage:

> insert into species (color,tick_health_impact) values ("#00FF00",0.01);
> insert into species (color,tick_health_impact) values ("#0000FF",0.008);
> insert into species (color,tick_health_impact) values ("#FF0000",0.012);

(you are going to need to look at the species to get the UUIDs)

insert into damage (attacker,victim,damage) values ("dea1db7d-2276-4386-aa40-cb0831204bce","56202df7-a085-4722-b025-00074333d439", 0.04);

-- Fine to start with no damges, but make the simulation a little dull. There isn't currenly
   a unique index on the attacker/victim pairings, but there should be.



-------------------
Building and running the code.

Install Java. I used OpenJDK: https://openjdk.org/install/

Install Maven: https://maven.apache.org/install.html

Then build it:

`$ mvn package`

And run it:

`$ mvn exec:java`


