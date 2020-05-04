-- This MySQL script produces emission rates for PM2.5 as grams per vehicle-mile for each defined link 
-- 
-- The MySQL table produced in the output database is called: pm25_Grams_Per_Veh_Mile
-- 
-- Users should consult Section 4 of EPA's "Transportation Conformity Guidance for Quantitative Hot-spot
-- Analyses in PM2.5 and PM10 Nonattainment and Maintenance Areas" for guidance on filling out
-- the MOVES RunSpec and importing the appropriate inputs
--


Drop   table if exists pm25_Grams_Per_Veh_Mile;
Create table pm25_Grams_Per_Veh_Mile
Select   movesRunId,
         yearId,
         monthId,
         hourId,
         linkId,
         'Total PM2.5' as pollutant,
         sum(rateperdistance) as GramsPerVehMile
From     rateperdistance
where    pollutantId in (110,116,117)
Group by movesRunId,
         yearId,
         monthId,
         hourId,
         linkId;


