-- Nonroad Post Processing Script (updated 7/26/2018):
-- Emission factors in grams per horsepower-hour by SCC, horsepower class, 
-- and model year
--  
-- MOVES-Nonroad Output Guidance:
--       SCC, HP class, and model year must be selected and present in the
--       results. This script will run faster if engine tech is not selected. 
--       It is strongly recommended to only have one sector and fuel type per
--       output database when running this script. Additionally, this script 
--       will run faster if there is only one year, month, and day in the output.
-- 
-- When prompted to save, specify one of the following file types: .xlsx, .xls, or .txt
-- The raw output of this script is also stored in the output database in a table called:
-- EmissionFactors_per_hphr_by_SCC_and_ModelYear
-- 
-- WARNING:
--       This script may take a long time to complete depending on
--       the size of the output database. A confirmation notice will
--       alert you when this action has completed.

flush tables;

-- Set up indexing
set @exist := (select count(*) from information_schema.statistics where table_schema = DATABASE() and table_name = 'movesoutput' and index_name = 'index1');
set @sqlstmt := if( @exist > 0, 'select ''INFO: Index already exists.''', 'create index index1 on movesoutput ( MOVESRunID )');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;

set @exist := (select count(*) from information_schema.statistics where table_schema = DATABASE() and table_name = 'movesoutput' and index_name = 'index103');
set @sqlstmt := if( @exist > 0, 'select ''INFO: Index already exists.''', 'create index index103 on movesoutput ( MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,pollutantID,processID,modelYearID )');
PREPARE stmt FROM @sqlstmt;																						
EXECUTE stmt;

set @exist := (select count(*) from information_schema.statistics where table_schema = DATABASE() and table_name = 'movesactivityoutput' and index_name = 'index10');
set @sqlstmt := if( @exist > 0, 'select ''INFO: Index already exists.''', 'create index index10 on movesactivityoutput ( activitytypeid )');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;

-- Get time units
set @timeUnits := (select timeUnits from movesrun limit 1);

-- Calculate total horsepower*hours*loadfactor before any aggregation occurs
-- Select all of the rows where the activity column represents source hours and
--   merge them with all of the rows where the activity column represents the average horsepower and
--   merge them with all of the rows where the activity column represents the load factor
drop table if exists sourceHours;
create table sourceHours
select 
	MOVESRunID,
	yearID,
	monthID,
	dayID,
	stateID,
	countyID,
	SCC,
	modelYearID,
	engTechID,
	hpID,
	activity as sourceHours
from movesactivityoutput  
where activitytypeid = 2;

drop table if exists horsepower;
create table horsepower
select 
	MOVESRunID,
	yearID,
	monthID,
	dayID,
	stateID,
	countyID,
	SCC,
	modelYearID,
	engTechID,
	hpID,
	activity as avgHorsepower 
from movesactivityoutput  
where activitytypeid = 9;

drop table if exists loadfactor;
create table loadfactor
select 
	MOVESRunID,
	yearID,
	monthID,
	dayID,
	stateID,
	countyID,
	SCC,
	modelYearID,
	engTechID,
	hpID,
	activity as loadFactor 
from movesactivityoutput  
where activitytypeid = 12;

create index index1 on sourceHours (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,modelYearID,engTechID,hpID);
create index index1 on horsepower (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,modelYearID,engTechID,hpID);
create index index1 on loadfactor (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,modelYearID,engTechID,hpID);

-- Note: b1.col = b2.col OR b1.col IS NULL AND b2.col IS NULL is used instead of just a simple 
--   USING() command because USING() doesn't work if there are NULLs
-- Additionally, JOIN followed by ALTER is used instead of a single statement with multiple joins
--   because if the tables are very large, the separate statements are faster
drop table if exists hphr;
create table hphr
select
	b1.MOVESRunID,
	b1.yearID,
	b1.monthID,
	b1.dayID,
	b1.stateID,
	b1.countyID,
	b1.SCC,
	b1.modelYearID,
	b1.engTechID,
	b1.hpID,
	b1.sourceHours,
	b2.avgHorsepower	
from sourceHours b1
join horsepower b2
	on ((b1.MOVESRunID=b2.MOVESRunID) AND
		(b1.yearID=b2.yearID) AND 
		(b1.monthID=b2.monthID) AND 
		(b1.dayID=b2.dayID) AND 
		(b1.stateID=b2.stateID OR b1.stateID IS NULL AND b2.stateID IS NULL) AND 
		(b1.countyID=b2.countyID OR b1.countyID IS NULL AND b2.countyID IS NULL) AND  
		(b1.SCC=b2.SCC) AND 
		(b1.modelYearID=b2.modelYearID) AND 
		(b1.engTechID=b2.engTechID OR b1.engTechID IS NULL AND b2.engTechID IS NULL) AND 
		(b1.hpID=b2.hpID));

