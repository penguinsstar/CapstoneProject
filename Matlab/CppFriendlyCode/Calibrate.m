function [SBP0, DBP0, PTT0] = Calibrate(PTT, RealDBP, RealSBP)


    %%Average of the 5 ensembled PTTs and cuff based BP (PTT BAR)
    meanPTT = mean(PTT);
    meanSBP = mean(RealSBP);
    meanDBP = mean(RealDBP);
    
    %% Calculating various parameters required for the model (mPTP)
    SBP0 = meanSBP;
    DBP0 = meanDBP;
    PTT0 = meanPTT;
    
end