% CalibrationMode = 0 : mPTP
% CalibrationMode = 1 : fPTP

function [SBP0, DBP0, PTT0] = ...
    Calibrate(ECG, PPG, RealDBP, RealSBP)

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
    
end