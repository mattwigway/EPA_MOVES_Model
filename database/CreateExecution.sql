-- Author Wesley Faler
-- Version 2014-09-17

drop table if exists hotellingFuelUsingActivity;

create table hotellingFuelUsingActivity (
	beginModelYearID smallint(6) not null,
	endModelYearID smallint(6) not null,
	fractionSpentConsumingFuel double not null,
	primary key (beginModelYearID, endModelYearID)
);

drop table if exists hotellingNonExtendedIdleActivity;

create table hotellingNonExtendedIdleActivity (
	beginModelYearID smallint(6) not null,
	endModelYearID smallint(6) not null,
	fractionSpentNotExtendedIdling double not null,
	primary key (beginModelYearID, endModelYearID)
);

drop table if exists hotellingHoursUsingFuel;

CREATE TABLE hotellingHoursUsingFuel (
	sourceTypeID         SMALLINT NOT NULL,
	hourDayID            SMALLINT NOT NULL,
	monthID              SMALLINT NOT NULL,
	yearID               SMALLINT NOT NULL,
	ageID                SMALLINT NOT NULL,
	zoneID               INTEGER NOT NULL,
	hotellingHoursUsingFuel DOUBLE NULL,
	primary key 		(sourceTypeID, hourDayID, monthID, yearID, ageID, zoneID),
	key (sourceTypeID),
	KEY (hourDayID),
	KEY (monthID),
	KEY (yearID),
	KEY (ageID),
	KEY (zoneID)
);

-- *************************************************************************************
-- The following tables are used in joins for calculations to filter the number of items
-- used in the calculations.
-- *************************************************************************************

drop table if exists RunSpecSourceType;

CREATE TABLE IF NOT EXISTS RunSpecSourceType (
	sourceTypeID SMALLINT NOT NULL,
	UNIQUE INDEX NdxSourceTypeID (
	sourceTypeID ASC)
);

TRUNCATE TABLE RunSpecSourceType;

drop table if exists RunSpecRoadType;

CREATE TABLE IF NOT EXISTS RunSpecRoadType (
	roadTypeID SMALLINT NOT NULL,
	UNIQUE INDEX NdxRoadTypeID (
	roadTypeID ASC)
);

TRUNCATE TABLE RunSpecRoadType;

drop table if exists RunSpecMonth;

CREATE TABLE IF NOT EXISTS RunSpecMonth (
	monthID SMALLINT NOT NULL,
	UNIQUE INDEX NdxMonthID (
	monthID ASC)
);

TRUNCATE TABLE RunSpecMonth;

drop table if exists RunSpecDay;

CREATE TABLE IF NOT EXISTS RunSpecDay (
	dayID SMALLINT NOT NULL,
	UNIQUE INDEX NdxDayID (
	dayID ASC)
);

TRUNCATE TABLE RunSpecDay;

drop table if exists RunSpecHour;

CREATE TABLE IF NOT EXISTS RunSpecHour (
	hourID SMALLINT NOT NULL,
	UNIQUE INDEX NdxHourID (
	hourID ASC)
);

TRUNCATE TABLE RunSpecHour;

drop table if exists RunSpecMonthGroup;

CREATE TABLE IF NOT EXISTS RunSpecMonthGroup (
	monthGroupID SMALLINT NOT NULL,
	UNIQUE INDEX NdxMonthGroupID (
	monthGroupID ASC)
);

TRUNCATE TABLE RunSpecMonthGroup;

drop table if exists RunSpecYear;

CREATE TABLE IF NOT EXISTS RunSpecYear (
	yearID SMALLINT NOT NULL,
	UNIQUE INDEX NdxHourID (
	yearID ASC)
);

TRUNCATE TABLE RunSpecYear;

drop table if exists RunSpecModelYearAge;

CREATE TABLE IF NOT EXISTS RunSpecModelYearAge (
	yearID SMALLINT NOT NULL,
	modelYearID SMALLINT NOT NULL,
	ageID SMALLINT NOT NULL,
	
	primary key (modelYearID, ageID, yearID),
	key (yearID, modelYearID, ageID),
	key (ageID, modelYearID)
);

