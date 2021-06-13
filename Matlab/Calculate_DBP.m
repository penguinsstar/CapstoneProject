%% ORIGINAL CODE BY ABHIJITH BAILUR, modified by Frank Li, Daniel Wan, Akshay Kumar & Khalil Ammar
function Calculate_DBP(ECG, PPG, isCalibrated, b1, b2, sampling_rate)

    %% Debug parameters
T = readtable('sensor-data.xlsx', 'Sheet', 'Sheet3');
sampling_rate = 100;
isCalibrated = 0;


%% calculate parameters
if (~isCalibrated)   
    for c = 1:5
        %%Getting 5 median PTT values from 30s intervals (1 required for
        %%debug variable)
        PTT(c) = Calculate_PTT(1);
        %%Prompts user to input values
        DBP(c) = input('DBP?');
        SBP(c) = input('SBP?');
    end
    %%Average of the 5 ensembled PTTs and cuff based BP (PTT BAR)
    meanPTT = mean(PTT);
    meanSBP = mean(SBP);
    meanDBP = mean(DBP);
    
    %%Calculating Penalty Factor (alpha)
    alphaPTT = (symsum(PTT(c) - meanPTT, c, 1, 5)) / (c*(symsum(abs(PTT(c) - meanPTT), c, 1, 5)));
    PTT0 = meanPTT*(1-alphaPTT);
    
    %%Calculating the BP alphas and initial (corrected) values seperately
    %%for getting PP0 
    alphaDBP = (symsum(DBP(c) - meanDBP, c, 1, 5)) / (c*(symsum(abs(DBP(c) - meanDBP), c, 1, 5)));
    DBP0 = meanDBP*(1-alphaDBP);
    
    alphaSBP = (symsum(SBP(c) - meanSBP, c, 1, 5)) / (c*(symsum(abs(SBP(c) - meanSBP), c, 1, 5)));
    SBP0 = meanSBP*(1-alphaSBP);
    
    %% Calculating various parameters required for the model
    PP0 = SBP0 - DBP0;
    MBP0 = (PP0 + DBP0)/3;
    %%solving for gamma
    eqn = MBP0 + ((2/gamma)*ln(PTT0/meanPTT)) - ((PP0/3)*((PTT0/meanPTT)^2)) - DBP0 == 0;
    gamma = solve(eqn, gamma);
    
end
 
%% calculate BP and PP
%% Calculate_PTT requires value for debug variable, 1 = ON
PTTcurrent = Calculate_PTT(1);
%%base values already calculated from the callibration
DBP = MBP0 + ((2/gamma)*ln(PTT0/PTTcurrent)) - ((PP0/3)*((PTT0/PTTcurrent)^2));
disp('Your Diastolic Blood Pressure Is: ');
disp(DBP);

end
