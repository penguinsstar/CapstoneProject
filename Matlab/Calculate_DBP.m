%% ORIGINAL CODE BY ABHIJITH BAILUR, modified by Frank Li, Daniel Wan, Akshay Kumar & Khalil Ammar
% CalibrationMode = 0 : mPTP
% CalibrationMode = 1 : fPTP
% Command to run model with pre-calibrated parameters: Calculate_DBP(1, struct('PTT0', 0.1, 'DBP0', 66.4, 'SBP0', 114.8), 1, 1, 0)
function Calculate_DBP(isCalibrated, userParameters, calibrationMode, debug, PTTdebug)

%% Debug parameters
if(debug)
    isCalibrated = 1;
    PTT = [0.11, 0.09, 0.09, 0.11, 0.1];
    DBP = [65, 68, 67, 66, 66];
    SBP = [120, 116, 114, 109, 115];
    SBP0 = userParameters.SBP0;
    DBP0 = userParameters.DBP0;
    PTT0 = userParameters.PTT0;
    gamma = 0.031;
    if(calibrationMode == 1)
        EstDBP = [70, 71, 69, 70, 70];
        RealDBP = [65, 65, 68, 65, 65];
        alphaPTT = (sum(PTT - mean(PTT))) / (length(PTT)*(sum(abs(PTT - mean(PTT)))));
        fPTT0 = mean(PTT)*(1-alphaPTT);
        alphaDBP = (sum(EstDBP - RealDBP)) / (length(EstDBP)*(sum(abs(EstDBP - RealDBP))));
        fDBP0 = mean(DBP)*(1-alphaDBP);
    end
end

%% Calibrate if not already done
if (~isCalibrated)
    c = 1;
    while c <= 5
        %%Getting 5 median PTT values from 30s intervals
        PTT(c) = Calculate_PTT(PTTdebug);
        disp('PTT:')
        disp(PTT(c));
        skip = input('Skip value y = 1 / n = 0 ? \n');
        if(skip == 1)
            continue;
        end
        %%Prompts user to input values
        DBP(c) = input('Please input your actual DBP: ');
        SBP(c) = input('Please input your actual SBP: ');
        c = c + 1;
    end
    %%Average of the 5 ensembled PTTs and cuff based BP (PTT BAR)
    meanPTT = mean(PTT);
    meanSBP = mean(SBP);
    meanDBP = mean(DBP);

    
    %% Calculating various parameters required for the model (mPTP)
    SBP0 = meanSBP;
    DBP0 = meanDBP;
    PTT0 = meanPTT;
    
    %% fPTP calibration
    if(calibrationMode == 1)
        %%Calculating Penalty Factor (alpha)
        alphaPTT = (sum(PTT - meanPTT)) / (length(PTT)*(sum(abs(PTT - meanPTT))));
        PTT0 = meanPTT*(1-alphaPTT);

        %%Calculating the adjusted DBP initial value
        % need to collect cuff BP and estimate BP values using mPTP calibration
        for c = 1:3
            PTTcurrent = Calculate_PTT(0);
            %%base values already calculated from the callibration
            EstDBP(c) = (SBP0 + 2*DBP0)/3 + ((2/gamma)*log(PTT0/PTTcurrent)) - ((SBP0 - DBP0)/3)*((PTT0/PTTcurrent)^2);
            RealDBP(c) = input('Please input your actual diastolic blood pressure');
        end

        alphaDBP = (sum(EstDBP - RealDBP)) / (length(EstDBP)*(sum(abs(EstDBP - RealDBP))));
        DBP0 = meanDBP*(1-alphaDBP);
    end
    
    %% Gamma is preset based on user's age
    gamma = 0.031;

    
end
 
%% calculate DBP
while true
    PTTcurrent = Calculate_PTT(PTTdebug);
    disp('PTT: ');
    disp(PTTcurrent);
    % BP estimation with mPTP parameters only
    DBP = (SBP0 + 2*DBP0)/3 + ((2/gamma)*log(PTT0/PTTcurrent)) - ((SBP0 - DBP0)/3)*((PTT0/PTTcurrent)^2);
    disp('Your Diastolic Blood Pressure Is (mPTP): ');
    disp(DBP);
    
    % BP estimation with fPTP parameters
    % TODO: remove one of these 2 after testing
    fDBP = (SBP0 + 2*fDBP0)/3 + ((2/gamma)*log(fPTT0/PTTcurrent)) - ((SBP0 - fDBP0)/3)*((fPTT0/PTTcurrent)^2);
    disp('Your Diastolic Blood Pressure Is (fPTP): ');
    disp(fDBP);
    input('Press any key to make another measurement');

end