TRUNCATE TABLE RunSpecModelYearAge;

drop table if exists RunSpecModelYearAgeGroup;

CREATE TABLE IF NOT EXISTS RunSpecModelYearAgeGroup (
	yearID smallint(6) NOT NULL,
	modelYearID smallint(6) NOT NULL,
	ageGroupID smallint(6) NOT NULL,
	PRIMARY KEY (modelYearID,ageGroupID,yearID),
	KEY yearID (yearID,modelYearID,ageGroupID),
	KEY yearID2 (yearID,ageGroupID,modelYearID),
	KEY ageID (ageGroupID,modelYearID,yearID)
);

TRUNCATE TABLE RunSpecModelYearAgeGroup;

drop table if exists RunSpecModelYear;

CREATE TABLE IF NOT EXISTS RunSpecModelYear (
	modelYearID SMALLINT NOT NULL primary key
);

TRUNCATE TABLE RunSpecModelYear;

drop table if exists RunSpecSourceFuelType;

CREATE TABLE IF NOT EXISTS RunSpecSourceFuelType (
	sourceTypeID SMALLINT NOT NULL,
	fuelTypeID TINYINT NOT NULL,
	UNIQUE INDEX NdxSourceFuelTypeID (
	sourceTypeID, fuelTypeID),
	unique key (fuelTypeID, sourceTypeID)
);

TRUNCATE TABLE RunSpecSourceFuelType;

drop table if exists RunSpecHourDay;

CREATE TABLE IF NOT EXISTS RunSpecHourDay (
	hourDayID SMALLINT NOT NULL,
	UNIQUE INDEX NdxHourDayID (
	hourDayID ASC)
);

TRUNCATE TABLE RunSpecHourDay;

drop table if exists RunSpecState;

CREATE TABLE IF NOT EXISTS RunSpecState (
	stateID SMALLINT NOT NULL,
	UNIQUE INDEX NdxState (
	stateID ASC)
);

TRUNCATE TABLE RunSpecState;

drop table if exists RunSpecCounty;

CREATE TABLE IF NOT EXISTS RunSpecCounty (
	countyID INTEGER NOT NULL,
	UNIQUE INDEX NDXCounty (
	countyID ASC)
);

TRUNCATE TABLE RunSpecCounty;

drop table if exists RunSpecFuelRegion;

CREATE TABLE IF NOT EXISTS RunSpecFuelRegion (
	fuelRegionID INTEGER NOT NULL,
	UNIQUE INDEX NDXFuelRegion (
	fuelRegionID ASC)
);

TRUNCATE TABLE RunSpecFuelRegion;

drop table if exists RunSpecZone;

CREATE TABLE IF NOT EXISTS RunSpecZone (
	zoneID INTEGER NOT NULL,
	UNIQUE INDEX NdxZone (
	zoneID ASC)
);

TRUNCATE TABLE RunSpecZone;

drop table if exists RunSpecLink;

CREATE TABLE IF NOT EXISTS RunSpecLink (
	linkID INTEGER NOT NULL,
	UNIQUE INDEX NdxLink (
	linkID ASC)
);

TRUNCATE TABLE RunSpecLink;

drop table if exists RunSpecPollutant;

CREATE TABLE IF NOT EXISTS RunSpecPollutant (
	pollutantID SMALLINT NOT NULL,
	UNIQUE INDEX NdxPollutant (
	pollutantID ASC)
);

TRUNCATE TABLE RunSpecPollutant;

drop table if exists RunSpecProcess;

CREATE TABLE IF NOT EXISTS RunSpecProcess (
	processID SMALLINT NOT NULL,
	UNIQUE INDEX NdxProcess (
	processID ASC)
);

TRUNCATE TABLE RunSpecProcess;

drop table if exists RunSpecPollutantProcess;

CREATE TABLE IF NOT EXISTS RunSpecPollutantProcess (
	polProcessID int NOT NULL,
	UNIQUE INDEX NdxPolProcess (
	polProcessID ASC)
);

TRUNCATE TABLE RunSpecPollutantProcess;

drop table if exists RunSpecChainedTo;

