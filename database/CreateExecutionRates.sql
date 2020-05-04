-- Author Wesley Faler
-- Version 2014-09-17

drop table if exists RatesOpModeDistribution;

CREATE TABLE IF NOT EXISTS RatesOpModeDistribution (
	sourceTypeID         SMALLINT NOT NULL,
	roadTypeID           SMALLINT NOT NULL,
	avgSpeedBinID        SMALLINT NOT NULL DEFAULT '0',
	hourDayID            SMALLINT NOT NULL DEFAULT '0',
	polProcessID         int NOT NULL,
	opModeID             SMALLINT NOT NULL,
	opModeFraction       FLOAT NULL,
	opModeFractionCV     FLOAT NULL,
	avgBinSpeed			 FLOAT NULL,
	avgSpeedFraction 	 float not null default '0',

	PRIMARY KEY (sourceTypeID, roadTypeID, avgSpeedBinID, hourDayID, polProcessID, opModeID),
	KEY (sourceTypeID),
	KEY (roadTypeID),
	KEY (avgSpeedBinID),
	KEY (hourDayID),
	KEY (polProcessID),
	KEY (opModeID)
);

--	key speed2 (roadTypeID,hourDayID,sourceTypeID,polProcessID,opModeID,avgSpeedBinID)

TRUNCATE TABLE RatesOpModeDistribution;

drop table if exists SBWeightedEmissionRateByAge;

CREATE TABLE IF NOT EXISTS SBWeightedEmissionRateByAge (
	sourceTypeID		SMALLINT NOT NULL,
	polProcessID		int NOT NULL,
	opModeID			SMALLINT NOT NULL,
	modelYearID			SMALLINT NOT NULL,
	fuelTypeID			SMALLINT NOT NULL,
	ageGroupID			SMALLINT NOT NULL,
	regClassID			SMALLINT NOT NULL,

	meanBaseRate		FLOAT NULL,
	meanBaseRateIM		FLOAT NULL,
	meanBaseRateACAdj	FLOAT NULL,
	meanBaseRateIMACAdj	FLOAT NULL,
	sumSBD				DOUBLE NULL,
	sumSBDRaw			DOUBLE NULL,
	unique key (sourceTypeID, polProcessID, opModeID, modelYearID, fuelTypeID, ageGroupID, regClassID)
);

TRUNCATE TABLE SBWeightedEmissionRateByAge;

drop table if exists SBWeightedEmissionRate;

CREATE TABLE IF NOT EXISTS SBWeightedEmissionRate (
	sourceTypeID		SMALLINT NOT NULL,
	polProcessID		int NOT NULL,
	opModeID			SMALLINT NOT NULL,
	modelYearID			SMALLINT NOT NULL,
	fuelTypeID			SMALLINT NOT NULL,
	regClassID			SMALLINT NOT NULL,

	meanBaseRate		FLOAT NULL,
	meanBaseRateIM		FLOAT NULL,
	meanBaseRateACAdj	FLOAT NULL,
	meanBaseRateIMACAdj	FLOAT NULL,
	sumSBD				DOUBLE NULL,
	sumSBDRaw			DOUBLE NULL,
	unique key (sourceTypeID, polProcessID, opModeID, modelYearID, fuelTypeID, regClassID)
);

TRUNCATE TABLE SBWeightedEmissionRate;

drop table if exists SBWeightedDistanceRate;

CREATE TABLE IF NOT EXISTS SBWeightedDistanceRate (
	sourceTypeID		SMALLINT NOT NULL,
	polProcessID		int NOT NULL,
	modelYearID			SMALLINT NOT NULL,
	fuelTypeID			SMALLINT NOT NULL,
	regClassID			SMALLINT NOT NULL,
	avgSpeedBinID 		smallint not null,

	meanBaseRate		FLOAT NULL,
	meanBaseRateIM		FLOAT NULL,
	meanBaseRateACAdj	FLOAT NULL,
	meanBaseRateIMACAdj	FLOAT NULL,
	sumSBD				DOUBLE NULL,
	sumSBDRaw			DOUBLE NULL,
	primary key (sourceTypeID, polProcessID, modelYearID, fuelTypeID, regClassID, avgSpeedBinID)
);