create index index1 on hphr (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,modelYearID,engTechID,hpID);

alter table hphr add loadFactor double, add hpHours double;
update hphr b1, loadfactor b3
set b1.loadFactor = b3.loadFactor,
    b1.hpHours = avgHorsepower * sourceHours * b3.loadFactor
 where ((b1.MOVESRunID=b3.MOVESRunID) AND
		(b1.yearID=b3.yearID) AND 
		(b1.monthID=b3.monthID) AND 
		(b1.dayID=b3.dayID) AND 
		(b1.stateID=b3.stateID OR b1.stateID IS NULL AND b3.stateID IS NULL) AND 
		(b1.countyID=b3.countyID OR b1.countyID IS NULL AND b3.countyID IS NULL) AND 
		(b1.SCC=b3.SCC) AND 
		(b1.modelYearID=b3.modelYearID) AND 
		(b1.engTechID=b3.engTechID OR b1.engTechID IS NULL AND b3.engTechID IS NULL) AND 
		(b1.hpID=b3.hpID));

create index index2 on hphr (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,modelYearID);


-- Set up unit conversions table
drop table if exists units;
create table units (fromUnit char(5), factor double, description text);
insert into units values 
('ton', 907185, 'From U.S. tons to grams'),
('lb', 453.592, 'From lbm to grams'),
('kg', 1000, 'From kg to grams'),
('g', 1, 'From grams to grams');

-- Get inventories by SCC and hpID
drop table if exists temp1;
create table temp1
select 
	MOVESRunID,
	yearID,
	monthID,
	dayID,
	stateID,
	countyID,
	SCC,
	hpID,
    pollutantID,
    processID,
	modelYearID,
    units.factor * sum(emissionQuant) as emissionQuant
from movesoutput m
left join movesrun using (movesrunid)
left join units on (movesrun.massUnits = units.fromUnit)
group by MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,pollutantID,processID,modelYearID;

create index index1 on temp1 (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,modelYearID);
create index index2 on temp1 (SCC);
create index index3 on temp1 (hpID);


-- Get horsepower-hours by SCC and hpID
drop table if exists temp2;
create table temp2
select 
	MOVESRunID,
	yearID,
	monthID,
	dayID,
	stateID,
	countyID,
	SCC,
	hpID,
	modelYearID,
    sum(hpHours) as hpHours
from hphr
group by MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,modelYearID;

create index index1 on temp2 (MOVESRunID,yearID,monthID,dayID,stateID,countyID,SCC,hpID,modelYearID);


-- Join temp1 and temp2 and calculate the emission rate for the resulting output table
drop table if exists EmissionFactors_per_hphr_by_SCC_and_ModelYear;
create table EmissionFactors_per_hphr_by_SCC_and_ModelYear
select
	b1.MOVESRunID,
	b1.yearID,
	b1.monthID,
	b1.dayID,
	b1.stateID,
	b1.countyID,
	b1.SCC,
	s.description as sccDescription,
	s.fuelTypeID,
	b1.hpID,
	h.binName as hpBin,
    b1.pollutantID,
    b1.processID,
	b1.modelYearID,
	b1.emissionQuant,
    b2.hpHours,
    IF(b2.hpHours != 0, b1.emissionQuant / b2.hpHours, NULL) as emissionRate,
    'g/hp-hr' as emissionRateUnits
from temp1 b1
inner join temp2 b2 
	on ((b1.MOVESRunID=b2.MOVESRunID) AND
		(b1.yearID=b2.yearID) AND 
		(b1.monthID=b2.monthID) AND 
		(b1.dayID=b2.dayID) AND 
		(b1.stateID=b2.stateID OR b1.stateID IS NULL AND b2.stateID IS NULL) AND 
		(b1.countyID=b2.countyID OR b1.countyID IS NULL AND b2.countyID IS NULL) AND 
		(b1.SCC=b2.SCC) AND
		(b1.hpID=b2.hpID) AND 
		(b1.modelYearID=b2.modelYearID))
left join ##defaultdb##.nrscc s on (b1.scc=s.scc)
left join ##defaultdb##.nrhprangebin h on (b1.hpID=h.NRHPRangeBinID);

drop table if exists sourceHours;
drop table if exists loadfactor;
drop table if exists horsepower;
drop table if exists temp1;
drop table if exists temp2;
drop table if exists hphr;
drop table if exists units;