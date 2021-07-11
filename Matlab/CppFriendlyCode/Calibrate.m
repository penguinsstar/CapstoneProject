% CalibrationMode = 0 : mPTP
% CalibrationMode = 1 : fPTP

function [SBP0, DBP0, PTT0, fPTT0, fDBP0] = ...
    Calibrate(ECG, PPG, RealDBP, RealSBP, gamma, CalibrationMode)

    PTT = zeros(1,5);
    
    for c = 1:5
        %%Getting 5 median PTT values from 30s intervals
        PTT(c) = Calculate_PTT(ECG(:,c), PPG(:,c));
    end
    %%Average of the 5 ensembled PTTs and cuff based BP (PTT BAR)
    meanPTT = mean(PTT);
    meanSBP = mean(RealSBP(1:5));
    meanDBP = mean(RealDBP(1:5));
    
    %% Calculating various parameters required for the model (mPTP)
    SBP0 = meanSBP;
    DBP0 = meanDBP;
    PTT0 = meanPTT;
    
    fPTT0 = 0;
    fDBP0 = 0;
    
    if(CalibrationMode)
        
        %% fPTP calibration
        EstDBP = zeros(1,5); 
        PTTcurrent = zeros(1,5);

        %%Calculating Penalty Factor (alpha)
        alphaPTT = (sum(PTT - meanPTT)) / (length(PTT)*(sum(abs(PTT - meanPTT))));
        PTT0 = meanPTT*(1-alphaPTT);

        %%Calculating the adjusted DBP initial value
        % need to collect cuff BP and estimate BP values using mPTP calibration
        for c = 6:10
            PTTcurrent(c-5) = Calculate_PTT(ECG(:,c), PPG(:,c));
            %%base values already calculated from the callibration
            EstDBP(c-5) = (SBP0 + 2*DBP0)/3 + ((2/gamma)*log(PTT0/PTTcurrent(c-5))) - ((SBP0 - DBP0)/3)*((PTT0/PTTcurrent(c-5))^2);
        end

        alphaDBP = (sum(EstDBP - RealDBP(6:10))) / (length(EstDBP)*(sum(abs(EstDBP - RealDBP(6:10)))));
        alphaPTT = (sum(PTTcurrent - mean(PTTcurrent))) / (length(PTTcurrent)*(sum(abs(PTTcurrent - mean(PTTcurrent)))));
        fPTT0 = mean(PTTcurrent)*(1-alphaPTT);
        fDBP0 = meanDBP*(1-alphaDBP);

    end    
end