TRUNCATE TABLE SBWeightedDistanceRate;

drop table if exists distanceEmissionRate;

create table if not exists distanceEmissionRate (
	polProcessID int not null,
	fuelTypeID smallint not null,
	sourceTypeID smallint not null,
	modelYearID smallint not null,
	avgSpeedBinID smallint not null,
	ratePerMile double not null,
	ratePerSHO double not null,
	primary key (sourceTypeID, polProcessID, modelYearID, fuelTypeID, avgSpeedBinID)
);

--	regClassID smallint not null,
--	primary key (sourceTypeID, polProcessID, modelYearID, fuelTypeID, regClassID, avgSpeedBinID)

truncate table distanceEmissionRate;

drop table if exists BaseRateByAge;

CREATE TABLE IF NOT EXISTS BaseRateByAge (
	sourceTypeID         SMALLINT NOT NULL,
	roadTypeID           SMALLINT NOT NULL,
	avgSpeedBinID        SMALLINT NOT NULL DEFAULT '0',
	hourDayID            SMALLINT NOT NULL DEFAULT '0',
	polProcessID         int NOT NULL,
	pollutantID          SMALLINT UNSIGNED NULL DEFAULT NULL,
	processID            SMALLINT UNSIGNED NULL DEFAULT NULL,
	modelYearID			 SMALLINT NOT NULL,
	fuelTypeID			 SMALLINT NOT NULL,
	ageGroupID			 SMALLINT NOT NULL,
	regClassID			 SMALLINT NOT NULL,
	opModeID			 SMALLINT NOT NULL,

	meanBaseRate		 FLOAT NULL,
	meanBaseRateIM		 FLOAT NULL,
	emissionRate		 FLOAT NULL,
	emissionRateIM		 FLOAT NULL,

	meanBaseRateACAdj	 FLOAT NULL,
	meanBaseRateIMACAdj	 FLOAT NULL,
	emissionRateACAdj    FLOAT NULL,
	emissionRateIMACAdj  FLOAT NULL,

	opModeFraction       FLOAT NULL,
	opModeFractionRate   FLOAT NULL,
	PRIMARY KEY (sourceTypeID, roadTypeID, avgSpeedBinID, hourDayID, polProcessID, modelYearID, fuelTypeID, ageGroupID, regClassID, opModeID)
);

TRUNCATE TABLE BaseRateByAge;

drop table if exists BaseRate;

CREATE TABLE IF NOT EXISTS BaseRate (
	sourceTypeID         SMALLINT NOT NULL,
	roadTypeID           SMALLINT NOT NULL,
	avgSpeedBinID        SMALLINT NOT NULL DEFAULT '0',
	hourDayID            SMALLINT NOT NULL DEFAULT '0',
	polProcessID         int NOT NULL,
	pollutantID          SMALLINT UNSIGNED NULL DEFAULT NULL,
	processID            SMALLINT UNSIGNED NULL DEFAULT NULL,
	modelYearID			 SMALLINT NOT NULL,
	fuelTypeID			 SMALLINT NOT NULL,
	regClassID			 SMALLINT NOT NULL,
	opModeID			 SMALLINT NOT NULL,

	meanBaseRate		 FLOAT NULL,
	meanBaseRateIM		 FLOAT NULL,
	emissionRate		 FLOAT NULL,
	emissionRateIM		 FLOAT NULL,

	meanBaseRateACAdj	 FLOAT NULL,
	meanBaseRateIMACAdj	 FLOAT NULL,
	emissionRateACAdj    FLOAT NULL,
	emissionRateIMACAdj  FLOAT NULL,

	opModeFraction       FLOAT NULL,
	opModeFractionRate   FLOAT NULL,
	PRIMARY KEY (sourceTypeID, roadTypeID, avgSpeedBinID, hourDayID, polProcessID, modelYearID, fuelTypeID, regClassID, opModeID)
);

TRUNCATE TABLE BaseRate;