CREATE TABLE IF NOT EXISTS RunSpecChainedTo (
	outputPolProcessID int not null,
	outputPollutantID smallint not null,
	outputProcessID smallint not null,
	inputPolProcessID int not null,
	inputPollutantID smallint not null,
	inputProcessID smallint not null,
	index InputChainedToIndex (
		inputPollutantID,
		inputProcessID
	),
	index InputChainedToProcessIndex (
		inputProcessID
	),
	index OutputChainedToPolProcessIndex (
		outputPolProcessID
	),
	index InputOutputChainedToIndex (
		outputPolProcessID,
		inputPolProcessID
	),
	index InputOutputChainedToIndex2 (
		inputPolProcessID,
		outputPolProcessID
	)
);

TRUNCATE TABLE RunSpecChainedTo;

drop table if exists RunSpecSectorFuelType;

CREATE TABLE IF NOT EXISTS RunSpecSectorFuelType (
	sectorID SMALLINT NOT NULL,
	fuelTypeID TINYINT NOT NULL,
	UNIQUE INDEX NdxSectorFuelTypeID (
	sectorID, fuelTypeID),
	unique key (fuelTypeID, sectorID)
);

TRUNCATE TABLE RunSpecSectorFuelType;

drop table if exists RunSpecSector;

CREATE TABLE IF NOT EXISTS RunSpecSector (
	sectorID SMALLINT NOT NULL,
	UNIQUE INDEX NdxSectorID (
	sectorID ASC)
);

TRUNCATE TABLE RunSpecSector;

drop table if exists RunSpecNonRoadModelYearAge;

CREATE TABLE IF NOT EXISTS RunSpecNonRoadModelYearAge (
	yearID SMALLINT NOT NULL,
	modelYearID SMALLINT NOT NULL,
	ageID SMALLINT NOT NULL,
	
	primary key (modelYearID, ageID, yearID),
	key (yearID, modelYearID, ageID),
	key (ageID, modelYearID)
);

TRUNCATE TABLE RunSpecNonRoadModelYearAge;

drop table if exists RunSpecNonRoadModelYear;

CREATE TABLE IF NOT EXISTS RunSpecNonRoadModelYear (
	modelYearID SMALLINT NOT NULL primary key
);

TRUNCATE TABLE RunSpecNonRoadModelYear;

drop table if exists RunSpecNonRoadChainedTo;

CREATE TABLE IF NOT EXISTS RunSpecNonRoadChainedTo (
	outputPolProcessID int not null,
	outputPollutantID smallint not null,
	outputProcessID smallint not null,
	inputPolProcessID int not null,
	inputPollutantID smallint not null,
	inputProcessID smallint not null,
	index InputChainedToIndex (
		inputPollutantID,
		inputProcessID
	),
	index InputChainedToProcessIndex (
		inputProcessID
	),
	index OutputChainedToPolProcessIndex (
		outputPolProcessID
	),
	index InputOutputChainedToIndex (
		outputPolProcessID,
		inputPolProcessID
	),
	index InputOutputChainedToIndex2 (
		inputPolProcessID,
		outputPolProcessID
	)
);

TRUNCATE TABLE RunSpecNonRoadChainedTo;

-- Example: If a 1995 Euro car should be treated as a 1991 US car,
-- then the 1991 PollutantProcessModelYear should be used for the
-- the 1995 modelyear. So, use reverse model year mapping so the
-- modelYearID in PollutantProcessMappedModelYear maps older
-- model year groups to a newer model year.

drop table if exists PollutantProcessMappedModelYear;

CREATE TABLE IF NOT EXISTS PollutantProcessMappedModelYear (
    polProcessID int NOT NULL ,
    modelYearID SMALLINT NOT NULL ,
    modelYearGroupID INT NOT NULL ,
    fuelMYGroupID INTEGER NULL,
    IMModelYearGroupID INTEGER NULL,
    key (modelYearID, polProcessID),
    key (polProcessID),
    key (modelYearID),
    primary key (polProcessID, modelYearID)
);

TRUNCATE TABLE PollutantProcessMappedModelYear;
